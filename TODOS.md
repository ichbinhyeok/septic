# TODOS

## Record-help operations

Customer status and follow-ups moved to the private operations ledger on
2026-09-13. Read `docs/OPERATIONS.md`, then run:

```powershell
node tools/operations.mjs status
```

The ledger includes located cases, per-topic investigations, review dates,
sent/acknowledged/received/delivered evidence, and regional route corrections.
Do not reconstruct customer status from Gmail threads or keep duplicate live
status here. The previous TODO contents were preserved in the private source
archive. Already answered questions were removed from the active queue.

## Record-help inquiry form

### Capture the customer's intended outcome

**Priority:** P2
**Status:** Planned for later — user approved recording this task on 2026-09-12, not implementation or deployment.

- When revisiting "what should we do next?" / "뭐 해야 하지?", include this open product improvement.
- Why: a recent inquiry supplied an address and missing-record status but no intended use. Knowing the customer's goal helps distinguish finding a permit from actually resolving their question and reduces follow-up friction.
- Proposed question: **What are you trying to find out?** (이 기록으로 무엇을 확인하고 싶으세요?)
- Options: tank/drainfield location or layout; existing permits/approvals; buying or selling a home; planning an addition or other work; not sure / other.
- Inspect the existing form first. Prefer replacing or clarifying the current concern field instead of adding a redundant question. The process-stage field alone does not establish the intended outcome.
- Keep free text optional and allow "not sure". Do not require customers to know technical document names or imply records alone establish construction feasibility or current condition.
- On implementation, carry the selection into the operator notification email, preserve existing submissions/context, and verify mobile usability and form validation. Do not send free-text concerns, addresses, or other personal details to analytics.
- Done when the operator can identify the requested outcome from the inquiry email without an obligatory extra clarification exchange, while uncertain customers can still submit.

## Twilio US business number

### Follow up on compliance verification ticket

**Priority:** P1
**Status:** Waiting on Twilio Support — ticket submitted 2026-09-13.

- Check [Twilio ticket #29531839](https://help.twilio.com/tickets/29531839) and the account email for a human response.
- Do not repeat Persona/KCB verification while the ticket is unanswered. The official re-launch flow still ended with `Couldn't verify photos`, and the primary compliance profile remains `Draft` with blank identity fields.
- Confirm whether Persona's earlier manual review is pending and linked to primary profile `BUacb8639170f20f7430f0a50f4719619b`.
- Obtain Twilio's confirmed South Korea identity-document requirements or an alternative verification route for a user without a passport.
- After approval: verify the Trust Hub profile is no longer `Draft`, purchase one standard US local number, confirm auto-recharge remains off, and test the number in the Prince George's County Momentum portal.
- Canonical status and evidence remain in `storage/operations/ledger.json` under task `us-business-number-setup`; update that task whenever the ticket status changes.

## Completed

