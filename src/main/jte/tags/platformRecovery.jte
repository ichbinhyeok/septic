@param String platform
<section class="section section--tight" id="search-problems" aria-labelledby="search-problems-title">
    <div class="container">
        <span class="eyebrow">Match the next step to the missing item</span>
        <h2 id="search-problems-title">${"NH".equals(platform) ? "OneStop: approval found, but where is the plan?" : "OnlineRME: why the expected file may not appear."}</h2>
        <div class="detail-grid">
        @if ("NH".equals(platform))
            <article class="detail-card"><h3>A newer approval appears, without a drawing</h3><p>NHDES distinguishes approvals from plan availability: most post-1986 approvals are searchable, while electronic plans are available for most systems approved after 2015. For an older plan, use the archive request even when the approval already appears online.</p><a href="https://onlineforms.nh.gov/?formtag=NHDES-W-05-010" target="_blank" rel="noreferrer" data-track-click="nav" data-track-source-context="nh_missing_plan_archive" data-track-target-type="official_source">Request the missing plan ↗</a></article>
            <article class="detail-card"><h3>An older system returns nothing</h3><p>For 1967–1986 approvals, use the file/archive request. The town may also hold plans or approvals. Supply the town, map and lot, approximate year and owner-at-installation clue; identify uncertain information as uncertain.</p><a href="/no-septic-records-found/">Build a focused missing-file request →</a></article>
            <article class="detail-card"><h3>The file contains one approval</h3><p>NHDES identifies Construction Approval and Operational Approval as separate records. Keep their dates and status separate. Ask for the missing approval; do not relabel an approved design as evidence of installed work.</p><a href="/septic-final-approval-records/">Read the approval stages →</a></article>
        @else
            <article class="detail-card"><h3>Loudoun address search returns nothing</h3><p>Select Virginia and Loudoun County. The county recommends Tax ID as PIN plus 000, or street number alone; the latter may omit files. This PIN suffix instruction is specific to Loudoun, not every OnlineRME county.</p><a href="/septic-records-checklist/virginia/loudoun-county/">Follow Loudoun’s record route →</a></article>
            <article class="detail-card"><h3>Loudoun land has approval but no installed system</h3><p>Try Loudoun Scanned Records for uninstalled systems: PSSD identifies septic files and WW well files. For approved subdivisions not yet built out, use Loudoun Parent Files. These are different collections, not alternate spellings of the same address search.</p><a href="https://www.loudoun.gov/5771/Well-Septic-Records" target="_blank" rel="noreferrer" data-track-click="nav" data-track-source-context="onlinerme_loudoun_collection" data-track-target-type="official_source">Open the county’s collection instructions ↗</a></article>
            <article class="detail-card"><h3>King County service history appears, without an as-built</h3><p>Check the separate county septic-record search for the design or as-built. A maintenance or pumping entry answers a different question. Record the missing document before asking for help, rather than requesting another copy of the service history.</p><a href="/septic-records-checklist/washington/king-county/">Find King County’s drawing route →</a></article>
        @endif
        </div>
        @if ("NH".equals(platform))
        <p><strong>Example, not an actual property:</strong> a 1998 approval with no attached layout should lead to a plan request, not a conclusion that the property was never approved. State the approval number already found and ask for the associated plan and any missing approval stage.</p><p><a href="https://www.des.nh.gov/sites/g/files/ehbemt341/files/documents/ssb-14.pdf" target="_blank" rel="noreferrer">NHDES SSB-14: record dates and approval types (PDF)</a> · Reviewed November 18, 2026. Dates describe typical coverage, not a guarantee that a particular file exists.</p>
        @else
        <p><strong>Still no usable file?</strong> Keep the county, collection and identifiers searched. Ask the local office for the specific missing plan, approval or repair record. Loudoun directs unresolved searches to its Health Department; an empty platform result does not establish a no-record finding.</p><p>Official county instructions reviewed November 18, 2026. No successful property-level search is implied by this guide.</p>
        @endif
    </div>
</section>
