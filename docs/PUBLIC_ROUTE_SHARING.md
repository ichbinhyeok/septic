# Public county-route sharing

The finder can share a county guide once a county route has been resolved. The
existing **Copy details** action still copies the visitor's private working note;
**Share county guide** is a separate public link with no property details.

The access index shares the selected state, route type, document type, guide
detail, and parcel-clue filter. Free-text search, other query values, and arbitrary
fragments are excluded. Shared URLs use the fixed marker `src=shared-route`.
Existing canonical URLs remain unchanged.

## Measurement

`/events/artifact-action` receives `sourceContext=route_sharing`, a public route
path, and categorical action/type values. The same action is emitted to GA4 with
the `route_` prefix and `measurement_version=public_share_v2`.

For `share_clicked`, `share_copied`, and `share_handoff`, first-party `sourcePage`
and GA `page_location` identify the allowlisted public page where sharing happened
(for example, the finder). GA `route_path` identifies the destination county guide
or index, and `artifactType` describes that destination. Query strings and fragments
are omitted. An unknown or private source path falls back to the safe destination.
For `share_arrival` and `share_official_open`, these page fields continue to identify
the shared landing path, even after navigation. Version 1 attributed sharing actions
to the destination; do not use those older events to compare originating surfaces.
First-party events do not have a version field, so use the correction's deployment
boundary when comparing their source-page counts.

| Action | What it establishes |
| --- | --- |
| `share_clicked` | The visitor tried to share a valid public route. |
| `share_copied` | Clipboard copying completed, including a successful legacy fallback. |
| `share_handoff` | The native share API resolved; recipient delivery/read is unknown. |
| `share_arrival` | A tagged public route was opened, deduplicated by landing path within a 30-minute tab session. |
| `share_official_open` | First external HTTPS official-route click in that session, attributed to its shared landing path. |

Arrivals are not unique people: a sharer can open their own link. Official clicks
are not record retrieval or completed tasks. A new tagged landing path starts a
new attribution window. With unavailable storage, deduplication works in the
current document only. GA event parameters override page location with a public
path and retain only referrer origin. No address, parcel, customer identifier,
workflow identifier, or arbitrary campaign value is added to this funnel.

Existing navigation events remain independent; do not add them to these actions
as if they were additional shares. Compare first-party events with GA4 as two
measurement systems, not a joined person-level funnel. No custom GA4 dimensions
are required to see event counts; parameter reporting setup is a separate step.

## Evaluation

Use complete post-release windows and separate Google, Bing, and referral traffic.
Check recrawl before interpreting county search changes. Small samples remain
inconclusive. National homepage, finder, and location URLs retain their current
roles and self-canonicals; the available query sample did not establish harmful
cannibalization.

For publisher adoption, record actual published links, referral/tagged arrivals,
and official-route actions separately from contact attempts or acceptance claims.
There is no approved publisher relationship implied by these features.

## Verification

Run `node --test tools/route-sharing.test.cjs` and the normal Gradle suite. Browser
checks cover clipboard behavior, filtered-index URLs, county resolution, saved
task restoration, document-mode reset, first-official-action deduplication, and
mobile overflow. Browser QA uses synthetic property context and local event storage.
