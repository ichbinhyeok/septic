@import com.example.septic.data.model.ContentPage
@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.PageMeta
@import com.example.septic.web.EditorialProfile
@param PageMeta page
@param ContentPage contentPage
@param java.util.List<StateProfile> states
@param java.util.List<com.example.septic.web.StateMoneyPageLink> stateMoneyPageLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.StateMoneyPageLink> featuredStateMoneyPageLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.ContentEvidenceLaneView> contentEvidenceLanes = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> internalLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> featuredInternalLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> secondaryInternalLinks = java.util.List.of()
@param String calculatorPath = "/septic-system-cost-calculator/"
@param String contentQuotePath = null
@param String calculatorCtaHeading = "Use the main estimator before you ask for quotes."
@param String calculatorCtaLabel = "Open the estimator"
@param String calculatorCtaNote = "Use your state and project assumptions first, then verify locally."
@param String calculatorCtaTargetType = "calculator"
@param EditorialProfile editorialPreparedBy = null
@param EditorialProfile editorialReviewedBy = null
@param String editorialReviewedAgainst = ""
@param String editorialLastReviewedAt = ""
@param String editorialNote = ""

@template.layouts.app(page = page, content = @`
    <section class="section section--heroish">
        <div class="container state-hero">
            <div>
                <span class="eyebrow">
                    @if ("buyer_page".equals(contentPage.intentType()))
                        Homebuyer guide
                    @elseif ("workflow_page".equals(contentPage.intentType()))
                        Permit and records guide
                    @else
                        Septic planning guide
                    @endif
                </span>
                <h1>${contentPage.title()}</h1>
                <p class="lede">${contentPage.introCopy()}</p>
                <div class="hero__actions">
                    <a class="button button--primary" href="${calculatorPath}" data-track-click="nav" data-track-source-context="content_page_primary_calculator" data-track-target-type="${calculatorCtaTargetType}">${calculatorCtaLabel}</a>
                    @if (contentQuotePath != null)
                        <a class="button button--secondary" href="${contentQuotePath}" data-track-click="nav" data-track-source-context="content_page_primary_quote" data-track-target-type="quote_form">Start short quote form</a>
                    @else
                        <a class="button button--secondary" href="#faq">Jump to FAQs</a>
                    @endif
                </div>
                @if (!featuredInternalLinks.isEmpty())
                    <div class="quick-link-strip" aria-label="Fast next steps">
                        <span class="quick-link-strip__label">Fast next steps</span>
                        @for (var link : featuredInternalLinks)
                            <a class="button button--secondary quick-link-strip__button"
                               href="${link.path()}"
                               data-track-click="nav"
                               data-track-source-context="content_page_featured_link"
                               data-track-target-type="internal_page">${link.title()}</a>
                        @endfor
                    </div>
                @endif
            </div>

            <article class="hero-sidecard">
                <span class="pill">
                    @if ("tank_size_estimator".equals(contentPage.calculatorModule()))
                        Tank size tool
                    @elseif ("pump_schedule_estimator".equals(contentPage.calculatorModule()))
                        Pump schedule tool
                    @else
                        Cost estimator
                    @endif
                </span>
                <strong>${contentPage.primaryKeyword()}</strong>
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
            <div class="section-jump-strip" aria-label="Jump between sections">
                <span class="section-jump-strip__label">Jump between sections</span>
                <a href="#drivers">Drivers</a>
                <a href="#state-pages">State pages</a>
                @if (!contentEvidenceLanes.isEmpty())
                    <a href="#state-lanes">Sources</a>
                @endif
                <a href="#faq">FAQ</a>
            </div>

            <div class="intent-rail">
                <article class="intent-card intent-card--primary">
                    <span class="intent-card__label">Run the estimate</span>
                    <h2>${calculatorCtaLabel}</h2>
                    <p>${calculatorCtaNote}</p>
                    <a class="button button--primary" href="${calculatorPath}" data-track-click="nav" data-track-source-context="content_page_action_rail_calculator" data-track-target-type="${calculatorCtaTargetType}">${calculatorCtaLabel}</a>
                </article>

                <article class="intent-card">
                    @if (contentQuotePath != null)
                        <span class="intent-card__label">Go straight to conversion</span>
                        <h2>Open the short quote form</h2>
                        <p>Use this when you already know the intent lane and want to skip directly into the shorter conversion path.</p>
                        <a class="button button--secondary" href="${contentQuotePath}" data-track-click="nav" data-track-source-context="content_page_action_rail_quote" data-track-target-type="quote_form">Start short quote form</a>
                    @else
                        <span class="intent-card__label">Clear the basics first</span>
                        <h2>Jump to the FAQ section</h2>
                        <p>Use the FAQs when this page is still too generic and you need the fastest clarification before clicking deeper.</p>
                        <a class="button button--secondary" href="#faq">Jump to FAQs</a>
                    @endif
                </article>

                <article class="intent-card">
                    @if (!featuredStateMoneyPageLinks.isEmpty())
                        <span class="intent-card__label">Best state-specific example</span>
                        <h2>${featuredStateMoneyPageLinks.get(0).title()}</h2>
                        <p>Open the strongest live state-specific page first when you want to see the official-source workflow behind this national overview.</p>
                        <a class="button button--secondary" href="${featuredStateMoneyPageLinks.get(0).path()}" data-track-click="nav" data-track-source-context="content_page_action_rail_featured_state" data-track-target-type="state_money_page">Open this state page</a>
                    @elseif (!featuredInternalLinks.isEmpty())
                        <span class="intent-card__label">Most likely next move</span>
                        <h2>${featuredInternalLinks.get(0).title()}</h2>
                        <p>${featuredInternalLinks.get(0).note()}</p>
                        <a class="button button--secondary" href="${featuredInternalLinks.get(0).path()}" data-track-click="nav" data-track-source-context="content_page_action_rail_internal" data-track-target-type="internal_page">Open next step</a>
                    @endif
                </article>
            </div>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container detail-grid">
            <article class="detail-card detail-card--full" id="drivers">
                <h2>Main estimate drivers</h2>
                <ul class="detail-list">
                    @for (var driver : contentPage.driverBullets())
                        <li>${driver}</li>
                    @endfor
                </ul>
            </article>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container detail-grid">
            <article class="detail-card">
                <h2>Who this page is for</h2>
                <p>${contentPage.targetReader()}</p>
                <ul class="detail-list">
                    @for (var bullet : contentPage.fitBullets())
                        <li>${bullet}</li>
                    @endfor
                </ul>
            </article>

            <article class="detail-card">
                <h2>How to use this page before you ask for quotes</h2>
                <ol class="detail-list">
                    @for (var step : contentPage.decisionSteps())
                        <li>${step}</li>
                    @endfor
                </ol>
            </article>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container detail-grid">
            <article class="detail-card">
                <h2>What usually kills the low end</h2>
                <ul class="detail-list">
                    @for (var risk : contentPage.lowEndBreakers())
                        <li>${risk}</li>
                    @endfor
                </ul>
            </article>

            <article class="detail-card">
                <h2>Bring this into the next estimate or quote</h2>
                <ul class="detail-list">
                    @for (var item : contentPage.quotePrepChecklist())
                        <li>${item}</li>
                    @endfor
                </ul>
            </article>
        </div>
    </section>

    <section class="section">
        <div class="container content-grid">
            <article class="trust-box trust-box--large">
                <span class="trust-box__label">Next best action</span>
                <h2>${calculatorCtaHeading}</h2>
                <p>${calculatorCtaNote} The result is most useful when you carry the file, inspection, or site uncertainty from this page into the estimate instead of starting from a generic statewide average.</p>
                <div class="hero__actions">
                    <a class="button button--primary" href="${calculatorPath}" data-track-click="nav" data-track-source-context="content_page_next_best_action" data-track-target-type="${calculatorCtaTargetType}">${calculatorCtaLabel}</a>
                    @if (contentQuotePath != null)
                        <a class="button button--secondary" href="${contentQuotePath}" data-track-click="nav" data-track-source-context="content_page_next_best_quote" data-track-target-type="quote_form">Start short quote form</a>
                    @endif
                </div>
            </article>

            <article class="detail-card">
                <h2>State guides</h2>
                <ul class="detail-list">
                    @for (var state : states)
                        <li><a href="/septic-system-cost-calculator/${state.slug()}/" data-track-click="nav" data-track-source-context="content_page_state_guides" data-track-target-type="state_guide">${state.stateName()} septic guide</a></li>
                    @endfor
                </ul>
            </article>
        </div>
    </section>

    @if (!contentEvidenceLanes.isEmpty())
        <section class="section section--tight" id="state-lanes">
            <div class="container">
                <article class="trust-box trust-box--large">
                    <span class="trust-box__label">How this page is sourced</span>
                    <h2>State-specific pages carry the official sources behind this national overview.</h2>
                    <p>This page stays generic on purpose. The linked state lanes below carry direct official sources, state-specific workflow context, and the last-reviewed dates that support the broader national guidance.</p>
                </article>
                <div class="detail-grid">
                    @for (var lane : contentEvidenceLanes)
                        <article class="detail-card">
                            <h3><a href="${lane.path()}" data-track-click="nav" data-track-source-context="content_page_source_lane" data-track-target-type="state_money_page">${lane.title()}</a></h3>
                            <p><strong>${lane.stateName()}</strong></p>
                            <p>${lane.reviewedAgainst()} Last reviewed ${lane.lastReviewedAt()}.</p>
                            <ul class="source-list source-list--compact">
                                @for (var source : lane.sources())
                                    <li>
                                        <strong>${source.agencyName()}</strong>
                                        <a href="${source.url()}" target="_blank" rel="noreferrer">${source.title()}</a>
                                        <div class="source-meta">
                                            @if (source.trustLevel() != null && !source.trustLevel().isBlank())
                                                <span>Trust: ${source.trustLevel()}</span>
                                            @endif
                                            @if (source.draftOrFinalStatus() != null && !source.draftOrFinalStatus().isBlank())
                                                <span>Status: ${source.draftOrFinalStatus()}</span>
                                            @endif
                                            @if (source.effectiveDate() != null && !source.effectiveDate().isBlank())
                                                <span>Effective: ${source.effectiveDate()}</span>
                                            @endif
                                            @if (source.lastVerifiedAt() != null && !source.lastVerifiedAt().isBlank())
                                                <span>Last verified: ${source.lastVerifiedAt()}</span>
                                            @endif
                                        </div>
                                    </li>
                                @endfor
                            </ul>
                        </article>
                    @endfor
                </div>
            </div>
        </section>
    @endif

    @if (!stateMoneyPageLinks.isEmpty())
        <section class="section section--tight">
            <div class="container">
                <article class="detail-card detail-card--full" id="state-pages">
                    <h2>State-specific pages</h2>
                    @if (!featuredStateMoneyPageLinks.isEmpty())
                        <div class="quick-link-strip" aria-label="Featured state-specific pages">
                            <span class="quick-link-strip__label">Featured states</span>
                            @for (var stateMoneyPageLink : featuredStateMoneyPageLinks)
                                <a class="button button--secondary quick-link-strip__button"
                                   href="${stateMoneyPageLink.path()}"
                                   data-track-click="nav"
                                   data-track-source-context="content_page_featured_state_specific"
                                   data-track-target-type="state_money_page">${stateMoneyPageLink.title()}</a>
                            @endfor
                        </div>
                    @endif
                    <ul class="detail-list">
                        @for (var stateMoneyPageLink : stateMoneyPageLinks)
                            <li><a href="${stateMoneyPageLink.path()}" data-track-click="nav" data-track-source-context="content_page_state_specific" data-track-target-type="state_money_page">${stateMoneyPageLink.title()}</a></li>
                        @endfor
                    </ul>
                </article>
            </div>
        </section>
    @endif

    <section class="section section--muted" id="faq">
        <div class="container">
            <div class="section-heading">
                <span class="eyebrow">FAQ</span>
                <h2>Questions this page should answer before the user clicks deeper.</h2>
            </div>

            <div class="detail-grid">
                @for (var faq : contentPage.faqBlocks())
                    <article class="detail-card">
                        <h3>${faq.question()}</h3>
                        <p>${faq.answer()}</p>
                    </article>
                @endfor
            </div>
        </div>
    </section>

    <section class="section">
        <div class="container">
            <article class="trust-box trust-box--large">
                <span class="trust-box__label">Related pages</span>
                <ul class="detail-list">
                    @for (var link : secondaryInternalLinks.isEmpty() ? internalLinks : secondaryInternalLinks)
                        <li>
                            <a href="${link.path()}" data-track-click="nav" data-track-source-context="content_page_related_links" data-track-target-type="internal_page">${link.title()}</a>
                            <p class="detail-list__note">${link.note()}</p>
                        </li>
                    @endfor
                </ul>
            </article>
        </div>
    </section>
`)
