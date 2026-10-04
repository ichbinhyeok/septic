# Free records and local-service inquiry funnel

Implementation: 2026-10-04. This document describes the code change, not a production deployment or proof of paid provider demand.

## Customer experience

- Keep existing SEO URLs, self-service tools, case studies, and public masked evidence.
- Research, agency requests, located source files, and our explanation are free. Agency fees need prior approval; field services are priced separately.
- Assisted intake collects a required phone number, role, and purpose. Timing and a written explanation are optional. Phone format validation is not identity/contact verification.
- The calculator links to the production intake, not a disabled design-preview route. Its results offer service follow-up immediately. The live service form posts to `/quote-request/`; the previous calculator/quote action had no production POST handler.
- Active-problem guides lead to service inquiries without waiting for agency records. Diagnosis and tank-location inquiries do not generate an invented replacement estimate.
- Request submission is not a booking, verified work order, guaranteed response, or completed introduction. New matching terms are saved with new requests only.

## Lower-friction intake follow-up

- Keep one existing record-help destination. Do not add a provider-referral form, partner signup, or channel-choice step: incoming provider referrals are a different, unvalidated acquisition experiment from selling existing inquiries.
- Mobile intake retains the original image but reduces the introductory scene from 340px to 112px. The six required record-help fields and explicit consent stay visible; timing and long-form context move into the optional disclosure. File review still requires source files.
- Location, buyer and failed-search guides use contextual links to that same form. County CTAs carry editable state/county values even without JavaScript. Entry copy comes from an allowlist, not arbitrary query text; source paths exclude queries and external URLs. No customer address or contact detail is put in these links.
- Preserve explicit source-page attribution through submission; do not overwrite it with stale browser attribution. Skip saved-property carryover when its state/county conflicts with the current destination.
- Direct service inquiries no longer ask for bedrooms or occupants, and the calculator is folded away. Optional timing/contact preferences stay available. Missing bedroom data remains null and produces no estimate; completed calculator handoffs can still retain actual planning inputs.
- Non-estimated service inquiries also retain null for uncollected site assumptions such as access, groundwater, kitchens and disposal; calculator defaults are not exported as customer-verified facts.
- An active-symptom selection immediately displays the existing non-emergency warning. A successful service submission displays its reference without presenting another blank submission form.
- These changes reduce visible effort; they do not establish a measured conversion lift or additional search demand. No new thin regional pages, partner claims, provider outreach or data sharing were created.

## Private free delivery

The existing private storage and download routes are retained for compatibility. `RELEASED_FREE` is distinct from `PAID`; free results have a zero amount and do not create payment records.

Use authenticated `POST /ops/record-results` (multipart) or `/ops/record-results/json`. The old creation URLs are aliases for free creation. The JSON shape remains `PaidUnlockController.JsonOfferRequest`, including `releaseApproval`, file name and base64 package bytes.

Before a real release or external message:

1. Follow `OPERATIONS.md` and the mandatory delivery gate in `AGENTS.md`: register the exact source hashes, review every page/property identity, and require the current gate receipt for the exact recipient, subject, files and filenames.
2. Build the release approval from that review, not from the surrounding email or filename. The application checks PASS, page/identity review attestations, recipient, package hash and an approval less than 24 hours old. These checks supplement the external manifest gate; they do not perform the human page review or run the gate tool themselves.
3. Create the free delivery. The endpoint returns `deliveryUrl` and `emailSent: false`. It does not email the customer or a professional. Creation is not evidence of delivery.
4. Send only with user authorization, recheck the applicable gate immediately before sending, and record the actual result transactionally in the private operations ledger.

Downloads retain expiry, count limits and hash rechecks. Private result routes are noindex/no-store/no-referrer; analytics omit their bearer tokens. Do not put delivery links in public content or analytics.

New PayPal order creation returns 410. Existing payment capture/webhook handling and historical payment/delivery records remain intact. Old unsent or unpaid links are not automatically released. A fresh approved free delivery is necessary for each historical result being reissued.

## Commercial handling

Research inquiries and explicit service inquiries remain different evidence classes. A record request, a missing record or a supplied phone number does not establish a verified service booking. Initial `commercialFollowup` fields intentionally remain unreviewed/not-confirmed until there is evidence.

