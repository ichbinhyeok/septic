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

## Mandatory attachment delivery gate

Before creating a Gmail draft, sending or forwarding email, or uploading to an
external portal when the message attaches or relies on a customer-case file:

1. Download and register the exact source file locally with its SHA-256.
2. Create a private manifest under `storage/operations/delivery-gates/` covering
   every attached or summarized file.
3. Review every page and record street/address, parcel, plat/subdivision, lot,
   owner/applicant, and geometry as match, mismatch, not present, or not
   applicable. Never infer a match from the email thread, request subject,
   agency sender, filename, or surrounding case context.
4. Run `node tools/delivery-gate.mjs <manifest-path>` and require a current
   `DELIVERY GATE: PASS` receipt. A mismatch, unlinked identity, incomplete page
   review, changed hash, or blocked source prohibits the external action.
5. Rerun the gate if the recipient, subject, source file, attachment set, file
   bytes, or customer-facing filename changes. Receipts expire after 24 hours.

Do not bypass or soften a failed gate. Keep an unmatched agency return as
unlinked evidence and update the case instead of sending or interpreting it as
the requested property's record.
