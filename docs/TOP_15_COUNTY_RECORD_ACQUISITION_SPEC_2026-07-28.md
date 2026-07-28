# Top 15 County Record Acquisition Specification — 2026-07-28

## 2026-07-28 no-inference correction

The production UI must not turn this research document into a substitute government request.

- A county PDF remains the submission document. SepticPath may show fields copied from it, but may not replace it with locally written request language.
- A county portal or search form remains the submission interface. SepticPath may link to it and show directly verified fields, but may not invent missing fields.
- Generated request text, generated document bundles, populated email bodies, and locally generated replacement PDFs are disabled until the receiving agency confirms that exact workflow.
- Unpublished fee and turnaround values display as unpublished, not estimated.
- When exact fields have not been directly checked, the page shows only the official route and an explicit unverified-fields status.
- Knox County's official page currently instructs users to complete an SSDS file-search PDF and email `kchd.filesearch@knoxcounty.org`, but the linked PDF returned 404 during the 2026-07-28 review. The earlier Jotform route has been removed and no replacement form is generated.

## Implemented production behavior (authoritative)

The county sections below preserve research history and earlier product hypotheses. When they mention drafting, prefilling, or generating a request, this table and the no-inference correction above supersede them.

| County | Implemented lane | What SepticPath now does |
|---|---|---|
| Prince William, VA | Official search | Collects verified repository identifiers, opens the repository, and routes empty/blocked results to the official VDH fallback. |
| Tarrant, TX | Jurisdiction + official portal | Shows the jurisdiction-first steps, opens JustFOIA, records the observed access-restriction risk, and sends a blocked outcome to the official OSSF office. No portal fields are invented. |
| Hamilton, TN | Official search | Collects the address and verified street-name search value, copies those values, opens Document Retrieval, and routes an empty result to Groundwater instructions. |
| Alamance, NC | Official PDF | Shows fields checked against the county PDF, copies completed values, opens the PDF and verified email recipient, but does not create or submit a replacement form. |
| Knox, TN | Broken official PDF route | Shows the official instructions, verified email and phone, and the current missing-PDF limitation. It creates no substitute form. |
| Lincoln, NC | Official portal | Collects only the officially stated address/PIN identifiers, opens NextRequest, stores the request reference on return, and exposes the county page/phone fallback because the portal returned 403 during the review. |
| DeKalb, GA | Official portal | Separates historical Open Records from current certification and prepares the exact fields observed on the current Environmental Health Open Records form. |
| Blount, TN | Official GovQA portal | Prepares the exact Developmental Services fields observed in the current GovQA records center. The older SSDS PDF remains a field/timing reference, not the live submission route. |
| St. Mary's, MD | Official PDF | Uses the current PIA PDF and Environmental Health route instead of promising the discontinued GIS application. |
| Suffolk, NY | Official phone-assisted route | Prepares the tax-map number, approximate construction year, and optional subdivision/lot details the county says callers must provide. The current FOIL PDF returned 403 and is not presented as a working route. |
| Maricopa, AZ | Free search + paid portal | Starts with the free official search, exposes fields checked against the paid form, and routes an empty result to the paid official request. |
| Brunswick, NC | Official metadata search | Queries the county ArcGIS permit index inside SepticPath, labels matches as metadata leads, and sends the user to Water Protection for the source file. |
| Forsyth, NC | Official contact form | Opens the Environmental Health form, selects the Septic category, and prepares the exact address/message/contact fields observed after that selection. It is still labeled as a general contact form, not a dedicated archive portal. |
| Denton, TX | Official PDF | Shows fields checked against the Public Information Request PDF and keeps jurisdiction confirmation explicit. |
| Brazoria, TX | Official OSSF contact | Uses the OSSF-specific address published by Environmental Health, `ehinspector@brazoriacountytx.gov`; no dedicated archive form or accepted email body is claimed. |

For every lane, the return panel records one of five real outcomes: document received, partial file, nothing online, route blocked, or request submitted. A county confirmation/case number is stored locally and restored on return. A received document continues to the upload-and-analysis workspace.

## Decision

