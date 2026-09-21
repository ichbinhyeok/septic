@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.AssetPaths
@param java.util.List<StateProfile> states = java.util.List.of()
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="robots" content="noindex,nofollow">
    <title>Septic Bedroom Permit Checker</title>
    <link rel="icon" href="${AssetPaths.versioned("/favicon.svg")}" type="image/svg+xml">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@400;500;600;700&family=Newsreader:opsz,wght@6..72,400;6..72,500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${AssetPaths.versioned("/bedroom-embed.css")}">
    <script src="${AssetPaths.versioned("/app.js")}" defer></script>
</head>
<body>
<main class="embed-shell" data-bedroom-permit-checker>
    <header class="embed-brand"><a href="https://septicpath.com/septic-bedroom-permit-checker/" target="_blank" rel="noreferrer">SepticPath</a><span>Bedroom file check</span></header>
    <section class="embed-intro" aria-labelledby="embed-title"><p class="embed-overline">Compare the recorded story</p><h1 id="embed-title">Does the listing match<br>the septic file?</h1><p>Compare what the property is advertised as with the bedroom count shown in an official septic permit or approval.</p><ul aria-label="Checker boundaries"><li>Nothing saved</li><li>Official file first</li><li>Not a compliance decision</li></ul></section>
    <form class="embed-form" data-bedroom-permit-form>
        <fieldset class="embed-counts"><legend>Compare bedroom counts</legend><label><span>Listing</span><input type="number" min="1" max="12" step="1" inputmode="numeric" required placeholder="3" data-bedroom-listing-count><small>advertised bedrooms</small></label><label><span>Permit</span><select data-bedroom-permit-count><option value="">Unknown</option>@for (int bedroomCount = 1; bedroomCount <= 12; bedroomCount++)<option value="${bedroomCount}">${bedroomCount}</option>@endfor</select><small>official file count</small></label></fieldset>
        <div class="embed-fields"><label><span>Property state</span><select required data-bedroom-state><option value="" selected disabled>Choose a state</option>@for (var state : states)<option value="${state.stateCode()}">${state.stateName()}</option>@endfor</select></label><label><span>What have you reviewed?</span><select data-bedroom-file-status><option value="not_checked">I have not checked an official file</option><option value="official">Official permit or approval reviewed</option><option value="missing">No file found yet</option><option value="conflicting">Seller, tax, or permit records conflict</option></select></label><label class="embed-fields__wide"><span>Property address <small>optional</small></span><input type="text" maxlength="180" autocomplete="street-address" placeholder="Used only in the note you copy or download" data-bedroom-address></label></div>
        <button class="embed-button" type="submit" data-bedroom-submit>Compare the counts <span aria-hidden="true">↗</span></button><p class="embed-privacy">Runs in your browser. SepticPath does not upload or save the address, counts, or result.</p>
    </form>
    <section class="embed-result" aria-live="polite" data-bedroom-result hidden>
        <p class="embed-overline" data-bedroom-result-label>File check</p><div class="embed-facts" data-bedroom-result-facts></div><h2 tabindex="-1" data-bedroom-result-heading></h2><p data-bedroom-result-body></p>
        <div class="embed-result__grid"><div><h3>Next records questions</h3><ol data-bedroom-result-steps></ol><nav aria-label="Next actions"><a href="/septic-permit-search-by-address/" target="_blank" rel="noreferrer" data-track-click="nav" data-track-source-context="bedroom_checker_result_address" data-track-target-type="internal_page">Find the official record route ↗</a><a href="/septic-records-request-builder/" target="_blank" rel="noreferrer" data-track-click="nav" data-track-source-context="bedroom_checker_result_request" data-track-target-type="internal_page">Build a records request ↗</a></nav></div><div class="embed-note"><label for="embed-note">Transaction note</label><textarea id="embed-note" readonly rows="12" data-bedroom-result-note></textarea><div><button class="embed-button" type="button" data-bedroom-copy>Copy note</button><button type="button" data-bedroom-download>Download .txt</button></div></div></div>
        <p class="embed-disclaimer">Comparison only—not an engineering, permit, MLS, lender, legal, or compliance determination.</p>
    </section>
    <footer class="embed-footer"><span>Clear records. Brighter decisions.</span><a href="https://septicpath.com/septic-bedroom-permit-checker/" target="_blank" rel="noreferrer">Open the full SepticPath checker ↗</a></footer>
</main>
</body>
</html>
