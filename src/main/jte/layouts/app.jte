@import com.example.septic.web.PageMeta
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
    <link rel="canonical" href="${page.canonicalUrl()}">
    <meta property="og:title" content="${page.title()}">
    <meta property="og:description" content="${page.description()}">
    <meta property="og:type" content="website">
    <meta property="og:url" content="${page.canonicalUrl()}">
    <meta name="twitter:card" content="summary_large_image">
    <meta name="twitter:title" content="${page.title()}">
    <meta name="twitter:description" content="${page.description()}">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@500;600;700;800&family=Public+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/app.css">
    @for (var jsonLd : page.jsonLdBlocks())
        <script type="application/ld+json">$unsafe{jsonLd}</script>
    @endfor
</head>
<body>
    <div class="shell">
        <header class="site-header">
            <div class="container site-header__inner">
                <a class="brand" href="/">
                    <span class="brand__mark">SS</span>
                    <span class="brand__text">
                        <strong>Septic System Cost & Size Estimator</strong>
                        <span>planning estimates with official-source context</span>
                    </span>
                </a>

                <nav class="site-nav">
                    <a href="/septic-system-cost-calculator/">Cost estimator</a>
                    <a href="/#states">Launch states</a>
                    <a href="/#sources">Source policy</a>
                </nav>
            </div>
        </header>

        <main>
            ${content}
        </main>
    </div>
</body>
</html>
