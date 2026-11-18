# Search and intake recovery — November 18, 2026

The TDEC entry points used the old permit-data viewer while the official SSDS
program linked to `https://tdec.tn.gov/document-viewer/search/stp`. The program
page and its outbound link were checked in Chrome on November 18. The new
document search returned 403 in that browser; this release does not claim that
access was restored or that an empty search means no property record exists.

Changes:

- Use the current official document search across the TDEC hero, route builder,
  county access profiles and related content. Retain the county/field-office
  fallback and the distinction between an access failure and a no-record result.
- Put existing-file search steps near the top of the TDEC page. Give Tennessee
  its own county/contract-county explanation and a direct county-selector link.
- Surface the county-specific search guide immediately after the county hero.
  Add source-backed guides for San Diego and Atlantic; do not invent archive
  coverage dates. The curated guide count changes from 16 to 18.
- Redirect both slash variants of the observed legacy Cabarrus URL directly to
  its existing North Carolina county page with HTTP 301.
- Show phone format guidance and feedback before submission, retaining required
  phone, requester role and consent.
- Batch native invalid events at the next task. A browser regression reproduced
  seven validation events for one empty-form submission; the fixed version
  records one batch and one bounded event per invalid field type.

The affected submit-attempt and validation events now use `measurement_version:
intake_v3`. Historical validation event counts are not directly comparable to
the new counts, and neither represents a cohort conversion rate. No entered
contact details, addresses or file names are added to event parameters.

Official sources reviewed:

- [TDEC SSDS program](https://www.tn.gov/environment/permits/water/septic-systems-permits.html)
- [San Diego document library](https://www.sandiegocounty.gov/content/sdc/deh/doclibrary.html)
- [Atlantic Environmental Health](https://www.atlanticcountynj.gov/government/county-departments/department-of-human-services/division-of-public-health/environmental-health)
- [Atlantic records requests](https://www.atlanticcountynj.gov/government/county-departments/department-of-law/open-public-records-act-opra)

Validation:

- `gradlew test bootJar`: 1,123 tests, zero failures/errors, 18 skipped.
- Selenium checks cover one-event batching, malformed and formatted phone input,
  correction feedback, mocked successful intake, document mode and context.
- Chrome desktop and 390px mobile checks: TDEC link, Tennessee county selector,
  San Diego instructions, phone correction and native required-field validation.
  No production inquiry or agency request was submitted.

The release preserves existing canonical/indexing policy and LeadSmart ownership
tokens. Improved content, accurate sitemap dates and a redirect are deployable
changes; Google recrawling, indexing and traffic recovery remain separate
outcomes. Recheck the previously selected pages after they are crawled again.
