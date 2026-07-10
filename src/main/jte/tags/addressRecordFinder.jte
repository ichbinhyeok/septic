@param String finderId = "address-record-finder"
@param String eyebrow = "Address record finder"
@param String heading = "Paste the address. Open the right septic records route."
@param String intro = "We resolve the property county, then send you to the best verified county records page or state records path already in SepticPath."
@param int totalCountyRouteCount = 0
@param boolean compact = false
@param boolean pageHeading = false

<div class="record-finder ${compact ? "record-finder--embedded" : ""}">
    <div class="record-finder__intro">
        <span class="eyebrow">${eyebrow}</span>
        @if (pageHeading)
            <h1>${heading}</h1>
        @else
            <h2>${heading}</h2>
        @endif
        <p>${intro}</p>
        <div class="record-finder__signals" aria-label="Record finder coverage">
            <span><strong>${totalCountyRouteCount}</strong> county routes</span>
            <span>Official-source backed</span>
            <span>Address not saved</span>
        </div>
    </div>

    <div class="record-finder__workspace" data-address-record-finder data-address-record-finder-api="/api/address-record-finder">
        <form class="record-finder__form" data-address-record-finder-form>
            <label for="${finderId}-address">Property address</label>
            <p>Street, city, state, and ZIP produce the most reliable county match.</p>
            <div class="record-finder__input-row">
                <input id="${finderId}-address"
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
            <h3 data-address-record-finder-heading></h3>
            <p data-address-record-finder-message></p>
            <div class="record-finder__result-meta" data-address-record-finder-meta hidden></div>
            <ol class="record-finder__relay-steps" data-address-record-finder-steps hidden></ol>
            <div class="record-finder__result-actions" data-address-record-finder-actions></div>
        </section>
    </div>
</div>
