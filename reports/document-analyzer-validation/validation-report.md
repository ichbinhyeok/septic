# Septic document analyzer validation

Validation date: 2026-07-27

## Outcome

The analyzer was tested against 26 downloaded government PDFs and 16 de-identified,
official-form-style completed fixtures.

| Measure | Result |
|---|---:|
| Downloaded official PDFs | 26 |
| Official PDF pages | 74 |
| Source states | 5 |
| Government publishers | 6 |
| Document types | 24 |
| Searchable-text PDFs | 26 / 26 |
| Blank-form false positives before hardening | 25 / 26 documents |
| Blank-form false positives after hardening | 0 / 26 documents |
| Completed style fixtures | 16 |
| Expected completed-fixture fields | 71 |
| Correct completed-fixture fields | 71 |
| Unexpected completed-fixture fields | 0 |
| Generated typed-scan OCR samples | 40 |
| OCR exact field recall | 169 / 176 (96.0%) |
| OCR unexpected fields | 0 |

## Corpus design

The downloaded corpus uses blank forms and guidance from Oregon DEQ, North Carolina
DPH, Minnesota local authorities, Kit Carson County Public Health, and King County
Public Health. It covers permit applications, site evaluations, construction and repair
guides, existing-system evaluations, as-built and record drawings, performance reports,
bedroom restrictions, final-inspection requests, and records requests.

Completed property records were not downloaded because public records can contain owner,
address, parcel, signature, and contact information. Recall testing instead uses
de-identified completed fixtures whose labels, ordering, and terminology are based on the
official forms.

The source manifest is in
`src/test/resources/septic-document-corpus/corpus.csv`. Candidates that were useful in
browser research but blocked automated download are preserved separately in
`blocked-sources.csv`.

## Failure discovered

The original extractor treated word presence as evidence. On blank official forms it
mistook:

- words following “permit application” for permit numbers;
- threshold examples such as “> 3,000 GPD” for property design flow;
- unselected system-type options for the installed system;
- instructions mentioning an as-built, repair, or reserve area for proof those records
  existed.

This was an overconfidence failure, not merely a formatting issue.

## Hardening applied

- Permit numbers now require an explicit `number`, `no.`, `#`, or `ID` label and reject
  administrative stop words.
- Bedroom values require approval/design language or a labeled field with a numeric value.
- Design flow requires a labeled flow or operational-capacity value.
- Tank capacity requires tank context on the same value expression.
- System type requires a populated field or property-specific narrative and rejects option
  lists.
- Final approval requires a parsed approval/inspection date.
- Layout, repair history, and reserve-area findings require affirmative property-specific
  phrases rather than raw keyword presence.
- Searchable PDFs preserve per-page text so each extracted fact can name its source page
  and show the exact supporting excerpt. Short values are not used alone to choose a page.

## Automated gates

`OfficialDocumentCorpusTests` enforces two complementary properties:

1. Every blank official PDF must produce zero property-specific findings.
2. Every completed official-style fixture must produce exactly the expected key/value set,
   with no extra fields.

The corpus download can be refreshed with:

```powershell
.\scripts\download-septic-document-corpus.ps1
```

## Typed-scan OCR validation

The follow-up benchmark uses 10 de-identified completed-document fixtures rendered in
four scan conditions: clean, low contrast, 1.1-degree skew, and faint low resolution.
This produces 40 image samples and 176 expected property fields. Tesseract WebAssembly
performed the repeatable local recognition pass; its text was then replayed through the
same production field extractor.

| Scan condition | Exact field recall | Unexpected fields |
|---|---:|---:|
| Clean | 43 / 44 (97.7%) | 0 |
| Low contrast | 43 / 44 (97.7%) | 0 |
| Skewed | 43 / 44 (97.7%) | 0 |
| Faint, low resolution | 40 / 44 (90.9%) | 0 |
| **Overall** | **169 / 176 (96.0%)** | **0** |

All 40 samples produced at least one correct property field. The seven errors were
character substitutions rather than invented field categories:

- permit identifiers such as `AOWE-24-7811` becoming `AOWE-24-7841`;
- letter/digit confusion such as `OSS-88219` becoming `0SS-88219`;
- a flow value of `900 GPD` becoming `800 GPD`.

That failure mode is materially important. OCR findings are therefore always labeled
low confidence, an OCR-only file can never receive a `supported` decision, and the first
next step requires comparison of every extracted number with the original scan.

The server path renders PDF pages in memory and pipes PNG bytes directly to Tesseract
through standard input. It does not create a temporary copy of the uploaded document.
The implementation limits OCR to eight pages, 200 DPI, eight million rendered pixels per
page, two concurrent jobs, and twelve seconds per page. Tests verify the in-memory
process pipe, missing-engine failure, oversized-page rejection, OCR confidence
downgrade, and the searchable-PDF bypass.

Refresh the OCR benchmark with:

```powershell
.\scripts\validate-septic-ocr.ps1
```

Government example sources that informed layouts, values, and scan characteristics are
listed in `ocr-source-notes.csv`. Individual completed property records were not retained.

## Remaining limitations

- Typed image scans are now supported when Tesseract is available on the server, but
  handwriting, faint stamps, drawing geometry, and crossed-out values remain manual-review
  cases.
- The repeatable recognition benchmark used Tesseract WebAssembly while production uses
  the Alpine Tesseract package. Both use Tesseract, but the exact production container
  still needs a post-build canary with these fixtures.
- The completed fixtures are realistic but synthetic and do not replace consented,
  manually labeled records from actual users.
- The corpus covers five states, not every state-specific vocabulary.
- A production claim should wait for consented, de-identified completed records from real
  users and manual ground-truth labeling.