The first production cohort is the 15 county pages that produced 70 Google Search Console clicks in the last 90 days. These users should not receive one generic “visit the county” workflow. Each county needs a small acquisition adapter that knows the record custodian, action page, required identifiers, record bundle, published fee and timing, user-only checkpoint, and fallback.

This cohort supports four product lanes:

1. **Retrieve or search** when an official public query is available.
2. **Prepare and submit** when the county uses a form, email, or public-records portal.
3. **Call with a prepared script** only when the office makes phone assistance the primary route.
4. **Preserve and ingest** every outcome so the user returns to the same property case with the record, request reference, or written no-record response.

“Not published” below is intentional. A new-permit fee is never shown as a historical-record-copy fee, and a statutory response deadline is never presented as a guaranteed delivery date.

## Cohort and routing summary

| Rank | County | GSC clicks | Best first lane | SepticPath should do | User-only checkpoint |
|---:|---|---:|---|---|---|
| 1 | Prince William, VA | 14 | Public document portal | Prepare address/GPIN; open exact repository; fall back to VDH FOIA | Portal interaction or FOIA submission |
| 2 | Tarrant, TX | 7 | Jurisdiction resolver + PIA | Determine county/city/ETJ; draft exact OSSF file request | Confirm authority and submit |
| 3 | Hamilton, TN | 6 | Free document retrieval | Open exact search; explain street-name-only rule; draft Groundwater fallback | Select address/register if required |
| 4 | Alamance, NC | 5 | Dedicated information-request PDF | Prefill property clues and requested document boxes | Review and email form |
| 5 | Knox, TN | 5 | Dedicated online file-search application | Prepare every live form field and request bundle | CAPTCHA/submission |
| 6 | Lincoln, NC | 5 | NextRequest public-records portal | Draft record description and carry address/PIN into request | Submit portal request |
| 7 | DeKalb, GA | 4 | Open Records form | Route historical file separately from certification evaluation | Submit Jotform |
| 8 | Blount, TN | 3 | GovQA Developmental Services request | Prepare every observed portal field and retain the older published timing as a labeled reference | CAPTCHA/submit and save reference |
| 9 | St. Mary’s, MD | 3 | PIA PDF/email fallback | Stop promising discontinued GIS; prefill official request | Sign/attach/send |
| 10 | Suffolk, NY | 3 | Phone-assisted search | Prepare the county-required tax-map and construction details | Call the county office |
| 11 | Maricopa, AZ | 3 | Free official search, then paid request | Start free search; prepare all paid-form fields | CAPTCHA, purpose declaration, payment |
| 12 | Brunswick, NC | 3 | Public ArcGIS metadata + office request | Query metadata in SepticPath; draft source-file request | Send request if document is absent |
| 13 | Forsyth, NC | 3 | Environmental Health web request | Prepare message for permit and soil evaluation | Submit county form |
| 14 | Denton, TX | 3 | Jurisdiction check + PIA PDF/email | Confirm unincorporated jurisdiction; prefill PDF and email | Sign/attach/send |
| 15 | Brazoria, TX | 3 | OSSF office email, then PIA fallback | Draft request to OSSF custodian with legal/property identifiers | Review/send; use PIA if required |

## Shared property case

Collect once and reuse only where the county asks for it:

- requester name, email, phone, mailing address;
- property street address, city, state, ZIP;
- county-specific parcel identifier: GPIN, PIN, Tax ID, tax-map number, or legal description;
- present owner and known former owners;
- subdivision, lot, block, section, unit, map, or grid;
- approximate construction and septic-installation years;
- purpose: purchase diligence, maintenance, repair, addition, sale/closing, or current-condition certification;
- desired records;
- preferred electronic delivery;
- fee ceiling and permission to continue if a fee estimate is issued.

Default record bundle:

- original septic/OSSF/SSDS permit or improvement permit;
- site evaluation, soil evaluation, perc results, design, and installed layout/site sketch;
- construction authorization and final inspection/approval, operation permit, or license to operate;
- repair, malfunction/complaint, alteration, and replacement records;
- maintenance/O&M records only when the authority maintains them;
- a written no-record or referral response when the file cannot be found.

