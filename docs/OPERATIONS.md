# SepticPath operations — start here

The private **current-state database** is `storage/operations/ledger.json`.
The browser/table view is `storage/operations/index.html`.
The concise agent-readable view is `storage/operations/STATUS.md`.

Start status questions with:

```powershell
node tools/operations.mjs status
node tools/operations.mjs show case-tn-blount-001
node tools/operations.mjs validate
```

These commands do not open Gmail. The database records customer intent, current
conclusions, independent investigation branches, deadlines, next actions,
confirmed deliveries, source IDs, original files, and reusable regional routes.
Same-name customers are distinguished by stable case ID, email, and parcel.
The JSON is canonical; CSV, HTML and STATUS.md are generated read-only views.
Do not maintain separate manual status lists in TODOS or research reports.

Route intelligence is accumulated automatically. Whenever an `apply` transaction
changes a branch's route, custodian, status, request number, evidence or material
conclusion, the tool appends an immutable observation for every linked route.
The derived files are `views/route-intelligence.json` and
`views/route-intelligence.csv`; they summarize route usage, outcomes and measured
response time without rebuilding state from mail. A schedule-only edit does not
create a false observation.

Search analytics snapshots live in `growth_signals`. When Bing Webmaster Tools,
Search Console or GA4 is inspected, record the observed window, platform, page or
query scope, exact metrics and related `route_ids` in the same working turn.
Snapshots are immutable: a later measurement is a new row, not an overwrite.
Generated views are `views/growth-signals.json` and `.csv`.
The renderer joins the latest signal for each platform/scope with route outcomes
and writes `views/growth-opportunities.json` and `.csv`. The opportunity type
distinguishes CTR/handoff work, operational bottlenecks, verified proof worth
publishing, and routes that still need evidence. Scores prioritize review; they
do not claim causal lift or guaranteed rankings.

Customer acquisition attribution is rendered automatically from each case's
`entry_page` and `source_context` into `views/case-attribution.json` and `.csv`.
Use this view to distinguish pages that merely attract visits from pages and CTA
contexts that produce delivered work. A missing historical `entry_page` remains
explicitly grouped by source context; do not invent a URL from the case location.

## What was migrated

The September 13, 2026 migration reconciled all located real record-help intakes
and the direct customer referral, the former nine-row case log, route cards,
route corrections, discovery checklist, private-well playbook, recent research
reports, local source PDFs and related case correspondence. Production tests,
duplicate Wayne intake, and vendor outreach are not additional customer cases.
Exact source inventory and limitations are in private `MIGRATION.md` and the
ledger's `migration` object. Original notes are preserved as dated evidence;
their historical 'not sent' statements do not override later verified sends.

## A normal work session

1. Read STATUS.md / relevant case and branch. Retrieve an original only when
   the analysis requires it, not to reconstruct the current state.
2. For new email, fetch incrementally from the last `sync.checked_through`,
   overlapping at least one calendar day. Include new intake subjects, known
   customer/custodian participants, portal notices and delivery failures.
   Paginate completely. Gmail labels/snippets alone are not proof of meaning.
3. Persist normalized **new** messages once, with ID, thread ID, date, from, to,
   subject, text, attachment metadata and Gmail link. Never overwrite an
   existing message. Import using `ingest-mail` below.
4. Triage every imported message. Link it to a case/branch, record what changed,
   update the next action and check date, and mark the inbox item triaged.
   Same email can affect more than one branch. If the property/recipient is
   uncertain, leave it untriaged rather than guessing.
5. Save the actual outcome of any authorized external action immediately,
   including its message/reference ID. Update the operational state even when
   the action fails. No external send occurs from this tool.
6. If a route changes, update the route and lesson in that same transaction.
7. Link every operational branch to the applicable `route_ids`. This is what
   makes sends, acknowledgements, failures and received records compound into
   reusable route intelligence without a separate logging task.
8. Validate and render. Advance the inbox checkpoint only after the complete
   incremental fetch was triaged. The tool blocks advancing with untriaged mail.

### Route knowledge to capture on every case

Treat each agency reply, portal search, file return, bounce, and no-record result
as a chance to improve the regional route. In the same `apply` transaction,
record the direct official URL or exact working contact, jurisdiction and record
type, the address/parcel/permit search keys and click path, requester or account
requirements, verified fee, observed outcome, known limitations, fallback, date,
and source IDs. Put actionable URLs in `first_action` or `primary_channel`, not
only in a source email. Use structured `availability`, `primary_channel`,
`requester_requirements`, and `fee_policy` fields; leave unverified values
`unknown`. Write a lesson when the finding changes how the next case should be
handled. A source that was suggested, a portal that was opened, a request that
was submitted, an automated receipt, an agency acknowledgement, and a returned
original are distinct outcomes. Replace a bounced or failed channel as the
primary route in the same turn. Check `views/route-intelligence.*` after render.

