# Cost calculator preview

## Input control refinement

Follow-up: borderless primary controls were not clearly recognizable as inputs.
Restored a warm light fill, visible 1px outline and 7px corners to primary controls,
with 56px minimum height, retained editorial type, native behavior and distinct
hover/focus states. Removed decorative separators between these controls. The
button shares their height and corner geometry. Verified desktop/mobile screenshots,
state selection, bedroom editing, successful calculation and no console errors.
Current screenshots: `calculator-input-affordance-*`.

Removed individual white boxes and full outlines from the calculator controls.
Primary values use editorial typography with subtle column separators on desktop
and ruled rows on mobile. Native select/number behavior and focus outlines remain.
The action uses a restrained dark-green surface, 4px corners and reduced-motion-safe
hover feedback. Secondary and quote controls use underlines instead of boxed fields.
Verified 1440px/390px rendering, native selection and calculation, browser assertions,
no horizontal overflow, and no console errors. Screenshots: `calculator-controls-*`.

## Approved mockup revision (v2)

The initial adjacent-photo form was rejected. Rebuilt around the approved image:
full-bleed landscape, ivory horizontal input console, editorial research story,
and a photograph-led deep-petrol report. Results appear before the preserved input
form; a native details editor plus progressive enhancement allows revisions.
The form, calculations, explanations and quote contracts remain unchanged.

New built-in imagegen asset:
`src/main/resources/static/images/studio/calculator-landscape-v2.webp`.
Reference: `exec-cecaeeac-7504-4746-8bde-fba4793b2ddb.png` in the thread's generated-images directory.
Final generation prompt: Generate a single pure architectural landscape photograph
matching the rural house photograph in the reference website mockup. Reference is
for photographic scene/style only. Wide 3:2 or 16:9 photograph, no text, UI, panels,
lettering, border or collage. Modern rural American house with dark timber, steep
standing-seam metal roof and stone chimney on the right, wooded mountain behind,
early morning valley mist, golden side light, mature oak framing left/top, meadow
and boulders in foreground. Calm dark forest shadows on the left for HTML text.
Refined photorealistic natural materials, muted olive meadow, deep petrol shadows.
House at 65–80% horizontal position, fully visible, modest elegant architecture.
Fill the canvas with one landscape photograph.

This is atmospheric generated imagery, not a customer property or agency evidence.
Desktop/mobile initial and result screenshots reviewed; edit inputs opens the
editor and retains state, occupancy and checkbox values. Browser assertions pass
with no overflow or console errors. Screenshot prefix: `calculator-v2-*`.

Path: `/design-preview/studio/calculator/`

Native studio workspace and result presentation. Uses the existing calculator,
estimator, quote validation and storage logic through the feature-gated preview
controller. Production routes, calculations and SEO metadata are unchanged.
No deployment or real inquiry submission performed.

## Design

Direct-entry editorial workspace, adjacent atmospheric property photograph,
optional site-detail disclosure, and full-width sage result section. Cost range
is the dominant result, followed by scenarios, precision/assumptions, all result
explanations, official-source context, cost evidence and optional follow-up.
Shared studio header/footer, native controls, responsive layout and reduced motion.
Input and quote control contracts are preserved independently of legacy page CSS.

## Functional parity

- All seven project types, state selection, bedrooms, occupancy, soil, access,
  timeline, disposal, kitchen/ADU and water-table inputs retained.
- GET prefills and bounded records context use the existing controller behavior.
- No new client-side calculations or invented prices.
- Invalid state messaging and malformed-input handling retained.
- All EstimatorResult explanatory lists, source labels and cost evidence retained.
- Alabama fee boundaries and drain-field-specific onward route retained.
- Quote-only mode, symptom warning, validation, confirmation and storage route
  attribution retained. Preview POST routes return to studio presentation.
- Generic guide-hub and national buyer-guide estimator links now open studio.
- Tank-capacity, pumping-schedule, and drain-field planning now share the main
  calculator workspace while retaining their distinct inputs and result logic.
- Legacy standalone GET routes permanently redirect to the matching main-tool
  mode; cached legacy POST forms still render a valid integrated result.
- Preview shell intentionally does not enable production analytics scripts.

## Verification

### Calculator-to-records inquiry handoff

The records CTA now carries only state, allowlisted project type and planning
bedrooms in its link. Intake prefills the state and editable concern, shows a
compact continuity note and retains calculator attribution. Bedroom inputs are
explicitly unverified planning inputs, never listing or permit bedroom facts.
Address and contact information are not carried in URLs. Unknown project values
are ignored and bedroom context is bounded to 1–20. All seven project types and
validation-error edit retention are covered by tests. The original construction
service inquiry remains separate. Frontend composition keeps the existing imagery
and uses a ruled note rather than another card.

39 studio tests pass. Mobile end-to-end calculation-to-intake navigation confirms
state/project/bedroom transfer with no overflow or console errors. No live inquiry
was submitted and no deployment was performed.

### Result lower-half revision

The approved lower-half mockup is implemented as three ruled explanatory
disclosures, a separate calculation/source disclosure, a photograph-led Roane
County investigation, and a deep-petrol records inquiry with the generated folio
image. All existing calculation details remain available. The case describes
the public curated research sequence, not invented outcomes or local availability.
Atmospheric imagery is distinguished from customer-property evidence.

Project service follow-up is a separate native disclosure. It opens for quote-only
entry, validation errors, confirmation, or the service link; no real inquiry was
submitted. Existing input styling and shared header/footer are unchanged.

Desktop 1440px and mobile 390px were visually inspected. Browser checks confirm
no overflow, source disclosure access, and service-link expansion. The 37 studio
tests pass, including assertions for the new result-to-case-to-inquiry sequence.
Preview only; production routes and SEO metadata are unchanged. No deployment.

37 studio tests passed, including six calculator tests: initial/prefilled context,
seven project result comparisons with the original, all 50 states, invalid input,
quote validation, and mocked successful quote storage. No actual lead was saved.

Browser QA covers desktop 1440px and mobile 390px, native form submission to the
local calculation endpoint, optional-field retention, result anchors, quote form
presence, no overflow, and no browser console errors. Screenshots are under
`build/premium-qa/studio/calculator-*`. `tools/qa-studio-calculator.js` provides
repeatable read-only checks. The quote form is not submitted in browser QA.

## Integrated capacity and maintenance modes — 2026-09-21

`mode=tank_size` uses the generated property landscape, state, bedrooms,
household-use profile, disposal, and additional-kitchen inputs. Its result leads
with minimum and conservative gallon bands, then shows official context, the
household adjustment, maintenance cadence, and a property-file handoff.

`mode=pump_schedule` uses the garden property image, tank capacity, occupants,
use profile, and disposal load. Its result leads with the cadence and inspection
rhythm while stating that actual condition and local O&M rules can override the
calendar. It does not require a state to produce the maintenance result.

Both modes are cardless, image-led, share the studio shell, use native controls,
and include reduced-motion behavior. Playwright verified desktop and 390px mobile
inputs and submitted results with no horizontal overflow. Screenshots are under
`output/playwright/tank-*` and `output/playwright/pump-*`. No deployment occurred.
