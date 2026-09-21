@import com.example.septic.web.PageMeta
@import com.example.septic.data.model.StateMoneyPage
@import com.example.septic.data.model.StateProfile
@import com.example.septic.data.model.SourceRecord
@import com.example.septic.web.PageLink
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
@param String percHeroImage
@param PageMeta page = null
@param boolean production = false
@template.studio.shell(page = page, production = production, title = stateMoneyPage.title(), heroImage = percHeroImage, mobileHeroImage = "/images/studio/guides-mobile-v1.webp", pageClass = "studio-state-perc", pageStyles = "/studio-state-perc.css", content = @`
<main id="main">
<section class="spt-hero"><img src="${percHeroImage}" alt="" width="1672" height="941" fetchpriority="high"><div class="spt-hero__veil"></div><div class="studio-wrap spt-hero__inner"><p class="studio-overline">${state.stateName()} / Soil &amp; site planning</p><h1>Read the ground.<br>Then price the path.</h1><p>${stateMoneyPage.introCopy()}</p><div><a class="studio-button" href="#soil-path">Understand the site path <span aria-hidden="true">↓</span></a><a href="${com.example.septic.web.StudioGuideRoutes.preview(calculatorPath)}">Open the planning estimator ↗</a></div><small>Independent planning guidance · Reviewed ${editorialLastReviewedAt}</small></div></section>

<section class="spt-question studio-wrap"><div><p class="studio-overline">One site question at a time</p><h2>A perc result<br>is not a system design.</h2></div><div><p>${stateMoneyPage.uniqueAngle()}</p><p>${state.siteEvalSummary()}</p></div></section>

<section class="spt-answers" id="soil-path"><div class="studio-wrap"><div class="spt-answers__heading"><p class="studio-overline">What the work needs to establish</p><h2>Soil. Space.<br>Local acceptance.</h2></div><div class="spt-answer-rows"><article><span>01</span><div><h3>Read the limiting layer</h3><p>Soil conditions, seasonal water, slope and usable depth can decide whether a conventional path remains plausible.</p></div></article><article><span>02</span><div><h3>Fit the system to the parcel</h3><p>Bedrooms, design flow, setbacks, reserve area and available footprint turn a soil finding into a site-planning question.</p></div></article><article><span>03</span><div><h3>Confirm who accepts the work</h3><p>${state.whoToCallFirst()} The responsible office determines which professional, form and review sequence apply.</p></div></article></div></div></section>

<section class="spt-field"><figure><img src="/images/studio/oak-pasture-v1.webp" alt="Open rural parcel used as an illustrative site-planning setting" width="1672" height="941" loading="lazy"><figcaption>Illustrative setting—not a named ${state.stateName()} parcel.</figcaption></figure><div class="spt-field__copy"><p class="studio-overline">The cost follows the question</p><h2>${costScopeView != null ? costScopeView.heading() : stateMoneyPage.riskHeading(state.stateName())}</h2>@if(costScopeView != null)<p>${costScopeView.intro()}</p><dl>@for(var row : costScopeView.cards())<div><dt>${row.label()}</dt><dd>${row.value()}</dd></div>@endfor</dl>@else<ul>@for(var item : stateMoneyPage.driverBullets())<li>${item}</li>@endfor</ul>@endif</div></section>

@if(planningSnapshot != null)
<section class="spt-range"><div class="studio-wrap"><div><p class="studio-overline">${state.stateCode()} planning reference</p><h2>Keep the soil work<br>separate from the build.</h2><p>${planningSnapshot.comparisonNote()}</p></div><div class="spt-range__figures"><div><span>Perc / soil planning</span><strong>${planningSnapshot.percRange()}</strong></div><div><span>New installation midpoint</span><strong>${planningSnapshot.installMidpoint()}</strong></div><div><span>Replacement midpoint</span><strong>${planningSnapshot.replacementMidpoint()}</strong></div><p>Planning references—not an official fee schedule, a parcel-specific result or a contractor quote.</p><a class="studio-button" href="${com.example.septic.web.StudioGuideRoutes.preview(calculatorPath)}">Build the site range ↗</a></div></div></section>
@endif

<section class="spt-sequence studio-wrap"><div><p class="studio-overline">Before paying for field work</p><h2>Put the decisions<br>in the right order.</h2></div><ol>@for(int i=0;i<stateMoneyPage.decisionSteps().size();i++)<li><span>0${i+1}</span><p>${stateMoneyPage.decisionSteps().get(i)}</p></li>@endfor</ol></section>

<section class="spt-plan"><div class="spt-plan__copy"><p class="studio-overline">What can widen the scope</p><h2>${stateMoneyPage.riskHeading(state.stateName())}</h2><div><h3>Pause at the low end when</h3><ul>@for(var item : stateMoneyPage.lowEndBreakers())<li>${item}</li>@endfor</ul><h3>Expect a wider conversation when</h3><ul>@for(var item : stateMoneyPage.driverBullets())<li>${item}</li>@endfor</ul></div></div><figure><img src="/images/studio/checklist-plan-v1.webp" alt="Illustrative property plan and site research notes" width="1672" height="941" loading="lazy"><figcaption>Illustrative research material with no property identity.</figcaption></figure></section>

@template.studio.research-proof(production = production, study = featuredStudy, directCase = true, imageOverride = "/images/studio/research-desk-v1.webp", imageNote = "Illustrative research desk", imageWidth = 1672, imageHeight = 941)

@if(workflowDecision != null || countyWorkflowSynthesis != null)
<section class="spt-local"><div class="studio-wrap"><div class="spt-local__heading"><p class="studio-overline">Local acceptance matters</p><h2>${workflowDecision != null ? workflowDecision.heading() : countyWorkflowSynthesis.heading()}</h2><p>${workflowDecision != null ? workflowDecision.intro() : countyWorkflowSynthesis.intro()}</p></div>@if(workflowDecision != null)<dl>@for(var row : workflowDecision.cards())<div><dt>${row.label()}</dt><dd>${row.value()}</dd></div>@endfor</dl>@endif@if(countyWorkflowSynthesis != null)@for(var signal : countyWorkflowSynthesis.signals())<details><summary>${signal.label()} <span aria-hidden="true">+</span></summary><p>${signal.summary()}</p><p>${signal.countyExamples()}</p><p><strong>First ask:</strong> ${signal.firstAsk()}</p></details>@endfor@endif@if(!countyRecordLinks.isEmpty())<details><summary>Browse ${countyRecordLinks.size()} published county routes <span aria-hidden="true">+</span></summary><ul>@for(var link : countyRecordLinks)<li><a href="${com.example.septic.web.StudioGuideRoutes.preview(link.path())}">${link.compactTitle()} ↗</a></li>@endfor</ul></details>@endif</div></section>
@endif

<section class="spt-prepare studio-wrap"><div><p class="studio-overline">Prepare the first conversation</p><h2>${stateMoneyPage.actionChecklistHeading()}</h2><p>${stateMoneyPage.targetReader()}</p></div><div><ul>@for(var item : stateMoneyPage.quotePrepChecklist())<li>${item}</li>@endfor</ul><div class="spt-prepare__actions"><a class="studio-button" href="${com.example.septic.web.StudioGuideRoutes.preview(calculatorPath)}">Estimate the planning range ↗</a><a class="studio-text-link" href="/offer-prep-septic-file-check/?state=${state.stateCode()}">Ask about my property ↗</a></div></div></section>

<section class="spt-sources"><div class="studio-wrap"><div><p class="studio-overline">Traceable context</p><h2>Official sources.<br>Clear limits.</h2></div><div><ul class="studio-sources">@for(var source : sources)<li><a href="${source.url()}" target="_blank" rel="noopener noreferrer">${source.title()} ↗</a><span>${source.agencyName()} · reviewed ${source.contentVerifiedAt()}</span></li>@endfor</ul>@if(!internalLinks.isEmpty())<details><summary>Continue the research <span aria-hidden="true">+</span></summary><ul>@for(var link : internalLinks)<li><a href="${com.example.septic.web.StudioGuideRoutes.preview(link.path())}">${link.title()} ↗</a></li>@endfor</ul></details>@endif</div></div></section>

<section class="spt-faq studio-wrap"><div><p class="studio-overline">Site evaluation questions</p><h2>Before you rely<br>on the result.</h2></div><div>@for(var faq : stateMoneyPage.faqBlocks())<details><summary>${faq.question()} <span aria-hidden="true">+</span></summary><p>${faq.answer()}</p></details>@endfor</div></section>

<section class="spt-closing"><img src="/images/studio/county-property-v1.webp" alt="" width="1672" height="941" loading="lazy"><div class="studio-wrap"><p class="studio-overline">Start with the parcel</p><h2>Know what the ground<br>allows next.</h2><p>Bring the address, the records you have, and the decision ahead.</p><a class="studio-button" href="/offer-prep-septic-file-check/?state=${state.stateCode()}">Ask SepticPath <span aria-hidden="true">↗</span></a></div></section>
</main>
`)
