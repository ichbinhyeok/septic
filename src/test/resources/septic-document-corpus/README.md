# Official septic document validation corpus

This corpus contains blank forms and guidance documents published by U.S. state or county
government agencies. It is used to test document-layout resilience and to ensure that labels,
checkbox options, examples, and instructions are not mistaken for property-specific facts.

- No completed property records or personal information are included.
- `corpus.csv` records the official publisher and source URL for every file.
- PDFs are downloaded by `scripts/download-septic-document-corpus.ps1`.
- `blocked-sources.csv` preserves useful official candidates that could be reviewed in
  browser research but could not be downloaded by the reproducible script.
- A blank official form must not produce a positive property decision.
- Value-extraction recall is tested separately with de-identified, completed fixtures whose
  vocabulary and field order are based on these forms.

The source agencies remain the authoritative publishers. Refresh the files from their original
URLs before a future benchmark if a source changes.
