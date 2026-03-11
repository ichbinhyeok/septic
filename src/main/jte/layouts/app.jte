@import com.example.septic.web.PageMeta
@import com.example.septic.web.AssetPaths
@import gg.jte.Content
@param PageMeta page
@param Content content

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${page.title()}</title>
    <meta name="description" content="${page.description()}">
    <meta name="robots" content="${page.robots()}">
    <link rel="icon" type="image/png" sizes="32x32" href="${AssetPaths.versioned("/favicon.png")}" />
    <link rel="canonical" href="${page.canonicalUrl()}">
    <meta property="og:title" content="${page.title()}">
    <meta property="og:description" content="${page.description()}">
    <meta property="og:type" content="website">
    <meta property="og:url" content="${page.canonicalUrl()}">
    @if (page.socialImageUrl() != null && !page.socialImageUrl().isBlank())
        <meta property="og:image" content="${page.socialImageUrl()}">
        <meta property="og:image:alt" content="${page.title()}">
    @endif
    <meta name="twitter:card" content="summary_large_image">
    <meta name="twitter:title" content="${page.title()}">
    <meta name="twitter:description" content="${page.description()}">
    @if (page.socialImageUrl() != null && !page.socialImageUrl().isBlank())
        <meta name="twitter:image" content="${page.socialImageUrl()}">
    @endif
    <link rel="icon" href="${AssetPaths.versioned("/favicon.svg")}" type="image/svg+xml">
    <link rel="shortcut icon" href="${AssetPaths.versioned("/favicon.svg")}" type="image/svg+xml">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="preload" href="https://fonts.googleapis.com/css2?family=Manrope:wght@500;600;700;800&family=Public+Sans:wght@400;500;600;700&display=swap" as="style" onload="this.onload=null;this.rel='stylesheet'">
    <noscript><link href="https://fonts.googleapis.com/css2?family=Manrope:wght@500;600;700;800&family=Public+Sans:wght@400;500;600;700&display=swap" rel="stylesheet"></noscript>
    <link rel="stylesheet" href="${AssetPaths.versioned("/app.css")}">
    <script async src="https://www.googletagmanager.com/gtag/js?id=G-S1SY1NS71P"></script>
    <script>
        window.dataLayer = window.dataLayer || [];
        function gtag(){dataLayer.push(arguments);}
        gtag('js', new Date());
        gtag('config', 'G-S1SY1NS71P');
    </script>
    <script src="${AssetPaths.versioned("/app.js")}" defer></script>
    @for (var jsonLd : page.jsonLdBlocks())
        <script type="application/ld+json">$unsafe{jsonLd}</script>
    @endfor
</head>
<body>
    <div class="shell">
        <header class="site-header">
            <div class="container site-header__inner">
                <a class="brand" href="/">
                    <img src="${AssetPaths.versioned("/favicon.png")}" alt="SepticPath logo" class="brand__logo" width="44" height="44">
                    <span class="brand__text">
                        <strong>SepticPath</strong>
                        <span>septic planning estimates with official-source context</span>
                    </span>
                </a>

                <button class="site-nav-toggle"
                        type="button"
                        aria-expanded="false"
                        aria-controls="site-nav-menu"
                        aria-label="Open navigation menu"
                        data-site-nav-toggle>
                    <span class="site-nav-toggle__icon" aria-hidden="true">
                        <span class="site-nav-toggle__line"></span>
                        <span class="site-nav-toggle__line"></span>
                        <span class="site-nav-toggle__line"></span>
                    </span>
                    <span class="site-nav-toggle__label">Menu</span>
                </button>

                <nav class="site-nav" id="site-nav-menu" aria-label="Primary">
                    <a href="/septic-system-cost-calculator/">Cost estimator</a>
                    <a href="/septic-tank-size-estimator/">Tank size</a>
                    <a href="/states/">All states</a>
                    <a href="/about/">About</a>
                </nav>
            </div>
        </header>

        <main>
            @if (page.breadcrumbs() != null && page.breadcrumbs().size() > 1)
                <section class="section section--breadcrumb">
                    <div class="container">
                        <nav class="breadcrumb-nav" aria-label="Breadcrumb">
                            <ol class="breadcrumb-list">
                                @for (var crumb : page.breadcrumbs())
                                    <li class="breadcrumb-list__item">
                                        @if (crumb == page.breadcrumbs().get(page.breadcrumbs().size() - 1))
                                            <span aria-current="page">${crumb.title()}</span>
                                        @else
                                            <a href="${crumb.path()}">${crumb.title()}</a>
                                        @endif
                                    </li>
                                @endfor
                            </ol>
                        </nav>
                    </div>
                </section>
            @endif
            ${content}
        </main>

        <footer class="site-footer">
            <div class="container footer-grid">
                <section class="footer-block footer-block--brand">
                    <a class="brand brand--footer" href="/">
                        <img src="${AssetPaths.versioned("/favicon.png")}" alt="SepticPath logo" class="brand__logo" width="44" height="44">
                        <span class="brand__text">
                            <strong>SepticPath</strong>
                            <span>Estimate-first guidance with official-source context.</span>
                        </span>
                    </a>
                    <p class="footer-copy">This site is a homeowner planning tool. It does not provide engineering design, permit approval, or legal compliance certification.</p>
                </section>

                <nav class="footer-block" aria-label="Core pages">
                    <h2 class="footer-heading">Core pages</h2>
                    <ul class="footer-list">
                        <li><a href="/septic-system-cost-calculator/">Cost estimator</a></li>
                        <li><a href="/septic-tank-size-estimator/">Tank size estimator</a></li>
                        <li><a href="/septic-pump-schedule-estimator/">Pump schedule estimator</a></li>
                        <li><a href="/drain-field-estimator/">Drain field estimator</a></li>
                        <li><a href="/states/">All state coverage</a></li>
                        <li><a href="/septic-replacement-cost/">Replacement cost</a></li>
                        <li><a href="/septic-inspection-cost/">Inspection cost</a></li>
                        <li><a href="/septic-permit-process/">Permit process</a></li>
                        <li><a href="/septic-records-checklist/">Records checklist</a></li>
                        <li><a href="/buying-a-house-with-a-septic-system/">Buying with septic</a></li>
                    </ul>
                </nav>

                <nav class="footer-block" aria-label="Trust pages">
                    <h2 class="footer-heading">Trust</h2>
                    <ul class="footer-list">
                        <li><a href="/about/">About</a></li>
                        <li><a href="/editorial-standards/">Editorial Standards</a></li>
                        <li><a href="/privacy-policy/">Privacy Policy</a></li>
                        <li><a href="/terms-of-use/">Terms of Use</a></li>
                        <li><a href="/contact/">Contact</a></li>
                    </ul>
                </nav>
            </div>
        </footer>
    </div>
</body>
</html>
