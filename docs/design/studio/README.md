# Studio rebuild: image-first implementation

## Status and boundary

This is a **new, isolated state-guide presentation**, not another CSS overlay and not a completed migration of the public site. It does not import `layouts/app.jte`, `app.css`, `pages.css`, `premium-families.css`, or the legacy card/workflow components.

Start locally:

```powershell
.\gradlew.bat bootRun --args="--server.port=8084 --app.studio-preview.enabled=true"
```

Open `/design-preview/studio/alabama/` or another valid state slug. The controller is disabled unless explicitly enabled. Preview pages use `noindex,nofollow` and are not added to the sitemap. Existing public routes, metadata, customer workflows, and checkout remain unchanged.

## Art direction contract

- **Visual thesis:** A contemporary property-research studio, combining editorial scale, warm paper, woodland photography, and precise emerald actions.
- **Content plan:** Property-led hero → explanation of the file → photographic research spread → three next-step rows → actual state facts and expandable sources → functional county directory → inquiry band → restrained footer.
- **Interaction thesis:** Short hero entrance, restrained desktop image depth, and clear link/button movement. Reduced motion disables these effects; all content stays visible without animation or JavaScript.

The mockup is `state-guide-reference-v1.webp`. The implementation intentionally adds real county navigation and expandable state sources; it does not fabricate local evidence or treat the generated desk image as a delivered case. Actual delivered work is linked separately.

## Assets and generation

Built-in image generation was used, not an external CLI/API script. Generated assets were copied into the repository; original generation files remain intact.

| Asset | File |
| --- | --- |
| Full-page reference | `docs/design/studio/state-guide-reference-v1.webp` |
| Clean hero photograph | `src/main/resources/static/images/studio/property-hero-v1.webp` |
| Research still-life | `src/main/resources/static/images/studio/research-desk-v1.webp` |

### Prompt set

**Reference:** A finished tall desktop website for SepticPath, private US septic property record research. Deep petrol, ivory, emerald, editorial typography, no inherited dashboard/card layout. Slim navigation; full-bleed wooded property hero with the headline “Know the property. Before you commit.”; “The right records. A clearer decision.” section with a wide photographic research-folder spread; three numbered full-width next-step rows; compact Alabama source facts; landscape inquiry band; oversized restrained wordmark footer. No fabricated testimonials, success counts, official seals, approval claims, or government identity.

**Hero:** Use the reference's top landscape only. Photoreal panoramic aerial property with a pale farmhouse in the right third, green lawn, forested hills and lake, warm late-afternoon light. Calm dark forest/petrol space on the left for live HTML. No text, UI, documents, badges, or frame.

**Desk:** Use the reference's middle photograph only. Premium cream folder on walnut desk over a technical site drawing, small plant and cup left, dark green books upper right. Readable “SepticPath”, “PROPERTY RECORDS”, “CLEAR FINDINGS”, and neutral “SITE PLAN”. Real material texture and soft window light. No personal address, agency seal, invented approval, certification, UI, or framing.

## Next migration gate

Use the new composition as the implementation reference, not the old templates. Before replacing a public route family, map its exact search intent, unique local content, existing actions, metadata, structured data, and functional state into the new architecture. Do not replace hundreds of distinct pages with this generic preview or claim SEO parity without that mapping. The previous full-route audit measures the old implementation and is not evidence of completion of this rebuild.

## County family — image-first continuation

Preview: `/design-preview/studio/alabama/autauga-county/`. State preview county links now lead into this new family. All published county records can use `/design-preview/studio/{stateSlug}/{countySlug}/`; no production route was replaced.

- **Visual thesis:** An immersive property photograph, oversized editorial type, tactile research materials, and an oak-pasture closing scene make the guide feel like a property-research publication.
- **Content plan:** Photographic hero → quiet section navigation → concise local facts → three next steps with expandable local detail → large document photograph with short file-type explanations → landscape inquiry band → actual local FAQ → traceable sources.
- **Interaction thesis:** Hero entrance/image settle; desktop sticky research photograph; restrained link and disclosure transitions, with reduced-motion support. Native disclosures and navigation work without JavaScript.

