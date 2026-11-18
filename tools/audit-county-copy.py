"""Compare published county-page copy using the county sitemap."""

import argparse
from collections import Counter, defaultdict
from concurrent.futures import ThreadPoolExecutor
from datetime import datetime, timezone
from itertools import combinations
import json
from pathlib import Path
import re
from urllib.parse import urlsplit
import xml.etree.ElementTree as ET

from bs4 import BeautifulSoup
from rapidfuzz import fuzz
import requests


COUNTY_PATH = re.compile(r"/septic-records-checklist/([a-z0-9-]+)/([a-z0-9-]+)/")
COUNTY_SUFFIX = re.compile(r"-(?:county|parish|borough|census-area|municipality|city)$")
SECTIONS = {"main": "main", "evidence": ".county-research-disclosure"}


def fetch(url):
    response = requests.get(url, timeout=25, allow_redirects=False)
    if response.status_code != 200:
        raise ValueError(f"HTTP {response.status_code}")
    return response


def county_urls(base):
    sitemap = ET.fromstring(fetch(base + "/sitemap-county.xml").content)
    urls = {loc.text.strip() for loc in sitemap.findall("./{*}url/{*}loc") if loc.text}
    if not urls:
        raise ValueError("The county sitemap contains no URLs")
    for url in sorted(urls):
        path = urlsplit(url).path
        if not COUNTY_PATH.fullmatch(path) or url != base + path:
            raise ValueError(f"Unexpected county sitemap URL: {url}")
    return sorted(urls)


def normalize_paragraphs(section, county_pattern):
    if section is None:
        return []
    paragraphs = []
    for paragraph in section.find_all("p"):
        value = " ".join(paragraph.get_text(" ", strip=True).split()).casefold()
        if value:
            paragraphs.append(county_pattern.sub("[county]", value))
    return paragraphs


def read_page(url):
    try:
        response = fetch(url)
        if "text/html" not in response.headers.get("Content-Type", "").lower():
            raise ValueError("Expected an HTML page")
        page = BeautifulSoup(response.content, "html.parser")
        canonical = page.select_one('link[rel="canonical"][href]')
        if canonical is None or canonical["href"] != url:
            raise ValueError("Expected a self-canonical page")
        robots = [response.headers.get("X-Robots-Tag", "")]
        robots.extend(tag.get("content", "") for tag in page.select('meta[name="robots"]'))
        if re.search(r"\b(?:noindex|none)\b", " ".join(robots), re.I):
            raise ValueError("Sitemap URL has a noindex directive")
        state, county = COUNTY_PATH.fullmatch(urlsplit(url).path).groups()
        words = COUNTY_SUFFIX.sub("", county).split("-")
        county_pattern = re.compile(r"\b" + r"[.\s-]+".join(map(re.escape, words)) + r"\b")
        paragraphs = {
            name: normalize_paragraphs(page.select_one(selector), county_pattern)
            for name, selector in SECTIONS.items()
        }
        if not paragraphs["main"]:
            raise ValueError("No main paragraphs found")
        source_links = sorted({link["href"] for link in page.select(
            'main a[data-track-target-type="official_source"][href]'
        )})
        return {"url": url, "state": state, "paragraphs": paragraphs, "source_links": source_links}
    except (ValueError, requests.RequestException) as exc:
        return {"url": url, "error": str(exc)}


def compare(left, right):
    scores = {}
    for section in SECTIONS:
        a, b = (page["paragraphs"][section] for page in (left, right))
        scores[section] = {
            "similarity": round(fuzz.ratio("\n".join(a), "\n".join(b)), 2) if a and b else None,
            "shared_distinct_paragraphs": len(set(a) & set(b)),
        }
    return {
        "urls": [left["url"], right["url"]],
        **scores,
        "different_tagged_source_links": left["source_links"] != right["source_links"],
    }


def state_report(pages):
    pairs = [compare(left, right) for left, right in combinations(pages, 2)]
    pairs.sort(key=lambda pair: (-pair["main"]["similarity"], pair["urls"]))
    evidence_counts = Counter()
    for page in pages:
        evidence_counts.update(set(page["paragraphs"]["evidence"]))
    recurring_evidence = sorted(evidence_counts.items(), key=lambda item: (-item[1], item[0]))
    return {
        "page_count": len(pages),
        "evidence_page_count": sum(bool(page["paragraphs"]["evidence"]) for page in pages),
        "pairs": pairs,
        "recurring_evidence": [
            {"paragraph": paragraph, "page_count": count}
            for paragraph, count in recurring_evidence if count > 1
        ],
    }


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("base", help="Site origin, such as https://septicpath.com")
    parser.add_argument("output", type=Path, help="Path to the JSON report")
    args = parser.parse_args()
    base = args.base.rstrip("/")
    try:
        urls = county_urls(base)
    except (ValueError, requests.RequestException, ET.ParseError) as exc:
        parser.exit(1, f"Unable to read county sitemap: {exc}\n")

    by_state = defaultdict(list)
    failures = []
    with ThreadPoolExecutor(max_workers=4) as pool:
        for page in pool.map(read_page, urls):
            if "error" in page:
                failures.append(page)
            else:
                by_state[page["state"]].append(page)

    states = {state: state_report(pages) for state, pages in sorted(by_state.items())}
    report = {
        "sitemap": base + "/sitemap-county.xml",
        "checked_at": datetime.now(timezone.utc).isoformat(),
        "sitemap_page_count": len(urls),
        "compared_page_count": len(urls) - len(failures),
        "failures": failures,
        "states": states,
        "pages": [{
            "url": page["url"],
            "paragraph_counts": {name: len(content) for name, content in page["paragraphs"].items()},
            "tagged_source_links": page["source_links"],
        } for pages in by_state.values() for page in pages],
    }
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(report, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    print(f"Compared {report['compared_page_count']}/{len(urls)} pages across {len(states)} states. "
          f"Fetch or validation failures: {len(failures)}. Report: {args.output}")
    return 1 if failures else 0


if __name__ == "__main__":
    raise SystemExit(main())
