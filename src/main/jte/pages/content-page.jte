@import com.example.septic.data.model.ContentPage
@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.PageMeta
@import com.example.septic.web.EditorialProfile
@import com.example.septic.web.CountyFinderLinkView
@param PageMeta page
@param ContentPage contentPage
@param java.util.List<StateProfile> states
@param java.util.List<com.example.septic.web.StateMoneyPageLink> stateMoneyPageLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.StateMoneyPageLink> featuredStateMoneyPageLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.ContentEvidenceLaneView> contentEvidenceLanes = java.util.List.of()
@param java.util.List<com.example.septic.web.ContentEvidenceLaneView> featuredContentEvidenceLanes = java.util.List.of()
@param com.example.septic.web.ContentWorkflowCoverageView contentWorkflowCoverage = null
@param java.util.List<com.example.septic.web.PageLink> internalLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> featuredInternalLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> secondaryInternalLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> permitLookupCountyLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> featuredPermitLookupCountyLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> secondaryPermitLookupCountyLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.CountyFinderLinkView> countyFinderLinks = java.util.List.of()
@param int totalCountyRouteCount = 0
@param java.util.List<com.example.septic.web.CountyRouteClusterView> countyRouteClusters = java.util.List.of()
@param String calculatorPath = "/septic-system-cost-calculator/"
@param String contentQuotePath = null
@param String calculatorCtaHeading = "Use the main estimator before you ask for quotes."
@param String calculatorCtaLabel = "Open the estimator"
@param String calculatorCtaNote = "Use your state and project assumptions first, then verify locally."
@param String calculatorCtaTargetType = "calculator"
@param String secondaryActionPath = null
@param String secondaryActionLabel = "Run the estimate"
@param String secondaryActionNote = ""
@param String secondaryActionTargetType = "calculator"
@param EditorialProfile editorialPreparedBy = null
@param EditorialProfile editorialReviewedBy = null
@param String editorialReviewedAgainst = ""
@param String editorialLastReviewedAt = ""
@param String editorialNote = ""

