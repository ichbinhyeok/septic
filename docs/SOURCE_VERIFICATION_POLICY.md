# Source verification policy

SepticPath records editorial verification and automated HTTP reachability as two different facts. An HTTP 200 response never advances an editorial review date, and a recent editorial review never implies that an automated client can reach the source today.

## Registry fields

| Field | Meaning |
|---|---|
| `last_content_verified_at` | Date a human reviewed the source's substance or approved a replacement source |
| `last_http_checked_at` | Date the automated source-health audit last requested the URL |
| `http_check_status` | `healthy`, `blocked`, `rate_limited`, `transient`, `inconclusive`, `review`, or `dead` |
| `verification_method` | Provenance of the editorial date, such as `editorial_registry`, `legacy_editorial_review`, or `source_replacement_review` |
| `review_status` | `content_reviewed_http_healthy` or `manual_http_review` |

## Publication gate

- A published evidence page must have an editorial content review within 180 days, or a healthy HTTP result paired with an editorial review within 365 days.
- Only confirmed HTTP 404 or 410 responses are classified as `dead` automatically.
- Bot blocks, timeouts, TLS failures, rate limits, and server errors enter `manual_http_review`; they are not silently treated as healthy or dead.
- Replacing a source requires an individual source review and uses `source_replacement_review`.
- A date-only registry edit is not evidence. The audit restores unsupported date-only bumps when a Git baseline is available.

## Operating procedure

1. Run `tools/check_source_health.py` weekly and on demand.
2. Keep the JSON evidence artifact from each run.
3. Resolve confirmed dead links before publication.
4. Review the manual queue by source importance and page reach, starting with sources used by the most indexable pages.
5. Update `last_content_verified_at` only after checking the source content and recording the appropriate method.

The GitHub workflow in `.github/workflows/source-health.yml` performs the scheduled audit. The PowerShell wrapper is available at `tools/check-source-health.ps1`.
