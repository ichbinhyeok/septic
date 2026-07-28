package com.example.septic.web;

import java.util.List;
import java.util.Map;

public final class CountyAcquisitionProfileCatalog {

    private static final Map<String, CountyAcquisitionProfileView> PROFILES = Map.ofEntries(
            Map.entry("VA::prince-william-county", acquisition(
                    "VA::prince-william-county",
                    "Prince William Health District — On-Site Sewage & Water Services",
                    "Historical document portal → VDH FOIA fallback",
                    "Portal downloads: no published fee. FOIA: actual search/copy cost may apply.",
                    "VDH response window: 5 working days; up to 7 additional working days when invoked.",
                    "Use the external portal or submit the VDH request in your own name.",
                    "",
                    "703-792-6310",
                    "Prince William septic and well records — {{address}}",
                    "The official repository is indexed by street address and GPIN and also supports names, dates, and other search terms.",
                    """
                    Please provide the complete onsite sewage and private-well property file for:

                    Property address: {{address}}
                    GPIN: {{parcel}}
                    Current or prior owner: {{owner}}
                    Approximate construction year: {{yearBuilt}}

                    Requested records:
                    {{documents}}

                    Please provide electronic copies. If records are indexed under a different address, GPIN, or former owner, please identify that reference. If charges may apply, please provide an estimate before processing.
                    """,
                    List.of(field("searchTerms", "Optional repository search terms", "Names, dates, or other known terms", false, "off")),
                    docs("septic permit or approval", "installed layout or site sketch", "final inspection or completion record", "repair and O&M history")
            )),
            Map.entry("TX::tarrant-county", acquisition(
                    "TX::tarrant-county",
                    "Tarrant County Engineering / the parcel's actual OSSF authority",
                    "Jurisdiction check → JustFOIA or responsible city",
                    "Inspection is generally no charge; written estimate required when county charges exceed $40.",
                    "If not produced within 10 working days, the county must provide a reasonable availability date.",
                    "Confirm the city, ETJ, contract-city, or county authority before submitting.",
                    "openrecords@tarrantcountytx.gov",
                    "817-212-7082",
                    "Public information request — OSSF file for {{address}}",
                    "Choose the jurisdiction first. The County Clerk affidavit search is supporting evidence, not the septic permit search.",
                    """
                    Under the Texas Public Information Act, please provide the existing OSSF file for:

                    Property address: {{address}}
                    Parcel/account number: {{parcel}}
                    City / ETJ / unincorporated status: {{jurisdiction}}
                    Current owner: {{owner}}

                    Requested records:
                    {{documents}}

                    Please provide electronic copies and identify any different city, district, or contract authority that maintains the file. Please send an estimate before incurring chargeable costs.
                    """,
                    List.of(
                            select("jurisdiction", "Which OSSF authority applies?", true,
                                    "Tarrant County or a contract city", "Another city", "ETJ / not confirmed")
                    ),
                    docs("OSSF permit and application", "site evaluation and approved design", "final inspection and license to operate", "repair, complaint, and maintenance records", "recorded OSSF affidavit when applicable")
            )),
            Map.entry("TN::hamilton-county", acquisition(
                    "TN::hamilton-county",
                    "Hamilton County Division of Groundwater Protection",
                    "Free document retrieval → Groundwater email fallback",
                    "Permit documents are free to download and print.",
                    "No record-copy turnaround is published.",
                    "In Document Retrieval, enter the street name only and select the correct full address.",
                    "gwp@hamiltontn.gov",
                    "423-209-7876",
                    "Existing septic permit and completion certificate — {{address}}",
                    "Copy the street name separately. Use email only when the address is absent or the online file is incomplete.",
                    """
                    Please provide the existing septic tank permit and septic tank installation certificate of completion for:

                    Property address: {{address}}
                    Street name used for Document Retrieval: {{streetName}}
                    Parcel or local ID: {{parcel}}
                    Requester relationship: {{requesterRole}}

                    I also request any available layout, final approval, and repair records. The address was {{portalOutcome}} in the county Document Retrieval site. Electronic delivery is preferred.
                    """,
                    List.of(
                            field("streetName", "Street name used in Document Retrieval", "Enter the street name used by the county search", true, "off")
                    ),
                    docs("existing septic tank permit", "installation certificate of completion", "layout and final approval", "repair or Groundwater notes")
            )),
            Map.entry("NC::alamance-county", acquisition(
                    "NC::alamance-county",
                    "Alamance County Health Department — Environmental Health",
                    "Official information-request PDF → email",
                    "No copy fee is published.",
                    "The county form states a 3-day turnaround.",
                    "Review the official PDF, add any missing signature/details, attach it, and send the email.",
                    "eh.admin@alamance-nc.com",
                    "336-570-6367",
                    "Environmental Health information request — {{address}}",
                    "The prepared summary mirrors the official form so the PDF can be completed without re-researching the property.",
                    """
                    Please process the attached Environmental Health Information Request for:

                    Property address: {{address}}
                    GPIN / parcel ID: {{parcel}}
                    Old tax-map number: {{oldTaxMap}}
                    Subdivision / lot: {{subdivision}} / {{lotNumber}}
                    Present owner: {{owner}}
                    Known past owners: {{previousOwner}}
                    Home built: {{yearBuilt}}
                    Septic installed: {{yearInstalled}}

                    Requested records:
                    {{documents}}

                    Please return electronic copies to {{requesterEmail}}.
                    """,
                    List.of(
                            field("requesterName", "Requester's name", "Full name", true, "name"),
                            field("requesterMailingAddress", "Requester's mailing address", "Mailing address", true, "street-address"),
                            field("requesterMailingAddress2", "Requester's mailing address, line 2", "Apartment, suite, or second line", false, "address-line2"),
                            field("requesterEmail", "Email address", "name@example.com", false, "email"),
                            field("requesterFax", "Fax number", "If used", false, "off"),
                            field("requesterPhone", "Phone number", "Phone number", false, "tel"),
                            field("oldTaxMap", "Old tax-map number", "If known", false, "off"),
                            field("subdivision", "Subdivision name", "Subdivision name", false, "off"),
                            field("lotNumber", "S/D lot number", "Lot number", false, "off"),
                            field("directions", "Directions to the property", "Directions used by the county", false, "off"),
                            field("owner", "Present owner", "Owner name", false, "name"),
                            field("previousOwner", "Known past owners", "Names used on older files", false, "off"),
                            field("yearBuilt", "Home built", "Year if known", false, "off"),
                            field("yearInstalled", "Septic installed", "Year if known", false, "off"),
                            select("wellPermitRequested", "Copy of well permit", true, "Request this record", "Do not request"),
                            select("septicPermitRequested", "Copy of septic permit", true, "Request this record", "Do not request"),
                            select("waterSampleRequested", "Copy of existing water sample results", true, "Request this record", "Do not request"),
                            select("soilEvaluationRequested", "Copy of soil evaluation", true, "Request this record", "Do not request")
                    ),
                    docs("copy of well permit", "copy of septic permit", "copy of existing water sample results", "copy of soil evaluation")
            )),
            Map.entry("TN::knox-county", acquisition(
                    "TN::knox-county",
                    "Knox County Health Department — Groundwater Division",
                    "County-branded public Jotform",
                    "No file-search fee is published.",
                    "No file-search turnaround is published.",
                    "Complete the live county-branded form and press Submit in your own name.",
                    "",
                    "865-215-5200",
                    "Knox County SSDS file search — {{address}}",
                    "These fields were verified directly against the live public form. The separate PDF link on the county landing page remains broken.",
                    """
                    Knox SSDS file-search field pack

                    Street address: {{address}}
                    City / ZIP: {{cityZip}}
                    Tax Map ID and parcel: {{parcel}}
                    Street number assigned: {{streetAssigned}}
                    Subdivision / unit / block / lot: {{subdivisionLot}}
                    Date constructed: {{yearBuilt}}
                    Current owner: {{owner}}
                    Original / previous owner: {{previousOwner}}
                    Applicant name: {{requesterName}}
                    Applicant phone: {{requesterPhone}}
                    Applicant email: {{requesterEmail}}

                    Comments: Please return the SSDS permit/drainfield layout, soil mapping, and completed repair records for this property.
                    """,
                    List.of(
                            select("propertyCounty", "Which county is the property located in?", true, "Knox County"),
                            field("owner", "Current Owner's Name", "Current owner", true, "name"),
                            field("streetAddress", "Street Address", "Street address as assigned in KGIS", true, "street-address"),
                            field("propertyCity", "City", "City", true, "address-level2"),
                            field("propertyZip", "Zip Code", "ZIP code", true, "postal-code"),
                            field("taxMapAndParcel", "Tax Map ID Number and Parcel Number", "Use KGIS if needed", true, "off"),
                            select("streetAssigned", "Is the street number assigned?", true, "Yes", "No"),
                            field("requestDetails", "Comments related to this request", "Optional factual comments", false, "off"),
                            field("requesterName", "Applicant Name", "Full name", true, "name"),
                            field("requesterPhone", "Applicant Phone Number", "(000) 000-0000", true, "tel"),
                            field("requesterEmail", "Applicant Email", "example@example.com", true, "email")
                    ),
                    docs("available SSDS property records")
            )),
            Map.entry("NC::lincoln-county", acquisition(
                    "NC::lincoln-county",
                    "Lincoln County Environmental Health",
                    "Environmental Health instructions → NextRequest fallback",
                    "No fee is published on the county landing page.",
                    "No record turnaround is published.",
                    "Use the accessible Environmental Health page and phone first; submit in NextRequest only when it is reachable.",
                    "",
                    "704-736-8426",
                    "Lincoln County septic and well records — {{address}}",
                    "The official county page says to use NextRequest with the property address and/or parcel number. Save the portal request reference.",
                    """
                    Please provide the existing septic-system property file for:

                    Property address: {{address}}
                    Parcel PIN: {{parcel}}
                    Current or former owner: {{owner}}
                    Subdivision / lot: {{subdivisionLot}}

                    Requested records:
                    {{documents}}

                    Electronic copies are preferred. If no file is located, please provide a written no-record response or identify the office that maintains it.
                    """,
                    List.of(),
                    docs("improvement permit", "construction authorization", "operation permit or final approval", "system layout and repair records")
            )),
            Map.entry("GA::dekalb-county", acquisition(
                    "GA::dekalb-county",
                    "DeKalb Public Health",
                    "Open Records request or current certification",
                    "Open-record assistance: $25/hour; public copies: $0.25/page.",
                    "No completion time is published.",
                    "Select Environmental Health, review the records description, complete the county contact fields, and submit.",
                    "",
                    "404-508-7900",
                    "DeKalb Environmental Health septic records — {{address}}",
                    "Historical records belong in Open Records. A loan/refinance/foster/adoption certification is a separate present-day evaluation.",
                    """
                    Georgia Open Records request — Environmental Health

                    Service needed: {{purpose}}
                    Property address: {{address}}
                    Parcel ID: {{parcel}}
                    Current / former owner: {{owner}}
                    Requester name: {{requesterName}}
                    Requester email: {{requesterEmail}}
                    Company: {{company}}

                    Requested records:
                    {{documents}}

                    Please provide existing electronic records and a cost estimate before chargeable custodian work. If "current certification" is selected, use the certification route rather than treating this as a historical-file request.
                    """,
                    List.of(
                            field("requestDate", "Date", "Date on the county form", false, "off"),
                            select("environmentalHealthRequest", "Is this an Environmental Health Records Request?", true, "Yes", "No"),
                            field("specificInformation", "Existing records requested", "Describe the existing records and include the property identifiers that apply", true, "off"),
                            field("requesterName", "Full name", "Full name", true, "name"),
                            field("contactDetail", "I can be contacted at", "Phone, email, or mailing contact", true, "off"),
                            field("company", "Company name", "Company name", true, "organization"),
                            field("companyAddress", "Company address", "Company mailing address", true, "street-address"),
                            field("requesterPhone", "Phone number", "(000) 000-0000", true, "tel"),
                            field("requesterEmail", "Email", "name@example.com", true, "email")
                    ),
                    docs("historical permit and installation inspection", "approved site plan or layout", "repair and complaint records")
            )),
            Map.entry("TN::blount-county", acquisition(
                    "TN::blount-county",
                    "Blount County Development Services / Environmental Health",
                    "GovQA Developmental Services request",
                    "No request fee is shown.",
                    "Minimum 7 business days.",
                    "Choose Developmental Services Requests, review the copied SSDS fields, complete CAPTCHA, and submit.",
                    "",
                    "865-681-9301",
                    "Completed SSDS information request — {{address}}",
                    "The field pack follows the current Developmental Services GovQA form and distinguishes historical SSDS information from an inspection letter.",
                    """
                    Please process the attached SSDS Information Request:

                    Property address: {{address}}
                    Subdivision / property and lot: {{subdivisionLot}}
                    SSDS installation date: {{yearInstalled}}
                    Original permittee: {{previousOwner}}
                    Previous street name: {{previousStreet}}
                    Current owner: {{owner}}
                    Agent / requester: {{requesterName}}
                    Requester phone: {{requesterPhone}}
                    Requester email: {{requesterEmail}}

                    Additional information requested:
                    {{documents}}

                    I understand this is a records response, not a site inspection, current-condition warranty, or loan-closing letter.
                    """,
                    List.of(
                            field("requesterEmail", "Email address", "name@example.com", true, "email"),
                            field("requesterFirstName", "First name", "First name", true, "given-name"),
                            field("requesterLastName", "Last name", "Last name", true, "family-name"),
                            field("requesterAddress", "Address 1", "Mailing address", false, "street-address"),
                            field("requesterAddress2", "Address 2", "Apartment, suite, etc.", false, "address-line2"),
                            field("requesterCity", "City", "City", false, "address-level2"),
                            field("requesterState", "State", "State", false, "address-level1"),
                            field("requesterZip", "ZIP", "ZIP code", false, "postal-code"),
                            field("requesterPhone", "Phone", "Phone number", true, "tel"),
                            field("company", "Company", "Company name", false, "organization"),
                            field("specificInformation", "SSDS record desired", "Describe the existing SSDS record needed", true, "off"),
                            field("subdivision", "Subdivision / property name", "Subdivision or property name", false, "off"),
                            field("lotNumber", "Lot number", "Lot number", false, "off"),
                            field("yearInstalled", "Date SSDS was installed", "If known", false, "off"),
                            field("owner", "Current property owner", "Owner name", false, "name"),
                            field("previousOwner", "Original SSDS permittee", "Name on original permit", false, "off"),
                            field("previousStreet", "Previous street name", "If the road was renamed", false, "off")
                    ),
                    docs("SSDS approval and approval date", "authorized bedroom count", "layout and repair records")
            )),
            Map.entry("MD::st-marys-county", acquisition(
                    "MD::st-marys-county",
                    "St. Mary’s County Health Department — Environmental Health",
                    "County GIS search → Public Information Act PDF fallback",
                    "No fee is published on the form.",
                    "No turnaround is published on the form.",
                    "Search GIS first. If the mapped Health Department record is missing or incomplete, sign the official PDF and email Environmental Health.",
                    "smchd.env@maryland.gov",
                    "301-475-4321",
                    "Environmental Health record request — {{address}}",
                    "The former app redirects to the current Public GIS Map. This exact field pack is only for the official PIA fallback.",
                    """
                    Please process the attached Public Information Act request:

                    Property location: {{address}}
                    Property owner: {{owner}}
                    Tax Map / Grid / Parcel: {{parcel}}
                    Subdivision / Lot / Section / Block: {{subdivisionLot}}
                    Requester name: {{requesterName}}
                    Requester email: {{requesterEmail}}
                    Delivery preference: Email

                    Information requested:
                    {{documents}}

                    Please return electronic copies or a written no-record/referral response.
                    """,
                    List.of(
                            field("requesterName", "Name", "Full name", true, "name"),
                            field("requesterAddress", "Address", "Mailing address", true, "street-address"),
                            field("requestDate", "Date", "Date on the official form", true, "off"),
                            field("requesterPhone", "Phone", "Phone number", false, "tel"),
                            field("requesterFax", "Fax", "Fax number", false, "off"),
                            field("requesterEmail", "Email", "name@example.com", false, "email"),
                            select("deliveryPreference", "How do you desire copies?", true, "Fax", "Mail", "Email"),
                            field("specificInformation", "Information requested", "Use the official form's wording", true, "off"),
                            field("owner", "Property owner", "Owner name", false, "name"),
                            field("subdivisionLot", "Subdivision / lot / section / block", "If known", false, "off")
                    ),
                    docs("septic permit and layout", "perc and site-evaluation history", "final approval", "repair or replacement records")
            )),
            Map.entry("NY::suffolk-county", acquisition(
                    "NY::suffolk-county",
                    "Suffolk County Department of Health Services — Office of Wastewater Management",
                    "Phone-assisted official file lookup",
                    "No phone-lookup fee is published.",
                    "No phone-lookup turnaround is published.",
                    "Call 631-852-5700 and give the tax-map number, construction year, and subdivision map/lot when available.",
                    "",
                    "631-852-5700",
                    "FOIL request — wastewater/septic records for {{address}}",
                    "Records are more likely for single-family homes built in 1973 or later. Older coverage is uncertain, not automatically absent.",
                    """
                    Suffolk County wastewater record request

                    Property address: {{address}}
                    Tax Map District / Section / Block / Lot: {{parcel}}
                    Approximate original construction year: {{yearBuilt}}
                    Subdivision / map / lot: {{subdivisionLot}}
                    Applicant name: {{requesterName}}
                    Applicant email: {{requesterEmail}}

                    Requested records:
                    {{documents}}

                    Please provide electronic copies. If records cannot be found after diligent search, please return the written FOIL disposition.
                    """,
                    List.of(
                            field("yearBuilt", "Original construction year", "Especially whether before/after 1973", true, "off"),
                            field("subdivisionLot", "Subdivision map name and lot number", "If the property is on a subdivision map", false, "off")
                    ),
                    docs("septic location record", "historical site and permit records", "installation or inspection documents")
            )),
            Map.entry("AZ::maricopa-county", acquisition(
                    "AZ::maricopa-county",
                    "Maricopa County Environmental Services",
                    "Free online search → paid research request",
                    "Free search: $0. Standard: $30. Expedited: $60.",
                    "Standard: 3–7 business days. Expedited: 1–2 business days.",
                    "For the free route, accept the county agreement, complete reCAPTCHA, and submit. The paid fields below apply only after a failed or incomplete free search.",
                    "",
                    "602-506-3011",
                    "Maricopa septic research request — {{address}}",
                    "Run the free search first. Use the paid field pack only when the free result is empty or incomplete.",
                    """
                    Maricopa septic research field pack

                    Site address / city / ZIP: {{siteAddress}} / {{siteCity}} / {{siteZip}}
                    Parcel number: {{siteParcel}}
                    Legal description: {{legalDescription}}
                    Subdivision / lot: {{subdivisionLot}}
                    Year installed: {{yearInstalled}}
                    Request purpose: {{purpose}}
                    Company / nature of business: {{company}}
                    Failing system: {{systemFailing}}
                    Expedited: {{expedited}}
                    Contact: {{requesterName}} / {{requesterEmail}} / {{requesterPhone}}

                    Requested records:
                    {{documents}}
                    """,
                    List.of(
                            field("company", "Company name", "Official form requires this field", true, "organization"),
                            field("natureOfBusiness", "Nature of business", "Official form requires this field", true, "organization"),
                            field("requesterName", "Contact name", "Full name", true, "name"),
                            field("contactTitle", "Contact title", "Title", true, "organization-title"),
                            field("requesterAddress", "Contact street address", "Street address", true, "street-address"),
                            field("requesterCity", "Contact city", "City", true, "address-level2"),
                            field("requesterState", "Contact state", "State", true, "address-level1"),
                            field("requesterZip", "Contact ZIP", "ZIP code", true, "postal-code"),
                            field("requesterEmail", "Contact email", "name@example.com", true, "email"),
                            field("requesterPhone", "Phone number", "Phone number", true, "tel"),
                            field("requesterFax", "Fax number", "Optional", false, "off"),
                            field("siteAddress", "Site address", "Property street address", true, "street-address"),
                            field("siteCity", "Site city", "City", true, "address-level2"),
                            field("siteZip", "Site ZIP", "ZIP code", true, "postal-code"),
                            field("yearInstalled", "Year installed", "If known", false, "off"),
                            field("legalDescription", "Legal description", "If known", false, "off"),
                            field("siteParcel", "Parcel number", "If known", false, "off"),
                            field("purpose", "Purpose of requested public records", "Use your own factual purpose", true, "off"),
                            field("subdivisionLot", "Subdivision name and lot number", "If known", false, "off"),
                            select("systemFailing", "System failing?", false, "Yes", "No"),
                            select("expedited", "Expedited", false, "Yes", "No")
                    ),
                    docs("septic plans and permits", "research response", "final inspection or approval records")
            )),
            Map.entry("NC::brunswick-county", acquisition(
                    "NC::brunswick-county",
                    "Brunswick County Environmental Health — Water Protection",
                    "Internal public-metadata query → source-file email",
                    "No historical-file fee is published.",
                    "No historical-file turnaround is published.",
                    "Search metadata here first, then review and send the source-file email.",
                    "septicplans@brunswickcountync.gov",
                    "910-253-2150",
                    "Request for septic source file — {{address}} / {{parcel}}",
                    "A permit metadata hit is only a lead. Add its permit number below before sending the request for the actual documents.",
                    """
                    Please provide the complete septic source file for:

                    Property address: {{address}}
                    Tax parcel ID: {{parcel}}
                    Candidate permit number: {{permitNumber}}
                    Candidate permit type/date: {{permitClue}}
                    Current owner: {{owner}}

                    Requested records:
                    {{documents}}

                    Please provide electronic copies. The public permit metadata was used only to identify the parcel/permit candidate and is not being treated as the complete septic file.
                    """,
                    List.of(),
                    docs("Improvement Permit", "Construction Authorization", "Operation Permit or final approval", "site evaluation, installed layout, and repair records")
            )),
            Map.entry("NC::forsyth-county", acquisition(
                    "NC::forsyth-county",
                    "Forsyth County Environmental Health",
                    "Environmental Health web request",
                    "No historical-copy fee is published.",
                    "No historical-copy turnaround is published.",
                    "Select Septic, paste or type your factual message, choose a contact method, and submit.",
                    "",
                    "336-703-3225",
                    "Forsyth septic permit and soil evaluation — {{address}}",
                    "The county form limits the message to 2,000 characters. SepticPath prepares the confirmed form fields but does not invent the message.",
                    """
                    Please provide the historical onsite wastewater property file for:

                    Address: {{address}}
                    Parcel PIN: {{parcel}}
                    Current or former owner: {{owner}}
                    Purpose: {{purpose}}

                    Requested records:
                    {{documents}}

                    Please send electronic copies or a written no-record/referral response. This is a historical record request, not an application for a Health Department Release or new repair permit.
                    """,
                    List.of(
                            field("specificInformation", "Message (2,000 character limit)", "Write the factual question or existing records you want", true, "off"),
                            field("requesterName", "Your name", "Full name", true, "name"),
                            select("contactMethod", "Contact by", true, "Phone", "Email", "Fax", "Cell Phone", "Mail"),
                            field("contactDetail", "Contact detail for the selected method", "Phone, email, fax, or mailing address", true, "off")
                    ),
                    docs("septic permit", "soil evaluation sheet", "final approval and layout", "repair or malfunction records")
            )),
            Map.entry("TX::denton-county", acquisition(
                    "TX::denton-county",
                    "Denton County Development Services / Environmental Health",
                    "Jurisdiction check → PIA PDF/email",
                    "Standard copies: $0.10/page; labor and nonstandard media may add charges.",
                    "10 business days concerns compliance/AG determination, not guaranteed delivery.",
                    "Confirm unincorporated jurisdiction, sign the official PDF, attach it, and send.",
                    "developmentpermits@dentoncounty.gov",
                    "940-349-2920",
                    "Denton County public information request — OSSF file for {{address}}",
                    "Use this only when Denton County owns the file. A municipality may be the custodian for an incorporated parcel.",
                    """
                    Texas Public Information Act request

                    Property address: {{address}}
                    Parcel/account number: {{parcel}}
                    Jurisdiction: {{jurisdiction}}
                    Current owner: {{owner}}
                    Requester: {{requesterName}}
                    Requester email: {{requesterEmail}}

                    I request the following existing Denton County records:
                    {{documents}}

                    Electronic copies are preferred. Please provide an advance estimate before chargeable work and identify the correct custodian if the parcel is outside county OSSF jurisdiction.
                    """,
                    List.of(
                            field("requesterName", "Requestor's name", "Full name", true, "name"),
                            field("requestDate", "Date", "Date on the official form", true, "off"),
                            field("requesterMailingAddress", "Mailing address", "Mailing address", true, "street-address"),
                            field("requesterPhone", "Phone", "Phone number", true, "tel"),
                            field("requesterEmail", "Email address", "name@example.com", true, "email"),
                            field("specificInformation", "Existing county records requested", "Describe the existing records without asking the county to create a new document", true, "off"),
                            select("deliveryChoice", "Delivery choice", true, "Receive copies and pick them up", "Inspect originals"),
                            select("notificationChoice", "Notification method", true, "Email", "Postal service")
                    ),
                    docs("OSSF application and permit", "site evaluation and approved design", "construction authorization and final approval/license to operate", "maintenance, complaint, and repair records")
            )),
            Map.entry("TX::brazoria-county", acquisition(
                    "TX::brazoria-county",
                    "Brazoria County Environmental Health — OSSF",
                    "OSSF office email → formal PIA fallback",
                    "No OSSF historical-copy fee is published.",
                    "No OSSF historical-copy turnaround is published.",
                    "The official Environmental Health page provides a general OSSF contact. Exact historical-record request fields are not published there.",
                    "ehinspector@brazoriacountytx.gov",
                    "979-864-1600",
                    "Existing OSSF file and planning materials — {{address}}",
                    "Use only the current official contact page; SepticPath does not generate an OSSF record request until the county publishes or confirms the required fields.",
                    """
                    Please provide the existing OSSF property file and planning materials for:

                    911 property address: {{address}}
                    Parcel/account number: {{parcel}}
                    Legal description: {{legalDescription}}
                    City / ETJ / unincorporated status: {{jurisdiction}}
                    Present or former owner: {{owner}}
                    Known permit / installer / date: {{permitClue}}

                    Requested records:
                    {{documents}}

                    Please provide electronic copies, a written no-record response, or a referral to the correct city/ETJ authority. If a formal Texas Public Information Act request is required, please advise where this request should be submitted.
                    """,
                    List.of(
                            field("legalDescription", "Legal description", "Subdivision, block, lot, tract", false, "off"),
                            select("jurisdiction", "Property jurisdiction", true, "Unincorporated Brazoria County", "Inside a city", "ETJ / unsure"),
                            field("owner", "Present or former owner", "Owner name", false, "name"),
                            field("permitClue", "Known permit, installer, or date", "Any old file clue", false, "off")
                    ),
                    docs("OSSF application and permit", "site evaluation and design/planning materials", "authorization and final approval", "maintenance, repair, and complaint records")
            ))
    );

