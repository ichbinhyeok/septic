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
@param int countyBackedStateCount = 0
@param int queuedStateCount
@param com.example.septic.web.WorkflowNetworkSnapshotView workflowNetworkSnapshot = null

@template.layouts.app(page = page, content = @`
    <section class="record-finder-hero home-hero">
        <div class="container">
            @template.tags.addressRecordFinder(
                finderId = "home-address-record-finder",
                eyebrow = "Start with the property",
                heading = "Find septic records for an address.",
                intro = "We identify the county, show what to look for, and open the best available official records site. Your address is not saved.",
                pageHeading = true,
                totalCountyRouteCount = totalCountyRouteCount
            )
        </div>
    </section>

    <section class="section section--tight home-workflow" aria-labelledby="home-workflow-title">
        <div class="container">
            <div class="section-heading home-section-heading">
                <span class="eyebrow">What happens next</span>
                <h2 id="home-workflow-title">From an address to a useful answer.</h2>
            </div>
            <ol class="home-workflow__steps">
                <li>
                    <span>1</span>
                    <div>
                        <h3>Find the right office</h3>
                        <p>We match the property to its county and the best records source we know.</p>
                    </div>
                </li>
                <li>
                    <span>2</span>
                    <div>
                        <h3>Check the official file</h3>
                        <p>Open the government site in a new tab with a short checklist of what to search for.</p>
                    </div>
                </li>
                <li>
                    <span>3</span>
                    <div>
                        <h3>Use the result without retyping it</h3>
                        <p>Upload a document for purpose-specific checks, or carry the property details into a records request.</p>
                    </div>
                </li>
            </ol>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container">
            <div class="section-heading home-section-heading">
                <span class="eyebrow">Popular record searches</span>
                <h2>Go straight to a proven local path.</h2>
                <p class="lede">These pages already help homeowners reach the correct state or county records system.</p>
            </div>
            <div class="home-route-list">
                <a href="/tdec-septic-records/" data-track-click="nav" data-track-source-context="home_proven_routes" data-track-target-type="internal_page">
                    <span><strong>Tennessee</strong><small>TDEC septic records and county fallback</small></span>
                    <span aria-hidden="true">Open →</span>
                </a>
                <a href="/septic-records-checklist/north-carolina/wake-county/" data-track-click="nav" data-track-source-context="home_proven_routes" data-track-target-type="county_records_page">
                    <span><strong>Wake County, North Carolina</strong><small>Permit Portal and parcel records</small></span>
                    <span aria-hidden="true">Open →</span>
                </a>
                <a href="/septic-records-checklist/indiana/st-joseph-county/" data-track-click="nav" data-track-source-context="home_proven_routes" data-track-target-type="county_records_page">
                    <span><strong>St. Joseph County, Indiana</strong><small>Health files, permits, and soil reports</small></span>
                    <span aria-hidden="true">Open →</span>
                </a>
                <a href="/septic-records-checklist/south-carolina/greenville-county/" data-track-click="nav" data-track-source-context="home_proven_routes" data-track-target-type="county_records_page">
                    <span><strong>Greenville County, South Carolina</strong><small>Parcel/TMS and SCDES permit copies</small></span>
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
