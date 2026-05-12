@import com.example.septic.web.PageMeta
@import com.example.septic.web.StateCoverageCardView
@import com.example.septic.web.StateQueuePlanView
@param PageMeta page
@param java.util.List<StateCoverageCardView> liveStates
@param java.util.List<com.example.septic.web.PageLink> featuredIntentPages = java.util.List.of()
@param java.util.List<StateQueuePlanView> priorityQueuePlans = java.util.List.of()
@param java.util.List<StateCoverageCardView> queuedStates
@param int liveGuideCount
@param int queuedStateCount
@param int liveIntentCount
@param int liveCountyCount = 0
@param int countyBackedStateCount = 0
@param com.example.septic.web.WorkflowNetworkSnapshotView workflowNetworkSnapshot = null

@template.layouts.app(page = page, content = @`
    <div class="state-coverage-page">
    <section class="section section--heroish">
        <div class="container content-grid">
            <article class="hero__content">
                <span class="eyebrow">State coverage</span>
                @if (queuedStateCount > 0)
                    <h1>Choose the state workflow that matches the file problem.</h1>
                    <p class="lede">Use this hub to route into records, permit, buyer, replacement, and county-file pages. Coverage breadth matters only when the next page gives you a concrete office, file, or local verification path.</p>
                @else
                    <h1>Start with the state, then narrow to the file path.</h1>
                    <p class="lede">Every state guide is public, but the useful move is choosing the page that matches the blocker: records, permit desk, buyer diligence, replacement scope, or county workflow.</p>
                @endif
                <div class="hero__actions">
                    <a class="button button--primary" href="/septic-records-checklist/">Start with records checklist</a>
                    <a class="button button--secondary" href="/septic-permit-process/">Open permit process guide</a>
                </div>
            </article>

            <article class="trust-box trust-box--large coverage-summary" data-nosnippet>
                <span class="trust-box__label">Coverage snapshot</span>
                <div class="coverage-summary__stats">
                    <article class="metric-card">
                        <span class="metric-card__label">State entry points</span>
                        <strong>${liveGuideCount}</strong>
                        <p>Use these as routing pages, not final answers.</p>
                    </article>
                    <article class="metric-card">
                        <span class="metric-card__label">Workflow pages</span>
                        <strong>${liveIntentCount}</strong>
                        <p>Pages kept public when they can point to a concrete workflow or evidence trail.</p>
                    </article>
                    <article class="metric-card">
                        <span class="metric-card__label">County file pages</span>
                        <strong>${liveCountyCount}</strong>
                        <p>Use these when the state page is still too broad.</p>
                    </article>
                    <article class="metric-card">
                        <span class="metric-card__label">States with county follow-up</span>
                        <strong>${countyBackedStateCount}</strong>
                        <p>State pages with at least one county-level next step behind them.</p>
                    </article>
                </div>
            </article>
        </div>
    </section>

    @if (workflowNetworkSnapshot != null)
        <section class="section section--tight">
            <div class="container">
                <div class="section-heading">
                    <span class="eyebrow">County-backed network</span>
                    <h2>${workflowNetworkSnapshot.heading()}</h2>
                    <p class="lede">${workflowNetworkSnapshot.summary()}</p>
                </div>

                <div class="content-grid">
                    <article class="detail-card">
                        <h3>What this means</h3>
                        <ul class="detail-list">
                            @for (var bullet : workflowNetworkSnapshot.bullets())
                                <li>${bullet}</li>
                            @endfor
                        </ul>
                    </article>

                    <article class="detail-card">
                        <h3>Where the backbone is already thick</h3>
                        <div class="detail-grid">
                            @for (var link : workflowNetworkSnapshot.featuredStateLinks())
                                <article class="detail-card">
                                    <span class="pill">County-backed state</span>
                                    <h4>${link.title()}</h4>
                                    <p>${link.note()}</p>
                                    <a href="${link.path()}">Open ${link.title()}</a>
                                </article>
                            @endfor
                        </div>
                    </article>
                </div>
            </div>
        </section>
    @endif

    @if (!featuredIntentPages.isEmpty())
        <section class="section section--tight">
            <div class="container">
                <div class="section-heading">
                    <span class="eyebrow">Best live deep pages</span>
                    <h2>Use the directory to open real intent pages, not just state guides.</h2>
                    <p class="lede">These live pages already cover the strongest permit, records, buyer, replacement, and site-risk wedges across the states with the best source-backed angles.</p>
                </div>

                <div class="card-grid">
                    @for (var link : featuredIntentPages)
                        <article class="detail-card">
                            <span class="pill">Live intent page</span>
                            <h3>${link.title()}</h3>
                            <p>${link.note()}</p>
                            <a href="${link.path()}"
                               data-track-click="nav"
                               data-track-source-context="state_coverage_featured_intent"
                               data-track-target-type="state_money_page">Open ${link.title()}</a>
                        </article>
                    @endfor
                </div>
            </div>
        </section>
    @endif

    @if (!priorityQueuePlans.isEmpty())
        <section class="section section--tight">
            <div class="container">
                <div class="section-heading">
                    <span class="eyebrow">Next rollout wave</span>
                    <h2>Top queued states for the next source-backed launch wave.</h2>
                    <p class="lede">These are the next ten state URLs that should move from generic queue landings into real state guides once the first source pack is in place.</p>
                </div>

                <div class="detail-grid">
                    @for (var plan : priorityQueuePlans)
                        <article class="detail-card">
                            <span class="pill pill--muted">${plan.rolloutWave()} | #${plan.priorityRank()}</span>
                            <h3>${plan.stateName()}</h3>
                            <p>${plan.whyNow()}</p>
                            <p><strong>Target angle:</strong> ${plan.launchAngle()}</p>
                            <p><a href="${plan.recommendedLink().path()}">${plan.recommendedLink().title()}</a></p>
                            <a class="button button--secondary" href="${plan.statePath()}">Open rollout plan</a>
                        </article>
                    @endfor
                </div>
            </div>
        </section>
    @endif

    <section class="section section--tight">
        <div class="container">
            <div class="section-heading">
                <span class="eyebrow">Live now</span>
                <h2>Published states with live guide coverage and linked intent pages.</h2>
            </div>

            <div class="coverage-grid">
                @for (var state : liveStates)
                    <article class="coverage-card coverage-card--live">
                        <div class="coverage-card__header">
                            <div>
                                <span class="pill">${state.statusLabel()}</span>
                                <h3>${state.stateName()}</h3>
                            </div>
                            <strong>${state.stateCode()}</strong>
                        </div>
                        <p>${state.summary()}</p>
                        <p class="coverage-card__meta">${state.metaLine()}</p>
                        <a class="button button--secondary" href="${state.actionPath()}">${state.actionLabel()}</a>
                    </article>
                @endfor
            </div>
        </div>
    </section>

    <section class="section section--muted">
        <div class="container">
            @if (queuedStateCount > 0)
                <div class="section-heading">
                    <span class="eyebrow">Research queue</span>
                    <h2>Queued states are held back until the official-source set is strong enough.</h2>
                    <p class="lede">This is the tradeoff: coverage expands fast, but public state guides should still launch with a real local angle instead of generic state-name swaps.</p>
                </div>

                <div class="coverage-grid coverage-grid--queued">
                    @for (var state : queuedStates)
                        <article class="coverage-card coverage-card--queued">
                            <div class="coverage-card__header">
                                <div>
                                    <span class="pill pill--muted">${state.statusLabel()}</span>
                                    <h3>${state.stateName()}</h3>
                                </div>
                                <strong>${state.stateCode()}</strong>
                            </div>
                            <p>${state.summary()}</p>
                            <p class="coverage-card__meta">${state.metaLine()}</p>
                            <a class="button button--secondary" href="${state.actionPath()}">${state.actionLabel()}</a>
                        </article>
                    @endfor
                </div>
            @else
                <div class="section-heading">
                    <span class="eyebrow">Queue closed</span>
                    <h2>All state guides are live. The next wave is deeper state intent coverage.</h2>
                    <p class="lede">Coverage work now shifts from queued state launches into stronger permit, records, buyer, replacement, and site-risk pages inside the states already live.</p>
                </div>
            @endif
        </div>
    </section>
    </div>
`)
