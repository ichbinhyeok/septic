# State cost guide family

## Visual thesis

The state cost page is a planning editorial, not a cloned calculator and not a generic state directory. It opens with a strong property image and an intentionally small planning ticket, then explains the three forces that move the range: parcel, system, and approval path. Reused imagery is treated as a shared visual library rather than location evidence; climate groups rotate the hero while research, real-work, and closing imagery each serve a different narrative role.

## Content contract

- Preserve the public state guide's official sources, local-authority notes, verified rule facts, planning snapshot, related state guides, and county routes.
- Never describe the planning snapshot as an official fee schedule, contractor quote, or parcel-specific result.
- Keep the route to the interactive calculator clear, but do not reproduce its form controls on the editorial page.
- Reuse strong images across states. The imagery is illustrative and must not claim to depict a named county or property.
- Show SepticPath's research value before the local directory so the page communicates a capability, not only navigation.

## Preview route

`/design-preview/studio/{stateSlug}/`

The production-equivalent route `/septic-system-cost-calculator/{stateSlug}/` maps to the preview family through `StudioGuideRoutes`.

## Verification

- All 50 published state guides render with their distinct state, source, calculator, and local-route context.
- The complete discoverable studio crawl covers 608 preview pages with zero HTTP, H1, or noindex failures.
- The asset audit covers 71 local assets with zero missing responses.
- Texas, Oregon, and Florida were rendered at 1440px and 390px; all six layouts have one visible H1, no horizontal overflow, no broken images, and no undersized inputs.
- Oregon verifies the richer cost-profile cohort with the planning ticket and four-line cost ledger. Texas verifies the conservative cohort without invented state cost figures.
- Visual references: `build/qa/state-cost-family/texas-desktop.png`, `build/qa/state-cost-family/texas-mobile.png`, `build/qa/state-cost-family/oregon-desktop.png`, and `build/qa/state-cost-family/oregon-mobile-viewport.png`.
