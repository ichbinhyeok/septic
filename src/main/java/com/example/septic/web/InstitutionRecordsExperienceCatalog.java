package com.example.septic.web;

import java.util.List;
import java.util.Optional;

public final class InstitutionRecordsExperienceCatalog {
    private InstitutionRecordsExperienceCatalog() {}

    public static Optional<InstitutionRecordsExperience> find(String stateCode) {
        return Optional.ofNullable(switch (stateCode == null ? "" : stateCode) {
            case "NH" -> new InstitutionRecordsExperience(
                    "NH", "NHDES", "Subsurface Systems Bureau",
                    "Find the NHDES record—even when OneStop is empty.",
                    "Start with the property. We turn its address, owner history, and map-and-lot clues into the right OneStop, archive, or town-office route.",
                    "A usable approval, plan, or a documented next step—not another list of government links.",
                    "https://www4.des.state.nh.us/SSBOneStop/", "Open NHDES OneStop",
                    "https://onlineforms.nh.gov/?formtag=NHDES-W-05-010", "Open the NHDES archive request",
                    "Town", "Concord", "Tax map and lot", "Map 12, Lot 34",
                    "Owner at installation, if known", "Approximate installation year",
                    "NHDES Subsurface Systems Bureau", "Septic approval and plan records for the property",
                    "Search OneStop first. Most post-1986 approvals appear there; older approvals and many pre-2015 plans may require the archive request or the town file.",
                    "A blank OneStop result does not establish that no record exists. Older approvals, pre-digital plans, owner-name changes, and town copies create separate search paths.",
                    "Two approvals can matter", "A New Hampshire file may contain both Construction Approval and Operational Approval. We separate them before summarizing what the record actually establishes.",
                    "/images/studio/tdec-desk-v1.webp",
                    List.of("Search OneStop with the strongest property identifiers.", "If the plan is not attached, move to the archive request and town file.", "Read approval type, design flow, bedrooms, tank, and disposal area without treating an old plan as a current inspection."),
                    List.of("Construction Approval", "Operational Approval", "Approved septic plan", "Design flow and bedroom basis", "Repair or replacement approvals"),
                    routeRules("NH")
            );
            case "VA" -> new InstitutionRecordsExperience(
                    "VA", "VDH", "Onsite Water and Wastewater Services",
                    "Find the right Virginia health district—and the actual septic file.",
                    "The address determines whether the route runs through VDH NextRequest, a local health district, or an independently administered county.",
                    "The correct custodian, a complete request, and a plain-English reading of the returned file.",
                    "https://www.vdh.virginia.gov/environmental-health/environmental-health-services/onsite-sewage-water-services/", "Open VDH records resources",
                    "https://vdh.nextrequest.com/", "Open VDH NextRequest",
                    "County or city", "Fairfax County", "Parcel or GPIN", "Parcel, GPIN, or tax-map number",
                    "Current or prior owner", "Approximate permit or construction year",
                    "the responsible Virginia health district", "Onsite sewage and private well records for the property",
                    "Resolve the county or independent local program before submitting. Ask for the permit packet, site sketch, inspection or final record, repair history, and any operation permit.",
                    "Virginia custody is not uniform. Arlington, Loudoun, Fairfax, and other local programs can require a different request route from the central VDH path.",
                    "The file changes the question", "We distinguish a conventional system from an AOSS, identify operation-permit or maintenance obligations, and keep historical approval separate from present condition.",
                    "/images/studio/property-hero-v1.webp",
                    List.of("Resolve the health district or independent local program.", "Search or request the full environmental-health file, not only a permit number.", "Separate design, final approval, operating obligations, and current-condition questions."),
                    List.of("Onsite sewage permit and application", "Site sketch or approved design", "Inspection and final approval", "Repair and alteration history", "AOSS operation or maintenance records"),
                    routeRules("VA")
            );
            case "ME" -> new InstitutionRecordsExperience(
                    "ME", "Maine CDC", "Subsurface Wastewater Program / HHE-200",
                    "Find the HHE-200—or trace the town file when the search is empty.",
                    "We search by town, era, street, owner, and map-and-lot clues, then move to the town or Local Plumbing Inspector when the state database is incomplete.",
                    "The actual septic design or a town-ready request that explains exactly what is still missing.",
                    "https://www.maine.gov/dhhs/mecdc/services/business-services/hydrology-and-wastewater/a-b-c-d-resources", "Open Maine HHE-200 search",
                    "https://www.maine.gov/dhhs/mecdc/services/business-services/hydrology-and-wastewater/a-b-c-d-resources", "Review the town-office fallback",
                    "Town or plantation", "Vassalboro", "Map and lot", "Map 8, Lot 21",
                    "Current or former owner", "Approximate installation or replacement year",
                    "the municipal office or Local Plumbing Inspector", "HHE-200 and HHE-200A records for the property",
                    "Search a broad year range. Pre-2019 files may be indexed inside filenames; newer files can be searched by structured street and owner fields.",
                    "The state database contains only records supplied by participating towns. Maine says the town normally keeps the complete property file, so an empty database result is only the end of the first search.",
                    "HHE-200 and HHE-200A are different", "We identify whether the returned file is the full subsurface wastewater design or only a replacement-tank application before answering the property question.",
                    "/images/studio/research-desk-v1.webp",
                    List.of("Search HHE-200 and HHE-200A across a useful year range.", "Retry older files through filename, prior owner, and map-and-lot clues.", "If the state index is empty, prepare the town or Local Plumbing Inspector request."),
                    List.of("HHE-200 design and permit application", "HHE-200A replacement tank application", "Site evaluator drawing", "Variance or inspection notes", "Municipal plumbing-permit history"),
                    routeRules("ME")
            );
            case "AL" -> new InstitutionRecordsExperience(
                    "AL", "ADPH", "Soil and Onsite Sewage Program",
                    "Get the county septic file—and understand the Approval for Use.",
                    "Alabama records live with the county health department. We identify the office, ask for the useful documents, and read the installation diagram that comes back.",
                    "A permit packet translated into tank, field, capacity, approval, and remaining-condition questions.",
                    "https://www.alabamapublichealth.gov/onsite/septic-tanks.html", "Open ADPH record guidance",
                    "https://www.alabamapublichealth.gov/about/health-departments.html", "Find the county health department",
                    "County", "Morgan County", "Parcel or PPIN", "Parcel, PPIN, subdivision, or lot",
                    "Owner, applicant, or original developer", "Approximate installation or repair year",
                    "the county health department Environmental Office", "Approval for Use and onsite sewage records for the property",
                    "Ask for both the Permit to Install trail and the completed Approval for Use, including the installation diagram and any repair or modification records.",
                    "Alabama does not provide one complete statewide homeowner lookup. The county file is the working record, and a non-owner may need the formal records-request route.",
                    "A real returned file can answer more", "An Approval for Use may show the installed tank, trench layout, design basis, and final approval. We extract those fields without claiming it proves current condition.",
                    "/images/studio/calculator-landscape-v2.webp",
                    List.of("Identify the county Environmental Office that owns the file.", "Request the Permit to Install, Approval for Use, diagram, and repair history together.", "Translate the returned drawing and approval fields into a concise property finding."),
                    List.of("Permit to Install", "Approval for Use", "Installed-system diagram", "Soil and site evaluation", "Repair or modification records"),
                    routeRules("AL")
            );
            case "AR" -> new InstitutionRecordsExperience(
                    "AR", "Arkansas ADH", "Onsite Wastewater Program",
                    "Find the county health unit holding the septic permit.",
                    "The public-facing permit portal is not the homeowner archive. We prepare the county Environmental Specialist search using the clues ADH says matter.",
                    "A county-ready permit-copy request and a structured reading of the EHP-19 file.",
                    "https://healthy.arkansas.gov/programs-services/public-health-safety/onsite-wastewater/onsite-wastewater-faqs/", "Open the ADH permit-copy guidance",
                    "https://healthy.arkansas.gov/health-units/", "Find the local health unit",
                    "County", "Pulaski County", "Subdivision and lot", "Subdivision, lot, or parcel",
                    "Original owner or developer", "Approximate year the home was built",
                    "the county health unit Onsite Environmental Specialist", "Copy of the onsite wastewater permit for the property",
                    "Carry the build year, subdivision, lot number, and original owner or developer. ADH specifically identifies these as useful permit-copy clues.",
                    "The visible online portal is used by licensed Designated Representatives for permit work; it is not a general existing-record search for homeowners.",
                    "The EHP-19 tells a sequence", "We separate design and soil data, installation inspection, and the Permit for Operation instead of reducing the file to a single permit date.",
                    "/images/studio/county-property-v1.webp",
                    List.of("Identify the county health unit and Onsite Environmental Specialist.", "Send build-era, subdivision, lot, and original-owner clues with the request.", "Read EHP-19 design, installation inspection, and Permit for Operation as separate stages."),
                    List.of("EHP-19 permit application", "Soil and system design fields", "Installation inspection", "Permit for Operation", "Repair, exemption, or utilization records"),
                    routeRules("AR")
            );
            case "OR" -> new InstitutionRecordsExperience(
                    "OR", "Oregon DEQ", "Onsite Wastewater Management",
                    "Turn an Oregon address into the right septic-record search.",
                    "Oregon searches often start with a tax lot, not a street address. We resolve the identifier and determine whether DEQ or a county program owns the file.",
                    "The correct custodian, the correct parcel syntax, and an explanation of the permit or site evaluation returned.",
                    "https://www.oregon.gov/deq/residential/pages/onsite-records.aspx", "Open Oregon DEQ record instructions",
                    "https://ormswd2.synergydcs.com/ORMSCMSearchDEQ/Search/SearchMain.aspx", "Open Oregon Records Management Solution",
                    "County", "Jackson County", "Map and tax lot", "Township-range-section and tax lot",
                    "Current or permit-era owner", "Approximate permit or site-evaluation year",
                    "Oregon DEQ or the county onsite program", "Onsite septic records for the property tax lot",
                    "Find the map-and-tax-lot identifier first. Where DEQ owns the record, ORMS commonly uses the parcel value followed by an asterisk; elsewhere the local county agent is the custodian.",
                    "DEQ directly maintains records for only certain counties and time periods. Other counties operate as local agents, and several custodianship boundaries changed in 2025.",
                    "A record is more than a permit", "We separate the site evaluation, construction permit, authorization notice, satisfactory completion, and replacement-area implications.",
                    "/images/studio/oak-pasture-v1.webp",
                    List.of("Resolve the map-and-tax-lot number from the address.", "Choose ORMS or the correct county onsite program based on place and record era.", "Read site evaluation, permit, authorization, and completion records as distinct evidence."),
                    List.of("Site evaluation", "Construction or installation permit", "Authorization notice", "Certificate of satisfactory completion", "Repair, alteration, and maintenance records"),
                    routeRules("OR")
            );
            case "VT" -> new InstitutionRecordsExperience(
                    "VT", "Vermont DEC", "Wastewater System and Potable Water Supply Program",
                    "Search the permit—and understand why it may not appear.",
                    "We broaden the state search with town, street, owner, SPAN, and permit clues, then distinguish an online miss from an older, exempt, or locally held file.",
                    "A defensible search trail and the right regional-office or town-file next step.",
                    "https://anrweb.vt.gov/DEC/WWDocs/Default.aspx", "Open Vermont wastewater permit search",
                    "https://dec.vermont.gov/water/wastewater-systems-and-potable-water-supply-program/wastewater-systems-and-potable-water", "Open DEC missing-record guidance",
                    "Town", "Hartford", "SPAN or permit number", "SPAN, project ID, or permit number",
                    "Landowner or applicant", "Approximate permit or construction year",
                    "the appropriate Vermont DEC Regional Office", "Wastewater and potable water permit documents for the property",
                    "Start broad: town plus part of the road or owner name. Older records may use a former road name, omit the street, or exist only in a town file or on microfilm.",
                    "No search result can mean an older or unnumbered record, a pre-permit-era lot, an exemption, a delegated municipality, or simply different indexing. Each has a different next step.",
                    "Missing is not one conclusion", "We separate no online match, no permit required at the time, exemption, delegated-town custody, and a file that needs regional-office or microfilm research.",
                    "/images/studio/guides-lane-v1.webp",
                    List.of("Search by town with broader street, owner, SPAN, and permit variants.", "Check the town file or delegated municipality when the statewide result is empty.", "Use the regional office for older, unnumbered, or microfilm records and explain what the returned permit proves."),
                    List.of("Wastewater and potable water permit", "Approved system plan", "Project determination or exemption", "Regional-office file or microfilm copy", "Amendment and compliance documents"),
                    routeRules("VT")
            );
            default -> null;
        });
    }

