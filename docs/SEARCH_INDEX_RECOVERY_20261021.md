# Search index recovery — October 21, 2026

## Scope and boundaries

Repair broken state-record handoffs, test intent-first TDEC/SCDES snippets,
strengthen contextual discovery of reviewed records assets, and reduce a
specific low-signal cost-search cohort. This is not a sitewide content purge.
No records/county URL, new case asset, service/trust page or observed organic
entrance was selected for removal. Existing privacy and intake rules are unchanged.

## Publication decisions

The private audit compared Aug23–Sep19 with Sep20–Oct17 and screened593 submitted
URLs. Of72 state-topic variants with no Google impressions/clicks or measured
GA4 Organic Search entrances in either window,22 were selected for reversible
`noindex,follow` plus sitemap exclusion. They lack a state cost profile and
largely repeat office/file-routing information rather than supply standalone
local price evidence:

| Topic | States |
|---|---|
| Replacement cost | AZ, CO, KS, MD, NY, NV, TN, SC, TX, VA, UT, WI, WY |
| Inspection cost | CA, CO, IN, KS, NV, SC, TX, UT, WY |

These pages still return200, retain self canonicals, and remain usable through
relevant existing links. They are not redirected to unrelated pages or blocked
in robots.txt. The explicit cohort lives in `PublishingPolicyService` so a later
evidence-backed reopening is deliberate, testable and reversible.

The other50 strict-zero variants are retained: they include distinct local
transfer/compliance rules, state cost profiles, meaningful site/soil workflows,
or established state-guide roles. Arizona and Minnesota inspection pages are
explicit protections, not incidental exceptions. The47 additional state review
candidates with some Google impressions and102 county evidence-review candidates
are retained pending stronger evidence, not automatically excluded.

Zero measured entrances does not establish zero Bing impressions, backlinks or
assisted value. The cohort is an editorial decision, not a claim that Google
penalized these URLs, or that fewer indexed pages guarantees ranking gains.
Private page-level measurements and dispositions stay out of public assets.

## Link and snippet changes

`institution-records-page.jte` now receives existence-checked related links.
Where a public state-specific page exists it is kept; otherwise a relevant
national guide supplies the next step. The required location, drainfield,
soil-test and replacement-area actions are ordered before secondary links.
This replaces19 broken targets on7 state records pages without manufacturing
thin content. Regression tests fetch every rendered related target for all8
institution records desks and require200 responses and distinct targets.

Morgan County is promoted in Alabama's visible county list. North Carolina's
existing evidence section links the Harnett file review and local search route.
No new customer evidence or identifying material is published.

TDEC and DHEC/SCDES titles/descriptions lead with the searched record task rather
than a generic free-help offer. They describe official routes, not an integrated
government-database search. Existing H1s, official routes and free-help actions
are preserved. The changed parent/state pages receive accurate scoped lastmod
dates; untouched county files are not falsely marked as freshly reviewed.

## Verification and follow-through

- `gradlew.bat test bootJar` is the release gate.
- `SearchIndexRecoveryRegressionTest` covers route existence/fallback,
  duplicate-link prevention, all22 noindex/canonical/sitemap contracts,
  protected records/new/local-rule assets and contextual links.
- Existing tests must reflect changed publication counts and only the dates
  that actually changed; unchanged dates remain asserted.
- Verify deployed HTML, all submitted URLs and the19 former broken-target
  references. Record exact deployment SHA and test results privately.
- Submit the current sitemaps and request Google recrawls only for protected
  or materially changed entrances. Do not request indexing of22 excluded URLs.
- Acceptance, queueing, crawling and actual indexing are separate outcomes.
- Keep a dated per-URL inspection roster for protected pages to distinguish
  future actual losses from aggregate report changes. Compare snippet results
  on stable complete windows; do not keep editing titles daily.
- Bing's458 Site Explorer warnings remain unclassified while its detailed
  grid is unavailable. This release does not claim to resolve those warnings.
