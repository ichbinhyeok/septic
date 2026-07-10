@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.AssetPaths
@param java.util.List<StateProfile> states = java.util.List.of()

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="robots" content="noindex,nofollow">
    <title>Septic Bedroom Permit Checker</title>
    <link rel="icon" href="${AssetPaths.versioned("/favicon.svg")}" type="image/svg+xml">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@500;600;700;800&family=Public+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${AssetPaths.versioned("/app.css")}">
    <script src="${AssetPaths.versioned("/app.js")}" defer></script>
</head>
<body class="bedroom-checker-embed-page">
    <main class="bedroom-checker-embed-shell">
        <div class="bedroom-checker bedroom-checker--embed" data-bedroom-permit-checker>
            <div class="bedroom-checker__intro">
                <span class="eyebrow">SepticPath quick check</span>
                <h1>Compare listing bedrooms to the septic permit.</h1>
                <p>Flag the official file question before a property listing, offer, inspection, or lender conversation relies on the wrong bedroom count.</p>
                <div class="bedroom-checker__signals" aria-label="Checker guardrails">
                    <span>Nothing saved</span>
                    <span>Official file first</span>
                </div>
            </div>

            <form class="bedroom-checker__form" data-bedroom-permit-form>
                <div class="bedroom-checker__fields">
                    <label>
                        <span>Property state</span>
                        <select data-bedroom-state>
                            @for (var state : states)
                                <option value="${state.stateCode()}" selected="${"TN".equals(state.stateCode())}">${state.stateName()}</option>
                            @endfor
                        </select>
                    </label>
                    <label>
                        <span>Listing bedrooms</span>
                        <input type="number" min="1" max="12" inputmode="numeric" value="3" required data-bedroom-listing-count>
                    </label>
                    <label>
                        <span>Septic permit bedrooms</span>
                        <select data-bedroom-permit-count>
                            <option value="">Not confirmed yet</option>
                            @for (int bedroomCount = 1; bedroomCount <= 12; bedroomCount++)
                                <option value="${bedroomCount}">${bedroomCount} bedrooms</option>
                            @endfor
                        </select>
                    </label>
                    <label>
                        <span>File status</span>
                        <select data-bedroom-file-status>
                            <option value="not_checked">I have not checked an official file</option>
                            <option value="official">Official permit or approval reviewed</option>
                            <option value="missing">No file found yet</option>
                            <option value="conflicting">Seller, tax, or permit records conflict</option>
                        </select>
                    </label>
                    <label class="bedroom-checker__address-field">
                        <span>Property address, optional</span>
                        <input type="text" maxlength="180" autocomplete="street-address" placeholder="Used only in the note you copy or download" data-bedroom-address>
                    </label>
                </div>
                <button class="button button--primary" type="submit" data-bedroom-submit>Check the record gap</button>
                <p class="bedroom-checker__privacy">This checker runs in your browser. SepticPath does not upload or save the address, bedroom counts, or result.</p>
            </form>

            <section class="bedroom-checker__result" aria-live="polite" data-bedroom-result hidden>
                <span class="eyebrow" data-bedroom-result-label>File check</span>
                <h2 data-bedroom-result-heading></h2>
                <p data-bedroom-result-body></p>
                <div class="bedroom-checker__facts" data-bedroom-result-facts></div>
                <ol class="bedroom-checker__steps" data-bedroom-result-steps></ol>
                <textarea class="bedroom-checker__note" rows="10" readonly data-bedroom-result-note></textarea>
                <div class="bedroom-checker__actions">
                    <button class="button button--primary" type="button" data-bedroom-copy>Copy transaction note</button>
                    <button class="button button--secondary" type="button" data-bedroom-download>Download note .txt</button>
                    <a class="button button--secondary" href="/septic-permit-search-by-address/" data-track-click="nav" data-track-source-context="bedroom_checker_result_address" data-track-target-type="internal_page">Find official record route</a>
                    <a class="button button--secondary" href="/septic-records-request-builder/" data-track-click="nav" data-track-source-context="bedroom_checker_result_request" data-track-target-type="internal_page">Build records request</a>
                </div>
            </section>
        </div>
        <p class="bedroom-checker-embed-shell__footer"><a href="https://septicpath.com/septic-bedroom-permit-checker/" target="_blank" rel="noreferrer">Open the full SepticPath checker</a></p>
    </main>
</body>
</html>