The county preview reuses `SiteController.countyRecordsPage` only to assemble the existing curated model, including operational access overrides. It never calls the legacy presentation. This is a temporary migration adapter; extract a shared model assembler before production rollout rather than growing controller-to-controller coupling.

Local request wording, buyer/seller/contractor lists, record requests, search instructions, limits, FAQ and source notes remain real data. Long source wording is behind disclosures rather than filling the photographic spread. Generated scenery and document still-life are visual assets, not claimed local evidence. The delivered-work link is separate. Availability and service outcomes are not guaranteed.

### County image assets and prompts

Generated using the built-in image tool (no CLI/API fallback), then copied into the repository:

| Asset | Saved path |
| --- | --- |
| County reference | `docs/design/studio/county-guide-reference-v1.webp` |
| Oak-framed house hero | `src/main/resources/static/images/studio/county-property-v1.webp` |
| Pasture inquiry scene | `src/main/resources/static/images/studio/oak-pasture-v1.webp` |

**County reference prompt:** New tall desktop county-records guide, using `state-guide-reference-v1.webp` as a style reference only. Match Newsreader-like serif, Manrope-like sans, petrol/ivory/sage/emerald, cardless editorial composition. Full-bleed oak-framed farmhouse; “Autauga County. Your property. Its paper trail.”; quiet section navigation; “A local route. Not another dead end.” with local fact rows; sage three-step source route; large research-folder photograph beside “What the file can tell you.”; scenic “Have the address? We can take it from here.” CTA; FAQ; source notes; large SepticPath footer. No invented authority, testimonials, statistics, fees, promises or seals.

**County hero prompt:** Generate only the clean top landscape from the county reference, no UI/text. Panoramic photoreal editorial scene: pale farmhouse and gray roof in right third, curving gravel driveway, timber fence, immense dark oak and foliage left for HTML copy, sunlit overhead branches and lawn. Same eye-level reference framing. Natural warm evening light, not mansion CGI. 16:9.

**Pasture prompt:** Generate only the lower landscape from the county reference, no UI/text. Photoreal panoramic eye-level pasture behind a dark timber fence; majestic oak trunk in right third, overhead branches; golden late-afternoon light from right and calm shadowed left for HTML copy; distant low wooded hills; natural bark and green/amber color. 16:9, no CGI or excessive saturation.

### Verification boundary

`StudioPreviewTest` renders every published county through its own preview URL and checks invalid routes, local data, standalone styling, and the Suffolk phone-route override. Browser QA covers representative desktop/mobile layouts and native disclosure navigation. This is not a claim of per-county visual review or production SEO/content/checkout parity; those remain migration gates.

2026-09-20 verification: full Gradle suite 940 tests, 922 passed, 18 skipped, zero failures/errors. Autauga at 1440px and 390px: images loaded, no horizontal overflow, mobile menu and property/FAQ/source/record-request disclosures working. Suffolk at 768px retains `tel:631-852-5700`; San Bernardino at 390px has no horizontal overflow. Fresh Autauga console has no errors. No inquiries were submitted and no external actions were taken.

Actual browser captures: `build/premium-qa/studio/county-desktop-final.png`, `county-file-final.png`, and `county-mobile-final.png`. These are implementation screenshots, not generated mockups.

## Service and work — second continuation

Previews: `/design-preview/studio/service/` and `/design-preview/studio/work/`. The shared preview navigation connects these two pages. These are new service-overview and case-study presentations, not a rebuilt intake form or checkout. Address research and existing-document review link to their original intake URLs; no new POST endpoint or payment behavior is introduced.

**Visual thesis:** Warm photographic research materials explain the service; original anonymized evidence establishes what the work actually found. Large serif hierarchy, ivory space, sage process sections, and scenic inquiry transitions retain the approved design direction.

**Content plan:** Service: desk hero → brief explanation → wide file-divider photo → three steps → actual price terms → two intake handoffs → FAQ. Work: atmospheric desk hero → Shelby case → original public plan plus findings → actual case trace and limitations → delivered findings excerpt → four further published cases → inquiry.

