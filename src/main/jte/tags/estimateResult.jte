@import com.example.septic.service.EstimatorResult
@param EstimatorResult result
@param java.util.List<com.example.septic.web.CostEvidenceView> costEvidence = java.util.List.of()

<section class="result-panel" id="result-top">
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

    <article class="result-cta-band">
        <div class="result-cta-band__copy">
            <span class="eyebrow">Next step</span>
            <h3>Ready to compare local quotes?</h3>
            <p>Use this planning range as your talking point, then jump straight to the short quote form.</p>
        </div>
        <div class="result-cta-band__actions">
            <a class="button button--primary" href="#quote-request">Jump to quote form</a>
            <div class="result-cta-band__meta">
                <span>Short form</span>
                <span>State context stays visible</span>
                <span>Estimate-first, not permit-ready</span>
            </div>
        </div>
    </article>

    @if ("Drain field replacement".equals(result.projectTypeLabel()))
        <article class="trust-box">
            <span class="trust-box__label">Field-specific next move</span>
            <p>If the field is clearly the main issue, use the dedicated drain field estimator to pressure-test redesign risk, replacement-area limits, and field-only versus whole-system scope.</p>
            <a href="/drain-field-estimator/?state=${result.stateCode()}">Open the drain field estimator</a>
        </article>
    @endif

    <div class="detail-grid">
        <article class="detail-card">
            <h3>Official rule and state anchor</h3>
            <ul class="detail-list">
                @for (var item : result.officialBasis())
                    <li>${item}</li>
                @endfor
            </ul>
        </article>

        <article class="detail-card">
            <h3>Heuristic adjustments applied</h3>
            <ul class="detail-list">
                @for (var item : result.heuristicAdjustments())
                    <li>${item}</li>
                @endfor
            </ul>
        </article>

        <article class="detail-card">
            <h3>Methodology limits</h3>
            <ul class="detail-list">
                @for (var item : result.methodologyLimits())
                    <li>${item}</li>
                @endfor
            </ul>
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
        <p>${result.costAnchorNote()}</p>
        <details class="disclosure-card">
            <summary>View source set</summary>
            <ul class="source-list source-list--compact">
                @for (var source : result.sourceLabels())
                    <li>${source}</li>
                @endfor
            </ul>
        </details>
    </article>

    @if (!costEvidence.isEmpty())
        <details class="detail-card detail-card--full disclosure-card">
            <summary>Cost evidence behind this planning range</summary>
            <div class="detail-grid disclosure-card__body">
                @for (var evidence : costEvidence)
                    <article class="detail-card">
                        <h4>${evidence.title()}</h4>
                        <p><strong>${evidence.valueSummary()}</strong></p>
                        <p>${evidence.note()}</p>
                        <p class="detail-card__source">${evidence.sourceSummary()}</p>
                    </article>
                @endfor
            </div>
        </details>
    @endif
</section>
