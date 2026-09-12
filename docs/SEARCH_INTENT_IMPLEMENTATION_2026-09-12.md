# Search acquisition changes — 2026-09-12

## Selection

Same final-web Search Console export as the prior audit: 2026-08-13 through 2026-09-09, sc-domain:septicpath.com. Source snapshot: `output/opportunity-audit-2026-09-12/source-data.json`; executed selection companion: `search-intent-selection.ipynb` in that folder.

Screening rule: at least 50 impressions, average position 4–15, CTR below 2.5%. This is a prioritization heuristic, not an industry CTR benchmark or forecast.

| Existing page | Clicks | Impressions | Mean position | Decision |
|---|---:|---:|---:|---|
| AZ / Maricopa | 0 | 261 | 8.45 | Explicit search queries; put the official free-search answer before proof |
| NC / Union | 1 | 213 | 11.82 | Permit lookup queries; distinguish existing records from new applications |
| NH records | 2 | 162 | 9.99 | Replace generic, awkward copy with OneStop and archive steps |

Query-page data is incomplete. NH query intent is not established by this extract; its content gap was confirmed directly in the page source. A low CTR is not proof that the title caused it. Title, answer placement, copy and links change together, so evaluate this as one intervention.

## Changes

- Maricopa title/description aligned with free septic search; visible first-screen disclaimer explanation, county fallback, and layout-document internal link. Proof is retained below the immediate answer.
- Union title/heading aligned with permit lookup and existing records request; visible distinction from new applications. The old form announces MyHD migration, so the quick-start points to current county guidance rather than promising the old form remains the final submission route.
- NH title/description and explanatory copy rewritten. Added OneStop → archive → town sequence, with qualified date coverage and original NHDES source link. Removed repetitive strategy-oriented language and conflation of general record retrieval with failure-verification paperwork.
- Arizona records hub links prominently to Maricopa's specific search guide. County answers and NH link contextually to the existing as-built guide; no thin duplicate pages created.
- TDEC and recently revised NC/AL primary titles left unchanged. Other low-CTR pages were not rewritten indiscriminately when visible query evidence was too thin or a recent change needs time.

## Official evidence checked

- https://www.maricopa.gov/2581/Online-Septic-Research — free search versus paid county research options.
- https://www.maricopa.gov/FormCenter/Environmental-Services-16/Online-Septic-Search-92 — first-screen agreement and reCAPTCHA; no property search submitted in verification.
- https://www.unioncountync.gov/government/departments-a-e/environmental-health/septic-systems — existing records resource and separate existing-system inspections. Direct reader returned 403; current indexed official content was readable. Not an end-to-end portal verification.
- https://lfportal.unioncountync.gov/Forms/WellSepticPermitRequest — official form's MyHD transition announcement. No fresh promise of the legacy fee or turnaround added.
- https://www.des.nh.gov/sites/g/files/ehbemt341/files/documents/ssb-14.pdf — official indexed guidance specifies OneStop, post-1986 approvals, most post-2015 electronic plans, older archive request, town copies. Direct PDF reader returned 403; indexed source text supplied these facts. Portal completion not verified.

## Evaluation

Record production deployment and first recrawl dates separately. Compare complete 14/28-day windows against the baseline by page; inspect available search queries and ranking changes as well as CTR. Preserve Bing/Yahoo contribution in GA4. Do not claim incremental traffic or inquiry lift until observed. Existing GA4 inquiry tracking is not paid conversion.

## Verification

- Full Gradle suite: 898 tests, 880 passed, 18 skipped, zero failures. Four integration tests cover new titles, official route links, answer placement, unchanged unrelated counties, and the Arizona hub link.
- Desktop 1280px and mobile 322px inspected. Correct titles/answers visible; no horizontal overflow on checked pages. Existing county CSS ordering was corrected for the new quick answers.
- Selection notebook executed successfully; three baseline ratios independently checked, saved HTML inspected. No source-query anonymity gaps were treated as zero demand.
- Final CSS change followed by successful bootJar build. Not deployed yet.
