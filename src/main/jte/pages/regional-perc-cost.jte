@import com.example.septic.data.model.StateMoneyPage
@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.PageMeta
@param PageMeta page
@param StateMoneyPage stateMoneyPage
@param StateProfile state
@param boolean sc = "SC".equals(state.stateCode())
@template.layouts.app(page = page, designFamily = "editorial", pageStyles = "/regional-perc-cost.css", content = @`
<div class="perc-guide">
    <section class="section perc-hero"><div class="container">
        <span class="eyebrow">${state.stateName()} · Soil and site evaluation</span>
        <h1>${stateMoneyPage.title()}</h1>
        <p class="lede">${stateMoneyPage.introCopy()}</p>
        <div class="perc-fee" data-verified-agency-fee>
            <strong>${sc ? "$150" : "$1,121"}</strong>
            <div><h2>${sc ? "SCDES residential application fee" : "DEQ single-family site evaluation: first lot"}</h2>
            <p>${sc ? "Published by SCDES; checked November 18, 2026." : "Table 9A, effective July 1, 2026; checked November 18, 2026."}</p>
            <p><b>Agency charge only.</b> ${sc ? "Private professional work, equipment and installation need separate quotes." : "County-agent fees may differ. Excavation and the construction permit are separate."}</p></div>
        </div>
        <nav class="perc-nav" aria-label="Cost guide sections"><a href="#cost-breakdown">What the fee covers</a><a href="#before-paying">Before paying</a><a href="#existing-file">Already have a report?</a><a href="#cost-sources">Official sources</a></nav>
    </div></section>
    <section class="section section--tight" id="cost-breakdown"><div class="container">
        <h2>Build the budget from separate line items.</h2>
        <p>A search for “perc test” can describe several different services. Confirm the deliverable on the quote: an agency suitability review, private soil work, an engineered design or an inspection of an existing system.</p>
        <div class="perc-table-wrap"><table class="perc-table"><caption>${state.stateName()} site-evaluation budget: verified fee versus work to quote</caption><thead><tr><th scope="col">Line item</th><th scope="col">What is known</th><th scope="col">What to confirm</th></tr></thead><tbody>
            <tr><th scope="row">Public application / evaluation</th><td>${sc ? "$150 SCDES residential application fee. The traditional process includes an agency site visit and suitability review." : "$1,121 DEQ first-lot single-family site evaluation. Use the county schedule if a county agent administers the property."}</td><td>${sc ? "Confirm the permitting method with SCDES before paying for separate private work." : "Confirm the responsible office and the fee for this application type."}</td></tr>
            <tr><th scope="row">Test pits and equipment</th><td>${sc ? "SCDES requires backhoe pits above the South Carolina Fall Line; the applicant arranges the operator." : "DEQ describes evaluation of test pits supplied by the applicant."}</td><td>Equipment, access, operator time, return visits, backfilling and restoration. Get preparation instructions before ordering excavation.</td></tr>
            <tr><th scope="row">Private evaluation or design</th><td>No statewide private price is established by the agency fee.</td><td>Who prepares the report or design, whether the office accepts it for your chosen process, and which services are actually included.</td></tr>
            <tr><th scope="row">Later construction</th><td>${sc ? "A Permit to Construct precedes installation. The $150 application is not a septic installation price." : "The construction permit is a separate step and fee. Evaluation is not permission to install."}</td><td>Permit, installer, tank, drainfield, site work and any engineering or alternative-system requirements.</td></tr>
        </tbody></table></div>
        @if (!sc)
        <p class="perc-note"><strong>Reading an older Oregon quote?</strong> The same DEQ Table 9A lists $843 in its previous-fee column and $1,121 in its current 2026 column. The table does not establish a universal total for every Oregon county or private contractor.</p>
        @else
        <p class="perc-note"><strong>“DHEC perc test” searches:</strong> the current residential septic application guidance is published by SCDES. Follow the current agency instructions rather than assuming an older DHEC fee or form is still the right one.</p>
        @endif
    </div></section>
    <section class="section section--muted" id="before-paying"><div class="container perc-columns"><div>
        <span class="eyebrow">Order the right work</span><h2>Before paying for the evaluation.</h2>
        <ol class="perc-steps">@for(var step : stateMoneyPage.decisionSteps())<li>${step}</li>@endfor</ol>
        @if(sc)
        <a href="https://epermitting.des.sc.gov" target="_blank" rel="noreferrer" data-track-click="nav" data-track-source-context="sc_perc_application" data-track-target-type="official_source">Open SCDES ePermitting ↗</a>
        @else
        <a href="https://www.oregon.gov/deq/residential/pages/onsite-contacts.aspx" target="_blank" rel="noreferrer" data-track-click="nav" data-track-source-context="or_perc_jurisdiction" data-track-target-type="official_source">Find the DEQ or county contact ↗</a>
        @endif
    </div><aside class="perc-quote"><h3>Ask for an itemized quote.</h3><p>“For this parcel and proposed use, please separate the agency fee, pit excavation and restoration, private soil or design work, and any repeat visit. What written result will I receive, and what remains before a construction permit?”</p><ul>@for(var item : stateMoneyPage.quotePrepChecklist())<li>${item}</li>@endfor</ul><p>Save the office’s evaluation and any site plan. A completed visit or paid fee does not guarantee approval of the proposed project.</p></aside></div></section>
    <section class="section" id="existing-file"><div class="container perc-columns"><div><span class="eyebrow">Choose your situation</span><h2>A new site and an existing system need different questions.</h2><h3>Vacant land with no prior evaluation</h3><p>Use the agency’s new-site process. You do not need to invent an old permit or stop simply because no historical septic file exists.</p><h3>Land with an old evaluation</h3><p>Check the parcel, evaluated location and proposed use against today’s project. Ask the office whether changes in the site or project require further review.</p><h3>A home with an installed system</h3><p>An old soil report or permit does not establish current condition. Distinguish record research from an on-site condition inspection, repair evaluation or expansion approval.</p></div><aside class="perc-quote"><h3>Need help understanding an existing file?</h3><p>SepticPath can find existing records and explain their contents. We do not perform soil testing or issue permits.</p><a href="${com.example.septic.web.RecordHelpEntry.of("review").link(stateMoneyPage.path(state.slug()), state.stateCode(), null)}" data-record-help-cta data-track-click="nav" data-track-source-context="${sc ? "sc_perc_existing_file" : "or_perc_existing_file"}" data-track-target-type="lead_form">Get free record help →</a><p>Research and explanation are free. Agency fees require your approval.</p><a href="/septic-records-checklist/${state.slug()}/">Find existing ${state.stateName()} septic records →</a></aside></div></section>
    <section class="section section--tight"><div class="container"><h2>Cost questions, answered.</h2>@for(var faq : stateMoneyPage.faqBlocks())<details class="perc-faq"><summary>${faq.question()}</summary><p>${faq.answer()}</p></details>@endfor</div></section>
    <section class="section section--muted" id="cost-sources"><div class="container"><h2>Official sources and scope.</h2><p>Fee and process review: November 18, 2026. These are published agency charges, not a survey of private contractor prices.</p><ul>
        @if(sc)
        <li><a href="https://www.des.sc.gov/permits-regulations/septic-tanks/permits-licenses/septic-tanks-residential-single-home-builder" target="_blank" rel="noreferrer">SCDES residential septic application, fee and site-evaluation instructions</a></li>
        @else
        <li><a href="https://www.oregon.gov/deq/wq/regulation/Pages/table9fees.aspx" target="_blank" rel="noreferrer">Oregon DEQ Table 9A: current and previous onsite fees</a></li>
        <li><a href="https://www.oregon.gov/deq/Residential/Pages/Onsite-about.aspx" target="_blank" rel="noreferrer">Oregon DEQ: site evaluation, test pits and separate construction permit</a></li>
        @endif
    </ul><p><a href="/perc-test-cost/">Compare the national perc-test cost guide →</a> · <a href="/septic-system-cost-calculator/${state.slug()}/">Plan the wider installation budget →</a></p></div></section>
</div>
`)
