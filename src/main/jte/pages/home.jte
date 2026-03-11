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
                <span class="eyebrow">Estimate-first septic planning</span>
                <h1>Get a septic planning range fast, then open the state guide that actually changes the next step.</h1>
                <p class="lede">SepticPath is built for homeowners and buyers who need a directionally honest budget before they start chasing quotes, county offices, or half-complete septic files.</p>
                <div class="hero__actions">
                    <a class="button button--primary" href="/septic-system-cost-calculator/">Run the main estimate</a>
                    <a class="button button--secondary" href="/states/">Browse all state coverage</a>
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
                        <strong>Need a budget now?</strong>
                        <p>Use the main estimator when replacement, permit, inspection, or perc risk is still mixed together.</p>
                    </div>
                    <div class="mini-card">
                        <strong>Need one state fast?</strong>
                        <p>Open the live state guide when the county office, records path, or buyer trigger matters more than the average.</p>
                    </div>
                    <div class="mini-card">
                        <strong>Only need tank size?</strong>
                        <p>Use the tank size tool when gallon band is the only open question.</p>
                    </div>
                </div>
            </aside>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container card-grid card-grid--primary">
            <article class="feature-card feature-card--primary">
                <span class="pill">Primary</span>
                <h3>Main cost estimator</h3>
                <p>Best first stop when you need one practical range before you decide whether this is really a replacement, permit, buyer-risk, or perc problem.</p>
                <a href="/septic-system-cost-calculator/">Start the estimate</a>
            </article>

            <article class="feature-card">
                <span class="pill">State guides</span>
                <h3>Official-source state coverage</h3>
                <p>Use the live guides for permit path, records to request, local office routing, and state-specific low-end breakers.</p>
                <a href="/states/">Browse all state coverage</a>
            </article>

            <article class="feature-card">
                <span class="pill">Quick tool</span>
                <h3>Tank size estimator</h3>
                <p>Use the shorter tool when bedroom count, occupancy, disposal load, or ADU risk is the main question.</p>
                <a href="/septic-tank-size-estimator/">Open tank size estimator</a>
            </article>
        </div>
    </section>

    @if (!featuredIntentPages.isEmpty())
        <section class="section section--tight">
            <div class="container">
                <div class="section-heading">
                    <span class="eyebrow">Live intent pages</span>
                    <h2>Open the highest-intent state-specific pages directly.</h2>
                    <p class="lede">These pages are already live and tend to match the strongest homeowner search intent better than a generic state directory click.</p>
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
                <h2>Coverage is widening now, but live guides still need a real state angle.</h2>
                <p>The target is to match 50-state breadth without publishing thin state-name swaps. Live guides launch only when the official-source set, local office path, records checklist, and state-specific wrinkle are strong enough.</p>
                <div class="hero__actions">
                    <a class="button button--primary" href="/states/">See live and queued states</a>
                    <a class="button button--secondary" href="/about/">Read the source standard</a>
                </div>
            </article>

            <article class="detail-card">
                <h2>Why the live pages are heavier</h2>
                <ul class="detail-list">
                    <li>Each live guide keeps a visible official-source module and last reviewed date.</li>
                    <li>Permit, records, buyer, and replacement pages are published only where the state workflow is actually distinct.</li>
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
