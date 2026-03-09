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
- `raw/review_queue.csv`

## Editing rules

- Do not invent state rules.
- Leave unknown values as null.
- Add a review queue item when a field is unclear.
- Keep official sources separate from commercial cost anchors.
