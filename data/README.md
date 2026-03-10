# Data Directory

This project uses files as the source of truth in V1.

## Directory layout

- `raw/`
  - hand-edited research data
  - versioned in git
- `generated/`
  - optional derived files for the application

## Current files

- `raw/source_registry.csv`
- `raw/state_profiles.json`
- `raw/cost_profiles.json`
- `raw/content_pages.json`
- `raw/cost_evidence.json`
- `raw/state_money_pages.json`
- `raw/state_rule_facts.json`
- `raw/review_queue.csv`

`review_queue.csv` is a research backlog file. It is not currently loaded by the runtime application.

## Editing rules

- Do not invent state rules.
- Leave unknown values as null.
- Add a review queue item when a field is unclear.
- Keep official sources separate from commercial cost anchors.
