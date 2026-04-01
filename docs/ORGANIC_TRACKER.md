# Organic Tracker

Last updated: 2026-04-01

## Why this file exists

This file is the operating log for organic search work after launch.

Use it to track:

- what Search Console data said on a specific date
- what was changed in the product or content
- what we learned from that data
- what to check next and when

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

## Entry Template

Copy this block for the next review:

```md
## YYYY-MM-DD

### Data

- Source:
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
