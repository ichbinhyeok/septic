@import com.example.septic.data.model.ContentPage
@import com.example.septic.data.model.CountyRecordsPage
@import com.example.septic.web.PageMeta
@import com.example.septic.web.AssetPaths
@param PageMeta page
@param ContentPage contentPage
@param java.util.List<CountyRecordsPage> texasCountyRoutes = java.util.List.of()
@param String editorialLastReviewedAt = ""

@template.layouts.app(page = page, content = @`
    <div class="tdec-desk tx-records-desk" data-tx-records-desk>
        <section class="tdec-desk__intro" aria-labelledby="tx-page-title">
            <div class="container tdec-desk__intro-grid">
                <div>
                    <span class="eyebrow">Texas existing OSSF records</span>
                    <h1 id="tx-page-title">Find who permitted the Texas septic system</h1>
                    <p>Texas has no single property-record database for every OSSF. Confirm the local permitting authority, then search or request the file from that authorized agent or TCEQ region.</p>
                </div>
                <dl class="tdec-desk__scope" aria-label="What this tool can do">
                    <div><dt>SepticPath does</dt><dd>identify the authority search, route verified counties, and prepare file clues.</dd></div>
                    <div><dt>SepticPath does not</dt><dd>query OARS or county systems, retrieve a permit, or decide whether an OSSF is legal.</dd></div>
                </dl>
            </div>
        </section>

        <section class="tx-authority" aria-labelledby="tx-authority-title">
            <div class="container tx-authority__grid">
                <div><span class="eyebrow">Official first step</span><h2 id="tx-authority-title">Check the local permitting authority</h2><p>TCEQ's OARS search lists the local contact for the county. Where no local jurisdiction is authorized, the appropriate TCEQ regional office is the permitting authority.</p></div>
                <a class="button button--primary" href="https://www6.tceq.texas.gov/oars/index.cfm?fuseaction=search.county" target="_blank" rel="noreferrer" data-track-click="official_source" data-track-source-context="tx_oars_authority_search" data-track-target-type="official_authority_finder">Open TCEQ authority search</a>
            </div>
        </section>

        <section class="tdec-workspace" id="tx-record-search" aria-labelledby="tx-workspace-title">
            <div class="container">
                <header class="tdec-workspace__header">
                    <div><span class="eyebrow">Texas OSSF records desk</span><h2 id="tx-workspace-title">Prepare a local file search</h2></div>
                    <ol aria-label="Search workflow"><li class="is-current"><span>1</span> County</li><li><span>2</span> Property clue</li><li><span>3</span> Record route</li></ol>
                </header>
                <form class="tdec-workspace__form" data-tx-route-form novalidate>
                    <fieldset>
                        <legend>1. Do we have a verified county record route?</legend>
                        <label for="tx-county">Verified county workflows</label>
                        <select id="tx-county" data-tx-county required>
                            <option value="">Choose a county</option>
                            @for (var county : texasCountyRoutes)
                                <option value="${county.countySlug()}" data-county-name="${county.countyName()}" data-internal-path="${county.path("texas")}" data-records-url="${county.recordsUrl()}" data-records-label="${county.recordsLabel()}">${county.countyName()}</option>
                            @endfor
                        </select>
                        <p class="field-help">${texasCountyRoutes.size()} county workflows have a source-reviewed record route. For every other county, use TCEQ OARS to identify the authorized agent.</p>
                        <a class="nc-directory-link" href="https://www6.tceq.texas.gov/oars/index.cfm?fuseaction=search.county" target="_blank" rel="noreferrer">My county is not listed</a>
                    </fieldset>
                    <fieldset>
                        <legend>2. What can the authority use to match the OSSF?</legend>
                        <div class="tdec-workspace__field-grid">
                            <label><span>Search by</span><select data-tx-clue-type><option value="address">Street address</option><option value="parcel">Parcel or account ID</option><option value="owner">Current or prior owner</option><option value="permit">Permit number</option></select></label>
                            <label><span data-tx-clue-label>Street address</span><input type="text" maxlength="140" autocomplete="street-address" placeholder="123 Main Street" data-tx-clue required></label>
                            <label><span>Legal description <small>optional</small></span><input type="text" maxlength="120" placeholder="Survey, abstract, tract, lot" data-tx-legal></label>
                            <label><span>Subdivision and lot <small>optional</small></span><input type="text" maxlength="100" placeholder="Subdivision, block, lot" data-tx-subdivision></label>
                        </div>
                    </fieldset>
                    <div class="tdec-workspace__submit-row"><button class="button button--primary" type="submit">Prepare OSSF record route</button><p>Nothing entered here is sent to TCEQ or a local authority, and SepticPath does not save it.</p></div>
                    <p class="form-error" role="alert" data-tx-form-error hidden></p>
                </form>

                <section class="tdec-result" data-tx-result aria-live="polite" hidden>
                    <header class="tdec-result__header"><div><span class="eyebrow">Local route prepared</span><h2 data-tx-result-title></h2><p data-tx-result-summary></p></div><button class="text-button" type="button" data-tx-edit-search>Edit search</button></header>
                    <div class="tdec-result__steps">
                        <section><span class="tdec-result__number">01</span><div><h3>Carry these match keys</h3><ol data-tx-search-variants></ol><button class="text-button" type="button" data-tx-copy-clues>Copy search clues</button></div></section>
                        <section><span class="tdec-result__number">02</span><div><h3>Open the verified record path</h3><p>Start with the county guide so you know whether the official link is a public portal, a request form, or an office contact.</p><div class="tdec-result__actions" data-tx-route-actions></div><p class="tdec-result__handoff">Keep this tab open. Return if the authority page has no property search or the record is incomplete.</p></div></section>
                        <section><span class="tdec-result__number">03</span><div><h3>Ask for the complete OSSF file</h3><ul class="tdec-document-list"><li>Permit application and permit</li><li>Authorization to Construct</li><li>Site evaluation and planning materials</li><li>Inspection and Notice of Approval</li><li>Repair, alteration, complaint, and maintenance records</li></ul></div></section>
                    </div>
                </section>
            </div>
        </section>

        @template.tags.stateRecordsReturn(stateCode = "TX", stateName = "Texas", officeLabel = "the local OSSF authorized agent")

        <section class="tdec-outcomes" id="after-search" aria-labelledby="tx-outcomes-title">
            <div class="container tdec-outcomes__grid">
                <header><span class="eyebrow">After the authority search</span><h2 id="tx-outcomes-title">What happened?</h2><p>An OARS contact result identifies the regulator. It is not the property permit itself.</p></header>
                <div class="tdec-outcomes__list">
                    <details data-state-fallback-outcome="found"><summary><span>I found an OSSF file</span><small>Check approval, property match, design, and later work</small></summary><div><p>Match the address or legal description, owner, permit number, and dates. Confirm the Notice of Approval and whether later repair or alteration records change the original design.</p><a href="/offer-prep-septic-file-check/">Check the file before a property decision</a></div></details>
                    <details data-state-fallback-outcome="wrong_agency blocked"><summary><span>The authority has no public search</span><small>Request the file from the listed authorized agent</small></summary><div><p>Use the OARS contact and send the property keys prepared above. Ask for electronic copies and a written no-record response.</p><button class="text-button" type="button" data-tx-open-request>Prepare an OSSF request</button></div></details>
                    <details data-state-fallback-outcome="not_found_online"><summary><span>No property result appeared</span><small>Retry legal description, owner, parcel, and permit number</small></summary><div><p>A blank portal result does not establish that the OSSF is unpermitted. Retry alternate identifiers, then request the file manually.</p><button class="text-button" type="button" data-tx-open-request>Prepare a no-result request</button></div></details>
                    <details data-state-fallback-outcome="no_record_response"><summary><span>The authority confirmed no record</span><small>Ask about predecessor jurisdictions and exemptions</small></summary><div><p>Keep the written response. Ask what date the local program began, whether TCEQ or a prior authority has older files, and whether the office is saying no file was found rather than declaring the system exempt.</p></div></details>
                </div>
            </div>
        </section>

        <section class="tdec-request" data-tx-request-section hidden aria-labelledby="tx-request-title">
            <div class="container tdec-request__grid"><header><span class="eyebrow">Manual fallback</span><h2 id="tx-request-title">Prepare an OSSF file request</h2><p>Send this wording to the authorized agent shown in OARS or through the verified county instructions.</p></header><div class="tdec-request__output"><label for="tx-request-copy">Request wording to copy</label><textarea id="tx-request-copy" readonly data-tx-request-copy></textarea><div><button class="button button--secondary" type="button" data-tx-copy-request>Copy request wording</button><a class="button button--primary" data-tx-request-route href="#">Open county instructions</a></div></div></div>
        </section>

        <section class="tdec-reference" aria-labelledby="tx-reference-title"><div class="container"><h2 id="tx-reference-title">Official-source notes</h2><p>TCEQ says it is not often the permitting authority. Local authorized agents administer most OSSF programs; the TCEQ regional office handles areas without an authorized local jurisdiction.</p><nav aria-label="Texas official sources"><a href="https://www6.tceq.texas.gov/oars/index.cfm?fuseaction=search.county" target="_blank" rel="noreferrer">TCEQ local authority search</a><a href="https://www.tceq.texas.gov/permitting/ossf/ossfpermits.html" target="_blank" rel="noreferrer">TCEQ OSSF permit guidance</a><a href="https://www.tceq.texas.gov/permitting/ossf/ossf_contacts.html" target="_blank" rel="noreferrer">TCEQ OSSF contacts</a></nav><p class="tdec-reference__disclosure"><strong>Independent guide.</strong> SepticPath is not affiliated with TCEQ or a local authority and cannot certify permit status, system condition, or compliance.@if (editorialLastReviewedAt != null && !editorialLastReviewedAt.isBlank()) Official route review: ${editorialLastReviewedAt}.@endif</p></div></section>
        <script src="${AssetPaths.versioned("/texas-ossf-records.js")}" defer></script>
        <script src="${AssetPaths.versioned("/state-records-return.js")}" defer></script>
    </div>
`)
