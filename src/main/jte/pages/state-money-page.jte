@import com.example.septic.data.model.SourceRecord
@import com.example.septic.data.model.StateMoneyPage
@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.PageMeta
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

@template.layouts.app(page = page, content = @`
    <section class="section section--heroish">
        <div class="container state-hero">
            <div>
                <span class="eyebrow">${state.stateCode()} money page</span>
                <h1>${stateMoneyPage.title()}</h1>
                <p class="lede">${stateMoneyPage.introCopy()}</p>
                <div class="hero__actions">
                    <a class="button button--primary" href="/septic-system-cost-calculator/?state=${state.stateCode()}&projectType=${stateMoneyPage.calculatorProjectType()}">${calculatorCtaLabel}</a>
                    <a class="button button--secondary" href="/septic-system-cost-calculator/${state.slug()}/">Open the ${state.stateName()} guide</a>
                </div>
                <p>${calculatorCtaNote}</p>
            </div>

            <article class="hero-sidecard">
                <span class="pill">${stateMoneyPage.contentSlug()}</span>
                <strong>${state.agencyName()}</strong>
                <strong>${state.ruleType()}</strong>
                <p>Last verified: ${state.lastVerifiedAt()}</p>
                <p>${stateMoneyPage.uniqueAngle()}</p>
            </article>
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
                        <p>Use the local office first when you want to move from a planning page into an actual permit or records workflow.</p>
                        <a class="button button--secondary" href="${primaryLocalAuthoritySource.url()}" target="_blank" rel="noreferrer">Open local authority directory</a>
                        <p>${primaryLocalAuthoritySource.agencyName()} | ${primaryLocalAuthoritySource.title()}</p>
                    </article>
                @endif

                @if (primaryRecordsLookupSource != null)
                    <article class="detail-card">
                        <h2>Look up septic records first</h2>
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
                    <h2>Local action checklist</h2>
                    <ol class="detail-list detail-list--numbered">
                        @for (var step : state.localActionSteps())
                            <li>${step}</li>
                        @endfor
                    </ol>
                </article>
            @endif

            <article class="detail-card">
                <h2>What changes this page in ${state.stateName()}</h2>
                <p>${stateMoneyPage.uniqueAngle()}</p>
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
        </div>
    </section>

    <section class="section">
        <div class="container detail-grid">
            @if (state.whoToCallFirst() != null)
                <article class="detail-card">
                    <h2>Who to call first</h2>
                    <p>${state.whoToCallFirst()}</p>
                </article>
            @endif

            @if (state.recordsToRequest() != null && !state.recordsToRequest().isEmpty())
                <article class="detail-card">
                    <h2>Records to request</h2>
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
                        <li>${state.localOverrideNote()}</li>
                    </ul>
                </article>
            @endif

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
                <article class="detail-card detail-card--full">
                    <h2>Special state wrinkle</h2>
                    <p>${state.specialAreaNote()}</p>
                </article>
            @endif

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

            <article class="trust-box">
                <span class="trust-box__label">Official-source context</span>
                <p><strong>${state.agencyName()}</strong> and related official materials support this page. Final design, permit path, and approval still need local verification.</p>
                <ul class="source-list source-list--compact">
                    @for (var source : sources)
                        <li>
                            <strong>${source.agencyName()}</strong>
                            <a href="${source.url()}" target="_blank" rel="noreferrer">${source.title()}</a>
                            <span>Last verified: ${source.lastVerifiedAt()}</span>
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
                <p>${calculatorCtaNote} The calculator result already shows the likely tank band, system class, cost range, and state-specific rule context.</p>
                <a href="/septic-system-cost-calculator/?state=${state.stateCode()}&projectType=${stateMoneyPage.calculatorProjectType()}">Run the estimate</a>
            </article>

            <article class="detail-card">
                <h2>Related links</h2>
                <ul class="detail-list">
                    @for (var target : stateMoneyPage.internalLinkTargets())
                        <li><a href="${target}">${target}</a></li>
                    @endfor
                </ul>
            </article>
        </div>
    </section>
`)
