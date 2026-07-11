@import com.example.septic.web.PageMeta
@import com.example.septic.web.RecordsAccessIndexStateView
@param PageMeta page
@param java.util.List<RecordsAccessIndexStateView> indexStates = java.util.List.of()
@param int priorityCountyRouteCount = 0
@param int totalCountyRouteCount = 0

@template.layouts.app(page = page, content = @`
    <section class="records-access-index-hero">
        <div class="container">
            <div class="records-access-index-hero__intro">
                <span class="eyebrow">Public records infrastructure</span>
                <h1>Find the septic file path before the transaction moves on.</h1>
                <p>One public index for Tennessee, Indiana, North Carolina, and South Carolina. Start from an address, then move into the county or state office that actually owns the permit, as-built, approval, or written no-record response.</p>
                <div class="records-access-index-hero__signals" aria-label="Records access index coverage">
                    <span><strong>4</strong> priority states</span>
                    <span><strong>${priorityCountyRouteCount}</strong> priority county routes</span>
                    <span>Addresses are not saved</span>
                </div>
            </div>
            @template.tags.addressRecordFinder(
                finderId = "records-access-index",
                eyebrow = "Address-to-record route",
                heading = "Search by property address.",
                intro = "Have the address? We will resolve the county and open the closest verified record route. No address? Use county search instead.",
                totalCountyRouteCount = totalCountyRouteCount,
                minimal = true
            )
        </div>
    </section>

    <section class="section records-access-index-section" aria-labelledby="records-access-states">
        <div class="container">
            <div class="section-heading">
                <span class="eyebrow">Choose the jurisdiction</span>
                <h2 id="records-access-states">A real next click for each state.</h2>
                <p>Every route starts with the public office or record owner, not a generic septic explainer.</p>
            </div>
            <div class="records-access-index-grid">
                @for (var state : indexStates)
                    <article class="records-access-card">
                        <div class="records-access-card__topline">
                            <span>${state.stateCode()}</span>
                            <strong>${state.stateName()}</strong>
                        </div>
                        <p>${state.firstStep()}</p>
                        <div class="records-access-card__metrics" aria-label="${state.stateName()} coverage">
                            <span><strong>${state.countyRouteCount()}</strong> county routes</span>
                            <span><strong>${state.highConfidenceRouteCount()}</strong> high-confidence</span>
                            @if (state.directOnlineRouteCount() > 0)
                                <span><strong>${state.directOnlineRouteCount()}</strong> direct online</span>
                            @endif
                        </div>
                        <div class="records-access-card__actions">
                            <a class="button button--primary" href="${state.recordsPath()}" data-track-click="nav" data-track-source-context="records_access_index_state_route" data-track-target-type="state_records_page">Open ${state.stateName()} records</a>
                            <a class="button button--secondary" href="${state.packetPath()}" data-track-click="nav" data-track-source-context="records_access_index_packet" data-track-target-type="workflow_packet">${state.packetLabel()}</a>
                        </div>
                    </article>
                @endfor
            </div>
        </div>
    </section>

`)