## County specifications

### 1. Prince William County, Virginia

**Custodian:** Prince William Health District, On-Site Sewage & Water Services.

**Best route:** The Health District says its document database is the historical repository and indexes property documents by street address and GPIN. The documented click path is `PWC State Health Department Documents → first street-name letter → street → house number`.

**Product adapter:**

- collect full address and GPIN;
- open the exact Laserfiche repository;
- show the click path before departure;
- let the user mark `downloaded`, `partial`, `empty`, or `blocked`;
- when blocked or incomplete, generate a VDH NextRequest FOIA request for the default record bundle.

**Published fee/time:** The self-service repository does not publish a download fee. VDH may charge actual staff/search/copy costs and will provide an advance estimate on request. VDH must make a statutory response within five working days, or may invoke seven additional working days; this is a response window, not a guaranteed document-delivery time.

**User checkpoint:** External repository use or VDH FOIA submission. A 403 is `portal blocked`, never `no record`.

**Fallback:** VDH NextRequest for routine well/septic records, or On-Site Sewage & Water Services at 703-792-6310 option 2.

**Official sources:**

- https://www.vdh.virginia.gov/prince-william/environmental-health/onsite-sewage-and-water-services/
- https://lfportal.pwcgov.org/healthweb/browse.aspx?startid=1
- https://www.vdh.virginia.gov/commissioner/freedom-of-information-act/
- https://vdh.nextrequest.com/

### 2. Tarrant County, Texas

**Custodian:** Depends on location. Tarrant County regulates unincorporated areas and named contract cities, while Arlington, Burleson, Fort Worth, Fort Worth Lake Management, and Tarrant water-improvement districts may regulate their own areas. ETJ status can change the answer.

**Best route:** Resolve authority first. Then request the OSSF source file from that authority. The County Clerk’s PublicSearch is secondary and can find a recorded affidavit; it is not the OSSF permit database.

**Product adapter:**

- resolve incorporated/unincorporated/ETJ and show the likely authority;
- if Tarrant County owns the record, prepare a JustFOIA or email request to `openrecords@tarrantcountytx.gov`;
- describe the file as the permit/application, site evaluation/design, authorization/final inspection, license to operate, recorded affidavit, repair, complaint, and maintenance records for the identified parcel;
- use County Clerk search only for the recorded OSSF affidavit.

**Published fee/time:** Inspection is most often no charge. If charges exceed $40, the county must issue a written estimate before work begins. If responsive information cannot be produced within 10 working days, the public information officer must give a reasonable availability date. Ten days is not a guaranteed completion promise.

**User checkpoint:** Confirm the resolved authority and submit the written request. A Clerk no-result is not an OSSF no-record result.

**Fallback:** Tarrant County OSSF office, 817-212-7082; countywide JustFOIA/public-information route.

**Official sources:**

- https://www.tarrantcountytx.gov/en/engineering-services/environmental/ossf.html
- https://www.tarrantcountytx.gov/en/county/website-use/public-information-act.html
- https://tarrantcountytx.justfoia.com/publicportal
- https://tarrant.tx.publicsearch.us/

### 3. Hamilton County, Tennessee

**Custodian:** Hamilton County Division of Groundwater Protection.

**Best route:** Free document retrieval at the county permit site. For site address, enter the **street name only**, not the street number, then select the correct address and provide contact information. Owners may email Groundwater for the existing permit or installation certificate; missing addresses also go to Groundwater.

**Product adapter:**

- parse and copy the street name separately from the house number;
- open the exact `Documents.aspx` page;
- ask owner/non-owner status before selecting fallback;
- draft a request to `gwp@hamiltontn.gov` for the existing septic permit and installation certificate of completion when the address is absent.

**Published fee/time:** Permit documents on the Document Retrieval page are free to download and print. No record-request turnaround is published. Do not show the county’s existing-system approval or inspection-letter fees as a copy fee.

**User checkpoint:** Address selection and contact registration if prompted.

**Fallback:** `gwp@hamiltontn.gov`, 423-209-7876.

