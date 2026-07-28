# County record completion validation

This protocol prevents a working link or a submitted request from being counted
as a record-retrieval success.

## Cohort

Run 20 property cases, stratified before testing:

- 4 direct official-search cases
- 4 search-with-fallback or metadata cases
- 4 official PDF, form, or portal request cases
- 4 phone, jurisdiction, or temporarily unavailable cases
- 4 “official starting point only” baseline cases

Use public test properties or properties the reviewer is authorized to research.
Do not place property addresses, parcel IDs, owner names, request numbers, email,
or phone numbers in GA4.

## Case record

Assign an anonymous `validation_id` and record these fields in the restricted
review tracker:

- county, profile scope, capability tier, and route review date
- expected and observed access method
- whether the correct custodian and property were reached
- official route opened, return observed, and blocker
- artifact type, official no-record response, or written referral
- request status: not started, prepared, submitted/pending, replied
- elapsed time to official action and usable outcome
- reviewer verdict, confusion note, fix owner, and retest date

## Completion truth

- **Prepared:** published required details are ready.
- **Pending:** an official request was submitted and may have a reference.
- **Obtained:** a property-matched file, written official no-record response, or
  written referral was received.
- **Resolved:** the obtained result was reviewed and the next property decision
  was selected.

A blank search, blocked page, request reference, or user-reported download is not
alone a resolved case.

## Release acceptance

- 20/20 cases have a reviewer verdict.
- Zero wrong-property files are accepted as obtained.
- Every failure has a blocker category, owner, fix, and retest date.
- Verified routes show the current source, required inputs, manual boundary,
  completion definition, and recent live check.
- Baseline routes make no unverified fee, timing, field, or submission claim.
- Browser back/focus/refresh preserves the task; clear removes it.
- Analytics can separate baseline from county-specific routes and pending from
  obtained, without personal or property identifiers.

## Hold or downgrade

Downgrade a route to “Official starting point only” or “Office help required”
when the custodian is uncertain, the primary route is broken without a working
fallback, required fields are inferred rather than published, or a live retest
cannot reproduce the stated handoff.

Pause broader SEO rollout if wrong-route errors recur, record-obtained rate
cannot be measured separately from request submissions, or the privacy page no
longer matches the implemented data flow.

## Browser route audit — 2026-07-29

This audit checks whether the handoff reaches the intended official search,
form, office, or document in a real headed browser. It does **not** count a
loaded page as a retrieved property record and does not submit a request.

| County | Intended door | Browser observation | Product handling |
|---|---|---|---|
| Prince William, VA | Laserfiche document search | Opened the Health District document repository | Search plus office fallback |
| Tarrant, TX | OSSF office route | Opened the county OSSF page | Jurisdiction check remains first |
| Hamilton, TN | Permit document retrieval | Opened the county permit-applications/documents page | Search, then Groundwater fallback |
| Alamance, NC | Official information-request PDF | PDF opened | Prepare the county PDF |
| Knox, TN | County-branded file-search form | Live Jotform opened | User completes final form submission |
| Lincoln, NC | Onsite Water office route | Official page opened | Prepare address/parcel and use office/portal |
| DeKalb, GA | Open-records form | Current county-branded Jotform opened | Historical file and certification tasks stay separate |
| Blount, TN | Records Center | GovQA Records Center opened | Choose Developmental Services |
| St. Mary's, MD | Replacement public GIS | Current ArcGIS experience opened | Use PIA PDF only when GIS is incomplete |
| Suffolk, NY | Wastewater Management office | Current OWM page opened and confirmed 631-852-5700 | Phone-assisted lookup with Tax Map details ready |
| Maricopa, AZ | Free septic search | Official form opened | Free search before paid fallback |
| Brunswick, NC | Permit metadata | Permit Reports page opened | Metadata is a lead, not the source document |
| Forsyth, NC | Environmental Health request | County request form opened | Historical file and project release stay separate |
| Denton, TX | Public-information PDF | County PDF opened | Confirm OSSF jurisdiction before requesting |
| Brazoria, TX | Environmental Health contact | Official contact page opened | Confirm city, ETJ, or county authority |
| Thurston, WA | Historic septic archive | Laserfiche search opened | Parcel/permit/project identifier required |
| Harford, MD | Well and septic PIA PDF | County Health PDF opened | Complete the original form |
| Cumberland, NC | Water and Sewage record options | Official service page opened | Citizen Connect/layout fallback remains visible |
| San Diego, CA | Environmental Health Document Library | Document search opened | Search by APN, Record ID, or address clues |
| San Bernardino, CA | Current NextRequest portal | EHS homepage confirmed the destination; portal returned Cloudflare 403 in the automated browser | Preserve county-authored preparation fields, let the user pass browser verification, and show 800-442-2283 fallback |

Result: 18 of 19 tested web destinations opened to the intended official
surface, Suffolk's current official office page and phone route were confirmed,
and San Bernardino was retained as a blocked current portal with an explicit
phone fallback. No row above is marked obtained without a property-matched file,
written no-record response, or written referral.
