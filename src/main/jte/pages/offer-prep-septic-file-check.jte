@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.ClosingRiskCheckForm
@import com.example.septic.web.PageMeta
@import com.example.septic.web.AssetPaths
@param PageMeta page
@param java.util.List<StateProfile> offerPrepStates = java.util.List.of()
@param int priorityCountyRouteCount = 0
@param ClosingRiskCheckForm closingRiskCheckForm
@param boolean closingRiskHasErrors = false
@param String closingRiskDocumentError = null
@param String closingRiskRequestId = null
@param String closingRiskSuccessSourceContext = "direct"
@param boolean closingRiskReceiptSent = false
@param String closingRiskMinimumDeadline
@param boolean documentReviewMode = false
@param java.util.List<StateProfile> states = java.util.List.of()

@template.layouts.app(page = page, designFamily = "signature", content = @`
    <link rel="stylesheet" href="${AssetPaths.versioned("/premium-intake.css")}">
    <div class="offer-prep-page" data-offer-prep-file-check>
        <section class="offer-prep-hero" aria-labelledby="offer-prep-title">
            <div class="container offer-prep-hero__grid">
                <div class="offer-prep-hero__intro">
                    <span class="offer-prep-kicker">Start a record search</span>
                    <h1 id="offer-prep-title">${documentReviewMode ? "Send the file. We’ll answer the questions it raises." : "Tell us the property. We’ll handle the record work."}</h1>
                    <p class="lede">${documentReviewMode ? "Does the file show an approved design or a completed installation? Send it with your questions. We’ll check the evidence and explain the limits." : "Share the address and what you need to know. We’ll identify the record owner, search available sources, and contact the responsible office when necessary."}</p>
                    <a class="button button--primary offer-prep-hero__start" href="#record-help" aria-label="${documentReviewMode ? "Send my file for review" : "Start my record investigation"}" data-record-help-cta data-track-click="nav" data-track-source-context="${documentReviewMode ? "document_review_hero" : "record_help_hero"}" data-track-target-type="lead_form">${documentReviewMode ? "Send my file for review" : "Start the research"}<span aria-hidden="true">→</span></a>
                    <p class="offer-prep-hero__free"><span aria-hidden="true">✦</span> Research and agency requests are free. Optional $29 result unlock. ${documentReviewMode ? "We only offer the unlock when the evidence supports a useful, source-backed answer to your question." : "We only offer the $29 unlock when we find useful, property-matched evidence that can answer your question."} No upfront payment or automatic charge. Agency fees are additional at cost, with your approval.</p>
                    <div class="offer-prep-hero__signals" aria-label="Research service benefits">
                        <span>No account required</span>
                        <span>Private property details</span>
                        <span>Real people, real research</span>
                    </div>
                </div>
                <aside class="offer-prep-hero__visual" aria-label="A property is matched to its permit, site plan, and source-backed findings">
                    <picture>
                        <source media="(max-width: 700px)" srcset="/images/brand/septicpath-hero-art-mobile-v3.png">
                        <img src="/images/brand/septicpath-hero-art-desktop-v3.png" alt="Aerial property view with a reconstructed permit, site plan, agency response, and clearly labeled findings" width="1792" height="1024">
                    </picture>
                    <div class="offer-prep-hero__match"><span aria-hidden="true">✓</span><strong>Property matched</strong><small>Address and parcel located</small></div>
                    <p>Properties matter.<br>So do the people<br>who live on them.</p>
                </aside>
            </div>
        </section>

        <section class="section section--tight offer-prep-workspace-section ${documentReviewMode ? "offer-prep-workspace-section--hidden" : ""}" id="self-serve-file-check" aria-labelledby="offer-prep-workspace-heading">
            <div class="container">
                <details class="offer-prep-workspace">
                    <summary class="offer-prep-workspace__summary">
                        <span class="eyebrow">Self-serve alternative</span>
                        <strong>Build a file request yourself</strong>
                        <span>No submission or email required. Your address stays in this browser. Routed coverage currently includes ${offerPrepStates.size()} states and ${priorityCountyRouteCount} county paths.</span>
                    </summary>
                    <div class="offer-prep-workspace__header">
                        <h2 id="offer-prep-workspace-heading">Resolve the file route. Build the request. Keep the offer moving.</h2>
                        <p>Address lookup only identifies the county. It does not save the property address or send a records request.</p>
                    </div>

                    <form class="offer-prep-form" data-offer-prep-form>
                        <fieldset class="offer-prep-form__group">
                            <legend>1. Find the public file route</legend>
                            <label class="offer-prep-form__wide">
                                <span>Property address</span>
                                <input type="text" inputmode="text" autocomplete="street-address" maxlength="180" placeholder="123 Main St, City, ST 12345" data-offer-prep-address>
                                <small>Optional. Used only to resolve the county through the U.S. Census geocoder.</small>
                            </label>
                            <div class="offer-prep-form__or"><span>or use county</span></div>
                            <label>
                                <span>State</span>
                                <select data-offer-prep-state>
                                    <option value="">Choose state</option>
                                    @for (var state : offerPrepStates)
                                        <option value="${state.stateCode()}">${state.stateName()}</option>
                                    @endfor
                                </select>
                            </label>
                            <label>
                                <span>County</span>
                                <input type="text" autocomplete="address-level2" maxlength="80" placeholder="e.g., Wake" data-offer-prep-county>
                            </label>
                        </fieldset>

                        <fieldset class="offer-prep-form__group">
                            <legend>2. Frame the file question</legend>
                            <label>
                                <span>Listing bedrooms</span>
                                <select data-offer-prep-listing-bedrooms>
                                    <option value="unknown">Not confirmed</option>
                                    @for (int count = 1; count <= 8; count++)
                                        <option value="${count}">${count} bedrooms</option>
                                    @endfor
                                    <option value="9+">9 or more bedrooms</option>
                                </select>
                            </label>
                            <label>
                                <span>Permit bedrooms</span>
                                <select data-offer-prep-permit-bedrooms>
                                    <option value="unknown">File not available yet</option>
                                    @for (int count = 1; count <= 8; count++)
                                        <option value="${count}">${count} bedrooms</option>
                                    @endfor
                                    <option value="9+">9 or more bedrooms</option>
                                </select>
                            </label>
                            <label>
                                <span>What is already available?</span>
                                <select data-offer-prep-file-status>
                                    <option value="missing">No septic file supplied</option>
                                    <option value="partial">A partial permit or report</option>
                                    <option value="unknown">Seller says a file exists, but it has not been reviewed</option>
                                    <option value="complete">Permit or approval file is available</option>
                                </select>
                            </label>
                            <label>
                                <span>Send the request to</span>
                                <select data-offer-prep-recipient>
                                    <option value="listing_agent">Listing agent</option>
                                    <option value="seller">Seller</option>
                                </select>
                            </label>
                        </fieldset>

                        <button class="button button--primary offer-prep-form__submit" type="submit" data-offer-prep-submit>Build the file check</button>
                    </form>

                    <section class="offer-prep-result" data-offer-prep-result aria-live="polite" hidden>
                        <div class="offer-prep-result__summary">
                            <span class="eyebrow" data-offer-prep-status>File route ready</span>
                            <h3 data-offer-prep-heading></h3>
                            <p data-offer-prep-message></p>
                            <div class="offer-prep-result__facts" data-offer-prep-facts></div>
                        </div>
                        <div class="offer-prep-result__route">
                            <span>Official file path</span>
                            <strong data-offer-prep-route-title></strong>
                            <p data-offer-prep-route-note></p>
                            <div class="offer-prep-result__actions" data-offer-prep-route-actions></div>
                        </div>
                        <div class="offer-prep-result__next" data-offer-prep-next hidden>
                            <span>Next decision</span>
                            <h3 data-offer-prep-next-heading></h3>
                            <p data-offer-prep-next-message></p>
                            <div class="offer-prep-result__actions" data-offer-prep-next-actions></div>
                        </div>
                        <div class="offer-prep-result__request">
                            <div>
                                <span>Copy-ready request</span>
                                <h3>Ask for the file before the offer relies on the septic story.</h3>
                            </div>
                            <textarea readonly aria-label="Offer-period septic file request" data-offer-prep-note></textarea>
                            <div class="offer-prep-result__actions">
                                <button class="button button--primary" type="button" data-offer-prep-copy>Copy request</button>
                                <button class="button button--secondary" type="button" data-offer-prep-download>Download .txt</button>
                                <button class="button button--secondary" type="button" data-offer-prep-print>Print request</button>
                            </div>
                            <p class="offer-prep-result__privacy">Only this browser sees the address and request text. SepticPath records anonymous tool actions, not the property or message.</p>
                        </div>
                    </section>
                </details>
            </div>
        </section>

        <section class="section closing-risk-beta-section" id="record-help" aria-labelledby="closing-risk-beta-heading">
            <div class="container closing-risk-beta-grid">
                <div class="closing-risk-beta-copy">
                    <span class="eyebrow">${documentReviewMode ? "Start the human review" : "Start the investigation"}</span>
                    <h2 id="closing-risk-beta-heading">${documentReviewMode ? "Send the source record and your questions." : "Tell us the property and what you need to learn."}</h2>
                    <p class="lede">${documentReviewMode ? "We connect each answer to the source page, distinguish proposed work from recorded approvals, and flag missing evidence or conflicting information." : "Tell us the question you are trying to answer. You do not need a permit number or the right office—we handle the search and agency requests, then show you what we found."}</p>
                    <ol class="detail-list">
                        @if (documentReviewMode)
                            <li><strong>Match:</strong> tie the file to the property, parcel, issuing office, and record date.</li>
                            <li><strong>Read:</strong> answer your questions from the source text and drawing—not assumptions.</li>
                            <li><strong>Separate:</strong> show confirmed facts, missing evidence, conflicts, and what still needs field or agency confirmation.</li>
                        @else
                            <li><strong>Find your record:</strong> check the property and search for permits, drawings, approvals and repair records.</li>
                            <li><strong>Handle the agency:</strong> request missing records and follow the response so you do not have to work out the process yourself.</li>
                            <li><strong>Keep moving when records stop:</strong> if field work is the next step, we can connect you with relevant local septic professionals.</li>
                            <li><strong>Choose your answers:</strong> preview the evidence and its limits, then unlock the original records and property-specific explanation for $29.</li>
                        @endif
                    </ol>
                    <p class="closing-risk-beta-copy__privacy">This beta privately stores the submitted address and attached records and emails them to the SepticPath operator so a human reply can be prepared. Do not submit Social Security numbers, payment details, access codes, or unrelated personal documents.</p>
                </div>

                <div class="closing-risk-beta-panel">
                    <div class="record-help-assurance" aria-label="What your reply will include">
                        <div>
                            <span class="eyebrow">Your reply will include</span>
                            <ul>
                                @if (documentReviewMode)
                                    <li>A free preview of the questions the source can answer</li>
                                    <li>Confirmed facts, limitations, gaps, and conflicts</li>
                                    <li>The next useful agency or field question, when needed</li>
                                @else
                                    <li>Records checked against your address and parcel</li>
                                    <li>Agency requests and updates handled for you</li>
                            <li>A free preview of the source, property match and answerable questions; originals and actual answers for $29 if you choose</li>
                                @endif
                            </ul>
                        </div>
                        <p><strong>A person handles your request</strong><span>We aim to send an initial update within 1–2 business days. Agency records can take longer.</span></p>
                    </div>
                    @if (closingRiskRequestId != null)
                        <div class="form-status form-status--success"
                             role="status"
                             data-closing-risk-request-success
                             data-ga-event="record_help_request_submitted"
                             data-ga-track-once="record-help-request:${closingRiskRequestId}"
                             data-ga-param-request-type="record_help_beta"
                             data-ga-param-source-context="${closingRiskSuccessSourceContext}">
                            <strong>${documentReviewMode ? "Your document review is in." : "Your record investigation is in."}</strong>
                            <p>Your reference is ${closingRiskRequestId}. ${closingRiskReceiptSent ? "We also emailed this reference and your request details to you." : "Save this reference and use it if you need to follow up."} We’ll begin the free research or document review and aim to email an initial update within 1–2 business days. If a useful verified result is ready, we’ll send a free preview and an optional $29 unlock offer. No payment is due now. Any agency fees require your approval first.</p>
                        </div>
                    @endif
                    @if (closingRiskHasErrors)
                        <div class="form-status form-status--error" role="alert">
                            <strong>Check the highlighted details.</strong>
                            <p>${closingRiskDocumentError == null ? "Use a real property address, valid email, and complete the required record-help fields and consent." : closingRiskDocumentError}</p>
                        </div>
                    @endif

                    <form class="quote-form closing-risk-beta-form" method="post" enctype="multipart/form-data" action="/offer-prep-septic-file-check/#record-help" data-record-help-request-form>
                        <div class="closing-risk-honeypot" aria-hidden="true" hidden>
                            <label>Website<input type="text" name="website" value="${closingRiskCheckForm.getWebsite() == null ? "" : closingRiskCheckForm.getWebsite()}" tabindex="-1" autocomplete="off"></label>
                        </div>
                        <input type="hidden" name="sourceContext" value="${closingRiskCheckForm.getSourceContextValue()}" data-record-help-source-context>
                        <input type="hidden" name="sourcePageHint" value="${closingRiskCheckForm.getSourcePageHintValue()}" data-record-help-source-page>
                        <input type="hidden" name="entryPageHint" value="${closingRiskCheckForm.getEntryPageHintValue()}" data-record-help-entry-page>
                        <div class="record-help-form-intro">
                            <span class="eyebrow">Free to start · Optional $29 unlock</span>
                            <strong>${documentReviewMode ? "Give us the source file and the questions you need answered." : "Give us the address and the decision you need to make."}</strong>
                            <small>Free research and agency requests. Preview first; choose the $29 records and answers later. Agency fees extra, with your approval.</small>
                        </div>
                        <div class="field-grid">
                            <label>
                                <span>Email</span>
                                <input type="email" name="email" value="${closingRiskCheckForm.getEmailValue()}" autocomplete="email" maxlength="160" required>
                                <small>We’ll send research updates and the result here. No newsletter signup.</small>
                            </label>
                            <label>
                                <span>Mobile number <small>(optional)</small></span>
                                <input type="tel" name="phone" value="${closingRiskCheckForm.getPhoneValue()}" autocomplete="tel" inputmode="tel" maxlength="30" placeholder="(555) 123-4567">
                                <small>Add a number if you would like connected local professionals to call or text about this request. Free research does not require a phone number.</small>
                            </label>
                            <label class="field--full">
                                <span>Property address</span>
                                <input type="text" name="propertyAddress" value="${closingRiskCheckForm.getPropertyAddressValue()}" autocomplete="street-address" maxlength="220" placeholder="123 Main St, City, ST 12345" required>
                            </label>
                            <label>
                                <span>State</span>
                                <select name="stateCode" required>
                                    <option value="">Choose state</option>
                                    @for (var state : states)
                                        <option value="${state.stateCode()}" selected="${state.stateCode().equals(closingRiskCheckForm.getStateCode())}">${state.stateName()}</option>
                                    @endfor
                                </select>
                            </label>
                            <div class="field--full record-help-documents" data-record-help-documents data-review-active="${documentReviewMode}">
                                <label>
                                    <span>Source files for human review</span>
                                    <input type="file"
                                           name="documents"
                                           accept="application/pdf,text/plain,image/png,image/jpeg,.pdf,.txt,.png,.jpg,.jpeg"
                                           multiple
                                           data-record-help-document-input>
                                    <small>Up to 3 PDF, TXT, PNG, or JPG files. 10 MB per file, 15 MB combined.</small>
                                </label>
                                <p>Attach the agency record itself whenever possible. Listing screenshots can provide context, but they do not replace the official source file.</p>
                            </div>
                            <label class="field--full">
                                <span data-record-help-question-label>${documentReviewMode ? "What questions do you want answered?" : "What do you need to decide, or what is missing?"}</span>
                                <textarea name="concern" maxlength="1200" minlength="10" rows="4" placeholder="${documentReviewMode ? "Example: Does this approve four bedrooms, and does the sketch show the installed drainfield or only the proposed design?" : "Example: I’m buying this property and need the approved bedroom count before September 25. I found a parcel page, but no permit or layout."}" required data-record-help-question>${closingRiskCheckForm.getConcernValue()}</textarea>
                                <small data-record-help-question-help>${documentReviewMode ? "Ask the exact questions affecting your purchase, sale, repair, addition, inspection, or records decision." : "One or two sentences is enough. Include any deadline or conflicting information."}</small>
                            </label>
                        </div>
                        <details class="closing-risk-optional" data-record-help-transaction-details>
                            <summary>Add details that may speed up the search <span>(optional)</span></summary>
                            <div class="field-grid">
                                <label>
                                    <span>Record type</span>
                                    <select name="recordType">
                                        <option value="septic" selected="${"septic".equals(closingRiskCheckForm.getRecordType())}">Septic system</option>
                                        <option value="well" selected="${"well".equals(closingRiskCheckForm.getRecordType())}">Private well</option>
                                        <option value="not_sure" selected="${"not_sure".equals(closingRiskCheckForm.getRecordType())}">Not sure</option>
                                    </select>
                                </label>
                                <label>
                                    <span>What do you need to find out?</span>
                                    <select name="researchGoal" data-record-help-goal>
                                        <option value="original_documents" selected="${"original_documents".equals(closingRiskCheckForm.getResearchGoal())}">Find original permit or approval documents</option>
                                        <option value="system_layout" selected="${"system_layout".equals(closingRiskCheckForm.getResearchGoal())}">Find tank, drainfield, or well location</option>
                                        <option value="approval_status" selected="${"approval_status".equals(closingRiskCheckForm.getResearchGoal())}">Verify permit or approval status</option>
                                        <option value="design_capacity" selected="${"design_capacity".equals(closingRiskCheckForm.getResearchGoal())}">Check bedroom or design capacity</option>
                                        <option value="repair_history" selected="${"repair_history".equals(closingRiskCheckForm.getResearchGoal())}">Find repair or modification history</option>
                                        <option value="understand_file" selected="${"understand_file".equals(closingRiskCheckForm.getResearchGoal())}">Understand a file I already have</option>
                                        <option value="other" selected="${"other".equals(closingRiskCheckForm.getResearchGoal())}">Not sure or something else</option>
                                    </select>
                                </label>
                                <label class="field--full">
                                    <span>${documentReviewMode ? "What best describes the file?" : "What have you tried or found so far?"}</span>
                                    <select name="recordStatus">
                                        @if (documentReviewMode)
                                            <option value="partial" selected="${"partial".equals(closingRiskCheckForm.getRecordStatus())}">I may not have the full file</option>
                                            <option value="conflicting" selected="${"conflicting".equals(closingRiskCheckForm.getRecordStatus())}">The document may conflict with the property or listing</option>
                                            <option value="unknown" selected="${"unknown".equals(closingRiskCheckForm.getRecordStatus())}">I’m not sure whether it is official or complete</option>
                                            <option value="request_unclear" selected="${"request_unclear".equals(closingRiskCheckForm.getRecordStatus())}">I’m not sure what this document is</option>
                                        @else
                                            <option value="not_started" selected="${"not_started".equals(closingRiskCheckForm.getRecordStatus())}">I haven’t searched yet</option>
                                            <option value="missing" selected="${"missing".equals(closingRiskCheckForm.getRecordStatus())}">I searched but found no matching record</option>
                                            <option value="route_unknown" selected="${"route_unknown".equals(closingRiskCheckForm.getRecordStatus())}">I don’t know which office owns it</option>
                                            <option value="request_unclear" selected="${"request_unclear".equals(closingRiskCheckForm.getRecordStatus())}">I’m not sure which document to request</option>
                                            <option value="partial" selected="${"partial".equals(closingRiskCheckForm.getRecordStatus())}">I found only part of the file</option>
                                            <option value="conflicting" selected="${"conflicting".equals(closingRiskCheckForm.getRecordStatus())}">The file conflicts with the property or listing</option>
                                            <option value="unknown" selected="${"unknown".equals(closingRiskCheckForm.getRecordStatus())}">Someone says a file exists, but I can’t confirm it</option>
                                        @endif
                                    </select>
                                </label>
                                <label>
                                    <span>Deadline <small>(optional)</small></span>
                                    <input type="date" name="deadline" value="${closingRiskCheckForm.getDeadlineValue()}" min="${closingRiskMinimumDeadline}">
                                </label>
                                <label>
                                    <span>Where are you in the process?</span>
                                    <select name="transactionRole" data-record-help-stage>
                                        <option value="">Choose if relevant</option>
                                        <option value="researching" selected="${"researching".equals(closingRiskCheckForm.getTransactionRole())}">Researching</option>
                                        <option value="buyer" selected="${"buyer".equals(closingRiskCheckForm.getTransactionRole())}">Buyer</option>
                                        <option value="seller" selected="${"seller".equals(closingRiskCheckForm.getTransactionRole())}">Seller</option>
                                        <option value="agent" selected="${"agent".equals(closingRiskCheckForm.getTransactionRole())}">Real-estate agent</option>
                                        <option value="other" selected="${"other".equals(closingRiskCheckForm.getTransactionRole())}">Other professional</option>
                                    </select>
                                </label>
                                <label>
                                    <span>Name</span>
                                    <input type="text" name="fullName" value="${closingRiskCheckForm.getFullNameValue()}" autocomplete="name" maxlength="120">
                                </label>
                                <label>
                                    <span>County</span>
                                    <input type="text" name="countyName" value="${closingRiskCheckForm.getCountyNameValue()}" autocomplete="address-level2" maxlength="120" placeholder="Example: Knox County">
                                </label>
                                <label>
                                    <span>Listing URL</span>
                                    <input type="url" name="listingUrl" value="${closingRiskCheckForm.getListingUrlValue()}" maxlength="500" placeholder="https://...">
                                </label>
                                <label>
                                    <span>Bedrooms shown in listing</span>
                                    <input type="number" name="listingBedrooms" value="${closingRiskCheckForm.getListingBedroomsValue()}" min="1" max="20">
                                </label>
                                <label>
                                    <span>Bedrooms shown on permit</span>
                                    <input type="number" name="permitBedrooms" value="${closingRiskCheckForm.getPermitBedroomsValue()}" min="1" max="20">
                                </label>
                            </div>
                        </details>
                        <label class="consent-field">
                            <input type="checkbox" name="consentAccepted" checked="${closingRiskCheckForm.isConsentAccepted()}" required>
                            <span>I agree to the free research and email updates. If records cannot answer my question, SepticPath may connect me with up to three relevant local professionals using the contact details I provide. Optional $29 unlock terms are below.</span>
                        </label>
                        <p class="form-note">A connected professional may respond by email or, if you provide a mobile number, by a manual call or text about this request. You are never required to hire anyone we connect you with. This does not authorize automated or prerecorded marketing, unrelated solicitations, or resale of your request.</p>
                        <details class="record-help-consent-details">
                            <summary>Read research consent and pricing terms</summary>
                            <p>${closingRiskCheckForm.getConsentTextSnapshot()}</p>
                        </details>
                        <p class="record-help-fee-note">Approved agency fees are separate and may apply even if no record is found or you do not unlock.</p>
                        <button class="button button--primary" type="submit" data-record-help-submit>${documentReviewMode ? "Send my file for human review" : "Ask SepticPath to investigate"}</button>
                        <p class="form-note">No card required. No automatic charge. Your contact details and private correspondence are not published. <a href="/privacy-policy/#research-publication">How we use research findings</a>.</p>
                    </form>
                </div>
            </div>
        </section>

        <section class="section section--tight record-help-pricing-faq" aria-labelledby="record-help-pricing">
            <div class="container record-help-pricing-grid">
                <div class="record-help-pricing-intro">
                    <span class="offer-prep-kicker">Clear before you pay</span>
                    <h2 id="record-help-pricing">How the $29 unlock works</h2>
                    <div class="record-help-package-compare" aria-label="Free preview and optional $29 package comparison">
                        <div><span>FREE PREVIEW</span><strong>“We found a property-matched permit that answers the recorded design-capacity question.”</strong><p>Illustrative preview · source and document scope · answerable questions · important limits · no payment required</p></div>
                        <div><span>OPTIONAL $29 PACKAGE</span><strong>The original permit, drawing, and page-by-page explanation.</strong><p>Source files · evidence-linked answers · conflicts and missing facts · next useful step</p></div>
                    </div>
                </div>
                <div class="record-help-pricing-questions">
                <details>
                    <summary>How do I pay and receive my result?</summary>
                    <p>We email your free preview first. If you want the US $29 package, we send secure provider-hosted payment instructions by email. After confirming payment, we email the source records and our detailed explanation. The payment email states the expected delivery timing; if anything is delayed, reply to that email with your reference number. Delivery is handled personally, not an instant website download. Never send card details by email. For files you already supplied, you are paying for the reviewed answers; your own files remain yours.</p>
                </details>
                <details>
                    <summary>What will I see before paying?</summary>
                    <p>You see the agency or official source, document type and date, available pages or drawings, how we matched the property, which of your questions the evidence can answer, and material limits. Your property-specific conclusions and the located source files are included in the optional $29 package. If the evidence cannot answer your question, we say so before offering payment. Public examples show completed work for other properties; they are not findings about yours.</p>
                </details>
                <details>
                    <summary>What does the $29 package add?</summary>
                    <p>The official source files we found, the relevant pages, and clear answers to your questions with the evidence behind them. We explain recorded layout, capacity, approval or repair details where available, flag conflicts and missing information, and identify what to ask next. Public records may also be available directly from the agency; the value of our service is finding, matching and interpreting them for your question.</p>
                </details>
                <details>
                    <summary>What if the agency is still searching or finds nothing?</summary>
                    <p>We keep you informed without asking you to pay for a pending request. An empty online search or a request receipt is not a useful result. If no source file was found, we tell you that for free. A substantive official response may still support a reviewed answer; its source, relevance and limits must be clear before any optional offer. A missing record does not prove that a system does not exist.</p>
                </details>
                <details>
                    <summary>Are agency requests included? Can there be extra costs?</summary>
                    <p>Our research and agency-request work are free. If an agency charges a search, copy, or portal fee, we explain the amount and ask for approval before incurring it. Those actual agency costs are separate from the optional $29 unlock and may apply even without a successful search or purchase.</p>
                </details>
                <details>
                    <summary>I already submitted during the free beta. Will I be charged?</summary>
                    <p>Your original free-beta request remains free, including records that arrive later and our explanation.</p>
                </details>
                </div>
            </div>
        </section>

        <section class="section offer-prep-proof-section">
            <div class="container offer-prep-proof-grid">
                <article>
                    <span>01</span>
                    <h2>One address is enough to begin</h2>
                    <p>We identify the likely county or state record owner and verify the property before relying on a result.</p>
                </article>
                <article>
                    <span>02</span>
                    <h2>We pursue the usable artifacts</h2>
                    <p>When appropriate, we request the permit, final record, layout, bedroom capacity, repair history, and relevant approval documents.</p>
                </article>
                <article>
                    <span>03</span>
                    <h2>You receive evidence, not a guess</h2>
                    <p>Your free preview identifies the official source, property match, questions it can answer and important limits. Your actual answers and located source records are included in the optional $29 package. Existing requests keep their accepted terms, including free-beta service and any earlier promise of a free finding.</p>
                </article>
            </div>
            <div class="container offer-prep-transfer-aftercare">
                <p>Need the broader seller disclosure or permit-history sequence instead?</p>
                <a href="/septic-transfer-compliance/" data-track-click="nav" data-track-source-context="closing_risk_aftercare" data-track-target-type="transfer_workflow">Open the transfer workflow <span aria-hidden="true">→</span></a>
            </div>
        </section>
    </div>
`)