    private static List<InstitutionRouteRule> routeRules(String stateCode) {
        return switch (stateCode) {
            case "NH" -> List.of(
                    datedRoute("nh-archive-historical", List.of(), "", null, 1986, "NHDES Subsurface Systems Bureau archive",
                            "Approvals from 1967 through 1986 and many older plans require the archive path rather than a OneStop-only search.",
                            "https://onlineforms.nh.gov/?formtag=NHDES-W-05-010", "Request the NHDES archive",
                            "https://www4.des.state.nh.us/SSBOneStop/", "Check NHDES OneStop",
                            "NHDES Subsurface Systems Bureau", "request", "Send the historical archive request with every match key.", "Archive request"),
                    route("nh-default", List.of(), "", "NHDES Subsurface Systems Bureau",
                            "OneStop is the first search. If an approval or plan is absent, the archive request and town file are the documented recovery path.",
                            "https://www4.des.state.nh.us/SSBOneStop/", "Search NHDES OneStop",
                            "https://onlineforms.nh.gov/?formtag=NHDES-W-05-010", "Request the NHDES archive",
                            "NHDES Subsurface Systems Bureau", "request", "Send the archive request with every match key.", "Archive request")
            );
            case "VA" -> List.of(
                    route("va-arlington", List.of("arlington"), "", "Arlington County Environmental Health / FOIA",
                            "Arlington administers its onsite records locally, so this property should not begin in the central VDH request queue.",
                            "https://www.arlingtonva.us/Government/Programs/FOIA/Requests", "Open Arlington FOIA requests",
                            "https://www.arlingtonva.us/Government/Programs/Health/Environmental-Health/Septic", "Review Arlington septic records guidance",
                            "Arlington County Environmental Health / FOIA", "request", "Use a county-specific records request.", "Arlington request draft"),
                    route("va-loudoun", List.of("loudoun"), "", "Loudoun County Health Department",
                            "Loudoun is locally administered. Use the county FOIA route for records and Environmental Health for service-specific follow-up.",
                            "https://www.loudoun.gov/923/Virginia-Freedom-of-Information-Act-FOIA", "Open Loudoun FOIA",
                            "https://www.loudoun.gov/1514/Online-Service-Requests-Concerns", "Open Environmental Health requests",
                            "Loudoun County Health Department", "request", "Use the local records request.", "Loudoun request draft"),
                    route("va-fairfax", List.of("fairfax"), "", "Fairfax County Health Department Environmental Health",
                            "Fairfax administers its onsite program locally. Start with the county record route rather than central VDH NextRequest.",
                            "https://www.fairfaxcounty.gov/publicaffairs/foia", "Open Fairfax County FOIA",
                            "https://www.fairfaxcounty.gov/health/sewage-and-water", "Review Fairfax sewage and water guidance",
                            "Fairfax County Health Department Environmental Health", "request", "Use the Fairfax County request route.", "Fairfax request draft"),
                    route("va-vdh-default", List.of(), "", "the responsible VDH health district",
                            "For most Virginia localities, VDH NextRequest or the responsible local health district is the official records route.",
                            "https://vdh.nextrequest.com/", "Open VDH NextRequest",
                            "https://www.vdh.virginia.gov/environmental-health/environmental-health-services/onsite-sewage-water-services/", "Find the responsible health district",
                            "the responsible VDH health district", "request", "Submit one complete VDH request.", "VDH request draft")
            );
            case "ME" -> List.of(
                    datedRoute("me-pre-1974", List.of(), "", null, 1973, "the municipal office or Local Plumbing Inspector",
                            "Maine says subsurface wastewater records before July 1974 generally are not available. The town file is the only responsible first check for surviving local material.",
                            "https://www.maine.gov/local/index.html", "Find the municipal office",
                            "https://www.maine.gov/dhhs/mecdc/services/business-services/hydrology-and-wastewater/a-b-c-d-resources", "Check the HHE-200 index",
                            "the municipal office or Local Plumbing Inspector", "request", "Ask whether any pre-1974 local file survives.", "Town request draft"),
                    route("me-town-default", List.of(), "", "the municipal office or Local Plumbing Inspector",
                            "The state HHE-200 search is the fastest first check, but Maine says the town normally holds the complete property file.",
                            "https://www.maine.gov/dhhs/mecdc/services/business-services/hydrology-and-wastewater/a-b-c-d-resources", "Search the HHE-200 index",
                            "https://www.maine.gov/local/index.html", "Find the municipal office",
                            "the municipal office or Local Plumbing Inspector", "request", "Ask the town for the complete file.", "Town request draft")
            );
            case "AL" -> List.of(
                    route("al-owner-agent", List.of(), "owner_agent", "the county health department Environmental Office",
                            "ADPH directs owners and authorized agents to the county health department that holds the working onsite file.",
                            "https://www.alabamapublichealth.gov/about/health-departments.html", "Find the county health department",
                            "https://www.alabamapublichealth.gov/onsite/septic-tanks.html", "Review ADPH onsite guidance",
                            "the county health department Environmental Office", "request", "Ask the county Environmental Office.", "County request draft"),
                    route("al-non-owner", List.of(), "non_owner", "ADPH Records Request",
                            "ADPH directs non-owners to the formal public-records route; the county directory remains useful for identifying the office that created the file.",
                            "https://adph.nextrequest.com/", "Open ADPH Records Request",
                            "https://www.alabamapublichealth.gov/about/health-departments.html", "Find the county health department",
                            "ADPH Records Request", "request", "Use the formal public-records request.", "ADPH request draft")
            );
            case "AR" -> List.of(
                    route("ar-county-default", List.of(), "", "the county health unit Onsite Environmental Specialist",
                            "ADH instructs record seekers to call the county health unit and ask for the Onsite Environmental Specialist. The public permit portal is not the homeowner archive.",
                            "https://healthy.arkansas.gov/health-units/", "Find the county health unit",
                            "https://healthy.arkansas.gov/programs-services/public-health-safety/onsite-wastewater/onsite-wastewater-faqs/", "Review ADH permit-copy guidance",
                            "the county health unit Onsite Environmental Specialist", "call", "Use a complete call script.", "Call script")
            );
            case "OR" -> List.of(
                    route("or-deq-current", List.of("curry", "jackson", "josephine"), "", "Oregon DEQ onsite program",
                            "DEQ maintains current onsite records for Curry, Jackson, and Josephine counties. Resolve the map-and-tax-lot value before searching ORMS.",
                            "https://ormswd2.synergydcs.com/ORMSCMSearchDEQ/Search/SearchMain.aspx", "Search Oregon ORMS",
                            "https://www.oregon.gov/deq/residential/pages/onsite-records.aspx", "Read DEQ search instructions",
                            "Oregon DEQ onsite program", "search", "Keep a fallback request ready if ORMS is incomplete.", "DEQ follow-up draft"),
                    datedRoute("or-deq-retained", List.of("baker", "union", "wallowa"), "", null, 2024, "Oregon DEQ onsite program",
                            "DEQ retains records created before June 1, 2025 for Baker, Union, and Wallowa counties. ORMS is the correct first search for this record year.",
                            "https://ormswd2.synergydcs.com/ORMSCMSearchDEQ/Search/SearchMain.aspx", "Search retained DEQ records",
                            "https://www.oregon.gov/deq/residential/pages/onsite-records.aspx", "Read the official transition rule",
                            "Oregon DEQ onsite program", "search", "Keep a DEQ follow-up ready if ORMS is incomplete.", "DEQ follow-up draft"),
                    datedRoute("or-local-new", List.of("baker", "union", "wallowa"), "", 2026, null, "the current county or contract onsite program",
                            "For records after the 2025 transfer, the current local onsite agent—not DEQ's retained archive—is the working custodian.",
                            "https://www.oregon.gov/deq/residential/pages/onsite-records.aspx", "Find the current local onsite agent",
                            "https://ormswd2.synergydcs.com/ORMSCMSearchDEQ/Search/SearchMain.aspx", "Check ORMS only for older retained records",
                            "the current county or contract onsite program", "request", "Ask the current local onsite program.", "Local-agent request draft"),
                    route("or-2025-transition", List.of("baker", "union", "wallowa"), "", "Oregon DEQ for pre-June 2025 records; the current local agent for newer files",
                            "DEQ retains records created before June 1, 2025 for Baker, Union, and Wallowa. Newer files belong with the current local onsite agent.",
                            "https://ormswd2.synergydcs.com/ORMSCMSearchDEQ/Search/SearchMain.aspx", "Search DEQ records before June 2025",
                            "https://www.oregon.gov/deq/residential/pages/onsite-records.aspx", "Find the current local agent",
                            "Oregon DEQ or the current local onsite agent", "search", "Use the record date to choose the custodian.", "Custodian follow-up draft"),
                    route("or-local-agent", List.of(), "", "the county or contract onsite program",
                            "Outside DEQ's directly managed counties and retained record periods, the county or its contract agent owns the file.",
                            "https://www.oregon.gov/deq/residential/pages/onsite-records.aspx", "Find the local onsite agent",
                            "https://ormswd2.synergydcs.com/ORMSCMSearchDEQ/Search/SearchMain.aspx", "Check ORMS for retained DEQ records",
                            "the county or contract onsite program", "request", "Ask the local onsite program for the file.", "Local-agent request draft")
            );
            case "VT" -> List.of(
                    route("vt-delegated-town", List.of("charlotte", "colchester"), "", "the municipal planning office",
                            "Charlotte and Colchester are delegated municipalities. Their planning office is the working custodian even though the state search can still provide useful permit clues.",
                            "https://anrweb.vt.gov/DEC/WWDocs/Default.aspx", "Search the state permit index",
                            "https://dec.vermont.gov/sites/dec/files/dwgwp/documents/Septic-Permit-Search-Guide.pdf", "Open the official search guide",
                            "the municipal planning office", "request", "Ask the delegated town for the property file.", "Town request draft"),
                    route("vt-dec-default", List.of(), "", "the appropriate Vermont DEC Regional Office",
                            "Search broadly first. Older, unnumbered, or microfilmed records may require the regional office or town file after the online search.",
                            "https://anrweb.vt.gov/DEC/WWDocs/Default.aspx", "Search Vermont wastewater permits",
                            "https://dec.vermont.gov/sites/dec/files/dwgwp/documents/Septic-Permit-Search-Guide.pdf", "Open the official search guide",
                            "the appropriate Vermont DEC Regional Office", "request", "Prepare the regional-office follow-up.", "Regional-office request draft")
            );
            default -> List.of();
        };
    }

