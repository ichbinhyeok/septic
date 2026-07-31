@import com.example.septic.data.model.SourceRecord
@import com.example.septic.data.model.StateMoneyPage
@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.PageMeta
@import com.example.septic.web.EditorialProfile
@import com.example.septic.web.CountyWorkflowFieldView
@import com.example.septic.web.StateMoneyPrimaryAction
@import com.example.septic.web.StateCountyWorkflowSynthesisView
@import com.example.septic.web.StateCostScopeView
@import com.example.septic.web.SearchIntentOpportunityView
@import com.example.septic.web.StateRecordsSearchResponseView
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
@param StateRecordsSearchResponseView stateRecordsSearchResponse = null
@param java.util.List<CountyWorkflowFieldView> stateOfficialFilePathRows = java.util.List.of()
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
                @if (("NC".equals(state.stateCode()) || "IN".equals(state.stateCode())) && "septic-records-checklist".equals(stateMoneyPage.contentSlug()) && !countyRecordLinks.isEmpty())
                    <div class="hero-county-launchpad hero-county-launchpad--before-panel" aria-label="${state.stateName()} county record picker">
                        <span class="hero-county-launchpad__label">County records</span>
                        <p class="hero-county-launchpad__note">Already know the county? Open its local permit-record workflow without leaving SepticPath.</p>
                        <form class="hero-county-picker" data-county-route-picker data-county-route-prefix="/septic-records-checklist/${state.slug()}/" data-county-route-state="${state.stateName()}">
                            <label for="${state.slug()}-county-route">Find your ${state.stateName()} county</label>
                            <div class="hero-county-picker__controls">
                                <select id="${state.slug()}-county-route" data-county-route-select required>
                                    <option value="">Choose a county</option>
                                    @for (var countyLink : (featuredCountyRecordLinks.isEmpty() ? countyRecordLinks : featuredCountyRecordLinks))
                                        <option value="${countyLink.path()}">${countyLink.compactTitle()}</option>
                                    @endfor
                                </select>
                                <button class="button button--primary" type="submit" data-county-route-submit disabled>Open county records</button>
                            </div>
                            <p class="hero-county-picker__status" data-county-route-status aria-live="polite">Select a county to stay on SepticPath and open its local permit-record workflow.</p>
                        </form>
                    </div>
                @endif
                <div class="workflow-console" aria-label="Septic workflow triage console">
                    <div class="workflow-console__topbar">
                        <span class="workflow-console__status">Start with the property</span>
                        <span>${state.stateCode()} records lookup</span>
                    </div>
                    <div class="workflow-console__body">
                        <div class="workflow-console__readout">
                            <span class="eyebrow">Current verdict</span>
                            <h2>
                                @if ("septic-permit-process".equals(stateMoneyPage.contentSlug()))
                                    Find the permit desk before pricing the work.
                                @elseif ("septic-records-checklist".equals(stateMoneyPage.contentSlug()))
                                    Find the county file before you trust the story.
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
                                @if ("septic-records-checklist".equals(stateMoneyPage.contentSlug()))
                                    <a class="button button--primary" href="/septic-record-finder/" data-track-click="nav" data-track-source-context="state_records_hero_address_finder" data-track-target-type="record_finder">Find records by address</a>
                                    <a class="button button--secondary" href="${primaryAction.path()}" data-track-click="nav" data-track-source-context="${primaryAction.sourceContext()}" data-track-target-type="${primaryAction.targetType()}">I already know the county</a>
                                @elseif (primaryAction.calculator() && showQuoteCta)
                                    <a class="button button--primary" href="${primaryAction.path()}" data-track-click="nav" data-track-source-context="${primaryAction.sourceContext()}" data-track-target-type="${primaryAction.targetType()}">${primaryAction.buttonLabel()}</a>
                                    <a class="button button--secondary" href="${quotePath}" data-track-click="nav" data-track-source-context="state_money_primary_quote" data-track-target-type="quote_form">Get matched with local septic pros</a>
                                @else
                                    <a class="button button--primary" href="${primaryAction.path()}" data-track-click="nav" data-track-source-context="${primaryAction.sourceContext()}" data-track-target-type="${primaryAction.targetType()}">${primaryAction.buttonLabel()}</a>
                                    @if (!"county_page_directory".equals(primaryAction.targetType()))
                                    <a class="button button--secondary" href="${calculatorPath}" data-track-click="nav" data-track-source-context="state_money_secondary_calculator" data-track-target-type="calculator">Run the estimate</a>
                                    @endif
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
                                        Who holds the file
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
                                <span class="workflow-console__label">What to bring</span>
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
                                <span class="workflow-console__label">Before you price</span>
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
                @if (stateRecordsSearchResponse != null && !stateRecordsSearchResponse.countyLinks().isEmpty())
                    <div class="hero-county-launchpad" aria-label="${state.stateName()} priority county record shortcuts">
                        <span class="hero-county-launchpad__label">County records</span>
                        <p class="hero-county-launchpad__note">Already know the county? Go straight to its official record route.</p>
                        @for (var link : stateRecordsSearchResponse.countyLinks().stream().limit(4).toList())
                            <a href="${link.path()}"
                               data-track-click="nav"
                               data-track-source-context="state_records_hero_county_handoff"
                               data-track-target-type="county_records_page">${link.compactTitle()}</a>
                        @endfor
                        <a href="#county-pages">All ${state.stateName()} county pages</a>
                    </div>
                @elseif (!featuredCountyRecordLinks.isEmpty())
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
                @if (!"septic-records-checklist".equals(stateMoneyPage.contentSlug()))
                    <div class="hero__actions">
                        <a class="button button--secondary" href="${guidePath}" data-track-click="nav" data-track-source-context="state_money_back_to_guide" data-track-target-type="state_guide">Open the ${state.stateName()} guide</a>
                    </div>
                @endif
                <p class="lede state-money-intro-copy">${stateMoneyPage.introCopy()}</p>
                @if (stateRecordsSearchResponse != null)
                    <div class="aeo-answer-capsule" aria-label="State answer summary and route comparison">
                        <span class="eyebrow">Answer at a glance</span>
                        <h2>${stateRecordsSearchResponse.heading()}</h2>
                        <p>${stateRecordsSearchResponse.summary()}</p>
                        @if (primaryRecordsLookupSource != null)
                            <p class="content-quick-answer__source">
                                Official source: <a href="${primaryRecordsLookupSource.url()}" target="_blank" rel="noreferrer">${primaryRecordsLookupSource.agencyName()}</a>
                                · content reviewed ${primaryRecordsLookupSource.contentVerifiedAt()}
                            </p>
                        @elseif (primaryLocalAuthoritySource != null)
                            <p class="content-quick-answer__source">
                                Official source: <a href="${primaryLocalAuthoritySource.url()}" target="_blank" rel="noreferrer">${primaryLocalAuthoritySource.agencyName()}</a>
                                · content reviewed ${primaryLocalAuthoritySource.contentVerifiedAt()}
                            </p>
                        @endif
                        @if (!stateRecordsSearchResponse.responseRows().isEmpty())
                            <div class="facts-table-wrap">
                                <table class="facts-table">
                                    <tbody>
                                    @for (var row : stateRecordsSearchResponse.responseRows().stream().limit(4).toList())
                                        <tr><th scope="row">${row.label()}</th><td>${row.value()}</td></tr>
                                    @endfor
                                    </tbody>
                                </table>
                            </div>
                        @endif
                    </div>
                @endif
                @if (!searchIntentOpportunities.isEmpty())
                    <div class="quick-link-strip" aria-label="${state.stateName()} records search shortcuts">
                        <span class="quick-link-strip__label">Questions answered</span>
                        @for (var opportunity : searchIntentOpportunities)
                            <a class="button button--secondary quick-link-strip__button"
                               href="#${opportunity.anchorId()}"
                               data-track-click="nav"
                               data-track-source-context="state_money_hero_search_match"
                               data-track-target-type="page_anchor">${opportunity.questionHeading()}</a>
                        @endfor
                    </div>
                @endif
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
                <strong>${state.ruleTypeLabel()}</strong>
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
                        <dt>Latest review activity</dt>
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
                                <p class="next-move-row__signal"><strong>Pull first for ${stateMoneyPage.title()}:</strong> ${countyWorkflowSynthesis.firstArtifacts().get(0)}</p>
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
                                <p class="next-move-row__signal"><strong>Hold ${state.stateName()} pricing when:</strong> ${countyWorkflowSynthesis.holdQuoteChecks().get(0)}</p>
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
                            <p>For ${stateMoneyPage.title()}, use the official records path when you still need the permit, as-built, inspection, or maintenance file before moving into quote mode.</p>
                            @if (countyWorkflowSynthesis != null && !countyWorkflowSynthesis.firstArtifacts().isEmpty())
                                <p class="next-move-row__signal"><strong>Start this ${state.stateName()} workflow with:</strong> ${countyWorkflowSynthesis.firstArtifacts().get(0)}</p>
                            @endif
                        </div>
                        <a class="button button--secondary" href="${primaryRecordsLookupSource.url()}" target="_blank" rel="noreferrer">Open records lookup</a>
                    @elseif (primaryLocalAuthoritySource != null)
                        <div class="next-move-row__body">
                            <span class="next-move-row__label">Verify the next office</span>
                            <h3>Confirm the local authority before you schedule work</h3>
                            <p>For ${stateMoneyPage.title()}, use the ${state.stateName()} local office path when you still need the real permit desk, reviewing authority, or delegated county office before trusting the low end.</p>
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

            @if ("septic-records-checklist".equals(stateMoneyPage.contentSlug()))
                <section class="records-proof-ladder" aria-label="${state.stateName()} records proof ladder">
                    <div class="section-heading">
                        <span class="eyebrow">Records proof ladder</span>
                        <h2>Turn the lookup into the exact file task before pricing.</h2>
                        <p class="lede">For ${state.stateName()}, use these routes when the search needs a parcel lookup, written records request, as-built file, or county page instead of another broad state summary.</p>
                    </div>
                    <div class="detail-grid">
                        <article class="detail-card">
                            <h3>Septic permit search by address</h3>
                            <p>Use this ${state.stateName()} route when the user has an address, parcel, owner, APN, TMS, or legal-description clue and needs the right file owner before the record is treated as missing.</p>
                            <a class="button button--secondary"
                               href="/septic-permit-search-by-address/"
                               data-track-click="nav"
                               data-track-source-context="state_records_proof_address"
                               data-track-target-type="internal_page">Open address search guide</a>
                        </article>
                        <article class="detail-card">
                            <h3>Septic permit records request</h3>
                            <p>Use this ${state.stateName()} route when the next move is asking for a permit copy, as-built, final approval, repair file, inspection letter, or written no-record response.</p>
                            <a class="button button--secondary"
                               href="/septic-permit-records-request/"
                               data-track-click="nav"
                               data-track-source-context="state_records_proof_request"
                               data-track-target-type="internal_page">Open records request guide</a>
                        </article>
                        <article class="detail-card">
                            <h3>Septic as-built records</h3>
                            <p>Use this ${state.stateName()} route when the installed layout, site sketch, tank location, drain field, reserve area, or repair scope can change the next quote or deal decision.</p>
                            <a class="button button--secondary"
                               href="/septic-as-built-records/"
                               data-track-click="nav"
                               data-track-source-context="state_records_proof_as_built"
                               data-track-target-type="internal_page">Open as-built records guide</a>
                        </article>
                        <article class="detail-card">
                            <h3>Septic records by county</h3>
                            <p>Use this ${state.stateName()} route when the county is known and the next useful click is the local permit file, records request, parcel lookup, or office path.</p>
                            <a class="button button--secondary"
                               href="/septic-records-by-county/"
                               data-track-click="nav"
                               data-track-source-context="state_records_proof_county"
                               data-track-target-type="internal_page">Open county records guide</a>
                        </article>
                    </div>
                </section>
            @endif

            @if (!stateOfficialFilePathRows.isEmpty())
                <section class="state-records-response" aria-label="${state.stateName()} official state file path">
                    <div class="state-records-response__head">
                        <div>
                            <span class="eyebrow">Official state file path</span>
                            <h2>Use the official file route before another broad search.</h2>
                            <p>This is the ${state.stateName()} records path to follow when the visitor needs a permit copy, as-built, inspection letter, repair trail, or written no-record response tied to a parcel.</p>
                        </div>
                        <div class="state-records-response__priority">
                            <span>Verification set</span>
                            <strong>Owner, artifact, request, fallback</strong>
                        </div>
                    </div>

                    <div class="state-records-response__body">
                        <div class="state-records-response__rows">
                            @for (var row : stateOfficialFilePathRows)
                                <article>
                                    <span>${row.label()}</span>
                                    <p>${row.value()}</p>
                                </article>
                            @endfor
                        </div>

                        <aside class="state-records-response__route" aria-label="${state.stateName()} official source and fallback links">
                            <span class="state-records-response__route-label">Action order</span>
                            <h3>Keep the next click inside the file workflow.</h3>
                            <div class="state-records-response__route-list">
                                @if (primaryRecordsLookupSource != null)
                                    <a href="${primaryRecordsLookupSource.url()}"
                                       target="_blank"
                                       rel="noreferrer"
                                       data-track-click="nav"
                                       data-track-source-context="state_official_file_path_source"
                                       data-track-target-type="official_source">
                                        <strong>Open ${state.stateName()} official records source</strong>
                                        <span>${primaryRecordsLookupSource.agencyName()} | ${primaryRecordsLookupSource.title()}</span>
                                    </a>
                                @elseif (primaryLocalAuthoritySource != null)
                                    <a href="${primaryLocalAuthoritySource.url()}"
                                       target="_blank"
                                       rel="noreferrer"
                                       data-track-click="nav"
                                       data-track-source-context="state_official_file_path_source"
                                       data-track-target-type="official_source">
                                        <strong>Open ${state.stateName()} official authority source</strong>
                                        <span>${primaryLocalAuthoritySource.agencyName()} | ${primaryLocalAuthoritySource.title()}</span>
                                    </a>
                                @endif
                                <a href="/septic-permit-search-by-address/"
                                   data-track-click="nav"
                                   data-track-source-context="state_official_file_path_address"
                                   data-track-target-type="internal_page">
                                    <strong>Search by address</strong>
                                    <span>Use address, parcel, APN, TMS, owner, or legal-description clues.</span>
                                </a>
                                <a href="/septic-permit-records-request/"
                                   data-track-click="nav"
                                   data-track-source-context="state_official_file_path_request"
                                   data-track-target-type="internal_page">
                                    <strong>Records request wording</strong>
                                    <span>Ask for permit copy, as-built, final approval, inspection letter, or no-record response.</span>
                                </a>
                                @if (!countyRecordLinks.isEmpty())
                                    <a href="#county-pages"
                                       data-track-click="nav"
                                       data-track-source-context="state_official_file_path_counties"
                                       data-track-target-type="page_anchor">
                                        <strong>Open county handoff</strong>
                                        <span>Use county pages when the search already names the county or local office.</span>
                                    </a>
                                @endif
                            </div>
                        </aside>
                    </div>
                </section>
            @endif

            @if (stateRecordsSearchResponse != null)
                <section class="state-records-response" aria-label="${state.stateName()} records lookup guide">
                    <div class="state-records-response__head">
                        <div>
                            <span class="eyebrow">${stateRecordsSearchResponse.badgeLabel()}</span>
                            <h2>${stateRecordsSearchResponse.heading()}</h2>
                            <p>${stateRecordsSearchResponse.summary()}</p>
                        </div>
                        <div class="state-records-response__priority">
                            <span>Priority lane</span>
                            <strong>${stateRecordsSearchResponse.priorityLabel()}</strong>
                        </div>
                    </div>

                    <div class="state-records-response__queries" aria-label="Search phrases this page answers">
                        @for (var question : stateRecordsSearchResponse.questions())
                            <span>${question}</span>
                        @endfor
                    </div>

                    <div class="state-records-response__body">
                        <div class="state-records-response__rows">
                            @for (var row : stateRecordsSearchResponse.responseRows())
                                <article>
                                    <span>${row.label()}</span>
                                    <p>${row.value()}</p>
                                </article>
                            @endfor
                        </div>

                        <aside class="state-records-response__route" aria-label="${state.stateName()} county route handoff">
                            <span class="state-records-response__route-label">County route handoff</span>
                            <h3>Do not make county-known searches restart.</h3>
                            @if (!stateRecordsSearchResponse.countyLinks().isEmpty())
                                <div class="state-records-response__route-list">
                                    @for (var link : stateRecordsSearchResponse.countyLinks())
                                        <a href="${link.path()}"
                                           data-track-click="nav"
                                           data-track-source-context="state_records_response_county_handoff"
                                           data-track-target-type="county_records_page">
                                            <strong>${link.compactTitle()}</strong>
                                            <span>${link.note()}</span>
                                        </a>
                                    @endfor
                                </div>
                            @else
                                <p>Keep the state records path tight, then send county-known searches into the county records index.</p>
                            @endif
                        </aside>
                    </div>

                    <div class="state-records-response__actions" aria-label="${state.stateName()} records next actions">
                        @for (var link : stateRecordsSearchResponse.actionLinks())
                            <a class="button button--secondary"
                               href="${link.path()}"
                               data-track-click="nav"
                               data-track-source-context="state_records_response_action"
                               data-track-target-type="internal_page">${link.title()}</a>
                        @endfor
                    </div>
                </section>
            @endif

            @if (!searchIntentOpportunities.isEmpty())
                <section class="search-intent-board" aria-label="Common search intents this page answers">
                        <div class="search-intent-board__header">
                            <span class="eyebrow">Common records searches</span>
                            <h2>Use the ${state.stateName()} search phrase that matches the job.</h2>
                            <p>Each ${state.stateName()} route below resolves a different records search into the same practical next step: identify the file owner, pull the permit trail, and then decide whether the record is strong enough to trust.</p>
                        </div>
                    <div class="search-intent-board__grid">
                        @for (var opportunity : searchIntentOpportunities)
                            <article class="search-intent-card" id="${opportunity.anchorId()}">
                                <span class="search-intent-card__label">${opportunity.eyebrow()}</span>
                                <h3>${opportunity.questionHeading()}</h3>
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
                            <p>For ${stateMoneyPage.title()}, use the local office first when you want to move from a planning page into an actual permit or records workflow.</p>
                            <a class="button button--secondary" href="${primaryLocalAuthoritySource.url()}" target="_blank" rel="noreferrer">Open local authority source</a>
                        </article>
                    @endif

                    @if (primaryRecordsLookupSource != null)
                        <article class="cost-gate-panel__source">
                            <span class="cost-gate-panel__source-label">Record gate</span>
                            <h3>${stateMoneyPage.recordsLookupHeading()}</h3>
                            <p>For ${stateMoneyPage.title()}, use the existing record trail to confirm whether this property still fits the low end before you move into quote mode.</p>
                            <a class="button button--secondary" href="${primaryRecordsLookupSource.url()}" target="_blank" rel="noreferrer">Open records lookup</a>
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
                            <td>${state.ruleTypeLabel()}</td>
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
                        <p><strong>${stateMoneyPage.title()} permit context:</strong> ${state.permitSummary()} The first practical check is usually the office, file path, or reviewer identified in this state workflow: ${state.whoToCallFirst()}</p>
                    @else
                        <p><strong>${stateMoneyPage.title()} permit context:</strong> ${state.permitSummary()} Use the local file and authority path below before you treat the lower planning range as real.</p>
                    @endif
                @endif
                @if (state.specialAreaNote() != null && !state.specialAreaNote().isBlank())
                    <p><strong>${stateMoneyPage.title()} local exception:</strong> ${state.specialAreaNote()} That is why this page pairs a planning estimate with official sources, records links, and a local checklist before you move into quote mode.</p>
                @elseif (state.permitTimelineNote() != null && !state.permitTimelineNote().isBlank())
                    <p><strong>${stateMoneyPage.title()} timing check:</strong> ${state.permitTimelineNote()} That is why this page pairs a planning estimate with official sources, records links, and a local checklist before you move into quote mode.</p>
                @elseif (state.buyerInspectionTrigger() != null && !state.buyerInspectionTrigger().isBlank() && stateMoneyPage.highlightBuyerTrigger())
                    <p><strong>${stateMoneyPage.title()} buyer trigger:</strong> ${state.buyerInspectionTrigger()} That is why this page pairs a planning estimate with official sources, records links, and a local checklist before you move into quote mode.</p>
                @elseif (state.maintenanceInspectionNote() != null && !state.maintenanceInspectionNote().isBlank() && stateMoneyPage.highlightMaintenanceNote())
                    <p><strong>${stateMoneyPage.title()} maintenance trigger:</strong> ${state.maintenanceInspectionNote()} That is why this page pairs a planning estimate with official sources, records links, and a local checklist before you move into quote mode.</p>
                @else
                    <p>For ${stateMoneyPage.title()}, use the official links and checklist below before you treat any estimate or contractor range as final. This page is designed to move you from a statewide planning number into the real local file and permit path.</p>
                @endif
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

    @if (countyWorkflowSynthesis != null && "septic-records-checklist".equals(stateMoneyPage.contentSlug()))
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
                                <p>For ${stateMoneyPage.title()}, ${signal.summary()} Ask the ${state.stateName()} county for ${signal.firstAsk()} ${signal.coverageNote()} Examples: ${signal.countyExamples()}.</p>
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
                        <p class="lede">Use these ${state.stateName()} routes when the state permit page is still too broad and the real blocker is a county permit desk, closeout artifact, or local repair branch.</p>
                    @elseif ("buying-a-house-with-a-septic-system".equals(stateMoneyPage.contentSlug()))
                        <h2>County diligence pages behind this buyer workflow</h2>
                        <p class="lede">Use these ${state.stateName()} routes when the buyer page is still too broad and the real blocker is a county file, transfer artifact, or local maintenance obligation.</p>
                    @else
                        <h2>County record pages behind this state workflow</h2>
                        <p class="lede">Use these ${state.stateName()} routes when this ${stateMoneyPage.title()} page is still too broad and the real blocker is a specific county file, location request, or local records form.</p>
                    @endif
                </div>

                <div class="detail-grid">
                    @for (var link : (featuredCountyRecordLinks.isEmpty() ? countyRecordLinks : featuredCountyRecordLinks).stream().limit(6).toList())
                        <article class="detail-card">
                            <h3><a href="${link.path()}" data-track-click="nav" data-track-source-context="state_money_county_page" data-track-target-type="county_page">${link.title()}</a></h3>
                            <a class="button button--secondary" href="${link.path()}" data-track-click="nav" data-track-source-context="state_money_county_page_cta" data-track-target-type="county_page">Open county page</a>
                        </article>
                    @endfor
                    @if ((featuredCountyRecordLinks.isEmpty() ? countyRecordLinks : featuredCountyRecordLinks).size() > 6)
                        <article class="detail-card detail-card--full">
                            <h3>More county pages are available</h3>
                    <p>This ${stateMoneyPage.title()} page shows the strongest six ${state.stateName()} county routes first so the workflow stays scannable. Use the state records page when you need the wider county list.</p>
                            <a class="button button--secondary" href="/septic-records-checklist/${state.slug()}/" data-track-click="nav" data-track-source-context="state_money_county_page_more" data-track-target-type="state_records_page">Open all ${state.stateName()} county routes</a>
                            <details class="county-link-overflow">
                                <summary>Show all county page links on this page</summary>
                                <ul class="detail-list detail-list--split">
                                    @for (var link : (featuredCountyRecordLinks.isEmpty() ? countyRecordLinks : featuredCountyRecordLinks).stream().skip(6).toList())
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
                        <p><strong>${state.stateName()}: ${stateMoneyPage.localAuthorityHeading()}.</strong></p>
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
                                        @template.tags.sourceVerification(source = source)
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
                                        @template.tags.sourceVerification(source = source)
                                    </div>
                                </li>
                            @endfor
                        </ul>
                    @endif
                </article>
            @endif

            <article class="trust-box">
                <span class="trust-box__label">Official-source context</span>
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
                                @template.tags.sourceVerification(source = source)
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
