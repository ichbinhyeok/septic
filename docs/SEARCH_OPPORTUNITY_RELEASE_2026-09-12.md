# Search opportunity implementation — 2026-09-12

## Measurement contract

- Business outcome: `record_help_request_submitted` = server accepted an inquiry. Not email delivery, a recovered file, a paid customer, or revenue.
- GA4 property 527957430: marked this existing event as a key event on September 12. Existing tool key events remain for continuity; never report the aggregate key-event count as inquiries.
- Event-scoped custom definitions: source_context, entry_page, source_page, state_code, county_key, outcome, request_type, cta_variant. New registration is prospective; historical parameter-level breakdowns are not backfilled.
- Self-service: record_finder_submit, county_official_route_opened. Report separately from assisted-service requests. GA4 engagement can be inflated by these key events.
- No address, parcel ID, email, uploaded text, or unique workflow ID belongs in a custom definition. Property carryover uses existing browser storage and the inquiry form, not URL/analytics parameters.
- Separate US organic from all traffic, and Google from Bing/Yahoo/DuckDuckGo. Keep unidentified landing traffic visible. Do not classify country alone as bots or activate a destructive traffic filter.

## Baseline and comparison

Source: `output/opportunity-audit-2026-09-12/source-data.json` and executed `opportunity-audit.ipynb` (local ignored audit artifacts).
GA4 timezone: America/Los_Angeles. GSC: final web data, sc-domain:septicpath.com.

| Metric | Jul 16–Aug 12 | Aug 13–Sep 9 |
|---|---:|---:|
| GSC site clicks | 549 | 522 |
| GSC site impressions | 22,243 | 21,482 |
| Finder organic sessions | 128 | 163 |
| NC dedicated tool organic sessions | 19 | 41 |
| SC dedicated tool organic sessions | 24 | 45 |
| TDEC page Google clicks | 140 | 114 |
| TDEC page impressions | 6,076 | 6,483 |
| TDEC CTR | 2.304% | 1.758% |
| TDEC average position | 6.12 | 6.20 |

The TDEC title changed September 10; proof deployed September 12. Do not retitle before measuring the existing change. GSC last observed crawl was September 9. Record the first subsequent crawl and exclude deployment/partial days. Compare equal complete 14- and 28-day windows with weekday alignment; also inspect query mix and position. Do not attribute all movement to the title when content and proof changed too.

Reproduce GSC with dimensions [page], page equals https://septicpath.com/tdec-septic-records/, dataState final, type web, aggregationType byPage. Query-page rows cover only part of total clicks. The earlier device-filtered export did not reconcile (50 vs 114 clicks); do not infer device causes from it.

For GA4 compare sessionSourceMedium + landingPage (organic) separately from eventName-filtered inquiries. New entry_page/source_page definitions let inquiry events be grouped without mistakenly using the form URL as the entrance. Event counts at consecutive steps are not an ordered cohort funnel.

September 5–9 US organic: CTA seen 124 users; clicked 12; form viewed 12; started 5; submitted 3. Small, separate audiences; not paid-demand proof. Do not use pre-September 5 missing events as zero historical inquiries.

## Product changes

- Finder: free input first, assisted option after form, help from results, actual anonymized originals and delivered explanation below the tool.
- Official return: found-file explanation versus continued investigation for blank/blocked/wrong-office/no-record outcomes. Pending requests retain follow-up actions rather than encouraging duplicates.
- Dynamic outcome CTA impressions are now observed; existing delegated clicks retain context carryover.
- Legacy state records pages (including OK, ME, AR, MO) receive the shared actual-case proof; costs/permits pages do not receive it indiscriminately.
- Florida/Texas dedicated tools: separate tool limitations from human research capability, shared proof with explicit Shelby County TN attribution.
- NC: existing Forsyth/Henderson/Johnston routes surfaced first, not new thin pages.
- Alabama cost: small existing-property record check after the cost tool, not a price-intent takeover.
- Maricopa: existing county page already has the proof and county-specific research route. No new duplicate page or unverified title claim is needed.

## Release guardrails

No new outreach, payment, promise of record availability, or claims of local case success. Preserve TDEC title and the original Shelby attribution. Test mobile overflow, proof image enlargement, inquiry links, official return branches, context transfer, and the free search workflow. Log deployment date separately; code changes do not become live merely because GA4 settings did.

## Verification completed

- GA4 administrator UI: inquiry event star confirmed on; all eight event-scoped custom definitions visible in the saved table.
- `gradlew test bootJar`: 894 tests, 876 passed, 18 skipped, zero failures. Three new bridge regression tests added. JavaScript syntax checks passed.
- Local HTTP: finder, OK/ME/AR/MO records, Florida lookup, Texas search, Maricopa county all 200, exactly one proof section each.
- Browser at 1280px and 322px: no horizontal overflow on checked finder; Florida mobile also fits. Actual document dialog opens on both desktop and mobile.
- NC blank-search branch shows assisted research; following it prefills North Carolina and missing-record status in the inquiry form. Found-file branch offers explanation. Submitted-request branch offers submission-evidence controls, not another research request. No test inquiry sent.
- New code is not deployed as part of this implementation turn. GA4 configuration is already active.
