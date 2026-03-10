@import com.example.septic.web.PageMeta
@import com.example.septic.web.PageLink
@import com.example.septic.web.StateQueuePlanView
@param PageMeta page
@param String stateCode
@param String stateName
@param StateQueuePlanView queuePlan = null
@param java.util.List<PageLink> starterLinks = java.util.List.of()
@param java.util.List<PageLink> benchmarkGuides = java.util.List.of()
@param int queuedStateCount = 0

@template.layouts.app(page = page, content = @`
    <section class="section section--heroish">
        <div class="container state-hero">
            <div>
                <span class="eyebrow">${stateCode} research queue</span>
                <h1>${stateName} septic guide is in the research queue.</h1>
                <p class="lede">This state URL is reserved now so homeowners have a real next-step landing page before the official-source ${stateName} guide is strong enough to publish.</p>
                <div class="hero__actions">
                    <a class="button button--primary" href="/septic-system-cost-calculator/?state=${stateCode}">Use the ${stateName} estimate</a>
                    <a class="button button--secondary" href="/states/">Browse all state coverage</a>
                </div>
            </div>

            <article class="hero-sidecard">
                <span class="pill pill--muted">Research queue</span>
                <strong>Why this page exists</strong>
                <ul class="detail-list">
                    <li>Keep a stable ${stateName} state URL live while research catches up.</li>
                    <li>Route homeowners into the right permit, records, buyer-risk, and replacement pages now.</li>
                    <li>Avoid publishing a thin state-name swap before the source set is defensible.</li>
                </ul>
                <p>${queuedStateCount} states are still moving through this queue.</p>
            </article>
        </div>
    </section>

    @if (queuePlan != null)
        <section class="section section--tight">
            <div class="container content-grid">
                <article class="trust-box trust-box--large">
                    <span class="trust-box__label">${queuePlan.rolloutWave()} priority #${queuePlan.priorityRank()}</span>
                    <h2>Why ${stateName} moves early</h2>
                    <p>${queuePlan.whyNow()}</p>
                    <p><strong>Target angle:</strong> ${queuePlan.launchAngle()}</p>
                    <a href="${queuePlan.recommendedLink().path()}">${queuePlan.recommendedLink().title()}</a>
                    <p>${queuePlan.recommendedLink().note()}</p>
                </article>

                <article class="detail-card">
                    <h2>First source pack to build</h2>
                    <ul class="detail-list">
                        @for (var task : queuePlan.researchTasks())
                            <li>${task}</li>
                        @endfor
                    </ul>
                </article>
            </div>
        </section>
    @endif

    <section class="section section--tight">
        <div class="container">
            <article class="trust-box trust-box--large">
                <span class="trust-box__label">Use this state path now</span>
                <h2>Start with the workflow pages that narrow the next call fastest.</h2>
                <p>Until the live ${stateName} guide is published, the pages below do more practical work than a fake state-specific summary would. Use them to decide whether the real blocker is permit path, missing records, buyer risk, replacement scope, or perc uncertainty.</p>
            </article>

            <div class="detail-grid">
                @for (var link : starterLinks)
                    <article class="detail-card">
                        <h3><a href="${link.path()}">${link.title()}</a></h3>
                        <p>${link.note()}</p>
                    </article>
                @endfor
            </div>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container content-grid">
            <article class="detail-card">
                <h2>What unlocks a live ${stateName} guide</h2>
                <ul class="detail-list">
                    <li>A verified official-source set for statewide homeowner rule language.</li>
                    <li>A real local authority path that tells homeowners which office to call first.</li>
                    <li>A usable records path for permits, as-builts, inspections, or repair history.</li>
                    <li>At least one state-specific wrinkle that changes the homeowner workflow in practice.</li>
                </ul>
            </article>

            <article class="detail-card">
                <h2>What this page avoids on purpose</h2>
                <ul class="detail-list">
                    <li>No invented permit steps that have not been tied to an official source.</li>
                    <li>No fake county summary pretending local offices all work the same way.</li>
                    <li>No precise state claim where the current research file is still too thin.</li>
                </ul>
            </article>
        </div>
    </section>

    <section class="section section--muted">
        <div class="container">
            <div class="section-heading">
                <span class="eyebrow">Publication standard</span>
                <h2>These live guides show what the finished state pages need to look like.</h2>
                <p class="lede">The point of the queue is to avoid shipping a weak ${stateName} page just because the slug is available. These live guides are the current bar.</p>
            </div>

            <div class="detail-grid">
                @for (var guide : benchmarkGuides)
                    <article class="detail-card">
                        <h3><a href="${guide.path()}">${guide.title()}</a></h3>
                        <p>${guide.note()}</p>
                    </article>
                @endfor
            </div>
        </div>
    </section>
`)
