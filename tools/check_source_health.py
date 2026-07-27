#!/usr/bin/env python3
"""Audit official-source URLs without conflating HTTP reachability with editorial review."""

from __future__ import annotations

import argparse
import concurrent.futures
import csv
import datetime as dt
import hashlib
import io
import json
import pathlib
import re
import ssl
import subprocess
import sys
import time
import urllib.error
import urllib.parse
import urllib.request
from collections import Counter


USER_AGENT = "SepticPath-SourceMonitor/2.0 (+https://septicpath.com/about/)"
ACTIONABLE_CLASSIFICATIONS = {
    "dead",
    "dns_error",
    "certificate_hostname_error",
    "nonproduction_host",
    "persistent_server_error",
}
SUMMARY_CLASSIFICATIONS = [
    "healthy",
    "blocked",
    "rate_limited",
    "transient",
    "persistent_server_error",
    "dns_error",
    "certificate_hostname_error",
    "tls_error",
    "nonproduction_host",
    "inconclusive",
    "review",
    "dead",
]
NONPRODUCTION_LABELS = {"old", "temp", "uat", "test", "testing", "stage", "staging", "qa", "dev"}
HEALTH_COLUMNS = [
    "last_http_checked_at",
    "http_check_status",
    "last_content_verified_at",
    "verification_method",
    "review_status",
]


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("--registry", default="data/raw/source_registry.csv")
    parser.add_argument("--output", default="reports/source-health.json")
    parser.add_argument("--timeout", type=int, default=20)
    parser.add_argument("--workers", type=int, default=20)
    parser.add_argument("--retries", type=int, default=2)
    parser.add_argument("--retry-delay", type=float, default=0.5)
    parser.add_argument("--update-registry", action="store_true")
    parser.add_argument("--baseline-ref", help="Git ref used to identify date-only migrations")
    parser.add_argument("--fail-on-dead", action="store_true")
    parser.add_argument("--fail-on-actionable", action="store_true")
    return parser.parse_args()


def classify(status: int) -> str:
    if 200 <= status < 400:
        return "healthy"
    if status in {401, 403}:
        return "blocked"
    if status in {404, 410}:
        return "dead"
    if status == 429:
        return "rate_limited"
    if status >= 500:
        return "transient"
    if status == 0:
        return "inconclusive"
    return "review"


def header(headers, name: str) -> str:
    return headers.get(name, "") if headers is not None else ""


def nonproduction_host_marker(url: str) -> str:
    hostname = (urllib.parse.urlsplit(url).hostname or "").lower()
    for label in hostname.split("."):
        if label in NONPRODUCTION_LABELS:
            return label
        if len(label) > 3 and re.search(r"(?:uat|staging|testing)$", label):
            return re.search(r"(?:uat|staging|testing)$", label).group(0)
    return ""


def classify_network_error(error: str) -> str:
    lowered = error.lower()
    if any(token in lowered for token in [
        "name or service not known",
        "nodename nor servname",
        "getaddrinfo failed",
        "no address associated with hostname",
        "errno 11001",
    ]):
        return "dns_error"
    if "hostname mismatch" in lowered or "certificate is not valid for" in lowered:
        return "certificate_hostname_error"
    if "certificate_verify_failed" in lowered:
        return "tls_error"
    return "inconclusive"


def should_retry(result: dict[str, object]) -> bool:
    status = int(result.get("statusCode", 0))
    classification = str(result.get("classification", ""))
    return status >= 500 or classification in {
        "dns_error",
        "certificate_hostname_error",
        "tls_error",
        "inconclusive",
    }


def confirm_dns_nxdomain(url: str, timeout: int) -> bool | None:
    hostname = urllib.parse.urlsplit(url).hostname or ""
    if not hostname:
        return True
    query = urllib.parse.urlencode({"name": hostname, "type": "A"})
    request = urllib.request.Request(
        f"https://dns.google/resolve?{query}",
        headers={"Accept": "application/dns-json", "User-Agent": USER_AGENT},
    )
    try:
        with urllib.request.urlopen(request, timeout=min(timeout, 10)) as response:
            payload = json.loads(response.read(65536).decode("utf-8"))
        status = int(payload.get("Status", -1))
        if status == 3:
            return True
        if status == 0:
            return False
    except Exception:
        return None
    return None