Record-help contacts stay in private request files. Service inquiries create local pending-routing exports; the application does not send or sell them automatically. Review the actual accepted terms and buyer service area before any sharing. New terms do not retrospectively expand old customer consent. Private uploads and agency correspondence are not part of a matching export without the customer's separate choice.

No provider coverage or paid buyer agreement was created by this change. Before expanding paid distribution, establish at least one buyer's accepted geography, inquiry definition, price, duplicate/refund terms and contact method.

## Measure the pivot

The October 4–5 follow-through adds purpose-specific private result guidance, a private commercial inquiry brief on new intakes, and a CLI that prepares evidence-linked commercial outcome transactions. See [SUNDAY_GROWTH_WORKFLOW.md](SUNDAY_GROWTH_WORKFLOW.md) for exact inputs, commands, measurement scope and verification. This is not provider dispatch or proof of revenue; deployment must be confirmed separately.

The existing-guide changes in the same release candidate are documented in [GROWTH_ASSET_GUIDES_2026-10-05.md](GROWTH_ASSET_GUIDES_2026-10-05.md): Guilford GIS routing, Union historical-file requests and Indiana county-first guidance.

Compare equivalent source-page, geography and time windows. Keep these stages separate:

| Stage | Evidence required |
| --- | --- |
| Form start / accepted submission | Existing form analytics / persisted request ID |
| Reachable contact | Actual successful contact; not phone-format validation |
| In-scope provider inquiry | Stated purpose, geography, actual consent scope and buyer definition |
| Offered / accepted by a provider | Timestamped action and actual provider response |
| Revenue | Received payment, less credits/refunds; not a projected lead price |
| Contribution | Revenue less direct fees and research/handling time cost |

The website captures intake context and existing submission events. It does not yet automate provider acceptance, billing or attribution of a closed job. Track those outcomes in private operations records; do not report initial metadata as paid conversion. When analyzing GA4/GSC/Bing, save a `growth_signals` snapshot using the existing operations transaction as required by `AGENTS.md`.

## Release checks

- Run free-pivot, intake, calculator, private-result, notification and privacy regression tests.
- Check desktop and mobile home/intake/service flows with synthetic data and mail disabled in isolated storage.
- Verify source images, canonical URLs and indexable guide pages remain intact.
- Verify production mail/storage configuration before deployment, then smoke-test public navigation without submitting real customer inquiries.
- Do not migrate historical customer results, contact providers, or publish the private operations dashboard as part of a website deployment.

## Verification completed locally

- 71 selected regression tests passed across 15 suites after the lower-friction changes; `bootJar` succeeded.
- Browser-tested desktop and 390px mobile layouts, required intake fields, synthetic record-request submission, symptom-guide-to-service submission, and cost-result-to-production-intake prefill.
- Verified synthetic private storage retained purpose, timeframe, consent version and attribution; diagnosis exports had `not_estimated` instead of fabricated costs.
- Found and fixed the production service-form action returning 405 and an inline-script template escaping error; the corrected service flow submitted successfully with no browser console errors.
- Mail was disabled and storage isolated under `build/pivot-browser-storage`. No customer email, provider sharing, live submission, historical-case migration, or production deployment was performed.
- Additional browser QA confirmed a county-page CTA carried NC/Wake County into the common intake, optional details could be omitted, and the resulting private request retained the county source page and `entry_missing` context. A separate synthetic service inquiry retained null bedrooms, an empty timeframe and `not_estimated`; both flows completed without browser warnings/errors.
- Final release audit found and corrected historical private-result rendering (obsolete required template parameters), the finder-to-intake research-goal handoff, a stale paid-offer meta description, and uncollected service-property defaults. Regression tests cover legacy READY/PAID links, real-browser purpose carryover, current free terms and null property assumptions.
- Final `gradlew test bootJar` on 2026-10-04 passed: 1,041 tests across 124 suites, 1,023 executed successfully, 18 pre-existing skips, zero failures/errors. Changed JavaScript syntax and `git diff --check` also passed. Deployment confirmation is recorded separately after the production workflow.
