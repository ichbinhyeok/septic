@import com.example.septic.web.AssetPaths
@import com.example.septic.web.PageMeta
@param String title
@param String heroImage
@param gg.jte.Content content
@param String pageClass = "studio-county"
@param String pageStyles = "/studio-county.css"
@param String mobileHeroImage = ""
@param PageMeta page = null
@param boolean production = false
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${!production || page == null ? title + " — SepticPath design preview" : page.title()}</title>
    <meta name="description" content="${!production || page == null ? "SepticPath visual design preview." : page.description()}">
    <meta name="robots" content="${!production || page == null ? "noindex,nofollow" : page.robots()}">
    @if (production && page != null)
    <link rel="canonical" href="${page.canonicalUrl()}">
    <meta property="og:title" content="${page.title()}">
    <meta property="og:description" content="${page.description()}">
    <meta property="og:type" content="website">
    <meta property="og:url" content="${page.canonicalUrl()}">
    @if (page.socialImageUrl() != null && !page.socialImageUrl().isBlank())
    <meta property="og:image" content="${page.socialImageUrl()}">
    <meta name="twitter:card" content="summary_large_image">
    <meta name="twitter:image" content="${page.socialImageUrl()}">
    @endif
    @endif
    <link rel="icon" href="/favicon.svg" type="image/svg+xml">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@400;500;600;700&family=Newsreader:opsz,wght@6..72,400;6..72,500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${AssetPaths.versioned("/studio.css")}">
    @if (!pageStyles.isBlank())
    <link rel="stylesheet" href="${AssetPaths.versioned(pageStyles)}">
    @endif
    <script src="${AssetPaths.versioned("/studio-navigation.js")}" defer></script>
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
    @if (mobileHeroImage.isBlank())
    <link rel="preload" as="image" href="${heroImage}" fetchpriority="high">
    @else
    <link rel="preload" as="image" href="${heroImage}" media="(min-width: 701px)" fetchpriority="high">
    <link rel="preload" as="image" href="${mobileHeroImage}" media="(max-width: 700px)" fetchpriority="high">
    @endif
    @if (production && page != null)
    @for (var jsonLd : page.jsonLdBlocks())
    <script type="application/ld+json">$unsafe{jsonLd}</script>
    @endfor
    @endif
</head>
<body class="studio ${pageClass}">
<a class="studio-skip" href="#main">Skip to content</a>
<header class="studio-nav">
    <a class="studio-brand" href="${production ? "/" : "/design-preview/studio/"}" aria-label="SepticPath research service">SepticPath<span>Clear records.<br>Brighter decisions.</span></a>
    <nav aria-label="Main navigation">@template.studio.navigation(pageClass = pageClass, primaryAction = true, production = production)</nav>
    <details class="studio-menu"><summary aria-controls="studio-mobile-navigation">Menu <span aria-hidden="true">+</span></summary><nav id="studio-mobile-navigation" aria-label="Mobile navigation">@template.studio.navigation(pageClass = pageClass, primaryAction = true, production = production)</nav></details>
</header>
${content}
<footer class="studio-footer">
    <div class="studio-wrap studio-footer__main"><div><a class="studio-footer__brand" href="${production ? "/" : "/design-preview/studio/"}">SepticPath</a><p class="studio-overline">Clear records.<br>Brighter decisions.</p></div><nav aria-label="Footer navigation">@template.studio.navigation(pageClass = pageClass, production = production)</nav></div>
    <div class="studio-wrap studio-footer__legal"><span>Independent research. Official-source context.</span><div><a href="${production ? "/privacy-policy/" : "/design-preview/studio/policies/privacy-policy/"}">Privacy</a><a href="${production ? "/terms-of-use/" : "/design-preview/studio/policies/terms-of-use/"}">Terms</a><a href="${production ? "/methodology/" : "/design-preview/studio/methodology/"}">Methodology</a></div></div>
</footer>
</body>
</html>
