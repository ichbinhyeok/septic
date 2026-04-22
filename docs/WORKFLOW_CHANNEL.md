# SepticPath Workflow Channel

Last updated: 2026-04-16

## Why this file exists

This file is the SepticPath-specific operating document for workflow-channel
work.

Read this together with `B2B_LAYER_DEFINITION.md`.

Use it to keep three things separate:

- SEO search capture and surface alignment
- vendor workflow insertion
- later monetization

The workflow channel is not a page-promotion plan. It is a vendor labor-saving
plan whose downstream effect is repeatable distribution.

## Core thesis

SepticPath should not ask vendors to "use our site."

SepticPath should give vendors a sendable artifact that:

1. reduces repeated explanation work
2. narrows the customer into one correct workflow page
3. carries the recipient into county pages or official sources
4. keeps broad estimator or quote pages out of the first send

The artifact is the product. The page network sits behind it.

## What this channel is

- a public but `noindex` handoff layer
- built for email and direct sharing
- useful even if the recipient never becomes a lead
- measured by packet -> workflow-page -> county/official-source movement

## What this channel is not

- not a generic content-distribution plan
- not a social or community plan
- not a contractor landing-page program
- not a quote-funnel redesign
- not a replacement for organic search

## SepticPath-specific insertion model

The chain should be:

`vendor sendable artifact -> narrow state workflow page -> county page or official source`

The chain should not be:

`vendor email -> broad calculator -> quote form`

## Current front surfaces and packet targets

### Search-led canonical surfaces

- `/septic-records-checklist/{state}/`
- `/septic-permit-process/{state}/`
- `/buying-a-house-with-a-septic-system/{state}/`

### Workflow-channel V0 artifacts

- Indiana records packet
- New York buyer diligence packet
- South Carolina permit prep packet

These are public share pages but should stay `noindex` so they do not leak into
the search surface.

## Why these first artifacts

### Indiana

- strongest current records wedge
- county pages already exist
- state -> county chain is clear

### New York

- buyer-intent proof already exists
- the real bottleneck is file quality, Appendix 75-A, and waiver history
- buyer page already hands off into records

### South Carolina

- permit path is already a clean state-specific wedge
- D-1740 and permit-copy routing are concrete enough for a sendable packet

## Active decisions

- Keep V0 narrow. Do not publish all states at once.
- Keep V0 `noindex`.
- Keep first-send assets off the main nav.
- Treat AL and GA as V1 candidates after the state-first realignment cycle is
  observed.
- Do not put quote or estimator language at the center of the packet.

## V0 / V1 / V2

### V0: shareable packet pages

Goal:

- get real vendor-sendable assets live fast

Scope:

- public `noindex` packet pages
- share-ready email copy on each packet
- pinned first move fixed to one narrow state workflow page
- supporting links fixed to county pages or official sources
- nav-click tracking on packet -> internal workflow links

### V1: packet generator

Goal:

- let an operator generate the right packet from state and scenario without
  manual page picking

Scope:

- choose packet type
- choose state
- choose county when county wedge exists
- output one shareable packet URL and send note

### V2: reusable vendor workspace

Goal:

- make the packet reusable enough that vendors adopt it as part of their own
  workflow

Scope:

- branded or sender-specific wrapper
- reusable saved packet variants
- packet usage tracking by sender or campaign
- stronger measurement of repeated sends and repeat recipients

## Success conditions

V0 is working only if at least one of these becomes true:

- vendors reuse the same packet more than once
- packet clicks reliably flow into the pinned state workflow page
- Indiana packet clicks continue into county pages
- New York packet clicks continue into records
- South Carolina packet clicks continue into permit links

If packet views happen but the next internal clicks do not, the artifact is too
broad or the first move is still wrong.

## Guardrails

- Never let workflow packets become the primary SEO surface.
- Never lead with a national broad page.
- Never start with quote-first copy.
- Never expand states faster than packet quality can be defended.
- Keep county pages as wedges, not as SEO clutter.
