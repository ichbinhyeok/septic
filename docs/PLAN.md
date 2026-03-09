# Septic Project Plan

## Why this file exists

This file is the anchor document for the project. It defines the product scope,
state rollout logic, file-based data strategy, and the execution order that all
later implementation work should follow.

## Product position

- Homeowner planning estimator, not engineering design software.
- Estimate-first, source-transparent, quote-lead business.
- State-aware cost and sizing guidance with honest uncertainty.
- File-based application and data pipeline. No database in V1.

## Core product for V1

- Homepage
- Main septic cost estimator
- Dedicated septic tank size estimator
- Dedicated pump schedule and maintenance estimator
- Quote form
- National money pages
- Ten state guides

## Rollout decision

Launch ten state guides in V1, but do not launch the full state x money-page
matrix on day one.

What goes live now:

- 10 state guides
- 1 national cost calculator
- 1 dedicated tank size estimator
- 1 dedicated pump schedule estimator
- 1 quote form integrated into calculator results
- 7 national money pages
- 18 state-specific money pages
- 1 quote form

What waits until traffic proves itself:

- Full state x money-page matrix for all 10 states
- Contractor landing pages
- County-level expansion

## State rollout tiers

Anchor states:

- GA
- PA
- CT
- OR

Supporting states:

- MO
- NC
- NJ
- WA
- MA
- FL

Anchor states get deeper rule logic and richer unique copy first. Supporting
states still go live, but can publish with wider estimate bands and stronger
"verify locally" framing until deeper verification is complete.

## State launch gate

A state can go live only if all are true:

1. An official state source is identified.
2. The state page has a unique angle that is not just a swapped state name.
3. Local or county override risk is disclosed.
4. The estimator can frame output as a planning estimate, not a compliance
   result.
5. The page includes source links, agency name, and last verified date.

## Data model decision

No database in V1.

Use files for three different jobs:

1. Versioned research data
   - JSON and CSV checked into the repo.
   - Good for provenance, diffs, and manual review.
2. Generated application data
   - JSON generated from reviewed source files.
   - Loaded read-only by the app at startup.
3. Runtime submissions and events
   - JSON or NDJSON files written to disk.
   - Partitioned by date for easier storage and export.

## File layout

- docs/
  - PLAN.md
  - DATA_STRATEGY.md
- data/
  - README.md
  - raw/
    - source_registry.csv
    - state_profiles.json
    - cost_profiles.json
    - review_queue.csv
- storage/
  - README.md
  - leads/
  - events/

## Research workflow

1. Find official state source.
2. Record source in source_registry.csv.
3. Add or update state_profiles.json with only verified facts.
4. If a field is unclear, leave it null and create a review_queue entry.
5. Use commercial sources only for broad public cost anchors.
6. Do not publish state pages from unverified assumptions.

## Copy and design direction

- Style target: modern utility SaaS
- Light-first, not dark-mode-first
- Warm neutral backgrounds plus deep slate and moss/teal accents
- Official-source trust box on calculator and state pages
- Result cards with calm, data-forward presentation

## Technical direction

- Spring Boot
- JTE templates
- Token-based vanilla CSS in V1
- htmx is deferred until partial-page interaction becomes worth the added surface area
- Alpine.js is deferred until small interaction gaps actually appear
- JSON and CSV files as the system of record
- File-backed runtime storage for leads, events, and buyer-ready exports

## Implementation order

1. Keep the current layout foundation aligned with trust and conversion.
2. Deepen state data with homeowner workflow modules and cost multipliers where source quality supports it.
3. Expand only the strongest state x intent combinations first.
4. Refine the dedicated tank size and pump estimator routes after the main calculator and quote funnel stabilize.
5. Refine cost profiles from public sources without inventing false state precision.

## Definition of done for the next working phase

- Planning docs match the actual shipped surface.
- Source registry exists with official anchors for all 10 launch states plus cost-anchor provenance.
- State profiles include homeowner workflow modules, not just rule summaries.
- Storage convention exists for leads, events, and buyer-ready exports.
- Legal and trust pages exist in the footer before public launch.
