# SEO Surface

Last updated: 2026-03-10

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
- state guide pages
- state-specific money pages

## Current Guardrails

- `quote-request` is disallowed in `robots.txt`
- 404 pages return `noindex,nofollow`
- state and money pages use absolute canonical URLs

## Next SEO Tasks

- add sitemap tests for every newly added state-specific page family
- add more FAQ-rich state-specific pages where official-source angle is already strong
