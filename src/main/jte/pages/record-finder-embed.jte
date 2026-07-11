@import com.example.septic.web.AssetPaths
@param int totalCountyRouteCount = 0

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="robots" content="noindex,nofollow">
    <title>Septic Records Finder</title>
    <link rel="icon" href="${AssetPaths.versioned("/favicon.svg")}" type="image/svg+xml">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@500;600;700;800&family=Public+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${AssetPaths.versioned("/app.css")}">
    <script src="${AssetPaths.versioned("/app.js")}" defer></script>
</head>
<body class="record-finder-embed-page">
    <main class="record-finder-embed-shell">
        @template.tags.addressRecordFinder(
            finderId = "record-finder-embed",
            eyebrow = "SepticPath records finder",
            heading = "Find the septic record route by address.",
            intro = "Resolve the property county, then open the closest verified public record, permit, or request path.",
            totalCountyRouteCount = totalCountyRouteCount,
            minimal = true
        )
        <p class="record-finder-embed-shell__footer"><a href="https://septicpath.com/septic-record-finder/" target="_blank" rel="noreferrer">Open the full SepticPath finder</a></p>
    </main>
</body>
</html>
