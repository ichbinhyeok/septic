@import com.example.septic.data.model.ContentPage
@import com.example.septic.data.model.CountyRecordsPage
@import com.example.septic.web.PageMeta
@import com.example.septic.web.AssetPaths
@param PageMeta page
@param ContentPage contentPage
@param java.util.List<CountyRecordsPage> northCarolinaCountyRoutes = java.util.List.of()
@param java.util.List<CountyRecordsPage> northCarolinaPriorityCountyRoutes = java.util.List.of()
@param String editorialLastReviewedAt = ""

@template.layouts.app(page = page, content = @`
    <div class="tdec-desk nc-records-desk" data-nc-records-desk>
        <section class="tdec-desk__intro" aria-labelledby="nc-page-title">
            <div class="container tdec-desk__intro-grid">
                <div>
                    <span class="eyebrow">North Carolina existing-system records</span>
                    <h1 id="nc-page-title">Find the county that holds the septic permit</h1>
                    <p>North Carolina does not provide one statewide property-level septic permit search. Choose the county, prepare a property clue, and open that county's verified search or request path.</p>
                </div>
                <dl class="tdec-desk__scope" aria-label="What this tool can do">
                    <div><dt>SepticPath does</dt><dd>route verified counties, prepare search keys, and draft a file request.</dd></div>
                    <div><dt>SepticPath does not</dt><dd>query county systems, download a permit, or confirm that a record exists.</dd></div>
                </dl>
            </div>
        </section>

        @if (!northCarolinaPriorityCountyRoutes.isEmpty())
            <section class="nc-priority-routes" aria-labelledby="nc-priority-routes-title">
                <div class="container nc-priority-routes__inner">
                    <header>
                        <span class="eyebrow">Popular county permit searches</span>
                        <h2 id="nc-priority-routes-title">Go straight to the county file route</h2>
                        <p>These county pages explain the official portal, form, email, or phone path and what to do when the first search is empty.</p>
                    </header>
                    <nav class="nc-priority-routes__links" aria-label="Popular North Carolina county septic permit lookups">
                        @for (var county : northCarolinaPriorityCountyRoutes.stream().limit(6).toList())
                            <a href="${county.path("north-carolina")}" data-track-click="nav" data-track-source-context="nc_priority_county_routes" data-track-target-type="county_records_page">
                                <strong>${county.countyName()} septic permit lookup</strong>
                                <span>Open county route</span>
                            </a>
                        @endfor
                    </nav>
                    @if (northCarolinaPriorityCountyRoutes.size() > 6)
                        <details class="nc-priority-routes__more">
                            <summary>More high-demand county routes</summary>
                            <nav aria-label="More North Carolina county septic permit lookups">
                                @for (var county : northCarolinaPriorityCountyRoutes.stream().skip(6).toList())
                                    <a href="${county.path("north-carolina")}" data-track-click="nav" data-track-source-context="nc_priority_county_routes_more" data-track-target-type="county_records_page">${county.countyName()} septic records</a>
                                @endfor
                            </nav>
                        </details>
                    @endif
                </div>
            </section>
        @endif

        <section class="tdec-workspace" id="nc-record-search" aria-labelledby="nc-workspace-title">
            <div class="container">
                <header class="tdec-workspace__header">
                    <div>
                        <span class="eyebrow">NC county records desk</span>
                        <h2 id="nc-workspace-title">Prepare the correct county search</h2>
                    </div>
                    <ol aria-label="Search workflow">
                        <li class="is-current"><span>1</span> County</li>
                        <li><span>2</span> Property clue</li>
                        <li><span>3</span> County route</li>
                    </ol>
                </header>

                <form class="tdec-workspace__form" data-nc-route-form novalidate>
                    <fieldset>
                        <legend>1. Which county is the property in?</legend>
                        <label for="nc-county">Verified county workflows</label>
                        <select id="nc-county" data-nc-county required aria-describedby="nc-county-help nc-form-error">
                            <option value="">Choose a county</option>
                            @for (var county : northCarolinaCountyRoutes)
                                <option value="${county.countySlug()}"
                                        data-county-name="${county.countyName()}"
                                        data-internal-path="${county.path("north-carolina")}"
                                        data-records-url="${county.recordsUrl()}"
                                        data-records-label="${county.recordsLabel()}">${county.countyName()}</option>
                            @endfor
                        </select>
                        <p class="field-help" id="nc-county-help">${northCarolinaCountyRoutes.size()} county workflows have a source-reviewed route. If your county is not listed, use the official statewide staff directory below.</p>
                        <a class="nc-directory-link" href="https://www.dph.ncdhhs.gov/environmental-health/ehs-directory-june-2026/download?attachment=" target="_blank" rel="noreferrer" data-track-click="official_source" data-track-source-context="nc_county_directory" data-track-target-type="official_directory">My county is not listed</a>
                    </fieldset>

                    <fieldset>
                        <legend>2. What can the county use to match the property?</legend>
                        <div class="tdec-workspace__field-grid">
                            <label>
                                <span>Search by</span>
                                <select data-nc-clue-type>
                                    <option value="address">Street address</option>
                                    <option value="parcel">Parcel ID or PIN</option>
                                    <option value="owner">Current or prior owner</option>
                                    <option value="permit">Permit number</option>
                                </select>
                            </label>
                            <label>
                                <span data-nc-clue-label>Street address</span>
                                <input type="text" maxlength="140" autocomplete="street-address" placeholder="123 Main Street" data-nc-clue required aria-describedby="nc-form-error">
                            </label>
                            <label>
                                <span>Prior owner <small>optional</small></span>
                                <input type="text" maxlength="100" autocomplete="name" placeholder="Name on an older permit" data-nc-owner>
                            </label>
                            <label>
                                <span>Subdivision and lot <small>optional</small></span>
                                <input type="text" maxlength="100" placeholder="Subdivision, lot number" data-nc-subdivision>
                            </label>
                        </div>
                    </fieldset>

                    <div class="tdec-workspace__submit-row">
                        <button class="button button--primary" type="submit">Prepare county route</button>
                        <p>Property details stay on this device for up to 30 days and are never sent in analytics.</p>
                    </div>
                    <p class="form-error" id="nc-form-error" role="alert" data-nc-form-error hidden></p>
                </form>

                <section class="tdec-result" data-nc-result aria-live="polite" hidden>
                    <header class="tdec-result__header">
                        <div>
                            <span class="eyebrow">County route prepared</span>
                            <h2 data-nc-result-title tabindex="-1"></h2>
                            <p data-nc-result-summary></p>
                        </div>
                        <button class="text-button" type="button" data-nc-edit-search>Edit search</button>
                    </header>
                    <div class="tdec-result__steps">
                        <section>
                            <span class="tdec-result__number">01</span>
                            <div><h3>Carry these match keys</h3><ol data-nc-search-variants></ol><button class="text-button" type="button" data-nc-copy-clues>Copy search clues</button></div>
                        </section>
                        <section>
                            <span class="tdec-result__number">02</span>
                            <div>
                                <h3>Use the county-specific route</h3>
                                <p>The SepticPath county guide explains whether the official next step is a portal, form, email, or call. The official county source opens separately.</p>
                                <div class="tdec-result__actions" data-nc-route-actions></div>
                                <p class="tdec-result__handoff">Keep this tab open. Return here if the county portal is empty, broken, or does not include attachments.</p>
                            </div>
                        </section>
                        <section>
                            <span class="tdec-result__number">03</span>
                            <div>
                                <h3>Ask for the complete property file</h3>
                                <ul class="tdec-document-list">
                                    <li>Improvement Permit and site evaluation</li>
                                    <li>Construction Authorization</li>
                                    <li>Operation Permit or final approval</li>
                                    <li>Approved layout or site sketch</li>
                                    <li>Repair, malfunction, or modification records</li>
                                </ul>
                            </div>
                        </section>
                    </div>
                </section>
            </div>
        </section>

        @template.tags.stateRecordsReturn(stateCode = "NC", stateName = "North Carolina", officeLabel = "the county Environmental Health office")

        <section class="tdec-outcomes" id="after-search" aria-labelledby="nc-outcomes-title">
            <div class="container tdec-outcomes__grid">
                <header>
                    <span class="eyebrow">After the county search</span>
                    <h2 id="nc-outcomes-title">What happened?</h2>
                    <p>A blank portal result is not the same as a county-confirmed no-record response.</p>
                </header>
                <div class="tdec-outcomes__list">
                    <details data-state-fallback-outcome="found">
                        <summary><span>I found permit documents</span><small>Confirm match, capacity, layout, and final status</small></summary>
                        <div><p>Match the address or PIN, owner, permit number, and dates. Check whether the Operation Permit or final approval agrees with the improvement permit and installed layout.</p><a href="/septic-record-finder/?mode=document">Add and review the official file</a></div>
                    </details>
                    <details data-state-fallback-outcome="not_found_online">
                        <summary><span>Nothing appeared online</span><small>Retry alternate keys, then contact county Environmental Health</small></summary>
                        <div><p>Try the PIN, street name without suffix, prior owner, subdivision, lot, and permit number. If still empty, prepare a written request below.</p><button class="text-button" type="button" data-nc-open-request>Prepare a no-result request</button></div>
                    </details>
                    <details data-state-fallback-outcome="blocked wrong_agency">
                        <summary><span>The county site is broken or confusing</span><small>Do not interpret a website failure as a property result</small></summary>
                        <div><p>Use the county guide to find the Environmental Health office and send the same match keys through its form, email, or phone route.</p><button class="text-button" type="button" data-nc-open-request>Prepare a manual request</button></div>
                    </details>
                    <details data-state-fallback-outcome="no_record_response">
                        <summary><span>The county confirmed no record</span><small>Preserve the response and ask what archive was checked</small></summary>
                        <div><p>Ask whether the search included prior owners, paper or pre-digital files, repair records, and predecessor health districts. Keep the written response with the property file.</p></div>
                    </details>
                </div>
            </div>
        </section>

        <section class="tdec-request" data-nc-request-section hidden aria-labelledby="nc-request-title">
            <div class="container tdec-request__grid">
                <header>
                    <span class="eyebrow">Manual fallback</span>
                    <h2 id="nc-request-title" tabindex="-1">Prepare a county file request</h2>
                    <p>SepticPath drafts the wording. You still send it through the county's official form, email, or phone workflow.</p>
                </header>
                <div class="tdec-request__output">
                    <label for="nc-request-copy">Request wording to copy</label>
                    <textarea id="nc-request-copy" readonly data-nc-request-copy></textarea>
                    <div><button class="button button--secondary" type="button" data-nc-copy-request>Copy request wording</button><a class="button button--primary" data-nc-request-route href="#">Open county instructions</a><a class="button button--secondary" href="https://www.dph.ncdhhs.gov/environmental-health/ehs-directory-june-2026/download?attachment=" target="_blank" rel="noreferrer" data-track-click="official_source" data-track-source-context="nc_request_county_directory" data-track-target-type="official_source">Open NC county staff directory</a></div>
                </div>
            </div>
        </section>

        <section class="tdec-reference" aria-labelledby="nc-reference-title">
            <div class="container">
                <h2 id="nc-reference-title">Why the county comes first</h2>
                <p>NC Division of Public Health provides statewide onsite-wastewater oversight. North Carolina's official guidance directs septic permitting and failed-system questions to environmental health staff in the property's county.</p>
                <nav aria-label="North Carolina official sources">
                    <a href="https://www.dph.ncdhhs.gov/programs/environmental-health/site-water-protection-branch/site-wastewater-program" target="_blank" rel="noreferrer">NC On-Site Wastewater Program</a>
                    <a href="https://www.dph.ncdhhs.gov/environmental-health/ehs-directory-june-2026/download?attachment=" target="_blank" rel="noreferrer">Environmental Health staff by county</a>
                </nav>
                <p class="tdec-reference__disclosure"><strong>Independent guide.</strong> SepticPath is not affiliated with NCDHHS or a county and cannot certify permit existence, system condition, or compliance. Official route review: ${editorialLastReviewedAt}.</p>
            </div>
        </section>
        <script src="${AssetPaths.versioned("/north-carolina-records.js")}" defer></script>
        <script src="${AssetPaths.versioned("/state-records-return.js")}" defer></script>
    </div>
`)
