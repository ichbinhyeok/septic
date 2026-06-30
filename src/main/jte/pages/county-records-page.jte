@import com.example.septic.data.model.CountyRecordsPage
@import com.example.septic.data.model.SourceRecord
@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.EditorialProfile
@import com.example.septic.web.CountyAvailabilityRowView
@import com.example.septic.web.CountyAvailabilitySummaryView
@import com.example.septic.web.CountyWorkflowStructureView
@import com.example.septic.web.CountyIntentRouteView
@import com.example.septic.web.CountyRequestBuilderOptionView
@import com.example.septic.web.PageLink
@import com.example.septic.web.PageMeta
@param PageMeta page
@param CountyRecordsPage countyPage
@param StateProfile state
@param java.util.List<SourceRecord> sources
@param CountyAvailabilitySummaryView countyAvailabilitySummary = null
@param java.util.List<CountyAvailabilityRowView> countyAvailabilityRows = java.util.List.of()
@param CountyWorkflowStructureView countyWorkflowStructure = null
@param java.util.List<CountyIntentRouteView> countyIntentRoutes = java.util.List.of()
@param java.util.List<CountyRequestBuilderOptionView> countyRequestOptions = java.util.List.of()
@param java.util.List<PageLink> internalLinks = java.util.List.of()
@param java.util.List<PageLink> featuredInternalLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.CountyFinderLinkView> siblingCountyRoutes = java.util.List.of()
@param EditorialProfile editorialPreparedBy = null
@param EditorialProfile editorialReviewedBy = null
@param String editorialReviewedAgainst = ""
@param String editorialLastReviewedAt = ""
@param String editorialNote = ""