**Official sources:**

- https://buildinginspection.hamiltontn.gov/Documents.aspx
- https://www.hamiltontn.gov/BuildingInspection_Septic.aspx
- https://www.hamiltontn.gov/Department_DevelopmentServices.aspx

### 4. Alamance County, North Carolina

**Custodian:** Alamance County Health Department, Environmental Health Section.

**Best route:** The county’s dedicated Information Request form, not its new septic application.

**Required form data:** requester name and mailing/contact details; GPIN/parcel ID; old tax-map number; subdivision and lot; property address and directions; present and known past owners; installation and home-build dates. The form has separate selections for septic permit and soil evaluation.

**Product adapter:**

- prefill all known property and requester fields;
- select `Copy of septic permit` and `Copy of soil evaluation` by default;
- add repair/malfunction/final approval in the specific-information text when relevant;
- download the completed packet and open an email draft to `eh.admin@alamance-nc.com`.

**Published fee/time:** The form says three-day turnaround for requested information. It does not publish a copy fee. The paid existing-system inspection is a different service.

**User checkpoint:** Review the form, attach it, and send.

**Official source:**

- https://eh.alamancecountync.gov/wp-content/uploads/sites/27/2019/06/Information-Request-Edited-Form.pdf

### 5. Knox County, Tennessee

**Custodian:** Knox County Health Department, Environmental Health Groundwater Division.

**Best route:** The live county page now links to a dedicated Jotform `Subsurface Sewage Disposal System File Search Application`. This supersedes older cached instructions that still mention a downloadable form and email.

**Live form data:** tax-map ID and parcel; whether street number is assigned; subdivision; unit/block/lot; construction date; request comments; requester name, phone, and email; current, original, and previous owner names; street address, city, ZIP, and county. The address must match KGIS.

**Product adapter:**

- resolve the KGIS parcel/tax-map ID before opening the form;
- prepare a field-by-field copy panel using the live labels;
- request the SSDS permit/drainfield layout, soil mapping, and completed repair records;
- retain a local case before departure and provide an upload/reply-email return path.

**Published fee/time:** Not published on the live application.

**User checkpoint:** Complete and submit the external Jotform. Do not promise that SepticPath submitted it.

**Fallback:** 865-215-5200. The current official page remains the canonical route because the form target has already changed once.

**Official sources:**

- https://www.knoxcounty.org/health/groundwater_protection.php
- https://knoxcounty.jotform.com/team/eh/ssds-file-search-app
- https://www.kgis.org/

### 6. Lincoln County, North Carolina

**Custodian:** Lincoln County Environmental Health through the county’s NextRequest portal.

**Best route:** `Request Septic and Well Records` links directly to NextRequest. The county specifically asks for property address and/or parcel number.

**Product adapter:**

- prepare address, parcel PIN, owner, and default record bundle;
- open the exact `requests/new` page;
- generate a concise request description and save the portal reference when returned;
- keep new septic applications out of this lane; the county currently requires those in person.

**Published fee/time:** Not published on the Environmental Health or public-records landing page.

**User checkpoint:** Portal submission and any account/contact verification.

**Fallback:** Environmental Health at 704-736-8426.

**Official sources:**

- https://www.lincolncountync.gov/2617/Onsite-Water
- https://lincolncountync.nextrequest.com/requests/new

### 7. DeKalb County, Georgia

**Custodian:** DeKalb Public Health.

**Best route:** Use the online Open Records Request form for historical septic permits and installation/inspection records. Use the septic page's separate certification-letter route only when the user needs a present-day system evaluation for a loan, refinancing, foster, or adoption purpose. That link currently opens a general Environmental Health Jotform, so the user must choose/describe the certification service rather than assume a dedicated record-copy form.

**Product adapter:**

- begin with a required purpose choice: `historical file` or `current certification`;
- historical file → prepare the Open Records Jotform request for existing records;
- certification → open the linked Environmental Health Jotform, prepare the certification description, and explain that the service evaluates apparent current function;
- never route a record-copy seeker to the new construction/repair application.

