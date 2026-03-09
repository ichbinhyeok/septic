@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.PageMeta
@param PageMeta page
@param java.util.List<StateProfile> states

@template.layouts.app(page = page, content = @`
    <section class="hero">
        <div class="container hero__grid">
            <div class="hero__content">
                <span class="eyebrow">Homeowner planning estimator</span>
                <h1>Rough septic cost, likely tank size, and what to verify next.</h1>
                <p class="lede">A state-aware planning tool for homeowners and homebuyers. Built for estimate ranges, source transparency, and fast next steps, not fake permit-ready precision.</p>
                <div class="hero__actions">
                    <a class="button button--primary" href="/septic-system-cost-calculator/">Open the estimator</a>
                    <a class="button button--secondary" href="#states">Browse launch states</a>
                </div>
                <div class="hero__notes">
                    <span>Estimate only</span>
                    <span>Official-source module on each state page</span>
                    <span>10 launch states</span>
                </div>
            </div>

            <aside class="hero-card">
                <div class="hero-card__top">
                    <span class="eyebrow">What the tool returns</span>
                    <h2>Planning output, not fake certainty</h2>
                </div>
                <div class="hero-card__stack">
                    <div class="mini-card">
                        <strong>Likely tank range</strong>
                        <p>Official minimums when available, widened when inputs are weak.</p>
                    </div>
                    <div class="mini-card">
                        <strong>Likely system class</strong>
                        <p>Conventional, chamber, alternative, or site-specific review.</p>
                    </div>
                    <div class="mini-card">
                        <strong>Cost drivers</strong>
                        <p>Soil, access, water table, replacement complexity, and permit steps.</p>
                    </div>
                </div>
            </aside>
        </div>
    </section>

    <section class="section">
        <div class="container">
            <div class="section-heading">
                <span class="eyebrow">V1 scope</span>
                <h2>Built to win cost-first intent before broad education content.</h2>
            </div>

            <div class="card-grid">
                <article class="feature-card">
                    <h3>Main cost estimator</h3>
                    <p>State-aware planning range for install, replacement, pumping, inspection, and more.</p>
                    <a href="/septic-system-cost-calculator/">Launch calculator</a>
                </article>

                <article class="feature-card">
                    <h3>Dedicated size and pump tools</h3>
                    <p>Separate estimator routes for tank sizing and maintenance cadence when the user is not ready for the full quote flow yet.</p>
                    <a href="/septic-tank-size-estimator/">Open tank size estimator</a>
                </article>

                <article class="feature-card">
                    <h3>State guide pages</h3>
                    <p>Plain-English rule summary, permit path, local override note, and official links.</p>
                    <a href="#states">See launch states</a>
                </article>

                <article class="feature-card">
                    <h3>Permit and records workflows</h3>
                    <p>State-specific pages for septic permit process, records to request, and buyer-side verification steps.</p>
                    <a href="/septic-permit-process/">Browse workflow pages</a>
                </article>

                <article class="feature-card">
                    <h3>Inspection and buyer-risk pages</h3>
                    <p>High-intent content for septic inspection cost, transfer timing, and the records gaps that can blow up a home purchase.</p>
                    <a href="/septic-inspection-cost/">Open inspection cost page</a>
                </article>

                <article class="feature-card">
                    <h3>File-based data model</h3>
                    <p>No database in V1. Research data stays versioned in JSON and CSV so provenance remains visible.</p>
                    <a href="#sources">See source policy</a>
                </article>
            </div>
        </div>
    </section>

    <section class="section section--muted" id="states">
        <div class="container">
            <div class="section-heading">
                <span class="eyebrow">Launch states</span>
                <h2>Ten state wedges with different rule styles and permit flows.</h2>
                <p class="lede">Anchor states carry the strongest early rule extraction. Supporting states are live with official-source context and intentionally conservative framing while the sizing logic gets deeper.</p>
            </div>

            <div class="state-group">
                <h3 class="state-group__heading">Anchor states</h3>
                <div class="state-grid">
                    @for (var state : states)
                        @if ("anchor".equals(state.launchTier()))
                            <article class="state-card">
                                <div class="state-card__header">
                                    <span class="pill">${state.launchTier()}</span>
                                    <strong>${state.stateName()}</strong>
                                </div>
                                <p>${state.pageAngle()}</p>
                                <ul class="state-card__meta">
                                    <li>${state.ruleType()}</li>
                                    <li>${state.agencyName()}</li>
                                    <li>Last verified ${state.lastVerifiedAt()}</li>
                                </ul>
                                <a href="/septic-system-cost-calculator/${state.slug()}/">Open guide</a>
                            </article>
                        @endif
                    @endfor
                </div>
            </div>

            <div class="state-group">
                <h3 class="state-group__heading">Supporting states</h3>
                <div class="state-grid">
                    @for (var state : states)
                        @if (!"anchor".equals(state.launchTier()))
                            <article class="state-card">
                                <div class="state-card__header">
                                    <span class="pill">${state.launchTier()}</span>
                                    <strong>${state.stateName()}</strong>
                                </div>
                                <p>${state.pageAngle()}</p>
                                <ul class="state-card__meta">
                                    <li>${state.ruleType()}</li>
                                    <li>${state.officialSourceIds().size()} official sources</li>
                                    <li>Override risk ${state.countyOverrideRisk()}</li>
                                </ul>
                                <a href="/septic-system-cost-calculator/${state.slug()}/">Open guide</a>
                            </article>
                        @endif
                    @endfor
                </div>
            </div>
        </div>
    </section>

    <section class="section" id="sources">
        <div class="container">
            <article class="trust-box trust-box--large">
                <span class="trust-box__label">Source policy</span>
                <h2>Every publishable state page needs official links, agency attribution, last verified date, and a local override note.</h2>
                <p>Commercial sources are allowed for broad public cost anchors only. If the rule is unclear, the estimate gets wider instead of pretending to be exact.</p>
            </article>
        </div>
    </section>
`)
