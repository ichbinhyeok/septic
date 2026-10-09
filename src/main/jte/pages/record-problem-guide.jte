@import com.example.septic.data.model.ContentPage
@import com.example.septic.web.PageMeta
@import com.example.septic.web.RecordHelpEntry
@param PageMeta page
@param ContentPage contentPage
@param boolean reading = "how-to-read-septic-as-built".equals(contentPage.slug())
@param String sourcePath = "/" + contentPage.slug() + "/"

@template.layouts.app(page = page, designFamily = "editorial", pageStyles = "/record-problem-guide.css", content = @`
<main id="main-content" class="problem-guide">
    <section class="problem-hero">
        <div class="container problem-hero__inner">
            <div class="problem-hero__copy">
                <span class="eyebrow">SepticPath / ${reading ? "Understand your file" : "Continue your search"}</span>
                <h1>${contentPage.title()}</h1>
                <p class="lede">${contentPage.introCopy()}</p>
                <a class="button button--primary" href="${RecordHelpEntry.forGuide(contentPage.slug()).link(sourcePath)}" data-record-help-cta data-track-click="nav" data-track-source-context="${contentPage.slug() + "_hero"}" data-track-target-type="lead_form">${reading ? "Send my file for an explanation" : "Help find my missing record"}</a>
                <p class="problem-hero__terms">Records and explanation are free. Agency fees only with your approval.</p>
                <a class="problem-text-link" href="#start">${reading ? "Read the drawing step by step" : "Follow the search steps yourself"} ↓</a>
            </div>
            @if (reading)
            <figure class="problem-plan">
                <span class="problem-plan__label">A drawing is a set of clues.</span>
                <img class="problem-plan__image" src="/images/guides/as-built-reading-v1.webp" width="1536" height="1024" fetchpriority="high" alt="Illustrative paper drawing: a house connects to a septic tank, distribution box and three field lines. Sage highlights identify the components and distance labels; title, date and approval fields show what to check on the record. Not an actual property plan.">
                <nav class="problem-plan__keys" aria-label="Drawing labels"><a href="#components">01 Components</a><a href="#dimensions">02 Dimensions</a><a href="#field-lines">03 Field lines</a><a href="#document-stage">04 Record stage</a></nav>
                <figcaption>SepticPath reading illustration. Not a property plan; not to scale. Symbols vary by file.</figcaption>
            </figure>
            @else
            <div class="problem-search" aria-label="The search sequence after an empty result">
                <span class="problem-plan__label">Where the search goes next</span>
                <ol>
                    <li><span>01</span><div><strong>Current address</strong><small>No matching result? Keep going.</small></div></li>
                    <li><span>02</span><div><strong>Parcel + older identifiers</strong><small>Old address, former owner, subdivision or lot</small></div></li>
                    <li><span>03</span><div><strong>Attachments + archives</strong><small>Drawing, inspection, repair or paper file</small></div></li>
                    <li><span>04</span><div><strong>The office that holds the file</strong><small>A focused request, with the search history</small></div></li>
                </ol>
                <p>Search sequence—not a promise that a file will be found.</p>
            </div>
            @endif
        </div>
    </section>

    <section class="problem-body section" id="start" data-reading-guide="${contentPage.slug()}">
        <div class="container problem-body__grid">
            <div>
                @if (reading)
                <span class="eyebrow">Read in this order</span>
                <h2>Four checks before you rely on the drawing.</h2>
                <p>First match the address, parcel or lot across every page. A correctly addressed agency email does not make every attachment the right property.</p>
                <article id="components" class="problem-step"><span>01</span><div><h3>Identify the tank, field and other components.</h3><p>Use the labels and legend. A tank may be shown as a rectangle; D-box may identify a distribution box. A separate pump chamber, treatment unit or reserve area needs its own label. Do not assign a meaning to an unmarked shape.</p></div></article>
                <article id="dimensions" class="problem-step"><span>02</span><div><h3>Read each number with its units and endpoints.</h3><p>A distance from a house corner to a tank is different from a field-line length. Read gallons, feet and gallons per day separately. If the scale, reference point or north arrow is missing, keep that uncertainty visible.</p></div></article>
                <article id="field-lines" class="problem-step"><span>03</span><div><h3>Separate line length, area and capacity.</h3><p>Only add dimensions that clearly label separate field runs. A total in linear feet is not square footage. Tank gallons and design flow do not, by themselves, establish the approved bedroom count; find that approval in the permit text.</p></div></article>
                <article id="document-stage" class="problem-step"><span>04</span><div><h3>Check whether the page describes planned or installed work.</h3><p>A construction permit describes authorized work. An as-built or installation record describes the recorded arrangement. Read the final approval and later repair pages alongside the plan. If values conflict, keep each value attached to its page and ask which record controls.</p></div></article>
                <div class="problem-example">
                    <span class="eyebrow">Worked example / existing public case</span>
                    <h3>60 + 80 + 85 feet = 225 linear feet.</h3>
                    <p>In our published Tennessee example, those lengths come from the sketch. The 900-gallon tank and three-bedroom design come from the accompanying permit. We explain each fact with its source rather than measuring the picture.</p>
                    <a href="/septic-as-built-records/#drawing-labels" data-track-click="nav" data-track-source-context="reading_original_example" data-track-target-type="case_study">Open the actual historical sketch and its explanation →</a>
                </div>
                <p class="problem-note">Use a historical drawing as a starting point for a local professional. It does not establish today's condition or authorize excavation.</p>
                @else
                <span class="eyebrow">Start with the result you actually saw</span>
                <h2>What stopped your search?</h2>
                <div class="problem-outcomes">
                    <details open><summary>No matching address or parcel</summary><p>Confirm the county and current parcel in the official assessor or GIS source. Retry the septic index with the search keys it accepts: permit number, shorter street wording, former owner, old road name, subdivision or lot. Keep candidate results separate until their property identifiers match.</p><a href="/septic-permit-search-by-address/#old-address-records">Follow the older-address search →</a></details>
                    <details><summary>The permit appears, but no drawing opens</summary><p>Save the permit number. Check for separate attachments and related inspection or repair entries. Ask the custodian specifically for the as-built, site sketch, approved plan and final approval pages. A missing scan and an absent file are different findings.</p><a href="/septic-as-built-records/">Find the drawing behind the permit →</a></details>
                    <details><summary>The official viewer will not load</summary><p>Save the exact URL and error. Check the agency's current records page for a replacement viewer or alternate records request. An access error, including a 403, does not establish whether a property file exists.</p><a href="/tdec-septic-records/">TDEC viewer and existing-file request routes →</a></details>
                    <details><summary>The office says it has no record</summary><p>Keep the written response and search scope. Ask whether the office checked older paper files, prior owner or parcel identifiers, repair records, and any predecessor or separate custodian. If that search is complete, describe the remaining gap rather than implying the file must exist somewhere.</p><a href="/septic-records-by-county/">Check the county's records route →</a></details>
                </div>
                <div class="problem-example">
                    <span class="eyebrow">Before another request</span>
                    <h3>Give the office a precise file search.</h3>
                    <p>Include the full address and county, optional parcel or permit number, identifiers already searched and the missing document. Ask for the existing permit, layout, installation or final approval and later repairs—not a new permit application.</p>
                    <p>If you already have a request number, follow up in that request with the missing information. Keep the agency's stated response date alongside your own deadline.</p>
                    <a href="/septic-records-request-builder/" data-track-click="nav" data-track-source-context="missing_request_builder" data-track-target-type="internal_tool">Prepare the records request →</a>
                </div>
                <p><a href="/septic-record-brief-example/#sale-case" data-track-click="nav" data-track-source-context="missing_completed_case" data-track-target-type="case_study">See how a public-search dead end became a returned file →</a></p>
                @endif
            </div>
            <aside class="problem-help">
                <span class="eyebrow">Let us take it from here</span>
                <h2>${reading ? "Your file. Your question." : "Send the address. We’ll investigate."}</h2>
                <p>${reading ? "Share the complete file and tell us what you need to understand. We check the property match, connect the pages and explain the findings." : "We check public sources and alternate identifiers, find the relevant records office, and request the existing file when needed."}</p>
                <h3>You receive</h3>
                <ul><li>${reading ? "Plain-English answers linked to the source pages" : "The records we find and a plain-English explanation"}</li><li>Conflicts, missing pages and unanswered questions identified</li><li>A useful next step for the office or local professional</li></ul>
                <a class="button button--primary" href="${RecordHelpEntry.forGuide(contentPage.slug()).link(sourcePath)}" data-record-help-cta data-track-click="nav" data-track-source-context="${contentPage.slug() + "_handoff"}" data-track-target-type="lead_form">${reading ? "Send my file" : "Continue my search"}</a>
                <p class="problem-help__terms">Free records and explanation. Any agency fee needs your approval. Agency response time controls files held only by that office.</p>
                <a href="${reading ? "/no-septic-records-found/" : "/how-to-read-septic-as-built/"}">${reading ? "Still missing the file? Start here →" : "Already have the drawing? Read it here →"}</a>
            </aside>
        </div>
    </section>

    <section class="section problem-faq">
        <div class="container problem-body__grid">
            <div><span class="eyebrow">Common questions</span><h2>${reading ? "Before you use the numbers." : "Before you call the record missing."}</h2>
                @for (var faq : contentPage.faqBlocks())
                <details><summary>${faq.question()}</summary><p>${faq.answer()}</p></details>
                @endfor
            </div>
            <div class="problem-sources"><h3>Official guidance behind this guide</h3>
                @if (reading)
                <p><a href="https://www.snohd.org/159/As-Built-Records" target="_blank" rel="noreferrer">Snohomish County: as-built records</a><br>Explains the information an as-built can contain and where to ask for help reading records.</p>
                @else
                <p><a href="https://www.healthygallatin.org/environmental-health/water-quality/wastewater-search-information/" target="_blank" rel="noreferrer">Gallatin County: permit search instructions</a><br>Lists older identifiers and explains that an unsuccessful search does not establish a missing record.</p>
                <p><a href="https://www.snohd.org/159/As-Built-Records" target="_blank" rel="noreferrer">Snohomish County: records absent online</a><br>Explains that additional files may be held by the Health Department.</p>
                @endif
                <p><a href="https://www.epa.gov/septic/frequent-questions-septic-systems" target="_blank" rel="noreferrer">EPA: septic system questions</a><br>Directs owners to local record drawings, permits and appropriate local help.</p>
                <p>County examples explain specific procedures; they do not establish a nationwide search policy. Reviewed ${contentPage.reviewedAt()}.</p>
                <p><a href="/methodology/">Our research method →</a></p>
            </div>
        </div>
    </section>
</main>
`)
