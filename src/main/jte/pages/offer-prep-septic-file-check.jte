@import com.example.septic.data.model.StateProfile
@import com.example.septic.web.PageMeta
@param PageMeta page
@param java.util.List<StateProfile> offerPrepStates = java.util.List.of()
@param int priorityCountyRouteCount = 0

@template.layouts.app(page = page, content = @`
    <div class="offer-prep-page" data-offer-prep-file-check>
        <section class="section section--heroish offer-prep-hero">
            <div class="container offer-prep-hero__grid">
                <div>
                    <span class="eyebrow">Buyer-side file check</span>
                    <h1>Before the offer, find the septic file path and send one clean request.</h1>
                    <p class="lede">Use a full U.S. address to resolve the public records route, or choose one of the states with published county-level coverage. Then turn the property question into a ready-to-send request for the listing agent or seller.</p>
                    <div class="offer-prep-hero__signals" aria-label="Offer prep tool coverage">
                        <span><strong>${offerPrepStates.size()}</strong> county-covered states</span>
                        <span><strong>${priorityCountyRouteCount}</strong> county routes</span>
                        <span>Address not saved</span>
                    </div>
                </div>
                <aside class="offer-prep-hero__note">
                    <span class="pill">Not a permit decision</span>
                    <strong>Use the official file before you negotiate the septic story.</strong>
                    <p>This tool identifies a records route and prepares a factual request. The local office and the official file decide what the property supports.</p>
                </aside>
            </div>
        </section>

        <section class="section section--tight offer-prep-workspace-section" aria-labelledby="offer-prep-workspace-heading">
            <div class="container">
                <div class="offer-prep-workspace">
                    <div class="offer-prep-workspace__header">
                        <span class="eyebrow">Offer-period relay</span>
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
        </section>
    </div>
`)
