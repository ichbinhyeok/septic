@import com.example.septic.web.PageMeta
@import com.example.septic.data.model.ContentPage
@import com.example.septic.web.AssetPaths
@import com.example.septic.web.PageLink
@import com.example.septic.web.StateMoneyPageLink
@import com.example.septic.web.ContentEvidenceLaneView
@import com.example.septic.web.CountyWorkflowFieldView
@import com.example.septic.web.ContentWorkflowCoverageView
@import com.example.septic.web.StudioCaseStudies.Study
@import com.example.septic.web.StudioGuideRoutes
@param ContentPage contentPage
@param boolean permitTopic
@param boolean recordTopic = false
@param boolean planningTopic = false
@param StudioGuideRoutes.Hero recordHero = null
@param Study featuredStudy
@param java.util.List<PageLink> internalLinks = java.util.List.of()
@param java.util.List<StateMoneyPageLink> stateMoneyPageLinks = java.util.List.of()
@param java.util.List<ContentEvidenceLaneView> contentEvidenceLanes = java.util.List.of()
@param java.util.List<CountyWorkflowFieldView> contentOfficialFilePathRows = java.util.List.of()
@param ContentWorkflowCoverageView contentWorkflowCoverage = null
@param String contentQuickAnswer = ""
@param String editorialLastReviewedAt = ""
@param String editorialReviewedAgainst = ""
@param String calculatorPath = "/septic-system-cost-calculator/"
@param PageMeta page = null
@param boolean production = false
@template.studio.shell(page = page, production = production, title = contentPage.title(), heroImage = planningTopic ? "/images/studio/oak-pasture-v1.webp" : permitTopic ? "/images/studio/research-room-v1.webp" : "/images/studio/county-property-v1.webp", mobileHeroImage = planningTopic ? "/images/studio/oak-pasture-v1.webp" : permitTopic ? "/images/studio/mobile-research-v1.webp" : "/images/studio/guides-mobile-v1.webp", pageClass = "studio-article", pageStyles = "/studio-article.css", content = @`
<main id="main">
    <section class="article-hero">
        <picture><source media="(max-width:700px)" srcset="${planningTopic ? "/images/studio/oak-pasture-v1.webp" : permitTopic ? "/images/studio/mobile-research-v1.webp" : "/images/studio/guides-mobile-v1.webp"}"><img src="${planningTopic ? "/images/studio/oak-pasture-v1.webp" : permitTopic ? "/images/studio/research-room-v1.webp" : "/images/studio/county-property-v1.webp"}" alt="" width="1672" height="941" fetchpriority="high"></picture>
        <div class="studio-wrap"><p class="studio-overline">SepticPath / ${recordHero != null ? recordHero.label() : permitTopic ? "Permits & approvals" : "Inspection planning"}</p><h1>${recordHero != null ? recordHero.heading() : permitTopic ? "A clearer path to approval." : "Know what you’re paying to learn."}</h1><p>${recordHero != null ? recordHero.intro() : permitTopic ? "Understand the office, the sequence, and the file you need." : "Define the inspection before you compare the price."}</p><a class="studio-button" href="#topic-overview">Read the guide <span aria-hidden="true">↓</span></a></div>
    </section>
    <div class="article-breadcrumb studio-wrap"><a href="${com.example.septic.web.StudioGuideRoutes.route("/states/", production)}">Property guides</a><span aria-hidden="true">/</span><span>${recordHero != null ? recordHero.label() : permitTopic ? "Permits & approvals" : "Inspection costs"}</span></div>
    @if("septic-as-built-records".equals(contentPage.slug()))
        @template.studio.drawing-investigation()
    @endif
    <div class="article-layout studio-wrap">
        <aside class="article-sidebar"><button class="article-toc-toggle" type="button" aria-expanded="true" aria-controls="article-toc" hidden>In this guide <span aria-hidden="true">+</span></button><nav id="article-toc" aria-label="In this guide"><p class="studio-overline">In this guide</p><a href="#topic-overview">01 &nbsp; The starting point</a><a href="#topic-steps">02 &nbsp; Your next steps</a><a href="#topic-checklist">03 &nbsp; What to prepare</a><a href="#topic-questions">04 &nbsp; Common questions</a><a href="#topic-local">05 &nbsp; State guidance</a><a href="#topic-sources">06 &nbsp; Sources & review</a></nav><a class="article-help" href="${com.example.septic.web.StudioGuideRoutes.route("/offer-prep-septic-file-check/", production)}">Need help with the file?<br><span>Ask SepticPath ↗</span></a></aside>
        <article class="article-body">
            <section id="topic-overview"><p class="studio-overline">The starting point</p><h2>${contentPage.title()}</h2><p class="article-lede">${contentPage.introCopy()}</p>
                @if(!contentQuickAnswer.isBlank() && !contentQuickAnswer.equals(contentPage.introCopy()))<div class="article-story"><p class="studio-overline">At a glance</p><p>${contentQuickAnswer}</p></div>@endif
                <div class="article-actions"><a href="${com.example.septic.web.StudioGuideRoutes.route("/septic-record-brief-example/", production)}">See how we investigate real files ↗</a><a href="${com.example.septic.web.StudioGuideRoutes.route("/official-septic-lookup-tools/", production)}">Find your records route ↗</a></div>
                @for(var paragraph : contentPage.deepDiveParagraphs())<p>${paragraph}</p>@endfor
                @if(recordTopic)<div class="article-actions"><a href="${com.example.septic.web.StudioGuideRoutes.route("/official-septic-lookup-tools/#find-route", production)}">Look up an address ↗</a><a href="${com.example.septic.web.StudioGuideRoutes.route("/states/", production)}">Browse state and county routes ↗</a><a href="${com.example.septic.web.StudioGuideRoutes.route("/official-septic-lookup-tools/#draft-request", production)}">Prepare a records request ↗</a></div>@endif
            </section>
            @if("septic-as-built-records".equals(contentPage.slug()))
            <details class="article-disclosure article-record-evidence"><summary>Read the original drawing and its evidence limits<span aria-hidden="true">+</span></summary>@template.tags.septicDrawingGuide()</details>
            @elseif("septic-permit-search-by-address".equals(contentPage.slug()))
            <details class="article-disclosure article-record-evidence"><summary>How an old address can change the property match<span aria-hidden="true">+</span></summary>@template.tags.recordLearningGuide(slug = contentPage.slug())</details>
            @endif
            <figure class="article-photo"><img src="/images/studio/${planningTopic ? "permit-file-spread-v1.webp" : permitTopic ? "research-desk-v1.webp" : "buyer-folio-v1.webp"}" alt="${planningTopic ? "Illustrative property file arranged for scope planning" : permitTopic ? "Illustrative research desk with property records arranged for review" : "Property research booklet arranged for a buyer’s review"}" width="1672" height="941" loading="lazy"><figcaption>${planningTopic ? "The recorded layout and approval trail can change which price belongs in the conversation." : permitTopic ? "Keep the application, approval and final record distinct." : "Records review and a current site inspection answer different questions."}</figcaption></figure>
            <section id="topic-steps"><p class="studio-overline">One decision at a time</p><h2>${recordTopic ? "Follow the file, step by step." : permitTopic ? "Put the steps in order." : "Compare the scope, then the price."}</h2><ol class="article-steps">@for(int i = 0; i < contentPage.decisionSteps().size(); i++)<li><span aria-hidden="true">0${i + 1}</span><p>${contentPage.decisionSteps().get(i)}</p></li>@endfor</ol>
                <h3>${recordTopic || permitTopic ? "Where the route can change" : "What can change the cost"}</h3><ul>@for(var item : contentPage.driverBullets())<li>${item}</li>@endfor</ul>
                <details class="article-disclosure"><summary>${recordTopic || permitTopic ? "Assumptions to check early" : "When a low estimate may not hold"}<span aria-hidden="true">+</span></summary><ul>@for(var item : contentPage.lowEndBreakers())<li>${item}</li>@endfor</ul></details>
                <a class="studio-text-link" href="${StudioGuideRoutes.preview(calculatorPath)}">Explore the next planning step <span aria-hidden="true">↗</span></a>
            </section>
            <section id="topic-checklist" class="article-checklist"><p class="studio-overline">A better first conversation</p><h2>${recordTopic || permitTopic ? "Before you contact the office." : "Before you ask for a quote."}</h2><ul>@for(var item : contentPage.quotePrepChecklist())<li>${item}</li>@endfor</ul><a class="studio-button" href="${com.example.septic.web.StudioGuideRoutes.route("/offer-prep-septic-file-check/", production)}">Ask about your records <span aria-hidden="true">↗</span></a></section>
            <section id="topic-questions"><p class="studio-overline">Common questions</p><h2>The details worth knowing.</h2>@for(var faq : contentPage.faqBlocks())<details class="article-disclosure"><summary>${faq.question()}<span aria-hidden="true">+</span></summary><p>${faq.answer()}</p></details>@endfor</section>
            <section id="topic-local"><p class="studio-overline">Start with the local details</p><h2>Find your state’s guidance.</h2>
                @if(contentWorkflowCoverage != null)<p>${contentWorkflowCoverage.summary()}</p><ul>@for(var item : contentWorkflowCoverage.bullets())<li>${item}</li>@endfor</ul>@endif
                @if(!contentOfficialFilePathRows.isEmpty())<details class="article-disclosure"><summary>Which official route comes first?<span aria-hidden="true">+</span></summary><dl>@for(var row : contentOfficialFilePathRows)<dt><strong>${row.label()}</strong></dt><dd>${row.value()}</dd>@endfor</dl></details>@endif
                <ul class="article-link-list" id="article-states">@for(var link : stateMoneyPageLinks)<li><a href="${StudioGuideRoutes.preview(link.path())}">${link.title()}<span aria-hidden="true">↗</span></a></li>@endfor</ul><button class="article-states-toggle" type="button" aria-expanded="false" aria-controls="article-states" hidden>Show all state guides ↓</button>
                <a class="studio-text-link" href="${com.example.septic.web.StudioGuideRoutes.route("/states/", production)}">Browse the records directory <span aria-hidden="true">↗</span></a>
            </section>
            <section id="topic-sources"><p class="studio-overline">Sources & review</p><h2>Follow the source.</h2><p>${editorialReviewedAgainst}</p><p class="studio-fine">Last editorial review: ${editorialLastReviewedAt}. Local requirements and service scope still need confirmation.</p>
                @for(var lane : contentEvidenceLanes)<details class="article-disclosure"><summary>${lane.stateName()} / Official sources<span aria-hidden="true">+</span></summary><p><a href="${StudioGuideRoutes.preview(lane.path())}">${lane.title()}</a></p><ul>@for(var source : lane.sources())<li><strong>${source.agencyName()}</strong><br><a href="${source.url()}" target="_blank" rel="noreferrer">${source.title()}</a><div class="studio-fine">@if(source.draftOrFinalStatus() != null && !source.draftOrFinalStatus().isBlank())<span>Status: ${source.draftOrFinalStatus()} · </span>@endif@if(source.effectiveDate() != null && !source.effectiveDate().isBlank())<span>Effective: ${source.effectiveDate()} · </span>@endif@if(source.trustLevel() != null && !source.trustLevel().isBlank())<span>Trust: ${source.trustLevel()} · </span>@endif@template.tags.sourceVerification(source = source)</div></li>@endfor</ul></details>@endfor
            </section>
            <section><p class="studio-overline">Continue your research</p><h2>The next useful read.</h2><ul class="article-link-list">@for(var link : internalLinks)<li><a href="${StudioGuideRoutes.preview(link.path())}">${link.title()}<span aria-hidden="true">↗</span></a></li>@endfor</ul></section>
            <details class="article-disclosure article-audience"><summary>Who this guide is for<span aria-hidden="true">+</span></summary><p>${contentPage.targetReader()}</p><ul>@for(var item : contentPage.fitBullets())<li>${item}</li>@endfor</ul></details>
        </article>
    </div>
    @if(!"septic-as-built-records".equals(contentPage.slug()))
        @template.studio.research-proof(production = production, study = featuredStudy)
    @endif
    <section class="studio-closing article-closing"><img src="/images/studio/oak-pasture-v1.webp" alt="" width="1672" height="941" loading="lazy"><div class="studio-wrap"><p class="studio-overline">Start with the property</p><h2>A clearer file.<br>A better next question.</h2><p>Tell us what you know, and what you still need to find.</p><a class="studio-button" href="${com.example.septic.web.StudioGuideRoutes.route("/offer-prep-septic-file-check/", production)}">Ask SepticPath <span aria-hidden="true">↗</span></a></div></section>
</main>
<script src="${AssetPaths.versioned("/studio-article.js")}" defer></script>
`)
