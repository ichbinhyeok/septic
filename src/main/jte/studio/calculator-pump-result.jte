@import com.example.septic.service.PumpScheduleResult
@param PumpScheduleResult result
@param String intakeUrl

<section class="calc-result calc-special-result calc-pump-result" id="result-top" tabindex="-1">
    <img class="calc-report-landscape" src="/images/studio/morgan-garden-v1.webp" alt="Established home landscape used as an editorial maintenance image" width="1672" height="941">
    <div class="calc-report-cover">
        <div class="studio-wrap">
            <div class="calc-result-heading">
                <div><p class="studio-overline">Maintenance / Planning report</p><h1>Your septic care rhythm</h1><p class="calc-project-label">${result.tankSizeGallons()} gallon tank · ${result.occupants()} occupants</p></div>
                <a href="#property-inputs" data-edit-property>Refine the use profile</a>
            </div>
            <div class="calc-special-hero-metrics">
                <div><span>Rough pumping cadence</span><strong>${result.roughPumpingCadence()}</strong></div>
                <div><span>Household pattern</span><strong>${result.usageProfileLabel()}</strong></div>
            </div>
        </div>
    </div>
    <div class="studio-wrap calc-special-body">
        <div class="calc-special-intro">
            <p class="studio-overline">Use the calendar as a prompt</p>
            <h2>Inspect the condition.<br>Do not worship the date.</h2>
            <p>${result.confidenceNote()}</p>
        </div>
        <dl class="calc-special-ledger">
            <div><dt>Inspection rhythm</dt><dd>${result.suggestedInspectionCadence()}</dd></div>
            <div><dt>Budget reminder</dt><dd>${result.maintenanceBudgetReminder()}</dd></div>
            @for (var driver : result.drivers())
                <div><dt>Schedule signal</dt><dd>${driver}</dd></div>
            @endfor
        </dl>
    </div>
    <div class="studio-wrap calc-special-actions">
        <div><p class="studio-overline">The strongest maintenance file</p><h2>Pair the next visit with the last pump, inspection, and permit record.</h2></div>
        <div><a class="studio-button" href="${intakeUrl}">Research my property file <span aria-hidden="true">↗</span></a><p class="studio-fine">Records research—not pumping dispatch, inspection, or a contractor quote.</p></div>
    </div>
</section>
