# State records studio family

Status: implemented and browser-verified. Noindex preview only; not deployed.

## Direction

- Visual thesis: a quiet editorial property journal paired with an official-records research desk; photographic, regional and evidence-led rather than dashboard-like.
- Content plan: full-bleed property hero, state route orientation, county routes, file categories, delivered-work proof, research method, state-specific depth, request preparation, sources, FAQ and inquiry.
- Interaction thesis: restrained hero entrance, live county filtering, expandable workflow and source detail, and row-level route affordances.
- All 50 public state checklist pages use one premium presentation while retaining their distinct editorial copy, state facts, county links, official sources and FAQs.

## Image reuse system

- Mountain, forest and northern states use `property-hero-v1.webp`.
- Southern, plains and pasture states use `oak-pasture-v1.webp`.
- Remaining states use `county-property-v1.webp`.
- `official-lookup-map-v1.webp`, `file-dividers-v1.webp`, `checklist-case-house-v1.webp` and `checklist-closing-house-v1.webp` provide a consistent research narrative below the fold.

Reuse is intentional. Regional atmosphere changes at the hero while the recurring evidence imagery acts like a recognizable visual system. Every reused record visual remains explicitly illustrative.

## Verification

- All 50 published state-records pages render through the premium family and retain their state-specific copy, workflow data, source URLs, FAQs and internal links.
- `StudioGuideRoutesTest` and the complete `StudioPreviewTest` suite pass: 32 targeted tests total.
- Alabama, North Carolina and California verify all three hero-image cohorts.
- Alabama desktop and North Carolina 375 px mobile layouts have zero horizontal overflow, one page H1, five loaded images and no console errors.
- County discovery shows eight priority routes initially, searches the complete set, expands and collapses the full list, and preserves direct county-guide links.
- The records checklist hub links all 50 state guides, from Alabama through Wyoming.
- The complete studio crawl now reaches 508 noindex preview routes with zero HTTP, H1 or noindex issues.
- Evidence: `build/qa/state-records-family/alabama-desktop.png` and `build/qa/state-records-family/north-carolina-mobile.png`.
