# Organic Tracker

Last updated: 2026-06-28

## Why this file exists

This file is the operating log for organic search work after launch.

Use it to track:

- what Search Console data said on a specific date
- what was changed in the product or content
- what we learned from that data
- what to check next and when

Workflow-channel work is tracked separately in `docs/WORKFLOW_TRACKER.md` so
search decisions do not get mixed with vendor-send experiments.

This is not a brainstorming doc. Each entry should leave a clear audit trail from
data -> change -> insight -> next action.

## How to use this file

For every meaningful SEO check-in, add one dated entry with:

1. Data window and source
2. What changed in search behavior
3. What we shipped
4. What we think it means
5. What we will check next

Rules:

- Always use absolute dates.
- For Search Console, note when data is still fresh or partially delayed.
- Separate observations from interpretations.
- Reference shipped commits when work is pushed.
- Do not mark a page family as a failure before at least 4 to 8 weeks unless
  there is a technical issue.

## Core Metrics To Watch

- Site-level clicks
- Site-level impressions
- Site-level CTR
- Site-level average position
- Indexing status of priority URLs
- Sitemap submitted vs indexed trend
- Page-level performance for:
  - `/perc-test-cost/`
  - `/septic-permit-process/`
  - `/septic-records-checklist/`
  - `/septic-system-cost-calculator/alabama/`
  - `/septic-system-cost-calculator/georgia/`
- Query-level movement for:
  - `perc test cost`
  - `percolation test cost`
  - `perc test cost near me`
  - `septic tank permit`
  - `septic permits`
  - `how much does a septic permit cost`

## Current Hypotheses

- The site is in early indexing, not decline.
- Early traction is strongest on `perc-test-cost` and state pages that satisfy
  permit or records intent.
- Alabama and Georgia state guides are being interpreted more as permit or
  records pages than generic calculator pages.
- Internal editorial links should prefer clean canonical paths over calculator
  query-string URLs.
- `drain-field-replacement-cost` needs deeper content, not just better metadata.

## 2026-06-28

### 3-week 100-click sprint update

- Goal: raise the probability of reaching 100 organic clicks/day within 3 weeks
  by expanding the already-visible records/permit lookup surface. This is not a
  guaranteed outcome, but it is the highest-probability site-side move from the
  current data.
- Search Console signal used:
  - last complete 28-day window ending 2026-06-25: 138 clicks, 9,809
    impressions, 1.41% CTR, average position 10.15
  - last 7 days ending 2026-06-25: 60 clicks, 2,713 impressions, 2.21% CTR,
    average position 10.02
  - fastest opportunity: Tennessee records queries already near page one,
    including `septic permit lookup`, `tennessee septic records`, `tdec septic
    records`, and `state of tn septic records`
- What shipped in this sprint update:
  - added `/septic-permit-lookup/` as a national state-first doorway for permit
    lookup, records search, county files, as-builts, and inspection-letter
    queries
  - wired that hub to both records and permit state pages instead of creating a
    thin duplicate content family
  - promoted TN, NC, TX, AL, and IN in the state-page ranking and home growth
    spotlights because those are current lookup/records opportunity states
  - expanded Tennessee county records coverage from 3 to 18 county pages, with
    TDEC SSDS record search and regional/contract county routing as the shared
    official-source backbone
  - added the new hub to header, footer, home, state coverage, related links,
    sitemap, and regression tests
- Follow-up check:
  - after deploy, inspect Google Search Console on or after 2026-07-06 for
    index discovery and early impressions on `/septic-permit-lookup/`
  - on or after 2026-07-19, compare Tennessee records CTR and total clicks
    against the 2026-06-28 baseline

### 3-week 100-click sprint second expansion

- Goal: turn `/septic-permit-lookup/` from a state-only doorway into a county
  lookup launchpad so high-intent users can move directly into county records
  pages when the county is already known.
- What shipped:
  - added a dedicated county lookup launchpad section to `/septic-permit-lookup/`
    with high-opportunity county routes across TN, NC, TX, AL, and IN
  - expanded county records coverage from 259 to 273 pages
  - added 26 official source registry entries for TX, AL, and IN county pages
  - added new official-source-backed county pages for:
    - Texas: Montgomery, Fort Bend, Brazoria, Bell, El Paso
    - Alabama: Shelby, Tuscaloosa, Lee, Limestone, Morgan
    - Indiana: St. Joseph, Porter, Marshall, Huntington
  - reweighted county ordering so state records pages and professional packets
    show the highest-opportunity lookup counties first without losing existing
    proven handoff pages
- Quality bar:
  - each new county page has its own first official path, first artifact to pull,
    low-end quote breakers, FAQ, and workflow structure
  - no new page relies only on a generic statewide source
- Follow-up check:
  - after deploy, confirm sitemap discovery for the new county URLs
  - on or after 2026-07-19, compare new county URL impressions and query
    coverage against the `/septic-permit-lookup/` launch baseline

### Data

- Source: Google Search Console MCP for `sc-domain:septicpath.com`
- Compare window:
  - current: 2026-05-28 to 2026-06-25
  - prior estimate from 56-day aggregate: 2026-04-30 to 2026-05-27
- Site summary:
  - current: 138 clicks, 9,809 impressions, 1.41% CTR, average position 10.15
  - prior estimate: 49 clicks, 4,969 impressions, 0.99% CTR, average position 11.87
  - change: clicks up about 182%, impressions up about 97%, CTR up about 43%
- Short read:
  - 2026-06-18 to 2026-06-25: 60 clicks, 2,713 impressions, 2.21% CTR,
    average position 10.02
- Main page signals:
  - `/septic-system-cost-calculator/alabama/`: 35 clicks, 2,232 impressions
  - `/septic-records-checklist/indiana/`: 17 clicks, 300 impressions, 5.67% CTR
  - `/septic-records-checklist/tennessee/`: 6 clicks, 1,043 impressions,
    0.58% CTR
- Query opportunity:
  - Tennessee records page is visible for `septic permit lookup`,
    `tdec septic records`, and `septic record search`, but CTR is still weak.

### Changes Shipped

- Commit: not committed yet in this tracker entry
- Files:
  - `data/raw/content_pages.json`
  - `data/raw/state_money_pages.json`
  - `src/main/java/com/example/septic/service/SeoService.java`
  - `src/main/java/com/example/septic/web/SiteController.java`
  - `src/main/java/com/example/septic/data/model/StateMoneyPage.java`
  - `src/main/jte/layouts/app.jte`
  - `src/main/jte/pages/home.jte`
  - `src/main/jte/pages/state-coverage.jte`
  - `src/test/java/com/example/septic/SepticApplicationTests.java`
- What changed:
  - shifted the national records page from `records checklist` framing toward
    `septic records lookup`, `septic permit lookup`, and `property record
    search` language
  - retitled the Tennessee records page around `Tennessee Septic Records
    Checklist and Permit Lookup`
  - added TDEC SSDS record search, permit-search result, and septic permit
    lookup language to Tennessee copy and FAQ
  - changed records CTA labels toward `records lookup` and county record lookup
    paths while keeping existing URLs stable
  - expanded the same lookup-intent framing across all 50 state records pages
    by moving titles toward `Records Checklist and Permit Lookup`, metadata
    toward `records lookup and permit search`, and body/FAQ copy toward permit
    search, county file, and official office routing
  - added an explicit state-guide records shortcut such as `Open Alabama records
    lookup` inside the state calculator bridge so high-impression state guides
    can push searchers into the records path before the calculator or quote
    flow

### Insights

- Observation: records and permit lookup language is the clearest current
  search expansion surface.
- Interpretation: `checklist` is still useful as product language, but `lookup`
  and `search` should carry the snippet and CTA layer where Search Console is
  already showing those queries.
- Observation: Tennessee has high impressions but weak CTR relative to page-one
  and near-page-one visibility.
- Interpretation: Tennessee is the fastest CTR test because the page already
  earns visibility; the issue is matching the visible search promise more
  directly.

### QA Check

