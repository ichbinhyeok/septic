# County Record Request Routes

> Historical route reference. Since 2026-09-13, live verification status,
> corrected routes and case evidence are maintained in the private operations
> ledger; read `docs/OPERATIONS.md`. The Anderson Field-Admin email route below
> later produced a restricted-group delivery failure and is not a confirmed
> accepted intake. Blount GovQA and Williamson form/phone-validation findings
> are in the live route table. Do not use this document alone for live status.

Living operating notes for record-access paths confirmed through actual casework. These notes supplement the public county profiles; they do not prove that a record exists for any particular property.

The cross-county research order, agency-request standard, outcome taxonomy, and
customer communication rules live in
`docs/RECORD_HELP_OPERATIONS_PLAYBOOK.md`.

## Rhea County, Tennessee

- **Record type:** Existing subsurface sewage disposal system construction,
  completion/final approval, design-flow or bedroom approval, layout, and repair
  records.
- **Property identity:** Use Tennessee Property Assessment Data (TPAD),
  jurisdiction code `072`, to establish the current parcel and structure record.
- **Identifier trap:** TPAD's glossary defines a `T` property identifier as a
  trailer record. Reconcile a legacy trailer ID with the current parent parcel
  before treating commercial-site acreage or duplicate-address entries as a
  second tract.
- **First lookup:** TDEC's SSDS permit/document search and Groundwater Protection
  data viewer.
- **Known blocker:** Both public TDEC viewer paths returned HTTP 403 during live
  casework on 2026-09-12. This is an access failure, not a no-record result.
- **Official request channel:** `septicsystem.files@tn.gov`.
- **Serving office:** TDEC Chattanooga Environmental Field Office,
  `TDEC.Chattanooga.EFO@tn.gov`, 423-634-5745. Copy this office when the statewide
  viewer is inaccessible or field-office routing may help.
- **Request bundle:** Address, current parcel ID, current owner when necessary,
  structure type/year, useful legacy trailer ID, and the complete document list.
- **Confirmed:** A targeted request using this route was accepted into Gmail
  Sent on 2026-09-12. Receipt or substantive agency response remains pending;
  do not mark the route `obtained` until one arrives.

## Wayne County, Indiana

- **Record type:** Septic location, existing on-site sewage file, inspection
  status, approved use/bedroom load, and related permit history.
- **First lookup:** Wayne County Health Department on-site sewage guidance.
- **Official request channel:** The county's Septic Location Request form/path.
- **Completion caution:** A location response alone does not prove that the
  system passed inspection or supports a changed bedroom/use load.
- **Operator status language:** `County location request submitted; official
  property file pending.` Do not mark the case resolved until the returned file
  is reviewed against the customer's question.

## Shelby County, Tennessee

- **Record type:** Existing septic permit, site plan/layout, inspection/final
  approval, and repair history.
- **File owner:** Shelby County Health Department Water Quality Branch because
  Shelby is a Tennessee contract county.
- **First action:** Contact Water Quality at 901-222-9599 with address, parcel,
  current or prior owner, and approximate installation year.
- **Known trap:** The published septic application is for new installation,
  modification, repair, or abandonment work; it is not the existing-record
  request route.
- **Routing rule:** Do not default this case to the statewide TDEC field-office
  lane.

## Roane County, Tennessee

- **Record type:** Historical septic construction permit, layout/system sketch,
  final approval, and later repair history.
- **File owner:** TDEC Division of Water Resources; Roane County is served by
  the Knoxville Environmental Field Office.
- **Working route:** Send a targeted request to `septicsystem.files@tn.gov` and
  copy `TDEC.KEFO.DWRPermits@tn.gov` when the public viewer is unavailable.
- **Verified outcome:** In September 2026, TDEC returned a historical septic file
  after the public viewer returned 403. SepticPath delivered both the original
  agency file and an interpreted Record Summary.
- **Reusable lesson:** Add parcel, owner/history, road-name variant, and building
  year clues. The underlying file may use an older owner or incomplete address.

## Overton County, Tennessee

- **Record type:** Historical construction, repair, and system-layout records.
- **File owner:** TDEC Division of Water Resources; Overton County is served by
  the Cookeville Environmental Field Office.
- **Working route:** Send the request to `septicsystem.files@tn.gov` and copy
  `TDEC.Cookeville.EFO@tn.gov`.
- **Verified outcome:** In September 2026, the Cookeville office returned a
  repair file for the road and historical owner supplied in the request.
- **Match caution:** The returned file did not state the modern street number.
  Treat road/former-owner/parcel reconciliation as part of the Record Brief;
  never imply that a weak address line is an exact match without explaining the
  evidence.

## Union County, Tennessee

- **Record type:** Existing septic file plus adjacent private-well location and
  driller-report research.
- **Septic route:** `septicsystem.files@tn.gov`, with Knoxville Environmental
  Field Office support through `TDEC.KEFO.DWRPermits@tn.gov`.
- **Well route:** TDEC Water Well Program. Keep this as a separate search from
  the septic request.
