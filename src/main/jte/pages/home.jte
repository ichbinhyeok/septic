@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.PageMeta
@param PageMeta page
@param java.util.List<StateProfile> featuredStates
@param java.util.List<StateProfile> spotlightStates
@param java.util.List<com.example.septic.web.PageLink> featuredIntentPages = java.util.List.of()
@param int liveGuideCount
@param int liveIntentCount
@param int queuedStateCount

@template.layouts.app(page = page, content = @`
    <section class="hero">
        <div class="container hero__grid">
            <div class="hero__content">
                <span class="eyebrow">Septic records, permits, and transfer prep</span>
                <h1>Start with the file, permit path, or buyer workflow that actually changes the septic story.</h1>
                <p class="lede">SepticPath is built for homeowners, buyers, and agents who need to know which county office, permit record, inspection trigger, or transfer step matters before they trust a clean-looking quote.</p>
                <div class="hero__actions">
                    <a class="button button--primary" href="/septic-records-checklist/">Start with records checklist</a>
                    <a class="button button--secondary" href="/septic-transfer-compliance/">Open transfer compliance guide</a>
                </div>
                <div class="hero__notes">
                    <span>${liveGuideCount} live guides</span>
                    <span>${liveIntentCount} live state pages</span>
                    <span>${queuedStateCount} states in research queue</span>
                </div>
            </div>

            <aside class="hero-card">
                <div class="hero-card__top">
                    <span class="eyebrow">Start with one lane</span>
                    <h2>Use the shortest path to the real question.</h2>
                </div>
                <div class="hero-card__stack">
                    <div class="mini-card">
                        <strong>Missing the file?</strong>
                        <p>Open records checklist when the permit copy, as-built, pumping history, or inspection notes are thinner than the current story.</p>
                    </div>
                    <div class="mini-card">
                        <strong>Blocked on the next office?</strong>
                        <p>Use permit process when the real bottleneck is county routing, site review, or figuring out which authority owns the next approval step.</p>
                    </div>
                    <div class="mini-card">
                        <strong>Stuck in a sale or closing?</strong>
                        <p>Open transfer compliance when records, permit history, buyer diligence, and county file quality all need to be sorted together before the deal moves.</p>
                    </div>
                    <div class="mini-card">
                        <strong>Need the number anyway?</strong>
                        <p>Run the estimator after the workflow is clearer, or when the file is still thin and you need a cautious planning range before the next call.</p>
                    </div>
                </div>
            </aside>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container card-grid card-grid--primary">
            <article class="feature-card feature-card--primary">
                <span class="pill">Primary</span>
                <h3>Records checklist</h3>
                <p>Start here when the septic story depends on permit records, as-builts, maintenance history, or one missing file that could widen the downside fast.</p>
                <a href="/septic-records-checklist/">Open records checklist</a>
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

    <section class="section section--muted">
        <div class="container content-grid">
            <article class="trust-box trust-box--large">
                <span class="trust-box__label">50-state rollout</span>
                <h2>Coverage is widening now, but the wedge is county records and permit workflow, not generic estimator copy.</h2>
                <p>The target is to match 50-state breadth without publishing thin state-name swaps. Live guides and state pages should launch only when the official-source set, local office path, records workflow, and state-specific wrinkle are strong enough to change the next action.</p>
                <div class="hero__actions">
                    <a class="button button--primary" href="/states/">Browse state guides</a>
                    <a class="button button--secondary" href="/about/">Read the source standard</a>
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
                <a class="button button--secondary" href="/states/">Browse all live and queued states</a>
            </div>
        </div>
    </section>
`)
