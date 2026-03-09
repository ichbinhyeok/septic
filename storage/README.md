# Runtime Storage

This project does not use a database in V1.

Runtime writes should go to disk in a predictable structure.

## Lead submissions

- `storage/leads/YYYY/MM/DD/<timestamp>-<uuid>.json`

Each lead file should contain:

- submission timestamp
- source page URL
- calculator type
- state
- project type
- user inputs snapshot
- result summary snapshot
- contact fields
- consent text snapshot

## Event stream

- `storage/events/YYYY/MM/DD.ndjson`

Each line should be a single JSON event.

Suggested event types:

- `calculator_started`
- `calculator_completed`
- `result_cta_clicked`
- `quote_form_started`
- `quote_form_submitted`

## File-write rules

- create parent directories on demand
- write to a temp file first
- rename atomically into place
- never rewrite historical lead files in place
