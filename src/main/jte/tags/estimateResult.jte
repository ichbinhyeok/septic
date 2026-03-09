@import com.example.septic.service.EstimatorResult
@param EstimatorResult result

<section class="result-panel">
    <div class="result-panel__header">
        <div>
            <span class="eyebrow">Planning estimate</span>
            <h2>${result.stateName()} ${result.projectTypeLabel()}</h2>
        </div>
        <span class="pill">${result.confidenceLabel()} confidence</span>
    </div>

    <div class="result-grid">
        <article class="metric-card">
            <span class="metric-card__label">Likely minimum tank size</span>
            <strong>${result.formattedLikelyMinimumTank()}</strong>
            <p>${result.officialMinimumNote()}</p>
        </article>

        <article class="metric-card">
            <span class="metric-card__label">Conservative recommended range</span>
            <strong>${result.formattedRecommendedTankRange()}</strong>
            <p>${result.rangeReason()}</p>
        </article>

        <article class="metric-card metric-card--accent">
            <span class="metric-card__label">Likely total cost range</span>
            <strong>${result.formattedTotalCostRange()}</strong>
            <p>Mid-point planning anchor: about ${result.formattedTotalCostMid()}.</p>
        </article>

        <article class="metric-card">
            <span class="metric-card__label">Likely system class</span>
            <strong>${result.likelySystemClass()}</strong>
            <p>System type becomes less certain when site conditions are unknown.</p>
        </article>
    </div>

    <div class="detail-grid">
        <article class="detail-card">
            <h3>What pushed the estimate</h3>
            <ul class="detail-list">
                @for (var driver : result.costDrivers())
                    <li>${driver}</li>
                @endfor
            </ul>
        </article>

        <article class="detail-card">
            <h3>What to verify next</h3>
            <ul class="detail-list">
                @for (var item : result.checklist())
                    <li>${item}</li>
                @endfor
            </ul>
        </article>

        <article class="detail-card">
            <h3>State rule context</h3>
            <ul class="detail-list">
                @for (var item : result.ruleHighlights())
                    <li>${item}</li>
                @endfor
            </ul>
        </article>

        <article class="detail-card detail-card--full">
            <h3>Likely permit path</h3>
            <ul class="detail-list">
                @for (var item : result.permitPathSteps())
                    <li>${item}</li>
                @endfor
            </ul>
        </article>
    </div>

    <article class="trust-box">
        <span class="trust-box__label">Official-source context</span>
        <p><strong>${result.agencyName()}</strong> guidance informs this state profile. Last verified: ${result.lastVerifiedAt()}.</p>
        <p>${result.localOverrideNote()}</p>
        <p>The estimate engine is using the state profile and public cost anchors below. Final design and permit outcomes still need local verification.</p>
        <ul class="source-list source-list--compact">
            @for (var source : result.sourceLabels())
                <li>${source}</li>
            @endfor
        </ul>
    </article>
</section>
