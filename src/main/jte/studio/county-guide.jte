@import com.example.septic.web.PageMeta
@import com.example.septic.data.model.CountyRecordsPage
@import com.example.septic.data.model.StateProfile
@import com.example.septic.data.model.SourceRecord
@import com.example.septic.web.CountyLocalContentView
@import com.example.septic.web.CountyAccessProfileView
@import com.example.septic.web.CountySearchGuideView
@import com.example.septic.web.CountyWorkflowFieldView
@param CountyRecordsPage countyPage
@param StateProfile state
@param com.example.septic.web.StudioCaseStudies.Study featuredStudy
@param CountyLocalContentView countyLocalContent
@param CountyAccessProfileView countyAccessProfile
@param CountySearchGuideView countySearchGuide = null
@param java.util.List<SourceRecord> sources
@param java.util.List<CountyWorkflowFieldView> countyOfficialFilePathRows = java.util.List.of()
@param String editorialLastReviewedAt = ""

@param PageMeta page = null
@param boolean production = false
@template.studio.shell(page = page, production = production, title = countyPage.countyName() + " septic records", heroImage = "/images/studio/county-property-v1.webp", content = @`
<main id="main">
    <section class="studio-hero county-hero" aria-labelledby="county-title">
        <img class="studio-hero__image" src="/images/studio/county-property-v1.webp" alt="" width="1672" height="941" fetchpriority="high">
        <div class="studio-hero__shade"></div>
        <div class="studio-wrap studio-hero__inner">
            <div class="studio-hero__copy">
                <p class="studio-overline">${state.stateName()} / Septic records</p>
                <h1 id="county-title">${countyPage.countyName()}.<br>Your property.<br>Its paper trail.</h1>
                <p class="studio-hero__intro">Septic records, official routes,<br>and a clearer next step.</p>
                <a class="studio-button" href="${com.example.septic.web.StudioGuideRoutes.route("/offer-prep-septic-file-check/", production)}">Investigate my property <span aria-hidden="true">↗</span></a>
                <a class="studio-text-link" href="#official-route">Find the official route <span aria-hidden="true">→</span></a>
            </div>
        </div>
    </section>
    <nav class="county-index studio-wrap" aria-label="On this page"><a href="#overview">Overview</a><a href="#official-route">Official route</a><a href="#our-research">Our research</a><a href="#the-file">The file</a><a href="#questions">Questions</a><a href="#sources">Sources</a></nav>

    <section class="county-overview studio-wrap" id="overview" aria-labelledby="overview-title">
        <div><p class="studio-overline">${countyPage.countyName()} / ${state.stateName()}</p><h2 id="overview-title">A local route.<br>Not another dead end.</h2><p class="county-deck">Start with the right office. Identify the property.<br>Understand what the response actually means.</p></div>
        <dl class="studio-facts"><div><dt>Records office</dt><dd><a href="${countyPage.officeUrl()}" target="_blank" rel="noopener noreferrer">${countyPage.officeLabel()} ↗</a></dd></div><div><dt>Access route</dt><dd>${countyAccessProfile.officialAccessLabel()}</dd></div><div><dt>Route scope</dt><dd>${countyAccessProfile.scopeLabel()}</dd></div><div><dt>Last reviewed</dt><dd>${editorialLastReviewedAt}</dd></div></dl>
    </section>

    @template.studio.research-proof(production = production, study = featuredStudy, countyEntry = true)

    <section class="studio-process" id="official-route" aria-labelledby="route-title">
        <div class="studio-wrap">
            <p class="studio-overline">A simple path forward</p><h2 id="route-title">Start with the source.</h2>
            <ol class="studio-steps">
                <li><span class="studio-steps__number" aria-hidden="true">01</span><div><h3>Identify the property.</h3><p>Have the address, parcel number, or previous owner information ready.</p></div><a href="#property-clues">What to have ready <span aria-hidden="true">→</span></a></li>
                <li><span class="studio-steps__number" aria-hidden="true">02</span><div><h3>Follow the official route.</h3><p>${countyAccessProfile.officialAccessLabel()}. The agency controls the final search or request.</p></div><a href="${countyAccessProfile.primaryUrl()}" target="${countyAccessProfile.primaryOpensNewWindow() ? "_blank" : "_self"}" rel="noopener noreferrer">${countyAccessProfile.primaryLabel()} <span aria-hidden="true">↗</span></a></li>
                <li><span class="studio-steps__number" aria-hidden="true">03</span><div><h3>Check what comes back.</h3><p>A missing search result is not an official no-record response.</p></div><a href="#the-file">What you may learn <span aria-hidden="true">→</span></a></li>
            </ol>
            <div class="county-details">
                <details id="property-clues"><summary>Property clues and local request instructions <span aria-hidden="true">+</span></summary><div class="county-details__body">
                    <div><h3>${countyAccessProfile.heading()}</h3><p>${countyAccessProfile.summary()}</p><p>${countyAccessProfile.scopeSummary()}</p><ul>@for (var input : countyAccessProfile.requiredInputs())<li>${input}</li>@endfor</ul>
                        @if (countyPage.hasParcelAnchor())<a class="studio-text-link" href="${countyPage.parcelAnchorUrl()}" target="_blank" rel="noopener noreferrer">${countyPage.parcelAnchorLabel()} ↗</a><p>${countyPage.parcelAnchorNote()}</p>@endif
                    </div>
                    <div><h3>Follow the local instructions</h3><ol>@for (var step : countyAccessProfile.steps())<li>${step}</li>@endfor</ol><p>${countyAccessProfile.limitation()}</p><p>${countyAccessProfile.primaryAccessNote()}</p>
                        @if (countyAccessProfile.hasSecondaryAction())<a class="studio-text-link" href="${countyAccessProfile.secondaryUrl()}" target="_blank" rel="noopener noreferrer">${countyAccessProfile.secondaryLabel()} ↗</a><p>${countyAccessProfile.secondaryAccessNote()}</p>@endif
                        <p>${countyPage.contactLine()}</p>
                    </div>
                </div></details>
                @if (countySearchGuide != null)
                <details><summary>Search instructions and coverage <span aria-hidden="true">+</span></summary><div class="county-details__body"><div><h3>Before searching</h3><p>${countySearchGuide.guide().summary()}</p><dl class="studio-facts"><div><dt>Coverage</dt><dd>${countySearchGuide.guide().coverage()}</dd></div><div><dt>Search with</dt><dd>${countySearchGuide.guide().identifiers()}</dd></div><div><dt>Documents</dt><dd>${countySearchGuide.guide().documents()}</dd></div></dl></div><div><h3>Search the permit and attachments</h3><ol>@for (var step : countySearchGuide.guide().steps())<li>${step}</li>@endfor</ol><p>${countySearchGuide.guide().noResult()}</p><p class="studio-fine">Instructions reviewed ${countySearchGuide.guide().reviewedAt()}</p></div></div></details>
                @endif
                <details><summary>Local context, record limits, and next steps <span aria-hidden="true">+</span></summary><div class="county-details__body"><div><h3>The local context</h3><p>${countyLocalContent.introCopy()}</p><p>${countyLocalContent.uniqueAngle()}</p><ol>@for (var step : countyLocalContent.decisionSteps())<li>${step}</li>@endfor</ol></div><div><h3>What still needs checking</h3><ul>@for (var limit : countyLocalContent.lowEndBreakers())<li>${limit}</li>@endfor</ul><dl class="studio-facts">@for (var row : countyOfficialFilePathRows)<div><dt>${row.label()}</dt><dd>${row.value()}</dd></div>@endfor</dl></div></div></details>
                @if (countyPage.hasActionPlaybook())
                <details><summary>Request wording and buyer, seller, or contractor checklists <span aria-hidden="true">+</span></summary><div class="county-details__body"><div>
                    @if (countyPage.requestScriptBody() != null && !countyPage.requestScriptBody().isBlank())<h3>${countyPage.requestScriptHeading()}</h3><p class="county-request-copy">${countyPage.requestScriptBody()}</p>@endif
                    @if (countyPage.redFlagChecklist() != null && !countyPage.redFlagChecklist().isEmpty())<h3>Watch for these gaps</h3><ul>@for (var item : countyPage.redFlagChecklist())<li>${item}</li>@endfor</ul>@endif
                </div><div>
                    @if (countyPage.buyerActions() != null && !countyPage.buyerActions().isEmpty())<h3>Buying a property</h3><ul>@for (var item : countyPage.buyerActions())<li>${item}</li>@endfor</ul>@endif
                    @if (countyPage.sellerActions() != null && !countyPage.sellerActions().isEmpty())<h3>Selling a property</h3><ul>@for (var item : countyPage.sellerActions())<li>${item}</li>@endfor</ul>@endif
                    @if (countyPage.contractorActions() != null && !countyPage.contractorActions().isEmpty())<h3>Planning work</h3><ul>@for (var item : countyPage.contractorActions())<li>${item}</li>@endfor</ul>@endif
                </div></div></details>
                @endif
            </div>
        </div>
    </section>

    <section class="county-file" id="the-file" aria-labelledby="file-title">
        <img src="/images/studio/research-desk-v1.webp" alt="SepticPath research folder with an illustrative site drawing" width="1672" height="941" loading="lazy">
        <div class="county-file__copy"><p class="studio-overline">From records to clarity</p><h2 id="file-title">What the file<br>can tell you.</h2><p>Look for the documents that explain the property's septic history.</p><ul><li>Permit history<span>What was permitted, and when.</span></li><li>Site plans<span>The documented layout and location.</span></li><li>Repair records<span>Changes recorded in the official file.</span></li></ul><p class="studio-fine">These are possible file types, not a guarantee of availability. A record is not a current-condition inspection.</p><details class="county-record-list"><summary>What to request in ${countyPage.countyName()} <span aria-hidden="true">+</span></summary><ul>@for (var record : countyLocalContent.recordsToRequest())<li>${record}</li>@endfor</ul></details><a class="studio-text-link" href="/septic-record-brief-example/${featuredStudy.slug()}/">View real delivered work <span aria-hidden="true">↗</span></a></div>
    </section>

    <section class="studio-closing county-closing" aria-labelledby="closing-title"><img src="/images/studio/oak-pasture-v1.webp" alt="" width="1672" height="941" loading="lazy"><div class="studio-wrap"><p class="studio-overline">Your next step</p><h2 id="closing-title">Have the address?<br>We can take it from here.</h2><p>We'll help trace the file and explain the next step.</p><a class="studio-button" href="${com.example.septic.web.StudioGuideRoutes.route("/offer-prep-septic-file-check/", production)}">Investigate my property <span aria-hidden="true">↗</span></a></div></section>

    <section class="county-questions studio-wrap" id="questions" aria-labelledby="questions-title"><p class="studio-overline">Common questions</p><h2 id="questions-title">Answers for what's next.</h2><div class="county-details">@for (var faq : countyPage.faqBlocks())<details><summary>${faq.question()} <span aria-hidden="true">+</span></summary><div class="county-answer"><p>${faq.answer()}</p></div></details>@endfor</div></section>

    <section class="county-sources" id="sources" aria-labelledby="sources-title"><div class="studio-wrap"><div class="county-source-heading"><div><p class="studio-overline">The official sources</p><h2 id="sources-title">Local facts.<br>Traceable sources.</h2></div><div><p>SepticPath is an independent research service, not a government office. The custodian determines record availability, fees, and processing time.</p><a class="studio-text-link" href="/septic-system-cost-calculator/${state.slug()}/">Explore ${state.stateName()} <span aria-hidden="true">→</span></a></div></div><details class="county-source-list"><summary>View ${sources.size()} official sources <span aria-hidden="true">+</span></summary><ul class="studio-sources">@for (var source : sources)<li><a href="${source.url()}" target="_blank" rel="noopener noreferrer">${source.title()} ↗</a><span>${source.agencyName()} · reviewed ${source.contentVerifiedAt()}</span></li>@endfor</ul></details></div></section>
</main>
`)
