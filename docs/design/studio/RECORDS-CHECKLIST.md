# Records checklist studio page

Status: implemented as a noindex studio preview. It is not deployed.

## Direction

- Photo-led editorial composition rather than a checkbox app or card dashboard.
- One clear sequence: prepare property clues, request the useful record set, see how a real investigation works, then start an inquiry.
- Shared Studio header and footer.
- No invented government marks, statistics, or claims that an illustrative image is a property-specific record.

## Mockup

`records-checklist-mockup-v1.webp` is the visual reference for hierarchy, spacing, image weight, and section rhythm. Production copy and interactions remain grounded in the actual SepticPath workflow.

The page uses a dedicated four-image editorial set:

- `src/main/resources/static/images/studio/checklist-desk-v1.webp` — hero research desk
- `src/main/resources/static/images/studio/checklist-plan-v1.webp` — detailed illustrative plan and record folder
- `src/main/resources/static/images/studio/checklist-case-house-v1.webp` — illustrative investigation setting
- `src/main/resources/static/images/studio/checklist-closing-house-v1.webp` — dusk inquiry CTA

They were generated for this layout with the built-in image tool using the full-page mockup as a composition and mood reference. The plan contains no property identity, and the case setting carries a visible illustrative label.

## Verification

- `StudioPreviewTest` and `StudioGuideRoutesTest` pass.
- Browser QA at 1440×900, 1280×720, 768×1024, and 375×812 found one H1, no horizontal overflow, no broken images, and no console errors.
- Mobile menu and no-result disclosure open correctly.
- The real Overton investigation, county directory, and inquiry CTAs resolve inside the studio experience.
- Exact national `/septic-records-checklist/` links can map to this preview; regional checklist routes remain untouched.

Screenshots are under `build/qa/checklist/`.
