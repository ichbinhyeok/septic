# Permit lookup studio hub

Status: implemented as a noindex preview. Not deployed.

## Direction

- Visual thesis: a premium research desk that makes the path from state to county custodian to actual record feel clear.
- Content plan: photo-led hero, state route selector, record set, no-result workflow, real investigation, inquiry.
- Interaction thesis: restrained hero entry, state-selection handoff, ruled route-row hover and disclosure.
- The hub complements the address finder; it does not duplicate it.
- Existing public `/septic-permit-lookup/` and all regional URLs remain unchanged.

## Mockup

`permit-lookup-mockup-v1.webp` is the visual reference for hierarchy, image weight, rectangular controls, and section rhythm. Production content remains grounded in the existing content model and published regional routes.

## Image set

- `src/main/resources/static/images/studio/permit-lookup-desk-v1.webp` — full-bleed desk and countryside hero
- `src/main/resources/static/images/studio/permit-file-spread-v1.webp` — permit packet, plan, inspection sheet, correspondence and non-identifying property photographs
- Existing approved `checklist-case-house-v1.webp` and `checklist-closing-house-v1.webp` connect the investigation and inquiry sections without claiming that either depicts the customer property.

The two page-specific assets were generated with the built-in image tool using the approved mockup as the composition and mood reference. They contain no address, owner, parcel, agency, county, seal or permit number.

## Content preservation

The preview delegates to the existing `septic-permit-lookup` content model. It renders the source title, introduction, target reader, deep-dive paragraphs, fit signals, decision steps, no-result risks, preparation list, route drivers, FAQ answers and every currently rendered internal link. Only the exact national URL maps to the preview; regional permit-lookup URLs remain unchanged.

## Verification

- `StudioPreviewTest` and `StudioGuideRoutesTest` pass.
- Browser QA at 1440×900 and 375×812 found one H1, no horizontal overflow, no broken images, and no console errors.
- The selector contains all 50 states; selecting Alabama navigated to `/design-preview/studio/alabama/`.
- Six priority routes remain visible and eight additional routes remain server-rendered inside an expandable section.
- The mobile and desktop inquiry photography, buttons and footer were visually inspected after lazy loading.
- Screenshots are under `build/qa/permit-lookup/`.
