@import com.example.septic.data.model.ContentPage
@import com.example.septic.data.model.CountyRecordsPage
@import com.example.septic.data.model.SourceRecord
@import com.example.septic.web.PageMeta
@param PageMeta page
@param ContentPage contentPage
@param java.util.List<CountyRecordsPage> onlineRmeCounties
@param java.util.Map<String, String> countyPaths
@param java.util.Map<String, java.util.List<SourceRecord>> countySources

@template.layouts.app(page = page, designFamily = "editorial", content = @`
    <main id="main-content" class="content-page content-page--onlinerme-septic-records">
        <section class="section section--heroish">
            <div class="container state-hero">
                <div>
                    <span class="eyebrow">Shared platform · county-specific records</span>
                    <h1>${contentPage.title()}</h1>
                    <p class="lede">${contentPage.introCopy()}</p>
                    <div class="hero__actions">
                        <a class="button button--primary" href="#county-instructions">Choose your county</a>
                        <a class="button button--secondary" href="/septic-record-finder/">Need help finding the record?</a>
                    </div>
                </div>
                <aside class="trust-box trust-box--large">
                    <span class="trust-box__label">The key distinction</span>
                    <h2>One platform does not mean one shared file.</h2>
                    <p>The county decides what appears, which property keys work, and what to do when a search is empty.</p>
                </aside>
            </div>
        </section>

        <section class="section section--tight" id="county-instructions">
            <div class="container">
                <div class="section-heading">
                    <span class="eyebrow">Official county instructions</span>
                    <h2>Choose the county that holds the property file.</h2>
                    <p class="lede">These county pages document materially different OnlineRME search scopes and missing-record paths. Use the official instructions for the property jurisdiction.</p>
                </div>
                <div class="detail-grid">
                    @for (var county : onlineRmeCounties)
                        <article class="detail-card">
                            <span class="pill">${county.stateCode()}</span>
                            <h3>${county.countyName()}, ${county.stateCode()}</h3>
                            @if ("WA::king-county".equals(county.key()))
                                <p>King County separates the collections: its septic-record search covers site designs, historic inspections, and as-built drawings; OnlineRME is the separate route for operation and maintenance records. Time-of-sale reports may carry updated drawings in Application History.</p>
                                <p><strong>If nothing appears:</strong> King County lists address/parcel changes and unscanned microfiche among possible causes and provides a direct as-built help route.</p>
                            @elseif ("WA::snohomish-county".equals(county.key()))
                                <p>Snohomish County says OnlineRME includes as-built drawings and service-history reports for current and past septic systems.</p>
                                <p><strong>If nothing appears:</strong> older systems may predate record requirements; the Health Department says additional information may still be on file and accepts records requests.</p>
                            @elseif ("VA::loudoun-county".equals(county.key()))
                                <p>Loudoun County recommends selecting Virginia and Loudoun County, then searching with the Tax ID (PIN plus 000) or street number. Its guidance warns that a street-number search may not include all files.</p>
                                <p><strong>If nothing appears:</strong> check the county's separate scanned-record and subdivision parent-file searches, then contact the Health Department.</p>
                            @endif
                            <nav class="policy-related" aria-label="${county.countyName()} official and SepticPath routes">
                                <a href="${county.recordsUrl()}" target="_blank" rel="noreferrer">${county.recordsLabel()} ↗</a>
                                <a href="${countyPaths.get(county.key())}">Open SepticPath's ${county.countyName()} route →</a>
                            </nav>
                            @if (countySources.containsKey(county.key()))
                                <details>
                                    <summary>Source notes</summary>
                                    <ul class="source-list source-list--compact">
                                        @for (var source : countySources.get(county.key()))
                                            <li><a href="${source.url()}" target="_blank" rel="noreferrer">${source.agencyName()}: ${source.title()}</a> · reviewed ${source.contentVerifiedAt()}</li>
                                        @endfor
                                    </ul>
                                </details>
                            @endif
                        </article>
                    @endfor
                </div>
            </div>
        </section>

        <section class="section section--muted" id="search-sequence">
            <div class="container">
                <div class="section-heading">
                    <span class="eyebrow">A reliable search sequence</span>
                    <h2>Match the county, the key, and the file.</h2>
                </div>
                <div class="detail-grid">
                    @for (var step : contentPage.decisionSteps())
                        <article class="detail-card"><p>${step}</p></article>
                    @endfor
                </div>
                <p class="lede">A historical record can help explain the property's file, but it does not establish current condition, exact present-day location, or later changes.</p>
            </div>
        </section>

        <section class="section section--tight" id="faq">
            <div class="container">
                <div class="section-heading"><span class="eyebrow">FAQ</span><h2>Before you treat a blank search as an answer.</h2></div>
                <div class="detail-grid">
                    @for (var faq : contentPage.faqBlocks())
                        <article class="detail-card"><h3>${faq.question()}</h3><p>${faq.answer()}</p></article>
                    @endfor
                </div>
                <p class="source-meta">Independent research guide. County procedures can change; confirm the current instructions with the official county source linked above.</p>
            </div>
        </section>
    </main>
`)
