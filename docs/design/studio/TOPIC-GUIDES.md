# Topic guide previews

## Scope

Two audited editorial surfaces now have native studio presentations:

- `/design-preview/studio/topics/septic-permit-process/`
- `/design-preview/studio/topics/septic-inspection-cost/`

The preview controller allowlists these slugs. It delegates data preparation to
the existing content controller, not the legacy presentation. Production routes
and SEO metadata are unchanged. All previews remain feature-gated and noindex.
No deployment was performed.

## Design

Full-width photographic hero, shared header/footer, sticky reading navigation,
editorial text, preparation checklist, expandable FAQ and official sources.
Existing generated images provide atmosphere, not evidence. Real investigations
are linked early and presented with the case's actual location below the guide.
The design uses the studio article motion and reduced-motion behavior.

## Preservation

All curated intro/deep-dive copy, audience, fit bullets, decision steps, risk
conditions, drivers, checklist and FAQ remain available. State and related links,
official route rows, workflow coverage and source metadata remain available.
The guide hub points its permits and inspections links at these previews.
State-specific topic pages and the calculator retain their original destinations;
they have not been represented as migrated. Other topic slugs intentionally 404
in this preview family until their custom functionality is audited.

## Verification

- Studio suite: 31 tests, zero failures/errors, including content/link/source parity
  for both topic previews and unchanged public views.
- Desktop permits: 1440 × 1000 screenshot inspected.
- Mobile inspections: 390 × 844 hero and research section inspected.
- Mobile table of contents opens and all six anchor destinations exist.
- State expansion reveals all 50 links.
- No horizontal overflow or browser console errors; all images decode.
- No intake submission, purchase, external message or deployment.

Next remaining implementation: calculator interface with complete input/result/error
and downstream quote-flow parity; further topic-specific and state-topic views.
