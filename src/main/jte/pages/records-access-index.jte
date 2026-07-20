@import com.example.septic.web.PageMeta
@import com.example.septic.web.RecordsAccessIndexStateView
@import com.example.septic.web.CountyFinderLinkView
@import com.example.septic.data.model.StateProfile
@param PageMeta page
@param java.util.List<RecordsAccessIndexStateView> indexStates = java.util.List.of()
@param java.util.List<CountyFinderLinkView> countyFinderLinks = java.util.List.of()
@param java.util.List<StateProfile> countyRouteStates = java.util.List.of()
@param int countyRouteStateCount = 0
@param int priorityCountyRouteCount = 0
@param int totalCountyRouteCount = 0
@param String dataLastUpdated = ""

@template.layouts.app(page = page, content = @`
    <section class="records-access-index-hero" data-records-access-index>
        <div class="container">
            <div class="records-access-index-hero__intro">
                <span class="eyebrow">Public records infrastructure</span>
                <h1>Search the county septic file path, then take the data with you.</h1>
                <p>A public index of county and state records routes backed by listed government sources. Search by address or county, compare confidence and review dates, download the CSV, and open the office that owns the permit, as-built, approval, repair file, or written no-record response.</p>
                <div class="records-access-index-hero__signals" aria-label="Records access index coverage">
                    <span><strong>${totalCountyRouteCount}</strong> county routes</span>
                    <span><strong>${countyRouteStateCount}</strong> states represented</span>
                    <span>Updated ${dataLastUpdated}</span>
                    <span>Addresses are not saved</span>
                </div>
                <div class="records-access-index-hero__actions">
                    <a class="button button--primary"
                       href="#records-access-county-search"
                       data-track-click="nav"
                       data-track-source-context="records_access_index_hero_search"
                       data-track-target-type="internal_section">Search county routes</a>
                    <a class="button button--secondary"
                       href="/septic-records-access-index.csv?v=${dataLastUpdated}"
                       data-records-index-download
                       data-track-click="nav"
                       data-track-source-context="records_access_index_hero_csv"
                       data-track-target-type="data_export">Download CSV</a>
                </div>
            </div>
            @template.tags.addressRecordFinder(
                finderId = "records-access-index",
                eyebrow = "Address-to-record route",
                heading = "Search by property address.",
                intro = "Have the address? We will resolve the county and open the closest reviewed record route. No address? Use county search instead.",
                totalCountyRouteCount = totalCountyRouteCount,
                minimal = true
            )
        </div>
    </section>

    @template.tags.countyFinder(
        countyFinderLinks = countyFinderLinks,
        stateOptions = countyRouteStates,
        finderId = "records-access-county-search",
        eyebrow = "Search the public index",
        heading = "Filter " + totalCountyRouteCount + " county records routes.",
        body = "Choose a state or search a county, then narrow by route type, first artifact, confidence, or parcel access. The filtered URL can be copied and shared.",
        sourceContext = "records_access_index_county_search",
        totalCountyRouteCount = totalCountyRouteCount,
        syncUrl = true
    )

    <section class="section section--tight records-access-embed-section" aria-labelledby="records-access-distribution">
        <div class="container records-access-distribution">
            <div>
                <span class="eyebrow">Download, cite, or share</span>
                <h2 id="records-access-distribution">Use the index as a working dataset.</h2>
                <p>The CSV includes state, county, route type, first artifact, confidence, official-source count, last review date, parcel availability, official records URL, and the SepticPath guide URL. Filtered page URLs preserve the selected state and search terms.</p>
                <p><strong>Quality note:</strong> Confidence describes route specificity and workflow depth, not the status of any property's permit. Review dates record the latest linked-source check; always confirm the government page before a transaction decision.</p>
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
            <details class="records-access-embed" open>
                <summary>Citation and reuse note</summary>
                <textarea readonly data-records-index-citation>SepticPath. "Septic Records Access Index." ${totalCountyRouteCount} official-source county septic records routes across ${countyRouteStateCount} states. Updated ${dataLastUpdated}. https://septicpath.com/septic-records-access-index/</textarea>
                <button class="button button--secondary" type="button" data-records-index-copy-citation>Copy citation</button>
                <p data-records-index-status aria-live="polite">Ready to copy. Verify the linked government source before relying on a file-status conclusion.</p>
            </details>
        </div>
    </section>

    <section class="section records-access-index-section" aria-labelledby="records-access-states">
        <div class="container">
            <div class="section-heading">
                <span class="eyebrow">Ready-made handoffs</span>
                <h2 id="records-access-states">Four priority-state packets for transaction work.</h2>
                <p>The national dataset covers every published county route. These four states also have a copy-ready handoff packet for agents, buyers, and coordinators.</p>
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
