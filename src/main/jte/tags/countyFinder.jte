@import com.example.septic.web.CountyFinderLinkView
@param java.util.List<CountyFinderLinkView> countyFinderLinks = java.util.List.of()
@param String finderId = "county-finder"
@param String eyebrow = "County finder"
@param String heading = "Type a county or state and open the local septic file path."
@param String body = "Search the live county workflow pages by county name, state, permit lookup, records request, parcel/TMS, as-built, or inspection letter."
@param String sourceContext = "county_finder"
@param int totalCountyRouteCount = 0

@if (!countyFinderLinks.isEmpty())
    <section class="section section--tight county-finder-section">
        <div class="container">
            <div class="county-finder" data-county-finder>
                <div class="county-finder__intro">
                    <span class="eyebrow">${eyebrow}</span>
                    <h2>${heading}</h2>
                    <p>${body}</p>
                    <div class="county-finder__stats" aria-label="County finder coverage">
                        <span><strong>${totalCountyRouteCount > 0 ? totalCountyRouteCount : countyFinderLinks.size()}</strong> live county pages</span>
                        <span><strong>${countyFinderLinks.stream().filter(link -> link.confidenceScore() >= 82).count()}</strong> high-confidence routes</span>
                        <span>${countyFinderLinks.size()} priority routes loaded</span>
                        <span>${countyFinderLinks.stream().filter(CountyFinderLinkView::parcelAnchorAvailable).count()} parcel anchors</span>
                        <span>Permit lookup</span>
                        <span>Records request</span>
                        <span>As-built</span>
                    </div>
                </div>

                <div class="county-finder__panel">
                    <div class="county-finder__priority" aria-label="Priority county routes">
                        <span>Push first</span>
                        <div>
                            @for (var link : countyFinderLinks.stream().limit(12).toList())
                                <a href="${link.path()}"
                                   data-track-click="nav"
                                   data-track-source-context="${sourceContext}_priority_route"
                                   data-track-target-type="county_records_page">${link.countyName()}, ${link.stateCode()} · ${link.confidenceScore()}%</a>
                            @endfor
                        </div>
                    </div>
                    <label class="county-finder__label" for="${finderId}-input">Find a county route</label>
                    <div class="county-finder__search">
                        <input id="${finderId}-input"
                               type="search"
                               inputmode="search"
                               autocomplete="off"
                               placeholder="Try Wake or Greenville"
                               data-county-finder-input>
                        <button class="button button--secondary"
                                type="button"
                                data-county-finder-clear>Clear</button>
                    </div>
                    <div class="county-finder__status" aria-live="polite">
                        <strong data-county-finder-count>${countyFinderLinks.size()} priority county routes indexed</strong>
                        <span>Showing the strongest matches first.</span>
                    </div>
                    <div class="county-finder__results" data-county-finder-results>
                        @for (var link : countyFinderLinks)
                            <a class="county-finder__result"
                               href="${link.path()}"
                               data-county-finder-result
                               data-search="${link.searchText()}"
                               data-share-url="${link.absoluteUrl()}"
                               data-share-query="${link.countyName()} ${link.stateCode()} septic permit lookup"
                               data-track-click="nav"
                               data-track-source-context="${sourceContext}"
                               data-track-target-type="county_records_page">
                                <div class="county-finder__result-top">
                                    <span>${link.title()}</span>
                                    <strong>${link.confidenceScore()}%</strong>
                                </div>
                                <div class="county-finder__result-meta" aria-label="${link.title()} route metadata">
                                    <span>${link.confidenceLabel()}</span>
                                    <span>${link.requestMethodLabel()}</span>
                                    <span>${link.sourceDepthLabel()}</span>
                                    @if (link.parcelAnchorAvailable())
                                        <span>Parcel anchor</span>
                                    @endif
                                </div>
                                <small><strong>First pull:</strong> ${link.firstArtifactLabel()}</small>
                                <small>${link.note()}</small>
                            </a>
                        @endfor
                    </div>
                    <p class="county-finder__empty" data-county-finder-empty hidden>No matching county route yet. Try the state name, a nearby county, or open the state records lookup.</p>
                </div>
            </div>
        </div>
    </section>
@endif