**Interaction thesis:** Hero entrance and image settle, gentle desktop image-depth/hover treatment, sticky case explanation, visible link motion and native FAQ disclosures. Reduced-motion overrides remove movement. No invented form submission or payment state.

### Image generation record

Built-in image generation, not the CLI. Assets copied into this repository:

| Asset | Path |
| --- | --- |
| Service reference | `docs/design/studio/service-reference-v1.webp` |
| Work reference | `docs/design/studio/work-reference-v1.webp` |
| Desk/window hero | `src/main/resources/static/images/studio/research-room-v1.webp` |
| File-divider photograph | `src/main/resources/static/images/studio/file-dividers-v1.webp` |
| Work hero photograph | `src/main/resources/static/images/studio/work-desk-v1.webp` |

**Service reference prompt:** A full-page editorial SepticPath service page matching the county reference's serif/sans system, ivory/sage/petrol/emerald palette and cardless layout. Full-bleed rustic research desk overlooking woodland, “You bring the address. We find the paper trail.”, free-research/optional-US-$29 line, “Less chasing records. More understanding.”, tactile divider photograph, three process rows, transparent pricing ledger with agency fees at cost/approval, scenic address/file handoff links, FAQ and oversized footer. No fabricated claims, testimonials, seals or forms.

**Research-room prompt:** Clean top photograph only, 16:9. Walnut desk beside broad timber window, oak pasture outside, golden light. Cream physical folder marked “SepticPath” / “PROPERTY RECORDS” on right, cup, pens, paper, plant. Dark shadowed left third for live HTML. Authentic paper/wood, no overlay UI, badges, claims or watermark.

**File-divider prompt:** Clean middle photograph only, 16:9. Cream cotton-paper tabs fanned diagonally over subtle line drawing on walnut: “PERMIT HISTORY”, “SITE PLANS”, “REPAIR RECORDS”, “AGENCY CORRESPONDENCE”. Warm window light, cropped cup and blurred plant. No official seals, personal addresses, signatures or certification. This is illustrative service imagery, not source evidence.

**Work reference prompt:** Full-page editorial case-study layout matching county style. Full-bleed cream folder on walnut; “The record. The finding. The difference.”; Shelby case intro; documentary spread; three-step case trace; four numbered further-case links; scenic inquiry and large footer. Reference-only placeholder document, no invented personal data or official text. Implementation must use actual existing redacted evidence instead.

**Work hero prompt:** Clean 16:9 photo matching work reference. Left 45% empty dark walnut/shadow for HTML, right 55% cream folder with small “SepticPath” / “PROPERTY RECORDS”, drawing beneath far right and dark green books upper right. Keep folder lettering to right of midpoint. Authentic paper/window light, no UI, official seals, personal data or fabricated signatures. Generated after browser comparison showed the reused desk image's lettering competing with the headline.

### Evidence and migration boundary

The work page reuses the two already-public files `shelby-site-plan-detail-public.png` and `shelby-brief-findings-public.png` without altering their bytes. Claims are drawn from the existing public case narrative, not inferred from generated imagery or a new private case audit. It preserves design-versus-current-condition limits, no-sale/no-inspection claim, and the unconfirmed Overton property-match warning. No private files or ledger data were accessed or published.

Service terms remain those in the existing offer page: research and agency requests free, useful property-matched result unlock optional US $29, agency fees separate at cost with approval, no upfront/automatic charge. A later migration must keep the real intake's validation, uploads, privacy, receipt/error states and checkout behavior; these are not claimed rebuilt here.

Verification 2026-09-20: full suite 942 tests, 924 passed, 18 skipped, no failures/errors. After the final work-hero replacement, the focused StudioPreview suite passed again. At 1440px both new pages loaded all images with no overflow or console errors. Service at 390px: mobile menu and FAQ work; the existing-file link opens the original intake with `mode=review` and `researchGoal=understand_file`. Service at 768px has no overflow. Work at 390px has no overflow; below-fold source images load when scrolled into view. No form submission, agency request, payment, or deployment was performed.

