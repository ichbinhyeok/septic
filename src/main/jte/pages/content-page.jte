@import com.example.septic.data.model.ContentPage
@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.PageMeta
@param PageMeta page
@param ContentPage contentPage
@param java.util.List<StateProfile> states
@param java.util.List<com.example.septic.web.StateMoneyPageLink> stateMoneyPageLinks = java.util.List.of()
@param String calculatorPath = "/septic-system-cost-calculator/"

@template.layouts.app(page = page, content = @`
    <section class="section section--heroish">
        <div class="container state-hero">
            <div>
                <span class="eyebrow">${contentPage.intentType()}</span>
                <h1>${contentPage.title()}</h1>
                <p class="lede">${contentPage.introCopy()}</p>
                <div class="hero__actions">
                    <a class="button button--primary" href="${calculatorPath}">Open the estimator</a>
                    <a class="button button--secondary" href="#faq">Jump to FAQs</a>
                </div>
            </div>

            <article class="hero-sidecard">
                <span class="pill">${contentPage.calculatorModule()}</span>
                <strong>${contentPage.primaryKeyword()}</strong>
                <p>This page exists to support cost-first search intent, then move visitors into the calculator and quote flow.</p>
            </article>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container detail-grid">
            <article class="detail-card detail-card--full">
                <h2>Main estimate drivers</h2>
                <ul class="detail-list">
                    @for (var driver : contentPage.driverBullets())
                        <li>${driver}</li>
                    @endfor
                </ul>
            </article>
        </div>
    </section>

    <section class="section">
        <div class="container content-grid">
            <article class="trust-box trust-box--large">
                <span class="trust-box__label">Next best action</span>
                <h2>Use the calculator with your state, bedroom count, and site assumptions before you ask for quotes.</h2>
                <p>The result page explains what pushed the estimate and what could still move it. That tends to create better conversations with local pros.</p>
                <a href="${calculatorPath}">Run the estimate</a>
            </article>

            <article class="detail-card">
                <h2>Launch states</h2>
                <ul class="detail-list">
                    @for (var state : states)
                        <li><a href="/septic-system-cost-calculator/${state.slug()}/">${state.stateName()} septic guide</a></li>
                    @endfor
                </ul>
            </article>
        </div>
    </section>

    @if (!stateMoneyPageLinks.isEmpty())
        <section class="section section--tight">
            <div class="container">
                <article class="detail-card detail-card--full">
                    <h2>State-specific pages already live</h2>
                    <ul class="detail-list">
                        @for (var stateMoneyPageLink : stateMoneyPageLinks)
                            <li><a href="${stateMoneyPageLink.path()}">${stateMoneyPageLink.title()}</a></li>
                        @endfor
                    </ul>
                </article>
            </div>
        </section>
    @endif

    <section class="section section--muted" id="faq">
        <div class="container">
            <div class="section-heading">
                <span class="eyebrow">FAQ</span>
                <h2>Questions this page should answer before the user clicks deeper.</h2>
            </div>

            <div class="detail-grid">
                @for (var faq : contentPage.faqBlocks())
                    <article class="detail-card">
                        <h3>${faq.question()}</h3>
                        <p>${faq.answer()}</p>
                    </article>
                @endfor
            </div>
        </div>
    </section>

    <section class="section">
        <div class="container">
            <article class="trust-box trust-box--large">
                <span class="trust-box__label">Internal links</span>
                <ul class="detail-list">
                    @for (var target : contentPage.internalLinkTargets())
                        <li><a href="${target}">${target}</a></li>
                    @endfor
                </ul>
            </article>
        </div>
    </section>
`)
