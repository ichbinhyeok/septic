@import com.example.septic.web.PageMeta
@import com.example.septic.web.AssetPaths
@import gg.jte.Content
@param PageMeta page
@param Content content
@param String designFamily = ""

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${page.title()}</title>
    <meta name="description" content="${page.description()}">
    <meta name="robots" content="${page.robots()}">
    <link rel="canonical" href="${page.canonicalUrl()}">
    @if (page.dataDownloadUrl() != null && !page.dataDownloadUrl().isBlank())
        <link rel="alternate" type="text/csv" title="Septic Records Access Index CSV" href="${page.dataDownloadUrl()}">
    @endif
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
    <link rel="preload" href="https://fonts.googleapis.com/css2?family=Manrope:wght@400;500;600;700;800&family=Newsreader:opsz,wght@6..72,600&display=swap" as="style" onload="this.onload=null;this.rel='stylesheet'">
    <noscript><link href="https://fonts.googleapis.com/css2?family=Manrope:wght@400;500;600;700;800&family=Newsreader:opsz,wght@6..72,600&display=swap" rel="stylesheet"></noscript>
    <link rel="stylesheet" href="${AssetPaths.versioned("/app.css")}">
    <link rel="stylesheet" href="${AssetPaths.versioned("/workflows.css")}">
    <link rel="stylesheet" href="${AssetPaths.versioned("/pages.css")}">
    @if (!designFamily.isBlank())
        <link rel="stylesheet" href="${AssetPaths.versioned("/premium-families.css")}">
    @endif
    <script>
        if (window.location.hostname === "septicpath.com") {
            window.dataLayer = window.dataLayer || [];
            window.gtag = function () { window.dataLayer.push(arguments); };
            const analyticsScript = document.createElement("script");
            analyticsScript.async = true;
            analyticsScript.src = "https://www.googletagmanager.com/gtag/js?id=G-S1SY1NS71P";
            document.head.appendChild(analyticsScript);
            window.gtag("js", new Date());
            window.gtag("config", "G-S1SY1NS71P");
        }
    </script>
    <script src="${AssetPaths.versioned("/app-core.js")}" defer></script>
    <script src="${AssetPaths.versioned("/record-task.js")}" defer></script>
    <script src="${AssetPaths.versioned("/state-records-ui.js")}" defer></script>
    <script>
        document.addEventListener("DOMContentLoaded", function () {
            if (document.querySelector("[data-reading-guide]")) {
                const readingScript = document.createElement("script");
                readingScript.src = "${AssetPaths.versioned("/record-reading.js")}";
                readingScript.defer = true;
                document.head.appendChild(readingScript);
            }
            const featureSelector = [
                "#result-top",
                "[data-choice-group]",
                "[data-county-finder]",
                "[data-official-map-directory]",
                "[data-county-access-workflow]",
                "[data-address-record-finder]",
                "[data-offer-prep-file-check]",
                "[data-bedroom-permit-checker]",
                "[data-alabama-perc-scope]",
                "[data-bedroom-embed-copy]",
                "[data-records-request-builder]",
                "[data-packet-note]",
                "[data-share-route]",
                "[data-county-route-picker]",
                "[data-state-surface-tool]"
            ].join(",");

            if (!document.querySelector(featureSelector)) {
                return;
            }

            const featureScript = document.createElement("script");
            featureScript.src = "${AssetPaths.versioned("/app.js")}";
            featureScript.defer = true;
            document.head.appendChild(featureScript);
        });
    </script>
    @for (var jsonLd : page.jsonLdBlocks())
        <script type="application/ld+json">$unsafe{jsonLd}</script>
    @endfor
