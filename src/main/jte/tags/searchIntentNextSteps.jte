@param String officialPath
@param String officialLabel
@param String sourceContext
@param String officialTargetType = "official_source"
@param boolean openOfficialInNewTab = false

<nav class="search-intent-next" aria-label="Choose the next septic record step">
    <div class="search-intent-next__intro">
        <span>Choose your situation</span>
        <strong>Leave with a next step, not another list of links.</strong>
    </div>
    <div class="search-intent-next__routes">
        @if (openOfficialInNewTab)
            <a href="${officialPath}"
               target="_blank"
               rel="noreferrer"
               data-track-click="nav"
               data-track-source-context="${sourceContext}_original"
               data-track-target-type="${officialTargetType}">
                <small>01 · I need the original record</small>
                <strong>${officialLabel}</strong>
                <span aria-hidden="true">→</span>
            </a>
        @else
            <a href="${officialPath}"
               data-track-click="nav"
               data-track-source-context="${sourceContext}_original"
               data-track-target-type="${officialTargetType}">
                <small>01 · I need the original record</small>
                <strong>${officialLabel}</strong>
                <span aria-hidden="true">→</span>
            </a>
        @endif
        <a href="/septic-record-finder/?mode=missing"
           data-track-click="nav"
           data-track-source-context="${sourceContext}_missing"
           data-track-target-type="record_finder">
            <small>02 · My search returned nothing</small>
            <strong>Resolve the failed search</strong>
            <span aria-hidden="true">→</span>
        </a>
        <a href="/septic-record-finder/?mode=document"
           data-track-click="nav"
           data-track-source-context="${sourceContext}_document"
           data-track-target-type="document_review">
            <small>03 · I already have a file</small>
            <strong>Check what the document proves</strong>
            <span aria-hidden="true">→</span>
        </a>
        <a href="/offer-prep-septic-file-check/#record-help"
           data-record-help-cta
           data-track-click="nav"
           data-track-source-context="${sourceContext}_human"
           data-track-target-type="lead_form">
            <small>04 · I want someone to handle it</small>
            <strong>Ask SepticPath to investigate</strong>
            <span aria-hidden="true">→</span>
        </a>
    </div>
</nav>
