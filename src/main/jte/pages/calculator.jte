@import com.example.septic.service.AccessDifficulty
@import com.example.septic.service.EstimatorResult
@import com.example.septic.service.ProjectType
@import com.example.septic.service.SoilPercStatus
@import com.example.septic.service.TimelinePreference
@import com.example.septic.data.model.ContentPage
@import com.example.septic.web.EstimateForm
@import com.example.septic.web.PageMeta
@import com.example.septic.web.PageLink
@import com.example.septic.web.QuoteLeadForm
@import com.example.septic.web.StateOptionView
@param PageMeta page
@param java.util.List<StateOptionView> states
@param EstimateForm estimateForm
@param EstimatorResult result = null
@param java.util.List<com.example.septic.web.CostEvidenceView> costEvidence = java.util.List.of()
@param ContentPage calculatorLanding = null
@param java.util.List<PageLink> calculatorLandingLinks = java.util.List.of()
@param QuoteLeadForm quoteLeadForm
@param String leadId = null
@param boolean quoteHasErrors = false
@param boolean showQuotePanel = false
@param ProjectType[] projectTypes
@param SoilPercStatus[] soilStatuses
@param AccessDifficulty[] accessLevels
@param TimelinePreference[] timelines

@template.layouts.app(page = page, content = @`
    <section class="section section--heroish">
        <div class="container content-grid">
            <div class="section-heading">
                <span class="eyebrow">Main calculator</span>
                <h1>Get the core septic range first, then tighten it only when the extra details matter.</h1>
                <p class="lede">Start with state, job type, and bedroom count. Add site and access details only when you know them.</p>
            </div>

            <article class="detail-card calculator-intro-card">
                <h2>Best use case</h2>
                <p>Use this when you need one practical planning range before you decide whether the real blocker is replacement risk, permit path, inspection timing, or missing records.</p>
            </article>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container calculator-layout">
            <form class="calculator-form" method="post" action="/septic-system-cost-calculator/">
                <div class="calculator-form__intro">
                    <span class="pill">Fast start</span>
                    <p>Choose the basics first. The secondary fields only help tighten the range.</p>
                </div>

                <section class="form-section">
                    <div class="form-section__header">
                        <h2>Start with the basics</h2>
                        <p>These inputs do most of the work.</p>
                    </div>

                    <div class="field-grid field-grid--primary">
                        <label class="field">
                            <span>State</span>
                            <select name="stateCode">
                                @for (var state : states)
                                    <option value="${state.stateCode()}" selected="${state.stateCode().equals(estimateForm.getStateCode())}">${state.stateName()}</option>
                                @endfor
                            </select>
                        </label>

                        <label class="field">
                            <span>Project type</span>
                            <select name="projectType">
                                @for (var projectType : projectTypes)
                                    <option value="${projectType.value()}" selected="${projectType.value().equals(estimateForm.getProjectType())}">${projectType.label()}</option>
                                @endfor
                            </select>
                        </label>

                        <label class="field">
                            <span>Bedrooms</span>
                            <input type="number" min="1" max="10" name="bedrooms" value="${estimateForm.getBedrooms()}">
                        </label>

                        <label class="field">
                            <span>Occupants (optional)</span>
                            <input type="number" min="1" max="20" name="occupants" value="${estimateForm.getOccupantsValue()}">
                        </label>
                    </div>
                </section>

                <section class="form-section form-section--secondary">
                    <div class="form-section__header">
                        <h2>Tighten the range</h2>
                        <p>Add these when you know them. Unknown is still a valid starting point.</p>
                    </div>

                    <div class="field-grid field-grid--secondary">
                        <label class="field">
                            <span>Soil / perc status</span>
                            <select name="soilPercStatus">
                                @for (var status : soilStatuses)
                                    <option value="${status.value()}" selected="${status.value().equals(estimateForm.getSoilPercStatus())}">${status.label()}</option>
                                @endfor
                            </select>
                        </label>

                        <label class="field">
                            <span>Lot / access difficulty</span>
                            <select name="accessDifficulty">
                                @for (var access : accessLevels)
                                    <option value="${access.value()}" selected="${access.value().equals(estimateForm.getAccessDifficulty())}">${access.label()}</option>
                                @endfor
                            </select>
                        </label>

                        <label class="field field--full">
                            <span>Timeline</span>
                            <select name="timeline">
                                @for (var timeline : timelines)
                                    <option value="${timeline.value()}" selected="${timeline.value().equals(estimateForm.getTimeline())}">${timeline.label()}</option>
                                @endfor
                            </select>
                        </label>
                    </div>

                    <div class="secondary-checks">
                        <label class="checkbox">
                            <input type="checkbox" name="garbageDisposal" checked="${estimateForm.isGarbageDisposal()}">
                            <span>Garbage disposal installed</span>
                        </label>

                        <label class="checkbox">
                            <input type="checkbox" name="additionalKitchen" checked="${estimateForm.isAdditionalKitchen()}">
                            <span>Additional kitchen or ADU</span>
                        </label>

                        <label class="checkbox">
                            <input type="checkbox" name="highWaterTableOrShallowBedrock" checked="${estimateForm.isHighWaterTableOrShallowBedrock()}">
                            <span>High water table or shallow bedrock suspected</span>
                        </label>
                    </div>
                </section>

                <div class="form-actions">
                    <button class="button button--primary" type="submit">Show planning estimate</button>
                    <p>Planning only. Final sizing, system design, and permitting still need local review.</p>
                </div>
            </form>

            <div class="calculator-output">
                @if (result != null)
                    @template.tags.estimateResult(result = result, costEvidence = costEvidence)
                @else
                    <article class="placeholder-panel">
                        <span class="eyebrow">Result preview</span>
                        <h2>One range, one likely system lane, and the next thing to verify.</h2>
                        <ul class="detail-list detail-list--compact">
                            <li>Conservative tank and cost range</li>
                            <li>Likely system class and main cost drivers</li>
                            <li>Checklist for the next county, record, or quote step</li>
                        </ul>
                    </article>
                @endif

                @if (showQuotePanel)
                    <section class="quote-panel" id="quote-request">
                        <div class="quote-panel__header">
                            <div>
                                <span class="eyebrow">Primary CTA</span>
                                <h2>Get matched with local septic pros</h2>
                            </div>
                            <span class="pill">Short form</span>
                        </div>

                        @if (result == null)
                            <article class="status-box">
                                <strong>Already know the job type?</strong>
                                <p>You can request local quotes now. Run the estimate first only if you want a tighter planning range before sending the lead.</p>
                            </article>
                        @endif

                        @if (leadId != null)
                            <article class="status-box status-box--success">
                                <strong>Request received</strong>
                                <p>Your project details are in. Use lead ID ${leadId} if you need to reference this request while you compare local septic quotes.</p>
                            </article>
                        @endif

                        @if (quoteHasErrors)
                            <article class="status-box status-box--error">
                                <strong>Finish the required fields</strong>
                                <p>Name, email, phone, ZIP, and consent are required before this lead can be stored.</p>
                            </article>
                        @endif

                        <form class="quote-form" method="post" action="/quote-request/">
                            @if (result != null)
                                <input type="hidden" name="stateCode" value="${quoteLeadForm.getStateCode()}">
                                <input type="hidden" name="projectType" value="${quoteLeadForm.getProjectType()}">
                                <input type="hidden" name="bedrooms" value="${quoteLeadForm.getBedrooms()}">
                                <input type="hidden" name="occupants" value="${quoteLeadForm.getOccupantsValue()}">
                            @endif
                            <input type="hidden" name="soilPercStatus" value="${quoteLeadForm.getSoilPercStatus()}">
                            <input type="hidden" name="accessDifficulty" value="${quoteLeadForm.getAccessDifficulty()}">
                            <input type="hidden" name="timeline" value="${quoteLeadForm.getTimeline()}">
                            <input type="hidden" name="garbageDisposal" value="${quoteLeadForm.isGarbageDisposal() ? "true" : "false"}">
                            <input type="hidden" name="additionalKitchen" value="${quoteLeadForm.isAdditionalKitchen() ? "true" : "false"}">
                            <input type="hidden" name="highWaterTableOrShallowBedrock" value="${quoteLeadForm.isHighWaterTableOrShallowBedrock() ? "true" : "false"}">

                            <div class="field-grid">
                                @if (result == null)
                                    <label class="field">
                                        <span>State</span>
                                        <select name="stateCode">
                                            @for (var state : states)
                                                <option value="${state.stateCode()}" selected="${state.stateCode().equals(quoteLeadForm.getStateCode())}">${state.stateName()}</option>
                                            @endfor
                                        </select>
                                    </label>

                                    <label class="field">
                                        <span>Project type</span>
                                        <select name="projectType">
                                            @for (var projectType : projectTypes)
                                                <option value="${projectType.value()}" selected="${projectType.value().equals(quoteLeadForm.getProjectType())}">${projectType.label()}</option>
                                            @endfor
                                        </select>
                                    </label>

                                    <label class="field">
                                        <span>Bedrooms</span>
                                        <input type="number" min="1" max="10" name="bedrooms" value="${quoteLeadForm.getBedrooms()}">
                                    </label>

                                    <label class="field">
                                        <span>Occupants (optional)</span>
                                        <input type="number" min="1" max="20" name="occupants" value="${quoteLeadForm.getOccupantsValue()}">
                                    </label>
                                @endif

                                <label class="field">
                                    <span>Full name</span>
                                    <input type="text" name="fullName" value="${quoteLeadForm.getFullNameValue()}">
                                </label>

                                <label class="field">
                                    <span>Email</span>
                                    <input type="email" name="email" value="${quoteLeadForm.getEmailValue()}">
                                </label>

                                <label class="field">
                                    <span>Phone</span>
                                    <input type="tel" name="phone" value="${quoteLeadForm.getPhoneValue()}">
                                </label>

                                <label class="field">
                                    <span>ZIP code</span>
                                    <input type="text" inputmode="numeric" name="zipCode" value="${quoteLeadForm.getZipCodeValue()}">
                                </label>
                            </div>

                            <label class="checkbox checkbox--wide">
                                <input type="checkbox" name="consentAccepted" checked="${quoteLeadForm.isConsentAccepted()}">
                                <span>I agree to be contacted about my septic project estimate and matching options.</span>
                            </label>

                            <div class="form-actions">
                                <button class="button button--primary" type="submit">Request local quotes</button>
                                <p>Short form only. The estimate stays visible above while you request local pricing.</p>
                            </div>
                        </form>
                    </section>
                @endif

                @if (result != null)
                    <div class="sticky-mobile-cta-spacer" aria-hidden="true"></div>
                    <section class="sticky-mobile-cta sticky-mobile-cta--dual" aria-label="Quick mobile actions">
                        <div class="sticky-mobile-cta__inner">
                            <a class="button button--secondary" href="#result-top">Back to result</a>
                            <a class="button button--primary" href="#quote-request">Jump to quote form</a>
                        </div>
                    </section>
                @endif
            </div>
        </div>
    </section>

    @if (calculatorLanding != null)
        <section class="section section--tight">
            <div class="container detail-grid">
                <article class="detail-card">
                    <h2>Who should use this first</h2>
                    <p>${calculatorLanding.targetReader()}</p>
                    <ul class="detail-list">
                        @for (var bullet : calculatorLanding.fitBullets())
                            <li>${bullet}</li>
                        @endfor
                    </ul>
                </article>

                <article class="detail-card">
                    <h2>What widens the range fastest</h2>
                    <ul class="detail-list">
                        @for (var risk : calculatorLanding.lowEndBreakers())
                            <li>${risk}</li>
                        @endfor
                    </ul>
                </article>
            </div>
        </section>

        <section class="section section--tight">
            <div class="container content-grid">
                <article class="trust-box trust-box--large">
                    <span class="trust-box__label">Next best pages</span>
                    <h2>After the estimate, move into the page that matches the real bottleneck.</h2>
                    <ul class="detail-list">
                        @for (var link : calculatorLandingLinks)
                            <li>
                                <a href="${link.path()}">${link.title()}</a>
                                <p class="detail-list__note">${link.note()}</p>
                            </li>
                        @endfor
                    </ul>
                </article>

                <article class="detail-card">
                    <h2>Before you trust the low end</h2>
                    <ul class="detail-list">
                        @for (var item : calculatorLanding.quotePrepChecklist())
                            <li>${item}</li>
                        @endfor
                    </ul>
                </article>
            </div>
        </section>
    @endif
`)
