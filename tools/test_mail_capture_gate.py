"""Regression checks for the customer-visible email screenshot gate."""

import importlib.util
import json
import tempfile
import unittest
from datetime import datetime, timezone
from pathlib import Path

from PIL import Image


SCRIPT = Path(__file__).with_name("mail-capture-gate.py")
SPEC = importlib.util.spec_from_file_location("mail_capture_gate", SCRIPT)
gate = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(gate)


class MailCaptureGateTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory(dir=gate.PRIVATE)
        self.addCleanup(self.temp.cleanup)
        self.folder = Path(self.temp.name)
        self.source = self.folder / "source.png"
        self.output = self.folder / "body.png"
        self.manifest_path = self.folder / "manifest.json"
        Image.new("RGB", (400, 300), "white").save(self.source)
        self.manifest = {
            "gate_version": 1,
            "capture_mode": "message_body_only",
            "source_message_id": "test-message",
            "source_path": str(self.source.relative_to(gate.PRIVATE)),
            "source_sha256": gate.digest(self.source),
            "output_path": str(self.output.relative_to(gate.PRIVATE)),
            "message_body_bounds": [100, 100, 390, 250],
            "crop": [110, 110, 380, 240],
            "excluded_regions": [
                {"name": "mail_navigation", "box": [0, 0, 90, 300]},
                {"name": "message_header_or_toolbar", "box": [90, 0, 400, 90]},
            ],
            "reviewer": "test visual reviewer",
            "reviewed_at": datetime.now(timezone.utc).isoformat(),
            "visual_review_complete": True,
            "only_target_message_body": True,
            "no_ui_or_other_mail": True,
        }

    def write_manifest(self):
        self.manifest_path.write_text(json.dumps(self.manifest), encoding="utf-8")

    def test_body_crop_passes_and_changed_output_blocks_share(self):
        self.write_manifest()
        gate.evaluate(self.manifest, gate.digest(self.manifest_path))
        self.assertEqual(gate.verify(self.manifest, self.manifest_path)["status"], "PASS")
        Image.new("RGB", (270, 130), "black").save(self.output)
        with self.assertRaisesRegex(ValueError, "cropped output changed"):
            gate.verify(self.manifest, self.manifest_path)

    def test_full_ui_and_header_intersection_are_blocked(self):
        self.manifest["message_body_bounds"] = [0, 0, 400, 300]
        self.manifest["crop"] = [0, 0, 400, 300]
        with self.assertRaises(ValueError):
            gate.evaluate(self.manifest)
        self.manifest["crop"] = [110, 80, 380, 240]
        with self.assertRaisesRegex(ValueError, "intersects excluded"):
            gate.evaluate(self.manifest)

    def test_changed_source_or_manifest_blocks(self):
        self.write_manifest()
        gate.evaluate(self.manifest, gate.digest(self.manifest_path))
        self.manifest["crop"] = [110, 110, 370, 240]
        self.write_manifest()
        with self.assertRaisesRegex(ValueError, "manifest changed"):
            gate.verify(self.manifest, self.manifest_path)
        self.manifest["crop"] = [110, 110, 380, 240]
        Image.new("RGB", (400, 300), "black").save(self.source)
        with self.assertRaisesRegex(ValueError, "source SHA-256 changed"):
            gate.evaluate(self.manifest)


if __name__ == "__main__":
    unittest.main()
