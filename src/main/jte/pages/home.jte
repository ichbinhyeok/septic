@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.PageMeta
@param PageMeta page
@param java.util.List<StateProfile> featuredStates
@param java.util.List<StateProfile> spotlightStates
@param java.util.List<com.example.septic.web.PageLink> featuredIntentPages = java.util.List.of()
@param java.util.List<com.example.septic.web.CountyFinderLinkView> countyFinderLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.CountyRouteClusterView> countyRouteClusters = java.util.List.of()
@param int liveGuideCount
@param int liveIntentCount
@param int liveCountyCount = 0
@param int totalCountyRouteCount = 0
@param int countySpecificRouteCount = 0
@param int preparedFieldPackCount = 0
@param int countyBackedStateCount = 0
@param int queuedStateCount
@param com.example.septic.web.WorkflowNetworkSnapshotView workflowNetworkSnapshot = null

@template.layouts.app(page = page, content = @`
    <section class="record-finder-hero home-hero">
        <div class="container">
            @template.tags.addressRecordFinder(
                finderId = "home-address-record-finder",
                eyebrow = "Septic property file workspace",
                heading = "Find, obtain, and understand a septic property file.",
                intro = "Start with an address, a failed search, or a document you already have. Keep the county route, missing evidence, and next action in one workspace.",
                pageHeading = true,
                totalCountyRouteCount = totalCountyRouteCount
            )
        </div>
    </section>

    <section class="section section--tight home-workflow" aria-labelledby="home-workflow-title">
        <div class="container">
            <div class="section-heading home-section-heading">
                <span class="eyebrow">From an address to a useful answer.</span>
                <h2 id="home-workflow-title">Find records → Review a file → Next decision.</h2>
            </div>
            <ol class="home-workflow__steps">
                <li>
                    <span>1</span>
                    <div>
                        <h3>Find records or prepare the official request</h3>
                        <p>Resolve the property county, required identifiers, and one real route to a file or a documented request.</p>
                    </div>
                </li>
                <li>
                    <span>2</span>
                    <div>
                        <h3>Review the file or official response</h3>
                        <p>Check property match, permit status, bedrooms, layout, final approval, repair history, and conflicts in one workspace.</p>
                    </div>
                </li>
                <li>
                    <span>3</span>
                    <div>
                        <h3>Take one concrete next action</h3>
                        <p>Use the evidence to request a missing record, resolve a conflict, inspect, repair, replace, or ask the responsible agency.</p>
                    </div>
                </li>
            </ol>
        </div>
    </section>

    <section class="section section--tight home-intents" aria-labelledby="home-intents-title">
        <div class="container">
            <div class="section-heading home-section-heading">
                <span class="eyebrow">Start with your question</span>
                <h2 id="home-intents-title">What are you trying to learn about the property?</h2>
            </div>
            <nav class="home-intent-list" aria-label="Common septic record questions">
                <a href="/septic-permit-search-by-address/">
                    <span><strong>Does this address have a septic permit?</strong><small>Find the county route and the identifiers that make the search work.</small></span>
                    <span>Find the permit →</span>
                </a>
                <a href="/septic-as-built-records/">
                    <span><strong>Where are the tank and drain field?</strong><small>Ask for the as-built, site sketch, or installed layout instead of a generic permit copy.</small></span>
                    <span>Find the drawing →</span>
                </a>
                <a href="/septic-bedroom-permit-checker/">
                    <span><strong>How many bedrooms were approved?</strong><small>Separate the listing count from the bedroom capacity supported by the official file.</small></span>
                    <span>Check bedrooms →</span>
                </a>
                <a href="/septic-system-cost-calculator/">
                    <span><strong>What could repair or replacement cost?</strong><small>Use a planning range and see which missing facts can still move it.</small></span>
                    <span>Plan the cost →</span>
                </a>
            </nav>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container">
            <div class="section-heading home-section-heading">
                <span class="eyebrow">High-demand record workflows</span>
                <h2>Start with the routes that already do more than explain.</h2>
                <p class="lede">Search demand is strongest on these paths, and the county routes below include a reviewed handoff or prepared official field pack. Starting-point-only examples remain visible after them.</p>
                <p class="home-route-depth" data-nosnippet><strong>${countySpecificRouteCount}</strong> county-specific handoffs · <strong>${preparedFieldPackCount}</strong> verified field packs · <strong>${Math.max(0, totalCountyRouteCount - countySpecificRouteCount)}</strong> official starting points</p>
            </div>
            <div class="home-route-list">
                <a href="/tdec-septic-records/" data-track-click="nav" data-track-source-context="home_priority_routes" data-track-target-type="internal_page">
                    <span><strong>Tennessee TDEC records</strong><small>Program, field-office, and public-records fallbacks · direct search may block</small></span>
                    <span aria-hidden="true">Open →</span>
                </a>
                <a href="/septic-records-checklist/virginia/prince-william-county/" data-track-click="nav" data-track-source-context="home_priority_routes" data-track-target-type="county_records_page">
                    <span><strong>Prince William County, Virginia</strong><small>Historical address/GPIN search · county-specific workflow</small></span>
                    <span aria-hidden="true">Open →</span>
                </a>
                <a href="/septic-records-checklist/texas/tarrant-county/" data-track-click="nav" data-track-source-context="home_priority_routes" data-track-target-type="county_records_page">
                    <span><strong>Tarrant County, Texas</strong><small>OSSF authority check and request handoff · county-specific workflow</small></span>
                    <span aria-hidden="true">Open →</span>
                </a>
                <a href="/septic-records-checklist/tennessee/hamilton-county/" data-track-click="nav" data-track-source-context="home_priority_routes" data-track-target-type="county_records_page">
                    <span><strong>Hamilton County, Tennessee</strong><small>Free document retrieval and email fallback · county-specific workflow</small></span>
                    <span aria-hidden="true">Open →</span>
                </a>
                <a href="/septic-records-checklist/north-carolina/alamance-county/" data-track-click="nav" data-track-source-context="home_priority_routes" data-track-target-type="county_records_page">
                    <span><strong>Alamance County, North Carolina</strong><small>Official request PDF and verified fields · prepared field pack</small></span>
                    <span aria-hidden="true">Open →</span>
                </a>
                <a href="/septic-records-checklist/tennessee/knox-county/" data-track-click="nav" data-track-source-context="home_priority_routes" data-track-target-type="county_records_page">
                    <span><strong>Knox County, Tennessee</strong><small>Live file-search form and verified fields · prepared field pack</small></span>
                    <span aria-hidden="true">Open →</span>
                </a>
                <a href="/septic-records-checklist/north-carolina/lincoln-county/" data-track-click="nav" data-track-source-context="home_priority_routes" data-track-target-type="county_records_page">
                    <span><strong>Lincoln County, North Carolina</strong><small>Phone-first intake and portal fallback · county-specific workflow</small></span>
                    <span aria-hidden="true">Open →</span>
                </a>
                <a href="/septic-records-checklist/north-carolina/wake-county/" data-track-click="nav" data-track-source-context="home_priority_routes" data-track-target-type="county_records_page">
                    <span><strong>Wake County, North Carolina</strong><small>iMAPS parcel match and Permit Search attachments · verified field pack</small></span>
                    <span aria-hidden="true">Open →</span>
                </a>
                <a href="/septic-records-checklist/indiana/st-joseph-county/" data-track-click="nav" data-track-source-context="home_priority_routes" data-track-target-type="county_records_page">
                    <span><strong>St. Joseph County, Indiana</strong><small>Official schematic PDF and verified fields · prepared field pack</small></span>
                    <span aria-hidden="true">Open →</span>
                </a>
                <a href="/septic-records-checklist/south-carolina/greenville-county/" data-track-click="nav" data-track-source-context="home_starting_point_routes" data-track-target-type="county_records_page">
                    <span><strong>Greenville County, South Carolina</strong><small>SCDES guidance and TMS clues · starting-point route</small></span>
                    <span aria-hidden="true">Open →</span>
                </a>
            </div>
        </div>
    </section>

    <section class="section section--tight section--muted">
        <div class="container">
            <div class="section-heading home-section-heading">
                <span class="eyebrow">If the record is missing</span>
                <h2>Finish the job without starting over.</h2>
            </div>
            <div class="home-tool-grid">
                <article>
                    <span class="home-tool-grid__number">01</span>
                    <h3>Ask for the right file</h3>
                    <p>Build a clear request for the permit, as-built, final approval, or repair history.</p>
                    <a href="/septic-records-request-builder/#records-request-builder">Create a records request</a>
                </article>
                <article>
                    <span class="home-tool-grid__number">02</span>
                    <h3>Check approved bedrooms</h3>
                    <p>Use the permit details to understand whether the septic approval may limit bedroom count.</p>
                    <a href="/septic-bedroom-permit-checker/">Check bedroom capacity</a>
                </article>
                <article>
                    <span class="home-tool-grid__number">03</span>
                    <h3>Plan the likely cost</h3>
                    <p>Get a broad planning range and see exactly which unknowns can move the price.</p>
                    <a href="/septic-system-cost-calculator/">Estimate septic cost</a>
                </article>
            </div>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container">
            <details class="home-directory">
                <summary>
                    <span>
                        <strong>Search by county instead</strong>
                        <small>Use this if you do not have the full property address.</small>
                    </span>
                    <span aria-hidden="true">Browse ${totalCountyRouteCount} county paths</span>
                </summary>
                @template.tags.countyFinder(
                    countyFinderLinks = countyFinderLinks,
                    finderId = "home-county-finder",
                    eyebrow = "County search",
                    heading = "Search by county or state.",
                    body = "Choose a result to open the local permit, records request, parcel, as-built, or inspection-letter page.",
                    sourceContext = "home_county_finder",
                    totalCountyRouteCount = totalCountyRouteCount
                )
            </details>
            <div class="home-coverage-links">
                <a href="/states/">Browse all state guides</a>
                <a href="/coverage/">See records coverage</a>
                <a href="/methodology/">How we verify sources</a>
            </div>
        </div>
    </section>
`)