- `./gradlew.bat test` passed.
- Local render check passed on:
  - `http://127.0.0.1:8080/septic-records-checklist/`
  - `http://127.0.0.1:8080/septic-records-checklist/tennessee/`
  - `http://127.0.0.1:8080/`
- Follow-up full-suite check after the 50-state records and Alabama shortcut
  expansion also passed with `./gradlew.bat test`.

### Next Actions

- After deploy and indexing, recheck 2026-07-06 or later for:
  - Tennessee records CTR on `septic permit lookup`, `tdec septic records`, and
    `septic record search`
  - whether national records page impressions shift toward lookup/search
    variants
  - whether county record lookup CTA clicks increase from Tennessee and other
    records pages
  - whether Alabama guide visitors use the records lookup shortcut before
    returning to estimate or quote flow

## 2026-04-23

### Data

- Source: internal execution follow-up after the 2026-04-22 Search Console read
- Fresh-data note: no new Search Console pull was used in this entry; this is a
  shipped-change log that locks the product state after the 2026-04-22
  interpretation.
- Page families touched:
  - `/septic-records-checklist/`
  - `/septic-permit-process/`
  - `/perc-test-cost/`
  - `/septic-inspection-cost/`
  - state guide surface
  - state money pages and workflow packets

### Changes Shipped

- Commit: `f9e7a41`
- Files:
  - `src/main/jte/pages/content-page.jte`
  - `src/main/resources/static/app.js`
  - `src/main/resources/static/app.css`
  - `src/main/jte/pages/state-guide.jte`
  - `src/main/java/com/example/septic/web/SiteController.java`
  - `src/main/java/com/example/septic/web/StateSurfaceSignalView.java`
  - `src/test/java/com/example/septic/SepticApplicationTests.java`
- What changed:
  - converted broad organic parents into a clearer state-aware router instead of
    leaving them as long national explainers
  - made state selection change not only CTA targets but also visible route
    judgment through `workflow fit`, `evidence depth`, and `tool handoff`
    signals
  - shortened state-specific route labels on broad parents so the chosen next
    move reads like a workflow action instead of a long editorial title
  - tightened the state-guide action rail around workflow-first and
    estimate-followup handoff
  - locked workflow packet pages, sitemap exclusion, and render tests into the
    shipped branch

### Insights

- Observation: the product surface now matches the 2026-04-22 interpretation
  more closely than it did before this push.
- Interpretation: the national pages are less likely to be read as final
  information destinations and more likely to act as a routing layer into the
  narrower state workflow or tool.
- Observation: state-aware route labels and decision signals reduce the amount
  of editorial wording inside the first-action block.
- Interpretation: this should help the broad parents behave more like a tool
  surface without deleting the supporting explanation that prevents thin
  content.
- Observation: workflow packet pages, state surfaces, and tests are now aligned
  in one pushed commit.
- Interpretation: the next read should focus on whether these surface changes
  change click distribution, not on whether the implementation shipped
  incompletely.

### Next Actions

- Recheck Search Console on 2026-04-29 or later so the 2026-04-23 ship has time
  to be reflected in impressions and click distribution.
- Watch whether `/septic-records-checklist/` and `/septic-permit-process/`
  start sending more visibility into state-specific records and permit pages
  instead of keeping it trapped on the national parent.
- Watch whether state-guide and state-money click paths show better handoff into
  the workflow pages and calculator after the shorter route labels.

## 2026-04-16

### Data

- Source: Google Search Console MCP for `sc-domain:septicpath.com`
- Compare window:
  - current: 2026-03-16 to 2026-04-13
  - prior: 2026-02-17 to 2026-03-15
- Fresh-data note: the newest few Search Console days can still move, but this
  window is already stable enough for page-family decisions.
- Site summary:
  - clicks: 22 vs 2 before
  - impressions: 5,211 vs 713 before
  - CTR: 0.42% vs 0.28% before
  - average position: 9.42 vs 11.89 before
- Priority page movement:
  - `/septic-records-checklist/indiana/`: 7 clicks, 330 impressions, 2.12% CTR,
    average position 6.58
  - `/septic-system-cost-calculator/alabama/`: 6 clicks, 948 impressions,
    average position 8.04, with visible perc, permit, and record-file intent
  - `/septic-system-cost-calculator/georgia/`: 2 clicks, 567 impressions,
    average position 7.82, with permit-cost and state-specific perc intent
  - `/buying-a-house-with-a-septic-system/new-york/`: 1 click, 155 impressions,
    average position 6.46
  - `/septic-permit-process/south-carolina/`: 1 click, 128 impressions,
    average position 12.09
  - `/perc-test-cost/`: 0 clicks, 406 impressions, average position 10.64
  - `/failed-perc-test-septic/`: 0 clicks, 149 impressions, average position
    13.28
  - `/drain-field-replacement-cost/`: 0 clicks, 61 impressions, average
    position 40.57
- Query movement:
  - Alabama guide is earning impressions for:
    - `perc test cost alabama`
    - `how much is a perc test in alabama`
    - `septic tank permit records alabama`
  - Georgia guide is earning impressions for:
    - `how much is a perc test in georgia`
    - `septic tank records georgia`
    - `septic permit cost`
  - South Carolina permit page is earning impressions for:
    - `septic permitting south carolina`
    - `septic system permit requirements south carolina`
    - `site evaluation for septic permit sc`

### Changes Shipped

- Commit: not committed yet in this tracker entry
- Files:
  - `src/main/java/com/example/septic/web/SiteController.java`
  - `src/main/jte/pages/state-money-page.jte`
  - `src/test/java/com/example/septic/SepticApplicationTests.java`
- What changed:
  - kept home workflow-first ordering intact
  - moved `/perc-test-cost/` to a state-first handoff so the national page
    pushes visitors into state-specific perc pages before the estimator
  - boosted county-records-backed records pages inside state guides so states
    like Indiana, Alabama, and Georgia hand off to the narrower record surface
    first
  - boosted state-specific examples on national hubs where Search Console
    already shows real proof: Indiana records, Alabama and Georgia perc/records,
    New York buyer, and South Carolina permit
  - fixed state-specific primary next action for Alabama, Georgia, Indiana, New
    York, and South Carolina so the canonical page stops leading with the
    estimator when the real next move is county pages, a narrower records page,
    or official permit/file links
  - sharpened `/failed-perc-test-septic/` and
    `/drain-field-replacement-cost/` so the national pages act more like
    sorting pages with state-specific proof ahead of the estimate, instead of
    standing in front like final broad answers

### Insights

- Observation: this window is not a site-level decline. Search visibility,
  clicks, and average position all improved versus the prior 28-day period.
- Interpretation: the main problem is surface fit, not discovery failure.
- Observation: Indiana records is the clearest proof-of-fit page in the current
  set.
- Interpretation: county-backed records workflows are a real wedge, not a side
  support page.
- Observation: Alabama and Georgia state guides are ranking for perc, permit,
  and records language at the same time.
- Interpretation: that is useful only if the guide quickly hands off to the
  narrower state workflow page instead of acting like the final answer itself.
- Observation: `failed-perc-test-septic` and `drain-field-replacement-cost`
  still have little click proof and weak ranking depth.
- Interpretation: keep them indexed, but do not let them lead the surface until
  they earn narrower proof.

### Next Actions

- Recheck on 2026-04-30 whether `/perc-test-cost/` starts shifting clicks into
  Alabama and Georgia state-specific perc pages.
- Watch whether Indiana records and county-backed records pages keep producing
  the best click efficiency in the current footprint.
- Watch whether New York buyer and South Carolina permit pages hold page-one
  average positions and turn more impressions into clicks.

## 2026-04-22

### Data

- Source: Google Search Console MCP for `sc-domain:septicpath.com`
- Compare window:
  - current: 2026-03-23 to 2026-04-19
  - prior: 2026-02-23 to 2026-03-22
- Fresh-data note: 2026-04-20 to 2026-04-22 was excluded on purpose to avoid
  normal Search Console lag. This read is stable enough for page-family and
  query-fit decisions.
- Site summary:
  - clicks: 24 vs 6 before
  - impressions: 5,126 vs 1,795 before
  - CTR: 0.47% vs 0.33% before
  - average position: 9.07 vs 11.32 before
