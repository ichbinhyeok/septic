@import com.example.septic.web.PageMeta
@import com.example.septic.data.model.StateMoneyPage
@import com.example.septic.data.model.StateProfile
@import com.example.septic.data.model.SourceRecord
@import com.example.septic.web.PageLink
@import com.example.septic.web.StateCostScopeView
@param StateMoneyPage stateMoneyPage
@param StateProfile state
@param java.util.List<SourceRecord> sources
@param java.util.List<PageLink> countyRecordLinks
@param java.util.List<PageLink> internalLinks
@param com.example.septic.web.StateCountyWorkflowSynthesisView countyWorkflowSynthesis = null
@param com.example.septic.web.StateWorkflowDecisionView workflowDecision = null
@param StateCostScopeView costScopeView = null
@param com.example.septic.web.StudioCaseStudies.Study featuredStudy
@param String calculatorPath
@param String editorialLastReviewedAt
@param String problemHeroImage
@param PageMeta page = null
@param boolean production = false
@template.studio.shell(page = page, production = production, title = stateMoneyPage.title(), heroImage = problemHeroImage, pageClass = "studio-state-problem studio-state-failed", pageStyles = "/studio-state-problem.css", content = @`
<main id="main">
<section class="spg-hero spg-hero--failed"><img src="${problemHeroImage}" alt="" width="1672" height="941" fetchpriority="high"><div class="spg-hero__shade"></div><div class="studio-wrap spg-hero__inner"><p class="studio-overline">${state.stateName()} / Failed site review</p><h1>The result says no.<br>The parcel may not.</h1><p>${stateMoneyPage.introCopy()}</p><div><a class="studio-button" href="#read-the-result">Read what failed <span aria-hidden="true">↓</span></a><a href="${com.example.septic.web.StudioGuideRoutes.preview(calculatorPath)}">Model the next path ↗</a></div><small>Independent planning guidance · Reviewed ${editorialLastReviewedAt}</small></div></section>

<section class="spg-opening studio-wrap" id="read-the-result"><div><p class="studio-overline">A result is not a verdict</p><h2>First identify<br>what said no.</h2></div><div><p>${stateMoneyPage.uniqueAngle()}</p><p>${state.siteEvalSummary()}</p></div></section>

<section class="spg-diagnostic spg-diagnostic--failed"><div class="studio-wrap"><header><p class="studio-overline">Read the failure in layers</p><h2>Ground. Footprint.<br>Review path.</h2></header><ol><li><span>01</span><div><h3>The limiting condition</h3><p>Separate slow soil, shallow usable depth, seasonal water, slope, or another site limitation from a generic “failed perc” label.</p></div></li><li><span>02</span><div><h3>The design consequence</h3><p>Ask whether the result changes field size, system class, reserve area, or the usable part of the parcel.</p></div></li><li><span>03</span><div><h3>The accepted next step</h3><p>${state.whoToCallFirst()} Confirm who can retest, redesign, review, or close the current file.</p></div></li></ol></div></section>

<section class="spg-media spg-media--plan"><figure><img src="/images/studio/checklist-plan-v1.webp" alt="Illustrative site plan and soil review notes" width="1672" height="941" loading="lazy"><figcaption>Illustrative planning material—not a named ${state.stateName()} parcel.</figcaption></figure><div><p class="studio-overline">Before another test</p><h2>${stateMoneyPage.riskHeading(state.stateName())}</h2><h3>Pause before assuming the low end</h3><ul>@for(var item : stateMoneyPage.lowEndBreakers())<li>${item}</li>@endfor</ul><h3>Expect the path to widen when</h3><ul>@for(var item : stateMoneyPage.driverBullets())<li>${item}</li>@endfor</ul></div></section>

<section class="spg-actions studio-wrap"><div><p class="studio-overline">The next conversation</p><h2>${stateMoneyPage.actionChecklistHeading()}</h2><p>${stateMoneyPage.targetReader()}</p></div><ol>@for(int i=0;i<stateMoneyPage.decisionSteps().size();i++)<li><span>0${i+1}</span><p>${stateMoneyPage.decisionSteps().get(i)}</p></li>@endfor</ol></section>

@template.studio.research-proof(production = production, study = featuredStudy, directCase = true, imageOverride = "/images/studio/research-desk-v1.webp", imageNote = "Illustrative research desk", imageWidth = 1672, imageHeight = 941)

<section class="spg-local"><div class="studio-wrap"><header><p class="studio-overline">The local acceptance path</p><h2>Who decides what comes after the failed result.</h2><p>A usable next step depends on the office, reviewer, and evidence that control the parcel—not on the shorthand written beside the result.</p></header>@if(workflowDecision != null)<dl>@for(var row : workflowDecision.cards())<div><dt>${row.label()}</dt><dd>${row.value()}</dd></div>@endfor</dl>@endif@if(countyWorkflowSynthesis != null)@for(var signal : countyWorkflowSynthesis.signals())<details><summary>${signal.label()} <span>+</span></summary><p>${signal.summary()}</p><p>${signal.countyExamples()}</p><p><strong>First ask:</strong> ${signal.firstAsk()}</p></details>@endfor@endif@if(!countyRecordLinks.isEmpty())<details><summary>Browse ${countyRecordLinks.size()} published county routes <span>+</span></summary><ul>@for(var link : countyRecordLinks)<li><a href="${com.example.septic.web.StudioGuideRoutes.preview(link.path())}">${link.compactTitle()} ↗</a></li>@endfor</ul></details>@endif</div></section>

<section class="spg-sources"><div class="studio-wrap"><div><p class="studio-overline">Trace the decision</p><h2>Official sources.<br>No invented certainty.</h2></div><div><ul class="studio-sources">@for(var source : sources)<li><a href="${source.url()}" target="_blank" rel="noopener noreferrer">${source.title()} ↗</a><span>${source.agencyName()} · reviewed ${source.contentVerifiedAt()}</span></li>@endfor</ul>@if(!internalLinks.isEmpty())<details><summary>Continue the research <span>+</span></summary><ul>@for(var link : internalLinks)<li><a href="${com.example.septic.web.StudioGuideRoutes.preview(link.path())}">${link.title()} ↗</a></li>@endfor</ul></details>@endif</div></div></section>

<section class="spg-faq studio-wrap"><div><p class="studio-overline">Failed-perc questions</p><h2>Before you abandon<br>the plan.</h2></div><div>@for(var faq : stateMoneyPage.faqBlocks())<details><summary>${faq.question()} <span>+</span></summary><p>${faq.answer()}</p></details>@endfor</div></section>

<section class="spg-close spg-close--failed"><img src="/images/studio/oak-pasture-v1.webp" alt="" width="1672" height="941" loading="lazy"><div class="studio-wrap"><p class="studio-overline">A clearer second move</p><h2>Do not price<br>the wrong problem.</h2><p>Bring the result, the parcel address, and the decision you need to make.</p><a class="studio-button" href="/offer-prep-septic-file-check/?state=${state.stateCode()}">Ask SepticPath <span aria-hidden="true">↗</span></a></div></section>
</main>
`)