def audit_url_once(item: dict[str, object], timeout: int) -> dict[str, object]:
    url = str(item["url"])
    request = urllib.request.Request(
        url,
        headers={
            "User-Agent": USER_AGENT,
            "Accept": "text/html,application/pdf,application/xhtml+xml,*/*;q=0.5",
            "Range": "bytes=0-65535",
        },
    )
    started = dt.datetime.now(dt.timezone.utc)
    status = 0
    final_url = url
    headers = None
    sample = b""
    error = ""

    try:
        context = ssl.create_default_context()
        with urllib.request.urlopen(request, timeout=timeout, context=context) as response:
            status = int(response.status)
            final_url = response.geturl()
            headers = response.headers
            sample = response.read(65536)
    except urllib.error.HTTPError as exception:
        status = int(exception.code)
        final_url = exception.geturl() or url
        headers = exception.headers
        try:
            sample = exception.read(65536)
        except Exception:
            sample = b""
        error = str(exception)
    except Exception as exception:
        error = f"{type(exception).__name__}: {exception}"

    elapsed = dt.datetime.now(dt.timezone.utc) - started
    classification = classify(status) if status else classify_network_error(error)
    return {
        "url": url,
        "finalUrl": final_url,
        "sourceIds": item["sourceIds"],
        "agencies": item["agencies"],
        "statusCode": status,
        "classification": classification,
        "contentType": header(headers, "Content-Type"),
        "contentLength": header(headers, "Content-Length"),
        "etag": header(headers, "ETag"),
        "lastModified": header(headers, "Last-Modified"),
        "sampleSha256": hashlib.sha256(sample).hexdigest() if sample else "",
        "durationMs": round(elapsed.total_seconds() * 1000),
        "error": error,
    }


def audit_url(
    item: dict[str, object],
    timeout: int,
    retries: int = 2,
    retry_delay: float = 0.5,
) -> dict[str, object]:
    url = str(item["url"])
    marker = nonproduction_host_marker(url)
    if marker:
        return {
            "url": url,
            "finalUrl": url,
            "sourceIds": item["sourceIds"],
            "agencies": item["agencies"],
            "statusCode": 0,
            "classification": "nonproduction_host",
            "contentType": "",
            "contentLength": "",
            "etag": "",
            "lastModified": "",
            "sampleSha256": "",
            "durationMs": 0,
            "attempts": 0,
            "error": f"Non-production hostname marker detected: {marker}",
        }

    result: dict[str, object] = {}
    attempts = max(1, retries + 1)
    for attempt in range(1, attempts + 1):
        result = audit_url_once(item, timeout)
        result["attempts"] = attempt
        if not should_retry(result) or attempt == attempts:
            break
        if retry_delay > 0:
            time.sleep(retry_delay)

    if int(result.get("statusCode", 0)) >= 500 and int(result.get("attempts", 1)) >= attempts:
        result["classification"] = "persistent_server_error"
    if result.get("classification") == "dns_error":
        confirmed = confirm_dns_nxdomain(url, timeout)
        if confirmed is not True:
            result["classification"] = "inconclusive"
            suffix = (
                "Public DNS still resolves this hostname."
                if confirmed is False
                else "Public DNS confirmation was unavailable."
            )
            result["error"] = f"{result.get('error', '')} {suffix}".strip()
    return result


def read_registry(path: pathlib.Path) -> tuple[list[dict[str, str]], list[str]]:
    with path.open("r", encoding="utf-8-sig", newline="") as handle:
        reader = csv.DictReader(handle)
        return list(reader), list(reader.fieldnames or [])


def read_baseline(ref: str, registry_path: pathlib.Path) -> dict[str, dict[str, str]]:
    repo_path = registry_path.as_posix()
    raw = subprocess.check_output(
        ["git", "show", f"{ref}:{repo_path}"],
        text=True,
        encoding="utf-8-sig",
    )
    return {row["source_id"]: row for row in csv.DictReader(io.StringIO(raw))}


def review_status_for(health: str, content_date: str) -> str:
    if health == "healthy":
        return "content_reviewed_http_healthy" if content_date else "content_review_due"
    if health in ACTIONABLE_CLASSIFICATIONS:
        return "replacement_required"
    if health in {"blocked", "rate_limited", "transient", "tls_error", "inconclusive"}:
        return "manual_http_review"
    return "review_required"


