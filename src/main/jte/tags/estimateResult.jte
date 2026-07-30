@import com.example.septic.service.EstimatorResult
@param EstimatorResult result
@param java.util.List<com.example.septic.web.CostEvidenceView> costEvidence = java.util.List.of()

<section class="result-panel" id="result-top">
    <div class="result-panel__header">
        <div>
            <span class="eyebrow">Planning estimate</span>
            <h2>${result.stateName()} ${result.projectTypeLabel()}</h2>
        </div>
    </div>

    <div class="result-grid">
        <article class="metric-card metric-card--accent metric-card--headline">
            <span class="metric-card__label">Planning cost range</span>
            <strong>${result.formattedTotalCostRange()}</strong>
            <p>Use this for early budgeting. The midpoint, about ${result.formattedTotalCostMid()}, is not an expected bid.</p>
        </article>

        <article class="metric-card">
            <span class="metric-card__label">Source profile quality</span>
            <strong>${result.confidenceLabel()}</strong>
            <p>How complete the state rules and planning inputs are. This is not price precision.</p>
        </article>

        <article class="metric-card">
            <span class="metric-card__label">Price precision</span>
            <strong>${result.pricePrecisionLabel()}</strong>
            <p>${result.pricePrecisionNote()}</p>
        </article>

        <article class="metric-card">
            <span class="metric-card__label">Likely tank lane</span>
            <strong>${result.formattedLikelyMinimumTank()}</strong>
            <p>${result.officialMinimumNote()}</p>
            <p>Recommended planning range: ${result.formattedRecommendedTankRange()}. ${result.rangeReason()}</p>
        </article>

        <article class="metric-card">
            <span class="metric-card__label">Likely system class</span>
            <strong>${result.likelySystemClass()}</strong>
            <p>System type becomes less certain when site conditions are unknown.</p>
        </article>
    </div>

    <div class="estimate-scenario-board" aria-label="Planning scenarios">
        <div class="estimate-scenario-board__intro">
            <span class="eyebrow">Read the range as scenarios</span>
            <h3>Which side of the range should you plan around?</h3>
            <p>These are planning lanes, not bid predictions. Move upward when the file or site reveals complexity.</p>
        </div>
        <div class="estimate-scenario-list">
            <article>
                <span>Known-basics lane</span>
                <strong>${result.formattedBaselineLane()}</strong>
                <p>Use only while access, soil, layout, system type, and repair scope remain straightforward.</p>
            </article>
            <article>
                <span>Complex-site lane</span>
                <strong>${result.formattedComplexSiteLane()}</strong>
                <p>Plan here when poor soil, difficult access, engineered design, or wider failure scope appears.</p>
            </article>
            <article>
                <span>Pause pricing</span>
                <strong>Key facts still missing</strong>
                <p>Do not turn this range into a contract price until the permit, site facts, and written local scope agree.</p>
            </article>
        </div>
    </div>

    <div class="estimate-tighteners">
        <div>
            <span class="eyebrow">What narrows this result</span>
            <h3>Bring these facts to the next quote.</h3>
        </div>
        <ol>
            @for (var item : result.checklist().stream().limit(3).toList())
                <li>${item}</li>
            @endfor
        </ol>
        <p><strong>Source profile checked ${result.lastVerifiedAt()}.</strong> Live local bids and contractor availability are not included.</p>
    </div>

    <article class="estimate-basis-strip">
        <div>
            <span class="eyebrow">How the cost range was built</span>
            <strong>${result.stateName()} planning profile + the inputs above</strong>
        </div>
        <p>${result.costAnchorNote()}</p>
        <p><strong>${costEvidence.size()} published cost evidence item${costEvidence.size() == 1 ? "" : "s"}</strong> available below. Local labor, soil, access, engineered design, and permit requirements can move the final bid outside this range.</p>
    </article>

    @if ("AL".equals(result.stateCode()))
        <section class="alabama-result-ledger" aria-label="Alabama estimate boundary">
            <div>
                <span class="eyebrow">Alabama cost boundary</span>
                <h3>Official fees and private project costs are different layers.</h3>
                <p>The result above uses an Alabama-adjusted construction planning anchor. ADPH's published $100-$200 dwelling application band and eligible $150-$250 public site-evaluation band do not represent the full install, repair, or professional-design price.</p>
            </div>
            <dl>
                <div>
                    <dt>County application</dt>
                    <dd>$100-$200 published band</dd>
                </div>
                <div>
                    <dt>Eligible public site evaluation</dt>
                    <dd>$150-$250 in participating counties</dd>
                </div>
                <div>
                    <dt>Still quote separately</dt>
                    <dd>Private soil work, survey, design, install, excavation, restoration</dd>
                </div>
            </dl>
            <div class="alabama-result-ledger__actions">
                <a href="https://www.alabamapublichealth.gov/programintegrity/assets/FeeSystemManual.pdf" target="_blank" rel="noreferrer">Open ADPH fee manual</a>
                <a href="https://www.alabamapublichealth.gov/onsite/forms.html" target="_blank" rel="noreferrer">Open CEP forms</a>
                <a href="/septic-records-checklist/alabama/">Check the county file first</a>
            </div>
        </section>
    @endif

    <article class="result-cta-band">
        <div class="result-cta-band__copy">
            <span class="eyebrow">Next step</span>
            <h3>Want help finding local options?</h3>
            <p>Send the project basics for follow-up. This is an inquiry, not a live contractor-availability lookup or accepted match.</p>
        </div>
        <div class="result-cta-band__actions">
            <a class="button button--primary" href="#quote-request">Request follow-up</a>
            <div class="result-cta-band__meta">
                <span>Short form</span>
                <span>State context stays visible</span>
                <span>No live availability result</span>
            </div>
        </div>
    </article>

    @if (result.projectTypeLabel() != null && result.projectTypeLabel().startsWith("Drain field"))
        <article class="trust-box">
            <span class="trust-box__label">Field-specific next move</span>
            <p>If the field is clearly the main issue, use the dedicated drain field estimator to pressure-test redesign risk, replacement-area limits, and field-only versus whole-system scope.</p>
            <a href="/drain-field-estimator/?state=${result.stateCode()}">Open the drain field estimator</a>
        </article>
    @endif

    <details class="estimate-detail-disclosure">
        <summary>
            <span>Need the calculation details?</span>
            <strong>See cost drivers, assumptions, limits, and local checks</strong>
        </summary>
        <div class="estimate-detail-disclosure__body">
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
        </div>
    </details>

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
