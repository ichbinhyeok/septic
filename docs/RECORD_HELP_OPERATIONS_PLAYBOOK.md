# Record Help Operations Playbook

Current pricing and preview/payment workflow: see `RECORD_HELP_UNLOCK.md`.
Research and agency requests remain free; useful results have an optional
US $29 unlock. Agency fees are additional at cost with prior approval. Existing
free-beta requests keep their original terms. No new customer or research-scope
restrictions are imposed by this offer.

Last updated: 2026-09-12

Operational migration: 2026-09-13. Read `docs/OPERATIONS.md` first for all live
case status and knowledge updates. `storage/operations/ledger.json` is the
canonical private case/branch/event/source/route/lesson database. This document
is a procedural reference, not a current status database. The newer OPERATIONS
workflow supersedes the older multi-file update instructions below.

## Purpose

This is the operator playbook for turning a property address into a defensible
SepticPath Record Brief. It captures reusable research order, routing knowledge,
failure recovery, agency-request practice, and completion truth learned from
real cases.

Do not store customer names, email addresses, phone numbers, full street
addresses, or unredacted agency files in this document. Case-specific details
belong only in restricted runtime storage and the private email thread.

## Knowledge ownership

Use these files as one operating system rather than creating another isolated
note:

- `docs/RECORD_HELP_OPERATIONS_PLAYBOOK.md`: canonical cross-jurisdiction
  research method, truth rules, agency practice, and customer communication.
- `docs/COUNTY_RECORD_REQUEST_ROUTES.md`: canonical route cards learned from
  agency confirmation or live property casework.
- `docs/RECORD_ROUTE_CORRECTIONS.md`: canonical ledger of wrong first moves,
  stale contacts, portal/form misunderstandings, and corrected default order.
- `docs/ONLINE_ROUTE_DISCOVERY_CHECKLIST.md`: canonical proof required before
  claiming that public online routes have been exhausted.
- `storage/operations/ledger.json`: private current state, per-topic branches,
  source-linked history, regional routes, next actions, and review dates.
- `docs/RECORD_HELP_CASE_LOG.csv`: generated anonymized inventory; do not edit it.
- `data/raw/source_registry.csv`: public-source registry used by the product;
  route-card facts should be promoted here only after source review.
- `docs/TDEC_WATER_WELL_ARCGIS_ROUTE.md`: canonical Tennessee private-well
  workflow, kept separate because it is not an SSDS/septic route.

Do not use `ORGANIC_TRACKER.md`, screenshots, conversation history, or Gmail
search results as the only home for a reusable route. When an email changes a
route, update the private route, lesson, case and branch in one operations
transaction. Generate views afterward. Do not duplicate live state in the
historical markdown notes.

## The product promise

SepticPath does not promise an instant record or guarantee that an agency has a
file. The service promise is:

1. identify the correct property and record custodian;
2. search all credible public paths;
3. resolve conflicting identifiers and weak third-party clues;
4. request and track the official file when public access stops;
5. return the original material with a plain-English explanation of what it
   proves, what it does not prove, and what the customer should do next.

An agency dependency is not automatically a failed case. It becomes a product
failure only when SepticPath leaves the customer to identify, contact, chase, or
interpret the agency alone.

## Standard research ladder

Follow this order. Preserve evidence and do not skip directly to a generic
contact address.

### 1. Normalize the intake

- Standardize the street suffix and test common address variants.
- Resolve state and county before choosing a records route.
- Capture the customer's actual question: location, permit existence, approved
  bedrooms/design flow, repair history, well record, sale deadline, or another
  decision.
- Separate septic and private-well requests. They may have different custodians,
  databases, and legal access rules even for the same parcel.

### 2. Establish the property identity

Prefer an official assessor, parcel map, recorder index, or state property
assessment record. Record the minimum useful identity bundle:

- normalized address;
- county and jurisdiction;
- current parcel ID;
- owner name only for agency search use;
- subdivision, lot, legal-description clue, or book/page when available;
- structure type and approximate year;
- public/private water and sewer indicators.

Never treat a listing-site page as the final property identity. Use commercial
property sites only to discover a candidate parcel, former owner, alternate
address, construction year, or legacy identifier, and then reconcile the clue
against an official source.

### 3. Resolve the file owner

Determine whether the practical custodian is:

- a state environmental agency;
- a state regional or field office;
- a contract county or delegated local program;
- a county health or environmental-health department;
- a building, development, planning, or records department;
- a water-well program or public-water utility for an adjacent well question.

The agency that regulates new construction is not always the office that holds
historical property files. Confirm both the program and the historical-record
intake route.

### 4. Search the public surfaces

Use all identifiers the source supports:

