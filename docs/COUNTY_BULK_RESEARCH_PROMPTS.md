# County Bulk Research Prompts

This file standardizes the prompt used to bulk-source county workflow wedges for septic records, permit, buyer, and transfer intent pages.

## Goal

Find county pages that are strong enough to become real `county workflow objects`, not generic county-name pages.

A strong county candidate must expose at least two of:

- `records artifact`: portal, file review request, APN or parcel search, permit-history search, as-built drawing path
- `transfer artifact`: property-transfer inspection, lender or buyer evaluation, waiver or extension form
- `permit artifact`: install, repair, alter, replace, abandonment, permit packet, design or site-plan requirement
- `operations artifact`: permit-to-operate, operation and maintenance, annual or five-year local service rule
- `layout artifact`: repair area, replacement area, bedroom-count mismatch, soils or perc trigger, sewer-availability gate

The output must prefer counties where the official county sources visibly change the user's next action.

## Master Prompt

Use this prompt template when spawning an explorer:

```text
Repo: C:\Development\Owner\septic

You are doing county wedge research for a septic workflow SEO/product system.

Your task:
Find the best NEW county candidates for {STATE_NAME} that can become county workflow pages.

Constraints:
1. Exclude counties already present in data/raw/county_records_pages.json for that state.
2. Use only official county or county-health / county-environmental-health / county-engineering / county-community-development sources.
3. Ignore generic directory pages unless they clearly route to a real septic artifact.
4. Prefer counties where next actions are explicit:
   - records request or file review
   - parcel/APN or permit history lookup
   - transfer inspection or buyer evaluation
   - permit / repair / replace / abandon workflow
   - operation and maintenance permit
   - repair area / replacement area / sewer-availability / variance / waiver branch
5. Do not edit code.

Return the TOP {N} counties only.

For each county return this exact structure:

COUNTY: <County Name>
SLUG: <county-slug>
WHY_IT_IS_STRONG:
- <bullet>
- <bullet>
- <bullet>

OFFICIAL_SOURCES:
- <proposed_source_id> | <agency> | <title> | <url>
- <proposed_source_id> | <agency> | <title> | <url>
- <proposed_source_id> | <agency> | <title> | <url>
- optional fourth source in same format

WORKFLOW_SUMMARY:
- <4 to 6 short bullets describing the real user workflow>

PAGE_ANGLE:
- intro: <1 sentence>
- unique_angle: <1 sentence>
- first_cta: <what the recordsLabel / first CTA should be>
- records_to_request:
  - <artifact 1>
  - <artifact 2>
  - <artifact 3>
- low_end_breakers:
  - <breaker 1>
  - <breaker 2>
  - <breaker 3>

RISKS:
- <what is weak or uncertain about this county, if anything>

At the end, include:
RECOMMENDED_ORDER:
1. <best county>
2. <second best county>
...

QUALITY RULE:
If a county does not have enough official artifacts to support a real county workflow page, do not include it.
```

## State Variants

### California

Use `N=5` and bias toward:

- file review request
- APN or parcel map lookup
- permit-history retrieval
- OWTS design, repair area, replacement area
- ADU or addition friction

### New York

Use `N=5` and bias toward:

- property-transfer inspection
- septic inspection protocol
- variance or waiver
- permit to operate
- replacement funding

### Ohio

Use `N=5` and bias toward:

- real-estate sewage evaluation
- transfer inspection
- sewer-availability gate
- O&M permit
- permit drawings / as-built / records portal

## Output Handling

When explorer results come back:

1. Rank counties by wedge strength, not by convenience.
2. Prefer counties with direct official artifact language over counties with only general septic pages.
3. Batch-add multiple counties per state once the source pack quality is high enough.
4. Add tests that verify:
   - sitemap inclusion
   - county page rendering
   - state records router surfacing the new county links

