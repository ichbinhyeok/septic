@import com.example.septic.web.PageMeta
@import com.example.septic.web.PageLink
@param PageMeta page
@param String message
@param java.util.List<PageLink> relatedLinks = java.util.List.of()
@param int statusCode = 404
@param String requestPath = ""

@template.layouts.app(page = page, designFamily = "recovery", content = @`
    <section class="recovery-hero" aria-labelledby="recovery-title">
        <div class="container recovery-hero__inner">
            <p class="recovery-status"><span>${statusCode == 404 ? "Route not found" : "Temporary interruption"}</span><span aria-hidden="true">·</span><span>${statusCode}</span></p>
            <h1 id="recovery-title">${statusCode == 404 ? "This route ends here. Your property search doesn’t." : "This route paused. Your research can keep moving."}</h1>
            <p class="lede">${message}</p>
            <div class="recovery-actions">
                <a class="button button--primary" href="/septic-record-finder/">Open the records workspace <span aria-hidden="true">→</span></a>
                <a class="recovery-text-link" href="/states/">Browse property guides</a>
            </div>
            <p class="recovery-assurance">No request or payment is created on this screen.</p>
        </div>
    </section>

    @if (!relatedLinks.isEmpty())
        <section class="recovery-next" aria-labelledby="recovery-next-title">
            <div class="container">
                <header class="recovery-next__heading">
                    <span class="recovery-legacy-label">Closest next pages</span>
                    <h2 id="recovery-next-title">Choose the next useful step.</h2>
                    <p>The closest intent match is listed first.</p>
                </header>
                <nav aria-label="Suggested next pages">
                    <ul class="recovery-routes">
                        @for (var link : relatedLinks)
                            <li>
                                <a href="${link.path()}"><span>${link.title()}</span><span aria-hidden="true">→</span></a>
                                <p>${link.note()}</p>
                            </li>
                        @endfor
                    </ul>
                </nav>
                <div class="recovery-help">
                    <p><span>Still unsure?</span> Tell us the property and the question. We’ll help identify the useful route.</p>
                    <a href="/offer-prep-septic-file-check/#record-help">Ask SepticPath <span aria-hidden="true">→</span></a>
                </div>
            </div>
        </section>
    @endif
`)
