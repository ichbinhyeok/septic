@import com.example.septic.data.model.ContentPage
@import com.example.septic.web.PageMeta
@import com.example.septic.web.TennesseeCountyRouteView
@import com.example.septic.web.AssetPaths
@param PageMeta page
@param ContentPage contentPage
@param java.util.List<TennesseeCountyRouteView> tennesseeCountyRoutes = java.util.List.of()
@param String editorialLastReviewedAt = ""

@template.layouts.app(page = page, content = @`
    <div class="tdec-desk" data-tdec-records-desk>
        <section class="tdec-desk__intro" aria-labelledby="tdec-page-title">
            <div class="container tdec-desk__intro-grid">
                <div>
                    <span class="eyebrow">Tennessee existing-system records</span>
                    <h1 id="tdec-page-title">Find a Tennessee septic permit or system record</h1>
                    <p>Choose the county. Leave with the correct official route and search clues.</p>
                </div>
                <dl class="tdec-desk__scope" aria-label="What this tool can do">
                    <div><dt>SepticPath does</dt><dd>Route the county and prepare search or request wording.</dd></div>
                    <div><dt>SepticPath does not</dt><dd>Search TDEC, retrieve files, or confirm a permit.</dd></div>
                </dl>
            </div>
        </section>

        <section class="tdec-workspace" id="tdec-record-search" aria-labelledby="tdec-workspace-title">
            <div class="container">
                <header class="tdec-workspace__header">
                    <div>
                        <span class="eyebrow">Tennessee records desk</span>
                        <h2 id="tdec-workspace-title">Prepare one accurate search</h2>
                    </div>
                    <ol aria-label="Search workflow">
                        <li class="is-current"><span>1</span> County</li>
                        <li><span>2</span> Property clue</li>
                        <li><span>3</span> Official route</li>
                    </ol>
                </header>

                <form class="tdec-workspace__form" data-tdec-route-form novalidate>
                    <fieldset>
                        <legend>1. Which county is the property in?</legend>
                        <label for="tdec-county">Tennessee county</label>
                        <select id="tdec-county" data-tdec-county required>
                            <option value="">Choose a county</option>
                            @for (var county : tennesseeCountyRoutes)
                                <option value="${county.countyKey()}"
                                        data-county-name="${county.countyName()}"
                                        data-contract-county="${county.contractCounty()}"
                                        data-internal-path="${county.internalPath()}">${county.countyName()}</option>
                            @endfor
                        </select>
                        <p class="field-help" data-tdec-county-help>Tennessee has 9 counties that use their own septic program. The county decides the route.</p>
                    </fieldset>

                    <fieldset>
                        <legend>2. What is your strongest property clue?</legend>
                        <div class="tdec-workspace__field-grid">
                            <label>
                                <span>Search by</span>
                                <select data-tdec-clue-type>
                                    <option value="address">Street address</option>
                                    <option value="parcel">Parcel or tax-map ID</option>
                                    <option value="owner">Current or prior owner</option>
                                    <option value="permit">Permit number</option>
                                </select>
                            </label>
                            <label>
                                <span data-tdec-clue-label>Street address</span>
                                <input type="text" maxlength="140" autocomplete="street-address" placeholder="123 Main Street" data-tdec-clue required>
                            </label>
                            <label>
                                <span>Subdivision and lot <small>optional</small></span>
                                <input type="text" maxlength="100" placeholder="Oak Ridge, Lot 12" data-tdec-subdivision>
                            </label>
                            <label>
                                <span>Current or prior owner <small>optional</small></span>
                                <input type="text" maxlength="100" autocomplete="name" placeholder="Owner name on the old permit" data-tdec-owner>
                            </label>
                        </div>
                    </fieldset>

                    <div class="tdec-workspace__submit-row">
                        <button class="button button--primary" type="submit" data-tdec-prepare>Prepare my official search</button>
                        <p>Nothing entered here is sent to TDEC or saved by SepticPath.</p>
                    </div>
                    <p class="form-error" role="alert" data-tdec-form-error hidden></p>
                </form>

                <section class="tdec-result" data-tdec-result aria-live="polite" hidden>
                    <header class="tdec-result__header">
                        <div>
                            <span class="eyebrow" data-tdec-result-status>Route prepared</span>
                            <h2 data-tdec-result-title></h2>
                            <p data-tdec-result-summary></p>
                        </div>
                        <button class="text-button" type="button" data-tdec-edit-search>Edit search</button>
                    </header>

                    <div class="tdec-result__steps">
                        <section aria-labelledby="tdec-search-plan-title">
                            <span class="tdec-result__number">01</span>
                            <div>
                                <h3 id="tdec-search-plan-title">Try these search values</h3>
                                <ol data-tdec-search-variants></ol>
                                <button class="text-button" type="button" data-tdec-copy-clues>Copy search clues</button>
                            </div>
                        </section>
                        <section aria-labelledby="tdec-official-route-title">
                            <span class="tdec-result__number">02</span>
                            <div>
                                <h3 id="tdec-official-route-title">Open the correct official route</h3>
                                <p data-tdec-route-explanation></p>
                                <div class="tdec-result__actions" data-tdec-route-actions></div>
                                <p class="tdec-result__handoff">The government site opens in a new tab. Keep this page open so you can return for a blocked or empty result.</p>
                            </div>
                        </section>
                        <section aria-labelledby="tdec-document-check-title">
                            <span class="tdec-result__number">03</span>
                            <div>
                                <h3 id="tdec-document-check-title">Save the useful documents</h3>
                                <ul class="tdec-document-list">
                                    <li>Construction permit or original approval</li>
                                    <li>Soil map or soil evaluation</li>
                                    <li>System layout or as-built</li>
                                    <li>Final inspection or Certificate of Completion</li>
                                    <li>Repair or modification permits</li>
                                </ul>
                            </div>
                        </section>
                    </div>
                </section>
            </div>
        </section>

        @template.tags.stateRecordsReturn(stateCode = "TN", stateName = "Tennessee", officeLabel = "TDEC or the responsible contract county")

        <section class="tdec-outcomes" id="after-search" aria-labelledby="tdec-outcomes-title">
            <div class="container tdec-outcomes__grid">
                <header>
                    <span class="eyebrow">After the official site</span>
                    <h2 id="tdec-outcomes-title">What happened?</h2>
                    <p>An empty result or 403 page does not prove that no septic record exists.</p>
                </header>
                <div class="tdec-outcomes__list">
                    <details data-state-fallback-outcome="found">
                        <summary><span>Found a permit or PDF</span><small>Check the property match and file completeness</small></summary>
                        <div>
                            <p>Match the county, address or parcel, owner, permit number, and dates. Then check whether layout, final approval, bedroom capacity, and repair history are present.</p>
                            <a href="/offer-prep-septic-file-check/" data-track-click="nav" data-track-source-context="tdec_found_file" data-track-target-type="internal_tool">Check what the file means for a property decision</a>
                        </div>
                    </details>
                    <details data-state-fallback-outcome="not_found_online">
                        <summary><span>No result appeared</span><small>Retry alternate keys before requesting the file</small></summary>
                        <div>
                            <p>Try street name only, remove Road/Street/Lane and direction letters, then try parcel, prior owner, subdivision, lot, or permit number. If still empty, prepare a written request below.</p>
                            <button class="text-button" type="button" data-tdec-open-request>No-result request</button>
                        </div>
                    </details>
                    <details data-tdec-viewer-outcome data-state-fallback-outcome="blocked">
                        <summary><span>The viewer returned 403</span><small>Use the working TDEC page or written request</small></summary>
                        <div>
                            <p>The TDEC record viewer blocks some visits. That is a website-access failure, not a property result. Use the current SSDS program page or submit the same clues through the official request form.</p>
                            <button class="text-button" type="button" data-tdec-open-request>Prepare blocked-viewer fallback</button>
                        </div>
                    </details>
                    <details data-state-fallback-outcome="no_record_response wrong_agency">
                        <summary><span>The office says no record exists</span><small>Keep that response and clarify the archive</small></summary>
                        <div>
                            <p>Ask whether the search included prior owners, archived or pre-digital files, repair records, and any delegated local office. Keep the written no-record response with the property file.</p>
                        </div>
                    </details>
                </div>
            </div>
        </section>

        <section class="tdec-request" data-tdec-request-section hidden aria-labelledby="tdec-request-title">
            <div class="container tdec-request__grid">
                <header>
                    <span class="eyebrow">Written fallback</span>
                    <h2 id="tdec-request-title">Prepare the TDEC request form</h2>
                    <p>The official form asks for contact details, county, date range, record type, keywords, and the responsible division. SepticPath only prepares the property-specific wording.</p>
                </header>
                <div class="tdec-request__output">
                    <label for="tdec-request-copy">Record description to paste</label>
                    <textarea id="tdec-request-copy" readonly data-tdec-request-copy></textarea>
                    <div>
                        <button class="button button--secondary" type="button" data-tdec-copy-request>Copy request wording</button>
                        <a class="button button--primary" href="https://www.tn.gov/environment/contacts/public-records-request.html" target="_blank" rel="noreferrer" data-track-click="official_source" data-track-source-context="tdec_records_request" data-track-target-type="official_request">Open official TDEC request</a>
                    </div>
                    <dl>
                        <div><dt>Division</dt><dd>Water Resources</dd></div>
                        <div><dt>Record type</dt><dd>Existing SSDS / septic system property records</dd></div>
                        <div><dt>Request</dt><dd>Copy / duplicate, preferably electronic</dd></div>
                    </dl>
                </div>
            </div>
        </section>

        <section class="tdec-reference" aria-label="Official sources and route verification">
            <div class="container">
                <details class="tdec-reference__details">
                    <summary>
                        <span>Official sources and route verification</span>
                        <small>Reviewed ${editorialLastReviewedAt}</small>
                    </summary>
                    <div>
                        <p>TDEC identifies its SSDS Record Search as the online route. Its current septic-services page lists Blount, Davidson/Nashville, Hamilton, Jefferson, Knox, Madison, Sevier, Shelby, and Williamson as locally handled counties.</p>
                        <nav aria-label="Tennessee official sources">
                            <a href="https://www.tn.gov/environment/permits/water/septic-systems-permits.html" target="_blank" rel="noreferrer">TDEC SSDS program</a>
                            <a href="https://www.tn.gov/environment/contacts/field-offices.html" target="_blank" rel="noreferrer">Environmental Field Offices</a>
                            <a href="https://www.tn.gov/environment/contacts/public-records-request.html" target="_blank" rel="noreferrer">Public Records Request</a>
                        </nav>
                        <p class="tdec-reference__disclosure"><strong>Independent guide.</strong> SepticPath is not affiliated with TDEC or a Tennessee county and cannot determine system condition or legal compliance.</p>
                    </div>
                </details>
            </div>
        </section>
        <script src="${AssetPaths.versioned("/tdec-records.js")}" defer></script>
        <script src="${AssetPaths.versioned("/state-records-return.js")}" defer></script>
    </div>
`)
