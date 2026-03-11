@import com.example.septic.web.PageMeta
@import com.example.septic.web.PageLink
@param PageMeta page
@param String message
@param java.util.List<PageLink> relatedLinks = java.util.List.of()

@template.layouts.app(page = page, content = @`
    <section class="section section--heroish">
        <div class="container narrow">
            <span class="eyebrow">404</span>
            <h1>Page not found</h1>
            <p class="lede">${message}</p>
            <a class="button button--primary" href="/">Go home</a>
        </div>
    </section>

    @if (!relatedLinks.isEmpty())
        <section class="section section--tight">
            <div class="container narrow">
                <article class="detail-card">
                    <h2>Closest next pages</h2>
                    <p class="detail-list__note">These are ordered from the closest intent match to the safest broader recovery path.</p>
                    <ul class="detail-list">
                        @for (var link : relatedLinks)
                            <li>
                                <a href="${link.path()}">${link.title()}</a>
                                <p class="detail-list__note">${link.note()}</p>
                            </li>
                        @endfor
                    </ul>
                </article>
            </div>
        </section>
    @endif
`)
