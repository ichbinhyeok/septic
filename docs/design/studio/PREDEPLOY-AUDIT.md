# Studio pre-deployment audit — 2026-09-20

Status: DONE_WITH_CONCERNS. Preview only; not a release approval.

## Verified

- 452 discoverable studio HTML routes: HTTP 200, one H1, noindex.
- 904 rendered layouts at 1440px and 390px: no page-width overflow,
  narrow visible inputs, load exceptions, or detected broken image URLs.
  This uses same-origin srcdoc frames, not 904 manual visual reviews.
- 43 distinct local image/srcset/script/stylesheet URLs referenced by those
  pages respond successfully to HEAD. This is not an image-quality assessment
  or a scan of every CSS background URL.
- 744 existing public HTML routes recrawled after fixes: HTTP 200, one H1,
  canonical present. URL set, title, canonical, robots and sitemap membership
  match the earlier local inventory exactly.
- 45 studio tests pass, including public request-builder heading regression
  and all state-buyer editorial content / mapped link expectations.

## Fixed

1. All 50 state-buyer planning actions now pass through the exact-equivalent
   preview route mapper. State, project and source-page query values survive.
   Direct mobile navigation from Alabama to the studio calculator was verified.
2. A hidden task-context heading in the public request builder was a second H1.
   It is now H2; its content and JavaScript data selector remain intact.
3. Layout audit now checkpoints each batch to disk and reloads between batches.
   An initial in-memory run lost browser state after 848 screens; the complete
   checkpointed rerun, not that partial run, supplies the 904-screen result.

## Not complete / release gates

- Studio links still reference 279 distinct non-studio destinations. These are
  NOT 279 confirmed missing designs: some are original evidence accounts or
  routes with equivalents whose links have not been mapped. Others genuinely
  lack a studio implementation. Do not redirect regional articles to generic
  guides merely to remove this count.
- Prioritize the county directory / record-checklist / permit-lookup families,
  then regional inspection, permit, replacement and cost articles. Inventory
  lists every outgoing destination and referencing-page count.
- Public routes still use existing production templates. The studio shell is
  intentionally noindex,nofollow and has preview titles without canonicals.
  Do not deploy it over public routes unchanged. An explicit route-by-route
  migration must preserve existing metadata and content obligations.
- Metadata equality is against a saved LOCAL checkpoint, not a current
  production crawl or evidence that search rankings will remain unchanged.
- No live analytics, customer submissions, external portal checks or deployment.
  Protected/customer/payment states are outside this public-page audit.
- Full aesthetic modernization is not established by passing layout checks.

## Reproduction and artifacts

- tools/qa-studio-all-layouts.ps1
- tools/audit-studio-assets.js
- tools/audit-public-seo.js
- build/premium-qa/studio-audit/inventory.json (pre-fix outgoing-link snapshot)
- build/premium-qa/studio-audit/rendered.json
- build/premium-qa/studio-audit/buyer-steps-after.png
- build/premium-qa/compare-seo.js (comparison against saved local inventory)

The national calculator outgoing source-page count fell from 51 to 1 after the
state-buyer fix. Destination count remains 279, because another source still
links there. This is an explicit remaining link-cleanup item.
