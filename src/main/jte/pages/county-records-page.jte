@import com.example.septic.data.model.CountyRecordsPage
@import com.example.septic.data.model.SourceRecord
@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.EditorialProfile
@import com.example.septic.web.CountyAvailabilityRowView
@import com.example.septic.web.CountyAvailabilitySummaryView
@import com.example.septic.web.CountyWorkflowStructureView
@import com.example.septic.web.CountyWorkflowFieldView
@import com.example.septic.web.CountyIntentRouteView
@import com.example.septic.web.CountyLocalContentView
@import com.example.septic.web.CountyRequestBuilderOptionView
@import com.example.septic.web.CountySearchResponseView
@import com.example.septic.web.PageLink
@import com.example.septic.web.PageMeta
@param PageMeta page
@param CountyRecordsPage countyPage
@param CountyLocalContentView countyLocalContent
@param StateProfile state
@param java.util.List<SourceRecord> sources
@param CountyAvailabilitySummaryView countyAvailabilitySummary = null
@param java.util.List<CountyAvailabilityRowView> countyAvailabilityRows = java.util.List.of()
@param CountyWorkflowStructureView countyWorkflowStructure = null
@param java.util.List<CountyIntentRouteView> countyIntentRoutes = java.util.List.of()
@param java.util.List<CountyRequestBuilderOptionView> countyRequestOptions = java.util.List.of()
@param CountySearchResponseView countySearchResponse = null
@param java.util.List<CountyWorkflowFieldView> countyOfficialFilePathRows = java.util.List.of()
@param java.util.List<PageLink> internalLinks = java.util.List.of()
@param java.util.List<PageLink> featuredInternalLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.CountyFinderLinkView> siblingCountyRoutes = java.util.List.of()
@param String countySeoHeading = ""
@param String countySeoIntro = ""
@param String countyQuickAnswer = ""
@param java.util.List<String> countySearchQueries = java.util.List.of()
@param String countyLeadProjectLabel = ""
@param String countyEstimatePath = ""
@param String countyQuotePath = ""
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
                <span class="eyebrow">${countyPage.countyName()} property file</span>
                @if (countySeoHeading != null && !countySeoHeading.isBlank())
                    <h1 class="county-records-page__search-title">${countySeoHeading}</h1>
                @else
                    <h1>${countyPage.title()}</h1>
                @endif
                <p class="county-records-page__source-title"
                   data-source-title="${countyPage.title()}">Find the permit, layout, approval, or repair trail without guessing which office owns it.<span hidden>${countyPage.title()}</span></p>
                @if (countyQuickAnswer != null && !countyQuickAnswer.isBlank())
                    <div class="content-quick-answer" aria-label="Quick answer">
                        <span class="content-quick-answer__label">Start here</span>
                        <p>${countyQuickAnswer}</p>
                        <p class="content-quick-answer__source">
                            Official route: <a href="${countyPage.recordsUrl()}" target="_blank" rel="noreferrer">${countyPage.recordsLabel()}</a>
                            @if (editorialLastReviewedAt != null && !editorialLastReviewedAt.isBlank())
                                · reviewed ${editorialLastReviewedAt}
                            @endif
                        </p>
                    </div>
                @endif
                @if (!countyOfficialFilePathRows.isEmpty())
                    <div class="aeo-answer-capsule" aria-label="County answer summary and route comparison">
                        <span class="eyebrow">What to do</span>
                        <h2>Get the ${countyPage.countyName()} file in four moves.</h2>
                        <div class="facts-table-wrap">
                            <table class="facts-table">
                                <tbody>
                                @for (var row : countyOfficialFilePathRows.stream().limit(4).toList())
                                    <tr><th scope="row">${row.label()}</th><td>${row.value()}</td></tr>
                                @endfor
                                </tbody>
                            </table>
                        </div>
                    </div>
                @endif
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
                       data-track-target-type="official_source">${countyPage.recordsActionLabel()}</a>
                    <button class="button button--secondary"
                            type="button"
                            data-share-route
                            data-share-url="${page.canonicalUrl()}"
                            data-share-title="${page.title()}"
                            data-share-text="${countySeoIntro}"
                            data-track-source-context="county_records_share_route">Share county route</button>
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
                @if (countyAvailabilitySummary != null)
                    <details class="county-route-proof-details">
                        <summary>Why we trust this route</summary>
                        <div class="county-proof-strip" aria-label="County record route proof">
                            <div>
                                <span>Route confidence</span>
                                <strong>${countyAvailabilitySummary.confidenceScore()}%</strong>
                                <small>${countyAvailabilitySummary.confidenceLabel()}</small>
                            </div>
                            <div>
                                <span>Request method</span>
                                <strong>${countyAvailabilitySummary.requestMethodLabel()}</strong>
                            </div>
                            <div>
                                <span>First file to pull</span>
                                <strong>${countyAvailabilitySummary.firstArtifactLabel()}</strong>
                            </div>
                            <div>
                                <span>Sources checked</span>
                                <strong>${countyAvailabilitySummary.sourceDepthLabel()}</strong>
                                <small>${countyAvailabilitySummary.lastVerifiedAt()}</small>
                            </div>
                        </div>
                    </details>
                @endif
                <div class="money-answer-panel" aria-label="County answer-first workflow">
                    <div class="money-answer-panel__header">
                        <span class="eyebrow">Your next steps</span>
                        <h2>Get the file before you price the job.</h2>
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
                @if (countyEstimatePath != null && !countyEstimatePath.isBlank() && countyQuotePath != null && !countyQuotePath.isBlank())
                    <div class="county-lead-bridge" aria-label="Quote-ready next step">
                        <div>
                            <span class="eyebrow">After the file</span>
                            <h2>Turn the county file into a quote-ready estimate.</h2>
                        </div>
                        <div class="county-lead-bridge__actions">
                            <a class="button button--primary"
                               href="${countyEstimatePath}"
                               data-track-click="nav"
                               data-track-source-context="county_records_quote_ready_estimate"
                               data-track-target-type="calculator">Run quote-ready estimate</a>
                            <a class="button button--secondary"
                               href="${countyQuotePath}"
                               data-track-click="nav"
                               data-track-source-context="county_records_quote_ready_form"
                               data-track-target-type="quote_form">Open quote request</a>
                        </div>
                    </div>
                @endif
                @if (countySeoIntro != null && !countySeoIntro.isBlank())
                    <p class="lede">${countySeoIntro}</p>
                    @if (!countyLocalContent.priorityPage())
                        <p class="county-records-page__support-copy">${countyLocalContent.introCopy()}</p>
                    @endif
                    <p class="county-records-page__local-signal"><strong>Local signal:</strong> ${countyPage.metaDescription()}</p>
                @else
                    <p class="lede">${countyLocalContent.introCopy()}</p>
                @endif
                @if (!countySearchQueries.isEmpty())
                    <div class="county-search-intent-strip" aria-label="Searches answered on this page">
                        <span class="county-search-intent-strip__label">Questions answered</span>
                        @for (var query : countySearchQueries)
                            <span class="county-search-intent-strip__query">How do I find ${query}?</span>
                        @endfor
                    </div>
                @endif
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

            <article class="hero-sidecard county-file-sidecard">
                <span class="pill">Who has the file</span>
                <strong>${countyPage.officeLabel()}</strong>
                <p>${countyPage.contactLine()}</p>
                <a class="button button--primary"
                   href="${countyPage.recordsUrl()}"
                   target="_blank"
                   rel="noreferrer"
                   data-track-click="nav"
                   data-track-source-context="county_records_owner_card"
                   data-track-target-type="official_source">Open official records</a>
                <details class="county-review-details">
                    <summary>How this route was checked</summary>
                    <dl class="review-meta">
                        <div>
                            <dt>Prepared by</dt>
                            <dd>
                                <span class="review-meta__name">${editorialPreparedBy.displayName()}</span>
                                <span>${editorialPreparedBy.roleTitle()}</span>
                            </dd>
                        </div>
                        <div>
                            <dt>Reviewed by</dt>
                            <dd>
                                <span class="review-meta__name">${editorialReviewedBy.displayName()}</span>
                                <span>${editorialReviewedBy.roleTitle()}</span>
                            </dd>
                        </div>
                        <div>
                            <dt>Reviewed against</dt>
                            <dd>${editorialReviewedAgainst}</dd>
                        </div>
                        <div>
                            <dt>Latest review</dt>
                            <dd>${editorialLastReviewedAt}</dd>
                        </div>
                    </dl>
                    <p>${editorialNote}</p>
                </details>
            </article>
        </div>
    </section>

    @if (countySearchResponse != null)
        <section class="section section--tight">
            <div class="container">
                <div class="county-click-board" aria-label="County search action board">
                    <div class="county-click-board__intro">
                        <span class="eyebrow">County search action board</span>
                        <h2>Choose the ${countyPage.countyName()} route that matches the file problem.</h2>
                        <p>If you are searching for ${countyPage.countyName()} septic records, move into the official file route, exact request wording, address or parcel fallback, or estimate only after the record story is clearer.</p>
                        <div class="county-click-board__queries" aria-label="County searches this board answers">
                            @for (var question : countySearchResponse.questions())
                                <span>${question}</span>
                            @endfor
                        </div>
                    </div>

                    <div class="county-click-board__grid">
                        <article class="county-click-card county-click-card--primary">
                            <span>Official file</span>
                            <h3>${countyPage.recordsLabel()}</h3>
                            <p>Start here when the user needs the septic permit, approval, repair trail, layout, or written no-record answer.</p>
                            <a class="button button--primary"
                               href="${countyPage.recordsUrl()}"
                               target="_blank"
                               rel="noreferrer"
                               data-track-click="nav"
                               data-track-source-context="county_click_board_records"
                               data-track-target-type="official_source">${countyPage.recordsActionLabel()}</a>
                        </article>

                        <article class="county-click-card">
                            <span>Request wording</span>
                            <h3>Ask for the exact septic artifacts</h3>
                            <p>Use a copy-ready request when the office needs parcel, owner, address, record type, and reason before it can answer.</p>
                            <a class="button button--secondary"
                               href="/septic-records-request-builder/"
                               data-track-click="nav"
                               data-track-source-context="county_click_board_request_builder"
                               data-track-target-type="internal_page">Build exact request</a>
                        </article>

                        <article class="county-click-card">
                            <span>Address or parcel</span>
                            @if (countyPage.hasParcelAnchor())
                                <h3>Anchor the parcel before the septic search</h3>
                                <p>${countyPage.parcelAnchorNote()}</p>
                                <a class="button button--secondary"
                                   href="${countyPage.parcelAnchorUrl()}"
                                   target="_blank"
                                   rel="noreferrer"
                                   data-track-click="nav"
                                   data-track-source-context="county_click_board_parcel_anchor"
                                   data-track-target-type="official_source">${countyPage.parcelAnchorLabel()}</a>
                            @else
                                <h3>Use address search only as the fallback</h3>
                                <p>Carry the address, parcel ID, owner name, legal description, or subdivision into the county records route before treating the file as missing.</p>
                                <a class="button button--secondary"
                                   href="/septic-record-finder/"
                                   data-track-click="nav"
                                   data-track-source-context="county_click_board_address_record_finder"
                                   data-track-target-type="internal_page">Find county from address</a>
                            @endif
                        </article>

                        @if (countyEstimatePath != null && !countyEstimatePath.isBlank())
                            <article class="county-click-card">
                                <span>After the file</span>
                                <h3>Estimate only after the route is clear</h3>
                                <p>${countyPage.countyName()} pricing gate: ${countyWorkflowStructure != null && countyWorkflowStructure.quoteGate() != null && !countyWorkflowStructure.quoteGate().isBlank() ? countyWorkflowStructure.quoteGate() : countyLocalContent.lowEndBreakers().get(0)}</p>
                                <a class="button button--secondary"
                                   href="${countyEstimatePath}"
                                   data-track-click="nav"
                                   data-track-source-context="county_click_board_estimate"
                                   data-track-target-type="calculator">Estimate after file check</a>
                            </article>
                        @endif
                    </div>
                </div>
            </div>
        </section>
    @endif

    <div data-sticky-trigger="county-records-after-hero" aria-hidden="true"></div>

    @if (countyLocalContent.priorityPage() && countyLocalContent.hasEvidenceFacts())
        <section class="section section--tight county-local-evidence"
                 data-county-local-evidence
                 data-replaced-unit-count="${countyLocalContent.replacedRepeatedUnitCount()}"
                 data-local-fact-count="${countyLocalContent.countyLocalFactCount()}">
            <div class="container">
                <div class="county-local-evidence__heading">
                    <span class="eyebrow">Verified local evidence</span>
                    <h2>What the official sources actually document for ${countyPage.countyName()}</h2>
                    <p>These source notes replace repeated county-template copy. They describe the real lookup, permit, map, inspection, or request path found in the cited government material.</p>
                </div>
                <ol class="county-local-evidence__list">
                    @for (var fact : countyLocalContent.evidenceFacts())
                        <li>
                            <div class="county-local-evidence__meta">
                                <span>${fact.scopeLabel()}</span>
                                @if (fact.verifiedAt() != null && !fact.verifiedAt().isBlank())
                                    <time datetime="${fact.verifiedAt()}">Content checked ${fact.verifiedAt()}</time>
                                @endif
                            </div>
                            <div>
                                <h3><a href="${fact.sourceUrl()}" target="_blank" rel="noreferrer">${fact.sourceTitle()}</a></h3>
                                <p>${fact.summary()}</p>
                                <small>${fact.agencyName()}</small>
                            </div>
                        </li>
                    @endfor
                </ol>
            </div>
        </section>
    @endif

    <section class="section section--tight county-research-disclosure-section">
        <div class="container">
            <details class="state-money-disclosure county-research-disclosure">
                <summary>
                    <span class="eyebrow">County evidence</span>
                    <strong>File details, route confidence, and search proof</strong>
                    <span>Open only when you need the full local evidence behind the official route above.</span>
                </summary>
                <div class="county-research-disclosure__content">

    @if (countySearchResponse != null)
        <section class="section section--tight">
            <div class="container">
                <div class="county-response-dossier" aria-label="Search response county records dossier">
                    <div class="county-response-dossier__intro">
                        <span class="eyebrow">${countySearchResponse.badgeLabel()}</span>
                        <h2>${countySearchResponse.heading()}</h2>
                        <p>For the ${countyPage.countyName()} search path: ${countySearchResponse.summary()}</p>
                        <div class="county-response-dossier__queries" aria-label="Search queries this county page is tuned to answer">
                            <span>${countySearchResponse.priorityLabel()}</span>
                            @for (var question : countySearchResponse.questions())
                                <span>${question}</span>
                            @endfor
                        </div>
                    </div>

                    <div class="county-response-dossier__rows" role="list" aria-label="County search response checklist">
                        @for (var row : countySearchResponse.dossierRows())
                            <article role="listitem">
                                <span>${row.label()}</span>
                                <p>${row.value()}</p>
                            </article>
                        @endfor
                    </div>

                    <div class="county-response-dossier__actions" aria-label="Response county handoff links">
                        <a class="button button--primary"
                           href="${countyPage.recordsUrl()}"
                           target="_blank"
                           rel="noreferrer"
                           data-track-click="nav"
                           data-track-source-context="county_search_response_official"
                           data-track-target-type="official_source">Open official county route</a>
                        @for (var link : countySearchResponse.actionLinks())
                            <a class="button button--secondary"
                               href="${link.path()}"
                               data-track-click="nav"
                               data-track-source-context="county_search_response_handoff"
                               data-track-target-type="internal_page">${link.title()}</a>
                        @endfor
                    </div>
                </div>
            </div>
        </section>
    @endif

    @if (!countyOfficialFilePathRows.isEmpty())
        <section class="section section--tight">
            <div class="container">
                <div class="county-response-dossier" aria-label="${countyPage.countyName()} official county file path">
                    <div class="county-response-dossier__intro">
                        <span class="eyebrow">Official county file path</span>
                        <h2>Use this ${countyPage.countyName()} file path before another broad search.</h2>
                        <p>${countyPage.countyName()} uses this table as a work surface: identify the file owner, carry the right property clue, request the first useful artifact, and preserve a written fallback when the lookup has no result.</p>
                        <div class="county-response-dossier__queries" aria-label="County file path signals">
                            <span>File owner</span>
                            <span>First artifact</span>
                            <span>Request method</span>
                            <span>No-record fallback</span>
                        </div>
                    </div>

                    <div class="county-response-dossier__rows" role="list" aria-label="Official county file path rows">
                        @for (var row : countyOfficialFilePathRows)
                            <article role="listitem">
                                <span>${row.label()}</span>
                                <p>${row.value()}</p>
                            </article>
                        @endfor
                    </div>

                    <div class="county-response-dossier__actions" aria-label="${countyPage.countyName()} official file path actions">
                        <a class="button button--primary"
                           href="${countyPage.recordsUrl()}"
                           target="_blank"
                           rel="noreferrer"
                           data-track-click="nav"
                           data-track-source-context="county_official_file_path_records"
                           data-track-target-type="official_source">${countyPage.recordsActionLabel()}</a>
                        @if (countyPage.hasParcelAnchor())
                            <a class="button button--secondary"
                               href="${countyPage.parcelAnchorUrl()}"
                               target="_blank"
                               rel="noreferrer"
                               data-track-click="nav"
                               data-track-source-context="county_official_file_path_parcel"
                               data-track-target-type="official_source">${countyPage.parcelAnchorLabel()}</a>
                        @else
                            <a class="button button--secondary"
                               href="/septic-record-finder/"
                               data-track-click="nav"
                               data-track-source-context="county_official_file_path_address_record_finder"
                               data-track-target-type="internal_page">Find county from address</a>
                        @endif
                        <a class="button button--secondary"
                           href="/septic-permit-records-request/"
                           data-track-click="nav"
                           data-track-source-context="county_official_file_path_request"
                           data-track-target-type="internal_page">Open records request wording</a>
                        <a class="button button--secondary"
                           href="/septic-records-checklist/${state.slug()}/"
                           data-track-click="nav"
                           data-track-source-context="county_official_file_path_state"
                           data-track-target-type="state_money_page">Open ${state.stateName()} records route</a>
                    </div>
                </div>
            </div>
        </section>
    @endif

    @if (countyAvailabilitySummary != null && !countyAvailabilityRows.isEmpty())
        <section class="section section--tight">
            <div class="container">
                <div class="county-data-console">
                    <div class="county-data-console__header">
                        <div>
                            <span class="eyebrow">County record availability matrix</span>
                            <h2>${countyPage.countyName()} file path, request method, and confidence score</h2>
                            <p>${countyPage.countyName()} availability confidence: ${countyAvailabilitySummary.confidenceNote()}</p>
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

                </div>
            </details>
        </div>
    </section>

    @if (!countyRequestOptions.isEmpty())
        <section class="section section--tight">
            <div class="container">
                <div class="county-request-builder" data-choice-group>
                    <div class="county-request-builder__intro">
                        <span class="eyebrow">Permit file request builder</span>
                        <h2>Turn the county page into the exact request you send.</h2>
                        <p>Choose the ${countyPage.countyName()} job context, then send ${countyPage.officeLabel()} a request for the artifacts that can change pricing, buyer risk, or permit scope.</p>
                        <p>Keep the ${countyPage.countyName()} parcel, APN, owner, address, or legal description ready for every request mode below.</p>
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
                                    @if (!line.contains("I can provide the parcel, APN, owner, address, or legal description"))
                                        <p>${line}</p>
                                    @endif
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
                    <p>Open the ${countyPage.countyName()} path, capture the parcel clue, ask for the local file artifacts, then move to the ${state.stateName()} workflow or cost estimate after this record story is clearer.</p>
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
                            <p>For ${countyPage.countyName()}, use the address, parcel identifier, owner name, or local office routing before relying on a price.</p>
                        @endif
                    </li>
                    <li>
                        <span>03</span>
                        <strong>Request the artifacts that change the answer</strong>
                    </li>
                    <li>
                        <span>04</span>
                        <strong>Stop pricing if the file is not clear</strong>
                        @if (countyWorkflowStructure != null && countyWorkflowStructure.quoteGate() != null && !countyWorkflowStructure.quoteGate().isBlank())
                            <p>At step four for ${countyPage.countyName()}: ${countyWorkflowStructure.quoteGate()}</p>
                        @else
                            <p>${!countyLocalContent.lowEndBreakers().isEmpty() ? countyLocalContent.lowEndBreakers().get(0) : countyPage.contactLine()}</p>
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
                    <p>Use this ${countyPage.countyName()} block when the search is not a broad septic question. It usually resolves one of four local file tasks: find the permit, request the records, anchor the parcel, or confirm the as-built and inspection trail.</p>
                </div>
                <div class="county-search-intent-grid">
                    <article class="county-search-intent-card">
                        <span>Permit lookup</span>
                        <h3>${countyPage.countyName()} septic permit lookup</h3>
                        <p>For ${countyPage.countyName()}, start with ${countyPage.recordsLabel()} before you trust a quote, repair story, buyer file, or permit closeout claim.</p>
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
                        <p>${countyPage.countyName()} request script: ${countyPage.requestScriptBody() != null && !countyPage.requestScriptBody().isBlank() ? countyPage.requestScriptBody() : countyPage.contactLine()}</p>
                        @if (countyPage.hasActionPlaybook())
                            <a class="button button--secondary"
                               href="#county-action-playbook"
                               data-track-click="nav"
                               data-track-source-context="county_search_intent_records_request"
                               data-track-target-type="page_anchor">Open request script</a>
                        @else
                            <a class="button button--secondary"
                               href="/septic-records-request-builder/"
                               data-track-click="nav"
                               data-track-source-context="county_search_intent_records_request_builder"
                               data-track-target-type="internal_page">Build a request script</a>
                        @endif
                        <a class="button button--secondary"
                           href="/septic-permit-records-request/"
                           data-track-click="nav"
                           data-track-source-context="county_search_intent_records_request_guide"
                           data-track-target-type="internal_page">Open records request guide</a>
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
                            <p>Use the ${state.stateName()} records path first, then confirm the ${countyPage.countyName()} parcel identifier with the local office before pricing.</p>
                            <a class="button button--secondary"
                               href="/septic-record-finder/"
                               data-track-click="nav"
                               data-track-source-context="county_search_intent_address_record_finder"
                               data-track-target-type="internal_page">Find county from address</a>
                        @endif
                    </article>

                    <article class="county-search-intent-card">
                        <span>As-built and inspection</span>
                        <h3>Ask for the file artifacts that change the answer</h3>
                        <a class="button button--secondary"
                           href="/septic-as-built-records/"
                           data-track-click="nav"
                           data-track-source-context="county_search_intent_as_built"
                           data-track-target-type="internal_page">Open as-built records guide</a>
                    </article>

                    <article class="county-search-intent-card">
                        <span>County comparison</span>
                        <h3>Compare septic records by county</h3>
                        <a class="button button--secondary"
                           href="/septic-records-by-county/"
                           data-track-click="nav"
                           data-track-source-context="county_search_intent_records_by_county"
                           data-track-target-type="internal_page">Open records by county</a>
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
                    @if (countyLocalContent.priorityPage() && countyLocalContent.hasEvidenceFacts())
                        <p>Use ${countyLocalContent.evidenceFacts().get(0).sourceTitle()} to confirm the owning route before a quote or repair decision.</p>
                    @else
                        <p>${countyPage.countyName()} workflow angle: ${countyLocalContent.uniqueAngle()}</p>
                    @endif
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
                    <p>${countyPage.countyName()} contact detail: ${countyPage.contactLine()}</p>
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
                    <p class="lede">These are the common ${countyPage.countyName()} septic searches that should resolve into a permit file, records request, address or parcel search, as-built, inspection letter, or buyer file check.</p>
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
                <p>${countyLocalContent.uniqueAngle()}</p>
                <p>${countyLocalContent.targetReader()}</p>
            </article>

            <article class="detail-card">
                <h2>County office and records path</h2>
                @if (countyPage.hasParcelAnchor())
                    <p><strong>Parcel anchor.</strong> <a href="${countyPage.parcelAnchorUrl()}" target="_blank" rel="noreferrer">${countyPage.parcelAnchorLabel()}</a></p>
                    <p>${countyPage.parcelAnchorNote()}</p>
                @endif
                <p><strong>Office path.</strong> <a href="${countyPage.officeUrl()}" target="_blank" rel="noreferrer">${countyPage.officeLabel()}</a></p>
                <p><strong>Records path.</strong> <a href="${countyPage.recordsUrl()}" target="_blank" rel="noreferrer">${countyPage.recordsLabel()}</a></p>
                <p>${countyPage.countyName()} office routing detail: ${countyPage.contactLine()}</p>
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
                            <p>Final ${countyPage.countyName()} workflow gate: ${countyWorkflowStructure.quoteGate()}</p>
                        </article>
                    </div>
                </article>
            @endif

            <article class="detail-card detail-card--full">
                <h2>How this county workflow usually unfolds</h2>
                <ol class="detail-list detail-list--numbered">
                    @for (var step : countyLocalContent.decisionSteps())
                        <li>${step}</li>
                    @endfor
                </ol>
            </article>

            <article class="detail-card">
                <h2>What to ask the county for</h2>
                <ul class="detail-list">
                    @for (var record : countyLocalContent.recordsToRequest())
                        <li>${record}</li>
                    @endfor
                </ul>
            </article>

            <article class="detail-card">
                <h2>What breaks the low-end story</h2>
                <ul class="detail-list">
                    @for (var breaker : countyLocalContent.lowEndBreakers())
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
                                @template.tags.sourceVerification(source = source)
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
                                <p class="detail-list__note">${countyPage.countyName()}: ${link.note()}</p>
                            </li>
                        @endfor
                        @if (internalLinks.size() > 6)
                            <li>
                                <details class="county-link-overflow">
                                    <summary>Show more related pages</summary>
                                    <ul class="detail-list">
                                        @for (var link : internalLinks.stream().skip(6).toList())
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
               data-track-target-type="official_source">${countyPage.recordsActionLabel()}</a>
        </div>
    </section>
    </div>
`)
