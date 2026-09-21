@import com.example.septic.service.AccessDifficulty
@import com.example.septic.service.EstimatorResult
@import com.example.septic.service.DrainfieldEstimatorResult
@import com.example.septic.service.TankSizeEstimatorResult
@import com.example.septic.service.PumpScheduleResult
@import com.example.septic.service.OccupancyProfile
@import com.example.septic.service.UsageProfile
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
@param boolean production = false
@param java.util.List<StateOptionView> states
@param EstimateForm estimateForm
@param EstimatorResult result = null
@param DrainfieldEstimatorResult drainfieldResult = null
@param boolean drainfieldMode = false
@param TankSizeEstimatorResult tankSizeResult = null
@param boolean tankSizeMode = false
@param PumpScheduleResult pumpScheduleResult = null
@param boolean pumpScheduleMode = false
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
@param ProjectType[] projectTypes = ProjectType.values()
@param SoilPercStatus[] soilStatuses = SoilPercStatus.values()
@param AccessDifficulty[] accessLevels = AccessDifficulty.values()
@param TimelinePreference[] timelines = TimelinePreference.values()
@param OccupancyProfile[] occupancyProfiles = OccupancyProfile.values()
@param UsageProfile[] usageProfiles = UsageProfile.values()


@template.studio.shell(page = page, production = production, title = tankSizeMode ? "Septic tank size planner" : pumpScheduleMode ? "Septic pumping schedule planner" : "Septic project cost calculator", heroImage = tankSizeMode ? "/images/studio/county-property-v1.webp" : pumpScheduleMode ? "/images/studio/morgan-garden-v1.webp" : drainfieldMode ? "/images/studio/oak-pasture-v1.webp" : "/images/studio/calculator-landscape-v2.webp", pageClass = tankSizeMode ? "studio-calculator studio-calculator--tank" : pumpScheduleMode ? "studio-calculator studio-calculator--pump" : drainfieldMode ? "studio-calculator studio-calculator--field" : "studio-calculator", pageStyles = "/studio-calculator.css", content = @`
<main id="main">
@if(tankSizeResult != null)
    @template.studio.calculator-tank-result(result = tankSizeResult, intakeUrl = com.example.septic.web.StudioCalculatorHandoff.link(estimateForm))
@elseif(pumpScheduleResult != null)
    @template.studio.calculator-pump-result(result = pumpScheduleResult, intakeUrl = com.example.septic.web.StudioCalculatorHandoff.link(estimateForm))
@elseif(result != null)
    @template.studio.calculator-result(result = result, drainfieldResult = drainfieldResult, costEvidence = costEvidence, intakeUrl = com.example.septic.web.StudioCalculatorHandoff.link(estimateForm), production = production)
@endif
<details class="calc-editor" id="property-inputs" open="${result == null && tankSizeResult == null && pumpScheduleResult == null}">
<summary class="studio-wrap">Edit property details <span aria-hidden="true">+</span></summary>
<section class="${tankSizeMode ? "calc-workspace calc-workspace--tank" : pumpScheduleMode ? "calc-workspace calc-workspace--pump" : drainfieldMode ? "calc-workspace calc-workspace--field" : "calc-workspace"}" aria-labelledby="calc-title" data-calculator-mode="${tankSizeMode ? "tank_size" : pumpScheduleMode ? "pump_schedule" : drainfieldMode ? "drainfield" : "general"}">
    <img class="calc-landscape" src="${tankSizeMode ? "/images/studio/county-property-v1.webp" : pumpScheduleMode ? "/images/studio/morgan-garden-v1.webp" : drainfieldMode ? "/images/studio/oak-pasture-v1.webp" : "/images/studio/calculator-landscape-v2.webp"}" alt="" width="1672" height="941" fetchpriority="high">
    <div class="calc-input studio-wrap">
        <div class="calc-hero-copy"><p class="studio-overline" data-calc-mode-kicker>${tankSizeMode ? "Capacity planning / Tank size" : pumpScheduleMode ? "Maintenance planning / Pump rhythm" : drainfieldMode ? "Field recovery / Scope estimator" : "Property planning / Cost estimator"}</p>
        @if(result == null && tankSizeResult == null && pumpScheduleResult == null)
            @if(tankSizeMode)<h1 id="calc-title">Size the tank.<br>Keep the context.</h1>@elseif(pumpScheduleMode)<h1 id="calc-title">Set the rhythm.<br>Watch the system.</h1>@elseif(drainfieldMode)<h1 id="calc-title" data-calc-mode-title>Read the field.<br>Price the real path.</h1>@else<h1 id="calc-title" data-calc-mode-title>Plan the project.<br>Understand the range.</h1>@endif
        @else
            @if(tankSizeMode)<h2 id="calc-title">Refine the load.<br>Protect the capacity.</h2>@elseif(pumpScheduleMode)<h2 id="calc-title">Refine the use.<br>Reset the rhythm.</h2>@elseif(drainfieldMode)<h2 id="calc-title" data-calc-mode-title>Refine the field.<br>Protect the low end.</h2>@else<h2 id="calc-title" data-calc-mode-title>Refine your<br>planning estimate.</h2>@endif
        @endif
        <p class="calc-intro" data-calc-mode-intro>${tankSizeMode ? "Translate bedrooms and household use into a conservative capacity band—then verify it against the local file." : pumpScheduleMode ? "Turn tank capacity and household load into a maintenance cadence without pretending the calendar can inspect the system." : drainfieldMode ? "Map soil, saturation, access and the replacement area before treating trench work as the whole job." : "Start with what you know about the property."}</p></div>
        <nav class="calc-mode-nav" aria-label="Calculator modes">
            <a class="${!tankSizeMode && !pumpScheduleMode ? "is-active" : ""}" href="${com.example.septic.web.StudioGuideRoutes.route("/septic-system-cost-calculator/", production)}">Project cost</a>
            <a class="${tankSizeMode ? "is-active" : ""}" href="${com.example.septic.web.StudioGuideRoutes.route("/septic-system-cost-calculator/?mode=tank_size", production)}">Tank capacity</a>
            <a class="${pumpScheduleMode ? "is-active" : ""}" href="${com.example.septic.web.StudioGuideRoutes.route("/septic-system-cost-calculator/?mode=pump_schedule", production)}">Pump schedule</a>
        </nav>
        <form id="cost-estimator-form" name="cost-estimator-form" method="post" action="${com.example.septic.web.StudioGuideRoutes.route("/septic-system-cost-calculator/#result-top", production)}">
                <input type="hidden" name="calculatorMode" value="${estimateForm.getCalculatorMode()}">
                <input type="hidden" name="calculationSubmitted" value="true">
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
                        <a href="${com.example.septic.web.StudioGuideRoutes.route("/official-septic-lookup-tools/", production)}">Back to the property file</a>
                    </div>
                @endif


                    <div class="field-grid field-grid--primary ${tankSizeMode ? "field-grid--tank" : pumpScheduleMode ? "field-grid--pump" : ""}">
                        @if(!pumpScheduleMode)
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
                        @endif

                        @if(tankSizeMode)
                        <input type="hidden" name="projectType" value="new_install">
                        <label class="field">
                            <span>Bedrooms</span>
                            <input type="number" min="1" max="10" name="bedrooms" value="${estimateForm.getBedrooms()}">
                        </label>
                        <label class="field">
                            <span>Household use</span>
                            <select name="occupancyProfile">
                                @for (var profile : occupancyProfiles)
                                    <option value="${profile.value()}" selected="${profile.value().equals(estimateForm.getOccupancyProfile())}">${profile.label()}</option>
                                @endfor
                            </select>
                        </label>
                        @elseif(pumpScheduleMode)
                        <input type="hidden" name="projectType" value="pumping">
                        <label class="field">
                            <span>Tank capacity</span>
                            <input type="number" min="750" max="3000" step="50" name="tankSizeGallons" value="${estimateForm.getTankSizeGallonsValue()}">
                        </label>
                        <label class="field">
                            <span>Occupants</span>
                            <input type="number" min="1" max="20" name="occupants" value="${estimateForm.getOccupantsValue().isBlank() ? "3" : estimateForm.getOccupantsValue()}">
                        </label>
                        <label class="field">
                            <span>Use profile</span>
                            <select name="usageProfile">
                                @for (var profile : usageProfiles)
                                    <option value="${profile.value()}" selected="${profile.value().equals(estimateForm.getUsageProfile())}">${profile.label()}</option>
                                @endfor
                            </select>
                        </label>
                        @else
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
                        @endif

                    </div>



        <div class="calc-submit"><button class="studio-button" type="submit">${tankSizeMode ? "Build my capacity plan" : pumpScheduleMode ? "Set my maintenance rhythm" : "Estimate my project"} <span aria-hidden="true">↗</span></button></div>
                <details class="form-section form-section--secondary calculator-advanced">
                    <summary>
                        <span>
                            <strong data-calc-mode-details>${tankSizeMode ? "Add capacity signals" : pumpScheduleMode ? "Add maintenance signals" : drainfieldMode ? "Define the field constraints" : "Add soil & site details"}</strong>

                        </span>
                        <span aria-hidden="true">+</span>
                    </summary>

                    @if(!tankSizeMode && !pumpScheduleMode)
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
                    @endif

                    <div class="secondary-checks">
                        <label class="checkbox">
                            <input type="checkbox" name="garbageDisposal" checked="${estimateForm.isGarbageDisposal()}">
                            <span>Garbage disposal installed</span>
                        </label>

                        @if(!pumpScheduleMode)
                        <label class="checkbox">
                            <input type="checkbox" name="additionalKitchen" checked="${estimateForm.isAdditionalKitchen()}">
                            <span>Additional kitchen or ADU</span>
                        </label>
                        @endif

                        @if(!tankSizeMode && !pumpScheduleMode)
                        <label class="checkbox">
                            <input type="checkbox" name="highWaterTableOrShallowBedrock" checked="${estimateForm.isHighWaterTableOrShallowBedrock()}">
                            <span data-calc-mode-water>${drainfieldMode ? "Wet spots, surfacing, odor, or high water suspected" : "High water table or shallow bedrock suspected"}</span>
                        </label>

                        <label class="checkbox calc-field-critical calc-field-only">
                            <input type="checkbox" name="noClearReplacementArea" checked="${estimateForm.isNoClearReplacementArea()}">
                            <span><strong>No clear replacement or reserve area identified</strong><small>This is often the point where a field-only quote becomes a layout or redesign problem.</small></span>
                        </label>
                        @endif
                    </div>
                </details>



        <p class="calc-disclaimer">Planning guidance, not a contractor quote. No contact details needed.</p>
        </form>
    </div>
</section>
</details>
@if(result == null && tankSizeResult == null && pumpScheduleResult == null)
@if(tankSizeMode)
<section class="calc-mode-primer studio-wrap" aria-labelledby="tank-primer-title">
    <div><p class="studio-overline">A capacity answer with context</p><h2 id="tank-primer-title">Gallons are the output.<br>Household load is the story.</h2></div>
    <ol><li><span>01</span><div><strong>Start with bedrooms</strong><p>Public rules often begin with bedroom count or bedroom-based design flow.</p></div></li><li><span>02</span><div><strong>Widen for real use</strong><p>Heavy occupancy, disposal use, and an additional kitchen can weaken the smallest plausible tank.</p></div></li><li><span>03</span><div><strong>Verify locally</strong><p>The recorded permit and local reviewer still decide what is acceptable for the property.</p></div></li></ol>
</section>
@elseif(pumpScheduleMode)
<section class="calc-mode-primer studio-wrap" aria-labelledby="pump-primer-title">
    <div><p class="studio-overline">A maintenance rhythm, not a promise</p><h2 id="pump-primer-title">The calendar starts the check.<br>The tank condition ends it.</h2></div>
    <ol><li><span>01</span><div><strong>Capacity sets the buffer</strong><p>A larger tank usually gives the same household more room between service visits.</p></div></li><li><span>02</span><div><strong>Use sets the load</strong><p>Occupants, full-time use, and disposal solids can shorten the practical interval.</p></div></li><li><span>03</span><div><strong>Inspection corrects the estimate</strong><p>Actual sludge, scum, alarms, and operating requirements matter more than a generic reminder.</p></div></li></ol>
</section>
@elseif(drainfieldMode)
<section class="calc-field-primer studio-wrap" aria-labelledby="field-primer-title">
    <p class="studio-overline">Three questions before a quote</p>
    <h2 id="field-primer-title">The trench is visible.<br>The constraint usually is not.</h2>
    <ol><li><span>01</span><div><strong>Can the parcel still carry a field?</strong><p>Soil and water conditions decide whether a conventional replacement is still believable.</p></div></li><li><span>02</span><div><strong>Where can it legally go?</strong><p>The recorded layout, setbacks, reserve area and current bedroom count can change the available footprint.</p></div></li><li><span>03</span><div><strong>Is this field-only scope?</strong><p>Access, distribution, tank condition and restoration can widen what first looked like trench work.</p></div></li></ol>
</section>
@else
<section class="calc-story studio-wrap">
<div><p class="studio-overline">Before you commit</p><h2>The file changes<br>the next question.</h2><p>A property research file puts the permit, layout and open questions in context—before your next conversation with a contractor or agency.</p><a href="${com.example.septic.web.StudioGuideRoutes.route("/septic-record-brief-example/", production)}">See our investigations <span aria-hidden="true">↗</span></a></div>
<a href="${com.example.septic.web.StudioGuideRoutes.route("/septic-record-brief-example/", production)}" aria-label="Explore our property investigations"><img src="/images/studio/buyer-folio-v1.webp" alt="SepticPath property research booklet, illustrative editorial image" width="1672" height="941" loading="lazy"></a>
</section>
@endif
@endif
<div class="calc-followup studio-wrap">
                @if (showQuotePanel)
                    <details class="calc-service-disclosure" id="quote-request" open="${(result == null && tankSizeResult == null && pumpScheduleResult == null) || quoteHasErrors || leadId != null}"><summary>Project service follow-up <span>Separate from property records research</span></summary>
                    <section class="quote-panel" id="service-followup-panel">
                        <div class="quote-panel__header">
                            <div>
                                <span class="eyebrow">Optional next step</span>
                                <h2>${quoteLeadForm.hasActiveProblem() ? "Request follow-up for this septic problem" : "Request help finding local options"}</h2>
                            </div>
                            <span class="pill">Short form</span>
                        </div>

                        @if (result == null && tankSizeResult == null && pumpScheduleResult == null)
                            <article class="status-box">
                                @if (quoteLeadForm.hasActiveProblem())
                                    <strong>Describe the symptom, not your guess at the repair.</strong>
                                    <p>The current symptom, ZIP, and timing make this useful for service follow-up without assuming that pumping, repair, or replacement is already the right scope.</p>
                                @else
                                    <strong>Already know the job type?</strong>
                                    <p>You can submit the project basics now. This stores an inquiry for follow-up; it does not check live contractor inventory or guarantee a response.</p>
                                @endif
                            </article>
                        @endif

                        @if (leadId != null)
                            <article class="status-box status-box--success"
                                     data-ga-event="generate_lead"
                                     data-ga-track-once="quote-request:${leadId}"
                                     data-ga-param-lead-type="quote_request"
                                     data-ga-param-form-id="quote_request_form"
                                     data-ga-param-state-code="${estimateForm.getStateCode()}"
                                     data-ga-param-project-type="${estimateForm.getProjectType()}"
                                     data-ga-param-service-need="${quoteLeadForm.getServiceNeed()}">
                                <strong>Project details received</strong>
                                <p>We saved your request under ID ${leadId}. This confirms submission, not contractor availability or a quote.</p>
                            </article>
                        @endif

                        @if (quoteHasErrors)
                            <article class="status-box status-box--error">
                                <strong>Finish the required fields</strong>
                                <p>Choose what is happening, then complete name, email, phone, ZIP, and consent.</p>
                            </article>
                        @endif

                        @if (quoteLeadForm.hasActiveProblem())
                            <article class="status-box status-box--warning">
                                <strong>This form is not emergency dispatch.</strong>
                                <p>If sewage is entering the home or surfacing where people or animals can contact it, reduce water use and contact a licensed local septic service directly. Submitting here does not guarantee response time.</p>
                            </article>
                        @endif

                        <form class="quote-form" id="quote-request-form" name="quote-request-form" method="post" action="${com.example.septic.web.StudioGuideRoutes.route("/septic-system-cost-calculator/quote/#quote-request", production)}">
                            @if (result != null || tankSizeResult != null || (pumpScheduleResult != null && estimateForm.getStateCode() != null && !estimateForm.getStateCode().isBlank()))
                                <input type="hidden" name="stateCode" value="${quoteLeadForm.getStateCode()}">
                                <input type="hidden" name="projectType" value="${quoteLeadForm.getProjectType()}">
                                <input type="hidden" name="sourcePageHint" value="${quoteLeadForm.getSourcePageHint()}">
                                <input type="hidden" name="bedrooms" value="${quoteLeadForm.getBedrooms()}">
                                <input type="hidden" name="occupants" value="${quoteLeadForm.getOccupantsValue()}">
                            @endif
                            @if (result == null && tankSizeResult == null && (pumpScheduleResult == null || estimateForm.getStateCode() == null || estimateForm.getStateCode().isBlank()))
                                <input type="hidden" name="sourcePageHint" value="${quoteLeadForm.getSourcePageHint()}">
                            @endif
                            <input type="hidden" name="recordStatus" value="${quoteLeadForm.getRecordStatus()}">
                            <input type="hidden" name="soilPercStatus" value="${quoteLeadForm.getSoilPercStatus()}">
                            <input type="hidden" name="accessDifficulty" value="${quoteLeadForm.getAccessDifficulty()}">
                            <input type="hidden" name="garbageDisposal" value="${quoteLeadForm.isGarbageDisposal() ? "true" : "false"}">
                            <input type="hidden" name="additionalKitchen" value="${quoteLeadForm.isAdditionalKitchen() ? "true" : "false"}">
                            <input type="hidden" name="highWaterTableOrShallowBedrock" value="${quoteLeadForm.isHighWaterTableOrShallowBedrock() ? "true" : "false"}">

                            <div class="field-grid">
                                @if (result == null && tankSizeResult == null && (pumpScheduleResult == null || estimateForm.getStateCode() == null || estimateForm.getStateCode().isBlank()))
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
                                    <span>What is happening at the property?</span>
                                    <select name="serviceNeed" required>
                                        <option value="planned_project" selected="${"planned_project".equals(quoteLeadForm.getServiceNeed())}">No active symptom — planning a project</option>
                                        <option value="backup_slow_drains" selected="${"backup_slow_drains".equals(quoteLeadForm.getServiceNeed())}">Backup or persistently slow drains</option>
                                        <option value="surfacing_wastewater" selected="${"surfacing_wastewater".equals(quoteLeadForm.getServiceNeed())}">Wet area or wastewater surfacing outside</option>
                                        <option value="odor" selected="${"odor".equals(quoteLeadForm.getServiceNeed())}">Strong septic odor</option>
                                        <option value="alarm" selected="${"alarm".equals(quoteLeadForm.getServiceNeed())}">Septic or pump alarm</option>
                                        <option value="failed_inspection" selected="${"failed_inspection".equals(quoteLeadForm.getServiceNeed())}">Failed inspection</option>
                                        <option value="repair_recommended" selected="${"repair_recommended".equals(quoteLeadForm.getServiceNeed())}">Repair or replacement was recommended</option>
                                    </select>
                                </label>

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
                                <button class="button button--primary" type="submit">${quoteLeadForm.hasActiveProblem() ? "Request service follow-up" : "Submit project inquiry"}</button>
                                <p>We do not show live provider inventory. Submitting does not guarantee a match, price, or response time.</p>
                            </div>
                        </form>
                    </section>
                    </details>
                @endif


</div>
<section class="calc-reading studio-wrap">
    <div><p class="studio-overline">Keep the file in view</p><h2>Better records.<br>Better questions.</h2><p>See the online research and agency follow-through behind real customer cases.</p><a class="studio-button" href="${com.example.septic.web.StudioGuideRoutes.route("/septic-record-brief-example/", production)}">Explore our investigations <span aria-hidden="true">↗</span></a></div>
    <div>
    @if(calculatorLanding != null)
        <details><summary>When this estimate is the right next step</summary><p>Estimate the cost of a septic project. ${calculatorLanding.targetReader()}</p><ul>@for(var item : calculatorLanding.fitBullets())<li>${item}</li>@endfor</ul></details>
        <details><summary>What widens the range fastest</summary><ul>@for(var item : calculatorLanding.lowEndBreakers())<li>${item}</li>@endfor</ul></details>
        <details><summary>Before you trust the low end</summary><ul>@for(var item : calculatorLanding.quotePrepChecklist())<li>${item}</li>@endfor</ul></details>
    @endif
        <details><summary>Next best pages</summary><p>Need records first?</p><ul>@for(var link : calculatorLandingLinks)<li><a href="${link.path()}">${link.title()}</a><p>${link.note()}</p></li>@endfor<li><a href="${com.example.septic.web.StudioGuideRoutes.route("/septic-records-checklist/", production)}">Septic records checklist</a></li><li><a href="${com.example.septic.web.StudioGuideRoutes.route("/septic-permit-process/", production)}">Septic permit process</a></li><li><a href="${com.example.septic.web.StudioGuideRoutes.route("/septic-system-cost-calculator/?mode=tank_size", production)}" rel="nofollow">Tank capacity mode</a></li><li><a href="${com.example.septic.web.StudioGuideRoutes.route("/septic-system-cost-calculator/?mode=pump_schedule", production)}" rel="nofollow">Pumping schedule mode</a></li><li><a href="${com.example.septic.web.StudioGuideRoutes.route("/septic-tank-size/", production)}">Tank size guide</a></li></ul></details>
    </div>
</section>
</main>
<script src="${com.example.septic.web.AssetPaths.versioned("/studio-calculator.js")}" defer></script>
`)
