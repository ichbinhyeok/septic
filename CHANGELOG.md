# Changelog

## 0.0.9.1 - 2026-09-06

### Fixed

- Replaced retired Missouri DHSS and Clackamas County septic URLs with their current official program and permit-guidance pages, and routed Stark County through its stable official Auditor entry page.
- Updated the published Clackamas County record route and added a regression check that prevents the retired 404 paths from returning.

## 0.0.9.0 - 2026-09-06

### Changed

- Made the process-stage qualifier optional so visitors can request record-path help without presenting themselves as an active buyer, seller, or agent.
- Added a concrete pre-submit preview of the reply: likely records office, exact document to request, official request link, and no-record fallback.
- Set the expectation that manually reviewed replies are normally sent within 1–2 business days and clarified that requests are not added to a newsletter.
- Updated stored request language metadata and Gmail subjects so an omitted process stage is not mislabeled as a transaction.

### Fixed

- Closing the process-stage selection now also closes transaction-only details while still rejecting unknown submitted stage values.
- Refreshed the Massachusetts and North Carolina official-source reviews required by the publication-health gate, including the current Massachusetts Title 5 regulation URL.

## 0.0.8.0 - 2026-09-05

### Changed

- Reframed the zero-click Closing Risk Check experiment as task-adjacent septic record help: visitors now ask for the likely office and exact file before being qualified for a deeper transaction review.
- Reduced the initial request to email, property address, state, search problem, and process stage; name, deadline, county, listing, bedroom, and concern details are optional.
- Contextual records CTAs now speak to a failed or uncertain record search, while buyer, seller, and agent selections reveal transaction details for follow-up qualification.
- Gmail notifications and stored requests now distinguish record-help demand from an active-transaction opportunity and preserve the originating CTA context.

### Added

- Added a clean GA4 funnel for `record_help_cta_viewed`, `record_help_cta_clicked`, `record_help_form_viewed`, `record_help_form_started`, `record_help_stage_selected`, validation errors, and successful requests.
- Added `request_type`, `source_context`, `cta_variant`, and anonymous transaction-intent parameters so the revised offer can be judged without property or contact data in analytics.

## 0.0.7.0 - 2026-09-02

### Added

- Closing Risk Check CTAs now report anonymous GA4 funnel stages for impressions, clicks, form views, form starts, validation errors, and successful submissions, with source attribution preserved across navigation.

### Changed

- Transaction-intent state, county, TDEC, and national record pages now introduce the free human review immediately after the relevant records workflow with explicit deadline-based copy.
- The Closing Risk Check now leads with the manual-review offer, moves the self-serve builder into a clearly labeled optional disclosure, and groups nonessential request details behind an optional section.
- County is no longer required when a complete property address and state are supplied, reducing request friction without removing useful file context.

### Fixed

- Global navigation names the Closing Risk Check directly, and regression tests verify contextual CTA source markers instead of passing on unrelated header or footer links.

## 0.0.6.0 - 2026-09-01

### Changed

- State guides and state-level buying, permit, and cost pages now enter the XML sitemap only when their Search Console demand cohort supports indexing; useful lower-demand pages remain accessible with `noindex,follow`.
- Record-research pages now hand transaction-intent visitors directly to the free Closing Risk Check, and calculator pages expose the related tank-size and pumping estimators.
- Alabama perc-test and Tennessee records snippets now align more tightly with observed search intent.

### Fixed

- Repaired obsolete internal routes, linked previously orphaned record and slow-drain guides, and removed deep or weak sitemap paths from the indexable crawl graph.
- Regression coverage now verifies sitemap and robots-policy alignment across every published state page, along with the new contextual links and canonical routes.

## 0.0.5.0 - 2026-08-31

### Added

- The offer-prep workflow now accepts qualified free Closing Risk Check requests for active buyer, seller, and agent transactions with a real due-diligence or closing deadline.
- Each request is stored with its consent snapshot and delivered to the SepticPath operator through Gmail, with the requester set as the reply-to address.
- Submission protection includes server-side validation, a hidden bot field, per-client hourly rate limits, and bounded in-memory abuse tracking.

