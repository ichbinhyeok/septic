@import com.example.septic.web.PageMeta
@import com.example.septic.data.model.StateMoneyPage
@import com.example.septic.data.model.StateProfile
@import com.example.septic.data.model.SourceRecord
@import com.example.septic.web.PageLink
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
@param com.example.septic.web.StudioCaseStudies.Study featuredStudy
@param String calculatorPath
@param String editorialLastReviewedAt
@param String drainFieldHeroImage
@param PageMeta page = null
@param boolean production = false
@template.studio.shell(page = page, production = production, title = stateMoneyPage.title(), heroImage = drainFieldHeroImage, mobileHeroImage = "/images/studio/guides-mobile-v1.webp", pageClass = "studio-state-field", pageStyles = "/studio-state-field.css", content = @`
<main id="main">
<section class="sdf-hero"><img src="${drainFieldHeroImage}" alt="" width="1672" height="941" fetchpriority="high"><div class="sdf-hero__veil"></div><div class="studio-wrap sdf-hero__inner"><p class="studio-overline">${state.stateName()} / Drain-field planning</p><h1>Recover the field.<br>Protect what comes next.</h1><p>${stateMoneyPage.introCopy()}</p><div><a class="studio-button" href="#field-scope">Map the real scope <span aria-hidden="true">↓</span></a><a href="${com.example.septic.web.StudioGuideRoutes.preview(calculatorPath)}">Open the field estimator ↗</a></div><small>Independent planning guidance · Reviewed ${editorialLastReviewedAt}</small></div></section>

<section class="sdf-diagnosis studio-wrap"><div><p class="studio-overline">The surface is only a clue</p><h2>Wet ground does not<br>price the repair.</h2></div><div><p>${stateMoneyPage.uniqueAngle()}</p><p>${state.maintenanceInspectionNote()}</p><a class="studio-text-link" href="#field-scope">See what defines the field scope <span aria-hidden="true">↓</span></a></div></section>

<section class="sdf-scope" id="field-scope"><figure><img src="/images/studio/county-property-v1.webp" alt="Illustrative rural property and open yard" width="1672" height="941" loading="lazy"><figcaption>Illustrative property setting—not a named ${state.stateName()} parcel.</figcaption></figure><div class="sdf-scope__copy"><p class="studio-overline">Below the ordinary yard</p><h2>${costScopeView != null ? costScopeView.heading() : stateMoneyPage.riskHeading(state.stateName())}</h2>@if(costScopeView != null)<p>${costScopeView.intro()}</p><dl>@for(var row : costScopeView.cards())<div><dt>${row.label()}</dt><dd>${row.value()}</dd></div>@endfor</dl>@else<ul>@for(var item : stateMoneyPage.driverBullets())<li>${item}</li>@endfor</ul>@endif</div></section>

<section class="sdf-layers"><div class="studio-wrap"><div><p class="studio-overline">The field is one system of decisions</p><h2>Four layers<br>of the quote.</h2></div><ol><li><span>01</span><div><h3>Confirm the failure</h3><p>Separate a field limitation from a tank, distribution, plumbing or surface-drainage problem.</p></div></li><li><span>02</span><div><h3>Find usable area</h3><p>Read the recorded layout, reserve area, setbacks and current site constraints before choosing a location.</p></div></li><li><span>03</span><div><h3>Define the field work</h3><p>Laterals, media, dosing, distribution, excavation and connection work determine what is actually being replaced.</p></div></li><li><span>04</span><div><h3>Restore the property</h3><p>Access, hauling, grading, landscaping and protection of the old area can sit outside the apparent field price.</p></div></li></ol></div></section>

<section class="sdf-plan"><div class="sdf-plan__copy"><p class="studio-overline">Before excavation</p><h2>The old drawing can<br>change the new field.</h2><p>An as-built, repair sketch, soil record or reserve-area note can establish where the approved system was intended to go—and what the next design still needs to prove.</p><ul>@for(var item : stateMoneyPage.quotePrepChecklist())<li>${item}</li>@endfor</ul><a class="studio-text-link" href="/offer-prep-septic-file-check/?state=${state.stateCode()}">Ask us to investigate the file ↗</a></div><figure><img src="/images/studio/checklist-plan-v1.webp" alt="Illustrative site plan with property research notes" width="1672" height="941" loading="lazy"><figcaption>Illustrative planning material with no property identity.</figcaption></figure></section>

<section class="sdf-risk studio-wrap"><div><p class="studio-overline">Where the low end disappears</p><h2>${stateMoneyPage.riskHeading(state.stateName())}</h2></div><div><h3>Pause before trusting a simple field price</h3><ul>@for(var item : stateMoneyPage.lowEndBreakers())<li>${item}</li>@endfor</ul><h3>Expect the scope to move when</h3><ul>@for(var item : stateMoneyPage.driverBullets())<li>${item}</li>@endfor</ul><a class="studio-button" href="${com.example.septic.web.StudioGuideRoutes.preview(calculatorPath)}">Estimate the field scope ↗</a></div></section>

@template.studio.research-proof(production = production, study = featuredStudy, directCase = true, imageOverride = "/images/studio/permit-file-spread-v1.webp", imageNote = "Illustrative permit and field-plan file", imageWidth = 1672, imageHeight = 941)

@if(workflowDecision != null || countyWorkflowSynthesis != null)
<section class="sdf-local"><div class="studio-wrap"><div class="sdf-local__heading"><p class="studio-overline">The local approval path</p><h2>${workflowDecision != null ? workflowDecision.heading() : countyWorkflowSynthesis.heading()}</h2><p>${workflowDecision != null ? workflowDecision.intro() : countyWorkflowSynthesis.intro()}</p></div>@if(workflowDecision != null)<dl>@for(var row : workflowDecision.cards())<div><dt>${row.label()}</dt><dd>${row.value()}</dd></div>@endfor</dl>@endif@if(countyWorkflowSynthesis != null)@for(var signal : countyWorkflowSynthesis.signals())<details><summary>${signal.label()} <span aria-hidden="true">+</span></summary><p>${signal.summary()}</p><p>${signal.countyExamples()}</p><p><strong>First ask:</strong> ${signal.firstAsk()}</p></details>@endfor@endif@if(!countyRecordLinks.isEmpty())<details><summary>Browse ${countyRecordLinks.size()} published county routes <span aria-hidden="true">+</span></summary><ul>@for(var link : countyRecordLinks)<li><a href="${com.example.septic.web.StudioGuideRoutes.preview(link.path())}">${link.compactTitle()} ↗</a></li>@endfor</ul></details>@endif</div></section>
@endif

<section class="sdf-steps studio-wrap"><div><p class="studio-overline">A better order of operations</p><h2>${stateMoneyPage.actionChecklistHeading()}</h2><p>${stateMoneyPage.targetReader()}</p></div><ol>@for(int i=0;i<stateMoneyPage.decisionSteps().size();i++)<li><span>0${i+1}</span><p>${stateMoneyPage.decisionSteps().get(i)}</p></li>@endfor</ol></section>

<section class="sdf-sources"><div class="studio-wrap"><div><p class="studio-overline">Traceable context</p><h2>Official sources.<br>Field-specific limits.</h2></div><div><ul class="studio-sources">@for(var source : sources)<li><a href="${source.url()}" target="_blank" rel="noopener noreferrer">${source.title()} ↗</a><span>${source.agencyName()} · reviewed ${source.contentVerifiedAt()}</span></li>@endfor</ul>@if(!internalLinks.isEmpty())<details><summary>Continue the research <span aria-hidden="true">+</span></summary><ul>@for(var link : internalLinks)<li><a href="${com.example.septic.web.StudioGuideRoutes.preview(link.path())}">${link.title()} ↗</a></li>@endfor</ul></details>@endif</div></div></section>

<section class="sdf-faq studio-wrap"><div><p class="studio-overline">Drain-field questions</p><h2>Before you accept<br>the quote.</h2></div><div>@for(var faq : stateMoneyPage.faqBlocks())<details><summary>${faq.question()} <span aria-hidden="true">+</span></summary><p>${faq.answer()}</p></details>@endfor</div></section>

<section class="sdf-closing"><img src="/images/studio/oak-pasture-v1.webp" alt="" width="1672" height="941" loading="lazy"><div class="studio-wrap"><p class="studio-overline">Start below the surface</p><h2>A better field begins<br>with a clearer file.</h2><p>Bring the address, the records you have, and the symptoms you are seeing.</p><a class="studio-button" href="/offer-prep-septic-file-check/?state=${state.stateCode()}">Ask SepticPath <span aria-hidden="true">↗</span></a></div></section>
</main>
`)
