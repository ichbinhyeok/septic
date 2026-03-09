@import com.example.septic.data.model.SourceRecord
@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.PageMeta
@param PageMeta page
@param StateProfile state
@param java.util.List<SourceRecord> sources
@param java.util.List<SourceRecord> localAuthoritySources = java.util.List.of()
@param java.util.List<SourceRecord> recordsLookupSources = java.util.List.of()
@param SourceRecord primaryLocalAuthoritySource = null
@param SourceRecord primaryRecordsLookupSource = null
@param java.util.List<com.example.septic.data.model.StateMoneyPage> stateMoneyPages = java.util.List.of()
@param java.util.List<com.example.septic.data.model.FaqBlock> guideFaqs = java.util.List.of()
@param String calculatorCtaLabel = "Open the main calculator"
@param String calculatorCtaNote = "Use your state and project assumptions first, then verify locally."
@param com.example.septic.web.StatePlanningSnapshot planningSnapshot = null

@template.layouts.app(page = page, content = @`
    <section class="section section--heroish">
        <div class="container state-hero">
            <div>
                <span class="eyebrow">${state.stateCode()} state guide</span>
                <h1>${state.stateName()} septic planning guide</h1>
                <p class="lede">${state.ruleSummaryPlainEnglish()}</p>
            </div>
            <div class="hero-sidecard">
                <span class="pill">${state.launchTier()}</span>
                <strong>${state.agencyName()}</strong>
                <strong>${state.ruleType()}</strong>
                <p>Confidence: ${state.confidenceScore()} | Last verified: ${state.lastVerifiedAt()}</p>
                <a class="button button--secondary" href="/septic-system-cost-calculator/?state=${state.stateCode()}">${calculatorCtaLabel}</a>
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
                                </li>
                            @endfor
                        </ul>
                    </article>
                @endif
            </div>
        </section>
    @endif

    @if (guideFaqs != null && !guideFaqs.isEmpty())
        <section class="section section--muted" id="faq">
            <div class="container">
                <div class="section-heading">
                    <span class="eyebrow">FAQ</span>
                    <h2>${state.stateName()} homeowner questions worth clearing up before you request quotes.</h2>
                </div>

                <div class="detail-grid">
                    @for (var faq : guideFaqs)
                        <article class="detail-card">
                            <h3>${faq.question()}</h3>
                            <p>${faq.answer()}</p>
                        </article>
                    @endfor
                </div>
            </div>
        </section>
    @endif

    <section class="section">
        <div class="container content-grid">
            <article class="trust-box trust-box--large">
                <span class="trust-box__label">Official sources</span>
                <ul class="source-list">
                    @for (var source : sources)
                        <li>
                            <strong>${source.agencyName()}</strong>
                            <a href="${source.url()}" target="_blank" rel="noreferrer">${source.title()}</a>
                            <span>Last verified: ${source.lastVerifiedAt()}</span>
                        </li>
                    @endfor
                </ul>
            </article>

            @if (!stateMoneyPages.isEmpty())
                <article class="detail-card">
                    <h2>Next high-intent pages</h2>
                    <ul class="detail-list">
                        @for (var stateMoneyPage : stateMoneyPages)
                            <li><a href="${stateMoneyPage.path(state.slug())}">${stateMoneyPage.title()}</a></li>
                        @endfor
                        <li><a href="/septic-system-cost-calculator/?state=${state.stateCode()}">Main septic cost calculator</a></li>
                    </ul>
                </article>
            @endif

            <article class="detail-card">
                <h2>Still under review</h2>
                <ul class="detail-list">
                    @for (var item : state.itemsNeedingVerification())
                        <li>${item}</li>
                    @endfor
                </ul>
            </article>
        </div>
    </section>
`)
