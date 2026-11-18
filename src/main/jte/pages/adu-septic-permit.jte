@import com.example.septic.web.PageMeta
@import com.example.septic.web.RecordHelpEntry
@param PageMeta page

@template.layouts.app(page = page, designFamily = "editorial", pageStyles = "/record-work-story.css", content = @`
    <article class="work-story work-story--intent">
        <header class="work-story__header container">
            <h1>Can an ADU share an existing septic system?</h1>
            <p class="work-story__lede">Sometimes. For an accessory dwelling unit (ADU), the local reviewer checks what the system was approved to serve, whether it can handle a second dwelling, and where the new work would go. Start with your property's septic file before you pay for plans.</p>
        </header>

        <section class="container" aria-label="ADU septic records lookup">
            @template.tags.addressRecordFinder(
                finderId = "adu-septic-records",
                eyebrow = "",
                heading = "Find your septic permit route",
                intro = "Choose your project and enter the address. Get the county records route and a checklist for the local review.",
                minimal = true,
                aduProject = true
            )
        </section>

        <section class="work-story__investigation container" aria-labelledby="adu-records-heading">
            <div class="work-story__sticky">
                <h2 id="adu-records-heading">What to check before you build</h2>
                <p>Bring the existing record and the proposed project to the local septic reviewer.</p>
            </div>
            <ol>
                <li><span aria-hidden="true">1</span><div>
                    <h3>What was the system approved to serve?</h3>
                    <p>Find the approved use, bedroom count or design flow in the permit or final approval. A label on the tank gives its volume. Use the permit to check the approved capacity of the whole system.</p>
                </div></li>
                <li><span aria-hidden="true">2</span><div>
                    <h3>Where are the drain field and reserve area?</h3>
                    <p>Use the installed drawing and later repair records to make a starting site plan. Include the proposed footprint and connection. Have the locations verified before a design depends on them.</p>
                </div></li>
                <li><span aria-hidden="true">3</span><div>
                    <h3>Who reviews the proposed connection?</h3>
                    <p>Ask whether the existing system can serve the project, whether it needs a field evaluation, and whether an expansion or separate system must be permitted. If a search comes back empty, ask the records office about archived files and what it needs to review an undocumented system.</p>
                </div></li>
            </ol>
        </section>

        <section class="work-story__findings container" aria-labelledby="adu-local-heading">
            <h2 id="adu-local-heading">How the process varies by location</h2>
            <h3>Raleigh, North Carolina</h3>
            <p>Raleigh separates <a href="https://raleighnc.gov/permits/services/building-accessory-dwelling-unit-adu/adu-change-use">conversions of existing space</a> from <a href="https://raleighnc.gov/permits/services/building-new-single-family-dwelling-or-duplex">new detached ADUs</a>. Both city guides direct properties with private septic to Wake County Environmental Services for approval before the city permit application. <a href="https://raleighnc.gov/planning/services/annexations/extraterritorial-jurisdiction-etj">Check your parcel's planning jurisdiction</a> before using the Raleigh steps.</p>
            <h3>Oregon</h3>
            <p>Oregon's DEQ says connecting an ADU to an existing system requires an authorization notice. An onsite specialist reviews the condition and whether the system can handle the proposed use. <a href="https://www.oregon.gov/deq/Residential/Pages/residentialseptic.aspx">See Oregon's residential septic permitting guide</a>.</p>
        </section>

        <section class="work-story__next container" aria-labelledby="adu-help-heading">
            <div>
                <h2 id="adu-help-heading">Have an old permit or a missing drawing?</h2>
                <p>Tell us what you are planning and which records you have. We can help find the file and explain what it says so you know what to ask the local reviewer.</p>
            </div>
            <div>
                <a class="button button--primary" href="${RecordHelpEntry.of("building").link("/adu-septic-permit/")}"
                   data-record-help-cta data-track-click="nav" data-track-source-context="adu_septic_guide"
                   data-track-target-type="lead_form">Get help with my septic records</a>
                <small>Records research and explanation are free. Any agency fee needs your approval.</small>
            </div>
        </section>

        <nav class="work-story__related container" aria-label="Related septic records guides">
            <a href="/septic-as-built-records/">Find the installed septic drawing</a>
            <a href="/septic-permit-process/">How septic permitting works</a>
        </nav>
    </article>
`)
