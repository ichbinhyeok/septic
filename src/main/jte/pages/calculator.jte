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
@param boolean recordsMode = false
@param String recordSystemType = ""
@param String recordTankCapacity = ""
@param String recordDesignFlow = ""
@param String calculatorError = ""
@param ProjectType[] projectTypes
@param SoilPercStatus[] soilStatuses
@param AccessDifficulty[] accessLevels
@param TimelinePreference[] timelines

@template.layouts.app(page = page, content = @`
    <section class="section section--heroish calculator-intro">
        <div class="container">
            <div class="section-heading">
                <span class="eyebrow">Septic cost calculator</span>
                <h1>Estimate the cost of a septic project.</h1>
                <p class="lede">Choose the state, job type, and property details. We will show a broad planning range, what moved it, and what to verify before trusting the low end.</p>
                @if (!calculatorLandingLinks.isEmpty())
                    <div class="quick-link-strip" aria-label="Workflow shortcuts">
                        <span class="quick-link-strip__label">Need records first?</span>
                        @for (var link : calculatorLandingLinks.stream().limit(4).toList())
                            <a class="button button--secondary quick-link-strip__button" href="${link.path()}">${link.title()}</a>
                        @endfor
                    </div>
                @endif
            </div>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container calculator-layout">
            <form class="calculator-form" id="cost-estimator-form" name="cost-estimator-form" method="post" action="/septic-system-cost-calculator/#result-top">
                <input type="hidden" name="sourcePageHint" value="${estimateForm.getSourcePageHint()}">
                <input type="hidden" name="recordsMode" value="${recordsMode}">
                <input type="hidden" name="recordSystemType" value="${recordSystemType}">
                <input type="hidden" name="recordTankCapacity" value="${recordTankCapacity}">
                <input type="hidden" name="recordDesignFlow" value="${recordDesignFlow}">
                @if (recordsMode)
                    <div class="calculator-record-context">
                        <span class="eyebrow">Carried from your property file</span>
                        <strong>The calculator uses only the record values it can price safely.</strong>
                        <p>State, project purpose, and an unconflicted bedroom count prefill the estimate. The other confirmed facts stay visible as reference; they do not secretly narrow the price range.</p>
                        @if (!recordSystemType.isBlank() || !recordTankCapacity.isBlank() || !recordDesignFlow.isBlank())
                            <dl class="calculator-record-context__facts">
                                @if (!recordSystemType.isBlank())
                                    <div><dt>System type</dt><dd>${recordSystemType}</dd></div>
                                @endif
                                @if (!recordTankCapacity.isBlank())
                                    <div><dt>Tank capacity</dt><dd>${recordTankCapacity}</dd></div>
                                @endif
                                @if (!recordDesignFlow.isBlank())
                                    <div><dt>Design flow</dt><dd>${recordDesignFlow}</dd></div>
                                @endif
                            </dl>
                            <small>Reference only — confirm these values with the original record and a local professional before using them to define scope.</small>
                        @endif
                        <a href="/#home-address-record-finder">Back to the property file</a>
                    </div>
                @endif
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
                            <select name="stateCode" required aria-describedby="calculator-state-error">
                                <option value="" selected="${estimateForm.getStateCode() == null || estimateForm.getStateCode().isBlank()}" disabled>Choose a state</option>
                                @for (var state : states)
                                    <option value="${state.stateCode()}" selected="${state.stateCode().equals(estimateForm.getStateCode())}">${state.stateName()}</option>
                                @endfor
                            </select>
                            @if (!calculatorError.isBlank())
                                <small class="field-error" id="calculator-state-error" role="alert">${calculatorError}</small>
                            @else
                                <small id="calculator-state-error">Costs and permit assumptions change by state.</small>
                            @endif
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

                    </div>
                </section>

                <div class="form-actions form-actions--quick">
                    <button class="button button--primary" type="submit">Show planning estimate</button>
                    <p>Start with a broad range. Add site details below only when you know them.</p>
                </div>

                <details class="form-section form-section--secondary calculator-advanced">
                    <summary>
                        <span>
                            <strong>Tighten the range</strong>
                            <small>Optional soil, access, and site details</small>
                        </span>
                        <span aria-hidden="true">+</span>
                    </summary>

                    <div class="field-grid field-grid--secondary">
                        <label class="field">
                            <span>Occupants (optional)</span>
                            <input type="number" min="1" max="20" name="occupants" value="${estimateForm.getOccupantsValue()}">
                        </label>

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
                </details>
            </form>

            <div class="calculator-output">
                @if (result != null)
                    <div hidden
                         data-ga-event="calculator_submit"
                         data-ga-param-estimator-type="cost_estimator"
                         data-ga-param-state-code="${estimateForm.getStateCode()}"
                         data-ga-param-project-type="${estimateForm.getProjectType()}"></div>
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
                                <span class="eyebrow">Optional next step</span>
                                <h2>Request help finding local options</h2>
                            </div>
                            <span class="pill">Short form</span>
                        </div>

                        @if (result == null)
                            <article class="status-box">
                                <strong>Already know the job type?</strong>
                                <p>You can submit the project basics now. This stores an inquiry for follow-up; it does not check live contractor inventory or guarantee a response.</p>
                            </article>
                        @endif

                        @if (leadId != null)
                            <article class="status-box status-box--success"
                                     data-ga-event="generate_lead"
                                     data-ga-track-once="quote-request:${leadId}"
                                     data-ga-param-lead-type="quote_request"
                                     data-ga-param-form-id="quote_request_form"
                                     data-ga-param-state-code="${estimateForm.getStateCode()}"
                                     data-ga-param-project-type="${estimateForm.getProjectType()}">
                                <strong>Project details received</strong>
                                <p>We saved your request under ID ${leadId}. This confirms submission, not contractor availability or a quote.</p>
                            </article>
                        @endif

                        @if (quoteHasErrors)
                            <article class="status-box status-box--error">
                                <strong>Finish the required fields</strong>
                                <p>Name, email, phone, ZIP, and consent are required before this lead can be stored.</p>
                            </article>
                        @endif

                        <form class="quote-form" id="quote-request-form" name="quote-request-form" method="post" action="/quote-request/">
                            @if (result != null)
                                <input type="hidden" name="stateCode" value="${quoteLeadForm.getStateCode()}">
                                <input type="hidden" name="projectType" value="${quoteLeadForm.getProjectType()}">
                                <input type="hidden" name="sourcePageHint" value="${quoteLeadForm.getSourcePageHint()}">
                                <input type="hidden" name="bedrooms" value="${quoteLeadForm.getBedrooms()}">
                                <input type="hidden" name="occupants" value="${quoteLeadForm.getOccupantsValue()}">
                            @endif
                            @if (result == null)
                                <input type="hidden" name="sourcePageHint" value="${quoteLeadForm.getSourcePageHint()}">
                            @endif
                            <input type="hidden" name="recordStatus" value="${quoteLeadForm.getRecordStatus()}">
                            <input type="hidden" name="soilPercStatus" value="${quoteLeadForm.getSoilPercStatus()}">
                            <input type="hidden" name="accessDifficulty" value="${quoteLeadForm.getAccessDifficulty()}">
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
                                        <input type="number" min="1" max="10" name="bedrooms" value="${quoteLeadForm.getBedrooms()}" required>
                                    </label>

                                    <label class="field">
                                        <span>Occupants (optional)</span>
                                        <input type="number" min="1" max="20" name="occupants" value="${quoteLeadForm.getOccupantsValue()}">
                                    </label>
                                @endif

                                <label class="field">
                                    <span>County (optional)</span>
                                    <input type="text" name="countyName" value="${quoteLeadForm.getCountyNameValue()}" autocomplete="address-level2" maxlength="120" placeholder="Example: Knox County">
                                </label>

                                <label class="field">
                                    <span>How soon do you need the next step?</span>
                                    <select name="timeline">
                                        @for (var timeline : timelines)
                                            <option value="${timeline.value()}" selected="${timeline.value().equals(quoteLeadForm.getTimeline())}">${timeline.label()}</option>
                                        @endfor
                                    </select>
                                </label>

                                <label class="field">
                                    <span>Preferred contact</span>
                                    <select name="preferredContactMethod">
                                        <option value="either" selected="${"either".equals(quoteLeadForm.getPreferredContactMethod())}">Email or phone</option>
                                        <option value="email" selected="${"email".equals(quoteLeadForm.getPreferredContactMethod())}">Email</option>
                                        <option value="phone" selected="${"phone".equals(quoteLeadForm.getPreferredContactMethod())}">Phone</option>
                                    </select>
                                </label>

                                <label class="field">
                                    <span>Full name</span>
                                    <input type="text" name="fullName" value="${quoteLeadForm.getFullNameValue()}" autocomplete="name" maxlength="120" required>
                                </label>

                                <label class="field">
                                    <span>Email</span>
                                    <input type="email" name="email" value="${quoteLeadForm.getEmailValue()}" autocomplete="email" maxlength="160" required>
                                </label>

                                <label class="field">
                                    <span>Phone</span>
                                    <input type="tel" name="phone" value="${quoteLeadForm.getPhoneValue()}" autocomplete="tel" maxlength="40" required>
                                </label>

                                <label class="field">
                                    <span>ZIP code</span>
                                    <input type="text" inputmode="numeric" name="zipCode" value="${quoteLeadForm.getZipCodeValue()}" autocomplete="postal-code" maxlength="5" pattern="[0-9]{5}" required>
                                </label>
                            </div>

                            <label class="checkbox checkbox--wide">
                                <input type="checkbox" name="consentAccepted" checked="${quoteLeadForm.isConsentAccepted()}" required>
                                <span>I agree to be contacted about this project and possible local service options. I understand this is not a confirmed match.</span>
                            </label>

                            <div class="form-actions">
                                <button class="button button--primary" type="submit">Submit project inquiry</button>
                                <p>We do not show live provider inventory. Submitting does not guarantee a match, price, or response time.</p>
                            </div>
                        </form>
                    </section>
                @endif

                @if (result != null)
                    <div class="sticky-mobile-cta-spacer" aria-hidden="true"></div>
                    <section class="sticky-mobile-cta sticky-mobile-cta--dual"
                             aria-label="Quick mobile actions"
                             data-sticky-mobile-cta
                             data-show-after=".result-panel">
                        <div class="sticky-mobile-cta__inner">
                            <a class="button button--secondary" href="#result-top">Back to result</a>
                            <a class="button button--primary" href="#quote-request" data-track-click="nav" data-track-source-context="calculator_result_followup" data-track-target-type="quote_form">Request follow-up</a>
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
                    <h2>When this estimate is the right next step</h2>
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
                    <h2>Use the page that matches the real bottleneck before or after the estimate.</h2>
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