- **Verified behavior:** The well database could return road-name candidates
  without an exact address match. TDEC supplied individual Driller Report links,
  but the public data did not establish which candidate, if any, belonged to the
  subject parcel.
- **Reusable lesson:** Exclude explicit wrong street numbers/counties, compare
  any usable coordinates with parcel geometry, and ask TDEC to inspect the
  underlying report when coordinates are redacted. A seller or listing agent
  reporting no records is another clue, not an official no-record result.
- **Current limitation:** The verified case remained open on the septic side and
  inconclusive on the exact well location as of 2026-09-12.

## Fayette County, Tennessee — water-source branch

- **Record type:** Private-well/Notice of Intent history and separate public-water
  service availability. This is an adjacent due-diligence branch, not a septic
  permit lookup.
- **Well route:** Use the TDEC Water Well Program workflow documented in
  `docs/TDEC_WATER_WELL_ARCGIS_ROUTE.md`.
- **Public-water route:** Verify service availability with the utility that may
  serve the address. For the Oakland system, the current utilities page directs
  service questions and forms to `sosborne@oaklandtn.gov`; the Fresh Water Plant
  publishes 901-465-8830 and Town Hall publishes 901-465-8523.
- **Partial-delivery lesson:** In a verified case, the primary utility message
  was accepted while one copied named staff address returned `550 User unknown`.
  Do not mark the whole request bounced when Gmail reports failure for only one
  recipient.
- **Completion rule:** An official TDEC address-field no-match does not answer
  whether public water is available. Keep the utility branch pending until the
  serving utility confirms availability for the property.

## Anderson County, South Carolina

- **Record type:** SCDES onsite wastewater permit, approval/final inspection,
  site plan, system specifications, and repair history.
- **First lookup:** Search the official SCDES nSITE system by address and confirm
  the property-matched permit number.
- **Metadata boundary:** A visible nSITE permit record does not itself provide
  the full official file or prove final installation details.
- **Official request channel used:** `BRLS-OSWW-Field-Admin@des.sc.gov` with the
  address, county, and exact nSITE permit number.
- **Operator status language:** `Official permit metadata located; underlying
  SCDES file requested.`
- **Confirmed:** A property-matched nSITE permit was located and the request was
  sent on 2026-09-12; the substantive agency response remains pending.

## Floyd County, Indiana

- **Record type:** Septic permit, system location, inspection history, repair
  file, and written no-record response.
- **File owner:** Floyd County Health Department.
- **Official route:** Use the Health Department public-records request process.
- **Agency confirmation:** The county Environmental Supervisor confirmed this
  route by email on 2026-07-22.

## Howard County, Indiana

- **Record type:** Existing septic record, permit, inspection, and system
  location.
- **Official route:** Email Howard County Environmental Health at
  `environmental@howardcountyin.gov`; a named Environmental Health specialist
  may also accept the request directly.
- **Known trap:** The county's existing-system request/application is for
  connecting an existing septic system to a new home. It is not the general
  records-request form.
- **Agency confirmation:** Howard County Environmental Health corrected this
  distinction by email on 2026-07-22.

## Johnston County, North Carolina

- **Record type:** Septic and well permit copies and available permit images.
- **First lookup:** Use Johnston County Environmental Health's `Find a
  Well/Septic Permit` search.
- **Fallback:** If the search result has no image, follow the site's email prompt
  to Environmental Health for staff research and a copy if one exists.
- **Agency confirmation:** County staff confirmed this sequence by email on
  2026-07-21 and stated that their internal research target was 24 hours. Treat
  that as the agency's stated target, not a SepticPath guarantee.

## Prince George's County, Maryland

- **Record type:** Health Department well/septic information, including historical septic applications, perc or soil evaluations, permits, denials, withdrawals, expirations, renewals, and review history.
- **First lookup:** Search the DPIE eRecords Explorer as a guest using the street number and street name, or a known application sequence/year/revision.
- **When the online lookup is empty:** Do not treat an empty result as a no-record determination.
- **Official request channel:** Submit a Health Department information request through the [Momentum portal](https://momentumhome.princegeorgescountymd.gov/). A direct email to Environmental Engineering may identify the right program, but it is not the intake channel for the records request.
- **Portal checkpoint:** A Momentum profile/login may be required. Stop before accepting a fee, making a legal certification, or using an owner's identity or authorization without that person's approval.
- **International-requester limitation:** The live profile form reviewed on 2026-09-12 required a U.S. state/territory from a fixed list and applied a U.S.-style phone field. A Seoul address and Korean mobile number could not be entered truthfully. Do not invent a Maryland address or U.S. phone number; ask the program contact for an international-requester intake route or an assisted submission.
- **Escalation contact:** Tiffany Judd, `TAJudd@co.pg.md.us`, 301-883-3406, for questions or portal problems.
- **Confirmed:** 2026-09-11, by a reply from the Prince George's County Health Department Environmental Engineering/Policy Program directing a historical septic-information request to Momentum.
- **Operator status language:** `Online search exhausted; official office confirmed; formal Momentum request pending/submitted.` Do not mark the case `record found` or `no record` until the county provides a substantive response.