These private observations are operating assets. Derive public SEO material
only after separate source and privacy review; do not expose customer parcels,
unredacted emails or private ledger entries.

### Mandatory returned-file identity gate

Before treating any agency attachment as responsive evidence, interpreting it,
renaming it, adding it to a customer packet, or forwarding it:

1. Open every page and record the visible street/house number, parcel ID,
   plat/subdivision, lot number, owner or applicant, and map geometry when each
   item is present.
2. Compare those anchors with the controlling case record and original parcel
   or plat. A matching email subject, thread, request number, or agency cover
   message does not prove that its attachment matches the property.
3. If an anchor conflicts, quarantine the attachment as a custodian mismatch,
   exclude all of its annotations and conclusions, and set a correction action
   if it was already delivered.
4. If the attachment lacks enough anchors to identify the property, keep it as
   unlinked evidence. Do not fill the gap from the filename or request context.
5. A customer-facing filename may describe a parcel only after this check is
   recorded. Otherwise preserve the agency filename and label the match as
   unverified.

For every external message or portal submission that attaches **or summarizes**
a customer-case file, the recorded check must be enforced by the local gate:

```powershell
node tools/delivery-gate.mjs storage/operations/delivery-gates/<manifest>.json
```

The command verifies the case recipient, local source and ledger SHA-256, every
page reviewed, all six identity anchors, strong property linkage, map geometry,
filename changes, the source blocklist and review freshness. It writes a receipt
valid for 24 hours only when every artifact passes. Do not create an external
draft, send, forward, or upload unless the exact planned delivery has a current
PASS receipt. Templates and field guidance are in
`storage/operations/delivery-gates/README.md`.

### Mandatory mail screenshot gate

For any email evidence screenshot shown to the user or attached externally,
capture the target message and deliver only a crop of its original body or
relevant answer. Do not include inbox/sidebar, toolbar, message header, other
messages, or translated/recreated text in that image. Preserve the full source
only in ignored private storage. In a private JSON manifest, record the target
message ID, source path and SHA-256, body bounds, crop, excluded UI regions,
reviewer and review time. The gate creates the crop and receipt:

```powershell
python tools/mail-capture-gate.py storage/operations/sources/<manifest>.json
python tools/mail-capture-gate.py --verify storage/operations/sources/<manifest>.json
```

Inspect the cropped image visually. Require a current `MAIL CAPTURE GATE: PASS`
verification immediately before display or send; rerun creation when any source
byte, crop, output, or review changes. If the crop cannot isolate the answer,
provide a text summary and source link rather than showing a full mail UI.
The gate checks image geometry and hashes; visual review confirms meaning.
When the screenshot is customer-case file evidence, also run the mandatory
attachment delivery gate before the external action.

There is **no background Gmail synchronizer** running. An agent/operator must
perform the incremental import. Do not claim the inbox is current past its
checkpoint. Reading the dashboard requires no mail access; checking *new*
replies naturally does. Do not perform a full mailbox reconstruction each time.

### Recent-window customer count gate

Before reporting a "last N days" intake or selecting customers for a bulk
progress update, state the exact timezone and start/end instants. Run a bounded,
paginated Gmail intake search for that window and compare every provider message
ID with case `intake_message_id` and `intake_at`. A missing case or timestamp
blocks a complete count until reconciled. Check each candidate's actual sent
thread and bounce history; `last_customer_update: null` is insufficient proof
that the customer received only the automatic acknowledgement. Distinguish
research begun, first personal update sent, completed/offer sent, undeliverable,
and intake not yet researched. Record source IDs and actual sends in the same
`apply` turn. Validation now rejects cases without a linked intake message and
verified UTC `intake_at`. A bounded Gmail audit does not advance the global sync
checkpoint; advance it only after the complete incremental inbox fetch and
triage described above.

## Transactional updates

Write a private JSON change file with apply_patch. Then run:

```powershell
node tools/operations.mjs apply storage/operations/change.json
node tools/operations.mjs validate
```

Example (replace identifiers and revision with values actually read):

