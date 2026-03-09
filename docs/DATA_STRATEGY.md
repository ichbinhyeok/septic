# File-Based Data Strategy

## Goal

Use simple files instead of a database while keeping the data model auditable,
versioned, and safe to publish.

## Why files are the right fit now

- Source data changes through research, not through high-volume transactions.
- The project benefits from diffs, code review, and provenance tracking.
- State rules and cost profiles are mostly reference data.
- Runtime lead volume in V1 can be handled with file writes.

## Data classes

### 1. Source-of-truth research files

These are hand-reviewed and committed to the repo.

- `data/raw/source_registry.csv`
- `data/raw/state_profiles.json`
- `data/raw/cost_profiles.json`
- `data/raw/review_queue.csv`

### 2. Derived application files

These can be generated later if needed.

- `data/generated/state_pages.json`
- `data/generated/calculator_rules.json`
- `data/generated/source_sets.json`

V1 can load directly from `data/raw/` if generation is not needed yet.

### 3. Runtime files

These are written by the app at runtime.

- `storage/leads/YYYY/MM/DD/<timestamp>-<uuid>.json`
- `storage/events/YYYY/MM/DD.ndjson`

## Format choices

Use CSV when:

- the records are flat
- spreadsheet review is useful
- row-based exports matter

Use JSON when:

- nested rule structures are required
- one state needs arrays or mixed-value logic
- preserving structure matters more than spreadsheet editing

## Recommended format by entity

- Source registry: CSV
- Review queue: CSV
- State profiles: JSON
- Cost profiles: JSON
- Runtime lead submissions: JSON
- Runtime event stream: NDJSON

## Validation rules

### source_registry.csv

Required columns:

- `source_id`
- `state_code`
- `source_type`
- `agency_name`
- `title`
- `url`
- `last_verified_at`
- `trust_level`

Rules:

- `source_id` must be unique.
- `state_code` must be `US` or a valid two-letter state code.
- `url` must be absolute.
- `trust_level` must be `high`, `medium`, or `low`.

### state_profiles.json

Rules:

- one record per state
- unknown values remain `null`
- arrays are used only when structure adds value
- each publishable rule must reference at least one `source_id`
- each state must include a visible `localOverrideNote`

### cost_profiles.json

Rules:

- numeric cost values remain null until sourced
- every public number needs at least one source reference
- commercial cost anchors are allowed, but must not be treated as legal or
  engineering truth

### runtime lead files

Rules:

- write one JSON document per submission
- never append multiple leads into a single mutable array file
- capture consent text snapshot and source URL at submission time
- store a normalized service and project type

## Write strategy

For runtime writes:

1. Serialize JSON to a temporary file in the target directory.
2. Flush and close the file.
3. Rename atomically into the final filename.

This avoids partial writes and makes later export jobs simpler.

## Review workflow

1. Research source URL.
2. Add source row.
3. Update state or cost profile.
4. If any field remains unclear, add a review queue row.
5. Only after review should the page logic or content use the field.

## Publication rule

The app should never silently fabricate missing values.

If a state field is missing or low confidence:

- widen the estimate band
- label the result as lower confidence
- show the local verification note
- show the official source module