- exact and variant address;
- parcel ID with and without punctuation or spaces;
- current and known former owners;
- subdivision and lot;
- permit/application number;
- approximate installation or structure year.

Search in this sequence:

1. official address or permit search;
2. official GIS or assessor record;
3. official document repository or public-record portal;
4. official ArcGIS item, feature layer, or agency data download;
5. recorder/deed index and historical ownership clues;
6. reputable third-party property sources as leads only.

If a viewer fails, test whether its underlying official dataset, ArcGIS Hub
download, alternate official viewer, printable endpoint, or regional-office
route remains available. A broken page is not proof that the record does not
exist.

Do not use `online research exhausted` until the applicable discovery classes
in `docs/ONLINE_ROUTE_DISCOVERY_CHECKLIST.md` have been recorded. When a
deadline justifies contacting the agency earlier, keep public discovery running
and describe the case as `agency request pending`.

### 5. Reconcile conflicts before requesting

Do not forward raw conflicts to the agency or customer without analysis.

- Check whether two identifiers are a parent parcel and a structure/trailer
  assessment rather than two land parcels.
- Distinguish a stale commercial record from the current official assessment.
- Treat bedroom count, acreage, structure size, and utility labels as separate
  facts with separate provenance.
- Use former-owner or legacy-ID clues in the agency request when they may help
  retrieve an older file, but label them as clues rather than verified current
  facts.

### 6. Classify the result truthfully

Use one of these states:

- `public_record_found`: property-matched official document obtained online.
- `metadata_only`: official metadata or parcel evidence found, but not the
  underlying septic/well file.
- `public_route_blocked`: the intended public system failed or denied access.
- `agency_request_pending`: a targeted request was sent to the likely custodian.
- `official_no_match`: the agency searched the supplied identifiers and reported
  no match. This is not proof that no system or well ever existed.
- `official_referral`: the contacted office identified a different custodian or
  mandatory portal.
- `resolved`: the official result was reviewed and a plain-English next step was
  delivered.

Do not use `no record` for an empty web search, a 403, a bounced email, or an
unanswered request.

## Agency-request standard

### Minimum request bundle

- full property address;
- current parcel ID;
- current owner when publicly verified and necessary for the search;
- former owner or legacy identifier only when useful;
- structure type and approximate year;
- exact document list;
- request for written confirmation if no file is found;
- request for referral if another office holds the file.

### Septic document list

Ask for the property file rather than only a generic "permit":

- construction or installation permit;
- Certificate of Completion, final approval, or operation approval;
- approved bedroom count or design flow;
- site plan, as-built, tank location, and absorption-field layout;
- repair, replacement, alteration, abandonment, complaint, and inspection
  history.

### Channel strategy

- Send to the confirmed records intake first.
- Copy the serving field or local program office when the statewide repository
  is inaccessible or jurisdiction could otherwise be lost.
- If email bounces, verify the current official directory and try the published
  portal or phone-assisted route. Do not keep retrying guessed addresses.
- If a portal prevents truthful international registration, do not invent a
  U.S. address, phone number, residency, or owner authorization. Ask the named
  program contact for an alternate intake or have the authorized customer
  complete the legal submission.

### Follow-up cadence

- Send the customer an immediate researched status note after the official
  request is actually submitted.
- Follow up with the agency after five business days unless the agency publishes
  a longer response window.
- Escalate once through a verified regional/program contact after the normal
  window or a bounce.
- Tell the customer when the case is waiting on an agency, but do not ask them
  to repeat work SepticPath can perform.

## Customer communication standard

Every first substantive update should contain four parts:

1. **What we matched:** county, parcel, property type, and other verified public
   facts that materially narrow the search.
2. **What we resolved:** duplicate IDs, wrong parcel risk, jurisdiction, former
   address, file-owner split, or another ambiguity.
3. **What we did next:** exact agency and exact documents requested. State this
   only after the request has been sent.
4. **What happens next:** SepticPath will track the response, review the source
   file, explain its meaning and limitations, and identify the next action.

Use this operator disclosure while the service is founder-led:

> SepticPath is currently founder-operated, so I’m personally handling your
> request and corresponding from this email address.

Avoid generic support language, unexplained document forwarding, and claims
that an individual sewage utility label proves the permit, design, condition,
or current legal status of a system.

## How to turn an agency response into a Record Brief

1. Preserve the original source and email thread privately.
2. Confirm the returned file matches the address, parcel, owner/history, or a
   defensible legacy identifier.
3. Extract document type, dates, permit/application number, approved bedroom
   count or design flow, tank/field layout, final approval, and repair history.
4. Separate explicit facts from operator inference.
5. Call out contradictions and missing closeout documents.
6. Explain the practical consequence for the customer's stated decision.
7. Return the official source plus the interpretation; do not merely attach a
   government PDF.

