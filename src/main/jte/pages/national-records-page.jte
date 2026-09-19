@import com.example.septic.data.model.ContentPage
@import com.example.septic.web.PageMeta
@param PageMeta page
@param ContentPage contentPage
@param String contentQuickAnswer = ""
@param java.util.List<com.example.septic.web.PageLink> featuredInternalLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.CountyFinderLinkView> countyFinderLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.OfficialRecordToolView> officialRecordTools = java.util.List.of()
@param java.util.List<com.example.septic.data.model.StateProfile> officialRecordToolStates = java.util.List.of()
@param java.util.List<com.example.septic.data.model.StateProfile> states = java.util.List.of()
@param String editorialLastReviewedAt = ""
@param int totalCountyRouteCount = 0

@template.layouts.app(page = page, designFamily = "directory", content = @`
    <div class="national-records-page">
        <section class="national-records-hero">
            <div class="container national-records-hero__inner">
                <div class="national-records-hero__copy">
                    <span class="eyebrow">Property record workflow</span>
                    <h1>${contentPage.title()}</h1>
                    <p>${contentQuickAnswer}</p>
                    @if ("septic-as-built-records".equals(contentPage.slug()))
                        <a class="drawing-guide-jump" href="#read-a-septic-drawing">See a real returned drawing and how to read it ↓</a>
                    @elseif ("septic-tank-location-records".equals(contentPage.slug()))
                        <a class="drawing-guide-jump" href="#location-projects">Read the guide for pools, additions, or pumping ↓</a>
                    @elseif ("how-to-find-septic-records-online".equals(contentPage.slug()))
                        <a class="drawing-guide-jump" href="#no-record-online">Understand what a missing search result means ↓</a>
                    @elseif ("septic-permit-search-by-address".equals(contentPage.slug()))
                        <a class="drawing-guide-jump" href="#old-address-records">Check old addresses and property matches ↓</a>
                    @endif
                    <div class="national-records-hero__boundary" role="note">
                        <strong>What you receive here</strong>
                        <span>The county, likely file owner, verified official route, useful search fields, and the next step for found, missing, blocked, or incomplete files.</span>
                    </div>
                    <div class="national-records-service-entry" aria-label="SepticPath assisted record research">
                        <div>
                            <strong>Prefer a human search?</strong>
                            <span>We’ll investigate public sources, contact the responsible office when needed, and explain what comes back.</span>
                        </div>
                        <a class="button button--primary"
                           href="/offer-prep-septic-file-check/#record-help"
                           data-record-help-cta
                           data-track-click="nav"
                           data-track-source-context="national_records_hero_record_help"
                           data-track-target-type="lead_form">Ask SepticPath to investigate</a>
                    </div>
                </div>
                @template.tags.addressRecordFinder(
                    finderId = "national-records-" + contentPage.slug(),
                    eyebrow = "Start with the property",
                    heading = "Enter the address to find the right office",
                    intro = "A full address resolves the county and starts the official route. If the public search stops, SepticPath can investigate the record and contact the responsible office for you.",
                    totalCountyRouteCount = totalCountyRouteCount,
                    minimal = true
                )
            </div>
        </section>

        @if ("septic-as-built-records".equals(contentPage.slug()))
            @template.tags.septicDrawingGuide()
        @endif

        @template.tags.recordLearningGuide(slug = contentPage.slug())

        <div class="container record-evidence-band">
            @template.tags.recordEvidence(sourceContext = "national_records_evidence")
        </div>

        <section class="national-records-intent">
            <div class="container">
                <span class="eyebrow">Choose the answer you need</span>
                <h2>The record search is only the first step</h2>
                <div class="national-records-intent__rows">
                    <a href="/septic-permit-search-by-address/"><strong>Find the permit file</strong><span>Resolve county and official source</span></a>
                    <a href="/septic-as-built-records/"><strong>Find the installed layout</strong><span>Ask for as-built, site sketch, or approved plan</span></a>
                    @if (!"septic-tank-location-records".equals(contentPage.slug()))
                        <a href="/septic-tank-location-records/"><strong>Locate the tank and drain field</strong><span>Start with the record drawing before arranging a professional locate</span></a>
                    @endif
                    <a href="/septic-records-request-builder/"><strong>No record appeared</strong><span>Prepare a precise written request</span></a>
                    <a href="/offer-prep-septic-file-check/#record-help" data-record-help-cta data-track-click="nav" data-track-source-context="national_records_record_help" data-track-target-type="lead_form"><strong>Still can’t find the right file?</strong><span>Send the address. We’ll investigate, contact the responsible office when needed, and explain the response.</span></a>
                </div>
            </div>
        </section>

        <section class="national-records-method">
            <div class="container national-records-method__grid">
                <div>
                    <span class="eyebrow">A defensible search</span>
                    <h2>Carry more than the street address</h2>
                    <p>Government archives may be indexed under an earlier owner, parcel, subdivision, lot, legal description, or permit number. A failed address search is not proof that no file exists.</p>
                </div>
                <dl>
                    <div><dt>Search clues</dt><dd>Address, parcel/APN, current and prior owner, subdivision, lot, legal description, permit number.</dd></div>
                    <div><dt>Useful documents</dt><dd>Permit/application, soil or site evaluation, approved plan, as-built, final approval, repair history.</dd></div>
                    <div><dt>Safe fallback</dt><dd>Ask the file owner for a written no-record response and the next archive or delegated office to contact.</dd></div>
                </dl>
            </div>
        </section>

        @if ("septic-tank-location-records".equals(contentPage.slug()) && !officialRecordTools.isEmpty())
            <section class="section section--tight" id="official-septic-map-directory">
                <div class="container">
                    <div class="official-map-directory" data-official-map-directory>
                        <header class="official-map-directory__header">
                            <div>
                                <span class="eyebrow">Official map &amp; GIS directory</span>
                                <h2>Open the public tool that can narrow the tank or drain-field search.</h2>
                                <p>These are reviewed government map, GIS, parcel, and direct permit-search routes. A mapped point or permit hit is evidence to inspect—not a field locate or proof of current condition.</p>
                            </div>
                            <dl class="official-map-directory__coverage" aria-label="Official map directory coverage">
                                <div><dt>Official routes</dt><dd>${officialRecordTools.size()}</dd></div>
                                <div><dt>States represented</dt><dd>${officialRecordToolStates.size()}</dd></div>
                            </dl>
                        </header>

                        <div class="official-map-directory__controls" aria-label="Filter official septic map routes">
                            <label class="official-map-directory__query">
                                <span>County or state</span>
                                <input type="search" autocomplete="off" placeholder="Try Gwinnett, Colorado, or NC" data-map-directory-query>
                            </label>
                            <label>
                                <span>Tool type</span>
                                <select data-map-directory-type>
                                    <option value="">All public tools</option>
                                    <option value="record_map">Septic map or GIS</option>
                                    <option value="direct_search">Direct permit search</option>
                                    <option value="parcel_map">Parcel map handoff</option>
                                </select>
                            </label>
                            <label>
                                <span>State</span>
                                <select data-map-directory-state>
                                    <option value="">All states</option>
                                    @for (var state : officialRecordToolStates)
                                        <option value="${state.stateCode()}" data-map-directory-state-option>${state.stateName()}</option>
                                    @endfor
                                </select>
                            </label>
                            <div class="official-map-directory__toggles">
                                <label class="official-map-directory__toggle">
                                    <input type="checkbox" data-map-directory-parcel>
                                    <span>Parcel clue</span>
                                </label>
                                <label class="official-map-directory__toggle">
                                    <input type="checkbox" data-map-directory-documents>
                                    <span>Drawing/document clue</span>
                                </label>
                            </div>
                            <button class="button button--quiet" type="button" data-map-directory-clear>Clear filters</button>
                        </div>

                        <div class="official-map-directory__status" aria-live="polite">
                            <strong data-map-directory-count>${officialRecordTools.size()} official routes</strong>
                            <span>Government sources open in a new tab.</span>
                        </div>

                        <div class="official-map-directory__list" data-map-directory-list>
                            @for (var tool : officialRecordTools)
                                <article class="official-map-route"
                                         data-map-directory-route
                                         data-map-type="${tool.toolType()}"
                                         data-map-state="${tool.stateCode()}"
                                         data-map-parcel="${tool.parcelClueAvailable()}"
                                         data-map-documents="${tool.documentClueAvailable()}"
                                         data-map-search="${tool.searchText()}">
                                    <div class="official-map-route__identity">
                                        <span>${tool.stateCode()} · ${tool.toolTypeLabel()}</span>
                                        <h3>${tool.countyName()}</h3>
                                    </div>
                                    <div class="official-map-route__detail">
                                        <p><strong>Search clues:</strong> ${tool.lookupClues()}</p>
                                        <p><strong>First file to inspect:</strong> ${tool.firstArtifactLabel()}</p>
                                    </div>
                                    <div class="official-map-route__actions">
                                        <a class="button button--primary"
                                           href="${tool.officialUrl()}"
                                           target="_blank"
                                           rel="noreferrer"
                                           data-track-click="nav"
                                           data-track-source-context="tank_location_official_map"
                                           data-track-target-type="official_source">${tool.officialLabel()}</a>
                                        <a href="${tool.countyPath()}"
                                           data-track-click="nav"
                                           data-track-source-context="tank_location_map_county_guide"
                                           data-track-target-type="county_records_page">Read the county handoff</a>
                                    </div>
                                </article>
                            @endfor
                        </div>
                        <p class="official-map-directory__empty" data-map-directory-empty hidden>No matching public tool in the reviewed set. Clear the filters or use the county request route below.</p>

                        <div class="official-map-directory__fallback">
                            <div>
                                <span class="eyebrow">No public map?</span>
                                <h3>A missing map does not mean the septic file is missing.</h3>
                                <p>Use the county finder below for request forms, office contacts, and written no-record follow-up.</p>
                            </div>
                            <a class="button button--secondary" href="#${contentPage.slug()}-county-finder">Find the county request route</a>
                        </div>
                    </div>
                </div>
            </section>
        @endif

        @template.tags.countyFinder(
            countyFinderLinks = countyFinderLinks,
            stateOptions = states,
            finderId = contentPage.slug() + "-county-finder",
            eyebrow = "County finder",
            heading = "Search the county route instead of stopping at the national guide.",
            body = "Use this when you know the state or county but do not have a complete property address.",
            sourceContext = "national_records_county_finder",
            totalCountyRouteCount = totalCountyRouteCount
        )

        <section class="national-records-outcomes">
            <div class="container">
                <span class="eyebrow">After the handoff</span>
                <h2>Continue from the result you actually received</h2>
                <div>
                    <details><summary>I downloaded a permit or PDF</summary><p>Save the original. Compare the address and parcel, permit number, approved use or bedrooms, installed layout, final approval, and later repairs. Do not infer current condition from an old permit.</p></details>
                    <details><summary>The portal returned no result</summary><p>Retry with alternate identifiers, then contact the office. Only a written office response should be treated as a confirmed no-record result.</p></details>
                    <details><summary>The file exists but is incomplete</summary><p>Name the missing artifact and request it directly. If the installed layout or current condition still cannot be established, a professional locate or inspection may be the next step.</p></details>
                </div>
            </div>
        </section>

        @if (!featuredInternalLinks.isEmpty())
            <section class="national-records-related">
                <div class="container">
                    <span class="eyebrow">Related workflows</span>
                    <nav aria-label="Related septic records workflows">
                        @for (var link : featuredInternalLinks)
                            <a href="${link.path()}"><strong>${link.compactTitle()}</strong><span>Open workflow</span></a>
                        @endfor
                    </nav>
                </div>
            </section>
        @endif

        <section class="national-records-reference">
            <div class="container">
                <p><strong>Independent guide.</strong> SepticPath is not a government agency and does not certify record existence, compliance, or system condition. Last review activity: ${editorialLastReviewedAt}</p>
                <details>
                    <summary>Questions and supporting guidance</summary>
                    @for (var faq : contentPage.faqBlocks())
                        <article><h2>${faq.question()}</h2><p>${faq.answer()}</p></article>
                    @endfor
                </details>
            </div>
        </section>
    </div>
`)
