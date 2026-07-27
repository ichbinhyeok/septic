@import com.example.septic.web.PageMeta
@import com.example.septic.web.RecordsAccessIndexStateView
@import com.example.septic.web.CountyFinderLinkView
@import com.example.septic.web.CountyRouteClusterView
@import com.example.septic.data.model.StateProfile
@param PageMeta page
@param java.util.List<RecordsAccessIndexStateView> indexStates = java.util.List.of()
@param java.util.List<CountyFinderLinkView> countyFinderLinks = java.util.List.of()
@param java.util.List<StateProfile> countyRouteStates = java.util.List.of()
@param java.util.List<CountyRouteClusterView> stateDirectory = java.util.List.of()
@param int countyRouteStateCount = 0
@param int priorityCountyRouteCount = 0
@param int totalCountyRouteCount = 0
@param String dataLastUpdated = ""

@template.layouts.app(page = page, content = @`
    <section class="records-access-index-hero" data-records-access-index>
        <div class="container">
            <div class="records-access-index-hero__intro">
                <span class="eyebrow">Find the file owner</span>
                <h1>Find the county office that has the septic file.</h1>
                <p>Start with an address or county. We will show the reviewed public route, the first file to request, and the fallback when an online search comes up empty.</p>
                <div class="records-access-index-hero__signals" aria-label="Records access index coverage">
                    <span><strong>${totalCountyRouteCount}</strong> county routes</span>
                    <span><strong>${countyRouteStateCount}</strong> states represented</span>
                    <span>Updated ${dataLastUpdated}</span>
                </div>
                <div class="records-access-index-hero__actions">
                    <a class="button button--primary"
                       href="#records-access-county-search"
                       data-track-click="nav"
                       data-track-source-context="records_access_index_hero_search"
                       data-track-target-type="internal_section">Search by county</a>
                    <a class="button button--secondary"
                       href="/septic-records-access-index.csv?v=${dataLastUpdated}"
                       data-records-index-download
                       data-track-click="nav"
                       data-track-source-context="records_access_index_hero_csv"
                       data-track-target-type="data_export">Download route data</a>
                </div>
            </div>
            @template.tags.addressRecordFinder(
                finderId = "records-access-index",
                eyebrow = "Start with the property",
                heading = "Enter the address to find its records office.",
                intro = "We identify the county and show the closest reviewed public route. SepticPath does not save the address.",
                totalCountyRouteCount = totalCountyRouteCount,
                minimal = true
            )
        </div>
    </section>

    @template.tags.countyFinder(
        countyFinderLinks = countyFinderLinks,
        stateOptions = countyRouteStates,
        finderId = "records-access-county-search",
        eyebrow = "No full address?",
        heading = "Find the records route by county.",
        body = "Type the county or state. Open More filters only when you need a specific file, online route, or parcel-search clue.",
        sourceContext = "records_access_index_county_search",
        totalCountyRouteCount = totalCountyRouteCount,
        syncUrl = true
    )

    <section class="section section--tight records-access-state-section"
             aria-labelledby="records-access-state-directory"
             data-records-state-directory
             data-state-count="${stateDirectory.size()}">
        <div class="container records-access-state-directory">
            <div class="records-access-state-directory__intro">
                <span class="eyebrow">Browse by state</span>
                <h2 id="records-access-state-directory">See the states with reviewed county routes.</h2>
                <p>Choose a state when the county is not yet known or the local search needs a broader fallback.</p>
            </div>
            <nav aria-label="State septic records directories">
                <ul class="records-access-state-list">
                    @for (var state : stateDirectory)
                        <li>
                            <a href="${state.stateRecordsLink().path()}"
                               data-track-click="nav"
                               data-track-source-context="records_access_index_all_states"
                               data-track-target-type="state_records_page">
                                <span>${state.stateCode()}</span>
                                <strong>${state.stateName()} records</strong>
                                <small>${state.liveCountyCount()} county route${state.liveCountyCount() == 1 ? "" : "s"}</small>
                            </a>
                        </li>
                    @endfor
                </ul>
            </nav>
        </div>
    </section>

    <section class="section section--tight records-access-embed-section" aria-labelledby="records-access-distribution">
        <div class="container">
            <details class="records-access-data-details">
                <summary>Download, share, or cite the full route dataset</summary>
                <div class="records-access-distribution">
                    <div>
                        <span class="eyebrow">For research and professional reuse</span>
                        <h2 id="records-access-distribution">Use all ${totalCountyRouteCount} routes as a working dataset.</h2>
                        <p>The CSV includes the county, route type, first file, route confidence, review date, parcel availability, official URL, and SepticPath guide URL.</p>
                        <p><strong>Important:</strong> Route confidence describes how clearly the file path is documented. It does not prove a property's permit status.</p>
                        <div class="records-access-card__actions">
                            <a class="button button--primary"
                               href="/septic-records-access-index.csv?v=${dataLastUpdated}"
                               data-records-index-download
                               data-track-click="nav"
                               data-track-source-context="records_access_index_distribution_csv"
                               data-track-target-type="data_export">Download all ${totalCountyRouteCount} routes</a>
                            <button class="button button--secondary"
                                    type="button"
                                    data-share-route
                                    data-share-title="Septic Records Access Index"
                                    data-share-text="${totalCountyRouteCount} official-source county septic records routes across ${countyRouteStateCount} states."
                                    data-track-source-context="records_access_index_filtered_share">Share filtered view</button>
                        </div>
                    </div>
                    <details class="records-access-embed">
                        <summary>Citation and reuse note</summary>
                        <textarea readonly data-records-index-citation>SepticPath. "Septic Records Access Index." ${totalCountyRouteCount} official-source county septic records routes across ${countyRouteStateCount} states. Updated ${dataLastUpdated}. https://septicpath.com/septic-records-access-index/</textarea>
                        <button class="button button--secondary" type="button" data-records-index-copy-citation>Copy citation</button>
                        <p data-records-index-status aria-live="polite">Ready to copy. Verify the linked government source before relying on a file-status conclusion.</p>
                    </details>
                </div>
            </details>
        </div>
    </section>

    <section class="section records-access-index-section" aria-labelledby="records-access-states">
        <div class="container">
            <div class="section-heading">
                <span class="eyebrow">For agents and professionals</span>
                <h2 id="records-access-states">Reusable packets for four priority states.</h2>
                <p>These optional packets turn the route into a message that can be forwarded to a buyer, seller, or coordinator.</p>
            </div>
            <div class="records-access-index-grid">
                @for (var state : indexStates)
                    <article class="records-access-card">
                        <div class="records-access-card__topline">
                            <span>${state.stateCode()}</span>
                            <strong>${state.stateName()}</strong>
                        </div>
                        <p>${state.firstStep()}</p>
                        <div class="records-access-card__metrics" aria-label="${state.stateName()} coverage">
                            <span><strong>${state.countyRouteCount()}</strong> county routes</span>
                            <span><strong>${state.highConfidenceRouteCount()}</strong> high-confidence</span>
                            @if (state.directOnlineRouteCount() > 0)
                                <span><strong>${state.directOnlineRouteCount()}</strong> direct online</span>
                            @endif
                        </div>
                        <div class="records-access-card__actions">
                            <a class="button button--primary" href="${state.recordsPath()}" data-track-click="nav" data-track-source-context="records_access_index_state_route" data-track-target-type="state_records_page">Open ${state.stateName()} records</a>
                            <a class="button button--secondary" href="${state.packetPath()}" data-track-click="nav" data-track-source-context="records_access_index_packet" data-track-target-type="workflow_packet">${state.packetLabel()}</a>
                        </div>
                    </article>
                @endfor
            </div>
        </div>
    </section>

`)
