@param String stateCode
@param String countySlug
@if (("TN".equals(stateCode) && "davidson-county".equals(countySlug)) || ("WI".equals(stateCode) && "st-croix-county".equals(countySlug)))
<link rel="stylesheet" href="${com.example.septic.web.AssetPaths.versioned("/research-assets.css")}">
@endif
@if ("TN".equals(stateCode) && "davidson-county".equals(countySlug))
<section class="local-archive-steps" id="local-archive-search" aria-labelledby="local-archive-heading">
    <h2 id="local-archive-heading">How to search Nashville’s HealthCode property files</h2>
    <ol><li>Confirm the full map/parcel number in the official property record. In HealthCode, remove spaces and punctuation but retain leading and trailing zeros.</li><li>Search the parcel first. If needed, try the street number and part of the street name; leave Suite blank when it prevents a match.</li><li>Scroll below the search form for results. Broad searches can time out and only the first 1,000 results return.</li><li>Open each relevant scanned entry. Compare the property identifiers on every page, then separate the approved design from construction inspection, as-built and later repairs.</li></ol>
    <p><a href="https://documents.nashville.gov/Request/Form/HealthCode" target="_blank" rel="noreferrer" data-track-click="nav" data-track-source-context="davidson_archive_steps" data-track-target-type="official_lookup">Open HealthCode property search ↗</a></p>
    <p><strong>Need the approved bedroom count?</strong> Metro also publishes a “How Many Bedrooms?” information request for existing residences. A design drawing and a construction inspection drawing answer different questions.</p>
    <p>If the file is absent or incomplete, use the contact or information-request route on <a href="https://www.nashville.gov/departments/health/environmental-health/septic-and-sewage-disposal-systems" target="_blank" rel="noreferrer">Metro’s septic program page</a>. This is a Davidson County route, not a replacement viewer for all TDEC records.</p>
    <p>Public instructions checked October 11, 2026. A search result still needs a property match; access to the form does not establish file availability.</p>
    <p><a href="/how-to-read-septic-as-built/#permit-vs-as-built">Have different drawings? Compare the record stages →</a></p>
</section>
@elseif ("WI".equals(stateCode) && "st-croix-county".equals(countySlug))
<section class="local-archive-steps" id="local-archive-search" aria-labelledby="local-archive-heading">
    <h2 id="local-archive-heading">Find the SAN documents—not just maintenance history</h2>
    <ol><li>Use the official parcel or site address in Ascent, then select the sanitary permit identified as SAN.</li><li>Review Maintenance for filed service history and Document for the permit attachments. They are separate parts of the record.</li><li>If the plan is missing, follow the county’s scanned Property Files route. The county explains how to browse by record type, town, section and parcel.</li><li>Compare permit numbers, parcel identifiers, dates and revised plans across the packet before using its dimensions or tank capacity.</li></ol>
    <p><a href="https://www.sccwi.gov/328/Sanitary-Program" target="_blank" rel="noreferrer" data-track-click="nav" data-track-source-context="stcroix_archive_steps" data-track-target-type="official_lookup">Open the county’s search instructions and links ↗</a></p>
    <p><strong>If the portal loops or will not load:</strong> keep the exact URL and error, then use Community Development’s contact on the program page. An access failure is not a no-record result. These are the county’s documented routes, not a claim that every endpoint is currently accessible.</p>
    <p><a href="/septic-tank-location-records/#addition-file-case">See how we separated revised plans from other archive pages →</a></p>
</section>
@endif
