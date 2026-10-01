#!/usr/bin/env python3
"""Fail-closed crop gate for customer-visible email evidence screenshots.

The reviewer must identify the target message body on the source image. The
gate creates the deliverable crop itself and binds it to a short-lived receipt.
Pixel geometry cannot prove semantic correctness, so visual review is mandatory.
"""

import hashlib
import json
import sys
from datetime import datetime, timezone
from pathlib import Path

from PIL import Image


ROOT = Path(__file__).resolve().parents[1]
PRIVATE = (ROOT / "storage" / "operations").resolve()
REQUIRED_EXCLUSIONS = {"mail_navigation", "message_header_or_toolbar"}


def inside_private(value):
    path = (PRIVATE / value).resolve()
    if not path.is_relative_to(PRIVATE):
        raise ValueError("all screenshot files must stay in private operations storage")
    return path


def box(value, name, width, height):
    if not isinstance(value, list) or len(value) != 4 or not all(type(v) is int for v in value):
        raise ValueError(f"{name} must be four integer coordinates [left, top, right, bottom]")
    left, top, right, bottom = value
    if not (0 <= left < right <= width and 0 <= top < bottom <= height):
        raise ValueError(f"{name} is outside source image or has zero area")
    return tuple(value)


def intersects(a, b):
    return a[0] < b[2] and b[0] < a[2] and a[1] < b[3] and b[1] < a[3]


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def evaluate(manifest, manifest_sha256=None, now=None):
    if manifest.get("gate_version") != 1:
        raise ValueError("gate_version must be 1")
    if manifest.get("capture_mode") != "message_body_only":
        raise ValueError("capture_mode must be message_body_only")
    if not manifest.get("source_message_id") or not manifest.get("reviewer"):
        raise ValueError("source_message_id and reviewer are required")
    if manifest.get("visual_review_complete") is not True:
        raise ValueError("visual_review_complete must be true")
    if manifest.get("only_target_message_body") is not True:
        raise ValueError("only_target_message_body must be true")
    if manifest.get("no_ui_or_other_mail") is not True:
        raise ValueError("no_ui_or_other_mail must be true")
    try:
        reviewed = datetime.fromisoformat(manifest["reviewed_at"].replace("Z", "+00:00"))
    except (KeyError, ValueError) as exc:
        raise ValueError("reviewed_at must be an ISO timestamp") from exc
    if reviewed.tzinfo is None:
        raise ValueError("reviewed_at must contain a timezone")
    now = now or datetime.now(timezone.utc)
    age = (now - reviewed).total_seconds()
    if age < -300 or age > 86400:
        raise ValueError("visual review must be current (within 24 hours)")

    source = inside_private(manifest.get("source_path", ""))
    output = inside_private(manifest.get("output_path", ""))
    if source == output or not source.is_file():
        raise ValueError("source must exist and output must be a different file")
    if source.suffix.lower() != ".png" or output.suffix.lower() != ".png":
        raise ValueError("source and output must be PNG files")
    if digest(source) != manifest.get("source_sha256", "").lower():
        raise ValueError("source SHA-256 changed")

    with Image.open(source) as image:
        image.verify()
    with Image.open(source) as image:
        width, height = image.size
        content = box(manifest.get("message_body_bounds"), "message_body_bounds", width, height)
        crop = box(manifest.get("crop"), "crop", width, height)
        if crop[0] < content[0] or crop[1] < content[1] or crop[2] > content[2] or crop[3] > content[3]:
            raise ValueError("crop extends outside reviewed target message body")
        if crop == (0, 0, width, height):
            raise ValueError("whole-UI screenshot cannot be the deliverable")
        exclusions = manifest.get("excluded_regions")
        if not isinstance(exclusions, list):
            raise ValueError("excluded_regions must be a list")
        names = set()
        for exclusion in exclusions:
            name = exclusion.get("name")
            if not isinstance(name, str) or name in names:
                raise ValueError("excluded region names must be unique")
            names.add(name)
            excluded_box = box(exclusion.get("box"), name, width, height)
            if intersects(crop, excluded_box):
                raise ValueError(f"crop intersects excluded {name} region")
        if not REQUIRED_EXCLUSIONS.issubset(names):
            raise ValueError("mail navigation and message header/toolbar exclusion boxes are required")
        output.parent.mkdir(parents=True, exist_ok=True)
        image.crop(crop).save(output, format="PNG")

    receipt = {
        "status": "PASS",
        "manifest_sha256": manifest_sha256,
        "source_message_id": manifest["source_message_id"],
        "source_path": str(source),
        "source_sha256": digest(source),
        "output_path": str(output),
        "output_sha256": digest(output),
        "crop": list(crop),
        "reviewed_at": manifest["reviewed_at"],
        "created_at": now.isoformat(),
    }
    receipt_path = output.with_suffix(".receipt.json")
    receipt_path.write_text(json.dumps(receipt, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    return receipt


def verify(manifest, manifest_path, now=None):
    """Recheck the exact approved input and output immediately before sharing."""
    now = now or datetime.now(timezone.utc)
    output = inside_private(manifest.get("output_path", ""))
    source = inside_private(manifest.get("source_path", ""))
    receipt_path = output.with_suffix(".receipt.json")
    receipt = json.loads(receipt_path.read_text(encoding="utf-8"))
    if receipt.get("status") != "PASS":
        raise ValueError("receipt is not PASS")
    if receipt.get("manifest_sha256") != digest(manifest_path):
        raise ValueError("manifest changed since crop creation")
    if receipt.get("source_message_id") != manifest.get("source_message_id"):
        raise ValueError("target message changed")
    if receipt.get("source_path") != str(source) or receipt.get("output_path") != str(output):
        raise ValueError("source or output path changed")
    if not source.is_file() or not output.is_file():
        raise ValueError("source or cropped output is missing")
    if digest(source) != receipt.get("source_sha256") or digest(output) != receipt.get("output_sha256"):
        raise ValueError("source or cropped output changed")
    if receipt.get("crop") != manifest.get("crop"):
        raise ValueError("crop changed")
    for field in ("reviewed_at", "created_at"):
        timestamp = datetime.fromisoformat(receipt[field].replace("Z", "+00:00"))
        if timestamp.tzinfo is None or not -300 <= (now - timestamp).total_seconds() <= 86400:
            raise ValueError(f"{field} is not current")
    if manifest.get("visual_review_complete") is not True:
        raise ValueError("visual review is not complete")
    with Image.open(output) as image:
        image.verify()
    with Image.open(output) as image:
        crop = receipt["crop"]
        if image.size != (crop[2] - crop[0], crop[3] - crop[1]):
            raise ValueError("output dimensions do not match approved crop")
    return receipt


def main(argv):
    verify_only = len(argv) == 3 and argv[1] == "--verify"
    if not verify_only and len(argv) != 2:
        print("Usage: python tools/mail-capture-gate.py [--verify] <private-manifest.json>", file=sys.stderr)
        return 2
    try:
        manifest_arg = argv[2] if verify_only else argv[1]
        manifest_path = (ROOT / manifest_arg).resolve() if not Path(manifest_arg).is_absolute() else Path(manifest_arg).resolve()
        if not manifest_path.is_relative_to(PRIVATE):
            raise ValueError("manifest must be in private operations storage")
        manifest = json.loads(manifest_path.read_text(encoding="utf-8"))
        receipt = verify(manifest, manifest_path) if verify_only else evaluate(manifest, digest(manifest_path))
    except (OSError, ValueError, KeyError) as exc:
        print(f"MAIL CAPTURE GATE: BLOCKED - {exc}", file=sys.stderr)
        return 1
    print(f"MAIL CAPTURE GATE: PASS\nOutput: {receipt['output_path']}\nSHA-256: {receipt['output_sha256']}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
