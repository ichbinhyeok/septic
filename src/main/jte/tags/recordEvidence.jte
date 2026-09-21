@import com.example.septic.web.AssetPaths
@param String sourceContext = "records_evidence"
@param String variant = "records"

<section class="record-evidence record-evidence--${variant}" data-record-evidence data-evidence-context="${sourceContext}" aria-label="Actual records delivered by SepticPath">
    <header class="record-evidence__heading">
        @if ("permit_process".equals(variant))
            <span class="record-evidence__eyebrow">REAL PERMIT FILES</span>
            <h2>A permit process matters when it leads to the property file.</h2>
            <p>Actual agency approvals, historical permits, and approved plans show what the Tennessee workflow is ultimately meant to produce—and what SepticPath can help explain.</p>
        @elseif ("county".equals(variant))
            <span class="record-evidence__eyebrow">REAL COUNTY FILES</span>
            <h2>This is the kind of record the route should produce.</h2>
            <p>Selected pages from completed requests show the original agency file, drawing, and plain-English brief. Availability still depends on the property and office.</p>
        @else
            <span class="record-evidence__eyebrow">REAL WORK SAMPLES</span>
            <h2>We chase the file. Then make it usable.</h2>
            <p>Selected full pages from separate completed requests: original agency records, hand-drawn plans, and the brief delivered after review.</p>
        @endif
    </header>
    <div class="record-evidence__documents">
        <div class="record-evidence__source">
            <span class="record-evidence__label">01 / RECEIVED FROM THE COUNTY</span>
            <p class="record-evidence__swipe-hint">Swipe to compare the original pages <span data-evidence-progress aria-live="polite">Document 1 of 3</span></p>
            <div class="record-evidence__papers" aria-label="Original document samples. Swipe or scroll horizontally on small screens.">
                <a class="record-evidence__letter" href="/images/case-study/redacted/agency-approval-letter-full-public.png" data-evidence-document="approval" aria-label="Enlarge the actual county approval letter">
                    <img src="/images/case-study/redacted/agency-approval-letter-full-public.png" width="1275" height="1650" loading="lazy" alt="Nearly full page of an actual county septic approval letter with identifying details redacted">
                    <span>Approval letter · full page ↗</span>
                </a>
                <a class="record-evidence__permit" href="/images/case-study/redacted/state-historical-permit-full-public.png" data-evidence-document="permit" aria-label="Enlarge the actual historical state septic permit">
                    <img src="/images/case-study/redacted/state-historical-permit-full-public.png" width="1283" height="1647" loading="lazy" alt="Nearly full historical state septic permit showing system specifications and a hand-drawn layout with identifying details redacted">
                    <span>Historical permit · full page ↗</span>
                </a>
                <a class="record-evidence__plan" href="/images/case-study/redacted/approved-site-plan-full-public.png" data-evidence-document="plan" aria-label="Enlarge the actual approved septic site plan">
                    <img src="/images/case-study/redacted/approved-site-plan-full-public.png" width="1275" height="1650" loading="lazy" alt="Nearly full page of an actual hand-drawn approved septic plan with identifying details redacted">
                    <span>Approved plan · full page ↗</span>
                </a>
            </div>
        </div>
        <div class="record-evidence__result">
            <span class="record-evidence__label">02 / EXPLAINED BY SEPTICPATH</span>
            <div class="record-evidence__brief">
                <div class="record-evidence__brief-title"><strong>SepticPath</strong><span>RECORD BRIEF</span></div>
                <a href="/images/case-study/redacted/record-brief-full-public.png" data-evidence-document="findings" aria-label="Enlarge the actual SepticPath Record Brief">
                    <img src="/images/case-study/redacted/record-brief-full-public.png" width="1275" height="1650" loading="lazy" alt="Nearly full page of a SepticPath Record Brief with the property identifiers redacted">
                </a>
                <p><strong>What this helps with</strong>A starting point for discussing the layout with a septic professional.</p>
                <p><strong>What it does not prove</strong>This is an approved design, not proof of the system’s current location or condition.</p>
                <a class="record-evidence__read" href="/images/case-study/redacted/record-brief-full-public.png" data-evidence-document="findings">Open the delivered brief ↗</a>
            </div>
        </div>
    </div>
    <p class="record-evidence__source-note"><span aria-hidden="true"></span><strong>Privacy reviewed:</strong> black bars remove identifying details; agency language, form structure, system data, and drawing geometry remain visible. Samples are from separate requests, not one property file. Record availability varies by property and jurisdiction.</p>
    <ol class="record-evidence__steps" aria-label="How we handle a record request">
        @if ("permit_process".equals(variant))
            <li>Identify the permit authority</li><li>Retrieve the property file</li><li>Explain approvals and gaps</li>
        @else
            <li>Search public sources</li><li>Request agency files when needed</li><li>Explain what comes back</li>
        @endif
    </ol>
    <div class="record-evidence__action">
        <a class="button button--primary" href="/offer-prep-septic-file-check/#record-help" data-record-help-cta data-track-click="nav" data-track-source-context="${sourceContext}" data-track-target-type="lead_form">Investigate my property</a>
        <span>Send the address and what you need to find out.</span>
    </div>
    <dialog class="record-evidence__dialog" aria-label="Enlarged customer document excerpt">
        <div class="record-evidence__dialog-bar"><strong data-evidence-caption>Document excerpt</strong><button type="button" data-evidence-close autofocus>Close ×</button></div>
        <p>Actual county record retrieval · Identifying details removed</p>
        <img data-evidence-image alt="">
    </dialog>
</section>
<link rel="stylesheet" href="${AssetPaths.versioned("/record-evidence.css")}">
<script src="${AssetPaths.versioned("/record-evidence.js")}" defer></script>
