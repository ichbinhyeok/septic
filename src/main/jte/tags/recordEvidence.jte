@import com.example.septic.web.AssetPaths
@param String sourceContext = "records_evidence"

<section class="record-evidence" data-record-evidence data-evidence-context="${sourceContext}" aria-label="Actual records delivered by SepticPath">
    <header class="record-evidence__heading">
        <span class="record-evidence__eyebrow">FROM A REAL CUSTOMER REQUEST</span>
        <h2>Original records. A clear explanation.</h2>
        <p>We obtained this county file and sent the customer the originals with a plain-English Record Brief.</p>
    </header>
    <div class="record-evidence__documents">
        <div class="record-evidence__source">
            <span class="record-evidence__label">01 / RECEIVED FROM THE COUNTY</span>
            <div class="record-evidence__papers">
                <a class="record-evidence__letter" href="/images/case-study/redacted/shelby-approval-letter-excerpt-public.png" data-evidence-document="approval" aria-label="Enlarge the actual county approval letter excerpt">
                    <img src="/images/case-study/redacted/shelby-approval-letter-excerpt-public.png" width="1040" height="560" loading="lazy" alt="Excerpt of the county's original letter describing approval of the septic plot plan">
                    <span>Approval letter ↗</span>
                </a>
                <a class="record-evidence__plan" href="/images/case-study/redacted/shelby-site-plan-detail-public.png" data-evidence-document="plan" aria-label="Enlarge the actual approved septic site plan">
                    <img src="/images/case-study/redacted/shelby-site-plan-detail-public.png" width="560" height="1210" loading="lazy" alt="Original hand-drawn approved plan showing the proposed house, 1,000-gallon tank and 600 feet of field lines">
                    <span>Approved site plan ↗</span>
                </a>
            </div>
        </div>
        <div class="record-evidence__result">
            <span class="record-evidence__label">02 / EXPLAINED BY SEPTICPATH</span>
            <div class="record-evidence__brief">
                <div class="record-evidence__brief-title"><strong>SepticPath</strong><span>RECORD BRIEF</span></div>
                <a href="/images/case-study/redacted/shelby-brief-findings-public.png" data-evidence-document="findings" aria-label="Enlarge the actual Record Brief findings">
                    <img src="/images/case-study/redacted/shelby-brief-findings-public.png" width="835" height="300" loading="lazy" alt="Our delivered brief explains the proposed four-bedroom residence, 1,000-gallon tank and 600 feet of field lines">
                </a>
                <p><strong>What this helps with</strong>A starting point for discussing the layout with a septic professional.</p>
                <p><strong>What it does not prove</strong>This is an approved design, not proof of the system’s current location or condition.</p>
                <a class="record-evidence__read" href="/images/case-study/redacted/shelby-brief-field-use-public.png" data-evidence-document="guidance">Read the delivered guidance ↗</a>
            </div>
        </div>
    </div>
    <p class="record-evidence__source-note">Actual Shelby County, Tennessee case. Document excerpts; identifying details removed. Record availability varies by property.</p>
    <ol class="record-evidence__steps" aria-label="How we handle a record request">
        <li>Search public sources</li><li>Request agency files when needed</li><li>Explain what comes back</li>
    </ol>
    <div class="record-evidence__action">
        <a class="button button--primary" href="/offer-prep-septic-file-check/#record-help" data-record-help-cta data-track-click="nav" data-track-source-context="${sourceContext}" data-track-target-type="lead_form">Investigate my property</a>
        <span>Send the address and what you need to find out.</span>
    </div>
    <dialog class="record-evidence__dialog" aria-label="Enlarged customer document excerpt">
        <div class="record-evidence__dialog-bar"><strong data-evidence-caption>Document excerpt</strong><button type="button" data-evidence-close autofocus>Close ×</button></div>
        <p>Actual Shelby County, TN case · Identifying details removed</p>
        <img data-evidence-image alt="">
    </dialog>
</section>
<link rel="stylesheet" href="${AssetPaths.versioned("/record-evidence.css")}">
<script src="${AssetPaths.versioned("/record-evidence.js")}" defer></script>
