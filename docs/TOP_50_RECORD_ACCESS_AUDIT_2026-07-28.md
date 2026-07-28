# Top 50 record-access audit — 2026-07-28

## Scope

- Selection: top 50 county landing pages in Google Search Console, last 90 days (2026-04-26 through 2026-07-25), sorted by clicks and then impressions.
- Question tested: can SepticPath obtain the user's septic record, or does the current route merely describe permits, applications, offices, or unrelated parcel data?
- Live endpoint check: 38 returned HTTP 200, 8 returned HTTP 403, and 4 could not be conclusively fetched.
- Existing source-health classification: 33 healthy, 11 blocked, 4 inconclusive, 2 not checked.
- Important limitation: HTTP 200 means only that a page loaded. It does not prove that a user can retrieve a septic record.

## Result

| Access mode | Count | Product meaning |
|---|---:|---|
| Confirmed public query/API candidate | 2 | SepticPath can query official public data, subject to field, license, privacy, and reliability review |
| Self-service portal candidate | 7 | User may be able to search, but SepticPath cannot yet safely retrieve the result itself |
| Request path | 20 | SepticPath should prepare, submit with user approval where permitted, track, and receive the response |
| Guidance/application/office only | 19 | Current route does not complete the existing-record job |
| Broken or materially wrong | 2 | Current promise should be removed until the route is replaced |

The two public query candidates are not equivalent:

1. **Hocking County, OH** is the strongest candidate. Its public ArcGIS Feature Service exposes septic-specific fields such as site address, permit number, tank size/type, system type, installation date, installer, and operation-permit dates.
2. **Brunswick County, NC** exposes an official public ArcGIS Feature Service with parcel address, parcel ID, permit number, category, status, description, and dates. It contains septic references, but appears to be a general permit index rather than a complete septic-document repository. It can improve triage and prefill, but may not replace a document request.

## Top 50 screening table

`API` means an official public query endpoint was confirmed. `Portal` means a user-facing search candidate, not a stable SepticPath integration. `Request` means a form/email/public-records flow. `Guidance` means the linked page is mainly an application, program, office, or instructions. This is a product-completion classification, not merely an uptime classification.

| # | County | GSC clicks / impressions | Current access mode | Main finding |
|---:|---|---:|---|---|
| 1 | Prince William, VA | 14 / 198 | Portal | Laserfiche septic-document portal; automated access currently returns 403 |
| 2 | Tarrant, TX | 7 / 92 | Portal | PublicSearch self-service UI; no documented public API confirmed |
| 3 | Hamilton, TN | 6 / 261 | Guidance | Septic information/forms page, not a confirmed existing-record result |
| 4 | Alamance, NC | 5 / 157 | Guidance | New septic application PDF, not existing-record retrieval |
| 5 | Knox, TN | 5 / 60 | Request | Groundwater office/file-search route |
| 6 | Lincoln, NC | 5 / 34 | Request | Onsite-water page plus request/records path; not direct retrieval |
| 7 | DeKalb, GA | 4 / 71 | Broken | Current record URL resolves to an unrelated commercial domain |
| 8 | Blount, TN | 3 / 140 | Request | SSDS request form |
| 9 | St. Mary's, MD | 3 / 107 | Broken | Health page links to an ArcGIS app now titled “Discontinued GIS Map” |
| 10 | Suffolk, NY | 3 / 80 | Request | FAQ/instructions route; current endpoint is also access-blocked |
| 11 | Maricopa, AZ | 3 / 58 | Portal | Free online search plus paid request fallback; CAPTCHA/form constraints |
| 12 | Brunswick, NC | 3 / 54 | API | Public ArcGIS permit Feature Service; useful metadata, incomplete document coverage likely |
| 13 | Forsyth, NC | 3 / 54 | Request | Owner guide/permit-copy instructions, not a direct result |
| 14 | Denton, TX | 3 / 39 | Guidance | Septic permit application packet |
| 15 | Brazoria, TX | 3 / 12 | Guidance | OSSF permitting process, not record retrieval |
| 16 | San Bernardino, CA | 2 / 112 | Guidance | Parcel-number research guidance only |
| 17 | Harford, MD | 2 / 72 | Guidance | Building-with-well-and-septic guide |
| 18 | Gloucester, NJ | 2 / 64 | Request | OPRA request route |
| 19 | Prince George's, MD | 2 / 57 | Guidance | Sewage-system licensing/permit page; no direct result confirmed |
| 20 | Adams, CO | 2 / 49 | Guidance | OWTS use-permit application |
| 21 | Genesee, NY | 2 / 48 | Guidance | New construction permit application |
| 22 | Wilson, TN | 2 / 48 | Request | TDEC field-office route; no direct county lookup |
| 23 | Mahoning, OH | 2 / 47 | Request | Public-records policy/request route |
| 24 | Sevier, TN | 2 / 47 | Request | Environmental Health contact/service route; endpoint returns 403 in automated check |
| 25 | Montgomery, TN | 2 / 43 | Request | TDEC field-office route |
| 26 | Guilford, NC | 2 / 41 | Portal | Accela/records and request tooling exists, but direct anonymous retrieval is unconfirmed |
| 27 | St. Croix, WI | 2 / 35 | Portal | Ascent self-service property/sanitary portal candidate; fetch inconclusive |
| 28 | Coconino, AZ | 2 / 26 | Portal | Online wastewater/application portal; endpoint currently returns 403 |
| 29 | Wyoming, NY | 2 / 26 | Request | Environmental Health Form Center |
| 30 | Sullivan, TN | 2 / 25 | Request | TDEC field-office/public-record route |
| 31 | Jefferson, TN | 2 / 24 | Guidance | Environmental Health/septic guidance; no direct records result |
| 32 | Delaware, OH | 2 / 16 | Request | Public-records request path |
| 33 | Cole, MO | 2 / 15 | Guidance | Onsite wastewater permit information |
| 34 | Tioga, NY | 2 / 14 | Guidance | Real-property viewer, not confirmed septic records |
| 35 | Somerset, MD | 2 / 13 | Guidance | New septic application |
| 36 | Worcester, MD | 2 / 12 | Request | Environmental Programs permits/contact routing |
| 37 | Hocking, OH | 2 / 12 | API | Public septic-specific ArcGIS Feature Service; strongest direct integration candidate |
| 38 | Albany, NY | 2 / 10 | Guidance | Septic replacement program, not existing-record lookup |
| 39 | Anderson, SC | 2 / 10 | Guidance | State “how to locate” instructions; endpoint returns 403 in automated check |
| 40 | Iron, UT | 2 / 9 | Guidance | EagleWeb property records, not confirmed septic records |
| 41 | Cape May, NJ | 2 / 8 | Request | Government-records/OPRA route |
| 42 | Forsyth, GA | 1 / 120 | Guidance | Sewage disposal permits/evaluations information |
| 43 | Hall, GA | 1 / 77 | Request | Existing-system evaluation/record assistance route |
| 44 | Tuolumne, CA | 1 / 52 | Guidance | Onsite wastewater permitting process; endpoint returns 403 |
| 45 | McHenry, IL | 1 / 38 | Portal | Electronic permit-record search; endpoint currently returns 403 |
| 46 | Yavapai, AZ | 1 / 19 | Request | Wastewater permit/site-investigation request |
| 47 | Lake, IL | 1 / 14 | Guidance | Well/septic evaluation information; endpoint currently returns 403 |
| 48 | El Dorado, CA | 1 / 12 | Request | Parcel research request PDF |
| 49 | Trinity, CA | 1 / 12 | Request | Parcel records request instructions |
| 50 | Santa Clara, CA | 1 / 7 | Request | Septic/OWTS as-built request guidance; endpoint currently returns 403 |

