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