@template.layouts.app(page = page, content = @`
    <div class="content-page">
    <section class="section section--heroish">
        <div class="container state-hero">
            <div>
                <span class="eyebrow">
                    @if ("septic-transfer-compliance".equals(contentPage.slug()))
                        Transfer compliance guide
                    @elseif ("buyer_page".equals(contentPage.intentType()))
                        Homebuyer guide
                    @elseif ("workflow_page".equals(contentPage.intentType()))
                        Permit and records guide
                    @else
                        Septic planning guide
                    @endif
                </span>
                <h1>${contentPage.title()}</h1>
                <p class="lede">${contentPage.introCopy()}</p>
                <p class="hero-router-intro">
                    @if ("septic-permit-lookup".equals(contentPage.slug()))
                        Start with the state or county that can actually hold the permit file. If the county is already known, skip the overview and open that county route.
                    @elseif ("septic-records-by-county".equals(contentPage.slug()))
                        Pick the county route first. The job is to get to the permit copy, as-built, inspection letter, or office path before another broad explanation slows you down.
                    @else
                        Pick the first move that matches your situation. The longer explanation below should justify the move, not replace it. @if (contentWorkflowCoverage != null) ${contentWorkflowCoverage.summary()} @endif
                    @endif
                </p>
                @if (!featuredPermitLookupCountyLinks.isEmpty())
                    <div class="hero-county-launchpad" aria-label="Fast county record routes">
                        <span class="hero-county-launchpad__label">
                            @if ("septic-permit-lookup".equals(contentPage.slug()))
                                High-demand permit examples
                            @elseif ("septic-records-by-county".equals(contentPage.slug()))
                                County record examples
                            @else
                                Fast county routes
                            @endif
                        </span>
                        <p class="hero-county-launchpad__note">Use these as quick examples. If your property is elsewhere, open the state picker or more county routes below.</p>
                        @for (var link : featuredPermitLookupCountyLinks.stream().limit(4).toList())
                            <a href="${link.path()}"
                               data-track-click="nav"
                               data-track-source-context="content_page_hero_county_launchpad"
                               data-track-target-type="county_records_page">${link.compactTitle()}</a>
                        @endfor
                        <a href="#county-lookup-launchpad">More county routes</a>
                    </div>
                @endif
                <div class="micro-tool micro-tool--hero"
                     data-choice-group
                     data-state-surface-tool
                     data-state-surface-mode="${secondaryActionPath != null ? "state-first" : "tool-first"}"
                     data-state-surface-tool-label="${secondaryActionPath != null ? secondaryActionLabel : calculatorCtaLabel}"
                     data-state-surface-tool-supports-state="${calculatorPath.startsWith("/septic-system-cost-calculator") || calculatorPath.startsWith("/septic-tank-size-estimator") || calculatorPath.startsWith("/drain-field-estimator")}">
                    <div class="hero-decision-grid hero-decision-grid--three micro-tool__choices">
                        <button class="hero-decision-card hero-decision-card--accent micro-tool__choice is-active"
                                type="button"
                                data-choice="first"
                                aria-pressed="true">
                            @if (secondaryActionPath != null)
                                <span class="hero-decision-card__label">
                                    @if ("septic-permit-lookup".equals(contentPage.slug()))
                                        Find my permit
                                    @elseif ("septic-records-by-county".equals(contentPage.slug()))
                                        County file path
                                    @else
                                        State-first move
                                    @endif
                                </span>
                                <h3>
                                    @if ("septic-permit-lookup".equals(contentPage.slug()))
                                        Find the state or county permit record
                                    @elseif ("septic-records-by-county".equals(contentPage.slug()))
                                        Open the county septic record route
                                    @else
                                        I need the state-specific workflow before I price anything
                                    @endif
                                </h3>
                                <p>
                                    @if ("septic-permit-lookup".equals(contentPage.slug()))
                                        Start here when the search is really about a permit copy, county office, public records doorway, or parcel-backed septic file.
                                    @elseif ("septic-records-by-county".equals(contentPage.slug()))
                                        Use this when the county is known and the next click should be the local record path, not another national overview.
                                    @else
                                        Choose this when the national page is not enough and the real blocker is permit path, file history, buyer diligence, or one state-specific lane.
                                    @endif
                                </p>
                            @else
                                <span class="hero-decision-card__label">Tool-first move</span>
                                <h3>I need the planning tool before I read around</h3>
                                <p>Choose this when the main value of this page is getting you into the right calculator or planning tool quickly, then using the article as support.</p>
                            @endif
                        </button>

                        <button class="hero-decision-card micro-tool__choice"
                                type="button"
                                data-choice="second"
                                aria-pressed="false">
                            @if (secondaryActionPath != null)
                                <span class="hero-decision-card__label">
                                    @if ("septic-permit-lookup".equals(contentPage.slug()))
                                        Already know county
                                    @elseif ("septic-records-by-county".equals(contentPage.slug()))
                                        Request the file
                                    @else
                                        Estimate after workflow
                                    @endif
                                </span>
                                <h3>
                                    @if ("septic-permit-lookup".equals(contentPage.slug()))
                                        Jump to the county lookup launchpad
                                    @elseif ("septic-records-by-county".equals(contentPage.slug()))
                                        Ask for the permit copy or as-built
                                    @else
                                        I need the cautious number after I narrow the lane
                                    @endif
                                </h3>
                                <p>
                                    @if ("septic-permit-lookup".equals(contentPage.slug()))
                                        Use the county routes when the address, parcel, seller story, or listing already points to one local office.
                                    @elseif ("septic-records-by-county".equals(contentPage.slug()))
                                        Use this when the office needs a clear request for the permit copy, final approval, repair file, or inspection letter.
                                    @else
                                        Choose this when you know the workflow well enough to use the estimate without flattening the problem too early.
                                    @endif
                                </p>
                            @elseif (contentQuotePath != null)
                                <span class="hero-decision-card__label">Conversion-ready</span>
                                <h3>I already know the lane and need the short form</h3>
                                <p>Choose this when the page already matches the live problem and you do not need another broad explainer first.</p>
                            @else
                                <span class="hero-decision-card__label">Fast clarification</span>
                                <h3>I need one short clarification before I click deeper</h3>
                                <p>Choose this when the page is close but you still need the shortest possible answer before the next move.</p>
                            @endif
                        </button>

                        <button class="hero-decision-card micro-tool__choice"
                                type="button"
                                data-choice="example"
                                aria-pressed="false">
                            @if (!featuredStateMoneyPageLinks.isEmpty())
                                <span class="hero-decision-card__label">
                                    @if ("septic-permit-lookup".equals(contentPage.slug()) || "septic-records-by-county".equals(contentPage.slug()))
                                        Live local proof
                                    @else
                                        Live example
                                    @endif
                                </span>
                                <h3>
                                    @if ("septic-permit-lookup".equals(contentPage.slug()) || "septic-records-by-county".equals(contentPage.slug()))
                                        Show me a real state or county route
                                    @else
                                        Show me a source-backed state page first
                                    @endif
                                </h3>
                                <p>
                                    @if ("septic-permit-lookup".equals(contentPage.slug()) || "septic-records-by-county".equals(contentPage.slug()))
                                        Use a live source-backed route when you want proof that the national page turns into a concrete local next step.
                                    @else
                                        Choose this when you want to see how the national problem turns into a concrete local workflow and file story.
                                    @endif
                                </p>
                            @elseif (!featuredInternalLinks.isEmpty())
                                <span class="hero-decision-card__label">Next internal move</span>
                                <h3>Show me the strongest next page now</h3>
                                <p>Choose this when the broad page already did enough and the main question is where to go next inside the site.</p>
                            @else
                                <span class="hero-decision-card__label">Read the evidence</span>
                                <h3>Use the page itself to narrow the question</h3>
                                <p>Choose this when no stronger live example exists yet and the fastest move is using the page structure below.</p>
                            @endif
                        </button>
                    </div>

                    @if (!stateMoneyPageLinks.isEmpty())
                        <div class="micro-tool__picker micro-tool__picker--shared">
                            <span class="micro-tool__kicker">State-aware route</span>
                            <label class="micro-tool__picker-label" for="state-picker-${contentPage.slug()}">Pick your state once. The workflow, estimate, and example actions below will update for that state.</label>
                            <div class="micro-tool__picker-row">
                                <select id="state-picker-${contentPage.slug()}" data-state-surface-select>
                                    <option value="" data-state-path="#state-pages">Choose your state</option>
                                    @for (var stateMoneyPageLink : stateMoneyPageLinks)
                                        <option value="${stateMoneyPageLink.stateCode()}"
                                                data-state-path="${stateMoneyPageLink.path()}"
                                                data-state-title="${stateMoneyPageLink.surfaceTitle()}"
                                                data-state-workflow-fit="${stateMoneyPageLink.surfaceSignals().workflowFitLabel()}"
                                                data-state-workflow-fit-note="${stateMoneyPageLink.surfaceSignals().workflowFitNote()}"
                                                data-state-evidence-depth="${stateMoneyPageLink.surfaceSignals().evidenceDepthLabel()}"
                                                data-state-evidence-depth-note="${stateMoneyPageLink.surfaceSignals().evidenceDepthNote()}"
                                                data-state-tool-handoff="${stateMoneyPageLink.surfaceSignals().toolHandoffLabel()}"
                                                data-state-tool-handoff-note="${stateMoneyPageLink.surfaceSignals().toolHandoffNote()}">${stateMoneyPageLink.stateName()}</option>
                                    @endfor
                                </select>
                                <a class="button button--primary"
                                   href="#state-pages"
                                   data-state-surface-link="state-page"
                                   data-state-surface-default-label="Browse state pages"
                                   data-state-surface-selected-label="Open selected state page"
                                   data-state-surface-selected-target-type="state_money_page"
                                   data-track-click="nav"
                                   data-track-source-context="content_page_surface_picker"
                                   data-track-target-type="state_money_page_directory">Browse state pages</a>
                            </div>
                            <p class="micro-tool__picker-note">This makes the national page act more like a routing tool than a long article. Choose the state once, then use the lane below that matches the blocker.</p>
                        </div>

                        <article class="micro-tool__result" data-state-surface-result aria-live="polite">
                            <span class="micro-tool__kicker">Current route</span>
                            <h2 data-state-surface-result-heading>Choose a state to turn this broad page into a sharper next-step route.</h2>
                            <p data-state-surface-result-body>This page should help you decide the next move fast. Once you choose a state, the route below will tell you whether to open the local workflow first or move straight into the tool.</p>
                            <div class="micro-tool__signal-grid">
                                <article class="mini-card mini-card--signal">
                                    <span class="intent-card__label">Workflow fit</span>
                                    <strong data-state-surface-result-fit>Choose a state</strong>
                                    <p data-state-surface-result-fit-note>See how strong the selected state is as the local wedge for this broad surface.</p>
                                </article>
                                <article class="mini-card mini-card--signal">
                                    <span class="intent-card__label">Evidence depth</span>
                                    <strong data-state-surface-result-evidence>Waiting for state</strong>
                                    <p data-state-surface-result-evidence-note>We will show whether the state page is lightly directional or strongly source-backed.</p>
                                </article>
                                <article class="mini-card mini-card--signal">
                                    <span class="intent-card__label">Tool handoff</span>
                                    <strong data-state-surface-result-handoff>Waiting for state</strong>
                                    <p data-state-surface-result-handoff-note>We will show if the tool should be used now, after one local check, or only as a backstop.</p>
                                </article>
                            </div>
                            <div class="micro-tool__result-grid">
                                <article class="mini-card">
                                    <span class="intent-card__label">Start here</span>
                                    <strong data-state-surface-result-primary>Open a selected state page</strong>
                                    <p data-state-surface-result-primary-note>Use the matching state page when the main blocker is local workflow, records, permits, or buyer timing.</p>
                                </article>
                                <article class="mini-card">
                                    <span class="intent-card__label">Then do this</span>
                                    <strong data-state-surface-result-secondary>${secondaryActionPath != null ? secondaryActionLabel : calculatorCtaLabel}</strong>
                                    <p data-state-surface-result-secondary-note>Use the tool only after the route is narrow enough that the number means something.</p>
                                </article>
                            </div>
                        </article>
                    @endif

                    <div class="micro-tool__panels">
                        <article class="micro-tool__panel is-active" data-choice-panel="first">
                            <span class="micro-tool__kicker">Recommended next best action</span>
                            @if (secondaryActionPath != null)
                                <h2>Open the state-specific workflow first</h2>
                                <p>${calculatorCtaNote} This page is the broad overview. The next move should usually be the live state-specific workflow, because that is where the file, office path, and local rule differences stop being generic.</p>
                                @if (!stateMoneyPageLinks.isEmpty())
                                    <div class="micro-tool__actions">
                                        <a class="button button--primary"
                                           href="#state-pages"
                                           data-state-surface-link="state-page"
                                           data-state-surface-default-label="Browse state pages"
                                           data-state-surface-selected-label="Open selected state page"
                                           data-state-surface-selected-target-type="state_money_page"
                                           data-track-click="nav"
                                           data-track-source-context="content_page_micro_tool_primary_state_page"
                                           data-track-target-type="${calculatorCtaTargetType}">Browse state pages</a>
                                        <a class="button button--secondary"
                                           href="${secondaryActionPath}"
                                           data-state-surface-link="tool"
                                           data-state-surface-default-label="${secondaryActionLabel}"
                                           data-state-surface-selected-label="${secondaryActionLabel}"
                                           data-track-click="nav"
                                           data-track-source-context="content_page_micro_tool_secondary"
                                           data-track-target-type="${secondaryActionTargetType}">${secondaryActionLabel}</a>
                                    </div>
                                @else
                                    <div class="micro-tool__actions">
                                        <a class="button button--primary" href="${calculatorPath}" data-track-click="nav" data-track-source-context="content_page_micro_tool_primary" data-track-target-type="${calculatorCtaTargetType}">${calculatorCtaLabel}</a>
                                        <a class="button button--secondary" href="${secondaryActionPath}" data-track-click="nav" data-track-source-context="content_page_micro_tool_secondary" data-track-target-type="${secondaryActionTargetType}">${secondaryActionLabel}</a>
                                    </div>
                                @endif
                            @else
                                <h2>Start with the planning tool now</h2>
                                <p>${calculatorCtaNote} This page should support the tool, not make you read the whole article before you act.</p>
                                <div class="micro-tool__actions">
                                    <a class="button button--primary"
                                       href="${calculatorPath}"
                                       data-state-surface-link="tool"
                                       data-state-surface-default-label="${calculatorCtaLabel}"
                                       data-state-surface-selected-label="${calculatorCtaLabel}"
                                       data-track-click="nav"
                                       data-track-source-context="content_page_micro_tool_primary"
                                       data-track-target-type="${calculatorCtaTargetType}">${calculatorCtaLabel}</a>
                                    @if (!featuredInternalLinks.isEmpty())
                                        <a class="button button--secondary" href="${featuredInternalLinks.get(0).path()}" data-track-click="nav" data-track-source-context="content_page_micro_tool_primary_internal" data-track-target-type="internal_page">Open next step</a>
                                    @endif
                                </div>
                            @endif
                        </article>

                        <article class="micro-tool__panel" data-choice-panel="second" hidden>
                            <span class="micro-tool__kicker">Recommended next best action</span>
                            @if (secondaryActionPath != null)
                                <h2>Run the estimate after the workflow check</h2>
                                <p>${secondaryActionNote} Use the narrower workflow first when needed, then come back to the estimate once the state, file, or permit path is clear enough to make the number honest.</p>
                                <div class="micro-tool__actions">
                                    <a class="button button--primary"
                                       href="${secondaryActionPath}"
                                       data-state-surface-link="tool"
                                       data-state-surface-default-label="${secondaryActionLabel}"
                                       data-state-surface-selected-label="${secondaryActionLabel}"
                                       data-track-click="nav"
                                       data-track-source-context="content_page_micro_tool_secondary"
                                       data-track-target-type="${secondaryActionTargetType}">${secondaryActionLabel}</a>
                                    <a class="button button--secondary"
                                       href="${calculatorPath}"
                                       data-state-surface-link="state-page"
                                       data-state-surface-default-label="${calculatorCtaLabel}"
                                       data-state-surface-selected-label="Return to selected state page"
                                       data-state-surface-selected-target-type="state_money_page"
                                       data-track-click="nav"
                                       data-track-source-context="content_page_micro_tool_secondary_back"
                                       data-track-target-type="${calculatorCtaTargetType}">${calculatorCtaLabel}</a>
                                </div>
                            @elseif (contentQuotePath != null)
                                <h2>Open the short quote form now</h2>
                                <p>Use the short quote form only when this page already matches the live problem and you do not need another state, records, or permit detour first.</p>
                                <div class="micro-tool__actions">
                                    <a class="button button--primary"
                                       href="${contentQuotePath}"
                                       data-state-surface-link="tool"
                                       data-state-surface-default-label="Start short quote form"
                                       data-state-surface-selected-label="Start short quote form"
                                       data-track-click="nav"
                                       data-track-source-context="content_page_micro_tool_quote"
                                       data-track-target-type="quote_form">Start short quote form</a>
                                    <a class="button button--secondary"
                                       href="${calculatorPath}"
                                       data-state-surface-link="tool"
                                       data-state-surface-default-label="${calculatorCtaLabel}"
                                       data-state-surface-selected-label="${calculatorCtaLabel}"
                                       data-track-click="nav"
                                       data-track-source-context="content_page_micro_tool_quote_estimate"
                                       data-track-target-type="${calculatorCtaTargetType}">${calculatorCtaLabel}</a>
                                </div>
                            @else
                                <h2>Use the FAQ or drivers for one fast clarification</h2>
                                <p>When no stronger second action exists yet, the fastest move is using the structure below to narrow the question before you click deeper.</p>
                                <div class="micro-tool__actions">
                                    <a class="button button--primary" href="#faq">Jump to FAQs</a>
                                    <a class="button button--secondary" href="#drivers">Jump to drivers</a>
                                </div>
                            @endif
                        </article>

                        <article class="micro-tool__panel" data-choice-panel="example" hidden>
                            <span class="micro-tool__kicker">Recommended next best action</span>
                            @if (!featuredStateMoneyPageLinks.isEmpty())
                                <h2>Open one live state-specific example</h2>
                                <p>Use a live state-specific page when you want to see how this national overview turns into a local file path, permit sequence, or buyer-risk workflow.</p>
                                <div class="micro-tool__actions">
                                    <a class="button button--primary"
                                       href="${featuredStateMoneyPageLinks.get(0).path()}"
                                       data-state-surface-link="state-page"
                                       data-state-surface-default-label="Open live state page"
                                       data-state-surface-selected-label="Open selected state page"
                                       data-state-surface-selected-target-type="state_money_page"
                                       data-track-click="nav"
                                       data-track-source-context="content_page_micro_tool_example_state"
                                       data-track-target-type="state_money_page">Open live state page</a>
                                    @if (featuredStateMoneyPageLinks.size() > 1)
                                        <a class="button button--secondary" href="${featuredStateMoneyPageLinks.get(1).path()}" data-track-click="nav" data-track-source-context="content_page_micro_tool_example_state_alt" data-track-target-type="state_money_page">Compare another state page</a>
                                    @endif
                                </div>
                            @elseif (!featuredInternalLinks.isEmpty())
                                <h2>Open the strongest next page in this workflow</h2>
                                <p>${featuredInternalLinks.get(0).note()}</p>
                                <div class="micro-tool__actions">
                                    <a class="button button--primary" href="${featuredInternalLinks.get(0).path()}" data-track-click="nav" data-track-source-context="content_page_micro_tool_example_internal" data-track-target-type="internal_page">Open next step</a>
                                    <a class="button button--secondary" href="#drivers">Jump to drivers</a>
                                </div>
                            @else
                                <h2>Use the page itself as the working surface</h2>
                                <p>No stronger live example exists yet, so use the drivers, decision steps, state pages, and FAQ below as the fastest way to narrow the question into a real next move.</p>
                                <div class="micro-tool__actions">
                                    <a class="button button--primary" href="#drivers">Jump to drivers</a>
                                    <a class="button button--secondary" href="#state-pages">Jump to state pages</a>
                                </div>
                            @endif
                        </article>
                    </div>
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
                    @if ("septic-permit-lookup".equals(contentPage.slug()))
                        Permit lookup guide
                    @elseif ("septic-records-by-county".equals(contentPage.slug()))
                        County records router
                    @elseif ("septic-records-checklist".equals(contentPage.slug()))
                        Records workflow
                    @elseif ("tank_size_estimator".equals(contentPage.calculatorModule()))
                        Tank size tool
                    @elseif ("drainfield_estimator".equals(contentPage.calculatorModule()))
                        Drain field tool
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
                @if (contentWorkflowCoverage != null)
                    <div class="mini-card">
                        <strong>County-backed coverage</strong>
                        <p>${contentWorkflowCoverage.summary()}</p>
                        @if (!contentWorkflowCoverage.bullets().isEmpty())
                            <p>${contentWorkflowCoverage.bullets().get(0)}</p>
                        @endif
                    </div>
                @endif
            </article>
        </div>
    </section>

    @if ("septic-records-by-county".equals(contentPage.slug()) && !countyFinderLinks.isEmpty())
        <section class="section section--tight">
            <div class="container">
                <div class="county-index-brief" aria-label="Septic records availability index">
                    <div class="county-index-brief__copy">
                        <span class="eyebrow">Septic records availability index</span>
                        <h2>Start with the county routes that already show the office, first file, and request method.</h2>
                        <p>This hub is built to answer the searches that usually get lost in county websites: septic permit lookup, permit copy request, as-built records, inspection letters, parcel or TMS search, and buyer file checks.</p>
                    </div>
                    <div class="county-index-brief__metrics" aria-label="County records index coverage">
                        <div>
                            <span>Live county pages</span>
                            <strong>${totalCountyRouteCount}</strong>
                        </div>
                        <div>
                            <span>High-confidence routes loaded</span>
                            <strong>${countyFinderLinks.stream().filter(link -> link.confidenceScore() >= 82).count()}</strong>
                        </div>
                        <div>
                            <span>Parcel anchors loaded</span>
                            <strong>${countyFinderLinks.stream().filter(CountyFinderLinkView::parcelAnchorAvailable).count()}</strong>
                        </div>
                        <div>
                            <span>Priority search set</span>
                            <strong>${countyFinderLinks.size()}</strong>
                        </div>
                    </div>
                    <div class="county-index-brief__queries" aria-label="Search intents covered">
                        <span>permit lookup</span>
                        <span>records request</span>
                        <span>as-built records</span>
                        <span>inspection letter</span>
                        <span>parcel/TMS search</span>
                        <span>buyer file check</span>
                    </div>
                </div>
            </div>
        </section>
    @endif

    @template.tags.countyFinder(
        countyFinderLinks = countyFinderLinks,
        finderId = contentPage.slug() + "-county-finder",
        eyebrow = "County finder",
        heading = "Search the county route instead of staying on the national page.",
        body = "Use this when the visitor already has a state, county, parcel clue, permit-copy question, or records request in mind.",
        sourceContext = "content_page_county_finder",
        totalCountyRouteCount = totalCountyRouteCount
    )

    @if (!countyRouteClusters.isEmpty())
        <section class="section section--tight">
            <div class="container">
                <div class="section-heading">
                    <span class="eyebrow">State-by-state county routes</span>
                    <h2>When the searcher already knows the state or county, skip directly to the local file path.</h2>
                    <p class="lede">This board turns the national records page into a routing surface for county permit lookup, records request, parcel/TMS search, and buyer-file questions.</p>
                </div>

                <div class="county-route-board county-route-board--dense">
                    @for (var cluster : countyRouteClusters)
                        <article class="county-route-cluster">
                            <div class="county-route-cluster__header">
                                <div>
                                    <span class="pill">${cluster.stateCode()}</span>
                                    <h3>${cluster.heading()}</h3>
                                </div>
                                <strong>${cluster.metricLabel()}</strong>
                            </div>
                            <p>${cluster.summary()}</p>
                            <div class="county-route-cluster__links" aria-label="${cluster.stateName()} county route shortcuts">
                                @for (var link : cluster.countyLinks())
                                    <a href="${link.path()}"
                                       data-track-click="nav"
                                       data-track-source-context="content_page_county_route_board"
                                       data-track-target-type="county_records_page">${link.compactTitle()}</a>
                                @endfor
                            </div>
                            <div class="county-route-cluster__actions">
                                <a class="button button--secondary"
                                   href="${cluster.stateRecordsLink().path()}"
                                   data-track-click="nav"
                                   data-track-source-context="content_page_county_route_state_records"
                                   data-track-target-type="state_money_page">${cluster.stateRecordsLink().title()}</a>
                                <a class="button button--secondary"
                                   href="${cluster.permitProcessLink().path()}"
                                   data-track-click="nav"
                                   data-track-source-context="content_page_county_route_permit_process"
                                   data-track-target-type="state_money_page">Permit process</a>
                            </div>
                        </article>
                    @endfor
                </div>
            </div>
        </section>
    @endif

    <section class="section section--tight">
        <div class="container">
            <div class="section-jump-strip" aria-label="Jump between sections">
                <span class="section-jump-strip__label">Jump between sections</span>
                <a href="#drivers">Drivers</a>
                @if (!featuredPermitLookupCountyLinks.isEmpty())
                    <a href="#county-lookup-launchpad">County lookup</a>
                @endif
                <a href="#state-pages">State pages</a>
                @if (!contentEvidenceLanes.isEmpty())
                    <a href="#state-lanes">Sources</a>
                @endif
                <a href="#faq">FAQ</a>
            </div>

            <div class="intent-rail">
                <article class="intent-card intent-card--primary">
                    <span class="intent-card__label">${secondaryActionPath != null ? "Primary move" : "Run the estimate"}</span>
                    <h2>${calculatorCtaLabel}</h2>
                    <p>${calculatorCtaNote} @if (contentWorkflowCoverage != null) ${contentWorkflowCoverage.summary()} @endif</p>
                    <a class="button button--primary" href="${calculatorPath}" data-track-click="nav" data-track-source-context="content_page_action_rail_primary" data-track-target-type="${calculatorCtaTargetType}">${calculatorCtaLabel}</a>
                </article>

                <article class="intent-card">
                    @if (secondaryActionPath != null)
                        <span class="intent-card__label">Use the estimate after the workflow check</span>
                        <h2>${secondaryActionLabel}</h2>
                        <p>${secondaryActionNote} @if (contentWorkflowCoverage != null && !contentWorkflowCoverage.bullets().isEmpty()) ${contentWorkflowCoverage.bullets().get(0)} @endif</p>
                        <a class="button button--secondary" href="${secondaryActionPath}" data-track-click="nav" data-track-source-context="content_page_action_rail_secondary" data-track-target-type="${secondaryActionTargetType}">${secondaryActionLabel}</a>
                    @elseif (contentQuotePath != null)
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
                        <p>Open the strongest live state-specific page first when you want to see the official-source workflow behind this national overview. @if (contentWorkflowCoverage != null && contentWorkflowCoverage.bullets().size() > 1) ${contentWorkflowCoverage.bullets().get(1)} @endif</p>
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

    @if (!featuredPermitLookupCountyLinks.isEmpty())
        <section class="section section--tight" id="county-lookup-launchpad">
            <div class="container">
                <div class="section-heading">
                    <span class="eyebrow">County lookup launchpad</span>
                    <h2>Start with the county file when the search is already specific.</h2>
                    <p>These are the high-opportunity county routes behind the permit lookup sprint. Use them when the user already has a county name, parcel story, or seller claim and needs the actual file path before reading another broad state guide.</p>
                </div>
                <div class="detail-grid">
                    @for (var link : featuredPermitLookupCountyLinks)
                        <article class="detail-card">
                            <h3>${link.title()}</h3>
                            <p>${link.note()}</p>
                            <a class="button button--secondary"
                               href="${link.path()}"
                               data-track-click="nav"
                               data-track-source-context="permit_lookup_county_launchpad"
                               data-track-target-type="county_records_page">Open county lookup</a>
                        </article>
                    @endfor
                </div>
                @if (!secondaryPermitLookupCountyLinks.isEmpty())
                    <details class="state-money-disclosure">
                        <summary>
                            <span class="eyebrow">More county routes</span>
                            <strong>Show more permit lookup counties</strong>
                            <span>Use these when the county is already known and the state page would be a slower first click.</span>
                        </summary>
                        <div class="detail-grid">
                            @for (var link : secondaryPermitLookupCountyLinks)
                                <article class="detail-card">
                                    <h3>${link.title()}</h3>
                                    <p>${link.note()}</p>
                                    <a href="${link.path()}"
                                       data-track-click="nav"
                                       data-track-source-context="permit_lookup_county_launchpad_overflow"
                                       data-track-target-type="county_records_page">Open county lookup</a>
                                </article>
                            @endfor
                        </div>
                    </details>
                @endif
            </div>
        </section>
    @endif

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

    @if (!featuredStateMoneyPageLinks.isEmpty())
        <section class="section section--tight">
            <div class="container detail-grid">
                <article class="detail-card detail-card--full">
                    <h2>Use a live state page before you trust the national range</h2>
                    <p>
                        This page stays national on purpose. If you want the source-backed version of this workflow, start with
                        <a href="${featuredStateMoneyPageLinks.get(0).path()}"
                           data-track-click="nav"
                           data-track-source-context="content_page_inline_state_link"
                           data-track-target-type="state_money_page">${featuredStateMoneyPageLinks.get(0).title()}</a>
                        @if (featuredStateMoneyPageLinks.size() > 1)
                            and compare it with
                            <a href="${featuredStateMoneyPageLinks.get(1).path()}"
                               data-track-click="nav"
                               data-track-source-context="content_page_inline_state_link"
                               data-track-target-type="state_money_page">${featuredStateMoneyPageLinks.get(1).title()}</a>.
                        @else
                            before you trust a broad national range.
                        @endif
                    </p>
                    @if (!contentEvidenceLanes.isEmpty())
                        <p>
                            The linked state pages carry direct official sources, last-reviewed dates, and the local file path that changes the quote story. That is why
                            <a href="${contentEvidenceLanes.get(0).path()}"
                               data-track-click="nav"
                               data-track-source-context="content_page_inline_evidence_link"
                               data-track-target-type="state_money_page">${contentEvidenceLanes.get(0).title()}</a>
                            @if (contentEvidenceLanes.size() > 1)
                                and
                                <a href="${contentEvidenceLanes.get(1).path()}"
                                   data-track-click="nav"
                                   data-track-source-context="content_page_inline_evidence_link"
                                   data-track-target-type="state_money_page">${contentEvidenceLanes.get(1).title()}</a>
                            @endif
                            are stronger next clicks than another generic explainer when you are about to pull records or call a contractor.
                        </p>
                    @endif
                    @if (featuredStateMoneyPageLinks.size() > 2)
                        <p>
                            If your situation looks closer to
                            <a href="${featuredStateMoneyPageLinks.get(2).path()}"
                               data-track-click="nav"
                               data-track-source-context="content_page_inline_state_link"
                               data-track-target-type="state_money_page">${featuredStateMoneyPageLinks.get(2).title()}</a>,
                            click through before you rely on the checklist below. The national page frames the question; the state page carries the file, office, and risk context that changes the answer.
                        </p>
                    @endif
                </article>
            </div>
        </section>
    @endif

    <section class="section section--tight">
        <div class="container detail-grid">
            <article class="detail-card detail-card--full">
                <h2>What this national page can answer before you touch a quote</h2>
                <p>${contentPage.targetReader()} This national page is strongest when you still need to frame the problem correctly before you call a contractor, ask for transfer records, or push into a permit conversation.</p>
                <p>${contentPage.introCopy()} Use this page to separate the broad cost story from the real bottleneck. In practice, that usually means deciding whether the next move is the estimator, a state-specific page, or a records and inspection workflow instead of another generic explainer.</p>
                @if (!featuredStateMoneyPageLinks.isEmpty())
                    <p>
                        If the shape of your situation already feels state-specific, move next into
                        <a href="${featuredStateMoneyPageLinks.get(0).path()}"
                           data-track-click="nav"
                           data-track-source-context="content_page_inline_state_link"
                           data-track-target-type="state_money_page">${featuredStateMoneyPageLinks.get(0).title()}</a>
                        @if (featuredStateMoneyPageLinks.size() > 1)
                            or
                            <a href="${featuredStateMoneyPageLinks.get(1).path()}"
                               data-track-click="nav"
                               data-track-source-context="content_page_inline_state_link"
                               data-track-target-type="state_money_page">${featuredStateMoneyPageLinks.get(1).title()}</a>
                        @endif
                        before you trust any low-end national range.
                    </p>
                @endif
            </article>
        </div>
    </section>

    @if (contentWorkflowCoverage != null)
        <section class="section section--tight">
            <div class="container detail-grid">
                <article class="detail-card detail-card--full">
                    <h2>${contentWorkflowCoverage.heading()}</h2>
                    <p>${contentWorkflowCoverage.summary()}</p>
                    <ul class="detail-list">
                        @for (var bullet : contentWorkflowCoverage.bullets())
                            <li>${bullet}</li>
                        @endfor
                    </ul>
                </article>
            </div>
        </section>
    @endif

    @if (contentPage.deepDiveParagraphs() != null && !contentPage.deepDiveParagraphs().isEmpty())
        <section class="section section--tight">
            <div class="container">
                <details class="state-money-disclosure">
                    <summary>
                        <span class="eyebrow">Decision context</span>
                        <strong>What this page is really helping you decide</strong>
                        <span>Open this if you need the deeper explanation behind the short route above.</span>
                    </summary>
                    <div class="detail-grid">
                        <article class="detail-card detail-card--full">
                            @for (var paragraph : contentPage.deepDiveParagraphs())
                                <p>${paragraph}</p>
                            @endfor
                        </article>
                    </div>
                </details>
            </div>
        </section>
    @endif

    @if (!featuredContentEvidenceLanes.isEmpty())
        <section class="section section--tight">
            <div class="container detail-grid">
                <article class="detail-card detail-card--full">
                    <h2>Representative state examples behind this national page</h2>
                    @for (var lane : featuredContentEvidenceLanes)
                        <p>
                            In <strong>${lane.stateName()}</strong>,
                            <a href="${lane.path()}"
                               data-track-click="nav"
                               data-track-source-context="content_page_state_example_link"
                               data-track-target-type="state_money_page">${lane.title()}</a>
                            is the stronger next read when ${lane.summary()}
                            @if (!lane.sources().isEmpty())
                                One of the primary official sources behind this example is
                                <a href="${lane.sources().get(0).url()}"
                                   target="_blank"
                                   rel="noreferrer"
                                   data-track-click="nav"
                                   data-track-source-context="content_page_state_example_source"
                                   data-track-target-type="official_source">${lane.sources().get(0).agencyName()}</a>.
                            @endif
                        </p>
                    @endfor
                </article>
            </div>
        </section>
    @endif

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

    @if (!featuredInternalLinks.isEmpty() || !featuredStateMoneyPageLinks.isEmpty())
        <section class="section section--tight">
            <div class="container detail-grid">
                <article class="detail-card detail-card--full">
                    <h2>When this page stops being enough</h2>
                    <p>The national page should get you to the right lane, not keep you here forever. Once you need the real file path, local office, reserve-area risk, transfer rule, or state review wrinkle, move into the narrower page that matches the blocker instead of rereading the same overview.</p>
                    @if (!featuredInternalLinks.isEmpty())
                        <p>
                            If the blocker is workflow rather than geography, go next to
                            <a href="${featuredInternalLinks.get(0).path()}"
                               data-track-click="nav"
                               data-track-source-context="content_page_inline_internal_link"
                               data-track-target-type="internal_page">${featuredInternalLinks.get(0).title()}</a>
                            @if (featuredInternalLinks.size() > 1)
                                or
                                <a href="${featuredInternalLinks.get(1).path()}"
                                   data-track-click="nav"
                                   data-track-source-context="content_page_inline_internal_link"
                                   data-track-target-type="internal_page">${featuredInternalLinks.get(1).title()}</a>
                            @endif
                            when the next question is really about records, permits, buyer timing, or inspection evidence.
                        </p>
                    @endif
                    @if (!featuredStateMoneyPageLinks.isEmpty())
                        <p>
                            If the blocker is state-specific, move from this overview into
                            <a href="${featuredStateMoneyPageLinks.get(0).path()}"
                               data-track-click="nav"
                               data-track-source-context="content_page_inline_state_link"
                               data-track-target-type="state_money_page">${featuredStateMoneyPageLinks.get(0).title()}</a>
                            @if (featuredStateMoneyPageLinks.size() > 2)
                                and keep
                                <a href="${featuredStateMoneyPageLinks.get(2).path()}"
                                   data-track-click="nav"
                                   data-track-source-context="content_page_inline_state_link"
                                   data-track-target-type="state_money_page">${featuredStateMoneyPageLinks.get(2).title()}</a>
                                as the comparison page
                            @endif
                            so the estimate and quote conversation stays tied to a real local workflow.
                        </p>
                    @endif
                </article>
            </div>
        </section>
    @endif

    <section class="section">
        <div class="container content-grid content-grid--top">
                <article class="trust-box trust-box--large">
                    <span class="trust-box__label">Next best action</span>
                    <h2>${calculatorCtaHeading}</h2>
                    <p>
                        ${calculatorCtaNote}
                        @if (secondaryActionPath == null)
                            The result is most useful when you carry the file, inspection, or site uncertainty from this page into the estimate instead of starting from a generic statewide average.
                        @endif
                    </p>
                    <div class="hero__actions">
                        <a class="button button--primary" href="${calculatorPath}" data-track-click="nav" data-track-source-context="content_page_next_best_action" data-track-target-type="${calculatorCtaTargetType}">${calculatorCtaLabel}</a>
                        @if (secondaryActionPath != null)
                            <a class="button button--secondary" href="${secondaryActionPath}" data-track-click="nav" data-track-source-context="content_page_next_best_secondary" data-track-target-type="${secondaryActionTargetType}">${secondaryActionLabel}</a>
                        @elseif (contentQuotePath != null)
                            <a class="button button--secondary" href="${contentQuotePath}" data-track-click="nav" data-track-source-context="content_page_next_best_quote" data-track-target-type="quote_form">Start short quote form</a>
                        @endif
                    </div>
            </article>

            <article class="detail-card">
                <h2>State guides</h2>
                <ul class="detail-list detail-list--compact detail-list--split">
                    @for (var state : states.stream().limit(12).toList())
                        <li><a href="/septic-system-cost-calculator/${state.slug()}/" data-track-click="nav" data-track-source-context="content_page_state_guides" data-track-target-type="state_guide">${state.stateName()} septic guide</a></li>
                    @endfor
                </ul>
                @if (states.size() > 12)
                    <details class="county-link-overflow">
                        <summary>Show more state guides</summary>
                        <ul class="detail-list detail-list--compact detail-list--split">
                            @for (var state : states)
                                <li><a href="/septic-system-cost-calculator/${state.slug()}/" data-track-click="nav" data-track-source-context="content_page_state_guides_overflow" data-track-target-type="state_guide">${state.stateName()} septic guide</a></li>
                            @endfor
                        </ul>
                    </details>
                @endif
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
                    @for (var lane : contentEvidenceLanes.stream().limit(6).toList())
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
                @if (contentEvidenceLanes.size() > 6)
                    <details class="county-link-overflow">
                        <summary>Show more source-backed state lanes</summary>
                        <div class="detail-grid">
                            @for (var lane : contentEvidenceLanes)
                                <article class="detail-card">
                                    <h3><a href="${lane.path()}" data-track-click="nav" data-track-source-context="content_page_source_lane_overflow" data-track-target-type="state_money_page">${lane.title()}</a></h3>
                                    <p><strong>${lane.stateName()}</strong></p>
                                    <p>${lane.reviewedAgainst()} Last reviewed ${lane.lastReviewedAt()}.</p>
                                </article>
                            @endfor
                        </div>
                    </details>
                @endif
            </div>
        </section>
    @endif

    @if (!stateMoneyPageLinks.isEmpty())
        <section class="section section--tight">
            <div class="container">
                <article class="detail-card detail-card--full" id="state-pages">
                    <h2>
                        @if ("septic-transfer-compliance".equals(contentPage.slug()))
                            State-specific transfer workflow pages
                        @else
                            State-specific pages
                        @endif
                    </h2>
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
                        @for (var stateMoneyPageLink : stateMoneyPageLinks.stream().limit(12).toList())
                            <li><a href="${stateMoneyPageLink.path()}" data-track-click="nav" data-track-source-context="content_page_state_specific" data-track-target-type="state_money_page">${stateMoneyPageLink.title()}</a></li>
                        @endfor
                    </ul>
                    @if (stateMoneyPageLinks.size() > 12)
                        <details class="county-link-overflow">
                            <summary>Show more state-specific workflow pages</summary>
                            <ul class="detail-list">
                                @for (var stateMoneyPageLink : stateMoneyPageLinks)
                                    <li><a href="${stateMoneyPageLink.path()}" data-track-click="nav" data-track-source-context="content_page_state_specific_overflow" data-track-target-type="state_money_page">${stateMoneyPageLink.title()}</a></li>
                                @endfor
                            </ul>
                        </details>
                    @endif
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
                @for (var faq : contentPage.faqBlocks().stream().limit(4).toList())
                    <article class="detail-card">
                        <h3>${faq.question()}</h3>
                        <p>${faq.answer()}</p>
                    </article>
                @endfor
            </div>
            @if (contentPage.faqBlocks().size() > 4)
                <details class="county-link-overflow">
                    <summary>Show all FAQ answers</summary>
                    <div class="detail-grid">
                        @for (var faq : contentPage.faqBlocks())
                            <article class="detail-card">
                                <h3>${faq.question()}</h3>
                                <p>${faq.answer()}</p>
                            </article>
                        @endfor
                    </div>
                </details>
            @endif
        </div>
    </section>

    <section class="section">
        <div class="container">
            <article class="trust-box trust-box--large">
                <span class="trust-box__label">Related pages</span>
                <ul class="detail-list">
                    @for (var link : (secondaryInternalLinks.isEmpty() ? internalLinks : secondaryInternalLinks).stream().limit(6).toList())
                        <li>
                            <a href="${link.path()}" data-track-click="nav" data-track-source-context="content_page_related_links" data-track-target-type="internal_page">${link.title()}</a>
                            <p class="detail-list__note">${link.note()}</p>
                        </li>
                    @endfor
                </ul>
                @if ((secondaryInternalLinks.isEmpty() ? internalLinks : secondaryInternalLinks).size() > 6)
                    <details class="county-link-overflow">
                        <summary>Show more related pages</summary>
                        <ul class="detail-list">
                            @for (var link : secondaryInternalLinks.isEmpty() ? internalLinks : secondaryInternalLinks)
                                <li>
                                    <a href="${link.path()}" data-track-click="nav" data-track-source-context="content_page_related_links_overflow" data-track-target-type="internal_page">${link.title()}</a>
                                    <p class="detail-list__note">${link.note()}</p>
                                </li>
                            @endfor
                        </ul>
                    </details>
                @endif
            </article>
        </div>
    </section>
    </div>
`)
