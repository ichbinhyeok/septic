@import com.example.septic.web.PageMeta
@param PageMeta page

@template.layouts.app(page = page, content = @`
    <div class="record-brief-example-page">
        <section class="record-brief-hero" aria-labelledby="record-brief-title">
            <div class="container record-brief-hero__grid">
                <div class="record-brief-hero__copy">
                    <span class="eyebrow">See what you receive</span>
                    <h1 id="record-brief-title">Official records. <em>Answers you can understand.</em></h1>
                    <p class="lede">See how an agency permit and drawing become clear findings about recorded capacity, layout and missing information. Research and agency requests are free. Preview your result before choosing the source files and explanation for US $29; agency fees are extra with approval.</p>
                    <div class="record-brief-hero__actions">
                        <a class="button button--primary" href="#pilot" data-record-help-cta data-track-click="nav" data-track-source-context="record_brief_example_hero" data-track-target-type="lead_form">Send one property</a>
                        <a class="text-button" href="#completed-brief">See the complete case</a>
                    </div>
                    <div class="record-brief-hero__promise" aria-label="Service promise">
                        <strong>One handoff.</strong>
                        <span>Original records</span>
                        <span>Source-linked findings</span>
                        <span>Open questions for the site visit</span>
                    </div>
                </div>

                <aside class="record-case-chain" aria-label="Anonymized completed case from request to inspection-ready brief">
                    <div class="record-case-chain__header">
                        <div><span>CASE SP–TN–014</span><strong>Completed case trace</strong></div>
                        <span class="record-case-chain__closed">CLOSED · 3-PAGE FILE</span>
                    </div>
                    <ol class="record-case-chain__steps">
                        <li class="record-case-chain__step record-case-chain__step--input">
                            <span class="record-case-chain__number">01</span>
                            <div class="record-case-chain__body">
                                <span class="record-case-chain__label">INPUT FROM INSPECTOR</span>
                                <strong>Property address + Friday deadline</strong>
                                <p>“Need the permit and any system layout before the site visit.”</p>
                            </div>
                            <span class="record-case-chain__state">RECEIVED</span>
                        </li>
                        <li class="record-case-chain__step record-case-chain__step--blocked">
                            <span class="record-case-chain__number">02</span>
                            <div class="record-case-chain__body">
                                <span class="record-case-chain__label">PUBLIC SEARCH</span>
                                <strong>Online route produced no usable file</strong>
                                <p>Viewer unavailable · address lookup exhausted</p>
                            </div>
                            <span class="record-case-chain__state">DEAD END</span>
                        </li>
                        <li class="record-case-chain__step record-case-chain__step--work">
                            <span class="record-case-chain__number">03</span>
                            <div class="record-case-chain__body">
                                <span class="record-case-chain__label">SEPTICPATH DESK WORK</span>
                                <strong>Jurisdiction resolved. Records office contacted.</strong>
                                <div class="record-case-chain__actions"><span>Parcel cross-check</span><span>Targeted request</span><span>Follow-up</span></div>
                            </div>
                            <span class="record-case-chain__state">HANDLED</span>
                        </li>
                        <li class="record-case-chain__step record-case-chain__step--return">
                            <span class="record-case-chain__number">04</span>
                            <div class="record-case-chain__body">
                                <span class="record-case-chain__label">AGENCY RETURN</span>
                                <strong>Historical permit + hand-drawn layout recovered</strong>
                                <p>Original reply and attachments preserved</p>
                            </div>
                            <figure class="record-case-chain__thumb">
                                <img src="/images/case-study/tn-historical-sketch-anonymized.png"
                                     alt="Cropped historical agency sketch showing the house, septic tank, and field lines"
                                     width="1110" height="560">
                            </figure>
                        </li>
                        <li class="record-case-chain__step record-case-chain__step--delivered">
                            <span class="record-case-chain__number">05</span>
                            <div class="record-case-chain__body">
                                <span class="record-case-chain__label">DELIVERED BEFORE SITE VISIT</span>
                                <strong>Original files + a brief the inspector can use</strong>
                                <div class="record-case-chain__result"><span><b>5</b> verified facts</span><span><b>3</b> field questions</span><span><b>1</b> clear limitation</span></div>
                            </div>
                            <span class="record-case-chain__state">READY</span>
                        </li>
                    </ol>
                    <p class="record-case-chain__note">Real completed workflow and source document. Identifying details removed; activity text condensed for display.</p>
                </aside>
            </div>
        </section>

        <section class="record-brief-handoff" aria-label="What SepticPath removes from the inspection workflow">
            <div class="container record-brief-handoff__grid">
                <div class="record-brief-handoff__before">
                    <span>WITHOUT SEPTICPATH</span>
                    <p>Find the jurisdiction · retry broken portals · locate the right office · write the request · follow up · interpret old records</p>
                </div>
                <div class="record-brief-handoff__arrow" aria-hidden="true">→</div>
                <div class="record-brief-handoff__after">
                    <span>WITH SEPTICPATH</span>
                    <strong>You send the address. We return the usable file.</strong>
                </div>
            </div>
        </section>

        <section class="section record-route-map" aria-labelledby="record-route-map-title">
            <div class="container record-journey">
                <aside class="record-journey__promise">
                    <span class="eyebrow">How SepticPath works</span>
                    <h2 id="record-route-map-title">You ask once.<br><em>We chase the answer.</em></h2>
                    <p>No portal hopping. No guessing which office owns the file. No raw document dropped in your inbox without context.</p>
                    <div><span>YOUR HANDOFF</span><strong>Address · purpose · deadline</strong><small>That is all we need to start.</small></div>
                </aside>

                <ol class="record-journey__steps">
                    <li class="record-journey__step record-journey__step--search">
                        <span class="record-journey__number">01</span>
                        <div class="record-journey__copy"><span>TRACE THE PROPERTY</span><h3>We build the trail from every useful clue.</h3><p>Different counties leave different footprints. We reconcile the address, parcel, jurisdiction and likely record holder before trusting a result.</p></div>
                        <div class="record-journey__sources" aria-label="Online research sources">
                            <span>Official portals</span><span>Parcel + GIS</span><span>Assessors</span><span>County directories</span><span>State records</span><span>Archives</span><span>Address variants</span><span>Permit clues</span>
                        </div>
                    </li>

                    <li class="record-journey__step record-journey__step--human">
                        <span class="record-journey__number">02</span>
                        <div class="record-journey__copy"><span>WHEN THE WEB STOPS</span><h3>We take the search to the people holding the file.</h3><p>A broken viewer or empty search is not the end. We identify the custodian, write the property-specific request, follow up, and reroute when another office owns the record.</p></div>
                        <div class="record-journey__handoffs" aria-label="Agency outreach sequence">
                            <span><b>RECORD HOLDER</b>County health or state office</span><i>→</i><span><b>FOCUSED REQUEST</b>Property and record clues included</span><i>→</i><span><b>FOLLOW-UP</b>Current address or alternate custodian</span>
                        </div>
                    </li>

                    <li class="record-journey__step record-journey__step--return" id="completed-brief">
                        <span class="record-journey__number">03</span>
                        <div class="record-journey__copy"><span>WHAT COMES BACK</span><h3>We turn the source packet into a usable answer.</h3><p>Originals stay attached. Confirmed facts, limitations and questions for field verification are separated so you know what the record proves—and what it does not.</p></div>
                        <div class="record-journey-case" aria-label="Actual anonymized county source packet transformed into a SepticPath Record Brief">
                            <div class="record-journey-case__topline"><span>ACTUAL COMPLETED CASE · SP–TN–028</span><strong>3 COUNTY PAGES → 1 FIELD-READY BRIEF</strong></div>
                            <div class="record-journey-case__source">
                                <span>AGENCY RETURN</span><strong>Original county file</strong>
                                <figure class="record-journey-case__letter"><img src="/images/case-study/redacted/shelby-approval-letter-excerpt-public.png" width="1040" height="560" loading="lazy" alt="Actual anonymized county approval letter excerpt"></figure>
                                <figure class="record-journey-case__plan"><img src="/images/case-study/redacted/shelby-site-plan-detail-public.png" width="560" height="1210" loading="lazy" alt="Actual anonymized approved septic site plan"></figure>
                            </div>
                            <div class="record-journey-case__arrow" aria-hidden="true"><span>READ</span><b>→</b><span>VERIFY</span></div>
                            <article class="record-journey-case__brief">
                                <header><div><span>SEPTICPATH</span><strong>Septic Record Brief</strong></div><b>RECORD LOCATED</b></header>
                                <figure><img src="/images/case-study/redacted/shelby-brief-findings-public.png" width="835" height="300" loading="lazy" alt="Actual anonymized SepticPath Record Brief findings"></figure>
                                <dl><div><dt>Design</dt><dd>4 bedrooms</dd></div><div><dt>Tank</dt><dd>1,000 gal</dd></div><div><dt>Field</dt><dd>600 ft</dd></div></dl>
                                <figure class="record-journey-case__use"><img src="/images/case-study/redacted/shelby-brief-field-use-public.png" width="835" height="325" loading="lazy" alt="Actual anonymized Record Brief field-use guidance and limitation"></figure>
                                <footer>ORIGINAL PDF ATTACHED · FIELD VERIFICATION QUESTIONS INCLUDED</footer>
                            </article>
                            <p>SAFE DISPLAY COPY · NAMES, ADDRESS, CONTACT DETAILS, PARCEL KEY AND SIGNATURES EXCLUDED</p>
                        </div>
                        <div class="record-journey__delivery"><span>BACK TO YOU</span><strong>Original files + clear findings + next steps</strong><small>If no record can be located, we return the search result and next-best route—not a false answer.</small></div>
                    </li>
                </ol>
            </div>
        </section>

        <section class="record-brief-pilot" id="pilot" aria-labelledby="record-brief-pilot-title">
            <div class="container record-brief-pilot__grid">
                <div>
                    <span class="eyebrow">For your property and your questions</span>
                    <h2 id="record-brief-pilot-title">Give us one upcoming property.</h2>
                    <p>Send the address, county, and inspection or due-diligence deadline. Research and agency requests are free. When we have a useful verified result, preview it before choosing the original records and human-reviewed Record Brief for US $29. Agency fees are extra at cost, with your approval.</p>
                </div>
                <div class="record-brief-pilot__action">
                    <a class="button button--primary" href="/offer-prep-septic-file-check/#record-help" data-record-help-cta data-track-click="nav" data-track-source-context="record_brief_example_pilot" data-track-target-type="lead_form">Send one property</a>
                    <small>No upfront payment · Existing free-beta requests keep their original terms</small>
                </div>
            </div>
        </section>
    </div>
`)
