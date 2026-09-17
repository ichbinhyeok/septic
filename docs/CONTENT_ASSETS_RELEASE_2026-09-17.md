# Case-derived content implementation — 2026-09-17

Status: implemented and verified locally; not deployed by this task.

## Follow-up: two representative real stories

Expanded the historical-layout guide at `#layout-case` and replaced the generic workflow narrative on the Record Brief example with the actual sale-preparation investigation at `#sale-case`. Both now explain the customer question, search obstacle, additional work, source-supported substantive answer, delivery and unresolved limits. The sale case includes the failed old contact, corrected request, returned design facts and the distinction between no repair permits found and no repairs ever made.

The two cases are explicitly separate. Existing public images are reused; no new customer originals were added. Removed unsupported display counts and synthetic case labels from the example trace. Public answers remain freely readable, while the separate personal preview remains illustrative. Added plan enlargement and allowlisted story-exposure events. Increased case body type and reduced headline dominance for sustained reading, following the frontend skill's editorial hierarchy.

Private evidence mapping remains under ignored operational plans. No inbox refresh, operational status change, customer contact or deployment was performed. Follow-up full suite: 920 tests total, 902 passed, 18 skipped, no failures; production effect remains unmeasured.

## What changed

- Expanded eight existing guides: as-built drawings, tank location, online no-result searches, address matching, permit stages, transfer files, buying a septic property, and agency requests.
- Added a step-by-step reading of the existing public Tennessee sketch, page comparison, five-question checklist, and accessible image enlargement.
- Added pool/addition/pumping scenarios, archive search explanations, identity checks, inspection-versus-approval distinctions, and buyer/seller document checklists.
- Expanded the completed Record Brief page with a clearly labeled illustrative personal preview and optional US $29 package comparison.
- Improved Morgan County AL, St. Croix County WI, and King County WA guidance against official sources. Added related educational links to these and existing Tennessee proof pages.
- Reused existing URLs and server-rendered content. Updated sitemap revision dates only for materially updated pages; did not change the TDEC title experiment.
- Applied the frontend skill's editorial approach: readable sections, source-image context, restrained dividers, mobile stacking, and direct next steps rather than a new visual identity.

## Commercial and evidence boundaries

Research, document review, and necessary agency requests remain free. New v2 intakes receive source/scope/property-match/question-coverage/limits in the free preview; substantive property answers and located originals follow an optional US $29 unlock. Approved agency charges remain extra at cost. No automatic charge, additional scope cap, or complexity surcharge was introduced. Email remains the manual payment/delivery workflow; no payment processor was added.

Existing free requests and earlier free-answer promises retain their accepted terms. Customer-owned uploaded files are never withheld from their owner. See RECORD_HELP_UNLOCK.md.

No new private source documents, customer identities, testimonials, success rates, or response-time claims were published. Existing public redacted material is reused; educational hypotheticals are labeled illustrative rather than represented as completed customer cases. The private case-to-content plan remains in ignored operational storage. Record drawings do not certify present condition, exact field location, or construction approval.

Official references checked during preparation include EPA septic-system explanations, Alabama public health/Morgan contacts, St. Croix's sanitary program, and King County OSS records and building-application guidance. Two King source-registry entries were added. One returned case is not treated as a county-wide rule.

## Measurement

The new record-reading.js sends allowlisted public content IDs and pathname-only source context through the existing artifact-action endpoint and a GA event when gtag is available. It does not send customer case IDs, property details, or URL query strings in its payload. Existing endpoint provenance/privacy handling remains in place.

- `read`: heading at least 50% in view, once per content ID per page load. This means section exposure, **not completed reading**.
- `enlarge`: opening the historical-sketch dialog, once per page load.
- CTA tracking reuses existing navigation/source-context hooks.

Live traffic, conversion, payment, and SEO improvements have not been established by local tests. Before deployment, capture a comparable 28-complete-day baseline from available analytics and save any actual analysis as an immutable growth_signals snapshot per OPERATIONS.md. After deployment, inspect behavior after 14 complete days and directional search/conversion results after 28 and 56 days; retain raw counts for small samples.

## Verification

- `gradlew.bat test bootJar`: successful, 919 tests total; 901 passed, 18 skipped, zero failures/errors.
- All nine content/example routes and seven affected county routes returned HTTP 200 locally.
- All same-page fragment links on the nine content/example routes resolved to rendered IDs.
- Browser review covered desktop drawing layout, mobile location guide, image modal open/close, and preview comparison. Local artifact logs confirmed exposure and enlargement events.
- `git diff --check`: no whitespace errors (existing Windows line-ending warnings remain).
- No customer submission, email, agency request, payment, deployment, or private-ledger publication occurred.

## Remaining release steps

