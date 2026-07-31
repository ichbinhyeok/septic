@import com.example.septic.data.model.CountyRecordsPage
@import com.example.septic.data.model.SourceRecord
@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.EditorialProfile
@import com.example.septic.web.CountyAvailabilityRowView
@import com.example.septic.web.CountyAvailabilitySummaryView
@import com.example.septic.web.CountyAccessProfileView
@import com.example.septic.web.CountyAcquisitionProfileView
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
@param CountyAccessProfileView countyAccessProfile = null
@param CountyAcquisitionProfileView countyAcquisitionProfile = null
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
                   data-source-title="${countyPage.title()}">Find the permit, layout, approval, or repair trail without guessing which office owns it.</p>
                @if (countyQuickAnswer != null && !countyQuickAnswer.isBlank())
                    <div class="content-quick-answer" aria-label="Quick answer">
                        <span class="content-quick-answer__label">Start here</span>
                        @if (countyAccessProfile != null)
                            <p>${countyAccessProfile.summary()}</p>
                        @else
                            <p>Start with the official county record path. Search with the property address${countyPage.hasParcelAnchor() ? " and parcel ID" : ""}, then contact ${countyPage.officeLabel()} before treating an empty result as proof that no file exists.</p>
                        @endif
                        <p class="content-quick-answer__source">
                            @if (countyAccessProfile != null)
                                Current route: <a href="${countyAccessProfile.primaryUrl()}" target="_blank" rel="noreferrer">${countyAccessProfile.primaryLabel()}</a>
                            @else
                                Official route: <a href="${countyPage.recordsUrl()}" target="_blank" rel="noreferrer">${countyPage.recordsLabel()}</a>
                            @endif
                            @if (editorialLastReviewedAt != null && !editorialLastReviewedAt.isBlank())
                                · reviewed ${editorialLastReviewedAt}
                            @endif
                        </p>
                        <details class="content-quick-answer__detail">
                            <summary>Local route note</summary>
                            <p>${countyQuickAnswer}</p>
                        </details>
                    </div>
                @endif
                @if (countyAccessProfile != null)
                    <section class="county-service-scope county-service-scope--${countyAccessProfile.countySpecific() ? "verified" : "baseline"}"
                             aria-label="What SepticPath can do on this route">
                        <div class="county-service-scope__lead">
                            <span>Official access</span>
                            <strong>${countyAccessProfile.officialAccessLabel()}</strong>
                        </div>
                        <div>
                            <span>SepticPath can</span>
                            <strong>${countyAccessProfile.capabilityLabel()}: ${countyAcquisitionProfile != null && countyAcquisitionProfile.hasPreparedFieldPack() ? "published fields and handoff" : "property clues and route"}</strong>
                        </div>
                        <div>
                            <span>You complete</span>
                            <strong>${countyAcquisitionProfile != null ? countyAcquisitionProfile.userOnlyAction() : "The current government-site or office action"}</strong>
                        </div>
                        <div>
                            <span>Done means</span>
                            <strong>${countyAccessProfile.completionLabel()}</strong>
                        </div>
                        <p><strong>${countyAccessProfile.scopeLabel()}.</strong> ${countyAccessProfile.scopeSummary()}@if (editorialLastReviewedAt != null && !editorialLastReviewedAt.isBlank()) <span>Route reviewed ${editorialLastReviewedAt}.</span>@endif</p>
                    </section>
                @endif
                <div class="county-command-bar" aria-label="Immediate county records actions">
                    @if (countyAccessProfile != null)
                        @if (countyAcquisitionProfile != null && countyAcquisitionProfile.hasPreparedFieldPack())
                            <a class="button button--primary"
                               href="#county-acquisition-workspace"
                               data-track-click="nav"
                               data-track-source-context="county_acquisition_prepare"
                               data-track-target-type="internal_tool">${countyAcquisitionProfile.archivedOfficialFieldPackVerified() ? "Prepare everything before the county handoff" : "Prepare for the official county route"}</a>
                            <a class="button button--secondary"
                               href="${countyAccessProfile.primaryUrl()}"
                               target="_blank"
                               rel="noreferrer"
                               data-county-access-official
                               data-track-click="nav"
                               data-track-source-context="county_access_primary"
                               data-track-target-type="official_source">${countyAccessProfile.primaryLabel()}</a>
                        @else
                            @if (!countyAccessProfile.countySpecific())
                                <a class="button button--primary"
                                   href="#county-access-workflow"
                                   data-track-click="nav"
                                   data-track-source-context="county_baseline_prepare"
                                   data-track-target-type="internal_tool">Prepare property details</a>
                                <a class="button button--secondary"
                                   href="${countyAccessProfile.primaryUrl()}"
                                   target="_blank"
                                   rel="noreferrer"
                                   data-county-access-official
                                   data-track-click="nav"
                                   data-track-source-context="county_access_primary"
                                   data-track-target-type="official_source">Open official next step</a>
                            @else
                                <a class="button button--primary"
                                   href="${countyAccessProfile.primaryUrl()}"
                                   target="_blank"
                                   rel="noreferrer"
                                   data-county-access-official
                                   data-track-click="nav"
                                   data-track-source-context="county_access_primary"
                                   data-track-target-type="official_source">${countyAccessProfile.primaryLabel()}</a>
                            @endif
                        @endif
                        @if (countyAccessProfile.hasSecondaryAction() && (countyAcquisitionProfile == null || countyAccessProfile.secondaryUrl().startsWith("http") || countyAccessProfile.secondaryUrl().startsWith("mailto:")))
                            @if (countyAccessProfile.secondaryUrl().startsWith("http"))
                                <a class="button button--secondary"
                                   href="${countyAccessProfile.secondaryUrl()}"
                                   target="_blank"
                                   rel="noreferrer"
                                   data-county-access-official
                                   data-track-click="nav"
                                   data-track-source-context="county_access_secondary"
                                   data-track-target-type="official_source">${countyAccessProfile.secondaryLabel()}</a>
                            @else
                                <a class="button button--secondary"
                                   href="${countyAccessProfile.secondaryUrl()}"
                                   data-county-access-official="${countyAccessProfile.secondaryUrl().startsWith("mailto:")}"
                                   data-track-click="nav"
                                   data-track-source-context="county_access_secondary"
                                   data-track-target-type="internal_tool">${countyAccessProfile.secondaryLabel()}</a>
                            @endif
                        @endif
                    @else
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
                    @endif
                    <button class="button button--secondary"
                            type="button"
                            data-share-route
                            data-share-url="${page.canonicalUrl()}"
                            data-share-title="${page.title()}"
                            data-share-text="${countySeoIntro}"
                            data-track-source-context="county_records_share_route">Share county route</button>
                </div>
                @if (countyAvailabilitySummary != null)
                    <details class="county-route-proof-details">
                        <summary>Why we trust this route</summary>
                        <div class="county-proof-strip" aria-label="County record route proof">
                            <div>
                                <span>Access type</span>
                                <strong>${countyAccessProfile != null ? countyAccessProfile.modeLabel() : countyAvailabilitySummary.requestMethodLabel()}</strong>
                            </div>
                            <div>
                                <span>Completion</span>
                                <strong>${countyAccessProfile != null ? countyAccessProfile.completionLabel() : "The county file or a written office response"}</strong>
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
                @if (countySeoIntro != null && !countySeoIntro.isBlank())
                    <p class="lede">${countySeoIntro}</p>
                    @if (!countyLocalContent.priorityPage())
                        <p class="county-records-page__support-copy">${countyLocalContent.introCopy()}</p>
                    @endif
                    <p class="county-records-page__local-signal"><strong>Local signal:</strong> ${countyPage.metaDescription()}</p>
                @else
                    <p class="lede">${countyLocalContent.introCopy()}</p>
                @endif
            </div>

            <article class="hero-sidecard county-file-sidecard">
                <span class="pill">${countyAccessProfile != null ? countyAccessProfile.modeLabel() : "Who has the file"}</span>
                <strong>${countyPage.officeLabel()}</strong>
                <p>${countyAccessProfile != null ? countyAccessProfile.limitation() : countyPage.contactLine()}</p>
                <a class="button button--primary"
                   href="${countyAcquisitionProfile != null ? "#county-acquisition-workspace" : (countyAccessProfile != null ? countyAccessProfile.primaryUrl() : countyPage.recordsUrl())}"
                   target="${countyAcquisitionProfile != null ? null : "_blank"}"
                   rel="${countyAcquisitionProfile != null ? null : "noreferrer"}"
                   data-county-access-official="${countyAcquisitionProfile == null}"
                   data-track-click="nav"
                   data-track-source-context="county_records_owner_card"
                   data-track-target-type="${countyAcquisitionProfile != null ? "internal_tool" : "official_source"}">${countyAcquisitionProfile != null ? (countyAcquisitionProfile.hasPreparedFieldPack() ? (countyAcquisitionProfile.archivedOfficialFieldPackVerified() ? "Prepare before contacting the county" : "Prepare with the official fields") : "Prepare the routing facts") : (countyAccessProfile != null ? countyAccessProfile.primaryLabel() : "Open official records")}</a>
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

    @if (countyAccessProfile != null)
        <section class="section section--tight county-access-workflow-section">
            <div class="container">
                <div class="county-access-workflow county-access-workflow--${countyAccessProfile.tone()}"
                     id="county-access-workflow"
                     data-county-access-workflow
                     data-county-key="${countyAccessProfile.countyKey()}"
                     data-county-mode="${countyAccessProfile.mode()}"
                     data-county-profile-scope="${countyAccessProfile.profileScope()}"
                     data-county-capability-tier="${countyAccessProfile.capabilityTier()}"
                     data-county-route-reviewed-at="${editorialLastReviewedAt}"
                     data-county-acquisition-method="${countyAcquisitionProfile != null ? countyAcquisitionProfile.acquisitionMethod() : ""}"
                     data-county-address-or-parcel="${countyAcquisitionProfile != null && countyAcquisitionProfile.requiresAddressOrParcel()}"
                     data-county-document-selection-required="${countyAcquisitionProfile != null && countyAcquisitionProfile.requiresOfficialDocumentSelection()}"
                     data-county-primary-url="${countyAccessProfile.primaryUrl()}"
                     data-county-secondary-url="${countyAccessProfile.secondaryUrl()}"
                     data-county-primary-label="${countyAccessProfile.primaryLabel()}"
                     data-county-agency="${countyAcquisitionProfile != null ? countyAcquisitionProfile.agency() : ""}"
                     data-county-fee="${countyAcquisitionProfile != null ? countyAcquisitionProfile.feeLabel() : ""}"
                     data-county-timing="${countyAcquisitionProfile != null ? countyAcquisitionProfile.timingLabel() : ""}"
                     data-county-manual-boundary="${countyAcquisitionProfile != null ? countyAcquisitionProfile.manualCompletionBoundary() : ""}"
                     data-county-recipient="${countyAcquisitionProfile != null && countyAcquisitionProfile.officialRecipientVerified() ? countyAcquisitionProfile.recipientEmail() : ""}"
                     data-county-email-subject="${countyAcquisitionProfile != null && countyAcquisitionProfile.officialRecipientVerified() ? countyAcquisitionProfile.emailSubject() : ""}">
                    <header class="county-access-workflow__header">
                        <div>
                            <span class="eyebrow">Property record task</span>
                            <h2>${countyAcquisitionProfile != null ? countyAcquisitionProfile.methodHeading() : "Leave with the search details. Return with the official outcome."}</h2>
                        </div>
                        <span class="county-access-workflow__badge">${countyAccessProfile.modeLabel()}</span>
                    </header>

                    @if (countyAcquisitionProfile == null)
                    <div class="county-access-workflow__layout">
                        <form class="county-access-workflow__case" data-county-access-case>
                            <div>
                                <span class="county-access-workflow__step">01 · Prepare</span>
                                <h3>Keep the property clues together</h3>
                                <p>These details stay on this device and are not submitted until you choose an official route.</p>
                            </div>
                            <label>
                                <span>Property address</span>
                                <input type="text"
                                       maxlength="180"
                                       autocomplete="street-address"
                                       placeholder="Street, city, state, ZIP"
                                       data-county-access-address>
                            </label>
                            <label>
                                <span>Parcel, GPIN, PIN, or Tax ID <small>optional</small></span>
                                <input type="text"
                                       maxlength="100"
                                       autocomplete="off"
                                       placeholder="Use the local property identifier"
                                       data-county-access-parcel>
                            </label>
                            <button class="button button--secondary" type="button" data-county-access-copy>Copy search details</button>
                            <p class="county-access-workflow__status" data-county-access-status aria-live="polite"></p>
                        </form>

                        <div class="county-access-workflow__plan">
                            <div>
                                <span class="county-access-workflow__step">02 · Complete the official action</span>
                                <ol>
                                    @for (var step : countyAccessProfile.steps())
                                        <li>${step}</li>
                                    @endfor
                                </ol>
                            </div>
                            <div class="county-access-workflow__actions">
                                <a class="button button--primary"
                                   href="${countyAccessProfile.primaryUrl()}"
                                   target="_blank"
                                   rel="noreferrer"
                                   data-county-access-official
                                   data-track-click="nav"
                                   data-track-source-context="county_access_workflow_primary"
                                   data-track-target-type="official_source">${countyAccessProfile.primaryLabel()}</a>
                                @if (countyAccessProfile.hasSecondaryAction())
                                    @if (countyAccessProfile.secondaryUrl().startsWith("http"))
                                        <a class="button button--secondary"
                                           href="${countyAccessProfile.secondaryUrl()}"
                                           target="_blank"
                                           rel="noreferrer"
                                           data-county-access-official
                                           data-track-click="nav"
                                           data-track-source-context="county_access_workflow_secondary"
                                           data-track-target-type="official_source">${countyAccessProfile.secondaryLabel()}</a>
                                    @else
                                        <a class="button button--secondary"
                                           href="${countyAccessProfile.secondaryUrl()}"
                                           data-track-click="nav"
                                           data-track-source-context="county_access_workflow_secondary"
                                           data-track-target-type="internal_tool">${countyAccessProfile.secondaryLabel()}</a>
                                    @endif
                                @endif
                            </div>
                        </div>

                        <aside class="county-access-workflow__evidence">
                            <span class="county-access-workflow__step">03 · Know what counts</span>
                            <h3>Expected property evidence</h3>
                            <ul>
                                @for (var artifact : countyAccessProfile.expectedArtifacts())
                                    <li>${artifact}</li>
                                @endfor
                            </ul>
                            <p><strong>Completion:</strong> ${countyAccessProfile.completionLabel()}</p>
                            <p class="county-access-workflow__limit">${countyAccessProfile.limitation()}</p>
                        </aside>
                    </div>
                    @endif

                    @if (countyAcquisitionProfile != null)
                        <section class="county-acquisition-workspace"
                                 id="county-acquisition-workspace"
                                 data-county-acquisition-workspace>
                            <header class="county-acquisition-workspace__header">
                                <div>
                                    <span class="eyebrow">County-specific preparation</span>
                                    <h3>${countyAcquisitionProfile.methodHeading()}</h3>
                                    <p>${countyAcquisitionProfile.methodInstruction()}</p>
                                </div>
                                <dl class="county-acquisition-facts" aria-label="Request facts">
                                    <div>
                                        <dt>File owner</dt>
                                        <dd>${countyAcquisitionProfile.agency()}</dd>
                                    </div>
                                    <div>
                                        <dt>Fee</dt>
                                        <dd>${countyAcquisitionProfile.feeLabel()}</dd>
                                    </div>
                                    <div>
                                        <dt>Timing</dt>
                                        <dd>${countyAcquisitionProfile.timingLabel()}</dd>
                                    </div>
                                    <div>
                                        <dt>You still do</dt>
                                        <dd>${countyAcquisitionProfile.userCheckpoint()}</dd>
                                    </div>
                                    @if (countyAcquisitionProfile.archivedOfficialFieldPackVerified())
                                    <div>
                                        <dt>Field verification</dt>
                                        <dd>${countyAcquisitionProfile.archivedFieldVerificationLabel()}</dd>
                                    </div>
                                    @else
                                    @if (!countyAcquisitionProfile.officialFieldPackVerified())
                                    <div>
                                        <dt>Field verification</dt>
                                        <dd>Official route confirmed; exact submission fields are not published or independently verified</dd>
                                    </div>
                                    @endif
                                    @endif
                                </dl>
                            </header>

                            <div class="county-acquisition-handoff">
                                <div>
                                    <span>SepticPath prepares</span>
                                    <strong>${countyAcquisitionProfile.preparationSummary()}</strong>
                                </div>
                                <div>
                                    <span>You finish at the county</span>
                                    <strong>${countyAcquisitionProfile.userOnlyAction()}</strong>
                                </div>
                            </div>

                            @if ("TN::hamilton-county".equals(countyAccessProfile.countyKey()))
                                <section class="hamilton-quick-search" aria-labelledby="hamilton-quick-search-heading" data-hamilton-quick-search>
                                    <div>
                                        <span class="eyebrow">Start with the official search</span>
                                        <h4 id="hamilton-quick-search-heading">Enter the street name, then choose the exact address</h4>
                                        <p>Hamilton's search works best with the street name only. Your entry stays in this browser until you open the county site.</p>
                                    </div>
                                    <div class="hamilton-quick-search__action">
                                        <label for="hamilton-quick-street">
                                            <span>Street name</span>
                                            <input id="hamilton-quick-street"
                                                   type="text"
                                                   maxlength="120"
                                                   autocomplete="address-line1"
                                                   placeholder="For example: Oakwood"
                                                   data-hamilton-quick-street>
                                        </label>
                                        <a class="button button--primary"
                                           href="${countyAccessProfile.primaryUrl()}"
                                           target="_blank"
                                           rel="noreferrer"
                                           data-county-access-official
                                           data-county-route-position="hamilton_quick_search"
                                           data-hamilton-quick-open>Open Hamilton's official search</a>
                                        <p class="hamilton-quick-search__hint" data-hamilton-quick-status aria-live="polite">Don't enter the house number here. Select the full address in the county search.</p>
                                    </div>
                                </section>
                            @endif

                            <div class="county-acquisition-workspace__body">
                                @if (countyAcquisitionProfile.hasPreparedFieldPack())
                                <form class="county-acquisition-fields" data-county-acquisition-fields>
                                    <div class="county-acquisition-fields__heading">
                                        <span class="county-access-workflow__step">${countyAcquisitionProfile.archivedOfficialFieldPackVerified() ? "County-authored preparation fields" : "Verified route inputs"}</span>
                                        <strong>${countyAcquisitionProfile.archivedOfficialFieldPackVerified() ? "Complete the known county fields before asking only what changed" : "Prepare the required details here, then copy them into the county form"}</strong>
                                        <p data-county-acquisition-readiness aria-live="polite">Start filling the confirmed inputs below.</p>
                                    </div>
                                    @if (countyAcquisitionProfile.officialUsesPropertyAddress() || countyAcquisitionProfile.officialUsesParcelIdentifier())
                                        <fieldset class="county-acquisition-fieldset">
                                            <legend>Property to identify</legend>
                                            <p>Use the same property clues you will carry into the official route.</p>
                                            <div class="county-acquisition-fieldset__grid">
                                                @if (countyAcquisitionProfile.officialUsesPropertyAddress())
                                                <div class="county-acquisition-field">
                                                    <label for="county-acquisition-address">
                                                        <span>Property address@if (!countyAcquisitionProfile.officialPropertyAddressRequired()) <small>${countyAcquisitionProfile.officialUsesParcelIdentifier() ? "optional if using the parcel ID" : "optional on the official form"}</small>@endif</span>
                                                        <input id="county-acquisition-address"
                                                               type="text"
                                                               maxlength="180"
                                                               autocomplete="street-address"
                                                               placeholder="Street, city, state, ZIP"
                                                               data-county-access-address
                                                               required="${countyAcquisitionProfile.officialPropertyAddressRequired()}">
                                                    </label>
                                                    <button class="county-acquisition-field__copy"
                                                            type="button"
                                                            data-county-acquisition-field-copy
                                                            aria-label="Copy property address">Copy</button>
                                                </div>
                                                @endif
                                                @if (countyAcquisitionProfile.officialUsesParcelIdentifier())
                                                <div class="county-acquisition-field">
                                                    <label for="county-acquisition-parcel">
                                                        <span>Parcel, GPIN, PIN, or Tax ID@if (!countyAcquisitionProfile.officialParcelIdentifierRequired()) <small>optional unless used instead of address</small>@endif</span>
                                                        <input id="county-acquisition-parcel"
                                                               type="text"
                                                               maxlength="100"
                                                               autocomplete="off"
                                                               placeholder="Use the local property identifier"
                                                               data-county-access-parcel
                                                               required="${countyAcquisitionProfile.officialParcelIdentifierRequired()}">
                                                    </label>
                                                    <button class="county-acquisition-field__copy"
                                                            type="button"
                                                            data-county-acquisition-field-copy
                                                            aria-label="Copy parcel or local ID">Copy</button>
                                                </div>
                                                @endif
                                            </div>
                                        </fieldset>
                                    @endif

                                    @if (countyAcquisitionProfile.fallbackFieldPack())
                                        <details class="county-acquisition-optional county-acquisition-optional--fallback"
                                                 data-county-acquisition-fallback>
                                            <summary>
                                                <span>
                                                    <strong>${countyAcquisitionProfile.fallbackFieldPackHeading()}</strong>
                                                    <small>${countyAcquisitionProfile.fallbackFieldPackInstruction()}</small>
                                                </span>
                                                <span>${countyAcquisitionProfile.fields().size()} fields</span>
                                            </summary>
                                            @if (!countyAcquisitionProfile.requiredFields().isEmpty())
                                            <fieldset class="county-acquisition-fieldset county-acquisition-fieldset--required">
                                                <legend>Required by the fallback form</legend>
                                                <p>These do not block the free search. They become required only while this fallback is open.</p>
                                                <div class="county-acquisition-fieldset__grid">
                                                    @for (var field : countyAcquisitionProfile.requiredFields())
                                                        @template.tags.countyAcquisitionField(field = field)
                                                    @endfor
                                                </div>
                                            </fieldset>
                                            @endif
                                            @if (!countyAcquisitionProfile.optionalFields().isEmpty())
                                            <fieldset class="county-acquisition-fieldset">
                                                <legend>Optional fallback details</legend>
                                                <div class="county-acquisition-fieldset__grid">
                                                    @for (var field : countyAcquisitionProfile.optionalFields())
                                                        @template.tags.countyAcquisitionField(field = field)
                                                    @endfor
                                                </div>
                                            </fieldset>
                                            @endif
                                        </details>
                                    @else
                                    @if (!countyAcquisitionProfile.requiredFields().isEmpty())
                                        <fieldset class="county-acquisition-fieldset county-acquisition-fieldset--required">
                                            <legend>${countyAcquisitionProfile.archivedOfficialFieldPackVerified() ? "Preparation essentials" : "Required by the county"}</legend>
                                            <p>${countyAcquisitionProfile.archivedOfficialFieldPackVerified() ? "The county-authored form asked for these details. Completing these essentials makes the handoff actionable; it does not claim the old PDF is still accepted." : "Complete these before opening the official form."}</p>
                                            <div class="county-acquisition-fieldset__grid">
                                                @for (var field : countyAcquisitionProfile.requiredFields())
                                                    @template.tags.countyAcquisitionField(field = field)
                                                @endfor
                                            </div>
                                        </fieldset>
                                    @endif

                                    @if (!countyAcquisitionProfile.optionalFields().isEmpty())
                                        <details class="county-acquisition-optional">
                                            <summary>
                                                <span>
                                                    <strong>${countyAcquisitionProfile.archivedOfficialFieldPackVerified() ? "All other county-authored fields" : "Optional details"}</strong>
                                                    <small>${countyAcquisitionProfile.archivedOfficialFieldPackVerified() ? "Add every detail you know so the office does not have to call back for basic property history." : "Add only if they help identify the record."}</small>
                                                </span>
                                                <span>${countyAcquisitionProfile.optionalFields().size()} fields</span>
                                            </summary>
                                            <div class="county-acquisition-fieldset__grid">
                                                @for (var field : countyAcquisitionProfile.optionalFields())
                                                    @template.tags.countyAcquisitionField(field = field)
                                                @endfor
                                            </div>
                                        </details>
                                    @endif
                                    @endif
                                </form>
                                @else
                                    <form class="county-acquisition-fields county-acquisition-fields--unverified"
                                          data-county-acquisition-fields>
                                        <div class="county-acquisition-fields__heading">
                                            <span class="county-access-workflow__step">Property facts to carry</span>
                                            <strong>Keep the address and parcel clue ready without pretending these are county form fields</strong>
                                        </div>
                                        <p>${countyAcquisitionProfile.methodInstruction()} These are routing facts for your own handoff, not claimed county submission fields.</p>
                                        <label>
                                            <span>Property address <small>your reference</small></span>
                                            <input type="text"
                                                   maxlength="180"
                                                   autocomplete="street-address"
                                                   placeholder="Street, city, state, ZIP"
                                                   data-county-access-address>
                                        </label>
                                        <label>
                                            <span>Parcel, GPIN, PIN, or Tax ID <small>if known</small></span>
                                            <input type="text"
                                                   maxlength="100"
                                                   autocomplete="off"
                                                   placeholder="Keep the local property identifier with the task"
                                                   data-county-access-parcel>
                                        </label>
                                        @if (!countyAcquisitionProfile.fields().isEmpty())
                                        <fieldset class="county-acquisition-fieldset">
                                            <legend>Routing facts to confirm</legend>
                                            <p>Use these to identify the responsible office before asking for the file.</p>
                                            <div class="county-acquisition-fieldset__grid">
                                                @for (var field : countyAcquisitionProfile.fields())
                                                    @template.tags.countyAcquisitionField(field = field)
                                                @endfor
                                            </div>
                                        </fieldset>
                                        @endif
                                    </form>
                                @endif

                                <div class="county-acquisition-request">
                                    @if (countyAcquisitionProfile.generatedRequestEnabled())
                                    <div class="county-acquisition-request__heading">
                                        <span class="county-access-workflow__step">Prepared output</span>
                                        <strong>Ready to paste, email, or carry into the official form</strong>
                                    </div>
                                    <template data-county-acquisition-template>${countyAcquisitionProfile.requestTemplate()}</template>
                                    <textarea readonly
                                              rows="18"
                                              aria-label="Prepared county request"
                                              data-county-acquisition-preview></textarea>
                                    <div class="county-acquisition-request__actions">
                                        <button class="button button--primary"
                                                type="button"
                                                data-county-acquisition-copy>Copy prepared request</button>
                                        <a class="button button--secondary"
                                           href="${countyAccessProfile.primaryUrl()}"
                                           target="_blank"
                                           rel="noreferrer"
                                           data-county-access-official
                                           data-track-click="nav"
                                           data-track-source-context="county_acquisition_official"
                                           data-track-target-type="official_source">${countyAccessProfile.primaryLabel()}</a>
                                        @if (countyAcquisitionProfile.hasEmail())
                                            <button class="button button--secondary"
                                                    type="button"
                                                    data-county-acquisition-email>Open email draft</button>
                                        @endif
                                        <button class="button button--secondary"
                                                type="button"
                                                data-county-acquisition-download>Download request text</button>
                                        <button class="button button--secondary"
                                                type="button"
                                                data-county-acquisition-print>Print or save PDF</button>
                                        @if (countyAcquisitionProfile.hasPhone())
                                            <a class="button button--secondary"
                                               href="tel:${countyAcquisitionProfile.phone()}">Call ${countyAcquisitionProfile.phone()}</a>
                                        @endif
                                    </div>
                                    <p class="county-acquisition-request__status"
                                       data-county-acquisition-status
                                       aria-live="polite"></p>
                                    @else
                                        <div class="county-acquisition-request__heading">
                                            <span class="county-access-workflow__step">Next official action</span>
                                            <strong>${countyAcquisitionProfile.methodHeading()}</strong>
                                        </div>
                                        <p class="county-acquisition-request__guardrail">${countyAcquisitionProfile.methodInstruction()}</p>
                                        <div class="county-acquisition-manual-boundary">
                                            <span>Your final manual step</span>
                                            <strong>${countyAcquisitionProfile.manualCompletionBoundary()}</strong>
                                            <small>SepticPath does not sign in, accept charges, sign, or press the county's final submit button for you.</small>
                                        </div>
                                        @if (countyAcquisitionProfile.hasPreparedHandoffScript())
                                        <section class="county-handoff-script">
                                            <div class="county-acquisition-request__heading">
                                                <span class="county-access-workflow__step">What to say when they answer</span>
                                                <strong>Ask only what the broken website cannot tell you</strong>
                                            </div>
                                            <p class="county-acquisition-request__guardrail">This call script is written by SepticPath from the county-authored field list. It is not county submission wording.</p>
                                            <template data-county-handoff-template>${countyAcquisitionProfile.requestTemplate()}</template>
                                            <textarea readonly
                                                      rows="18"
                                                      aria-label="Prepared county handoff script"
                                                      data-county-handoff-preview></textarea>
                                            <div class="county-acquisition-request__actions">
                                                <button class="button button--primary"
                                                        type="button"
                                                        data-county-handoff-copy>Copy prepared call script</button>
                                                <a class="button button--secondary"
                                                   href="tel:${countyAcquisitionProfile.phone()}">Call ${countyAcquisitionProfile.phone()}</a>
                                            </div>
                                            <div class="county-handoff-script__channels">
                                                <strong>Contacts printed on the county-authored form</strong>
                                                <span>Email: ${countyAcquisitionProfile.recipientEmail()}</span>
                                                <span>Fax: ${countyAcquisitionProfile.publishedFaxPendingIntakeConfirmation()}</span>
                                                <small>Confirm that either channel still accepts file-search requests before sending personal or property information.</small>
                                            </div>
                                        </section>
                                        @endif
                                        <section class="county-preparation-sheet" data-county-preparation-sheet>
                                            <div class="county-preparation-sheet__heading">
                                                <span class="county-access-workflow__step">Carry everything to the counter</span>
                                                <strong>${countyAcquisitionProfile.preparationSheetLabel()}</strong>
                                                <p>${countyAcquisitionProfile.preparationSheetInstruction()}</p>
                                            </div>
                                            <div class="county-preparation-sheet__notice">
                                                <strong>Preparation aid — not an official county form</strong>
                                                <span>It organizes confirmed values, official links, and your task scope. The county PDF, portal, search, contact form, or call remains the official route.</span>
                                            </div>
                                            <div class="county-preparation-sheet__actions">
                                                @if (countyAcquisitionProfile.supportsPreparedOfficialPdf())
                                                <button class="button button--primary"
                                                        type="button"
                                                        data-county-official-pdf-prepare>Fill the county's original PDF</button>
                                                @if (!countyAcquisitionProfile.preparedPdfRemainder().isBlank())
                                                    <p class="county-preparation-sheet__remainder">${countyAcquisitionProfile.preparedPdfRemainder()}</p>
                                                @endif
                                                @endif
                                                @if (countyAcquisitionProfile.hasPreparedFieldPack())
                                                <button class="button button--secondary"
                                                        type="button"
                                                        data-county-acquisition-next-copy>Copy next prepared field</button>
                                                @endif
                                                <button class="button button--quiet"
                                                        type="button"
                                                        data-county-preparation-download>Download preparation sheet</button>
                                                <button class="button button--quiet"
                                                        type="button"
                                                        data-county-preparation-print>Print or save preparation sheet</button>
                                            </div>
                                        </section>
                                        <div class="county-acquisition-request__actions">
                                            <a class="button button--primary"
                                               href="${countyAccessProfile.primaryUrl()}"
                                               target="${countyAccessProfile.primaryOpensNewWindow() ? "_blank" : null}"
                                               rel="${countyAccessProfile.primaryOpensNewWindow() ? "noreferrer" : null}"
                                               data-county-access-official
                                               data-track-click="nav"
                                               data-track-source-context="county_acquisition_official_only"
                                               data-track-target-type="official_source">${countyAccessProfile.primaryLabel()}</a>
                                            @if (!countyAccessProfile.primaryAccessNote().isBlank())
                                                <p class="county-route-access-note">${countyAccessProfile.primaryAccessNote()}</p>
                                            @endif
                                            @if (countyAccessProfile.hasSecondaryAction() && (countyAccessProfile.secondaryUrl().startsWith("http") || countyAccessProfile.secondaryUrl().startsWith("mailto:")))
                                                <a class="button button--quiet"
                                                   href="${countyAccessProfile.secondaryUrl()}"
                                                   target="${countyAccessProfile.secondaryUrl().startsWith("http") ? "_blank" : null}"
                                                   rel="${countyAccessProfile.secondaryUrl().startsWith("http") ? "noreferrer" : null}"
                                                   data-county-access-official>${countyAccessProfile.secondaryLabel()}</a>
                                                @if (!countyAccessProfile.secondaryAccessNote().isBlank())
                                                    <p class="county-route-access-note county-route-access-note--warning">${countyAccessProfile.secondaryAccessNote()}</p>
                                                @endif
                                            @endif
                                            @if (countyAcquisitionProfile.hasEmail()
                                                    && countyAcquisitionProfile.officialRecipientVerified()
                                                    && !countyAccessProfile.secondaryUrl().equals("mailto:" + countyAcquisitionProfile.recipientEmail()))
                                                <a class="button button--quiet"
                                                   href="mailto:${countyAcquisitionProfile.recipientEmail()}"
                                                   data-county-access-official>Open official email recipient</a>
                                            @endif
                                            @if (countyAcquisitionProfile.hasPublishedContactEmailPendingIntakeConfirmation())
                                                <a class="button button--quiet"
                                                   href="mailto:${countyAcquisitionProfile.recipientEmail()}">Open the listed email after the office confirms intake</a>
                                            @endif
                                            @if (countyAcquisitionProfile.hasPhone() && (countyAcquisitionProfile.hasPreparedFieldPack() || countyAcquisitionProfile.officialRecipientVerified()))
                                                <a class="button button--quiet"
                                                   href="tel:${countyAcquisitionProfile.phone()}">Call ${countyAcquisitionProfile.phone()}</a>
                                            @endif
                                            <button class="button button--quiet"
                                                    type="button"
                                                    data-county-acquisition-input-copy>${countyAcquisitionProfile.hasPreparedFieldPack() ? "Copy all filled inputs" : "Copy property facts"}</button>
                                        </div>
                                        <div class="county-acquisition-route-steps">
                                            <strong>County-specific steps</strong>
                                            <ol>
                                                @for (var step : countyAccessProfile.steps())
                                                    <li>${step}</li>
                                                @endfor
                                            </ol>
                                        </div>
                                        <p class="county-acquisition-request__status"
                                           data-county-acquisition-status
                                           aria-live="polite"></p>
                                    @endif
                                </div>
                            </div>

                            <footer class="county-acquisition-documents">
                                <div>
                                    <strong>Task scope to review</strong>
                                    <small>This is your saved job scope, not county-authored request wording.</small>
                                </div>
                                @if (!countyAcquisitionProfile.requestedDocuments().isEmpty())
                                <ul>
                                    @for (var document : countyAcquisitionProfile.requestedDocuments())
                                        <li>${document}</li>
                                    @endfor
                                </ul>
                                @else
                                <p>No detailed document checklist is published for this route. SepticPath does not invent one.</p>
                                @endif
                            </footer>
                        </section>
                    @endif

                    @if ("NC::brunswick-county".equals(countyAccessProfile.countyKey()))
                        <section class="brunswick-permit-lookup" id="brunswick-permit-search" data-brunswick-permit-lookup>
                            <div>
                                <span class="eyebrow">Official public query</span>
                                <h3>Search Brunswick permit metadata here</h3>
                                <p>Uses the address or parcel ID above to query Brunswick County's public ArcGIS permit index. A matching row is a lead to the source file, not the septic document itself.</p>
                            </div>
                            <button class="button button--primary" type="button" data-brunswick-permit-search>
                                Search official permit metadata
                            </button>
                            <div class="brunswick-permit-lookup__results" data-brunswick-permit-results aria-live="polite"></div>
                        </section>
                    @endif

                    @if ("WA::thurston-county".equals(countyAccessProfile.countyKey()))
                        <section class="thurston-record-lookup" id="thurston-record-search" data-thurston-record-lookup>
                            <div>
                                <span class="eyebrow">Official public query</span>
                                <h3>Search Thurston County records by parcel</h3>
                                <p>Uses the parcel number above to query the county's public Laserfiche archive. A result is an official document candidate, not proof that the file is a septic record for the property.</p>
                            </div>
                            <button class="button button--primary" type="button" data-thurston-record-search>
                                Search official county records
                            </button>
                            <div class="thurston-record-lookup__results" data-thurston-record-results aria-live="polite"></div>
                        </section>
                    @endif

                    <section class="county-access-return" id="county-access-return" data-county-access-return hidden>
                        <div>
                            <span class="eyebrow">Back from the official route?</span>
                            <h3>Record the real outcome</h3>
                            <p>An empty online search is not an official no-record response.</p>
                        </div>
                        <div class="county-access-return__outcomes">
                            @if ("TN::hamilton-county".equals(countyAccessProfile.countyKey()))
                                <button type="button" data-county-access-outcome="artifact">I found the permit or certificate</button>
                                <button type="button" data-county-access-outcome="not_found_online">The address was not listed</button>
                                <button type="button" data-county-access-outcome="blocked">The official search was blocked</button>
                            @else
                                <button type="button" data-county-access-outcome="artifact">I downloaded a document</button>
                                <button type="button" data-county-access-outcome="partial">I found only part of the file</button>
                                <button type="button" data-county-access-outcome="not_found_online">Nothing appeared online</button>
                                <button type="button" data-county-access-outcome="blocked">The route was blocked</button>
                                <button type="button" data-county-access-outcome="request_submitted">I submitted a request</button>
                            @endif
                        </div>
                        <label class="county-access-return__reference">
                            <span>Confirmation, case, or request number <small>if the county issued one</small></span>
                            <input type="text"
                                   maxlength="120"
                                   autocomplete="off"
                                   placeholder="Save it here on this device"
                                   data-county-access-reference>
                        </label>
                        <div class="county-access-return__next" data-county-access-next></div>
                        <button class="county-access-return__clear" type="button" data-county-access-clear>Clear this task</button>
                    </section>
                </div>
            </div>
        </section>
    @endif

    <div data-sticky-trigger="county-records-after-hero" aria-hidden="true"></div>

    <details class="county-reference-library">
        <summary>
            <span>Need the supporting research?</span>
            <strong>Open official evidence, local exceptions, FAQs, and related routes</strong>
        </summary>
        <div class="county-reference-library__body">
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
                    <strong>File details, access method, and source checks</strong>
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
                            <h2>${countyPage.countyName()} file path, request method, and expected evidence</h2>
                            <p>Availability describes the documented official route. It does not predict whether a property-specific file exists.</p>
                        </div>
                        <div class="county-confidence-meter">
                            <span class="county-confidence-meter__label">Documented access type</span>
                            <strong>${countyAccessProfile != null ? countyAccessProfile.modeLabel() : countyAvailabilitySummary.requestMethodLabel()}</strong>
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

    @if (countyAcquisitionProfile == null && !countyRequestOptions.isEmpty())
        <section class="section section--tight">
            <div class="container">
                <details class="county-request-builder county-request-builder--disclosure" data-choice-group>
                    <summary>
                        <span class="eyebrow">Need to contact the office?</span>
                        <strong>Prepare a records-request draft</strong>
                        <span>Use it only after checking whether the official source requires its own form.</span>
                    </summary>
                    <div class="county-request-builder__intro">
                        <span class="eyebrow">Plain-language draft · not an official form</span>
                        <h2>Prepare the details before you contact the office.</h2>
                        <p>Choose the ${countyPage.countyName()} job context, then send ${countyPage.officeLabel()} a request for the artifacts that can change pricing, buyer risk, or permit scope.</p>
                        <p>Keep the ${countyPage.countyName()} parcel, APN, owner, address, or legal description ready for every request mode below.</p>
                        <p><strong>Before sending:</strong> if the official source publishes a required PDF, portal, signature, attachment, or payment step, use that official route. This draft does not prove that email or free-form text is accepted.</p>
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
                </details>
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
                            <span class="trust-box__label">Copy-ready request draft · not an official form</span>
                            <h3>${countyPage.requestScriptHeading() == null || countyPage.requestScriptHeading().isBlank() ? "Use only when the official contact accepts free text" : countyPage.requestScriptHeading()}</h3>
                            <p>${countyPage.requestScriptBody()}</p>
                            <small>This wording was drafted by SepticPath from the cited official material. It is not a county form and should not replace required portal fields, PDFs, signatures, or identity checks.</small>
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
        <div class="container content-grid content-grid--county-next">
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
        </div>
    </details>
    <div class="sticky-mobile-cta-spacer" aria-hidden="true"></div>
    <section class="sticky-mobile-cta sticky-mobile-cta--single"
             aria-label="Open county septic record path"
             data-sticky-mobile-cta
             data-show-after='[data-sticky-trigger="county-records-after-hero"]'>
        <div class="sticky-mobile-cta__inner">
            @if(countyAcquisitionProfile != null)
                <a class="button button--primary"
                   href="#county-acquisition-workspace"
                   data-track-click="nav"
                   data-track-source-context="county_records_sticky_prepare"
                   data-track-target-type="internal_tool">Prepare for the county step</a>
            @else
                <a class="button button--primary"
                   href="${countyPage.recordsUrl()}"
                   target="_blank"
                   rel="noreferrer"
                   data-track-click="nav"
                   data-track-source-context="county_records_sticky_records"
                   data-track-target-type="official_source">${countyPage.recordsActionLabel()}</a>
            @endif
        </div>
    </section>
    </div>
`)
