@import com.example.septic.data.model.SourceRecord
@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.PageMeta
@import com.example.septic.web.EditorialProfile
@param PageMeta page
@param StateProfile state
@param java.util.List<SourceRecord> sources
@param java.util.List<SourceRecord> localAuthoritySources = java.util.List.of()
@param java.util.List<SourceRecord> recordsLookupSources = java.util.List.of()
@param SourceRecord primaryLocalAuthoritySource = null
@param SourceRecord primaryRecordsLookupSource = null
@param java.util.List<com.example.septic.data.model.StateMoneyPage> stateMoneyPages = java.util.List.of()
@param java.util.List<com.example.septic.web.StateRuleFactView> stateRuleFacts = java.util.List.of()
@param java.util.List<com.example.septic.data.model.FaqBlock> guideFaqs = java.util.List.of()
@param java.util.List<com.example.septic.web.CoreStateComparisonRow> coreStateComparisonRows = java.util.List.of()
@param String guideHeading = "State septic cost guide"
@param String calculatorCtaLabel = "Open the main calculator"
@param String calculatorCtaNote = "Use your state and project assumptions first, then verify locally."
@param com.example.septic.web.StatePlanningSnapshot planningSnapshot = null
@param EditorialProfile editorialPreparedBy = null
@param EditorialProfile editorialReviewedBy = null
@param String editorialReviewedAgainst = ""
@param String editorialLastReviewedAt = ""
@param String editorialNote = ""

