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

### Capture the customer's intended outcome

**Completed:** v0.0.22.0 (2026-10-04)

- Required plain-language purpose includes location, inspection/buying/selling, pumping, repair, building, record copies and "not sure".
- Free text and timing remain optional. The selection is stored with the inquiry and included in the operator notification; historical submissions are unchanged.
- Mobile layout, minimal submission, validation and saved context are covered by `FreeRecordPivotTest`, `FreeIntakeStorageTest` and `ClosingRiskNotificationServiceTest`.
- Implementation and commercial limitations: [FREE_RECORDS_PIVOT.md](docs/FREE_RECORDS_PIVOT.md).

