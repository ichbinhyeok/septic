@import com.example.septic.data.model.FaqBlock
@import com.example.septic.data.model.StateProfile
@import com.example.septic.service.AccessDifficulty
@import com.example.septic.service.DrainfieldEstimatorResult
@import com.example.septic.service.SoilPercStatus
@import com.example.septic.service.TimelinePreference
@import com.example.septic.web.DrainfieldEstimatorForm
@import com.example.septic.web.PageMeta
@param PageMeta page
@param java.util.List<StateProfile> states
@param DrainfieldEstimatorForm drainfieldEstimatorForm
@param DrainfieldEstimatorResult result = null
@param StateProfile selectedState = null
@param java.util.List<FaqBlock> drainfieldFaqs = java.util.List.of()
@param java.util.List<com.example.septic.web.StateMoneyPageLink> drainfieldStateLinks = java.util.List.of()
@param String selectedDrainfieldPagePath = ""
@param String selectedDrainfieldPageTitle = ""
@param SoilPercStatus[] soilStatuses
@param AccessDifficulty[] accessLevels
@param TimelinePreference[] timelines

@template.layouts.app(page = page, content = @`
    <section class="section section--heroish">
        <div class="container section-heading">
            <span class="eyebrow">Drain field estimator</span>
            <h1>Estimate drain field replacement risk, redesign pressure, and likely cost swing.</h1>
            <p class="lede">Use this when the field, not the tank, looks like the main problem. The output is meant to answer whether the job may still stay conventional, whether the replacement area is the real blocker, and how far the quote can widen before you call contractors.</p>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container">
            <div class="section-jump-strip" aria-label="Jump between sections">
                <span class="section-jump-strip__label">Jump between sections</span>
                <a href="#how-to-use">How to use</a>
                <a href="#wideners">What widens it</a>
                @if (!drainfieldStateLinks.isEmpty())
                    <a href="#state-lanes">State lanes</a>
                @endif
                <a href="#faq">FAQ</a>
            </div>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container calculator-layout">
            <form class="calculator-form" id="drain-field-estimator-form" name="drain-field-estimator-form" method="post" action="/drain-field-estimator/">
                <div class="field-grid">
                    <label class="field">
                        <span>State</span>
                        <select name="stateCode">
                            @for (var state : states)
                                <option value="${state.stateCode()}" selected="${state.stateCode().equals(drainfieldEstimatorForm.getStateCode())}">${state.stateName()}</option>
                            @endfor
                        </select>
                    </label>

                    <label class="field">
                        <span>Bedrooms served</span>
                        <input type="number" min="1" max="10" name="bedrooms" value="${drainfieldEstimatorForm.getBedrooms()}">
                    </label>

                    <label class="field">
                        <span>Soil / perc status</span>
                        <select name="soilPercStatus">
                            @for (var soilStatus : soilStatuses)
                                <option value="${soilStatus.value()}" selected="${soilStatus.value().equals(drainfieldEstimatorForm.getSoilPercStatus())}">${soilStatus.label()}</option>
                            @endfor
                        </select>
                    </label>

                    <label class="field">
                        <span>Access difficulty</span>
                        <select name="accessDifficulty">
                            @for (var accessLevel : accessLevels)
                                <option value="${accessLevel.value()}" selected="${accessLevel.value().equals(drainfieldEstimatorForm.getAccessDifficulty())}">${accessLevel.label()}</option>
                            @endfor
                        </select>
                    </label>

                    <label class="field">
                        <span>Timeline</span>
                        <select name="timeline">
                            @for (var timeline : timelines)
                                <option value="${timeline.value()}" selected="${timeline.value().equals(drainfieldEstimatorForm.getTimeline())}">${timeline.label()}</option>
                            @endfor
                        </select>
                    </label>

                    <label class="checkbox">
                        <input type="checkbox" name="wetGroundOrSurfacing" checked="${drainfieldEstimatorForm.isWetGroundOrSurfacing()}">
                        <span>Wet spots, surfacing, or odor near the field</span>
                    </label>

                    <label class="checkbox">
                        <input type="checkbox" name="noClearReplacementArea" checked="${drainfieldEstimatorForm.isNoClearReplacementArea()}">
                        <span>No clear replacement area or reserve area identified</span>
                    </label>
                </div>

                <div class="form-actions">
                    <button class="button button--primary" type="submit">Show drain field estimate</button>
                    <p>This is a planning tool for layout risk and cost swing, not an engineered drain field design.</p>
                </div>
            </form>

            <div class="calculator-output">
                @if (result != null)
                    <section class="result-panel"
                             data-ga-event="calculator_submit"
                             data-ga-param-estimator-type="drain_field_estimator"
                             data-ga-param-state-code="${drainfieldEstimatorForm.getStateCode()}">
                        <div class="result-panel__header">
                            <div>
                                <span class="eyebrow">Drain field result</span>
                                <h2>${result.estimate().stateName()} drain field planning range</h2>
                            </div>
                            <span class="pill">${result.estimate().confidenceLabel()} confidence</span>
                        </div>

                        <div class="result-grid">
                            <article class="metric-card metric-card--accent">
                                <span class="metric-card__label">Likely field path</span>
                                <strong>${result.fieldOutlookLabel()}</strong>
                                <p>${result.fieldRiskNote()}</p>
                            </article>

                            <article class="metric-card">
                                <span class="metric-card__label">Planning cost range</span>
                                <strong>${result.estimate().formattedTotalCostRange()}</strong>
                                <p>Midpoint about ${result.estimate().formattedTotalCostMid()}. ${result.costSwingNote()}</p>
                            </article>

                            <article class="metric-card">
                                <span class="metric-card__label">Likely system class</span>
                                <strong>${result.estimate().likelySystemClass()}</strong>
                                <p>${result.redesignRiskLabel()}</p>
                            </article>

                            <article class="metric-card">
                                <span class="metric-card__label">Replacement-area note</span>
                                <strong>Check the layout first</strong>
                                <p>${result.replacementAreaNote()}</p>
                            </article>
                        </div>

                        <div class="detail-grid">
                            <article class="detail-card">
                                <h3>What shaped the range</h3>
                                <ul class="detail-list">
                                    @for (var driver : result.estimate().costDrivers())
                                        <li>${driver}</li>
                                    @endfor
                                </ul>
                            </article>

                            <article class="detail-card">
                                <h3>Decision signals</h3>
                                <ul class="detail-list">
                                    @for (var signal : result.decisionSignals())
                                        <li>${signal}</li>
                                    @endfor
                                </ul>
                            </article>

                            <article class="detail-card">
                                <h3>What to do next</h3>
                                <ol class="detail-list detail-list--numbered">
                                    @for (var step : result.nextSteps())
                                        <li>${step}</li>
                                    @endfor
                                </ol>
                            </article>

                            <article class="detail-card">
                                <h3>Official-source context</h3>
                                <details class="disclosure-card">
                                    <summary>View source set</summary>
                                    <ul class="detail-list disclosure-card__body">
                                        @for (var source : result.estimate().sourceLabels())
                                            <li>${source}</li>
                                        @endfor
                                    </ul>
                                </details>
                            </article>
                        </div>

                        <article class="trust-box">
                            <span class="trust-box__label">Next best action</span>
                            <p>If the field is clearly the bottleneck, keep the drain field page open while you compare the full state estimate and the local file trail.</p>
                            <div class="hero__actions">
                                <a class="button button--primary" href="/septic-system-cost-calculator/?state=${result.estimate().stateCode()}&projectType=drainfield_replacement">Open the full cost estimator</a>
                                @if (selectedState != null)
                                    <a class="button button--secondary" href="/septic-system-cost-calculator/${selectedState.slug()}/">Open ${selectedState.stateName()} guide</a>
                                @endif
                                @if (selectedDrainfieldPagePath != null && !selectedDrainfieldPagePath.isBlank())
                                    <a class="button button--secondary" href="${selectedDrainfieldPagePath}">${selectedDrainfieldPageTitle}</a>
                                @endif
                            </div>
                        </article>
                    </section>
                @else
                    <article class="placeholder-panel">
                        <span class="eyebrow">Result preview</span>
                        <h2>Use this when the drain field may be the real cost swing.</h2>
                        <p>You will get a field-path outlook, redesign pressure, and the main records or site questions to settle before you trust a contractor's lower-end number.</p>
                    </article>
                @endif
            </div>
        </div>
    </section>

    <section class="section section--tight" id="how-to-use">
        <div class="container card-grid">
            <article class="feature-card feature-card--primary">
                <span class="pill">Use it first</span>
                <h3>When the field is the real question</h3>
                <p>This tool is for cases where the tank is not the headline. Use it when wetness, surfacing, reserve-area uncertainty, or old field-layout doubts are driving the quote swing.</p>
            </article>
            <article class="feature-card">
                <span class="pill">Do not over-read it</span>
                <h3>It is not a layout design</h3>
                <p>The result is meant to frame whether the job may still stay conventional or whether the field story is drifting into redesign, reserve-area, or alternative-system territory.</p>
            </article>
            <article class="feature-card">
                <span class="pill">Best workflow</span>
                <h3>Estimate, then verify the file</h3>
                <p>The best sequence is field estimate, permit or as-built pull, site or perc confirmation, then contractor quotes. That keeps the low end honest before you convert.</p>
            </article>
        </div>
    </section>

    <section class="section section--muted" id="wideners">
        <div class="container">
            <div class="section-heading">
                <span class="eyebrow">What widens it</span>
                <h2>Drain field jobs stop looking simple when one of four constraints is already visible.</h2>
            </div>

            <div class="card-grid">
                <article class="feature-card">
                    <h3>Replacement area is unclear</h3>
                    <p>If no reserve or replacement area is obvious, the job can stop being a field-only quote and become a parcel-layout problem.</p>
                </article>
                <article class="feature-card">
                    <h3>Soil and perc signal is weak</h3>
                    <p>Unknown, poor, or failed soil signals do more than move a small testing fee. They can change the whole field path and the likely system class.</p>
                </article>
                <article class="feature-card">
                    <h3>Wetness is already visible</h3>
                    <p>Surface moisture, seepage, persistent odor, or soggy ground near the field often means the practical issue is larger than replacing a few trenches.</p>
                </article>
                <article class="feature-card">
                    <h3>Excavation and restoration are ugly</h3>
                    <p>Hard access, slope, landscaping, trees, walls, or tight clearances can make a field job expensive even before redesign pressure is settled.</p>
                </article>
            </div>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container">
            <div class="section-heading">
                <span class="eyebrow">Deep dives</span>
                <h2>Open the page that matches the actual field blocker behind the estimate.</h2>
            </div>

            <div class="card-grid">
                <article class="feature-card">
                    <h3>Failed Perc Test for Septic</h3>
                    <p>Use this when the lot already has a weak or failed perc result and the real question is whether the project can still stay conventional.</p>
                    <a href="/failed-perc-test-septic/">Open failed perc guide</a>
                </article>
                <article class="feature-card">
                    <h3>Septic Replacement Area Guide</h3>
                    <p>Use this when reserve area, replacement footprint, or layout viability is the main reason the field quote no longer looks simple.</p>
                    <a href="/septic-replacement-area/">Open replacement-area guide</a>
                </article>
                <article class="feature-card">
                    <h3>Wet Yard Over Septic Drain Field</h3>
                    <p>Use this when soggy ground, seepage, or odor near the field is already visible and you need to pressure-test failure scope early.</p>
                    <a href="/wet-yard-over-septic-drain-field/">Open wet-yard guide</a>
                </article>
            </div>
        </div>
    </section>

    @if (selectedState != null)
        <section class="section section--tight">
            <div class="container detail-grid">
                <article class="detail-card detail-card--full">
                    <h2>${selectedState.stateName()} field-replacement snapshot</h2>
                    <div class="result-grid">
                        <article class="metric-card">
                            <span class="metric-card__label">Site-evaluation angle</span>
                            <strong>${selectedState.ruleType()}</strong>
                            <p>${selectedState.siteEvalSummary()}</p>
                        </article>
                        <article class="metric-card">
                            <span class="metric-card__label">Permit path summary</span>
                            <strong>Updated ${selectedState.lastVerifiedAt()}</strong>
                            <p>${selectedState.permitSummary()}</p>
                        </article>
                        <article class="metric-card">
                            <span class="metric-card__label">Who to call first</span>
                            <strong>${selectedState.agencyName()}</strong>
                            <p>${selectedState.whoToCallFirst()}</p>
                        </article>
                        <article class="metric-card">
                            <span class="metric-card__label">Why the range stays wide</span>
                            <strong>${selectedState.countyOverrideRisk()} override risk</strong>
                            <p>${selectedState.localOverrideNote()}</p>
                        </article>
                    </div>
                </article>

                <article class="trust-box trust-box--large">
                    <span class="trust-box__label">Field-specific next move</span>
                    <h2>Use the file and the state workflow before you compare field-only quotes.</h2>
                    <p>Drain field jobs get expensive when the replacement area, the field layout, or the site story is thinner than the contractor summary. Pull the record trail first, then compare it against the estimate.</p>
                    <div class="hero__actions">
                        @if (selectedDrainfieldPagePath != null && !selectedDrainfieldPagePath.isBlank())
                            <a class="button button--primary" href="${selectedDrainfieldPagePath}">${selectedDrainfieldPageTitle}</a>
                        @endif
                        <a class="button button--secondary" href="/drain-field-replacement-cost/">Open the national drain field guide</a>
                        <a class="button button--secondary" href="/septic-system-cost-calculator/${selectedState.slug()}/">Open ${selectedState.stateName()} guide</a>
                    </div>
                </article>
            </div>
        </section>
    @endif

    @if (!drainfieldStateLinks.isEmpty())
        <section class="section section--tight" id="state-lanes">
            <div class="container">
                <article class="trust-box trust-box--large">
                    <span class="trust-box__label">Live state lanes</span>
                    <h2>Use the strongest state-specific drain field pages when the permit path or file quality changes the answer.</h2>
                    <p>These pages carry the state workflow, the official-source trail, and the local records angle behind the broader estimator output.</p>
                </article>

                <div class="card-grid">
                    @for (var stateLink : drainfieldStateLinks)
                        <article class="feature-card">
                            <h3>${stateLink.title()}</h3>
                            <p>${stateLink.stateName()} drain field lane with state workflow context and source-backed file risk.</p>
                            <a href="${stateLink.path()}">${stateLink.title()}</a>
                        </article>
                    @endfor
                </div>
            </div>
        </section>
    @endif

    @if (!drainfieldFaqs.isEmpty())
        <section class="section" id="faq">
            <div class="container">
                <details class="detail-card detail-card--full disclosure-card">
                    <summary>Questions this drain field page should answer before the user asks for quotes</summary>
                    <div class="detail-grid disclosure-card__body">
                        @for (var faq : drainfieldFaqs)
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

    <section class="section">
        <div class="container content-grid">
            <details class="trust-box trust-box--large disclosure-card">
                <summary>Related high-intent pages</summary>
                <ul class="detail-list disclosure-card__body">
                    <li><a href="/drain-field-replacement-cost/">Drain field replacement cost</a></li>
                    <li><a href="/failed-perc-test-septic/">Failed perc test for septic</a></li>
                    <li><a href="/septic-replacement-area/">Septic replacement area guide</a></li>
                    <li><a href="/wet-yard-over-septic-drain-field/">Wet yard over septic drain field</a></li>
                    <li><a href="/septic-replacement-cost/">Septic replacement cost</a></li>
                    <li><a href="/perc-test-cost/">Perc test cost</a></li>
                    <li><a href="/septic-system-cost-calculator/">Main septic cost calculator</a></li>
                </ul>
            </details>

            <article class="detail-card">
                <h2>When this tool is enough</h2>
                <p>Use it when the question is mainly whether the field still looks conventional, whether the lot has room to replace it, and how much the field can widen the quote.</p>
            </article>

            <article class="detail-card">
                <h2>When to switch tools</h2>
                <p>Move to the full cost estimator when the field story becomes a whole-system replacement question or when you need the broader quote range with timeline and state cost anchors included.</p>
            </article>
        </div>
    </section>
`)