def update_registry(
    registry_path: pathlib.Path,
    rows: list[dict[str, str]],
    fieldnames: list[str],
    results_by_url: dict[str, dict[str, object]],
    checked_date: str,
    baseline_ref: str | None,
) -> dict[str, int]:
    baseline = read_baseline(baseline_ref, registry_path) if baseline_ref else {}
    restored_date_only = 0
    replacement_reviews = 0

    for row in rows:
        previous = baseline.get(row.get("source_id", ""))
        method = row.get("verification_method", "").strip()
        if previous:
            legacy_fields = [name for name in previous if name != "last_verified_at"]
            substantive_change = any(previous.get(name, "") != row.get(name, "") for name in legacy_fields)
            date_changed = previous.get("last_verified_at", "") != row.get("last_verified_at", "")
            if date_changed and not substantive_change:
                row["last_verified_at"] = previous.get("last_verified_at", "")
                restored_date_only += 1
                method = "legacy_editorial_review"
            elif substantive_change:
                replacement_reviews += 1
                method = "source_replacement_review"

        content_date = row.get("last_content_verified_at", "").strip()
        if not content_date:
            content_date = row.get("last_verified_at", "").strip()
        row["last_content_verified_at"] = content_date
        row["verification_method"] = method or "editorial_registry"

        result = results_by_url.get(row.get("url", "").strip(), {})
        health = str(result.get("classification", "inconclusive"))
        row["last_http_checked_at"] = checked_date
        row["http_check_status"] = health
        row["review_status"] = review_status_for(health, content_date)

    output_fields = fieldnames + [name for name in HEALTH_COLUMNS if name not in fieldnames]
    with registry_path.open("w", encoding="utf-8", newline="") as handle:
        writer = csv.DictWriter(handle, fieldnames=output_fields, lineterminator="\n")
        writer.writeheader()
        writer.writerows(rows)
    return {
        "restoredDateOnlyVerifications": restored_date_only,
        "sourceReplacementReviews": replacement_reviews,
    }


def main() -> int:
    args = parse_args()
    registry_path = pathlib.Path(args.registry)
    output_path = pathlib.Path(args.output)
    rows, fieldnames = read_registry(registry_path)

    grouped: dict[str, dict[str, object]] = {}
    for row in rows:
        url = row.get("url", "").strip()
        if not url:
            continue
        item = grouped.setdefault(url, {"url": url, "sourceIds": [], "agencies": []})
        item["sourceIds"].append(row.get("source_id", ""))
        agency = row.get("agency_name", "").strip()
        if agency and agency not in item["agencies"]:
            item["agencies"].append(agency)

    with concurrent.futures.ThreadPoolExecutor(max_workers=args.workers) as executor:
        results = list(executor.map(
            lambda item: audit_url(item, args.timeout, args.retries, args.retry_delay),
            grouped.values(),
        ))

    results.sort(key=lambda item: (str(item["classification"]), str(item["url"])))
    counts = Counter(str(item["classification"]) for item in results)
    checked_at = dt.datetime.now(dt.timezone.utc).isoformat()
    checked_date = checked_at[:10]
    migration = {}
    if args.update_registry:
        migration = update_registry(
            registry_path,
            rows,
            fieldnames,
            {str(item["url"]): item for item in results},
            checked_date,
            args.baseline_ref,
        )

    report = {
        "checkedAt": checked_at,
        "registry": str(registry_path.resolve()),
        "uniqueUrlCount": len(results),
        "policy": (
            "HTTP reachability is recorded separately from editorial content verification. "
            "Confirmed 404/410 responses, DNS failures, hostname-certificate mismatches, "
            "non-production hosts, and server errors that persist through retries are actionable."
        ),
        "summary": {name: counts.get(name, 0) for name in SUMMARY_CLASSIFICATIONS},
        "migration": migration,
        "results": results,
    }
    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding="utf-8")
    print(json.dumps(report["summary"], separators=(",", ":")))
    print(f"Source health report: {output_path}")
    if args.fail_on_dead and counts.get("dead", 0):
        return 1
    if args.fail_on_actionable and any(counts.get(name, 0) for name in ACTIONABLE_CLASSIFICATIONS):
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
