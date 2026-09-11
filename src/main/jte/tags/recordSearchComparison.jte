@param java.util.List<com.example.septic.web.CountySearchGuideView> entries
@param String heading = "Compare what the county search actually provides"

@if (!entries.isEmpty())
    <section class="section section--tight" id="record-search-comparison" aria-labelledby="record-search-comparison-heading">
        <div class="container record-search-comparison">
            <header>
                <span class="eyebrow">Before you search</span>
                <h2 id="record-search-comparison-heading">${heading}</h2>
                <p>Detailed search instructions are available for these ${entries.size()} counties. Other counties remain in the county directory; an unlisted date range means we have not established it from the reviewed source.</p>
            </header>
            <div class="record-search-comparison__rows">
                @for (var entry : entries)
                    <article class="record-search-comparison__row">
                        <div>
                            <span class="eyebrow">${entry.stateCode()}</span>
                            <h3><a href="${entry.countyPath()}#record-search-instructions" data-track-click="nav" data-track-source-context="records_comparison_county" data-track-target-type="county_records_page">${entry.countyName()} septic records</a></h3>
                            <p>${entry.guide().summary()}</p>
                            <small>Reviewed ${entry.guide().reviewedAt()}</small>
                        </div>
                        <dl class="record-search-facts">
                            <div><dt>Years covered</dt><dd>${entry.guide().coverage()}</dd></div>
                            <div><dt>Search with</dt><dd>${entry.guide().identifiers()}</dd></div>
                            <div><dt>Documents</dt><dd>${entry.guide().documents()}</dd></div>
                        </dl>
                        <div class="record-search-comparison__fallback">
                            <strong>If the file is missing</strong>
                            <p>${entry.guide().noResult()}</p>
                            <details>
                                <summary>Official sources</summary>
                                @for (var source : entry.sources())
                                    <a href="${source.url()}" target="_blank" rel="noreferrer">${source.title()}</a>
                                @endfor
                            </details>
                        </div>
                    </article>
                @endfor
            </div>
            <p class="record-search-comparison__note">Published search coverage describes the archive, not whether a particular property has a permit. A map result, permit entry, or construction plan alone does not establish current system condition.</p>
        </div>
    </section>
@endif
