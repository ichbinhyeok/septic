# County Content Rubric

## Goal

Use county pages to build the part of the site that is hardest to fake:

- real office routing
- real file retrieval
- real permit or transfer friction
- real local exceptions that change the next action

This rubric exists to keep county expansion focused on pages that act like a
workflow tool, not just a county-name wrapper around state content.

## What "good" county content actually is

A strong county page changes what the user does next.

That usually means the page gives at least one of these:

- a concrete records search surface
- a concrete permit packet or form surface
- a concrete transfer, sale, or inspection workflow
- a concrete O&M or compliance obligation
- a concrete local exception that widens the scope

If the page only says "call Environmental Health," it is not strong enough.

## Highest-value county page angles

### 1. Records search pages

Best when the county gives:

- parcel or APN lookup
- address-based search
- permit-number search
- searchable HSTS or OWTS portal
- public-records request path when the digital file is incomplete

These pages are strongest because the user can act immediately.

### 2. Transfer or buyer pages

Best when the county gives:

- point-of-sale inspection
- home-sale evaluation
- buyer-submitted transfer inspection
- certificate or occupancy-linked septic check
- owner-change O&M update requirement

These pages are valuable because they change transaction timing and risk.

### 3. Permit packet pages

Best when the county gives:

- permit packet PDF
- site-plan requirements
- plan-review sequence
- pre-construction inspection
- final approval or license-to-operate sequence

These pages are strongest when they expose the real permit ladder.

### 4. Exception-driven pages

Best when the county gives:

- deed recordation
- ETJ or jurisdiction split
- subdivision or zoning blocker
- flood or missing-record fallback
- alternative-system O&M obligation

These are useful because they break the "simple low-end story."

## Minimum source pack by page type

### Records-led county page

Must have:

- one official office page
- one official records path

Should have:

- search instructions
- parcel, APN, address, or permit-number input guidance
- explicit "digital file may be incomplete" note if true

### Transfer-led county page

Must have:

- one official sale, transfer, or buyer inspection source
- one official office or forms source

Should have:

- timing rule
- requester responsibility
- transfer blockers

### Permit-led county page

Must have:

- one official permit packet or application source
- one official office or workflow source

Should have:

- sequencing detail
- field inspection or approval milestone
- file items required before permit issuance

### Exception-led county page

Must have:

- one official source that clearly states the exception
- one official source that shows what the user does next

Should have:

- a second exception or compliance source
- a records fallback if the exception depends on file quality

## Raw data fields we actually need

The current county JSON object supports publication, but it does not force the
researcher to capture the most decision-relevant facts. Bulk acquisition should
capture these fields even if the public page does not render all of them yet.

### Core identity

- `county`
- `state`
- `wedge_type`
- `primary_user_intent`

### Office and records

- `office_url`
- `records_url`
- `search_mode`
- `search_inputs`
- `public_records_fallback`

### Workflow artifacts

- `must_request_artifacts`
- `permit_sequence`
- `inspection_trigger`
- `transfer_trigger`
- `o_and_m_trigger`

### Local variance

- `jurisdiction_split`
- `exception_trigger`
- `missing_record_fallback`
- `nonstandard_system_trigger`

### Trust and publication

- `source_ids`
- `last_verified_at`
- `launch_tier`
- `why_this_county_is_not_generic`

## How to turn raw sources into content

Do not stop at source aggregation.

Each county page should be transformed into:

- one clear first move
- one clear file or form to pull
- one clear reason the cheap story may be wrong
- one clear county-specific rule that makes this page different

The page should answer:

- where do I start
- what do I request
- what does the county need next
- what can widen the job or delay the transaction

## Publication tiers

### Tier A

Publish and surface aggressively.

Requirements:

- strong records or search artifact
- strong permit or transfer artifact
- obvious next action

### Tier B

Publish, but do not prioritize until the county graph around it is thicker.

Requirements:

- at least one strong official artifact
- meaningful local difference
- weaker search or retrieval surface than Tier A

### Tier C

Research only. Do not publish yet.

Examples:

- generic office contact page
- no retrieval method
- no transfer, permit, or exception detail
- county page repeats the state page without changing the next action

## Bulk acquisition priorities

Prioritize counties where at least one of these is true:

- the county has a searchable record surface
- the county has a point-of-sale or transfer inspection
- the county has a permit packet with sequence detail
- the county has an exception that makes the page memorable

De-prioritize counties where the only official signal is a phone number.

## Working rule

If the county page would not change the user's first or second move, do not
publish it yet.
