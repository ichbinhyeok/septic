# Lead Export

Last updated: 2026-03-09

## Decision

Keep the app database-free in V1, but write every submitted lead in two formats:

- internal archival lead JSON
- normalized buyer export JSON plus a daily CSV queue

## Why

- Internal lead files preserve the exact quote snapshot.
- Export files make it easy to hand leads to a platform buyer later without rewriting historical data.
- Daily CSV gives a simple review and upload surface for manual operations.

## Runtime Files

- `storage/leads/YYYY/MM/DD/<timestamp>-<uuid>.json`
- `storage/events/YYYY/MM/DD.ndjson`
- `storage/exports/pending/YYYY/MM/DD/<timestamp>-<uuid>.json`
- `storage/exports/daily/YYYY/MM/DD.csv`

## Normalized Export Payload

Top-level sections:

- `exportVersion`
- `leadId`
- `submittedAt`
- `exportStatus`
- `vertical`
- `serviceCategory`
- `leadType`
- `consumer`
- `project`
- `estimate`
- `consent`
- `provenance`
- `routingHints`

## Consent / Provenance Fields

The export payload captures:

- consent accepted flag
- consent accepted timestamp
- consent text snapshot
- consent language version
- submitted path and URL
- referrer
- user agent
- forwarded IP header when present
- remote address

## Routing Hints

Current routing hints are intentionally simple:

- buyer channels: `batch_json`, `batch_csv`
- urgency bucket from calculator timeline
- risk band from likely system class
- geo target from state and ZIP
- tags from state, project type, and likely system class

## Notes

- This is not a buyer-specific API contract.
- It is a stable internal schema meant to preserve first-party lead context and make later buyer integration easier.
- If a specific platform requires a stricter field set, add a translator layer rather than rewriting stored history.
