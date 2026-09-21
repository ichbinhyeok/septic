# Studio modernization coverage — 2026-09-20

Branch: codex/premium-visual-redesign. Preview only; no deployment.

## Scope and evidence

Fresh read-only crawl of all studio HTML destinations discoverable from service,
guides and the buyer guide: 445 before this change, 448 after it. Every discovered
page returned HTTP 200, one H1 and noindex. This is route/content structure QA,
**not** visual approval of 448 screens. It is separate from the older public-route
audit; a legacy “premium” CSS family does not establish mockup-level quality.

Repeat with `tools/audit-studio-coverage.js` using browse eval on localhost.
The full inventory and legacy destination/source counts remain available as
`window.studioCoverage` immediately after the audit.

## First shared fix

Every studio footer linked to legacy privacy, terms and methodology. These three
destinations now have isolated studio templates: photo-led header, ruled article
layout, section navigation and the shared header/footer. Privacy/terms use the
same complete policy sections, bullets and callout copy as the original. The
research-publication anchor is preserved. Methodology reuses source metrics,
standards and coverage data. No new operational or policy claims were added.

Original public routes, canonical metadata, sitemap, robots and source models
are unchanged by this pass. Preview URLs remain noindex,nofollow. A production
migration still needs route-by-route metadata and content parity checks.

## Remaining modernization queue

1. General records articles: as-built records and permit search by address each
   still linked from 53 studio pages; inspection letter and permit-record request
   from 52. These need dedicated editorial templates, not generic redirects.
2. Shared article onward links: old national calculator linked from 53 pages,
   buyer guide from 16. Equivalent studio destinations exist; audit query and
   source-context parity before converting these links.
3. Regional permit and inspection-cost guides: links remain on original routes;
   do not replace regional detail with generic state pages.
4. Specialized tank/pump/drainfield tools and full request builder: preserve
   distinct inputs, packet/download and follow-up functionality during migration.
5. Source policy, coverage, editorial standards, contact and standalone home
   composition still need dedicated studio review. Methodology deliberately keeps
   these original destinations until their equivalents exist.
6. Paid/private outcomes, embed surfaces and error screens are outside this crawl;
   no claim of full modernization or payment-flow verification is made.

## Verification

41 studio tests passed after adding policy copy parity and methodology data tests.
New templates inspected at desktop and mobile; privacy publication anchor retained.
Mobile county → Roane case navigation verified directly. No customer form submitted.
Screenshots: build/premium-qa/studio/policy-desktop.png and methodology-mobile.png.