    private CountyAcquisitionProfileCatalog() {
    }

    public static CountyAcquisitionProfileView find(String countyKey) {
        return PROFILES.get(countyKey);
    }

    private static CountyAcquisitionProfileView acquisition(
            String countyKey,
            String agency,
            String channelLabel,
            String feeLabel,
            String timingLabel,
            String userCheckpoint,
            String recipientEmail,
            String phone,
            String emailSubject,
            String preparationNote,
            String requestTemplate,
            List<CountyAcquisitionFieldView> fields,
            List<String> requestedDocuments
    ) {
        return new CountyAcquisitionProfileView(
                countyKey,
                agency,
                channelLabel,
                feeLabel,
                timingLabel,
                userCheckpoint,
                recipientEmail,
                phone,
                emailSubject,
                preparationNote,
                requestTemplate.strip(),
                List.copyOf(fields),
                List.copyOf(requestedDocuments)
        );
    }

    private static CountyAcquisitionFieldView field(
            String key,
            String label,
            String placeholder,
            boolean required,
            String autocomplete
    ) {
        return new CountyAcquisitionFieldView(key, label, placeholder, required, autocomplete, List.of());
    }

    private static CountyAcquisitionFieldView select(
            String key,
            String label,
            boolean required,
            String... options
    ) {
        return new CountyAcquisitionFieldView(key, label, "", required, "off", List.of(options));
    }

    private static List<String> docs(String... documents) {
        return List.of(documents);
    }
}
