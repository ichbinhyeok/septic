# Workflow Tracker

Last updated: 2026-04-16

## Why this file exists

This file tracks the vendor workflow channel separately from organic search.

Use it to record:

- which packet was live
- who it was designed for
- what the pinned first move was
- whether recipients moved into the correct workflow page
- whether the workflow was reused

Do not use this file to log search impressions or ranking changes unless they
directly affect packet routing.

## Core metrics

- packet page visits
- packet -> state workflow page clicks
- packet -> county page clicks
- packet -> guide clicks
- repeated use of the same packet by the same outreach motion
- manual vendor feedback
- whether recipients reached the official-source layer

## Active packets

- `V0 live`: Indiana records packet
- `V0 live`: New York buyer diligence packet
- `V0 live`: South Carolina permit prep packet
- `V1 queued`: Alabama records / perc packet
- `V1 queued`: Georgia records / perc packet
- `V2 backlog`: packet generator and sender-specific reuse

## 2026-04-16

### What changed

- added SepticPath-specific workflow-channel strategy in
  `docs/WORKFLOW_CHANNEL.md`
- split workflow tracking from `docs/ORGANIC_TRACKER.md`
- shipped public `noindex` packet pages for:
  - `/for-professionals/records-packet/indiana/`
  - `/for-professionals/buyer-diligence-packet/new-york/`
  - `/for-professionals/permit-prep-packet/south-carolina/`
- fixed each packet to one explicit first move:
  - Indiana -> `/septic-records-checklist/indiana/`
  - New York -> `/buying-a-house-with-a-septic-system/new-york/`
  - South Carolina -> `/septic-permit-process/south-carolina/`

### Why these packets were chosen

- Indiana is the cleanest records and county wedge already proven in search.
- New York already shows buyer-intent proof and naturally hands off into
  records.
- South Carolina already shows state-specific permit-path proof.

### What to check next

- whether packet clicks move into the pinned state workflow page
- whether Indiana packet clicks continue into county pages
- whether New York packet clicks continue into records
- whether South Carolina packet clicks continue into permit links and source
  sections
- whether any packet is reused in manual outreach more than once

## Entry template

```md
## YYYY-MM-DD

### What changed

- packet(s) updated:
- first move pinned to:
- send note changed:

### Early signals

- packet page visits:
- packet -> state workflow clicks:
- packet -> county clicks:
- repeat sends:
- vendor feedback:

### Interpretation

- what seems to be working:
- where the chain is breaking:

### Next action

- narrow:
- expand:
- pause:
```