- Stable week-over-week read:
  - 2026-04-13 to 2026-04-19: 6 clicks, 1,336 impressions, 0.45% CTR,
    average position 8.48
  - 2026-04-06 to 2026-04-12: 1 click, 1,322 impressions, 0.08% CTR,
    average position 9.10
- Priority page movement:
  - `/septic-system-cost-calculator/alabama/`: 10 clicks, 984 impressions,
    1.02% CTR, average position 8.04
  - `/septic-system-cost-calculator/georgia/`: 2 clicks, 547 impressions,
    0.37% CTR, average position 7.76
  - `/perc-test-cost/`: 0 clicks, 159 impressions, average position 7.84
    versus 432 impressions and average position 11.44 before
  - `/septic-permit-process/`: 0 clicks, 123 impressions, average position
    5.20 versus 66 impressions before
  - `/septic-records-checklist/`: 0 clicks, 168 impressions, average position
    7.65 versus 70 impressions before
- Page-family movement:
  - `septic-records-checklist` family rose from roughly 2 clicks / 208
    impressions to roughly 7 clicks / 902 impressions, led again by
    `/septic-records-checklist/indiana/`
  - `septic-permit-process` family rose from roughly 0 clicks / 129
    impressions to roughly 3 clicks / 631 impressions, with South Carolina,
    Rhode Island, and Nebraska now carrying the visible click proof
  - `perc-test-cost` family fell from roughly 451 impressions to roughly 314
    impressions overall, but the national parent improved in average position
    and West Virginia picked up a new 144-impression state wedge
- Query movement for the tracked exact set:
  - total clicks across the exact tracked queries were still 0
  - `septic tank permit`: 3 impressions at average position 6.67 versus 2
    impressions at 10.00 before
  - `how much does a septic permit cost`: 1 impression at average position
    4.00 versus 1 impression at 9.00 before
  - `perc test cost`: 6 impressions at average position 38.83 versus 23
    impressions at 39.39 before
  - `perc test cost near me`: no exact impressions in this window after 2
    impressions in the prior one
- Query-to-page mapping that mattered:
  - the exact permit-intent tracked queries that did fire mapped to
    `/septic-system-cost-calculator/alabama/`
  - the exact `perc test cost` query still mapped primarily to
    `/perc-test-cost/`, with only a small spill into the Alabama guide
- Technical check:
  - inspected priority URLs for `/perc-test-cost/`, `/septic-permit-process/`,
    `/septic-records-checklist/`, and the Alabama and Georgia guides all
    returned `Submitted and indexed`
  - user canonical and Google canonical matched on all five inspected URLs
  - FAQ and breadcrumb rich-result detection passed on all five inspected URLs
  - workflow packet URLs for Indiana, New York, and South Carolina were still
    `URL is unknown to Google`, which is consistent with the intended public
    `noindex` separation
  - sitemap report currently shows `428 submitted / 0 indexed` even though the
    sampled URLs above are explicitly reported as submitted and indexed

### Changes Shipped

- Commit: none in this check-in
- Files referenced as operating inputs:
  - `docs/ORGANIC_TRACKER.md`
  - `docs/SEO_SURFACE.md`
  - `docs/ORGANIC_5000_SPRINT.md`
  - `docs/WORKFLOW_CHANNEL.md`
- What changed:
  - this was a measurement-only Search Console inspection
  - verified that workflow packet URLs are still off the search surface
  - verified that the tracked canonical URLs are indexed and canonicalized

### Insights

- Observation: site-level clicks, impressions, CTR, and average position all
  improved versus the prior stable 28-day window.
- Interpretation: the site is still in active growth, not in a stall or
  decline phase.
- Observation: Alabama is now the clearest live winner and is absorbing the
  tracked exact permit-intent queries that still surface.
- Interpretation: Alabama remains the strongest proof that state-guide plus
  workflow intent is outperforming broader national framing.
- Observation: records and permit families are compounding visibility faster
  than the perc family.
- Interpretation: the next SEO work should keep leaning into records, permit,
  buyer, and county-file wedges instead of widening back into broader
  educational surfaces.
- Observation: the national `perc-test-cost` page improved in average position
  but lost exact-query demand and still did not earn clicks.
- Interpretation: perc intent should keep handing off into proven state pages
  rather than relying on the national parent to be the final click winner.
- Observation: workflow packet URLs are still unknown to Google.
- Interpretation: the search surface and workflow-channel surface are still
  separated correctly.
- Observation: sitemap reporting is now the main technical anomaly because the
  sitemap says `0 indexed` while inspected sample URLs from that sitemap are
  clearly indexed.
- Interpretation: this is the highest-priority technical follow-up from this
  check-in.

### Next Actions

- Manually verify the sitemap report in the Search Console UI for
  `https://septicpath.com/sitemap.xml`.
- If the UI still shows `428 submitted / 0 indexed` after verification,
  resubmit the sitemap and recheck whether the downloaded timestamp and indexed
  count move together.
- Keep `/septic-system-cost-calculator/alabama/` as the benchmark page for
  blended permit, perc, and records intent.
- Recheck on 2026-04-29 whether the permit and records families continue
  compounding clicks and whether the national parent pages convert any of their
  improved page-one visibility into actual clicks.
- If new internal-link work ships before that recheck, bias it toward:
  - `/septic-records-checklist/` -> live county and state records wedges
  - `/septic-permit-process/` -> proven state permit pages
  - `/perc-test-cost/` -> proven state-specific perc wedges instead of broader
    parent-page copy

## Entry Template

Copy this block for the next review:

```md
## YYYY-MM-DD

### Data

- Source:

## 2026-04-12

### Data

- Source: Google Search Console MCP
- Compare window:
  - current: 2026-03-30 to 2026-04-10
  - prior: 2026-03-18 to 2026-03-29
- Fresh-data note: Search Console data still carries normal short lag, so this
  read should be treated as directional rather than final for the latest 1 to 2
  days.
- Site summary:
  - clicks: 9 vs 11 before
  - impressions: 2,275 vs 1,932 before
  - CTR: 0.40% vs 0.57% before
  - average position: 8.86 vs 10.48 before
- Page signals that mattered:
  - `/septic-records-checklist/indiana/` kept the strongest click efficiency
  - `/septic-system-cost-calculator/alabama/` and
    `/septic-system-cost-calculator/georgia/` kept earning the biggest
    impression pools
  - `/buying-a-house-with-a-septic-system/new-york/` showed early buyer-intent
    proof at page-one average position
  - `/septic-permit-process/south-carolina/` showed impression growth but still
    needed sharper query-language alignment

### Changes Shipped

- Commit: not committed yet in this tracker entry
- Files:
  - `src/main/java/com/example/septic/service/SeoService.java`
  - `data/raw/state_money_pages.json`
  - `src/test/java/com/example/septic/SepticApplicationTests.java`
- What changed:
  - retitled Alabama and Georgia state guides around the exact intent that is
    already surfacing in Search Console, especially `perc test`, `county
    records`, and `soil analysis`
  - added Alabama and Georgia FAQ language that directly answers hidden-but-real
    query shapes around perc testing and county file checks
  - tightened the New York buyer page around the real pre-closing wedge:
    Appendix 75-A file review, county health records, as-built history, and
    direct links into New York state subpages
  - sharpened the South Carolina permit page around `permit requirements`,
    `site evaluation`, `D-1740`, and stronger state-specific internal links

### Insights

- Observation: visibility is still rising while average position improved into a
  page-one range on important terms.
- Interpretation: the problem is not discovery; the problem is click yield once
  Google already shows the page.
- Observation: Alabama and Georgia are being interpreted more like permit,
  records, and site-prep workflows than generic cost calculators.
- Interpretation: those pages should lean harder into exact workflow language
  instead of protecting a broader but weaker "calculator" framing.
- Observation: New York buyer and South Carolina permit pages already have query
  and impression proof, but the page copy and link paths were still too generic.
- Interpretation: these are worth polishing now because they are already close
  enough to convert impressions into the next clicks.

### Next Actions

- Deploy these copy and internal-link changes.
- After deploy, request indexing for:
  - `/septic-system-cost-calculator/alabama/`
  - `/septic-system-cost-calculator/georgia/`
  - `/buying-a-house-with-a-septic-system/new-york/`
  - `/septic-permit-process/south-carolina/`
- Recheck on 2026-04-19:
  - whether Alabama starts earning more `perc test` and permit-file clicks
  - whether Georgia picks up better CTR on county-record and soil-analysis
    queries
  - whether New York buyer and South Carolina permit pages convert their current
    page-one impressions into actual clicks
- Window:
- Fresh-data note:
- Site summary:
- Priority page movement:
- Query movement:
- Indexing / technical checks:

### Changes Shipped

- Commit:
- Files:
- What changed:

### Insights

- Observation:
- Interpretation:
- Risk:

### Next Actions

- Action 1:
- Action 2:
- Action 3:

### Next Check

- Date:
- What to verify:
```

