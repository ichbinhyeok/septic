# Content consolidation map

## Current footprint

- The public sitemap currently contains 259 URLs.
- The larger studio crawl includes public routes, design previews, county pages, and their linked internal surfaces; it is not a count of 722 indexable production pages.
- Large URL families are intentional programmatic-search collections, but some national guides and tools compete for the same next action.

## Decision rule

Keep two pages separate only when a visitor arrives with a materially different question and receives a materially different next action. Shared vocabulary alone is not duplication.

Do not delete, redirect, or change a canonical from this design audit alone. Confirm impressions, clicks, backlinks, and conversion contribution before consolidating a live URL.

## Soil and perc cluster

| Route | User question | Role | Decision |
|---|---|---|---|
| `/perc-test-cost/` | What will testing cost before I schedule it? | Pre-test cost and scope guide | Keep |
| `/failed-perc-test-septic/` | The result failed; what can happen next? | Post-result redesign and approval guide | Keep |
| `/drain-field-estimator/` | What might the field path cost or require? | Legacy field-risk entry URL | 301 to the main calculator's field mode |

The first two pages should cross-link but should not be merged. Their search intent and emotional moment are different.

## Field and replacement cluster

| Route | User question | Role | Decision |
|---|---|---|---|
| `/wet-yard-over-septic-drain-field/` | Is this visible symptom serious? | Symptom triage | Keep |
| `/septic-replacement-area/` | Does the parcel preserve a usable future field area? | Parcel constraint and reserve-area guide | Keep |
| `/drain-field-replacement-cost/` | What does field-only replacement cost? | Field-only cost guide | Keep |
| `/septic-replacement-cost/` | What does whole-system replacement cost? | Whole-system cost guide | Keep, with a clear field-only boundary |
| `/drain-field-estimator/` | What might my field-specific scope look like? | Legacy field-risk entry URL | Consolidated 2026-09-21 |
| `/septic-tank-size-estimator/` | What capacity band fits the household? | Legacy capacity entry URL | Consolidated into `?mode=tank_size` 2026-09-21 |
| `/septic-pump-schedule-estimator/` | When should this household inspect and pump? | Legacy maintenance entry URL | Consolidated into `?mode=pump_schedule` 2026-09-21 |
| `/septic-system-cost-calculator/` | What might my broader septic project cost? | Primary multi-project calculator | Keep as the canonical calculator experience |

## Strongest consolidation candidate

`/drain-field-estimator/` overlaps with the drain-field mode already supported by `/septic-system-cost-calculator/`.

Implemented product direction — 2026-09-21:

1. Preserve the field-specific entry experience and copy.
2. Run it as a focused mode of the primary calculator rather than a separate calculation system.
3. Redirect the old estimator URL permanently after Search Console confirmed negligible independent demand.
4. Preserve the old form's replacement-area input and specialized field-risk interpretation inside the main estimator.

## Information architecture

The national pages should form one decision funnel instead of a flat collection:

1. Symptom or event: wet yard, failed perc result.
2. Constraint: replacement area and site viability.
3. Scope: field-only replacement versus whole-system replacement.
4. Action: one calculator with a preselected mode.
5. Proof: investigation cases and official-source methodology.
6. Conversion: property-specific research intake.

## Next implementation order

1. Stop adding new parallel URL families until index coverage improves.
2. Modernize the national editorial entry pages while preserving the distinct jobs that already have search evidence.
3. Make their headings and cross-links explicitly explain the boundaries above.
4. ~~Unify the specialized calculator experiences at the product level.~~ Completed 2026-09-21.
5. Review live search and conversion evidence before any redirect or canonical change.

## Search Console evidence — 2026-09-20

Observed window: 2026-06-18 through 2026-09-17, Web search, septicpath.com domain property.

- Sitewide: 1,595 clicks, 63,663 impressions, 2.5% CTR, average position 8.8.
- `/perc-test-cost/` family: 27 reported pages, 53 clicks, 3,779 impressions. Demand is on state pages; the national root had no reported row.
- `/failed-perc-test-septic/` family: 6 reported pages, 1 click, 238 impressions. The national root had 1 click and 81 impressions at average position 16.7.
- `/septic-replacement-area/` family: 6 reported pages, 0 clicks, 69 impressions.
- `/wet-yard-over-septic-drain-field/` family: no page appeared in the complete 498-row performance table.
- `/drain-field-replacement-cost/` family: 8 reported pages, 4 clicks, 95 impressions.
- `/septic-replacement-cost/` family: 9 reported pages, 0 clicks, 48 impressions. The national root had 22 impressions at average position 7.5.
- `/septic-system-cost-calculator/` family: 12 reported pages, 116 clicks, 8,922 impressions. Alabama alone contributed 109 clicks and 7,286 impressions.
- `/drain-field-estimator/`: 0 clicks, 25 impressions, average position 45.2.

Index report last updated 2026-09-14:

- 324 indexed pages and 573 not indexed.
- 291 crawled but not indexed.
- 151 alternate pages with a proper canonical.
- 66 excluded by `noindex`.
- 62 discovered but not indexed.
- 3 redirecting pages.
- 18 of the 291 crawled-but-not-indexed examples were calculator query-string variants.

## Evidence-led conclusion

- Do not merge `perc-test-cost` into `failed-perc-test-septic`: the former already has materially stronger state-level demand and serves a different moment.
- Do not expand the wet-yard or replacement-area families further until their existing pages earn impressions or demonstrate conversion value.
- Preserve the calculator family and especially the Alabama route; it is a major search asset.
- Consolidate the drain-field estimator into a focused mode of the main calculator at the product level. Its separate URL currently has negligible search value.
- Remove crawl incentives for calculator query-string variants while preserving usable form state for visitors.
- Treat the near-zero national roots as navigational hubs, not as justification for more parallel pSEO pages.
