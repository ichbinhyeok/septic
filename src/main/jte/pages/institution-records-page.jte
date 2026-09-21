@import com.example.septic.data.model.SourceRecord
@import com.example.septic.data.model.StateMoneyPage
@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.PageMeta
@import com.example.septic.web.InstitutionRecordsExperience
@import com.example.septic.web.InstitutionRouteRule
@import com.example.septic.web.AssetPaths
@param PageMeta page
@param StateMoneyPage stateMoneyPage
@param StateProfile state
@param InstitutionRecordsExperience institutionExperience
@param java.util.List<com.example.septic.web.PageLink> countyRecordLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> featuredCountyRecordLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> internalLinks = java.util.List.of()
@param java.util.List<SourceRecord> sources = java.util.List.of()
@param String editorialLastReviewedAt = ""

@template.layouts.app(page = page, designFamily = "regional", pageStyles = "/institution-records.css", content = @`
<div class="institution-records" data-institution-records data-state-code="${institutionExperience.stateCode()}" data-agency="${institutionExperience.agencyLabel()}" data-program="${institutionExperience.programLabel()}" data-request-recipient="${institutionExperience.requestRecipient()}" data-request-subject="${institutionExperience.requestSubject()}">
    <div hidden data-institution-route-catalog>
        @for(var route : institutionExperience.routeRules())
            <span data-institution-route-rule data-route-key="${route.key()}" data-area-matches="${String.join("|", route.areaMatches())}" data-requester-relation="${route.requesterRelation()}" data-minimum-year="${route.minimumYear() == null ? "" : route.minimumYear().toString()}" data-maximum-year="${route.maximumYear() == null ? "" : route.maximumYear().toString()}" data-custodian="${route.custodian()}" data-route-note="${route.routeNote()}" data-primary-url="${route.primaryUrl()}" data-primary-label="${route.primaryLabel()}" data-secondary-url="${route.secondaryUrl()}" data-secondary-label="${route.secondaryLabel()}" data-request-recipient="${route.requestRecipient()}" data-contact-mode="${route.contactMode()}" data-artifact-heading="${route.artifactHeading()}" data-artifact-label="${route.artifactLabel()}"></span>
        @endfor
    </div>
    <section class="institution-hero" aria-labelledby="institution-title">
        <img src="${institutionExperience.heroImage()}" alt="" width="1672" height="941" fetchpriority="high">
        <div class="institution-hero__veil"></div>
        <div class="container institution-hero__inner">
            <p class="institution-overline">${institutionExperience.agencyLabel()} · ${state.stateName()} property records</p>
            <h1 id="institution-title">${institutionExperience.heroTitle()}</h1>
            <p class="institution-hero__intro">${institutionExperience.heroIntro()}</p>
            <div class="institution-hero__actions">
                <a class="institution-button" href="#start-record-route" data-track-click="nav" data-track-source-context="institution_hero_start_${state.stateCode().toLowerCase(java.util.Locale.ROOT)}" data-track-target-type="request_builder">Start with my property <span aria-hidden="true">↓</span></a>
                <a href="/offer-prep-septic-file-check/?mode=research" data-track-click="nav" data-track-source-context="institution_hero_help_${state.stateCode().toLowerCase(java.util.Locale.ROOT)}" data-track-target-type="lead_form">Ask SepticPath to handle it <span aria-hidden="true">↗</span></a>
            </div>
            <p class="institution-hero__promise"><span aria-hidden="true">✓</span>${institutionExperience.outcomePromise()}</p>
        </div>
    </section>

    <section class="institution-start" id="start-record-route" aria-labelledby="institution-start-title">
        <div class="container institution-start__layout">
            <header>
                <p class="institution-overline">Property research desk</p>
                <h2 id="institution-start-title">Bring the clues.<br>We’ll prepare the route.</h2>
                <p>${institutionExperience.searchInstruction()}</p>
            </header>
            <form class="institution-form" data-institution-form novalidate>
                <label class="institution-form__wide" for="institution-address-${state.stateCode().toLowerCase()}">Property address <span>Required</span>
                    <input id="institution-address-${state.stateCode().toLowerCase()}" name="address" autocomplete="street-address" placeholder="Street address, city, state, ZIP" required data-institution-address>
                </label>
                <label for="institution-area-${state.stateCode().toLowerCase()}">${institutionExperience.areaLabel()}
                    <input id="institution-area-${state.stateCode().toLowerCase()}" name="area" placeholder="${institutionExperience.areaPlaceholder()}" data-institution-area>
                </label>
                <p class="institution-location-status institution-form__wide" data-institution-location-status hidden aria-live="polite"><span data-institution-location-message></span><a href="/septic-records-by-county/" data-institution-location-action hidden data-track-click="nav" data-track-source-context="institution_location_recovery_${state.stateCode().toLowerCase(java.util.Locale.ROOT)}" data-track-target-type="state_records">Continue to the matching records page <span aria-hidden="true">→</span></a></p>
                <label for="institution-parcel-${state.stateCode().toLowerCase()}">${institutionExperience.parcelLabel()}
                    <input id="institution-parcel-${state.stateCode().toLowerCase()}" name="parcel" placeholder="${institutionExperience.parcelPlaceholder()}" data-institution-parcel>
                </label>
                <label for="institution-owner-${state.stateCode().toLowerCase()}">Owner or applicant clue
                    <input id="institution-owner-${state.stateCode().toLowerCase()}" name="owner" placeholder="${institutionExperience.ownerPlaceholder()}" data-institution-owner>
                </label>
                <label for="institution-year-${state.stateCode().toLowerCase()}">Year clue
                    <input id="institution-year-${state.stateCode().toLowerCase()}" name="year" inputmode="numeric" placeholder="${institutionExperience.yearPlaceholder()}" data-institution-year>
                </label>
                @if ("AL".equals(state.stateCode()))
                <fieldset class="institution-form__wide institution-relation">
                    <legend>Your relationship to the property</legend>
                    <div class="institution-statuses institution-statuses--compact">
                        <label><input type="radio" name="requester_relation" value="owner_agent" checked><span>Owner or authorized agent</span></label>
                        <label><input type="radio" name="requester_relation" value="non_owner"><span>Neither</span></label>
                    </div>
                    <p>Alabama publishes different record routes for owners or agents and other requesters.</p>
                </fieldset>
                @endif
                <fieldset class="institution-form__wide">
                    <legend>Where are you now?</legend>
                    <div class="institution-statuses">
                        <label><input type="radio" name="status" value="not_started" checked><span>I have not searched yet</span></label>
                        <label><input type="radio" name="status" value="no_result"><span>The search returned nothing</span></label>
                        <label><input type="radio" name="status" value="incomplete"><span>I found an incomplete file</span></label>
                        <label><input type="radio" name="status" value="have_file"><span>I have a file to understand</span></label>
                    </div>
                </fieldset>
                <div class="institution-form__submit institution-form__wide">
                    <button class="institution-button" type="submit">Prepare my record route <span aria-hidden="true">→</span></button>
                    <p>Used only to confirm the county through the U.S. Census. Not saved or sent to analytics.</p>
                </div>
                <p class="institution-form__error institution-form__wide" data-institution-error role="alert" hidden>Enter the complete property address so the route can be prepared.</p>
            </form>
        </div>
    </section>

    <section class="institution-result" data-institution-result hidden aria-live="polite" aria-labelledby="institution-result-title">
        <div class="container">
            <header class="institution-result__header">
                <div><p class="institution-overline" data-institution-result-kicker>Route prepared</p><h2 id="institution-result-title" tabindex="-1" data-institution-result-title>Your ${institutionExperience.agencyLabel()} route is ready.</h2><p data-institution-result-summary></p></div>
                <button type="button" class="institution-text-button" data-institution-edit>Edit property details</button>
            </header>
            <div class="institution-result__custodian"><span>Working custodian</span><strong data-institution-custodian>${institutionExperience.requestRecipient()}</strong><p data-institution-route-note>${institutionExperience.searchInstruction()}</p></div>
            <ol class="institution-result__steps">
                <li><span>01</span><div><h3>Carry these match keys</h3><ul data-institution-clues></ul><button type="button" class="institution-text-button" data-institution-copy-clues>Copy match keys</button></div></li>
                <li><span>02</span><div><h3 data-institution-action-heading>Open the official route</h3><p data-institution-action-copy>${institutionExperience.searchInstruction()}</p><div class="institution-result__actions"><a class="institution-button" href="${institutionExperience.officialUrl()}" target="_blank" rel="noreferrer" data-institution-primary-route data-track-click="official_source" data-track-source-context="institution_result_primary_${state.stateCode().toLowerCase(java.util.Locale.ROOT)}" data-track-target-type="official_source">${institutionExperience.officialLabel()} <span aria-hidden="true">↗</span></a><a href="${institutionExperience.fallbackUrl()}" target="_blank" rel="noreferrer" data-institution-secondary-route data-track-click="official_source" data-track-source-context="institution_result_fallback_${state.stateCode().toLowerCase(java.util.Locale.ROOT)}" data-track-target-type="official_source">${institutionExperience.fallbackLabel()} <span aria-hidden="true">↗</span></a></div></div></li>
                <li><span>03</span><div><h3 data-institution-artifact-heading>Ask for the complete property file</h3><ul>@for(var document : institutionExperience.documents())<li>${document}</li>@endfor</ul></div></li>
            </ol>
            <div class="institution-request">
                <div><p class="institution-overline">Ready-to-review wording</p><h3 data-institution-artifact-title>Use a complete request the first time.</h3><p>This wording is prepared from the clues above. Review it before using it; copying it does not submit anything.</p></div>
                <div><label for="institution-request-${state.stateCode().toLowerCase()}" data-institution-artifact-label>Request draft</label><textarea id="institution-request-${state.stateCode().toLowerCase()}" rows="12" readonly data-institution-request></textarea><div class="institution-request__actions"><button type="button" class="institution-button" data-institution-copy-request>Copy wording</button><a href="/offer-prep-septic-file-check/?mode=research" data-track-click="nav" data-track-source-context="institution_result_help_${state.stateCode().toLowerCase(java.util.Locale.ROOT)}" data-track-target-type="lead_form">Have SepticPath investigate instead <span aria-hidden="true">↗</span></a></div></div>
            </div>
        </div>
    </section>

    <section class="institution-break" aria-labelledby="institution-break-title">
        <div class="container institution-break__layout">
            <div><p class="institution-overline">When the easy search breaks</p><h2 id="institution-break-title">No result is<br>not one conclusion.</h2></div>
            <div><p class="institution-break__lead">${institutionExperience.noResultExplanation()}</p><ol>@for(int i = 0; i < institutionExperience.routeSteps().size(); i++)<li><span>0${i + 1}</span><p>${institutionExperience.routeSteps().get(i)}</p></li>@endfor</ol></div>
        </div>
    </section>

    <section class="institution-proof" aria-labelledby="institution-proof-title">
        <div class="institution-proof__visual">
            @if ("AL".equals(state.stateCode()))
                <img src="/images/case-study/redacted/agency-approval-letter-preview.webp" alt="An anonymized agency approval letter returned during a septic record request" width="1200" height="1500" loading="lazy">
            @else
                <img src="/images/studio/official-lookup-files-v1.webp" alt="Property record research materials arranged for review" width="1672" height="941" loading="lazy">
            @endif
        </div>
        <div class="institution-proof__copy"><p class="institution-overline">What becomes useful</p><h2 id="institution-proof-title">${institutionExperience.proofTitle()}</h2><p>${institutionExperience.proofBody()}</p><ul>@for(var document : institutionExperience.documents().stream().limit(3).toList())<li>${document}</li>@endfor</ul><a href="/septic-record-brief-example/">See a completed record brief <span aria-hidden="true">↗</span></a></div>
    </section>

    @if (!countyRecordLinks.isEmpty())
    <section class="institution-counties" id="county-routes" aria-labelledby="institution-counties-title">
        <div class="container institution-counties__heading"><div><p class="institution-overline">Local file owners</p><h2 id="institution-counties-title">Go straight to the county route.</h2></div><p>Use a local route when the property location already identifies the office, portal, form, or request channel.</p></div>
        <div class="container institution-counties__links">@for(var link : countyRecordLinks.stream().limit(8).toList())<a href="${link.path()}"><span>${link.compactTitle()}</span><b aria-hidden="true">↗</b></a>@endfor</div>
        @if (countyRecordLinks.size() > 8)<div class="container"><details><summary>Browse all ${countyRecordLinks.size()} published ${state.stateName()} county routes <span aria-hidden="true">+</span></summary><div class="institution-counties__all">@for(var link : countyRecordLinks.stream().skip(8).toList())<a href="${link.path()}">${link.compactTitle()}</a>@endfor</div></details></div>@endif
    </section>
    @endif

    <section class="institution-related" aria-labelledby="institution-related-title"><div class="container"><div><p class="institution-overline">Continue the property decision</p><h2 id="institution-related-title">Use the file<br>for the next question.</h2></div><nav aria-label="Related ${state.stateName()} septic guidance"><a href="/drain-field-replacement-cost/${state.slug()}/"><span>${state.stateName()} drain-field scope</span><small>Use the recovered layout and site history before treating field work as a generic replacement.</small><b aria-hidden="true">↗</b></a><a href="/failed-perc-test-septic/${state.slug()}/"><span>Failed site or soil result</span><small>Separate a test result from the design, approval, and parcel questions that follow.</small><b aria-hidden="true">↗</b></a><a href="/septic-replacement-area/${state.slug()}/"><span>Replacement-area review</span><small>Check whether the old file identifies a reserve area before planning the next use.</small><b aria-hidden="true">↗</b></a>@for(var link : internalLinks.stream().limit(3).toList())<a href="${link.path()}"><span>${link.compactTitle()}</span><small>${link.note()}</small><b aria-hidden="true">↗</b></a>@endfor</nav></div></section>

    <section class="institution-sources">
        <div class="container institution-sources__layout"><div><p class="institution-overline">Official-source context</p><h2>Trace the route.</h2></div><div><p><strong>Independent service.</strong> SepticPath is not affiliated with ${institutionExperience.agencyLabel()} and cannot certify permit existence, compliance, location, or current system condition. Route review: ${editorialLastReviewedAt}.</p><nav aria-label="Official ${state.stateName()} record sources"><a href="${institutionExperience.officialUrl()}" target="_blank" rel="noreferrer">${institutionExperience.officialLabel()} ↗</a><a href="${institutionExperience.fallbackUrl()}" target="_blank" rel="noreferrer">${institutionExperience.fallbackLabel()} ↗</a>@for(var source : sources.stream().limit(4).toList())<a href="${source.url()}" target="_blank" rel="noreferrer">${source.title()} ↗</a>@endfor</nav></div></div>
    </section>

    <section class="institution-final" aria-labelledby="institution-final-title"><img src="/images/studio/checklist-closing-house-v1.webp" alt="" width="1774" height="887" loading="lazy"><div class="container"><p class="institution-overline">One property. One unanswered question.</p><h2 id="institution-final-title">We can take it<br>from here.</h2><p>Send the address, what you already tried, and the decision you need to make.</p><a class="institution-button" href="/offer-prep-septic-file-check/?mode=research" data-track-click="nav" data-track-source-context="institution_final_${state.stateCode().toLowerCase(java.util.Locale.ROOT)}" data-track-target-type="lead_form">Ask SepticPath to investigate <span aria-hidden="true">↗</span></a></div></section>
</div>
<script src="${AssetPaths.versioned("/institution-records.js")}" defer></script>
`)
