@import com.example.septic.data.model.SourceRecord
@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.PageMeta
@param PageMeta page
@param StateProfile state
@param java.util.List<SourceRecord> sources
@param java.util.List<com.example.septic.data.model.StateMoneyPage> stateMoneyPages = java.util.List.of()

@template.layouts.app(page = page, content = @`
    <section class="section section--heroish">
        <div class="container state-hero">
            <div>
                <span class="eyebrow">${state.stateCode()} state guide</span>
                <h1>${state.stateName()} septic planning guide</h1>
                <p class="lede">${state.ruleSummaryPlainEnglish()}</p>
            </div>
            <div class="hero-sidecard">
                <span class="pill">${state.launchTier()}</span>
                <strong>${state.agencyName()}</strong>
                <strong>${state.ruleType()}</strong>
                <p>Confidence: ${state.confidenceScore()} | Last verified: ${state.lastVerifiedAt()}</p>
                <a class="button button--secondary" href="/septic-system-cost-calculator/?state=${state.stateCode()}">Use the main calculator</a>
            </div>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container detail-grid">
            <article class="detail-card">
                <h2>Why this state is unique</h2>
                <p>${state.pageAngle()}</p>
            </article>

            <article class="detail-card">
                <h2>Permit path summary</h2>
                <p>${state.permitSummary()}</p>
            </article>

            <article class="detail-card">
                <h2>Site evaluation summary</h2>
                <p>${state.siteEvalSummary()}</p>
            </article>

            <article class="detail-card detail-card--full">
                <h2>Local override note</h2>
                <p>${state.localOverrideNote()} Override risk: ${state.countyOverrideRisk()}.</p>
            </article>
        </div>
    </section>

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

    <section class="section">
        <div class="container content-grid">
            <article class="trust-box trust-box--large">
                <span class="trust-box__label">Official sources</span>
                <ul class="source-list">
                    @for (var source : sources)
                        <li>
                            <strong>${source.agencyName()}</strong>
                            <a href="${source.url()}" target="_blank" rel="noreferrer">${source.title()}</a>
                            <span>Last verified: ${source.lastVerifiedAt()}</span>
                        </li>
                    @endfor
                </ul>
            </article>

            @if (!stateMoneyPages.isEmpty())
                <article class="detail-card">
                    <h2>Next money pages</h2>
                    <ul class="detail-list">
                        @for (var stateMoneyPage : stateMoneyPages)
                            <li><a href="${stateMoneyPage.path(state.slug())}">${stateMoneyPage.title()}</a></li>
                        @endfor
                        <li><a href="/septic-system-cost-calculator/?state=${state.stateCode()}">Main septic cost calculator</a></li>
                    </ul>
                </article>
            @endif

            <article class="detail-card">
                <h2>Still under review</h2>
                <ul class="detail-list">
                    @for (var item : state.itemsNeedingVerification())
                        <li>${item}</li>
                    @endfor
                </ul>
            </article>
        </div>
    </section>
`)
