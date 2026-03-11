@import com.example.septic.data.model.StateProfile
@import com.example.septic.data.model.FaqBlock
@import com.example.septic.service.OccupancyProfile
@import com.example.septic.service.TankSizeEstimatorResult
@import com.example.septic.web.PageMeta
@import com.example.septic.web.TankSizeForm
@param PageMeta page
@param java.util.List<StateProfile> states
@param TankSizeForm tankSizeForm
@param TankSizeEstimatorResult result = null
@param StateProfile selectedState = null
@param java.util.List<FaqBlock> tankSizeFaqs = java.util.List.of()
@param java.util.List<com.example.septic.web.StateRuleFactView> stateRuleFacts = java.util.List.of()
@param OccupancyProfile[] occupancyProfiles

@template.layouts.app(page = page, content = @`
    <section class="section section--heroish">
        <div class="container section-heading">
            <span class="eyebrow">Tank size estimator</span>
            <h1>Estimate likely septic tank size by state, bedroom count, and household use.</h1>
            <p class="lede">Use this page when the first question is tank size, not total project cost. It turns bedrooms, occupancy, disposal use, and state-specific sizing signals into a likely minimum, a conservative range, and a rough pumping cadence.</p>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container calculator-layout">
            <form class="calculator-form" id="tank-size-estimator-form" name="tank-size-estimator-form" method="post" action="/septic-tank-size-estimator/">
                <div class="field-grid">
                    <label class="field">
                        <span>State</span>
                        <select name="stateCode">
                            @for (var state : states)
                                <option value="${state.stateCode()}" selected="${state.stateCode().equals(tankSizeForm.getStateCode())}">${state.stateName()}</option>
                            @endfor
                        </select>
                    </label>

                    <label class="field">
                        <span>Bedrooms</span>
                        <input type="number" min="1" max="10" name="bedrooms" value="${tankSizeForm.getBedrooms()}">
                    </label>

                    <label class="field">
                        <span>Occupancy profile</span>
                        <select name="occupancyProfile">
                            @for (var occupancyProfile : occupancyProfiles)
                                <option value="${occupancyProfile.value()}" selected="${occupancyProfile.value().equals(tankSizeForm.getOccupancyProfile())}">${occupancyProfile.label()}</option>
                            @endfor
                        </select>
                    </label>

                    <label class="checkbox">
                        <input type="checkbox" name="garbageDisposal" checked="${tankSizeForm.isGarbageDisposal()}">
                        <span>Garbage disposal installed</span>
                    </label>

                    <label class="checkbox">
                        <input type="checkbox" name="additionalKitchen" checked="${tankSizeForm.isAdditionalKitchen()}">
                        <span>Additional kitchen or ADU</span>
                    </label>
                </div>

                <div class="form-actions">
                    <button class="button button--primary" type="submit">Show tank size estimate</button>
                    <p>Bedroom tables and design-flow rules are used where they exist. Otherwise the result stays conservative.</p>
                </div>
            </form>

            <div class="calculator-output">
                @if (result != null)
                    <section class="result-panel"
                             data-ga-event="calculator_submit"
                             data-ga-param-estimator-type="tank_size_estimator"
                             data-ga-param-state-code="${tankSizeForm.getStateCode()}">
                        <div class="result-panel__header">
                            <div>
                                <span class="eyebrow">Tank size result</span>
                                <h2>${result.estimate().stateName()} tank size planning range</h2>
                            </div>
                            <span class="pill">${result.occupancyProfileLabel()}</span>
                        </div>

                        <div class="result-grid">
                            <article class="metric-card">
                                <span class="metric-card__label">Likely minimum tank size</span>
                                <strong>${result.estimate().formattedLikelyMinimumTank()}</strong>
                                <p>${result.estimate().officialMinimumNote()}</p>
                            </article>

                            <article class="metric-card">
                                <span class="metric-card__label">Conservative recommended range</span>
                                <strong>${result.estimate().formattedRecommendedTankRange()}</strong>
                                <p>${result.estimate().rangeReason()}</p>
                            </article>

                            <article class="metric-card metric-card--accent">
                                <span class="metric-card__label">Rough pumping cadence</span>
                                <strong>${result.pumpSchedule().roughPumpingCadence()}</strong>
                                <p>${result.pumpSchedule().maintenanceBudgetReminder()}</p>
                            </article>

                            <article class="metric-card">
                                <span class="metric-card__label">Suggested inspection cadence</span>
                                <strong>${result.pumpSchedule().usageProfileLabel()}</strong>
                                <p>${result.pumpSchedule().suggestedInspectionCadence()}</p>
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
                                <h3>How the occupancy profile was handled</h3>
                                <p>${result.occupancyNote()}</p>
                            </article>

                            <article class="detail-card">
                                <h3>What to verify locally</h3>
                                <ul class="detail-list">
                                    @for (var item : result.estimate().checklist())
                                        <li>${item}</li>
                                    @endfor
                                </ul>
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
                            <p>If you also need price bands and system-class risk, move into the full cost estimator with your state already selected. If the next question is maintenance cadence, jump to the pump schedule estimator instead.</p>
                            <div class="hero__actions">
                                <a class="button button--primary" href="/septic-system-cost-calculator/?state=${result.estimate().stateCode()}&projectType=new_install">Open the full cost estimator</a>
                                <a class="button button--secondary" href="/septic-pump-schedule-estimator/">Open pump schedule estimator</a>
                                @if (selectedState != null)
                                    <a class="button button--secondary" href="/septic-system-cost-calculator/${selectedState.slug()}/">Open ${selectedState.stateName()} guide</a>
                                @endif
                            </div>
                        </article>
                    </section>
                @else
                    <article class="placeholder-panel">
                        <span class="eyebrow">Result preview</span>
                        <h2>Use this when the first question is tank size, not total project cost.</h2>
                        <p>You will get a likely minimum, a conservative recommendation range, and a rough maintenance cadence that stays honest when usage is uncertain.</p>
                    </article>
                @endif
            </div>
        </div>
    </section>

    @if (selectedState != null)
        <section class="section section--tight">
            <div class="container detail-grid">
                <article class="detail-card detail-card--full">
                    <h2>${selectedState.stateName()} sizing rule snapshot</h2>
                    <div class="result-grid">
                        <article class="metric-card">
                            <span class="metric-card__label">Rule style</span>
                            <strong>${selectedState.ruleType()}</strong>
                            <p>${selectedState.agencyName()}</p>
                        </article>
                        <article class="metric-card">
                            <span class="metric-card__label">Public sizing signal</span>
                            <strong>
                                @if (selectedState.minTankSizeGallons() != null)
                                    ${selectedState.minTankSizeGallons()} gal minimum
                                @elseif (selectedState.designFlowPerBedroomGpd() != null)
                                    ${selectedState.designFlowPerBedroomGpd()} GPD per bedroom
                                @else
                                    Conservative fallback
                                @endif
                            </strong>
                            <p>${selectedState.ruleSummaryPlainEnglish()}</p>
                        </article>
                        <article class="metric-card">
                            <span class="metric-card__label">Garbage disposal policy</span>
                            <strong>
                                @if (selectedState.garbageDisposalPolicy() != null && !selectedState.garbageDisposalPolicy().isBlank())
                                    State-specific note
                                @else
                                    Heuristic only
                                @endif
                            </strong>
                            <p>${selectedState.garbageDisposalPolicy() != null && !selectedState.garbageDisposalPolicy().isBlank() ? selectedState.garbageDisposalPolicy() : "Disposal use still widens the homeowner-facing range, even when the state rule is less explicit."}</p>
                        </article>
                        <article class="metric-card">
                            <span class="metric-card__label">Local verification note</span>
                            <strong>Updated ${selectedState.lastVerifiedAt()}</strong>
                            <p>${selectedState.localOverrideNote()}</p>
                        </article>
                    </div>
                </article>

                @if (!stateRuleFacts.isEmpty())
                    <details class="detail-card detail-card--full disclosure-card">
                        <summary>Source-backed sizing facts for ${selectedState.stateName()}</summary>
                        <div class="detail-grid disclosure-card__body">
                            @for (var fact : stateRuleFacts)
                                <article class="detail-card">
                                    <h3>${fact.label()}</h3>
                                    <p><strong>${fact.renderedValue()}</strong></p>
                                    <p>${fact.note()}</p>
                                    @if (!fact.sourceTitle().isBlank())
                                        <p class="detail-card__source">
                                            Source:
                                            <a href="${fact.sourceUrl()}" target="_blank" rel="noreferrer">${fact.sourceTitle()}</a>
                                            @if (!fact.sourceSection().isBlank())
                                                <span> | ${fact.sourceSection()}</span>
                                            @endif
                                        </p>
                                    @endif
                                </article>
                            @endfor
                        </div>
                    </details>
                @endif

                <article class="trust-box trust-box--large">
                    <span class="trust-box__label">Use the right next step</span>
                    <h2>If ${selectedState.stateName()} tank size is only the first question, move into the matching guide next.</h2>
                    <p>The state guide explains the permit path, local verification links, records to request, and the reasons the final quote can move above the low end.</p>
                    <div class="hero__actions">
                        <a class="button button--primary" href="/septic-system-cost-calculator/${selectedState.slug()}/">Open ${selectedState.stateName()} guide</a>
                        <a class="button button--secondary" href="/septic-system-cost-calculator/?state=${selectedState.stateCode()}&projectType=new_install">Run full cost estimate for ${selectedState.stateName()}</a>
                    </div>
                </article>
            </div>
        </section>
    @endif

    <section class="section section--muted">
        <div class="container">
            <div class="section-heading">
                <span class="eyebrow">What changes the range</span>
                <h2>The recommendation widens when the homeowner inputs stop looking like a simple bedroom table.</h2>
            </div>

            <div class="card-grid">
                <article class="feature-card">
                    <h3>Bedrooms still anchor the answer</h3>
                    <p>Most public rules start with bedrooms or bedroom-based design flow, which is why this tool treats them as the first sizing signal.</p>
                </article>
                <article class="feature-card">
                    <h3>Occupancy can still widen the range</h3>
                    <p>When household use is obviously heavier than bedroom count alone suggests, the conservative range moves up instead of pretending the lower end is still safe.</p>
                </article>
                <article class="feature-card">
                    <h3>Disposals and extra kitchens matter</h3>
                    <p>Garbage disposals and additional kitchens or ADUs raise the planning load and can justify a bigger homeowner-facing recommendation.</p>
                </article>
                <article class="feature-card">
                    <h3>Local review still wins in the end</h3>
                    <p>Even a strong tank-size estimate is not permit approval. Final sizing still depends on local review, site conditions, and the actual system path.</p>
                </article>
            </div>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container">
            <details class="detail-card detail-card--full disclosure-card">
                <summary>Open a tank size estimate with the state already selected</summary>
                <div class="disclosure-card__body">
                    <p class="lede">These state links are useful when you want a quick starting point before opening the full cost estimator or the state guide.</p>
                    <div class="state-grid">
                        @for (var state : states)
                            <article class="state-card">
                                <div class="state-card__header">
                                    <span class="pill">Tank size</span>
                                    <strong>${state.stateName()}</strong>
                                </div>
                                <p>${state.pageAngle()}</p>
                                <ul class="state-card__meta">
                                    <li>${state.ruleType()}</li>
                                    <li>
                                        @if (state.minTankSizeGallons() != null)
                                            ${state.minTankSizeGallons()} gal minimum anchor
                                        @elseif (state.designFlowPerBedroomGpd() != null)
                                            ${state.designFlowPerBedroomGpd()} GPD per bedroom
                                        @else
                                            Conservative fallback range
                                        @endif
                                    </li>
                                    <li>Updated ${state.lastVerifiedAt()}</li>
                                </ul>
                                <div class="state-card__links">
                                    <a href="/septic-tank-size-estimator/?state=${state.stateCode()}">Open estimator</a>
                                    <a href="/septic-system-cost-calculator/${state.slug()}/">State guide</a>
                                </div>
                            </article>
                        @endfor
                    </div>
                </div>
            </details>
        </div>
    </section>

    @if (!tankSizeFaqs.isEmpty())
        <section class="section" id="faq">
            <div class="container">
                <details class="detail-card detail-card--full disclosure-card">
                    <summary>Questions this tank size page should answer before the user asks for quotes</summary>
                    <div class="detail-grid disclosure-card__body">
                        @for (var faq : tankSizeFaqs)
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
                    <li><a href="/septic-system-cost-calculator/">Main septic cost calculator</a></li>
                    <li><a href="/septic-pumping-cost/">Septic pumping cost</a></li>
                    <li><a href="/septic-replacement-cost/">Septic replacement cost</a></li>
                    <li><a href="/perc-test-cost/">Perc test cost</a></li>
                </ul>
            </details>

            <article class="detail-card">
                <h2>When this page is enough</h2>
                <p>Use this estimator when you mainly want to know the likely tank band and whether disposal use, occupancy, or extra kitchens should widen it before you talk to installers.</p>
            </article>

            <article class="detail-card">
                <h2>When to switch tools</h2>
                <p>Move to the full cost estimator when you need system-class risk, replacement planning, permit-path context, or a broader quote conversation.</p>
            </article>
        </div>
    </section>
`)
