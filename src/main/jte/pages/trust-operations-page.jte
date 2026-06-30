@import com.example.septic.web.PageMeta
@import com.example.septic.web.TrustOperationsPageView
@param PageMeta page
@param TrustOperationsPageView operationsPage

@template.layouts.app(page = page, content = @`
    <div class="trust-ops-page">
        <section class="section section--heroish">
            <div class="container state-hero">
                <div>
                    <span class="eyebrow">${operationsPage.eyebrow()}</span>
                    <h1>${operationsPage.heading()}</h1>
                    <p class="lede">${operationsPage.intro()}</p>
                    @if (!operationsPage.nextLinks().isEmpty())
                        <div class="quick-link-strip" aria-label="Related trust pages">
                            <span class="quick-link-strip__label">Trust surface</span>
                            @for (var link : operationsPage.nextLinks())
                                <a class="button button--secondary quick-link-strip__button"
                                   href="${link.path()}"
                                   data-track-click="nav"
                                   data-track-source-context="trust_operations_hero"
                                   data-track-target-type="trust_page">${link.title()}</a>
                            @endfor
                        </div>
                    @endif
                </div>

                <aside class="hero-sidecard trust-ops-sidecard">
                    <span class="pill">${operationsPage.sideLabel()}</span>
                    <strong>${operationsPage.sideValue()}</strong>
                    <p>${operationsPage.sideBody()}</p>
                </aside>
            </div>
        </section>

        @if (!operationsPage.metrics().isEmpty())
            <section class="section section--tight">
                <div class="container trust-ops-metrics" aria-label="Trust operation metrics">
                    @for (var metric : operationsPage.metrics())
                        <article class="trust-ops-metric">
                            <span>${metric.label()}</span>
                            <strong>${metric.value()}</strong>
                            <p>${metric.note()}</p>
                        </article>
                    @endfor
                </div>
            </section>
        @endif

        @if (!operationsPage.lanes().isEmpty())
            <section class="section">
                <div class="container">
                    <div class="section-heading">
                        <span class="eyebrow">Operating rules</span>
                        <h2>The bar is task completion, not page count.</h2>
                    </div>
                    <div class="trust-ops-lanes">
                        @for (var lane : operationsPage.lanes())
                            <article class="trust-ops-lane">
                                <span>${lane.label()}</span>
                                <h3>${lane.title()}</h3>
                                <p>${lane.body()}</p>
                                @if (lane.linkPath() != null && !lane.linkPath().isBlank())
                                    <a href="${lane.linkPath()}"
                                       data-track-click="nav"
                                       data-track-source-context="trust_operations_lane"
                                       data-track-target-type="trust_related">${lane.linkLabel()}</a>
                                @endif
                            </article>
                        @endfor
                    </div>
                </div>
            </section>
        @endif

        @if (!operationsPage.coverageRows().isEmpty())
            <section class="section section--muted">
                <div class="container">
                    <div class="section-heading">
                        <span class="eyebrow">Coverage board</span>
                        <h2>${operationsPage.rowSectionTitle()}</h2>
                        <p class="lede">${operationsPage.rowSectionIntro()}</p>
                    </div>
                    <div class="trust-ops-table-wrap">
                        <table class="trust-ops-table">
                            <thead>
                                <tr>
                                    <th scope="col">State</th>
                                    <th scope="col">Workflow pages</th>
                                    <th scope="col">County pages</th>
                                    <th scope="col">Source depth</th>
                                    <th scope="col">Confidence</th>
                                    <th scope="col">Last verified</th>
                                </tr>
                            </thead>
                            <tbody>
                                @for (var row : operationsPage.coverageRows())
                                    <tr>
                                        <td>
                                            <a href="${row.statePath()}"
                                               data-track-click="nav"
                                               data-track-source-context="trust_operations_coverage_row"
                                               data-track-target-type="state_page">${row.stateName()}</a>
                                            <span>${row.stateCode()}</span>
                                        </td>
                                        <td>${row.workflowPageCount()}</td>
                                        <td>${row.countyPageCount()}</td>
                                        <td>${row.sourceCountLabel()}</td>
                                        <td>${row.confidenceLabel()}</td>
                                        <td>${row.lastVerifiedAt()}</td>
                                    </tr>
                                @endfor
                            </tbody>
                        </table>
                    </div>
                </div>
            </section>
        @endif

        <section class="section">
            <div class="container content-grid">
                <article class="trust-box trust-box--large">
                    <span class="trust-box__label">Next publishing decision</span>
                    <h2>${operationsPage.ctaHeading()}</h2>
                    <p>${operationsPage.ctaBody()}</p>
                    @if (!operationsPage.nextLinks().isEmpty())
                        <div class="hero__actions">
                            @for (var link : operationsPage.nextLinks())
                                <a class="button button--secondary"
                                   href="${link.path()}"
                                   data-track-click="nav"
                                   data-track-source-context="trust_operations_cta"
                                   data-track-target-type="trust_page">${link.title()}</a>
                            @endfor
                        </div>
                    @endif
                </article>

                <article class="detail-card">
                    <h2>What this prevents</h2>
                    <ul class="detail-list">
                        <li>Publishing a page that only swaps the state or county name.</li>
                        <li>Letting an estimate outrank the permit file, county record, or local authority path.</li>
                        <li>Scaling pages faster than the source layer can support them.</li>
                    </ul>
                </article>
            </div>
        </section>
    </div>
`)
