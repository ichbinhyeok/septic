@import com.example.septic.data.model.ContentPage
@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.PageMeta
@import com.example.septic.web.EditorialProfile
@import com.example.septic.web.CountyFinderLinkView
@import com.example.septic.web.CountyWorkflowFieldView
@param PageMeta page
@param ContentPage contentPage
@param java.util.List<StateProfile> states
@param java.util.List<com.example.septic.web.StateMoneyPageLink> stateMoneyPageLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.StateMoneyPageLink> featuredStateMoneyPageLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.ContentEvidenceLaneView> contentEvidenceLanes = java.util.List.of()
@param java.util.List<com.example.septic.web.ContentEvidenceLaneView> featuredContentEvidenceLanes = java.util.List.of()
@param com.example.septic.web.ContentWorkflowCoverageView contentWorkflowCoverage = null
@param java.util.List<CountyWorkflowFieldView> contentOfficialFilePathRows = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> internalLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> featuredInternalLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> secondaryInternalLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> permitLookupCountyLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> featuredPermitLookupCountyLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> secondaryPermitLookupCountyLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.CountyFinderLinkView> countyFinderLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.CountyFinderLinkView> directOnlineCountyFinderLinks = java.util.List.of()
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
    <div class="content-page content-page--${contentPage.slug()}">
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
                @if (secondaryActionPath != null || contentQuotePath != null || !featuredStateMoneyPageLinks.isEmpty() || !featuredInternalLinks.isEmpty() || !featuredPermitLookupCountyLinks.isEmpty() || !contentOfficialFilePathRows.isEmpty())
                    <div class="hero-command-board" aria-label="Fast search intent routes">
                        <a class="hero-command hero-command--primary"
                           href="${"septic-records-by-county".equals(contentPage.slug()) ? "#septic-records-by-county-county-finder" : ("septic-permit-search-by-address".equals(contentPage.slug()) ? "#address-record-finder" : (("official-septic-lookup-tools".equals(contentPage.slug()) || "tdec-septic-records".equals(contentPage.slug()) || "north-carolina-septic-permit-lookup".equals(contentPage.slug()) || "texas-ossf-records-search".equals(contentPage.slug()) || "florida-ostds-permit-lookup".equals(contentPage.slug()) || "dhec-septic-permit-lookup".equals(contentPage.slug())) ? "#official-lookup-command-board" : calculatorPath))}"
                           data-track-click="nav"
                           data-track-source-context="content_page_hero_command_primary"
                           data-track-target-type="${"septic-permit-search-by-address".equals(contentPage.slug()) ? "internal_section" : calculatorCtaTargetType}">
                            <span>Start here</span>
                            <strong>${"septic-records-by-county".equals(contentPage.slug()) ? "Search a county record route" : ("septic-permit-search-by-address".equals(contentPage.slug()) ? "Find my county records route" : calculatorCtaLabel)}</strong>
                            <small>${"septic-records-by-county".equals(contentPage.slug()) ? "Type the county you already know and open its local septic file path." : ("septic-permit-search-by-address".equals(contentPage.slug()) ? "Paste the property address to resolve the county, then open the right public records path." : calculatorCtaHeading)}</small>
                        </a>
                        @if ("buying-a-house-with-a-septic-system".equals(contentPage.slug()) || "septic-transfer-compliance".equals(contentPage.slug()) || "septic-permit-search-by-address".equals(contentPage.slug()))
                            <a class="hero-command"
                               href="/septic-bedroom-permit-checker/"
                               data-track-click="nav"
                               data-track-source-context="content_page_hero_bedroom_checker"
                               data-track-target-type="internal_tool">
                                <span>Listing file check</span>
                                <strong>Compare bedrooms to the septic permit</strong>
                                <small>Flag a permit-count gap before the listing, offer, lender, or inspection conversation relies on the wrong number.</small>
                            </a>
                        @endif
                        @if (secondaryActionPath != null)
                            <a class="hero-command"
                               href="${secondaryActionPath}"
                               data-track-click="nav"
                               data-track-source-context="content_page_hero_command_secondary"
                               data-track-target-type="${secondaryActionTargetType}">
                                <span>Then validate</span>
                                <strong>${secondaryActionLabel}</strong>
                                <small>${secondaryActionNote}</small>
                            </a>
                        @elseif (contentQuotePath != null)
                            <a class="hero-command"
                               href="${contentQuotePath}"
                               data-track-click="nav"
                               data-track-source-context="content_page_hero_command_quote"
                               data-track-target-type="quote_form">
                                <span>Already decided</span>
                                <strong>Open the short quote form</strong>
                                <small>Use this only when the page already matches the live project and no local file path is blocking the decision.</small>
                            </a>
                        @elseif (!featuredInternalLinks.isEmpty())
                            <a class="hero-command"
                               href="${featuredInternalLinks.get(0).path()}"
                               data-track-click="nav"
                               data-track-source-context="content_page_hero_command_internal"
                               data-track-target-type="internal_page">
                                <span>Next best page</span>
                                <strong>${featuredInternalLinks.get(0).title()}</strong>
                                <small>${featuredInternalLinks.get(0).note()}</small>
                            </a>
                        @endif
                        @if (!contentOfficialFilePathRows.isEmpty() && ("official-septic-lookup-tools".equals(contentPage.slug()) || "tdec-septic-records".equals(contentPage.slug()) || "north-carolina-septic-permit-lookup".equals(contentPage.slug()) || "texas-ossf-records-search".equals(contentPage.slug()) || "florida-ostds-permit-lookup".equals(contentPage.slug()) || "dhec-septic-permit-lookup".equals(contentPage.slug())))
                            <a class="hero-command"
                               href="#official-file-path"
                               data-track-click="nav"
                               data-track-source-context="content_page_hero_command_file_path"
                               data-track-target-type="internal_section">
                                <span>Need the file?</span>
                                <strong>Open the official file path</strong>
                                <small>Use the owner, first artifact, request method, and fallback before trusting a generic septic answer.</small>
                            </a>
                        @elseif (!featuredPermitLookupCountyLinks.isEmpty())
                            <a class="hero-command"
                               href="${featuredPermitLookupCountyLinks.get(0).path()}"
                               data-track-click="nav"
                               data-track-source-context="content_page_hero_command_county"
                               data-track-target-type="county_records_page">
                                <span>Known county?</span>
                                <strong>${featuredPermitLookupCountyLinks.get(0).compactTitle()}</strong>
                                <small>Skip the national overview when the address, parcel, seller story, or search result already names a county.</small>
                            </a>
                        @elseif ("septic-permit-lookup".equals(contentPage.slug()) || "septic-records-by-county".equals(contentPage.slug()) || "septic-permit-search-by-address".equals(contentPage.slug()) || "septic-permit-records-request".equals(contentPage.slug()) || "septic-as-built-records".equals(contentPage.slug()) || "how-to-find-septic-records-online".equals(contentPage.slug()))
                            <a class="hero-command"
                               href="#records-next-click"
                               data-track-click="nav"
                               data-track-source-context="content_page_hero_command_records"
                               data-track-target-type="internal_section">
                                <span>Search stalled?</span>
                                <strong>Use the records next-click board</strong>
                                <small>Move from broad search intent into an official tool, county file pull, or copy-ready request.</small>
                            </a>
                        @elseif (!featuredStateMoneyPageLinks.isEmpty())
                            <a class="hero-command"
                               href="${featuredStateMoneyPageLinks.get(0).path()}"
                               data-track-click="nav"
                               data-track-source-context="content_page_hero_command_state"
                               data-track-target-type="state_money_page">
                                <span>Local proof</span>
                                <strong>${featuredStateMoneyPageLinks.get(0).title()}</strong>
                                <small>Open a source-backed state workflow when the broad page is not specific enough.</small>
                            </a>
                        @elseif (!featuredInternalLinks.isEmpty())
                            <a class="hero-command"
                               href="${featuredInternalLinks.get(0).path()}"
                               data-track-click="nav"
                               data-track-source-context="content_page_hero_command_next"
                               data-track-target-type="internal_page">
                                <span>Next move</span>
                                <strong>${featuredInternalLinks.get(0).title()}</strong>
                                <small>${featuredInternalLinks.get(0).note()}</small>
                            </a>
                        @endif
                    </div>
                @endif
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

    @if (!contentOfficialFilePathRows.isEmpty())
        <section class="section section--tight" id="official-file-path">
            <div class="container">
                <div class="state-records-response" aria-label="${contentPage.title()} official content file path">
                    <div class="state-records-response__head">
                        <div>
                            <span class="eyebrow">Official content file path</span>
                            <h2>Turn this search into a file owner, first artifact, request method, and fallback.</h2>
                            <p>This block applies the records-page pattern to the national hub so a high-intent searcher can move from broad lookup intent into an official file pull without restarting on Google.</p>
                        </div>
                        <div class="state-records-response__priority">
                            <span>Winning pattern</span>
                            <strong>Owner, artifact, request, fallback</strong>
                        </div>
                    </div>

                    <div class="state-records-response__body">
                        <div class="state-records-response__rows">
                            @for (var row : contentOfficialFilePathRows)
                                <article>
                                    <span>${row.label()}</span>
                                    <p>${row.value()}</p>
                                </article>
                            @endfor
                        </div>

                        <aside class="state-records-response__route" aria-label="Official content file path action order">
                            <span class="state-records-response__route-label">Action order</span>
                            <h3>Keep the next click in the records workflow.</h3>
                            <div class="state-records-response__route-list">
                                @if ("tdec-septic-records".equals(contentPage.slug()))
                                    <a href="https://tdec.tn.gov/document-viewer/search/stp"
                                       target="_blank"
                                       rel="noreferrer"
                                       data-track-click="nav"
                                       data-track-source-context="content_official_file_path_external"
                                       data-track-target-type="official_source">
                                        <strong>Open TDEC SSDS search</strong>
                                        <span>Use the official Tennessee septic records search before the county fallback.</span>
                                    </a>
                                @elseif ("dhec-septic-permit-lookup".equals(contentPage.slug()))
                                    <a href="https://des.sc.gov/permits-regulations/septic-tanks"
                                       target="_blank"
                                       rel="noreferrer"
                                       data-track-click="nav"
                                       data-track-source-context="content_official_file_path_external"
                                       data-track-target-type="official_source">
                                        <strong>Open SCDES septic route</strong>
                                        <span>Translate DHEC searches into the current South Carolina septic path.</span>
                                    </a>
                                @elseif ("texas-ossf-records-search".equals(contentPage.slug()))
                                    <a href="https://www.tceq.texas.gov/permitting/ossf"
                                       target="_blank"
                                       rel="noreferrer"
                                       data-track-click="nav"
                                       data-track-source-context="content_official_file_path_external"
                                       data-track-target-type="official_source">
                                        <strong>Open TCEQ OSSF context</strong>
                                        <span>Use this before resolving the local authorized-agent or county route.</span>
                                    </a>
                                @else
                                    <a href="/official-septic-lookup-tools/"
                                       data-track-click="nav"
                                       data-track-source-context="content_official_file_path_tools"
                                       data-track-target-type="internal_page">
                                        <strong>Open official tools</strong>
                                        <span>Use TDEC, SCDES, OSSF, OSTDS, and county records routes from one board.</span>
                                    </a>
                                @endif
                                <p class="state-records-response__route-note">
                                    If the official site blocks the new tab, stay in this workflow and use address search, county routing, or copy-ready request wording next.
                                </p>
                                <a href="/septic-permit-search-by-address/"
                                   data-track-click="nav"
                                   data-track-source-context="content_official_file_path_address"
                                   data-track-target-type="internal_page">
                                    <strong>Search by address</strong>
                                    <span>Carry address, parcel, APN/TMS, owner, and legal-description clues.</span>
                                </a>
                                <a href="/septic-permit-records-request/"
                                   data-track-click="nav"
                                   data-track-source-context="content_official_file_path_request"
                                   data-track-target-type="internal_page">
                                    <strong>Records request wording</strong>
                                    <span>Ask for the permit copy, as-built, final approval, repair file, or no-record response.</span>
                                </a>
                                <a href="/septic-records-by-county/"
                                   data-track-click="nav"
                                   data-track-source-context="content_official_file_path_county"
                                   data-track-target-type="internal_page">
                                    <strong>Open county routes</strong>
                                    <span>Use county pages when the search already names the local office or county.</span>
                                </a>
                            </div>
                        </aside>
                    </div>
                </div>
            </div>
        </section>
    @endif

    @if ("septic-permit-search-by-address".equals(contentPage.slug()))
        <section class="section section--tight" id="address-record-finder" aria-label="Address-to-record route finder">
            <div class="container">
                @template.tags.addressRecordFinder(
                    finderId = "permit-search-by-address",
                    eyebrow = "Address-to-record relay",
                    heading = "Turn one address into the right public-record route.",
                    intro = "Resolve the county first. Tennessee addresses also open the parcel, TDEC SSDS, and inspection-letter relay without storing the address.",
                    totalCountyRouteCount = totalCountyRouteCount,
                    compact = true
                )
            </div>
        </section>
    @endif

    @if ("septic-permit-lookup".equals(contentPage.slug()) || "septic-records-by-county".equals(contentPage.slug()) || "septic-permit-search-by-address".equals(contentPage.slug()) || "septic-permit-records-request".equals(contentPage.slug()) || "septic-as-built-records".equals(contentPage.slug()) || "how-to-find-septic-records-online".equals(contentPage.slug()))
        <section class="section section--tight" id="records-next-click">
            <div class="container">
                <div class="records-escalation-board" aria-label="Records lookup next-click accelerator">
                    <div class="records-escalation-board__intro">
                        <span class="eyebrow">Next-click accelerator</span>
                        <h2>
                            @if ("septic-permit-search-by-address".equals(contentPage.slug()))
                                If the address search stalls, move to parcel, official tools, or a copy-ready request.
                            @elseif ("septic-permit-records-request".equals(contentPage.slug()))
                                Turn the records request into a precise file pull before the office answers vaguely.
                            @elseif ("septic-as-built-records".equals(contentPage.slug()))
                                If the layout is missing, request the exact as-built, final approval, or no-record response.
                            @elseif ("septic-records-by-county".equals(contentPage.slug()))
                                Open the county route, then escalate to official tools or a written request when the file is thin.
                            @else
                                Move from broad septic-records search intent into an official route or county file pull.
                            @endif
                        </h2>
                        <p>These are the high-intent exits for searchers who already need a permit copy, as-built, D-1740 file, parcel anchor, or county office response.</p>
                    </div>

                    <div class="records-escalation-board__grid">
                        <article class="records-escalation-card records-escalation-card--primary">
                            <span>Official source</span>
                            <h3>Open the official lookup board</h3>
                            <p>Use this when the visitor needs TDEC, SCDES, OSSF, OSTDS, county health, or public-records routing before another article.</p>
                            <a class="button button--primary"
                               href="/official-septic-lookup-tools/"
                               data-track-click="nav"
                               data-track-source-context="records_escalation_official_tools"
                               data-track-target-type="internal_page">Open official tools</a>
                        </article>

                        <article class="records-escalation-card records-escalation-card--primary">
                            <span>TN / TDEC</span>
                            <h3>Answer TDEC septic records searches directly</h3>
                            <p>Push Tennessee records searches into the TDEC SSDS route, permit lookup, county fallback, and request wording before the user bounces back to Google.</p>
                            <a class="button button--primary"
                               href="/tdec-septic-records/"
                               data-track-click="nav"
                               data-track-source-context="records_escalation_tdec"
                               data-track-target-type="internal_page">Open TDEC records guide</a>
                        </article>

                        <article class="records-escalation-card">
                            <span>Request builder</span>
                            <h3>Generate the email before the user leaves</h3>
                            <p>Convert address, county, parcel, record type, and reason into a copy-ready request for the office.</p>
                            <a class="button button--secondary"
                               href="/septic-records-request-builder/"
                               data-track-click="nav"
                               data-track-source-context="records_escalation_request_builder"
                               data-track-target-type="internal_page">Build records request</a>
                        </article>

                        <article class="records-escalation-card">
                            <span>SC / DHEC</span>
                            <h3>DHEC searchers need the SCDES route</h3>
                            <p>Route South Carolina DHEC-style searches into current SCDES septic, ePermitting, D-1740, permit-copy, and county contact paths.</p>
                            <a class="button button--secondary"
                               href="/dhec-septic-permit-lookup/"
                               data-track-click="nav"
                               data-track-source-context="records_escalation_dhec"
                               data-track-target-type="internal_page">Open DHEC/SCDES guide</a>
                        </article>

                        <article class="records-escalation-card records-escalation-card--queries">
                            <span>County routes getting signals</span>
                            <h3>Push the county paths already showing demand</h3>
                            <div>
                                <a href="/septic-records-checklist/tennessee/blount-county/"
                                   data-track-click="nav"
                                   data-track-source-context="records_escalation_county_query"
                                   data-track-target-type="county_records_page">Blount County septic records</a>
                                <a href="/septic-records-checklist/north-carolina/buncombe-county/"
                                   data-track-click="nav"
                                   data-track-source-context="records_escalation_county_query"
                                   data-track-target-type="county_records_page">Buncombe County septic permit lookup</a>
                                <a href="/septic-records-checklist/north-carolina/alamance-county/"
                                   data-track-click="nav"
                                   data-track-source-context="records_escalation_county_query"
                                   data-track-target-type="county_records_page">Alamance County septic records</a>
                                <a href="/septic-records-checklist/texas/tarrant-county/"
                                   data-track-click="nav"
                                   data-track-source-context="records_escalation_county_query"
                                   data-track-target-type="county_records_page">Tarrant County septic records</a>
                                <a href="/septic-records-checklist/texas/comal-county/"
                                   data-track-click="nav"
                                   data-track-source-context="records_escalation_county_query"
                                   data-track-target-type="county_records_page">Comal County septic permit search</a>
                            </div>
                        </article>
                    </div>
                </div>
            </div>
        </section>
    @endif

    @if ("septic-records-by-county".equals(contentPage.slug()) && !countyFinderLinks.isEmpty())
        <section class="section section--tight">
            <div class="container">
                <div class="county-index-brief" aria-label="Septic records availability index">
                    <div class="county-index-brief__copy">
                        <span class="eyebrow">Septic records availability index</span>
                        <h2>Start with the county routes that already show the office, first file, and request method.</h2>
                        <p>This hub is built to answer the searches that usually get lost in county websites: septic permit lookup, permit copy request, as-built records, inspection letters, parcel or TMS search, and buyer file checks.</p>
                        <a href="/septic-records-access-index/" data-track-click="nav" data-track-source-context="county_records_index_access_index" data-track-target-type="internal_page">Open the TN, IN, NC, and SC access index</a>
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

    @if ("septic-records-by-county".equals(contentPage.slug()) && !directOnlineCountyFinderLinks.isEmpty())
        <section class="section section--tight" id="direct-online-septic-searches">
            <div class="container">
                <div class="direct-search-lane" aria-label="Direct official septic record searches">
                    <div class="direct-search-lane__intro">
                        <span class="eyebrow">Direct official searches</span>
                        <h2>Search a public permit database now, then validate the actual septic file.</h2>
                        <p>These routes have a live public search rather than only a contact page or request form. A property result is a starting point: match the address, parcel, permit status, and completed file before relying on it for a sale, repair, or quote.</p>
                    </div>
                    <div class="direct-search-lane__grid">
                        @for (var link : directOnlineCountyFinderLinks)
                            <article class="direct-search-route">
                                <span>${link.stateCode()} / public search</span>
                                <h3>${link.countyName()}</h3>
                                <p>${link.note()}</p>
                                <div class="direct-search-route__actions">
                                    <a class="button button--primary"
                                       href="${link.recordsUrl()}"
                                       target="_blank"
                                       rel="noreferrer"
                                       data-track-click="nav"
                                       data-track-source-context="county_direct_official_search"
                                       data-track-target-type="official_source">Open official search</a>
                                    <a class="text-link"
                                       href="${link.path()}"
                                       data-track-click="nav"
                                       data-track-source-context="county_direct_search_guide"
                                       data-track-target-type="county_records_page">Use county guide</a>
                                </div>
                            </article>
                        @endfor
                    </div>
                </div>
            </div>
        </section>
    @endif

    @if ("official-septic-lookup-tools".equals(contentPage.slug()) || "tdec-septic-records".equals(contentPage.slug()) || "north-carolina-septic-permit-lookup".equals(contentPage.slug()) || "texas-ossf-records-search".equals(contentPage.slug()) || "florida-ostds-permit-lookup".equals(contentPage.slug()) || "dhec-septic-permit-lookup".equals(contentPage.slug()))
        <section class="section section--tight" id="official-lookup-command-board">
            <div class="container">
                <div class="official-tool-console" aria-label="Official septic lookup tools command board">
                    <div class="official-tool-console__intro">
                        <span class="eyebrow">Official lookup command board</span>
                        <h2>
                            @if ("tdec-septic-records".equals(contentPage.slug()))
                                TDEC records searches need the state search, then a county fallback.
                            @elseif ("north-carolina-septic-permit-lookup".equals(contentPage.slug()))
                                North Carolina searches usually resolve at county environmental health.
                            @elseif ("texas-ossf-records-search".equals(contentPage.slug()))
                                Texas OSSF searches need the state context and the local authorized agent.
                            @elseif ("florida-ostds-permit-lookup".equals(contentPage.slug()))
                                Florida OSTDS searches start with the county health record path.
                            @elseif ("dhec-septic-permit-lookup".equals(contentPage.slug()))
                                DHEC searches should land on the current SCDES septic route.
                            @else
                                Start with the official source, then use the county request path.
                            @endif
                        </h2>
                        <p>These routes are built for searchers who need a permit copy, as-built, final approval, repair file, inspection letter, or written no-record response before they trust a quote or property story.</p>
                    </div>

                    <div class="official-tool-console__grid">
                        <article class="official-tool-route">
                            <span>TN / TDEC</span>
                            <h3>TDEC septic records search</h3>
                            <p>Use the TDEC septic permit search first. If the parcel is not visible, fall back to the Tennessee county route or a records request.</p>
                            <div class="official-tool-route__actions">
                                <a class="button button--primary"
                                   href="https://tdec.tn.gov/document-viewer/search/stp"
                                   target="_blank"
                                   rel="noreferrer">Open TDEC search</a>
                                <a class="button button--secondary"
                                   href="/tdec-septic-records/"
                                   data-track-click="nav"
                                   data-track-source-context="official_tool_console"
                                   data-track-target-type="internal_page">Use TDEC guide</a>
                            </div>
                            <p class="official-tool-route__fallback">If the official tab returns 403 or blocks your region, use the guide and request builder so the search does not restart on Google.</p>
                        </article>

                        <article class="official-tool-route">
                            <span>NC / county EH</span>
                            <h3>North Carolina septic permit lookup</h3>
                            <p>Use county environmental health, permit portals, and records request paths; statewide public records pages are usually a backstop, not the final file owner.</p>
                            <div class="official-tool-route__actions">
                                <a class="button button--primary"
                                   href="https://www.deq.nc.gov/about/divisions/water-resources/water-resources-public-information/public-records"
                                   target="_blank"
                                   rel="noreferrer">Open NC records</a>
                                <a class="button button--secondary"
                                   href="/north-carolina-septic-permit-lookup/"
                                   data-track-click="nav"
                                   data-track-source-context="official_tool_console"
                                   data-track-target-type="internal_page">Use NC guide</a>
                                <a class="button button--secondary"
                                   href="/septic-records-checklist/north-carolina/"
                                   data-track-click="nav"
                                   data-track-source-context="official_tool_console"
                                   data-track-target-type="state_money_page">NC records page</a>
                            </div>
                        </article>

                        <article class="official-tool-route">
                            <span>TX / OSSF</span>
                            <h3>Texas OSSF records search</h3>
                            <p>Use TCEQ OSSF context to identify the program, then resolve the permit file through the county, authorized agent, or local permitting authority.</p>
                            <div class="official-tool-route__actions">
                                <a class="button button--primary"
                                   href="https://www.tceq.texas.gov/permitting/ossf"
                                   target="_blank"
                                   rel="noreferrer">Open TCEQ OSSF</a>
                                <a class="button button--secondary"
                                   href="/texas-ossf-records-search/"
                                   data-track-click="nav"
                                   data-track-source-context="official_tool_console"
                                   data-track-target-type="internal_page">Use Texas guide</a>
                            </div>
                        </article>

                        <article class="official-tool-route">
                            <span>SC / DHEC -> SCDES</span>
                            <h3>DHEC septic permit lookup</h3>
                            <p>Many South Carolina searches still say DHEC. Use current SCDES septic tank, ePermitting, D-1740, permit-copy, and county contact paths.</p>
                            <div class="official-tool-route__actions">
                                <a class="button button--primary"
                                   href="https://des.sc.gov/permits-regulations/septic-tanks"
                                   target="_blank"
                                   rel="noreferrer">Open SCDES septic</a>
                                <a class="button button--secondary"
                                   href="/dhec-septic-permit-lookup/"
                                   data-track-click="nav"
                                   data-track-source-context="official_tool_console"
                                   data-track-target-type="internal_page">Use DHEC guide</a>
                            </div>
                            <p class="official-tool-route__fallback">If SCDES blocks the tab, use the DHEC/SCDES guide to carry county, parcel/TMS, address, owner, and D-1740 wording into the next request.</p>
                        </article>

                        <article class="official-tool-route">
                            <span>FL / OSTDS</span>
                            <h3>Florida OSTDS permit lookup</h3>
                            <p>Use the county health department, local archive, or eBridge-style records path when the search is about a septic permit, final approval, or old OSTDS file.</p>
                            <div class="official-tool-route__actions">
                                <a class="button button--primary"
                                   href="https://floridadep.gov/water/onsite-sewage"
                                   target="_blank"
                                   rel="noreferrer">Open Florida OSTDS</a>
                                <a class="button button--secondary"
                                   href="/florida-ostds-permit-lookup/"
                                   data-track-click="nav"
                                   data-track-source-context="official_tool_console"
                                   data-track-target-type="internal_page">Use Florida guide</a>
                            </div>
                        </article>
                    </div>

                    <div class="official-tool-console__handoff">
                        <strong>When the search tool does not find the file</strong>
                        <p>Retry with parcel ID, owner name, street name, subdivision, permit number, or legal description. If the record still does not appear, send a precise request instead of assuming the septic file does not exist.</p>
                        <a class="button button--secondary"
                           href="/septic-records-request-builder/"
                           data-track-click="nav"
                           data-track-source-context="official_tool_console_builder"
                           data-track-target-type="internal_page">Build records request</a>
                    </div>
                </div>
            </div>
        </section>
    @endif

    @if ("septic-records-request-builder".equals(contentPage.slug()))
        <section class="section section--tight" id="records-request-builder">
            <div class="container">
                <div class="records-request-builder" data-records-request-builder>
                    <div class="records-request-builder__intro">
                        <span class="eyebrow">Records request builder</span>
                        <h2>Generate the exact septic records request before you call the office.</h2>
                        <p>Pick the state route, record type, and reason. The script updates into a copy-ready request for permit copies, as-builts, final approvals, repair records, inspection letters, or no-record responses.</p>
                    </div>

                    <ul class="records-request-builder__packet-strip" data-records-request-quality aria-label="Download packet contents">
                        <li>
                            <span>Send</span>
                            <strong>Clean office message</strong>
                            <p>A ready-to-send subject line and request body, without burying the office in generic septic wording.</p>
                        </li>
                        <li>
                            <span>File context</span>
                            <strong>Identifiers and search keys</strong>
                            <p>Address, county, parcel, owner, legal-description, subdivision, and permit-number prompts for retrying a weak lookup.</p>
                        </li>
                        <li>
                            <span>Fallback proof</span>
                            <strong>No-record response wording</strong>
                            <p>Language that asks for a written no-record response and the next archive or delegated-office route.</p>
                        </li>
                        <li>
                            <span>Print</span>
                            <strong>PDF-ready cover packet</strong>
                            <p>Open a clean printable version for saving as PDF or attaching to a transaction, lender, or contractor file.</p>
                        </li>
                    </ul>

                    <div class="records-request-builder__workspace">
                        <div class="records-request-builder__controls">
                            <label>
                                <span>State route</span>
                                <select data-request-state>
                                    <option value="DEFAULT">County health office</option>
                                    <option value="TN">Tennessee / TDEC</option>
                                    <option value="NC">North Carolina / county EH</option>
                                    <option value="TX">Texas / OSSF agent</option>
                                    <option value="SC">South Carolina / SCDES</option>
                                    <option value="FL">Florida / OSTDS health</option>
                                </select>
                            </label>
                            <label>
                                <span>Record needed</span>
                                <select data-request-record>
                                    <option value="permit_copy">Permit copy and permit history</option>
                                    <option value="as_built">As-built, site sketch, or layout</option>
                                    <option value="final_approval">Final approval or operation permit</option>
                                    <option value="repair_record">Repair, malfunction, or complaint file</option>
                                    <option value="inspection_letter">Inspection or lender letter</option>
                                    <option value="no_record">Written no-record response</option>
                                </select>
                            </label>
                            <label>
                                <span>Reason</span>
                                <select data-request-reason>
                                    <option value="buying">Buying or selling a house</option>
                                    <option value="repair">Repair or suspected issue</option>
                                    <option value="addition">Addition, pool, driveway, or grading</option>
                                    <option value="replacement">Replacement or drain-field planning</option>
                                    <option value="lender">Lender or closing file</option>
                                    <option value="owner_records">Owner records</option>
                                </select>
                            </label>
                            <label>
                                <span>County</span>
                                <input type="text" data-request-county placeholder="Wake, Blount, Tarrant">
                            </label>
                            <label>
                                <span>Property address</span>
                                <input type="text" data-request-address placeholder="Street address">
                            </label>
                            <label>
                                <span>Parcel / APN / tax ID</span>
                                <input type="text" data-request-parcel placeholder="Parcel ID if known">
                            </label>
                            <label>
                                <span>Owner name</span>
                                <input type="text" data-request-owner placeholder="Current or prior owner">
                            </label>
                        </div>

                        <div class="records-request-builder__output">
                            <div class="records-request-builder__output-head">
                                <div>
                                    <span>Office-ready message</span>
                                    <strong data-records-request-filename>septic-records-request.txt</strong>
                                </div>
                                <em data-records-request-status aria-live="polite">Ready to copy or download</em>
                            </div>
                            <p data-records-request-preview></p>
                            <div class="records-request-builder__submission" data-records-request-submission>
                                <div>
                                    <span>Submission route</span>
                                    <strong data-records-request-route-title>County health office</strong>
                                    <p data-records-request-route-body>Send through the official county health, environmental health, onsite wastewater, permitting, or public-records channel.</p>
                                </div>
                                <ul data-records-request-checklist aria-label="Before sending checklist"></ul>
                            </div>
                            <textarea data-records-request-output readonly rows="14"></textarea>
                            <p class="records-request-builder__privacy"><strong>Copy</strong> sends only the office-ready message. <strong>Download</strong> saves a fuller plain-text packet. <strong>Print</strong> opens a PDF-ready version you can save from the browser. Nothing is uploaded or stored by SepticPath.</p>
                            <div class="records-request-builder__actions">
                                <button class="button button--primary"
                                        type="button"
                                        data-records-request-copy>Copy request</button>
                                <button class="button button--secondary"
                                        type="button"
                                        data-records-request-download>Download packet .txt</button>
                                <button class="button button--secondary"
                                        type="button"
                                        data-records-request-print>Print / save PDF</button>
                                <a class="button button--secondary"
                                   href="/septic-records-by-county/"
                                   data-track-click="nav"
                                   data-track-source-context="records_request_builder_county_index"
                                   data-track-target-type="internal_page">Find county route</a>
                            </div>
                        </div>
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
                    <p>Use these county routes when you already have a county name, parcel detail, or seller claim and need the actual file path before reading another broad state guide.</p>
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
                        <summary>Browse all state guides</summary>
                        <p><a href="/states/" data-track-click="nav" data-track-source-context="content_page_state_guides_directory" data-track-target-type="state_directory">Open the full state guide directory</a></p>
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
                            <summary>Browse state-specific workflow pages</summary>
                            <p><a href="/states/" data-track-click="nav" data-track-source-context="content_page_state_specific_directory" data-track-target-type="state_directory">Open the full state guide directory</a></p>
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
