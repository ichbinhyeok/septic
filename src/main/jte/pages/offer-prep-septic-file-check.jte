@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.ClosingRiskCheckForm
@import com.example.septic.web.PageMeta
@param PageMeta page
@param java.util.List<StateProfile> offerPrepStates = java.util.List.of()
@param int priorityCountyRouteCount = 0
@param ClosingRiskCheckForm closingRiskCheckForm
@param boolean closingRiskHasErrors = false
@param String closingRiskRequestId = null
@param String closingRiskSuccessSourceContext = "direct"
@param String closingRiskMinimumDeadline
@param java.util.List<StateProfile> states = java.util.List.of()

@template.layouts.app(page = page, content = @`
    <div class="offer-prep-page" data-offer-prep-file-check>
        <section class="section section--heroish offer-prep-hero">
            <div class="container offer-prep-hero__grid">
                <div>
                    <span class="eyebrow">Human septic record research · free beta</span>
                    <h1>Send the address. We’ll investigate the septic record for you.</h1>
                    <p class="lede">We search available public sources, verify the property, contact the responsible office when needed, and explain the records we receive in plain English.</p>
                    <div class="hero__actions offer-prep-hero__actions">
                        <a class="button button--primary" href="#record-help" data-record-help-cta data-track-click="nav" data-track-source-context="record_help_hero" data-track-target-type="lead_form">Start my record investigation</a>
                        <a class="button button--secondary" href="#self-serve-file-check" data-track-click="nav" data-track-source-context="closing_risk_hero" data-track-target-type="internal_tool">Search records myself</a>
                    </div>
                    <div class="offer-prep-hero__signals" aria-label="Offer prep tool coverage">
                        <span><strong>Human-reviewed</strong> research</span>
                        <span><strong>Agency follow-up</strong> when needed</span>
                        <span><strong>Original files</strong> plus clear findings</span>
                    </div>
                </div>
                <aside class="offer-prep-hero__note service-scope-ledger" aria-label="How the record investigation works">
                    <div class="service-scope-ledger__topline"><span>SEPTICPATH / RESEARCH DESK</span><span>01—03</span></div>
                    <ol>
                        <li><span>01</span><div><strong>Verify the property</strong><p>Match the address, parcel, county, and responsible record holder.</p></div></li>
                        <li><span>02</span><div><strong>Follow the record</strong><p>Search credible sources and contact the public office when the online path stops.</p></div></li>
                        <li><span>03</span><div><strong>Explain the result</strong><p>Return the original response, confirmed facts, unknowns, and next useful step.</p></div></li>
                    </ol>
                    <p class="offer-prep-hero__transfer-note">Record availability and response times vary. SepticPath is not an inspection, permit authority, or compliance certification.</p>
                </aside>
            </div>
        </section>

        <section class="section section--tight offer-prep-workspace-section" id="self-serve-file-check" aria-labelledby="offer-prep-workspace-heading">
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
                    <span class="eyebrow">Start the investigation</span>
                    <h2 id="closing-risk-beta-heading">Tell us the property and what you need to learn.</h2>
                    <p class="lede">You do not need to know the county office, record name, or request process. Send the address and the problem; we’ll take the search as far as the available evidence allows.</p>
                    <ol class="detail-list">
                        <li><strong>Research:</strong> search available public sources and match the property to the right jurisdiction.</li>
                        <li><strong>Acquisition:</strong> contact the responsible office for the permit, layout, approval, or written response when needed.</li>
                        <li><strong>Interpretation:</strong> return original materials with confirmed facts, important gaps, and the next useful step.</li>
                    </ol>
                    <p class="closing-risk-beta-copy__privacy">This beta stores the submitted address and emails it to the SepticPath operator so a human reply can be prepared. Do not submit Social Security numbers, payment details, access codes, or unrelated personal documents.</p>
                </div>

                <div class="closing-risk-beta-panel">
                    <div class="record-help-assurance" aria-label="What your reply will include">
                        <div>
                            <span class="eyebrow">Your reply will include</span>
                            <ul>
                                <li>A verified property and record-owner route</li>
                                <li>Agency outreach when an online record is unavailable</li>
                                <li>Original files plus a plain-English findings summary</li>
                            </ul>
                        </div>
                        <p><strong>Human-reviewed</strong><span>We aim to reply by email within 1–2 business days.</span></p>
                    </div>
                    @if (closingRiskRequestId != null)
                        <div class="form-status form-status--success"
                             role="status"
                             data-closing-risk-request-success
                             data-ga-event="record_help_request_submitted"
                             data-ga-track-once="record-help-request:${closingRiskRequestId}"
                             data-ga-param-request-type="record_help_beta"
                             data-ga-param-source-context="${closingRiskSuccessSourceContext}">
                            <strong>Your record investigation is in.</strong>
                            <p>Your reference is ${closingRiskRequestId}. We’ll begin with the available public sources and email you within 1–2 business days with either an answer, an agency-request update, or the next fact we need.</p>
                        </div>
                    @endif
                    @if (closingRiskHasErrors)
                        <div class="form-status form-status--error" role="alert">
                            <strong>Check the highlighted details.</strong>
                            <p>Use a real property address, valid email, and complete the required record-help fields and consent.</p>
                        </div>
                    @endif

                    <form class="quote-form closing-risk-beta-form" method="post" action="/offer-prep-septic-file-check/#record-help" data-record-help-request-form>
                        <div class="closing-risk-honeypot" aria-hidden="true" hidden>
                            <label>Website<input type="text" name="website" value="${closingRiskCheckForm.getWebsite() == null ? "" : closingRiskCheckForm.getWebsite()}" tabindex="-1" autocomplete="off"></label>
                        </div>
                        <input type="hidden" name="sourceContext" value="${closingRiskCheckForm.getSourceContextValue()}" data-record-help-source-context>
                        <div class="record-help-form-intro">
                            <span class="eyebrow">About two minutes</span>
                            <strong>Email and property address are enough to start.</strong>
                            <small>We’ll ask only for details that help us find the right record.</small>
                        </div>
                        <div class="field-grid">
                            <label>
                                <span>Email</span>
                                <input type="email" name="email" value="${closingRiskCheckForm.getEmailValue()}" autocomplete="email" maxlength="160" required>
                                <small>We’ll send research updates and the result here. No newsletter signup.</small>
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
                            <label>
                                <span>Where did the search stop?</span>
                                <select name="recordStatus" required>
                                    <option value="">Choose the closest match</option>
                                    <option value="missing" selected="${"missing".equals(closingRiskCheckForm.getRecordStatus())}">I searched but found no septic record</option>
                                    <option value="route_unknown" selected="${"route_unknown".equals(closingRiskCheckForm.getRecordStatus())}">I don’t know which office owns the record</option>
                                    <option value="request_unclear" selected="${"request_unclear".equals(closingRiskCheckForm.getRecordStatus())}">I’m not sure which document to request</option>
                                    <option value="partial" selected="${"partial".equals(closingRiskCheckForm.getRecordStatus())}">I found only part of the file</option>
                                    <option value="conflicting" selected="${"conflicting".equals(closingRiskCheckForm.getRecordStatus())}">The file conflicts with the property or listing</option>
                                    <option value="unknown" selected="${"unknown".equals(closingRiskCheckForm.getRecordStatus())}">Someone says a file exists, but I can’t confirm it</option>
                                </select>
                            </label>
                            <label class="field--full">
                                <span>Where are you in the process? <small>(optional)</small></span>
                                <select name="transactionRole" data-record-help-stage>
                                    <option value="">Choose if relevant</option>
                                    <option value="researching" selected="${"researching".equals(closingRiskCheckForm.getTransactionRole())}">Just researching the property or records</option>
                                    <option value="buyer" selected="${"buyer".equals(closingRiskCheckForm.getTransactionRole())}">Buyer</option>
                                    <option value="seller" selected="${"seller".equals(closingRiskCheckForm.getTransactionRole())}">Seller</option>
                                    <option value="agent" selected="${"agent".equals(closingRiskCheckForm.getTransactionRole())}">Real-estate agent</option>
                                    <option value="other" selected="${"other".equals(closingRiskCheckForm.getTransactionRole())}">Other transaction professional</option>
                                </select>
                                <small>This helps us tailor the reply but is not required.</small>
                            </label>
                        </div>
                        <details class="closing-risk-optional" data-record-help-transaction-details>
                            <summary>Add context for a faster answer <span>(optional)</span></summary>
                            <div class="field-grid">
                                <label>
                                    <span>Name</span>
                                    <input type="text" name="fullName" value="${closingRiskCheckForm.getFullNameValue()}" autocomplete="name" maxlength="120">
                                </label>
                                <label>
                                    <span>Due-diligence or closing deadline</span>
                                    <input type="date" name="deadline" value="${closingRiskCheckForm.getDeadlineValue()}" min="${closingRiskMinimumDeadline}">
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
                                <label class="field--full">
                                    <span>What happened, or what decision is blocked?</span>
                                    <textarea name="concern" maxlength="1200" rows="4" placeholder="Example: The county search returned nothing, and I’m not sure whether to ask the health department or building office.">${closingRiskCheckForm.getConcernValue()}</textarea>
                                </label>
                            </div>
                        </details>
                        <label class="consent-field">
                            <input type="checkbox" name="consentAccepted" checked="${closingRiskCheckForm.isConsentAccepted()}" required>
                            <span>I agree that SepticPath may store these property and contact details and email them to its operator to research the relevant septic records, contact the responsible public office when appropriate, and follow up about the result. I understand this is not an inspection, permit decision, legal opinion, or compliance certification.</span>
                        </label>
                        <button class="button button--primary" type="submit">Ask SepticPath to investigate</button>
                        <p class="form-note">Free beta requests are reviewed manually and are not added to a newsletter. Public offices control record availability and response timing; if a fee or owner authorization is required, we’ll tell you before anything else happens.</p>
                    </form>
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
                    <p>We preserve the original source and clearly separate confirmed facts, unresolved questions, and decisions that require a licensed professional.</p>
                </article>
            </div>
            <div class="container offer-prep-transfer-aftercare">
                <p>Need the broader seller disclosure or permit-history sequence instead?</p>
                <a href="/septic-transfer-compliance/" data-track-click="nav" data-track-source-context="closing_risk_aftercare" data-track-target-type="transfer_workflow">Open the transfer workflow <span aria-hidden="true">→</span></a>
            </div>
        </section>
    </div>
`)