Remaining migration families include the real intake error/success states, the full record finder, professional pages and utility/private states. The full request builder now has a dedicated studio preview with packet, print and device-local follow-up behavior preserved; the public production route remains unchanged until an explicit cutover.

## Reference-fidelity refinement

Visual thesis: recover the reference's photographic dominance by narrowing the service headline, shortening the process/pricing bands, and art-directing mobile imagery rather than cropping away its subject. Content order and price/privacy terms are unchanged. Existing entrance, image-depth and disclosure motion are retained with reduced-motion support.

Both editorial heroes now select a portrait image below 701px, with matching media-qualified preloads. Saved asset: `src/main/resources/static/images/studio/mobile-research-v1.webp`, generated with the built-in image tool. Prompt: “Photorealistic premium editorial mobile website background, portrait 9:16. Upper 60% calm very dark forest-green study wall, no objects or lettering, for live white text. Lower 40% complete cream SepticPath / PROPERTY RECORDS folder on warm walnut with illustrative drawing and cropped ceramic cup, natural window light. Tactile cotton paper, no CGI, website UI, official seals, addresses or watermark.”

Per user feedback, the case spread no longer lets a tall scan dominate the initial composition. A clearly illustrative photograph leads; the actual unchanged redacted plan is available in the adjacent native “See the actual plan” disclosure, including full-size access. Findings and limitations remain visible. No evidence was redrawn, enhanced, synthesized, or removed. The later delivered findings excerpt remains actual published evidence.

Refinement QA: at 1440px the service page height changed from 4108px to 3651px (about 11% shorter) while retaining pricing and FAQ content. At 390px the hero selects the portrait source and the inquiry action remains in the first viewport; 390px and 768px have no horizontal overflow. The work source disclosure opens the original redacted image on mobile without overflow. Full regression suite passed with 942 tests (18 skipped). Screenshots: `service-desktop-refined.png`, `service-mobile-refined.png`, `work-evidence-refined.png` under `build/premium-qa/studio/`. These are preview-only refinements, not a production launch.

## Inquiry intake — mockup-led implementation

### Shared shell consolidation

Verification: 21 focused studio tests passed after consolidation, including all published county renders and the existing intake scenarios. Browser QA at 390px confirmed menu appearance, Escape/focus return, outside-click close, desktop-resize close and preserved county filtering. At 1440px intake has exactly one header/footer and correctly marked current links, with no horizontal overflow or console errors. No form submissions or deployment in this refinement.

All five studio page families now use `studio/shell.jte`, including the state guide. One navigation partial supplies identical destinations and labels for desktop, mobile and footer; the inquiry page retains its intentionally compact footer sizing. Visual thesis: preserve the approved ivory/petrol editorial frame and photo-led pages. Content structure remains unchanged. Interaction thesis: retain entrance/depth motion, add subtle current-page/hover cues and a short mobile-menu reveal. Escape closes the menu and restores focus; outside clicks, moving focus outside and switching to desktop close it too. Native details keep navigation usable without JavaScript. Public production layouts, route data and indexing rules remain unchanged. Guides and legal links still intentionally lead to existing public pages; this does not claim those pages are modernized.

Supersedes the earlier intake handoff notes: studio service, work, county and state inquiry CTAs now point to `/design-preview/studio/intake/`; file-review CTAs add `?mode=review`. Public production URLs and templates are unchanged. The opt-in controller remains disabled by default. When enabled, this is a functional form, not a fake submit demo: a valid submission invokes the existing private-storage and notification services. QA must mock those services or use invalid requests; do not submit real-looking test leads to the running preview.

Visual thesis: a photographic walnut/ivory research scene beside a calm, spacious working form, with no inherited legacy form layout. Content: property/address/email/question first; optional uploads and context as native disclosures; explicit consent and pricing; compact next steps. Interaction: restrained entrance, desktop sticky introduction, mode underline/disclosure transitions; reduced motion respected. Mobile uses a short photographic introduction so the form remains close to the first viewport.

