# SepticPath operating context

## Customer record-help operations

For customer status, next actions, case research, or agency correspondence, read
`docs/OPERATIONS.md` first. Run `node tools/operations.mjs status` and read the
relevant case in `storage/operations/ledger.json` before searching email.

The private ledger is the current operational source of truth. Gmail and old
research notes are evidence, not an alternate status database. Do not rebuild
known cases from full mail threads. Fetch only new mail since the saved sync
checkpoint (with an overlap), deduplicate by message ID, and triage the new
evidence. Explicit user requests to re-audit a source still take precedence.

After any intake, search finding, request, bounce, referral, reply, delivery, or
promise: update the case, affected branch, next action/check date, source IDs,
and reusable route knowledge in the same working turn. Use the transactional
`apply` command documented in OPERATIONS.md; render and validate afterward.
An external action is not fully recorded until its actual result is in the
ledger. A draft/planned action is never a sent or acknowledged action.

Always attach the applicable `route_ids` to each operational branch. Branch
status, custodian, request-number, evidence, and conclusion changes are
automatically captured as immutable route observations by `operations.mjs`.
When a new route is found, upsert it in that same transaction with structured
availability/channel/requester/fee fields when verified. Never wait for the user
to separately request knowledge capture. Review the generated private
`views/route-intelligence.*` output instead of counting outcomes from email.

Whenever Bing Webmaster Tools, Google Search Console, or GA4 is analyzed, add an
immutable `growth_signals` snapshot in the same operational transaction. Include
the measurement window, page/query scope, exact metrics, and applicable
`route_ids`. Do this without waiting for a separate user request so search
demand, operating outcomes, and route quality compound in one database.

Never email merely because a ledger task is due: sending still needs user
authorization. Keep customer data and unredacted sources inside ignored private
storage. Never publish the private dashboard or commit the private ledger.
Do not claim inbox freshness beyond the saved checkpoint. If the ledger is
missing, report missing local data and recover a private backup; do not silently
create an empty replacement or mark historical cases completed.
