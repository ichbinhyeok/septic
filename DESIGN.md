# SepticPath product design direction

## Product promise

SepticPath is a public-records workflow, not a government-record database. Every page must state what the site can complete, what remains on an official system, and what the user should do after that handoff.

## Primary journey

1. Identify the correct state, county, and record owner.
2. Prepare the property identifiers the official source accepts.
3. Open the verified search or request route without implying that SepticPath retrieved a record.
4. Handle found, missing, blocked, and written no-record outcomes.
5. Move a returned document into interpretation, property diligence, or professional help only when the evidence supports that next step.

## Interface principles

- Lead with the task and the expected result, not editorial prose.
- Use one bounded workspace per page. Avoid card walls and repeated promotional panels.
- Put jurisdiction selection before property input when the official workflow is jurisdiction-first.
- Keep failure states beside the primary task so users can return after visiting an official site.
- Use image-led composition, generous spacing, strong typography, and compact operational labels. Evidence surfaces may remain document-like, but the surrounding experience should feel contemporary rather than archival.
- Keep mobile forms single-column with the first actionable field visible near the first viewport.
- Preserve keyboard focus, explicit labels, 44px minimum controls, and visible error text.

## Visual direction

SepticPath should feel like a premium research studio: calm, exact, and visually confident. The core palette is midnight ink, emerald, warm white, and pale aqua, with coral reserved for a small number of high-attention moments. Use Manrope for product typography and Newsreader only when an editorial voice materially helps.

The home page leads with one full-bleed property image and one real evidence package. Interior record pages may reuse a small, curated library of jurisdiction-neutral property images. Reuse is expected: vary crops, ordering, and supporting evidence rather than manufacturing a unique image for every county.

Generated or representative property imagery is atmospheric, not proof. It must not contain a real agency seal, invent a local document, or imply that the pictured property belongs to the jurisdiction named on the page. When a privacy-reviewed real source file exists, prefer it for evidence and label its provenance accurately.

Motion is limited to three jobs: establish the hero hierarchy, create depth while scrolling, and sharpen interactive affordance. Respect `prefers-reduced-motion` and never make motion necessary to understand or complete a task.

## Truth boundary

The interface may route, prepare, organize, and explain. It must not claim to search a government database, retrieve a permit, prove no record exists, certify capacity or condition, or submit a government request unless the underlying integration actually performs and verifies that action.

## Content standard

Use official agency and county sources for jurisdiction, required fields, document names, contact routes, and exceptions. Put transient failures such as a 403 response in fallback guidance, never in the main search intent or page title.
