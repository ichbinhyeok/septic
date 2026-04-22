# SepticPath B2B Layer Definition

Last updated: 2026-04-19

## Why this file exists

This file defines the SepticPath-specific B2B layer.

Use it to keep four things separate:

- the existing search-led B2C surface
- the new B2B entry surface
- the sendable artifact layer
- later monetization

This is not a pricing document. It is the product-definition document for the
B2B layer that sits on top of the current septic project.

## Core product definition

SepticPath's B2B layer is:

`a vendor-office handoff layer for records, transfer, buyer diligence, and permit-prep communication`

The job is not to explain septic basics.

The job is to help a small inspector office, installer office, coordinator, or
transaction-side operator send one clear packet that explains:

1. why this contact is happening now
2. what file, office, permit, or inspection step matters next
3. what narrow page or official source should be opened first

## Problem framing

Why this layer exists:

- B2C search is useful, but it is slow.
- SepticPath already has strong records, transfer, permit, and county workflow
  content.
- That workflow value should not stay only in consumer search pages.
- It should also become a sendable office tool that can be pushed directly into
  active transfer and permit situations.

This means the near-term goal is **not** full monetization and **not** a full
vendor SaaS.

The near-term goal is:

- add a B2B entry beside the B2C entry
- ship real sendable packets
- verify whether offices and coordinators actually use them

The key test is workflow fit, not pricing sophistication.

## What stays true

The current B2C project already has real workflow value and should stay in
place.

Current B2C strengths:

- records-checklist surfaces
- transfer-compliance surfaces
- permit-process surfaces
- county-record pages
- estimator and quote flow

The B2B layer does **not** replace this.

The correct structure is:

`B2C entry + B2B entry + shared workflow pages + shared packet logic`

## Current project status

The existing project is already unusually close to this direction.

Current codebase realities:

- homepage already leads with records, transfer, permit, then estimator
- state workflow pages already exist
- county records pages already exist
- professional packet routes already exist in limited form

This means the project does **not** need a consumer rewrite before B2B work.

What stays:

- homepage
- records pages
- transfer pages
- permit pages
- county pages
- calculator
- quote flow

What gets added or sharpened:

- clearer B2B entry
- clearer packet selection
- stronger packet hierarchy
- cleaner mapping from packet -> workflow page -> county or official source

The B2B layer should sharpen what already exists, not restart the product.

## What this B2B layer is

- a second entry surface for professionals
- built for inspectors, installers, office coordinators, and buyer-side helpers
- centered on sendable packets, not broad educational articles
- narrow enough for email and repeat direct sharing
- useful before any setup fee or subscription exists

## What this B2B layer is not

- not a septic SaaS
- not a CRM
- not a county portal replacement
- not a quote-funnel redesign
- not a general contractor marketing site
- not a broad "for pros" content hub

## Current B2C front surface

The current public front surface is already workflow-heavy B2C.

Primary current surfaces:

- homepage
- records checklist pages
- transfer compliance pages
- permit process pages
- county records pages
- calculator
- quote request flow

This is good. The B2B layer should sit beside it, not replace it.

## B2B entry model

The new B2B structure should be:

`vendor entry page -> narrow packet type -> packet -> state workflow page -> county page or official source`

The wrong structure is:

`vendor page -> broad calculator -> quote form`

## Primary user

Primary user:

- office coordinator
- buyer-side coordinator
- septic inspector office
- installer office
- county-file helper on the vendor side

Secondary user:

- buyer agent or transaction helper who needs a reusable explanation

The B2B layer is not designed around a homeowner discovering the site through
search.

## Main workflow thesis

The strongest septic B2B angle is not "septic education."

It is:

`file and transfer friction reduction`

The B2B layer should help around moments like:

- records are missing
- the county office path is unclear
- the buyer needs a clean diligence summary
- the permit path needs to be explained before a quote is trusted

## Priority order

### Priority 1: Records-first transfer packet

This is the main wedge.

Use when:

- a sale or transfer is moving
- the septic story depends on records quality
- a seller summary or informal story is not enough
- someone needs to know which file or office matters first

Why it comes first:

- broadest reusable trigger
- strongest fit with existing records and county surfaces
- easiest outbound message
- cleanest bridge from B2B send into current B2C workflow pages

### Priority 2: Buyer diligence packet

This is the first supporting scenario.

Use when:

- the buyer side needs one calm explanation before closing
- the file, waiver, inspection, or county history is still unclear
- the problem is deal diligence, not a broad septic lesson

Why it is second:

- naturally follows the transfer context
- keeps the packet human and transaction-aware
- works especially well when records alone are not enough

