@import com.example.septic.data.model.SourceRecord
@import com.example.septic.data.model.StateMoneyPage
@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.PageMeta
@import com.example.septic.web.EditorialProfile
@import com.example.septic.web.StateMoneyPrimaryAction
@import com.example.septic.web.StateCountyWorkflowSynthesisView
@import com.example.septic.web.StateCostScopeView
@import com.example.septic.web.SearchIntentOpportunityView
@param PageMeta page
@param StateMoneyPage stateMoneyPage
@param StateProfile state
@param java.util.List<SourceRecord> sources
@param java.util.List<SourceRecord> localAuthoritySources = java.util.List.of()
@param java.util.List<SourceRecord> recordsLookupSources = java.util.List.of()
@param SourceRecord primaryLocalAuthoritySource = null
@param SourceRecord primaryRecordsLookupSource = null
@param StateMoneyPrimaryAction primaryAction
@param String calculatorPath
@param String quotePath
@param String guidePath
@param String calculatorCtaLabel = "Open the main calculator"
@param String calculatorCtaNote = "Use your state and project assumptions first, then verify locally."
@param com.example.septic.web.StatePlanningSnapshot planningSnapshot = null
@param boolean showQuoteCta = false
@param java.util.List<com.example.septic.web.PageLink> internalLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> featuredInternalLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> secondaryInternalLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> countyRecordLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> featuredCountyRecordLinks = java.util.List.of()
@param java.util.List<SearchIntentOpportunityView> searchIntentOpportunities = java.util.List.of()
@param StateCountyWorkflowSynthesisView countyWorkflowSynthesis = null
@param com.example.septic.web.StateWorkflowDecisionView workflowDecision = null
@param StateCostScopeView costScopeView = null
@param EditorialProfile editorialPreparedBy = null
@param EditorialProfile editorialReviewedBy = null
@param String editorialReviewedAgainst = ""
@param String editorialLastReviewedAt = ""
@param String editorialNote = ""

