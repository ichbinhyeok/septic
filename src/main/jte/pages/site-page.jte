@import com.example.septic.web.PageMeta
@import com.example.septic.web.SitePageSection
@param PageMeta page
@param String eyebrow
@param String heading
@param String intro
@param java.util.List<SitePageSection> sections
@param String calloutTitle
@param String calloutBody

@template.layouts.app(page = page, designFamily = "editorial", content = @`
    <section class="section section--heroish site-page-hero">
        <div class="container page-hero">
            <div class="page-hero__content">
                <span class="eyebrow">${eyebrow}</span>
                <h1>${heading}</h1>
                <p class="lede">${intro}</p>
            </div>

            <aside class="hero-sidecard">
                <span class="pill">Independent research service</span>
                <strong>Human-reviewed public record research</strong>
                <p>${calloutBody}</p>
            </aside>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container detail-grid">
            @for (var section : sections)
                <article class="detail-card" id="${"Research findings and public examples".equals(section.title()) ? "research-publication" : null}">
                    <h2>${section.title()}</h2>
                    <p>${section.body()}</p>
                    @if (section.bullets() != null && !section.bullets().isEmpty())
                        <ul class="detail-list">
                            @for (var bullet : section.bullets())
                                <li>${bullet}</li>
                            @endfor
                        </ul>
                    @endif
                </article>
            @endfor
        </div>
    </section>

    <section class="section">
        <div class="container">
            <article class="trust-box trust-box--large">
                <span class="trust-box__label">Important note</span>
                <h2>${calloutTitle}</h2>
                <p>${calloutBody}</p>
                <a href="/offer-prep-septic-file-check/#record-help" data-record-help-cta>Start a property record investigation</a>
            </article>
        </div>
    </section>
`)