### Priority 3: Permit prep packet

This is the third scenario.

Use when:

- the real blocker is permit routing
- the recipient needs to understand the permit path before install or
  replacement discussion
- the office keeps repeating the same permit-copy or local-office explanation

Why it is third:

- useful and real, but narrower than the transfer/records wedge
- more vendor-native than buyer-facing

## V1 implementation brief

This section is for fresh implementation agents.

### V1 objective

Ship the first usable B2B entry and first sendable packet without changing the
existing B2C front surface.

### What to implement first

1. a dedicated or sharpened B2B entry page
2. the priority-1 records-first transfer packet
3. a clean route from packet -> pinned workflow page -> county or official
   source
4. secondary scenario stubs for buyer diligence and permit prep

### Recommended first route shape

Use a clean B2B path such as:

- `/for-professionals/`
- `/for-professionals/records-transfer/`
- `/for-professionals/buyer-diligence/`
- `/for-professionals/permit-prep/`

Exact slugs can change, but the structure should stay packet-type first.

### First page rule

The first B2B page should answer:

- who this is for
- when to use it
- what problem it solves
- which packet type should be sent first

It should **not** act like a broad marketing page.

### First packet rule

The first packet should be:

- customer-facing
- easy to forward
- narrow to one records / transfer problem
- pinned to one existing workflow page
- intentionally kept away from broad quote or estimator language

### Existing codebase mapping

Use the current records checklist, transfer compliance, permit process, and
county records pages as the semantic source of truth.

Do not rebuild those pages. Reuse them behind the packet layer.

### Measurement expectation

Track at minimum:

- B2B entry page views
- packet-type selection
- packet open or generation
- packet -> workflow page click
- workflow page -> county or official-source click

### Indexing rule

The packet layer should start as public but `noindex` if there is any chance it
could leak ahead of the canonical B2C surfaces.

## Entry and surface rules

### Public search surfaces

Keep these as canonical B2C surfaces:

- `/septic-records-checklist/{state}/`
- `/buying-a-house-with-a-septic-system/{state}/`
- `/septic-permit-process/{state}/`
- county records pages
- calculator

### B2B surfaces

Create or sharpen a distinct B2B entry and packet layer:

- `/for-professionals/`
- packet-type selection surface
- public `noindex` share pages or generated packet outputs

### Packet rules

The packet is the product.

Each packet should do four things:

1. explain the trigger
2. point to the next narrow workflow page
3. keep broad estimator or quote pages out of the first send
4. route into county pages or official sources only after the packet has
   narrowed the problem

## Content rules

### Good B2B content

- records-first packet
- buyer diligence packet
- permit prep packet
- county-file handoff
- office-ready send note

### Bad B2B content

- what is a septic system
- generic maintenance explainers
- broad cost page as first send
- generic contractor pitch page
- broad homepage as the default handoff

## Message discipline

The first send should sound like:

- "start with this file path"
- "here is the county or workflow move that matters first"
- "do not trust the quote story yet"

The first send should not sound like:

- "use our site"
- "get quotes now"
- "run the estimator first"
- "learn septic basics"

## Success criteria

The B2B layer is working only if some of these become true:

- the packet type is easy to choose
- the packet is useful in real office communication
- recipients move into the pinned workflow page
- those recipients continue into county pages or official sources
- the records-first packet gets reused
- buyer-diligence and permit-prep work as real branches instead of duplicating
  the first packet

## Guardrails

- do not rebuild SepticPath around B2B
- do not demote the current B2C workflow pages
- do not make all three packet types equal-weight on day one
- do not lead from B2B into the calculator
- do not let packet pages become the primary SEO surface
- do not turn the layer into a fake team portal

## Not now

Do not build these in V1:

- login
- workspace accounts
- CRM features
- billing
- scheduling
- permissions
- county submission platform replacement
- vendor automation system
- full team operating portal

Those are later if and only if real usage proves the packet layer belongs in
daily office workflow.

## Initial implementation order

1. Create or sharpen the B2B entry page.
2. Put the records-first transfer packet in front.
3. Keep buyer diligence as the second branch.
4. Keep permit prep as the third branch.
5. Route each packet into one narrow next page.
6. Validate real use before expanding pricing or workspace logic.

## Definition of done for V1

The SepticPath B2B layer is defined enough for V1 only if all are true:

- the B2C front surface remains intact
- the B2B entry is separate and clear
- the main wedge is records-first transfer
- the packet is sendable, not just readable
- buyer diligence and permit prep are secondary branches
- the first-send flow routes into one narrow state workflow page instead of
  spilling into the whole site