```json
{
  "expected_revision": 2,
  "reason": "County acknowledged the existing request",
  "upsert": {
    "branches": [{
      "id": "case-tn-example-001-septic",
      "status": "acknowledged",
      "summary": "Acknowledged; file search still pending",
      "source_ids": ["actual-new-message-id"],
      "next_action": "Review returned file or follow up after stated window",
      "next_check": "2026-09-21"
    }],
    "cases": [{
      "id": "case-tn-example-001",
      "status": "waiting_agency",
      "summary": "County acknowledged; original not yet received",
      "next_action": "Check county response",
      "next_check": "2026-09-21"
    }],
    "inbox": [{"id":"inbox-actual-new-message-id","status":"triaged"}]
  },
  "event": {
    "case_id": "case-tn-example-001",
    "summary": "County acknowledged; awaiting source file",
    "source_ids": ["actual-new-message-id"]
  }
}
```

`upsert` supports cases, branches, routes, growth_signals, sources, tasks,
lessons and inbox.
Rows are merged by stable ID. Include full arrays such as source_ids/route_ids
when extending them; arrays replace rather than append. Evidence is immutable.
Every transaction checks revision, foreign keys, duplicate intake IDs, evidence
requirements and next-action dates; it saves the previous ledger to backups
before atomic replacement. Stale revisions fail rather than overwrite work.
`route_observations` and `events` are append-only and generated by the tool; do
not upsert them manually. Optional structured route fields are `availability`,
`primary_channel`, `requester_requirements`, and `fee_policy`. Unknown values
must remain `unknown` rather than being inferred from an unanswered request.
For a new case include the fields in an existing case, with unknowns explicitly
null and at least one branch. Never reuse a case ID for a different property.

Incremental email import format:

```json
{"messages":[{"id":"provider-message-id","thread_id":"provider-thread-id","timestamp":"2026-09-14T14:00:00Z","from":"sender","to":"recipient","subject":"subject","text":"decoded body","attachments":[]}]}
```

```powershell
node tools/operations.mjs ingest-mail storage/operations/new-mail.json
```

Import is idempotent by message ID and creates an untriaged queue. It does not
infer receipt, resolution, jurisdiction, permission to send, or customer intent.

## Status rules

- Case and branch states are separate. A well no-match does not close septic.
- `sent_unconfirmed` means provider accepted outgoing email, not agency receipt.
- `acknowledged` needs agency/portal evidence and its actual request number.
- `delivery_issue` includes a bounce/restricted group; keep exact failed recipient.
- `blocked_intake` means a form/profile prevented submission. An email asking
  for assistance does not establish completed formal intake.
- `received` means a document arrived; property matching and interpretation
  may remain. Metadata, a marketing map and a historical seller disclosure are
  distinct from the actual approved source record.
- `delivered` / `delivered_limited` need customer-delivery evidence. They mean
  the researched result was sent, not that the property is certified safe or
  the customer's transaction succeeded.
- `closed_undeliverable` is an internal stop for a customer address confirmed
  invalid by delivery-failure evidence. It is not a delivered result or a
  septic-record conclusion. Do not send to that address again; preserve any
  already-sent agency branch and record later agency replies as route evidence.
- `closed_customer_withdrew` records the customer's own reply that they solved
  the issue or no longer need the result. Keep that reply as closure evidence;
  this state is not counted as a delivered SepticPath result.
- No reply found is not rejection. A named failed recipient does not prove
  failure of all recipients. Unknown remains unknown.
- Dates for deadlines are local dates with case timezone; mail timestamps are
  UTC. Do not conflate Korea's date with the agency's receipt date.
- Next-action dates are internal review dates, not automatic external sends.

## Durable evidence and recovery

- Local PDFs/reports were copied out of tmp/output into `storage/operations/sources`.
- Each copied source has SHA-256 and its original path. Mail bodies are saved
  locally with exact source and thread IDs. Attachments not available locally
  remain explicitly referenced to the source email; do not claim downloaded.
- `storage/operations/backups/` contains pre-edit snapshots. Back up the entire
  `storage/operations` folder to an owner-controlled private backup location for
  protection against disk loss. No external backup destination is configured.
- The database and generated views contain personal information and are ignored
  by Git. Do not deploy them, place them under static resources, or upload them
  to a public dashboard. Source files are retained, not deleted during migration.
- If starting on another machine, recover the private folder first. Missing
  ledger is an error, not permission to create a blank one.

## Prior documents

`RECORD_HELP_OPERATIONS_PLAYBOOK.md` and `ONLINE_ROUTE_DISCOVERY_CHECKLIST.md`
remain procedural references. Regional route cards and correction markdown are
historical reference material; current route status and evidence live in the
private ledger. `RECORD_HELP_CASE_LOG.csv` is now a generated anonymized export.
`TODOS.md` remains a product backlog plus a pointer to the operational queue.
Public source-registry changes still require a separate reviewed product edit.

## Verify maintenance tooling

```powershell
node --test tools/operations.test.mjs
node tools/operations.mjs validate
```
