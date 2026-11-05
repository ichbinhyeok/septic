@import com.example.septic.data.model.ContentPage
@import com.example.septic.web.PageMeta
@import com.example.septic.web.RecordHelpEntry
@param PageMeta page
@param ContentPage contentPage
@template.layouts.app(page = page, designFamily = "editorial", pageStyles = "/record-problem-guide.css", content = @`
<div class="problem-guide">
    <section class="problem-hero">
        <div class="container problem-hero__inner">
            <div class="problem-hero__copy">
                <span class="eyebrow">North Carolina · Appalachian District</span>
                <h1>${contentPage.title()}</h1>
                <p class="lede">${contentPage.introCopy()}</p>
                <a class="button button--primary" href="https://public.cdpehs.com/NCENVPBLo/OSW_PROPERTY/ShowOSW_PROPERTYTablePage.aspx?ESTTST_CTY=D5" target="_blank" rel="noreferrer">Open the official permit search ↗</a>
                <nav class="policy-related" aria-label="Jump to county search tips">
                    <a href="#ashe">Ashe ↓</a><a href="#watauga">Watauga ↓</a><a href="#alleghany">Alleghany ↓</a>
                </nav>
            </div>
            <aside class="trust-box trust-box--large">
                <span class="trust-box__label">Start with the right PIN</span>
                <h2>Searching in Ashe County?</h2>
                <p>AppHealthCare says to use the old parcel ID from Ashe County GIS while the permit index is being updated. The change took place in October 2025.</p>
                <a href="#ashe">See the Ashe search steps ↓</a>
            </aside>
        </div>
    </section>
    <section class="section problem-body" aria-labelledby="apphealth-search">
        <div class="container problem-body__grid">
            <div>
                <span class="eyebrow">How to use Property Criteria</span>
                <h2 id="apphealth-search">Find the property, then open its forms.</h2>
                <ol class="checklist">
                    @for (var step : contentPage.decisionSteps())
                        <li>${step}</li>
                    @endfor
                </ol>
                <article class="problem-step" id="ashe"><span>01</span><div><h3>Ashe County</h3><p>Look up the old parcel ID through Ashe County’s mapping office. Select Ashe in the permit portal and use the old PIN as printed, including the dash. Try the original applicant or subdivision if the PIN misses. <a href="https://www.ashecountygov.com/tax-administration/tax-administration">Contact Ashe County Tax Administration ↗</a></p></div></article>
                <article class="problem-step" id="watauga"><span>02</span><div><h3>Watauga County</h3><p>Select Watauga and keep the dashes in the PIN. For older lots, try the subdivision, lot or owner when permitted. If the current deed is recent, use older deeds to identify the owner at the time of installation.</p></div></article>
                <article class="problem-step" id="alleghany"><span>03</span><div><h3>Alleghany County</h3><p>Select Alleghany and try the permit number or the PIN from your tax bill. If the street address is missing on older records, try the original applicant or subdivision and lot.</p></div></article>
                <p><a href="/septic-as-built-records/">How to identify the installed drawing in the file →</a></p>
            </div>
            <aside class="problem-help">
                <span class="eyebrow">If the file is missing</span>
                <h2>Give the county the keys you tried.</h2>
                <p>Ask the office that issued the permit to search the earlier owner, PIN, subdivision and approximate installation year.</p>
                <ul class="checklist">
                    <li>Ashe · <a href="tel:+13362463356">336-246-3356</a></li>
                    <li>Watauga · <a href="tel:+18282644995">828-264-4995</a></li>
                    <li>Alleghany · <a href="tel:+13363728813">336-372-8813</a></li>
                </ul>
                <a class="button button--primary" href="${RecordHelpEntry.of("missing").link("/apphealthcare-septic-permit-search/", "NC", null)}" data-record-help-cta data-track-click="nav" data-track-source-context="apphealthcare_existing_file" data-track-target-type="lead_form">Get help finding the existing file</a>
            </aside>
        </div>
    </section>
    <section class="section problem-faq">
        <div class="container problem-body__grid">
            <div><h2>Common search problems</h2>
            @for (var faq : contentPage.faqBlocks())
                <details><summary>${faq.question()}</summary><p>${faq.answer()}</p></details>
            @endfor
            </div>
            <div class="problem-sources"><h3>Official sources</h3><p><a href="https://www.apphealthcare.com/environmental-health/septic-system-program/">AppHealthCare search instructions</a></p><p><a href="https://public.cdpehs.com/NCENVPBLo/OSW_PROPERTY/ShowOSW_PROPERTYTablePage.aspx?ESTTST_CTY=D5">Public property search</a></p><p>Reviewed ${contentPage.reviewedAt()}.</p><p><a href="/north-carolina-septic-permit-lookup/">Looking in another North Carolina county? →</a></p></div>
        </div>
    </section>
</div>
`)
