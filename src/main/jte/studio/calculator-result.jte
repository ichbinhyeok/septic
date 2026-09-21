@import com.example.septic.service.EstimatorResult
@import com.example.septic.service.DrainfieldEstimatorResult
@param EstimatorResult result
@param DrainfieldEstimatorResult drainfieldResult = null
@param String intakeUrl
@param boolean production = false
@param java.util.List<com.example.septic.web.CostEvidenceView> costEvidence = java.util.List.of()
<section class="calc-result" id="result-top" aria-labelledby="result-title">
<img class="calc-report-landscape" src="${drainfieldResult != null ? "/images/studio/oak-pasture-v1.webp" : "/images/studio/calculator-landscape-v2.webp"}" alt="" width="1672" height="941" fetchpriority="high">
<div class="calc-report-cover"><div class="studio-wrap">
    <div class="calc-result-heading"><div><p class="studio-overline">${drainfieldResult != null ? "Field recovery estimate" : "Your planning estimate"} / ${result.stateName()}</p><h1 id="result-title">@if(drainfieldResult != null)Price the field.<br>Read the constraint.@else A starting point.<br>Not a final price.@endif</h1><p class="calc-project-label">${result.projectTypeLabel()}</p></div><a href="#property-inputs" data-edit-property>Edit property details ↑</a></div>
    <div class="calc-range"><p class="studio-overline">Planning cost range</p><strong>${result.formattedTotalCostRange()}</strong><p>For early budgeting only. The midpoint, about ${result.formattedTotalCostMid()}, is not an expected bid.</p></div>
    <div class="calc-scenarios" aria-label="Read the range as scenarios"><div><p class="studio-overline">01 / Known basics</p><h3>${result.formattedBaselineLane()}</h3><p>Only while soil, access, layout, system type and repair scope stay straightforward.</p></div><div><p class="studio-overline">02 / Complex site</p><h3>${result.formattedComplexSiteLane()}</h3><p>Poor soil, difficult access, engineered design or wider failure scope can move the project upward.</p></div><div><p class="studio-overline">03 / Before a contract</p><h3>Verify the scope.</h3><p>The permit, site facts and written local scope need to agree before treating this as a contract price.</p></div></div>
</div></div><div class="studio-wrap">
@if(drainfieldResult != null)
<section class="calc-field-readout" aria-labelledby="field-outlook-title">
    <div><p class="studio-overline">Field outlook</p><h2 id="field-outlook-title">${drainfieldResult.fieldOutlookLabel()}</h2><p>${drainfieldResult.fieldRiskNote()}</p></div>
    <dl><div><dt>Primary constraint</dt><dd>${drainfieldResult.redesignRiskLabel()}</dd></div><div><dt>Replacement area</dt><dd>${drainfieldResult.replacementAreaNote()}</dd></div><div><dt>Cost swing</dt><dd>${drainfieldResult.costSwingNote()}</dd></div></dl>
</section>
@endif
<section class="calc-understand" id="understand-estimate">
<p class="studio-overline">Understand your estimate</p><h2>What narrows this result?</h2>
<details class="calc-question"><summary><span class="calc-question-number">01</span><span class="calc-question-title">Soil &amp; access</span><span class="calc-question-note">Ground conditions and equipment access can change the scope.</span></summary><ul>@for(var item : result.costDrivers())<li>${item}</li>@endfor</ul></details>
<details class="calc-question"><summary><span class="calc-question-number">02</span><span class="calc-question-title">The approved system</span><span class="calc-question-note">Read the design basis, not just the tank size.</span></summary><p>${result.officialMinimumNote()}</p><p>${result.rangeReason()}</p><p>${result.localOverrideNote()}</p></details>
<details class="calc-question"><summary><span class="calc-question-number">03</span><span class="calc-question-title">What the file cannot tell you</span><span class="calc-question-note">Historical records are not a current site inspection.</span></summary><p>A file may not show later changes, exact present-day locations or current condition. Records research does not replace a site evaluation or a written local construction scope.</p><ul>@for(var item : result.methodologyLimits())<li>${item}</li>@endfor</ul></details>
</section>
<details class="calc-evidence" id="calculation-evidence"><summary>Need the calculation details? Sources, assumptions &amp; limits</summary>
    <div class="calc-result-body"><div><p class="studio-overline">What this range assumes</p><h2>Read the limits<br>with the number.</h2><p>${result.costAnchorNote()}</p><p class="studio-fine">Use the source context and local checks below before relying on the planning range.</p></div>
    <div class="calc-facts"><dl>
        <div><dt>Price precision / ${result.pricePrecisionLabel()}</dt><dd>${result.pricePrecisionNote()}</dd></div>
        <div><dt>Source profile / ${result.confidenceLabel()}</dt><dd>Completeness of state rules and planning inputs, not price precision.</dd></div>
        <div><dt>Likely tank lane / ${result.formattedLikelyMinimumTank()}</dt><dd>${result.officialMinimumNote()} Recommended planning range: ${result.formattedRecommendedTankRange()}. ${result.rangeReason()}</dd></div>
        <div><dt>Likely system class</dt><dd>${result.likelySystemClass()}. System type is less certain when site conditions are unknown.</dd></div>
    </dl></div></div>
    <div class="calc-details">
<details><summary>What to verify next</summary><ul>@for(var item : result.checklist())<li>${item}</li>@endfor</ul></details>
<details><summary>What moved the estimate</summary><ul>@for(var item : result.costDrivers())<li>${item}</li>@endfor</ul></details>
<details><summary>Official rule and state anchor</summary><ul>@for(var item : result.officialBasis())<li>${item}</li>@endfor</ul></details>
<details><summary>Heuristic adjustments applied</summary><ul>@for(var item : result.heuristicAdjustments())<li>${item}</li>@endfor</ul></details>
<details><summary>Methodology limits</summary><ul>@for(var item : result.methodologyLimits())<li>${item}</li>@endfor</ul></details>
<details><summary>State rule context</summary><ul>@for(var item : result.ruleHighlights())<li>${item}</li>@endfor</ul></details>
<details><summary>Likely permit path</summary><ul>@for(var item : result.permitPathSteps())<li>${item}</li>@endfor</ul></details>

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


    @if (result.projectTypeLabel() != null && result.projectTypeLabel().startsWith("Drain field"))
        <article class="trust-box">
            <span class="trust-box__label">Field-specific next move</span>
            <p>This estimate already carries the field-specific soil, saturation, access, and replacement-area path. Refine those inputs above before comparing local scopes.</p>
            <a href="#property-inputs" data-edit-property>Refine the field scope</a>
        </article>
    @endif


    <details><summary>Official sources & review</summary><p><strong>${result.agencyName()}</strong> guidance informs this state profile. Last verified: ${result.lastVerifiedAt()}.</p><p>${result.localOverrideNote()}</p><ul>@for(var source : result.sourceLabels())<li>${source}</li>@endfor</ul></details>
    <details><summary>Cost evidence behind this planning range / ${costEvidence.size()} published items</summary><p>Local labor, soil, access, engineered design and permit requirements can move a final bid outside this range.</p>@for(var evidence : costEvidence)<article><h3>${evidence.title()}</h3><p><strong>${evidence.valueSummary()}</strong></p><p>${evidence.note()}</p><p class="studio-fine">${evidence.sourceSummary()}</p></article>@endfor</details>
    </div>
</details>
</div>
</section>
@template.studio.calculator-research(intakeUrl = intakeUrl, production = production)
