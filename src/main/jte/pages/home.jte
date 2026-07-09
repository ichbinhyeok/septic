@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.PageMeta
@param PageMeta page
@param java.util.List<StateProfile> featuredStates
@param java.util.List<StateProfile> spotlightStates
@param java.util.List<com.example.septic.web.PageLink> featuredIntentPages = java.util.List.of()
@param java.util.List<com.example.septic.web.CountyFinderLinkView> countyFinderLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.CountyRouteClusterView> countyRouteClusters = java.util.List.of()
@param int liveGuideCount
@param int liveIntentCount
@param int liveCountyCount = 0
@param int totalCountyRouteCount = 0
@param int countyBackedStateCount = 0
@param int queuedStateCount
@param com.example.septic.web.WorkflowNetworkSnapshotView workflowNetworkSnapshot = null

@template.layouts.app(page = page, content = @`
    <section class="hero">
        <div class="container hero__grid">
            <div class="hero__content">
                <span class="eyebrow">Septic records lookup and permit search</span>
                <h1>Find the county septic file before the quote.</h1>
                <p class="lede">Start with the permit copy, as-built, inspection letter, or county records path that can change a buyer decision, repair scope, or planning range.</p>
                <div class="hero-quick-actions" aria-label="Most common septic record actions">
                    <a class="button button--primary"
                       href="/septic-records-by-county/"
                       data-track-click="nav"
                       data-track-source-context="home_hero_primary_records"
                       data-track-target-type="internal_page">Search records by county</a>
                    <a class="button button--secondary"
                       href="/tdec-septic-records/"
                       data-track-click="nav"
                       data-track-source-context="home_hero_tdec"
                       data-track-target-type="internal_page">TDEC records</a>
                    <a class="button button--secondary"
                       href="/dhec-septic-permit-lookup/"
                       data-track-click="nav"
                       data-track-source-context="home_hero_dhec"
                       data-track-target-type="internal_page">DHEC/SCDES lookup</a>
                    <a class="button button--secondary"
                       href="/septic-records-checklist/texas/tarrant-county/"
                       data-track-click="nav"
                       data-track-source-context="home_hero_tarrant"
                       data-track-target-type="county_records_page">Tarrant records</a>
                    <a class="button button--secondary"
                       href="/septic-permit-search-by-address/"
                       data-track-click="nav"
                       data-track-source-context="home_hero_address_search"
                       data-track-target-type="internal_page">Search by address</a>
                </div>
                <p class="hero-router-intro">Use the fastest lane first. If the county file is still unclear, then move into permit process, transfer compliance, or cost planning.</p>
                <div class="hero-decision-grid hero-decision-grid--compact">
                    <article class="hero-decision-card hero-decision-card--accent">
                        <span class="hero-decision-card__label">File first</span>
                        <h3>I know the county and need the actual septic record path</h3>
                        <p>Start here when the permit copy, as-built, final approval, repair file, or inspection letter matters more than a broad state explainer.</p>
                        <a class="button button--primary" href="/septic-records-by-county/">Open county records</a>
                    </article>

                    <article class="hero-decision-card">
                        <span class="hero-decision-card__label">Lookup path</span>
                        <h3>I need the state or county permit lookup doorway</h3>
                        <p>Use this when the next move is a permit search, records portal, county office, or official source before any estimate.</p>
                        <a class="button button--secondary" href="/septic-permit-lookup/">Open permit lookup</a>
                    </article>

                    <article class="hero-decision-card">
                        <span class="hero-decision-card__label">Permit path</span>
                        <h3>I do not know which office or approval step controls this job</h3>
                        <p>Use the permit workflow when county routing, site review, project type, or closeout is the real blocker.</p>
                        <a class="button button--secondary" href="/septic-permit-process/">Open permit process</a>
                    </article>

                    <article class="hero-decision-card">
                        <span class="hero-decision-card__label">Sale or transfer</span>
                        <h3>I am trying to keep a deal or closing on track</h3>
                        <p>Use the transfer workflow when records, permit history, inspection timing, and county file quality need one checklist.</p>
                        <a class="button button--secondary" href="/septic-transfer-compliance/">Open transfer compliance</a>
                    </article>
                </div>
                <div class="hero__notes">
                    <span>${liveGuideCount} live guides</span>
                    <span>${liveIntentCount} live state pages</span>
                    <span>${liveCountyCount} live county workflow pages</span>
                    <span>${countyBackedStateCount} county-backed states</span>
                    @if (queuedStateCount > 0)
                        <span>${queuedStateCount} states in research queue</span>
                    @else
                        <span>All 50 state guides live</span>
                    @endif
                </div>
            </div>

            <aside class="hero-card">
                <div class="hero-card__top">
                    <span class="eyebrow">Use it like a tool</span>
                    <h2>Most visits should start with a file, not a price.</h2>
                    <p>Get the property record path clear first. The estimator is strongest after the county file, permit branch, or buyer workflow is known.</p>
                </div>
                <div class="hero-card__stack">
                    <div class="mini-card">
                        <strong>1. Find the county owner</strong>
                        <p>Use the county, address, or permit-copy path to identify who can actually search the file.</p>
                    </div>
                    <div class="mini-card">
                        <strong>2. Pull the record that changes scope</strong>
                        <p>Permit copy, as-built, final approval, repair note, inspection letter, or maintenance history.</p>
                    </div>
                    <div class="mini-card">
                        <strong>3. Price after the file is clearer</strong>
                        <p>Run the cost guide once the missing record, county office, or permit step is no longer vague.</p>
                    </div>
                </div>
            </aside>
        </div>
    </section>

    @template.tags.countyFinder(
        countyFinderLinks = countyFinderLinks,
        finderId = "home-county-finder",
        eyebrow = "County finder",
        heading = "Search live county septic record paths before you read another overview.",
        body = "Type the county or state and jump straight to the local permit file, records request, parcel/TMS, as-built, or inspection-letter route.",
        sourceContext = "home_county_finder",
        totalCountyRouteCount = totalCountyRouteCount
    )

    <section class="section section--tight">
        <div class="container">
            <div class="section-heading">
                <span class="eyebrow">2-week search response routes</span>
                <h2>Push the queries already close enough to win clicks.</h2>
                <p class="lede">These routes match searches already showing impressions or index gaps: TDEC records, DHEC/SCDES lookup, Tarrant County records, and septic permit lookup.</p>
            </div>

            <div class="card-grid card-grid--primary">
                <article class="feature-card feature-card--primary">
                    <span class="pill">TN / TDEC</span>
                    <h3>TDEC septic records</h3>
                    <p>Use this for TDEC septic records, state of TN septic records, SSDS permit files, and Tennessee county fallback searches.</p>
                    <a href="/tdec-septic-records/"
                       data-track-click="nav"
                       data-track-source-context="home_search_response_tdec"
                       data-track-target-type="internal_page">Open TDEC records</a>
                </article>

                <article class="feature-card feature-card--primary">
                    <span class="pill">SC / SCDES</span>
                    <h3>DHEC septic permit lookup</h3>
                    <p>Use this for DHEC searches that need the current SCDES septic tank route, D-1740 file, permit copy, or county contact.</p>
                    <a href="/dhec-septic-permit-lookup/"
                       data-track-click="nav"
                       data-track-source-context="home_search_response_dhec"
                       data-track-target-type="internal_page">Open DHEC/SCDES lookup</a>
                </article>

                <article class="feature-card">
                    <span class="pill">TX / county</span>
                    <h3>Tarrant County septic records</h3>
                    <p>Use this when the query is county-specific and the answer needs Tarrant OSSF records, official records search, or permit file routing.</p>
                    <a href="/septic-records-checklist/texas/tarrant-county/"
                       data-track-click="nav"
                       data-track-source-context="home_search_response_tarrant"
                       data-track-target-type="county_records_page">Open Tarrant records</a>
                </article>

                <article class="feature-card">
                    <span class="pill">Permit lookup</span>
                    <h3>Official septic permit lookup</h3>
                    <p>Use this broad route for state records search, county files, address search, as-built records, and permit-copy request paths.</p>
                    <a href="/septic-permit-lookup/"
                       data-track-click="nav"
                       data-track-source-context="home_search_response_permit_lookup"
                       data-track-target-type="internal_page">Open permit lookup</a>
                </article>
            </div>
        </div>
    </section>

    @if (!countyRouteClusters.isEmpty())
        <section class="section section--tight">
            <div class="container">
                <div class="section-heading">
                    <span class="eyebrow">Fast county routes</span>
                    <h2>Open the exact county file path before another broad guide.</h2>
                    <p class="lede">These are the county-backed states most likely to turn a searcher into a useful click: permit lookup, records request, parcel/TMS search, buyer file, or repair trail.</p>
                </div>

                <div class="county-route-board">
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
                                       data-track-source-context="home_county_route_board"
                                       data-track-target-type="county_records_page">${link.compactTitle()}</a>
                                @endfor
                            </div>
                            <div class="county-route-cluster__actions">
                                <a class="button button--secondary"
                                   href="${cluster.stateRecordsLink().path()}"
                                   data-track-click="nav"
                                   data-track-source-context="home_county_route_state_records"
                                   data-track-target-type="state_money_page">${cluster.stateRecordsLink().title()}</a>
                                <a class="button button--secondary"
                                   href="${cluster.permitProcessLink().path()}"
                                   data-track-click="nav"
                                   data-track-source-context="home_county_route_permit_process"
                                   data-track-target-type="state_money_page">Permit process</a>
                            </div>
                        </article>
                    @endfor
                </div>
            </div>
        </section>
    @endif

    <section class="section section--tight">
        <div class="container card-grid card-grid--primary">
            <article class="feature-card feature-card--primary">
                <span class="pill">Primary</span>
                <h3>Permit lookup</h3>
                <p>Use this doorway when you need the state record search, county permit file, or TDEC-style lookup path before choosing the next workflow.</p>
                <a href="/septic-permit-lookup/">Open permit lookup</a>
            </article>

            <article class="feature-card feature-card--primary">
                <span class="pill">County records</span>
                <h3>Septic records by county</h3>
                <p>Use this when the county is known and the next click should be the local file owner, permit copy, as-built, or inspection letter path.</p>
                <a href="/septic-records-by-county/">Open county records</a>
            </article>

            <article class="feature-card">
                <span class="pill">Address search</span>
                <h3>Permit search by address</h3>
                <p>Use this when an address search needs parcel, owner, legal-description, or county portal tactics before the record is treated as missing.</p>
                <a href="/septic-permit-search-by-address/">Search by address</a>
            </article>

            <article class="feature-card">
                <span class="pill">Request form</span>
                <h3>Permit records request</h3>
                <p>Use this when the office needs a specific request for the permit copy, as-built, final approval, repair file, or inspection letter.</p>
                <a href="/septic-permit-records-request/">Open records request</a>
            </article>

            <article class="feature-card">
                <span class="pill">Layout file</span>
                <h3>As-built records</h3>
                <p>Use this when tank, field, reserve-area, site sketch, or installed-layout details can change repair, addition, or replacement scope.</p>
                <a href="/septic-as-built-records/">Open as-built records</a>
            </article>

            <article class="feature-card">
                <span class="pill">Closing file</span>
                <h3>Inspection letter</h3>
                <p>Use this when a lender, buyer, or closing file needs more than a generic permit copy or public records result.</p>
                <a href="/septic-inspection-letter/">Open inspection letter</a>
            </article>

            <article class="feature-card">
                <span class="pill">Records</span>
                <h3>Records lookup</h3>
                <p>Start here when the septic story depends on permit records, as-builts, maintenance history, or one missing file that could widen the downside fast.</p>
                <a href="/septic-records-checklist/">Open records lookup</a>
            </article>

            <article class="feature-card">
                <span class="pill">Transfer</span>
                <h3>Transfer compliance</h3>
                <p>Use this when records, permit routing, inspection timing, and buyer-seller workflow need to be resolved together before closing.</p>
                <a href="/septic-transfer-compliance/">Open transfer compliance</a>
            </article>

            <article class="feature-card">
                <span class="pill">Workflow</span>
                <h3>Permit process</h3>
                <p>Use this when the next office, permit step, site review, or approval sequence is the actual blocker behind the budget question.</p>
                <a href="/septic-permit-process/">Open permit process</a>
            </article>

            <article class="feature-card">
                <span class="pill">Planning tool</span>
                <h3>Cost estimator</h3>
                <p>Use the estimator after the workflow is clearer, or when you still need one practical range before the next county, file, or inspection step.</p>
                <a href="/septic-system-cost-calculator/">Open cost estimator</a>
            </article>
        </div>
    </section>

    @if (!featuredIntentPages.isEmpty())
        <section class="section section--tight">
            <div class="container">
                <div class="section-heading">
                    <span class="eyebrow">Live workflow pages</span>
                    <h2>Open the state-specific permit, records, and buyer pages already proving demand.</h2>
                    <p class="lede">These pages are the clearest proof that users want county file paths, permit routing, and transfer diligence before they want another broad septic explainer.</p>
                </div>

                <div class="card-grid">
                    @for (var link : featuredIntentPages)
                        <article class="detail-card">
                            <span class="pill">State-specific page</span>
                            <h3>${link.title()}</h3>
                            <p>${link.note()}</p>
                            <a href="${link.path()}"
                               data-track-click="nav"
                               data-track-source-context="home_featured_intent"
                               data-track-target-type="state_money_page">Open page</a>
                        </article>
                    @endfor
                </div>
            </div>
        </section>
    @endif

    @if (workflowNetworkSnapshot != null)
        <section class="section section--tight">
            <div class="container content-grid">
                <article class="trust-box trust-box--large">
                    <span class="trust-box__label">County-backed network</span>
                    <h2>${workflowNetworkSnapshot.heading()}</h2>
                    <p>${workflowNetworkSnapshot.summary()}</p>
                    <ul class="detail-list">
                        @for (var bullet : workflowNetworkSnapshot.bullets())
                            <li>${bullet}</li>
                        @endfor
                    </ul>
                </article>

                <article class="detail-card">
                    <h2>Where the live backbone is already thick</h2>
                    <div class="detail-grid">
                        @for (var link : workflowNetworkSnapshot.featuredStateLinks())
                            <article class="detail-card">
                                <span class="pill">County-backed state</span>
                                <h3>${link.title()}</h3>
                                <p>${link.note()}</p>
                                <a href="${link.path()}">Open county-backed state page</a>
                            </article>
                        @endfor
                    </div>
                </article>
            </div>
        </section>
    @endif

    <section class="section section--muted">
        <div class="container content-grid">
            <article class="trust-box trust-box--large">
                @if (queuedStateCount > 0)
                    <span class="trust-box__label">50-state rollout</span>
                    <h2>Coverage is widening now, but the wedge is county records and permit workflow, not generic estimator copy.</h2>
                    <p>The target is to match 50-state breadth without publishing thin state-name swaps. Live guides and state pages should launch only when the official-source set, local office path, records workflow, and state-specific wrinkle are strong enough to change the next action.</p>
                @else
                    <span class="trust-box__label">50-state base is live</span>
                    <h2>All state guides are public. The next quality gains come from deeper records, permit, buyer, and county workflow pages.</h2>
                    <p>The job now is not to claim broader coverage. It is to make the live footprint more trustworthy by keeping weak cost pages out of the way and making the strongest workflow pages easier to open first.</p>
                @endif
                <div class="hero__actions">
                    <a class="button button--primary" href="/states/">Browse state guides</a>
                    <a class="button button--secondary" href="/methodology/">Read the methodology</a>
                    <a class="button button--secondary" href="/coverage/">Open coverage board</a>
                </div>
            </article>

            <article class="detail-card">
                <h2>Why the live pages are heavier</h2>
                <ul class="detail-list">
                    <li>Each live guide keeps a visible official-source module and last reviewed date.</li>
                    <li>Permit, records, buyer, and transfer pages are published only where the state or local workflow is actually distinct.</li>
                    <li>Unknown rule details widen the estimate instead of being invented to fill the page.</li>
                </ul>
            </article>
        </div>
    </section>

    <section class="section" id="states">
        <div class="container">
            <div class="section-heading">
                <span class="eyebrow">Live guide spotlight</span>
                <h2>Current live states with the clearest homeowner workflow angles.</h2>
                <p class="lede">These are the states where the published guide is already doing real work beyond a generic calculator result.</p>
            </div>

            <div class="state-grid">
                @for (var state : spotlightStates)
                    <article class="state-card">
                        <div class="state-card__header">
                            <span class="pill">${"anchor".equalsIgnoreCase(state.launchTier()) ? "Featured" : "Live"}</span>
                            <strong>${state.stateName()}</strong>
                        </div>
                        <p>${state.pageAngle()}</p>
                        <ul class="state-card__meta">
                            <li>${state.ruleType()}</li>
                            <li>${state.officialSourceIds().size()} official sources</li>
                            <li>Updated ${state.lastVerifiedAt()}</li>
                        </ul>
                        <a href="/septic-system-cost-calculator/${state.slug()}/">Open guide</a>
                    </article>
                @endfor
            </div>

            <div class="hero__actions home-section-actions">
                @if (queuedStateCount > 0)
                    <a class="button button--secondary" href="/states/">Browse all live and queued states</a>
                @else
                    <a class="button button--secondary" href="/states/">Browse all live states</a>
                @endif
            </div>
        </div>
    </section>
`)