## 2026-03-23

### Data

- Source: Google Search Console MCP for `sc-domain:septicpath.com`
- Window: 2026-02-23 to 2026-03-22
- Fresh-data note: stable interpretation should lean on roughly 2026-03-19 and
  earlier because the newest Search Console data can still shift.
- Site summary:
  - clicks: 4
  - impressions: 1,574
  - CTR: 0.254%
  - average position: 10.85
- Key timing change:
  - impressions started showing meaningfully around 2026-03-10
  - the site looked like early indexing and testing, not a traffic drop
- Priority page movement:
  - `/perc-test-cost/`: strong early opportunity, impressions but no clicks,
    with long-tail queries around page 1 bottom / page 2 top
  - `/septic-system-cost-calculator/alabama/`: getting permit and records
    intent, not just calculator intent
  - `/septic-system-cost-calculator/georgia/`: same pattern as Alabama, with
    permit-cost and permit-step style demand
  - `/septic-records-checklist/` and state variants: early support role with
    useful ranking signals
  - `/drain-field-replacement-cost/`: impressions exist, but rankings are still
    weak enough that metadata alone will not solve it
- Query movement:
  - `perc test cost near me`
  - `how much is a perc test in sc`
  - `how much does a perk test cost`
  - `septic tank permit`
  - `septic permits`
  - `how much does a septic permit cost`
- Indexing / technical checks:
  - inspected priority URLs were indexed
  - canonical signals were generally clean
  - some calculator query-string URLs appeared in reports but had proper
    canonical handling
  - sitemap report looked inconsistent with live URL inspection and should be
    monitored, not treated as a confirmed indexing failure yet

### Changes Shipped

- Commit: `6e9b941`
- Branch: `master`
- Pushed: yes, to `origin/master`
- Files:
  - `src/main/java/com/example/septic/service/SeoService.java`
  - `src/main/java/com/example/septic/web/SiteController.java`
  - `data/raw/content_pages.json`
  - `data/raw/state_profiles.json`
  - `src/test/java/com/example/septic/SepticApplicationTests.java`
- What changed:
  - rewrote SEO titles for `perc-test-cost`, `septic-permit-process`, and
    `septic-records-checklist`
  - reframed Alabama and Georgia state guides toward permit and records intent
  - updated `perc-test-cost` copy to match `perc / perk / percolation` search
    language
  - strengthened `septic-permit-process` and `septic-records-checklist` as hub
    pages with cleaner internal-link targets
  - canonicalized editorial internal links so they prefer clean paths over
    calculator query-string URLs where a public destination exists
  - updated tests and ran the full test suite successfully

### Insights

- Observation: the site is only about two weeks into deployment and already has
  clear impression growth.
- Interpretation: this is the right time to sharpen the pages Google is already
  testing, rather than judging the whole project too early.
- Observation: Georgia and Alabama guides are ranking on permit or records
  intent.
- Interpretation: state guides need to earn trust as practical permit and file
  pages, not just as generic cost explainers.
- Observation: `perc-test-cost` has ranking potential with poor click capture.
- Interpretation: better wording, clearer SERP intent alignment, and cleaner
  internal links are the correct short-term move.
- Risk: `drain-field-replacement-cost` is still too weak competitively and may
  underperform until the body content is expanded.

### Next Actions

- Deploy the current production build containing commit `6e9b941`.
- Request reindexing for:
  - `/perc-test-cost/`
  - `/septic-permit-process/`
  - `/septic-records-checklist/`
  - `/septic-system-cost-calculator/alabama/`
  - `/septic-system-cost-calculator/georgia/`
- Verify production output for canonical tags and editorial internal links on
  those priority pages.
- Prepare the next content pass for `/drain-field-replacement-cost/` with deeper
  body coverage, not just metadata changes.

### Next Check

- Date: 2026-03-30
- What to verify:
  - whether `perc-test-cost` starts earning clicks
  - whether Alabama and Georgia improve on average position for permit-style
    queries
  - whether sitemap reporting starts aligning better with URL inspection
  - whether query-string calculator URLs appear less often in editorial paths

## 2026-04-01

### Data

- Source: Google Search Console MCP for `sc-domain:septicpath.com`, plus live
  production HTML checks
- Window:
  - site summary: 2026-03-01 to 2026-03-29
  - short-interval comparison: 2026-03-23 to 2026-03-29 vs 2026-03-16 to
    2026-03-22
- Fresh-data note: the newest 1 to 2 days in Search Console can still move, so
  the strongest reading is on 2026-03-29 and earlier.
- Site summary:
  - clicks: 15
  - impressions: 3,026
  - CTR: 0.496%
  - average position: 10.79
- Short-interval comparison after the 2026-03-23 tracking entry:
  - clicks: 9 vs 4, up 125%
  - impressions: 1,231 vs 1,082, up 13.8%
  - CTR: 0.731% vs 0.370%, up 97.8%
  - average position: 10.02 vs 10.94, improved by 0.92
- Priority page movement:
  - `/septic-records-checklist/indiana/`: 4 clicks, 178 impressions, position
    6.02
  - `/septic-system-cost-calculator/alabama/`: 2 clicks, 510 impressions,
    position 7.78
  - `/septic-system-cost-calculator/georgia/`: 2 clicks, 345 impressions,
    position 7.78
  - `/septic-permit-process/` state variants started producing clicks in
    Nebraska, Rhode Island, and South Carolina
  - `/perc-test-cost/` still has no clicks, but query coverage remains aligned
    with perc / perk / percolation intent
- Query movement on priority pages:
  - `perc-test-cost` still shows:
    - `cost of a perc test`
    - `cost of perc test`
    - `how much does a perk test cost`
    - `how much is a perc test in sc`
  - Alabama still shows:
    - `how much does a septic permit cost`
    - `how much is a permit for a septic tank`
    - `septic permits`
  - Georgia still shows:
    - `how much does a septic permit cost`
    - `how much is a septic permit`
    - `septic permit cost`
- Indexing / technical checks:
  - `https://septicpath.com/perc-test-cost/`: submitted and indexed
  - `https://septicpath.com/septic-system-cost-calculator/alabama/`: submitted
    and indexed
  - `https://septicpath.com/septic-system-cost-calculator/georgia/`: submitted
    and indexed
  - `https://septicpath.com/septic-permit-process/`: submitted and indexed
  - `https://septicpath.com/septic-records-checklist/`: submitted and indexed
  - live production titles match the 2026-03-23 SEO changes
  - editorial link cleanup appears live:
    `septic-records-checklist/new-jersey/` contains the clean
    `/drain-field-replacement-cost/new-jersey/` path and not the old
    calculator query-string path
  - sitemap report still shows `submitted: 414 / indexed: 0`, so the report
    mismatch remains unresolved even though live URL inspection is healthy

### Changes Shipped

- Commit status:
  - no new content or code changes were shipped in this check-in
  - the 2026-03-23 changes from commit `6e9b941` are confirmed live in
    production
- What was verified live:
  - `Perc Test Cost and Percolation Test Price | SepticPath`
  - `Septic Permit Process by State | Permits, records, and next steps | SepticPath`
  - `Septic Records Checklist | Permit records, as-builts, and file lookup | SepticPath`

