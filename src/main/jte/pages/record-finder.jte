@import com.example.septic.web.PageMeta
@param PageMeta page
@param int totalCountyRouteCount = 0

@template.layouts.app(page = page, content = @`
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

`)
