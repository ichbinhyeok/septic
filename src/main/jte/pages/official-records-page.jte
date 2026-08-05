@import com.example.septic.data.model.ContentPage
@import com.example.septic.web.PageMeta
@param PageMeta page
@param ContentPage contentPage
@param java.util.List<com.example.septic.web.PageLink> permitLookupCountyLinks = java.util.List.of()
@param String officialLookupPrimaryUrl = null
@param String officialLookupPrimaryLabel = "Open official records source"
@param String editorialLastReviewedAt = ""
@param int totalCountyRouteCount = 0

@template.layouts.app(page = page, content = @`
    <div class="official-records-page official-records-page--${contentPage.slug()}">
        <section class="official-records-hero">
            <div class="container official-records-hero__inner">
                <div class="official-records-hero__copy">
                    <span class="eyebrow">Official property record route</span>
                    @if ("tdec-septic-records".equals(contentPage.slug()))
                        <h1>Find Tennessee septic records</h1>
                        <p>Enter the property address to identify the county and the office that should hold the TDEC or local SSDS file. SepticPath routes the search; it does not store records or claim a permit was found.</p>
                    @elseif ("north-carolina-septic-permit-lookup".equals(contentPage.slug()))
                        <h1>Find a North Carolina septic permit by county</h1>
                        <p>North Carolina septic files are normally held by county environmental health. Start with the property address, then open the matching county search or request.</p>
                    @elseif ("texas-ossf-records-search".equals(contentPage.slug()))
                        <h1>Find Texas OSSF permits and septic records</h1>
                        <p>Texas OSSF files are handled by counties and local authorized agents. Resolve the county first, then use the official local record route.</p>
                    @elseif ("florida-ostds-permit-lookup".equals(contentPage.slug()))
                        <h1>Find Florida OSTDS permits and septic records</h1>
                        <p>Florida OSTDS records may sit with a county health department or local archive. Resolve the county before opening the official source.</p>
                    @else
                        <h1>Find South Carolina septic permits and SCDES records</h1>
                        <p>Older searches often use DHEC. Current septic routes use SCDES and regional or county contacts. Start with the property location and open the current official path.</p>
                    @endif
                    <div class="official-records-hero__boundary" role="note">
                        <strong>What this page does</strong>
                        <span>Identifies the county, file owner, official route, useful search fields, and a safe fallback.</span>
                    </div>
                </div>

                @template.tags.addressRecordFinder(
                    finderId = contentPage.slug() + "-address-finder",
                    eyebrow = "Start with the property",
                    heading = "Enter the address to find the record owner",
                    intro = "Use a full street address, city, state, and ZIP. The result identifies the county and the strongest verified route; it does not claim to retrieve the government file.",
                    addressPlaceholder = "tdec-septic-records".equals(contentPage.slug()) ? "401 Church St, Nashville, TN 37219" : "123 Main St, City, ST 12345",
                    totalCountyRouteCount = totalCountyRouteCount,
                    minimal = true
                )
            </div>
        </section>

        <section class="official-records-route" id="official-route">
            <div class="container official-records-route__grid">
                <div class="official-records-route__primary">
                    <span class="eyebrow">Official starting point</span>
                    @if ("tdec-septic-records".equals(contentPage.slug()))
                        <h2>Confirm the county before using the Tennessee search</h2>
                        <p>Most Tennessee counties use the TDEC route. Blount, Davidson, Hamilton, Jefferson, Knox, Madison, Sevier, Shelby, and Williamson use their own local septic program.</p>
                    @elseif ("north-carolina-septic-permit-lookup".equals(contentPage.slug()))
                        <h2>Use county environmental health, not a generic statewide search</h2>
                        <p>Search with the address or parcel first. If the portal is incomplete, ask the county for the improvement permit, construction authorization, operation permit, site sketch, and repair history.</p>
                    @elseif ("texas-ossf-records-search".equals(contentPage.slug()))
                        <h2>Find the local OSSF permitting authority</h2>
                        <p>TCEQ explains the statewide program, but the county or authorized agent normally owns the property file. Carry the address, parcel, owner, legal description, subdivision, lot, and permit number.</p>
                    @elseif ("florida-ostds-permit-lookup".equals(contentPage.slug()))
                        <h2>Start with Florida OSTDS context, then use the county file owner</h2>
                        <p>Ask for the OSTDS permit, approved plan or site sketch, final approval, repair or abandonment record, and a written no-record response when the archive is incomplete.</p>
                    @else
                        <h2>Translate DHEC searches into the current SCDES route</h2>
                        <p>Use SCDES septic guidance and the regional or county contact. Ask for the D-1740 trail, permit copy, site evaluation, Permit to Construct, final inspection, and repair history.</p>
                    @endif
                    @if (officialLookupPrimaryUrl != null)
                        <a class="button button--primary official-records-route__cta"
                           href="${officialLookupPrimaryUrl}"
                           target="_blank"
                           rel="noreferrer"
                           data-track-click="nav"
                           data-track-source-context="official_records_primary"
                           data-track-target-type="official_source">${officialLookupPrimaryLabel}</a>
                        <small>Opens an official government website in a new tab.</small>
                    @endif
                </div>

                <aside class="official-records-documents" aria-labelledby="official-records-documents-title">
                    <span class="eyebrow">Ask for the file, not just a search result</span>
                    <h2 id="official-records-documents-title">Documents that usually answer the property question</h2>
                    <ul>
                        <li><strong>Permit or application</strong><span>Shows the approved project and identifiers.</span></li>
                        <li><strong>Installed layout or as-built</strong><span>Shows tank, field, and reserve-area placement when available.</span></li>
                        <li><strong>Final approval</strong><span>Shows whether the installation reached the final county or state step.</span></li>
                        <li><strong>Repair history</strong><span>Shows later work that may change the original file story.</span></li>
                    </ul>
                </aside>
            </div>
        </section>

        @if (!permitLookupCountyLinks.isEmpty())
            <section class="official-records-counties" id="county-routes">
                <div class="container">
                    <div class="official-records-section-heading">
                        <span class="eyebrow">Already know the county?</span>
                        <h2>Open the local record route directly</h2>
                        <p>County pages show whether the next step is a public search, form, email, or call.</p>
                    </div>
                    <form class="official-records-county-picker" data-county-route-picker>
                        <label for="${contentPage.slug()}-county-route">County record route</label>
                        <div>
                            <select id="${contentPage.slug()}-county-route" data-county-route-select required>
                                <option value="">Choose a county</option>
                                @for (var link : permitLookupCountyLinks)
                                    <option value="${link.path()}">${link.compactTitle()}</option>
                                @endfor
                            </select>
                            <button class="button button--primary" type="submit" data-county-route-submit disabled>Open county route</button>
                        </div>
                        <p data-county-route-status aria-live="polite">Choose a county to stay on SepticPath and prepare for its official handoff.</p>
                    </form>
                </div>
            </section>
        @endif

        <section class="official-records-outcomes">
            <div class="container">
                <div class="official-records-section-heading">
                    <span class="eyebrow">After the official search</span>
                    <h2>Choose the result you actually received</h2>
                </div>
                <div class="official-records-outcomes__list">
                    <details>
                        <summary>I found a document</summary>
                        <p>Save the original PDF or screenshot and check the permit number, approved use or bedrooms, installed layout, final approval, and repair history. Use the document workspace in the address result to identify missing or conflicting facts.</p>
                    </details>
                    <details>
                        <summary>Nothing appeared online</summary>
                        <p>Retry with parcel ID, current and prior owner, street-only search, subdivision, lot, legal description, or permit number. An empty portal result is not an official no-record response.</p>
                    </details>
                    <details>
                        <summary>The official site was blocked</summary>
                        <p>Do not treat the block as proof that the file is missing. Use the county page, local office, or written records-request route with the same property clues.</p>
                    </details>
                    <details>
                        <summary>The office confirmed no record</summary>
                        <p>Keep the written response. Ask whether archived, delegated, pre-digital, regional, or prior-owner files are held elsewhere before deciding what professional inspection or locate is needed.</p>
                    </details>
                </div>
            </div>
        </section>

        <section class="official-records-trust">
            <div class="container">
                <p><strong>Independent guide.</strong> SepticPath is not affiliated with a state or county agency and does not certify permit existence, system condition, or compliance.</p>
                @if (editorialLastReviewedAt != null && !editorialLastReviewedAt.isBlank())
                    <p>Official route review activity: ${editorialLastReviewedAt}</p>
                @endif
                <details>
                    <summary>Common questions</summary>
                    <div>
                        @for (var faq : contentPage.faqBlocks())
                            <article><h2>${faq.question()}</h2><p>${faq.answer()}</p></article>
                        @endfor
                    </div>
                </details>
            </div>
        </section>
    </div>
`)
