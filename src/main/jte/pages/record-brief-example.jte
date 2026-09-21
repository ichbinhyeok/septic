@import com.example.septic.web.PageMeta
@param PageMeta page

@template.layouts.app(page = page, designFamily = "signature", content = @`
    <div class="record-brief-example-page">
        <section class="record-brief-hero" aria-labelledby="record-brief-title">
            <div class="container record-brief-hero__grid">
                <div class="record-brief-hero__copy">
                    <span class="eyebrow">Real septic record cases · See what you receive</span>
                    <h1 id="record-brief-title">Official records. <em>Answers you can understand.</em></h1>
                    <p class="lede">See how an agency permit and drawing become clear findings about recorded capacity, layout and missing information. Research and agency requests are free. Preview your result before choosing the source files and explanation for US $29; agency fees are extra with approval.</p>
                    <div class="record-brief-hero__actions">
                        <a class="button button--primary" href="/offer-prep-septic-file-check/#record-help" data-record-help-cta data-track-click="nav" data-track-source-context="record_brief_example_hero" data-track-target-type="lead_form">Send one property</a>
                        <a class="text-button" href="#real-cases">Find a case like yours</a>
                    </div>
                    <div class="record-brief-hero__promise" aria-label="Service promise">
                        <strong>One handoff.</strong>
                        <span>Original records</span>
                        <span>Source-linked findings</span>
                        <span>Open questions for the site visit</span>
                    </div>
                </div>

                <aside class="record-case-chain" aria-label="Anonymized completed record request" data-reading-guide="record-brief-example">
                    <div class="record-case-chain__header">
                        <div><span>HISTORICAL LAYOUT REQUEST</span><strong>Completed case trace</strong></div>
                        <span class="record-case-chain__closed">RECORDS DELIVERED</span>
                    </div>
                    <ol class="record-case-chain__steps">
                        <li class="record-case-chain__step record-case-chain__step--input">
                            <span class="record-case-chain__number">01</span>
                            <div class="record-case-chain__body">
                                <span class="record-case-chain__label">THE RECORD QUESTION</span>
                                <strong>Property address + records needed</strong>
                                <p>Find the permit and the system layout, then explain what they establish.</p>
                            </div>
                            <span class="record-case-chain__state">RECEIVED</span>
                        </li>
                        <li class="record-case-chain__step record-case-chain__step--blocked">
                            <span class="record-case-chain__number">02</span>
                            <div class="record-case-chain__body">
                                <span class="record-case-chain__label">PUBLIC SEARCH</span>
                                <strong>Property information did not supply the layout</strong>
                                <p>The tank and field positions were still unanswered.</p>
                            </div>
                            <span class="record-case-chain__state">DEAD END</span>
                        </li>
                        <li class="record-case-chain__step record-case-chain__step--work">
                            <span class="record-case-chain__number">03</span>
                            <div class="record-case-chain__body">
                                <span class="record-case-chain__label">SEPTICPATH DESK WORK</span>
                                <strong>Jurisdiction resolved. Records office contacted.</strong>
                                <div class="record-case-chain__actions"><span>Parcel cross-check</span><span>Targeted request</span><span>Source review</span></div>
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
                                <span class="record-case-chain__label">RECORDS AND EXPLANATION</span>
                                <strong>Original files + findings and limits</strong>
                                <div class="record-case-chain__result"><span>Historical design</span><span>Layout explained</span><span>Field limits stated</span></div>
                            </div>
                            <span class="record-case-chain__state">READY</span>
                        </li>
                    </ol>
                    <p class="record-case-chain__note">This historical-layout request is separate from the sale-preparation case below. <a href="/septic-as-built-records/#layout-case">Read its actual findings and limitations.</a> Identifying details omitted.</p>
                </aside>
            </div>
        </section>

        @template.tags.recordCaseCollection()

        <section class="record-brief-handoff" aria-label="What SepticPath removes from the inspection workflow">
            <div class="container record-brief-handoff__grid">
                <div class="record-brief-handoff__before">
                    <span>WITHOUT SEPTICPATH</span>
                    <p>Find the jurisdiction · retry broken portals · locate the right office · write the request · follow up · interpret old records</p>
                </div>
                <div class="record-brief-handoff__arrow" aria-hidden="true">→</div>
                <div class="record-brief-handoff__after">
                    <span>WITH SEPTICPATH</span>
                    <strong>You send the question. We investigate the evidence.</strong>
                </div>
            </div>
        </section>

        <section class="section record-route-map" id="sale-case" aria-labelledby="record-route-map-title" data-reading-guide="sale-case">
            <div class="container record-journey">
                <aside class="record-journey__promise">
                    <span class="eyebrow">Actual completed request · Shelby County, TN</span>
                    <h2 id="record-route-map-title">Finding the missing file.<br><em>From county records to clear findings.</em></h2>
                    <p>We investigated the permit and drawing together to establish the recorded design, tank capacity and field-line layout. Here is how the County file became a readable explanation.</p>
                    <div><span>THE DOCUMENTARY RESULT</span><strong>A four-bedroom design, 1,000-gallon tank and 600 feet of field lines.</strong><small>Recorded design—not verified present-day installation or condition. This example explains our document research, not a customer endorsement.</small></div>
                </aside>

                <ol class="record-journey__steps">
                    <li class="record-journey__step record-journey__step--search">
                        <span class="record-journey__number">01</span>
                        <div class="record-journey__copy"><span>01 / THE SEARCH GAP</span><h3>The subdivision plat was not the missing septic drawing.</h3><p>We checked the public property, plat, GIS and permit records. The plat helped identify the lot, but did not show the house-specific septic installation; the public permit search supplied no septic record for the address. Those were useful search limits, not proof that the County held nothing.</p></div>
                        <div class="record-journey__sources" aria-label="Online research sources">
                            <span>Official portals</span><span>Parcel + GIS</span><span>Subdivision plat</span><span>County file request</span>
                        </div>
                    </li>

                    <li class="record-journey__step record-journey__step--human">
                        <span class="record-journey__number">02</span>
                        <div class="record-journey__copy"><span>02 / THE WORK BEYOND SEARCH</span><h3>An outdated contact was a failed route, not a final answer.</h3><p>An older direct email address returned an invalid-recipient error. We resent to the current published contact, with the parcel and lot references and a specific list of requested records. The County returned the permitted site plan. A later coordinator reply confirmed the recorded design and clarified the limits of the department's file.</p></div>
                        <div class="record-journey__handoffs" aria-label="Agency outreach sequence">
                            <span><b>FAILED CONTACT</b>Old direct address rejected</span><i>→</i><span><b>CORRECTED ROUTE</b>Current County contact</span><i>→</i><span><b>RETURNED EVIDENCE</b>Plan and file clarification</span>
                        </div>
                    </li>

                    <li class="record-journey__step record-journey__step--return" id="completed-brief">
                        <span class="record-journey__number">03</span>
                        <div class="record-journey__copy"><span>03 / THE ANSWER, WITH ITS SOURCE</span><h3>The plan supplied the missing specifications—not a current inspection.</h3><p>Read the handwritten residence label below: it identifies a proposed four-bedroom home and 600 feet of field lines. The tank annotation specifies 1,000 gallons. Our brief carries those values forward as recorded design facts, without turning the drawing into proof of today's installation.</p></div>
                        <div class="record-journey-case" aria-label="Actual anonymized county source packet transformed into a SepticPath Record Brief">
                            <div class="record-journey-case__topline"><span>THIS CASE'S EXISTING PUBLIC EXCERPTS</span><strong>3 COUNTY PAGES → 1 RECORD BRIEF</strong></div>
                            <div class="record-journey-case__source">
                                <span>AGENCY RETURN</span><strong>Original county file</strong>
                                <figure class="record-journey-case__letter"><img src="/images/case-study/redacted/shelby-approval-letter-excerpt-public.png" width="1040" height="560" loading="lazy" alt="Actual anonymized county approval letter excerpt"></figure>
                                <figure class="record-journey-case__plan"><a href="/images/case-study/redacted/shelby-site-plan-detail-public.png" data-reading-image="sale-plan" aria-label="Enlarge the sale case approved plan"><img src="/images/case-study/redacted/shelby-site-plan-detail-public.png" width="560" height="1210" loading="lazy" alt="Actual anonymized approved septic site plan"></a></figure>
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
                        <div class="record-journey__delivery"><span>DELIVERED TO THE CUSTOMER</span><strong>Original files + clear findings + next steps</strong><small>The original three-page County file and a one-page Record Brief were delivered. The later agency clarification was also passed on. This documents a records delivery; it does not establish that the property sale closed or that the system passed an inspection.</small></div>
                    </li>
                </ol>
            </div>
        </section>

        <div class="container">
            @template.tags.recordFindingVisual(variant = "shelby")
        </div>

        <section class="record-learning section" aria-labelledby="sale-case-limits" data-reading-milestone="sale-case-limits">
            <div class="container record-learning__body">
                <span class="eyebrow">The part a drawing alone could not settle</span>
                <h2 id="sale-case-limits">One permit in the file is not proof that nothing ever changed.</h2>
                <div class="record-learning__pair">
                    <article><h3>What the County actually confirmed</h3><p>In its reply for this property, the department identified one historical permit and said it found no repair permits or soil/perc records in its file. It referred questions about final inspections from that period to Code Enforcement.</p><p>We reported the scope of that search. We did not rewrite it as “no repairs ever occurred” or “final installation approved.” Those would be different claims.</p></article>
                    <article><h3>What still needed a different kind of work</h3><p>The returned plan was an approved design. Current tank and field positions, condition and any undocumented changes still needed field verification.</p><p>The result was a source-backed explanation with the remaining questions made explicit—not a substitute for a current inspection.</p></article>
                </div>
                <p class="record-learning__note">Source basis: saved customer intake, County return and coordinator clarification, and the customer delivery. The images above are existing redacted public excerpts from this case. Personal correspondence is retained privately. This single file response is not a general statement about County archives or current procedures.</p>
                <div class="record-learning__next"><a href="/septic-transfer-compliance/#sale-record-packet">Build your own sale-record checklist</a><a href="/septic-as-built-records/#layout-case">Read the separate historical-layout case</a></div>
            </div>
        </section>

        <section class="record-learning section" aria-labelledby="preview-example-heading" data-reading-guide="preview-example">
            <div class="container record-learning__body">
                <span class="eyebrow">Your result, before you choose</span>
                <h2 id="preview-example-heading">What will your free preview tell you?</h2>
                <p class="record-learning__lede">The completed examples on this page show the work you can receive. Your own preview first shows whether we have relevant, property-matched material and what your optional package will contain.</p>
                <div class="record-learning__pair" data-reading-milestone="preview-comparison">
                    <article><span class="eyebrow">Illustrative preview · no payment required</span><h3>Relevant permit and drawing located</h3><p>Source: the responsible records office. The pages have been checked against the property's identifiers. The packet contains information relevant to the recorded design capacity and layout.</p><p><strong>Included in your actual preview:</strong> the source, document dates and extent, property-match status, which questions the material can answer, and any important gaps. This sample does not refer to your property.</p></article>
                    <article><span class="eyebrow">Optional US $29 package</span><h3>The source files and your actual answers</h3><p>Receive the original records and an explanation tied to the relevant pages: the recorded values, what the drawing shows, differences between documents, and what still needs confirmation.</p><p>Research and agency requests are free. Agency fees are separate at cost and need your approval. There is no automatic charge.</p></article>
                </div>
                <p class="record-learning__note">If the file cannot be matched or does not answer your question, we explain that before you decide. A historical record does not establish current condition or a precise field location.</p>
                <a class="button button--primary" href="/offer-prep-septic-file-check/#record-help" data-record-help-cta data-track-click="nav" data-track-source-context="learning_preview_request" data-track-target-type="lead_form">Start with my address and question</a>
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
