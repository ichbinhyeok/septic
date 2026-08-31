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

## Next SEO Tasks

- monitor indexed-versus-submitted movement and reopen state cohorts only when Search Console demand or stronger first-party evidence supports it
- deepen FAQ and decision content on demand-backed state pages before expanding the indexable footprint
