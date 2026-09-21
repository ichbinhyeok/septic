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
@template.studio.shell(page = page, production = production, title = stateMoneyPage.title(), heroImage = problemHeroImage, pageClass = "studio-state-problem studio-state-wet", pageStyles = "/studio-state-problem.css", content = @`
<main id="main">
<section class="spg-hero spg-hero--wet"><img src="${problemHeroImage}" alt="" width="1672" height="941" fetchpriority="high"><div class="spg-hero__shade"></div><div class="studio-wrap spg-hero__inner"><p class="studio-overline">${state.stateName()} / Visible field symptoms</p><h1>The wet patch<br>is evidence—not scope.</h1><p>${stateMoneyPage.introCopy()}</p><div><a class="studio-button" href="#triage">Triage the signal <span aria-hidden="true">↓</span></a><a href="${com.example.septic.web.StudioGuideRoutes.preview(calculatorPath)}">Model the field risk ↗</a></div><small>Independent planning guidance · Reviewed ${editorialLastReviewedAt}</small></div></section>

<section class="spg-opening studio-wrap"><div><p class="studio-overline">Treat the symptom seriously</p><h2>Visible water does not<br>name the failure.</h2></div><div><p>${stateMoneyPage.uniqueAngle()}</p><p>${state.maintenanceInspectionNote()}</p></div></section>

<section class="spg-triage" id="triage"><div class="studio-wrap"><header><p class="studio-overline">A calm first read</p><h2>Observe. Protect.<br>Then diagnose.</h2></header><div class="spg-triage__steps"><article><span>01 / Observe</span><h3>Document where and when</h3><p>Note rainfall, odor, color, ponding, slow drains, backups, and whether the wet area aligns with the recorded field.</p></article><article><span>02 / Protect</span><h3>Reduce exposure and loading</h3><p>Keep people, pets, and vehicles away from suspected sewage or saturated field areas while the condition is assessed.</p></article><article><span>03 / Diagnose</span><h3>Separate the possible causes</h3><p>A provider may need to distinguish field saturation from plumbing, tank, distribution, surface-water, or grading problems.</p></article></div></div></section>

<section class="spg-media spg-media--wet"><figure><img src="/images/studio/morgan-garden-v1.webp" alt="Illustrative landscaped yard used to discuss visible septic field symptoms" width="1672" height="941" loading="lazy"><figcaption>Illustrative setting—not a named ${state.stateName()} property.</figcaption></figure><div><p class="studio-overline">What changes the response</p><h2>${stateMoneyPage.riskHeading(state.stateName())}</h2><h3>Do not assume a small repair when</h3><ul>@for(var item : stateMoneyPage.lowEndBreakers())<li>${item}</li>@endfor</ul><h3>Expect a broader field conversation when</h3><ul>@for(var item : stateMoneyPage.driverBullets())<li>${item}</li>@endfor</ul></div></section>

<section class="spg-actions studio-wrap"><div><p class="studio-overline">Prepare the service call</p><h2>${stateMoneyPage.actionChecklistHeading()}</h2><p>${stateMoneyPage.targetReader()}</p></div><ol>@for(int i=0;i<stateMoneyPage.decisionSteps().size();i++)<li><span>0${i+1}</span><p>${stateMoneyPage.decisionSteps().get(i)}</p></li>@endfor</ol></section>

@template.studio.research-proof(production = production, study = featuredStudy, directCase = true, imageOverride = "/images/studio/work-desk-v1.webp", imageNote = "Illustrative investigation workspace", imageWidth = 1672, imageHeight = 941)

<section class="spg-local spg-local--wet"><div class="studio-wrap"><header><p class="studio-overline">If the symptom becomes a file</p><h2>${workflowDecision != null ? workflowDecision.heading() : stateMoneyPage.localAuthorityHeading()}</h2><p>${workflowDecision != null ? workflowDecision.intro() : "Inspection, repair, complaint, or permit records can change what the visible symptom means for the next decision."}</p></header>@if(workflowDecision != null)<dl>@for(var row : workflowDecision.cards())<div><dt>${row.label()}</dt><dd>${row.value()}</dd></div>@endfor</dl>@endif@if(countyWorkflowSynthesis != null)@for(var signal : countyWorkflowSynthesis.signals())<details><summary>${signal.label()} <span>+</span></summary><p>${signal.summary()}</p><p>${signal.countyExamples()}</p><p><strong>First ask:</strong> ${signal.firstAsk()}</p></details>@endfor@endif@if(!countyRecordLinks.isEmpty())<details><summary>Browse ${countyRecordLinks.size()} published county routes <span>+</span></summary><ul>@for(var link : countyRecordLinks)<li><a href="${com.example.septic.web.StudioGuideRoutes.preview(link.path())}">${link.compactTitle()} ↗</a></li>@endfor</ul></details>@endif</div></section>

<section class="spg-sources"><div class="studio-wrap"><div><p class="studio-overline">Traceable context</p><h2>Official sources.<br>Symptom-specific limits.</h2></div><div><ul class="studio-sources">@for(var source : sources)<li><a href="${source.url()}" target="_blank" rel="noopener noreferrer">${source.title()} ↗</a><span>${source.agencyName()} · reviewed ${source.contentVerifiedAt()}</span></li>@endfor</ul>@if(!internalLinks.isEmpty())<details><summary>Continue the research <span>+</span></summary><ul>@for(var link : internalLinks)<li><a href="${com.example.septic.web.StudioGuideRoutes.preview(link.path())}">${link.title()} ↗</a></li>@endfor</ul></details>@endif</div></div></section>

<section class="spg-faq studio-wrap"><div><p class="studio-overline">Wet-yard questions</p><h2>Before anyone calls it<br>a drain-field failure.</h2></div><div>@for(var faq : stateMoneyPage.faqBlocks())<details><summary>${faq.question()} <span>+</span></summary><p>${faq.answer()}</p></details>@endfor</div></section>

<section class="spg-close spg-close--wet"><img src="/images/studio/checklist-case-house-v1.webp" alt="" width="1672" height="941" loading="lazy"><div class="studio-wrap"><p class="studio-overline">Move from symptom to evidence</p><h2>See the yard.<br>Read the file.</h2><p>Bring the address, photographs, timing, and any records already in hand.</p><a class="studio-button" href="/offer-prep-septic-file-check/?state=${state.stateCode()}">Ask SepticPath <span aria-hidden="true">↗</span></a></div></section>
</main>
`)
