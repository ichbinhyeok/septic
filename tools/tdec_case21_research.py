#!/usr/bin/env python3
"""One-off public-record lookup for 421 Dry Weakley Rd."""

import json
import mimetypes
import os
import re
import urllib.error
import urllib.request
from pathlib import Path


BASE = "https://tdec.tn.gov"
OUT = Path(os.environ.get("TDEC_OUTPUT", "/tmp/tdec_case21_output"))
OUT.mkdir(parents=True, exist_ok=True)

HEADERS = {
    "Accept": "application/json, text/plain, */*",
    "Content-Type": "application/json",
    "Origin": BASE,
    "Referer": f"{BASE}/document-viewer/search/stp",
    "User-Agent": "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 Chrome/140 Safari/537.36",
}


def call(path, payload=None):
    data = None if payload is None else json.dumps(payload).encode("utf-8")
    req = urllib.request.Request(BASE + path, data=data, headers=HEADERS)
    try:
        with urllib.request.urlopen(req, timeout=60) as response:
            return response.status, dict(response.headers), response.read()
    except urllib.error.HTTPError as exc:
        return exc.code, dict(exc.headers), exc.read()
    except Exception as exc:  # Preserve transport failures in the artifact.
        return 0, {}, repr(exc).encode("utf-8")


def safe_name(value):
    return re.sub(r"[^A-Za-z0-9._-]+", "_", str(value)).strip("._")[:100] or "document"


status, headers, body = call("/docview_api/publicConfig/getbyid/stp")
(OUT / "config_body.json").write_bytes(body)

results_fields = ["County", "StreetAddress", "ZipCode", "Subdivision", "LotNumber", "PropertyOwner"]
criteria_sets = [
    {"County": ["50"], "StreetAddress": "421 DRY WEAKLEY"},
    {"County": ["50"], "StreetAddress": "DRY WEAKLEY"},
    {"StreetAddress": "421 DRY WEAKLEY RD", "ZipCode": "38456"},
    {"StreetAddress": "421 DRY WEAKLEY CREEK RD", "ZipCode": "38456"},
    {"ParcelID": "058 00601 000"},
    {"ParcelID": "058 00601"},
    {"ParcelID": "050058 00601"},
    {"MapAndGroupID": "058 00601"},
]

summary = {
    "config_status": status,
    "config_content_type": headers.get("Content-Type", ""),
    "searches": [],
    "unique_result_count": 0,
    "downloads": [],
}
unique = {}

for index, criteria in enumerate(criteria_sets, 1):
    payload = {
        "classId": "SubServiceSewageDisposal",
        "criteria": criteria,
        "results": results_fields,
    }
    status, response_headers, body = call("/docview_api/publicDoc/search", payload)
    raw_name = f"search_{index:02d}.json"
    (OUT / raw_name).write_bytes(body)
    entry = {
        "index": index,
        "criteria": criteria,
        "status": status,
        "content_type": response_headers.get("Content-Type", ""),
        "body_file": raw_name,
    }
    try:
        parsed = json.loads(body)
        entry["result_count"] = len(parsed) if isinstance(parsed, list) else None
        if isinstance(parsed, list):
            for item in parsed:
                if isinstance(item, dict):
                    key = str(item.get("Id") or json.dumps(item, sort_keys=True))
                    unique[key] = item
    except Exception as exc:
        entry["parse_error"] = repr(exc)
        entry["body_preview"] = body[:500].decode("utf-8", "replace")
    summary["searches"].append(entry)

summary["unique_result_count"] = len(unique)
(OUT / "combined_results.json").write_text(
    json.dumps(list(unique.values()), indent=2, ensure_ascii=False), encoding="utf-8"
)

for number, item in enumerate(unique.values(), 1):
    document_id = item.get("Id")
    if not document_id:
        continue
    status, response_headers, body = call(f"/docview_api/publicDoc/download/{document_id}")
    disposition = response_headers.get("Content-Disposition", "")
    match = re.search(r"filename\*?=(?:UTF-8''|\")?([^\";]+)", disposition, re.I)
    title = item.get("DocumentTitle") or item.get("Title") or (match.group(1) if match else "")
    content_type = response_headers.get("Content-Type", "application/octet-stream").split(";", 1)[0]
    extension = mimetypes.guess_extension(content_type) or ".bin"
    if title and Path(str(title)).suffix:
        extension = Path(str(title)).suffix
    filename = f"document_{number:03d}_{safe_name(title or document_id)}"
    if not filename.lower().endswith(extension.lower()):
        filename += extension
    (OUT / filename).write_bytes(body)
    summary["downloads"].append({
        "id": document_id,
        "status": status,
        "content_type": content_type,
        "bytes": len(body),
        "file": filename,
        "source_result": item,
    })

(OUT / "summary.json").write_text(json.dumps(summary, indent=2, ensure_ascii=False), encoding="utf-8")
print(json.dumps({"config_status": summary["config_status"], "unique_result_count": len(unique), "downloads": len(summary["downloads"])}))

