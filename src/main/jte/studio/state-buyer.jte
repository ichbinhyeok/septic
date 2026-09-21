@import com.example.septic.web.PageMeta
@import com.example.septic.data.model.StateMoneyPage
@import com.example.septic.data.model.StateProfile
@import com.example.septic.data.model.SourceRecord
@import com.example.septic.web.PageLink
@import com.example.septic.web.AssetPaths
@param StateMoneyPage stateMoneyPage
@param StateProfile state
@param java.util.List<SourceRecord> sources
@param java.util.List<SourceRecord> localAuthoritySources
@param java.util.List<SourceRecord> recordsLookupSources
@param java.util.List<PageLink> countyRecordLinks
@param java.util.List<PageLink> internalLinks
@param java.util.List<com.example.septic.web.CountyWorkflowFieldView> stateOfficialFilePathRows
@param com.example.septic.web.StateCountyWorkflowSynthesisView countyWorkflowSynthesis = null
@param com.example.septic.web.StateWorkflowDecisionView workflowDecision = null
@param com.example.septic.web.StudioCaseStudies.Study featuredStudy
@param String calculatorPath
@param String editorialLastReviewedAt
@param PageMeta page = null
@param boolean production = false
@template.studio.shell(page = page, production = production, title = stateMoneyPage.title(), heroImage = "/images/studio/county-property-v1.webp", mobileHeroImage = "/images/studio/guides-mobile-v1.webp", pageClass = "studio-article", pageStyles = "/studio-article.css", content = @`
<main id="main">
<section class="article-hero"><picture><source media="(max-width:700px)" srcset="/images/studio/guides-mobile-v1.webp"><img src="/images/studio/county-property-v1.webp" alt="" width="1672" height="941" fetchpriority="high"></picture><div class="studio-wrap"><p class="studio-overline">${state.stateName()} / Buyer due diligence</p><h1>Before you buy.<br>Understand the file.</h1><p>Buying a house with a septic system in ${state.stateName()}.</p><a class="studio-button" href="#buyer-local">Start with the local questions <span aria-hidden="true">↓</span></a></div></section>
<nav class="article-breadcrumb studio-wrap" aria-label="Breadcrumb"><a href="${com.example.septic.web.StudioGuideRoutes.route("/buying-a-house-with-a-septic-system/", production)}">Buying a home</a><span aria-hidden="true">/</span><span>${state.stateName()}</span></nav>
<div class="article-layout studio-wrap"><aside class="article-sidebar"><button class="article-toc-toggle" type="button" aria-expanded="true" aria-controls="article-toc" hidden>In this guide <span aria-hidden="true">+</span></button><nav id="article-toc" aria-label="In this guide"><p class="studio-overline">The buyer's file</p><a href="#buyer-local">01 / Local questions</a><a href="#buyer-steps">02 / Next steps</a><a href="#buyer-checklist">03 / Before you close</a><a href="#buyer-counties">04 / County routes</a><a href="#buyer-questions">05 / Questions</a><a href="#buyer-sources">06 / Official sources</a></nav><a class="article-help" href="${com.example.septic.web.StudioGuideRoutes.route("/offer-prep-septic-file-check/", production)}">Have a property in mind?<span>Ask SepticPath ↗</span></a></aside>
<article class="article-body">
<section id="buyer-local"><p class="studio-overline">Reviewed ${editorialLastReviewedAt}</p><h2>${stateMoneyPage.title()}</h2><p class="article-lede">${stateMoneyPage.introCopy()}</p><p>${stateMoneyPage.uniqueAngle()}</p><div class="article-actions"><a href="${com.example.septic.web.StudioGuideRoutes.route("/septic-record-brief-example/", production)}">See how we investigate real properties ↗</a><a href="${com.example.septic.web.StudioGuideRoutes.route("/official-septic-lookup-tools/", production)}">Find your records route ↗</a></div>
@if(workflowDecision != null)<h3>${workflowDecision.heading()}</h3><p>${workflowDecision.intro()}</p><dl class="studio-facts">@for(var row : workflowDecision.cards())<div><dt>${row.label()}</dt><dd>${row.value()}</dd></div>@endfor</dl>@endif
</section>
<section id="buyer-steps"><p class="studio-overline">Put the questions in order</p><h2>Your next steps in ${state.stateName()}.</h2><ol class="article-steps">@for(int i=0;i<stateMoneyPage.decisionSteps().size();i++)<li><span aria-hidden="true">0${i+1}</span><p>${stateMoneyPage.decisionSteps().get(i)}</p></li>@endfor</ol><h3>${stateMoneyPage.riskHeading(state.stateName())}</h3><ul>@for(var item : stateMoneyPage.lowEndBreakers())<li>${item}</li>@endfor</ul><details class="article-disclosure"><summary>What affects the scope <span aria-hidden="true">+</span></summary><ul>@for(var item : stateMoneyPage.driverBullets())<li>${item}</li>@endfor</ul></details><a class="studio-text-link" href="${com.example.septic.web.StudioGuideRoutes.preview(calculatorPath)}">Open the local planning estimator ↗</a></section>
<figure class="article-photo article-photo--buyer"><img src="/images/studio/buyer-folio-v1.webp" alt="" width="1672" height="941" loading="lazy"><figcaption>Records describe documented history. Current condition requires a separate inspection.</figcaption></figure>
<section class="article-checklist" id="buyer-checklist"><p class="studio-overline">For the next conversation</p><h2>${stateMoneyPage.quoteChecklistHeading()}</h2><ul>@for(var item : stateMoneyPage.quotePrepChecklist())<li>${item}</li>@endfor</ul><a class="studio-button" href="${com.example.septic.web.StudioGuideRoutes.route("/offer-prep-septic-file-check/?mode=review", production)}">Ask about my file <span aria-hidden="true">↗</span></a></section>
<section id="buyer-counties"><p class="studio-overline">The local route</p><h2>Bring the question to the county.</h2><dl class="studio-facts">@for(var row : stateOfficialFilePathRows)<div><dt>${row.label()}</dt><dd>${row.value()}</dd></div>@endfor</dl>
@if(countyWorkflowSynthesis != null)<h3>${countyWorkflowSynthesis.heading()}</h3><p>${countyWorkflowSynthesis.intro()}</p>
@for(var signal : countyWorkflowSynthesis.signals())<details class="article-disclosure"><summary>${signal.label()} <span aria-hidden="true">+</span></summary><p>${signal.summary()}</p><p>${signal.countyExamples()}</p><p>${signal.coverageNote()}</p><p>${signal.firstAsk()}</p></details>@endfor
<dl class="studio-facts">@for(var row : countyWorkflowSynthesis.structureHighlights())<div><dt>${row.label()}</dt><dd>${row.value()}</dd></div>@endfor</dl>
<h3>${countyWorkflowSynthesis.firstArtifactsHeading()}</h3><ul>@for(var item : countyWorkflowSynthesis.firstArtifacts())<li>${item}</li>@endfor</ul><h3>${countyWorkflowSynthesis.countyDropHeading()}</h3><ul>@for(var item : countyWorkflowSynthesis.countyDropTriggers())<li>${item}</li>@endfor</ul><h3>${countyWorkflowSynthesis.holdQuoteHeading()}</h3><ul>@for(var item : countyWorkflowSynthesis.holdQuoteChecks())<li>${item}</li>@endfor</ul>@endif
<ul class="article-link-list">@for(var link : countyRecordLinks)<li><a href="${link.path()}">${link.title()} ↗</a></li>@endfor</ul><a class="studio-text-link" href="/septic-records-checklist/${state.slug()}/#county-routes">Explore ${state.stateName()} county routes ↗</a></section>
<section id="buyer-questions"><p class="studio-overline">Common questions</p><h2>Before the next decision.</h2>@for(var faq : stateMoneyPage.faqBlocks())<details class="article-disclosure"><summary>${faq.question()} <span aria-hidden="true">+</span></summary><p>${faq.answer()}</p></details>@endfor</section>
<section id="buyer-sources"><p class="studio-overline">Traceable guidance</p><h2>Official sources.</h2><ul class="studio-sources">@for(var source : sources)<li><a href="${source.url()}" target="_blank" rel="noopener noreferrer">${source.title()} ↗</a><span>${source.agencyName()} · reviewed ${source.contentVerifiedAt()}</span></li>@endfor</ul><h3>Records and local offices</h3><ul class="article-link-list">@for(var source : recordsLookupSources)<li><a href="${source.url()}" target="_blank" rel="noopener noreferrer">${source.title()} ↗</a></li>@endfor @for(var source : localAuthoritySources)<li><a href="${source.url()}" target="_blank" rel="noopener noreferrer">${source.title()} ↗</a></li>@endfor</ul></section>
<section><h2>Continue your research.</h2><ul class="article-link-list">@for(var link : internalLinks)<li><a href="${com.example.septic.web.StudioGuideRoutes.preview(link.path())}">${link.title()} ↗</a></li>@endfor</ul></section>
<details class="article-disclosure article-audience"><summary>Who this guide is for <span aria-hidden="true">+</span></summary><p>${stateMoneyPage.targetReader()}</p><ul>@for(var item : stateMoneyPage.fitBullets())<li>${item}</li>@endfor</ul></details>
</article></div>
@template.studio.research-proof(production = production, study = featuredStudy)
<section class="studio-closing article-closing"><img src="/images/studio/oak-pasture-v1.webp" alt="" width="1672" height="941" loading="lazy"><div class="studio-wrap"><p class="studio-overline">Your property's unanswered question</p><h2>Start with the address.<br>Leave with a clearer next step.</h2><a class="studio-button" href="${com.example.septic.web.StudioGuideRoutes.route("/offer-prep-septic-file-check/", production)}">Ask SepticPath <span aria-hidden="true">↗</span></a></div></section>
</main><script src="${AssetPaths.versioned("/studio-article.js")}" defer></script>
`)
