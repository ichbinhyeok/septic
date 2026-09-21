@import com.example.septic.web.PageMeta
@import com.example.septic.data.model.StateMoneyPage
@import com.example.septic.data.model.StateProfile
@import com.example.septic.data.model.SourceRecord
@import com.example.septic.web.PageLink
@param StateMoneyPage stateMoneyPage
@param StateProfile state
@param java.util.List<SourceRecord> sources
@param java.util.List<PageLink> countyRecordLinks
@param java.util.List<PageLink> internalLinks
@param com.example.septic.web.StateCountyWorkflowSynthesisView countyWorkflowSynthesis = null
@param com.example.septic.web.StateWorkflowDecisionView workflowDecision = null
@param com.example.septic.web.StudioCaseStudies.Study featuredStudy
@param String calculatorPath
@param String editorialLastReviewedAt
@param String problemHeroImage
@param PageMeta page = null
@param boolean production = false
@template.studio.shell(page = page, production = production, title = stateMoneyPage.title(), heroImage = problemHeroImage, pageClass = "studio-state-problem studio-state-area", pageStyles = "/studio-state-problem.css", content = @`
<main id="main">
<section class="spg-hero spg-hero--area"><img src="${problemHeroImage}" alt="" width="1672" height="941" fetchpriority="high"><div class="spg-hero__shade"></div><div class="studio-wrap spg-hero__inner"><p class="studio-overline">${state.stateName()} / Replacement-area planning</p><h1>The open yard<br>is not empty space.</h1><p>${stateMoneyPage.introCopy()}</p><div><a class="studio-button" href="#map-the-area">Map the constraints <span aria-hidden="true">↓</span></a><a href="${com.example.septic.web.StudioGuideRoutes.preview(calculatorPath)}">Estimate field scope ↗</a></div><small>Independent planning guidance · Reviewed ${editorialLastReviewedAt}</small></div></section>

<section class="spg-opening studio-wrap"><div><p class="studio-overline">Protect the future footprint</p><h2>A reserve area<br>is a promise on paper.</h2></div><div><p>${stateMoneyPage.uniqueAngle()}</p><p>${state.siteEvalSummary()}</p></div></section>

<section class="spg-map" id="map-the-area"><figure><img src="/images/studio/checklist-plan-v1.webp" alt="Illustrative property plan used to reason about a replacement area" width="1672" height="941" loading="lazy"><figcaption>Illustrative plan—not a recorded ${state.stateName()} property file.</figcaption></figure><div><p class="studio-overline">What the drawing must reconcile</p><h2>Field. Setbacks.<br>Future use.</h2><dl><div><dt>Existing system</dt><dd>Locate the approved field, tank, connections, and any repair history that changes the usable footprint.</dd></div><div><dt>Protected area</dt><dd>Check whether a reserve or replacement area was recorded—and whether later site changes intrude on it.</dd></div><div><dt>Current parcel</dt><dd>Reconcile wells, structures, slopes, water, access, additions, and lot changes against today’s approval path.</dd></div></dl></div></section>

<section class="spg-area-risk"><div class="studio-wrap"><header><p class="studio-overline">Where the assumption breaks</p><h2>${stateMoneyPage.riskHeading(state.stateName())}</h2></header><div><h3>Do not call the yard available when</h3><ul>@for(var item : stateMoneyPage.lowEndBreakers())<li>${item}</li>@endfor</ul><h3>Expect redesign pressure when</h3><ul>@for(var item : stateMoneyPage.driverBullets())<li>${item}</li>@endfor</ul></div></div></section>

<section class="spg-actions studio-wrap"><div><p class="studio-overline">Before a new layout</p><h2>${stateMoneyPage.actionChecklistHeading()}</h2><p>${stateMoneyPage.targetReader()}</p></div><ol>@for(int i=0;i<stateMoneyPage.decisionSteps().size();i++)<li><span>0${i+1}</span><p>${stateMoneyPage.decisionSteps().get(i)}</p></li>@endfor</ol></section>

@template.studio.research-proof(production = production, study = featuredStudy, directCase = true, imageOverride = "/images/studio/permit-file-spread-v1.webp", imageNote = "Illustrative permit and plan file", imageWidth = 1672, imageHeight = 941)

<section class="spg-local spg-local--paper"><div class="studio-wrap"><header><p class="studio-overline">From drawing to local acceptance</p><h2>Where the replacement footprint becomes official.</h2><p>The recorded area only becomes useful when it matches the current parcel, the current use, and the local review path.</p></header>@if(workflowDecision != null)<dl>@for(var row : workflowDecision.cards())<div><dt>${row.label()}</dt><dd>${row.value()}</dd></div>@endfor</dl>@endif@if(countyWorkflowSynthesis != null)@for(var signal : countyWorkflowSynthesis.signals())<details><summary>${signal.label()} <span>+</span></summary><p>${signal.summary()}</p><p>${signal.countyExamples()}</p><p><strong>First ask:</strong> ${signal.firstAsk()}</p></details>@endfor@endif@if(!countyRecordLinks.isEmpty())<details><summary>Browse ${countyRecordLinks.size()} published county routes <span>+</span></summary><ul>@for(var link : countyRecordLinks)<li><a href="${com.example.septic.web.StudioGuideRoutes.preview(link.path())}">${link.compactTitle()} ↗</a></li>@endfor</ul></details>@endif</div></section>

<section class="spg-sources"><div class="studio-wrap"><div><p class="studio-overline">Traceable context</p><h2>Official sources.<br>Parcel-specific limits.</h2></div><div><ul class="studio-sources">@for(var source : sources)<li><a href="${source.url()}" target="_blank" rel="noopener noreferrer">${source.title()} ↗</a><span>${source.agencyName()} · reviewed ${source.contentVerifiedAt()}</span></li>@endfor</ul>@if(!internalLinks.isEmpty())<details><summary>Continue the research <span>+</span></summary><ul>@for(var link : internalLinks)<li><a href="${com.example.septic.web.StudioGuideRoutes.preview(link.path())}">${link.title()} ↗</a></li>@endfor</ul></details>@endif</div></div></section>

<section class="spg-faq studio-wrap"><div><p class="studio-overline">Replacement-area questions</p><h2>Before you build<br>over the answer.</h2></div><div>@for(var faq : stateMoneyPage.faqBlocks())<details><summary>${faq.question()} <span>+</span></summary><p>${faq.answer()}</p></details>@endfor</div></section>

<section class="spg-close spg-close--area"><img src="/images/studio/county-property-v1.webp" alt="" width="1672" height="941" loading="lazy"><div class="studio-wrap"><p class="studio-overline">Preserve the option</p><h2>Know which ground<br>must remain open.</h2><p>Bring the address, the old drawing, and any planned changes to the property.</p><a class="studio-button" href="/offer-prep-septic-file-check/?state=${state.stateCode()}">Ask SepticPath <span aria-hidden="true">↗</span></a></div></section>
</main>
`)
