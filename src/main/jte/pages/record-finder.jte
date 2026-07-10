@import com.example.septic.web.PageMeta
@param PageMeta page
@param int totalCountyRouteCount = 0

@template.layouts.app(page = page, content = @`
    <section class="record-finder-hero">
        <div class="container">
            <div class="record-finder">
                <div class="record-finder__intro">
                    <span class="eyebrow">Address record finder</span>
                    <h1>Paste the address. Open the right septic records route.</h1>
                    <p>We resolve the property county, then send you to the best verified county records page or state records path already in SepticPath.</p>
                    <div class="record-finder__signals" aria-label="Record finder coverage">
                        <span><strong>${totalCountyRouteCount}</strong> county routes</span>
                        <span>Official-source backed</span>
                        <span>Address not saved</span>
                    </div>
                </div>

                <div class="record-finder__workspace" data-address-record-finder data-address-record-finder-api="/api/address-record-finder">
                    <form class="record-finder__form" data-address-record-finder-form>
                        <label for="record-finder-address">Property address</label>
                        <p>Street, city, state, and ZIP produce the most reliable county match.</p>
                        <div class="record-finder__input-row">
                            <input id="record-finder-address"
                                   name="address"
                                   type="text"
                                   autocomplete="street-address"
                                   placeholder="401 Church St, Nashville, TN 37219"
                                   maxlength="180"
                                   required
                                   data-address-record-finder-input>
                            <button class="button button--primary" type="submit" data-address-record-finder-submit>Find record route</button>
                        </div>
                        <p class="record-finder__privacy">The address is sent only to the U.S. Census geocoder to identify the county. SepticPath does not save it.</p>
                    </form>

                    <section class="record-finder__result" data-address-record-finder-result aria-live="polite" hidden>
                        <span class="eyebrow" data-address-record-finder-status>County route found</span>
                        <h2 data-address-record-finder-heading></h2>
                        <p data-address-record-finder-message></p>
                        <div class="record-finder__result-meta" data-address-record-finder-meta hidden></div>
                        <div class="record-finder__result-actions" data-address-record-finder-actions></div>
                    </section>
                </div>
            </div>
        </div>
    </section>

    <section class="section section--tight">
        <div class="container record-finder-steps">
            <article>
                <span>01</span>
                <h2>Resolve the county</h2>
                <p>Use a full U.S. address so the next click starts with the office most likely to own the parcel file.</p>
            </article>
            <article>
                <span>02</span>
                <h2>Open the file path</h2>
                <p>When a verified county route exists, open it first. Otherwise use the state records path with the same address and parcel clues.</p>
            </article>
            <article>
                <span>03</span>
                <h2>Request the artifact</h2>
                <p>Pull the permit copy, as-built, final approval, repair file, inspection letter, or written no-record response before pricing.</p>
            </article>
        </div>
    </section>
`)
