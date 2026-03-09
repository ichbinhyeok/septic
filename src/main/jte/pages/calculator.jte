@import com.example.septic.data.model.StateProfile
@import com.example.septic.service.AccessDifficulty
@import com.example.septic.service.EstimatorResult
@import com.example.septic.service.ProjectType
@import com.example.septic.service.SoilPercStatus
@import com.example.septic.service.TimelinePreference
@import com.example.septic.web.EstimateForm
@import com.example.septic.web.PageMeta
@param PageMeta page
@param java.util.List<StateProfile> states
@param EstimateForm estimateForm
@param EstimatorResult result = null
@param ProjectType[] projectTypes
@param SoilPercStatus[] soilStatuses
@param AccessDifficulty[] accessLevels
@param TimelinePreference[] timelines

@template.layouts.app(page = page, content = @`
    <section class="section section--heroish">
        <div class="container section-heading">
            <span class="eyebrow">Main calculator</span>
            <h1>Estimate likely size, system class, and septic project budget.</h1>
            <p class="lede">Use this for early planning. The result widens when soil data, site conditions, or local rules are still unclear.</p>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container calculator-layout">
            <form class="calculator-form" method="post" action="/septic-system-cost-calculator/">
                <div class="field-grid">
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

                    <label class="field">
                        <span>Timeline</span>
                        <select name="timeline">
                            @for (var timeline : timelines)
                                <option value="${timeline.value()}" selected="${timeline.value().equals(estimateForm.getTimeline())}">${timeline.label()}</option>
                            @endfor
                        </select>
                    </label>

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

                <div class="form-actions">
                    <button class="button button--primary" type="submit">Show planning estimate</button>
                    <p>Estimate only. Final sizing and design still need local review.</p>
                </div>
            </form>

            <div class="calculator-output">
                @if (result != null)
                    @template.tags.estimateResult(result = result)
                @else
                    <article class="placeholder-panel">
                        <span class="eyebrow">Result preview</span>
                        <h2>Fill the form to generate a planning range.</h2>
                        <p>You will get a conservative tank range, likely system class, cost range, and a short checklist for the next step.</p>
                    </article>
                @endif
            </div>
        </div>
    </section>
`)
