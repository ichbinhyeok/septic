@import com.example.septic.web.PageMeta
@import com.example.septic.web.StateCoverageCardView
@import com.example.septic.web.StateQueuePlanView
@param PageMeta page
@param java.util.List<StateCoverageCardView> liveStates
@param java.util.List<StateQueuePlanView> priorityQueuePlans = java.util.List.of()
@param java.util.List<StateCoverageCardView> queuedStates
@param int liveGuideCount
@param int queuedStateCount
@param int liveIntentCount

@template.layouts.app(page = page, content = @`
    <section class="section section--heroish">
        <div class="container content-grid">
            <article class="hero__content">
                <span class="eyebrow">State coverage</span>
                @if (queuedStateCount > 0)
                    <h1>50-state coverage is expanding in waves, not with thin copy swaps.</h1>
                    <p class="lede">The goal is to match broad state coverage quickly while keeping the live guides tied to official sources, local office context, and real homeowner workflow differences.</p>
                @else
                    <h1>All 50 state guides are live, and the next gains come from deeper intent pages.</h1>
                    <p class="lede">Every state guide is now public. The next wave is deeper permit, records, buyer, replacement, and site-risk coverage built on official sources and real local workflow differences.</p>
                @endif
                <div class="hero__actions">
                    <a class="button button--primary" href="/septic-system-cost-calculator/">Run the main estimate</a>
                    <a class="button button--secondary" href="/#states">See current live guides</a>
                </div>
            </article>

            <article class="trust-box trust-box--large coverage-summary">
                <span class="trust-box__label">Coverage snapshot</span>
                <div class="coverage-summary__stats">
                    <article class="metric-card">
                        <span class="metric-card__label">Live guides</span>
                        <strong>${liveGuideCount}</strong>
                        <p>Fully published state guides with official-source coverage.</p>
                    </article>
                    <article class="metric-card">
                        <span class="metric-card__label">Live deep pages</span>
                        <strong>${liveIntentCount}</strong>
                        <p>State-specific replacement, permit, records, buyer, and related workflow pages.</p>
                    </article>
                    <article class="metric-card">
                        <span class="metric-card__label">Queue states</span>
                        <strong>${queuedStateCount}</strong>
                        <p>States reserved for official-source research before a public guide goes live.</p>
                    </article>
                </div>
            </article>
        </div>
    </section>

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
`)
