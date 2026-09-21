# North Carolina records studio hub

Status: implemented and browser-verified. Noindex preview only; not deployed.

## Direction

- Visual thesis: wooded mountain property photography and an archival county-records desk expressing North Carolina's county-first permit route.
- Content plan: full-bleed property hero, county-first explanation, county finder, published county routes, complete-file interpretation, investigation proof, workflow, related guidance, FAQ and inquiry.
- Interaction thesis: restrained hero entrance, county-result reveal, route-row affordance and expandable research details.
- Existing public `/north-carolina-septic-permit-lookup/`, source copy, official URLs and county data remain unchanged.

## Reused image set

- `property-hero-v1.webp` — wooded mountain property hero.
- `official-lookup-map-v1.webp` — state/county research map and property index.
- `file-dividers-v1.webp` — permit history, site plan, repair and correspondence dividers.
- Existing case-study and closing images support proof and conversion.

Reuse is intentional: this regional page receives a distinct composition while sharing the approved research-image library. All images remain illustrative and do not represent a North Carolina property file.

## Verification

- Targeted `StudioGuideRoutesTest` and `StudioPreviewTest` suite passes.
- Desktop and 375 px mobile layouts have zero horizontal overflow.
- County finder exposes 29 published counties plus the empty prompt option.
- Durham County selection resolves to the studio county guide, the official county source, the request workspace, and intake while preserving the entered property clue.
- All five page images load successfully; the browser console is clean.
- QA captures: `build/qa/north-carolina-records/desktop-full.png` and `build/qa/north-carolina-records/mobile-full.png`.