### Insights

- Observation: the site-level trend improved meaningfully in clicks, CTR, and
  average position after the prior check.
- Interpretation: the site is still in the early growth and testing phase, but
  the direction is now clearly positive rather than merely neutral.
- Observation: Alabama and Georgia remain two of the strongest organic assets.
- Interpretation: the permit and records angle for those state guides is still
  the right strategic framing.
- Observation: state-level permit and records pages are starting to win small
  clicks outside the original focus pages.
- Interpretation: the workflow-page cluster is beginning to behave like a real
  traffic surface, not just support content.
- Observation: `perc-test-cost` is indexed and the updated title is live, but
  clicks have not started yet.
- Interpretation: the title fix was correct, but the page probably still needs
  more ranking improvement or stronger snippet relevance before CTR moves.
- Risk: the sitemap indexed count is still misleading or lagged, so it cannot
  yet be used as the main health signal.

### Next Actions

- Keep monitoring `perc-test-cost` for the first real click instead of changing
  it again immediately.
- Start the deeper content pass for `/drain-field-replacement-cost/` because it
  still looks like the weakest major opportunity.
- Expand the workflow cluster that is already proving traction, especially state
  permit and records pages.
- Use selective manual indexing only for priority pages after meaningful edits,
  not site-wide.

### Next Check

- Date: 2026-04-08
- What to verify:
  - whether `perc-test-cost` earns its first click
  - whether Alabama and Georgia keep gaining on permit-style queries
  - whether additional state permit or records pages start generating clicks
  - whether the sitemap mismatch begins to resolve

## 2026-04-01 (Execution Note)

### Data

- Source: product instrumentation review, lead-storage schema review, and full
  application test run
- Window: same-day implementation review on 2026-04-01
- Fresh-data note: this is not a Search Console reading; it is a monetization
  readiness update tied to attribution and lead-routing evidence.
- Site summary:
  - organic growth is improving, but revenue attribution was previously too weak
    because content and state pages could hand users into the quote flow without
    preserving the originating page in stored lead artifacts
- Priority page movement:
  - this change does not alter rankings directly
  - it changes how future leads can be attributed back to:
    - national content pages
    - state guides
    - state money pages
- Query movement:
  - not applicable for this execution note
- Indexing / technical checks:
  - hidden `sourcePageHint` fields now survive the estimator and quote form flow
  - content-page and state-page CTA links now pass the originating path forward
  - lead JSON, export JSON, and event logs now preserve effective source-page
    attribution
  - full test suite passed after the attribution changes

### Changes Shipped

- Commit: not committed yet in this tracker entry
- Files:
  - `src/main/java/com/example/septic/service/LeadStorageService.java`
  - `src/main/java/com/example/septic/web/EstimateForm.java`
  - `src/main/java/com/example/septic/web/QuoteLeadForm.java`
  - `src/main/java/com/example/septic/web/SiteController.java`
  - `src/main/jte/pages/calculator.jte`
  - `src/main/jte/pages/state-guide.jte`
  - `src/main/jte/pages/state-money-page.jte`
  - `src/test/java/com/example/septic/SepticApplicationTests.java`
- What changed:
  - propagated `sourcePageHint` through calculator and quote flows
  - added source attribution to lead payloads, export payloads, and quote-submit
    events
  - updated CTA links so national content pages, state guides, and state money
    pages preserve the originating path when they send users into the estimator
    or short quote form
  - updated tests to assert the new attribution behavior and current CTA URLs

### Insights

- Observation: before this change, the quote flow could collapse many organic
  entry pages into the generic calculator source.
- Interpretation: that would have made monetization decisions slower and weaker,
  because we could not answer which page families actually produce leads.
- Observation: the site now has enough early traffic that attribution quality
  matters more than another round of cosmetic copy edits.
- Interpretation: this is the correct "genius move" for this week because it
  upgrades the business feedback loop, not just the content layer.
- Risk: attribution is now ready, but buyer routing and lead monetization still
  need an external destination or partner workflow.

### Next Actions

- Commit and push the attribution changes.
- After the next production deploy, submit one test lead from:
  - a national content page
  - a state guide
  - a state money page
  and verify the stored `sourcePage` values are distinct.
- Use the next organic leads to build a simple leaderboard by source page and
  page family before making the next monetization decision.

### Next Check

- Date: 2026-04-08
- What to verify:
  - whether production lead artifacts preserve source-page attribution
  - whether any real submissions cluster around a specific content family
  - whether the new attribution is sufficient to evaluate partner-fit by page
    type

## 2026-04-01 (CTR Sprint Note)

### Data

- Source: Search Console review plus tracker comparison
- Window: `2026-03-01` to `2026-03-29`
- Fresh-data note: early-growth data, still low volume, but enough to see which
  page families deserve CTR-focused work.
- Site summary:
  - `15 clicks / 3,026 impressions / 0.496% CTR / 10.79 average position`
- Priority page movement:
  - `/septic-system-cost-calculator/alabama/` remained one of the strongest
    pages and was already showing permit-cost and permit-record queries in the
    upper part of page one
  - `/septic-system-cost-calculator/georgia/` remained one of the strongest
    pages and was already showing permit-cost queries in the upper part of page
    one
  - `/septic-records-checklist/indiana/` was the clearest proof that the
    records workflow cluster can earn real clicks
  - `/septic-permit-process/nebraska/`,
    `/septic-permit-process/rhode-island/`, and
    `/septic-permit-process/south-carolina/` had enough traction to justify
    more explicit title and snippet language
- Query movement:
  - Alabama was strongest on permit-cost, permit, and permit-record queries
  - Georgia was strongest on permit-cost and permit-requirement style queries
  - Indiana and selected permit-process states showed workflow intent rather
    than broad informational intent
- Indexing / technical checks:
  - full application test suite passed after the CTR-oriented copy changes

### Changes Shipped

- Commit: not committed yet in this tracker entry
- Files:
  - `src/main/java/com/example/septic/service/SeoService.java`
  - `src/main/java/com/example/septic/web/SiteController.java`
  - `data/raw/state_profiles.json`
  - `data/raw/state_money_pages.json`
  - `src/test/java/com/example/septic/SepticApplicationTests.java`
- What changed:
  - rewrote Alabama and Georgia state-guide headings, titles, descriptions, and
    FAQ prompts to match the permit-cost and permit-record queries Search
    Console is already showing
  - adjusted Alabama and Georgia CTA language so the first call to action reads
    like permit-cost decision support instead of generic estimation
  - upgraded click-proven workflow pages with more explicit snippet language:
    Indiana records, Nebraska permit process, South Carolina permit process,
    and Rhode Island permit process
  - kept the current attribution changes in place so any future click gain can
    still be tied back to the originating page family

### Why This Was Not Just Cosmetic

- This was not a simple title refresh or a thin copy layer on top of the same
  page behavior.
- Alabama and Georgia were repositioned from broad "state cost guide" language
  toward the exact query shape Google is already rewarding:
  permit cost, permit records, county file, and permit requirements.
- The work changed three layers at once:
  - search-result layer: titles and descriptions
  - page-intent layer: headings, summaries, and FAQs
  - action layer: CTA wording and the decision framing around the estimate
- The workflow pages were not edited at random. They were selected because
  Search Console already showed early click proof on Indiana records and
  several permit-process state pages.
- The practical bet is that a page already ranking in the upper part of page
  one will respond faster to intent sharpening than a lower-ranking page will
  respond to more generic content expansion.

### Insights

- Observation: Alabama and Georgia are already close enough to the top of page
  one that CTR work matters more than another round of structural changes.
- Interpretation: the fastest click growth now comes from sharper search-result
  copy on pages already ranking, not from publishing a large new content batch.
- Observation: the workflow-page cluster is now earning both impressions and a
  few real clicks.
- Interpretation: titles and descriptions on those workflow pages should keep
  leaning into permit file, permit copy, file search, and county routing
  language because that is the query shape Google is rewarding.
