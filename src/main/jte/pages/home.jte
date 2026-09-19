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

@template.layouts.app(page = page, designFamily = "signature", content = @`
    <section class="home-v2-hero" aria-labelledby="home-v2-title">
        <div class="home-v2-hero__image" aria-hidden="true"></div>
        <div class="home-v2-hero__veil" aria-hidden="true"></div>
        <div class="container home-v2-hero__body">
            <div class="home-v2-hero__copy">
                <span class="home-v2-kicker">Public record research</span>
                <h1 id="home-v2-title">Send the address.<br><em>We’ll find the record</em><br>and explain the answer.</h1>
                <p>Permits, drawings, agency replies—and the questions the file still cannot answer.</p>
                <div class="home-v2-hero__actions">
                    <a class="button home-v2-button--light"
                       href="/offer-prep-septic-file-check/#record-help"
                       data-record-help-cta
                       data-track-click="nav"
                       data-track-source-context="home_v2_hero"
                       data-track-target-type="lead_form">Start with an address <span aria-hidden="true">→</span></a>
                    <a class="home-v2-text-link"
                       href="/septic-record-brief-example/"
                       data-track-click="nav"
                       data-track-source-context="home_v2_hero"
                       data-track-target-type="case_study">See an actual completed brief <span aria-hidden="true">↗</span></a>
                </div>
                <small>Research and agency requests are free. Optional $29 result unlock.</small>
                <span class="home-v2-seo-copy">Septic records.<br>Clear answers.</span>
                <span class="home-v2-seo-copy">Ask SepticPath to investigate</span>
                <a class="home-service-hero__self-serve" href="#self-serve-records">Prefer to search public records yourself? <span aria-hidden="true">→</span></a>
            </div>

        </div>
        <div class="home-v2-hero__principles" aria-label="How SepticPath works">
            <span>⌕ <b>We search<br>across sources</b></span>
            <span>▤ <b>We get the<br>actual records</b></span>
            <span>◌ <b>We explain<br>what they mean</b></span>
        </div>
    </section>

    <section class="home-v2-proof" id="home-proof" aria-labelledby="home-v2-proof-title">
        <div class="container home-v2-proof__intro">
            <div>
                <span class="home-v2-kicker">One completed request</span>
                <h2 id="home-v2-proof-title">A public search failed.<br><em>The record did not.</em></h2>
                <p>The public search failed. We retrieved the permit and explained the layout. What looks like a dead end online is often just offline.</p>
            </div>
            <div class="home-v2-before-after" aria-label="A public search result compared with the records SepticPath found">
                <article class="home-v2-browser">
                    <span>Before</span>
                    <div class="home-v2-browser__bar"><i></i><i></i><i></i></div>
                    <p>123 Maple Ridge Road <b>⌕</b></p>
                    <strong>No results found</strong>
                    <small>Try a different search or check your spelling.</small>
                </article>
                <span class="home-v2-before-after__arrow" aria-hidden="true">→</span>
                <article class="home-v2-record-found">
                    <span>After</span>
                    <div class="home-v2-record-found__papers" aria-hidden="true">
                        <i>PERMIT<small>3 bedrooms<br>900-gallon tank</small></i>
                        <i>SITE PLAN<small>House · tank<br>3 field lines</small></i>
                        <i>AGENCY REPLY<small>Records located<br>File attached</small></i>
                    </div>
                    <p><b>✓</b><span><strong>Record found.</strong><small>Permits, drawings, agency replies and more—all in one place.</small></span></p>
                </article>
            </div>
        </div>

        <div class="container home-v2-proof__stage">
            <figure class="home-v2-proof__scan">
                <div class="home-v2-proof__scan-topline"><span>ILLUSTRATIVE RECORD RECONSTRUCTION</span><span>Source facts preserved</span></div>
                <div class="home-v2-proof__diagram" role="img" aria-label="Reconstructed property diagram showing a house, septic tank, and three field lines">
                    <svg viewBox="0 0 900 450" aria-hidden="true">
                        <path class="proof-lot" d="M65 48H835V395H65Z"/>
                        <path class="proof-drive" d="M80 78C186 90 220 120 273 192"/>
                        <path class="proof-house" d="M254 154H472V282H254Z M235 154L362 75L492 154"/>
                        <path class="proof-flow" d="M472 220H565V252H472Z M565 236H670"/>
                        <path class="proof-field" d="M670 150V326 M714 150V326 M758 150V326"/>
                        <circle cx="520" cy="236" r="10"/>
                        <text x="316" y="220">HOUSE</text>
                        <text x="484" y="212">900 GAL TANK</text>
                        <text x="677" y="348">3 × 75 FT FIELD LINES</text>
                    </svg>
                    <span class="home-v2-proof__pin home-v2-proof__pin--house">Property matched</span>
                    <span class="home-v2-proof__pin home-v2-proof__pin--tank">Tank location clue</span>
                    <span class="home-v2-proof__pin home-v2-proof__pin--field">225 ft total</span>
                </div>
                <figcaption>Clean reconstruction for orientation. The downloadable brief links back to the original source record.</figcaption>
            </figure>

            <aside class="home-v2-proof__reading" aria-label="Findings extracted from the record">
                <span class="home-v2-proof__number">01</span>
                <p class="home-v2-proof__label">What the file establishes</p>
                <h3>Three bedrooms.<br>One 900-gallon tank.<br>225 feet of field line.</h3>
                <dl>
                    <div><dt>Property match</dt><dd>Address and agency file aligned</dd></div>
                    <div><dt>Record type</dt><dd>Historical construction permit</dd></div>
                    <div><dt>Location clue</dt><dd>Tank drawn beside the house</dd></div>
                </dl>
                <a href="/septic-record-brief-example/" data-track-click="nav" data-track-source-context="home_v2_proof" data-track-target-type="case_study">Read the complete Record Brief <span aria-hidden="true">→</span></a>
            </aside>

            <div class="home-v2-proof__limit">
                <span>What this does not answer</span>
                <strong>The historical file does not confirm present condition.</strong>
                <p>Current condition is not confirmed. A site inspection is still needed to assess how the system performs today.</p>
            </div>
        </div>

        <section class="home-v2-source-proof" aria-labelledby="home-v2-source-proof-title">
            <div class="container">
                <header class="home-v2-source-proof__header">
                    <div>
                        <span class="home-v2-kicker">Original masked evidence</span>
                        <h2 id="home-v2-source-proof-title">The summary is polished.<br><em>The source stays attached.</em></h2>
                    </div>
                    <p>A separate completed request, shown with identifying details removed. The source document, our reading, and the limits remain distinguishable.</p>
                </header>

                <div class="home-v2-source-proof__story">
                    <figure class="home-v2-source-proof__plan">
                        <span>01 · Original site plan excerpt</span>
                        <img src="/images/case-study/redacted/shelby-site-plan-detail-public.png"
                             alt="Masked historical site plan excerpt showing a proposed four-bedroom residence, 1,000-gallon tank, and field lines"
                             width="560"
                             height="1200"
                             loading="lazy">
                        <figcaption>Handwritten dimensions and layout clues remain visible; identifying details are excluded.</figcaption>
                    </figure>

                    <div class="home-v2-source-proof__translation" aria-label="Facts translated from the original record">
                        <span>02 · What we can responsibly say</span>
                        <dl>
                            <div><dt>Residence</dt><dd><strong>Proposed 4-bedroom residence</strong><small>Written inside the building footprint</small></dd></div>
                            <div><dt>Tank</dt><dd><strong>1,000-gallon concrete tank</strong><small>Drawn beside the residence</small></dd></div>
                            <div><dt>Field</dt><dd><strong>600 ft of field lines</strong><small>Noted directly on the plan</small></dd></div>
                            <div><dt>Boundary</dt><dd><strong>Historical approval, not present condition</strong><small>A current inspection is still a separate step</small></dd></div>
                        </dl>
                    </div>

                    <figure class="home-v2-source-proof__reply">
                        <span>03 · Agency language</span>
                        <img src="/images/case-study/redacted/shelby-approval-letter-excerpt-public.png"
                             alt="Public agency letter excerpt confirming approval of a septic system plot plan"
                             width="1040"
                             height="560"
                             loading="lazy">
                        <figcaption>The agency letter confirms what was approved and explains that changes require further review.</figcaption>
                    </figure>
                </div>

                <details class="home-v2-source-proof__drawer">
                    <summary>
                        <span><strong>Inspect the full masked originals</strong><small>See the source before relying on our explanation.</small></span>
                        <span>Open archive <b aria-hidden="true">＋</b></span>
                    </summary>
                    <div class="home-v2-source-proof__originals">
                        <figure>
                            <img src="/images/case-study/redacted/agency-approval-letter-full-public.png"
                                 alt="Full public agency approval letter with identifying details masked"
                                 width="1275"
                                 height="1650"
                                 loading="lazy">
                            <figcaption>Agency approval letter · identifying details masked</figcaption>
                        </figure>
                        <figure>
                            <img src="/images/case-study/redacted/approved-site-plan-full-public.png"
                                 alt="Full approved septic site plan with identifying details masked"
                                 width="1275"
                                 height="1650"
                                 loading="lazy">
                            <figcaption>Approved site plan · identifying details masked</figcaption>
                        </figure>
                    </div>
                </details>
            </div>
        </section>
    </section>

    <section class="home-v2-service" aria-labelledby="home-v2-service-title">
        <div class="home-v2-service__image" aria-hidden="true"></div>
        <div class="container home-v2-service__body">
            <div class="home-v2-service__headline">
                <span class="home-v2-kicker">One request. A complete research path.</span>
                <h2 id="home-v2-service-title">One request.<br>Every credible path.</h2>
            </div>
            <ol class="home-v2-service__steps">
                <li><span>01</span><div><h3>We identify the right record owner.</h3><p>Address, parcel, county, agency, and likely file are checked before the search begins.</p></div></li>
                <li><span>02</span><div><h3>We keep going when the portal stops.</h3><p>If the record is not online, we send a focused request to the responsible public office.</p></div></li>
                <li><span>03</span><div><h3>You receive the source and the answer.</h3><p>The original file, property-specific findings, conflicts, limits, and next step stay together.</p></div></li>
            </ol>
        </div>
    </section>

    <section class="home-v2-brief" aria-labelledby="home-v2-brief-title">
        <div class="container home-v2-brief__grid">
            <div class="home-v2-brief__copy">
                <span class="home-v2-kicker">The $29 completed package</span>
                <h2 id="home-v2-brief-title">Not another link.<br><em>A usable property record.</em></h2>
                <p><strong>The original record. The answer to your question. What to do next.</strong></p>
                <p>You see the source, the match, the answer to your question, and the line between documented fact and professional judgment.</p>
                <a class="button button--primary" href="/offer-prep-septic-file-check/#record-help" data-record-help-cta data-track-click="nav" data-track-source-context="home_v2_brief" data-track-target-type="lead_form">Start with my address</a>
            </div>
            <div class="home-v2-brief__contents">
                <p class="home-v2-proof__label">Included in every completed brief</p>
                <ol>
                    <li><span>01</span><strong>Verified property and jurisdiction match</strong></li>
                    <li><span>02</span><strong>Original agency files and response</strong></li>
                    <li><span>03</span><strong>Permit, bedroom, layout, and history facts</strong></li>
                    <li><span>04</span><strong>Annotated location clues when a drawing exists</strong></li>
                    <li><span>05</span><strong>Unknowns and the next professional step</strong></li>
                </ol>
            </div>
        </div>
    </section>

    <section class="home-v2-questions" aria-labelledby="home-v2-questions-title">
        <div class="container">
            <header class="home-v2-questions__header">
                <div><span class="home-v2-kicker">Start with the question</span><h2 id="home-v2-questions-title">What are you trying to learn about the property?</h2></div>
                <p>Open a focused route, or send the address and let us trace the file for you.</p>
            </header>
            <nav class="home-v2-questions__list" aria-label="Common septic record questions">
                <a href="/septic-permit-search-by-address/"><span>01</span><strong>Does this property have a septic permit?</strong><em>Find the record</em></a>
                <a href="/septic-as-built-records/"><span>02</span><strong>Where are the tank and drain field?</strong><em>Find the drawing</em></a>
                <a href="/septic-bedroom-permit-checker/"><span>03</span><strong>How many bedrooms were approved?</strong><em>Check capacity</em></a>
                <a href="/septic-system-cost-calculator/"><span>04</span><strong>What could repair or replacement cost?</strong><em>Plan the cost</em></a>
            </nav>
        </div>
    </section>

    <section class="home-v2-routes" aria-labelledby="home-v2-routes-title">
        <div class="container home-v2-routes__grid">
            <header>
                <span class="home-v2-kicker">High-demand record workflows</span>
                <h2 id="home-v2-routes-title">Start with the routes that already do more than explain.</h2>
                <p>These reviewed paths include a county-specific handoff or a prepared official field pack. Other locations remain clearly labeled as a starting-point route.</p>
            </header>
            <nav aria-label="Reviewed county record routes">
                <a href="/septic-records-checklist/virginia/prince-william-county/"><span>Prince William County, Virginia</span><em>County-specific workflow</em></a>
                <a href="/septic-records-checklist/texas/tarrant-county/"><span>Tarrant County, Texas</span><em>County-specific workflow</em></a>
                <a href="/septic-records-checklist/tennessee/hamilton-county/"><span>Hamilton County, Tennessee</span><em>County-specific workflow</em></a>
                <a href="/septic-records-checklist/north-carolina/wake-county/"><span>Wake County, North Carolina</span><em>iMAPS parcel match and Permit Search attachments · verified field pack</em></a>
                <a href="/septic-records-checklist/indiana/st-joseph-county/"><span>St. Joseph County, Indiana</span><em>Official schematic PDF and verified fields · prepared field pack</em></a>
                <a href="/septic-records-checklist/south-carolina/greenville-county/"><span>Greenville County, South Carolina</span><em>Guidance and property clues · starting-point route</em></a>
                <a href="/tdec-septic-records/"><span>Tennessee TDEC records</span><em>Program and field-office paths</em></a>
            </nav>
        </div>
    </section>

    <section class="home-v2-stories" aria-labelledby="home-v2-stories-title">
        <div class="container">
            <header><span class="home-v2-kicker">Real investigations</span><h2 id="home-v2-stories-title">Start with a case like yours.</h2></header>
            <nav aria-label="Real septic record investigations">
                <a href="/septic-as-built-records/#layout-case">Finding the tank and field from a historical layout <span>Read case →</span></a>
                <a href="/septic-record-brief-example/#sale-case">Recovering a missing file before a sale <span>Read case →</span></a>
                <a href="/septic-permit-search-by-address/#old-address-case">A repair file without a modern street number <span>Read case →</span></a>
                <a href="/septic-permit-process/#design-final-case">When the planned and installed lengths differed <span>Read case →</span></a>
                <a href="/septic-tank-location-records/#addition-file-case">Checking the historic layout before an addition <span>Read case →</span></a>
            </nav>
            <a class="home-v2-stories__all" href="/septic-record-brief-example/#real-cases">Explore all five real record-help cases →</a>
        </div>
    </section>

    <section class="home-v2-self-serve" id="self-serve-records" aria-labelledby="home-v2-self-serve-title">
        <div class="container home-v2-self-serve__grid">
            <div>
                <span class="home-v2-kicker">Self-serve alternative</span>
                <h2 id="home-v2-self-serve-title">Start with the address.</h2>
                <p>Prefer to search public records yourself? We’ll identify the county and route you toward the most likely public record source. Your address is not stored by SepticPath.</p>
                <p><strong>Find septic records</strong> by address, or find the record route by county name below.</p>
                <a href="/septic-record-finder/?mode=document" data-record-document-direct>Have a permit, drawing, letter, or screenshot?</a>
            </div>
            <div>
                @template.tags.addressRecordFinder(
                    finderId = "home-address-record-finder",
                    eyebrow = "Self-serve record routing",
                    heading = "Find the public record path",
                    intro = "Enter the property address to identify the county and likely record source.",
                    pageHeading = false,
                    totalCountyRouteCount = totalCountyRouteCount,
                    minimal = true
                )
            </div>
        </div>
    </section>

    <section class="home-v2-directory">
        <div class="container">
            <details>
                <summary><span><strong>Search by county instead</strong><small>Use this when you do not have the full property address.</small></span><span>Browse ${totalCountyRouteCount} county paths</span></summary>
                @template.tags.countyFinder(
                    countyFinderLinks = countyFinderLinks,
                    finderId = "home-county-finder",
                    eyebrow = "County search",
                    heading = "Search by county or state.",
                    body = "Open the local permit, records request, parcel, as-built, or inspection-letter route.",
                    sourceContext = "home_county_finder",
                    totalCountyRouteCount = totalCountyRouteCount
                )
            </details>
            <nav class="home-v2-directory__links" aria-label="Coverage and research standards">
                <a href="/states/">All state guides</a>
                <a href="/septic-records-by-county/">Browse records by county</a>
                <a href="/septic-records-request-builder/#records-request-builder">Build a records request</a>
                <a href="/coverage/">Records coverage</a>
                <a href="/methodology/">How we verify sources</a>
            </nav>
            <p class="home-v2-directory__return">Back from the official site? Keep the property details together and continue with the next available route.</p>
        </div>
    </section>

    <section class="home-v2-final" aria-labelledby="home-v2-final-title">
        <div class="home-v2-final__image" aria-hidden="true"></div>
        <div class="container home-v2-final__body">
            <span class="home-v2-kicker">Your property deserves a clear record</span>
            <h2 id="home-v2-final-title">Stop searching.<br><em>Start knowing.</em></h2>
            <a class="button home-v2-button--light" href="/offer-prep-septic-file-check/#record-help" data-record-help-cta data-track-click="nav" data-track-source-context="home_v2_final" data-track-target-type="lead_form">Ask SepticPath to investigate</a>
            <small>SepticPath is an independent research service, not an inspection or permitting authority.</small>
        </div>
    </section>
`)
