@import com.example.septic.data.model.StateProfile
@import com.example.septic.service.OccupancyProfile
@import com.example.septic.service.TankSizeEstimatorResult
@import com.example.septic.web.PageMeta
@import com.example.septic.web.TankSizeForm
@param PageMeta page
@param java.util.List<StateProfile> states
@param TankSizeForm tankSizeForm
@param TankSizeEstimatorResult result = null
@param OccupancyProfile[] occupancyProfiles

@template.layouts.app(page = page, content = @`
    <section class="section section--heroish">
        <div class="container section-heading">
            <span class="eyebrow">Dedicated estimator</span>
            <h1>Estimate likely septic tank size before you open the full cost flow.</h1>
            <p class="lede">This estimator is narrower than the full project calculator. It focuses on likely minimum tank size, a conservative range, and a rough maintenance cadence based on your state and usage profile.</p>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container calculator-layout">
            <form class="calculator-form" method="post" action="/septic-tank-size-estimator/">
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
                    <section class="result-panel">
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
                                <ul class="detail-list">
                                    @for (var source : result.estimate().sourceLabels())
                                        <li>${source}</li>
                                    @endfor
                                </ul>
                            </article>
                        </div>

                        <article class="trust-box">
                            <span class="trust-box__label">Next best action</span>
                            <p>If you also need price bands and system-class risk, move into the full cost estimator with your state already selected.</p>
                            <a href="/septic-system-cost-calculator/?state=${result.estimate().stateCode()}&projectType=new_install">Open the full cost estimator</a>
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
`)