@template.layouts.app(page = page, content = @`
    <section class="section section--heroish">
        <div class="container state-hero">
            <div>
                <span class="eyebrow">${state.stateCode()} state guide</span>
                <h1>${guideHeading}</h1>
                <div class="hero__actions">
                    <a class="button button--primary" href="/septic-system-cost-calculator/?state=${state.stateCode()}" data-track-click="nav" data-track-source-context="state_guide_primary_calculator" data-track-target-type="calculator">${calculatorCtaLabel}</a>
                    <a class="button button--secondary" href="/septic-system-cost-calculator/?state=${state.stateCode()}&quoteMode=true#quote-request" data-track-click="nav" data-track-source-context="state_guide_primary_quote" data-track-target-type="quote_form">Start short quote form</a>
                </div>
                <p class="lede">${state.ruleSummaryPlainEnglish()}</p>
            </div>
            <div class="hero-sidecard">
                <span class="pill">Official-source guide</span>
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
                <a class="button button--secondary" href="/septic-system-cost-calculator/?state=${state.stateCode()}&quoteMode=true#quote-request" data-track-click="nav" data-track-source-context="state_guide_side_quote" data-track-target-type="quote_form">Get matched with local septic pros</a>
                <p>${calculatorCtaNote}</p>
            </div>
        </div>
    </section>

    @if (planningSnapshot != null || primaryLocalAuthoritySource != null || primaryRecordsLookupSource != null)
        <section class="section section--tight">
            <div class="container detail-grid">
                @if (planningSnapshot != null)
                    <article class="detail-card detail-card--full">
                        <h2>Planning cost snapshot</h2>
                        <div class="result-grid">
                            <article class="metric-card">
                                <span class="metric-card__label">Install midpoint</span>
                                <strong>${planningSnapshot.installMidpoint()}</strong>
                            </article>
                            <article class="metric-card">
                                <span class="metric-card__label">Replacement midpoint</span>
                                <strong>${planningSnapshot.replacementMidpoint()}</strong>
                            </article>
                            <article class="metric-card">
                                <span class="metric-card__label">Perc planning range</span>
                                <strong>${planningSnapshot.percRange()}</strong>
                            </article>
                            <article class="metric-card">
                                <span class="metric-card__label">Pumping planning range</span>
                                <strong>${planningSnapshot.pumpingRange()}</strong>
                            </article>
                        </div>
                        <p>${planningSnapshot.comparisonNote()} These figures are still planning-only ranges, not an official fee schedule.</p>
                    </article>
                @endif

                @if (primaryLocalAuthoritySource != null)
                    <article class="detail-card">
                        <h2>Find the local permitting authority</h2>
                        <p>${state.stateName()} usually becomes more concrete once you confirm the actual local office handling septic permitting and review.</p>
                        <a class="button button--secondary" href="${primaryLocalAuthoritySource.url()}" target="_blank" rel="noreferrer">Open local authority directory</a>
                        <p>${primaryLocalAuthoritySource.agencyName()} | ${primaryLocalAuthoritySource.title()}</p>
                    </article>
                @endif

                @if (primaryRecordsLookupSource != null)
                    <article class="detail-card">
                        <h2>Look up septic records first</h2>
                        <p>Before trusting the low end, pull the existing permit, as-built, inspection, or management records tied to the property.</p>
                        <a class="button button--secondary" href="${primaryRecordsLookupSource.url()}" target="_blank" rel="noreferrer">Open records lookup</a>
                        <p>${primaryRecordsLookupSource.agencyName()} | ${primaryRecordsLookupSource.title()}</p>
                    </article>
                @endif
            </div>
        </section>
    @endif

    <section class="section section--tight">
        <div class="container detail-grid">
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
                            <td>${state.officialSourceIds().size()}</td>
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
                        </tbody>
                    </table>
                </div>
            </article>

            @if (stateRuleFacts != null && !stateRuleFacts.isEmpty())
                <article class="detail-card detail-card--full">
                    <h2>Source-backed rule facts for ${state.stateName()}</h2>
                    <div class="detail-grid">
                        @for (var fact : stateRuleFacts)
                            <article class="detail-card">
                                <span class="trust-box__label">${fact.label()}</span>
                                <h3>${fact.renderedValue()}</h3>
                                <p>${fact.note()}</p>
                                <div class="source-meta">
                                    @if (fact.confidenceLabel() != null && !fact.confidenceLabel().isBlank())
                                        <span>${fact.confidenceLabel()}</span>
                                    @endif
                                    @if (fact.sourceTrustLevel() != null && !fact.sourceTrustLevel().isBlank())
                                        <span>Trust: ${fact.sourceTrustLevel()}</span>
                                    @endif
                                    @if (fact.sourceStatus() != null && !fact.sourceStatus().isBlank())
                                        <span>Status: ${fact.sourceStatus()}</span>
                                    @endif
                                    @if (fact.lastVerifiedAt() != null && !fact.lastVerifiedAt().isBlank())
                                        <span>Last verified: ${fact.lastVerifiedAt()}</span>
                                    @endif
                                    @if (fact.effectiveDate() != null && !fact.effectiveDate().isBlank())
                                        <span>Effective: ${fact.effectiveDate()}</span>
                                    @endif
                                </div>
                                <p><strong>${fact.sourceAgency()}</strong></p>
                                <p><a href="${fact.sourceUrl()}" target="_blank" rel="noreferrer">${fact.sourceTitle()}</a></p>
                                @if (fact.sourceSection() != null && !fact.sourceSection().isBlank())
                                    <p>Source section: ${fact.sourceSection()}</p>
                                @endif
                            </article>
                        @endfor
                    </div>
                </article>
            @endif

            @if (state.localActionSteps() != null && !state.localActionSteps().isEmpty())
                <article class="detail-card">
                    <h2>Local action checklist</h2>
                    <ol class="detail-list detail-list--numbered">
                        @for (var step : state.localActionSteps())
                            <li>${step}</li>
                        @endfor
                    </ol>
                </article>
            @endif

            <article class="detail-card">
                <h2>Why this state is unique</h2>
                <p>${state.pageAngle()}</p>
            </article>

            <article class="detail-card">
                <h2>Permit path summary</h2>
                <p>${state.permitSummary()}</p>
            </article>

            <article class="detail-card">
                <h2>Site evaluation summary</h2>
                <p>${state.siteEvalSummary()}</p>
            </article>

            <article class="detail-card detail-card--full">
                <h2>Local override note</h2>
                <p>${state.localOverrideNote()} Override risk: ${state.countyOverrideRisk()}.</p>
            </article>
        </div>
    </section>

    @if (state.permitPathSteps() != null && !state.permitPathSteps().isEmpty())
        <section class="section section--tight">
            <div class="container detail-grid">
                <article class="detail-card">
                    <h2>Permit path steps</h2>
                    <ul class="detail-list">
                        @for (var step : state.permitPathSteps())
                            <li>${step}</li>
                        @endfor
                    </ul>
                </article>

                <article class="detail-card">
                    <h2>Rule highlights</h2>
                    <ul class="detail-list">
                        @for (var ruleHighlight : state.ruleHighlights())
                            <li>${ruleHighlight}</li>
                        @endfor
                    </ul>
                </article>
            </div>
        </section>
    @endif

    @if (state.whoToCallFirst() != null || (state.recordsToRequest() != null && !state.recordsToRequest().isEmpty()) || (state.lowEndRiskChecks() != null && !state.lowEndRiskChecks().isEmpty()))
        <section class="section section--tight">
            <div class="container detail-grid">
                @if (state.whoToCallFirst() != null)
                    <article class="detail-card">
                        <h2>Who to call first</h2>
                        <p>${state.whoToCallFirst()}</p>
                    </article>
                @endif

                @if (state.recordsToRequest() != null && !state.recordsToRequest().isEmpty())
                    <article class="detail-card">
                        <h2>Records to request first</h2>
                        <ul class="detail-list">
                            @for (var record : state.recordsToRequest())
                                <li>${record}</li>
                            @endfor
                        </ul>
                    </article>
                @endif

                @if (state.lowEndRiskChecks() != null && !state.lowEndRiskChecks().isEmpty())
                    <article class="detail-card detail-card--full">
                        <h2>What can kill the low end</h2>
                        <ul class="detail-list">
                            @for (var risk : state.lowEndRiskChecks())
                                <li>${risk}</li>
                            @endfor
                        </ul>
                    </article>
                @endif
            </div>
        </section>
    @endif

    @if (state.permitTimelineNote() != null || state.buyerInspectionTrigger() != null || state.maintenanceInspectionNote() != null || state.specialAreaNote() != null)
        <section class="section section--tight">
            <div class="container detail-grid">
                @if (state.permitTimelineNote() != null)
                    <article class="detail-card">
                        <h2>Permit timeline watch</h2>
                        <p>${state.permitTimelineNote()}</p>
                    </article>
                @endif

                @if (state.buyerInspectionTrigger() != null)
                    <article class="detail-card">
                        <h2>Buyer trigger</h2>
                        <p>${state.buyerInspectionTrigger()}</p>
                    </article>
                @endif

                @if (state.maintenanceInspectionNote() != null)
                    <article class="detail-card">
                        <h2>Maintenance / inspection note</h2>
                        <p>${state.maintenanceInspectionNote()}</p>
                    </article>
                @endif

                @if (state.specialAreaNote() != null)
                    <article class="detail-card">
                        <h2>Special state wrinkle</h2>
                        <p>${state.specialAreaNote()}</p>
                    </article>
                @endif
            </div>
        </section>
    @endif

    @if (!localAuthoritySources.isEmpty() || !recordsLookupSources.isEmpty())
        <section class="section section--tight">
            <div class="container detail-grid">
                @if (!localAuthoritySources.isEmpty())
                    <article class="detail-card">
                        <h2>Verify locally</h2>
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
                    </article>
                @endif

                @if (!recordsLookupSources.isEmpty())
                    <article class="detail-card">
                        <h2>Records and lookup links</h2>
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
                    </article>
                @endif
            </div>
        </section>
    @endif

    @if (coreStateComparisonRows != null && !coreStateComparisonRows.isEmpty())
        <section class="section section--tight">
            <div class="container">
                <details class="detail-card detail-card--full disclosure-card">
                    <summary>How the core six launch states differ</summary>
                    <div class="disclosure-card__body">
                        <div class="facts-table-wrap">
                            <table class="facts-table comparison-table">
                                <thead>
                                <tr>
                                    <th>State</th>
                                    <th>Call first</th>
                                    <th>Pull first</th>
                                    <th>Low-end killer</th>
                                    <th>Best next page</th>
                                </tr>
                                </thead>
                                <tbody>
                                @for (var row : coreStateComparisonRows)
                                    <tr class="${row.active() ? "comparison-table__row--active" : ""}">
                                        <td>
                                            <strong>${row.stateName()}</strong>
                                            @if (row.active())
                                                <div class="comparison-table__badge">You are here</div>
                                            @endif
                                        </td>
                                        <td data-label="Call first">${row.firstCall()}</td>
                                        <td data-label="Pull first">${row.firstRecord()}</td>
                                        <td data-label="Low-end killer">${row.lowEndTrigger()}</td>
                                        <td data-label="Best next page"><a href="${row.nextPagePath()}" data-track-click="nav" data-track-source-context="state_guide_core_comparison_next" data-track-target-type="next_page">${row.nextPageTitle()}</a></td>
                                    </tr>
                                @endfor
                                </tbody>
                            </table>
                        </div>
                        <div class="comparison-cards">
                            @for (var row : coreStateComparisonRows)
                                <article class="comparison-card ${row.active() ? "comparison-card--active" : ""}">
                                    <div class="comparison-card__header">
                                        <strong>${row.stateName()}</strong>
                                        @if (row.active())
                                            <span class="comparison-table__badge">You are here</span>
                                        @endif
                                    </div>
                                    <dl class="comparison-card__list">
                                        <div>
                                            <dt>Call first</dt>
                                            <dd>${row.firstCall()}</dd>
                                        </div>
                                        <div>
                                            <dt>Pull first</dt>
                                            <dd>${row.firstRecord()}</dd>
                                        </div>
                                        <div>
                                            <dt>Low-end killer</dt>
                                            <dd>${row.lowEndTrigger()}</dd>
                                        </div>
                                        <div>
                                            <dt>Best next page</dt>
                                            <dd><a href="${row.nextPagePath()}" data-track-click="nav" data-track-source-context="state_guide_core_comparison_next" data-track-target-type="next_page">${row.nextPageTitle()}</a></dd>
                                        </div>
                                    </dl>
                                </article>
                            @endfor
                        </div>
                    </div>
                </details>
            </div>
        </section>
    @endif

    @if (guideFaqs != null && !guideFaqs.isEmpty())
        <section class="section section--muted" id="faq">
            <div class="container">
                <details class="detail-card detail-card--full disclosure-card">
                    <summary>${state.stateName()} homeowner questions worth clearing up before you request quotes</summary>
                    <div class="detail-grid disclosure-card__body">
                        @for (var faq : guideFaqs)
                            <article class="detail-card">
                                <h3>${faq.question()}</h3>
                                <p>${faq.answer()}</p>
                            </article>
                        @endfor
                    </div>
                </details>
            </div>
        </section>
    @endif

    <section class="section section--tight">
        <div class="container">
            <article class="trust-box trust-box--large">
                <span class="trust-box__label">Ready for real quotes?</span>
                <h2>Use the estimate first, or skip straight to the short quote form.</h2>
                <p>${calculatorCtaNote} If you already know the state and job type, you can move straight into the short quote request flow.</p>
                <div class="hero__actions">
                    <a class="button button--primary" href="/septic-system-cost-calculator/?state=${state.stateCode()}" data-track-click="nav" data-track-source-context="state_guide_ready_calculator" data-track-target-type="calculator">${calculatorCtaLabel}</a>
                    <a class="button button--secondary" href="/septic-system-cost-calculator/?state=${state.stateCode()}&quoteMode=true#quote-request" data-track-click="nav" data-track-source-context="state_guide_ready_quote" data-track-target-type="quote_form">Get matched with local septic pros</a>
                </div>
            </article>
        </div>
    </section>

    <section class="section" id="sources">
        <div class="container content-grid">
            <details class="trust-box trust-box--large disclosure-card">
                <summary>Official sources for ${state.stateName()}</summary>
                <ul class="source-list disclosure-card__body">
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
            </details>

            @if (!stateMoneyPages.isEmpty())
                <details class="detail-card disclosure-card">
                    <summary>Next high-intent pages in ${state.stateName()}</summary>
                    <ul class="detail-list disclosure-card__body">
                        @for (var stateMoneyPage : stateMoneyPages)
                            <li><a href="${stateMoneyPage.path(state.slug())}" data-track-click="nav" data-track-source-context="state_guide_next_high_intent" data-track-target-type="state_money_page">${stateMoneyPage.title()}</a></li>
                        @endfor
                        <li><a href="/septic-system-cost-calculator/?state=${state.stateCode()}" data-track-click="nav" data-track-source-context="state_guide_next_high_intent_calculator" data-track-target-type="calculator">Main septic cost calculator</a></li>
                    </ul>
                </details>
            @endif

        </div>
    </section>

    <div class="sticky-mobile-cta-spacer" aria-hidden="true"></div>
    <section class="sticky-mobile-cta sticky-mobile-cta--dual" aria-label="Run estimate or quote">
        <div class="sticky-mobile-cta__inner">
            <a class="button button--secondary" href="/septic-system-cost-calculator/?state=${state.stateCode()}" data-track-click="nav" data-track-source-context="state_guide_sticky_calculator" data-track-target-type="calculator">${calculatorCtaLabel}</a>
            <a class="button button--primary" href="/septic-system-cost-calculator/?state=${state.stateCode()}&quoteMode=true#quote-request" data-track-click="nav" data-track-source-context="state_guide_sticky_quote" data-track-target-type="quote_form">Short quote form</a>
        </div>
    </section>
`)