- Observation: the CTR sprint changed page framing, CTA framing, and FAQ framing
  in the same direction instead of mixing multiple intents on the same page.
- Interpretation: this is deeper than cosmetic SEO because the page now
  presents one clearer story to both Google and the user.
- Observation: `perc-test-cost` is still mostly a position problem outside a
  few long-tail terms.
- Interpretation: it is not the best immediate CTR lever this week.

## 2026-04-04

### Data

- Source:
  - Google Search Console MCP for `sc-domain:septicpath.com`
  - live product-surface review of home, nav, calculator, and national workflow pages
  - targeted external web review of current official-source records and permit surfaces
- Window:
  - site summary: `2026-03-04` to `2026-04-01`
  - short comparison: `2026-03-27` to `2026-04-02` vs `2026-03-20` to `2026-03-26`
- Fresh-data note:
  - the `2026-04-02` edge can still move slightly, so the stronger read remains on `2026-04-01` and earlier
- Site summary:
  - clicks: `22`
  - impressions: `3,571`
  - CTR: `0.616%`
  - average position: `10.56`
- Short comparison:
  - clicks: `10` vs `9`, up `11.1%`
  - impressions: `1,094` vs `1,223`, down `10.5%`
  - CTR: `0.914%` vs `0.736%`, up `24.2%`
  - average position: `9.94` vs `10.50`, improved by `0.56`
- Page-family movement:
  - state guides: `8 clicks / 1,673 impressions / position 9.96`
  - records pages: `7 clicks / 418 impressions / position 7.40`
  - permit pages: `3 clicks / 349 impressions / position 7.75`
  - buyer pages: `1 click / 212 impressions / position 7.12`
  - `perc-test-cost`: `0 clicks / 622 impressions / position 10.50`
  - `septic-replacement-cost`: `0 clicks / 122 impressions / position 5.76`
  - `drain-field-replacement-cost`: `0 clicks / 113 impressions / position 47.93`
- Priority URL movement:
  - `/septic-records-checklist/indiana/`: `6 clicks / 210 impressions / position 6.22`
  - `/septic-system-cost-calculator/alabama/`: `6 clicks / 587 impressions / position 7.62`
  - `/septic-system-cost-calculator/georgia/`: `2 clicks / 395 impressions / position 7.78`
  - `/buying-a-house-with-a-septic-system/new-york/`: first click with buyer-intent proof
  - `/septic-permit-process/nebraska/`, `/rhode-island/`, `/south-carolina/`: continued click proof for permit workflow pages
- External official-source note:
  - current public-government surfaces continue to reinforce the same wedge shape: permit search, septic-record lookup, county or district routing, and transfer or inspection workflow

### Changes Shipped

- Commit status:
  - not committed yet in this tracker entry
- Files:
  - `src/main/jte/layouts/app.jte`
  - `src/main/jte/pages/home.jte`
  - `src/main/jte/pages/state-coverage.jte`
  - `src/main/jte/pages/calculator.jte`
  - `src/main/jte/pages/content-page.jte`
  - `src/main/jte/pages/state-guide.jte`
  - `src/main/jte/pages/state-money-page.jte`
  - `src/main/jte/pages/county-records-page.jte`
  - `src/main/java/com/example/septic/service/SeoService.java`
  - `src/main/java/com/example/septic/service/SitemapService.java`
  - `src/main/java/com/example/septic/web/SiteController.java`
  - `src/main/java/com/example/septic/service/ResearchDataService.java`
  - `src/main/java/com/example/septic/service/ProjectType.java`
  - `src/main/java/com/example/septic/service/EstimatorService.java`
  - `src/test/java/com/example/septic/SepticApplicationTests.java`
  - `data/raw/content_pages.json`
  - `data/raw/state_profiles.json`
  - `data/raw/source_registry.csv`
  - `data/raw/county_records_pages.json`
- What changed:
  - repositioned the product surface from estimator-first toward records, permit, and buyer-workflow-first entry points
  - moved home and navigation emphasis toward records checklist, permit process, and state guides
  - changed national workflow and buyer pages so the first action points to state-specific workflow pages, with the estimator moved into the secondary slot
  - updated calculator copy and calculator-hub internal links so the estimator behaves more like step two in the workflow instead of the site-wide front door
  - adjusted estimator project-type labels and workflow-specific checklist language for buyer and inspection scenarios so the result pages better match the actual organic wedge
  - restructured the state-guide template so state guides now lead with the highest-intent workflow page, then the county office and records path, then permit and buyer-risk context, with the estimator demoted to the later planning step
  - tightened the Alabama, Georgia, Arizona, Virginia, and Missouri state-guide copy around county file retrieval, permit steps, transfer or buyer risk, and local authority routing
  - added Georgia county environmental health office sources so the Georgia guide can point to an actual county-office list instead of only a broad onsite-program page
  - launched Indiana county records pages for Howard, Floyd, Noble, and Wayne counties, each tied to real county office or records-request surfaces rather than generic statewide copy
  - added a dedicated county-records template, route, sitemap support, and SEO/breadcrumb handling for `/septic-records-checklist/{state}/{county}/`
  - surfaced county-page cards on the Indiana state guide and Indiana records workflow page so the state-level winner now routes directly into the county-file step
  - added current Indiana county official sources and records-form links so the new pages can point to actual local forms, office pages, and request paths
  - launched a national `septic-transfer-compliance` hub so the product now has one clear umbrella page for records, permit routing, buyer diligence, and county-file friction
  - changed home and global navigation so transfer compliance is now a primary surface, while permit process remains inside the flow instead of competing with the umbrella concept
  - changed the transfer-compliance page to rank and surface live state pages across records, permit, and buyer families instead of pretending transfer is a standalone empty family

### Insights

- Observation: the records and permit cluster is now producing a meaningful share of the real clicks, not just impressions.
- Interpretation: SepticPath should be presented more like a septic transfer, records, and permit-workflow product with a cost estimator inside it, not like an estimator product with workflow support pages around it.
- Observation: Alabama and Georgia are still strong, but their winning query shape is permit cost, permit records, and county-file language rather than pure estimator intent.
- Interpretation: the state-guide surface should keep leaning into workflow and file context instead of generic cost-guide framing.
- Observation: top state guides with the best impression volume are still Alabama, Georgia, Virginia, Arizona, and Missouri, but only Alabama and Georgia are turning that into clicks right now.
- Interpretation: the broad guide pages need to behave more like workflow routers in those states, not like generic estimate landing pages.
- Observation: buyer pages now have early click proof, while `perc-test-cost` and broad cost pages still lag on clicks.
- Interpretation: buyer, records, and permit pages deserve the front-door position before another round of generic money-page expansion.
- Observation: current official-source surfaces on the public web keep resolving to county, district, or delegated local file paths.
- Interpretation: county-aware workflow remains the stronger wedge than a purely statewide estimator promise.
- Observation: Indiana records already had the clearest state-level click proof, and Indiana counties also expose usable public record or request surfaces.
- Interpretation: Indiana was the right first state for a county wedge because it compounds an already-winning records family instead of starting a new family from zero.
- Observation: Georgia's environmental-health contact page explicitly routes record requests and other locally related questions to county environmental health offices, and Alabama's ADPH septic-tank page explicitly sends owners or agents to the local health department or the records-request path for permit copies.
- Interpretation: `transfer compliance` is not just copy polish; it matches the actual official-source shape where transfer risk resolves through county office routing, permit-file access, and buyer-side diligence rather than through a standalone estimator promise.

### Next Actions

- Verify production after deploy to make sure:
  - home and nav now lead with workflow pages
  - national workflow pages lead with state-specific workflow pages
  - calculator still remains easy to reach as a secondary step
  - the top state guides now lead with workflow pages and show county office, records, and permit-risk sections before estimate CTAs
- Watch whether:
  - records, permit, and buyer pages pick up higher CTR once they become primary site entry points
  - state-guide clicks hold or improve even after estimator prominence is reduced on the broad site surface
  - estimator conversions from workflow pages remain attributable through `sourcePageHint`
