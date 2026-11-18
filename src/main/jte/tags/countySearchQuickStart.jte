@param String countyKey
@param String recordHelpUrl = ""

@if ("AZ::maricopa-county".equals(countyKey))
    <section class="content-quick-answer" aria-label="Maricopa septic search quick start" data-search-intent-answer>
        <h2>Where do I search Maricopa County septic records?</h2>
        <p>Open the county’s free Online Septic Search and review the data disclaimer. Select Agree to continue and have the property address or assessor parcel number ready.</p>
        <p><a class="button button--secondary" href="https://www.maricopa.gov/FormCenter/Environmental-Services-16/Online-Septic-Search-92" target="_blank" rel="noreferrer" data-track-click="official_source" data-record-official-link data-track-source-context="maricopa_search_quick_start" data-track-target-type="official_source">Open free county septic search</a></p>
        <h3>If the free search returns no permit or drawing</h3>
        <p>Check the address and parcel number first. If the file still does not appear, you can ask the county to research its records.</p>
        <ul>
            <li><strong>Standard research:</strong> $30, typically 3–7 business days.</li>
            <li><strong>Expedited research:</strong> $60, typically 1–2 business days.</li>
        </ul>
        <p><a href="https://www.maricopa.gov/2581/Online-Septic-Research" target="_blank" rel="noreferrer">Check current county fees and open the research request form</a>. Fee and timing checked November 18, 2026.</p>
        <p>Looking for the drawing rather than just a permit number? <a href="/septic-as-built-records/">See which layout documents to look for</a>.</p>
    </section>
@elseif ("IN::tippecanoe-county".equals(countyKey))
    <section class="content-quick-answer" id="tippecanoe-septic-drawing" aria-labelledby="tippecanoe-septic-drawing-title" data-search-intent-answer>
        <h2 id="tippecanoe-septic-drawing-title">Have a building permit number but need the septic drawing?</h2>
        <p>Use <a href="https://www.tippecanoe.in.gov/418/Building-Permits-Online-Search" target="_blank" rel="noreferrer" data-track-click="nav" data-track-source-context="tippecanoe_building_permit_search" data-track-target-type="official_source">Building Permits Online Search</a> for a dwelling permit reference. Ask the <a href="https://www.tippecanoe.in.gov/569/Sewage-Disposal" target="_blank" rel="noreferrer" data-track-click="nav" data-track-source-context="tippecanoe_diagram_request" data-track-target-type="official_source">Health Department</a> for the septic diagram using the site address and any known parcel or permit number. The department can provide available diagrams by email or in print. Coverage varies by property.</p>
    </section>
@elseif ("NC::union-county".equals(countyKey))
    <section class="content-quick-answer" id="union-record-search" aria-label="Union County permit lookup quick start" data-search-intent-answer>
        <h2>Union County permit search: which route has the septic file?</h2>
        <ol>
            <li><strong>Start with the address and tax parcel ID.</strong> Keep prior owner, subdivision, and lot clues if the address has changed.</li>
            <li><strong>Use Evolve for building-permit clues.</strong> Open <a href="https://ucinspect.unioncountync.gov/evolvepublic/" target="_blank" rel="noreferrer" data-track-click="official_source" data-track-source-context="union_evolve_search" data-track-target-type="official_source">Permits → Permit Search</a>. Search by tax parcel number, permit number, subdivision/lot, date range, or contractor. Public searches do not require an account; applications and inspection scheduling do. A building permit is not the septic layout, and an empty result does not establish that Environmental Health has no file.</li>
            <li><strong>Request the historical septic file from Environmental Health.</strong> Use the existing septic and well records route, not an application for a new system.</li>
        </ol>
        <p><a class="button button--secondary" href="https://www.unioncountync.gov/government/departments-a-e/environmental-health/septic-systems" target="_blank" rel="noreferrer" data-track-click="official_source" data-record-official-link data-track-source-context="union_search_quick_start" data-track-target-type="official_source">Open county records guidance</a></p>
        <p>Look for <strong>Existing Septic and Well Permit Records Request</strong>. Follow the current county instructions, including any redirect to MyHD. If the form is blocked, call <a href="tel:704-283-3553">704-283-3553</a> for the current intake route.</p>
        <p><strong>County search fee:</strong> Environmental Health confirmed a $10 research fee on September 30, 2026, billed after submission and payable before the search begins. Confirm the current amount with the county. SepticPath's research and explanation are free; agency charges require your approval.</p>
        <p><small>Evolve search instructions checked November 18, 2026. The Environmental Health page blocked automated access during that check; its current form fields and fee were not reverified.</small></p>
        <p><strong>What a useful result looks like:</strong> <a href="/septic-record-brief-example/#sale-case" data-track-click="nav" data-track-source-context="union_related_case" data-track-target-type="case_study">See how we recovered and explained a missing sale file in Shelby County, Tennessee</a>. It illustrates the research process, not Union County's rules or a guaranteed outcome.</p>
        @if (!recordHelpUrl.isBlank())
            <p><a class="button button--primary" href="${recordHelpUrl}" data-record-help-cta data-track-click="nav" data-track-source-context="union_practical_guide_help" data-track-target-type="lead_form">Let us trace my Union County record →</a></p>
        @endif
        <p>Wrong Union County? This page is for North Carolina. <a href="/septic-records-by-county/">Find another county</a>.</p>
    </section>
@elseif ("NC::guilford-county".equals(countyKey))
    <section class="content-quick-answer" id="guilford-record-search" aria-label="Guilford GIS septic search quick start" data-search-intent-answer>
        <h2>How to search Guilford County septic records online</h2>
        <ol>
            <li><strong>Match the parcel.</strong> Open the <a href="https://gisdv.guilfordcountync.gov/guilford/" target="_blank" rel="noreferrer" data-track-click="official_source" data-track-source-context="guilford_gis_search" data-track-target-type="official_source">official GIS Data Viewer</a>, read its disclaimer, and search the address or REID. Save the REID and PIN for the property you actually need.</li>
            <li><strong>Check the well and septic file, not just the map.</strong> Review linked property documents and the Water Quality layers for septic tank, trench, layout, and permit-grid clues. Read every relevant page: a well document alone does not answer a septic question.</li>
            <li><strong>Fill the specific gap.</strong> If the septic file is missing or incomplete, check earlier parcel identifiers when applicable and <a href="https://guilfordcountync.nextrequest.com/" target="_blank" rel="noreferrer">request the missing records through NextRequest</a>. Keep the written office response alongside any file.</li>
        </ol>
        <p><strong>Need help from the office?</strong> Guilford publishes <a href="tel:336-641-7613">336-641-7613</a>, 8–10 a.m., for system type and location when it has an updated property file. A missing map feature is not an official no-record finding, and a historical sketch is not an on-site locate.</p>
        <p><strong>What can a drawing tell you?</strong> <a href="/septic-as-built-records/#layout-case" data-track-click="nav" data-track-source-context="guilford_related_case" data-track-target-type="case_study">Read our historical-layout investigation in Roane County, Tennessee</a> to see how we turn a source drawing into useful location clues. The example is from another county; your file must be checked separately.</p>
        @if (!recordHelpUrl.isBlank())
            <p><a class="button button--primary" href="${recordHelpUrl}" data-record-help-cta data-track-click="nav" data-track-source-context="guilford_practical_guide_help" data-track-target-type="lead_form">Let us research my Guilford County file →</a></p>
            <p>Research, located records, and a plain-English explanation are free. Any agency fee needs your approval.</p>
        @endif
    </section>
@endif
