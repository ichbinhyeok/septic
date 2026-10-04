# Existing-guide growth improvements

Status: implemented and locally verified; included in release candidate 0.0.23.0. Production deployment requires a successful release workflow and live checks.

## Scope

- Guilford County, NC: promote the official GIS address/REID route, linked well/septic documents, Water Quality layers, and focused NextRequest fallback. Retain the published phone option. Align the primary action, acquisition method, metadata, FAQs, and source registry.
- Union County, NC: distinguish no-login Evolve building-permit clues from the Environmental Health historical-file request. Explain the dated $10 staff-search charge, payment sequence, and current-intake confirmation when access is blocked. Keep new permits and inspections separate from copies of existing records.
- Indiana records hub: add prominent free-help access and open, readable guidance for location drawings, older-address searches, and county-specific file routes.
- Connect existing public case sections at the relevant point in the guides. Name their actual Tennessee jurisdictions; do not imply they are NC/Indiana cases. No new case story or source file was published.
- Preserve source page, state and county when handing off to the existing research form. No form fields or requirements changed.
- Keep existing masked drawings and evidence images unchanged. Add an opaque reading surface only to the two NC practical guides.
- Advance page-specific material sitemap dates for the three changed routes. New official-source review dates also update the derived access-index dataset date.

## Source and publication review

- Guilford official [GIS directory](https://www.guilfordcountync.gov/government/departments-and-agencies/information-technology/geographic-information-systems/gis-online-services), [viewer](https://gisdv.guilfordcountync.gov/guilford/), and [Water Quality layer inventory](https://gcgis.guilfordcountync.gov/arcgis/rest/services/GISDV/WaterQuality_WellSeptic/MapServer) reviewed. Viewer disclaimer and search controls opened without a customer search. Its unrelated voting/council-layer console errors do not prove all GIS layers are healthy.
- Union [Evolve Public](https://ucinspect.unioncountync.gov/evolvepublic/) explicitly describes no-account permit searches and building/development scope.
- Union's linked historical-record form was access-blocked in both HTTP and interactive review. This is not a successful live-form inspection, a retirement finding, or a no-record finding. The public page gives the published EH phone fallback and follows current county instructions if redirected.
- The $10 charge and billing sequence are paraphrased from a retained original agency response dated September 30, 2026. Public copy states that date and asks users to confirm the current amount. The response itself, source ID, correspondence and all property identifiers remain private.
- Indiana's official onsite-sewage program and local-health-department responsibilities support the county-first guidance; no uniform statewide records availability or timing is claimed.
- Private route review was captured transactionally; no emails, agency requests, payments or customer-case status changes occurred.

## Verification

- Added seven regression tests covering county workflow differences, dated fee scope, contextual form handoff, Indiana-only content, existing public case anchors, sitemap lastmod and the state hero's record-help tracking marker.
- Final release-candidate Java suite after the tracking correction: 1,057 tests, zero failures/errors, 18 existing skips (1,039 passed); `bootJar` succeeded.
- `bootJar` built successfully; after the final CSS-only readability change, packaging was rebuilt successfully.
- Actual browser checks used the production templates with studio preview disabled and separate local storage. Mail credentials were blank; no intake was submitted.
- Verified NC/Indiana guide rendering, direct request handoffs, selected state, public case destinations, no horizontal overflow at 390px, and no application console warnings/errors in inspected flows. Existing field requirements were unchanged.
- Private ledger validated at revision 619. Existing unrelated working-tree changes were preserved.

## Measurement after deployment

Use entry-page attributed submitted inquiries alongside search clicks; track documented unresolved field-work needs separately. Contractor acceptance and actual payment remain separate outcomes. These changes are not evidence of conversion lift or buyer demand. No new analytics export was analyzed in this implementation turn.
