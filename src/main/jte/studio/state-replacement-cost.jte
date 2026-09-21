@import com.example.septic.web.PageMeta
@import com.example.septic.data.model.StateMoneyPage
@import com.example.septic.data.model.StateProfile
@import com.example.septic.data.model.SourceRecord
@import com.example.septic.web.PageLink
@import com.example.septic.web.AssetPaths
@import com.example.septic.web.StatePlanningSnapshot
@import com.example.septic.web.StateCostScopeView
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
@param StateCostScopeView costScopeView = null
@param StatePlanningSnapshot planningSnapshot = null
@param com.example.septic.web.StudioCaseStudies.Study featuredStudy
@param String calculatorPath
@param String editorialLastReviewedAt
@param String replacementHeroImage
@param PageMeta page = null
@param boolean production = false
@template.studio.shell(page = page, production = production, title = stateMoneyPage.title(), heroImage = replacementHeroImage, mobileHeroImage = "/images/studio/guides-mobile-v1.webp", pageClass = "studio-state-replacement", pageStyles = "/studio-state-replacement.css", content = @`
<main id="main">
<section class="srp-hero"><picture><source media="(max-width:700px)" srcset="/images/studio/guides-mobile-v1.webp"><img src="${replacementHeroImage}" alt="" width="1672" height="941" fetchpriority="high"></picture><div class="srp-hero__veil"></div><div class="studio-wrap srp-hero__inner"><p class="studio-overline">${state.stateName()} / Replacement planning</p><h1>Replace the failure.<br>Not the assumptions.</h1><p>${stateMoneyPage.introCopy()}</p><div class="srp-hero__actions"><a class="studio-button" href="#replacement-scope">Build the scope <span aria-hidden="true">↓</span></a><a href="${com.example.septic.web.StudioGuideRoutes.preview(calculatorPath)}">Open the planning estimator ↗</a></div><small>Independent planning guidance · Reviewed ${editorialLastReviewedAt}</small></div></section>

<section class="srp-diagnosis studio-wrap" aria-labelledby="diagnosis-title"><div><p class="studio-overline">Before the replacement quote</p><h2 id="diagnosis-title">Confirm what failed.<br>Then price what changes.</h2></div><div><p>${stateMoneyPage.uniqueAngle()}</p><p>${state.maintenanceInspectionNote()}</p><a class="studio-text-link" href="#replacement-scope">Separate the cost layers <span aria-hidden="true">↓</span></a></div></section>

<section class="srp-plan" id="replacement-scope"><figure><img src="/images/studio/checklist-plan-v1.webp" alt="Illustrative site plan and property research notes" width="1672" height="941" loading="lazy"><figcaption>Illustrative planning material—no property identity.</figcaption></figure><div class="srp-plan__body"><p class="studio-overline">The replacement scope</p><h2>${costScopeView != null ? costScopeView.heading() : stateMoneyPage.riskHeading(state.stateName())}</h2>@if(costScopeView != null)<p>${costScopeView.intro()}</p><dl>@for(var row : costScopeView.cards())<div><dt>${row.label()}</dt><dd>${row.value()}</dd></div>@endfor</dl>@else<ul>@for(var item : stateMoneyPage.driverBullets())<li>${item}</li>@endfor</ul>@endif</div></section>

@if(planningSnapshot != null)
<section class="srp-range" aria-labelledby="range-title"><div class="studio-wrap"><div><p class="studio-overline">${state.stateCode()} planning anchors</p><h2 id="range-title">Use the midpoint<br>as a question.</h2><p>${planningSnapshot.comparisonNote()}</p></div><dl><div><dt>Replacement midpoint</dt><dd>${planningSnapshot.replacementMidpoint()}</dd></div><div><dt>New installation midpoint</dt><dd>${planningSnapshot.installMidpoint()}</dd></div><div><dt>Perc / soil work</dt><dd>${planningSnapshot.percRange()}</dd></div><div><dt>Routine pumping</dt><dd>${planningSnapshot.pumpingRange()}</dd></div></dl><p class="srp-range__note">Planning references only. The property, local permit path, design and contractor scope determine the actual price.</p><a class="studio-button studio-button--light" href="${com.example.septic.web.StudioGuideRoutes.preview(calculatorPath)}">Build the property range <span aria-hidden="true">↗</span></a></div></section>
@endif

<section class="srp-drivers studio-wrap" aria-labelledby="drivers-title"><div><p class="studio-overline">Where the low end breaks</p><h2 id="drivers-title">The quote widens<br>when the site does.</h2></div><div class="srp-driver-list"><div><span>01</span><h3>${stateMoneyPage.riskHeading(state.stateName())}</h3><ul>@for(var item : stateMoneyPage.lowEndBreakers())<li>${item}</li>@endfor</ul></div><div><span>02</span><h3>Scope and restoration</h3><ul>@for(var item : stateMoneyPage.driverBullets())<li>${item}</li>@endfor</ul></div></div></section>

@template.studio.research-proof(production = production, study = featuredStudy, directCase = true, imageOverride = "/images/studio/permit-file-spread-v1.webp", imageNote = "Illustrative permit and site-plan file", imageWidth = 1672, imageHeight = 941)

<section class="srp-file"><div class="srp-file__copy"><p class="studio-overline">Read before you excavate</p><h2>The old file can<br>change the new scope.</h2><p>An approved plan, repair history, system capacity, or recorded replacement area can answer questions that a fresh quote otherwise has to guess at.</p><ul>@for(var item : stateMoneyPage.quotePrepChecklist())<li>${item}</li>@endfor</ul><a class="studio-text-link" href="/offer-prep-septic-file-check/?state=${state.stateCode()}">Ask us to investigate the property <span aria-hidden="true">↗</span></a></div><figure><img src="/images/studio/file-dividers-v1.webp" alt="Illustrative permit, site plan, repair and correspondence file sections" width="1792" height="960" loading="lazy"></figure></section>

@if(workflowDecision != null || countyWorkflowSynthesis != null)
<section class="srp-local" id="local-route"><div class="studio-wrap"><div class="srp-local__heading"><p class="studio-overline">Where the state guide ends</p><h2>${workflowDecision != null ? workflowDecision.heading() : countyWorkflowSynthesis.heading()}</h2><p>${workflowDecision != null ? workflowDecision.intro() : countyWorkflowSynthesis.intro()}</p></div>@if(workflowDecision != null)<dl>@for(var row : workflowDecision.cards())<div><dt>${row.label()}</dt><dd>${row.value()}</dd></div>@endfor</dl>@endif@if(countyWorkflowSynthesis != null)@for(var signal : countyWorkflowSynthesis.signals())<details><summary>${signal.label()} <span aria-hidden="true">+</span></summary><p>${signal.summary()}</p><p>${signal.countyExamples()}</p><p><strong>First ask:</strong> ${signal.firstAsk()}</p></details>@endfor@endif@if(!countyRecordLinks.isEmpty())<details><summary>Browse ${countyRecordLinks.size()} published county routes <span aria-hidden="true">+</span></summary><ul>@for(var link : countyRecordLinks)<li><a href="${com.example.septic.web.StudioGuideRoutes.preview(link.path())}">${link.compactTitle()} ↗</a></li>@endfor</ul></details>@endif</div></section>
@endif

<section class="srp-ready studio-wrap"><div><p class="studio-overline">Before requesting bids</p><h2>${stateMoneyPage.actionChecklistHeading()}</h2><p>${stateMoneyPage.targetReader()}</p></div><div><ol>@for(int i=0;i<stateMoneyPage.decisionSteps().size();i++)<li><span>0${i+1}</span><p>${stateMoneyPage.decisionSteps().get(i)}</p></li>@endfor</ol><div class="srp-ready__actions"><a class="studio-button" href="${com.example.septic.web.StudioGuideRoutes.preview(calculatorPath)}">Estimate the scope <span aria-hidden="true">↗</span></a><a class="studio-text-link" href="/offer-prep-septic-file-check/?state=${state.stateCode()}">Ask about my property ↗</a></div></div></section>

<section class="srp-sources studio-wrap"><div><p class="studio-overline">Traceable context</p><h2>Official sources<br>and next routes.</h2></div><div><ul class="studio-sources">@for(var source : sources)<li><a href="${source.url()}" target="_blank" rel="noopener noreferrer">${source.title()} ↗</a><span>${source.agencyName()} · reviewed ${source.contentVerifiedAt()}</span></li>@endfor</ul>@if(!internalLinks.isEmpty())<details><summary>Continue the research <span aria-hidden="true">+</span></summary><ul>@for(var link : internalLinks)<li><a href="${com.example.septic.web.StudioGuideRoutes.preview(link.path())}">${link.title()} ↗</a></li>@endfor</ul></details>@endif</div></section>

<section class="srp-faq studio-wrap"><p class="studio-overline">Replacement questions</p><h2>Before you accept<br>the scope.</h2>@for(var faq : stateMoneyPage.faqBlocks())<details><summary>${faq.question()} <span aria-hidden="true">+</span></summary><p>${faq.answer()}</p></details>@endfor</section>

<section class="srp-closing"><img src="/images/studio/oak-pasture-v1.webp" alt="" width="1672" height="941" loading="lazy"><div class="studio-wrap"><p class="studio-overline">Move with evidence</p><h2>Replace with<br>a clearer plan.</h2><p>Bring the property, the records you have, and the decision ahead.</p><a class="studio-button" href="/offer-prep-septic-file-check/?state=${state.stateCode()}">Ask SepticPath <span aria-hidden="true">↗</span></a></div></section>
</main>
`)
