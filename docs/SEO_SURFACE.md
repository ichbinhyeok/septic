# SEO Surface

Last updated: 2026-03-09

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

Set `app.site.base-url` to the real production origin before launch.

Current default:

- `http://localhost:8080`

This value drives:

- canonical URLs
- sitemap URLs
- robots sitemap pointer
- structured data URLs

## Included In Sitemap

- home page
- main calculator
- national money pages
- state guide pages
- state-specific money pages

## Current Guardrails

- `quote-request` is disallowed in `robots.txt`
- 404 pages return `noindex,nofollow`
- state and money pages use absolute canonical URLs

## Next SEO Tasks

- add simple `Organization` / `LocalBusiness`-safe site entity only after a real business identity and domain are finalized
- add sitemap tests for every newly added state-specific page family
- add more FAQ-rich state-specific pages where official-source angle is already strong
