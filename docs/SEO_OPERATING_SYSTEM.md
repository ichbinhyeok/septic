# Search portfolio operating system

SepticPath grows from verified search demand and completed record-help work, not from publishing another generic county page.

## Page roles

- `core`: a page with measured search demand or route-specific operational proof. Protect its query match, put the next action near the top, and measure each handoff.
- `support`: a locally grounded page with enough official-source and decision depth to support a core route. Keep it linked, but do not expand it until demand or an operating outcome appears.
- `evidence_queue`: a page that needs stronger local evidence before further SEO work. Do not add more generic copy merely to make it longer.

Generate the current portfolio from the source data:

```powershell
node tools/search-portfolio.mjs --write=build/reports/search-portfolio.json
```

The report is derived from `search_response_targets.json`, state workflow data, county route data, and operational proof embedded in the county dataset. It is an editorial queue, not an automatic `noindex` switch.

## Weekly loop

1. Save new Google Search Console, Bing Webmaster, and GA4 evidence as an immutable operations `growth_signal` before changing a page.
2. Add an observed-demand page to `search_response_targets.json` with its window, clicks, impressions, CTR, position, queries, and evidence note.
3. Re-run the portfolio report. Work on `core` pages first; use verified customer outcomes to promote pages from `support` or `evidence_queue`.
4. Keep one search promise per page. Route users by situation: original record, empty result, existing document, or human investigation.
5. After a material change, update only the affected sitemap `lastmod`. Freeze the title during the full comparison window.
6. Compare complete 28-day windows. Review impressions and position first, CTR second, then GA4 handoffs and qualified requests.

## Decision rules

- Rising impressions with flat clicks: improve title, description, and above-the-fold query match.
- Healthy clicks with weak next-step events: improve the situation handoff, not the snippet.
- Strong handoffs with few completed requests: investigate the form or agency route bottleneck.
- No demand and no operational proof: do not create more near-duplicate copy; gather evidence or consolidate the page.
- A verified agency response is both customer evidence and reusable route intelligence. Record it in the private operations ledger before publishing any privacy-reviewed derivative.
