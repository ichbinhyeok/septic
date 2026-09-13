# Record Route Corrections

> Archived baseline, imported on 2026-09-13. New corrections belong in the
> `lessons` and `routes` collections of `storage/operations/ledger.json` through
> `tools/operations.mjs apply`. See `docs/OPERATIONS.md`. The private ledger also
> records the Anderson restricted-group bounce, Blount duplicate/complete
> submission distinction, Williamson listing PDFs and form failure, and mixed
> parcel reports discovered after this baseline.

Last updated: 2026-09-12

## Purpose

This ledger records wrong first moves, incomplete routes, stale contacts, and
agency corrections discovered through live work. It exists so a successful
fallback does not erase the mistake that made the case slower.

Use it together with `RECORD_HELP_OPERATIONS_PLAYBOOK.md` and
`COUNTY_RECORD_REQUEST_ROUTES.md`. The required search-depth gate lives in
`ONLINE_ROUTE_DISCOVERY_CHECKLIST.md`. Do not store customer identity or
property addresses here.

## Correction ledger

| Jurisdiction / workflow | What happened | Why the first route was incomplete | Correct default next time | Status |
| --- | --- | --- | --- | --- |
| Prince George's County, MD historical septic/perc file | SepticPath emailed the Environmental Engineering program first. County staff replied that Health Department information requests are processed through Momentum. | The program email could explain the route but was not the formal records intake. | Search eRecords first, then use Momentum for the historical Health file. Email the named program contact only for portal failure, international-access help, or routing ambiguity. | Corrected; international intake still unresolved. |
| Howard County, IN existing septic record | The public index initially treated the existing-system request form as a likely record route. County staff clarified that it is for connecting a new home to an existing system. | The form title sounded like an existing-record request but its transaction purpose was different. | Request the historical septic record through Howard County Environmental Health email. Use the existing-system form only for the connection/approval workflow it describes. | Corrected and agency-confirmed. |
| Shelby County, TN existing septic file | A staff address copied from an older county source bounced. The request was resent to the current program contact and supported by the general Water Quality/public-records routes. | A real agency domain does not prove a named mailbox is current. | Verify the live department directory first. Use the current program mailbox/contact, and retain the county public-records route as a routing fallback. Check partial delivery when only one recipient in a multi-recipient message bounces. | Corrected; official file obtained. |
| Fayette County, TN public-water verification | A request went to a valid Oakland utility contact with two copied staff addresses; one copied address returned `550 User unknown`. | The town's published staff pages were not internally consistent/current for every named mailbox. | Use the role/service contact published on the live utility page (`sosborne@oaklandtn.gov`) and phone 901-465-8830 or Town Hall 901-465-8523 as fallback. Treat a single copied-recipient bounce as partial delivery, not failure of the entire request. | Primary delivered; response pending. |
| TDEC private-well formal request | The Water Well Program directed SepticPath to the state public-records form. The form accepted non-citizen selection but required a U.S.-format phone; TDEC then explained the non-resident limitation. | The official portal was the intended tracking route but could not accept the operator's truthful international contact data. | Search the current viewers and bulk dataset first. For a formal request, ask TDEC for assisted international intake or have the Tennessee requester submit it. Never invent a U.S. phone, address, residency, or authorization. | Constraint documented. |
| TDEC SSDS viewer | The official septic document/viewer routes repeatedly returned 403. | The existence of a public viewer did not mean it was usable from the operator's network/region. | Try the official viewer once, check the official alternative/data surface, then move to the statewide records inbox plus serving field office. Record `public_route_blocked`, not `no record`. | Working fallback proven in Roane and Overton. |
| TDEC private-well online discovery | SepticPath initially inferred that useful online well records were unavailable after older/direct TDEC viewer paths returned 403. Later research and an agency reply exposed current map/tabular viewers, ArcGIS items, a working Hub CSV download, and individual Driller Report links. | The first audit focused too heavily on the visible official viewer and did not fully separate the application UI from ArcGIS/Hub datasets and underlying services. | Complete `ONLINE_ROUTE_DISCOVERY_CHECKLIST.md` before saying the online route is exhausted. Distinguish water-well datasets from SSDS/septic datasets and verify coverage before using a candidate. | Corrected; working well-data fallback documented. |
| Union County, TN combined septic and well request | Septic and private-well questions were initially carried together in parts of the outreach. The Water Well Program could search road-name candidates but did not own the septic file. | One property can have separate custodians, identifiers, databases, and evidentiary limits. | Open separate septic and well branches from the start, with independent completion states. Recombine them only in the customer-facing brief. | Corrected; septic branch pending, well branch inconclusive. |
| Anderson County, SC permit result | nSITE exposed a property-matched permit number, but not every underlying approval, layout, or repair artifact. | Permit metadata is not the complete official file. | Search nSITE first and include the exact permit number in the later SCDES file request. Contact the agency only for the documents the portal does not expose. | Correct sequence used; file pending. |
| Overton County, TN returned file | TDEC returned a repair file tied to the road and historical owner, but the document did not state the modern street number. | An agency attachment can still have a property-match gap. | Reconcile road, former owner, parcel history, dates, and system context; disclose the weak address line in the Record Brief. Do not equate `agency sent a file` with `exact match proven`. | File delivered with limitation. |
| Wayne County, IN intake | The same customer submitted the same property request twice. | Raw form submissions overcounted demand and could trigger duplicate agency work. | Deduplicate on normalized email + normalized property + short time window before creating another case or sending another request. | Counted as one case. |

## Route-choice rule

For each new property, classify the first official surface before taking action:

1. **Search/download portal:** use it first; contact the agency only for missing
   artifacts, an access error, or a result that cannot be matched.
2. **Mandatory request portal:** prepare identifiers, then use the portal; do not
   substitute a general office email unless the agency permits it or the portal
   is unusable.
3. **Published email/form:** send the exact artifact-led request through that
   channel.
4. **Phone-assisted route:** prepare the full identity bundle and record who
   owns the next action.
5. **Unconfirmed route:** ask the program which channel owns historical files
   before transmitting a full customer case.

The presence of a portal does not always eliminate agency contact. The key
question is what the portal actually provides: full documents, metadata only,
formal intake, or a new-work application unrelated to historical records.

## Required closeout

Whenever a route is corrected:

- update the relevant county route card;
- update the anonymized case state and next action;
- update `data/raw/source_registry.csv` if a publishable official source changed;
- preserve the failure category (`wrong_channel`, `form_purpose_mismatch`,
  `stale_contact`, `portal_access_blocked`, `partial_delivery`, or
  `metadata_only`);
- do not quietly replace the old route without retaining the lesson.