- Decide the next deeper build:
  - verify whether Indiana county pages begin appearing in impressions and internal-link traffic before expanding the county wedge further
  - if early engagement is real, add county-level wedges in the next records- or transfer-proof states
  - if the new transfer-compliance hub starts attracting internal clicks, decide whether it deserves its own state family or should remain an umbrella page that routes into records, permit, and buyer winners

### Next Check

- Date: `2026-04-11`
- What to verify:
  - whether workflow-first navigation changes shift clicks toward records, permit, and buyer pages
  - whether calculator-assisted leads still flow after the estimator is demoted to step two on national workflow pages
  - whether Alabama, Georgia, and Indiana keep strengthening on permit-record and county-file query shapes
  - whether the new Indiana county pages start collecting impressions or assist clicks from the state-guide and records-page surfaces
  - whether the new transfer-compliance hub earns internal clicks and starts behaving like the right umbrella page for buyer, records, and permit intent

### Next Actions

- Deploy the CTR-oriented copy changes.
- After deployment, use selective indexing requests only for the updated
  priority URLs:
  - `/septic-system-cost-calculator/alabama/`
  - `/septic-system-cost-calculator/georgia/`
  - `/septic-records-checklist/indiana/`
  - `/septic-permit-process/nebraska/`
  - `/septic-permit-process/south-carolina/`
  - `/septic-permit-process/rhode-island/`
- Compare the next 7-day window against the prior one with emphasis on:
  - clicks and CTR for Alabama and Georgia
  - whether Indiana keeps converting workflow impressions into clicks
  - whether the upgraded permit-process pages begin picking up additional clicks

### Next Check

- Date: 2026-04-08
- What to verify:
  - whether Alabama and Georgia improve CTR on permit-style queries
  - whether the upgraded workflow pages earn more first clicks
  - whether click gains line up with future attributed leads

## 2026-04-01 (Workflow Depth + Drain-Field Breadth)

### Data

- Source: tracker review plus current content-map review
- Window: same-day execution follow-up on 2026-04-01
- Fresh-data note: this entry records structural follow-through after the CTR
  sprint, not a new Search Console pull.
- Site summary:
  - winning search intent still clusters around permit, records, buyer, and
    inspection workflow pages
  - drain-field intent is strategically important but still much thinner by
    state coverage than the rest of the site
- Coverage summary:
  - `buying-a-house-with-a-septic-system`: 50 state pages
  - `septic-inspection-cost`: 50 state pages
  - `drain-field-replacement-cost`: was 10 state pages before today's work

### Changes Shipped

- Commit: not committed yet in this tracker entry
- Files:
  - `data/raw/content_pages.json`
  - `data/raw/state_money_pages.json`
  - `src/main/java/com/example/septic/web/SiteController.java`
  - `src/test/java/com/example/septic/SepticApplicationTests.java`
- What changed:
  - deepened the national buyer and inspection pages so they now read like
    records-first and permit-first due-diligence workflows instead of generic
    cost pages
  - tightened buyer and inspection FAQ prompts around permit files, as-builts,
    O&M logs, pumping history, and inspection leverage
  - changed buyer and inspection CTA framing so the estimate comes after the
    file review, not before it
  - added new drain-field replacement state pages for:
    - Alabama
    - Indiana
    - South Carolina
    - Rhode Island
  - linked those new drain-field pages into existing records, inspection, and
    buyer state pages through the default cross-link system

### Why This Matters

- This was the next step after the CTR sprint, not a separate random batch of
  edits.
- The buyer and inspection parent pages now better match the same workflow
  intent that Search Console is already rewarding on records and permit pages.
- The drain-field cluster is now expanding through states that already showed
  organic traction elsewhere, instead of being spread randomly across the map.
- That gives the site a clearer flow:
  - records / permit / buyer intent brings in early traffic
  - drain-field pages catch the higher-risk replacement path once the file or
    inspection story turns negative

### Insights

- Observation: the strongest national follow-through move was to make buyer and
  inspection pages more like due-diligence workflow pages.
- Interpretation: this reduces intent mismatch between the parent pages and the
  state-level pages already winning clicks.
- Observation: drain-field intent is still early, but it was under-covered in
  some of the states already proving traction elsewhere.
- Interpretation: adding Alabama, Indiana, South Carolina, and Rhode Island
  gives the site a better chance to convert workflow traffic into higher-risk
  replacement paths without waiting for a full 50-state expansion.

### Next Actions

- Deploy the workflow-depth and drain-field-breadth changes together.
- On the next check, compare whether the new drain-field pages start appearing
  in internal-link surfaces and whether the buyer and inspection pages hold a
  cleaner records-first framing.
- If early signals stay positive, expand the drain-field cluster again using
  the next proven workflow states rather than launching all remaining states at
  once.

### Next Check

- Date: 2026-04-08
- What to verify:
  - whether buyer and inspection pages still read coherently as due-diligence
    workflows after deploy
  - whether Alabama, Indiana, South Carolina, and Rhode Island now expose
    drain-field links from their workflow pages
  - whether the next drain-field expansion should keep following proven states
    or shift to a larger batch

## 2026-04-04 (County Wedge Expansion in Alabama and Georgia)

### Data

- Source: official-source review plus current county-page architecture follow-up
- Window: same-day execution follow-up on 2026-04-04
- Fresh-data note: this is a structural expansion entry, not a new Google
  Search Console pull
- Site summary:
  - county pages were previously Indiana-only in practice
  - Alabama and Georgia already had proven state-level permit and records
    traction, so they were the highest-value next county wedge
- Coverage summary:
  - Indiana county pages live: 4
  - Alabama county pages added today: 2
  - Georgia county pages added today: 3

### Changes Shipped

- Commit: not committed yet in this tracker entry
- Files:
  - `data/raw/county_records_pages.json`
  - `data/raw/source_registry.csv`
  - `data/raw/content_pages.json`
  - `src/main/jte/pages/county-records-page.jte`
  - `src/test/java/com/example/septic/SepticApplicationTests.java`
- What changed:
  - removed Indiana-only copy from the reusable county-records template so
    county pages can scale cleanly across states
  - added Alabama county pages for:
    - Baldwin County
    - Madison County
  - added Georgia county pages for:
    - DeKalb County
    - Fulton County
    - Gwinnett County
  - added official local-source rows for those counties using county public
    health or environmental-health pages
  - surfaced Alabama and Georgia county examples inside the transfer-compliance
    hub so the new wedge is not trapped only behind state pages
  - expanded sitemap and render coverage with county-page assertions
  - reran `./gradlew.bat test` after the county expansion and the suite passed

### Insights

- Observation: Alabama and Georgia were already winning on permit and records
  query shapes at the state-guide level.
- Interpretation: those states are better next county-wedge targets than
  starting from colder states with no workflow proof yet.
- Observation: the reusable county template still hard-coded Indiana copy.
- Interpretation: leaving that in place would have made the county expansion
  look thin and product-incoherent the moment Alabama or Georgia pages went
  live.
- Observation: Georgia county pages are especially strong where a county page
  exposes existing-system certification, plan review, or site-evaluation
  workflow on one screen.
- Interpretation: transfer-compliance and records surfaces should keep leaning
  into counties where the office path and the file path are both visible.

### Next Actions

- Deploy the Alabama and Georgia county-page expansion.
- After deploy, request indexing only for the five new county pages:
  - `/septic-records-checklist/alabama/baldwin-county/`
  - `/septic-records-checklist/alabama/madison-county/`
  - `/septic-records-checklist/georgia/dekalb-county/`
  - `/septic-records-checklist/georgia/fulton-county/`
  - `/septic-records-checklist/georgia/gwinnett-county/`
- Check internal-click flow from:
  - Georgia state guide
  - Alabama state records page
  - national transfer-compliance hub

### Next Check

- Date: 2026-04-11
- What to verify:
  - whether the new Alabama and Georgia county pages start collecting
    impressions or assisted clicks
  - whether county links on Georgia and Alabama state surfaces earn internal
    navigation events
  - whether the next county expansion should stay inside proven states or move
    to a new state family

## 2026-04-04 (Transfer Compliance Surface Polish)

