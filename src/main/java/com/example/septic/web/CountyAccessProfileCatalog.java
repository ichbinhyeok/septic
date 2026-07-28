package com.example.septic.web;

import java.util.List;
import java.util.Map;

public final class CountyAccessProfileCatalog {

    private static final Map<String, CountyAccessProfileView> PROFILES = Map.ofEntries(
            Map.entry("VA::prince-william-county", profile(
                    "VA::prince-william-county",
                    "portal_with_fallback",
                    "Direct public document portal",
                    "Search Prince William County septic records by address or GPIN",
                    "Use the Health District document portal for the historical property file. A blocked or empty portal result is not proof that no record exists.",
                    "Open the Health District document portal",
                    "https://lfportal.pwcgov.org/healthweb/browse.aspx?startid=1",
                    "Optional VDH request fallback (may require verification)",
                    "https://vdh.nextrequest.com/",
                    "A property-matched document bundle or a written response from the file owner",
                    "The portal may return 403 and historical files do not prove current system condition.",
                    List.of("Full property address", "GPIN if available", "Current or prior owner"),
                    List.of("Septic permit or approval", "Installed layout or site sketch", "Repair and O&M history"),
                    List.of(
                            "Confirm the address and GPIN before opening the portal.",
                            "Match the parcel, then download the permit, approval, layout, and any repair file.",
                            "Return here and record whether the file was complete, partial, missing online, or blocked."
                    )
            )),
            Map.entry("TX::tarrant-county", profile(
                    "TX::tarrant-county",
                    "jurisdiction_first",
                    "Jurisdiction check required",
                    "Confirm the Tarrant County OSSF authority before requesting a septic file",
                    "The county, a contract city, or an ETJ may own the OSSF file. The County Clerk search is only a secondary route for recorded affidavits, not a septic permit database.",
                    "Open the Tarrant County OSSF office",
                    "https://www.tarrantcountytx.gov/en/engineering-services/environmental/ossf.html",
                    "Optional JustFOIA fallback (may be access-restricted)",
                    "https://tarrantcountytx.justfoia.com/publicportal",
                    "The responsible OSSF authority plus its permit, LTO, site evaluation, or written referral",
                    "Do not treat a County Clerk no-result as proof that no OSSF file exists.",
                    List.of("Property address", "Parcel or account number", "City, unincorporated, or ETJ status"),
                    List.of("Permit or license to operate", "Site evaluation", "Recorded OSSF affidavit when applicable"),
                    List.of(
                            "Ask which county, contract-city, or ETJ office regulates the parcel.",
                            "Request the OSSF file from that authority; use the Clerk only for a recorded affidavit.",
                            "Save the authority name and the document or written referral."
                    )
            )),
            Map.entry("TN::hamilton-county", profile(
                    "TN::hamilton-county",
                    "portal_with_fallback",
                    "Direct retrieval with email fallback",
                    "Find a Hamilton County septic permit and certificate of completion",
                    "Hamilton County provides document-retrieval instructions and a Groundwater fallback. The portal search has a local quirk: begin with the street name rather than the full house number.",
                    "Open free Hamilton document retrieval",
                    "https://buildinginspection.hamiltontn.gov/Documents.aspx",
                    "Open the Groundwater fallback instructions",
                    "https://www.hamiltontn.gov/BuildingInspection_Septic.aspx",
                    "The permit and installation certificate, or a Groundwater response explaining what is missing",
                    "Permit documents are free to download. Owner and non-owner routes may differ, and an address can be absent from the online list.",
                    List.of("Property address", "Street name", "Owner or non-owner requester status"),
                    List.of("Existing septic tank permit", "Installation certificate of completion", "Repair or Groundwater notes"),
                    List.of(
                            "Choose the owner or non-owner route shown by Hamilton County.",
                            "In document retrieval, try the street name first and select the correct address.",
                            "If the address is absent, use the Groundwater contact and preserve the response."
                    )
            )),
            Map.entry("NC::alamance-county", profile(
                    "NC::alamance-county",
                    "official_request",
                    "Official records request",
                    "Request Alamance County septic records for an existing property",
                    "Use Alamance County's dedicated information-request form for an existing septic permit or soil evaluation. This is separate from a new application or paid field inspection.",
                    "Open the Alamance information-request form",
                    "https://eh.alamancecountync.gov/wp-content/uploads/sites/27/2019/06/Information-Request-Edited-Form.pdf",
                    "",
                    "",
                    "The latest permit or inspection file, repair history, or an official no-record response",
                    "The official form says to allow three days. It does not publish a copy fee; a paid existing-system inspection is a different job.",
                    List.of("Property address", "GPIN or parcel number", "Present and former owners", "Approximate home and septic dates"),
                    List.of("Septic permit", "Soil evaluation", "Malfunction or repair file when requested"),
                    List.of(
                            "Complete the county information form with as many property clues as possible.",
                            "Select the septic permit and soil evaluation, then add repair or malfunction history when relevant.",
                            "Email the completed form to the address printed on it and track the three-day window."
                    )
            )),
            Map.entry("TN::knox-county", profile(
                    "TN::knox-county",
                    "portal_with_fallback",
                    "Live county-branded request form",
                    "Request a Knox County SSDS permit and file search",
                    "Knox County's public page still points to a PDF that returns 404. Its county-branded public Jotform is live and exposes the current SSDS file-search fields, so that live form is now the working front door.",
                    "Open the live Knox SSDS file-search form",
                    "https://knoxcounty.jotform.com/team/eh/ssds-file-search-app",
                    "Check Knox Groundwater file-search instructions",
                    "https://www.knoxcounty.org/health/groundwater_protection/",
                    "The returned SSDS file or a written Knox County no-record response",
                    "The public Jotform is live, but the county landing page's PDF link remains broken. No fee or turnaround is published.",
                    List.of("Knox County", "Current owner", "Street, city, and ZIP", "Tax Map ID and parcel", "Applicant contact"),
                    List.of("Available SSDS property records", "Written form submission outcome"),
                    List.of(
                            "Prepare the exact fields shown by the live county-branded form.",
                            "Open the form, enter Knox County on its first screen, and transfer the prepared values.",
                            "Review the details and press Submit in your own name."
                    )
            )),
            Map.entry("NC::lincoln-county", profile(
                    "NC::lincoln-county",
                    "official_request",
                    "Public-records request",
                    "Request a Lincoln County septic permit by address or parcel number",
                    "Existing septic records and new permit applications are different lanes. The accessible Environmental Health page confirms that record requests need the property address and/or parcel number.",
                    "Open Lincoln Environmental Health records instructions",
                    "https://www.lincolncountync.gov/2617/Onsite-Water",
                    "Optional NextRequest submission (may be access-restricted)",
                    "https://lincolncountync.nextrequest.com/requests/new",
                    "A request reference plus the permit, approval, layout, or official response",
                    "Do not describe the general On-Site Water page as an instant online record search.",
                    List.of("Property address", "Parcel PIN", "Owner name", "Requested record types"),
                    List.of("Improvement permit", "Construction authorization or operation permit", "System layout"),
                    List.of(
                            "Separate an existing-record request from a new permit application.",
                            "Use the Environmental Health page and phone to confirm the route; open NextRequest only if it is reachable.",
                            "Save the request reference and attach the response when it arrives."
                    )
            )),
            Map.entry("GA::dekalb-county", profile(
                    "GA::dekalb-county",
                    "official_request",
                    "Two official service lanes",
                    "Choose a DeKalb septic file request or certification-letter evaluation",
                    "A historical permit copy and a certification letter are different products. Choose the old property file for diligence; choose certification only when a current evaluation is required.",
                    "Open the DeKalb Open Records form",
                    "https://form.jotform.com/260676781949072",
                    "Choose records or current certification",
                    "https://dekalbpublichealth.com/environmental-health/septic-systems/",
                    "Either the historical permit file or the separately requested certification outcome",
                    "Open-record assistance is listed at $25 per custodian hour and copies at $0.25 per page. A certification letter is a separate current-condition evaluation.",
                    List.of("Property address", "Parcel number", "Historical-file or certification purpose"),
                    List.of("Historical permit and repair file", "Certification letter when separately ordered", "Written office response"),
                    List.of(
                            "Choose whether you need an old file or a present-day certification evaluation.",
                            "Use the Environmental Health route that matches that purpose.",
                            "Keep the resulting document labeled by service type so it is not misread."
                    )
            )),
            Map.entry("TN::blount-county", profile(
                    "TN::blount-county",
                    "official_request",
                    "Official Developmental Services portal",
                    "Prepare the Blount SSDS fields, then submit in the county Records Center",
                    "The county's current Public Records page sends septic-tank and Developmental Services record requests to GovQA. Choose Developmental Services Requests; the older SSDS PDF remains a field reference, not the best front door.",
                    "Open the Blount Records Center",
                    "https://blounttn.govqa.us/WEBAPP/_rs/supporthome.aspx",
                    "Open the official SSDS field reference",
                    "https://www.blounttn.gov/DocumentCenter/View/17507/SSDS-Request-Form",
                    "The SSDS response, an unable-to-locate response, or the separate inspection-letter result",
                    "Allow at least seven business days; the records form does not warrant current or future system condition.",
                    List.of("Address", "Subdivision and lot", "Original permittee or prior owner", "Approximate installation date"),
                    List.of("SSDS approval and date", "Authorized bedroom count", "Layout or repair file if available"),
                    List.of(
                            "Open the Records Center and choose Developmental Services Requests, not the general county form.",
                            "Carry the prepared contact, property, owner, and SSDS-record fields into GovQA.",
                            "Complete CAPTCHA, submit, save the reference, and track the seven-business-day window."
                    )
            )),
            Map.entry("MD::st-marys-county", profile(
                    "MD::st-marys-county",
                    "portal_with_fallback",
                    "Current county GIS with request fallback",
                    "Search St. Mary's County septic records before requesting assistance",
                    "The Health Department instructs users to search GIS by address or Tax ID. Its retired ArcGIS app now points to the county's replacement Public GIS Map; use the official request PDF only when Health Department records are missing or incomplete.",
                    "Open the current St. Mary's Public GIS Map",
                    "https://experience.arcgis.com/experience/8aef3502c23b46869e5afd27cd5a713c",
                    "Open the official Environmental Health request PDF",
                    "https://smchd.org/wp-content/uploads/Public-Information-Request-Form-NEW.pdf",
                    "A Health Department record response tied to the address or Tax ID",
                    "Confirm the Health Dept records section in the replacement map. An empty map result is not a written no-record response.",
                    List.of("Property address", "Tax ID if available", "Requested environmental health records"),
                    List.of("Septic permit or layout", "Perc history", "Repair or replacement record"),
                    List.of(
                            "Search the replacement county GIS by address or Tax ID.",
                            "Open the property, then review the County section and Health Dept records.",
                            "If missing or incomplete, use the official PDF and keep the written response."
                    )
            )),
            Map.entry("NY::suffolk-county", profile(
                    "NY::suffolk-county",
                    "phone_assisted",
                    "Phone-assisted file lookup",
                    "Call for a Suffolk County septic location record",
                    "Suffolk's existing-home lookup is primarily phone-assisted and is more likely to cover single-family homes built in 1973 or later.",
                    "Call Suffolk Wastewater Management",
                    "tel:631-852-5700",
                    "Read the official septic-record instructions (may be blocked)",
                    "https://www.suffolkcountyny.gov/Departments/Health-Services/Common-Issues-and-Questions",
                    "A location record, a documented phone outcome, or a formal request reference",
                    "Older homes may have limited coverage, and a phone outcome should not be presented as an online search result.",
                    List.of("Tax Map number", "Approximate construction year", "Subdivision, map, and lot if available"),
                    List.of("Septic location record", "Historical site record", "Written no-record or referral response"),
                    List.of(
                            "Collect the Tax Map number and approximate construction year before calling.",
                            "Ask the Office of Wastewater Management to search the property file.",
                            "Record the call outcome and open a written request if the file cannot be resolved."
                    )
            )),
            Map.entry("AZ::maricopa-county", profile(
                    "AZ::maricopa-county",
                    "portal_with_fallback",
                    "Free search with paid fallback",
                    "Search Maricopa County septic records online",
                    "Run the free official search first. If it is empty, choose the county's standard or expedited research request instead of treating the online result as a no-record finding.",
                    "Open the free Maricopa septic search",
                    "https://www.maricopa.gov/FormCenter/Environmental-Services-16/Online-Septic-Search-92",
                    "Open the paid research fallback",
                    "https://www.maricopa.gov/FormCenter/Environmental-Services-16/Online-Septic-Research-Request-Form-91",
                    "A downloaded record, a paid research response, or an official no-record result",
                    "The free agreement and paid research forms use reCAPTCHA. Standard research is $30 in 3–7 business days; expedited research is $60 in 1–2 business days.",
                    List.of("Site address", "Parcel number", "Year installed if known", "Subdivision and lot"),
                    List.of("Septic plans or permits", "Research response", "Ownership-transfer documents when applicable"),
                    List.of(
                            "Run the free official septic search.",
                            "If empty, prepare the county research form and choose standard or expedited service.",
                            "Return the record or county response to the property case."
                    )
            )),
            Map.entry("NC::brunswick-county", profile(
                    "NC::brunswick-county",
                    "metadata_only",
                    "Public permit metadata",
                    "Check Brunswick permit activity, then request the septic source file",
                    "Brunswick publishes live and archived permit metadata. Use it to identify a parcel and permit candidate, then request the original septic IP, CA, OP, or related file.",
                    "Search Brunswick permit metadata",
                    "https://www.brunswickcountync.gov/195/Permit-Reports",
                    "Open the Water Protection Program",
                    "https://www.brunswickcountync.gov/288/Water-Protection-Program",
                    "A parcel-matched permit candidate plus the original septic document or official response",
                    "General permit metadata can be revised, voided, or unrelated to the septic source document.",
                    List.of("Property address", "Parcel ID", "Date range or project type"),
                    List.of("Improvement permit", "Construction authorization", "Operation permit or notice"),
                    List.of(
                            "Search live or archived permit metadata and confirm the parcel.",
                            "Treat the result as a lead, not as the complete septic file.",
                            "Request the original septic documents from Water Protection."
                    )
            )),
            Map.entry("NC::forsyth-county", profile(
                    "NC::forsyth-county",
                    "official_request",
                    "Official file request",
                    "Request a Forsyth County septic permit and soil evaluation",
                    "The owner's guide explains the file but is not a search portal. Ask Environmental Health for the permit and soil evaluation, then use a separate release path for additions, pools, or accessory structures.",
                    "Open the Forsyth Environmental Health request",
                    "https://www.forsyth.cc/hhs/report_form.aspx",
                    "Check Forsyth septic services",
                    "https://www.forsyth.cc/publichealth/environmentalhealth/septic_main.aspx",
                    "The permit and soil evaluation, a project release, or an official no-record response",
                    "A Health Department Release for new work is not the same as obtaining the historical property file.",
                    List.of("Property address", "Parcel number", "Record, addition, or repair purpose"),
                    List.of("Septic permit", "Soil evaluation sheet", "Release or repair record when relevant"),
                    List.of(
                            "Request the historical permit and soil evaluation from Environmental Health.",
                            "Use the release lane only for additions, pools, decks, or other new work.",
                            "Attach the returned file or written response to the property case."
                    )
            )),
            Map.entry("TX::denton-county", profile(
                    "TX::denton-county",
                    "jurisdiction_first",
                    "Jurisdiction check required",
                    "Confirm Denton County OSSF jurisdiction, then request the existing permit",
                    "The current permit packet is for new work, not an archive search. Confirm that the parcel is in the county's unincorporated OSSF lane before requesting its existing permit or license to operate.",
                    "Open the Denton public-information form",
                    "https://www.dentoncounty.gov/DocumentCenter/View/10774/Public-Information-Request-Form-PDF",
                    "Check Denton Development Services",
                    "https://www.dentoncounty.gov/667/Development-Services",
                    "The correct authority plus the LTO, final approval, site plan, or written referral",
                    "Standard copies are $0.10 per page and other charges may apply. The 10-business-day language is not a guaranteed fulfillment time.",
                    List.of("Property address", "Parcel number", "Municipality or unincorporated status", "Requester contact and signature"),
                    List.of("License to operate", "Final approval", "Site plan and maintenance record"),
                    List.of(
                            "Confirm whether Denton County or a municipality regulates the parcel.",
                            "Ask the responsible authority for the existing OSSF permit and LTO.",
                            "Open the new-application packet only if new or repair work is actually required."
                    )
            )),
            Map.entry("TX::brazoria-county", profile(
                    "TX::brazoria-county",
                    "jurisdiction_first",
                    "Permit-status confirmation",
                    "Confirm the Brazoria OSSF authority and request the existing file",
                    "The official page explains the permitting process but does not provide a countywide existing-record search. Confirm the city, ETJ, or county authority before requesting permit status.",
                    "Open Brazoria Environmental Health contacts",
                    "https://www.brazoriacountytx.gov/departments/environmental-health/contact-us",
                    "",
                    "",
                    "An authority-confirmed final approval, permit file, unpermitted finding, or written referral",
                    "A process page is not a property record, and an older unpermitted system may require a separate remediation path.",
                    List.of("Property address", "Parcel number", "City, ETJ, or unincorporated status"),
                    List.of("Authorization to construct", "Final approval or transferable permit", "Maintenance or repair record"),
                    List.of(
                            "Confirm which city, ETJ, or county office owns the OSSF file.",
                            "Ask that authority to confirm permit status and return the existing file.",
                            "If no permit is confirmed, preserve the written outcome before discussing repair or sale actions."
                    )
            ))
    );

    private CountyAccessProfileCatalog() {
    }

    public static CountyAccessProfileView find(String countyKey) {
        return PROFILES.get(countyKey);
    }

    private static CountyAccessProfileView profile(
            String countyKey,
            String mode,
            String modeLabel,
            String heading,
            String summary,
            String primaryLabel,
            String primaryUrl,
            String secondaryLabel,
            String secondaryUrl,
            String completionLabel,
            String limitation,
            List<String> requiredInputs,
            List<String> expectedArtifacts,
            List<String> steps
    ) {
        return new CountyAccessProfileView(
                countyKey,
                mode,
                modeLabel,
                heading,
                summary,
                primaryLabel,
                primaryUrl,
                secondaryLabel,
                secondaryUrl,
                completionLabel,
                limitation,
                List.copyOf(requiredInputs),
                List.copyOf(expectedArtifacts),
                List.copyOf(steps)
        );
    }
}
