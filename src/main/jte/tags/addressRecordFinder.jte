@param String finderId = "address-record-finder"
@param String eyebrow = "Address record finder"
@param String heading = "Paste the address. Open the right septic records route."
@param String intro = "We resolve the property county, then send you to the best verified county records page or state records path already in SepticPath."
@param int totalCountyRouteCount = 0
@param boolean compact = false
@param boolean pageHeading = false
@param boolean minimal = false

<div id="${finderId}" class="record-finder ${compact ? "record-finder--embedded" : ""} ${minimal ? "record-finder--minimal" : ""}">
    @if (!minimal)
        <div class="record-finder__intro">
            <span class="eyebrow">${eyebrow}</span>
            @if (pageHeading)
                <h1>${heading}</h1>
            @else
                <h2>${heading}</h2>
            @endif
            <p>${intro}</p>
            <div class="record-finder__signals" aria-label="Record finder coverage">
                <span><strong>${totalCountyRouteCount}</strong> county routes</span>
                <span>Official-source backed</span>
                <span>No account required</span>
            </div>
            <div class="record-finder__result-preview" aria-label="Example property file result">
                <div class="record-finder__result-preview-head">
                    <span>Example property file</span>
                    <strong>3 of 5 confirmed</strong>
                </div>
                <ul>
                    <li data-preview-status="complete"><span aria-hidden="true">✓</span><strong>Permit number</strong><small>OWTS-26-4408</small></li>
                    <li data-preview-status="complete"><span aria-hidden="true">✓</span><strong>Approved bedrooms</strong><small>4 bedrooms</small></li>
                    <li data-preview-status="complete"><span aria-hidden="true">✓</span><strong>Final approval</strong><small>Dated record found</small></li>
                    <li data-preview-status="missing"><span aria-hidden="true">—</span><strong>Installed layout</strong><small>Still missing</small></li>
                    <li data-preview-status="missing"><span aria-hidden="true">—</span><strong>Repair history</strong><small>Still missing</small></li>
                </ul>
                <p>The real result names the source file and shows the exact text behind each fact.</p>
            </div>
        </div>
    @endif

    <div class="record-finder__workspace" data-address-record-finder data-address-record-finder-api="/api/address-record-finder">
        @if (minimal)
            <div class="record-finder__minimal-intro">
                <span class="eyebrow">${eyebrow}</span>
                <h2>${heading}</h2>
                <p>${intro}</p>
            </div>
        @endif
        <form class="record-finder__form"
              id="${finderId}-form"
              name="address-record-finder"
              data-address-record-finder-form>
            <label for="${finderId}-purpose">What are you trying to confirm?</label>
            <select id="${finderId}-purpose" name="purpose" data-address-record-finder-purpose>
                <option value="buying">Buyer or seller property file</option>
                <option value="bedrooms">Approved bedroom count or addition</option>
                <option value="location">Tank, drain field, or reserve-area location</option>
                <option value="repair">Repair history or current problem</option>
                <option value="replacement">Replacement scope and cost</option>
                <option value="lender">Lender, closing, or inspection letter</option>
                <option value="owner">Owner records and future reference</option>
            </select>
            <label for="${finderId}-address">Property address</label>
            <p>Street, city, state, and ZIP produce the most reliable county match.</p>
            <div class="record-finder__input-row">
                <input id="${finderId}-address"
                       name="address"
                       type="text"
                       autocomplete="street-address"
                       placeholder="401 Church St, Nashville, TN 37219"
                       maxlength="180"
                       required
                       data-address-record-finder-input>
                <button class="button button--primary" type="submit" data-address-record-finder-submit>Find septic records</button>
            </div>
            <p class="record-finder__privacy">The address is sent only to the U.S. Census geocoder to identify the county. SepticPath does not save it on a server. If you open an official route, a 30-day progress marker stays on this device so you can continue.</p>
            <p class="record-finder__form-outcome"><strong>What you get:</strong> the exact file owner, the first record to request, and a reusable property-file checklist.</p>
            <p class="record-finder__fallback"><span>Do not have the full address?</span> <a href="/septic-records-by-county/#septic-records-by-county-county-finder" data-track-click="nav" data-track-source-context="address_record_finder_county_fallback" data-track-target-type="internal_page">Find the record route by county name</a></p>
        </form>

        <section class="record-finder__result" data-address-record-finder-result aria-live="polite" hidden>
            <span class="eyebrow" data-address-record-finder-status>County route found</span>
            <h3 data-address-record-finder-heading></h3>
            <p data-address-record-finder-message></p>
            <div class="record-finder__result-meta" data-address-record-finder-meta hidden></div>
            <div class="record-finder__office" data-address-record-finder-office hidden>
                <span>Exact file owner</span>
                <strong data-address-record-finder-office-label></strong>
                <p data-address-record-finder-contact></p>
                <small data-address-record-finder-reviewed></small>
            </div>
            <ol class="record-finder__relay-steps" data-address-record-finder-steps hidden></ol>
            <p class="record-finder__official-note"><strong>What happens next:</strong> SepticPath prepares the office and search path. The original record still opens on the government or county site. Opening it saves a 30-day progress marker on this device.</p>
            <div class="record-finder__result-actions" data-address-record-finder-actions></div>
            <div class="record-finder__return" data-address-record-finder-return hidden>
                <div>
                    <span class="eyebrow">Back from the official site?</span>
                    <h4>What happened?</h4>
                    <p>Your answer stays on this device for 30 days so the next visit can continue from here.</p>
                </div>
                <div class="record-finder__outcomes" data-address-record-finder-outcomes>
                    <button type="button" data-record-outcome="found">I found the record</button>
                    <button type="button" data-record-outcome="missing">No result</button>
                    <button type="button" data-record-outcome="blocked">Site blocked me</button>
                    <button type="button" data-record-outcome="request">I need to request it</button>
                </div>
                <button class="record-finder__clear-progress" type="button" data-record-progress-clear>Delete this saved task</button>
                <div class="record-finder__next" data-address-record-finder-next hidden></div>
                <section class="record-document" data-record-document-workspace hidden>
                    <div class="record-document__intro">
                        <span class="eyebrow">Your septic property file</span>
                        <h4>Add the records you found.</h4>
                        <p>Each document updates one shared checklist. We keep the extracted summary in this browser tab, show conflicts between files, and carry confirmed facts into the next tool.</p>
                    </div>
                    <form data-record-document-form enctype="multipart/form-data">
                        <label>
                            <span>Add a permit, as-built, approval, repair file, or letter</span>
                            <input type="file" name="file" accept="application/pdf,text/plain,.pdf,.txt" required data-record-document-file>
                        </label>
                        <button class="button button--primary" type="submit" data-record-document-submit>Add to property file</button>
                    </form>
                    <div class="record-document__resume">
                        <span>Continuing a saved session?</span>
                        <label class="button button--quiet">
                            Resume a property file
                            <input type="file" accept="application/json,.json" data-record-workspace-import>
                        </label>
                    </div>
                    <p class="record-document__privacy">Maximum 10 MB per source file. Originals and OCR images are never saved. The extracted summary stays only in this tab unless you choose “Save session for later.”</p>
                    <div class="record-document__status" data-record-document-status aria-live="polite"></div>
                    <div class="record-document__analysis" data-record-document-analysis hidden></div>
                </section>
            </div>
        </section>
    </div>
</div>