@template.layouts.app(page = page, content = @`
    <div class="state-money-page">
    <section class="section section--heroish state-money-hero-section">
        <div class="container state-hero">
            <div>
                <span class="eyebrow">${state.stateCode()} homeowner guide</span>
                <h1>${stateMoneyPage.title()}</h1>
                <div class="workflow-console" aria-label="Septic workflow triage console">
                    <div class="workflow-console__topbar">
                        <span class="workflow-console__status">Live triage</span>
                        <span>${state.stateCode()} / ${stateMoneyPage.contentSlug()}</span>
                    </div>
                    <div class="workflow-console__body">
                        <div class="workflow-console__readout">
                            <span class="eyebrow">Current verdict</span>
                            <h2>
                                @if ("septic-permit-process".equals(stateMoneyPage.contentSlug()))
                                    Find the permit desk before pricing the work.
                                @elseif ("septic-records-checklist".equals(stateMoneyPage.contentSlug()))
                                    Use the file trail before you trust the story.
                                @elseif ("buying-a-house-with-a-septic-system".equals(stateMoneyPage.contentSlug()))
                                    Resolve the buyer file before negotiating price.
                                @elseif ("septic-inspection-cost".equals(stateMoneyPage.contentSlug()))
                                    Pull inspection history before pricing the visit.
                                @elseif ("septic-pumping-cost".equals(stateMoneyPage.contentSlug()))
                                    Check maintenance history before pricing the pump-out.
                                @elseif ("perc-test-cost".equals(stateMoneyPage.contentSlug()) || "failed-perc-test-septic".equals(stateMoneyPage.contentSlug()))
                                    Confirm the site-review lane before trusting a perc number.
                                @elseif ("septic-replacement-cost".equals(stateMoneyPage.contentSlug()) || "drain-field-replacement-cost".equals(stateMoneyPage.contentSlug()) || "septic-replacement-area".equals(stateMoneyPage.contentSlug()) || "wet-yard-over-septic-drain-field".equals(stateMoneyPage.contentSlug()))
                                    Resolve the failure branch before trusting a replacement range.
                                @else
                                    Use this state route to narrow the next action.
                                @endif
                            </h2>
                            <div class="workflow-console__actions">
                                <a class="button button--primary" href="${primaryAction.path()}" data-track-click="nav" data-track-source-context="${primaryAction.sourceContext()}" data-track-target-type="${primaryAction.targetType()}">${primaryAction.buttonLabel()}</a>
                                @if (primaryAction.calculator() && showQuoteCta)
                                    <a class="button button--secondary" href="${quotePath}" data-track-click="nav" data-track-source-context="state_money_primary_quote" data-track-target-type="quote_form">Get matched with local septic pros</a>
                                @elseif (!"county_page_directory".equals(primaryAction.targetType()))
                                    <a class="button button--secondary" href="${calculatorPath}" data-track-click="nav" data-track-source-context="state_money_secondary_calculator" data-track-target-type="calculator">Run the estimate</a>
                                @endif
                            </div>
                        </div>
                        <div class="workflow-console__checks">
                            <div class="workflow-console__row">
                                <span class="workflow-console__index">01</span>
                                <span class="workflow-console__label">
                                    @if ("septic-pumping-cost".equals(stateMoneyPage.contentSlug()))
                                        Maintenance lane
                                    @elseif ("septic-permit-process".equals(stateMoneyPage.contentSlug()))
                                        Permit authority
                                    @elseif ("buying-a-house-with-a-septic-system".equals(stateMoneyPage.contentSlug()))
                                        Buyer file
                                    @elseif ("septic-records-checklist".equals(stateMoneyPage.contentSlug()))
                                        Record owner
                                    @elseif ("perc-test-cost".equals(stateMoneyPage.contentSlug()) || "failed-perc-test-septic".equals(stateMoneyPage.contentSlug()))
                                        Site review
                                    @else
                                        First branch
                                    @endif
                                </span>
                                <strong>${primaryAction.heading()}</strong>
                            </div>
                            <div class="workflow-console__row">
                                <span class="workflow-console__index">02</span>
                                <span class="workflow-console__label">Evidence to pull</span>
                                <strong>
                                    @if (("perc-test-cost".equals(stateMoneyPage.contentSlug()) || "failed-perc-test-septic".equals(stateMoneyPage.contentSlug()) || "septic-replacement-cost".equals(stateMoneyPage.contentSlug()) || "drain-field-replacement-cost".equals(stateMoneyPage.contentSlug()) || "septic-replacement-area".equals(stateMoneyPage.contentSlug()) || "wet-yard-over-septic-drain-field".equals(stateMoneyPage.contentSlug())) && primaryLocalAuthoritySource != null)
                                        ${primaryLocalAuthoritySource.title()}
                                    @elseif (countyWorkflowSynthesis != null && !countyWorkflowSynthesis.firstArtifacts().isEmpty())
                                        ${countyWorkflowSynthesis.firstArtifacts().get(0)}
                                    @elseif (state.recordsToRequest() != null && !state.recordsToRequest().isEmpty())
                                        ${state.recordsToRequest().get(0)}
                                    @elseif (primaryRecordsLookupSource != null)
                                        ${primaryRecordsLookupSource.title()}
                                    @elseif (primaryLocalAuthoritySource != null)
                                        ${primaryLocalAuthoritySource.title()}
                                    @else
                                        ${state.whoToCallFirst()}
                                    @endif
                                </strong>
                            </div>
                            <div class="workflow-console__row">
                                <span class="workflow-console__index">03</span>
                                <span class="workflow-console__label">Pricing gate</span>
                                <strong>
                                    @if (countyWorkflowSynthesis != null && !countyWorkflowSynthesis.holdQuoteChecks().isEmpty())
                                        ${countyWorkflowSynthesis.holdQuoteChecks().get(0)}
                                    @else
                                        Use the estimate after the local file path is clear.
                                    @endif
                                </strong>
                            </div>
                        </div>
                    </div>
                </div>
                @if (!featuredCountyRecordLinks.isEmpty())
                    <div class="hero-county-launchpad" aria-label="${state.stateName()} county record shortcuts">
                        <span class="hero-county-launchpad__label">Open county route</span>
                        @for (var link : featuredCountyRecordLinks.stream().limit(3).toList())
                            <a href="${link.path()}"
                               data-track-click="nav"
                               data-track-source-context="state_money_hero_county_launchpad"
                               data-track-target-type="county_records_page">${link.compactTitle()}</a>
                        @endfor
                        <a href="#county-pages">All county pages</a>
                    </div>
                @endif
                <div class="hero__actions">
                    <a class="button button--secondary" href="${guidePath}" data-track-click="nav" data-track-source-context="state_money_back_to_guide" data-track-target-type="state_guide">Open the ${state.stateName()} guide</a>
                </div>
                <p class="lede state-money-intro-copy">${stateMoneyPage.introCopy()}</p>
                @if (!featuredInternalLinks.isEmpty())
                    <div class="quick-link-strip" aria-label="Fast next steps">
                        <span class="quick-link-strip__label">Fast next steps</span>
                        @for (var link : featuredInternalLinks)
                            <a class="button button--secondary quick-link-strip__button"
                               href="${link.path()}"
                               data-track-click="nav"
                               data-track-source-context="state_money_featured_link"
                               data-track-target-type="related_internal">${link.title()}</a>
                        @endfor
                    </div>
                @endif
            </div>

            <article class="hero-sidecard">
                <span class="pill">State-specific guide</span>
                <strong>${state.agencyName()}</strong>
                <strong>${state.ruleType()}</strong>
                <dl class="review-meta">
                    <div>
                        <dt>Prepared by</dt>
                        <dd>
                            <span class="review-meta__name">${editorialPreparedBy.displayName()}</span>
                            <span>${editorialPreparedBy.roleTitle()}</span>
                            <span>${editorialPreparedBy.focusSummary()}</span>
                        </dd>
                    </div>
                    <div>
                        <dt>Reviewed by</dt>
                        <dd>
                            <span class="review-meta__name">${editorialReviewedBy.displayName()}</span>
                            <span>${editorialReviewedBy.roleTitle()}</span>
                            <span>${editorialReviewedBy.focusSummary()}</span>
                        </dd>
                    </div>
                    <div>
                        <dt>Reviewed against</dt>
                        <dd>${editorialReviewedAgainst}</dd>
                    </div>
                    <div>
                        <dt>Last reviewed</dt>
                        <dd>${editorialLastReviewedAt}</dd>
                    </div>
                </dl>
                <p>${editorialNote}</p>
            </article>
        </div>
    </section>

    <div data-sticky-trigger="state-money-after-hero" aria-hidden="true"></div>

    <section class="section section--tight">
        <div class="container">
            <div class="section-jump-strip" aria-label="Jump between sections">
                <span class="section-jump-strip__label">Jump between sections</span>
                <a href="#workflow">Workflow</a>
                <a href="#risk">Risk checks</a>
                @if (!countyRecordLinks.isEmpty())
                    <a href="#county-pages">County pages</a>
                @endif
                <a href="#sources">Sources</a>
                <a href="#faq">FAQ</a>
            </div>

            <section class="next-move-board" aria-label="Next septic workflow moves">
                <div class="next-move-board__header">
                    <span class="eyebrow">Next move board</span>
                    <h2>Do these in order before the page becomes a price page.</h2>
                </div>

                <div class="next-move-board__rows">
                <article class="next-move-row next-move-row--primary">
                    <span class="next-move-row__index">01</span>
                    <div class="next-move-row__body">
                        <span class="next-move-row__label">${primaryAction.eyebrow()}</span>
                        <h3>
                            @if ("septic-pumping-cost".equals(stateMoneyPage.contentSlug()))
                                Find the county maintenance record lane
                            @elseif ("septic-permit-process".equals(stateMoneyPage.contentSlug()))
                                Identify the county permit desk
                            @elseif ("buying-a-house-with-a-septic-system".equals(stateMoneyPage.contentSlug()))
                                Match the seller story to the file
                            @elseif ("septic-records-checklist".equals(stateMoneyPage.contentSlug()))
                                Find the office that owns the file
                            @elseif ("perc-test-cost".equals(stateMoneyPage.contentSlug()) || "failed-perc-test-septic".equals(stateMoneyPage.contentSlug()))
                                Confirm who reviews the site
                            @else
                                ${primaryAction.heading()}
                            @endif
                        </h3>
                        <p>${primaryAction.note()}</p>
                        @if (countyWorkflowSynthesis != null && !countyWorkflowSynthesis.structureHighlights().isEmpty())
                            <p class="next-move-row__signal"><strong>County-backed read:</strong> ${countyWorkflowSynthesis.structureHighlights().get(0).value()}</p>
                        @endif
                    </div>
                    <a class="button button--primary" href="${primaryAction.path()}" data-track-click="nav" data-track-source-context="${primaryAction.sourceContext()}" data-track-target-type="${primaryAction.targetType()}">${primaryAction.buttonLabel()}</a>
                </article>

                @if (primaryAction.calculator())
                    <article class="next-move-row">
                        <span class="next-move-row__index">02</span>
                        <div class="next-move-row__body">
                            <span class="next-move-row__label">Return to the broader state guide</span>
                            <h3>Open the ${state.stateName()} guide</h3>
                            <p>Use the broader guide when you still need the state-level rule style, local office path, and low-end risk before committing to this one intent lane.</p>
                            @if (countyWorkflowSynthesis != null && !countyWorkflowSynthesis.firstArtifacts().isEmpty())
                                <p class="next-move-row__signal"><strong>Pull first:</strong> ${countyWorkflowSynthesis.firstArtifacts().get(0)}</p>
                            @endif
                        </div>
                        <a class="button button--secondary" href="${guidePath}" data-track-click="nav" data-track-source-context="state_money_action_rail_guide" data-track-target-type="state_guide">Open the guide</a>
                    </article>
                @else
                    <article class="next-move-row">
                        <span class="next-move-row__index">02</span>
                        <div class="next-move-row__body">
                            <span class="next-move-row__label">Run the state estimate</span>
                            <h3>${calculatorCtaLabel}</h3>
                            <p>${calculatorCtaNote}</p>
                            @if (countyWorkflowSynthesis != null && !countyWorkflowSynthesis.holdQuoteChecks().isEmpty())
                                <p class="next-move-row__signal"><strong>Hold pricing when:</strong> ${countyWorkflowSynthesis.holdQuoteChecks().get(0)}</p>
                            @endif
                        </div>
                        <a class="button button--secondary" href="${calculatorPath}" data-track-click="nav" data-track-source-context="state_money_action_rail_calculator" data-track-target-type="calculator">Run the estimate</a>
                    </article>
                @endif

                <article class="next-move-row">
                    <span class="next-move-row__index">03</span>
                    @if (primaryRecordsLookupSource != null)
                        <div class="next-move-row__body">
                            <span class="next-move-row__label">Pull the file first</span>
                            <h3>Open records before you trust the price story</h3>
                            <p>Use the official records path when you still need the permit, as-built, inspection, or maintenance file before moving into quote mode.</p>
                            @if (countyWorkflowSynthesis != null && !countyWorkflowSynthesis.firstArtifacts().isEmpty())
                                <p class="next-move-row__signal"><strong>Start with:</strong> ${countyWorkflowSynthesis.firstArtifacts().get(0)}</p>
                            @endif
                        </div>
                        <a class="button button--secondary" href="${primaryRecordsLookupSource.url()}" target="_blank" rel="noreferrer">Open records lookup</a>
                    @elseif (primaryLocalAuthoritySource != null)
                        <div class="next-move-row__body">
                            <span class="next-move-row__label">Verify the next office</span>
                            <h3>Confirm the local authority before you schedule work</h3>
                            <p>Use the local office path when you still need the real permit desk, reviewing authority, or delegated county office before trusting the low end.</p>
                            @if (countyWorkflowSynthesis != null && !countyWorkflowSynthesis.structureHighlights().isEmpty())
                                <p class="next-move-row__signal"><strong>County-backed file pattern:</strong> ${countyWorkflowSynthesis.structureHighlights().get(0).value()}</p>
                            @endif
                        </div>
                        <a class="button button--secondary" href="${primaryLocalAuthoritySource.url()}" target="_blank" rel="noreferrer">Open local authority source</a>
                    @elseif (!featuredInternalLinks.isEmpty())
                        <div class="next-move-row__body">
                            <span class="next-move-row__label">Most likely next move</span>
                            <h3>${featuredInternalLinks.get(0).title()}</h3>
                            <p>${featuredInternalLinks.get(0).note()}</p>
                        </div>
                        <a class="button button--secondary" href="${featuredInternalLinks.get(0).path()}" data-track-click="nav" data-track-source-context="state_money_action_rail_internal" data-track-target-type="related_internal">Open next step</a>
                    @endif
                </article>
                </div>
            </section>

            @if (!searchIntentOpportunities.isEmpty())
                <section class="search-intent-board" aria-label="Common search intents this page answers">
                    <div class="search-intent-board__header">
                        <span class="eyebrow">Common records searches</span>
                        <h2>Use the Tennessee search phrase that matches the job.</h2>
                        <p>Each route below resolves a different records search into the same practical next step: identify the file owner, pull the permit trail, and then decide whether the record is strong enough to trust.</p>
                    </div>
                    <div class="search-intent-board__grid">
                        @for (var opportunity : searchIntentOpportunities)
                            <article class="search-intent-card" id="${opportunity.anchorId()}">
                                <span class="search-intent-card__label">${opportunity.eyebrow()}</span>
                                <h3>${opportunity.heading()}</h3>
                                <p>${opportunity.summary()}</p>
                                @if ("official_source".equals(opportunity.targetType()))
                                    <a class="button button--secondary"
                                       href="${opportunity.actionPath()}"
                                       target="_blank"
                                       rel="noreferrer"
                                       data-track-click="nav"
                                       data-track-source-context="gsc_intent_patch"
                                       data-track-target-type="${opportunity.targetType()}">${opportunity.actionLabel()}</a>
                                @else
                                    <a class="button button--secondary"
                                       href="${opportunity.actionPath()}"
                                       data-track-click="nav"
                                       data-track-source-context="gsc_intent_patch"
                                       data-track-target-type="${opportunity.targetType()}">${opportunity.actionLabel()}</a>
                                @endif
                            </article>
                        @endfor
                    </div>
                </section>
            @endif
        </div>
    </section>

    @if (workflowDecision != null)
        <section class="section section--tight">
            <div class="container">
                <details class="state-money-disclosure state-money-evidence-drawer">
                    <summary>
                        <span class="eyebrow">${workflowDecision.eyebrow()}</span>
                        <strong>${workflowDecision.heading()}</strong>
                        <span>${workflowDecision.intro()}</span>
                    </summary>
                    <div class="detail-grid">
                        @for (var card : workflowDecision.cards())
                            <article class="detail-card">
                                <h3>${card.label()}</h3>
                                <p>${card.value()}</p>
                            </article>
                        @endfor
                    </div>
                </details>
            </div>
        </section>
    @endif

    @if (costScopeView != null)
        <section class="section section--tight">
            <div class="container">
                <details class="state-money-disclosure state-money-evidence-drawer">
                    <summary>
                        <span class="eyebrow">${costScopeView.eyebrow()}</span>
                        <strong>${costScopeView.heading()}</strong>
                        <span>${costScopeView.intro()}</span>
                    </summary>
                    <div class="detail-grid">
                        @for (var card : costScopeView.cards())
                            <article class="detail-card">
                                <h3>${card.label()}</h3>
                                <p>${card.value()}</p>
                            </article>
                        @endfor

                        @if (!costScopeView.scopeWideners().isEmpty())
                            <article class="detail-card">
                                <h3>${costScopeView.scopeWidenersHeading()}</h3>
                                <ul class="detail-list">
                                    @for (var widener : costScopeView.scopeWideners())
                                        <li>${widener}</li>
                                    @endfor
                                </ul>
                            </article>
                        @endif

                        @if (!costScopeView.readinessChecks().isEmpty())
                            <article class="detail-card">
                                <h3>${costScopeView.readinessHeading()}</h3>
                                <ul class="detail-list">
                                    @for (var check : costScopeView.readinessChecks())
                                        <li>${check}</li>
                                    @endfor
                                </ul>
                            </article>
                        @endif
                    </div>
                </details>
            </div>
        </section>
    @endif

    @if (planningSnapshot != null || primaryLocalAuthoritySource != null || primaryRecordsLookupSource != null)
        <section class="section section--tight">
            <div class="container">
                <div class="cost-gate-panel">
                @if (planningSnapshot != null)
                    <article class="cost-gate-panel__range">
                        <div class="cost-gate-panel__header">
                            <span class="eyebrow">Planning cost snapshot</span>
                            <h2>Use these ranges only after the file path is clear.</h2>
                            <p>${planningSnapshot.comparisonNote()} These figures are planning-only ranges, not an official fee schedule.</p>
                        </div>
                        <div class="cost-gate-panel__metrics">
                            <article class="cost-gate-panel__metric">
                                <span>Install midpoint</span>
                                <strong>${planningSnapshot.installMidpoint()}</strong>
                            </article>
                            <article class="cost-gate-panel__metric">
                                <span>Replacement midpoint</span>
                                <strong>${planningSnapshot.replacementMidpoint()}</strong>
                            </article>
                            <article class="cost-gate-panel__metric">
                                <span>Perc planning range</span>
                                <strong>${planningSnapshot.percRange()}</strong>
                            </article>
                            <article class="cost-gate-panel__metric">
                                <span>Pumping planning range</span>
                                <strong>${planningSnapshot.pumpingRange()}</strong>
                            </article>
                        </div>
                    </article>
                @endif

                <div class="cost-gate-panel__sources">
                    @if (primaryLocalAuthoritySource != null)
                        <article class="cost-gate-panel__source">
                            <span class="cost-gate-panel__source-label">Authority gate</span>
                            <h3>${stateMoneyPage.localAuthorityHeading()}</h3>
                            <p>Use the local office first when you want to move from a planning page into an actual permit or records workflow.</p>
                            <a class="button button--secondary" href="${primaryLocalAuthoritySource.url()}" target="_blank" rel="noreferrer">Open local authority source</a>
                            <p class="cost-gate-panel__source-meta">${primaryLocalAuthoritySource.agencyName()} | ${primaryLocalAuthoritySource.title()}</p>
                        </article>
                    @endif

                    @if (primaryRecordsLookupSource != null)
                        <article class="cost-gate-panel__source">
                            <span class="cost-gate-panel__source-label">Record gate</span>
                            <h3>${stateMoneyPage.recordsLookupHeading()}</h3>
                            <p>Use the existing record trail to confirm whether this property still fits the low end before you move into quote mode.</p>
                            <a class="button button--secondary" href="${primaryRecordsLookupSource.url()}" target="_blank" rel="noreferrer">Open records lookup</a>
                            <p class="cost-gate-panel__source-meta">${primaryRecordsLookupSource.agencyName()} | ${primaryRecordsLookupSource.title()}</p>
                        </article>
                    @endif
                </div>
                </div>
            </div>
        </section>
    @endif

    <section class="section section--tight state-money-disclosure-section">
        <div class="container">
            <details class="state-money-disclosure">
                <summary>
                    <span class="eyebrow">State context</span>
                    <strong>Quick facts, fit, and workflow details</strong>
                    <span>Open when you need the full state context behind the answer panel.</span>
                </summary>

        <div class="detail-grid">
            <article class="detail-card detail-card--full">
                <h2>Quick facts</h2>
                <div class="facts-table-wrap">
                    <table class="facts-table">
                        <tbody>
                        <tr>
                            <th>Rule style</th>
                            <td>${state.ruleType()}</td>
                            <th>Override risk</th>
                            <td>${state.countyOverrideRisk()}</td>
                        </tr>
                        <tr>
                            <th>Last verified</th>
                            <td>${state.lastVerifiedAt()}</td>
                            <th>Official sources</th>
                            <td>${stateMoneyPage.officialSourceIds().size()}</td>
                        </tr>
                        <tr>
                            <th>Local verification links</th>
                            <td>${localAuthoritySources.size()}</td>
                            <th>Records links</th>
                            <td>${recordsLookupSources.size()}</td>
                        </tr>
                        <tr>
                            <th>Public sizing signal</th>
                            <td>
                                @if (state.minTankSizeGallons() != null)
                                    ${state.minTankSizeGallons()} gallon minimum anchor
                                @elseif (state.designFlowPerBedroomGpd() != null)
                                    ${state.designFlowPerBedroomGpd()} gallons per bedroom
                                @else
                                    Conservative fallback range
                                @endif
                            </td>
                            <th>Primary first call</th>
                            <td>${state.whoToCallFirst()}</td>
                        </tr>
                        @if (countyWorkflowSynthesis != null && !countyWorkflowSynthesis.firstArtifacts().isEmpty() && !countyWorkflowSynthesis.holdQuoteChecks().isEmpty())
                            <tr>
                                <th>County-backed first pull</th>
                                <td>${countyWorkflowSynthesis.firstArtifacts().get(0)}</td>
                                <th>Hold pricing when</th>
                                <td>${countyWorkflowSynthesis.holdQuoteChecks().get(0)}</td>
                            </tr>
                        @endif
                        </tbody>
                    </table>
                </div>
            </article>

            @if (state.localActionSteps() != null && !state.localActionSteps().isEmpty())
                <article class="detail-card">
                    <h2>${stateMoneyPage.actionChecklistHeading()}</h2>
                    <ol class="detail-list detail-list--numbered">
                        @for (var step : state.localActionSteps())
                            <li>${step}</li>
                        @endfor
                    </ol>
                </article>
            @endif

            @if ((stateMoneyPage.targetReader() != null && !stateMoneyPage.targetReader().isBlank())
                    || (stateMoneyPage.fitBullets() != null && !stateMoneyPage.fitBullets().isEmpty()))
                <article class="detail-card">
                    <h2>Who this page is for</h2>
                    @if (stateMoneyPage.targetReader() != null && !stateMoneyPage.targetReader().isBlank())
                        <p>${stateMoneyPage.targetReader()}</p>
                    @endif
                    @if (stateMoneyPage.fitBullets() != null && !stateMoneyPage.fitBullets().isEmpty())
                        <ul class="detail-list">
                            @for (var fitBullet : stateMoneyPage.fitBullets())
                                <li>${fitBullet}</li>
                            @endfor
                        </ul>
                    @endif
                </article>
            @endif

            <article class="detail-card detail-card--full">
                <h2>What changes this page in ${state.stateName()}</h2>
                <p>${stateMoneyPage.targetReader()} ${stateMoneyPage.uniqueAngle()}</p>
                @if (state.permitSummary() != null && !state.permitSummary().isBlank())
                    @if (state.whoToCallFirst() != null && !state.whoToCallFirst().isBlank())
                        <p>${state.permitSummary()} The first practical check is usually the office, file path, or reviewer identified in this state workflow: ${state.whoToCallFirst()}</p>
                    @else
                        <p>${state.permitSummary()} Use the local file and authority path below before you treat the lower planning range as real.</p>
                    @endif
                @endif
                @if (state.specialAreaNote() != null && !state.specialAreaNote().isBlank())
                    <p>${state.specialAreaNote()} That is why this page pairs a planning estimate with official sources, records links, and a local checklist before you move into quote mode.</p>
                @elseif (state.permitTimelineNote() != null && !state.permitTimelineNote().isBlank())
                    <p>${state.permitTimelineNote()} That is why this page pairs a planning estimate with official sources, records links, and a local checklist before you move into quote mode.</p>
                @elseif (state.buyerInspectionTrigger() != null && !state.buyerInspectionTrigger().isBlank() && stateMoneyPage.highlightBuyerTrigger())
                    <p>${state.buyerInspectionTrigger()} That is why this page pairs a planning estimate with official sources, records links, and a local checklist before you move into quote mode.</p>
                @elseif (state.maintenanceInspectionNote() != null && !state.maintenanceInspectionNote().isBlank() && stateMoneyPage.highlightMaintenanceNote())
                    <p>${state.maintenanceInspectionNote()} That is why this page pairs a planning estimate with official sources, records links, and a local checklist before you move into quote mode.</p>
                @else
                    <p>Use the official links and checklist below before you treat any estimate or contractor range as final. This page is designed to move you from a statewide planning number into the real local file and permit path.</p>
                @endif
            </article>

            <article class="detail-card">
                <h2>Permit path summary</h2>
                <p>${state.permitSummary()}</p>
            </article>

            <article class="detail-card detail-card--full">
                <h2>Main estimate drivers in ${state.stateName()}</h2>
                <ul class="detail-list">
                    @for (var driver : stateMoneyPage.driverBullets())
                        <li>${driver}</li>
                    @endfor
                </ul>
            </article>

            @if (stateMoneyPage.decisionSteps() != null && !stateMoneyPage.decisionSteps().isEmpty())
                <article class="detail-card detail-card--full" id="workflow">
                    <h2>How this workflow usually unfolds in ${state.stateName()}</h2>
                    <ol class="detail-list detail-list--numbered">
                        @for (var step : stateMoneyPage.decisionSteps())
                            <li>${step}</li>
                        @endfor
                    </ol>
                </article>
            @endif
        </div>
            </details>
        </div>
    </section>

    @if (countyWorkflowSynthesis != null)
        <section class="section section--tight state-money-disclosure-section">
            <div class="container">
                <details class="state-money-disclosure">
                    <summary>
                        <span class="eyebrow">${countyWorkflowSynthesis.eyebrow()}</span>
                        <strong>${countyWorkflowSynthesis.heading()}</strong>
                        <span>${countyWorkflowSynthesis.intro()}</span>
                    </summary>

                    <div class="detail-grid">
                        @for (var signal : countyWorkflowSynthesis.signals())
                            <article class="detail-card">
                                <h3>${signal.label()}</h3>
                                <p>${signal.summary()}</p>
                                <p><strong>Ask the county for:</strong> ${signal.firstAsk()}</p>
                                <p><strong>Coverage:</strong> ${signal.coverageNote()}</p>
                                <p><strong>Seen in:</strong> ${signal.countyExamples()}</p>
                            </article>
                        @endfor

                        @for (var field : countyWorkflowSynthesis.structureHighlights())
                            <article class="detail-card">
                                <h3>${field.label()}</h3>
                                <p>${field.value()}</p>
                            </article>
                        @endfor

                        <article class="detail-card">
                            <h3>${countyWorkflowSynthesis.firstArtifactsHeading()}</h3>
                            <ul class="detail-list">
                                @for (var artifact : countyWorkflowSynthesis.firstArtifacts())
                                    <li>${artifact}</li>
                                @endfor
                            </ul>
                        </article>

                        <article class="detail-card">
                            <h3>${countyWorkflowSynthesis.countyDropHeading()}</h3>
                            <ul class="detail-list">
                                @for (var trigger : countyWorkflowSynthesis.countyDropTriggers())
                                    <li>${trigger}</li>
                                @endfor
                            </ul>
                        </article>

                        <article class="detail-card">
                            <h3>${countyWorkflowSynthesis.holdQuoteHeading()}</h3>
                            <ul class="detail-list">
                                @for (var holdQuoteCheck : countyWorkflowSynthesis.holdQuoteChecks())
                                    <li>${holdQuoteCheck}</li>
                                @endfor
                            </ul>
                        </article>
                    </div>
                </details>
            </div>
        </section>
    @endif

    @if (!countyRecordLinks.isEmpty())
        <section class="section section--tight" id="county-pages">
            <div class="container">
                <div class="section-heading">
                    <span class="eyebrow">County Wedge</span>
                    @if ("septic-permit-process".equals(stateMoneyPage.contentSlug()))
                        <h2>County permit pages behind this state workflow</h2>
                        <p class="lede">Use these when the state permit page is still too broad and the real blocker is a county permit desk, closeout artifact, or local repair branch.</p>
                    @elseif ("buying-a-house-with-a-septic-system".equals(stateMoneyPage.contentSlug()))
                        <h2>County diligence pages behind this buyer workflow</h2>
                        <p class="lede">Use these when the buyer page is still too broad and the real blocker is a county file, transfer artifact, or local maintenance obligation.</p>
                    @else
                        <h2>County record pages behind this state workflow</h2>
                        <p class="lede">Use these when the state page is still too broad and the real blocker is a specific county file, location request, or local records form.</p>
                    @endif
                </div>

                <div class="detail-grid">
                    @for (var link : (featuredCountyRecordLinks.isEmpty() ? countyRecordLinks : featuredCountyRecordLinks).stream().limit(6).toList())
                        <article class="detail-card">
                            <h3><a href="${link.path()}" data-track-click="nav" data-track-source-context="state_money_county_page" data-track-target-type="county_page">${link.title()}</a></h3>
                            <p>${link.note()}</p>
                            <a class="button button--secondary" href="${link.path()}" data-track-click="nav" data-track-source-context="state_money_county_page_cta" data-track-target-type="county_page">Open county page</a>
                        </article>
                    @endfor
                    @if ((featuredCountyRecordLinks.isEmpty() ? countyRecordLinks : featuredCountyRecordLinks).size() > 6)
                        <article class="detail-card detail-card--full">
                            <h3>More county pages are available</h3>
                            <p>This page shows the strongest six county routes first so the workflow stays scannable. Use the state records page when you need the wider county list.</p>
                            <a class="button button--secondary" href="/septic-records-checklist/${state.slug()}/" data-track-click="nav" data-track-source-context="state_money_county_page_more" data-track-target-type="state_records_page">Open all ${state.stateName()} county routes</a>
                            <details class="county-link-overflow">
                                <summary>Show all county page links on this page</summary>
                                <ul class="detail-list detail-list--split">
                                    @for (var link : featuredCountyRecordLinks.isEmpty() ? countyRecordLinks : featuredCountyRecordLinks)
                                        <li><a href="${link.path()}" data-track-click="nav" data-track-source-context="state_money_county_page_all" data-track-target-type="county_page">${link.title()}</a></li>
                                    @endfor
                                </ul>
                            </details>
                        </article>
                    @endif
                </div>
            </div>
        </section>
    @endif

    <section class="section state-money-disclosure-section" id="sources">
        <div class="container">
            <details class="state-money-disclosure">
                <summary>
                    <span class="eyebrow">Verification layer</span>
                    <strong>Prep checks and official sources</strong>
                    <span>Open when you need the authority links, records sources, and low-end risk checks.</span>
                </summary>

        <div class="detail-grid">
            @if (state.whoToCallFirst() != null || (state.recordsToRequest() != null && !state.recordsToRequest().isEmpty()))
                <article class="detail-card">
                    <h2>${stateMoneyPage.localPrepHeading()}</h2>
                    @if (state.whoToCallFirst() != null)
                        <p><strong>Who to call first.</strong> ${state.whoToCallFirst()}</p>
                    @endif
                    @if (state.recordsToRequest() != null && !state.recordsToRequest().isEmpty())
                        <p><strong>Records to request.</strong></p>
                        <ul class="detail-list">
                            @for (var record : state.recordsToRequest())
                                <li>${record}</li>
                            @endfor
                        </ul>
                    @endif
                </article>
            @endif

            @if ((state.lowEndRiskChecks() != null && !state.lowEndRiskChecks().isEmpty()) || (stateMoneyPage.lowEndBreakers() != null && !stateMoneyPage.lowEndBreakers().isEmpty()))
                <article class="detail-card detail-card--full" id="risk">
                    <h2>${stateMoneyPage.riskHeading(state.stateName())}</h2>
                    @if (state.lowEndRiskChecks() != null && !state.lowEndRiskChecks().isEmpty())
                        <p><strong>State-level checks.</strong></p>
                        <ul class="detail-list">
                            @for (var risk : state.lowEndRiskChecks())
                                <li>${risk}</li>
                            @endfor
                            <li>${state.localOverrideNote()}</li>
                        </ul>
                    @endif
                    @if (stateMoneyPage.lowEndBreakers() != null && !stateMoneyPage.lowEndBreakers().isEmpty())
                        <p><strong>Page-specific checks.</strong></p>
                        <ul class="detail-list">
                            @for (var lowEndBreaker : stateMoneyPage.lowEndBreakers())
                                <li>${lowEndBreaker}</li>
                            @endfor
                        </ul>
                    @endif
                </article>
            @endif

            @if (state.permitTimelineNote() != null)
                <article class="detail-card">
                    <h2>Permit timeline watch</h2>
                    <p>${state.permitTimelineNote()}</p>
                </article>
            @endif

            @if (state.buyerInspectionTrigger() != null && stateMoneyPage.highlightBuyerTrigger())
                <article class="detail-card">
                    <h2>${stateMoneyPage.buyerTriggerHeading()}</h2>
                    <p>${state.buyerInspectionTrigger()}</p>
                </article>
            @endif

            @if (state.maintenanceInspectionNote() != null && stateMoneyPage.highlightMaintenanceNote())
                <article class="detail-card">
                    <h2>${stateMoneyPage.maintenanceHeading()}</h2>
                    <p>${state.maintenanceInspectionNote()}</p>
                </article>
            @endif

            @if (state.specialAreaNote() != null)
                <article class="detail-card detail-card--full">
                    <h2>Special state wrinkle</h2>
                    <p>${state.specialAreaNote()}</p>
                </article>
            @endif

            @if (stateMoneyPage.quotePrepChecklist() != null && !stateMoneyPage.quotePrepChecklist().isEmpty())
                <article class="detail-card">
                    <h2>${stateMoneyPage.quoteChecklistHeading()}</h2>
                    <ul class="detail-list">
                        @for (var checklistItem : stateMoneyPage.quotePrepChecklist())
                            <li>${checklistItem}</li>
                        @endfor
                    </ul>
                </article>
            @endif

            @if (!localAuthoritySources.isEmpty() || !recordsLookupSources.isEmpty())
                <article class="detail-card">
                    <h2>${stateMoneyPage.officialLinksHeading()}</h2>
                    @if (!localAuthoritySources.isEmpty())
                        <p><strong>${stateMoneyPage.localAuthorityHeading()}.</strong></p>
                        <ul class="source-list source-list--compact">
                            @for (var source : localAuthoritySources)
                                <li>
                                    <strong>${source.agencyName()}</strong>
                                    <a href="${source.url()}" target="_blank" rel="noreferrer">${source.title()}</a>
                                    <div class="source-meta">
                                        @if (source.trustLevel() != null && !source.trustLevel().isBlank())
                                            <span>Trust: ${source.trustLevel()}</span>
                                        @endif
                                        @if (source.draftOrFinalStatus() != null && !source.draftOrFinalStatus().isBlank())
                                            <span>Status: ${source.draftOrFinalStatus()}</span>
                                        @endif
                                        @if (source.effectiveDate() != null && !source.effectiveDate().isBlank())
                                            <span>Effective: ${source.effectiveDate()}</span>
                                        @endif
                                        @if (source.lastVerifiedAt() != null && !source.lastVerifiedAt().isBlank())
                                            <span>Last verified: ${source.lastVerifiedAt()}</span>
                                        @endif
                                    </div>
                                </li>
                            @endfor
                        </ul>
                    @endif
                    @if (!recordsLookupSources.isEmpty())
                        <p><strong>${stateMoneyPage.recordsLookupHeading()}.</strong></p>
                        <ul class="source-list source-list--compact">
                            @for (var source : recordsLookupSources)
                                <li>
                                    <strong>${source.agencyName()}</strong>
                                    <a href="${source.url()}" target="_blank" rel="noreferrer">${source.title()}</a>
                                    <div class="source-meta">
                                        @if (source.trustLevel() != null && !source.trustLevel().isBlank())
                                            <span>Trust: ${source.trustLevel()}</span>
                                        @endif
                                        @if (source.draftOrFinalStatus() != null && !source.draftOrFinalStatus().isBlank())
                                            <span>Status: ${source.draftOrFinalStatus()}</span>
                                        @endif
                                        @if (source.effectiveDate() != null && !source.effectiveDate().isBlank())
                                            <span>Effective: ${source.effectiveDate()}</span>
                                        @endif
                                        @if (source.lastVerifiedAt() != null && !source.lastVerifiedAt().isBlank())
                                            <span>Last verified: ${source.lastVerifiedAt()}</span>
                                        @endif
                                    </div>
                                </li>
                            @endfor
                        </ul>
                    @endif
                </article>
            @endif

            <article class="trust-box">
                <span class="trust-box__label">Official-source context</span>
                <p><strong>${state.agencyName()}</strong> and related official materials support this page. Final design, permit path, and approval still need local verification.</p>
                <ul class="source-list source-list--compact">
                    @for (var source : sources)
                        <li>
                            <strong>${source.agencyName()}</strong>
                            <a href="${source.url()}" target="_blank" rel="noreferrer">${source.title()}</a>
                            <div class="source-meta">
                                @if (source.trustLevel() != null && !source.trustLevel().isBlank())
                                    <span>Trust: ${source.trustLevel()}</span>
                                @endif
                                @if (source.draftOrFinalStatus() != null && !source.draftOrFinalStatus().isBlank())
                                    <span>Status: ${source.draftOrFinalStatus()}</span>
                                @endif
                                @if (source.effectiveDate() != null && !source.effectiveDate().isBlank())
                                    <span>Effective: ${source.effectiveDate()}</span>
                                @endif
                                @if (source.lastVerifiedAt() != null && !source.lastVerifiedAt().isBlank())
                                    <span>Last verified: ${source.lastVerifiedAt()}</span>
                                @endif
                            </div>
                        </li>
                    @endfor
                </ul>
            </article>
        </div>
            </details>
        </div>
    </section>

    <section class="section section--muted" id="faq">
        <div class="container">
            <div class="section-heading">
                <span class="eyebrow">FAQ</span>
                <h2>${state.stateName()} questions this page should answer before a quote request.</h2>
            </div>

            <div class="detail-grid">
                @for (var faq : stateMoneyPage.faqBlocks())
                    <article class="detail-card">
                        <h3>${faq.question()}</h3>
                        <p>${faq.answer()}</p>
                    </article>
                @endfor
            </div>
        </div>
    </section>

    <section class="section">
        <div class="container content-grid">
            <article class="trust-box trust-box--large">
                <span class="trust-box__label">Next best action</span>
                <h2>${calculatorCtaLabel}</h2>
                <p>
                    ${calculatorCtaNote} The calculator result already shows the likely tank band, system class, cost range, and state-specific rule context.
                    @if (showQuoteCta)
                        If you already know the project type, you can also skip straight to the short quote form.
                    @else
                        Use the file, permit, or authority path above before you move into quote mode.
                    @endif
                </p>
                @if (countyWorkflowSynthesis != null && !countyWorkflowSynthesis.firstArtifacts().isEmpty())
                    <p><strong>Pull first.</strong> ${countyWorkflowSynthesis.firstArtifacts().get(0)}</p>
                @endif
                @if (countyWorkflowSynthesis != null && !countyWorkflowSynthesis.holdQuoteChecks().isEmpty())
                    <p><strong>Hold quote until.</strong> ${countyWorkflowSynthesis.holdQuoteChecks().get(0)}</p>
                @endif
                <div class="hero__actions">
                    <a class="button button--primary" href="/septic-system-cost-calculator/?state=${state.stateCode()}&projectType=${stateMoneyPage.calculatorProjectType()}" data-track-click="nav" data-track-source-context="state_money_next_best_calculator" data-track-target-type="calculator">Run the estimate</a>
                    @if (showQuoteCta)
                        <a class="button button--secondary" href="/septic-system-cost-calculator/?state=${state.stateCode()}&projectType=${stateMoneyPage.calculatorProjectType()}&quoteMode=true#quote-request" data-track-click="nav" data-track-source-context="state_money_next_best_quote" data-track-target-type="quote_form">Get matched with local septic pros</a>
                    @endif
                </div>
            </article>

            <article class="detail-card">
                <h2>Related links</h2>
                <ul class="detail-list">
                    @for (var link : secondaryInternalLinks.isEmpty() ? internalLinks : secondaryInternalLinks)
                        <li>
                            <a href="${link.path()}" data-track-click="nav" data-track-source-context="state_money_related_link" data-track-target-type="related_internal">${link.title()}</a>
                            <p class="detail-list__note">${link.note()}</p>
                        </li>
                    @endfor
                </ul>
            </article>
        </div>
    </section>

    <div class="sticky-mobile-cta-spacer" aria-hidden="true"></div>
    <section class="sticky-mobile-cta sticky-mobile-cta--single"
              aria-label="Run estimate"
              data-sticky-mobile-cta
              data-show-after='[data-sticky-trigger="state-money-after-hero"]'>
        <div class="sticky-mobile-cta__inner">
            @if (primaryAction.calculator())
                <a class="button button--primary" href="/septic-system-cost-calculator/?state=${state.stateCode()}&projectType=${stateMoneyPage.calculatorProjectType()}" data-track-click="nav" data-track-source-context="state_money_sticky_calculator" data-track-target-type="calculator">Run state estimate</a>
            @else
                <a class="button button--primary" href="${primaryAction.path()}" data-track-click="nav" data-track-source-context="${primaryAction.sourceContext()}" data-track-target-type="${primaryAction.targetType()}">${primaryAction.buttonLabel()}</a>
            @endif
        </div>
    </section>
    </div>
`)
