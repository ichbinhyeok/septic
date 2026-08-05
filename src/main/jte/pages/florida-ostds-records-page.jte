@import com.example.septic.data.model.ContentPage
@import com.example.septic.web.PageMeta
@import com.example.septic.web.AssetPaths
@import com.example.septic.web.FloridaCountyRouteView
@param PageMeta page
@param ContentPage contentPage
@param java.util.List<FloridaCountyRouteView> floridaCountyRoutes = java.util.List.of()
@param String editorialLastReviewedAt = ""

@template.layouts.app(page = page, content = @`
    <div class="tdec-desk fl-records-desk" data-fl-records-desk>
        <section class="tdec-desk__intro" aria-labelledby="fl-page-title">
            <div class="container tdec-desk__intro-grid">
                <div><span class="eyebrow">Florida existing OSTDS records</span><h1 id="fl-page-title">Find the office that holds the Florida septic file</h1><p>Florida is mid-transition. Select the county to separate the current permit authority from the office that may still hold an older property record.</p></div>
                <dl class="tdec-desk__scope" aria-label="What this tool can do"><div><dt>SepticPath does</dt><dd>identify the current county route, prepare record keys, and explain the archive fallback.</dd></div><div><dt>SepticPath does not</dt><dd>search DEP or county systems, retrieve documents, or confirm permit status.</dd></div></dl>
            </div>
        </section>

        <section class="fl-transition" aria-labelledby="fl-transition-title"><div class="container"><span class="eyebrow">Current authority split</span><h2 id="fl-transition-title">17 counties use DEP; 50 still use county health departments</h2><p>DEP began permitting Northwest Florida counties in January 2025 and Marion County in July 2025. Older records may still require the county health department even where DEP handles new permits now.</p></div></section>

        <section class="tdec-workspace" id="fl-record-search" aria-labelledby="fl-workspace-title"><div class="container">
            <header class="tdec-workspace__header"><div><span class="eyebrow">Florida OSTDS records desk</span><h2 id="fl-workspace-title">Route the county before searching</h2></div><ol aria-label="Search workflow"><li class="is-current"><span>1</span> County</li><li><span>2</span> Property clue</li><li><span>3</span> Record route</li></ol></header>
            <form class="tdec-workspace__form" data-fl-route-form novalidate>
                <fieldset><legend>1. Where is the property?</legend><label for="fl-county">Florida county</label><select id="fl-county" data-fl-county required><option value="">Choose a county</option>@for (var county : floridaCountyRoutes)<option value="${county.countyKey()}" data-county-name="${county.countyName()}" data-route-owner="${county.depManaged() ? "dep" : "county-doh"}">${county.countyName()}</option>@endfor</select><p class="field-help" data-fl-county-help>The county determines whether the current permit route starts with DEP or the local Department of Health office.</p></fieldset>
                <fieldset><legend>2. What can the office use to match the property?</legend><div class="tdec-workspace__field-grid">
                    <label><span>Search by</span><select data-fl-clue-type><option value="address">Street address</option><option value="parcel">Parcel or property ID</option><option value="owner">Current or prior owner</option><option value="permit">Permit number</option></select></label>
                    <label><span data-fl-clue-label>Street address</span><input type="text" maxlength="140" autocomplete="street-address" placeholder="123 Main Street" data-fl-clue required></label>
                    <label><span>Prior owner <small>optional</small></span><input type="text" maxlength="100" autocomplete="name" placeholder="Name on an older permit" data-fl-owner></label>
                    <label><span>Lot or legal description <small>optional</small></span><input type="text" maxlength="120" placeholder="Subdivision, block, lot" data-fl-legal></label>
                </div></fieldset>
                <div class="tdec-workspace__submit-row"><button class="button button--primary" type="submit">Prepare Florida record route</button><p>Nothing entered here is sent to DEP or a county, and SepticPath does not save it.</p></div><p class="form-error" role="alert" data-fl-form-error hidden></p>
            </form>
            <section class="tdec-result" data-fl-result aria-live="polite" hidden><header class="tdec-result__header"><div><span class="eyebrow" data-fl-result-status>Route prepared</span><h2 data-fl-result-title></h2><p data-fl-result-summary></p></div><button class="text-button" type="button" data-fl-edit-search>Edit search</button></header><div class="tdec-result__steps">
                <section><span class="tdec-result__number">01</span><div><h3>Carry these match keys</h3><ol data-fl-search-variants></ol><button class="text-button" type="button" data-fl-copy-clues>Copy search clues</button></div></section>
                <section><span class="tdec-result__number">02</span><div><h3>Open the current authority</h3><p data-fl-route-explanation></p><div class="tdec-result__actions" data-fl-route-actions></div><p class="tdec-result__handoff">Keep this tab open. If the current authority cannot find an older file, use the historical-record fallback below.</p></div></section>
                <section><span class="tdec-result__number">03</span><div><h3>Ask for the complete OSTDS file</h3><ul class="tdec-document-list"><li>Application and construction permit</li><li>Site evaluation and site plan</li><li>Building floor plan in the permit packet</li><li>Final inspection or final approval</li><li>Repair, modification, abandonment, and operating records</li></ul></div></section>
            </div></section>
        </div></section>

        <section class="tdec-outcomes" id="after-search" aria-labelledby="fl-outcomes-title"><div class="container tdec-outcomes__grid"><header><span class="eyebrow">After the official route</span><h2 id="fl-outcomes-title">What happened?</h2><p>A current permit portal and a historical property archive are not always the same system.</p></header><div class="tdec-outcomes__list">
            <details><summary><span>I found the OSTDS documents</span><small>Check the property match and final approval</small></summary><div><p>Match county, address or parcel, owner, permit number, and dates. Confirm the approved plan and final inspection, then look for later repair or abandonment records.</p><a href="/offer-prep-septic-file-check/">Check the file before a property decision</a></div></details>
            <details><summary><span>The portal has no older record</span><small>Ask the county health department for the historical file</small></summary><div><p>In transitioned counties, DEP may handle current permits while the county health department still has older property files. Request both before treating the file as missing.</p><button class="text-button" type="button" data-fl-open-request>Prepare a historical-file request</button></div></details>
            <details><summary><span>No result appeared</span><small>Retry parcel, owner, street-only, and permit number</small></summary><div><p>An empty online result is not a confirmed no-record response. Retry alternate keys, then send a manual request.</p><button class="text-button" type="button" data-fl-open-request>Prepare a no-result request</button></div></details>
            <details><summary><span>The office confirmed no record</span><small>Preserve the response and clarify both archives</small></summary><div><p>Ask whether DEP and the county health department were both checked, including pre-digital records and prior owners. Keep the written response with the property file.</p></div></details>
        </div></div></section>

        <section class="tdec-request" data-fl-request-section hidden aria-labelledby="fl-request-title"><div class="container tdec-request__grid"><header><span class="eyebrow">Manual fallback</span><h2 id="fl-request-title">Prepare an OSTDS file request</h2><p>Use this wording with the county health department and, for transitioned counties, the DEP Onsite Sewage Program.</p></header><div class="tdec-request__output"><label for="fl-request-copy">Request wording to copy</label><textarea id="fl-request-copy" readonly data-fl-request-copy></textarea><div><button class="button button--secondary" type="button" data-fl-copy-request>Copy request wording</button><a class="button button--primary" href="https://www.floridahealth.gov/all-county-locations.html" target="_blank" rel="noreferrer">Open county health directory</a></div></div></div></section>

        <section class="tdec-reference" aria-labelledby="fl-reference-title"><div class="container"><h2 id="fl-reference-title">Official-source notes</h2><p>Florida DEP's January 2026 guidance lists the 17 DEP-permitted counties and the 50 counties still permitted through local DOH Environmental Public Health programs.</p><nav aria-label="Florida official sources"><a href="https://floridadep.gov/water/onsite-sewage/content/onsite-sewage-faq-permitting" target="_blank" rel="noreferrer">DEP county transition table</a><a href="https://ostds.floridadep.gov/" target="_blank" rel="noreferrer">DEP OSTDS portal</a><a href="https://www.floridahealth.gov/all-county-locations.html" target="_blank" rel="noreferrer">Florida county health locations</a></nav><p class="tdec-reference__disclosure"><strong>Independent guide.</strong> SepticPath is not affiliated with DEP, DOH, or a county and cannot certify permit status, condition, or compliance.@if (editorialLastReviewedAt != null && !editorialLastReviewedAt.isBlank()) Official route review: ${editorialLastReviewedAt}.@endif</p></div></section>
        <script src="${AssetPaths.versioned("/florida-ostds-records.js")}" defer></script>
    </div>
`)
