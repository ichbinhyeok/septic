# GSC index audit — 2026-07-25

## Scope

Live Google Search Console review for `septicpath.com`. The Page indexing
report itself was last updated on July 10, so this is a triage baseline rather
than a post-release measurement.

## Observed cohorts

| Cohort | Count | First visible URL samples | Triage |
| --- | ---: | --- | --- |
| Crawled — currently not indexed | 189 | Anne Arundel County records, California replacement cost, offer-prep file check, records access index, Kenosha County records | Audit individually only when the page has demand or is a records hub. |
| Discovered — currently not indexed | 238 | About page; buyer workflow root; buyer workflow pages for Alabama, Arizona, Arkansas, California, Connecticut, Delaware, Florida, Illinois | Mostly broad state workflow variants. Confirm their publishing policy before attempting to index them. |

One crawled-not-indexed sample was a calculator URL with query parameters:

`/septic-system-cost-calculator/?state=AZ&projectType=inspection&sourcePageHint=/septic-inspection-cost/arizona/`

That is a parameterized tool state, not an SEO landing page. Its canonical
handling should remain separate from the decision to index editorial pages.

## Do not do

- Do not request indexing for every excluded URL.
- Do not change canonical tags or sitemap dates in bulk.
- Do not add noindex workflow variants to sitemaps simply to raise index count.

## Next review set

Inspect these pages first because they are either records hubs or county pages
with a plausible search-use case:

1. `/septic-records-access-index/`
2. `/offer-prep-septic-file-check/`
3. `/septic-records-checklist/maryland/anne-arundel-county/`
4. `/septic-records-checklist/wisconsin/kenosha-county/`
5. `/septic-records-checklist/virginia/loudoun-county/`
6. `/septic-records-checklist/nevada/carson-city/`
7. `/septic-replacement-cost/california/`
8. `/drain-field-replacement-cost/colorado/`
9. `/septic-inspection-cost/utah/`

For each URL, record the Google-selected canonical, last crawl, sitemap
membership, referrers, rendered page content, and whether it has current
impressions or conversion activity. Promote only pages with a distinct official
route or observed search demand.
