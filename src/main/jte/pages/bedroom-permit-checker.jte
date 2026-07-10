@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.PageMeta
@param PageMeta page
@param java.util.List<StateProfile> states = java.util.List.of()

@template.layouts.app(page = page, content = @`
    <section class="section section--heroish bedroom-checker-hero">
        <div class="container state-hero">
            <div>
                <span class="eyebrow">Offer-period checker</span>
                <h1>Does the listing bedroom count match the septic permit?</h1>
                <p class="lede">Compare the advertised bedroom count with the number supported by the septic file. When they differ, create a clean records-first note before an offer, listing, lender request, or inspection conversation gets ahead of the file.</p>
                <div class="hero__actions">
                    <a class="button button--primary" href="#bedroom-permit-checker" data-track-click="nav" data-track-source-context="bedroom_checker_hero" data-track-target-type="internal_section">Check the file question</a>
                    <a class="button button--secondary" href="/septic-permit-search-by-address/" data-track-click="nav" data-track-source-context="bedroom_checker_hero_address" data-track-target-type="internal_page">Find records by address</a>
                </div>
            </div>
            <article class="hero-sidecard">
                <span class="pill">Not a compliance decision</span>
                <strong>Match the source before you market, negotiate, or price.</strong>
                <p>This checker flags a records question. The permit, official file owner, local health office, broker, lender, and qualified inspector decide what the property can support.</p>
            </article>
        </div>
    </section>

    <section class="section section--tight" id="bedroom-permit-checker">
        <div class="container">
            <div class="bedroom-checker" data-bedroom-permit-checker>
                <div class="bedroom-checker__intro">
                    <span class="eyebrow">Listing-to-file relay</span>
                    <h2>Turn the mismatch into the exact next request.</h2>
                    <p>Use the number shown on an official septic permit, approval, operation permit, or county record. A tax card, seller statement, or room count alone is not a septic-capacity answer.</p>
                    <div class="bedroom-checker__signals" aria-label="Checker guardrails">
                        <span>Buyer and listing use</span>
                        <span>Copy-ready handoff</span>
                        <span>Nothing saved</span>
                    </div>
                </div>

                <form class="bedroom-checker__form" data-bedroom-permit-form>
                    <div class="bedroom-checker__fields">
                        <label>
                            <span>Property state</span>
                            <select data-bedroom-state>
                                @for (var state : states)
                                    <option value="${state.stateCode()}" selected="${"TN".equals(state.stateCode())}">${state.stateName()}</option>
                                @endfor
                            </select>
                        </label>
                        <label>
                            <span>Listing bedrooms</span>
                            <input type="number" min="1" max="12" inputmode="numeric" value="3" required data-bedroom-listing-count>
                        </label>
                        <label>
                            <span>Septic permit bedrooms</span>
                            <select data-bedroom-permit-count>
                                <option value="">Not confirmed yet</option>
                                @for (int bedroomCount = 1; bedroomCount <= 12; bedroomCount++)
                                    <option value="${bedroomCount}">${bedroomCount} bedrooms</option>
                                @endfor
                            </select>
                        </label>
                        <label>
                            <span>File status</span>
                            <select data-bedroom-file-status>
                                <option value="not_checked">I have not checked an official file</option>
                                <option value="official">Official permit or approval reviewed</option>
                                <option value="missing">No file found yet</option>
                                <option value="conflicting">Seller, tax, or permit records conflict</option>
                            </select>
                        </label>
                        <label class="bedroom-checker__address-field">
                            <span>Property address, optional</span>
                            <input type="text" maxlength="180" autocomplete="street-address" placeholder="Used only in the note you copy or download" data-bedroom-address>
                        </label>
                    </div>
                    <button class="button button--primary" type="submit" data-bedroom-submit>Check the record gap</button>
                    <p class="bedroom-checker__privacy">This checker runs in your browser. SepticPath does not upload or save the address, bedroom counts, or result.</p>
                </form>

                <section class="bedroom-checker__result" aria-live="polite" data-bedroom-result hidden>
                    <span class="eyebrow" data-bedroom-result-label>File check</span>
                    <h2 data-bedroom-result-heading></h2>
                    <p data-bedroom-result-body></p>
                    <div class="bedroom-checker__facts" data-bedroom-result-facts></div>
                    <ol class="bedroom-checker__steps" data-bedroom-result-steps></ol>
                    <textarea class="bedroom-checker__note" rows="10" readonly data-bedroom-result-note></textarea>
                    <div class="bedroom-checker__actions">
                        <button class="button button--primary" type="button" data-bedroom-copy>Copy transaction note</button>
                        <button class="button button--secondary" type="button" data-bedroom-download>Download note .txt</button>
                        <a class="button button--secondary" href="/septic-permit-search-by-address/" data-track-click="nav" data-track-source-context="bedroom_checker_result_address" data-track-target-type="internal_page">Find official record route</a>
                        <a class="button button--secondary" href="/septic-records-request-builder/" data-track-click="nav" data-track-source-context="bedroom_checker_result_request" data-track-target-type="internal_page">Build records request</a>
                    </div>
                </section>
            </div>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container bedroom-checker-guide">
            <article>
                <span>01</span>
                <h2>Find the official count</h2>
                <p>Use the septic permit, improvement permit, operation permit, final approval, design, or official county response. If the record is old, ask for the prior owner, parcel, subdivision, or permit-number index.</p>
            </article>
            <article>
                <span>02</span>
                <h2>Flag, do not guess</h2>
                <p>A mismatch or a missing file does not prove that a listing is wrong. It proves that the transaction needs the responsible office, broker, inspector, or lender to verify the file before relying on the bedroom story.</p>
            </article>
            <article>
                <span>03</span>
                <h2>Keep the response</h2>
                <p>Save the permit copy, county response, inspection result, and any written no-record answer with the deal file. Do not replace the official answer with a calculator result.</p>
            </article>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container detail-grid">
            <article class="detail-card">
                <h2>Why this matters in Tennessee and North Carolina</h2>
                <p>Both states commonly route septic questions through a state or county-held permit trail. Tennessee addresses can begin with the property and TDEC record relay; North Carolina buyers and listing teams should make the county Environmental Health file part of the first transaction check.</p>
                <a href="/septic-records-checklist/tennessee/">Open Tennessee records path</a>
                <a href="/septic-records-checklist/north-carolina/">Open North Carolina records path</a>
            </article>
            <article class="detail-card">
                <h2>What to request when the counts differ</h2>
                <p>Ask for the permit copy, approved bedroom or design-flow count, as-built or layout, final approval or operation record, repair history, and the office's written direction when the parcel cannot be matched.</p>
                <a href="/septic-permit-records-request/">Open records request guide</a>
            </article>
            <article class="detail-card">
                <h2>Use the right expert next</h2>
                <p>For an active purchase or listing, the next answer may need a county environmental health specialist, septic inspector, broker, real-estate attorney, lender, or local permit professional. This page only keeps the question tied to the file.</p>
                <a href="/septic-inspection-letter/">Open inspection-letter guide</a>
            </article>
        </div>
    </section>
`)
