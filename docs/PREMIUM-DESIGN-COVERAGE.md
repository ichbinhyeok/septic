# Premium design coverage

Local implementation on `codex/premium-visual-redesign`, 2026-09-20. Not deployed.

## Scope

All 30 public application page templates opt into an explicit design family in `layouts/app.jte`. This is template coverage, not a claim that every generated county/state URL received an individual visual inspection.

| Family | Templates |
| --- | --- |
| Signature | home, county-records-page, florida-ostds-records-page, offer-prep-septic-file-check, record-brief-example, tdec-records-page |
| Regional | north-carolina-records-page, south-carolina-records-page, texas-ossf-records-page, official-records-page, state-records-page |
| Directory | national-records-page, records-access-index, record-finder, state-coverage |
| Editorial | content-page, site-page, state-guide, state-money-page, state-guide-queued, workflow-packet-page, trust-operations-page |
| Tool | calculator, tank-size-estimator, drainfield-estimator, pump-schedule-estimator, bedroom-permit-checker |
| Service | contact-page |
| Transaction | paid-unlock |
| Recovery | not-found |

The two embedded tools and private event report are intentionally excluded. Existing URL routing, metadata generation, record lookup, pricing, and transaction authorization are preserved. Generated imagery is decorative, not evidence of a specific county/property outcome. Real source evidence remains separate and clickable.

## Verification

- Full Gradle test suite: 932 total, 914 passed, 18 skipped, zero failures/errors.
- `tools/qa-premium-pages.ps1` captures 32 representative public routes at 1440×1000 and 390×844, including advanced county variants, inquiry/review modes, and the expected 404 page.
- Browser checks record title, canonical, robots, H1 count, body family, and horizontal overflow. Both viewport runs have zero page-width overflow.
- Review gallery: `build/premium-qa/families/index.html`; machine-readable results: `audit-desktop.json` and `audit-mobile.json` beside it.
- Request builder has an existing additional H1 in a hidden task-continuation view; the audit reports DOM counts, not visible headings.
- No live payment, customer submission, production deployment, or search-ranking outcome was tested. Paid unlock receives the shared design family but was not opened using a real customer token. Queued and fallback templates compile but are not individually represented in the screenshot matrix.
- Updated two homepage assertions referencing removed decorative HTML to assert retained source-evidence copy. Tennessee browser test still uses a native click, but now waits for instant scrolling and hit-testing to settle.

## New art

`images/brand/septicpath-research-studio-v1.png` was generated for this iteration: premium photorealistic property-research desk, deep teal negative space on the left, ivory document folder/site plan on the right, readable generic research headings, no government seals, property addresses, approval claims, or UI cards. Used for the research studio/finder and TDEC visual direction. Headings, actions, factual copy, and forms remain HTML.

## Follow-up boundary

Before deployment, review the gallery and important below-fold interactions, optimize large image payloads, and compare production search/conversion baselines. Visual consistency alone does not establish improved SEO or conversion rates.
