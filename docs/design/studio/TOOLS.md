# Records workspace — isolated premium preview

Route: `/design-preview/studio/tools/`. Requires `app.studio-preview.enabled=true`; shared shell supplies noindex/nofollow. Production URLs, controllers and SEO metadata are unchanged. Guide hub records links now lead to this preview workspace.

## Design

Reference: `tools-reference-v1.webp`, generated with the built-in image tool. The implementation follows its photographic desk hero, ivory working area, sage explanatory column, editorial result rows, paper-like draft preview and photographic handoff. Existing approved `work-desk-v1.webp` and `research-room-v1.webp` are reused as illustrative imagery, not evidence of a particular property or completed search. Reference example addresses/results are not shipped.

Generation prompt (style reference: intake mockup):

> Use case ui-mockup. Premium shippable SepticPath property record finder and request drafting workspace, desktop full page. Supplied image is STYLE reference only: ivory/petrol/emerald, Newsreader and Manrope, large authentic photo. Header same brand links. Compact fullbleed 340px photograph of warm walnut desk with cream property-record folder right, dark left with heading 'Find the source. Make the next move.' Below ivory working area two underlined tabs 'Find the records route' and 'Draft a request'. Left 55% spacious form titled 'Start with the address.' labeled full property address, emerald 'Find the official route →', small Census lookup privacy text. Right 45% quiet sage aside with numbered 'County match / Official source / Next steps', explicit note 'A route is not a property record.' Below a calm result example titled 'Your next step starts here.' editorial rows Matched address / Responsible office / Route reviewed, button Open official source and secondary Draft a request. No fabricated permit facts or verification stamps, no fake official documents. Below full width photographic folder strip with two plain links 'Have a file already? / Want us to handle the research?'. Deep petrol spacious footer. Modern luxury editorial, very restrained no rounded cards or dashboard tiles. Include a small second lower editorial section showing request drafting concept with State, County, Address inputs beside readable letter preview and 'Copy request' button; note 'Draft only. Nothing is sent automatically.'

## Behavior and boundaries

- Uses existing `/api/address-record-finder`; displays returned route fields, source links and next steps. No invented property records.
- Invalid, not-found, unavailable, network error and timeout states remain recoverable. Editing the address cancels lookup and invalidates stale responses.
- Explicitly carrying an address populates state/county/address; when changing properties it clears previous parcel/owner identifiers.
- All 50 states, six document choices and six purposes; draft live preview, copy with manual fallback, plain-text download, clear.
- Drafts remain in page memory. No browser persistence, automatic agency submission, lead creation or payment action.
- This is a concise workspace, not feature parity with every legacy tool: original printable request packets, saved progress, follow-up tracking and the larger records workflow remain available at their original URLs. Do not retire those routes yet.

## Verification

### Workspace refinement

The frontend skill's composition-first approach informed the mobile hero: reuse the approved portrait `mobile-research-v1.webp`, with title above the folder rather than over its logo. A responsive picture and media-qualified preload avoid loading the desktop hero on mobile. Draft inputs now use two semantic fieldsets, with optional parcel/owner inputs in a native disclosure. Review/edit anchors connect the form and letter. The readonly letter grows to its full text height after edits, reset, font loading and width changes; copy/download behavior remains unchanged. Small-screen shared footer branding is sized down below 360px to prevent overflow.

Refinement verification: 24 focused Java tests passed, 19 browser fixture checks passed, including disclosure and long-letter sizing. Inspected 390px portrait hero and full letter plus 1440px draft spread. Resize checks include 320px. No real lead, agency or payment submissions.

- Focused Java preview/intake/disabled suite: 24 tests passed.
- Browser fixture checks: result rendering, unsafe URL rejection, external link isolation, carryover and identifier clearing, draft updates/reset, stale response prevention, not-found/network recovery, no persistence, no outbound lead/email/payment request and mobile overflow.
- One real invalid API request returned HTTP 400/status invalid (no geocoder call). Successful lookups use browser-only fixtures; no customer address or agency request was submitted.
- Desktop 1440×1000 and mobile 390×844 screenshots inspected under `build/premium-qa/studio/tools-*.png`. No broken images or horizontal overflow. No page console errors before intentional invalid-API check.
- Run `tools/qa-studio-tools.js` with browse eval, then await `window.studioToolsQa` with browse js. Reload after testing to remove fixture state.
