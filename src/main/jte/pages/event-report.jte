@import com.example.septic.web.PageMeta
@import com.example.septic.web.EventAnalyticsReport
@import com.example.septic.web.EventAnalyticsRow
@import com.example.septic.web.CountyWorkflowFunnelRow
@import com.example.septic.web.WorkflowFunnelRow
@param PageMeta page
@param EventAnalyticsReport report

@template.layouts.app(page = page, content = @`
    <div class="ops-report-page">
        <section class="section section--heroish">
            <div class="container ops-report-hero">
                <div>
                    <span class="eyebrow">Private operations report</span>
                    <h1>Behavior signals, not vanity totals.</h1>
                    <p class="lede">This report joins anonymous task stages from the last 7 and 28 days. It shows whether a browser task moved from preparation to an official route, returned with a result, and finished a usable property file.</p>
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

        <section class="section section--tight">
            <div class="container ops-workflow-funnel">
                <div class="section-heading">
                    <span class="eyebrow">Observed completion funnel</span>
                    <h2>Can a visitor finish the records job?</h2>
                    <p class="lede">${report.workflowTasksLastSevenDays()} anonymous browser ${report.workflowTasksLastSevenDays() == 1 ? "task" : "tasks"} started in the last 7 days; ${report.workflowTasksLastTwentyEightDays()} started in 28 days. Rates below use the 7-day start cohort and never include an address, parcel ID, document, or request text.</p>
                </div>
                <ol class="ops-workflow-funnel__steps">
                    @for(WorkflowFunnelRow row : report.workflowFunnel())
                        <li>
                            <span>${row.label()}</span>
                            <strong>${row.lastSevenDays()}</strong>
                            <small>${row.sevenDayRate()}% of 7-day starts · ${row.lastTwentyEightDays()} in 28d</small>
                            <div aria-hidden="true"><span style="width: ${row.sevenDayRate()}%"></span></div>
                        </li>
                    @endfor
                </ol>
                <p class="ops-workflow-funnel__note">A route opening is not a successful record search. “Core file ready” requires the purpose-specific checklist to be filled without unresolved document conflicts; “finished” requires the visitor to confirm completion.</p>
            </div>
        </section>

        <section class="section section--tight">
            <div class="container ops-county-funnel">
                <div class="section-heading">
                    <span class="eyebrow">County completion comparison</span>
                    <h2>Fix the busiest broken step first.</h2>
                    <p class="lede">Each row joins anonymous stages by workflow ID and county for the last 28 days. Step rates use only tasks that reached the prior step. Fewer than five starts is marked too early, so one visitor cannot make a county look healthy or broken.</p>
                </div>
                @if (report.countyWorkflowFunnels().isEmpty())
                    <p class="ops-event-table-section__empty">No county-linked workflow has started in the last 28 days.</p>
                @else
                    <div class="ops-event-table-wrap">
                        <table class="ops-event-table ops-county-funnel__table">
                            <thead>
                                <tr>
                                    <th scope="col">County</th>
                                    <th scope="col">Starts</th>
                                    <th scope="col">Prepared</th>
                                    <th scope="col">Official</th>
                                    <th scope="col">Returned</th>
                                    <th scope="col">Upload handoff</th>
                                    <th scope="col">Reviewed</th>
                                    <th scope="col">File ready</th>
                                    <th scope="col">Evidence</th>
                                    <th scope="col">Next action</th>
                                </tr>
                            </thead>
                            <tbody>
                                @for (CountyWorkflowFunnelRow row : report.countyWorkflowFunnels())
                                    <tr>
                                        <td>
                                            <strong>${row.countyLabel()}</strong>
                                            <code>${row.countyKey()}</code>
                                        </td>
                                        <td>
                                            <strong>${row.startsLastTwentyEightDays()}</strong>
                                            <small>${row.startsLastSevenDays()} in 7d</small>
                                        </td>
                                        <td>${row.preparedLastTwentyEightDays()}</td>
                                        <td>
                                            <strong>${row.officialOpenedLastTwentyEightDays()}</strong>
                                            <small>${row.officialOpenRate()}%</small>
                                        </td>
                                        <td>
                                            <strong>${row.outcomesLastTwentyEightDays()}</strong>
                                            <small>${row.outcomeReturnRate()}%</small>
                                        </td>
                                        <td>
                                            <strong>${row.documentHandoffsLastTwentyEightDays()}</strong>
                                            <small>${row.documentHandoffRate()}%</small>
                                        </td>
                                        <td>
                                            <strong>${row.documentsReviewedLastTwentyEightDays()}</strong>
                                            <small>${row.documentReviewRate()}%</small>
                                        </td>
                                        <td>
                                            <strong>${row.propertyFilesReadyLastTwentyEightDays()}</strong>
                                            <small>${row.propertyFileReadyRate()}%</small>
                                        </td>
                                        <td><span class="ops-county-funnel__evidence">${row.evidenceLabel()}</span></td>
                                        <td class="ops-county-funnel__action">${row.nextAction()}</td>
                                    </tr>
                                @endfor
                            </tbody>
                        </table>
                    </div>
                    <p class="ops-workflow-funnel__note">The 50%, 25%, and 50% cutoffs behind “Next action” are investigation triggers, not industry benchmarks. They decide where to inspect first; they do not declare a county successful.</p>
                @endif
            </div>
        </section>

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
