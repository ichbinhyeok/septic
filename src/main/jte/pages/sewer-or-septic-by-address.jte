@import com.example.septic.data.model.ContentPage
@import com.example.septic.web.AssetPaths
@import com.example.septic.web.PageMeta
@param PageMeta page
@param ContentPage contentPage
@template.layouts.app(page = page, designFamily = "editorial", pageStyles = "/wastewater-lookup.css", content = @`
<div class="wastewater-lookup" data-wastewater-lookup>
    <section class="section section--heroish">
        <div class="container state-hero">
            <div>
                <span class="eyebrow">Property records · Seattle and Tacoma</span>
                <h1>${contentPage.title()}</h1>
                <p class="lede">${contentPage.introCopy()}</p>
                <p>Start with the building you are checking. A nearby sewer main can show where service is available. The building’s connection record or the utility can tell you whether it is connected.</p>
            </div>
            <form class="trust-box trust-box--large wastewater-lookup__form" data-wastewater-form hidden>
                <h2>Find the official sources</h2>
                <label for="wastewater-city">Property is inside these city limits</label>
                <select id="wastewater-city" required>
                    <option value="">Choose a city</option>
                    <option value="seattle">Seattle, Washington</option>
                    <option value="tacoma">Tacoma, Washington</option>
                    <option value="other">Outside these cities or unsure</option>
                </select>
                <label for="wastewater-address">Street address</label>
                <input id="wastewater-address" type="text" maxlength="160" placeholder="House number and street" autocomplete="off" required>
                <p>The address stays on this page. Copy it into the official search when you are ready. Check the actual city limits if the mailing city is uncertain.</p>
                <button class="button button--primary" type="submit">Show me where to check</button>
            </form>
        </div>
    </section>
    <section class="section section--tight" aria-labelledby="wastewater-sources">
        <div class="container">
            <h2 id="wastewater-sources" tabindex="-1">Official sources by city</h2>
            <div class="wastewater-lookup__selection" data-wastewater-selection hidden>
                <strong data-wastewater-address></strong>
                <button type="button" class="button button--secondary" data-wastewater-copy>Copy address</button>
                <span role="status" data-wastewater-copy-status></span>
            </div>
            <div class="wastewater-lookup__cities">
                <article class="detail-card" data-wastewater-city="seattle">
                    <h3>Seattle, WA</h3>
                    <dl>
                        <div><dt>Building connection evidence</dt><dd><a href="https://maps.seattle.gov/sidesewercardviewer/">Seattle Side Sewer Cards and Maps ↗</a>. Search the house number and street. Open the exact parcel card and look for a sanitary connection for the building. Check its date and whether the card shows drainage only.</dd></div>
                        <div><dt>Public sewer availability</dt><dd><a href="https://cityofseattle.org/utilities/your-services/sewer-and-drainage/sewer-system">Seattle Public Utilities sewer maps ↗</a>. Check mapped infrastructure and ask the utility whether service is active at the address.</dd></div>
                        <div><dt>Older septic evidence</dt><dd><a href="https://kingcounty.gov/asbuilt">King County septic record drawings ↗</a>. Look up the same property and compare the drawing date with any later sewer connection or abandonment record.</dd></div>
                    </dl>
                </article>
                <article class="detail-card" data-wastewater-city="tacoma">
                    <h3>Tacoma, WA</h3>
                    <dl>
                        <div><dt>Building connection evidence</dt><dd><a href="https://www.tacomapermits.org/tip-sheet-index/searching-for-permit-records">Tacoma permit and DART search instructions ↗</a>. Search by site address and parcel number. Look for a side sewer inspection card or completed connection record. Older permits may be in a different archive.</dd></div>
                        <div><dt>Public sewer availability</dt><dd><a href="https://tacoma.gov/government/departments/environmental-services/wastewater/wastewater-resources/">Tacoma sewer availability guidance ↗</a>. Use the city’s address or parcel route to check proximity and ask Environmental Services about service at the property.</dd></div>
                        <div><dt>Older septic evidence</dt><dd><a href="https://edocs.tpchd.org/">Tacoma–Pierce County Health document search ↗</a>. Open the matching septic record drawing, then check city records for a later conversion.</dd></div>
                    </dl>
                </article>
                <article class="detail-card" data-wastewater-city="other" hidden>
                    <h3>Start with the actual city or utility</h3>
                    <p>Use the city or utility serving the parcel for sanitary connection records. For a past septic system, use the <a href="/septic-permit-search-by-address/">septic records finder</a> to find the county and record custodian.</p>
                </article>
            </div>
            <div class="trust-box wastewater-lookup__result" data-wastewater-result hidden>
                <label for="wastewater-evidence">What did the official sources show?</label>
                <select id="wastewater-evidence">
                    <option value="">Still looking</option>
                    <option value="connected">Utility confirmed active sanitary service for this building</option>
                    <option value="lateral">I found a matching sanitary side sewer card or completed permit</option>
                    <option value="available">A public sewer main is nearby or service is available</option>
                    <option value="historic">I found a historical septic drawing only</option>
                    <option value="conflict">I found both sewer and septic records</option>
                    <option value="empty">I found no matching record</option>
                </select>
                <p aria-live="polite" data-wastewater-conclusion>Check both the city connection records and the health department archive for the same property.</p>
            </div>
        </div>
    </section>
    <section class="section section--tight">
        <div class="container">
            <h2>How to read the evidence</h2>
            <div class="detail-grid">
            @for (var faq : contentPage.faqBlocks())
                <article class="detail-card"><h3>${faq.question()}</h3><p>${faq.answer()}</p></article>
            @endfor
            </div>
            <p>Sources reviewed ${contentPage.reviewedAt()}.</p>
            <p><a href="/septic-records-checklist/washington/">Other Washington septic records routes →</a></p>
        </div>
    </section>
</div>
<script src="${AssetPaths.versioned("/wastewater-lookup.js")}" defer></script>
`)
