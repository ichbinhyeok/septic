@import com.example.septic.data.model.SourceRecord
@import com.example.septic.data.model.StateMoneyPage
@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.PageMeta
@param PageMeta page
@param StateMoneyPage stateMoneyPage
@param StateProfile state
@param java.util.List<SourceRecord> sources

@template.layouts.app(page = page, content = @`
    <section class="section section--heroish">
        <div class="container state-hero">
            <div>
                <span class="eyebrow">${state.stateCode()} money page</span>
                <h1>${stateMoneyPage.title()}</h1>
                <p class="lede">${stateMoneyPage.introCopy()}</p>
                <div class="hero__actions">
                    <a class="button button--primary" href="/septic-system-cost-calculator/?state=${state.stateCode()}&projectType=${stateMoneyPage.calculatorProjectType()}">Open the main calculator</a>
                    <a class="button button--secondary" href="/septic-system-cost-calculator/${state.slug()}/">Open the ${state.stateName()} guide</a>
                </div>
            </div>

            <article class="hero-sidecard">
                <span class="pill">${stateMoneyPage.contentSlug()}</span>
                <strong>${state.agencyName()}</strong>
                <strong>${state.ruleType()}</strong>
                <p>Last verified: ${state.lastVerifiedAt()}</p>
                <p>${stateMoneyPage.uniqueAngle()}</p>
            </article>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container detail-grid">
            <article class="detail-card">
                <h2>What changes this page in ${state.stateName()}</h2>
                <p>${stateMoneyPage.uniqueAngle()}</p>
            </article>

            <article class="detail-card">
                <h2>Permit path summary</h2>
                <p>${state.permitSummary()}</p>
            </article>

            <article class="detail-card detail-card--full">
                <h2>Main estimate drivers in ${state.stateName()}</h2>
                <ul class="detail-list">
                    @for (var driver : stateMoneyPage.driverBullets())
                        <li>${driver}</li>
                    @endfor
                </ul>
            </article>
        </div>
    </section>

    <section class="section">
        <div class="container detail-grid">
            @if (state.whoToCallFirst() != null)
                <article class="detail-card">
                    <h2>Who to call first</h2>
                    <p>${state.whoToCallFirst()}</p>
                </article>
            @endif

            @if (state.recordsToRequest() != null && !state.recordsToRequest().isEmpty())
                <article class="detail-card">
                    <h2>Records to request</h2>
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
                        <li>${state.localOverrideNote()}</li>
                    </ul>
                </article>
            @endif

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
                <article class="detail-card detail-card--full">
                    <h2>Special state wrinkle</h2>
                    <p>${state.specialAreaNote()}</p>
                </article>
            @endif

            <article class="trust-box">
                <span class="trust-box__label">Official-source context</span>
                <p><strong>${state.agencyName()}</strong> and related official materials support this page. Final design, permit path, and approval still need local verification.</p>
                <ul class="source-list source-list--compact">
                    @for (var source : sources)
                        <li>
                            <strong>${source.agencyName()}</strong>
                            <a href="${source.url()}" target="_blank" rel="noreferrer">${source.title()}</a>
                            <span>Last verified: ${source.lastVerifiedAt()}</span>
                        </li>
                    @endfor
                </ul>
            </article>
        </div>
    </section>

    <section class="section section--muted" id="faq">
        <div class="container">
            <div class="section-heading">
                <span class="eyebrow">FAQ</span>
                <h2>${state.stateName()} questions this page should answer before a quote request.</h2>
            </div>

            <div class="detail-grid">
                @for (var faq : stateMoneyPage.faqBlocks())
                    <article class="detail-card">
                        <h3>${faq.question()}</h3>
                        <p>${faq.answer()}</p>
                    </article>
                @endfor
            </div>
        </div>
    </section>

    <section class="section">
        <div class="container content-grid">
            <article class="trust-box trust-box--large">
                <span class="trust-box__label">Next best action</span>
                <h2>Run the calculator with your state and project assumptions, then move into the quote form.</h2>
                <p>The calculator result already shows the likely tank band, system class, cost range, and state-specific rule context.</p>
                <a href="/septic-system-cost-calculator/?state=${state.stateCode()}&projectType=${stateMoneyPage.calculatorProjectType()}">Run the estimate</a>
            </article>

            <article class="detail-card">
                <h2>Related links</h2>
                <ul class="detail-list">
                    @for (var target : stateMoneyPage.internalLinkTargets())
                        <li><a href="${target}">${target}</a></li>
                    @endfor
                </ul>
            </article>
        </div>
    </section>
`)