</head>
<body class="${designFamily.isBlank() ? null : "premium-site premium-site--" + designFamily}">
    <a class="skip-link" href="#main-content">Skip to main content</a>
    <div class="shell">
        <header class="site-header">
            <div class="container site-header__inner">
                <a class="brand" href="/">
                    <img src="${AssetPaths.versioned("/favicon.svg")}" alt="SepticPath logo" class="brand__logo" width="44" height="44">
                    <span class="brand__text">
                        <strong>SepticPath</strong>
                        <span>Research, retrieval &amp; clear findings</span>
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
                    <a href="/septic-record-finder/">Find records</a>
                    <a href="/septic-record-finder/?mode=document">Review a file</a>
                    <a href="/offer-prep-septic-file-check/#record-help"
                       class="site-nav__help"
                       data-record-help-link
                       data-track-click="nav"
                       data-track-source-context="global_header"
                       data-track-target-type="lead_form">Ask SepticPath</a>
                    <a href="/septic-record-brief-example/#real-cases" data-track-click="nav" data-track-source-context="global_reading" data-track-target-type="internal_page">Real cases</a>
                    <a href="/states/">States</a>
                    <a href="/septic-system-cost-calculator/">Cost planning</a>
                </nav>
            </div>
        </header>

        <main id="main-content">
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
            <div class="container">
                <section class="footer-callout" aria-labelledby="footer-callout-title">
                    <div>
                        <span>Human record research</span>
                        <h2 id="footer-callout-title">A public record is only useful when it answers your property question.</h2>
                    </div>
                    <a class="footer-callout__action"
                       href="/offer-prep-septic-file-check/#record-help"
                       data-record-help-link
                       data-track-click="nav"
                       data-track-source-context="global_footer_callout"
                       data-track-target-type="lead_form">Ask SepticPath <span aria-hidden="true">→</span></a>
                </section>

                <div class="footer-grid">
                <section class="footer-block footer-block--brand">
                    <a class="brand brand--footer" href="/">
                        <img src="${AssetPaths.versioned("/favicon.svg")}" alt="SepticPath logo" class="brand__logo" width="44" height="44">
                        <span class="brand__text">
                            <strong>SepticPath</strong>
                            <span>Human record research with official-source context.</span>
                        </span>
                    </a>
                    <p class="footer-copy">SepticPath researches public septic records, follows the responsible agency path when needed, and explains the evidence in plain English. It does not provide engineering design, permit approval, inspection, or legal compliance certification.</p>
                </section>

                <nav class="footer-block" aria-label="Core pages">
                    <h2 class="footer-heading">Core pages</h2>
                    <ul class="footer-list">
                        <li><a href="/septic-record-finder/">Find records by address</a></li>
                        <li><a href="/septic-record-finder/?mode=document">Review a file</a></li>
                        <li><a href="/offer-prep-septic-file-check/#record-help"
                               data-record-help-link
                               data-track-click="nav"
                               data-track-source-context="global_footer"
                               data-track-target-type="lead_form">Ask SepticPath for help</a></li>
                        <li><a href="/septic-permit-lookup/">Permit lookup</a></li>
                        <li><a href="/septic-records-checklist/">Records guides</a></li>
                        <li><a href="/septic-records-by-county/">County record paths</a></li>
                        <li><a href="/septic-records-access-index/">County access dataset</a></li>
                        <li><a href="/septic-transfer-compliance/">Transfer compliance</a></li>
                        <li><a href="/septic-system-cost-calculator/">Cost estimator</a></li>
                        <li><a href="/states/">State guides</a></li>
                    </ul>
                </nav>

                <nav class="footer-block" aria-label="Trust pages">
                    <h2 class="footer-heading">Trust</h2>
                    <ul class="footer-list">
                        <li><a href="/septic-record-brief-example/#real-cases">Real record-help cases</a></li>
                        <li><a href="/#read-record-stories">Practical record guides</a></li>
                        <li><a href="/about/">About</a></li>
                        <li><a href="/editorial-standards/">Editorial standards</a></li>
                        <li><a href="/methodology/">Methodology</a></li>
                        <li><a href="/coverage/">Coverage</a></li>
                        <li><a href="/contact/">Contact</a></li>
                        <li><a href="/privacy-policy/">Privacy</a> · <a href="/terms-of-use/">Terms</a> · <a href="/data-license/">Data license</a></li>
                    </ul>
                </nav>
                </div>

                <div class="footer-meta">
                    <span>Independent research service</span>
                    <span>Official agencies control record availability and permit status.</span>
                    <span>© SepticPath</span>
                </div>
            </div>
        </footer>
    </div>
</body>
</html>
