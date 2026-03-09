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
- Septic tank size estimator
- Pump schedule and maintenance estimator
- Quote form
- National money pages
- Ten state guides

## Rollout decision

Launch ten state guides in V1, but do not launch the full state x money-page
matrix on day one.

What goes live now:

- 10 state guides
- 1 national cost calculator
- 1 national tank size estimator
- 1 national pump schedule estimator
- 4 to 6 national money pages
- 1 quote form

What waits until traffic proves itself:

- State-specific replacement pages for all 10 states
- State-specific perc/drainfield/pumping pages for all 10 states
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
- Tailwind CSS
- htmx for partial page updates
- Alpine.js only for small interaction gaps
- JSON and CSV files as the system of record

## Implementation order

1. Add front-end stack and layout foundation.
2. Load file-based source and state data into typed Java models.
3. Build homepage and calculator shell.
4. Build state page template with trust box and source module.
5. Build lead form and file-based submission storage.
6. Add event tracking and result-page explanation modules.
7. Expand state coverage and refine cost profiles.

## Definition of done for the next working phase

- Planning docs exist and are stable.
- Source registry exists with official anchors for the launch states.
- Initial state profile JSON exists for the launch states.
- Review queue exists for fields that still need verification.
- Storage convention exists for leads and events.