## Knowledge captured from verified routes

### Tennessee — TDEC-managed septic counties

- Confirm whether the county is TDEC-managed before using a regional office.
- Establish the parcel through the Tennessee Comptroller property assessment
  system when possible.
- Use the public SSDS/TDEC record viewers first, but treat 403 responses as
  access failure rather than a no-record result.
- Current statewide historical-file request route used in casework:
  `septicsystem.files@tn.gov`.
- Copy the field office serving the county when the public viewer is blocked or
  the regional route may help retrieve the file.
- Ask for the complete property file and an explicit no-match/referral response.

### Rhea County, Tennessee

- Practical owner: TDEC Division of Water Resources.
- Serving office: Chattanooga Environmental Field Office,
  `TDEC.Chattanooga.EFO@tn.gov`, 423-634-5745.
- Official property identity can be established through Tennessee TPAD,
  jurisdiction code `072`.
- A TPAD `T` property identifier denotes a trailer record. A legacy trailer ID
  can be associated with the current parent parcel and should not automatically
  be treated as a separate tract.
- In a verified case, TPAD's `PUBLIC / INDIVIDUAL` utility label supported a
  public-water/individual-wastewater search path, but did not prove the septic
  permit, layout, capacity, or condition.

### Tennessee — contract counties

- Do not send a contract-county case into the general TDEC regional lane by
  default.
- Shelby County's existing septic file is handled through the Shelby County
  Health Department Water Quality Branch. The published new-work application
  is not an existing-record lookup.

### South Carolina — SCDES nSITE

- Use the official SCDES nSITE result to establish permit metadata before
  requesting an underlying file.
- A permit number and public status are valuable metadata, but they are not the
  installed layout, final approval, or repair file.
- Include the exact nSITE permit number in the SCDES records request so the
  field-administration team is not forced to repeat the address search.

### Tennessee — private water wells

- Water-well records are a separate adjacent workflow. Use
  `docs/TDEC_WATER_WELL_ARCGIS_ROUTE.md`.
- Search the current TDEC viewers first and retain the ArcGIS Hub CSV as the
  working bulk-data fallback.
- TDEC's well database may not support parcel-ID search and may redact exact
  coordinates. Search address variants, county, owners, date, and well/driller
  attributes.
- An official address-field no-match does not prove a well never existed.
- Public-water service must be verified separately with the relevant utility.

### Wayne County, Indiana

- Practical owner: Wayne County Health Department/on-site sewage program.
- Start with the county's Septic Location Request path.
- A file-location result must still be reconciled with inspection status and any
  bedroom-load change before the existing-system story is treated as complete.

### Other agency-confirmed routes

- Floyd County, Indiana confirmed that the Health Department retains septic
  records and releases them through its public-records request process.
- Howard County, Indiana confirmed that general Environmental Health email is
  the records route. Its existing-system application is for connecting a new
  home to an existing system, not a general record request.
- Johnston County, North Carolina confirmed that users should start with Find a
  Well/Septic Permit. When no image is available, the site prompts an email
  request and county staff research the file.

### Prince George's County, Maryland

- Search DPIE eRecords Explorer first using street and application clues.
- Empty eRecords results are not a no-record determination.
- Historical Health Department well/septic requests route through Momentum.
- The reviewed registration flow did not accept truthful Korean address/phone
  details. Use the published program contact for an international intake path;
  do not fabricate U.S. identity details.
- The detailed verified route is maintained in
  `docs/COUNTY_RECORD_REQUEST_ROUTES.md`.

## Per-case learning entry

After each case closes or materially changes route, append an anonymized entry
to the appropriate route document using this template:

```md
### YYYY-MM-DD — State / County — record type

- Intake intent:
- Property identity source:
- Public routes tried:
- Working route:
- Blocked or misleading route:
- Identifiers that unlocked the search:
- Agency contacted:
- Documents requested:
- Outcome state:
- Response time:
- Interpretation delivered:
- New reusable lesson:
- Route last verified:
```

Do not record the customer's street address, name, email, or phone in the
committed entry.

## Operational metrics worth retaining

Track these without personal/property identifiers in analytics or committed
documents:

- percentage resolved online;
- percentage requiring an agency request;
- correct-custodian rate on the first request;
- agency reply rate and median business days to reply;
- bounced or redirected request rate;
- official record obtained rate;
- official no-match rate;
- percentage of obtained files that require material interpretation;
- operator minutes per case;
- customer reply, repeat-request, and eventual paid-conversion rate.

These metrics distinguish a valuable concierge records product from a free
search service and reveal which counties are worth automating or publishing
next.
