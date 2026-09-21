@import com.example.septic.web.PageMeta
@import com.example.septic.web.AssetPaths
@param PageMeta page = null
@param boolean production = false
@template.studio.shell(page = page, production = production, title = "Septic records request builder", heroImage = "/images/studio/permit-lookup-desk-v1.webp", pageClass = "studio-request-builder", pageStyles = "/studio-request-builder.css", content = @`
<main id="main" data-records-request-builder>
    <section class="request-hero" aria-labelledby="request-title">
        <img src="/images/studio/permit-lookup-desk-v1.webp" alt="Property plans and a records folder on a research desk overlooking rural land" width="1792" height="940" fetchpriority="high">
        <div class="request-hero__shade"></div>
        <div class="studio-wrap request-hero__content">
            <p class="studio-overline">Records request studio</p>
            <h1 id="request-title">Request the right record.<br>Route it to the right desk.</h1>
            <p>Build a precise property-file request, keep the search clues together, and leave with a packet you can actually use.</p>
            <a class="studio-button" href="#builder-workspace">Build my request <span aria-hidden="true">↘</span></a>
            <ul aria-label="Builder benefits"><li>Property-specific wording</li><li>Route-aware checklist</li><li>Printable packet</li></ul>
        </div>
    </section>

    <div class="request-task studio-wrap" data-records-task-context hidden>
        <p class="studio-overline">Continue this property file</p>
        <h2 data-records-task-heading>Request the missing record without starting over.</h2>
        <p data-records-task-copy>The address, county, purpose, and missing document were carried here from the records workspace.</p>
        <a href="${com.example.septic.web.StudioGuideRoutes.route("/official-septic-lookup-tools/", production)}" data-records-task-back>Back to property file ↑</a>
    </div>

    <section class="request-workspace" id="builder-workspace" aria-labelledby="workspace-title">
        <div class="studio-wrap request-workspace__heading">
            <div><p class="studio-overline">One property. One clear ask.</p><h2 id="workspace-title">Build the message<br>an office can act on.</h2></div>
            <p>Start with what you know. Missing parcel or owner details will remain visible as prompts instead of silently disappearing.</p>
        </div>

        <div class="studio-wrap request-layout">
            <form class="request-fields" autocomplete="off">
                <fieldset>
                    <legend><span>01</span><strong>Property</strong><small>Identify the file, not just the street.</small></legend>
                    <label class="request-field request-field--full"><span>Property address</span><input type="text" data-request-address placeholder="Street address, city and ZIP" autocomplete="street-address"></label>
                    <div class="request-field-row">
                        <label class="request-field"><span>State route</span><select data-request-state><option value="DEFAULT">County health office</option><option value="TN">Tennessee / TDEC</option><option value="NC">North Carolina / county EH</option><option value="TX">Texas / OSSF agent</option><option value="SC">South Carolina / SCDES</option><option value="FL">Florida / OSTDS health</option></select></label>
                        <label class="request-field"><span>County</span><input type="text" data-request-county placeholder="Wake, Blount, Tarrant"></label>
                    </div>
                    <details class="request-optional"><summary>Add stronger search clues <span aria-hidden="true">+</span></summary><div class="request-field-row"><label class="request-field"><span>Parcel / APN / tax ID</span><input type="text" data-request-parcel placeholder="If known"></label><label class="request-field"><span>Owner or applicant</span><input type="text" data-request-owner placeholder="Current or prior owner"></label></div></details>
                </fieldset>
                <fieldset>
                    <legend><span>02</span><strong>Record</strong><small>Ask for the artifact you need.</small></legend>
                    <div class="request-field-row">
                        <label class="request-field"><span>Record needed</span><select data-request-record><option value="permit_copy">Permit copy and permit history</option><option value="as_built">As-built, site sketch, or layout</option><option value="final_approval">Final approval or operation permit</option><option value="repair_record">Repair, malfunction, or complaint file</option><option value="inspection_letter">Inspection or lender letter</option><option value="no_record">Written no-record response</option></select></label>
                        <label class="request-field"><span>Reason</span><select data-request-reason><option value="buying">Buying or selling a house</option><option value="repair">Repair or suspected issue</option><option value="addition">Addition, pool, driveway, or grading</option><option value="replacement">Replacement or drain-field planning</option><option value="lender">Lender or closing file</option><option value="owner_records">Owner records</option></select></label>
                    </div>
                </fieldset>
                <div class="request-route" aria-live="polite">
                    <div class="request-route__number">03</div>
                    <div><p class="studio-overline">Official route</p><h3 data-records-request-route-title>County health office or onsite wastewater records desk</h3><p data-records-request-route-body>Send through the official county health, environmental health, onsite wastewater, permitting, or public-records channel.</p><a href="${com.example.septic.web.StudioGuideRoutes.route("/septic-records-by-county/", production)}">Find the county route <span aria-hidden="true">↗</span></a></div>
                </div>
                <div class="request-check"><p class="studio-overline">Before sending</p><ul data-records-request-checklist aria-label="Before sending checklist"></ul></div>
            </form>

            <aside class="request-preview" id="request-preview" aria-label="Request preview">
                <div class="request-preview__top"><div><p class="studio-overline">Live request preview</p><strong data-records-request-filename>septic-records-request.txt</strong></div><em data-records-request-status aria-live="polite">Confirm official intake before sending</em></div>
                <p class="request-preview__summary" data-records-request-preview></p>
                <div class="request-paper"><div class="request-paper__mast"><span>SepticPath</span><span>REQUEST DRAFT</span></div><textarea data-records-request-output readonly rows="18" aria-label="Generated records request"></textarea><span class="request-paper__note">Routing draft · not an official government form</span></div>
                <label class="request-confirm"><input type="checkbox" data-records-request-channel-confirmed><span><strong>I checked the official intake.</strong>The office accepts free text, or I transferred these facts into its required form or portal.</span></label>
                <div class="request-actions">
                    <button class="studio-button" type="button" data-records-request-copy>Copy request <span aria-hidden="true">↗</span></button>
                    <button type="button" data-records-request-download>Download packet <span aria-hidden="true">↓</span></button>
                    <button type="button" data-records-request-print>Print / save PDF <span aria-hidden="true">↗</span></button>
                    <button type="button" data-records-request-mark-sent disabled>Mark sent <span aria-hidden="true">✓</span></button>
                </div>
                <p class="request-privacy">Nothing is submitted by this page. Saved progress remains only in this browser for up to 30 days.</p>
            </aside>
        </div>
    </section>

    <section class="request-proof" aria-labelledby="proof-title">
        <img src="/images/studio/official-lookup-files-v1.webp" alt="A permit application, as-built sketch, final approval and repair history arranged on a desk" width="1456" height="1024" loading="lazy">
        <div><p class="studio-overline">What makes it actionable</p><h2 id="proof-title">A complete request gives the archive more ways to say yes.</h2><p>The address begins the search. Parcel IDs, prior owners, exact document names, and a written no-record fallback are what keep it moving when the first lookup is blank.</p><ol><li><span>01</span><strong>Identify the property</strong><p>Give staff more than one reliable way to match the file.</p></li><li><span>02</span><strong>Name the artifact</strong><p>Permit, layout, final approval, repair history, or no-record response.</p></li><li><span>03</span><strong>Ask for the next owner</strong><p>If the first desk does not hold it, ask who does.</p></li></ol></div>
    </section>

    <section class="request-tracking studio-wrap" aria-labelledby="tracking-title">
        <div class="request-tracking__intro"><p class="studio-overline">After you send</p><h2 id="tracking-title">Keep the request moving.</h2><p>Save a private follow-up note on this device. This is a planning reminder, not a legal response deadline.</p></div>
        <details data-records-request-tracker><summary>Track this request <span aria-hidden="true">+</span></summary><div class="request-tracking__fields">
            <label class="request-field"><span>Status</span><select data-records-request-progress-status><option value="draft">Draft</option><option value="sent">Sent — waiting for response</option><option value="received">Response received</option><option value="no_record">Written no-record response received</option><option value="closed">Finished</option></select></label>
            <label class="request-field"><span>Date sent</span><input type="date" data-records-request-sent-date></label>
            <label class="request-field"><span>Follow-up plan</span><select data-records-request-followup-window><option value="7">Check again in 7 days</option><option value="14">Check again in 14 days</option><option value="custom">Choose a date</option></select></label>
            <label class="request-field"><span>Personal follow-up date</span><input type="date" data-records-request-followup-date></label>
            <label class="request-field request-field--wide"><span>Office or response note</span><input type="text" maxlength="240" placeholder="Staff name, confirmation number, or next office" data-records-request-progress-note></label>
        </div><div class="request-tracking__actions"><button class="studio-button" type="button" data-records-request-save-progress>Save progress</button><button type="button" data-records-request-clear-progress>Clear saved progress</button></div><p data-records-request-progress-message aria-live="polite">No progress saved on this device.</p></details>
    </section>

    <section class="request-final"><img src="/images/studio/permit-file-spread-v1.webp" alt="A rich property permit file with site plan, inspection notes and correspondence" width="1456" height="1024" loading="lazy"><div class="studio-wrap"><p class="studio-overline">Need more than a draft?</p><h2>We can research the route,<br>contact the office, and close the loop.</h2><a class="studio-button" href="${com.example.septic.web.StudioGuideRoutes.route("/offer-prep-septic-file-check/", production)}">Get help with my property <span aria-hidden="true">↗</span></a></div></section>
</main>
<script src="${AssetPaths.versioned("/app.js")}" defer></script>
`)
