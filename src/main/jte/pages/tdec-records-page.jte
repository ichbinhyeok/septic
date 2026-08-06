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
                    <span class="eyebrow">Tennessee septic records</span>
                    <h1 id="tdec-page-title">Find the office and search path for an existing septic file</h1>
                    <p>Select the property county and what you need. We verify a full address when possible, then prepare the correct TDEC or local-county route.</p>
                </div>
                <aside class="tdec-desk__boundary" aria-label="Service boundary">
                    <strong>What you get here</strong>
                    <p>A verified county route, useful search keys, and the next step if the official site is empty or blocked.</p>
                    <small>SepticPath cannot see a government database, retrieve its PDF, or certify the system’s current condition.</small>
                </aside>
            </div>
        </section>

        <section class="tdec-workspace" id="tdec-record-search" aria-labelledby="tdec-workspace-title">
            <div class="container">
                <header class="tdec-workspace__header">
                    <div>
                        <span class="eyebrow">Start here</span>
                        <h2 id="tdec-workspace-title">Prepare your official route</h2>
                    </div>
                    <p>About 2 minutes · Nothing is submitted to an agency</p>
                </header>

                <form class="tdec-workspace__form" data-tdec-route-form novalidate>
                    <fieldset class="tdec-purpose">
                        <legend>1. What do you need?</legend>
                        <div class="tdec-purpose__options">
                            <label><input type="radio" name="tdec-purpose" value="records" checked><span><strong>Find the existing file</strong><small>Permit, soil map, layout, final approval, repair history</small></span></label>
                            <label><input type="radio" name="tdec-purpose" value="status"><span><strong>Confirm approved status</strong><small>Bedrooms, inspection letter, or official approval status</small></span></label>
                            <label><input type="radio" name="tdec-purpose" value="missing"><span><strong>No result or old file</strong><small>Online search was empty, blocked, or incomplete</small></span></label>
                            <label><input type="radio" name="tdec-purpose" value="repair"><span><strong>Repair or modify a system</strong><small>Start the current TDEC or local service process</small></span></label>
                        </div>
                    </fieldset>

                    <fieldset class="tdec-property">
                        <legend>2. Identify the property</legend>
                        <div class="tdec-property__grid">
                            <label class="tdec-property__county">
                                <span>County <b aria-hidden="true">*</b></span>
                                <select data-tdec-county required>
                                    <option value="">Choose a Tennessee county</option>
                                    @for (var county : tennesseeCountyRoutes)
                                        <option value="${county.countyKey()}"
                                                data-county-name="${county.countyName()}"
                                                data-contract-county="${county.contractCounty()}"
                                                data-internal-path="${county.internalPath()}"
                                                data-field-office-name="${county.fieldOfficeName()}"
                                                data-field-office-url="${county.fieldOfficeUrl()}"
                                                data-records-url="${county.recordsUrl()}"
                                                data-records-label="${county.recordsLabel()}"
                                                data-records-hint="${county.recordsHint()}">${county.countyName()}</option>
                                    @endfor
                                </select>
                                <small data-tdec-county-help>The county determines whether TDEC or a local program owns the record.</small>
                            </label>
                            <label class="tdec-property__address">
                                <span>Full property address</span>
                                <input type="text" maxlength="160" autocomplete="street-address" placeholder="123 Main St, Franklin, TN 37064" data-tdec-address>
                                <small>Include city and ZIP so we can verify the selected county.</small>
                            </label>
                            <details class="tdec-property__more">
                                <summary>Add parcel, owner, lot, or permit number</summary>
                                <div>
                                    <label><span>Parcel or tax-map ID</span><input type="text" maxlength="80" autocomplete="off" placeholder="Optional" data-tdec-parcel></label>
                                    <label><span>Current or prior owner</span><input type="text" maxlength="100" autocomplete="name" placeholder="Optional" data-tdec-owner></label>
                                    <label><span>Subdivision and lot</span><input type="text" maxlength="100" autocomplete="off" placeholder="Optional" data-tdec-subdivision></label>
                                    <label><span>Permit number</span><input type="text" maxlength="80" autocomplete="off" placeholder="Optional" data-tdec-permit></label>
                                </div>
                            </details>
                        </div>
                    </fieldset>

                    <div class="tdec-workspace__submit-row">
                        <button class="button button--primary" type="submit" data-tdec-prepare>Build my route</button>
                        <p>Use an address or at least one other property clue. Property details stay on this device for up to 30 days and are never sent in analytics.</p>
                    </div>
                    <p class="form-error" role="alert" data-tdec-form-error hidden></p>
                </form>

                <section class="tdec-result" data-tdec-result aria-live="polite" tabindex="-1" hidden>
                    <header class="tdec-result__header">
                        <div>
                            <span class="eyebrow" data-tdec-result-status>Route ready</span>
                            <h2 data-tdec-result-title></h2>
                            <p data-tdec-result-summary></p>
                        </div>
                        <button class="text-button" type="button" data-tdec-edit-search>Edit property</button>
                    </header>

                    <div class="tdec-result__body">
                        <section class="tdec-result__route" aria-labelledby="tdec-route-title">
                            <span class="tdec-result__step">Next official step</span>
                            <h3 id="tdec-route-title" data-tdec-route-title></h3>
                            <p data-tdec-route-explanation></p>
                            <div class="tdec-result__actions" data-tdec-route-actions></div>
                            <p class="tdec-result__hint" data-tdec-route-hint></p>
                        </section>

                        <aside class="tdec-result__clues" aria-labelledby="tdec-clues-title">
                            <span class="tdec-result__step">Take these with you</span>
                            <h3 id="tdec-clues-title">Property search keys</h3>
                            <ul data-tdec-search-clues></ul>
                            <p data-tdec-search-verification></p>
                            <button class="text-button" type="button" data-tdec-copy-clues>Copy search keys</button>
                        </aside>
                    </div>

                    <section class="tdec-return" data-tdec-return hidden aria-labelledby="tdec-return-title">
                        <div>
                            <span class="eyebrow">When you return</span>
                            <h3 id="tdec-return-title">What happened on the official site?</h3>
                        </div>
                        <div class="tdec-return__choices" role="group" aria-label="Official search outcome">
                            <button type="button" data-tdec-outcome="found">Found a file</button>
                            <button type="button" data-tdec-outcome="empty">No result</button>
                            <button type="button" data-tdec-outcome="blocked">Site blocked or failed</button>
                            <button type="button" data-tdec-outcome="no_record">Office found no record</button>
                        </div>
                        <div class="tdec-return__next" data-tdec-outcome-next hidden></div>
                    </section>
                </section>
            </div>
        </section>

        <section class="tdec-request" data-tdec-request-section hidden aria-labelledby="tdec-request-title">
            <div class="container tdec-request__grid">
                <header>
                    <span class="eyebrow">Missing-file fallback</span>
                    <h2 id="tdec-request-title">Send the office a complete property description</h2>
                    <p data-tdec-request-guidance></p>
                </header>
                <div class="tdec-request__output">
                    <label for="tdec-request-copy">Copy and edit this wording</label>
                    <textarea id="tdec-request-copy" readonly data-tdec-request-copy></textarea>
                    <div class="tdec-request__actions">
                        <button class="button button--secondary" type="button" data-tdec-copy-request>Copy wording</button>
                        <a class="button button--primary" href="#" target="_blank" rel="noreferrer" data-tdec-request-link data-track-click="official_source" data-track-source-context="tdec_records_request" data-track-target-type="official_request">Open responsible office</a>
                    </div>
                    <p class="tdec-request__citizen"><strong>Formal TDEC public-records form:</strong> the form asks whether the requester is a Tennessee citizen. Start with the responsible field office if you are not eligible or are unsure.</p>
                </div>
            </div>
        </section>

        <section class="tdec-quick-help" aria-labelledby="tdec-help-title">
            <div class="container">
                <header>
                    <span class="eyebrow">Before you decide what the file means</span>
                    <h2 id="tdec-help-title">A record search cannot prove current system condition</h2>
                </header>
                <div class="tdec-quick-help__rows">
                    <a href="/offer-prep-septic-file-check/"><strong>Found a PDF?</strong><span>Check whether the file matches the parcel and what is still missing.</span></a>
                    <a href="/septic-bedroom-permit-checker/"><strong>Need approved bedrooms?</strong><span>Use an official approval or Inspection Letter, not a listing claim.</span></a>
                    <a href="/septic-system-replacement/"><strong>Failure or repair concern?</strong><span>Separate record history from a current site evaluation.</span></a>
                </div>
            </div>
        </section>

        <section class="tdec-reference" aria-label="Official sources and route verification">
            <div class="container">
                <details class="tdec-reference__details">
                    <summary><span>Sources, county ownership, and limitations</span><small>Reviewed ${editorialLastReviewedAt}</small></summary>
                    <div>
                        <p>TDEC’s SSDS Record Search is the statewide online record route. Blount, Davidson, Hamilton, Jefferson, Knox, Madison, Sevier, Shelby, and Williamson administer their own septic programs. TDEC’s June 2026 assistance map is used for the remaining county-to-field-office routing. A 403 is an access failure, not evidence that a property has no record.</p>
                        <nav aria-label="Tennessee official sources">
                            <a href="https://www.tn.gov/environment/permits/water/septic-systems-permits.html" target="_blank" rel="noreferrer">TDEC septic systems program</a>
                            <a href="https://tdec.tn.gov/document-viewer/search/stp" target="_blank" rel="noreferrer">SSDS Record Search</a>
                            <a href="https://www.tn.gov/environment/contacts/field-offices.html" target="_blank" rel="noreferrer">Environmental Field Offices</a>
                            <a href="https://www.tn.gov/environment/contacts/public-records-request.html" target="_blank" rel="noreferrer">Formal public-records form</a>
                        </nav>
                        <p class="tdec-reference__disclosure"><strong>Independent guide.</strong> SepticPath is not affiliated with TDEC or a Tennessee county. Official agencies control record availability and permit status.</p>
                    </div>
                </details>
            </div>
        </section>
        <script src="${AssetPaths.versioned("/tdec-records.js")}" defer></script>
    </div>
`)
