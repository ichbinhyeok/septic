@import com.example.septic.web.PageMeta
@import com.example.septic.web.EventAnalyticsReport
@import com.example.septic.web.EventAnalyticsRow
@param PageMeta page
@param EventAnalyticsReport report

@template.layouts.app(page = page, content = @`
    <div class="ops-report-page">
        <section class="section section--heroish">
            <div class="container ops-report-hero">
                <div>
                    <span class="eyebrow">Private operations report</span>
                    <h1>Behavior signals, not vanity totals.</h1>
                    <p class="lede">This report shows only anonymous action events from the last 7 and 28 days. Use it to find the pages and CTAs that make visitors open an official source or keep a usable records packet.</p>
                </div>
                <aside class="hero-sidecard ops-report-sidecard">
                    <span class="pill">Generated</span>
                    <strong>${report.generatedAt()}</strong>
                    <p>Official-source clicks, artifact actions, and internal routing are counted separately. Web vitals and lead payloads are excluded.</p>
                </aside>
            </div>
        </section>

        @if (!report.storageReadable())
            <section class="section section--tight">
                <div class="container">
                    <p class="ops-report-alert">Some event files could not be read. The visible totals use the files that were available.</p>
                </div>
            </section>
        @endif

        <section class="section section--tight">
            <div class="container ops-report-metrics" aria-label="Event action totals">
                <article>
                    <span>Tracked actions, 7d</span>
                    <strong>${report.trackedActionsLastSevenDays()}</strong>
                    <p>${report.trackedActionsLastTwentyEightDays()} in the last 28 days</p>
                </article>
                <article>
                    <span>Official source clicks</span>
                    <strong>${report.officialSourceClicksLastSevenDays()}</strong>
                    <p>Direct public agency or county search exits, 7d</p>
                </article>
                <article>
                    <span>Artifact actions</span>
                    <strong>${report.artifactActionsLastSevenDays()}</strong>
                    <p>Generated, copied, downloaded, or PDF-opened, 7d</p>
                </article>
                <article>
                    <span>Internal route clicks</span>
                    <strong>${report.internalNavigationClicksLastSevenDays()}</strong>
                    <p>Page-to-page workflow handoffs, 7d</p>
                </article>
            </div>
        </section>

        @if (!report.hasTrackedActions())
            <section class="section">
                <div class="container">
                    <div class="ops-report-empty">
                        <span class="eyebrow">Awaiting first signals</span>
                        <h2>The instrumentation is live. Let real visitors choose the next improvement.</h2>
                        <p>Do not infer a winner yet. Revisit after a full 7-day window, then compare the rows below by official-source clicks and artifact completion rather than by page count.</p>
                    </div>
                </div>
            </section>
        @endif

        <section class="section">
            <div class="container ops-report-stack">
                <div class="section-heading">
                    <span class="eyebrow">Outcome queue</span>
                    <h2>What visitors actually completed.</h2>
                    <p class="lede">A page with both an official-source click and a packet action is a stronger expansion candidate than a page with only impressions.</p>
                </div>
                @template.tags.opsEventTable(
                    heading = "Official source clicks",
                    description = "Which source-backed CTAs send visitors into a real public search or agency path.",
                    rows = report.officialSourceClicks(),
                    emptyLabel = "No official-source clicks in the last 28 days."
                )
                @template.tags.opsEventTable(
                    heading = "Artifact actions",
                    description = "Which records packets or field notes visitors considered worth keeping.",
                    rows = report.artifactActions(),
                    emptyLabel = "No artifact actions in the last 28 days."
                )
                @template.tags.opsEventTable(
                    heading = "Internal route clicks",
                    description = "Which handoffs keep users moving deeper into the useful local workflow.",
                    rows = report.internalNavigationClicks(),
                    emptyLabel = "No internal route clicks in the last 28 days."
                )
            </div>
        </section>
    </div>
`)
