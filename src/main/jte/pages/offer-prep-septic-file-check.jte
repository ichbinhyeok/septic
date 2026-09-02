@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.ClosingRiskCheckForm
@import com.example.septic.web.PageMeta
@param PageMeta page
@param java.util.List<StateProfile> offerPrepStates = java.util.List.of()
@param int priorityCountyRouteCount = 0
@param ClosingRiskCheckForm closingRiskCheckForm
@param boolean closingRiskHasErrors = false
@param String closingRiskRequestId = null
@param String closingRiskMinimumDeadline
@param java.util.List<StateProfile> states = java.util.List.of()

@template.layouts.app(page = page, content = @`
    <div class="offer-prep-page" data-offer-prep-file-check>
        <section class="section section--heroish offer-prep-hero">
            <div class="container offer-prep-hero__grid">
                <div>
                    <span class="eyebrow">Septic closing risk check</span>
                    <h1>Under contract on a septic home? Find what must be confirmed before your deadline.</h1>
                    <p class="lede">Compare the listing story with the available septic file, identify missing or conflicting evidence, and prepare the exact questions that keep due diligence moving.</p>
                    <div class="hero__actions offer-prep-hero__actions">
                        <a class="button button--primary" href="#free-closing-risk-check" data-closing-risk-cta data-track-click="nav" data-track-source-context="closing_risk_hero" data-track-target-type="lead_form">Request a free manual review</a>
                        <a class="button button--secondary" href="#self-serve-file-check" data-track-click="nav" data-track-source-context="closing_risk_hero" data-track-target-type="internal_tool">Use the self-serve check</a>
                    </div>
                    <div class="offer-prep-hero__signals" aria-label="Offer prep tool coverage">
                        <span><strong>Free beta</strong> for the first 10 qualified requests</span>
                        <span><strong>Human reply</strong> by email</span>
                        <span>No payment details requested</span>
                    </div>
                </div>
                <aside class="offer-prep-hero__note">
                    <span class="pill">Not a permit decision</span>
                    <strong>Use the official file before you negotiate the septic story.</strong>
                    <p>This tool identifies a records route and prepares a factual request. The local office and the official file decide what the property supports.</p>
                    <p class="offer-prep-hero__transfer-note">For a live transaction, start with the manual review below. If you only need a template, use the self-serve check instead.</p>
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

        <section class="section closing-risk-beta-section" id="free-closing-risk-check" aria-labelledby="closing-risk-beta-heading">
            <div class="container closing-risk-beta-grid">
                <div class="closing-risk-beta-copy">
                    <span class="eyebrow">Free beta · first 10 qualified requests</span>
                    <h2 id="closing-risk-beta-heading">Ask for a manual closing-risk review.</h2>
                    <p class="lede">If a real transaction has a deadline, send the property facts that are blocking the next decision. We will compare the listing details with the available file path and reply with what is confirmed, missing, or conflicting.</p>
                    <ol class="detail-list">
                        <li><strong>One-page readiness brief:</strong> listing facts, permit evidence, and unresolved gaps.</li>
                        <li><strong>Deadline-based next steps:</strong> who to contact and what to request before due diligence ends.</li>
                        <li><strong>No false certification:</strong> this is document preparation, not an inspection, engineering opinion, or permit decision.</li>
                    </ol>
                    <p class="closing-risk-beta-copy__privacy">Unlike the self-serve tool above, this beta stores the submitted address and emails it to the SepticPath operator so a human review can be completed. Do not submit Social Security numbers, payment details, access codes, or unrelated personal documents.</p>
                </div>

                <div class="closing-risk-beta-panel">
                    @if (closingRiskRequestId != null)
                        <div class="form-status form-status--success"
                             role="status"
                             data-closing-risk-request-success
                             data-ga-event="closing_risk_request_submitted"
                             data-ga-track-once="closing-risk-request:${closingRiskRequestId}"
                             data-ga-param-request-type="free_beta">
                            <strong>Request received.</strong>
                            <p>Your reference is ${closingRiskRequestId}. We will reply by email after reviewing the property file path and deadline.</p>
                        </div>
                    @endif
                    @if (closingRiskHasErrors)
                        <div class="form-status form-status--error" role="alert">
                            <strong>Check the highlighted request details.</strong>
                            <p>Use a real property, a current or future deadline, and complete contact information. All required fields and consent must be valid.</p>
                        </div>
                    @endif

                    <form class="quote-form closing-risk-beta-form" method="post" action="/offer-prep-septic-file-check/#free-closing-risk-check" data-closing-risk-request-form>
                        <div class="closing-risk-honeypot" aria-hidden="true" hidden>
                            <label>Website<input type="text" name="website" value="${closingRiskCheckForm.getWebsite() == null ? "" : closingRiskCheckForm.getWebsite()}" tabindex="-1" autocomplete="off"></label>
                        </div>
                        <div class="field-grid">
                            <label>
                                <span>Name</span>
                                <input type="text" name="fullName" value="${closingRiskCheckForm.getFullNameValue()}" autocomplete="name" maxlength="120" required>
                            </label>
                            <label>
                                <span>Email</span>
                                <input type="email" name="email" value="${closingRiskCheckForm.getEmailValue()}" autocomplete="email" maxlength="160" required>
                            </label>
                            <label>
                                <span>Your role</span>
                                <select name="transactionRole" required>
                                    <option value="">Choose role</option>
                                    <option value="buyer" selected="${"buyer".equals(closingRiskCheckForm.getTransactionRole())}">Buyer</option>
                                    <option value="seller" selected="${"seller".equals(closingRiskCheckForm.getTransactionRole())}">Seller</option>
                                    <option value="agent" selected="${"agent".equals(closingRiskCheckForm.getTransactionRole())}">Real-estate agent</option>
                                    <option value="other" selected="${"other".equals(closingRiskCheckForm.getTransactionRole())}">Other transaction professional</option>
                                </select>
                            </label>
                            <label>
                                <span>Due-diligence or closing deadline</span>
                                <input type="date" name="deadline" value="${closingRiskCheckForm.getDeadlineValue()}" min="${closingRiskMinimumDeadline}" required>
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
                            <label class="field--full">
                                <span>What is known about the septic file?</span>
                                <select name="recordStatus" required>
                                    <option value="">Choose file status</option>
                                    <option value="missing" selected="${"missing".equals(closingRiskCheckForm.getRecordStatus())}">No septic file was supplied</option>
                                    <option value="partial" selected="${"partial".equals(closingRiskCheckForm.getRecordStatus())}">A partial permit, drawing, or report is available</option>
                                    <option value="conflicting" selected="${"conflicting".equals(closingRiskCheckForm.getRecordStatus())}">The listing and available records conflict</option>
                                    <option value="unknown" selected="${"unknown".equals(closingRiskCheckForm.getRecordStatus())}">The seller says a file exists, but it has not been reviewed</option>
                                </select>
                            </label>
                        </div>
                        <details class="closing-risk-optional">
                            <summary>Add county or file details <span>(optional)</span></summary>
                            <div class="field-grid">
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
                                    <span>What decision is blocked?</span>
                                    <textarea name="concern" maxlength="1200" rows="4" placeholder="Example: The listing says four bedrooms, but the permit copy appears to show three.">${closingRiskCheckForm.getConcernValue()}</textarea>
                                </label>
                            </div>
                        </details>
                        <label class="consent-field">
                            <input type="checkbox" name="consentAccepted" checked="${closingRiskCheckForm.isConsentAccepted()}" required>
                            <span>I agree that SepticPath may store these property and contact details and email them to its operator to prepare this free beta file check. I understand this is not an inspection, permit decision, legal opinion, or compliance certification.</span>
                        </label>
                        <button class="button button--primary" type="submit">Request the free closing-risk check</button>
                        <p class="form-note">Qualified beta requests are reviewed manually. Submission does not guarantee a response time, record availability, or professional service match.</p>
                    </form>
                </div>
            </div>
        </section>

        <section class="section offer-prep-proof-section">
            <div class="container offer-prep-proof-grid">
                <article>
                    <span>01</span>
                    <h2>Do not guess the file owner</h2>
                    <p>Start at the county or state route that owns the permit, layout, approval, or written no-record response.</p>
                </article>
                <article>
                    <span>02</span>
                    <h2>Ask for the usable artifacts</h2>
                    <p>Request the permit and final record with the layout, bedroom capacity, repair history, and any inspection or approval record that applies.</p>
                </article>
                <article>
                    <span>03</span>
                    <h2>Keep the claim narrow</h2>
                    <p>A listing detail or an old report is not a compliance conclusion. Use the official response before changing an offer, price, or repair decision.</p>
                </article>
            </div>
            <div class="container offer-prep-transfer-aftercare">
                <p>Need the broader seller disclosure or permit-history sequence instead?</p>
                <a href="/septic-transfer-compliance/" data-track-click="nav" data-track-source-context="closing_risk_aftercare" data-track-target-type="transfer_workflow">Open the transfer workflow <span aria-hidden="true">→</span></a>
            </div>
        </section>
    </div>
`)
