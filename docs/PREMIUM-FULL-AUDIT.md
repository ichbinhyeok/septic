# Full public-route design audit — 2026-09-20

Branch: `codex/premium-visual-redesign`. Checkpoint: `f5b1bf7`.

## Inventory and verification boundary

The local sitemap plus same-site public navigation yielded **744 HTML routes**, all HTTP 200 and all assigned a premium design family: 332 signature, 388 editorial, 9 regional, 9 directory, 5 tools, and 1 service.

This includes discoverable noindex pages; sitemap-only counts do not represent the whole public application. Internal reports, protected customer/payment states, and external government portals are not part of this public-route crawl.

`tools/audit-public-routes.js` discovers the routes without submitting forms. `tools/qa-all-public-pages.ps1` and `tools/audit-rendered-layout.js` render the returned HTML with its CSS in same-origin srcdoc frames at desktop 1440px and mobile 390px. This avoids the site's intentional frame-denial header without changing application security. It tests content layout, not top-level routing behavior or all interactive states. Direct browser navigation and screenshots are used separately for visual findings.

Artifacts are local under `build/premium-qa/full-audit/`. Inventory and rendered-layout results are machine-readable JSON. Intentional horizontal carousels and breadcrumb overflow are not page-width overflow. Empty upload-preview images without a source are not broken public assets.

## Audit status

Initial automated audit complete: 1,488 rendered screens; no page-width overflow, input under 90px (excluding radio/checkbox), nonempty broken-image URL, or load exception. All 744 routes include canonical metadata. 259 appear in the sitemap; 158 are marked noindex. Existing indexability is not changed by this design task. Visual fix verification remains in progress.

## Findings

- **F001 — High / layout:** State workflow actions inherited a two-column grid for three children, compressing buttons and wrapping `01` onto two lines. Replaced the card mosaic with three explicit editorial rows: index, readable body, action. Mobile moves the action below its own body. Commit `c79f49f`. Evidence: `board-before.png`, `board-after.png`. Desktop verified; final responsive pass pending.
- **F002 — High / hierarchy and contrast:** Baseline county pages placed the complete evidence comparison inside the left half of a workspace grid; the headline and original pages were squeezed while the right side sat empty. Moved the existing evidence section after the workspace at full container width. Preserved its data context, links, factual copy, and conditional rendering. Also corrected dark-on-dark secondary action and route eyebrow. Commit `29ec8e0`. Evidence: `county-before.png`; after capture pending.

Initial design grade: C (working hero direction, but inconsistent lower sections). Initial generic-template risk: C (dense inherited grid patterns). These are qualitative audit judgments, not measured customer outcomes.
