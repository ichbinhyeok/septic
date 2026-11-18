# Source-backed search expansion, November 18, 2026

## What changed

The South Carolina and Oregon perc-cost pages now answer the cost question
before offering existing-record help. They separate verified public fees from
excavation, private professional services and construction. Neither published
fee is represented as an all-in contractor quote. Vacant land without an old
permit follows the new-site process rather than an artificial records prerequisite.

Henderson NC now explains the modern and legacy search fields and the OP
attachment. Lake OH now has a sourced search guide, including house-number-first
search and street-name variants. Contextual links from the corresponding state
landing pages lead directly to the instructions. Existing canonical paths remain.

OnlineRME, Carmody SepticSearch and NHDES OneStop guidance distinguishes a missing
property result, missing plan and missing approval. Loudoun-specific PIN and
collection rules are explicitly local. No property search or successful record
retrieval is implied by reviewing public instructions.

The as-built hub and reading guide include a new fictional diagram explaining
distance endpoints, line length and tank capacity. It is clearly labeled as an
educational illustration, with crawlable HTML images, explanatory alt text,
full-size PNG links and image sitemap entries on the two actual landing pages.
The original public case examples remain distinct from this invented exercise.

## Sources and limitations

- [SCDES residential septic application](https://www.des.sc.gov/permits-regulations/septic-tanks/permits-licenses/septic-tanks-residential-single-home-builder): $150 application and preparation requirements. Official indexed text was available; direct local fetch returned 403.
- [Oregon DEQ Table 9A](https://www.oregon.gov/deq/wq/regulation/Pages/table9fees.aspx): browser-rendered current 2026 first-lot single-family fee $1,121, effective July 1; previous fee $843. County schedules may differ.
- [Oregon process](https://www.oregon.gov/deq/Residential/Pages/Onsite-about.aspx): applicant-provided pits and separate evaluation/construction permit steps.
- [Henderson instructions](https://www.hendersoncountync.gov/sites/default/files/fileattachments/environmental_health/page/137660/permit_search_tips.pdf): official extracted PDF text. The local download and screenshot retrieval failed; no downloaded PDF or screenshot is presented as reviewed evidence.
- [Lake digital portal](https://www.lcghd.org/environmental-health/environmental-health-digital-record-portal/): public instructions reviewed; downstream access agreement was not accepted and no property searched.
- [Loudoun instructions](https://www.loudoun.gov/5771/Well-Septic-Records), [Carmody search](https://septicsearch.com/property-records), and [NHDES SSB-14](https://www.des.nh.gov/sites/g/files/ehbemt341/files/documents/ssb-14.pdf): public search and document-scope guidance. NHDES official indexed PDF text was available; direct fetch was blocked.
- [Google image guidance](https://developers.google.com/search/docs/appearance/google-images): HTML image discovery, descriptive context and sitemap association.

## Validation and measurement

`SeoDepthExpansionRegressionTest` covers fee scope, indexability, attachments,
county-specific recovery, source links, image responses and parsed image sitemap
associations. Existing coverage-count, content and material-lastmod assertions
are updated only where the release intentionally changes their contract.

Keep web and image search measurements separate. Compare page clicks,
impressions, CTR and position over complete 14- and 28-day windows; inspect
country/device mix before interpreting a change. The archived pre-release
analytics remain private. No ranking, traffic or lead uplift is claimed at launch.

Track `nav-click` source contexts `sc_perc_existing_file`,
`or_perc_existing_file`, `nc_henderson_search_steps`, `oh_lake_search_steps`,
`reading_dimension_image`, `as_built_dimension_example`,
`nh_missing_plan_archive`, `onlinerme_loudoun_collection` and
`septicsearch_missing_scan`. Image views and official-source clicks are not
inquiries. Join actual intake source paths to operational outcomes; keep paid
or accepted field leads separate from record-help requests.

Check crawling and indexing after release, then assess the first full 14 days
(November 19–December 2) after reporting lag. The full 28-day follow-up is
November 19–December 16. The county pages' prior crawled-not-indexed status is
not resolved merely by deploying the revision or submitting a sitemap.