**Published fee/time:** Open-record assistance is $25 per custodian hour and public copies are $0.25 per page under the published fee schedule. No completion time is published. Certification pricing should be taken from the live form before display; do not infer it from general land-use evaluation fees.

**User checkpoint:** Jotform submission and any certification payment/site access.

**Official sources:**

- https://dekalbpublichealth.com/environmental-health/septic-systems/
- https://dekalbpublichealth.com/about-us/media-center/open-records-requests/
- https://dekalbpublichealth.com/wp-content/uploads/2025/07/DKPH-EH-Fees-2024.pdf

### 8. Blount County, Tennessee

**Custodian:** Blount County Development Services / Environmental Health.

**Best route:** The current county Public Records page directs septic/Developmental Services requests to the GovQA Records Center. Select `Developmental Services Requests`. The older SSDS PDF is retained only as an official field and timing reference.

**Required form data:** subdivision/property and lot; street address; installation date; original permittee; former street name; additional information; agent; current owner; company; phone/fax; date; email.

**Product adapter:**

- prepare the exact fields observed in the live GovQA route;
- tell the user to select `Developmental Services Requests`;
- carry the property, owner, subdivision/lot, installation, original-permittee, former-street-name, and desired-record values to the portal;
- preserve the returned GovQA reference.

**Published fee/time:** No request fee is shown. Minimum seven business days.

**User checkpoint:** Enter any remaining identity fields, complete CAPTCHA, submit in GovQA, and save the reference. A returned historical file is not an inspection letter, current-condition warranty, or loan-closing document.

**Official source:**

- https://www.blounttn.gov/DocumentCenter/View/17507/SSDS-Request-Form

### 9. St. Mary’s County, Maryland

**Custodian:** St. Mary’s County Health Department, Environmental Health Division.

**Best route:** Use the official Public Information Act PDF emailed to `smchd.env@maryland.gov`. The Health Department still describes a GIS path, but the linked ArcGIS item is currently titled `Discontinued GIS Map`; it must not be presented as a dependable first action.

**Required form data:** requester name/signature/address/date/contact; delivery choice; requested information; property owner and location; tax map/grid/parcel; subdivision/lot/section/block.

**Product adapter:**

- optionally attempt the county GIS only after labelling it unverified;
- prefill the PIA PDF and request septic permit/layout, perc/site evaluation, final approval, and repair/replacement records;
- prepare email delivery and retain the signed request.

**Published fee/time:** Not published on the records page or form.

**User checkpoint:** Signature, attachment, and email send.

**Fallback:** Environmental Health at 301-475-4321.

**Official sources:**

- https://smchd.org/permits-records/environmental-health-records/
- https://smchd.org/wp-content/uploads/Public-Information-Request-Form-NEW.pdf
- https://www.arcgis.com/sharing/rest/content/items/f0010a9a5db04695b37c39c033c33edb?f=pjson

### 10. Suffolk County, New York

**Custodian:** Suffolk County Department of Health Services, Office of Wastewater Management / FOIL Officer.

**Best route:** Call 631-852-5700 with the complete tax-map number and approximate original construction year. The county says subdivision map name and lot may help and that records are more likely for single-family homes built in 1973 or later. The FOIL PDF returned 403 during the 2026-07-28 review, so it is not exposed as a dependable live route.

**Required FOIL data:** date; applicant/business/contact/mailing details; signature; inspect or receive copies; exact record description; one complete property address per request; complete District/Section/Block/Lot tax-map number.

**Product adapter:**

- prepare the exact lookup facts named by the county;
- open the official FAQ beside a phone-first action;
- label pre-1973 coverage as uncertain, not absent;
- let the user record found, partial, no-record, blocked, or follow-up-needed outcomes.

**Published fee/time:** No fee or completion time is published for the phone-assisted lookup.

**User checkpoint:** Make the call and provide the prepared tax-map/construction details.

**Official source:**

- https://suffolkcountyny.gov/Departments/Health-Services/Environmental-Quality/Environmental-Engineering/Common-Issues-and-Frequently-Asked-Questions

### 11. Maricopa County, Arizona

