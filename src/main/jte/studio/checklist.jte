@import com.example.septic.web.PageMeta
@import com.example.septic.web.StudioCaseStudies.Study
@param Study featuredStudy
@param java.util.List<com.example.septic.web.StudioPreviewController.GuideState> guideStates = java.util.List.of()
@param PageMeta page = null
@param boolean production = false
@template.studio.shell(page = page, production = production, title = "The septic records checklist", heroImage = "/images/studio/checklist-desk-v1.webp", pageClass = "studio-checklist", pageStyles = "/studio-checklist.css", content = @`
<main id="main">
    <section class="checklist-hero" aria-labelledby="checklist-title">
        <img src="/images/studio/checklist-desk-v1.webp" alt="Illustrative property research desk overlooking a rural landscape" width="1817" height="866" fetchpriority="high">
        <div class="checklist-hero__shade"></div>
        <div class="studio-wrap checklist-hero__inner"><div><p class="studio-overline">The records checklist</p><h1 id="checklist-title">Bring the clues.<br>Ask for the file.</h1><p>A practical path from property details to records you can actually use.</p><a class="checklist-light-button" href="#prepare">Start the checklist <span aria-hidden="true">↓</span></a></div></div>
    </section>

    <section class="checklist-prepare studio-wrap" id="prepare" aria-labelledby="prepare-title">
        <p class="studio-overline">Before you search</p><h2 id="prepare-title">Three things to prepare.</h2>
        <ol class="checklist-prep-list">
            <li><span aria-hidden="true">01</span><h3>Property identity</h3><p>Gather the street address, parcel or tax ID, and current or prior owner names if you have them.</p></li>
            <li><span aria-hidden="true">02</span><h3>Search history</h3><p>Note the offices, portals, search terms, and responses you have already tried—including a no-result search.</p></li>
            <li><span aria-hidden="true">03</span><h3>The decision ahead</h3><p>Name the question the record must help answer and the date you need to make your next decision.</p></li>
        </ol>
    </section>

    <section class="checklist-request" id="request" aria-labelledby="request-title">
        <div class="checklist-request__copy"><p class="studio-overline">What to request</p><h2 id="request-title">Ask for the record,<br>not just the answer.</h2><ul>
            <li>Permit and application history</li>
            <li>Site plan, as-built, or recorded layout</li>
            <li>Final approval, inspection, or completion record</li>
            <li>Repair, alteration, or replacement records</li>
            <li>Soil, perc, or site evaluation, if held</li>
            <li>Written no-record response and the next referral</li>
        </ul><p class="checklist-boundary">Names and availability vary by jurisdiction. A historical file does not establish current system condition or exact field location.</p></div>
        <figure><img src="/images/studio/checklist-plan-v1.webp" alt="Illustrative site plan and organized property record folder" width="1456" height="1080" loading="lazy"><figcaption>Illustrative research materials—not a record for your property.</figcaption></figure>
    </section>

    <section class="checklist-context studio-wrap" aria-labelledby="context-title"><div><p class="studio-overline">Read what comes back</p><h2 id="context-title">A file is evidence.<br>It is not the whole property.</h2></div><div><p>Confirm that the street, parcel, owner, subdivision, or lot details actually tie the file to the property.</p><details><summary>If the search returns nothing <span aria-hidden="true">+</span></summary><p>Record the source, date, terms used, and result. Then ask the office whether another custodian, archive, environmental health unit, or predecessor agency may hold the file. A blank portal result alone is not an official no-record response.</p></details></div></section>

    <section class="checklist-states studio-wrap" aria-labelledby="checklist-states-title"><div><p class="studio-overline">The local route</p><h2 id="checklist-states-title">Open your state’s<br>records guide.</h2></div><ul>@for(var state : guideStates)@if(state.published())<li><a href="${com.example.septic.web.StudioGuideRoutes.route("/septic-records-checklist/" + state.slug() + "/", production)}"><span>${state.name()}</span><small>${state.code()} records route</small><b aria-hidden="true">↗</b></a></li>@endif@endfor</ul></section>

    @template.studio.research-proof(production = production, study = featuredStudy, directCase = true, imageOverride = "/images/studio/checklist-case-house-v1.webp", imageNote = "Illustrative setting", imageWidth = 1561, imageHeight = 1008)

    <section class="checklist-next" aria-labelledby="checklist-next-title"><img src="/images/studio/checklist-closing-house-v1.webp" alt="" width="1774" height="887" loading="lazy"><div class="studio-wrap"><p class="studio-overline">Have the address?</p><h2 id="checklist-next-title">We can take it<br>from here.</h2><p>Send one property and the question you need answered.</p><a class="studio-button" href="${com.example.septic.web.StudioGuideRoutes.route("/offer-prep-septic-file-check/", production)}">Investigate my property <span aria-hidden="true">↗</span></a><a class="checklist-directory-link" href="${com.example.septic.web.StudioGuideRoutes.route("/septic-records-by-county/", production)}">Or find the county guide →</a></div></section>
</main>
`)
