# TDEC records studio hub

Status: implemented and browser-verified. Noindex preview only; not deployed.

## Direction

- Visual thesis: Tennessee mountain atmosphere and archival record texture presented as a premium official-route research room.
- Content plan: hero, three Tennessee routes, county finder, identity clues, file interpretation, investigation, county directory, inquiry.
- Interaction thesis: restrained hero entrance, county-route reveal, and expandable official-source / limitation details.
- Existing public `/tdec-septic-records/`, official URLs and county routes remain unchanged.

## Mockup

`tdec-records-mockup-v1.webp` is the visual reference for imagery, typography, cardless route rows, rectangular controls and page rhythm.

## Generated image set

- `tdec-desk-v1.webp` — Tennessee mountain records desk used as the page hero.
- `tdec-identity-v1.webp` — parcel index, tax map, deed, permit and sketch research surface.
- `tdec-file-v1.webp` — permit application, site sketch, repair history and final-approval spread.

The images are illustrative compositions, contain no customer identity, and do not claim to be records for the selected county.

## Implementation

- Preview: `/design-preview/studio/tdec-records/`
- Original public route preserved: `/tdec-septic-records/`
- Shared studio header and footer, independent TDEC page CSS and JavaScript.
- The county route finder uses all 95 existing `TennesseeCountyRouteView` records. It distinguishes TDEC field-office counties from contract counties and can hand off to the county guide or request workspace.
- Existing title, introduction, target reader, deep-dive copy, fit criteria, decision steps, failure cases, request-preparation list, commercial drivers, FAQs, internal links and official sources are retained.
- The preview remains `noindex,nofollow`; no public URL, canonical or deployed SEO signal has changed.

## Verification

- Targeted Spring tests: `StudioPreviewTest` and `StudioGuideRoutesTest` pass (29 tests).
- Desktop 1440 × 900 and mobile 375 × 812 browser checks pass with one H1, 96 county selector options (blank plus 95 counties), no horizontal overflow, and no console errors.
- Overton County resolves to the Cookeville Environmental Field Office/TDEC path; Davidson County resolves to its contract-county program.
- All page image requests return HTTP 200. Full-page evidence is under `build/qa/tdec/`.
