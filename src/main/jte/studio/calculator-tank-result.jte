@import com.example.septic.service.TankSizeEstimatorResult
@param TankSizeEstimatorResult result
@param String intakeUrl

<section class="calc-result calc-special-result calc-tank-result" id="result-top" tabindex="-1">
    <img class="calc-report-landscape" src="/images/studio/county-property-v1.webp" alt="Open property landscape used as an editorial planning image" width="1672" height="941">
    <div class="calc-report-cover">
        <div class="studio-wrap">
            <div class="calc-result-heading">
                <div><p class="studio-overline">Tank capacity / Planning report</p><h1>${result.estimate().stateName()} capacity outlook</h1><p class="calc-project-label">${result.occupancyProfileLabel()} household profile</p></div>
                <a href="#property-inputs" data-edit-property>Refine the household inputs</a>
            </div>
            <div class="calc-special-hero-metrics">
                <div><span>Likely minimum</span><strong>${result.estimate().formattedLikelyMinimumTank()}</strong></div>
                <div><span>Conservative planning band</span><strong>${result.estimate().formattedRecommendedTankRange()}</strong></div>
            </div>
        </div>
    </div>
    <div class="studio-wrap calc-special-body">
        <div class="calc-special-intro">
            <p class="studio-overline">What the number means</p>
            <h2>Size for the use.<br>Verify against the file.</h2>
            <p>${result.occupancyNote()}</p>
        </div>
        <dl class="calc-special-ledger">
            <div><dt>Official sizing signal</dt><dd>${result.estimate().officialMinimumNote()}</dd></div>
            <div><dt>Why the range can widen</dt><dd>${result.estimate().rangeReason()}</dd></div>
            <div><dt>Rough pumping cadence</dt><dd><strong>${result.pumpSchedule().roughPumpingCadence()}</strong><br>${result.pumpSchedule().suggestedInspectionCadence()}</dd></div>
            <div><dt>Local decision</dt><dd>${result.estimate().localOverrideNote()}</dd></div>
            @for (var item : result.estimate().costDrivers())
                <div><dt>Capacity signal</dt><dd>${item}</dd></div>
            @endfor
        </dl>
    </div>
    <div class="studio-wrap calc-special-actions">
        <div><p class="studio-overline">Before an installer sizes the job</p><h2>Bring the recorded capacity and bedroom basis into the conversation.</h2></div>
        <div><a class="studio-button" href="${intakeUrl}">Ask about my property <span aria-hidden="true">↗</span></a><p class="studio-fine">Records research and planning support—not engineered design or permit approval.</p></div>
    </div>
</section>
