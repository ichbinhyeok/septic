# Bing search and AI growth plan — 2026-09-13

## Baseline

- Bing Webmaster Tools, 2026-06-13 through 2026-09-12: 1.4K clicks, 36.5K impressions, 3.91% CTR.
- Bing organic in GA4, 2026-08-15 through 2026-09-12: 553 sessions, 491 active users, 436 engaged sessions.
- Bing AI Performance for the same three-month window: 9K citations and 20 average cited pages.
- Both production sitemaps are healthy: 258 main URLs plus 325 county URLs, last crawled 2026-09-12 with zero reported errors or warnings.

## Changes shipped for Bing's recommendations

- IndexNow key is hosted at `/66e41240613868dc4e9a8fffdcad9b77.txt`.
- Production deploys snapshot the old sitemaps, compare them with the post-deploy sitemaps, and submit only added, materially changed, or deleted URLs to IndexNow.
- Material page revisions are maintained independently from broad template dates so narrow releases do not falsely refresh the whole site.
- The low-CTR TDEC title, description, H1, and hero remain frozen during the existing 14-complete-day test. Its sitemap date now reflects the 2026-09-12 material page update.
- Bing's inbound-link recommendation is an external distribution issue. Do not buy or manufacture links; use the existing public record evidence and research workflow as the linkable asset when outreach begins.

## Priority cohorts

1. Tennessee demand: `/tdec-septic-records/`, `/septic-records-checklist/tennessee/`, and `/septic-permit-process/tennessee/`. These already rank near page one but have 1.11%–2.47% CTR.
2. Product entry: `/septic-record-finder/`. It already has 7.27% Bing CTR and is the clearest path from search intent to a record-help request.
3. AI citation expansion: Maine, Oregon, Rhode Island, Wisconsin, North Carolina, and South Carolina pages already cited by Bing. Improve only where citations produce qualified landings or reveal a missing answer; citation count alone is not the product metric.

## Weekly scorecard

Record these by landing page and query cohort, comparing complete same-weekday periods:

| Metric | Source | Decision use |
| --- | --- | --- |
| Clicks, impressions, CTR, average position | Bing Search Performance | Separate snippet opportunity from ranking opportunity |
| Citations, cited pages, grounding-query share | Bing AI Performance | Find answers Bing already trusts and gaps worth expanding |
| Organic sessions and engaged sessions | GA4, Bing source/medium | Confirm search visibility becomes real usage |
| Record-help starts and submissions | GA4 events, landing-page attribution | Decide whether a traffic cohort has business value |
| IndexNow changed URL count and response | Deployment log | Verify discovery automation rather than infer it |

## Decision dates

- On or after 2026-09-25: review the TDEC snippet cohort after 14 complete days. Keep the current snippet if CTR is at least 2.0%; target 2.3%–2.8%. Change one snippet variable only if it remains below the floor at comparable position.
- At 28 complete days: decide whether Tennessee needs another CTR test, stronger internal handoff, or no change.
- Review AI citations weekly, but promote a cited state only when GA4 engagement or record-help intent supports it.
