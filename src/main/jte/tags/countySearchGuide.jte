@param com.example.septic.web.CountySearchGuideView entry

<section class="section section--tight" id="record-search-instructions" aria-labelledby="record-search-heading">
    <div class="container record-search-guide">
        <header>
            <span class="eyebrow">${entry.countyName()}, ${entry.stateCode()} · Existing records</span>
            <h2 id="record-search-heading">How to find the permit and its attachments</h2>
            <p>${entry.guide().summary()}</p>
        </header>
        <div class="record-search-guide__body">
            <dl class="record-search-facts">
                <div><dt>Years covered</dt><dd>${entry.guide().coverage()}</dd></div>
                <div><dt>Search with</dt><dd>${entry.guide().identifiers()}</dd></div>
                <div><dt>What you can obtain</dt><dd>${entry.guide().documents()}</dd></div>
            </dl>
            <div>
                <h3>Follow the county's search path</h3>
                <ol class="record-search-steps">
                    @for (var step : entry.guide().steps())
                        <li>${step}</li>
                    @endfor
                </ol>
                <p class="record-search-fallback"><strong>If no usable file appears:</strong> ${entry.guide().noResult()}</p>
            </div>
        </div>
        <footer class="record-search-sources">
            <span>Instructions reviewed ${entry.guide().reviewedAt()} · Official sources:</span>
            @for (var source : entry.sources())
                <a href="${source.url()}" target="_blank" rel="noreferrer" data-track-click="nav" data-track-source-context="county_search_instructions_source" data-track-target-type="official_source">${source.title()}</a>
            @endfor
        </footer>
        <div class="record-search-next">
            <a href="/septic-as-built-records/#read-a-septic-drawing">See how to read a returned drawing</a>
            <a href="/offer-prep-septic-file-check/#record-help" data-record-help-cta data-track-click="nav" data-track-source-context="county_search_instructions_help" data-track-target-type="lead_form">Ask SepticPath to investigate this property</a>
        </div>
    </div>
</section>
