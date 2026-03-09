@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.PageMeta
@param PageMeta page
@param java.util.List<StateProfile> featuredStates
@param java.util.List<StateProfile> additionalStates

@template.layouts.app(page = page, content = @`
    <section class="hero">
        <div class="container hero__grid">
            <div class="hero__content">
                <span class="eyebrow">Homeowner planning estimator</span>
                <h1>Rough septic cost, likely tank size, and what to verify next.</h1>
                <p class="lede">A state-aware planning tool for homeowners and homebuyers. Built for estimate ranges, source transparency, and fast next steps, not fake permit-ready precision.</p>
                <div class="hero__actions">
                    <a class="button button--primary" href="/septic-system-cost-calculator/">Start quote-ready estimate</a>
                    <a class="button button--secondary" href="/septic-system-cost-calculator/">Get matched with local septic pros</a>
                </div>
                <a class="hero__subaction" href="#states">Browse state guides first</a>
                <div class="hero__notes">
                    <span>Short match form unlocked after the estimate</span>
                    <span>Official-source module on each state page</span>
                    <span>10 state guides</span>
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
                <span class="eyebrow">What you can use here</span>
                <h2>Built around homeowner cost, permit, records, and buyer-risk questions.</h2>
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
                    <a href="#states">See state guides</a>
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
                    <h3>Source transparency</h3>
                    <p>Each state page is built around official sources, visible agency attribution, and a clear local-override warning.</p>
                    <a href="#sources">See source policy</a>
                </article>
            </div>
        </div>
    </section>

    <section class="section section--muted" id="states">
        <div class="container">
            <div class="section-heading">
                <span class="eyebrow">State guides</span>
                <h2>Ten state guides with different rule styles and permit paths.</h2>
                <p class="lede">Each guide explains the likely sizing logic, permit path, local verification steps, and the main reasons your final quote can move away from the low end.</p>
            </div>

            <div class="state-group">
                <h3 class="state-group__heading">Featured guides</h3>
                <div class="state-grid">
                    @for (var state : featuredStates)
                        <article class="state-card">
                            <div class="state-card__header">
                                <span class="pill">Featured</span>
                                <strong>${state.stateName()}</strong>
                            </div>
                            <p>${state.pageAngle()}</p>
                            <ul class="state-card__meta">
                                <li>${state.ruleType()}</li>
                                <li>${state.agencyName()}</li>
                                <li>Updated ${state.lastVerifiedAt()}</li>
                            </ul>
                            <a href="/septic-system-cost-calculator/${state.slug()}/">Open guide</a>
                        </article>
                    @endfor
                </div>
            </div>

            <div class="state-group">
                <h3 class="state-group__heading">More state guides</h3>
                <div class="state-grid">
                    @for (var state : additionalStates)
                        <article class="state-card">
                            <div class="state-card__header">
                                <span class="pill">State guide</span>
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