### Changed

- The offer-prep page now leads with under-contract closing risk, explains the one-page readiness brief, and separates the browser-only self-serve tool from the address-storing manual beta.
- Privacy guidance now explicitly covers stored property details, operator email delivery, prohibited sensitive data, and anonymous analytics fields that exclude addresses and contact details.
- Production deployment now receives Gmail credentials only through GitHub Actions secrets.

## 0.0.4.0 - 2026-08-31

- The records access index now renders static links to every reviewed county route, keeping all 325 county guides discoverable without JavaScript or search interaction.
- Parameterized calculator and quote links now use `nofollow`, and the records CSV link no longer creates a date-stamped crawl variant.
- Regression coverage protects the complete county crawl directory, keeps it off the homepage, and verifies crawl hints on state pages.

## 0.0.3.0 - 2026-08-17

- The Tennessee records page now leads with the official TDEC SSDS search and keeps county or field-office routing as the fallback instead of presenting SepticPath as the record database.
- The county router requires only a county; optional address verification runs in the background with a four-second client timeout and never blocks the official route.
- Search title, description, H1, primary actions, responsive layout, and GA4 route events now describe the same direct-lookup workflow.
- Editorial labels now identify product maintenance and official-source checks without implying named professional reviewers.
- Regression coverage protects the statewide viewer, nine locally administered counties, optional property keys, asynchronous verification, and the revised search snippet.

## 0.0.2.9 - 2026-08-11

- Sitemap modification dates now include the latest material shared-workflow revision, so Google can distinguish recently rebuilt state and county pages from their older crawled versions.
- The downloadable records-access CSV now sends `X-Robots-Tag: noindex, follow`, keeping the canonical HTML dataset page indexable without treating the download artifact as a search landing page.
- Regression coverage protects both crawl signals and the updated sitemap dates.

## 0.0.2.8 - 2026-08-11

- Search Console-backed titles and descriptions now match Tennessee permit-search, Alabama perc-cost, Georgia permit-record, Alaska cost-and-record, Arkansas perc-test, and Forsyth County lookup intent.
- The North Carolina permit workspace now links the current high-demand county routes directly, while keeping the first six visible and the remaining routes in a compact expandable list.
- Georgia state guidance now prioritizes Forsyth County and other demand-backed county record routes before the alphabetical directory.
- Regression coverage protects all sixteen North Carolina acquisition routes, the revised snippets, and the page-to-county handoff.

## 0.0.2.7 - 2026-07-31

- Thurston County now offers an in-product parcel search against the county's public Laserfiche archive before the record-drawing fallback.
- Search results stay labeled as official document candidates until the user confirms the parcel and septic file type in the county record itself.
- The lookup keeps parcel data in the request only, limits result display, and records anonymous archive-query outcomes for the county workflow funnel.

## 0.0.2.6 - 2026-07-31

- Submitted county requests now offer a privacy-safe seven-day calendar reminder and return link so users can record the county's later response.
- Reminder files and URLs exclude property addresses, parcel IDs, and county request numbers.
- Returning from a reminder opens the saved county outcome workspace and records follow-up scheduling, link-copy, and resume events.

## 0.0.2.5 - 2026-07-31

- Tarrant County now opens the JustFOIA public-information portal as the primary county-owned OSSF request route after the jurisdiction check.
- A blocked portal or unresolved authority now falls back to the Tarrant County OSSF office instead of sending users to an informational page under a submission heading.
- Regression coverage protects both official destinations and their primary/fallback roles.

## 0.0.2.4 - 2026-07-30

- Search Console-backed titles and descriptions now match the highest-impression Tennessee and Alabama queries while making the TDEC 403 fallback explicit before the click.
- West Virginia perc-test content now answers cost intent with a bounded national planning range before explaining local-health, site-review, permit-stage, and quote-scope variables.
- North Carolina's proven state records page now gives additional internal prominence to emerging Forsyth, Pitt, Pender, and Onslow County searches.
- SEO regression tests protect the revised search snippets, headings, cost caveats, and official-workflow handoffs.

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