**Custodian:** Maricopa County Environmental Services.

**Best route:** Run the official no-charge Online Septic Search first. If it is empty, submit a paid Online Septic Research Request.

**Paid-form data:** company and nature of business; contact name/title/address/email/phone; site address/city/ZIP; installation year; legal description; parcel; request purpose; subdivision/lot; failing-system and expedited flags.

**Product adapter:**

- collect a purpose declaration before sending the user out;
- open the free search and record its result;
- if empty, copy every paid-form field in page order;
- display exact standard/expedited fee and time;
- do not automate around reCAPTCHA or payment.

**Published fee/time:** Free search: no charge. Standard research: $30 and 3–7 business days. Expedited: $60 and 1–2 business days.

**User checkpoint:** Agreement, reCAPTCHA, purpose declaration, submission, and payment. Arizona commercial-purpose requests require special handling and may have different charges.

**Official sources:**

- https://www.maricopa.gov/2581/Online-Septic-Research
- https://www.maricopa.gov/FormCenter/Environmental-Services-16/Online-Septic-Search-92
- https://www.maricopa.gov/FormCenter/Environmental-Services-16/Online-Septic-Research-Request-Form-91

### 12. Brunswick County, North Carolina

**Custodian:** Brunswick County Environmental Health, Water Protection Program.

**Best route:** SepticPath can query the county’s public ArcGIS permit metadata by address or parcel ID. The result is a permit lead, not necessarily the septic source document.

**Product adapter:**

- run the existing internal ArcGIS lookup;
- match parcel ID, address, permit number/category/status/description/date;
- if the hit is septic-related, prefill a source-file email to `septicplans@brunswickcountync.gov`;
- request the Improvement Permit, Construction Authorization, Operation Permit, site evaluation/design, installed layout, and repair records using the candidate permit number;
- if metadata is empty, still allow a direct file request.

**Published fee/time:** No historical-file fee or turnaround is published. New-permit and water-testing fees do not apply to this copy request unless the office confirms otherwise.

**User checkpoint:** Review and send the source-file request. Metadata alone is never labelled a complete septic record.

**Official sources:**

- https://www.brunswickcountync.gov/195/Permit-Reports
- https://www.brunswickcountync.gov/250/Environmental-Health
- https://www.brunswickcountync.gov/179/Public-Record-Requests
- https://services1.arcgis.com/W6gamXPYQeLXrdAd/arcgis/rest/services/Permit_Locations/FeatureServer/0

### 13. Forsyth County, North Carolina

**Custodian:** Forsyth County Environmental Health.

**Best route:** Use the county’s `Submit a Request or Complaint` form with `Septic` selected, or call 336-703-3225. The owner guide says the department can provide a copy of the septic permit and soil evaluation sheet.

**Product adapter:**

- prepare a 2,000-character-or-less message requesting the default historical bundle;
- include address, PIN, owner/former owner, and purpose;
- open the county form with a field-by-field copy panel;
- keep additions/pools/decks in the separate Health Department Release lane.

**Published fee/time:** Not published for historical copies. New/repair permit fees do not apply to a record request.

**User checkpoint:** Select `Septic`, paste the prepared message, complete contact fields, and submit.

**Official sources:**

- https://forsyth.cc/publichealth/environmentalhealth/septic_main.aspx
- https://www.forsyth.cc/hhs/report_form.aspx

### 14. Denton County, Texas

**Custodian:** Denton County Development Services for records it possesses; Environmental Health regulates OSSF in the applicable unincorporated lane.

**Best route:** Confirm county jurisdiction, then complete the county Public Information Request PDF and email it to `developmentpermits@dentoncounty.gov`.

**Required form data:** requester name/date/mailing address/phone/email; detailed existing-record description; copies or inspection; notification choice; signature.

**Product adapter:**

- resolve unincorporated/municipal jurisdiction first;
- prefill the PDF for the OSSF application/permit, site evaluation/design, construction authorization, final approval/license to operate, maintenance, complaint, and repair records;
- draft the email with PDF attachment;
- store any advance cost estimate before the user authorizes payment.