@template.layouts.app(page = page, content = @`
    <div class="county-records-page">
    <section class="section section--heroish">
        <div class="container state-hero">
            <div>
                <span class="eyebrow">${state.stateCode()} county records page</span>
                <h1>${countyPage.title()}</h1>
                <div class="county-command-bar" aria-label="Immediate county records actions">
                    @if (countyPage.hasParcelAnchor())
                        <a class="button button--secondary"
                           href="${countyPage.parcelAnchorUrl()}"
                           target="_blank"
                           rel="noreferrer"
                           data-track-click="nav"
                           data-track-source-context="county_records_command_parcel_anchor"
                           data-track-target-type="official_source">Find parcel/TMS</a>
                    @endif
                    <a class="button button--primary"
                       href="${countyPage.recordsUrl()}"
                       target="_blank"
                       rel="noreferrer"
                       data-track-click="nav"
                       data-track-source-context="county_records_command_records"
                       data-track-target-type="official_source">Open county record path</a>
                    @if (countyPage.hasActionPlaybook())
                        <a class="button button--secondary"
                           href="#county-action-playbook"
                           data-track-click="nav"
                           data-track-source-context="county_records_command_playbook"
                           data-track-target-type="page_anchor">Copy request script</a>
                    @endif
                    <a class="button button--secondary"
                       href="/septic-records-checklist/${state.slug()}/"
                       data-track-click="nav"
                       data-track-source-context="county_records_command_state"
                       data-track-target-type="state_money_page">${state.stateCode()} state route</a>
                </div>
                <div class="money-answer-panel" aria-label="County answer-first workflow">
                    <div class="money-answer-panel__header">
                        <span class="eyebrow">County file first</span>
                        <h2>Do these before you trust a quote.</h2>
                    </div>
                    <ol class="money-answer-steps">
                        @if (countyPage.hasParcelAnchor())
                            <li>
                                <span class="money-answer-steps__number">1</span>
                                <div>
                                    <strong>Capture the parcel anchor</strong>
                                    <p>${countyPage.parcelAnchorLabel()}</p>
                                </div>
                            </li>
                            <li>
                                <span class="money-answer-steps__number">2</span>
                                <div>
                                    <strong>Open the septic record path</strong>
                                    <p>${countyPage.recordsLabel()}</p>
                                </div>
                            </li>
                            <li>
                                <span class="money-answer-steps__number">3</span>
                                <div>
                                    <strong>Verify the owning office</strong>
                                    <p>${countyPage.officeLabel()}</p>
                                </div>
                            </li>
                            <li>
                                <span class="money-answer-steps__number">4</span>
                                <div>
                                    <strong>Price only after the file is clearer</strong>
                                    @if (countyWorkflowStructure != null && countyWorkflowStructure.quoteGate() != null && !countyWorkflowStructure.quoteGate().isBlank())
                                        <p>${countyWorkflowStructure.quoteGate()}</p>
                                    @else
                                        <p>${countyPage.contactLine()}</p>
                                    @endif
                                </div>
                            </li>
                        @else
                        <li>
                            <span class="money-answer-steps__number">1</span>
                            <div>
                                <strong>Open the county record path</strong>
                                <p>${countyPage.recordsLabel()}</p>
                            </div>
                        </li>
                        <li>
                            <span class="money-answer-steps__number">2</span>
                            <div>
                                <strong>Verify the owning office</strong>
                                <p>${countyPage.officeLabel()}</p>
                            </div>
                        </li>
                        <li>
                            <span class="money-answer-steps__number">3</span>
                            <div>
                                <strong>Price only after the file is clearer</strong>
                                @if (countyWorkflowStructure != null && countyWorkflowStructure.quoteGate() != null && !countyWorkflowStructure.quoteGate().isBlank())
                                    <p>${countyWorkflowStructure.quoteGate()}</p>
                                @else
                                    <p>${countyPage.contactLine()}</p>
                                @endif
                            </div>
                        </li>
                        @endif
                    </ol>
                </div>
                <div class="hero__actions">
                    @if (countyPage.hasParcelAnchor())
                        <a class="button button--secondary"
                           href="${countyPage.parcelAnchorUrl()}"
                           target="_blank"
                           rel="noreferrer"
                           data-track-click="nav"
                           data-track-source-context="county_records_primary_parcel_anchor"
                           data-track-target-type="official_source">${countyPage.parcelAnchorLabel()}</a>
                    @endif
                    <a class="button button--primary"
                       href="${countyPage.recordsUrl()}"
                       target="_blank"
                       rel="noreferrer"
                       data-track-click="nav"
                       data-track-source-context="county_records_primary_records"
                       data-track-target-type="official_source">${countyPage.recordsLabel()}</a>
                    <a class="button button--secondary"
                       href="${countyPage.officeUrl()}"
                       target="_blank"
                       rel="noreferrer"
                       data-track-click="nav"
                       data-track-source-context="county_records_primary_office"
                       data-track-target-type="official_source">${countyPage.officeLabel()}</a>
                    <a class="button button--secondary"
                       href="/septic-records-checklist/${state.slug()}/"
                       data-track-click="nav"
                       data-track-source-context="county_records_back_to_state"
                       data-track-target-type="state_money_page">Open ${state.stateName()} records lookup</a>
                </div>
                <p class="lede">${countyPage.introCopy()}</p>
                @if (!featuredInternalLinks.isEmpty())
                    <div class="quick-link-strip" aria-label="Fast next steps">
                        <span class="quick-link-strip__label">Next best state pages</span>
                        @for (var link : featuredInternalLinks)
                            <a class="button button--secondary quick-link-strip__button"
                               href="${link.path()}"
                               data-track-click="nav"
                               data-track-source-context="county_records_featured_link"
                               data-track-target-type="related_internal">${link.title()}</a>
                        @endfor
                    </div>
                @endif
            </div>

            <article class="hero-sidecard">
                <span class="pill">County-specific workflow</span>
                <strong>${countyPage.countyName()}, ${state.stateCode()}</strong>
                <strong>Records-first wedge</strong>
                <dl class="review-meta">
                    <div>
                        <dt>Prepared by</dt>
                        <dd>
                            <span class="review-meta__name">${editorialPreparedBy.displayName()}</span>
                            <span>${editorialPreparedBy.roleTitle()}</span>
                            <span>${editorialPreparedBy.focusSummary()}</span>
                        </dd>
                    </div>
                    <div>
                        <dt>Reviewed by</dt>
                        <dd>
                            <span class="review-meta__name">${editorialReviewedBy.displayName()}</span>
                            <span>${editorialReviewedBy.roleTitle()}</span>
                            <span>${editorialReviewedBy.focusSummary()}</span>
                        </dd>
                    </div>
                    <div>
                        <dt>Reviewed against</dt>
                        <dd>${editorialReviewedAgainst}</dd>
                    </div>
                    <div>
                        <dt>Last reviewed</dt>
                        <dd>${editorialLastReviewedAt}</dd>
                    </div>
                </dl>
                <p>${editorialNote}</p>
            </article>
        </div>
    </section>

    <div data-sticky-trigger="county-records-after-hero" aria-hidden="true"></div>

    @if (countyAvailabilitySummary != null && !countyAvailabilityRows.isEmpty())
        <section class="section section--tight">
            <div class="container">
                <div class="county-data-console">
                    <div class="county-data-console__header">
                        <div>
                            <span class="eyebrow">County record availability matrix</span>
                            <h2>${countyPage.countyName()} file path, request method, and confidence score</h2>
                            <p>${countyAvailabilitySummary.confidenceNote()}</p>
                        </div>
                        <div class="county-confidence-meter" style="--confidence-score: ${countyAvailabilitySummary.confidenceScore()}%;">
                            <span class="county-confidence-meter__label">${countyAvailabilitySummary.confidenceLabel()}</span>
                            <strong>${countyAvailabilitySummary.confidenceScore()}%</strong>
                            <span class="county-confidence-meter__track"><span></span></span>
                        </div>
                    </div>

                    <div class="county-data-strip" aria-label="County record route metrics">
                        <div>
                            <span>Primary route</span>
                            <strong>${countyAvailabilitySummary.primaryRouteLabel()}</strong>
                        </div>
                        <div>
                            <span>Request method</span>
                            <strong>${countyAvailabilitySummary.requestMethodLabel()}</strong>
                        </div>
                        <div>
                            <span>First artifact</span>
                            <strong>${countyAvailabilitySummary.firstArtifactLabel()}</strong>
                        </div>
                        <div>
                            <span>Evidence depth</span>
                            <strong>${countyAvailabilitySummary.sourceDepthLabel()}</strong>
                        </div>
                    </div>

                    <div class="county-availability-matrix" role="list" aria-label="County septic record availability rows">
                        @for (var row : countyAvailabilityRows)
                            <article class="county-availability-row county-availability-row--${row.statusTone()}" role="listitem">
                                <div>
                                    <span class="county-availability-row__label">${row.artifactLabel()}</span>
                                    <strong>${row.availabilityLabel()}</strong>
                                </div>
                                <div>
                                    <span>Method</span>
                                    <strong>${row.methodLabel()}</strong>
                                </div>
                                <p>${row.evidenceNote()}</p>
                                @if (row.external())
                                    <a class="button button--secondary"
                                       href="${row.actionUrl()}"
                                       target="_blank"
                                       rel="noreferrer"
                                       data-track-click="nav"
                                       data-track-source-context="county_availability_matrix"
                                       data-track-target-type="${row.targetType()}">${row.actionLabel()}</a>
                                @else
                                    <a class="button button--secondary"
                                       href="${row.actionUrl()}"
                                       data-track-click="nav"
                                       data-track-source-context="county_availability_matrix"
                                       data-track-target-type="${row.targetType()}">${row.actionLabel()}</a>
                                @endif
                            </article>
                        @endfor
                    </div>
                </div>
            </div>
        </section>
    @endif

    @if (!countyRequestOptions.isEmpty())
        <section class="section section--tight">
            <div class="container">
                <div class="county-request-builder" data-choice-group>
                    <div class="county-request-builder__intro">
                        <span class="eyebrow">Permit file request builder</span>
                        <h2>Turn the county page into the exact request you send.</h2>
                        <p>Choose the job context, then send the county a request that asks for the artifacts that actually change pricing, buyer risk, or permit scope.</p>
                    </div>
                    <div class="county-request-builder__tabs" role="group" aria-label="Choose request context">
                        @for (var option : countyRequestOptions)
                            <button type="button"
                                    class="county-request-builder__tab ${"buyer".equals(option.key()) ? "is-active" : ""}"
                                    data-choice="${option.key()}"
                                    aria-pressed="${"buyer".equals(option.key()) ? "true" : "false"}">${option.label()}</button>
                        @endfor
                    </div>
                    @for (var option : countyRequestOptions)
                        <article class="county-request-panel ${"buyer".equals(option.key()) ? "is-active" : ""}"
                                 data-choice-panel="${option.key()}">
                            <div class="county-request-panel__script">
                                <span class="county-request-panel__label">Subject</span>
                                <h3>${option.subjectLine()}</h3>
                                @for (var line : option.scriptLines())
                                    <p>${line}</p>
                                @endfor
                            </div>
                            <div class="county-request-panel__checklist">
                                <span class="county-request-panel__label">Attach or ask for</span>
                                <ul class="detail-list">
                                    @for (var item : option.checklist())
                                        <li>${item}</li>
                                    @endfor
                                </ul>
                                @if (option.external())
                                    <a class="button button--primary"
                                       href="${option.actionUrl()}"
                                       target="_blank"
                                       rel="noreferrer"
                                       data-track-click="nav"
                                       data-track-source-context="county_request_builder"
                                       data-track-target-type="official_source">${option.actionLabel()}</a>
                                @else
                                    <a class="button button--primary"
                                       href="${option.actionUrl()}"
                                       data-track-click="nav"
                                       data-track-source-context="county_request_builder"
                                       data-track-target-type="internal_page">${option.actionLabel()}</a>
                                @endif
                            </div>
                        </article>
                    @endfor
                </div>
            </div>
        </section>
    @endif

    <section class="section section--tight">
        <div class="container">
            <div class="county-file-workflow">
                <div class="county-file-workflow__header">
                    <span class="eyebrow">Five-minute file workflow</span>
                    <h2>Use this page as a work surface, not just a reference page.</h2>
                    <p>Open the county path, capture the parcel clue, ask for the file artifacts, then move only to the state workflow or cost estimate after the record story is clearer.</p>
                </div>
                <ol class="county-file-workflow__steps">
                    <li>
                        <span>01</span>
                        <strong>Open the county record path</strong>
                        <p>${countyPage.recordsLabel()}</p>
                    </li>
                    <li>
                        <span>02</span>
                        <strong>Anchor the parcel or property</strong>
                        @if (countyPage.hasParcelAnchor())
                            <p>${countyPage.parcelAnchorLabel()}</p>
                        @else
                            <p>Use address, parcel identifier, owner name, or local office routing before relying on a price.</p>
                        @endif
                    </li>
                    <li>
                        <span>03</span>
                        <strong>Request the artifacts that change the answer</strong>
                        @if (countyPage.recordsToRequest() != null && !countyPage.recordsToRequest().isEmpty())
                            <p>${countyPage.recordsToRequest().get(0)}</p>
                        @else
                            <p>Ask for permit, as-built, inspection, repair, and final approval records tied to the parcel.</p>
                        @endif
                    </li>
                    <li>
                        <span>04</span>
                        <strong>Stop pricing if the file is not clear</strong>
                        @if (countyWorkflowStructure != null && countyWorkflowStructure.quoteGate() != null && !countyWorkflowStructure.quoteGate().isBlank())
                            <p>${countyWorkflowStructure.quoteGate()}</p>
                        @else
                            <p>${countyPage.lowEndBreakers() != null && !countyPage.lowEndBreakers().isEmpty() ? countyPage.lowEndBreakers().get(0) : countyPage.contactLine()}</p>
                        @endif
                    </li>
                </ol>
            </div>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container">
            <div class="county-search-intent-pack">
                <div class="county-search-intent-pack__intro">
                    <span class="eyebrow">Search intent answer pack</span>
                    <h2>${countyPage.countyName()} septic permit lookup, records request, and address search path</h2>
                    <p>Use this block when the search is not a broad septic question. It is usually one of four file tasks: find the permit, request the records, anchor the parcel, or confirm the as-built and inspection trail.</p>
                </div>
                <div class="county-search-intent-grid">
                    <article class="county-search-intent-card">
                        <span>Permit lookup</span>
                        <h3>${countyPage.countyName()} septic permit lookup</h3>
                        <p>Start with ${countyPage.recordsLabel()} before you trust a quote, repair story, buyer file, or permit closeout claim.</p>
                        <a class="button button--secondary"
                           href="${countyPage.recordsUrl()}"
                           target="_blank"
                           rel="noreferrer"
                           data-track-click="nav"
                           data-track-source-context="county_search_intent_permit_lookup"
                           data-track-target-type="official_source">Open permit lookup path</a>
                    </article>

                    <article class="county-search-intent-card">
                        <span>Records request</span>
                        <h3>Request the septic records, not just a price</h3>
                        <p>${countyPage.requestScriptBody() != null && !countyPage.requestScriptBody().isBlank() ? countyPage.requestScriptBody() : countyPage.contactLine()}</p>
                        <a class="button button--secondary"
                           href="#county-action-playbook"
                           data-track-click="nav"
                           data-track-source-context="county_search_intent_records_request"
                           data-track-target-type="page_anchor">Open request script</a>
                    </article>

                    <article class="county-search-intent-card">
                        <span>Address or parcel</span>
                        <h3>Search by address only after you have the parcel anchor</h3>
                        @if (countyPage.hasParcelAnchor())
                            <p>${countyPage.parcelAnchorNote()}</p>
                            <a class="button button--secondary"
                               href="${countyPage.parcelAnchorUrl()}"
                               target="_blank"
                               rel="noreferrer"
                               data-track-click="nav"
                               data-track-source-context="county_search_intent_parcel_anchor"
                               data-track-target-type="official_source">${countyPage.parcelAnchorLabel()}</a>
                        @else
                            <p>Use the state records path first, then confirm the parcel identifier with the local office before pricing.</p>
                            <a class="button button--secondary"
                               href="/septic-permit-search-by-address/"
                               data-track-click="nav"
                               data-track-source-context="county_search_intent_address_fallback"
                               data-track-target-type="internal_page">Open search-by-address guide</a>
                        @endif
                    </article>

                    <article class="county-search-intent-card">
                        <span>As-built and inspection</span>
                        <h3>Ask for the file artifacts that change the answer</h3>
                        <p>
                            @if (countyPage.recordsToRequest() != null && !countyPage.recordsToRequest().isEmpty())
                                ${countyPage.recordsToRequest().get(0)}
                            @else
                                Ask for the permit file, as-built sketch, inspection letter, repair note, and any final approval record tied to the parcel.
                            @endif
                        </p>
                        <a class="button button--secondary"
                           href="/septic-as-built-records/"
                           data-track-click="nav"
                           data-track-source-context="county_search_intent_as_built"
                           data-track-target-type="internal_page">Open as-built records guide</a>
                    </article>
                </div>
            </div>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container">
            <div class="intent-rail">
                @if (countyPage.hasParcelAnchor())
                    <article class="intent-card">
                        <span class="intent-card__label">Capture parcel/TMS first</span>
                        <h2>${countyPage.parcelAnchorLabel()}</h2>
                        <p>${countyPage.parcelAnchorNote()}</p>
                        <a class="button button--secondary"
                           href="${countyPage.parcelAnchorUrl()}"
                           target="_blank"
                           rel="noreferrer"
                           data-track-click="nav"
                           data-track-source-context="county_records_action_rail_parcel_anchor"
                           data-track-target-type="official_source">Open parcel anchor</a>
                    </article>
                @endif
                <article class="intent-card intent-card--primary">
                    <span class="intent-card__label">Open the county record path first</span>
                    <h2>${countyPage.recordsLabel()}</h2>
                    <p>${countyPage.uniqueAngle()}</p>
                    <a class="button button--primary"
                       href="${countyPage.recordsUrl()}"
                       target="_blank"
                       rel="noreferrer"
                       data-track-click="nav"
                       data-track-source-context="county_records_action_rail_records"
                       data-track-target-type="official_source">Open county records</a>
                </article>

                <article class="intent-card">
                    <span class="intent-card__label">Verify the county office</span>
                    <h2>${countyPage.officeLabel()}</h2>
                    <p>${countyPage.contactLine()}</p>
                    <a class="button button--secondary"
                       href="${countyPage.officeUrl()}"
                       target="_blank"
                       rel="noreferrer"
                       data-track-click="nav"
                       data-track-source-context="county_records_action_rail_office"
                       data-track-target-type="official_source">Open county office page</a>
                </article>

                <article class="intent-card">
                    <span class="intent-card__label">Price only after the file is clearer</span>
                    <h2>${state.stateName()} records lookup</h2>
                    <p>Use the state page when you still need the broader ${state.stateName()} rule story, sewer-availability context, or county-first workflow before a planning range.</p>
                    <a class="button button--secondary"
                       href="/septic-records-checklist/${state.slug()}/"
                       data-track-click="nav"
                       data-track-source-context="county_records_action_rail_state"
                       data-track-target-type="state_money_page">Open ${state.stateName()} records lookup</a>
                </article>
            </div>
        </div>
    </section>

    @if (!countyIntentRoutes.isEmpty())
        <section class="section section--tight">
            <div class="container">
                <div class="section-heading">
                    <span class="eyebrow">County intent matrix</span>
                    <h2>Use the exact ${countyPage.countyName()} search intent before you trust the file story.</h2>
                    <p class="lede">These are the common county-level septic searches that should resolve into a permit file, records request, address or parcel search, as-built, inspection letter, or buyer file check.</p>
                </div>

                <div class="county-intent-matrix">
                    @for (var route : countyIntentRoutes)
                        <article class="county-intent-card" id="${route.anchorId()}">
                            <span class="pill">${route.eyebrow()}</span>
                            <h2>${route.heading()}</h2>
                            <p>${route.summary()}</p>
                            <div class="county-intent-card__actions">
                                @if ("official_source".equals(route.primaryTargetType()))
                                    <a class="button button--primary"
                                       href="${route.primaryPath()}"
                                       target="_blank"
                                       rel="noreferrer"
                                       data-track-click="nav"
                                       data-track-source-context="county_intent_matrix_primary"
                                       data-track-target-type="${route.primaryTargetType()}">${route.primaryLabel()}</a>
                                @else
                                    <a class="button button--primary"
                                       href="${route.primaryPath()}"
                                       data-track-click="nav"
                                       data-track-source-context="county_intent_matrix_primary"
                                       data-track-target-type="${route.primaryTargetType()}">${route.primaryLabel()}</a>
                                @endif
                                @if ("official_source".equals(route.secondaryTargetType()))
                                    <a class="button button--secondary"
                                       href="${route.secondaryPath()}"
                                       target="_blank"
                                       rel="noreferrer"
                                       data-track-click="nav"
                                       data-track-source-context="county_intent_matrix_secondary"
                                       data-track-target-type="${route.secondaryTargetType()}">${route.secondaryLabel()}</a>
                                @else
                                    <a class="button button--secondary"
                                       href="${route.secondaryPath()}"
                                       data-track-click="nav"
                                       data-track-source-context="county_intent_matrix_secondary"
                                       data-track-target-type="${route.secondaryTargetType()}">${route.secondaryLabel()}</a>
                                @endif
                            </div>
                        </article>
                    @endfor
                </div>
            </div>
        </section>
    @endif

    @if (countyPage.hasActionPlaybook())
        <section class="section section--tight" id="county-action-playbook">
            <div class="container">
                <div class="section-heading">
                    <span class="eyebrow">County Action Playbook</span>
                    <h2>Use this before anyone talks price.</h2>
                    <p class="lede">This is the working script and role-by-role checklist for turning the county parcel anchor into an SCDES file request, not another generic septic explainer.</p>
                </div>
                <div class="detail-grid">
                    @if (countyPage.requestScriptBody() != null && !countyPage.requestScriptBody().isBlank())
                        <article class="trust-box trust-box--large detail-card--full">
                            <span class="trust-box__label">Copy-ready request</span>
                            <h3>${countyPage.requestScriptHeading() == null || countyPage.requestScriptHeading().isBlank() ? "Send this to the records contact" : countyPage.requestScriptHeading()}</h3>
                            <p>${countyPage.requestScriptBody()}</p>
                        </article>
                    @endif
                    @if (countyPage.buyerActions() != null && !countyPage.buyerActions().isEmpty())
                        <article class="detail-card">
                            <h3>Buyer move</h3>
                            <ul class="detail-list">
                                @for (var action : countyPage.buyerActions())
                                    <li>${action}</li>
                                @endfor
                            </ul>
                        </article>
                    @endif
                    @if (countyPage.sellerActions() != null && !countyPage.sellerActions().isEmpty())
                        <article class="detail-card">
                            <h3>Seller move</h3>
                            <ul class="detail-list">
                                @for (var action : countyPage.sellerActions())
                                    <li>${action}</li>
                                @endfor
                            </ul>
                        </article>
                    @endif
                    @if (countyPage.contractorActions() != null && !countyPage.contractorActions().isEmpty())
                        <article class="detail-card">
                            <h3>Contractor move</h3>
                            <ul class="detail-list">
                                @for (var action : countyPage.contractorActions())
                                    <li>${action}</li>
                                @endfor
                            </ul>
                        </article>
                    @endif
                    @if (countyPage.redFlagChecklist() != null && !countyPage.redFlagChecklist().isEmpty())
                        <article class="detail-card">
                            <h3>Stop if you see this</h3>
                            <ul class="detail-list">
                                @for (var flag : countyPage.redFlagChecklist())
                                    <li>${flag}</li>
                                @endfor
                            </ul>
                        </article>
                    @endif
                </div>
            </div>
        </section>
    @endif

    <section class="section section--tight state-money-disclosure-section">
        <div class="container">
            <details class="state-money-disclosure">
                <summary>
                    <span class="eyebrow">County detail</span>
                    <strong>Workflow structure, requests, and low-end breakers</strong>
                    <span>Open when you need the full county file logic behind the answer panel.</span>
                </summary>

        <div class="detail-grid">
            <article class="detail-card">
                <h2>Why ${countyPage.countyName()} is worth its own page</h2>
                <p>${countyPage.uniqueAngle()}</p>
                <p>${countyPage.targetReader()}</p>
            </article>

            <article class="detail-card">
                <h2>County office and records path</h2>
                @if (countyPage.hasParcelAnchor())
                    <p><strong>Parcel anchor.</strong> <a href="${countyPage.parcelAnchorUrl()}" target="_blank" rel="noreferrer">${countyPage.parcelAnchorLabel()}</a></p>
                    <p>${countyPage.parcelAnchorNote()}</p>
                @endif
                <p><strong>Office path.</strong> <a href="${countyPage.officeUrl()}" target="_blank" rel="noreferrer">${countyPage.officeLabel()}</a></p>
                <p><strong>Records path.</strong> <a href="${countyPage.recordsUrl()}" target="_blank" rel="noreferrer">${countyPage.recordsLabel()}</a></p>
                <p>${countyPage.contactLine()}</p>
            </article>

            @if (countyWorkflowStructure != null)
                <article class="detail-card detail-card--full">
                    <h2>County workflow structure</h2>
                    <div class="detail-grid">
                        @for (var field : countyWorkflowStructure.fields())
                            <article class="detail-card">
                                <h3>${field.label()}</h3>
                                <p>${field.value()}</p>
                            </article>
                        @endfor
                        <article class="detail-card">
                            <h3>Do not price yet when</h3>
                            <p>${countyWorkflowStructure.quoteGate()}</p>
                        </article>
                    </div>
                </article>
            @endif

            <article class="detail-card detail-card--full">
                <h2>How this county workflow usually unfolds</h2>
                <ol class="detail-list detail-list--numbered">
                    @for (var step : countyPage.decisionSteps())
                        <li>${step}</li>
                    @endfor
                </ol>
            </article>

            <article class="detail-card">
                <h2>What to ask the county for</h2>
                <ul class="detail-list">
                    @for (var record : countyPage.recordsToRequest())
                        <li>${record}</li>
                    @endfor
                </ul>
            </article>

            <article class="detail-card">
                <h2>What breaks the low-end story</h2>
                <ul class="detail-list">
                    @for (var breaker : countyPage.lowEndBreakers())
                        <li>${breaker}</li>
                    @endfor
                </ul>
            </article>
        </div>
            </details>
        </div>
    </section>

    <section class="section section--muted state-money-disclosure-section">
        <div class="container">
            <details class="state-money-disclosure">
                <summary>
                    <span class="eyebrow">Source layer</span>
                    <strong>FAQs and official county sources</strong>
                    <span>Open when you need the source list or county-specific FAQ answers.</span>
                </summary>

        <div class="detail-grid">
            @for (var faq : countyPage.faqBlocks())
                <article class="detail-card">
                    <h3>${faq.question()}</h3>
                    <p>${faq.answer()}</p>
                </article>
            @endfor

            <article class="trust-box trust-box--large">
                <span class="trust-box__label">Official county sources</span>
                <ul class="source-list">
                    @for (var source : sources)
                        <li>
                            <strong>${source.agencyName()}</strong>
                            <a href="${source.url()}" target="_blank" rel="noreferrer">${source.title()}</a>
                            <div class="source-meta">
                                @if (source.trustLevel() != null && !source.trustLevel().isBlank())
                                    <span>Trust: ${source.trustLevel()}</span>
                                @endif
                                @if (source.lastVerifiedAt() != null && !source.lastVerifiedAt().isBlank())
                                    <span>Last verified: ${source.lastVerifiedAt()}</span>
                                @endif
                            </div>
                        </li>
                    @endfor
                </ul>
                <div class="hero__actions">
                    <a class="button button--secondary"
                       href="/source-policy/"
                       data-track-click="nav"
                       data-track-source-context="county_records_source_policy"
                       data-track-target-type="trust_page">Read source policy</a>
                    <a class="button button--secondary"
                       href="/methodology/"
                       data-track-click="nav"
                       data-track-source-context="county_records_methodology"
                       data-track-target-type="trust_page">Read methodology</a>
                </div>
            </article>
        </div>
            </details>
        </div>
    </section>

    <section class="section">
        <div class="container content-grid">
            <article class="trust-box trust-box--large">
                <span class="trust-box__label">Next best action</span>
                <h2>Use the state workflow after the county file is clearer</h2>
                <p>Once the county form, location, or record history is in hand, move back into the ${state.stateName()} records or permit page before you rely on a planning range.</p>
                <div class="hero__actions">
                    <a class="button button--primary"
                       href="/septic-records-checklist/${state.slug()}/"
                       data-track-click="nav"
                       data-track-source-context="county_records_next_best_state"
                       data-track-target-type="state_money_page">Open ${state.stateName()} records lookup</a>
                    <a class="button button--secondary"
                       href="/septic-system-cost-calculator/${state.slug()}/"
                       data-track-click="nav"
                       data-track-source-context="county_records_next_best_guide"
                       data-track-target-type="state_guide">Open the ${state.stateName()} guide</a>
                </div>
            </article>

            @if (!internalLinks.isEmpty())
                <article class="detail-card">
                    <h2>Related ${state.stateName()} pages</h2>
                    <ul class="detail-list">
                        @for (var link : internalLinks.stream().limit(6).toList())
                            <li>
                                <a href="${link.path()}"
                                   data-track-click="nav"
                                   data-track-source-context="county_records_related_link"
                                   data-track-target-type="related_internal">${link.title()}</a>
                                <p class="detail-list__note">${link.note()}</p>
                            </li>
                        @endfor
                        @if (internalLinks.size() > 6)
                            <li>
                                <details class="county-link-overflow">
                                    <summary>Show more related pages</summary>
                                    <ul class="detail-list">
                                        @for (var link : internalLinks)
                                            <li><a href="${link.path()}" data-track-click="nav" data-track-source-context="county_records_related_link_all" data-track-target-type="related_internal">${link.title()}</a></li>
                                        @endfor
                                    </ul>
                                </details>
                            </li>
                        @endif
                    </ul>
                </article>
            @endif

            @if (!siblingCountyRoutes.isEmpty())
                <article class="detail-card detail-card--full">
                    <h2>Other strong ${state.stateName()} county routes</h2>
                    <p>Use these when the searcher is comparing nearby counties, checking a different parcel, or moving from a state guide into another local records path.</p>
                    <div class="county-sibling-routes" aria-label="Related county records routes">
                        @for (var route : siblingCountyRoutes)
                            <a href="${route.path()}"
                               data-track-click="nav"
                               data-track-source-context="county_records_sibling_route"
                               data-track-target-type="county_records_page">
                                <span>${route.stateCode()}</span>
                                <strong>${route.countyName()} records</strong>
                                <small>${route.note()}</small>
                            </a>
                        @endfor
                    </div>
                </article>
            @endif
        </div>
    </section>
    <div class="sticky-mobile-cta-spacer" aria-hidden="true"></div>
    <section class="sticky-mobile-cta sticky-mobile-cta--single"
             aria-label="Open county septic record path"
             data-sticky-mobile-cta
             data-show-after='[data-sticky-trigger="county-records-after-hero"]'>
        <div class="sticky-mobile-cta__inner">
            <a class="button button--primary"
               href="${countyPage.recordsUrl()}"
               target="_blank"
               rel="noreferrer"
               data-track-click="nav"
               data-track-source-context="county_records_sticky_records"
               data-track-target-type="official_source">Open county record path</a>
        </div>
    </section>
    </div>
`)
