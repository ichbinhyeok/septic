@import com.example.septic.web.PageMeta
@import com.example.septic.web.AssetPaths
@import gg.jte.Content
@param PageMeta page
@param Content content
@param String designFamily = ""
@param String pageStyles = ""

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
    @if (!pageStyles.isBlank())
        <link rel="stylesheet" href="${AssetPaths.versioned(pageStyles)}">
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
                <a class="brand" href="/" aria-label="SepticPath home">
                    <span class="brand__text"><strong>SepticPath</strong><span>Clear records. Brighter decisions.</span></span>
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
                    <a href="/offer-prep-septic-file-check/">Our service</a>
                    <a href="/septic-record-brief-example/#real-cases" data-track-click="nav" data-track-source-context="global_reading" data-track-target-type="internal_page">Our work</a>
                    <a href="/septic-records-checklist/">Guides</a>
                    <a href="/offer-prep-septic-file-check/#record-help"
                       class="site-nav__help"
                       data-record-help-link
                       data-track-click="nav"
                       data-track-source-context="global_header"
                       data-track-target-type="lead_form">Ask SepticPath <span aria-hidden="true">↗</span></a>
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
            <div class="container footer-grid">
                <section class="footer-block footer-block--brand">
                    <a class="brand brand--footer" href="/"><span class="brand__text"><strong>SepticPath</strong><span>Clear records. Brighter decisions.</span></span></a>
                    <p class="footer-copy">Independent property-record research. We search public systems, follow the responsible agency path, and explain what the returned evidence can—and cannot—establish.</p>
                    <a class="footer-inquiry" href="/offer-prep-septic-file-check/#record-help" data-record-help-link data-track-click="nav" data-track-source-context="global_footer" data-track-target-type="lead_form">Ask SepticPath <span aria-hidden="true">↗</span></a>
                </section>
                <nav class="footer-block" aria-label="Start here"><h2 class="footer-heading">Start here</h2><ul class="footer-list">
                    <li><a href="/septic-record-finder/">Find records</a></li><li><a href="/septic-record-finder/?mode=document">Review a file</a></li><li><a href="/septic-permit-lookup/">Permit lookup</a></li><li><a href="/septic-system-cost-calculator/">Cost planning</a></li>
                </ul></nav>
                <nav class="footer-block" aria-label="Explore"><h2 class="footer-heading">Explore</h2><ul class="footer-list">
                    <li><a href="/septic-record-brief-example/#real-cases">Our work</a></li><li><a href="/septic-records-checklist/">Records guides</a></li><li><a href="/septic-records-by-county/">County record paths</a></li><li><a href="/septic-records-access-index/">County access dataset</a></li><li><a href="/septic-transfer-compliance/">Transfer compliance</a></li><li><a href="/states/">State guides</a></li>
                </ul></nav>
                <nav class="footer-block" aria-label="Standards"><h2 class="footer-heading">Standards</h2><ul class="footer-list">
                    <li><a href="/about/">About</a></li><li><a href="/methodology/">Methodology</a></li><li><a href="/editorial-standards/">Editorial standards</a></li><li><a href="/coverage/">Coverage</a></li><li><a href="/contact/">Contact</a></li>
                </ul></nav>
            </div>
            <div class="container footer-meta"><span>Independent research. Not an inspection or permitting authority.</span><nav aria-label="Legal"><a href="/privacy-policy/">Privacy</a><a href="/terms-of-use/">Terms</a><a href="/data-license/">Data license</a></nav><span>© SepticPath</span></div>
        </footer>
    </div>
</body>
</html>
