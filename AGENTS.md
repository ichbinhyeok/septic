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
For every case, capture any newly learned official portal or agency channel as
reusable route knowledge without a separate user prompt. Record the direct URL
or exact contact channel, jurisdiction and record type, search keys and steps,
requester requirements, verified fee, actual result, limitations, fallback,
verification date, and source IDs. Distinguish a route that was merely suggested
from one that was opened, submitted, acknowledged, or returned original files.
If a channel bounces or fails, retire it as the primary route immediately and
record the verified alternate or the unresolved gap. Keep customer identifiers
and unredacted examples private; publishable SEO guidance needs separate review.

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

For any "recent N days" customer count or bulk follow-up, define the exact
timezone/window and reconcile bounded Gmail intake message IDs against ledger
`intake_message_id` and `intake_at` before stating a complete count. Paginate
the bounded search. Check the customer's actual sent thread for personal updates
and bounces; `last_customer_update: null` alone does not prove that only an
automatic acknowledgement was sent. Resolve mismatches in the ledger before
selecting recipients. If the saved sync checkpoint predates the window, label
the result a bounded Gmail audit, not a fully current inbox sync.

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

## Mandatory mail screenshot gate

Before showing a mail screenshot to the user or attaching one to an external
message, crop it to the relevant target message's original body or answer only.
Never show the Gmail inbox, navigation, toolbar, message header, other messages,
or translated/recreated text as if it were the original reply. Keep the full
capture in ignored private storage. Record the target message ID, source SHA-256,
reviewed body bounds, crop and excluded UI regions in a private manifest. Run
`python tools/mail-capture-gate.py <manifest-path>` and visually inspect the
output. Immediately before sharing, run
`python tools/mail-capture-gate.py --verify <manifest-path>` and require a
current `MAIL CAPTURE GATE: PASS` receipt. Changed source, crop, output, or
review date requires a fresh gate. A blocked or visually incorrect crop must
not be shown or sent. This gate supplements the attachment delivery gate when
the screenshot is also customer-case evidence.
