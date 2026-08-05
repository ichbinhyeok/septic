@import com.example.septic.data.model.ContentPage
@import com.example.septic.web.PageMeta
@import com.example.septic.web.AssetPaths
@import com.example.septic.web.SouthCarolinaCountyRouteView
@param PageMeta page
@param ContentPage contentPage
@param java.util.List<SouthCarolinaCountyRouteView> southCarolinaCountyRoutes = java.util.List.of()
@param String editorialLastReviewedAt = ""

@template.layouts.app(page = page, content = @`
    <main class="tdec-desk sc-records-desk" data-sc-records-desk>
        <section class="tdec-desk__intro" aria-labelledby="sc-page-title">
            <div class="container tdec-desk__intro-grid">
                <div>
                    <span class="eyebrow">South Carolina existing septic records</span>
                    <h1 id="sc-page-title">Search SCDES for the property’s septic file</h1>
                    <p>Use the public SCDES Site Explorer first. This page prepares the county and property clues, then shows what to do if the online search has no match.</p>
                </div>
                <dl class="tdec-desk__scope" aria-label="What this tool can do">
                    <div><dt>SepticPath does</dt><dd>prepare search keys, open the public SCDES search, and draft a records follow-up.</dd></div>
                    <div><dt>SepticPath does not</dt><dd>query SCDES, retrieve a PDF, confirm a permit, or judge system condition.</dd></div>
                </dl>
            </div>
        </section>

        <section class="sc-official-search" aria-labelledby="sc-official-search-title">
            <div class="container sc-official-search__grid">
                <div><span class="eyebrow">Official public search</span><h2 id="sc-official-search-title">SCDES Site Explorer is the first stop</h2><p>It accepts identifying information and displays matching sites on a map. “DHEC” is the former agency name; the current records authority is SCDES.</p></div>
                <a class="button button--primary" href="https://epermitting.des.sc.gov/ext/nsite/default/map/help" target="_blank" rel="noreferrer" data-track-click="official_source" data-track-source-context="sc_site_explorer" data-track-target-type="official_search">Open SCDES Site Explorer</a>
            </div>
        </section>

        <section class="tdec-workspace" id="sc-record-search" aria-labelledby="sc-workspace-title"><div class="container">
            <header class="tdec-workspace__header"><div><span class="eyebrow">Search preparation</span><h2 id="sc-workspace-title">Prepare a search that SCDES can match</h2></div><ol aria-label="Search workflow"><li class="is-current"><span>1</span> County</li><li><span>2</span> Property</li><li><span>3</span> Official search</li></ol></header>
            <form class="tdec-workspace__form sc-workspace__form" data-sc-route-form novalidate>
                <fieldset><legend>1. Where and when?</legend>
                    <label for="sc-county">South Carolina county</label><select id="sc-county" data-sc-county required><option value="">Choose a county</option>@for (var county : southCarolinaCountyRoutes)<option value="${county.countyKey()}" data-county-name="${county.countyName()}" data-internal-path="${county.internalPath()}">${county.countyName()}</option>@endfor</select>
                    <label for="sc-age">Approximate age of the home</label><select id="sc-age" data-sc-age><option value="newer">Built within about 20 years</option><option value="older">Older than about 20 years</option><option value="unknown">I do not know</option></select>
                    <p class="field-help">All 46 counties use the SCDES route. SCDES says permits for newer homes may be on file; older or unlocated files can require a licensed contractor to locate the system.</p>
                </fieldset>
                <fieldset><legend>2. What identifying information do you have?</legend><div class="tdec-workspace__field-grid">
                    <label><span>Search by</span><select data-sc-clue-type><option value="address">Physical address</option><option value="tms">Tax map / TMS number</option><option value="owner">Original or prior owner</option><option value="permit">Permit number</option></select></label>
                    <label><span data-sc-clue-label>Physical address</span><input type="text" maxlength="140" autocomplete="street-address" placeholder="123 Main Street" data-sc-clue required></label>
                    <label><span>Subdivision, lot, block <small>optional</small></span><input type="text" maxlength="120" placeholder="Subdivision, lot and block" data-sc-lot></label>
                    <label><span>Original permit holder <small>optional</small></span><input type="text" maxlength="100" autocomplete="name" placeholder="Name used when installed" data-sc-owner></label>
                </div></fieldset>
                <div class="tdec-workspace__submit-row"><button class="button button--primary" type="submit">Prepare my SCDES search</button><p>Your entries stay in this browser. SepticPath does not send them to SCDES.</p></div><p class="form-error" role="alert" data-sc-form-error hidden></p>
            </form>

            <section class="tdec-result" data-sc-result aria-live="polite" hidden><header class="tdec-result__header"><div><span class="eyebrow" data-sc-result-status>Search ready</span><h2 data-sc-result-title></h2><p data-sc-result-summary></p></div><button class="text-button" type="button" data-sc-edit-search>Edit details</button></header><div class="tdec-result__steps">
                <section><span class="tdec-result__number">01</span><div><h3>Try these identifiers</h3><ol data-sc-search-variants></ol><button class="text-button" type="button" data-sc-copy-clues>Copy search clues</button></div></section>
                <section><span class="tdec-result__number">02</span><div><h3>Search the official system</h3><p>Paste the strongest identifier into SCDES Site Explorer. Review the result details and attachments; do not assume an empty result means no permit exists.</p><div class="tdec-result__actions" data-sc-route-actions></div><p class="tdec-result__handoff">Keep this page open so you can return to the correct next step.</p></div></section>
                <section><span class="tdec-result__number">03</span><div><h3>Look for the complete file</h3><ul class="tdec-document-list"><li>Application or D-1740 trail</li><li>Permit to Construct</li><li>Soil evaluation and approved layout</li><li>Final inspection / DES 4432</li><li>Approval to Operate</li><li>Repair or modification records</li></ul></div></section>
            </div></section>
        </div></section>

        @template.tags.stateRecordsReturn(stateCode = "SC", stateName = "South Carolina", officeLabel = "SCDES Onsite Wastewater")

        <section class="tdec-outcomes" id="after-search" aria-labelledby="sc-outcomes-title"><div class="container tdec-outcomes__grid"><header><span class="eyebrow">After Site Explorer</span><h2 id="sc-outcomes-title">What did you find?</h2><p>Choose the actual outcome. Each one has a different next step.</p></header><div class="tdec-outcomes__list">
            <details data-state-fallback-outcome="found"><summary><span>I found documents</span><small>Verify the property and final status</small></summary><div><p>Match the county, address or TMS, owner, permit number, and dates. Check for the approved layout and final inspection before relying on the file.</p><a href="/offer-prep-septic-file-check/">Check the septic file before a property decision</a></div></details>
            <details data-state-fallback-outcome="not_found_online"><summary><span>No online match appeared</span><small>Retry alternate identifiers, then contact SCDES</small></summary><div><p>Try TMS, original owner, subdivision, lot, block, and approximate install year. If there is still no match, prepare a manual follow-up.</p><button class="text-button" type="button" data-sc-open-request>Prepare an SCDES follow-up</button></div></details>
            <details data-state-fallback-outcome="no_record_response wrong_agency blocked"><summary><span>SCDES could not locate a file</span><small>Age changes the next step</small></summary><div><p>Ask for written confirmation of what was searched. For an older home—or when no permit can be located—SCDES advises contacting a licensed septic contractor to physically locate the existing system.</p><button class="text-button" type="button" data-sc-open-request>Copy the record request first</button></div></details>
            <details><summary><span>I need to locate or assess the system</span><small>A permit file cannot prove present condition</small></summary><div><p>A record may show the approved design, not today’s exact location or condition. Physical locating and inspection require an appropriate licensed professional.</p></div></details>
        </div></div></section>

        <section class="tdec-request" data-sc-request-section hidden aria-labelledby="sc-request-title"><div class="container tdec-request__grid"><header><span class="eyebrow">Manual follow-up</span><h2 id="sc-request-title">Ask SCDES for the property file</h2><p>The draft includes the identifiers SCDES asks homeowners to provide. You still review and send it yourself.</p></header><div class="tdec-request__output"><label for="sc-request-copy">Request wording to copy</label><textarea id="sc-request-copy" readonly data-sc-request-copy></textarea><div><button class="button button--secondary" type="button" data-sc-copy-request>Copy request wording</button><a class="button button--primary" href="mailto:OSWWCentral@des.sc.gov">Email SCDES Onsite Wastewater</a></div><dl><div><dt>Customer service</dt><dd>1-855-731-2504</dd></div><div><dt>Email</dt><dd>OSWWCentral@des.sc.gov</dd></div></dl></div></div></section>

        <section class="tdec-reference" aria-labelledby="sc-reference-title"><div class="container"><h2 id="sc-reference-title">Official-source notes</h2><p>SCDES describes Site Explorer as the public ePermitting search. Its homeowner guidance asks for tax map number, lot, block, physical address, installation or construction date, original permit holder, and subdivision when staff search for a file.</p><nav aria-label="South Carolina official sources"><a href="https://epermitting.des.sc.gov/ext/nsite/default/map/help" target="_blank" rel="noreferrer">SCDES Site Explorer</a><a href="https://des.sc.gov/permits-regulations/septic-tanks/homeowner-resources/how-locate-septic-tank" target="_blank" rel="noreferrer">How SCDES locates records</a><a href="https://www.des.sc.gov/permits-regulations/septic-tanks/permits-licenses/septic-tanks-residential-single-home-builder" target="_blank" rel="noreferrer">Residential permit process</a><a href="https://des.sc.gov/sites/des/files/Library/D-4432.pdf" target="_blank" rel="noreferrer">Final inspection form DES 4432</a></nav><p class="tdec-reference__disclosure"><strong>Independent guide.</strong> SepticPath is not affiliated with SCDES and cannot certify permit existence, compliance, location, or condition.@if (editorialLastReviewedAt != null && !editorialLastReviewedAt.isBlank()) Official route review: ${editorialLastReviewedAt}.@endif</p></div></section>
        <script src="${AssetPaths.versioned("/south-carolina-records.js")}" defer></script>
        <script src="${AssetPaths.versioned("/state-records-return.js")}" defer></script>
    </main>
`)
