# Official lookup tools studio hub

Status: implemented and browser-verified. Noindex preview only; not deployed.

## Direction

- Visual thesis: a premium nationwide property-records atlas built from maps, county indexes, archival documents and rural landscape photography.
- Content plan: full-bleed atlas hero, three route choices, state finder, five priority official programs, artifact interpretation, investigation proof, source workflow, related guidance, FAQ and inquiry.
- Interaction thesis: restrained hero entrance, state-route reveal, route-row movement and expandable research details.
- Existing public `/official-septic-lookup-tools/`, source copy, official URLs and SEO structure remain unchanged.

## Mockup

`official-lookup-mockup-v1.webp` is the approved visual reference for the hero composition, cardless route rows, rectangular controls, document section and page rhythm.

## Generated image set

- `official-lookup-hero-v1.webp` — nationwide map and official-records research desk.
- `official-lookup-map-v1.webp` — state/county selection map, parcel sheets and property index.
- `official-lookup-files-v1.webp` — permit, as-built, final approval, repair history and parcel evidence.

The images are illustrative, contain no customer identity and do not represent a real agency or property file.

## Verification

- Targeted Spring tests: `StudioPreviewTest` and `StudioGuideRoutesTest` pass (30 tests).
- Desktop 1440 × 900 and mobile 375 × 812 browser checks pass with one H1, 51 state selector options (blank plus 50 states), no horizontal overflow, and no console errors.
- Tennessee + Overton resolves to the studio TDEC route; Oregon resolves to its published state guide.
- All generated and supporting image requests return HTTP 200. Full-page evidence is under `build/qa/official-lookup/`.
- The shared Guides hub now links directly to this page.
