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
@param String inspectionHeroImage
@param PageMeta page = null
@param boolean production = false
@template.studio.shell(page = page, production = production, title = stateMoneyPage.title(), heroImage = inspectionHeroImage, mobileHeroImage = "/images/studio/guides-mobile-v1.webp", pageClass = "studio-state-inspection", pageStyles = "/studio-state-inspection.css", content = @`
<main id="main">
<section class="si-hero"><picture><source media="(max-width:700px)" srcset="/images/studio/guides-mobile-v1.webp"><img src="${inspectionHeroImage}" alt="" width="1672" height="941" fetchpriority="high"></picture><div class="si-hero__shade"></div><div class="studio-wrap si-hero__inner"><p class="studio-overline">${state.stateName()} / Inspection planning</p><h1>Price the scope.<br>Not the uncertainty.</h1><p>${stateMoneyPage.introCopy()}</p><div><a class="studio-button" href="#inspection-scope">Understand the scope <span aria-hidden="true">↓</span></a><a href="${com.example.septic.web.StudioGuideRoutes.preview(calculatorPath)}">Open the estimator ↗</a></div><small>Independent planning guidance · Reviewed ${editorialLastReviewedAt}</small></div></section>

<section class="si-intro studio-wrap"><div><p class="studio-overline">Before the appointment</p><h2>The price follows<br>the question.</h2></div><div><p class="si-intro__lede">${stateMoneyPage.uniqueAngle()}</p><p>${state.maintenanceInspectionNote()}</p><a class="studio-text-link" href="#inspection-steps">See what to verify <span aria-hidden="true">↓</span></a></div></section>

<section class="si-scope" id="inspection-scope"><figure><img src="/images/studio/buyer-folio-v1.webp" alt="Illustrative property diligence folio and inspection notes" width="1672" height="941" loading="lazy"><figcaption>Illustrative diligence materials—not an inspection report.</figcaption></figure><div><p class="studio-overline">Define the engagement</p><h2>${state.stateName()},<br>before the quote.</h2><dl>@for(var row : stateOfficialFilePathRows)<div><dt>${row.label()}</dt><dd>${row.value()}</dd></div>@endfor</dl>@if(workflowDecision != null)<details><summary>${workflowDecision.heading()} <span aria-hidden="true">+</span></summary><p>${workflowDecision.intro()}</p><dl>@for(var row : workflowDecision.cards())<div><dt>${row.label()}</dt><dd>${row.value()}</dd></div>@endfor</dl></details>@endif</div></section>

<section class="si-steps studio-wrap" id="inspection-steps"><div><p class="studio-overline">A useful inspection plan</p><h2>Put the checks<br>in order.</h2></div><ol>@for(int i=0;i<stateMoneyPage.decisionSteps().size();i++)<li><span>0${i+1}</span><p>${stateMoneyPage.decisionSteps().get(i)}</p></li>@endfor</ol></section>

<section class="si-records"><div><p class="studio-overline">Before the field visit</p><h2>Read the file.<br>Then inspect the system.</h2><p>A historical record and a current inspection answer different questions. Together they make the visit more useful.</p><ul>@for(var item : stateMoneyPage.quotePrepChecklist())<li>${item}</li>@endfor</ul></div><figure><img src="/images/studio/file-dividers-v1.webp" alt="Illustrative permit history, site plan, repair record and correspondence file" width="1792" height="960" loading="lazy"><figcaption>Illustrative file categories with no property identity.</figcaption></figure></section>

<section class="si-drivers"><div class="studio-wrap"><div><p class="studio-overline">What moves the scope</p><h2>${stateMoneyPage.riskHeading(state.stateName())}</h2></div><div class="si-drivers__columns"><div><h3>Do not trust the low end when</h3><ul>@for(var item : stateMoneyPage.lowEndBreakers())<li>${item}</li>@endfor</ul></div><div><h3>Expect the quote to change when</h3><ul>@for(var item : stateMoneyPage.driverBullets())<li>${item}</li>@endfor</ul></div></div><a class="studio-button" href="${com.example.septic.web.StudioGuideRoutes.preview(calculatorPath)}">Plan the ${state.stateName()} range <span aria-hidden="true">↗</span></a></div></section>

@template.studio.research-proof(production = production, study = featuredStudy, directCase = true, imageOverride = "/images/studio/checklist-case-house-v1.webp", imageNote = "Illustrative setting", imageWidth = 1561, imageHeight = 1008)

@if(countyWorkflowSynthesis != null)<section class="si-local"><div class="studio-wrap"><div class="si-local__heading"><p class="studio-overline">Local variation</p><h2>${countyWorkflowSynthesis.heading()}</h2><p>${countyWorkflowSynthesis.intro()}</p></div><div class="si-local__facts">@for(var row : countyWorkflowSynthesis.structureHighlights())<div><span>${row.label()}</span><strong>${row.value()}</strong></div>@endfor</div>@for(var signal : countyWorkflowSynthesis.signals())<details><summary>${signal.label()} <span aria-hidden="true">+</span></summary><p>${signal.summary()}</p><p>${signal.countyExamples()}</p><p>${signal.coverageNote()}</p><p><strong>First ask:</strong> ${signal.firstAsk()}</p></details>@endfor@if(!countyRecordLinks.isEmpty())<details class="si-county-routes"><summary>Browse ${countyRecordLinks.size()} published county record routes <span aria-hidden="true">+</span></summary><ul>@for(var link : countyRecordLinks)<li><a href="${com.example.septic.web.StudioGuideRoutes.preview(link.path())}">${link.compactTitle()} <span aria-hidden="true">↗</span></a></li>@endfor</ul></details>@endif</div></section>@endif

<section class="si-ready studio-wrap"><div><p class="studio-overline">Before you book</p><h2>${stateMoneyPage.actionChecklistHeading()}</h2><p>${stateMoneyPage.targetReader()}</p></div><div><ul>@for(var item : stateMoneyPage.fitBullets())<li>${item}</li>@endfor</ul><div class="si-ready__actions"><a class="studio-button" href="${com.example.septic.web.StudioGuideRoutes.preview(calculatorPath)}">Estimate the scope <span aria-hidden="true">↗</span></a><a class="studio-text-link" href="${com.example.septic.web.StudioGuideRoutes.route("/offer-prep-septic-file-check/", production)}">Ask about my property ↗</a></div></div></section>

<section class="si-sources studio-wrap" id="official-sources"><div><p class="studio-overline">Traceable context</p><h2>Official sources.</h2></div><div><ul class="studio-sources">@for(var source : sources)<li><a href="${source.url()}" target="_blank" rel="noopener noreferrer">${source.title()} ↗</a><span>${source.agencyName()} · reviewed ${source.contentVerifiedAt()}</span></li>@endfor</ul>@if(!internalLinks.isEmpty())<details class="si-related"><summary>Continue the research <span aria-hidden="true">+</span></summary><ul>@for(var link : internalLinks)<li><a href="${com.example.septic.web.StudioGuideRoutes.preview(link.path())}">${link.title()} <span aria-hidden="true">↗</span></a></li>@endfor</ul></details>@endif</div></section>

<section class="si-faq studio-wrap"><p class="studio-overline">Inspection questions</p><h2>Before you rely<br>on the result.</h2>@for(var faq : stateMoneyPage.faqBlocks())<details><summary>${faq.question()} <span aria-hidden="true">+</span></summary><p>${faq.answer()}</p></details>@endfor</section>

<section class="si-closing"><img src="/images/studio/calculator-landscape-v2.webp" alt="" width="1792" height="960" loading="lazy"><div class="studio-wrap"><p class="studio-overline">Plan the next decision</p><h2>A useful inspection<br>starts with context.</h2><p>Bring the property, the records you have, and the decision ahead.</p><a class="studio-button" href="${com.example.septic.web.StudioGuideRoutes.route("/offer-prep-septic-file-check/", production)}">Ask SepticPath <span aria-hidden="true">↗</span></a></div></section>
</main>
`)
