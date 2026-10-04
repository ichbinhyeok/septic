# Search demand → useful results → evidenced commercial outcomes

Implemented locally October 4–5, 2026. This document does not confirm deployment, provider coverage, outreach, a sale or conversion lift.

## Priorities and source boundary

The product/business analysis used Google Search Console for `sc-domain:septicpath.com`: final web data, September 4–October 1, 2026 inclusive, Pacific calendar dates, all devices/countries. Separate page and page/query queries returned 360 and 309 rows, respectively, below their 1,000-row limits. GSC can omit anonymized queries and other rows; query totals are not treated as page totals.

| Existing page | Impressions | Clicks | Decision |
| --- | ---: | ---: | --- |
| TDEC records | 5,881 | 154 | Match the production hero/title to permit-search intent; make free assistance visible. |
| NC records hub | 1,177 | 29 | Preserve county/address routing; add a direct free-help route and clearer search copy. |
| TN records hub | 981 | 18 | Preserve county/address routing; add the same state-aware free-help route. |
| Alabama cost page | 1,800 | 25 | Inspected, not changed: production already addresses perc-test intent. |

This is prioritization evidence, not evidence that low CTR is caused by copy or that visitors will become paid leads. No new indexable pages, public case details, or claims of available contractors were added. Existing public record-evidence images remain unchanged. Only the three materially changed URLs received updated sitemap modification dates.

The exact API requests/results, executed notebook and immutable growth transaction are private under `storage/operations/analysis/2026-10-04-sunday-*`. The snapshots were applied in ledger revision 617. The inbox checkpoint was not advanced and no inbox-freshness claim is made.

## Purpose-specific free results

Authenticated `POST /ops/record-results` and `/ops/record-results/json` accept optional `helpPurpose`:

`location`, `inspection`, `pumping`, `repair`, `building`, `records`, `unsure`.

Copy the customer's actual submitted purpose from the reviewed intake. Do not infer a repair need from a missing record. Omitted/blank purpose uses generic guidance; historical offers without this field remain compatible. Unsupported values are rejected before package creation.

The private result page supplies three practical preparation steps for that purpose. Records-only results do not solicit a service visit. Other purposes link to the existing service form, with the project type preselected when appropriate. No private token, address or contact detail is included in that link. This is not automatic matching or a booking.

All existing attachment-delivery gates, approval/hash checks, private-link expiry, download limits and mail authorization requirements remain in force. Result creation returns `emailSent: false`; creation is not delivery. Historical packages are not migrated or automatically reissued.

## Private commercial briefs

New record and service intakes save an internal `commercialBrief` and an unreviewed `commercialFollowup`. Supplied phone numbers, customer-reported record status, agency conclusions, confirmed service intent and buyer acceptance remain distinct facts. The intake summary does not verify any of them or attach files.

Use the current private ledger and read the relevant case before preparing case material. To prepare local review artifacts:

```powershell
node tools/commercial-brief.mjs case CASE_ID --purpose location
node tools/commercial-brief.mjs intake storage/PATH/TO/ACTUAL_INTAKE.json
```

These commands create a new ignored directory under `storage/operations/` containing:

- `internal.md`: private contact details and saved research context for operator review.
- `anonymous.md`: a format example limited to state, inquiry class and purpose. County, dates, identity, contacts, original files and free-text findings are omitted. It is not a live offer.
- `brief.json`: structured private summary.

`--purpose` is an operator classification for ledger cases only. Intake briefs preserve the submitted selection. Historical cases do not automatically gain current interest or new sharing terms. Read the actual accepted terms and buyer's service area before any sharing. A file-based external summary still needs the mandatory delivery gate. The tool sends nothing.

## Record real acceptance and money

After an authorized action actually occurs, register its evidence using the operations workflow. Prepare a private details JSON with a unique `id`, `provider_id`, `kind`, actual `occurred_at`, existing evidence `source_ids`, and optional `note`.

Supported kinds: `offered`, `accepted`, `declined`, `contact_reached`, `paid`, `refunded`. Only received payments/refunds have a positive integer `amount_cents`; currency is USD. An invoice, quoted price or acceptance alone is not payment.

```powershell
node tools/commercial-brief.mjs prepare-outcome CASE_ID storage/operations/ACTUAL_DETAILS.json
# Review the generated change.json against the actual evidence first.
node tools/operations.mjs apply storage/operations/commercial-outcome-XXXX/change.json
node tools/operations.mjs validate
```

Preparation does not modify the ledger. The transaction uses the current revision, adds an evidence-linked commercial event and leaves research status intact. Duplicate events, future timestamps, nonexistent source IDs, fractional/negative amounts and refunds exceeding that provider's receipts are rejected. Net received revenue subtracts refunds; it is not profit or a completed-job count.

For the first buyer pilot, agree on geography, inquiry definition, price, duplicates/refunds and delivery method. Compare offered → accepted → paid using actual events, and record research/handling cost separately. There is no automatic provider dispatch, invoicing or collection.

## Local verification

- Full Java suite and production build: 1,050 tests, 1,032 passed, 18 pre-existing skips, zero failures/errors; 127 suites.
- Ten commercial CLI tests pass, including anonymous-output leakage, consent versions, evidence validation, net receipts/refunds, no ledger mutation during preparation and compatibility with the real operations transaction validator. All 26 existing operations tests also pass.
- Seven result purposes and legacy offers without a purpose are covered; protected creation still reports no email sent.
- Browser QA covered TDEC, NC and TN entry pages at desktop/390px mobile size, state-aware handoffs to the existing intake, synthetic pumping/records-only private results, and the pumping service selection. No horizontal overflow was observed. The result-page secondary button contrast was corrected during this check; English expiry formatting is now independent of the server locale.
- The executed notebook has three successfully executed code cells and no errors; its rendered presentation contains the four-page comparison and four query tables. It was opened and inspected locally.
- `operations.mjs validate` confirms revision 617. `git diff --check` passes. Original customer files, historical delivery objects and unrelated pre-existing operations edits were preserved.
- A historical NC case was rendered into an ignored private review brief and an anonymous format example. Its closed status/next action were preserved; it is not listed as a currently available lead and was not sent anywhere.
- QA used fictional data with mail disabled and isolated storage. No customer email, provider contact, live submission, production deployment or case-file release was performed.
