@import com.example.septic.web.PageMeta
@import com.example.septic.data.model.StateProfile
@import com.example.septic.data.model.CountyRecordsPage
@import com.example.septic.data.model.SourceRecord
@import com.example.septic.data.model.StateMoneyPage
@import com.example.septic.web.AssetPaths
@import com.example.septic.web.StatePlanningSnapshot
@import com.example.septic.web.StateRuleFactView
@param StateProfile state
@param com.example.septic.web.StudioCaseStudies.Study featuredStudy
@param java.util.List<CountyRecordsPage> counties
@param java.util.List<SourceRecord> sources
@param java.util.List<SourceRecord> localAuthoritySources = java.util.List.of()
@param java.util.List<SourceRecord> recordsLookupSources = java.util.List.of()
@param java.util.List<StateMoneyPage> featuredStateMoneyPages = java.util.List.of()
@param java.util.List<StateRuleFactView> stateRuleFacts = java.util.List.of()
@param String guideHeading = "State septic cost guide"
@param String stateQuickAnswer = ""
@param String calculatorCtaLabel = "Build my planning range"
@param String calculatorCtaNote = "A planning estimate is not a contractor bid."
@param StatePlanningSnapshot planningSnapshot = null
@param String editorialLastReviewedAt = ""
@param String costHeroImage
@param PageMeta page = null
@param boolean production = false
@template.studio.shell(page = page, production = production, title = guideHeading, heroImage = costHeroImage, mobileHeroImage = "/images/studio/guides-mobile-v1.webp", pageClass = "studio-state-cost", pageStyles = "/studio-state-cost.css", content = @`
<main id="main">
    <section class="state-cost-hero" aria-labelledby="state-cost-title">
        <img src="${costHeroImage}" alt="" width="1672" height="941" fetchpriority="high">
        <div class="state-cost-hero__wash"></div>
        <div class="studio-wrap state-cost-hero__inner">
            <div class="state-cost-hero__copy">
                <p class="studio-overline">${state.stateName()} / Cost &amp; project planning</p>
                <h1 id="state-cost-title">Price the scope.<br>Not just the tank.</h1>
                <p>${stateQuickAnswer}</p>
                <div class="state-cost-hero__actions">
                    <a class="studio-button" href="${com.example.septic.web.StudioGuideRoutes.route("/septic-system-cost-calculator/?state=" + state.stateCode(), production)}">${calculatorCtaLabel} <span aria-hidden="true">↗</span></a>
                    <a class="studio-text-link" href="#cost-scope">See what moves the range <span aria-hidden="true">↓</span></a>
                </div>
            </div>
            @if (planningSnapshot != null)
            <aside class="state-cost-ticket" aria-label="${state.stateName()} planning snapshot">
                <div><span>New installation</span><strong>${planningSnapshot.installMidpoint()}</strong></div>
                <div><span>Replacement</span><strong>${planningSnapshot.replacementMidpoint()}</strong></div>
                <p>Planning midpoints, before the parcel and local permit path are known.</p>
            </aside>
            @endif
        </div>
    </section>

    @template.studio.research-proof(production = production, study = featuredStudy)

    <section class="state-cost-intro" id="cost-scope" aria-labelledby="cost-scope-title">
        <div class="studio-wrap state-cost-intro__grid">
            <div><p class="studio-overline">The number needs a scope</p><h2 id="cost-scope-title">Three layers shape<br>the real project.</h2></div>
            <p class="state-cost-intro__lead">A useful estimate separates the physical site, the system being designed, and the local approval path. That is where a generic national average stops being useful.</p>
        </div>
        <div class="studio-wrap state-cost-layers">
            <article><span>01</span><h3>The parcel</h3><p>Soil, slope, groundwater, access, setbacks, and the available replacement area can change the system class and field work.</p></article>
            <article><span>02</span><h3>The system</h3><p>Bedroom design flow, tank capacity, treatment type, pumping, excavation, and restoration define what is actually being priced.</p></article>
            <article><span>03</span><h3>The approval</h3><p>Begin with this route: ${state.whoToCallFirst()} Application, evaluation, design, inspection, and recording may be separate cost layers.</p></article>
        </div>
    </section>

    <section class="state-cost-visual" aria-labelledby="state-cost-research-title">
        <figure><img src="/images/studio/research-desk-v1.webp" alt="Research notes, a property photograph, and an illustrative site drawing arranged on a desk" width="1672" height="941" loading="lazy"></figure>
        <div class="state-cost-visual__copy">
            <p class="studio-overline">Evidence before assumptions</p>
            <h2 id="state-cost-research-title">The existing file can change what you price next.</h2>
            <p>Records may reveal an approved layout, prior repair, design flow, or a missing approval. We trace the available file and explain what it establishes before you pay for work that may duplicate it.</p>
            <a class="studio-text-link" href="${com.example.septic.web.StudioGuideRoutes.route("/septic-record-brief-example/" + featuredStudy.slug() + "/", production)}">See how we investigate a file <span aria-hidden="true">↗</span></a>
        </div>
    </section>

    @if (planningSnapshot != null)
    <section class="state-cost-ledger" aria-labelledby="planning-title">
        <div class="studio-wrap">
            <div class="state-cost-ledger__heading"><p class="studio-overline">${state.stateCode()} planning snapshot</p><h2 id="planning-title">A starting band.<br>Then localize it.</h2><p>${planningSnapshot.comparisonNote()}</p></div>
            <dl>
                <div><dt>New installation midpoint</dt><dd>${planningSnapshot.installMidpoint()}</dd></div>
                <div><dt>Replacement midpoint</dt><dd>${planningSnapshot.replacementMidpoint()}</dd></div>
                <div><dt>Perc / soil work</dt><dd>${planningSnapshot.percRange()}</dd></div>
                <div><dt>Routine pumping</dt><dd>${planningSnapshot.pumpingRange()}</dd></div>
            </dl>
            <p class="state-cost-ledger__note">These are conservative planning references, not official fee schedules or live contractor quotes. ${calculatorCtaNote}</p>
            <a class="studio-button studio-button--light" href="${com.example.septic.web.StudioGuideRoutes.route("/septic-system-cost-calculator/?state=" + state.stateCode(), production)}">Build the property range <span aria-hidden="true">↗</span></a>
        </div>
    </section>
    @endif

    <section class="state-cost-local" id="official-route" aria-labelledby="local-rules-title">
        <div class="studio-wrap state-cost-local__grid">
            <div><p class="studio-overline">Official context</p><h2 id="local-rules-title">What ${state.stateName()} establishes.</h2><p>${state.ruleSummaryPlainEnglish()}</p></div>
            <div class="state-cost-local__facts">
                <div><span>Lead agency</span><strong>${state.agencyName()}</strong></div>
                <div><span>Authority pattern</span><strong>${state.ruleTypeLabel()}</strong></div>
                <div><span>Start here</span><strong>${state.whoToCallFirst()}</strong></div>
                <div><span>Research reviewed</span><strong>${editorialLastReviewedAt}</strong></div>
            </div>
        </div>
        <div class="studio-wrap state-cost-local__details">
            <details open><summary>Permit and local-rule notes <span aria-hidden="true">+</span></summary><div><p>${state.permitSummary()}</p><p>${state.localOverrideNote()}</p></div></details>
            @if (!stateRuleFacts.isEmpty())
            <details><summary>Verified rule facts <span aria-hidden="true">+</span></summary><div class="state-cost-rule-list">@for (var fact : stateRuleFacts)<p><strong>${fact.label()}</strong>${fact.renderedValue()}</p>@endfor</div></details>
            @endif
            <details><summary>Official sources <span aria-hidden="true">+</span></summary><div><ul class="state-cost-sources">@for (var source : sources)<li><a href="${source.url()}" target="_blank" rel="noopener noreferrer">${source.title()} <span aria-hidden="true">↗</span></a><small>${source.agencyName()} · reviewed ${source.contentVerifiedAt()}</small></li>@endfor</ul></div></details>
        </div>
    </section>

    @if (!featuredStateMoneyPages.isEmpty())
    <section class="state-cost-guides" aria-labelledby="state-guides-title">
        <div class="studio-wrap">
            <div class="state-cost-guides__heading"><div><p class="studio-overline">Go one layer deeper</p><h2 id="state-guides-title">${state.stateName()} project guides.</h2></div><p>Use the guide that matches the decision in front of you.</p></div>
            <div class="state-cost-guide-grid">@for (var guide : featuredStateMoneyPages)<a href="${com.example.septic.web.StudioGuideRoutes.preview(guide.path(state.slug()))}"><span>State planning guide</span><strong>${guide.title()}</strong><em>Open guide ↗</em></a>@endfor</div>
        </div>
    </section>
    @endif

    <section class="state-cost-counties" id="county-routes" aria-labelledby="county-title">
        <div class="studio-wrap">
            <div class="state-cost-counties__heading"><div><p class="studio-overline">Localize the research</p><h2 id="county-title">Find your county route.</h2></div>@if (!counties.isEmpty())<label>County name<input type="search" placeholder="Search ${state.stateName()} counties" data-studio-county-search aria-controls="studio-county-list"></label>@endif</div>
            @if (!counties.isEmpty())
            <ul id="studio-county-list">@for (var county : counties)<li data-studio-county="${county.countyName()}"><a href="${com.example.septic.web.StudioGuideRoutes.route("/septic-records-checklist/" + state.slug() + "/" + county.countySlug() + "/", production)}"><span>${county.countyName()}</span><span aria-hidden="true">↗</span></a></li>@endfor</ul>
            <p class="studio-fine" data-studio-county-status aria-live="polite">${counties.size()} published county routes. A listed route does not guarantee that a property file exists.</p>
            @else
            <p>We do not yet have a published county guide here. Start with the property and we can trace the responsible route.</p>
            @endif
        </div>
    </section>

    <section class="state-cost-closing" aria-labelledby="cost-closing-title">
        <img src="/images/brand/septicpath-lake-property-v1.jpg" alt="" width="1536" height="1024" loading="lazy">
        <div class="studio-wrap"><p class="studio-overline">A clearer next step</p><h2 id="cost-closing-title">Do not price<br>the wrong scope.</h2><p>Tell us what you know about the property. We can help identify the file, the local route, and the unanswered questions.</p><a class="studio-button" href="/offer-prep-septic-file-check/?state=${state.stateCode()}">Ask SepticPath <span aria-hidden="true">↗</span></a></div>
    </section>
</main>
<script src="${AssetPaths.versioned("/studio.js")}" defer></script>
`)
