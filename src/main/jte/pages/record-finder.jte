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
                intro = "We resolve the property county, then send you to the best verified county records page or state records path already in SepticPath.",
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
                <p>When a verified county route exists, open it first. Otherwise use the state records path with the same address and parcel clues.</p>
            </article>
            <article>
                <span>03</span>
                <h2>Request the artifact</h2>
                <p>Pull the permit copy, as-built, final approval, repair file, inspection letter, or written no-record response before pricing.</p>
            </article>
        </div>
    </section>
`)
