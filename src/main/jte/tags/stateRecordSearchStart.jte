@param String stateCode
@if (java.util.Set.of("IN", "WV", "IL").contains(stateCode))
<section class="section section--tight" aria-labelledby="state-record-search-start">
    <div class="container record-search-guide">
        <h2 id="state-record-search-start">Start with the office that holds the property file</h2>
        @if ("IN".equals(stateCode))
            <p>Indiana’s local health departments issue residential septic permits. Start with the property county’s environmental health office and ask for the existing permit, installation drawing and inspection record. A new-system application is a different request.</p>
            <p><strong>La Porte County example:</strong> its health department says it generally holds septic drawings from July 1979 onward. Ask whether the file for your particular address is available; that date is not a guarantee that every drawing survives.</p>
            <p><a href="/septic-records-checklist/indiana/la-porte-county/">La Porte County records guide →</a> · <a href="/septic-records-checklist/indiana/porter-county/">Porter County records guide →</a></p>
            <p>Official sources: <a href="https://www.in.gov/health/eph/onsite-sewage-systems-program/" target="_blank" rel="noreferrer">Indiana onsite sewage program</a> · <a href="https://www.in.gov/health/lhd/local-health-department-map" target="_blank" rel="noreferrer">Find the local health department</a> · <a href="https://www.in.gov/localhealth/laportecounty/septic-division/" target="_blank" rel="noreferrer">La Porte drawing coverage</a>.</p>
        @elseif ("IL".equals(stateCode))
            <p>Illinois calls this program <strong>private sewage disposal</strong>. IDPH and local health agencies review construction plans. Start with the local health department serving the property and ask who holds its existing permit, site plan and installation or inspection file.</p>
            <p>The state’s contractor search identifies service providers; it is not a property-record lookup. A system evaluation form is also different from the original permit and drawing.</p>
            <p>Official source: <a href="https://dph.illinois.gov/topics-services/environmental-health-protection/private-sewage-disposal.html" target="_blank" rel="noreferrer">IDPH Private Sewage Disposal, forms and local-agency responsibilities</a>.</p>
        @else
            <p>West Virginia’s state program provides onsite wastewater guidance. For an existing property file, begin with the local health department serving the county and ask it to confirm the record custodian. Use the property’s county rather than the owner’s current mailing address.</p>
            <p>Ask for the existing sewage-system permit and any site sketch, installation, inspection or repair pages. If the first office does not hold the file, ask which local or district office does before submitting a new construction application.</p>
            <p>Official starting point: <a href="https://oehs.wvdhhr.org/phs/on-site-wastewater-management/" target="_blank" rel="noreferrer">West Virginia On-Site Wastewater Management</a>. Local availability, request method and fees need confirmation for the property.</p>
        @endif
        <p><strong>Have these clues ready:</strong> full address, county, parcel or tax-map number if known, approximate installation year and any earlier owner or permit reference. Ask for the drawing and attachments, not only a permit number.</p>
        <p><a href="/septic-tank-location-records/#location-start">Need the tank or drainfield location? See which drawing to request →</a></p>
        <small>Public program guidance checked November 18, 2026. No individual property search or record availability is implied.</small>
    </div>
</section>
@endif
