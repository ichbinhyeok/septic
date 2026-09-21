# Buyer guide editorial preview

Preview: `/design-preview/studio/buying-guide/`, enabled only with the studio feature flag. Original `/buying-a-house-with-a-septic-system/` still uses its original view, URL and SEO metadata. No deployment or migration was performed.

## Content and design

Frontend skill directed an image-led hero, narrow sticky table of contents, readable article column, desk image, restrained sage checklist and shared closing CTA/footer. Imagegen built-in generated `buyer-reference-v1.webp`; the final layout reuses approved county-property/guides-mobile/work-desk/oak-pasture images. Decorative model-generated signage, invented scenarios and claims in the mockup are not used as factual content.

The controller delegates to the existing public article model; the independent JTE template renders its title, introduction, all deep-dive paragraphs, fit bullets, decision steps, risk/driver bullets, preparation checklist, FAQ answers and internal links. The original buyer learning scenario remains explicitly illustrative. State buyer links use the original model and are not silently redirected. All text is server-rendered; JS only updates the reading-location indicator. No new author, review date, reading time, verification or approval claims.

This is one representative longform article, not an automatic rollout to every article or state page. Review remaining supplemental production modules before any production promotion. Preview noindex/nofollow is deliberate; production SEO schema and metadata were not copied into the preview or altered.

Refinement: centered reading grid with a tighter TOC gap, shorter desktop/mobile hero, and an illustrative pasture photograph before the checklist. JS now also collapses the mobile TOC and initially shows six state links, with an accessible button exposing all 50. Without JS the full navigation and state list remain visible in server-rendered HTML.

## Generation prompt

### Buyer folio refinement

Built-in image generation created `src/main/resources/static/images/studio/buyer-folio-v1.webp` for the article (not an official record). It replaces the shared desk photograph. Three seller statements are now separate numbered, ruled editorial blocks. Four introductory paragraphs have scan-friendly subheadings and a two-column desktop reading layout; all original explanations remain server-rendered.

Final asset prompt:

> Use case: photorealistic-natural. Asset type: premium editorial buyer-guide photograph for SepticPath website, landscape 16:9. Primary request: quiet contemporary home-buying due diligence still life, tactile printed buyers reading folio on a beautiful pale oak table beside a window overlooking soft green garden. One open cream editorial booklet with exact clean readable dark petrol headings "Before you close", "Property records", "Inspection", "Open questions" and short meaningful text "Keep the approval, layout and supporting history together." A dark green folio underneath has small serif wordmark "SepticPath". A simple brass house key and slim graphite pen nearby. Not an official permit or technical drawing: no seals, stamps, signatures, addresses, parcel numbers, certificates, invented evidence or approval claims. Real photograph look, elegant premium magazine art direction, oblique overhead viewpoint, centered booklet readable within a mobile crop, generous oak surface around it, afternoon window light, natural paper fibers, subtle shadows, restrained ivory oak petrol sage palette. No people, no UI, no collages, no overlaid marketing text, no watermark. The document should have real editorial typography and a few neat text blocks, not blank paper and not dense illegible gibberish.

### Original layout reference

> Use case: ui-mockup. High-fidelity complete premium editorial webpage for SepticPath buyer guide. Ivory paper, deep petrol text, emerald links, Newsreader serif and Manrope sans. Shared header SepticPath / Our service / Our work / Guides / Ask SepticPath. Hero fullbleed cinematic photo of welcoming country house and tree-lined drive with calm dark left text 'Buying a house with a septic system.' Small eyebrow 'The buyer’s field guide', button 'Start with the records'. Below small breadcrumb and article reading layout: narrow sticky left table of contents, generous main reading column. Heading 'Know what the file can tell you.' Short readable paragraphs. Wide warm wooden desk photograph with property folder interrupts article after first section. Sage checklist section 'Before you close.' with four elegant ruled checklist rows, NOT dashboard cards. Midarticle heading 'A seller’s statement is a question to verify.' Narrative example 'Two tanks. One drawing.' expressly illustrative, no fake official evidence. Sections Records / Inspection / Questions to resolve / Buyer checklist / FAQs. Bottom restrained related guides rows then photographic CTA 'A clearer file. A better next question.' and big petrol SepticPath footer. Practical shippable longform design, rich photographic atmosphere, typography and generous whitespace. No invented statistics, certifications, compliance guarantees, reading time or author/review date.

## Validation

- 26 focused Java preview/intake/disabled tests passed. New test checks preservation of all curated content arrays, FAQ and internal-link targets, table-of-contents anchors, one shared header and the unchanged production view.
- Browser checks passed for six anchors, active reading location, FAQ opening/closing, one H1, image loading and 320px overflow.
- All 67 unique local destination pages linked from the article returned HTTP 200.
- Desktop1440, mobile390 hero, midarticle and checklist screenshots inspected in `build/premium-qa/studio/buyer-*.png`; no console errors.
- Run `tools/qa-studio-article.js` through browse eval and await `window.articleQa`. This performs GET-only link checks; no intake/agency/payment actions.
