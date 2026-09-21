# Work evidence preview

## Mobile and evidence refinement

Built-in image generation added `src/main/resources/static/images/studio/work-archive-mobile-v3.webp`. CSS magnifies the existing public plan at source coordinates x=200, y=670, width=360, height=340; no image bytes or handwriting are altered. The full public excerpt and its full-size link remain directly below.

Mobile image prompt:

> Use case: photorealistic-natural. Asset type: portrait 9:16 mobile website hero background for premium records-research service. Quiet contemporary archive library, deep petrol green shelves and warm walnut desk. Composition critical: TOP 62 PERCENT of image is calm very dark softly out-of-focus green shelving, no bright items, no lettering, no objects competing with white website title and button. BOTTOM 38 PERCENT a small cream accordion folio on walnut desk, its complete silhouette visible, elegant warm daylight grazing paper. Folio has four subtle small serif lines of text exactly "PROPERTY RECORDS", "SITE PLANS", "CORRESPONDENCE", "FIELD NOTES". All writing must lie below 70 percent of image height. No other text, no seals, no technical drawings, no people, no UI, no watermark. Premium architectural editorial photography, natural materials and shadows, restrained dark green walnut cream palette. Do not put the folio in the middle or upper part. Plenty of quiet vertical space above the desk.

Route: `/design-preview/studio/work/`. Existing studio shell, original public case excerpts and existing case links retained. No private files accessed; no delivery, submission or production migration.

The frontend skill informed the photographic archive hero, cardless evidence gallery and explicit two-column distinction between recorded design and unresolved field questions. Original plan pixels remain unmodified; clicking the plan opens the existing public image at full size. The generated mockup's illustrative drawing is never used as evidence.

Built-in image generation outputs:
- `docs/design/studio/work-reference-v2.webp` (composition only)
- `src/main/resources/static/images/studio/work-archive-v2.webp` (hero atmosphere)

## Mockup prompt

> Use case: ui-mockup. High fidelity full-page desktop premium SepticPath case-study website, tall editorial composition, ivory paper, deep petrol Newsreader serif, Manrope sans, emerald action. Shared header SepticPath / Our service / Our work / Guides / Ask SepticPath. Full width cinematic hero of a quiet contemporary archive desk with cream folio on right, dark green shelves and dark negative space left, heading "The record. The finding. The difference." Below white two-column opening "A missing file. A documented starting point." small Shelby County Tennessee / Sale preparation. Main large evidence gallery on ivory: left a tall black-and-white hand drawn anonymized site-plan excerpt on muted sage background, right elegant large typography list "Four bedrooms", "1,000-gallon tank", "600 feet of field lines", label Recorded design only, and Enlarge source button. Evidence visually documentary not generated glamour; no stamps or fake official badges. Next a restrained three-step horizontal editorial timeline Search gap / Agency return / Source-linked explanation. Then broad green pasture photo with heading "A file has limits. We make them clear." Then two columns What the record establishes / What still needs checking, actual findings excerpt gallery. Bottom four ruled links to other case stories and photographic CTA "What is your property’s unanswered question?" Footer giant SepticPath. No cards, rounded dashboard boxes, fake testimonials, metrics, guaranteed outcomes or invented identifying information. This is a composition reference, not authentic evidence.

## Hero prompt

> Use case: photorealistic-natural. Asset type: wide 16:9 website hero photo for premium SepticPath records research service. A quiet contemporary private library with deep dark petrol green built-in shelves, restrained old green linen archive binders and natural walnut desk. On the RIGHT HALF of the desk a cream accordion folio standing upright, with exact elegant small serif lettering "PROPERTY RECORDS", "SITE PLANS", "CORRESPONDENCE", "FIELD NOTES" in four lines. No other text. LEFT HALF dark calm negative space from softly out-of-focus shelves, suitable for white website heading. Warm directional daylight grazing cream textured paper and rich wood grain. Photorealistic premium editorial architectural photography, eye-level wide composition, no people, no official seals, no fabricated permits, no plans, no signatures, no UI, no graphics, no watermark. Keep folio around right third with ample breathing room, no cropped-off edges.
