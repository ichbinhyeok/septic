@import com.example.septic.web.RecordHelpEntry
@import com.example.septic.web.AssetPaths
@param String sourcePath
@param String stateCode
@param String regionName
@param String countyName = null

<link rel="stylesheet" href="${AssetPaths.versioned("/regional-record-goals.css")}">
<section class="record-goals" aria-labelledby="record-goals-title">
    <div class="record-goals__inner">
        <header>
            <p class="record-goals__eyebrow">Free property research · ${regionName}</p>
            <h2 id="record-goals-title">What do you need to find out?</h2>
            <p>No drawing or permit number needed to start. Give us the property address and the question that matters to you.</p>
        </header>
        <div class="record-goals__grid">
            <a href="${RecordHelpEntry.of("location").link(sourcePath, stateCode, countyName)}" data-record-help-cta data-track-click="nav" data-track-source-context="regional_goal_location" data-track-target-type="lead_form">
                <h3>Where is my septic tank?</h3>
                <p>Look for a permit or site sketch with tank and drainfield location clues—and learn what still needs an on-site locate.</p>
                <span>Find my location records <b aria-hidden="true">→</b></span>
            </a>
            <a href="${RecordHelpEntry.of("building").link(sourcePath, stateCode, countyName)}" data-record-help-cta data-track-click="nav" data-track-source-context="regional_goal_building" data-track-target-type="lead_form">
                <h3>Planning a deck or addition?</h3>
                <p>Look for the recorded drainfield layout before planning around it. Confirm the current location and project requirements locally.</p>
                <span>Check my project’s records <b aria-hidden="true">→</b></span>
            </a>
            <a href="${RecordHelpEntry.of("buying").link(sourcePath, stateCode, countyName)}" data-record-help-cta data-track-click="nav" data-track-source-context="regional_goal_buying" data-track-target-type="lead_form">
                <h3>Buying a home with septic?</h3>
                <p>Find the permit, layout and available approval records so you know what to ask the seller, inspector or records office.</p>
                <span>Check this property’s records <b aria-hidden="true">→</b></span>
            </a>
        </div>
        <nav class="record-goals__reading" aria-label="Read before requesting records">
            <a href="/septic-tank-location-records/#location-start">Have an address but no drawing? Start here →</a>
            <a href="/septic-tank-location-records/#location-pumping">Preparing for pumping or maintenance →</a>
            <a href="/septic-tank-location-records/#addition-file-case">See an actual addition-planning records request →</a>
        </nav>
        <p class="record-goals__note">Research, records and explanation are free. Agency fees only with your approval. We research records—not on-site inspections or permission to build. Any local field work is separate.</p>
    </div>
</section>
