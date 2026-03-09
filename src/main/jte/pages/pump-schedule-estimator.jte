@import com.example.septic.service.PumpScheduleResult
@import com.example.septic.service.UsageProfile
@import com.example.septic.web.PageMeta
@import com.example.septic.web.PumpScheduleForm
@param PageMeta page
@param PumpScheduleForm pumpScheduleForm
@param PumpScheduleResult result = null
@param UsageProfile[] usageProfiles

@template.layouts.app(page = page, content = @`
    <section class="section section--heroish">
        <div class="container section-heading">
            <span class="eyebrow">Maintenance estimator</span>
            <h1>Estimate a rough septic pumping and inspection cadence.</h1>
            <p class="lede">This tool is for homeowner maintenance planning. It uses tank size, occupancy, disposal use, and seasonal versus full-time use to suggest a rough cadence before you call local pumping pros.</p>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container calculator-layout">
            <form class="calculator-form" method="post" action="/septic-pump-schedule-estimator/">
                <div class="field-grid">
                    <label class="field">
                        <span>Tank size in gallons</span>
                        <input type="number" min="750" max="3000" step="50" name="tankSizeGallons" value="${pumpScheduleForm.getTankSizeGallonsValue()}">
                    </label>

                    <label class="field">
                        <span>Occupants</span>
                        <input type="number" min="1" max="20" name="occupants" value="${pumpScheduleForm.getOccupantsValue()}">
                    </label>

                    <label class="field">
                        <span>Use profile</span>
                        <select name="usageProfile">
                            @for (var usageProfile : usageProfiles)
                                <option value="${usageProfile.value()}" selected="${usageProfile.value().equals(pumpScheduleForm.getUsageProfile())}">${usageProfile.label()}</option>
                            @endfor
                        </select>
                    </label>

                    <label class="checkbox">
                        <input type="checkbox" name="garbageDisposal" checked="${pumpScheduleForm.isGarbageDisposal()}">
                        <span>Garbage disposal installed</span>
                    </label>
                </div>

                <div class="form-actions">
                    <button class="button button--primary" type="submit">Show pumping schedule</button>
                    <p>This is a planning cadence, not a substitute for local O&amp;M requirements or inspection findings.</p>
                </div>
            </form>

            <div class="calculator-output">
                @if (result != null)
                    <section class="result-panel">
                        <div class="result-panel__header">
                            <div>
                                <span class="eyebrow">Pump schedule result</span>
                                <h2>${result.usageProfileLabel()} maintenance plan</h2>
                            </div>
                            <span class="pill">${result.tankSizeGallons()} gal tank</span>
                        </div>

                        <div class="result-grid">
                            <article class="metric-card metric-card--accent">
                                <span class="metric-card__label">Rough pumping cadence</span>
                                <strong>${result.roughPumpingCadence()}</strong>
                                <p>${result.confidenceNote()}</p>
                            </article>

                            <article class="metric-card">
                                <span class="metric-card__label">Suggested inspection cadence</span>
                                <strong>${result.occupants()} occupants</strong>
                                <p>${result.suggestedInspectionCadence()}</p>
                            </article>

                            <article class="metric-card">
                                <span class="metric-card__label">Maintenance budget reminder</span>
                                <strong>Budget for routine pumping</strong>
                                <p>${result.maintenanceBudgetReminder()}</p>
                            </article>

                            <article class="metric-card">
                                <span class="metric-card__label">Use profile</span>
                                <strong>${result.usageProfileLabel()}</strong>
                                <p>Tank size and use intensity matter more than a fixed calendar reminder.</p>
                            </article>
                        </div>

                        <div class="detail-grid">
                            <article class="detail-card">
                                <h3>What shaped this cadence</h3>
                                <ul class="detail-list">
                                    @for (var driver : result.drivers())
                                        <li>${driver}</li>
                                    @endfor
                                </ul>
                            </article>

                            <article class="detail-card">
                                <h3>Source context</h3>
                                <ul class="detail-list">
                                    @for (var source : result.sourceLabels())
                                        <li>${source}</li>
                                    @endfor
                                </ul>
                            </article>
                        </div>

                        <article class="trust-box">
                            <span class="trust-box__label">Next best action</span>
                            <p>If your main question is price rather than cadence, move into the cost estimator or the pumping cost page next.</p>
                            <a href="/septic-system-cost-calculator/?projectType=pumping">Open the cost estimator</a>
                        </article>
                    </section>
                @else
                    <article class="placeholder-panel">
                        <span class="eyebrow">Result preview</span>
                        <h2>Use this after you already know the approximate tank size.</h2>
                        <p>You will get a rough pumping cadence, a simple inspection reminder, and a maintenance-budget note that stays honest about uncertainty.</p>
                    </article>
                @endif
            </div>
        </div>
    </section>
`)
