@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.PageMeta
@param PageMeta page
@param java.util.List<StateProfile> featuredStates
@param java.util.List<StateProfile> spotlightStates
@param java.util.List<com.example.septic.web.PageLink> featuredIntentPages = java.util.List.of()
@param int liveGuideCount
@param int liveIntentCount
@param int liveCountyCount = 0
@param int countyBackedStateCount = 0
@param int queuedStateCount
@param com.example.septic.web.WorkflowNetworkSnapshotView workflowNetworkSnapshot = null

@template.layouts.app(page = page, content = @`
    <section class="hero">
        <div class="container hero__grid">
            <div class="hero__content">
                <span class="eyebrow">Septic records, permits, and transfer prep</span>
                <h1>Start with the file, permit path, or buyer workflow that actually changes the septic story.</h1>
                <p class="lede">SepticPath is built for homeowners, buyers, and agents who need to know which county office, permit record, inspection trigger, or transfer step matters before they trust a clean-looking quote.</p>
                <p class="hero-router-intro">Pick the first move that matches your live problem. The deeper reading and state guides can wait until the route is clear.</p>
                <div class="hero-decision-grid">
                    <article class="hero-decision-card hero-decision-card--accent">
                        <span class="hero-decision-card__label">File first</span>
                        <h3>I need the septic file before I trust the story</h3>
                        <p>Start here when the permit copy, as-built, pumping history, or inspection notes are thinner than the current seller, buyer, or contractor story.</p>
                        <a class="button button--primary" href="/septic-records-checklist/">Open records checklist</a>
                    </article>

                    <article class="hero-decision-card">
                        <span class="hero-decision-card__label">Permit path</span>
                        <h3>I do not know which office or step controls this job</h3>
                        <p>Use the permit workflow when the real blocker is county routing, site review, project classification, or the next approval step.</p>
                        <a class="button button--secondary" href="/septic-permit-process/">Open permit process</a>
                    </article>

                    <article class="hero-decision-card">
                        <span class="hero-decision-card__label">Sale or transfer</span>
                        <h3>I am trying to keep a deal or closing on track</h3>
                        <p>Use the transfer workflow when records, permit history, inspection timing, and county file quality all need to be sorted together before the deal moves.</p>
                        <a class="button button--secondary" href="/septic-transfer-compliance/">Open transfer compliance</a>
                    </article>

                    <article class="hero-decision-card">
                        <span class="hero-decision-card__label">Planning range</span>
                        <h3>I need one cautious number before the next call</h3>
                        <p>Run the estimator after the workflow is clearer, or when the file is still thin and you need a conservative planning range instead of a fake low end.</p>
                        <a class="button button--secondary" href="/septic-system-cost-calculator/">Open cost estimator</a>
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
                    <h2>Choose first. Read deeper second.</h2>
                    <p>The site works best when you match the live problem to one lane, get the next action, and only then open the longer explanation.</p>
                </div>
                <div class="hero-card__stack">
                    <div class="mini-card">
                        <strong>1. Match the real blocker</strong>
                        <p>File gap, permit bottleneck, sale timing, or cautious range. Do not start from a generic quote question.</p>
                    </div>
                    <div class="mini-card">
                        <strong>2. Open the narrow workflow</strong>
                        <p>Use the records, permit, buyer, or transfer lane before you let one statewide average flatten the problem.</p>
                    </div>
                    <div class="mini-card">
                        <strong>3. Price after the story is clearer</strong>
                        <p>The estimator is there to sharpen the next decision, not to replace the file, county office, or inspection history.</p>
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
                @if (queuedStateCount > 0)
                    <a class="button button--secondary" href="/states/">Browse all live and queued states</a>
                @else
                    <a class="button button--secondary" href="/states/">Browse all live states</a>
                @endif
            </div>
        </div>
    </section>
`)
