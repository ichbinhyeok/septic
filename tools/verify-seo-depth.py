"""Read-only HTTP release check. Usage: python tools/verify-seo-depth.py BASE OUT.json"""
import hashlib
import json
import sys
import urllib.request
import xml.etree.ElementTree as ET
from datetime import datetime, timezone
from pathlib import Path
from bs4 import BeautifulSoup

base = sys.argv[1].rstrip('/')
targets = {
    '/perc-test-cost/south-carolina/': ['$150', 'Agency charge only.', 'SCDES'],
    '/perc-test-cost/oregon/': ['$1,121', 'County-agent fees may differ', 'July 1, 2026'],
    '/septic-records-checklist/north-carolina/henderson-county/': ['Last,First', 'Permit Notes', 'Operations Permit'],
    '/septic-records-checklist/ohio/lake-county/': ['Wood-Crest', 'house number'],
    '/north-carolina-septic-permit-lookup/': ['Searching Henderson County?'],
    '/septic-records-checklist/ohio/': ['Lake County, Ohio: find the address'],
    '/septic-records-checklist/new-hampshire/': ['OneStop: approval found', 'most post-1986', 'after 2015'],
    '/onlinerme-septic-records/': ['PSSD', 'specific to Loudoun'],
    '/septicsearch-records/': ['Fictional search example', 'septicsearch-no-results'],
    '/septic-as-built-records/': ['septic-as-built-dimensions-explained.webp'],
    '/how-to-read-septic-as-built/': ['septic-as-built-dimensions-explained.webp'],
}

def fetch(path):
    with urllib.request.urlopen(base + path, timeout=30) as response:
        return response.status, response.headers, response.read()

report = {'base': base, 'checked_at': datetime.now(timezone.utc).isoformat(), 'pages': [], 'assets': []}
sitemaps = {}
image_parents = []
for path in ['/sitemap.xml', '/sitemap-county.xml']:
    status, headers, body = fetch(path)
    assert status == 200
    root = ET.fromstring(body)
    for entry in root:
        loc = entry.find('{http://www.sitemaps.org/schemas/sitemap/0.9}loc').text
        lastmod = entry.find('{http://www.sitemaps.org/schemas/sitemap/0.9}lastmod')
        sitemaps[loc] = lastmod.text if lastmod is not None else None
        for image in entry.findall('{http://www.google.com/schemas/sitemap-image/1.1}image'):
            image_parents.append({'page': loc, 'image': image[0].text})

for path, markers in targets.items():
    status, headers, raw = fetch(path)
    html = raw.decode('utf-8')
    soup = BeautifulSoup(html, 'html.parser')
    canonical = soup.find('link', rel='canonical')['href']
    robots = soup.find('meta', attrs={'name': 'robots'})['content']
    assert status == 200 and canonical == 'https://septicpath.com' + path, path
    assert 'noindex' not in robots and 'noindex' not in headers.get('X-Robots-Tag', ''), path
    assert len(soup.find_all('h1')) == 1, path
    assert sitemaps.get(canonical) == '2026-11-18', path
    for marker in markers:
        assert marker in html, (path, marker)
    for script in soup.find_all('script', type='application/ld+json'):
        json.loads(script.string or script.get_text())
    report['pages'].append({'path': path, 'status': status, 'canonical': canonical, 'robots': robots,
                            'lastmod': sitemaps[canonical], 'title': soup.title.get_text(),
                            'markers_pass': True, 'sha256': hashlib.sha256(raw).hexdigest()})

for extension in ['webp', 'png']:
    path = '/images/guides/septic-as-built-dimensions-explained.' + extension
    status, headers, raw = fetch(path)
    expected = Path('src/main/resources/static' + path).read_bytes()
    assert status == 200 and raw == expected, path
    report['assets'].append({'path': path, 'status': status, 'bytes': len(raw), 'sha256': hashlib.sha256(raw).hexdigest()})
assert len(image_parents) == 2
assert {item['page'] for item in image_parents} == {
    'https://septicpath.com/septic-as-built-records/', 'https://septicpath.com/how-to-read-septic-as-built/'}
report['image_sitemap'] = image_parents
report['result'] = 'PASS'
Path(sys.argv[2]).write_text(json.dumps(report, indent=2) + '\n', encoding='utf-8')
print('PASS:', len(report['pages']), 'pages,', len(report['assets']), 'matching image assets, 2 image sitemap associations')
