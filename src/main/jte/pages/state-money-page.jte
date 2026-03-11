@import com.example.septic.data.model.SourceRecord
@import com.example.septic.data.model.StateMoneyPage
@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.PageMeta
@import com.example.septic.web.EditorialProfile
@param PageMeta page
@param StateMoneyPage stateMoneyPage
@param StateProfile state
@param java.util.List<SourceRecord> sources
@param java.util.List<SourceRecord> localAuthoritySources = java.util.List.of()
@param java.util.List<SourceRecord> recordsLookupSources = java.util.List.of()
@param SourceRecord primaryLocalAuthoritySource = null
@param SourceRecord primaryRecordsLookupSource = null
@param String calculatorCtaLabel = "Open the main calculator"
@param String calculatorCtaNote = "Use your state and project assumptions first, then verify locally."
@param com.example.septic.web.StatePlanningSnapshot planningSnapshot = null
@param java.util.List<com.example.septic.web.PageLink> internalLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> featuredInternalLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> secondaryInternalLinks = java.util.List.of()
@param EditorialProfile editorialPreparedBy = null
@param EditorialProfile editorialReviewedBy = null
@param String editorialReviewedAgainst = ""
@param String editorialLastReviewedAt = ""
@param String editorialNote = ""

@template.layouts.app(page = page, content = @`
    <section class="section section--heroish">
        <div class="container state-hero">
            <div>
                <span class="eyebrow">${state.stateCode()} homeowner guide</span>
                <h1>${stateMoneyPage.title()}</h1>
                <div class="hero__actions">
                    <a class="button button--primary" href="/septic-system-cost-calculator/?state=${state.stateCode()}&projectType=${stateMoneyPage.calculatorProjectType()}" data-track-click="nav" data-track-source-context="state_money_primary_calculator" data-track-target-type="calculator">${calculatorCtaLabel}</a>
                    <a class="button button--secondary" href="/septic-system-cost-calculator/?state=${state.stateCode()}&projectType=${stateMoneyPage.calculatorProjectType()}&quoteMode=true#quote-request" data-track-click="nav" data-track-source-context="state_money_primary_quote" data-track-target-type="quote_form">Get matched with local septic pros</a>
                    <a class="button button--secondary" href="/septic-system-cost-calculator/${state.slug()}/" data-track-click="nav" data-track-source-context="state_money_back_to_guide" data-track-target-type="state_guide">Open the ${state.stateName()} guide</a>
                </div>
                <p class="lede">${stateMoneyPage.introCopy()}</p>
                <p>${calculatorCtaNote}</p>
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
                <p>This page stays narrow on purpose. Use it when this exact cost lane is already the real question and the broader state guide would slow the next decision down.</p>
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
                <a href="#sources">Sources</a>
                <a href="#faq">FAQ</a>
            </div>

            <div class="intent-rail">
                <article class="intent-card intent-card--primary">
                    <span class="intent-card__label">Run the state estimate</span>
                    <h2>${calculatorCtaLabel}</h2>
                    <p>${calculatorCtaNote}</p>
                    <a class="button button--primary" href="/septic-system-cost-calculator/?state=${state.stateCode()}&projectType=${stateMoneyPage.calculatorProjectType()}" data-track-click="nav" data-track-source-context="state_money_action_rail_calculator" data-track-target-type="calculator">Run the estimate</a>
                </article>

                <article class="intent-card">
                    <span class="intent-card__label">Return to the broader state guide</span>
                    <h2>Open the ${state.stateName()} guide</h2>
                    <p>Use the broader guide when you still need the state-level rule style, local office path, and low-end risk before committing to this one intent lane.</p>
                    <a class="button button--secondary" href="/septic-system-cost-calculator/${state.slug()}/" data-track-click="nav" data-track-source-context="state_money_action_rail_guide" data-track-target-type="state_guide">Open the guide</a>
                </article>

                <article class="intent-card">
                    @if (primaryRecordsLookupSource != null)
                        <span class="intent-card__label">Pull the file first</span>
                        <h2>Open records before you trust the price story</h2>
                        <p>Use the official records path when you still need the permit, as-built, inspection, or maintenance file before moving into quote mode.</p>
                        <a class="button button--secondary" href="${primaryRecordsLookupSource.url()}" target="_blank" rel="noreferrer">Open records lookup</a>
                    @elseif (primaryLocalAuthoritySource != null)
                        <span class="intent-card__label">Verify the next office</span>
                        <h2>Confirm the local authority before you schedule work</h2>
                        <p>Use the local office path when you still need the real permit desk, reviewing authority, or delegated county office before trusting the low end.</p>
                        <a class="button button--secondary" href="${primaryLocalAuthoritySource.url()}" target="_blank" rel="noreferrer">Open local authority source</a>
                    @elseif (!featuredInternalLinks.isEmpty())
                        <span class="intent-card__label">Most likely next move</span>
                        <h2>${featuredInternalLinks.get(0).title()}</h2>
                        <p>${featuredInternalLinks.get(0).note()}</p>
                        <a class="button button--secondary" href="${featuredInternalLinks.get(0).path()}" data-track-click="nav" data-track-source-context="state_money_action_rail_internal" data-track-target-type="related_internal">Open next step</a>
                    @endif
                </article>
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
                        <h2>${stateMoneyPage.localAuthorityHeading()}</h2>
                        <p>Use the local office first when you want to move from a planning page into an actual permit or records workflow.</p>
                        <a class="button button--secondary" href="${primaryLocalAuthoritySource.url()}" target="_blank" rel="noreferrer">Open local authority source</a>
                        <p>${primaryLocalAuthoritySource.agencyName()} | ${primaryLocalAuthoritySource.title()}</p>
                    </article>
                @endif

                @if (primaryRecordsLookupSource != null)
                    <article class="detail-card">
                        <h2>${stateMoneyPage.recordsLookupHeading()}</h2>
                        <p>Use the existing record trail to confirm whether this property still fits the low end before you move into quote mode.</p>
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
    </section>

    <section class="section" id="sources">
        <div class="container detail-grid">
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
                <p>${calculatorCtaNote} The calculator result already shows the likely tank band, system class, cost range, and state-specific rule context. If you already know the project type, you can also skip straight to the short quote form.</p>
                <div class="hero__actions">
                    <a class="button button--primary" href="/septic-system-cost-calculator/?state=${state.stateCode()}&projectType=${stateMoneyPage.calculatorProjectType()}" data-track-click="nav" data-track-source-context="state_money_next_best_calculator" data-track-target-type="calculator">Run the estimate</a>
                    <a class="button button--secondary" href="/septic-system-cost-calculator/?state=${state.stateCode()}&projectType=${stateMoneyPage.calculatorProjectType()}&quoteMode=true#quote-request" data-track-click="nav" data-track-source-context="state_money_next_best_quote" data-track-target-type="quote_form">Get matched with local septic pros</a>
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
            <a class="button button--primary" href="/septic-system-cost-calculator/?state=${state.stateCode()}&projectType=${stateMoneyPage.calculatorProjectType()}" data-track-click="nav" data-track-source-context="state_money_sticky_calculator" data-track-target-type="calculator">Run state estimate</a>
        </div>
    </section>
`)