Remaining-case visual follow-up: expanded the original component into five source-bound editorial variants and placed each new visual beside its detailed public story. Roane compares the three recorded field-line lengths and 225-foot total; Shelby shows the failed-contact-to-returned-file route; St. Croix shows 26 archive pages reviewed, a three-page focused extract, and the preserved 1,200/1,255-gallon discrepancy; Overton shows the historical clue chain with a prominent limited-match state. Each variant identifies itself as a SepticPath-created summary rather than an original record or physical layout, exposes its research steps in native details, and carries case-specific limits. Existing masked drawings, excerpts, evidence components and image bytes remain unchanged. Desktop and 390px mobile browser review covered all four new variants with no horizontal overflow or console errors. Targeted case/policy tests and bootJar passed; deployment remains pending.

Scope clarification: the new research comparison is **additional editorial content**, not a replacement for the existing masked drawings or conversion-proof sections on other pages. Preserve those existing assets, their links, and evidence components. Compact follow-up changes only the new visual's title, spacing and explicit document labels (Permit to Construct / Final approval); no existing evidence image is replaced or removed. Any image-rights review remains a separate question, not authorization to remove existing proof.

Original visual pilot: added `recordFindingVisual.jte` to the case collection, presenting Anderson's 300 ft planned versus 200 ft final recorded trench total, segmented 40/80/80, using original HTML/CSS rather than a copied or traced plan. Source-stage labels, factual tank/flow values, a native details explanation and explicit not-a-site-plan caption are server rendered. Added exposure measurement (locally verified), reduced-motion-aware animation and regression coverage. Targeted case/policy suites (7 tests) and bootJar passed; desktop and 390px mobile screenshots reviewed, no horizontal overflow, native details interaction verified. Fixed inherited dark label colors found during screenshot review. This is one visual pilot, not a replacement of all existing source images or a deployment.

Research-publication policy follow-up: kept the existing research consent and added no publication checkbox. Replaced the blanket form privacy sentence with contact/correspondence protection and a direct link to the new privacy section. The section distinguishes public-record research explanations from private requests, disclaims blanket document/testimonial permission, preserves earlier commitments and separates image rights from redaction. Removed the listing-agent anecdote, specific customer sale/addition purpose and Anderson feedback claim from affected public narratives. Added `RESEARCH_PUBLICATION_STANDARD.md` and policy regression coverage. Existing source images are unchanged and not copyright-cleared by this policy change; image replacement/withdrawal and deployment remain separate pending work.

Visual/privacy follow-up: changed Anderson collection copy from explaining why the installation length changed to comparing the recorded design and final lengths. Added allowlisted `real-cases` reading exposure and verified a local `artifact_action` receipt (exposure is not proof of completed reading). Moved the related-case link outside the guide CTA flex row. Browser checks at 1440px desktop, 390px mobile and 768px guide/CTA showed no horizontal document overflow; screenshots were visually reviewed. Four regression tests and bootJar passed. Visually reviewed all 12 existing public case-study PNGs: customer identifiers are cropped or covered; some full agency pages retain agency officials' names/signatures and historical dates, so do not claim removal of every name/signature or irreversible anonymization. No PNG text/EXIF metadata chunks were found. The five images used by the example page exclude those agency signatures. No source image bytes were changed, no new private source was published, and no deployment occurred.

Case-led presentation follow-up: added a five-case, server-rendered collection at `/septic-record-brief-example/#real-cases`, linked from home, global navigation/footer and learning guides. Cards lead with the customer's question and the useful work delivered; document-specific qualifications remain in the detailed narratives, with Overton's unconfirmed match also preserved in its card. Public findings remain free to read, with a separate optional own-property package CTA. Reused existing editorial styles. The four-test RecordBriefExampleRegressionTest suite and bootJar passed; git diff --check passed. No deployment or fresh visual-browser verification was performed in this follow-up.

Exhaustive case audit follow-up: reconciled all 22 local ledger cases to editorial dispositions in ignored operational plans (2 existing deep stories, 3 expanded from existing county proof, 3 requiring further publication review, 14 held as individual narratives). Added Overton address-match, Anderson planned/final trench-length and St. Croix addition-record narratives to existing guides with home discovery links. Added request-status/payment and deadline explanations without presenting pending cases as solved. Corrected St. Croix's capacity attribution: 1,200 on inspection/application versus 1,255 on the revised plan. No new private original was published. Full tests/bootJar passed before the new discovery regression was added; the targeted three-test RecordBriefExampleRegressionTest suite then passed. Four affected guide routes returned 200 with valid same-page anchors; the new Anderson mobile narrative had no horizontal overflow. Deployment remains pending.

Public-discovery preparation: added an SSR home section (`#read-record-stories`) linking both actual investigations and four practical guides, plus a global header/footer entry. Live robots currently allows the public site and advertises both sitemaps; the existing drawing, example and no-result URLs are already in the live main sitemap. This does not mean the new copy is deployed or recrawled. The land-and-deploy preflight stopped because `codex/customer-value-unlock` has no PR. No merge/push/deploy was performed in that preflight. A release PR with only reviewed in-scope changes is still required; unrelated operational changes remain outside the content release.

Deploy when authorized, then check production routes, canonical/indexing metadata, analytics receipt and the real intake/email journey without creating unsolicited customer actions. Measurement follow-up is a release checklist, not a scheduled automation.
