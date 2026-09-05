@import com.example.septic.data.model.ContentPage
@import com.example.septic.web.PageMeta
@param PageMeta page
@param ContentPage contentPage
@param String contentQuickAnswer = ""
@param java.util.List<com.example.septic.web.PageLink> featuredInternalLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.CountyFinderLinkView> countyFinderLinks = java.util.List.of()
@param java.util.List<com.example.septic.data.model.StateProfile> states = java.util.List.of()
@param String editorialLastReviewedAt = ""
@param int totalCountyRouteCount = 0

@template.layouts.app(page = page, content = @`
    <div class="national-records-page">
        <section class="national-records-hero">
            <div class="container national-records-hero__inner">
                <div class="national-records-hero__copy">
                    <span class="eyebrow">Property record workflow</span>
                    <h1>${contentPage.title()}</h1>
                    <p>${contentQuickAnswer}</p>
                    <div class="national-records-hero__boundary" role="note">
                        <strong>What you receive here</strong>
                        <span>The county, likely file owner, verified official route, useful search fields, and the next step for found, missing, blocked, or incomplete files.</span>
                    </div>
                </div>
                @template.tags.addressRecordFinder(
                    finderId = "national-records-" + contentPage.slug(),
                    eyebrow = "Start with the property",
                    heading = "Enter the address to find the right office",
                    intro = "A full address resolves the county. SepticPath prepares the official handoff; it does not retrieve or certify the government record.",
                    totalCountyRouteCount = totalCountyRouteCount,
                    minimal = true
                )
            </div>
        </section>

        <section class="national-records-intent">
            <div class="container">
                <span class="eyebrow">Choose the answer you need</span>
                <h2>The record search is only the first step</h2>
                <div class="national-records-intent__rows">
                    <a href="/septic-permit-search-by-address/"><strong>Find the permit file</strong><span>Resolve county and official source</span></a>
                    <a href="/septic-as-built-records/"><strong>Find the installed layout</strong><span>Ask for as-built, site sketch, or approved plan</span></a>
                    @if (!"septic-tank-location-records".equals(contentPage.slug()))
                        <a href="/septic-tank-location-records/"><strong>Locate the tank and drain field</strong><span>Start with the record drawing before arranging a professional locate</span></a>
                    @endif
                    <a href="/septic-records-request-builder/"><strong>No record appeared</strong><span>Prepare a precise written request</span></a>
                    <a href="/offer-prep-septic-file-check/#record-help" data-record-help-cta data-track-click="nav" data-track-source-context="national_records_record_help" data-track-target-type="lead_form"><strong>Still can’t find the right file?</strong><span>Send the address and we’ll help identify the office and record to request</span></a>
                </div>
            </div>
        </section>

        <section class="national-records-method">
            <div class="container national-records-method__grid">
                <div>
                    <span class="eyebrow">A defensible search</span>
                    <h2>Carry more than the street address</h2>
                    <p>Government archives may be indexed under an earlier owner, parcel, subdivision, lot, legal description, or permit number. A failed address search is not proof that no file exists.</p>
                </div>
                <dl>
                    <div><dt>Search clues</dt><dd>Address, parcel/APN, current and prior owner, subdivision, lot, legal description, permit number.</dd></div>
                    <div><dt>Useful documents</dt><dd>Permit/application, soil or site evaluation, approved plan, as-built, final approval, repair history.</dd></div>
                    <div><dt>Safe fallback</dt><dd>Ask the file owner for a written no-record response and the next archive or delegated office to contact.</dd></div>
                </dl>
            </div>
        </section>

        @template.tags.countyFinder(
            countyFinderLinks = countyFinderLinks,
            stateOptions = states,
            finderId = contentPage.slug() + "-county-finder",
            eyebrow = "County finder",
            heading = "Search the county route instead of stopping at the national guide.",
            body = "Use this when you know the state or county but do not have a complete property address.",
            sourceContext = "national_records_county_finder",
            totalCountyRouteCount = totalCountyRouteCount
        )

        <section class="national-records-outcomes">
            <div class="container">
                <span class="eyebrow">After the handoff</span>
                <h2>Continue from the result you actually received</h2>
                <div>
                    <details><summary>I downloaded a permit or PDF</summary><p>Save the original. Compare the address and parcel, permit number, approved use or bedrooms, installed layout, final approval, and later repairs. Do not infer current condition from an old permit.</p></details>
                    <details><summary>The portal returned no result</summary><p>Retry with alternate identifiers, then contact the office. Only a written office response should be treated as a confirmed no-record result.</p></details>
                    <details><summary>The file exists but is incomplete</summary><p>Name the missing artifact and request it directly. If the installed layout or current condition still cannot be established, a professional locate or inspection may be the next step.</p></details>
                </div>
            </div>
        </section>

        @if (!featuredInternalLinks.isEmpty())
            <section class="national-records-related">
                <div class="container">
                    <span class="eyebrow">Related workflows</span>
                    <nav aria-label="Related septic records workflows">
                        @for (var link : featuredInternalLinks)
                            <a href="${link.path()}"><strong>${link.compactTitle()}</strong><span>Open workflow</span></a>
                        @endfor
                    </nav>
                </div>
            </section>
        @endif

        <section class="national-records-reference">
            <div class="container">
                <p><strong>Independent guide.</strong> SepticPath is not a government agency and does not certify record existence, compliance, or system condition. Last review activity: ${editorialLastReviewedAt}</p>
                <details>
                    <summary>Questions and supporting guidance</summary>
                    @for (var faq : contentPage.faqBlocks())
                        <article><h2>${faq.question()}</h2><p>${faq.answer()}</p></article>
                    @endfor
                </details>
            </div>
        </section>
    </div>
`)
