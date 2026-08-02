package com.example.septic.web;

import com.example.septic.data.model.CountyRecordsPage;

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
                    "https://lfportal.pwcgov.org/healthweb/Search.aspx?dbid=0&repo=HEALTH-DEPT",
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
                    "Confirm that Tarrant County or a contract city owns the OSSF file, then use the county public-information portal. If the portal is blocked or the authority is unclear, return to the OSSF office instead of treating the route as a no-record result.",
                    "Open the Tarrant County public-information portal",
                    "https://tarrantcountytx.justfoia.com/publicportal",
                    "Use the Tarrant County OSSF office if the portal is blocked or jurisdiction is unclear",
                    "https://www.tarrantcountytx.gov/en/engineering-services/environmental/ossf.html",
                    "The responsible OSSF authority plus its permit, LTO, site evaluation, or written referral",
                    "Do not treat a County Clerk no-result as proof that no OSSF file exists.",
                    List.of("Property address", "Parcel or account number", "City, unincorporated, or ETJ status"),
                    List.of("Permit or license to operate", "Site evaluation", "Recorded OSSF affidavit when applicable"),
                    List.of(
                            "Ask which county, contract-city, or ETJ office regulates the parcel.",
                            "When Tarrant County owns the file, open the public-information portal and submit in your own name.",
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
            Map.entry("TN::sumner-county", profile(
                    "TN::sumner-county",
                    "state_search_with_fallback",
                    "TDEC search with documented fallback",
                    "Search the TDEC septic file first, then use Sumner's published fallback",
                    "Sumner County directs septic-file searches to TDEC and publishes a records email when the online file search does not resolve the property.",
                    "Open the official TDEC septic search",
                    "https://tdec.tn.gov/document-viewer/search/stp",
                    "Open Sumner County's septic-file instructions",
                    "https://sumnercountytn.gov/departments/building-codes/",
                    "A property-matched TDEC septic permit, approval, certificate, layout, or written file response",
                    "The TDEC search may return 403 or no online match. Neither outcome proves that no record exists; Sumner publishes septicsystem.files@tn.gov and 615-687-7000 as the fallback.",
                    List.of("Property address", "Parcel or tax-map clue", "Current or prior owner when available"),
                    List.of("Septic permit or approval", "Certificate of completion", "System layout or written no-record response"),
                    List.of(
                            "Copy the strongest property clue and try the official TDEC septic search.",
                            "If TDEC returns 403 or no usable match, use Sumner County's published septic-file email or assistance number.",
                            "For building work, confirm both the valid septic approval and certificate of completion before treating the file as complete."
                    )
            )),
            Map.entry("TN::rutherford-county", profile(
                    "TN::rutherford-county",
                    "state_search_with_fallback",
                    "TDEC search with county guidance",
                    "Search the TDEC septic file before relying on Rutherford building plans",
                    "Rutherford County directs owners to Tennessee Environmental septic records and requires the recorded bedroom count to support residential additions and remodeling.",
                    "Open the official TDEC septic search",
                    "https://tdec.tn.gov/document-viewer/search/stp",
                    "Open Rutherford County's septic-record guidance",
                    "https://rutherfordcountytn.gov/planning-faq",
                    "A property-matched TDEC septic map, permit, bedroom approval, or written file response",
                    "The TDEC search may return 403 or no online match. Neither outcome proves that no file exists, and a remodel cannot assume more bedrooms than the septic record supports.",
                    List.of("Property address", "Parcel or tax-map clue", "Current or prior owner when available"),
                    List.of("Septic map or permit", "Approved bedroom count", "Repair record or written no-record response"),
                    List.of(
                            "Copy the strongest property clue and try the official TDEC septic search.",
                            "If the search is blocked or incomplete, follow Rutherford's Tennessee Environmental records guidance and request the file directly.",
                            "Before an addition or remodel, match the proposed bedroom count to the septic record."
                    )
            )),
            Map.entry("TN::cumberland-county", profile(
                    "TN::cumberland-county",
                    "state_search_with_fallback",
                    "TDEC search with Cookeville fallback",
                    "Search the TDEC SSDS file, then use the Cookeville field office",
                    "Cumberland County states that Tennessee issues septic permits. TDEC's Cookeville Environmental Field Office serves Cumberland County and publishes a septic-inquiries number plus public-records fallback.",
                    "Open the official TDEC SSDS record search",
                    "https://tdec.tn.gov/document-viewer/search/stp",
                    "Open the TDEC Cookeville Field Office",
                    "https://www.tn.gov/environment/contacts/field-offices/cookeville.html",
                    "A property-matched SSDS permit, approval, layout, inspection letter, or written TDEC response",
                    "Cumberland County's Codes Department is the building-permit route, not the septic-file owner. A blank TDEC search is not a no-record determination; use Cookeville's published 931-206-6329 septic-inquiries line or public-records route.",
                    List.of("Property address", "Parcel or tax-map clue", "Current and prior owner names when available", "Subdivision and lot when applicable"),
                    List.of("SSDS permit or approval", "System layout or inspection letter", "Repair record or written no-record response"),
                    List.of(
                            "Search the TDEC SSDS record index with the strongest property clue.",
                            "If the search is blocked or incomplete, contact the Cookeville Environmental Field Office, which serves Cumberland County.",
                            "Keep the returned permit, layout, inspection letter, repair file, or written no-record response with the property file."
                    )
            )),
            Map.entry("NC::iredell-county", profile(
                    "NC::iredell-county",
                    "direct_portal",
                    "Direct county GIS record search",
                    "Open the property in Iredell GIS and use the Septic Records tab",
                    "Iredell County publishes a direct workflow: accept the GIS disclaimer, search the property address, open the highlighted parcel, and use the Septic Records tab near the bottom of the property details.",
                    "Search Iredell County GIS",
                    "https://iredellcountync.mapgeo.io/datasets/properties?abuttersDistance=100&latlng=35.785949%2C-80.887561",
                    "Read Iredell's official search instructions",
                    "https://www.iredellcountync.gov/1034/View-Your-Septic-Record-Online",
                    "The latest operation permit tied to the highlighted Iredell County property, or a written Environmental Health response",
                    "The county says most permits are online, not all permits. If the Septic Records tab is absent or incomplete, contact Environmental Health rather than treating the blank result as proof that no record exists.",
                    List.of("Property address", "Parcel PIN if the address match is uncertain"),
                    List.of("Latest operation permit", "Septic layout or permit image available from the parcel", "Written follow-up response when the online record is missing"),
                    List.of(
                            "Accept the county GIS disclaimer and search the property address in the upper-right search.",
                            "Select the highlighted parcel and verify the address and parcel identity.",
                            "Scroll through the left property panel and open the Septic Records tab near the bottom.",
                            "Save the latest operation permit; if no usable record appears, follow up with Iredell Environmental Health."
                    )
            )),
            Map.entry("IN::porter-county", profile(
                    "IN::porter-county",
                    "phone_assisted",
                    "County record lookup by installation era",
                    "Use Porter County's installation-year rules before requesting the drawing",
                    "Porter County publishes different lookup requirements by installation era: no county permit records before 1974; address, subdivision and lot, plus the installation-era owner or applicant for 1974–2001; and the property address for 2001 to present.",
                    "Open Porter's existing-system record instructions",
                    "https://www.in.gov/localhealth/portercounty/environmental-health/septic-systems/locating-existing-septic-systems/",
                    "Open Porter County Environmental Health",
                    "https://www.in.gov/localhealth/portercounty/environmental-health/",
                    "A county septic drawing, field investigation report, permit material, or a documented pre-1974/no-file response",
                    "Porter County says it does not hold septic permit records before 1974. When no drawing exists, the county instructs the owner to locate the system or hire someone to locate it; do not invent a layout from general setback guidance.",
                    List.of("Property address", "Approximate installation year", "Subdivision name and lot for 1974–2001 records", "Installation-era owner or permit applicant for 1974–2001 records"),
                    List.of("Septic system drawing", "Repair or septic/well field investigation report", "Permit or documented record-availability response"),
                    List.of(
                            "Determine whether the installation was before 1974, from 1974–2001, or from 2001 onward.",
                            "Prepare the address and the additional subdivision, lot, and prior-owner clues required for the applicable era.",
                            "Contact Porter County Environmental Health and ask for the drawing or applicable field investigation and permit material.",
                            "If the county has no drawing, use a qualified locate for a project decision instead of guessing from typical dimensions."
                    )
            )),
            Map.entry("SC::horry-county", profile(
                    "SC::horry-county",
                    "jurisdiction_first",
                    "SCDES route after county parcel match",
                    "Confirm the Horry parcel, then route the septic file through SCDES",
                    "Horry County's GIS and online services establish the parcel or TMS clue; South Carolina DES remains the septic permitting and permit-copy route. The county parcel fabric is a tax and routing clue, not a survey or septic layout.",
                    "Open the SCDES septic contact route",
                    "https://des.sc.gov/permits-regulations/septic-tanks/septic-tanks-who-call",
                    "Open Horry County online services and GIS",
                    "https://www.horrycountysc.gov/online-services/",
                    "A parcel-matched septic permit copy, D-1740 or site-review trail, final-inspection status, or written SCDES response",
                    "Do not use the Horry GIS parcel fabric as the septic layout. Capture the TMS or parcel identity there, then use SCDES to resolve the permit file and any final-inspection, repair, replacement, or abandonment history.",
                    List.of("Property address", "Horry County TMS or parcel ID", "Owner and subdivision when available", "Permit number or builder clue when available"),
                    List.of("Septic permit copy", "D-1740 application or site-review record", "Permit to Construct or final-inspection status", "Repair, replacement, malfunction, or abandonment record"),
                    List.of(
                            "Use Horry County GIS to confirm the address, TMS or parcel ID, owner, and subdivision clue.",
                            "Open the SCDES septic contact route and identify the current county or regional intake for Horry County.",
                            "Request the permit copy, D-1740 or site-review trail, and final-inspection status tied to the parcel.",
                            "Keep the county parcel clue separate from the septic layout and treat an empty search as pending until SCDES responds."
                    )
            )),
            Map.entry("TN::williamson-county", profile(
                    "TN::williamson-county",
                    "official_request",
                    "County record-copy request",
                    "Request the existing Williamson County sewage-disposal inspection file",
                    "Williamson County publishes a dedicated Inspection Duplication of Records Request under Sewage Disposal forms. Use that existing-record lane before electronic plan review for new work.",
                    "Open the inspection records request",
                    "https://www.williamsoncounty-tn.gov/DocumentCenter/View/1855/INSPECTION-DUPLICATION-OF-RECORDS-REQUEST",
                    "Open Williamson County Sewage Disposal",
                    "https://www.williamsoncounty-tn.gov/126/Sewage-Disposal",
                    "A duplicated inspection or sewage-disposal property file, or a written county response",
                    "This is a record-copy request, not an instant search or a new electronic plan-review submission. The office lists 615-790-5751 and weekday hours of 8:00 a.m. to 4:30 p.m.",
                    List.of("Property address", "Parcel or lot clue", "Owner name", "Exact inspection or sewage-disposal records needed"),
                    List.of("Inspection record copy", "Septic location or approval material in the county file", "Written county response"),
                    List.of(
                            "Prepare the property address, parcel or lot, owner, and requested record types.",
                            "Open and complete the county's Inspection Duplication of Records Request.",
                            "Use electronic plan review only after the existing file is understood and new work is actually being submitted."
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
                    "Open the current Wastewater Management office page",
                    "https://www.suffolkcountyny.gov/Departments/Health-Services/WWM",
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
            )),
            Map.entry("WA::thurston-county", profile(
                    "WA::thurston-county",
                    "portal_with_fallback",
                    "Historic permit search with form fallback",
                    "Search Thurston septic records by parcel, permit, or project number",
                    "The county's Laserfiche search exposes historic septic system records, record drawings, and permit documents. Use the county record-drawing form when the online archive does not finish the job.",
                    "Search Thurston historic septic permits",
                    "https://weblink.co.thurston.wa.us/dspublic/customsearch.aspx?searchname=search",
                    "Open the official record drawing request",
                    "https://www.thurstoncountywa.gov/media/13802",
                    "A parcel-matched permit, historic septic record, record drawing, or county response",
                    "The archive requires at least one permit, project, or parcel value and does not accept wildcards. The request form asks for the tax parcel number and site address and states up to five working days.",
                    List.of("Eleven-digit tax parcel number", "Site address", "Permit or project number when known"),
                    List.of("Historic septic system record", "Record drawing or as-built", "Permit or project document"),
                    List.of(
                            "Find the parcel number from the address when it is not already known.",
                            "Search the county archive by parcel, permit, or project number and choose the septic record or record-drawing document type.",
                            "If the archive is incomplete, use the official record-drawing request and provide the parcel number and site address.",
                            "Return with the downloaded document or the county response."
                    )
            )),
            Map.entry("MD::harford-county", profile(
                    "MD::harford-county",
                    "official_request",
                    "Official well and septic records form",
                    "Complete Harford County's public-information form for the property file",
                    "Harford County Health Department publishes a PIA form specifically listing septic permits, inspections, enforcement complaints, site plans, perc tests, and well reports.",
                    "Open the Harford well and septic records form",
                    "https://harfordcountyhealth.com/wp-content/uploads/2019/10/PIA-Request-Form-Rev-2-9-19-Online-Version.pdf",
                    "Check the current Environmental Health instructions",
                    "https://harfordcountyhealth.com/wp-content/uploads/2025/11/SFD-Booklet-Enclosures-v.11-2025-Jodi-Higgs-HCHD-.pdf",
                    "The requested property records or a written Harford County response",
                    "The form states a 30-day PIA fulfillment window. Complete and send the county PDF itself; SepticPath does not replace, sign, or submit it.",
                    List.of("Applicant contact details", "Property address and 8-digit Tax ID", "Map, parcel, subdivision, and lot when available"),
                    List.of("Septic permits and inspections", "Perc tests and site plans", "Enforcement complaints or other selected records"),
                    List.of(
                            "Open the official PIA PDF and complete the applicant and property sections.",
                            "Select the exact well, septic, perc, inspection, complaint, or site-plan records needed.",
                            "Send the completed county form to the recipient printed on the form and retain the submission copy.",
                            "Return with the records or written response."
                    )
            )),
            Map.entry("CA::san-bernardino-county", profile(
                    "CA::san-bernardino-county",
                    "official_request",
                    "Current records portal with phone fallback",
                    "Prepare the property file, then open San Bernardino's current request portal",
                    "The current Environmental Health homepage sends Records Request users to the county's NextRequest portal. Prepare the property and record-scope facts here before opening it; if its security check blocks the browser, call with the same carry sheet.",
                    "Open the current San Bernardino records portal",
                    "https://sanbernardinocounty.nextrequest.com/requests/new",
                    "Call Environmental Health if the portal is blocked",
                    "tel:800-442-2283",
                    "The requested Environmental Health file, a request reference, or a written no-record/referral response",
                    "The current portal returned a Cloudflare 403 in our automated browser. The preparation fields are preserved from a county-authored records form, but they are not claimed to be the portal's current field list. Do not submit the older PDF unless the office confirms it is still accepted.",
                    List.of("Property location", "Specific records and date range", "Requester contact and preferred delivery email"),
                    List.of("OWTS permit or plan record", "Inspection or complaint record when requested", "Written no-record or referral response"),
                    List.of(
                            "Prepare the location, date range, record description, and requester details preserved from the county-authored form.",
                            "Open the current NextRequest portal, pass any browser verification yourself, and follow only the fields it currently displays.",
                            "If the portal remains blocked, call 800-442-2283 with the carry sheet and confirm the current email, fax, mail, or office intake.",
                            "Return with the record, request reference, or written department response."
                    )
            )),
            Map.entry("NC::cumberland-county", profile(
                    "NC::cumberland-county",
                    "portal_with_fallback",
                    "County record and layout options",
                    "Use Cumberland County's Water and Sewage record options",
                    "The official Water and Sewage page exposes Citizen Connect, a septic-layout request, the onsite office phone, and current service fees. New-permit and paid inspection fees are not historical copy fees.",
                    "Open Cumberland Water and Sewage record options",
                    "https://www.cumberlandcountync.gov/departments/public-health-group/public-health/environmental/water-sewage",
                    "Open Cumberland Citizen Connect",
                    "https://cumberlandcounty-nc-cc.connect.socrata.com/",
                    "A septic layout, permit record, portal result, or written onsite-office response",
                    "Citizen Connect may be slow or unavailable. Use the Water and Sewage page and its onsite phone when the portal or layout link does not resolve the property.",
                    List.of("Property address", "Tax parcel number when available", "Existing layout, permit, or other record needed"),
                    List.of("Septic layout or plot plan", "Existing permit or inspection record", "Portal result or office response"),
                    List.of(
                            "Open the Water and Sewage page and choose Citizen Connect or Request for Septic Layout.",
                            "Carry the address and parcel number into the official route.",
                            "If the online option fails, use the published onsite office phone and preserve the response.",
                            "Return with the layout, permit file, portal result, or request reference."
                    )
            )),
            Map.entry("CA::san-diego-county", profile(
                    "CA::san-diego-county",
                    "portal_with_fallback",
                    "Document library with PRRC fallback",
                    "Search San Diego septic records by APN, record ID, or street",
                    "The county publishes septic and graywater Land Use Program records in its Environmental Health Document Library. If the file is not online, the county directs users to the centralized Public Records Request Center.",
                    "Search the Environmental Health Document Library",
                    "https://www.sandiegocounty.gov/content/sdc/deh/doclibrary.html",
                    "Open the San Diego Public Records Request Center",
                    "https://pra.sandiegocounty.gov/requests/new",
                    "A parcel-matched septic permit, layout, inspection record, or PRRC response",
                    "Search with only one method at a time. Do not add street type, special characters, or wildcards. For a PRRC fallback, select DEHQ - Food, Water and Housing Division and complete the county's current request fields.",
                    List.of("Record ID, or APN, or street number and street name", "Street name without Ave, Dr, St, Ct, or another street type", "Request description and relevant attachment only when using PRRC"),
                    List.of("OWTS permit or layout", "Land Use Program inspection or related septic record", "PRRC confirmation or county response"),
                    List.of(
                            "Search with one identifier method only: Record ID, APN, or street number and street name.",
                            "Choose the septic or Land Use Program document category and review the matched documents.",
                            "If the file is not online, open PRRC and select DEHQ - Food, Water and Housing Division.",
                            "Return with the downloaded file, PRRC reference, or written response."
                    )
            )),
            Map.entry("MI::washtenaw-county", profile(
                    "MI::washtenaw-county",
                    "official_search",
                    "Embedded well and septic records search",
                    "Search Washtenaw building, well, and septic permit records",
                    "Washtenaw County embeds its public permit-record search and says the best first search uses only the street number.",
                    "Open Washtenaw's well and septic records search",
                    "https://www.washtenaw.org/search-permit-records",
                    "",
                    "",
                    "A property-matched well, sewage, septic, building, or inspection record",
                    "For best results, enter only the street number first. An empty search is not a county no-record determination.",
                    List.of("Street number for the first search", "Full property address for confirming the match", "Permit or owner clue only if the official search asks"),
                    List.of("Sewage or septic permit record", "Well or related Environmental Health record", "Building or inspection record tied to the property"),
                    List.of(
                            "Open the county search and begin with only the street number.",
                            "Use the returned address and parcel details to confirm the correct property.",
                            "Open the sewage, septic, well, building, or inspection documents that belong to that match.",
                            "Return with the downloaded record or a note that the online search did not resolve the property."
                    )
            )),
            Map.entry("MT::gallatin-county", profile(
                    "MT::gallatin-county",
                    "portal_with_office_copy",
                    "Permit archive with unscanned-file fallback",
                    "Search Gallatin wastewater permits across current and historic property clues",
                    "Gallatin County's official archive covers locally issued wastewater permits from 1966 forward and publishes the alternate search keys needed when an address does not work.",
                    "Open the Gallatin wastewater permit archive",
                    "https://gallatincountymt-tcmweb.tylerhost.net/eaglecm/web/",
                    "Read Gallatin's official search instructions",
                    "https://www.healthygallatin.org/environmental-health/water-quality/wastewater-search-information/",
                    "A wastewater permit file, scanned image, or identified unscanned permit",
                    "Use less information, try historic owners and land descriptions, and review the entire file. A result marked No Images requires an in-office copy; the county publishes per-page copy charges.",
                    List.of("Current or prior property owner", "Current or prior road address", "Subdivision, COS, lot, section, township, or range when available"),
                    List.of("Individual, shared, or multi-user wastewater permit", "Inspection, variance, addendum, or memo contained in the file", "Permit reference showing that the images are not yet scanned"),
                    List.of(
                            "Open the archive, choose Enter, and start with the smallest useful owner or address clue.",
                            "Retry with subdivision, COS, lot, section, township, range, issue date, or inspection date when needed.",
                            "Review the full matched document rather than only the first permit page.",
                            "If the result says No Images, preserve the permit reference and use the county's published in-office copy route."
                    )
            )),
            Map.entry("MD::frederick-county", profile(
                    "MD::frederick-county",
                    "official_request",
                    "Official property information request",
                    "Complete Frederick County's well and septic information form",
                    "Frederick County requires its official research form for property-specific well and septic records and states that incomplete requests will not be processed.",
                    "Open the Frederick information request form",
                    "https://health.frederickcountymd.gov/376/Information-Request-Form",
                    "Open the printable county form",
                    "https://health.frederickcountymd.gov/DocumentCenter/View/3444/Request-for-Information-2023",
                    "The requested septic location, permitted-bedroom result, well report, or written county response",
                    "The county requires the current owner and prior owners back to 1950 or the installation year. Well and septic information takes at least 10 business days; technical or percolation requests may take up to 30 business days.",
                    List.of("Street address, subdivision, lot, tax map, parcel, and year built", "Current owner and previous owners back to 1950 or the installation year", "Requester contact and preferred delivery method"),
                    List.of("Existing or proposed septic location", "Bedrooms permitted or total bedrooms allowed", "Well completion report or another specifically selected record"),
                    List.of(
                            "Use county property and deed searches to assemble the current and required prior-owner names.",
                            "Complete every applicable property, owner, requester, and delivery field on the official form.",
                            "Select the exact existing-property or new-property records needed.",
                            "Complete CAPTCHA and final submission on the county site, then retain the submitted copy and response."
                    )
            )),
            Map.entry("MN::st-louis-county", profile(
                    "MN::st-louis-county",
                    "official_search",
                    "Free Land Explorer septic archive",
                    "Open St. Louis County's scanned sanitary permit collection",
                    "The county provides scanned sanitary permits through Land Explorer for free, without a password or membership fee, and publishes a parcel-based access guide.",
                    "Open St. Louis County Land Explorer",
                    "https://gis.stlouiscountymn.gov/landexplorer/?webmap=60d0f48848744fc1bc12d68ad3e253b8",
                    "Read the county septic-record instructions",
                    "https://www.stlouiscountymn.gov/departments-a-z/planning-zoning/onsite-wastewater/training-and-education/septic-records",
                    "A parcel-matched sanitary permit or scanned septic document",
                    "Choose the On-Site Wastewater theme, select the parcel, open Septic Records, accept the document disclaimer, and use View Doc. Not every county record has been scanned.",
                    List.of("Property location or parcel PIN", "Correct parcel selected in Land Explorer", "Lease PIN when the property uses one"),
                    List.of("Scanned sanitary permit", "Septic record linked from the parcel popup", "A note that the matched parcel has no scanned document"),
                    List.of(
                            "Open Land Explorer, accept its entry notice, and choose the On-Site Wastewater theme.",
                            "Zoom to or search for the parcel and select the correct property.",
                            "Open Septic Records from the parcel popup and move through popup records when needed.",
                            "Accept the record disclaimer, choose View Doc, and return with the downloaded permit."
                    )
            )),
            Map.entry("MI::livingston-county", profile(
                    "MI::livingston-county",
                    "split_archive",
                    "Current and archived record search",
                    "Choose Livingston well and septic records by document year",
                    "Livingston County separates current records from 2018 forward and archived records from 2017 and earlier. The county says to search with only the street address or parcel ID.",
                    "Search current Livingston records (2018-present)",
                    "https://bsaonline.com/Home/MunicipalityHome?uid=2015",
                    "Search archived Livingston records (2017 and older)",
                    "https://images.livgov.com/EH/CustomSearch.aspx?SearchName=EnvironmentalHealth",
                    "A property-matched well or septic permit record from the correct year range",
                    "Soil evaluations are excluded from the online well and septic record search. Search only by street address or parcel ID and use the archive that matches the likely document year.",
                    List.of("Street address or parcel ID", "Approximate permit or installation year", "Owner or record clue only after a property match"),
                    List.of("Current well or septic record from 2018-present", "Archived well or septic record from 2017 or earlier", "Property addition or site-review record when separately relevant"),
                    List.of(
                            "Estimate whether the target record is from 2018-present or 2017 and earlier.",
                            "Open the matching county-linked archive and search only by street address or parcel ID.",
                            "Confirm the property before opening or downloading the record.",
                            "If neither archive resolves the file, retain both search outcomes before contacting Environmental Health."
                    )
            )),
            Map.entry("VA::hanover-county", profile(
                    "VA::hanover-county",
                    "official_request",
                    "Official district records form",
                    "Complete the Chickahominy well, septic, and drainfield request",
                    "The official health-district form covers Hanover and asks for the identifiers used across current GPIN, tax-map, subdivision, and older owner or builder files.",
                    "Open the Hanover onsite-information request form",
                    "https://www.vdh.virginia.gov/content/uploads/sites/84/2017/02/Request-for-Onsite-Info.pdf",
                    "Open Chickahominy onsite sewage services",
                    "https://www.vdh.virginia.gov/chickahominy/onsite-sewage-water-services/",
                    "A well, septic, or drainfield record, a no-record result, or a fee notice",
                    "The form states 3-5 business days after a complete request. Pre-1986 septic systems may have no permit file. Research, copy, and mailing fees may apply, while one copy of the requester's own record is free.",
                    List.of("Requested information and requester contact details", "Property address, GPIN, tax map, subdivision, section, block, and lot", "Approximate house age plus original owner or builder for older properties"),
                    List.of("Septic permit or drainfield information", "Well information when selected", "Written no-record result or advance fee notice"),
                    List.of(
                            "Download and complete the health district's official request form.",
                            "Add every available parcel and subdivision identifier, especially for an older property.",
                            "Use the current Chickahominy service page to confirm the receiving office and submission method.",
                            "Retain the completed form, any fee notice, and the records or no-record response."
                    )
            )),
            Map.entry("NC::craven-county", profile(
                    "NC::craven-county",
                    "portal_with_fallback",
                    "GIS operation permit with official form fallback",
                    "Find Craven operation permits in GIS, then request the missing file",
                    "Craven County directs users to GIS first for operation permits issued from 2003 forward and publishes a specific septic-and-well document form when GIS does not resolve the file.",
                    "Search Craven County GIS",
                    "https://gis.cravencountync.gov/maps/map.htm",
                    "Open the Craven septic and well document form",
                    "https://www.cravencountync.gov/FormCenter/FLI-26/Request-For-Document-Septic-Wells-137",
                    "An operation permit, septic or well document, or county form confirmation",
                    "In GIS, search by name, address, or parcel number, open EH Permits, and select the complete Operation Permit. The fallback form requires requester name and email plus property owner and address; CAPTCHA and final submission remain on the county site.",
                    List.of("Owner name, property address, city, state, and ZIP", "Parcel number or PID when available", "Requester name, date, and email for the fallback form"),
                    List.of("Operation Permit issued from 2003 forward", "Septic or well document returned through the official form", "County confirmation or documented no-file outcome"),
                    List.of(
                            "Search GIS by owner name, address, or parcel number and confirm the property.",
                            "Open EH Permits and choose the complete Operation Permit record.",
                            "If GIS does not contain the file, complete the county's Request For Document form.",
                            "Handle CAPTCHA and final submission on the county site, then return with the permit or confirmation."
                    )
            )),
            Map.entry("NJ::gloucester-county", profile(
                    "NJ::gloucester-county",
                    "official_request",
                    "Official OPRA request form",
                    "Request Gloucester County septic records with the exact parcel clues",
                    "Gloucester County prefers its online OPRA form and specifically tells septic and well requesters to include the lot, block, street address, and city.",
                    "Open the Gloucester County OPRA form",
                    "https://nj-gloucestercounty.civicplus.com/FormCenter/Human-Resources-14/Open-Records-Request-Form-OPRA-69",
                    "Review the county septic program",
                    "https://www.gloucestercountynj.gov/697/Septic-Systems",
                    "An OPRA confirmation plus the property-matched septic, well, repair, or inspection records",
                    "The form uses reCAPTCHA and an electronic signature. Most requests use a seven-business-day response window; commercial-purpose or Daniel's Law review may use fourteen business days. Paper copies and special service work may cost extra.",
                    List.of(
                            "Requester contact and mailing information",
                            "Property street address and city",
                            "Lot and block",
                            "Preferred delivery and the form's required certifications"
                    ),
                    List.of(
                            "Septic permit, plan, or continuing-use record",
                            "Real-estate inspection, repair, or alteration record",
                            "Written partial, denial, or no-record response"
                    ),
                    List.of(
                            "Prepare the requester details, lot, block, street address, and city shown by the official form.",
                            "Describe the existing septic and well records precisely and choose the delivery method.",
                            "Review the certifications, add your electronic signature, complete reCAPTCHA, and submit in your own name.",
                            "Save the confirmation and track the applicable seven- or fourteen-business-day window."
                    )
            )),
            Map.entry("MD::prince-georges-county", profile(
                    "MD::prince-georges-county",
                    "portal_with_fallback",
                    "Guest record search with Momentum fallback",
                    "Search Prince George's County records as a guest before opening Momentum",
                    "The county's eRecords Explorer lets a guest search by street number and street name or by application sequence, year, and revision. The Health Department says well and septic information requests are processed in Momentum.",
                    "Open the DPIE eRecords Explorer",
                    "https://lookseerecords.princegeorgescountymd.gov/",
                    "Open Momentum for a Health information request",
                    "https://momentumhome.princegeorgescountymd.gov/",
                    "A property-matched eRecords result, application record, or tracked Momentum information request",
                    "The eRecords beta says data reaches back as far as case year 2010. Momentum requires a profile or login, and an empty eRecords search is not an official no-record response.",
                    List.of(
                            "Street number and street name",
                            "Application sequence number and year when known",
                            "Revision number when known"
                    ),
                    List.of(
                            "DPIE permit record or plan",
                            "Well or septic information-request response",
                            "Application or request tracking number"
                    ),
                    List.of(
                            "Continue as Guest in eRecords and choose address or application-number search.",
                            "Enter the exact split fields shown by the official search and review the matched case.",
                            "If the record is missing or the Health Department owns it, create or use a Momentum profile and start the information-request route.",
                            "Complete login, payment if shown, and final submission on Momentum, then retain the tracking number."
                    )
            )),
            Map.entry("CO::adams-county", profile(
                    "CO::adams-county",
                    "portal_with_fallback",
                    "Live county septic search",
                    "Search Adams County septic records by address, parcel, owner, or permit number",
                    "The official Adams County search covers records before 2023 and records from 2023 forward. Search by address, parcel, owner, or permit number before starting a sale or remodel permit.",
                    "Open the Adams County septic search",
                    "https://experience.arcgis.com/experience/aec5c4ffe767495f8576de0d235c7a55",
                    "Open the current septic program and use-permit forms",
                    "https://adamscountyhealthdepartment.org/licensing-inspections/septic-systems/",
                    "A property-matched search result or the correct use, transfer, repair, or expansion permit route",
                    "The search is updated weekly and map points may be misplaced by geocoding. A sale requires a use/transfer-of-title permit and certified inspection; multiple systems need separate inspection reports and fees.",
                    List.of(
                            "One search clue: address, parcel, owner, or permit number",
                            "For a use permit: APN, owner and applicant details, dwelling and bedroom details",
                            "Certified inspection report, fee, and recent pumper receipt when available"
                    ),
                    List.of(
                            "Pre-2023 or 2023-forward septic record",
                            "Use or transfer-of-title permit",
                            "Repair, expansion, or final approval record"
                    ),
                    List.of(
                            "Search the official tool with one exact property clue and confirm the parcel.",
                            "Check both the Before 2023 and 2023 & After record layers.",
                            "If the purpose is a sale, remodel, addition, or repair, choose the matching current county form.",
                            "Transfer the form fields, attach the certified report and required items, then submit and pay on the official route."
                    )
            )),
            Map.entry("OH::mahoning-county", profile(
                    "OH::mahoning-county",
                    "official_request",
                    "Direct official email request",
                    "Request Mahoning County septic records by email",
                    "Mahoning County Public Health tells requesters to email the information they are looking for and returns public records by email after processing. Its septic program separately states that septic and well testing is required before a home sale.",
                    "Open the Mahoning public-records policy",
                    "https://www.mahoninghealth.org/public-records-policy/",
                    "Email Mahoning County Public Health",
                    "mailto:info@mahoninghealth.org",
                    "An emailed property record, written referral, denial explanation, or documented no-record response",
                    "Emailed records are free. Paper copies are $0.05 per page and mailed records can add postage. The county promises a reasonable period based on volume and review, not a fixed delivery date.",
                    List.of(
                            "Property address and parcel clue",
                            "Current or prior owner when known",
                            "Specific existing septic, well, sale-test, permit, or repair records sought"
                    ),
                    List.of(
                            "Septic permit and installation or approval record",
                            "Sale-time septic and well test record",
                            "Repair, alteration, or monitoring record"
                    ),
                    List.of(
                            "Identify the property and list the existing records needed.",
                            "Open your email app from the verified county address and review the factual request.",
                            "Send in your own name and retain the sent message.",
                            "Attach the returned file or written outcome to the property task."
                    )
            )),
            Map.entry("TN::bradley-county", profile(
                    "TN::bradley-county",
                    "state_search_with_fallback",
                    "TDEC search with Chattanooga fallback",
                    "Search the Bradley County SSDS file, then use the Chattanooga field office",
                    "TDEC's Chattanooga Environmental Field Office lists Bradley among the counties it serves. Search the statewide SSDS index first; when the result is blocked, incomplete, or does not match the parcel, use the field office's Division of Water Resources or public-records route.",
                    "Open the official TDEC SSDS record search",
                    "https://tdec.tn.gov/document-viewer/search/stp",
                    "Open the Chattanooga Environmental Field Office",
                    "https://www.tn.gov/environment/contacts/field-offices/chattanooga.html",
                    "A property-matched SSDS permit, approval, layout, inspection letter, repair file, or written TDEC response",
                    "The Chattanooga page confirms service territory and a public-records fallback, but it does not publish a Bradley-specific intake form, fee, or turnaround. A blank viewer result is not an official no-record determination.",
                    List.of(
                            "Property address",
                            "Parcel or tax-map clue",
                            "Current and prior owner names when available",
                            "Subdivision and lot when applicable"
                    ),
                    List.of(
                            "SSDS construction permit or approval",
                            "System layout, inspection letter, or certificate of completion",
                            "Repair record or written no-record response"
                    ),
                    List.of(
                            "Search the TDEC SSDS index with the strongest property clue and confirm the parcel match.",
                            "If the search is blocked or incomplete, use the Chattanooga field office, which explicitly serves Bradley County.",
                            "Ask Division of Water Resources for the permit, layout, closeout, and repair history tied to the parcel.",
                            "Keep the returned file or written response with the property record task."
                    )
            )),
            Map.entry("TN::sullivan-county", profile(
                    "TN::sullivan-county",
                    "official_request",
                    "Published septic-layout email route",
                    "Request a Sullivan County septic layout from the Johnson City field office",
                    "The Johnson City Environmental Field Office explicitly serves Sullivan County and publishes a septic-layout request route. Its instructions say to email the property address, subdivision name when applicable, original owner, and previous owner.",
                    "Email the Johnson City septic-layout desk",
                    "mailto:TDEC.Johnsoncity.EFO@tn.gov",
                    "Open the Johnson City Environmental Field Office",
                    "https://www.tn.gov/environment/contacts/field-offices/johnson.html",
                    "A septic layout, related SSDS file, public-records response, or documented no-record outcome",
                    "The office publishes 423-854-5392 for 24-hour automated septic-layout and public-records assistance, but it does not promise a response time or a complete historical file for every property.",
                    List.of(
                            "Property address",
                            "Subdivision name when applicable",
                            "Original owner name",
                            "Previous owner name"
                    ),
                    List.of(
                            "Existing-system septic layout",
                            "SSDS permit, approval, or repair record returned with the layout",
                            "Written referral or no-record response"
                    ),
                    List.of(
                            "Prepare the four property clues published by the Johnson City field office.",
                            "Open the email route and request the existing Sullivan County septic layout in your own name.",
                            "If email is not workable, use the published 423-854-5392 automated assistance line or public-records route.",
                            "Save the sent request and attach the returned layout or written outcome to the property task."
                    )
            )),
            Map.entry("TN::loudon-county", profile(
                    "TN::loudon-county",
                    "state_search_with_fallback",
                    "County-published TDEC search and fallback",
                    "Search the Loudon County septic file, then use the published email fallback",
                    "Loudon County publishes three concrete routes: the TDEC septic service, the online septic-file search, and septicsystem.files@tn.gov for a request. It also directs further assistance to the Knoxville Environmental Field Office.",
                    "Open the official TDEC SSDS record search",
                    "https://tdec.tn.gov/document-viewer/search/stp",
                    "Open Loudon County's septic contact instructions",
                    "https://planningandcodes.loudoncounty-tn.gov/buildingCodes_septicSystemContact.php",
                    "A property-matched SSDS file, emailed record response, or documented no-record outcome",
                    "Loudon County publishes the route but not a guaranteed response time, fee, or exact email field list. Use the strongest property clues and do not treat a blank online search as a no-record determination.",
                    List.of(
                            "Property address",
                            "Parcel, tax-map, or legal-description clue",
                            "Current or prior owner when available",
                            "Approximate installation or permit year when known"
                    ),
                    List.of(
                            "SSDS permit or approval",
                            "System layout, inspection letter, or certificate of completion",
                            "Repair file or written no-record response"
                    ),
                    List.of(
                            "Search the official TDEC SSDS index and confirm the property match.",
                            "If the search is blocked or incomplete, follow Loudon County's published septicsystem.files@tn.gov request route.",
                            "Use the published Knoxville field-office number only when the file search and email route do not resolve ownership or next steps.",
                            "Keep the returned record, request correspondence, or written no-record response."
                    )
            )),
            Map.entry("TN::maury-county", profile(
                    "TN::maury-county",
                    "state_search_with_fallback",
                    "TDEC search with Columbia fallback",
                    "Search the Maury County SSDS file, then use the Columbia field office",
                    "TDEC's Columbia Environmental Field Office lists Maury among the counties it serves and coordinates public-records requests when a data viewer does not resolve the file. Search the statewide SSDS index first, then carry a precise parcel request into that fallback.",
                    "Open the official TDEC SSDS record search",
                    "https://tdec.tn.gov/document-viewer/search/stp",
                    "Open the Columbia Environmental Field Office",
                    "https://www.tn.gov/environment/contacts/field-offices/columbia.html",
                    "A property-matched SSDS permit, approval, layout, inspection letter, repair file, or written TDEC response",
                    "The Columbia page confirms Maury service territory and public-records coordination, but it does not publish a Maury-specific form, fee, or turnaround. A blank viewer result is not an official no-record determination.",
                    List.of(
                            "Property address",
                            "Parcel or tax-map clue",
                            "Current and prior owner names when available",
                            "Subdivision, lot, and approximate permit year when known"
                    ),
                    List.of(
                            "SSDS construction permit or approval",
                            "System layout, inspection letter, or certificate of completion",
                            "Repair record or written no-record response"
                    ),
                    List.of(
                            "Search the TDEC SSDS index with the strongest property clue and confirm the parcel match.",
                            "If the search is blocked or incomplete, use the Columbia field office, which explicitly serves Maury County.",
                            "Ask Division of Water Resources or the public-records coordinator for the site-specific SSDS file.",
                            "Keep the returned file or written response with the property record task."
                    )
            )),
            Map.entry("TN::jefferson-county", profile(
                    "TN::jefferson-county",
                    "phone_assisted",
                    "County Environmental Health intake",
                    "Confirm the Jefferson County permit, final approval, or existing-system record",
                    "Jefferson County Environmental Health issues septic permits and publishes separate services for repair, site evaluation, inspection letters, existing-system evaluation, and certificate of verification. For new work it requires subdivision and lot information or a recorded plat and soil map, followed by a final inspection before cover-up.",
                    "Call Jefferson County Environmental Health",
                    "tel:865-397-1617",
                    "Open Jefferson County septic permit guidance",
                    "https://jeffersoncountytn.gov/environmental-health/",
                    "The parcel permit and final approval, an existing-system evaluation or verification artifact, or a written office outcome",
                    "The official page currently shows conflicting new-permit prices: $500 in the fee schedule and $250 in the step-by-step instructions. Confirm the current fee and service scope with the county before payment.",
                    List.of(
                            "Property address and parcel or owner clue",
                            "Subdivision name and lot number when applicable",
                            "Recorded plat and soil map for a non-subdivision new-permit request",
                            "Purpose: historical record, repair, inspection letter, existing-system evaluation, or certificate of verification"
                    ),
                    List.of(
                            "Septic permit and final approval",
                            "Inspection letter, existing-system evaluation, or certificate of verification when requested",
                            "Repair, upgrade, site-evaluation, or written no-record outcome"
                    ),
                    List.of(
                            "Identify whether you need the historical permit file or a new paid evaluation or verification service.",
                            "Call Environmental Health with the address, parcel or owner clue, and subdivision and lot when applicable.",
                            "Ask for the permit and final-approval record first; order a current inspection letter or verification only when the transaction or project requires it.",
                            "Confirm the current fee because the county page publishes two different new-permit amounts, then keep the receipt and resulting artifact."
                    )
            )),
            Map.entry("TN::wilson-county", profile(
                    "TN::wilson-county",
                    "official_request",
                    "TDEC public-records portal",
                    "Submit a Wilson County SSDS records request through TDEC",
                    "TDEC lists Wilson County in the Nashville Field Office service area. Its current public-records Formstack accepts a site-specific county, location, date range, record type, keywords, division, and supporting files.",
                    "Open the TDEC public-records form",
                    "https://stateoftennessee.formstack.com/forms/public_records_request",
                    "Open the Nashville Environmental Field Office",
                    "https://www.tn.gov/environment/contacts/field-offices/nashville.html",
                    "A TDEC request confirmation and the Wilson County SSDS file or written disposition",
                    "The form may request proof of Tennessee citizenship. More than three requests per month may incur additional costs. Signature, uploads, any cost waiver, and final submission remain on TDEC.",
                    List.of(
                            "Requester name, phone, and confirmed email",
                            "Tennessee citizenship and litigation answers",
                            "Inspection or copy choice, delivery, date range, Wilson County, location, and record description",
                            "Division of Water Resources and supporting map when needed"
                    ),
                    List.of(
                            "SSDS construction permit and certificate of completion",
                            "Approved layout or site record",
                            "Repair, inspection, or correspondence record"
                    ),
                    List.of(
                            "Prepare the exact fields shown by the TDEC public-records form.",
                            "Choose Wilson County and Division of Water Resources, then identify the site and date range.",
                            "Review citizenship, cost, delivery, and litigation answers; upload a map only if useful.",
                            "Sign and submit in your own name, then save the Formstack confirmation."
                    )
            )),
            Map.entry("TN::sevier-county", profile(
                    "TN::sevier-county",
                    "phone_assisted",
                    "Prepared county handoff",
                    "Prepare the Sevier County SSD file search before contacting Environmental Health",
                    "TDEC directs Sevier users to the county. SepticPath preserves the exact property and result-delivery fields from the county-authored information form, while treating its current submission channel as unconfirmed because the county website returns 403 and the direct PDF returns 404.",
                    "Call with the prepared script",
                    "tel:865-429-1766",
                    "Open the TDEC county-jurisdiction notice",
                    "https://www.tn.gov/environment/permits/water/septic-systems-permits/ssp/wr-sds-online-application-for-ground-water-protection-services.html",
                    "The current county file-search instruction, a permit or certificate response, or a documented no-record outcome",
                    "Do not use the statewide SSDS application. The preserved county fields are reliable preparation evidence, but the old PDF must not be represented as a currently accepted submission document until the office confirms it.",
                    List.of(
                            "Current owner, property address, and road name",
                            "Subdivision, lot, block, phase, and section when known",
                            "Vacant-lot status, construction date, bedroom count, and owner history",
                            "Preferred result delivery and requester contact details"
                    ),
                    List.of(
                            "SSD system permit",
                            "Certificate of completion",
                            "Written or documented no-record response"
                    ),
                    List.of(
                            "Complete the preserved county-authored property and delivery fields.",
                            "Use the prepared script when calling 865-429-1766; ask whether the former form is still current.",
                            "Confirm whether the office now accepts email, fax, mail, pickup, or another method, plus any fee and turnaround.",
                            "Sign, date, and send only through the confirmed route; keep the staff name or reference number.",
                            "Return with the permit, certificate, or documented no-record result."
                    )
            )),
            Map.entry("TN::montgomery-county", profile(
                    "TN::montgomery-county",
                    "official_request",
                    "TDEC public-records portal",
                    "Submit a Montgomery County SSDS records request through TDEC",
                    "TDEC lists Montgomery County in the Nashville Field Office service area. Its current public-records Formstack provides the working structured fallback when the SSDS viewer does not resolve the parcel.",
                    "Open the TDEC public-records form",
                    "https://stateoftennessee.formstack.com/forms/public_records_request",
                    "Open the Nashville Environmental Field Office",
                    "https://www.tn.gov/environment/contacts/field-offices/nashville.html",
                    "A TDEC request confirmation and the Montgomery County SSDS file or written disposition",
                    "The form may request proof of Tennessee citizenship. More than three requests per month may incur additional costs. Signature, uploads, any cost waiver, and final submission remain on TDEC.",
                    List.of(
                            "Requester name, phone, and confirmed email",
                            "Tennessee citizenship and litigation answers",
                            "Inspection or copy choice, delivery, date range, Montgomery County, location, and record description",
                            "Division of Water Resources and supporting map when needed"
                    ),
                    List.of(
                            "SSDS construction permit and certificate of completion",
                            "Approved layout or site record",
                            "Repair, inspection, or correspondence record"
                    ),
                    List.of(
                            "Prepare the exact fields shown by the TDEC public-records form.",
                            "Choose Montgomery County and Division of Water Resources, then identify the site and date range.",
                            "Review citizenship, cost, delivery, and litigation answers; upload a map only if useful.",
                            "Sign and submit in your own name, then save the Formstack confirmation."
                    )
            )),
            Map.entry("NC::guilford-county", profile(
                    "NC::guilford-county",
                    "phone_assisted",
                    "Phone lookup with records fallback",
                    "Call Guilford County for the system location, then request the underlying file",
                    "Guilford County says owners may call 336-641-7613 between 8 a.m. and 10 a.m. for system type and location when the county has an updated property file. The county also links its official public-records portal.",
                    "Call Guilford On-Site Water Protection",
                    "tel:336-641-7613",
                    "Open Guilford County public records",
                    "https://guilfordcountync.nextrequest.com/",
                    "A documented system-location answer, property file, request reference, or written no-record response",
                    "The phone lookup depends on the county having an updated file and is limited to the published 8-10 a.m. call window. NextRequest may require verification and final submission.",
                    List.of(
                            "Property address",
                            "Parcel or owner clue when known",
                            "Whether you need location only or the full permit, layout, and repair file"
                    ),
                    List.of(
                            "System type and location information",
                            "Improvement permit, construction authorization, and operation permit",
                            "Layout, repair, monitoring, or abandonment record"
                    ),
                    List.of(
                            "Call between 8 a.m. and 10 a.m. with the property clues ready.",
                            "Record the system type and location answer and ask whether an updated file exists.",
                            "If you need copies, open the county public-records portal and request the underlying On-Site Water Protection file.",
                            "Complete verification and final submission on the county portal, then save the request reference."
                    )
            )),
            Map.entry("TN::davidson-county", profile(
                    "TN::davidson-county",
                    "portal_with_fallback",
                    "Scanned property-file search",
                    "Search Davidson County septic engineering files by parcel",
                    "Metro Nashville publishes a scanned Environmental Engineering record portal. Start with the parcel or tax ID from Property Information, search the Health Environmental Engineering Reports, and use the published phone or email only when the desired file is absent.",
                    "Search Metro Nashville engineering records",
                    "https://documents.nashville.gov/",
                    "Open Metro septic records instructions",
                    "https://www.nashville.gov/departments/health/environmental-health/septic-and-sewage-disposal-systems",
                    "A property-matched scanned engineering file, bedroom approval and inspection dates, or a written Metro response",
                    "The portal contains scanned documents of record and adds files periodically. An empty search is not proof that Metro has no file; use 615-340-5630 or septicinfo@nashville.gov for the missing-record fallback.",
                    List.of(
                            "Property address",
                            "Map and parcel number or tax ID",
                            "Current owner name",
                            "Subdivision and lot number when applicable"
                    ),
                    List.of(
                            "Scanned Health Environmental Engineering property file",
                            "Approved bedroom count and approval or inspection dates",
                            "SSDS approval, layout, repair record, or written no-record response"
                    ),
                    List.of(
                            "Use Metro Property Information to confirm the map and parcel number or tax ID.",
                            "Open the document portal and choose Health Environmental Engineering Reports.",
                            "Search the confirmed property and export the matching scanned record as a PDF.",
                            "If the desired file is missing, contact 615-340-5630 or septicinfo@nashville.gov and retain the response."
                    )
            )),
            Map.entry("TN::madison-county", profile(
                    "TN::madison-county",
                    "official_request",
                    "County septic-record request form",
                    "Request the Madison County septic drawing and property file",
                    "Madison County provides a dedicated septic-system records form for Madison County properties. It asks for the current owner, other known owners, full property address, requester contact details and role, with the tax map ID or parcel number as an optional strengthening clue.",
                    "Open the Madison County records request",
                    "https://madisoncountytn.gov/FormCenter/Health-Department-11/Septic-System-Records-Request-89",
                    "Call without sharing contact information",
                    "tel:731-423-3020",
                    "The requested septic drawing or property record, an emailed form copy, or a documented county response",
                    "The form is limited to Madison County, uses reCAPTCHA, and requires requester contact details for online submission. The county publishes option 4 at 731-423-3020 when a requester does not want to share contact information online, but it does not promise a turnaround.",
                    List.of(
                            "Current property owner name",
                            "Original owner or other known owners when available",
                            "Full property address including city, state, and ZIP",
                            "Applicant name, phone, email, and role",
                            "Tax map ID or parcel number when available"
                    ),
                    List.of(
                            "Existing septic-system drawing",
                            "Property-matched permit or approval record returned with the drawing",
                            "Email copy, request outcome, or written no-record response"
                    ),
                    List.of(
                            "Confirm that the property is in Madison County and collect the owner and address fields.",
                            "Add prior owners and the tax map or parcel number when available to reduce false matches.",
                            "Open the county form, review the values, complete reCAPTCHA, and submit in your own name.",
                            "Save the emailed copy and attach the returned drawing or written outcome to the property task."
                    )
            )),
            Map.entry("TN::shelby-county", profile(
                    "TN::shelby-county",
                    "office_help",
                    "County Water Quality permit route",
                    "Route a Shelby County septic installation, repair, modification, or abandonment",
                    "Shelby County's Water Quality Branch requires an application for septic installation, modification, repair, or abandonment. The office checks completeness before scheduling a site visit, inspects installation, and requires a repair permit before repair work begins.",
                    "Open Shelby County SSDS guidance",
                    "https://www.shelbycountytn.gov/FAQ.aspx?PRINT=YES&QID=322",
                    "Open the Water Quality Branch page",
                    "https://www.shelbycountytn.gov/221/Water-Quality-Branch",
                    "A complete application routed to the Water Quality Branch, a permit, inspection outcome, or written office direction",
                    "The county FAQ publishes a $175 non-refundable processing fee and a normal one-to-two-week permit window only after a complete application is received. Confirm current forms, fees, and timing with the county before payment; this route is for permit work, not proof of current system condition.",
                    List.of(
                            "Property address and parcel or owner clue",
                            "Work type: install, modify, repair, or abandon",
                            "Plot plan",
                            "Soil analysis",
                            "Applicant and contractor contact information"
                    ),
                    List.of(
                            "Water Quality Branch application and receipt",
                            "Site-visit or inspection outcome",
                            "Installation, modification, repair, or abandonment permit"
                    ),
                    List.of(
                            "Choose the correct work type before opening the county route.",
                            "Prepare the plot plan, soil analysis, property identifiers, and applicant details.",
                            "Confirm the current fee and intake channel with the Water Quality Branch, then submit in your own name.",
                            "Do not begin repair work before the required site inspection and permit; save the permit and final inspection outcome."
                    )
            )),
            Map.entry("TN::putnam-county", profile(
                    "TN::putnam-county",
                    "state_search_with_fallback",
                    "TDEC search with Cookeville fallback",
                    "Search the Putnam County SSDS file, then use the Cookeville field office",
                    "TDEC's Cookeville Environmental Field Office explicitly serves Putnam County and coordinates public-records requests when a data viewer does not resolve the file. Search the statewide SSDS index first, then carry precise property clues into the office fallback.",
                    "Open the official TDEC SSDS record search",
                    "https://tdec.tn.gov/document-viewer/search/stp",
                    "Open the Cookeville Environmental Field Office",
                    "https://www.tn.gov/environment/contacts/field-offices/cookeville.html",
                    "A property-matched SSDS permit, layout, approval, repair file, or written TDEC response",
                    "The office confirms Putnam County coverage, public-records coordination, and 931-206-6329 for septic inquiries, but it does not publish a Putnam-specific intake form, fee, or turnaround. A blank viewer result is not a no-record determination.",
                    List.of(
                            "Property address",
                            "Parcel or tax-map clue",
                            "Current and prior owner names when available",
                            "Subdivision, lot, and approximate permit year when known"
                    ),
                    List.of(
                            "SSDS construction permit or approval",
                            "System layout, inspection letter, or certificate of completion",
                            "Repair record or written no-record response"
                    ),
                    List.of(
                            "Search the TDEC SSDS index with the strongest property clue and confirm the parcel match.",
                            "If the result is blocked or incomplete, use the Cookeville field office, which explicitly serves Putnam County.",
                            "Ask Division of Water Resources or the public-records coordinator for the site-specific file.",
                            "Keep the returned file, request reference, or written response with the property task."
                    )
            )),
            Map.entry("IN::monroe-county", profile(
                    "IN::monroe-county",
                    "portal_with_fallback",
                    "OpenGov septic permit workflow",
                    "Prepare and track a Monroe County septic permit in OpenGov",
                    "Monroe County publishes a five-step permit workflow: application, registered soil evaluation, county Minimum Specs, septic site plan, and permit issuance. The issued permit is downloadable in OpenGov, while the Wastewater office is the fallback for existing historical property files or workflow questions.",
                    "Open Monroe County wastewater guidance",
                    "https://www.in.gov/counties/monroe/Departments/health-department/wastewater/",
                    "Email the Wastewater office",
                    "mailto:wastewater@co.monroe.in.us",
                    "A downloadable OpenGov permit and its soil report, Minimum Specs, approved site plan, or a documented office response",
                    "The county publishes current fee examples but says fees can change. Work on an existing or new septic field generally requires a registered soil evaluation; confirm the current fee and application record number before payment. Historical records may be incomplete and do not prove current condition.",
                    List.of(
                            "Property address and parcel clue",
                            "Permit type: new, repair, renewal or modification, or commercial",
                            "OpenGov application record number",
                            "Registered soil evaluation when the field is new or modified",
                            "Septic site plan and applicant contact information"
                    ),
                    List.of(
                            "Soil evaluation and county Minimum Specs document",
                            "Approved septic site plan",
                            "Issued OpenGov septic permit or written Wastewater response"
                    ),
                    List.of(
                            "Choose the permit type and submit the OpenGov septic application to generate a record number.",
                            "Arrange the required registered soil evaluation and upload it to the application.",
                            "Use the county's Minimum Specs to prepare and upload the septic site plan.",
                            "Track corrections through OpenGov, download the issued permit, and keep the final file with the property task."
                    )
            )),
            Map.entry("TN::anderson-county", profile(
                    "TN::anderson-county",
                    "state_search_with_fallback",
                    "TDEC search with Knoxville fallback",
                    "Search the Anderson County SSDS file, then use the Knoxville field office",
                    "Anderson County is served by TDEC's Knoxville Environmental Field Office for Division of Water Resources work. Search the statewide SSDS index first; when the viewer is blocked, incomplete, or does not match the parcel, carry the exact property clues to the Knoxville office instead of treating the result as no record.",
                    "Open the official TDEC SSDS record search",
                    "https://tdec.tn.gov/document-viewer/search/stp",
                    "Open the TDEC field-office directory",
                    "https://www.tn.gov/environment/contacts/field-offices/knoxville.html",
                    "A property-matched SSDS permit, layout, approval, repair file, or written TDEC response",
                    "The official field-office directory assigns Anderson County to Knoxville and publishes 865-594-6035, but it does not publish an Anderson-specific records form, fee, or turnaround. A blank viewer result is not a no-record determination.",
                    List.of(
                            "Property address",
                            "Parcel or tax-map clue",
                            "Current and prior owner names when available",
                            "Subdivision, lot, and approximate permit year when known"
                    ),
                    List.of(
                            "SSDS construction permit or approval",
                            "System layout, inspection letter, or certificate of completion",
                            "Repair record or written no-record response"
                    ),
                    List.of(
                            "Search the TDEC SSDS index with the strongest property clue and confirm the parcel match.",
                            "If the result is blocked or incomplete, use the field-office directory and choose Knoxville for Anderson County.",
                            "Call 865-594-6035 and ask Division of Water Resources for the site-specific SSDS file.",
                            "Keep the returned file, request reference, or written response with the property task."
                    )
            )),
            Map.entry("NC::randolph-county", profile(
                    "NC::randolph-county",
                    "portal_with_fallback",
                    "County ePermits workflow",
                    "Choose the Randolph County septic permit job, then apply in ePermits",
                    "Randolph County separates new, repair, expansion or modification, and existing-system authorization work. New systems move from an Improvement Permit to a separate Construction Authorization and, after installation inspection, an Operation Permit. Existing structures and failing systems use different applications.",
                    "Open Randolph County ePermits",
                    "https://esuite.randolphcountync.gov/eSuite.Permits/WelcomePage.aspx",
                    "Open the county workflow and forms",
                    "https://www.randolphcountync.gov/397/On-Site-Water-Protection-Program",
                    "The correct application plus its IP, CA, Operation Permit, repair permit, expansion approval, or existing-system authorization",
                    "The county requires the parcel number and address, a signed application, site-ready document, and site plan for applicable work. Existing-system authorization may require uncovering the tank; when no permit is on file and no home is connected, the county form limits authorization to two residential bedrooms or four commercial employees.",
                    List.of(
                            "Parcel number and property address",
                            "Job type: new, repair, expansion or modification, or existing-system authorization",
                            "Signed application or owner authorization",
                            "Site-ready document and site plan",
                            "Proposed structures, wells, driveways, surface water, and property-line distances"
                    ),
                    List.of(
                            "Improvement Permit and Construction Authorization for new or expansion work",
                            "Operation Permit after the final installation inspection",
                            "Repair permit or existing-system authorization for the applicable job"
                    ),
                    List.of(
                            "Choose the actual property job before opening ePermits; do not use a new-site application for a repair or accessory structure.",
                            "Confirm the parcel and address, then prepare the signed application, site-ready document, and matching site plan.",
                            "Submit through ePermits and complete the county site-preparation and inspection steps.",
                            "Retain the issued IP, CA, Operation Permit, repair permit, or existing-system authorization as separate artifacts."
                    )
            )),
            Map.entry("AL::tuscaloosa-county", profile(
                    "AL::tuscaloosa-county",
                    "phone_assisted",
                    "County Environmental Office intake",
                    "Call Tuscaloosa County for the septic permit or record path",
                    "Tuscaloosa County Health Department publishes onsite sewage and septic-tank applications and permits, and directs every septic-tank request to its Environmental Office by phone. Prepare a property-specific request before calling so staff can distinguish a permit copy from new, repair, complaint, or pumper work.",
                    "Call Tuscaloosa County Health Department",
                    "tel:205-562-6900",
                    "Open Tuscaloosa environmental services",
                    "https://www.alabamapublichealth.gov/tuscaloosa/services.html",
                    "The responsible Environmental Office route plus a permit copy, application instructions, inspection outcome, or documented response",
                    "The county publishes the phone route but no public parcel search, record-copy form, fee, or turnaround for septic requests. Confirm the current intake and any fee before sending documents or payment; a historical permit does not prove present system condition.",
                    List.of(
                            "Property address and parcel or tax-map clue",
                            "Current and prior owner names when available",
                            "Request type: existing record, new permit, repair, complaint, or pumper matter",
                            "Approximate installation or permit year when known"
                    ),
                    List.of(
                            "Onsite sewage or septic-tank application and permit",
                            "Available approval or inspection record",
                            "Written or documented Environmental Office referral or no-record outcome"
                    ),
                    List.of(
                            "Prepare the address, parcel clue, owners, approximate year, and exact request type.",
                            "Call 205-562-6900 and ask for the Environmental Office as the county instructs.",
                            "Confirm whether staff can provide the existing permit file or requires a separate application or office visit.",
                            "Record the staff direction and retain the returned permit, inspection result, or no-record response."
                    )
            )),
            Map.entry("AL::calhoun-county", profile(
                    "AL::calhoun-county",
                    "phone_assisted",
                    "Direct Environmental Department intake",
                    "Route a Calhoun County septic permit or record request",
                    "Calhoun County's Environmental Department publishes a direct phone number and explicitly handles onsite sewage applications and permits, septic tanks, small and large flow development, and septic pumper inspections. Start with the property and job type instead of a generic health-department inquiry.",
                    "Call Calhoun Environmental Department",
                    "tel:256-237-4324",
                    "Open Calhoun environmental services",
                    "https://www.alabamapublichealth.gov/calhoun/environmental-services.html",
                    "The correct county intake plus an onsite sewage permit, application direction, inspection record, or documented office response",
                    "The official page publishes services and the direct Environmental Department number but no public record-search tool, copy form, fee, or turnaround. Confirm whether the request is for a historical file or a new paid service before proceeding.",
                    List.of(
                            "Property address and parcel or tax-map clue",
                            "Current or prior owner when available",
                            "Request type: historical permit, new or repair permit, small or large flow, or pumper matter",
                            "Approximate installation or permit year when known"
                    ),
                    List.of(
                            "Onsite sewage application or permit",
                            "Available approval, inspection, or system record",
                            "Documented Environmental Department referral or no-record outcome"
                    ),
                    List.of(
                            "Identify the property and whether the job is a historical file, permit, repair, development, or pumper request.",
                            "Call 256-237-4324, the direct Environmental Department number published by the county.",
                            "Confirm the current intake, documents, fees, and whether an existing record can be copied.",
                            "Retain the permit, inspection artifact, staff referral, or documented no-record outcome."
                    )
            )),
            Map.entry("SC::charleston-county", profile(
                    "SC::charleston-county",
                    "portal_with_fallback",
                    "TMS lookup with Coastal records route",
                    "Find the Charleston County TMS, then request the SCDES septic file",
                    "Charleston County's property-card search provides the parcel ID or TMS that SCDES asks for when locating a permit. SCDES assigns Charleston to its Coastal regional division and routes copies of permits and final inspections through Onsite Wastewater customer support.",
                    "Search Charleston property record cards",
                    "https://prcweb.charlestoncounty.org/",
                    "Call SCDES for permits and final inspections",
                    "tel:18557312504",
                    "A parcel-matched Permit to Construct, Approval to Operate or final inspection, site information, or documented no-file response",
                    "SCDES says permit copies are most likely for homes built within roughly the last 20 years. If the office cannot locate a copy or the home is older, a licensed septic contractor may be needed to locate the physical system; a missing file does not prove no system exists.",
                    List.of(
                            "Charleston County parcel ID or TMS",
                            "Physical address",
                            "Lot and block numbers when applicable",
                            "Original permit holder and subdivision when known",
                            "Installation or home-build date when known"
                    ),
                    List.of(
                            "SCDES Permit to Construct",
                            "Approval to Operate or final inspection record",
                            "Permit copy, regional referral, or documented no-file response"
                    ),
                    List.of(
                            "Use the county property-card search to confirm the parcel ID or TMS and address.",
                            "Add the lot, block, original permit holder, subdivision, and approximate date when available.",
                            "Call 1-855-731-2504 for the permit or final-inspection copy; Charleston is in the SCDES Coastal division at 843-953-0150.",
                            "Keep the returned permit and final inspection, or document the no-file result before arranging physical location work."
                    )
            )),
            Map.entry("SC::greenville-county", profile(
                    "SC::greenville-county",
                    "portal_with_fallback",
                    "Map-number lookup with Piedmont II route",
                    "Find the Greenville County map number, then request the SCDES septic file",
                    "Greenville County's official real-property search supports street, owner, map number, and subdivision clues. Use the matched property card to prepare the SCDES permit-copy request; SCDES assigns Greenville County to its Piedmont II regional division.",
                    "Search Greenville County real property",
                    "https://www.greenvillecounty.org/appsas400/RealProperty/",
                    "Call SCDES for permits and final inspections",
                    "tel:18557312504",
                    "A map-number-matched Permit to Construct, Approval to Operate or final inspection, or documented SCDES response",
                    "The county property search identifies the parcel but does not itself prove a septic permit exists. SCDES says older homes or files it cannot locate may require a licensed septic contractor to identify the system in the field.",
                    List.of(
                            "Greenville County map number or parcel clue",
                            "Physical address",
                            "Owner, lot, block, and subdivision when known",
                            "Original permit holder when known",
                            "Installation or home-build date when known"
                    ),
                    List.of(
                            "SCDES Permit to Construct",
                            "Approval to Operate or final inspection record",
                            "Permit copy, Piedmont II referral, or documented no-file response"
                    ),
                    List.of(
                            "Use Greenville County real-property search to match the address and map number.",
                            "Prepare the additional SCDES clues: lot, block, subdivision, original permit holder, and approximate date.",
                            "Call 1-855-731-2504 for copies; Greenville is in Piedmont II, whose published regional number is 864-638-4185.",
                            "Save the permit and final inspection, or retain the documented no-file response before field work."
                    )
            )),
            Map.entry("SC::anderson-county", profile(
                    "SC::anderson-county",
                    "portal_with_fallback",
                    "Assessor lookup with Piedmont II route",
                    "Confirm the Anderson County parcel, then request the SCDES septic file",
                    "Anderson County's Assessor and real-property resources provide the parcel anchor needed for the state septic search. SCDES assigns Anderson County to Piedmont II and routes copies of permits and final inspections through its Onsite Wastewater customer-support line.",
                    "Open Anderson County property resources",
                    "https://www.andersoncountysc.org/departments-a-z/assessor/",
                    "Call SCDES for permits and final inspections",
                    "tel:18557312504",
                    "A parcel-matched Permit to Construct, Approval to Operate or final inspection, or documented SCDES response",
                    "The assessor resource establishes the property identity, not septic approval or current condition. SCDES may have no copy for an older property, in which case a licensed septic contractor may be required to locate the system physically.",
                    List.of(
                            "Anderson County parcel or tax-map number",
                            "Physical address",
                            "Owner, lot, block, and subdivision when known",
                            "Original permit holder when known",
                            "Installation or home-build date when known"
                    ),
                    List.of(
                            "SCDES Permit to Construct",
                            "Approval to Operate or final inspection record",
                            "Permit copy, Piedmont II referral, or documented no-file response"
                    ),
                    List.of(
                            "Use the Anderson County assessor resource to confirm the parcel and address.",
                            "Prepare the lot, block, subdivision, original permit holder, and approximate date when available.",
                            "Call 1-855-731-2504 for copies; Anderson is in Piedmont II at 864-638-4185.",
                            "Retain the returned permit and final inspection or the documented no-file result."
                    )
            )),
            Map.entry("SC::spartanburg-county", profile(
                    "SC::spartanburg-county",
                    "portal_with_fallback",
                    "GIS parcel lookup with Piedmont I route",
                    "Confirm the Spartanburg County parcel, then request the SCDES septic file",
                    "Spartanburg County's official GIS resources maintain digital parcel and road data that can anchor the state record request. SCDES assigns Spartanburg County to Piedmont I and uses its customer-support route for copies of permits and final inspections.",
                    "Open Spartanburg County GIS",
                    "https://www.spartanburgcounty.gov/185/Geographic-Information-Systems",
                    "Call SCDES for permits and final inspections",
                    "tel:18557312504",
                    "A parcel-matched Permit to Construct, Approval to Operate or final inspection, or documented SCDES response",
                    "County GIS is a parcel-identification aid, not a septic approval or survey. When SCDES cannot locate an older permit copy, its homeowner guidance directs users to a licensed septic contractor for physical system location.",
                    List.of(
                            "Spartanburg County parcel or map clue",
                            "Physical address",
                            "Owner, lot, block, and subdivision when known",
                            "Original permit holder when known",
                            "Installation or home-build date when known"
                    ),
                    List.of(
                            "SCDES Permit to Construct",
                            "Approval to Operate or final inspection record",
                            "Permit copy, Piedmont I referral, or documented no-file response"
                    ),
                    List.of(
                            "Use Spartanburg County GIS to confirm the parcel and physical address.",
                            "Prepare the other SCDES clues: lot, block, subdivision, original permit holder, and approximate date.",
                            "Call 1-855-731-2504 for copies; Spartanburg is in Piedmont I at 803-285-7461.",
                            "Keep the returned permit and final inspection or document the no-file result before field work."
                    )
            )),
            Map.entry("NC::buncombe-county", profile(
                    "NC::buncombe-county",
                    "portal_with_fallback",
                    "Accela search with Environmental Health fallback",
                    "Search Buncombe County well and septic records, then choose the correct follow-up",
                    "Buncombe County links its Accela search from Environmental Health and separates a historical record search from an Existing System Request, inspection, new permit, or repair permit. Start with the parcel PIN and search before choosing the next county job.",
                    "Search Buncombe well and septic records",
                    "https://aca-prod.accela.com/buncombeconc/default.aspx",
                    "Open Buncombe Environmental Health",
                    "https://www.buncombenc.gov/456/Environmental-Health",
                    "A parcel-matched permit, approval, layout, inspection record, request reference, or written no-record response",
                    "Accela may block automated access or return no match for an incomplete identity. An empty result is not proof that no county file exists, and an Existing System Request or Inspection is a separate job from a new or repair permit. Buncombe publishes 828-250-5016 for help.",
                    List.of(
                            "Property address",
                            "Buncombe County parcel PIN",
                            "Current or prior owner name",
                            "Permit or case number when known",
                            "Subdivision, lot, and approximate construction year"
                    ),
                    List.of(
                            "Septic permit, Improvement Permit, or Authorization to Construct",
                            "Operation approval, layout, or site sketch",
                            "Existing System Request or Inspection result",
                            "Repair record, request reference, or written no-record response"
                    ),
                    List.of(
                            "Confirm the address and parcel PIN before opening Accela.",
                            "Search by the strongest available property clue and inspect the matching case attachments.",
                            "If the file is missing or the task concerns reuse, an addition, or a repair, open Environmental Health and choose the corresponding existing-system, inspection, new, or repair route.",
                            "Save the matched permit and approval documents, or retain the request reference or written office response."
                    )
            )),
            Map.entry("NC::wake-county", profile(
                    "NC::wake-county",
                    "portal_with_fallback",
                    "Permit Search with iMAPS parcel workflow",
                    "Match the Wake County parcel in iMAPS, then open the septic permit attachments",
                    "Wake County's guide starts with an iMAPS search by address, PIN, owner, or Real Estate ID. After selecting the parcel, use the Septic result to open Permit Search and download the scanned permit attachments.",
                    "Open Wake County Permit Search",
                    "https://permitsearch.wake.gov/",
                    "Open the official iMAPS search guide",
                    "https://s3.us-west-1.amazonaws.com/wakegov.com.if-us-west-1/s3fs-public/documents/2023-07/iMAPS%20Permit%20Search.pdf",
                    "A parcel-matched scanned septic permit and attachments, or a documented Wake County assistance outcome",
                    "If iMAPS does not show a Septic box, the permit may be unscanned or the property may not use onsite wastewater; that state is not an official no-record result. Wake County directs users who cannot find a permit to 919-856-7400.",
                    List.of(
                            "Property address",
                            "Wake County PIN",
                            "Owner name",
                            "Real Estate ID",
                            "Permit number or approximate permit year when known"
                    ),
                    List.of(
                            "Scanned septic permit PDF",
                            "Permit Search attachments and layout",
                            "Improvement, construction, operation, repair, or abandonment record",
                            "Written or phone-assisted outcome for an unscanned file"
                    ),
                    List.of(
                            "Use iMAPS with the address, PIN, owner, or Real Estate ID to select the exact parcel.",
                            "Open the parcel's Septic result and follow it into Permit Search.",
                            "Download the permit and every relevant attachment; do not stop at the result row.",
                            "If the Septic result or scan is missing, call 919-856-7400 and document the county's answer before treating the search as complete."
                    )
            )),
            Map.entry("NC::union-county", profile(
                    "NC::union-county",
                    "official_request",
                    "Existing permit request with inspection branch",
                    "Request the Union County septic file before planning work around the existing system",
                    "Union County provides a dedicated existing septic and well permit request and separately requires an existing-system inspection before construction such as additions, garages, decks, pools, or irrigation on a septic-served parcel.",
                    "Request an existing septic or well permit",
                    "https://lfportal.unioncountync.gov/Forms/WellSepticPermitRequest",
                    "Open Union County septic-system guidance",
                    "https://www.unioncountync.gov/government/departments-a-e/environmental-health/septic-systems",
                    "The existing permit file plus any required inspection, repair, compliance, or O&M record tied to the parcel",
                    "The request form may block automated access, and a historical permit copy does not clear new construction or prove current condition. Union County publishes 704-283-3553 for Environmental Health; inspection, repair, and new-work decisions remain separate county actions.",
                    List.of(
                            "Property address and parcel number",
                            "Current and prior owner names when known",
                            "Subdivision and lot",
                            "Requested permit or record type",
                            "Project type, including any addition, deck, pool, garage, or irrigation work",
                            "Requester contact information"
                    ),
                    List.of(
                            "Existing septic and well permit file",
                            "Existing-system or compliance inspection result",
                            "Repair permit or construction authorization",
                            "Operation and maintenance record when applicable"
                    ),
                    List.of(
                            "Submit the existing permit request with the exact parcel identity and requested record scope.",
                            "Save the confirmation or returned file and compare its approved layout with the current property.",
                            "If construction or a site change is planned, use the county guidance to start the required existing-system inspection rather than treating the old permit as clearance.",
                            "Keep the permit, inspection, repair, and O&M artifacts together before making a build or pricing decision."
                    )
            )),
            Map.entry("NC::pitt-county", profile(
                    "NC::pitt-county",
                    "portal_with_fallback",
                    "EnerGov permit search with Environmental Health fallback",
                    "Search Pitt County permits, then verify the Authorization to Construct and repair area",
                    "Pitt County links its EnerGov self-service portal from the On-Site Wastewater program. Use the parcel and applicant details to find the file, then confirm that the permit and site plan address both the primary drainfield and repair area.",
                    "Search Pitt County permits",
                    "https://pittcountync-energovweb.tylerhost.net/apps/selfservice#/home",
                    "Open Pitt County On-Site Wastewater",
                    "https://www.pittcountync.gov/378/On-Site-Wastewater-Septic-Systems",
                    "A parcel-matched permit or Authorization to Construct with its site plan, approval or inspection record, or documented office response",
                    "EnerGov may require interactive browser access and an online result alone does not prove construction approval or current capacity. Pitt County publishes 252-902-3200; the county still controls site evaluation, primary and repair-area acceptance, and final approval.",
                    List.of(
                            "Property address and tax parcel number",
                            "Property owner and mailing address",
                            "Subdivision and lot",
                            "Structure type, bedroom or occupant count, and projected wastewater flow",
                            "Water-supply type",
                            "Site plan or plat and directions to the property"
                    ),
                    List.of(
                            "Improvement Permit or Authorization to Construct",
                            "Site plan showing the primary drainfield and repair area",
                            "Operation permit, final inspection, or approval record",
                            "Repair file, portal reference, or written Environmental Health response"
                    ),
                    List.of(
                            "Search EnerGov with the parcel, address, owner, or known permit clue.",
                            "Open the matched record and retain its permit number and available documents.",
                            "Compare the permit and site plan with the proposed structure, wastewater flow, primary field, and repair area.",
                            "If the portal is blocked or incomplete, contact Environmental Health at 252-902-3200 and retain the resulting file or written response."
                    )
            )),
            Map.entry("CA::ventura-county", profile(
                    "CA::ventura-county",
                    "official_request",
                    "Environmental Health records request with permit cross-check",
                    "Request Ventura County's historical septic file, then check current permit activity separately",
                    "Ventura County publishes an Environmental Health records-request form with property, date-range, and Septic/OWTS/ISDS fields. Use that form for the historical file; Citizen Access is a separate cross-check for available current permit activity and an empty portal result is not a no-record response.",
                    "Email Ventura Environmental Health records",
                    "mailto:EHDRecordSearchRequest@ventura.org?subject=Ventura%20County%20Septic%20Records%20Request",
                    "Open Ventura County Citizen Access",
                    "https://vcca.venturacounty.gov/citizenaccess/Default.aspx",
                    "A property-matched Septic/OWTS/ISDS file, written no-record or referral response, or a county request reference that remains pending until the file arrives",
                    "The form and records coordinator are the historical-file path; Citizen Access is only a separate current-permit cross-check. If the county document server is unavailable, request the current form at EHDRecordSearchRequest@ventura.org. A missing permit may still require county-directed field verification, and Ventura County controls response timing.",
                    List.of(
                            "Property or site name",
                            "Exact property address",
                            "Assessor's Parcel Number when available",
                            "Relevant record date range",
                            "Septic/OWTS/ISDS record type",
                            "Requester name, phone, and email"
                    ),
                    List.of(
                            "Returned Environmental Health septic or OWTS file",
                            "Permit, layout, approval, repair, or inspection records tied to the property",
                            "Written no-record, referral, or follow-up response",
                            "Any separate current permit activity found in Citizen Access"
                    ),
                    List.of(
                            "Complete the official request with the exact address, APN when available, date range, and Septic/OWTS/ISDS record type.",
                            "Submit it through the current instructions printed on the county form; if the document server does not open, email EHDRecordSearchRequest@ventura.org for the current form and retain the sent message.",
                            "Check Citizen Access separately for available current permit activity without treating an empty result as a historical no-record response.",
                            "Compare the returned file with the current property and follow any county direction for field verification when no permit or system location is available."
                    )
            )),
            Map.entry("NC::pender-county", profile(
                    "NC::pender-county",
                    "official_request",
                    "Septic permit information request",
                    "Send Pender County the exact fields needed to locate the septic permit file",
                    "Pender County publishes a dedicated Septic Permit Information Request. Prepare the address, subdivision and lot, parcel ID, permit or owner name, and year built so Environmental Health can return the permit status and type without an address-only guess.",
                    "Open the Pender septic information request",
                    "https://www.pendercountync.gov/DocumentCenter/View/164/Septic-Permit-Information-Request-PDF",
                    "Open Pender County On-Site Wastewater",
                    "https://pendercountync.gov/230/On-Site-Wastewater-Program-Wells",
                    "A returned septic permit record with its status and permit type, or a documented Environmental Health response",
                    "This is a manual county request, and the published form does not promise a fee or turnaround. A returned current-through or expired status and permit type describe the file; they do not prove present system condition, capacity, or an unobstructed repair area. Pender publishes 910-270-5000 in Hampstead and 910-259-1233 in Burgaw.",
                    List.of(
                            "Request date",
                            "Property address",
                            "Subdivision and lot number",
                            "Parcel Identification Number",
                            "Name on the permit or current owner",
                            "Year built",
                            "Requester name, email, and phone"
                    ),
                    List.of(
                            "Septic permit information returned by the county",
                            "Current-through or expired file status",
                            "Permit type and available layout or location record",
                            "Written no-record, referral, or follow-up response"
                    ),
                    List.of(
                            "Complete every known property-identity field on the county request instead of relying on the address alone.",
                            "Send the form through the current Environmental Health channel shown by the county.",
                            "Save the returned status, permit type, attached file, and any follow-up reference.",
                            "Use the program page or the appropriate office number if the returned file is missing, expired, or does not answer the repair-area or planned-work question."
                    )
            ))
    );

    private CountyAccessProfileCatalog() {
    }

    public static CountyAccessProfileView find(String countyKey) {
        return PROFILES.get(countyKey);
    }

    public static int countySpecificProfileCount() {
        return PROFILES.size();
    }

    static List<CountyAccessProfileView> all() {
        return List.copyOf(PROFILES.values());
    }

    public static CountyAccessProfileView findOrBaseline(CountyRecordsPage countyPage) {
        CountyAccessProfileView verifiedProfile = find(countyPage.key());
        if (verifiedProfile != null) {
            return verifiedProfile;
        }

        String secondaryLabel = "";
        String secondaryUrl = "";
        if (countyPage.officeUrl() != null
                && !countyPage.officeUrl().isBlank()
                && !countyPage.officeUrl().equals(countyPage.recordsUrl())) {
            secondaryLabel = "Open the responsible office page";
            secondaryUrl = countyPage.officeUrl();
        }

        return baselineProfile(
                countyPage.key(),
                "official_route",
                "Official starting point only",
                "Prepare the property clues, then use the official " + countyPage.countyName() + " route",
                "SepticPath has not verified a county-specific intake form for this route. Prepare reusable property clues here, follow only the fields published by the official source, and return with the document or response.",
                countyPage.recordsLabel(),
                countyPage.recordsUrl(),
                secondaryLabel,
                secondaryUrl,
                "A property-matched file, written referral, or documented no-record response. A request reference means the task is still pending.",
                "The official source may require different identifiers, CAPTCHA, login, signature, payment, or direct contact. SepticPath does not replace or submit that government step.",
                List.of(
                        "Property address",
                        "Parcel, PIN, tax ID, or legal description when available",
                        "Owner, permit number, subdivision, or other clue only when the official source asks"
                ),
                List.of(
                        "The permit, approval, layout, or record returned by the official source",
                        "A request or confirmation number when the source provides one",
                        "A written no-record response, referral, or access-block note"
                ),
                List.of(
                        "Prepare the address and any known property identifier before leaving SepticPath.",
                        "Open the official route and complete only the fields and documents it currently publishes.",
                        "Handle CAPTCHA, login, signature, payment consent, and final submission on the official site.",
                        "Return here and record whether you received a document, partial file, no result, access block, or request reference."
                )
        );
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
                true,
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

    private static CountyAccessProfileView baselineProfile(
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
                false,
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
