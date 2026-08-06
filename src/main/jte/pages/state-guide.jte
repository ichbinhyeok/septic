@import com.example.septic.data.model.SourceRecord
@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.PageMeta
@import com.example.septic.web.EditorialProfile
@import com.example.septic.web.CountyWorkflowFieldView
@param PageMeta page
@param StateProfile state
@param java.util.List<SourceRecord> sources
@param java.util.List<SourceRecord> localAuthoritySources = java.util.List.of()
@param java.util.List<SourceRecord> recordsLookupSources = java.util.List.of()
@param SourceRecord primaryLocalAuthoritySource = null
@param SourceRecord primaryRecordsLookupSource = null
@param java.util.List<com.example.septic.data.model.StateMoneyPage> stateMoneyPages = java.util.List.of()
@param java.util.List<com.example.septic.data.model.StateMoneyPage> featuredStateMoneyPages = java.util.List.of()
@param java.util.List<com.example.septic.data.model.StateMoneyPage> plannedStateMoneyPages = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> featuredStateWorkflowLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.StateRuleFactView> stateRuleFacts = java.util.List.of()
@param java.util.List<com.example.septic.data.model.FaqBlock> guideFaqs = java.util.List.of()
@param java.util.List<com.example.septic.web.CoreStateComparisonRow> coreStateComparisonRows = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> countyRecordLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> featuredCountyRecordLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.PageLink> guideGrowthSearchLinks = java.util.List.of()
@param String stateRecordsLookupPath = null
@param String stateRecordsLookupTitle = ""
@param com.example.septic.web.StateCountyWorkflowSynthesisView guideCountyWorkflowSynthesis = null
@param java.util.List<CountyWorkflowFieldView> guideOfficialFilePathRows = java.util.List.of()
@param java.util.List<CountyWorkflowFieldView> guideGrowthAnswerRows = java.util.List.of()
@param String guideHeading = "State septic cost guide"
@param String stateQuickAnswer = ""
@param String calculatorCtaLabel = "Open the main calculator"
@param String calculatorCtaNote = "Use your state and project assumptions first, then verify locally."
@param com.example.septic.web.StatePlanningSnapshot planningSnapshot = null
@param EditorialProfile editorialPreparedBy = null
@param EditorialProfile editorialReviewedBy = null
@param String editorialReviewedAgainst = ""
@param String editorialLastReviewedAt = ""
@param String editorialNote = ""
@param String alabamaPercPlanningRange = "Under review"
@param java.util.List<com.example.septic.web.AlabamaCountyCostRouteView> alabamaCountyCostRoutes = java.util.List.of()

