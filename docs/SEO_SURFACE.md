# SEO Surface

Last updated: 2026-09-01

## What Exists

- `robots.txt`
- `sitemap.xml`
- canonical tags
- robots meta
- basic Open Graph / Twitter meta
- JSON-LD blocks for:
  - `WebSite`
  - `WebPage`
  - `FAQPage`
  - `BreadcrumbList`

## Production Requirement

Default production origin is `https://septicpath.com`.

Override `app.site.base-url` with `APP_SITE_BASE_URL` in non-production environments.

Examples:

- local: `APP_SITE_BASE_URL=http://localhost:8080`
- preview: `APP_SITE_BASE_URL=https://preview-host.example`

This value drives:

- canonical URLs
- sitemap URLs
- robots sitemap pointer
- structured data URLs

The app now also issues a permanent redirect when the incoming request is the same site on `http` or `www`, so production should converge on `https://septicpath.com`.

## Included In Sitemap

- home page
- main calculator
- national money pages
- Search Console demand-backed state guide pages
- evidence- and demand-backed state-specific money and workflow pages
- county records pages in the separate `sitemap-county.xml`

Useful state routes that have not yet earned enough search demand remain available to users and crawlers with `noindex,follow`, but stay out of the XML sitemap until the publishing policy reopens them.

## Current Guardrails

- `quote-request` is disallowed in `robots.txt`
- 404 pages return `noindex,nofollow`
- state and money pages use absolute canonical URLs
- sitemap inclusion and page-level robots directives share the same publishing policy
- regression coverage checks sitemap and robots-policy alignment across every published state guide and state money page

## County Template Audit

Run the read-only comparison against production or a preview with a matching site origin.

```sh
python -m pip install beautifulsoup4 requests rapidfuzz==3.14.6
python tools/audit-county-copy.py https://septicpath.com build/reports/county-copy-audit.json
```

The audit reads `sitemap-county.xml` and compares county pages within each state. It normalizes each page's own county name and reports paragraph similarity separately for the main content and the expandable county evidence section. The report includes recurring evidence paragraphs and the URLs of links tagged as official sources. Use these alongside the page to decide which copy to shorten and which local steps to keep. A similarity score is an editorial signal. An unreadable or invalid sitemap page is recorded as an error and makes the command exit nonzero.

## Next SEO Tasks

- monitor indexed-versus-submitted movement and reopen state cohorts only when Search Console demand or stronger first-party evidence supports it
- deepen FAQ and decision content on demand-backed state pages before expanding the indexable footprint