    private static InstitutionRouteRule route(
            String key,
            List<String> areaMatches,
            String requesterRelation,
            String custodian,
            String routeNote,
            String primaryUrl,
            String primaryLabel,
            String secondaryUrl,
            String secondaryLabel,
            String requestRecipient,
            String contactMode,
            String artifactHeading,
            String artifactLabel
    ) {
        return new InstitutionRouteRule(key, areaMatches, requesterRelation, null, null, custodian, routeNote,
                primaryUrl, primaryLabel, secondaryUrl, secondaryLabel, requestRecipient,
                contactMode, artifactHeading, artifactLabel);
    }

    private static InstitutionRouteRule datedRoute(
            String key,
            List<String> areaMatches,
            String requesterRelation,
            Integer minimumYear,
            Integer maximumYear,
            String custodian,
            String routeNote,
            String primaryUrl,
            String primaryLabel,
            String secondaryUrl,
            String secondaryLabel,
            String requestRecipient,
            String contactMode,
            String artifactHeading,
            String artifactLabel
    ) {
        return new InstitutionRouteRule(key, areaMatches, requesterRelation, minimumYear, maximumYear, custodian, routeNote,
                primaryUrl, primaryLabel, secondaryUrl, secondaryLabel, requestRecipient,
                contactMode, artifactHeading, artifactLabel);
    }
}
