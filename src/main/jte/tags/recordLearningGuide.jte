@param String slug

@if (java.util.Set.of("septic-tank-location-records", "how-to-find-septic-records-online", "septic-permit-search-by-address", "septic-permit-process", "septic-transfer-compliance", "buying-a-house-with-a-septic-system", "septic-permit-records-request").contains(slug))
<section class="record-learning section" aria-label="Understand your septic records" data-reading-guide="${slug}">
    <div class="container record-learning__body">
        <span class="eyebrow">From the search to the answer</span>
        @if ("septic-tank-location-records".equals(slug))
            <h2 id="location-projects">What do you need the location for?</h2>
            <p class="record-learning__lede">A record drawing can help you prepare for a site visit. The next question depends on whether you are planning construction or trying to service the existing tank.</p>
            <nav class="record-learning__contents" aria-label="Choose your project">
                <a href="#location-pool">Pool or landscaping</a><a href="#location-addition">Home addition</a><a href="#location-pumping">Pumping or service</a><a href="#location-reading">Read a real sketch</a>
            </nav>
            <article id="location-pool">
                <h3>Planning a pool: the tank is only part of the picture</h3>
                <p>Finding a tank symbol does not identify every part of the system. Request the drain-field layout and any replacement-area information as well. Look for later repair drawings before assuming that the oldest sketch is the current arrangement.</p>
                <p>Keep the planned pool footprint separate from the historical drawing. A local professional and permitting authority can check field conditions and project requirements; the record search itself does not approve the pool location.</p>
            </article>
            <article id="location-addition">
                <h3>Adding to the house: compare use as well as location</h3>
                <p>A drawing helps identify possible conflicts with the proposed footprint. The permit may answer a different question: what use or design capacity was recorded? Bring both to the local reviewer, along with any repair or installation records.</p>
                <p>A historical approval for the existing system does not by itself approve an addition. If a page is missing, name it in the request instead of treating the incomplete packet as the full history.</p>
            </article>
            <article id="location-pumping">
                <h3>Arranging pumping: give the provider a useful starting point</h3>
                <p>Share the original drawing, its date, and any dimensions tied to recognizable features. Tell the provider when the house, driveway, or landscaping has changed. Do not convert an unscaled scan into precise coordinates.</p>
                <p>Ask a septic professional to confirm the components on site. Avoid opening or entering a tank yourself.</p>
            </article>
            <article class="record-learning__story" id="addition-file-case" data-reading-milestone="addition-file-case">
                <span class="eyebrow">Actual request · St. Croix County, Wisconsin</span>
                <h3>The archive packet needed more than a permit-summary number.</h3>
                <p>We matched the parcel to the County archive and reviewed the packet for recorded size, type and layout, separating the revised system pages from superseded layouts and an unrelated appendix.</p>
                <p>The reviewed records described a four-bedroom, 600-gallon-per-day design, one tank and three dispersal cells using 45 chambers. But the tank capacity was not consistent: the inspection and application recorded 1,200 gallons, while the revised plan showed 1,255. We preserved that difference rather than silently choosing a number.</p>
                <p>We organized the relevant material into a three-page extract and explanation. The drawing supplied relative layout clues, not precise field coordinates or approval for a new addition. Its rectangular tank symbol did not establish the tank's outside dimensions.</p>
                <p class="record-learning__note">This is a separate completed records request, not the Tennessee sketch shown below. No source image from another property is presented as this case's drawing.</p>
                <a href="/septic-records-checklist/wisconsin/st-croix-county/" data-track-click="nav" data-track-source-context="learning_addition_case" data-track-target-type="county_records_page">See the St. Croix record route and case findings →</a>
            </article>
            @template.tags.recordFindingVisual(variant = "stcroix")
            <article class="record-learning__story" id="location-reading" data-reading-milestone="location-sketch">
                <span class="eyebrow">A real drawing, read carefully</span>
                <h3>Three lines on a sketch can answer one question and leave another open.</h3>
                <p>In the historical Tennessee drawing already shown in our public example, the house, a small tank symbol, and three dashed field runs are visible. The lines carry written lengths. That gives the reader something concrete to follow: which parts are drawn, how they relate to the house, and what the labels actually say.</p>
                <p>It does not make the picture a survey. The visible line lengths do not establish the width of the field, a current underground location, or permission to build nearby. The document type matters too: a proposed layout and an installation record have different roles.</p>
                <p>This is why the useful result includes both the source and its explanation. A reader should be able to see where a statement came from, without turning an old drawing into a claim about today's ground conditions.</p>
                <a href="/septic-as-built-records/#layout-case" data-track-click="nav" data-track-source-context="learning_location_drawing" data-track-target-type="internal_page">Read how we found the layout, then follow the real sketch →</a>
            </article>
            <p class="record-learning__source">Official background: <a href="https://www.epa.gov/septic/frequent-questions-septic-systems" target="_blank" rel="noreferrer">EPA guidance on record drawings, professional locating, and construction around drainfields</a>. Local project requirements still need local review.</p>
        @elseif ("how-to-find-septic-records-online".equals(slug))
            <h2 id="no-record-online">No septic records found online? Check what “no result” means.</h2>
            <p class="record-learning__lede">An empty screen describes the search you just ran. It does not establish whether a system exists, whether older paper records survive, or whether another office holds the file.</p>
            <dl class="record-learning__steps">
                <div><dt>01 · The portal would not open</dt><dd>Record the access problem and use the office's published request route. A blocked viewer is not a no-record response.</dd></div>
                <div><dt>02 · The address returned no match</dt><dd>Check the parcel, earlier address, prior owner, subdivision or lot if the search supports them. Keep candidate results separate until the property matches.</dd></div>
                <div><dt>03 · A permit row appeared, but no file</dt><dd>Use the permit reference to ask for the actual documents and attachments. Index metadata cannot stand in for the drawing you need.</dd></div>
                <div><dt>04 · The office said no record was found</dt><dd>Preserve the response. Ask which property identifiers, years and archives were checked and whether another custodian may hold older files. Do not reinterpret the response as proof that no tank exists.</dd></div>
            </dl>
            <div class="record-learning__pair" data-reading-milestone="no-result-examples">
                <article><span class="eyebrow">Illustrative search example</span><h3>The property is old. The address is newer.</h3><p>A modern address produces nothing, while an earlier owner or lot description produces a candidate. The next task is to connect those identities. Finding a similar name is a lead, not the end of the check.</p><a href="/septic-permit-search-by-address/#old-address-records">Follow the property clues →</a></article>
                <article><span class="eyebrow">Illustrative search example</span><h3>The office accepted the request.</h3><p>A request number confirms that a request was created. It does not confirm that the requested drawing has been found. Keep “requested,” “received,” and “matched to the property” as separate steps.</p><a href="/septic-permit-records-request/#request-follow-through">What to ask for next →</a></article>
            </div>
        @elseif ("septic-permit-search-by-address".equals(slug))
            <h2 id="old-address-records">When an old septic record has no current street address</h2>
            <p class="record-learning__lede">Work from the property toward the document. An old owner name or road name can help locate a candidate, but several clues should agree before you rely on it.</p>
            <ol class="record-learning__steps">
                <li><strong>Establish today's parcel.</strong> Use the official property record to identify the parcel and legal description. A mailing address alone can point to the wrong place.</li>
                <li><strong>Collect earlier identifiers.</strong> Look for prior owner names, subdivision and lot, older road names, permit references, or recorded parcel changes.</li>
                <li><strong>Compare the whole packet.</strong> Check visible property identifiers and the sketch's orientation and neighboring features. Do not resolve a conflict just because the email subject uses the right address.</li>
                <li><strong>State what remains uncertain.</strong> If the older file cannot be linked securely, ask the custodian to confirm it. Keep it separate from the verified property packet.</li>
            </ol>
            <p class="record-learning__note">A boundary change, nearby lot, or similar owner name can explain a tempting but incorrect result. Preserve the uncertainty instead of filling the gap with an assumption.</p>
            <article class="record-learning__story" id="old-address-case" data-reading-milestone="old-address-case">
                <span class="eyebrow">Actual request · Overton County, Tennessee · limited match</span>
                <h3>The archive found a repair record—but it had no street number.</h3>
                <p>Public sources did not supply a directly accessible permit or layout. We connected the current property to earlier owner and road-name clues and included those clues in the TDEC archive request.</p>
                <p>The returned file documented a 1993 repair. The former-owner and road wording supported a possible connection, but the document did not print the modern street number. We disclosed that limitation with the delivered record instead of claiming a certain property match.</p>
                <p>That distinction matters: finding a plausible historical file is useful progress, but its system specifications must not become confirmed facts about today's property until the identity link is secure. A correct request subject or agency reply does not supply a missing identifier on the attachment.</p>
                <a href="/septic-records-checklist/tennessee/overton-county/" data-track-click="nav" data-track-source-context="learning_old_address_case" data-track-target-type="county_records_page">Read the returned-file findings and matching limitation →</a>
            </article>
            @template.tags.recordFindingVisual(variant = "overton")
            <a href="/how-to-find-septic-records-online/#no-record-online" data-track-click="nav" data-track-source-context="learning_address_no_result" data-track-target-type="internal_page">Still no match? Choose the next search step →</a>
        @elseif ("septic-permit-process".equals(slug))
            <h2 id="permit-status-reading">A passed inspection and a final permit answer different questions.</h2>
            <p class="record-learning__lede">Read the status of the document you need, not just the most reassuring word on the screen. Names and approval steps vary by jurisdiction.</p>
            <dl class="record-learning__steps">
                <div><dt>Permit issued</dt><dd>Check what work the permit authorizes. An issued permit alone does not show that installation and every closeout step are complete.</dd></div>
                <div><dt>Inspection result</dt><dd>Read the inspection type, date and scope. A satisfactory entry answers a question about that inspection; inspect the remaining permit workflow separately.</dd></div>
                <div><dt>Record drawing</dt><dd>Check whether the installed layout has been submitted, reviewed and made available. An approved design and a record of installed work are different documents.</dd></div>
                <div><dt>Final or operating approval</dt><dd>Locate the actual closeout document or obtain the authority's written explanation of the current status. Do not infer it from a contractor's completion date.</dd></div>
            </dl>
            <article class="record-learning__story" data-reading-milestone="permit-status-example"><h3>Try this reading exercise</h3><p><strong>Illustrative status screen:</strong> permit issued; one inspection marked satisfactory; no final date displayed. The supported statement is that the screen shows an issued permit and that inspection result. Whether final approval exists needs another document or an agency answer.</p><p>King County separates its septic document search from permitting services. Its local guide provides a concrete starting point for checking those different sources.</p><a href="/septic-records-checklist/washington/king-county/" data-track-click="nav" data-track-source-context="learning_permit_king" data-track-target-type="county_records_page">Follow the King County record route →</a></article>
            <article class="record-learning__story" id="design-final-case" data-reading-milestone="design-final-case">
                <span class="eyebrow">Actual request · Anderson County, South Carolina</span>
                <h3>The permit planned 300 feet. Final approval recorded 200.</h3>
                <p>We reviewed the permit, layout and recorded capacity together. After the property identifiers were matched, the state file supplied both the construction permit and the later final approval—not just one page carrying a reassuring title.</p>
                <p>The construction permit planned 300 feet of trench. The final installation record listed three trenches of 40, 80 and 80 feet, totaling 200. We explained the two document stages instead of averaging the lengths or treating the earlier design as the installed configuration.</p>
                <p>The file also specified a 1,000-gallon tank and 360 gallons per day of design flow. It did not directly state an approved bedroom count, so we did not invent one from those numbers. The customer received the official record and an explanatory guide.</p>
                <p class="record-learning__note">Final approval described the historical installation. The drawing was not to scale and its GPS fields were blank; present condition and precise underground location remained outside the record's answer.</p>
                <a href="/septic-records-checklist/south-carolina/anderson-county/" data-track-click="nav" data-track-source-context="learning_design_final_case" data-track-target-type="county_records_page">See the Anderson file findings and official record route →</a>
            </article>
        @elseif ("septic-transfer-compliance".equals(slug))
            <h2 id="sale-record-packet">Build a septic record packet a buyer can follow.</h2>
            <p class="record-learning__lede">Put each document next to the question it can answer. A neatly labeled packet is easier to review than an unexplained collection of scans.</p>
            <dl class="record-learning__steps">
                <div><dt>What was approved?</dt><dd>Permit and approval pages, including the recorded design use or capacity where shown.</dd></div>
                <div><dt>Where was it planned or installed?</dt><dd>The drawing, with its document type and date. Separate a proposed plan from an installation record.</dd></div>
                <div><dt>What changed later?</dt><dd>Repair permits, revised drawings and related inspection or approval records. Keep superseded versions labeled.</dd></div>
                <div><dt>What needs a fresh check?</dt><dd>Current condition, present field location and any locally required transfer inspection. Historical records do not settle those questions.</dd></div>
            </dl>
            <p>Start with a one-page index: document, date, question answered, and missing item. If an attachment is absent, list it as missing rather than describing the packet as complete. Ask the local authority which transfer requirements apply to the property.</p>
            <a href="/septic-record-brief-example/#completed-brief" data-track-click="nav" data-track-source-context="learning_sale_example" data-track-target-type="internal_page">See an original packet turned into a readable brief →</a>
        @elseif ("buying-a-house-with-a-septic-system".equals(slug))
            <h2 id="seller-claim-records">Does the record actually support the seller's statement?</h2>
            <p class="record-learning__lede">Write the claim as a question, then identify the document that could answer it. Keep an unanswered question open until the evidence supports a conclusion.</p>
            <article class="record-learning__story" data-reading-milestone="buyer-two-tanks"><span class="eyebrow">Illustrative review scenario</span><h3>“There are two tanks.” The drawing shows one.</h3><p>One tank on one historical drawing confirms what that drawing depicts. It does not establish whether a second tank was installed later, omitted from that sheet, or never present.</p><p>Check the property match and drawing date, then request later installation or repair records and the complete file. A professional field check may still be needed. The useful outcome is a precise unanswered question, not an unsupported yes or no.</p></article>
            <div class="record-learning__pair"><article><h3>“The system supports the listed bedrooms.”</h3><p>Compare the recorded design basis with the listing. Tank size alone should not be substituted for an approved bedroom count.</p></article><article><h3>“The drain field was replaced.”</h3><p>Ask for the repair or replacement file and the related inspection or approval record. A quote or invoice and an official approval have different roles.</p></article></div>
            <a href="/septic-transfer-compliance/#sale-record-packet" data-track-click="nav" data-track-source-context="learning_buyer_packet" data-track-target-type="internal_page">See the document checklist for a sale →</a>
        @elseif ("septic-permit-records-request".equals(slug))
            <h2 id="request-follow-through">After you request the file, what should happen next?</h2>
            <p class="record-learning__lede">Track the request, the returned documents and the property match separately. Each answers a different question.</p>
            <ol class="record-learning__steps"><li><strong>Save the acknowledgement.</strong> Keep the reference number, requested document list, fee notice and the authority's stated response date.</li><li><strong>Check the return against the request.</strong> A permit cover page may arrive without the drawing. Ask for the missing attachment in the existing thread.</li><li><strong>Match each document.</strong> Check property identifiers and the visible drawing before interpreting it. A correctly addressed cover email does not make every attachment correct.</li><li><strong>Describe an incomplete result honestly.</strong> List what arrived, what remains pending, and which question cannot yet be answered.</li></ol>
            <p class="record-learning__note">Tell the office your deadline, but do not assume its response date will move. If your transaction deadline comes first, discuss the missing evidence with the people handling the transaction. A record request cannot guarantee the timing of an agency response.</p>
            <article id="request-status-check"><h3>Sent, accepted, paid and found are different states.</h3><p>A sent email establishes an attempt. An acknowledgement establishes receipt or acceptance. A fee notice establishes a charge or required next step, not that the file has been found. Ask what has actually happened before describing a request as processing or complete.</p><p>Keep the amount, any separate portal charge, approval and payment status together. Never treat a payment option offered to the customer as a payment already made.</p></article>
            <article id="deadline-record-gap"><h3>The agency's date may fall after your deadline.</h3><p>Keep both dates visible. Prepare a bounded update listing the sources checked, documents received, unanswered questions and next responsible party. Do not promise a faster agency response or describe a pending replacement record as proof that replacement work was approved.</p></article>
        @endif
        <p><a href="/septic-record-brief-example/#real-cases" data-track-click="nav" data-track-source-context="learning_case_collection" data-track-target-type="case_study">Explore more real record-help cases →</a></p>
        <div class="record-learning__next">
            <div><h3>Want us to connect the records to your question?</h3><p>Research and agency requests are free. See what relevant material we found and what it can answer, then choose the original records and detailed answers for US $29. Agency fees require your approval and are additional at cost.</p></div>
            <a class="button button--primary" href="/offer-prep-septic-file-check/#record-help" data-record-help-cta data-track-click="nav" data-track-source-context="${"learning_" + slug}" data-track-target-type="lead_form">Ask about my property</a>
        </div>
    </div>
</section>
@endif