**Published fee/time:** Standard copies are $0.10 per page; labor and nonstandard media may add charges; an advance estimate is provided. The county’s 10-business-day language concerns compliance or Attorney General determination and is not a guaranteed fulfillment time.

**User checkpoint:** Signature, attachment, email send, and later fee approval.

**Official sources:**

- https://www.dentoncounty.gov/667/Development-Services
- https://www.dentoncounty.gov/DocumentCenter/View/10774/Public-Information-Request-Form-PDF

### 15. Brazoria County, Texas

**Custodian:** Brazoria County Environmental Health, OSSF-Septic Systems.

**Best route:** Email `ehinspector@brazoriacountytx.gov` for the existing OSSF file/planning materials. If the office requires a formal Texas Public Information Act request, continue through the county public-information route rather than sending the user to the new-permit process.

**Product adapter:**

- confirm city/ETJ/unincorporated authority;
- prepare an email using 911 address, parcel/account number, legal description, present/former owner, and known permit/installer dates;
- request application/permit, site evaluation, design/planning materials, authorization, final approval, maintenance, repair, and complaint records;
- ask for a written referral or no-record response;
- preserve the county’s reply and route to formal PIA only when requested.

**Published fee/time:** Not published for an OSSF record copy.

**User checkpoint:** Review and send. New OSSF applications may require mail or in-person intake, but that restriction must not be misapplied to an existing-record request.

**Fallback:** `ehadmin@brazoriacountytx.gov`, 979-864-1600, then the county public-information route.

**Official sources:**

- https://www.brazoriacountytx.gov/departments/environmental-health/contact-us
- https://www.brazoriacountytx.gov/departments/district-attorney/public-information-request
- https://www.brazoriacountytx.gov/home/showpublisheddocument/664/635914117233200000

## Product implementation order

### Wave 1 — direct completion improvement

1. Hamilton exact free-search deep link and street-name-only helper.
2. Alamance official PDF prefill and three-day tracker.
3. Knox live-form field pack with KGIS parcel preparation.
4. Lincoln exact NextRequest handoff and request-reference capture.
5. Blount GovQA Developmental Services field pack and request-reference tracker.
6. St. Mary’s PIA PDF/email route replacing the discontinued GIS promise.
7. Suffolk phone-first lookup pack using the county-required identifiers.
8. Denton PDF/email pack with cost-estimate state.

These are high-confidence, bounded integrations and cover 33 of the 70 cohort clicks.

### Wave 2 — jurisdiction and purpose routing

1. Prince William portal status handling plus VDH NextRequest fallback.
2. Tarrant authority resolver and county PIA draft.
3. DeKalb historical-file versus certification split.
4. Maricopa free-versus-paid flow and commercial-purpose handling.
5. Forsyth request-form field pack.
6. Brazoria authority confirmation and OSSF email/PIA escalation.

These cover 34 additional clicks.

### Wave 3 — metadata-to-document bridge

1. Keep Brunswick’s internal ArcGIS metadata lookup.
2. Add automatic candidate permit number/category/date to the Water Protection source-file request.
3. Make `metadata found` a middle state, never the completion state.

## Required analytics

Measure the acquisition funnel by county and mode:

- `property_case_started`
- `parcel_identifier_added`
- `official_search_opened`
- `request_packet_generated`
- `request_submitted_confirmed`
- `fee_estimate_received`
- `record_received`
- `written_no_record_received`
- `record_uploaded_or_forwarded`
- `record_analyzed`

The key product metric is not outbound click-through rate. It is **record-or-written-outcome rate per property case**. Secondary metrics are time to request readiness, submission confirmation rate, record return rate, and return-to-analysis rate.

## Verification policy

- Recheck action URLs monthly and whenever source-health reports a failure.
- Store `last human verified`, not only HTTP status.
- Treat CAPTCHA, login, signature, declaration, payment, and phone calls as explicit user checkpoints.
- Never convert portal blocking, an empty metadata result, or an unrelated clerk search into `no record`.
- Never use a new-permit application, current-condition inspection, or certification letter as a substitute for a historical property file.
