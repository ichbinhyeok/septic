@import com.example.septic.data.model.CountyRecordsPage
@import com.example.septic.data.model.SourceRecord
@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.EditorialProfile
@import com.example.septic.web.CountyWorkflowStructureView
@import com.example.septic.web.PageLink
@import com.example.septic.web.PageMeta
@param PageMeta page
@param CountyRecordsPage countyPage
@param StateProfile state
@param java.util.List<SourceRecord> sources
@param CountyWorkflowStructureView countyWorkflowStructure = null
@param java.util.List<PageLink> internalLinks = java.util.List.of()
@param java.util.List<PageLink> featuredInternalLinks = java.util.List.of()
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

    @if (countyPage.hasActionPlaybook())
        <section class="section section--tight">
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
        </div>
    </section>
    </div>
`)