## Recommended product hierarchy

1. **Retrieve inside SepticPath**
   - Use a documented public API or officially public query endpoint.
   - Normalize the result, show source and retrieval time, and let the user save the record to the case.
   - If the endpoint provides metadata but not the document, use it to prefill the next request rather than pretending the job is finished.

2. **Assisted request**
   - SepticPath identifies the custodian, selects the correct form, prefills property/requester fields, creates the jurisdiction-specific wording, obtains explicit user approval, submits where permitted, tracks deadlines/fees, and ingests the reply.
   - Prefer submission in the user's identity. A SepticPath proxy requester should be enabled only after jurisdiction and portal terms are reviewed.
   - CAPTCHA, login, signature, proof of residency/citizenship, fee authorization, and declarations of purpose remain user checkpoints.

3. **Action-ready handoff**
   - Use only when neither integration nor assisted submission is feasible.
   - Send the user to the exact working action page with address, parcel ID, owner name, date range, record type, exact fields/clicks, expected output, and a fallback already prepared.
   - Preserve the SepticPath case before departure and provide a return link/upload-by-email path so the workflow does not reset.

## Product implications

- “Healthy link” and “record obtainable” must be separate states.
- County data needs explicit fields for access mode, verified action URL, required inputs, output type, fee, turnaround, user checkpoint, fallback, and last human verification.
- Search-oriented copy should be shown only for `API` or proven `Portal` routes.
- `Request` pages should promise “prepare and track a request,” not “search records.”
- `Guidance` pages should not be presented as record lookup.
- Broken routes should fall back inside SepticPath before opening an external site.

## Evidence notes

- Brunswick County’s official permit page states that live and archived permit data are on its ArcGIS Open Data site and can be downloaded.
- Hocking County states that its ArcGIS application contains private-water and sewage-treatment permit information and that pre-2017 coverage is incomplete.
- St. Mary’s Health Department describes an address/Tax ID GIS workflow and an email fallback, but the linked ArcGIS application metadata currently marks the app discontinued.
- Maricopa offers both a free online search and a paid department search ($30 standard, $60 expedited), while its public form uses reCAPTCHA.
- ArcGIS Feature Service layers support an official REST `/query` operation.
- Accela exposes a REST API, but use generally requires app registration and authentication; the presence of an Accela portal is not by itself permission to ingest an agency’s data.
- Tennessee has no central public-record repository, grants the enforceable access right to Tennessee citizens, and agencies may require identification. This makes user-identity request submission materially safer than a blanket SepticPath proxy.
- Arizona requires disclosure of a commercial purpose and permits different charges for commercial use, so automated proxy requests need state-specific purpose handling.

