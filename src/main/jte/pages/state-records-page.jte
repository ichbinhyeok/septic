@import com.example.septic.data.model.SourceRecord
@import com.example.septic.data.model.StateMoneyPage
@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.PageMeta
@import com.example.septic.web.StateRecordsSearchResponseView
@import com.example.septic.web.AssetPaths
@param PageMeta page
@param StateMoneyPage stateMoneyPage
@param StateProfile state
@param SourceRecord primaryLocalAuthoritySource = null
@param SourceRecord primaryRecordsLookupSource = null
@param java.util.List<com.example.septic.web.PageLink> countyRecordLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> featuredCountyRecordLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> internalLinks = java.util.List.of()
@param StateRecordsSearchResponseView stateRecordsSearchResponse = null
@param String editorialLastReviewedAt = ""
@param int totalCountyRouteCount = 0

@template.layouts.app(page = page, content = @`
    <div class="state-records-page">
        <section class="state-records-hero">
            <div class="container state-records-hero__inner">
                <div class="state-records-hero__copy">
                    <span class="eyebrow">${state.stateCode()} property records</span>
                    @if ("NC".equals(state.stateCode()))
                        <h1>North Carolina septic records by county</h1>
                        <p>Find the county permit file owner by address, then open the official route for the permit copy, as-built, final approval, repair record, or written no-record response.</p>
                    @else
                        <h1>Find ${state.stateName()} septic records</h1>
                        <p>Enter the property address to identify the county, file owner, official search or request route, and the permit documents to ask for.</p>
                    @endif
                    <div class="state-records-hero__promise" role="note">
                        <strong>Start here</strong>
                        <span>Enter the property address. SepticPath identifies the county and the best verified public route; it does not claim that a permit exists or retrieve a government file automatically.</span>
                    </div>
                </div>
                @template.tags.addressRecordFinder(
                    finderId = "state-records-" + state.stateCode().toLowerCase(),
                    eyebrow = "Find the file owner",
                    heading = "Enter the property address",
                    intro = "Use the full street address, city, state, and ZIP so the county route can be resolved accurately.",
                    totalCountyRouteCount = totalCountyRouteCount,
                    minimal = true,
                    expectedStateCode = state.stateCode()
                )
            </div>
        </section>

        <section class="state-records-route">
            <div class="container state-records-route__grid">
                <div>
                    <span class="eyebrow">What happens next</span>
                    <h2>Use the office that owns this property file</h2>
                    @if (stateRecordsSearchResponse != null)
                        <p>${stateRecordsSearchResponse.summary()}</p>
                    @else
                        <p>Septic records may be held by a state program, county health office, local authorized agent, or an older archive. The county result determines the correct next step.</p>
                    @endif
                    @if ("NC".equals(state.stateCode()))
                        <a class="state-records-route__workspace" href="/north-carolina-septic-permit-lookup/">Use the NC county permit workspace</a>
                    @elseif ("SC".equals(state.stateCode()))
                        <a class="state-records-route__workspace" href="/dhec-septic-permit-lookup/">Use the SCDES records workspace</a>
                    @elseif ("TX".equals(state.stateCode()))
                        <a class="state-records-route__workspace" href="/texas-ossf-records-search/">Find the Texas OSSF permitting authority</a>
                    @elseif ("TN".equals(state.stateCode()))
                        <a class="state-records-route__workspace" href="/tdec-septic-records/">Use the Tennessee SSDS records workspace</a>
                    @endif
                    @if (primaryRecordsLookupSource != null)
                        <a class="button button--secondary" href="${primaryRecordsLookupSource.url()}" target="_blank" rel="noreferrer" data-track-click="official_source" data-track-source-context="state_records_hub" data-track-target-type="official_source">Open ${primaryRecordsLookupSource.agencyName()}</a>
                        <small>Official statewide starting point. A county route may still be required.</small>
                    @elseif (primaryLocalAuthoritySource != null)
                        <a class="button button--secondary" href="${primaryLocalAuthoritySource.url()}" target="_blank" rel="noreferrer" data-track-click="official_source" data-track-source-context="state_records_hub" data-track-target-type="official_source">Open ${primaryLocalAuthoritySource.agencyName()}</a>
                        <small>Official agency source. Confirm the property county before requesting a file.</small>
                    @endif
                </div>
                <div class="state-records-route__steps">
                    <ol>
                        @for (var step : stateMoneyPage.decisionSteps().stream().limit(3).toList())
                            <li><span>${step}</span></li>
                        @endfor
                    </ol>
                </div>
            </div>
        </section>

        <section class="section section--tight closing-risk-cta-section">
            <div class="container">
                @template.tags.closingRiskCta(
                    sourceContext = "state_records_" + state.stateCode().toLowerCase(),
                    heading = "Have a real property and deadline? Turn the record route into a closing decision."
                )
            </div>
        </section>

        @template.tags.stateRecordsReturn(
            stateCode = state.stateCode(),
            stateName = state.stateName(),
            officeLabel = state.stateName() + " septic records office"
        )

        @if (!countyRecordLinks.isEmpty())
            <section class="state-records-counties" id="county-pages">
                <div class="container">
                    <span class="eyebrow">Already know the county?</span>
                    <h2>Find your ${state.stateName()} county</h2>
                    <p>County pages tell you whether the working route is an online search, form, email, call, or public-records request.</p>
                    @if (!featuredCountyRecordLinks.isEmpty())
                        <nav class="state-records-priority-counties" aria-label="Popular ${state.stateName()} county permit searches">
                            @for (var link : featuredCountyRecordLinks.stream().limit(6).toList())
                                <a href="${link.path()}">${link.compactTitle()}</a>
                            @endfor
                        </nav>
                    @endif
                    <form class="state-records-county-picker" data-county-route-picker data-county-route-prefix="/septic-records-checklist/${state.slug()}/" data-county-route-state="${state.stateName()}">
                        <label for="${state.stateCode().toLowerCase()}-records-county">County</label>
                        <div>
                            <select id="${state.stateCode().toLowerCase()}-records-county" data-county-route-select required>
                                <option value="">Choose a county</option>
                                @for (var link : countyRecordLinks)
                                    <option value="${link.path()}">${link.compactTitle()}</option>
                                @endfor
                            </select>
                            <button class="button button--primary" type="submit" data-county-route-submit disabled>Open county route</button>
                        </div>
                        <p data-county-route-status aria-live="polite">Choose a county to open its local permit-record workflow.</p>
                    </form>
                    <details class="state-records-county-index">
                        <summary>Browse all ${countyRecordLinks.size()} verified county routes</summary>
                        <nav aria-label="All verified ${state.stateName()} county record routes">
                            @for (var link : countyRecordLinks)
                                <a href="${link.path()}">${link.compactTitle()}</a>
                            @endfor
                        </nav>
                    </details>
                </div>
            </section>
        @endif

        <section class="state-records-documents">
            <div class="container state-records-documents__grid">
                <div>
                    <span class="eyebrow">Request the useful artifact</span>
                    <h2>Do not stop at “a record was found”</h2>
                    <p>The useful answer is usually inside the actual permit packet, drawing, approval, or repair history.</p>
                </div>
                <ul>
                    @if (state.recordsToRequest() != null)
                        @for (var record : state.recordsToRequest().stream().limit(6).toList())
                            <li>${record}</li>
                        @endfor
                    @endif
                </ul>
            </div>
        </section>

        <section class="state-records-outcomes">
            <div class="container">
                <span class="eyebrow">If the first route fails</span>
                <h2>An empty search is not a no-record determination</h2>
                <div>
                    <details><summary>No result appeared</summary><p>Retry with parcel ID, current and prior owner, subdivision, lot, legal description, or permit number. Then ask the file owner for a written response.</p></details>
                    <details><summary>The government site was blocked</summary><p>Use the county page or agency contact route. A browser block does not establish that the property has no file.</p></details>
                    <details><summary>The file is incomplete or conflicts with the property</summary><p>Save every document and identify the exact missing fact: installed layout, final approval, bedroom use, repair history, or current condition. That determines whether an office follow-up or professional inspection is next.</p></details>
                </div>
            </div>
        </section>

        <section class="state-records-reference">
            <div class="container">
                <p><strong>Independent routing guide.</strong> SepticPath is not a government agency and does not certify permit existence, compliance, or system condition. Last route review activity: ${editorialLastReviewedAt}</p>
                <details>
                    <summary>Questions and source context</summary>
                    @if (stateRecordsSearchResponse != null && !stateRecordsSearchResponse.responseRows().isEmpty())
                        <dl class="state-records-reference__facts">
                            @for (var row : stateRecordsSearchResponse.responseRows())
                                <div><dt>${row.label()}</dt><dd>${row.value()}</dd></div>
                            @endfor
                        </dl>
                    @endif
                    <div class="state-records-reference__supporting">
                        @for (var item : stateMoneyPage.fitBullets())
                            <p>${item}</p>
                        @endfor
                        @for (var item : stateMoneyPage.quotePrepChecklist())
                            <p>${item}</p>
                        @endfor
                    </div>
                    @if (!internalLinks.isEmpty())
                        <nav class="state-records-reference__links" aria-label="Related ${state.stateName()} septic workflows">
                            @for (var link : internalLinks)
                                <a href="${link.path()}">${link.compactTitle()}</a>
                            @endfor
                        </nav>
                    @endif
                    @for (var faq : stateMoneyPage.faqBlocks())
                        <article><h2>${faq.question()}</h2><p>${faq.answer()}</p></article>
                    @endfor
                </details>
            </div>
        </section>
        <script src="${AssetPaths.versioned("/state-records-return.js")}" defer></script>
    </div>
`)