@template.layouts.app(page = page, content = @`
    <div class="state-guide-page state-guide-page--${state.stateCode().toLowerCase()}">
    <section class="section section--heroish">
        <div class="container state-hero @if ("AL".equals(state.stateCode())) state-hero--alabama @endif">
            <div>
                <span class="eyebrow">${state.stateCode()} county records and permit guide</span>
                <h1>${guideHeading}</h1>
                @if (stateQuickAnswer != null && !stateQuickAnswer.isBlank())
                    <div class="content-quick-answer" aria-label="Quick answer">
                        <span class="content-quick-answer__label">Quick answer</span>
                        <p>${stateQuickAnswer}</p>
                        @if ("AL".equals(state.stateCode()))
                            <p class="content-quick-answer__source">
                                Official fee source: <a href="https://www.alabamapublichealth.gov/programintegrity/assets/FeeSystemManual.pdf" target="_blank" rel="noreferrer">Alabama Department of Public Health Fee System Manual</a>
                                · reviewed 2026-07-30
                            </p>
                        @elseif (primaryRecordsLookupSource != null)
                            <p class="content-quick-answer__source">
                                Official source: <a href="${primaryRecordsLookupSource.url()}" target="_blank" rel="noreferrer">${primaryRecordsLookupSource.agencyName()}</a>
                                · content reviewed ${primaryRecordsLookupSource.contentVerifiedAt()}
                            </p>
                        @elseif (primaryLocalAuthoritySource != null)
                            <p class="content-quick-answer__source">
                                Official source: <a href="${primaryLocalAuthoritySource.url()}" target="_blank" rel="noreferrer">${primaryLocalAuthoritySource.agencyName()}</a>
                                · content reviewed ${primaryLocalAuthoritySource.contentVerifiedAt()}
                            </p>
                        @elseif (!sources.isEmpty())
                            <p class="content-quick-answer__source">
                                Official source: <a href="${sources.get(0).url()}" target="_blank" rel="noreferrer">${sources.get(0).agencyName()}</a>
                                · content reviewed ${sources.get(0).contentVerifiedAt()}
                            </p>
                        @endif
                    </div>
                @endif
                @if ("AL".equals(state.stateCode()))
                    <section class="alabama-perc-scope" id="alabama-perc-cost" data-alabama-perc-scope aria-label="Alabama perc test quote scope checker">
                        <div class="alabama-perc-scope__intro">
                            <span class="eyebrow">Alabama cost baseline</span>
                            <h2>How much does a perc test cost in Alabama? Plan on ${alabamaPercPlanningRange}, then separate the official fees.</h2>
                            <p class="alabama-perc-scope__answer">The planning range covers the wider private site-work conversation. It is not the same thing as the county fee. ADPH separately publishes a $150-$250 public site-evaluation fee in participating counties and a $100-$200 dwelling permit-application fee.</p>
                            <p>Use the three answers below to identify which cost layer applies, what the quote must include, and whether pricing should pause until the county file or sewer question is resolved.</p>
                            <div class="alabama-perc-scope__primary-actions">
                                <a class="button button--primary" href="/septic-system-cost-calculator/?state=AL&projectType=perc_test" data-track-click="nav" data-track-source-context="alabama_perc_primary_estimate" data-track-target-type="calculator">Run the Alabama cost estimate</a>
                                <span>The scope checker below prepares the county and professional questions; it does not calculate a price.</span>
                            </div>
                        </div>
                        <div class="alabama-fee-ledger" aria-label="Verified Alabama septic cost layers">
                            <article>
                                <span>Public site evaluation</span>
                                <strong>$150-$250</strong>
                                <p>Only for counties with an approved ADPH Site Evaluation Program and conventional small-flow systems.</p>
                            </article>
                            <article>
                                <span>Dwelling permit application</span>
                                <strong>$100-$200</strong>
                                <p>ADPH's flat application band for conventional or engineered small-flow dwelling systems. Non-refundable.</p>
                            </article>
                            <article>
                                <span>Private perc / soil planning</span>
                                <strong>${alabamaPercPlanningRange}</strong>
                                <p>A state-adjusted planning band, not an official fee or live professional quote.</p>
                            </article>
                            <div class="alabama-fee-ledger__footer">
                                <p><strong>Do not add all three automatically.</strong> The county may not provide the public site evaluation, and private professional work can replace or exceed that layer.</p>
                                <div>
                                    <a href="https://www.alabamapublichealth.gov/programintegrity/assets/FeeSystemManual.pdf" target="_blank" rel="noreferrer" data-track-click="nav" data-track-source-context="alabama_perc_fee_manual" data-track-target-type="official_source">Verify ADPH fee manual</a>
                                    <a href="https://www.alabamapublichealth.gov/onsite/forms.html" target="_blank" rel="noreferrer" data-track-click="nav" data-track-source-context="alabama_perc_cep_forms" data-track-target-type="official_source">Open official CEP forms</a>
                                </div>
                            </div>
                        </div>
                        <form class="alabama-perc-scope__form" data-alabama-perc-scope-form>
                            <label>
                                <span>What are you pricing?</span>
                                <select data-alabama-perc-project>
                                    <option value="new">New home, mobile home, or modular home</option>
                                    <option value="replacement">Replacement or repair of an existing septic system</option>
                                    <option value="purchase">Land or a home purchase decision</option>
                                </select>
                            </label>
                            <label>
                                <span>What site evidence do you already have?</span>
                                <select data-alabama-perc-evidence>
                                    <option value="none">No soil test, perc test, or plot plan</option>
                                    <option value="report">A prior soil or perc report and plot plan</option>
                                    <option value="file">An existing Permit to Install or Approval for Use</option>
                                    <option value="unknown">I do not know what the county file contains</option>
                                </select>
                            </label>
                            <label>
                                <span>Is public sewer available?</span>
                                <select data-alabama-perc-sewer>
                                    <option value="no">No or not expected</option>
                                    <option value="unknown">I need to confirm</option>
                                    <option value="yes">Yes or likely available</option>
                                </select>
                            </label>
                            <label>
                                <span>Alabama county</span>
                                <select data-alabama-perc-county autocomplete="address-level2">
                                    <option value="">Choose a county if known</option>
                                    @for (var countyRoute : alabamaCountyCostRoutes)
                                        <option value="${countyRoute.countyName()}"
                                                data-county-status="${countyRoute.routeStatus()}"
                                                data-county-phone="${countyRoute.phone()}"
                                                data-county-page="${countyRoute.officialPageUrl()}"
                                                data-county-page-label="${countyRoute.officialPageLabel()}"
                                                data-county-form="${countyRoute.formUrl()}"
                                                data-county-form-label="${countyRoute.formLabel()}"
                                                data-county-question="${countyRoute.callQuestion()}">${countyRoute.countyName()}</option>
                                    @endfor
                                    <option value="Other Alabama county">Another Alabama county</option>
                                </select>
                            </label>
                            <label>
                                <span>Property address or parcel ID</span>
                                <input type="text" data-alabama-perc-property autocomplete="street-address" placeholder="Optional, but useful for the request">
                            </label>
                            <button class="button button--secondary" type="submit">Build my county-ready scope</button>
                        </form>

                        <div class="alabama-perc-scope__result" data-alabama-perc-result hidden aria-live="polite">
                            <div>
                                <span class="trust-box__label" data-alabama-perc-label>Right next scope</span>
                                <h3 data-alabama-perc-heading></h3>
                                <p data-alabama-perc-body></p>
                            </div>
                            <ol class="alabama-perc-scope__steps" data-alabama-perc-steps></ol>
                            <div class="alabama-perc-scope__county-handoff" data-alabama-perc-selected-county hidden>
                                <span>Selected county handoff</span>
                                <strong data-alabama-perc-selected-county-heading></strong>
                                <p data-alabama-perc-selected-county-question></p>
                                <div>
                                    <a data-alabama-perc-selected-county-page target="_blank" rel="noreferrer" data-track-click="nav" data-track-source-context="alabama_perc_selected_county_page" data-track-target-type="official_source"></a>
                                    <a data-alabama-perc-selected-county-form target="_blank" rel="noreferrer" data-track-click="nav" data-track-source-context="alabama_perc_selected_county_form" data-track-target-type="official_source"></a>
                                </div>
                            </div>
                            <div class="alabama-perc-scope__note">
                                <span>Copy-ready request</span>
                                <textarea data-alabama-perc-note readonly aria-label="Alabama professional quote request"></textarea>
                                <div class="alabama-perc-scope__actions">
                                    <button class="button button--secondary" type="button" data-alabama-perc-copy>Copy request</button>
                                    <a class="button button--secondary"
                                       href="/septic-system-cost-calculator/?state=AL&projectType=perc_test"
                                       data-track-click="nav"
                                       data-track-source-context="alabama_perc_scope_result_estimate"
                                       data-track-target-type="calculator">Run planning range</a>
                                    <a class="button button--secondary"
                                       href="/septic-records-checklist/alabama/"
                                       data-track-click="nav"
                                       data-track-source-context="alabama_perc_scope_result_records"
                                       data-track-target-type="state_money_page">Check county file</a>
                                </div>
                            </div>
                        </div>
                        @if (!alabamaCountyCostRoutes.isEmpty())
                            <section class="alabama-county-handoffs" aria-labelledby="alabama-county-handoffs-title">
                                <div class="alabama-county-handoffs__header">
                                    <span class="eyebrow">Verified county handoffs</span>
                                    <h2 id="alabama-county-handoffs-title">Take the packet to the right county door.</h2>
                                    <p>These counties surfaced in the reviewed Search Console query sample. A county appears here only when an official office path was checked; unpublished fees and program participation stay marked as unknown.</p>
                                </div>
                                <div class="alabama-county-handoffs__list">
                                    @for (var countyRoute : alabamaCountyCostRoutes)
                                        <details class="alabama-county-handoff">
                                            <summary>
                                                <span>
                                                    <strong>${countyRoute.countyName()}</strong>
                                                    <small>${countyRoute.routeStatus()}</small>
                                                </span>
                                                <span class="alabama-county-handoff__phone">${countyRoute.phone()}</span>
                                            </summary>
                                            <div class="alabama-county-handoff__body">
                                                <p class="alabama-county-handoff__signal">${countyRoute.demandSignal()}</p>
                                                <div class="alabama-county-handoff__grid">
                                                    <div>
                                                        <span>Prepare before contact</span>
                                                        <p>${countyRoute.prepSummary()}</p>
                                                    </div>
                                                    <div>
                                                        <span>What is verified</span>
                                                        <p>${countyRoute.publicEvaluationStatus()}</p>
                                                    </div>
                                                </div>
                                                <div class="alabama-county-handoff__script">
                                                    <span>Ask this exact question</span>
                                                    <p>“${countyRoute.callQuestion()}”</p>
                                                </div>
                                                <div class="alabama-county-handoff__actions">
                                                    <a href="${countyRoute.officialPageUrl()}" target="_blank" rel="noreferrer" data-track-click="nav" data-track-source-context="alabama_perc_county_page" data-track-target-type="official_source">${countyRoute.officialPageLabel()}</a>
                                                    <a href="${countyRoute.formUrl()}" target="_blank" rel="noreferrer" data-track-click="nav" data-track-source-context="alabama_perc_county_form" data-track-target-type="official_source">${countyRoute.formLabel()}</a>
                                                    @if (countyRoute.hasCountyRecordsPath())
                                                        <a href="${countyRoute.countyRecordsPath()}">Prepare the county records request</a>
                                                    @endif
                                                </div>
                                            </div>
                                        </details>
                                    @endfor
                                </div>
                                <p class="alabama-county-handoffs__note"><strong>Not listed?</strong> Use the same preparation packet, then verify the local Environmental Office through ADPH. Absence from this list does not mean the county lacks septic service.</p>
                            </section>
                        @endif
                        <div class="alabama-cost-routes" aria-label="Alabama cost calculator routes">
                            <a href="/septic-system-cost-calculator/?state=AL&projectType=new_install">
                                <span>New system</span>
                                <strong>Run Alabama install estimate</strong>
                            </a>
                            <a href="/septic-system-cost-calculator/?state=AL&projectType=replacement">
                                <span>Existing system</span>
                                <strong>Run replacement estimate</strong>
                            </a>
                            <a href="/septic-system-cost-calculator/?state=AL&projectType=drainfield_replacement">
                                <span>Field problem</span>
                                <strong>Price drain-field scope</strong>
                            </a>
                        </div>
                        <p class="alabama-perc-scope__source-note">This produces a budget lane and copy-ready scope, not an engineering conclusion, county invoice, or contractor bid. Verify sewer availability and the county health file before accepting a low quote. <a href="#sources">Read the official ADPH sources below</a>.</p>
                    </section>
                @endif
                @if (!featuredCountyRecordLinks.isEmpty() && !"AL".equals(state.stateCode()))
                    <div class="hero-county-launchpad hero-county-launchpad--before-panel" aria-label="${state.stateName()} county record shortcuts">
                        <span class="hero-county-launchpad__label">County file first</span>
                        @for (var link : featuredCountyRecordLinks.stream().limit(3).toList())
                            <a href="${link.path()}"
                               data-track-click="nav"
                               data-track-source-context="state_guide_hero_county_launchpad"
                               data-track-target-type="county_records_page">${link.compactTitle()}</a>
                        @endfor
                        @if (stateRecordsLookupPath != null)
                            <a href="${stateRecordsLookupPath}"
                               data-track-click="nav"
                               data-track-source-context="state_guide_hero_records_lookup"
                               data-track-target-type="state_money_page">Open records lookup</a>
                        @endif
                    </div>
                @endif
                <div class="money-answer-panel state-guide-calculator-bridge" aria-label="State calculator route">
                    <div class="money-answer-panel__header">
                        <span class="eyebrow">State calculator prep</span>
                        <h2>This URL prepares the estimate before opening the calculator.</h2>
                    </div>
                    <ol class="money-answer-steps">
                        <li>
                            <span class="money-answer-steps__number">1</span>
                            <div>
                                <strong>Confirm the local file or office first</strong>
                                <p>${state.whoToCallFirst()}</p>
                            </div>
                        </li>
                        <li>
                            <span class="money-answer-steps__number">2</span>
                            <div>
                                <strong>Use the state-specific workflow if the file is still thin</strong>
                                @if (!featuredStateWorkflowLinks.isEmpty())
                                    <p>${featuredStateWorkflowLinks.get(0).title()}</p>
                                @else
                    <p>Review the ${state.stateName()} source and permit sections below before treating the number as reliable.</p>
                                @endif
                            </div>
                        </li>
                        <li>
                            <span class="money-answer-steps__number">3</span>
                            <div>
                                <strong>Then run the calculator with ${state.stateCode()} preselected</strong>
                                <p>${calculatorCtaNote}</p>
                            </div>
                        </li>
                    </ol>
                    @if (stateRecordsLookupPath != null)
                        <div class="quick-link-strip" aria-label="${state.stateName()} records lookup shortcut">
                            <span class="quick-link-strip__label">Records lookup</span>
                            <a class="button button--secondary quick-link-strip__button"
                               href="${stateRecordsLookupPath}"
                               data-track-click="nav"
                               data-track-source-context="state_guide_calculator_bridge_records_lookup"
                               data-track-target-type="state_money_page">Open ${state.stateName()} records lookup</a>
                        </div>
                        <div class="quick-link-strip" aria-label="${state.stateName()} permit file proof path">
                            <span class="quick-link-strip__label">Record proof path</span>
                            <a class="button button--secondary quick-link-strip__button"
                               href="/septic-permit-search-by-address/"
                               data-track-click="nav"
                               data-track-source-context="state_guide_record_proof_address"
                               data-track-target-type="internal_page">Permit search by address</a>
                            <a class="button button--secondary quick-link-strip__button"
                               href="/septic-permit-records-request/"
                               data-track-click="nav"
                               data-track-source-context="state_guide_record_proof_request"
                               data-track-target-type="internal_page">Permit records request</a>
                            <a class="button button--secondary quick-link-strip__button"
                               href="/septic-as-built-records/"
                               data-track-click="nav"
                               data-track-source-context="state_guide_record_proof_as_built"
                               data-track-target-type="internal_page">As-built records</a>
                            <a class="button button--secondary quick-link-strip__button"
                               href="/septic-records-by-county/"
                               data-track-click="nav"
                               data-track-source-context="state_guide_record_proof_county"
                               data-track-target-type="internal_page">Records by county</a>
                        </div>
                    @endif
                </div>
                @if (!guideOfficialFilePathRows.isEmpty())
                    <div class="state-records-response" aria-label="${state.stateName()} official estimate file path">
                        <div class="state-records-response__head">
                            <div>
                                <span class="eyebrow">Official state estimate file path</span>
                                <h2>Check the file owner and first artifact before the estimate becomes the answer.</h2>
                        <p>This ${state.stateName()} guide is strongest when it routes the visitor into the local septic file, county office, or missing-record fallback before the calculator compresses the problem into one number.</p>
                            </div>
                            <div class="state-records-response__priority">
                                <span>Estimate gate</span>
                                <strong>Owner, artifact, request, fallback</strong>
                            </div>
                        </div>

                        <div class="state-records-response__body">
                            <div class="state-records-response__rows">
                                @for (var row : guideOfficialFilePathRows)
                                    <article>
                                        <span>${row.label()}</span>
                                        <p>${row.value()}</p>
                                    </article>
                                @endfor
                            </div>

                            <aside class="state-records-response__route" aria-label="${state.stateName()} estimate file path actions">
                                <span class="state-records-response__route-label">Action order</span>
                                <h3>Resolve the file path before a cheap number.</h3>
                                <div class="state-records-response__route-list">
                                    @if (primaryRecordsLookupSource != null)
                                        <a href="${primaryRecordsLookupSource.url()}"
                                           target="_blank"
                                           rel="noreferrer"
                                           data-track-click="nav"
                                           data-track-source-context="state_guide_official_file_path_source"
                                           data-track-target-type="official_source">
                                            <strong>Open ${state.stateName()} official records source</strong>
                                            <span>${primaryRecordsLookupSource.agencyName()} | ${primaryRecordsLookupSource.title()}</span>
                                        </a>
                                    @elseif (primaryLocalAuthoritySource != null)
                                        <a href="${primaryLocalAuthoritySource.url()}"
                                           target="_blank"
                                           rel="noreferrer"
                                           data-track-click="nav"
                                           data-track-source-context="state_guide_official_file_path_source"
                                           data-track-target-type="official_source">
                                            <strong>Open ${state.stateName()} official authority source</strong>
                                            <span>${primaryLocalAuthoritySource.agencyName()} | ${primaryLocalAuthoritySource.title()}</span>
                                        </a>
                                    @endif
                                    @if (stateRecordsLookupPath != null)
                                        <a href="${stateRecordsLookupPath}"
                                           data-track-click="nav"
                                           data-track-source-context="state_guide_official_file_path_records"
                                           data-track-target-type="state_money_page">
                                            <strong>Open ${state.stateName()} records lookup</strong>
                                            <span>${stateRecordsLookupTitle}</span>
                                        </a>
                                    @endif
                                    <a href="/septic-permit-search-by-address/"
                                       data-track-click="nav"
                                       data-track-source-context="state_guide_official_file_path_address"
                                       data-track-target-type="internal_page">
                                        <strong>Search by address</strong>
                                        <span>Use address, parcel, owner, APN/TMS, legal description, or prior permit clues.</span>
                                    </a>
                                    <a href="/septic-permit-records-request/"
                                       data-track-click="nav"
                                       data-track-source-context="state_guide_official_file_path_request"
                                       data-track-target-type="internal_page">
                                        <strong>Records request wording</strong>
                                        <span>Ask for the permit copy, as-built, final approval, repair trail, or no-record response.</span>
                                    </a>
                                </div>
                            </aside>
                        </div>
                    </div>
                @endif
                @if (!guideGrowthSearchLinks.isEmpty())
                    <div class="quick-link-strip" aria-label="${state.stateName()} growth search shortcuts">
                        <span class="quick-link-strip__label">${state.stateName()} search path</span>
                        @for (var link : guideGrowthSearchLinks)
                            <a class="button button--secondary quick-link-strip__button"
                               href="${link.path()}"
                               data-track-click="nav"
                               data-track-source-context="state_guide_growth_search_shortcut"
                               data-track-target-type="${link.path().contains("/septic-records-checklist/") && link.path().split("/").length > 3 ? "county_records_page" : link.path().startsWith("/septic-") && !link.path().contains("/" + state.slug() + "/") ? "internal_page" : "state_money_page"}">${link.title()}</a>
                        @endfor
                    </div>
                    <div class="detail-grid" aria-label="${state.stateName()} growth search answer">
                        @for (var row : guideGrowthAnswerRows)
                            <article class="mini-card">
                                <strong>${row.label()}</strong>
                                <p>${row.value()}</p>
                            </article>
                        @endfor
                    </div>
                @endif
                <p class="lede state-guide-summary-copy">${state.ruleSummaryPlainEnglish()}</p>
                @if (!featuredStateWorkflowLinks.isEmpty())
                    <p class="hero-router-intro">Pick the first move that matches the blocker. Use the narrower workflow or file path first, and estimate only after the local story is clear enough to price. @if (guideCountyWorkflowSynthesis != null) ${guideCountyWorkflowSynthesis.intro()} @endif</p>
                @else
                    <p class="hero-router-intro">${calculatorCtaNote}</p>
                @endif
                <div class="micro-tool micro-tool--hero" data-choice-group>
                    <div class="hero-decision-grid hero-decision-grid--three micro-tool__choices">
                        <button class="hero-decision-card hero-decision-card--accent micro-tool__choice is-active"
                                type="button"
                                data-choice="file"
                                aria-pressed="true">
                            <span class="hero-decision-card__label">File or office first</span>
                            <h3>I need the local file or county office before I trust the story</h3>
                            <p>Choose the ${state.stateName()} records route when the main blocker is the permit file, parcel record, county page, or the official office that owns the next step.</p>
                        </button>

                        <button class="hero-decision-card micro-tool__choice"
                                type="button"
                                data-choice="workflow"
                                aria-pressed="false">
                            <span class="hero-decision-card__label">Workflow first</span>
                            <h3>I need the narrow state workflow before I price anything</h3>
                            <p>Choose a narrower ${state.stateName()} workflow when the broad guide is not enough and the real question is records, permit path, buyer diligence, or one intent lane.</p>
                        </button>

                        <button class="hero-decision-card micro-tool__choice"
                                type="button"
                                data-choice="estimate"
                                aria-pressed="false">
                            <span class="hero-decision-card__label">Planning range</span>
                            <h3>I need a cautious number after the path is clearer</h3>
                            <p>Choose the ${state.stateName()} planning estimate when you understand the workflow well enough to avoid flattening the local story.</p>
                        </button>
                    </div>

                    <div class="micro-tool__panels">
                        <article class="micro-tool__panel is-active" data-choice-panel="file">
                            <span class="micro-tool__kicker">Recommended next best action</span>
                            @if (primaryRecordsLookupSource != null)
                                <h2>Pull the local septic file first</h2>
                                <p>Open the ${state.stateName()} records path before you trust a quote, because the permit copy, as-built sketch, inspection trail, or parcel file can change the downside faster than another broad guide.</p>
                                <div class="micro-tool__actions">
                                    <a class="button button--primary"
                                       href="${primaryRecordsLookupSource.url()}"
                                       target="_blank"
                                       rel="noreferrer"
                                       data-track-click="nav"
                                       data-track-source-context="state_guide_micro_tool_records"
                                       data-track-target-type="official_source">Open local records path</a>
                                    @if (!featuredCountyRecordLinks.isEmpty() && !"AL".equals(state.stateCode()))
                                        <a class="button button--secondary"
                                           href="${featuredCountyRecordLinks.get(0).path()}"
                                           data-track-click="nav"
                                           data-track-source-context="state_guide_micro_tool_county"
                                           data-track-target-type="county_records_page">Open county records page</a>
                                    @endif
                                </div>
                            @elseif (!featuredCountyRecordLinks.isEmpty() && !"AL".equals(state.stateCode()))
                                <h2>Open the county records page first</h2>
                                <p>The ${state.stateName()} guide has narrowed the problem. The next move is the county page, where the actual parcel file and office path become concrete enough to trust.</p>
                                <div class="micro-tool__actions">
                                    <a class="button button--primary"
                                       href="${featuredCountyRecordLinks.get(0).path()}"
                                       data-track-click="nav"
                                       data-track-source-context="state_guide_micro_tool_county"
                                       data-track-target-type="county_records_page">Open county records page</a>
                                    @if (primaryLocalAuthoritySource != null)
                                        <a class="button button--secondary"
                                           href="${primaryLocalAuthoritySource.url()}"
                                           target="_blank"
                                           rel="noreferrer"
                                           data-track-click="nav"
                                           data-track-source-context="state_guide_micro_tool_authority"
                                           data-track-target-type="official_source">Open county office path</a>
                                    @endif
                                </div>
                            @elseif (primaryLocalAuthoritySource != null)
                                <h2>Verify the right county office first</h2>
                                <p>When the ${state.stateName()} file path is still fuzzy, first confirm which local authority owns the next permit, review, or inspection step.</p>
                                <div class="micro-tool__actions">
                                    <a class="button button--primary"
                                       href="${primaryLocalAuthoritySource.url()}"
                                       target="_blank"
                                       rel="noreferrer"
                                       data-track-click="nav"
                                       data-track-source-context="state_guide_micro_tool_authority"
                                       data-track-target-type="official_source">Open county office path</a>
                                    <a class="button button--secondary" href="#sources">Jump to source block</a>
                                </div>
                            @else
                                <h2>Use the official source block first</h2>
                                <p>${state.stateName()} still needs a verified agency trail before the next move becomes specific. Start with the source list, then enter a narrower workflow after the rule path is clearer.</p>
                                <div class="micro-tool__actions">
                                    <a class="button button--primary" href="#sources">Jump to sources</a>
                                    @if (!featuredStateWorkflowLinks.isEmpty())
                                        <a class="button button--secondary"
                                           href="${featuredStateWorkflowLinks.get(0).path()}"
                                           data-track-click="nav"
                                           data-track-source-context="state_guide_micro_tool_workflow"
                                           data-track-target-type="state_money_page">${featuredStateWorkflowLinks.get(0).title()}</a>
                                    @endif
                                </div>
                            @endif
                        </article>

                        <article class="micro-tool__panel" data-choice-panel="workflow" hidden>
                            <span class="micro-tool__kicker">Recommended next best action</span>
                            @if (!featuredStateMoneyPages.isEmpty())
                                <h2>Open the narrow state workflow now</h2>
                                <p>${featuredStateMoneyPages.get(0).uniqueAngle()} Use the narrower workflow page once the broad state story is clear enough and the live blocker is no longer "what kind of state is this?" but "what do I do next?"</p>
                                <div class="micro-tool__actions">
                                    <a class="button button--primary"
                                       href="${featuredStateWorkflowLinks.get(0).path()}"
                                       data-track-click="nav"
                                       data-track-source-context="state_guide_micro_tool_workflow"
                                       data-track-target-type="state_money_page">${featuredStateWorkflowLinks.get(0).title()}</a>
                                    @if (featuredStateWorkflowLinks.size() > 1)
                                        <a class="button button--secondary"
                                           href="${featuredStateWorkflowLinks.get(1).path()}"
                                           data-track-click="nav"
                                           data-track-source-context="state_guide_micro_tool_workflow_alt"
                                           data-track-target-type="state_money_page">${featuredStateWorkflowLinks.get(1).title()}</a>
                                    @endif
                                </div>
                            @else
                                <h2>Stay in the guide just long enough to narrow the workflow</h2>
                                <p>No stronger ${state.stateName()} lane is published yet, so use the workflow overview, permit-risk section, and sources to choose records, local office verification, or a cautious estimate.</p>
                                <div class="micro-tool__actions">
                                    <a class="button button--primary" href="#workflow-overview">Review workflow overview</a>
                                    <a class="button button--secondary" href="#permit-risk">Review permit and buyer risk</a>
                                </div>
                            @endif
                        </article>

                        <article class="micro-tool__panel" data-choice-panel="estimate" hidden>
                            <span class="micro-tool__kicker">Recommended next best action</span>
                            <h2>Run the planning estimate after the local story is clear enough</h2>
                            <p>${calculatorCtaNote} The estimate is strongest after you confirm the file, county office, or narrow workflow that actually governs this property.</p>
                            <div class="micro-tool__actions">
                                <a class="button button--primary"
                                   href="/septic-system-cost-calculator/?state=${state.stateCode()}&sourcePageHint=/septic-system-cost-calculator/${state.slug()}/"
                                   data-track-click="nav"
                                   data-track-source-context="state_guide_micro_tool_calculator"
                                   data-track-target-type="calculator">${calculatorCtaLabel}</a>
                                @if (!featuredStateWorkflowLinks.isEmpty())
                                    <a class="button button--secondary"
                                       href="${featuredStateWorkflowLinks.get(0).path()}"
                                       data-track-click="nav"
                                       data-track-source-context="state_guide_micro_tool_calculator_workflow"
                                       data-track-target-type="state_money_page">${featuredStateWorkflowLinks.get(0).title()}</a>
                                @endif
                            </div>
                        </article>
                    </div>
                </div>
                @if (!featuredStateWorkflowLinks.isEmpty())
                    <div class="quick-link-strip" aria-label="Fast next steps">
                        <span class="quick-link-strip__label">Fast next steps</span>
                        @for (var workflowLink : featuredStateWorkflowLinks)
                            <a class="button button--secondary quick-link-strip__button"
                               href="${workflowLink.path()}"
                               data-track-click="nav"
                               data-track-source-context="state_guide_featured_link"
                               data-track-target-type="state_money_page">${workflowLink.title()}</a>
                        @endfor
                    </div>
                @endif
            </div>
            <div class="hero-sidecard">
                <span class="pill">Official-source guide</span>
                <strong>${state.agencyName()}</strong>
                <strong>${state.ruleTypeLabel()}</strong>
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
                        <dt>Latest review activity</dt>
                        <dd>${editorialLastReviewedAt}</dd>
                    </div>
                </dl>
                <p>${editorialNote}</p>
                @if (!featuredStateMoneyPages.isEmpty())
                    <a class="button button--secondary"
                       href="${featuredStateMoneyPages.get(0).path(state.slug())}"
                       data-track-click="nav"
                       data-track-source-context="state_guide_side_workflow"
                       data-track-target-type="state_money_page">Open the next workflow page</a>
                                <p>This ${state.stateName()} guide is the overview. The next move should usually be the narrower workflow page, not a quote form.</p>
                @else
                    <a class="button button--secondary" href="#sources">Jump to official sources</a>
                    <p>${calculatorCtaNote}</p>
                @endif
            </div>
        </div>
    </section>

    <div data-sticky-trigger="state-guide-after-hero" aria-hidden="true"></div>

    <section class="section section--tight">
        <div class="container">
            <div class="section-jump-strip" aria-label="Jump between sections">
                <span class="section-jump-strip__label">Jump between sections</span>
                <a href="#workflow-overview">Workflow</a>
                <a href="#records">Records</a>
                <a href="#permit-risk">Permit & buyer risk</a>
                <a href="#facts">Quick facts</a>
                @if (!countyRecordLinks.isEmpty())
                    <a href="#county-pages">County pages</a>
                @endif
                @if (!stateMoneyPages.isEmpty())
                    <a href="#intents">Intent pages</a>
                @endif
                <a href="#sources">Sources</a>
                <a href="#faq">FAQ</a>
            </div>

            <div class="intent-rail">
                <article class="intent-card intent-card--primary">
                    @if (!featuredStateMoneyPages.isEmpty())
                        <span class="intent-card__label">Open the most likely next workflow page</span>
                        <h2>${featuredStateMoneyPages.get(0).title()}</h2>
                        <p>${featuredStateMoneyPages.get(0).uniqueAngle()} @if (guideCountyWorkflowSynthesis != null && !guideCountyWorkflowSynthesis.holdQuoteChecks().isEmpty()) Do not price yet when ${guideCountyWorkflowSynthesis.holdQuoteChecks().get(0).toLowerCase()}. @endif</p>
                        @if (guideCountyWorkflowSynthesis != null && !guideCountyWorkflowSynthesis.firstArtifacts().isEmpty())
                            <p><strong>Pull first.</strong> ${guideCountyWorkflowSynthesis.firstArtifacts().get(0)}</p>
                        @endif
                        <a class="button button--primary"
                           href="${featuredStateMoneyPages.get(0).path(state.slug())}"
                           data-track-click="nav"
                           data-track-source-context="state_guide_action_rail_workflow"
                           data-track-target-type="state_money_page">Open next workflow page</a>
                    @else
                        <span class="intent-card__label">Run the state estimate</span>
                        <h2>${calculatorCtaLabel}</h2>
                        <p>${calculatorCtaNote}</p>
                        <a class="button button--primary" href="/septic-system-cost-calculator/?state=${state.stateCode()}" data-track-click="nav" data-track-source-context="state_guide_action_rail_calculator" data-track-target-type="calculator">${calculatorCtaLabel}</a>
                    @endif
                </article>

                <article class="intent-card">
                    @if (primaryRecordsLookupSource != null)
                        <span class="intent-card__label">Pull records first</span>
                        <h2>Open the local file path before you trust the low end</h2>
                        <p>Use the records lookup before you compare the cheapest quote against the real permit, as-built, or inspection story. @if (guideCountyWorkflowSynthesis != null && !guideCountyWorkflowSynthesis.firstArtifacts().isEmpty()) Start with ${guideCountyWorkflowSynthesis.firstArtifacts().get(0).toLowerCase()}. @endif</p>
                        <a class="button button--secondary" href="${primaryRecordsLookupSource.url()}" target="_blank" rel="noreferrer">Open records lookup</a>
                    @elseif (primaryLocalAuthoritySource != null)
                        <span class="intent-card__label">Verify the right office</span>
                        <h2>Confirm the local authority before you schedule work</h2>
                    <p>Use the official ${state.stateName()} local-authority path when the homeowner has not confirmed which office controls the next permit or review step.</p>
                        <a class="button button--secondary" href="${primaryLocalAuthoritySource.url()}" target="_blank" rel="noreferrer">Open local authority source</a>
                    @else
                        <span class="intent-card__label">Use the official source section</span>
                        <h2>Jump to the verified source list</h2>
                    <p>Use the ${state.stateName()} source block when you still need the official local workflow, file path, or policy context before moving deeper.</p>
                        <a class="button button--secondary" href="#sources">Jump to sources</a>
                    @endif
                </article>

                <article class="intent-card">
                    <span class="intent-card__label">Price it after the workflow is clearer</span>
                    <h2>${calculatorCtaLabel}</h2>
                    <p>${calculatorCtaNote}</p>
                    <a class="button button--secondary" href="/septic-system-cost-calculator/?state=${state.stateCode()}" data-track-click="nav" data-track-source-context="state_guide_action_rail_calculator_followup" data-track-target-type="calculator">Run the estimate</a>
                </article>
            </div>
        </div>
    </section>

    @if (planningSnapshot != null || primaryLocalAuthoritySource != null || primaryRecordsLookupSource != null)
        <section class="section section--tight">
            <div class="container detail-grid">
                @if (planningSnapshot != null)
                    <article class="detail-card detail-card--full">
                        <h2>Planning cost snapshot</h2>
                        <div class="result-grid">
                            <article class="metric-card">
                                <span class="metric-card__label">Install midpoint</span>
                                <strong>${planningSnapshot.installMidpoint()}</strong>
                            </article>
                            <article class="metric-card">
                                <span class="metric-card__label">Replacement midpoint</span>
                                <strong>${planningSnapshot.replacementMidpoint()}</strong>
                            </article>
                            <article class="metric-card">
                                <span class="metric-card__label">Perc planning range</span>
                                <strong>${planningSnapshot.percRange()}</strong>
                            </article>
                            <article class="metric-card">
                                <span class="metric-card__label">Pumping planning range</span>
                                <strong>${planningSnapshot.pumpingRange()}</strong>
                            </article>
                        </div>
                        <p>${planningSnapshot.comparisonNote()} These figures are still planning-only ranges, not an official fee schedule.</p>
                    </article>
                @endif

                @if (primaryLocalAuthoritySource != null)
                    <article class="detail-card">
                        <h2>Find the local permitting authority</h2>
                        <p>${state.stateName()} usually becomes more concrete once you confirm the actual local office handling septic permitting and review.</p>
                        <a class="button button--secondary" href="${primaryLocalAuthoritySource.url()}" target="_blank" rel="noreferrer">Open local authority source</a>
                        <p>${primaryLocalAuthoritySource.agencyName()} | ${primaryLocalAuthoritySource.title()}</p>
                    </article>
                @endif

                @if (primaryRecordsLookupSource != null)
                    <article class="detail-card">
                        <h2>Look up septic records first</h2>
                    <p>Before trusting the ${state.stateName()} low end, pull the existing permit, as-built, inspection, or management records tied to the property.</p>
                        <a class="button button--secondary" href="${primaryRecordsLookupSource.url()}" target="_blank" rel="noreferrer">Open records lookup</a>
                        <p>${primaryRecordsLookupSource.agencyName()} | ${primaryRecordsLookupSource.title()}</p>
                    </article>
                @endif
            </div>
        </section>
    @endif

    <section class="section section--tight" id="workflow-overview">
        <div class="container detail-grid">
            <article class="detail-card" id="records">
                <h2>County office and records path</h2>
                @if (state.whoToCallFirst() != null && !state.whoToCallFirst().isBlank())
                    <p><strong>Who to call first.</strong> ${state.whoToCallFirst()}</p>
                @endif
                @if (state.recordsToRequest() != null && !state.recordsToRequest().isEmpty())
                    <p><strong>Pull these ${state.stateName()} records before you trust the low end.</strong></p>
                    <ul class="detail-list">
                        @for (var record : state.recordsToRequest())
                            <li>${record}</li>
                        @endfor
                    </ul>
                @endif
                @if (primaryLocalAuthoritySource != null)
                    <p><a href="${primaryLocalAuthoritySource.url()}" target="_blank" rel="noreferrer">Open the local authority source</a></p>
                @endif
                @if (primaryRecordsLookupSource != null)
                    <p><a href="${primaryRecordsLookupSource.url()}" target="_blank" rel="noreferrer">Open the records lookup path</a></p>
                @endif
            </article>

            <article class="detail-card" id="permit-risk">
                <h2>Permit requirements and timing</h2>
                <p>${state.permitSummary()}</p>
                @if (state.permitTimelineNote() != null && !state.permitTimelineNote().isBlank())
                    <p>${state.permitTimelineNote()}</p>
                @endif
                @if (state.permitPathSteps() != null && !state.permitPathSteps().isEmpty())
                    <ol class="detail-list detail-list--numbered">
                        @for (var step : state.permitPathSteps())
                            <li>${step}</li>
                        @endfor
                    </ol>
                @endif
            </article>

            @if ((state.buyerInspectionTrigger() != null && !state.buyerInspectionTrigger().isBlank())
                    || (state.maintenanceInspectionNote() != null && !state.maintenanceInspectionNote().isBlank())
                    || (state.specialAreaNote() != null && !state.specialAreaNote().isBlank()))
                <article class="detail-card">
                    <h2>Transfer, buyer, and ownership risk</h2>
                    @if (state.buyerInspectionTrigger() != null && !state.buyerInspectionTrigger().isBlank())
                        <p>${state.buyerInspectionTrigger()}</p>
                    @endif
                    @if (state.maintenanceInspectionNote() != null && !state.maintenanceInspectionNote().isBlank())
                        <p>${state.maintenanceInspectionNote()}</p>
                    @endif
                    @if (state.specialAreaNote() != null && !state.specialAreaNote().isBlank())
                        <p><strong>State wrinkle.</strong> ${state.specialAreaNote()}</p>
                    @endif
                </article>
            @endif

            @if (state.localActionSteps() != null && !state.localActionSteps().isEmpty())
                <article class="detail-card detail-card--full" id="prep">
                    <h2>County-aware prep checklist</h2>
                    <ol class="detail-list detail-list--numbered">
                        @for (var step : state.localActionSteps())
                            <li>${step}</li>
                        @endfor
                    </ol>
                </article>
            @endif
        </div>
    </section>

    @if (!countyRecordLinks.isEmpty())
        <section class="section section--tight" id="county-pages">
            <div class="container">
                <div class="section-heading">
                    <span class="eyebrow">County Wedge</span>
                    <h2>County records pages now live in ${state.stateName()}</h2>
                    <p class="lede">Use these ${state.stateName()} routes when the state guide is too broad and the real question is which county file, search form, or local office controls the next step.</p>
                </div>

                @if ("AL".equals(state.stateCode()))
                    <div class="alabama-county-directory" data-alabama-county-directory>
                        <label for="alabama-county-directory-search">Search the county directory</label>
                        <input id="alabama-county-directory-search" type="search" autocomplete="off" placeholder="Type a county name" data-alabama-county-directory-search>
                        <p class="alabama-county-directory__status" aria-live="polite" data-alabama-county-directory-status>${(featuredCountyRecordLinks.isEmpty() ? countyRecordLinks : featuredCountyRecordLinks).size()} county routes</p>
                        <ul>
                            @for (var link : (featuredCountyRecordLinks.isEmpty() ? countyRecordLinks : featuredCountyRecordLinks))
                                <li data-alabama-county-directory-row data-county-name="${link.compactTitle().toLowerCase()}">
                                    <a href="${link.path()}" data-track-click="nav" data-track-source-context="alabama_county_directory" data-track-target-type="county_page">${link.compactTitle()}</a>
                                </li>
                            @endfor
                        </ul>
                        <p data-alabama-county-directory-empty hidden>No published Alabama county route matches that search.</p>
                    </div>
                @else
                    <div class="detail-grid">
                        @for (var link : (featuredCountyRecordLinks.isEmpty() ? countyRecordLinks : featuredCountyRecordLinks).stream().limit(6).toList())
                            <article class="detail-card">
                                <h3><a href="${link.path()}" data-track-click="nav" data-track-source-context="state_guide_county_page" data-track-target-type="county_page">${link.title()}</a></h3>
                                <a class="button button--secondary" href="${link.path()}" data-track-click="nav" data-track-source-context="state_guide_county_page_cta" data-track-target-type="county_page">Open county page</a>
                            </article>
                        @endfor
                    </div>
                    @if ((featuredCountyRecordLinks.isEmpty() ? countyRecordLinks : featuredCountyRecordLinks).size() > 6)
                        <details class="county-link-overflow">
                            <summary>Show all ${state.stateName()} county records pages</summary>
                            <div class="detail-grid">
                                @for (var link : (featuredCountyRecordLinks.isEmpty() ? countyRecordLinks : featuredCountyRecordLinks).stream().skip(6).toList())
                                    <article class="detail-card">
                                        <h3><a href="${link.path()}" data-track-click="nav" data-track-source-context="state_guide_county_page_overflow" data-track-target-type="county_page">${link.title()}</a></h3>
                                        <a class="button button--secondary" href="${link.path()}" data-track-click="nav" data-track-source-context="state_guide_county_page_cta_overflow" data-track-target-type="county_page">Open county page</a>
                                    </article>
                                @endfor
                            </div>
                        </details>
                    @endif
                @endif
            </div>
        </section>
    @endif

    <section class="section section--tight">
        <div class="container detail-grid">
            <details class="state-money-disclosure" id="facts">
                <summary>
                    <span class="eyebrow">Quick facts</span>
                    <strong>${state.stateName()} source snapshot</strong>
                    <span>Open this when you need rule style, local-link count, records-link count, and sizing anchors.</span>
                </summary>
                <div class="detail-grid">
                    <article class="detail-card detail-card--full">
                        <h2>Quick facts</h2>
                        <div class="facts-table-wrap">
                            <table class="facts-table">
                                <tbody>
                                <tr>
                                    <th>Rule style</th>
                                    <td>${state.ruleTypeLabel()}</td>
                                    <th>Override risk</th>
                                    <td>${state.countyOverrideRisk()}</td>
                                </tr>
                                <tr>
                                    <th>Last verified</th>
                                    <td>${state.lastVerifiedAt()}</td>
                                    <th>Official sources</th>
                                    <td>${state.officialSourceIds().size()}</td>
                                </tr>
                                <tr>
                                    <th>Local verification links</th>
                                    <td>${localAuthoritySources.size()}</td>
                                    <th>Records links</th>
                                    <td>${recordsLookupSources.size()}</td>
                                </tr>
                                <tr>
                                    <th>Public sizing signal</th>
                                    <td>
                                        @if (state.minTankSizeGallons() != null)
                                            ${state.minTankSizeGallons()} gallon minimum anchor
                                        @elseif (state.designFlowPerBedroomGpd() != null)
                                            ${state.designFlowPerBedroomGpd()} gallons per bedroom
                                        @else
                                            Conservative fallback range
                                        @endif
                                    </td>
                                    <th>Primary first call</th>
                                    <td>${state.whoToCallFirst()}</td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </article>
                </div>
            </details>

            @if (stateRuleFacts != null && !stateRuleFacts.isEmpty())
                <article class="detail-card detail-card--full">
                    <h2>Source-backed rule facts for ${state.stateName()}</h2>
                    <div class="detail-grid">
                        @for (var fact : stateRuleFacts)
                            <article class="detail-card">
                                <span class="trust-box__label">${fact.label()}</span>
                                <h3>${fact.renderedValue()}</h3>
                                <p>${fact.note()}</p>
                                <div class="source-meta">
                                    @if (fact.confidenceLabel() != null && !fact.confidenceLabel().isBlank())
                                        <span>${fact.confidenceLabel()}</span>
                                    @endif
                                    @if (fact.sourceTrustLevel() != null && !fact.sourceTrustLevel().isBlank())
                                        <span>Trust: ${fact.sourceTrustLevel()}</span>
                                    @endif
                                    @if (fact.sourceStatus() != null && !fact.sourceStatus().isBlank())
                                        <span>Status: ${fact.sourceStatus()}</span>
                                    @endif
                                    @if (fact.lastVerifiedAt() != null && !fact.lastVerifiedAt().isBlank())
                                        <span>Last verified: ${fact.lastVerifiedAt()}</span>
                                    @endif
                                    @if (fact.effectiveDate() != null && !fact.effectiveDate().isBlank())
                                        <span>Effective: ${fact.effectiveDate()}</span>
                                    @endif
                                </div>
                                <p><strong>${fact.sourceAgency()}</strong></p>
                                <p><a href="${fact.sourceUrl()}" target="_blank" rel="noreferrer">${fact.sourceTitle()}</a></p>
                                @if (fact.sourceSection() != null && !fact.sourceSection().isBlank())
                                    <p>Source section: ${fact.sourceSection()}</p>
                                @endif
                            </article>
                        @endfor
                    </div>
                </article>
            @endif

            <article class="detail-card">
                <h2>Why this state is unique</h2>
                <p>${state.pageAngle()}</p>
            </article>

            <article class="detail-card">
                <h2>Site evaluation summary</h2>
                <p>${state.siteEvalSummary()}</p>
            </article>

            @if (state.lowEndRiskChecks() != null && !state.lowEndRiskChecks().isEmpty())
                <article class="detail-card">
                    <h2>What breaks the low end</h2>
                    <ul class="detail-list">
                        @for (var check : state.lowEndRiskChecks())
                            <li>${check}</li>
                        @endfor
                    </ul>
                </article>
            @endif

            <article class="detail-card detail-card--full">
                <h2>Local override note</h2>
                <p>${state.localOverrideNote()} Override risk: ${state.countyOverrideRisk()}.</p>
            </article>
        </div>
    </section>

    @if (!featuredStateMoneyPages.isEmpty() || primaryLocalAuthoritySource != null || primaryRecordsLookupSource != null)
        <section class="section section--tight">
            <div class="container detail-grid">
                <article class="detail-card detail-card--full">
                    <h2>How to use this ${state.stateName()} guide before you click into one intent page</h2>
                    <p>
                        Use this guide for the broad statewide story first: rule style, office path, file trail, and what usually breaks the low end. Once you know which part of the workflow is actually blocking you, move into
                        @if (!featuredStateMoneyPages.isEmpty())
                            <a href="${featuredStateMoneyPages.get(0).path(state.slug())}"
                               data-track-click="nav"
                               data-track-source-context="state_guide_inline_intent_link"
                               data-track-target-type="state_money_page">${featuredStateMoneyPages.get(0).title()}</a>
                        @else
                            the most specific next page
                        @endif
                        instead of staying at the statewide level.
                    </p>
                    @if (featuredStateMoneyPages.size() > 1)
                        <p>
                            If your bottleneck is different, compare it with
                            <a href="${featuredStateMoneyPages.get(1).path(state.slug())}"
                               data-track-click="nav"
                               data-track-source-context="state_guide_inline_intent_link"
                               data-track-target-type="state_money_page">${featuredStateMoneyPages.get(1).title()}</a>.
                            The goal is to carry the right file, permit, or site-risk narrative into the estimate instead of relying on one statewide average.
                        </p>
                    @endif
                    @if (primaryRecordsLookupSource != null)
                        <p>
                            Before you trust the low end, pull the actual file from
                            <a href="${primaryRecordsLookupSource.url()}"
                               target="_blank"
                               rel="noreferrer"
                               data-track-click="nav"
                               data-track-source-context="state_guide_inline_source_link"
                               data-track-target-type="official_source">${primaryRecordsLookupSource.agencyName()}</a>.
                            The permit, as-built, inspection, or management record usually tells you faster than a contractor quote whether this property still fits the cheaper path.
                        </p>
                    @elseif (primaryLocalAuthoritySource != null)
                        <p>
                            Before you trust the low end, verify the actual reviewing office through
                            <a href="${primaryLocalAuthoritySource.url()}"
                               target="_blank"
                               rel="noreferrer"
                               data-track-click="nav"
                               data-track-source-context="state_guide_inline_source_link"
                               data-track-target-type="official_source">${primaryLocalAuthoritySource.agencyName()}</a>.
                            The right county, district, or delegated authority changes how fast the project moves and which requirements matter first.
                        </p>
                    @endif
                </article>
            </div>
        </section>
    @endif

    @if (state.permitPathSteps() != null && !state.permitPathSteps().isEmpty())
        <section class="section section--tight">
            <div class="container detail-grid">
                <article class="detail-card">
                    <h2>Permit path steps</h2>
                    <ul class="detail-list">
                        @for (var step : state.permitPathSteps())
                            <li>${step}</li>
                        @endfor
                    </ul>
                </article>

                <article class="detail-card">
                    <h2>Rule highlights</h2>
                    <ul class="detail-list">
                        @for (var ruleHighlight : state.ruleHighlights())
                            <li>${ruleHighlight}</li>
                        @endfor
                    </ul>
                </article>
            </div>
        </section>
    @endif

    @if (guideCountyWorkflowSynthesis != null)
        <section class="section section--tight">
            <div class="container">
                <details class="state-money-disclosure">
                    <summary>
                        <span class="eyebrow">County Workflow Snapshot</span>
                        <strong>${guideCountyWorkflowSynthesis.heading()}</strong>
                        <span>${guideCountyWorkflowSynthesis.intro()}</span>
                    </summary>
                    <div class="detail-grid">
                        @for (var field : guideCountyWorkflowSynthesis.structureHighlights())
                            <article class="detail-card">
                                <h3>${field.label()}</h3>
                                <p>${field.value()}</p>
                            </article>
                        @endfor

                        <article class="detail-card">
                            <h3>${guideCountyWorkflowSynthesis.firstArtifactsHeading()}</h3>
                            <ul class="detail-list">
                                @for (var artifact : guideCountyWorkflowSynthesis.firstArtifacts())
                                    <li>${artifact}</li>
                                @endfor
                            </ul>
                        </article>

                        <article class="detail-card">
                            <h3>${guideCountyWorkflowSynthesis.holdQuoteHeading()}</h3>
                            <ul class="detail-list">
                                @for (var holdQuoteCheck : guideCountyWorkflowSynthesis.holdQuoteChecks())
                                    <li>${holdQuoteCheck}</li>
                                @endfor
                            </ul>
                        </article>
                    </div>
                </details>
            </div>
        </section>
    @endif

    @if (state.whoToCallFirst() != null || (state.recordsToRequest() != null && !state.recordsToRequest().isEmpty()) || (state.lowEndRiskChecks() != null && !state.lowEndRiskChecks().isEmpty()))
        <section class="section section--tight" id="state-prep">
            <div class="container detail-grid">
                @if (state.whoToCallFirst() != null)
                    <article class="detail-card">
                        <h2>Who to call first</h2>
                        <p>${state.whoToCallFirst()}</p>
                    </article>
                @endif

                @if (state.recordsToRequest() != null && !state.recordsToRequest().isEmpty())
                    <article class="detail-card">
                        <h2>Records to request first</h2>
                        <ul class="detail-list">
                            @for (var record : state.recordsToRequest())
                                <li>${record}</li>
                            @endfor
                        </ul>
                    </article>
                @endif

                @if (state.lowEndRiskChecks() != null && !state.lowEndRiskChecks().isEmpty())
                    <article class="detail-card detail-card--full">
                        <h2>What can kill the low end</h2>
                        <ul class="detail-list">
                            @for (var risk : state.lowEndRiskChecks())
                                <li>${risk}</li>
                            @endfor
                        </ul>
                    </article>
                @endif
            </div>
        </section>
    @endif

    @if (state.permitTimelineNote() != null || state.buyerInspectionTrigger() != null || state.maintenanceInspectionNote() != null || state.specialAreaNote() != null)
        <section class="section section--tight">
            <div class="container detail-grid">
                @if (state.permitTimelineNote() != null)
                    <article class="detail-card">
                        <h2>Permit timeline watch</h2>
                        <p>${state.permitTimelineNote()}</p>
                    </article>
                @endif

                @if (state.buyerInspectionTrigger() != null)
                    <article class="detail-card">
                        <h2>Buyer trigger</h2>
                        <p>${state.buyerInspectionTrigger()}</p>
                    </article>
                @endif

                @if (state.maintenanceInspectionNote() != null)
                    <article class="detail-card">
                        <h2>Maintenance / inspection note</h2>
                        <p>${state.maintenanceInspectionNote()}</p>
                    </article>
                @endif

                @if (state.specialAreaNote() != null)
                    <article class="detail-card">
                        <h2>Special state wrinkle</h2>
                        <p>${state.specialAreaNote()}</p>
                    </article>
                @endif
            </div>
        </section>
    @endif

    @if (!localAuthoritySources.isEmpty() || !recordsLookupSources.isEmpty())
        <section class="section section--tight">
            <div class="container detail-grid">
                @if (!localAuthoritySources.isEmpty())
                    <article class="detail-card">
                        <h2>Verify locally</h2>
                        <ul class="source-list source-list--compact">
                            @for (var source : localAuthoritySources.stream().limit(4).toList())
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
                                        @template.tags.sourceVerification(source = source)
                                    </div>
                                </li>
                            @endfor
                        </ul>
                        @if (localAuthoritySources.size() > 4)
                            <details class="county-link-overflow">
                                <summary>Show all local authority sources</summary>
                                <ul class="source-list source-list--compact">
                                    @for (var source : localAuthoritySources.stream().skip(4).toList())
                                        <li>
                                            <strong>${source.agencyName()}</strong>
                                            <a href="${source.url()}" target="_blank" rel="noreferrer">${source.title()}</a>
                                        </li>
                                    @endfor
                                </ul>
                            </details>
                        @endif
                    </article>
                @endif

                @if (!recordsLookupSources.isEmpty())
                    <article class="detail-card">
                        <h2>Records and lookup links</h2>
                        <ul class="source-list source-list--compact">
                            @for (var source : recordsLookupSources.stream().limit(4).toList())
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
                                        @template.tags.sourceVerification(source = source)
                                    </div>
                                </li>
                            @endfor
                        </ul>
                        @if (recordsLookupSources.size() > 4)
                            <details class="county-link-overflow">
                                <summary>Show all records lookup sources</summary>
                                <ul class="source-list source-list--compact">
                                    @for (var source : recordsLookupSources.stream().skip(4).toList())
                                        <li>
                                            <strong>${source.agencyName()}</strong>
                                            <a href="${source.url()}" target="_blank" rel="noreferrer">${source.title()}</a>
                                        </li>
                                    @endfor
                                </ul>
                            </details>
                        @endif
                    </article>
                @endif
            </div>
        </section>
    @endif

    @if (coreStateComparisonRows != null && !coreStateComparisonRows.isEmpty())
        <section class="section section--tight">
            <div class="container">
                <details class="detail-card detail-card--full disclosure-card">
                    <summary>How the core six launch states differ</summary>
                    <div class="disclosure-card__body">
                        <div class="facts-table-wrap">
                            <table class="facts-table comparison-table">
                                <thead>
                                <tr>
                                    <th>State</th>
                                    <th>Call first</th>
                                    <th>Pull first</th>
                                    <th>Low-end killer</th>
                                    <th>Best next page</th>
                                </tr>
                                </thead>
                                <tbody>
                                @for (var row : coreStateComparisonRows)
                                    <tr class="${row.active() ? "comparison-table__row--active" : ""}">
                                        <td>
                                            <strong>${row.stateName()}</strong>
                                            @if (row.active())
                                                <div class="comparison-table__badge">You are here</div>
                                            @endif
                                        </td>
                                        <td data-label="Call first">${row.firstCall()}</td>
                                        <td data-label="Pull first">${row.firstRecord()}</td>
                                        <td data-label="Low-end killer">${row.lowEndTrigger()}</td>
                                        <td data-label="Best next page"><a href="${row.nextPagePath()}" data-track-click="nav" data-track-source-context="state_guide_core_comparison_next" data-track-target-type="next_page">${row.nextPageTitle()}</a></td>
                                    </tr>
                                @endfor
                                </tbody>
                            </table>
                        </div>
                        <div class="comparison-cards">
                            @for (var row : coreStateComparisonRows)
                                <article class="comparison-card ${row.active() ? "comparison-card--active" : ""}">
                                    <div class="comparison-card__header">
                                        <strong>${row.stateName()}</strong>
                                        @if (row.active())
                                            <span class="comparison-table__badge">You are here</span>
                                        @endif
                                    </div>
                                    <dl class="comparison-card__list">
                                        <div>
                                            <dt>Call first</dt>
                                            <dd>${row.firstCall()}</dd>
                                        </div>
                                        <div>
                                            <dt>Pull first</dt>
                                            <dd>${row.firstRecord()}</dd>
                                        </div>
                                        <div>
                                            <dt>Low-end killer</dt>
                                            <dd>${row.lowEndTrigger()}</dd>
                                        </div>
                                        <div>
                                            <dt>Best next page</dt>
                                            <dd><a href="${row.nextPagePath()}" data-track-click="nav" data-track-source-context="state_guide_core_comparison_next" data-track-target-type="next_page">${row.nextPageTitle()}</a></dd>
                                        </div>
                                    </dl>
                                </article>
                            @endfor
                        </div>
                    </div>
                </details>
            </div>
        </section>
    @endif

    @if (guideFaqs != null && !guideFaqs.isEmpty())
        <section class="section section--muted" id="faq">
            <div class="container">
                <details class="detail-card detail-card--full disclosure-card">
                    <summary>${state.stateName()} homeowner questions worth clearing up before you request quotes</summary>
                    <div class="detail-grid disclosure-card__body">
                        @for (var faq : guideFaqs)
                            <article class="detail-card">
                                <h3>${faq.question()}</h3>
                                <p>${faq.answer()}</p>
                            </article>
                        @endfor
                    </div>
                </details>
            </div>
        </section>
    @endif

    <section class="section section--tight">
        <div class="container">
            <article class="trust-box trust-box--large">
                <span class="trust-box__label">Need a planning range after the county check?</span>
                <h2>Use the estimate after the file, permit path, and buyer story are clear enough.</h2>
                <p>${calculatorCtaNote} If the local file is still thin, go back to the narrower workflow page instead of jumping into quote mode too early.</p>
                <div class="hero__actions">
                    <a class="button button--primary" href="/septic-system-cost-calculator/?state=${state.stateCode()}" data-track-click="nav" data-track-source-context="state_guide_ready_calculator" data-track-target-type="calculator">${calculatorCtaLabel}</a>
                    @if (!featuredStateMoneyPages.isEmpty())
                        <a class="button button--secondary"
                           href="${featuredStateMoneyPages.get(0).path(state.slug())}"
                           data-track-click="nav"
                           data-track-source-context="state_guide_ready_workflow"
                           data-track-target-type="state_money_page">Return to the workflow page</a>
                    @endif
                </div>
            </article>
        </div>
    </section>

    <section class="section" id="sources">
        <div class="container content-grid">
            <details class="trust-box trust-box--large disclosure-card">
                <summary>Official sources for ${state.stateName()}</summary>
                <ul class="source-list disclosure-card__body">
                    @for (var source : sources)
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
                                @template.tags.sourceVerification(source = source)
                            </div>
                        </li>
                    @endfor
                </ul>
            </details>

            @if (!stateMoneyPages.isEmpty())
                <article class="detail-card detail-card--full" id="intents">
                    <h2>High-intent next steps in ${state.stateName()}</h2>
                    <p>Use these ${state.stateName()} pages when the guide is not specific enough and the real bottleneck is replacement scope, the file, permit path, buyer risk, inspection history, or the site-review story.</p>
                    <div class="detail-grid">
                        @for (var stateMoneyPage : stateMoneyPages.stream().limit(6).toList())
                            <article class="detail-card">
                                <h3><a href="${stateMoneyPage.path(state.slug())}" data-track-click="nav" data-track-source-context="state_guide_next_high_intent" data-track-target-type="state_money_page">${stateMoneyPage.title()}</a></h3>
                                <p>${stateMoneyPage.uniqueAngle()}</p>
                                <a class="button button--secondary" href="${stateMoneyPage.path(state.slug())}" data-track-click="nav" data-track-source-context="state_guide_next_high_intent_cta" data-track-target-type="state_money_page">Open this page</a>
                            </article>
                        @endfor
                        <article class="detail-card">
                            <h3>Main septic cost calculator</h3>
                    <p>Use the calculator when you still need a ${state.stateName()} planning range before choosing one file, permit, or buyer narrative.</p>
                            <a class="button button--secondary" href="/septic-system-cost-calculator/?state=${state.stateCode()}" data-track-click="nav" data-track-source-context="state_guide_next_high_intent_calculator" data-track-target-type="calculator">Open the calculator</a>
                        </article>
                    </div>
                    @if (stateMoneyPages.size() > 6)
                        <details class="county-link-overflow">
                            <summary>Show all ${state.stateName()} workflow pages</summary>
                            <div class="detail-grid">
                                @for (var stateMoneyPage : stateMoneyPages.stream().skip(6).toList())
                                    <article class="detail-card">
                                        <h3><a href="${stateMoneyPage.path(state.slug())}" data-track-click="nav" data-track-source-context="state_guide_next_high_intent_overflow" data-track-target-type="state_money_page">${stateMoneyPage.title()}</a></h3>
                                        <p>${stateMoneyPage.uniqueAngle()}</p>
                                        <a class="button button--secondary" href="${stateMoneyPage.path(state.slug())}" data-track-click="nav" data-track-source-context="state_guide_next_high_intent_cta_overflow" data-track-target-type="state_money_page">Open this page</a>
                                    </article>
                                @endfor
                            </div>
                        </details>
                    @endif
                </article>
            @endif

            @if (!plannedStateMoneyPages.isEmpty())
                <details class="county-link-overflow">
                    <summary>Planned ${state.stateName()} workflow coverage</summary>
                    <p>These topics remain visible as coverage notes, but they are not linked until their source and local-workflow depth meets the publication threshold.</p>
                    <div class="detail-grid">
                        @for (var plannedPage : plannedStateMoneyPages)
                            <article class="detail-card">
                                <h3>${plannedPage.title()}</h3>
                                <p>${plannedPage.uniqueAngle()}</p>
                            </article>
                        @endfor
                    </div>
                </details>
            @endif

        </div>
    </section>

    <div class="sticky-mobile-cta-spacer" aria-hidden="true"></div>
    <section class="sticky-mobile-cta sticky-mobile-cta--single"
             aria-label="Run estimate"
             data-sticky-mobile-cta
             data-show-after='[data-sticky-trigger="state-guide-after-hero"]'>
        <div class="sticky-mobile-cta__inner">
            @if (!featuredStateMoneyPages.isEmpty())
                <a class="button button--primary"
                   href="${featuredStateMoneyPages.get(0).path(state.slug())}"
                   data-track-click="nav"
                   data-track-source-context="state_guide_sticky_workflow"
                   data-track-target-type="state_money_page">Open next workflow page</a>
            @else
                <a class="button button--primary" href="/septic-system-cost-calculator/?state=${state.stateCode()}" data-track-click="nav" data-track-source-context="state_guide_sticky_calculator" data-track-target-type="calculator">Run state estimate</a>
            @endif
        </div>
    </section>
    </div>
`)
