# County directory: mockup-led implementation

Scope: /design-preview/studio/counties/ only. No production route replacement.
Public county guides are rendered from ResearchDataService, not mockup examples.
The exact national directory link now maps here in StudioGuideRoutes; regional
checklists and permit-lookup pages are not migrated by this change.

## Art direction and assets

Built-in image generation was used, not CLI fallback.
Reference: county-directory-mockup-v1.webp in this directory.
Hero: src/main/resources/static/images/studio/county-directory-lane-v1.webp.
The hero is illustrative scenery, not evidence of a customer property.
Shared approved header/footer remain unchanged; invented mockup slogans,
copyright year and ornamental claims were not copied.

Implementation follows the reference's full-bleed landscape, serif headline,
rectangular search field, ruled county list and dark photo-led investigation
section. Six initial rows keep the default page compact; Show more adds 24.
All county links are server-rendered and visible without JavaScript.
Search supports county/state words, exact state abbreviations and a state filter.
No matches means no published guide match, not no property records.

## Generation prompt

Use case: ui-mockup. Create a high fidelity premium desktop website design reference for SepticPath county septic-records directory. Tall full-page screenshot, 1440px-wide design proportions. Brand understated editorial luxury, warm ivory #f8f8f1, very dark forest green #092e29, emerald buttons, Newsreader-like elegant serif and Manrope-like clean sans. Cream header with SepticPath wordmark left and Our service, Our work, Guides, Ask SepticPath right. Full bleed hero panoramic photograph of pastoral American countryside with winding lane, mature oak trees and understated stone country house on right, soft sun, deep green darker left. Large white headline on left exactly 'Your county.\nYour next step.' Small eyebrow 'THE RECORDS DIRECTORY'. Supporting sentence 'Find the local office. Follow the paper trail.' Under headline a clear rectangular cream search input 'County name or state' and green 'Find my county' button, NOT rounded pill. Hero fits first desktop viewport with header. Below spacious ivory directory section: left headline 'A local route, clearly mapped.' right a rectangular 'All states' dropdown. Below a refined three-column ruled text list of county names and states: Autauga County / Alabama; Morgan County / Alabama; Roane County / Tennessee; Williamson County / Tennessee; St. Croix County / Wisconsin; Anderson County / South Carolina. Each row ends small arrow. No cards, no map infographic, no fake numerical claims. Then wide forest-green editorial story section with beautiful research desk photo occupying half, right white headline 'An office is only the beginning.' short line 'See how we follow the clues and interpret the file.' outlined 'See our investigations' action. Calm ivory footer. Show actual typography and finished UI, not wireframe or collage of screens, no device frame. Intent: search-led and photo-rich, exceptionally polished and buildable.

Hero prompt: Photorealistic wide 16:9 countryside with winding gravel lane,
mature oak framing and a naturally shaded left half, dry stone wall, understated
stone house at far right, rolling pasture, warm afternoon light. Premium
editorial property photography. No text, logos, signs, UI or watermarks.

## Verification

Studio tests passed after adding county-directory route and complete published
county link coverage. Browser checks cover search, filters, pagination, empty
results, loaded hero, noindex and responsive overflow.
No live inquiry submitted; no deployment.
