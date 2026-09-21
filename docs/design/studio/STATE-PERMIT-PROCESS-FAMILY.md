# State permit-process studio family

Status: implemented and browser-verified. Noindex preview only; not deployed.

## Direction

- Visual thesis: a calm rural property story paired with a precise permit desk; editorial, sequential and grounded in actual approval artifacts.
- Content plan: full-bleed regional hero, approving-path orientation, ordered decision steps, permit-file imagery, scope holds, delivered-work proof, county variation, preparation, sources, FAQ and inquiry.
- Interaction thesis: restrained hero entrance, expandable state and county detail, and strong row-level navigation without dashboard chrome.
- Every public state permit-process page keeps its distinct copy, workflow, official sources, county routes and FAQs.

## Reused image system

- Regional hero cohorts reuse the approved property imagery.
- `permit-lookup-desk-v1.webp` introduces the approving authority and first filing decision.
- `permit-file-spread-v1.webp` carries the permit-file narrative.
- Existing case and closing imagery connects the permit guide to SepticPath's research value and inquiry path.

## Verification

- All 50 state permit-process pages render through the premium family and retain their distinct workflow copy, steps, risk conditions, preparation checklist, FAQs, sources and internal routes.
- `StudioGuideRoutesTest`, `StudioPreviewTest` and `StudioCalculatorTest` pass: 39 targeted tests total.
- Texas, Oregon and Illinois cover all three regional hero cohorts.
- Texas desktop and Oregon 375 px mobile layouts have one H1, zero horizontal overflow, five successful images and no browser-console errors.
- The complete studio crawl now reaches 558 noindex preview routes with zero HTTP, H1 or noindex issues, including 50 state permit-process pages.
- Related state links now remain inside the studio journey rather than falling back to the legacy presentation.
- Evidence: `build/qa/state-permit-process-family/texas-desktop.png` and `build/qa/state-permit-process-family/oregon-mobile.png`.