- Source: same-day follow-up after county wedge expansion plus browser QA
- Window: 2026-04-04
- Fresh-data note: this is a structural polish entry, not a new GSC export
- Site summary:
  - the county wedge was live, but the national transfer-compliance surface
    still visually led with Indiana examples
  - that made the newest Alabama and Georgia county work less visible than it
    should be

### Changes Shipped

- Commit: not committed yet in this tracker entry
- Files:
  - `src/main/java/com/example/septic/web/SiteController.java`
  - `src/test/java/com/example/septic/SepticApplicationTests.java`
- What changed:
  - reweighted transfer-compliance state ranking so Georgia and Alabama
    records pages surface ahead of Indiana on the national hub
  - promoted county records pages inside related-link scoring so county pages
    can appear before broader state pages when the page intent is transfer or
    records workflow
  - updated county-link notes to talk about county file, certification-letter,
    and local health-office workflow rather than generic state navigation
  - extended path parsing so county records pages inherit the same state and
    content-family scoring logic as their parent records family
  - updated transfer-compliance assertions to require Georgia and Alabama
    county examples, not just Indiana state examples
  - reran `./gradlew.bat test` after the ranking polish and the suite passed

### QA Check

- Browser check: verified on `http://127.0.0.1:8081/septic-transfer-compliance/`
- What changed visually:
  - fast next steps now start with:
    - `DeKalb County Georgia Septic Records Checklist`
    - `Madison County Alabama Septic Records Checklist`
    - `Floyd County Indiana Septic Records Checklist`
  - best state-specific example now resolves to `Georgia Septic Records Checklist`
  - representative state examples now lead with Georgia, then Alabama, then
    Indiana
  - county examples now appear before broader state examples in the
    "When this page stops being enough" surface

### Insight

- Observation: the county wedge became materially stronger once the national
  hub stopped defaulting to Indiana-first ordering.
- Interpretation: the product surface is now more consistent with where the
  newest workflow proof exists, which should improve both perceived relevance
  and downstream discovery of the new county pages.

### Next Actions

- Deploy the transfer-compliance ranking polish with the rest of the county
  wedge work.
- After deploy, watch whether Georgia and Alabama county pages receive more
  internal navigation from the national transfer-compliance hub.
## 2026-05-09 (Workflow Cost Reopen Policy)

- Source: internal code and data verification
- Window: 2026-05-09
- Fresh-data note: this is a structural indexing-policy entry, not a new
  Search Console export
- Site summary:
  - published county pages: 244
  - published state money pages: 345
  - published workflow cost pages reopened by policy: 195
  - published state guides: 50

### Changes Shipped

- Commit: not committed yet in this tracker entry
- Files:
  - `src/main/java/com/example/septic/service/PublishingPolicyService.java`
  - `src/main/java/com/example/septic/web/SiteController.java`
  - `src/main/java/com/example/septic/web/StateCostScopeView.java`
  - `src/main/jte/pages/state-money-page.jte`
  - `src/test/java/com/example/septic/SepticApplicationTests.java`
- What changed:
  - cost pages now use a `Cost scope router` surface with `Clear first`,
    `Low-end breaker`, `County widener`, and `Stop trusting midpoint when`
  - replacement, inspection, perc/site-review, pumping, failed-perc,
    replacement-area, drain-field, and wet-yard pages now receive county
    workflow synthesis when the state has enough local workflow evidence
  - `PublishingPolicyService` now exposes `isCostReopenCandidate(...)`
  - indexability for workflow cost pages now uses source-backed cost decision
    inputs plus local workflow evidence instead of only a state cost profile
  - sitemap expectations were flipped for reopened cost URLs such as
    California replacement, Texas replacement, Ohio inspection, Arizona perc,
    and Colorado drain-field pages
  - added a regression test proving every published workflow cost page is a
    reopen candidate and appears in the sitemap

### QA Check

- Commands:
  - `./gradlew test --tests com.example.septic.SepticApplicationTests`
  - `./gradlew test`
- Result: both passed
- Reopened workflow cost page count by family:
  - `septic-replacement-cost`: 50
  - `perc-test-cost`: 50
  - `septic-inspection-cost`: 50
  - `drain-field-replacement-cost`: 14
  - `failed-perc-test-septic`: 10
  - `septic-replacement-area`: 10
  - `wet-yard-over-septic-drain-field`: 10
  - `septic-pumping-cost`: 1

### Watchlist

- Watch reopened cost cohorts separately from workflow cohorts:
  - replacement: `/septic-replacement-cost/*`
  - inspection: `/septic-inspection-cost/*`
  - perc/site review: `/perc-test-cost/*`
  - drain field and failure variants:
    `/drain-field-replacement-cost/*`, `/failed-perc-test-septic/*`,
    `/septic-replacement-area/*`, `/wet-yard-over-septic-drain-field/*`
- Good 7 to 30 day signals:
  - county-modified cost queries begin landing on reopened pages
  - impressions grow without a simultaneous CTR collapse
  - reopened pages send internal clicks to county record pages
  - county and records pages keep stable impressions while cost impressions
    widen
- Bad 7 to 30 day signals:
  - all cost cohorts gain broad impressions but CTR stays flat or falls
  - records/permit/buyer pages decline at the same time cost pages expand
  - snippets pull generic planning-range language instead of county file,
    quote-gate, or cost-scope-router language

### Next Check

- Recheck Search Console after enough post-deploy data is available to compare:
  - reopened cost pages vs county pages
  - cost query impressions vs records/permit/buyer impressions
  - internal navigation from reopened cost pages to county records pages

## 2026-06-28 - 3-week 100-click sprint third expansion

- Added 34 official-source county lookup pages across AL, IN, NC, TN, and TX, bringing the county records surface from 273 to 307 pages.
- Added the broad records-intent hub `/how-to-find-septic-records-online/` for live GSC queries like `how to find septic tank records online`, `how to look up septic records`, and `property septic records`.
- Changed the permit lookup county launchpad from a hard-coded list to a ranked dynamic surface across TN, NC, TX, AL, and IN so new county pages automatically receive internal links from the lookup hub.
- Pushed high-intent internal links from state records and permit pages into the new county cluster instead of waiting for sitemap discovery alone.
- Quality bar: every new county page has an official local or state source, a records/permit artifact to request, a repair or closeout risk, and state-specific workflow language.

## 2026-06-29 - Maximum records-intent surface expansion

- Added 5 new national records-intent entry points: `/septic-records-by-county/`, `/septic-permit-search-by-address/`, `/septic-permit-records-request/`, `/septic-as-built-records/`, and `/septic-inspection-letter/`.
- Connected each new hub to the ranked county lookup launchpad so broad records searches can move into the existing 307-page county records network instead of stopping at a generic article.
- Routed state records, permit-process, and buyer workflow pages into the new hubs so already-impressed pages can pass internal link equity into the expanded records cluster.
- Added priority-state county backlinks into the new records hubs across AL, IN, NC, TN, and TX to make the cluster reciprocal without touching unrelated states.
- Strategic goal: turn one records hub into a multi-entry records cluster covering county, address, request-form, as-built, and inspection-letter query variants before the 3-week click sprint review.

## 2026-06-29 - CTR surgery on already-impressed records pages

- Rechecked GSC top pages and prioritized pages already showing impressions: Alabama state guide, Tennessee records, Indiana records, North Carolina records, South Carolina records and permit process, and Tennessee perc.
- Rewrote high-impression state records titles and snippets around exact click reasons: TDEC SSDS records search, county records search, permit lookup by address, SCDES D-1740, permit copy, as-built records, and inspection-letter language.
- Reordered internal links on the target state pages so the first links point into records cluster hubs and proven county pages instead of generic calculator paths.
- Promoted the records cluster from the home page so `/septic-records-by-county/`, `/septic-permit-search-by-address/`, `/septic-permit-records-request/`, `/septic-as-built-records/`, and `/septic-inspection-letter/` are discoverable immediately.
- Adjusted Alabama guide title/H1/CTA from a broad permit-cost guide toward county records, Permit to Install, Approval for Use, and perc-test search intent.
