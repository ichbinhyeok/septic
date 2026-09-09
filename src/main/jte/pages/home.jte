@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.PageMeta
@param PageMeta page
@param java.util.List<StateProfile> featuredStates
@param java.util.List<StateProfile> spotlightStates
@param java.util.List<com.example.septic.web.PageLink> featuredIntentPages = java.util.List.of()
@param java.util.List<com.example.septic.web.CountyFinderLinkView> countyFinderLinks = java.util.List.of()
@param java.util.List<com.example.septic.web.CountyRouteClusterView> countyRouteClusters = java.util.List.of()
@param int liveGuideCount
@param int liveIntentCount
@param int liveCountyCount = 0
@param int totalCountyRouteCount = 0
@param int countySpecificRouteCount = 0
@param int preparedFieldPackCount = 0
@param int countyBackedStateCount = 0
@param int queuedStateCount
@param com.example.septic.web.WorkflowNetworkSnapshotView workflowNetworkSnapshot = null

@template.layouts.app(page = page, content = @`
    <section class="home-service-hero" aria-labelledby="home-service-title">
        <div class="container home-service-hero__grid">
            <div class="home-service-hero__copy">
                <span class="eyebrow">Human septic record research</span>
                <h1 id="home-service-title">Send the address.<br>We’ll investigate the septic record.</h1>
                <p class="lede">We search public sources, verify the property, contact the responsible office when needed, and turn the response into a clear record summary.</p>
                <div class="home-service-hero__actions">
                    <a class="button button--primary"
                       href="/offer-prep-septic-file-check/#record-help"
                       data-record-help-cta
                       data-track-click="nav"
                       data-track-source-context="home_service_hero"
                       data-track-target-type="lead_form">Ask SepticPath to investigate</a>
                    <a class="text-button" href="#self-serve-records" data-track-click="nav" data-track-source-context="home_service_hero" data-track-target-type="internal_tool">Search public records myself</a>
                </div>
                <ul class="home-service-hero__signals" aria-label="What SepticPath handles">
                    <li><strong>Human-reviewed</strong><span>Not an automated link list</span></li>
                    <li><strong>Agency follow-up</strong><span>When the public route stops</span></li>
                    <li><strong>Clear findings</strong><span>Original records plus plain English</span></li>
                </ul>
                <small class="home-service-hero__boundary">Record availability and response times vary. SepticPath is an independent research service, not an inspection or permit authority.</small>
            </div>

            <aside class="service-dossier" aria-label="Example SepticPath record summary">
                <div class="service-dossier__topline">
                    <span>REPRESENTATIVE RECORD SUMMARY</span>
                    <span>DETAILS ADAPTED</span>
                </div>
                <div class="service-dossier__status">
                    <span aria-hidden="true">✓</span>
                    <div><small>Research outcome</small><strong>Agency record located</strong></div>
                </div>
                <dl class="service-dossier__facts">
                    <div><dt>Property</dt><dd>Address and parcel matched</dd></div>
                    <div><dt>Source</dt><dd>Responsible public agency</dd></div>
                    <div><dt>Files</dt><dd>Permit + system drawing</dd></div>
                </dl>
                <div class="service-dossier__sketch" aria-hidden="true">
                    <span class="service-dossier__house">HOUSE</span>
                    <span class="service-dossier__line"></span>
                    <span class="service-dossier__tank">TANK</span>
                    <span class="service-dossier__field">DISPOSAL AREA</span>
                    <span class="service-dossier__annotation">Location clues highlighted</span>
                </div>
                <div class="service-dossier__finding">
                    <small>What we explain</small>
                    <p>The original file, the facts it supports, what remains unknown, and the next useful step.</p>
                </div>
                <div class="service-dossier__footer"><span>Original source preserved</span><span>Plain-English summary included</span></div>
            </aside>
        </div>
    </section>

    <section class="section home-case" aria-labelledby="home-case-title">
        <div class="container">
            <header class="home-case__header">
                <div>
                    <span class="eyebrow">Completed request · identifying details changed</span>
                    <h2 id="home-case-title">The public search failed. We retrieved the permit and explained the layout.</h2>
                </div>
                <p>Built from the agency reply and historical file returned in a real Tennessee request. Personal details were replaced; the document type, system facts, and outcome were preserved.</p>
            </header>

            <div class="home-case__body">
                <div class="case-evidence" aria-label="Reconstructed example of an agency reply, returned record, and annotated system drawing">
                    <div class="case-evidence__desk-label">
                        <span>COMPLETED CASE / TENNESSEE</span>
                        <strong>Actual workflow · anonymized evidence</strong>
                    </div>

                    <article class="case-document case-document--record" aria-label="Anonymized reconstruction of the returned historical permit">
                        <div class="case-document__masthead">
                            <span>RETURNED AGENCY FILE</span>
                            <small>PAGE 02 / 03</small>
                        </div>
                        <p class="case-document__agency">HISTORICAL FORM · 1970s RECORD</p>
                        <h3>Permit for construction of subsurface sewage disposal system</h3>
                        <div class="case-permit-form">
                            <p><b class="case-form-check">✓</b> Standard System <span></span> Alternating System <span></span> Other</p>
                            <p>Issued To: <em>[NAME REPLACED]</em></p>
                            <p>Authorized at: <em>[PROPERTY ADDRESS REPLACED]</em></p>
                            <p>Septic tank of <em>900</em> gallons with <em>225</em> linear feet of field line</p>
                            <p>in <em>3</em> trenches, <em>36</em> inches wide and <em>30</em> inches deep</p>
                            <p>designed for Residential · <em>3 bedrooms</em></p>
                        </div>
                        <div class="case-document__stamp">HISTORICAL<br>RECORD</div>
                        <small class="case-document__facsimile">Reconstructed from returned file · names, address, dates, signatures changed</small>
                    </article>

                    <article class="case-document case-document--email" aria-label="Anonymized reconstruction of the actual agency response email">
                        <div class="case-email__toolbar"><i></i><i></i><i></i><span>ACTUAL REPLY · PERSONAL DETAILS REMOVED</span></div>
                        <dl class="case-email__headers">
                            <div><dt>From</dt><dd>Tennessee public records team</dd></div>
                            <div><dt>To</dt><dd>requester@redacted.email</dd></div>
                            <div><dt>Subject</dt><dd>Septic file request — [ADDRESS REPLACED]</dd></div>
                        </dl>
                        <div class="case-email__message">
                            <p>Good afternoon and thank you for your request.</p>
                            <p><strong>Please find attached the septic file you requested.</strong></p>
                            <p class="case-email__limit">The documents reflect historical agency files and do not attest to the system’s current functionality.</p>
                        </div>
                        <div class="case-email__attachment"><span>PDF</span><div><strong>historical_septic_file.pdf</strong><small>3 pages · attached by agency</small></div></div>
                    </article>

                    <article class="case-document case-document--plan" aria-label="Anonymized reconstruction of the handwritten agency sketch with SepticPath annotation">
                        <div class="case-plan__title"><strong>ACTUAL AGENCY RECORD RETURNED FOR A SEPTICPATH REQUEST</strong><span>IDENTIFIERS CROPPED</span></div>
                        <figure class="case-plan__scan">
                            <img src="/images/case-study/tn-historical-sketch-anonymized.png"
                                 alt="Cropped portion of the actual historical agency sketch showing a driveway, house, tank, and three field lines labeled 60, 80, and 85 feet"
                                 width="1110"
                                 height="560"
                                 loading="lazy">
                            <span class="case-plan__scan-mark" aria-hidden="true"></span>
                            <figcaption>Original agency scan · identifying header and road label excluded from the crop</figcaption>
                        </figure>
                        <ul class="case-plan__facts" aria-label="Facts extracted from the returned record">
                            <li><strong>900 gal</strong><span>Septic tank</span></li>
                            <li><strong>3 bedrooms</strong><span>Design basis</span></li>
                            <li><strong>225 ft</strong><span>Three field lines</span></li>
                        </ul>
                        <div class="case-plan__note"><span>SEPTICPATH NOTE 01</span><p>The sketch places the tank immediately right of the house, about 5 ft away, with three field lines labeled 60, 80, and 85 ft.</p></div>
                    </article>

                    <p class="case-evidence__disclosure">Based on the actual reply and returned file. Personal details changed; system specifications retained.</p>
                </div>

                <aside class="home-case__outcome" aria-label="Representative case outcome">
                    <div class="home-case__outcome-topline"><span>CASE OUTCOME</span><strong>DELIVERED</strong></div>
                    <p>A dead-end public search became a usable evidence package.</p>
                    <dl class="home-case__verified-facts">
                        <div><dt>Record</dt><dd>Historical construction permit</dd></div>
                        <div><dt>Design</dt><dd>3 bedrooms · 900-gallon tank</dd></div>
                        <div><dt>Field</dt><dd>225 ft · 3 trenches</dd></div>
                        <div><dt>Location clue</dt><dd>Tank drawn beside house</dd></div>
                        <div><dt>Important limit</dt><dd>Does not prove current condition</dd></div>
                    </dl>
                    <div class="home-case__delivered">
                        <span>DELIVERED TO REQUESTER</span>
                        <strong>Original PDF · annotated plan · plain-English brief</strong>
                    </div>
                    <a class="button button--primary"
                       href="/offer-prep-septic-file-check/#record-help"
                       data-record-help-cta
                       data-track-click="nav"
                       data-track-source-context="home_representative_case"
                       data-track-target-type="lead_form">Start with my property address</a>
                    <small>Records can be unavailable, incomplete, or require owner authorization or a fee. We’ll explain the next step if that happens.</small>
                </aside>
            </div>
        </div>
    </section>

    <section class="section section--tight home-workflow" aria-labelledby="home-workflow-title">
        <div class="container">
            <div class="section-heading home-section-heading">
                <span class="eyebrow">One request. A complete research path.</span>
                <h2 id="home-workflow-title">We keep working when the public search stops.</h2>
            </div>
            <ol class="home-workflow__steps">
                <li>
                    <span>1</span>
                    <div>
                        <h3>Research every credible source</h3>
                        <p>We match the address, parcel, jurisdiction, and likely record owner across available public sources.</p>
                    </div>
                </li>
                <li>
                    <span>2</span>
                    <div>
                        <h3>Request the missing record</h3>
                        <p>If the online trail ends, we prepare and send a focused request to the responsible public office when appropriate.</p>
                    </div>
                </li>
                <li>
                    <span>3</span>
                    <div>
                        <h3>Return a useful answer</h3>
                        <p>You receive the original response, the key facts in plain English, and a clear explanation of what to do next.</p>
                    </div>
                </li>
            </ol>
        </div>
    </section>

    <section class="section home-deliverable" aria-labelledby="home-deliverable-title">
        <div class="container home-deliverable__grid">
            <div>
                <span class="eyebrow">The deliverable</span>
                <h2 id="home-deliverable-title">Not another search result. A property-specific record brief.</h2>
                <p class="lede">We preserve the source material and separate confirmed facts from assumptions, so you know exactly what the record does—and does not—say.</p>
            </div>
            <ul class="home-deliverable__list">
                <li><span>01</span><strong>Verified property and jurisdiction match</strong></li>
                <li><span>02</span><strong>Original agency files and response</strong></li>
                <li><span>03</span><strong>Permit, bedroom, layout, and history facts extracted</strong></li>
                <li><span>04</span><strong>Location clues annotated when a drawing exists</strong></li>
                <li><span>05</span><strong>Unknowns and next professional step made explicit</strong></li>
            </ul>
        </div>
    </section>

    <section class="section section--tight home-self-serve" id="self-serve-records" aria-labelledby="home-self-serve-title">
        <div class="container">
            @template.tags.addressRecordFinder(
                finderId = "home-address-record-finder",
                eyebrow = "Self-serve alternative",
                heading = "Prefer to search public records yourself?",
                intro = "Start with the property address and we’ll route you to the most likely public record source.",
                pageHeading = false,
                totalCountyRouteCount = totalCountyRouteCount,
                minimal = true
            )
        </div>
    </section>

    <section class="section section--tight home-intents" aria-labelledby="home-intents-title">
        <div class="container">
            <div class="section-heading home-section-heading">
                <span class="eyebrow">Start with your question</span>
                <h2 id="home-intents-title">What are you trying to learn about the property?</h2>
            </div>
            <nav class="home-intent-list" aria-label="Common septic record questions">
                <a href="/septic-permit-search-by-address/">
                    <span><strong>Does this address have a septic permit?</strong><small>Find the county route and the identifiers that make the search work.</small></span>
                    <span>Find the permit →</span>
                </a>
                <a href="/septic-as-built-records/">
                    <span><strong>Where are the tank and drain field?</strong><small>Ask for the as-built, site sketch, or installed layout instead of a generic permit copy.</small></span>
                    <span>Find the drawing →</span>
                </a>
                <a href="/septic-bedroom-permit-checker/">
                    <span><strong>How many bedrooms were approved?</strong><small>Separate the listing count from the bedroom capacity supported by the official file.</small></span>
                    <span>Check bedrooms →</span>
                </a>
                <a href="/septic-system-cost-calculator/">
                    <span><strong>What could repair or replacement cost?</strong><small>Use a planning range and see which missing facts can still move it.</small></span>
                    <span>Plan the cost →</span>
                </a>
            </nav>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container">
            <div class="section-heading home-section-heading">
                <span class="eyebrow">High-demand record workflows</span>
                <h2>Start with the routes that already do more than explain.</h2>
                <p class="lede">Search demand is strongest on these paths, and the county routes below include a reviewed handoff or prepared official field pack. Starting-point-only examples remain visible after them.</p>
                <p class="home-route-depth" data-nosnippet><strong>${countySpecificRouteCount}</strong> county-specific handoffs · <strong>${preparedFieldPackCount}</strong> verified field packs · <strong>${Math.max(0, totalCountyRouteCount - countySpecificRouteCount)}</strong> official starting points</p>
            </div>
            <div class="home-route-list">
                <a href="/tdec-septic-records/" data-track-click="nav" data-track-source-context="home_priority_routes" data-track-target-type="internal_page">
                    <span><strong>Tennessee TDEC records</strong><small>Program, field-office, and public-records fallbacks · direct search may block</small></span>
                    <span aria-hidden="true">Open →</span>
                </a>
                <a href="/septic-records-checklist/virginia/prince-william-county/" data-track-click="nav" data-track-source-context="home_priority_routes" data-track-target-type="county_records_page">
                    <span><strong>Prince William County, Virginia</strong><small>Historical address/GPIN search · county-specific workflow</small></span>
                    <span aria-hidden="true">Open →</span>
                </a>
                <a href="/septic-records-checklist/texas/tarrant-county/" data-track-click="nav" data-track-source-context="home_priority_routes" data-track-target-type="county_records_page">
                    <span><strong>Tarrant County, Texas</strong><small>OSSF authority check and request handoff · county-specific workflow</small></span>
                    <span aria-hidden="true">Open →</span>
                </a>
                <a href="/septic-records-checklist/tennessee/hamilton-county/" data-track-click="nav" data-track-source-context="home_priority_routes" data-track-target-type="county_records_page">
                    <span><strong>Hamilton County, Tennessee</strong><small>Free document retrieval and email fallback · county-specific workflow</small></span>
                    <span aria-hidden="true">Open →</span>
                </a>
                <a href="/septic-records-checklist/north-carolina/alamance-county/" data-track-click="nav" data-track-source-context="home_priority_routes" data-track-target-type="county_records_page">
                    <span><strong>Alamance County, North Carolina</strong><small>Official request PDF and verified fields · prepared field pack</small></span>
                    <span aria-hidden="true">Open →</span>
                </a>
                <a href="/septic-records-checklist/tennessee/knox-county/" data-track-click="nav" data-track-source-context="home_priority_routes" data-track-target-type="county_records_page">
                    <span><strong>Knox County, Tennessee</strong><small>Live file-search form and verified fields · prepared field pack</small></span>
                    <span aria-hidden="true">Open →</span>
                </a>
                <a href="/septic-records-checklist/north-carolina/lincoln-county/" data-track-click="nav" data-track-source-context="home_priority_routes" data-track-target-type="county_records_page">
                    <span><strong>Lincoln County, North Carolina</strong><small>Phone-first intake and portal fallback · county-specific workflow</small></span>
                    <span aria-hidden="true">Open →</span>
                </a>
                <a href="/septic-records-checklist/north-carolina/wake-county/" data-track-click="nav" data-track-source-context="home_priority_routes" data-track-target-type="county_records_page">
                    <span><strong>Wake County, North Carolina</strong><small>iMAPS parcel match and Permit Search attachments · verified field pack</small></span>
                    <span aria-hidden="true">Open →</span>
                </a>
                <a href="/septic-records-checklist/indiana/st-joseph-county/" data-track-click="nav" data-track-source-context="home_priority_routes" data-track-target-type="county_records_page">
                    <span><strong>St. Joseph County, Indiana</strong><small>Official schematic PDF and verified fields · prepared field pack</small></span>
                    <span aria-hidden="true">Open →</span>
                </a>
                <a href="/septic-records-checklist/south-carolina/greenville-county/" data-track-click="nav" data-track-source-context="home_starting_point_routes" data-track-target-type="county_records_page">
                    <span><strong>Greenville County, South Carolina</strong><small>SCDES guidance and TMS clues · starting-point route</small></span>
                    <span aria-hidden="true">Open →</span>
                </a>
            </div>
        </div>
    </section>

    <section class="section section--tight section--muted">
        <div class="container">
            <div class="section-heading home-section-heading">
                <span class="eyebrow">If the record is missing</span>
                <h2>Finish the job without starting over.</h2>
            </div>
            <div class="home-tool-grid">
                <article>
                    <span class="home-tool-grid__number">01</span>
                    <h3>Ask for the right file</h3>
                    <p>Build a clear request for the permit, as-built, final approval, or repair history.</p>
                    <a href="/septic-records-request-builder/#records-request-builder">Create a records request</a>
                </article>
                <article>
                    <span class="home-tool-grid__number">02</span>
                    <h3>Check approved bedrooms</h3>
                    <p>Use the permit details to understand whether the septic approval may limit bedroom count.</p>
                    <a href="/septic-bedroom-permit-checker/">Check bedroom capacity</a>
                </article>
                <article>
                    <span class="home-tool-grid__number">03</span>
                    <h3>Plan the likely cost</h3>
                    <p>Get a broad planning range and see exactly which unknowns can move the price.</p>
                    <a href="/septic-system-cost-calculator/">Estimate septic cost</a>
                </article>
            </div>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container">
            <details class="home-directory">
                <summary>
                    <span>
                        <strong>Search by county instead</strong>
                        <small>Use this if you do not have the full property address.</small>
                    </span>
                    <span aria-hidden="true">Browse ${totalCountyRouteCount} county paths</span>
                </summary>
                @template.tags.countyFinder(
                    countyFinderLinks = countyFinderLinks,
                    finderId = "home-county-finder",
                    eyebrow = "County search",
                    heading = "Search by county or state.",
                    body = "Choose a result to open the local permit, records request, parcel, as-built, or inspection-letter page.",
                    sourceContext = "home_county_finder",
                    totalCountyRouteCount = totalCountyRouteCount
                )
            </details>
            <div class="home-coverage-links">
                <a href="/states/">Browse all state guides</a>
                <a href="/coverage/">See records coverage</a>
                <a href="/methodology/">How we verify sources</a>
            </div>
        </div>
    </section>
`)
