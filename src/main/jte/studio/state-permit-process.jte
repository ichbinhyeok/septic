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
@param String processHeroImage
@param PageMeta page = null
@param boolean production = false
@template.studio.shell(page = page, production = production, title = stateMoneyPage.title(), heroImage = processHeroImage, mobileHeroImage = "/images/studio/mobile-research-v1.webp", pageClass = "studio-state-process", pageStyles = "/studio-state-process.css", content = @`
<main id="main">
<section class="sp-hero"><picture><source media="(max-width:700px)" srcset="/images/studio/mobile-research-v1.webp"><img src="${processHeroImage}" alt="" width="1672" height="941" fetchpriority="high"></picture><div class="sp-hero__shade"></div><div class="studio-wrap sp-hero__inner"><p class="studio-overline">${state.stateName()} / Permit process</p><h1>From the site.<br>To the sign-off.</h1><p>${stateMoneyPage.introCopy()}</p><div><a class="studio-button" href="#permit-steps">See the approval path <span aria-hidden="true">↓</span></a><a href="${com.example.septic.web.StudioGuideRoutes.route("/offer-prep-septic-file-check/", production)}">Ask about my property ↗</a></div><small>Independent guidance · Official-source context · Reviewed ${editorialLastReviewedAt}</small></div></section>

<section class="sp-intro studio-wrap"><div><p class="studio-overline">Before the application</p><h2>Know who decides.<br>Know what comes first.</h2></div><div><p class="sp-intro__lede">${stateMoneyPage.uniqueAngle()}</p><p>${state.permitSummary()}</p><a class="studio-text-link" href="#official-sources">Trace the official sources <span aria-hidden="true">↓</span></a></div></section>

<section class="sp-desk"><figure><img src="/images/studio/permit-lookup-desk-v1.webp" alt="Illustrative permit research desk with plans and property notes" width="1817" height="866" loading="lazy"><figcaption>Illustrative planning materials—not a ${state.stateName()} permit file.</figcaption></figure><div><p class="studio-overline">The approving path</p><h2>${state.stateName()},<br>before you file.</h2><dl>@for(var row : stateOfficialFilePathRows)<div><dt>${row.label()}</dt><dd>${row.value()}</dd></div>@endfor</dl>@if(workflowDecision != null)<details><summary>${workflowDecision.heading()} <span aria-hidden="true">+</span></summary><p>${workflowDecision.intro()}</p><dl>@for(var row : workflowDecision.cards())<div><dt>${row.label()}</dt><dd>${row.value()}</dd></div>@endfor</dl></details>@endif</div></section>

<section class="sp-steps studio-wrap" id="permit-steps"><div class="sp-steps__heading"><p class="studio-overline">The working sequence</p><h2>Move in the<br>right order.</h2><p>A permit path can change by county, system, site and project scope. These state-specific steps keep the dependencies visible.</p></div><ol>@for(int i=0;i<stateMoneyPage.decisionSteps().size();i++)<li><span>0${i+1}</span><p>${stateMoneyPage.decisionSteps().get(i)}</p></li>@endfor</ol></section>

<section class="sp-file"><div><p class="studio-overline">The approval file</p><h2>Keep the paper trail<br>with the property.</h2><p>A complete path may include more than an application and a receipt.</p><ul>@for(var item : stateMoneyPage.quotePrepChecklist())<li>${item}</li>@endfor</ul></div><figure><img src="/images/studio/permit-file-spread-v1.webp" alt="Illustrative permit file, site plan and approval paperwork" width="1792" height="960" loading="lazy"><figcaption>Illustrative permit materials with no property identity.</figcaption></figure></section>

<section class="sp-holds"><div class="studio-wrap"><div><p class="studio-overline">Before cost or schedule</p><h2>${stateMoneyPage.riskHeading(state.stateName())}</h2></div><div class="sp-holds__lists"><div><h3>Pause when</h3><ul>@for(var item : stateMoneyPage.lowEndBreakers())<li>${item}</li>@endfor</ul></div><div><h3>Scope changes when</h3><ul>@for(var item : stateMoneyPage.driverBullets())<li>${item}</li>@endfor</ul></div></div><a class="studio-text-link" href="${com.example.septic.web.StudioGuideRoutes.preview(calculatorPath)}">Open the ${state.stateName()} planning estimator <span aria-hidden="true">↗</span></a></div></section>

@template.studio.research-proof(production = production, study = featuredStudy, directCase = true, imageOverride = "/images/studio/checklist-case-house-v1.webp", imageNote = "Illustrative setting", imageWidth = 1561, imageHeight = 1008)

@if(countyWorkflowSynthesis != null)<section class="sp-local" id="local-routes"><div class="studio-wrap"><div class="sp-local__heading"><p class="studio-overline">Where statewide guidance ends</p><h2>${countyWorkflowSynthesis.heading()}</h2><p>${countyWorkflowSynthesis.intro()}</p></div><div class="sp-local__facts">@for(var row : countyWorkflowSynthesis.structureHighlights())<div><span>${row.label()}</span><strong>${row.value()}</strong></div>@endfor</div><div class="sp-local__signals">@for(var signal : countyWorkflowSynthesis.signals())<details><summary>${signal.label()} <span aria-hidden="true">+</span></summary><p>${signal.summary()}</p><p>${signal.countyExamples()}</p><p>${signal.coverageNote()}</p><p><strong>First ask:</strong> ${signal.firstAsk()}</p></details>@endfor</div>@if(!countyRecordLinks.isEmpty())<details class="sp-county-routes"><summary>Browse ${countyRecordLinks.size()} published ${state.stateName()} county routes <span aria-hidden="true">+</span></summary><ul>@for(var link : countyRecordLinks)<li><a href="${com.example.septic.web.StudioGuideRoutes.preview(link.path())}">${link.compactTitle()} <span aria-hidden="true">↗</span></a></li>@endfor</ul></details>@endif</div></section>@endif

<section class="sp-ready studio-wrap"><div><p class="studio-overline">Before the next call</p><h2>${stateMoneyPage.actionChecklistHeading()}</h2><p>${stateMoneyPage.targetReader()}</p></div><div><ul>@for(var item : stateMoneyPage.fitBullets())<li>${item}</li>@endfor</ul><a class="studio-button" href="${com.example.septic.web.StudioGuideRoutes.route("/offer-prep-septic-file-check/", production)}">Ask SepticPath <span aria-hidden="true">↗</span></a></div></section>

<section class="sp-sources studio-wrap" id="official-sources"><div><p class="studio-overline">Traceable guidance</p><h2>Official sources.</h2></div><div><ul class="studio-sources">@for(var source : sources)<li><a href="${source.url()}" target="_blank" rel="noopener noreferrer">${source.title()} ↗</a><span>${source.agencyName()} · reviewed ${source.contentVerifiedAt()}</span></li>@endfor</ul>@if(!internalLinks.isEmpty())<details class="sp-related"><summary>Continue the research <span aria-hidden="true">+</span></summary><ul>@for(var link : internalLinks)<li><a href="${com.example.septic.web.StudioGuideRoutes.preview(link.path())}">${link.title()} <span aria-hidden="true">↗</span></a></li>@endfor</ul></details>@endif</div></section>

<section class="sp-faq studio-wrap"><p class="studio-overline">Permit questions</p><h2>Before work begins.</h2>@for(var faq : stateMoneyPage.faqBlocks())<details><summary>${faq.question()} <span aria-hidden="true">+</span></summary><p>${faq.answer()}</p></details>@endfor</section>

<section class="sp-closing"><img src="/images/studio/checklist-closing-house-v1.webp" alt="" width="1774" height="887" loading="lazy"><div class="studio-wrap"><p class="studio-overline">A clearer approval path</p><h2>Know the next gate.<br>Before you spend.</h2><p>Bring the property, the project, and the uncertainty.</p><a class="studio-button" href="${com.example.septic.web.StudioGuideRoutes.route("/offer-prep-septic-file-check/", production)}">Investigate my property <span aria-hidden="true">↗</span></a></div></section>
</main>
`)