The controller delegates the existing validation, document signature/count/size policy, throttling, storage, notification and receipt handling to SiteController. It adds no alternative customer database. A server-enforced review mode requires files even without JavaScript. Existing optional fields remain available. Error summaries preserve bound input and link to invalid fields; reselecting files after a server response is explicit. Successful requests show the actual reference and only claim an emailed receipt when the service confirms one. Oversized HTTP uploads return to the new form; that transport-level rejection cannot preserve the discarded multipart body. Client-side checks prevent common oversize mistakes before submission. No client-side address or file data is persisted in browser storage.

Built-in image generation (not CLI):
- Mockup: `docs/design/studio/intake-reference-v1.webp`.
- Clean photographic asset: `src/main/resources/static/images/studio/intake-room-v1.webp`.
- Both are illustrative design assets, not agency evidence.

### Intake image prompts

Verification: full Gradle suite **952 tests, 934 passed, 18 skipped, zero failures/errors**. Includes 10 dedicated intake tests with storage/notifications mocked: both modes, retained invalid values, required review attachment without JS, file signatures/count/size, success with/without receipt, throttling, honeypot and oversized-upload recovery. Browser checks at 1440/768/390px found no horizontal overflow; review switching and selected-file feedback worked, unsupported file extension was rejected, server error response retained inputs and marked the email field. No real lead, email, payment or deployment was performed. Browser screenshots: `build/premium-qa/studio/intake-desktop-final.png`, `intake-mobile-form.png`, `intake-mobile-error.png`. Success content was integration-tested with mocked services, not submitted through the live browser. Existing Gradle deprecation warnings remain.

Mockup prompt:
Use case: ui-mockup. High fidelity premium desktop website intake page for SepticPath, continuation of reference brand (reference image style only, not same layout). Canvas 1536x1600 approx. Ivory top navigation SepticPath / Our service / Our work / Guides. Below clean 43% left photographic editorial panel and 57% warm ivory practical inquiry form. Left tall photorealistic oak countryside viewed through window, walnut desk and cream folder below, deep petrol calm upper region, white elegant Newsreader serif heading 'Every property has a paper trail.' eyebrow 'LET’S FIND YOURS', short copy 'Start with an address. We’ll take it from here.' Bottom small assurance 'Free research. No upfront payment.' Right generous margins elegant heading 'Tell us about the property.' Small mode tabs 'Find my records' / 'Review my file'. Realistic restrained UI fields: Property address, State, Email, What would you like to find out? Spacious light inputs hairline border. Native disclosure row 'Attach files (optional) +' and 'Additional details +'. Small consent checkbox and legible brief consent text. One emerald wide button 'Start my free research →'. Under it fine print 'No card required. Optional US $29 result package.' Bottom of form a thin line and three small numbered editorial next steps 'We trace the source. / You preview the findings. / You choose what’s next.' Deep petrol compact footer SepticPath. No rounded SaaS cards, gradients, fake seals, decorative icons. Premium architectural editorial restraint, strong photograph, precise baseline alignment, airy polished production-shippable composition. Keep form usable rather than huge marketing hero.

Clean asset prompt:
Use case: photorealistic-natural. Asset type: portrait website photographic panel, no website UI. Reference is style/composition guide: recreate ONLY the left photographic scene of this SepticPath mockup, without any overlay headline/nav/footnote/website chrome. Tall portrait 2:3 photograph of walnut research desk beside tall window looking onto oak pasture and distant hills in warm late afternoon. Cream textured folder lower-right on desk, restrained physical print 'SepticPath' and 'PROPERTY RECORDS' on folder, no fake official seals or documents. Ceramic mug, small plant lower-left. Upper left 35% calm very dark petrol shaded interior, usable negative space for white HTML heading. Window and countryside occupy upper right and center. Folder fully visible lower third with realistic paper layers. Photorealistic architectural editorial photograph, rich wood grain, warm light, quiet premium aesthetic. No extra text, no UI frames, no collage, no watermark. Preserve lighting/material palette and scene proportions from the mockup.
