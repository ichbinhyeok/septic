# Changelog

## 0.0.2.3 - 2026-07-30

- The private operations report now compares anonymous county workflows across preparation, official-route opening, reported return, document-workspace handoff, document review, and usable property-file completion.
- County conversion rates use only workflows that reached the prior step, while 28-day cohorts with fewer than five starts are explicitly marked as too early for route changes.
- Each observed county receives a traffic-ordered diagnostic next action so product work starts with the busiest unresolved step instead of page impressions.
- The document-workspace handoff stage is now accepted by the server and included in both the overall and county completion funnels.

## 0.0.2.2 - 2026-07-30

- Demand-backed county workflows now hand the same property, county, purpose, and workflow identity directly into the document workspace after an official-route return.
- Users who report a downloaded county document no longer need to repeat the address lookup before uploading and reviewing the file.
- Regression coverage verifies the prepare, official handoff, return outcome, request tracking, and document-review entry points across all 35 researched priority county routes.

## 0.0.2.1 - 2026-07-30

- Alabama estimates now separate state-adjusted private project ranges from published county application and eligible public site-evaluation fees.
- The Alabama guide prepares county-specific handoffs for nine Search Console-observed locations, including verified office contacts, official forms, required inputs, and exact confirmation questions.
- Jefferson County now uses its own JCDH permit workflow and application instead of inheriting the generic statewide form path.
- Alabama users can select a county and generate a copy-ready request containing the verified office, form, and unresolved fee or submission questions.

## 0.0.2.0 - 2026-07-28

- The homepage now explains the product through a real property-file outcome and common homeowner questions.
- Address lookup results now name the exact file-owning office, show contact and review details, and preserve context while official portals open in a new tab.
- Saved property-file sessions can be downloaded and resumed without storing original PDFs or scans.
- Extracted facts now show source filenames, PDF page numbers, and evidence excerpts, with safer page matching for short values.
- Calculator handoffs preserve confirmed record context and distinguish priced inputs, reference-only facts, unknowns, and appropriate uses of the estimate.
- County pages now lead with plain-language actions while keeping technical confidence and editorial metadata available on demand.

## 0.0.1.0 - 2026-07-27

- Homeowners can start with the record they need and follow plain-language next steps from the homepage.
- Documents found on official sites can now be analyzed on SepticPath, including searchable PDFs and typed scans.
- Extracted facts now lead to purpose-specific decisions for verifying a system, estimating work, or preparing a records request without re-entering details.
- Calculator results now explain their assumptions, confidence, and practical limits more clearly.
- Document analysis is backed by an official-form corpus, degraded-scan benchmarks, and production OCR support.
- Production PDF uploads now pass through the web proxy up to the advertised 10 MB file limit.
