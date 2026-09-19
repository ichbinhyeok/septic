@import com.example.septic.web.PageMeta
@param PageMeta page
@param int totalCountyRouteCount = 0

@template.layouts.app(page = page, designFamily = "directory", content = @`
    <section class="record-finder-hero">
        <div class="container">
            @template.tags.addressRecordFinder(
                finderId = "record-finder",
                eyebrow = "Address record finder",
                heading = "Paste the address. Open the right septic records route.",
                intro = "We resolve the property county, then show the best available county or state route and how deeply that route has been verified.",
                totalCountyRouteCount = totalCountyRouteCount,
                pageHeading = true
            )
        </div>
    </section>

    <div class="container record-evidence-band">
        @template.tags.recordEvidence(sourceContext = "record_finder_evidence")
    </div>

    <section class="section section--tight">
        <div class="container record-finder-steps">
            <article>
                <span>01</span>
                <h2>Resolve the county</h2>
                <p>Use a full U.S. address so the next click starts with the office most likely to own the parcel file.</p>
            </article>
            <article>
                <span>02</span>
                <h2>Open the file path</h2>
                <p>Open the county route first, then check its depth label. Some routes have county-specific preparation; others confirm only an official place to start.</p>
            </article>
            <article>
                <span>03</span>
                <h2>Request the artifact</h2>
                <p>Pull the permit copy, as-built, final approval, repair file, inspection letter, or written no-record response before pricing.</p>
            </article>
        </div>
    </section>

    <section class="section" aria-labelledby="finder-reading-title">
        <div class="container">
            <header class="section-heading">
                <span class="eyebrow">From search route to usable evidence</span>
                <h2 id="finder-reading-title">A route is not the record.</h2>
                <p>The public link only identifies where to start. A useful property answer comes from matching the right file, reading what it confirms, and keeping its limits visible.</p>
            </header>

            <div class="detail-grid">
                <article class="detail-card">
                    <span>THE FILE MAY CONFIRM</span>
                    <h3>What the file can answer</h3>
                    <ul>
                        <li>Permit or approval history tied to the property</li>
                        <li>Approved bedroom count or design flow</li>
                        <li>Tank, drain field, or reserve-area clues shown on a drawing</li>
                        <li>Final approval, inspection, repair, or replacement entries</li>
                    </ul>
                </article>
                <article class="detail-card">
                    <span>KEEP THE BOUNDARY CLEAR</span>
                    <h3>What a historical record cannot prove</h3>
                    <ul>
                        <li>The system's present condition or remaining life</li>
                        <li>An exact field location when no installed drawing exists</li>
                        <li>That the installed system still matches the old approval</li>
                        <li>That a blank portal result means no record exists</li>
                    </ul>
                </article>
                <article class="detail-card">
                    <span>WHEN THE ONLINE TRAIL STOPS</span>
                    <h3>An empty search is not an official no-record finding</h3>
                    <p>Older files may be indexed by a prior owner, subdivision, permit number, or tax-map reference. A blocked viewer is an access failure. A written office response is stronger evidence than a blank screen.</p>
                    <a href="/offer-prep-septic-file-check/#record-help"
                       data-record-help-cta
                       data-track-click="nav"
                       data-track-source-context="record_finder_explainer"
                       data-track-target-type="lead_form">Ask SepticPath to investigate the missing file →</a>
                </article>
            </div>
        </div>
    </section>

`)
