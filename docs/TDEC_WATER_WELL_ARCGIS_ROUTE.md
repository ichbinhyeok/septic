# TDEC Water-Well ArcGIS Route

Last verified: 2026-09-12

## Scope

This route is for Tennessee **water-well records**, not Tennessee septic/SSDS
permit files. Water-well research is an adjacent due-diligence service inside
SepticPath; the product's primary subject remains septic records and septic
system due diligence.

The mistake that led to discovery of this fallback, and the required checks
before any future `online route exhausted` conclusion, are documented in
`docs/ONLINE_ROUTE_DISCOVERY_CHECKLIST.md`.

## Current official first-look routes

Use these two links first. They were supplied on 2026-09-12 by the TDEC Water
Well Program Coordinator in response to an actual address-level record search:

- Current map viewer: `https://tdeconline.tn.gov/tdecwaterwells/`
- Current tabular viewer:
  `https://dataviewers.tdec.tn.gov/dataviewers/f?p=2005:39929:`

These are different from the older `f?p=2005:39900` table route and the long
ArcGIS Web App URL previously used. Do not assume an error on the old viewer
means that no public search route exists.

## Bulk-data and fallback routes

- TDEC ArcGIS item ID: `3b59f1b3cf38441297fd9252243c9295`
- Dataset: `Tennessee Water Wells`
- CSV download:
  `https://hub.arcgis.com/api/v3/datasets/3b59f1b3cf38441297fd9252243c9295_0/downloads/data?format=csv&spatialRefId=4326`
- TDEC public map application:
  `https://tdec.maps.arcgis.com/apps/webappviewer/index.html?id=051a9e7834434574b0c1c76d38e2cd9a`
- Direct TDEC feature service:
  `https://tdeconline.tn.gov/arcgis/rest/services/WLTS_Well_Locations_PUBLIC/FeatureServer/0`

The ArcGIS Hub CSV remained downloadable when the direct TDEC viewer, detail
pages, and feature service returned HTTP 403. Treat the Hub URL as the working
bulk-data fallback, not as access to every TDEC or SSDS dataset.

## Useful fields

The export includes well number, driller tag, completion date, depth, estimated
yield, casing, county, owner, location text, driller license, well use, and
coordinate fields when public values exist.

## Required limitations

- Exact well locations may be redacted from public records. Blank `X`, `Y`,
  `LATITUDE_DD`, and `LONGITUDE_DD` values can therefore be intentional rather
  than a download failure.
- Do not use approximate public well locations as the final basis for a
  property-line, building, or septic-system decision.
- When coordinates are absent, narrow candidates using county, address, owner,
  dates, and well attributes, then ask TDEC to evaluate the parcel or inspect
  the underlying driller report/location sketch.
- The individual report viewer may still be unavailable even while the bulk
  CSV works.
- The Water Well Program does not track parcel IDs in its well database. A
  parcel number alone is not a valid negative search; use address variants,
  former addresses, and former-owner names when known.
- A database search can confirm that no record matches the submitted fields.
  It cannot prove that a parcel has never contained a well.
- Private-well results do not determine whether public water service is
  available or connected. Verify that separately with the relevant utility.
- TDEC's public-record form allows a requester to select that they are not a
  Tennessee citizen, but its required phone field accepts only U.S.-format
  numbers. TDEC advised that it is not obligated to answer non-Tennessee
  requesters under Rule 0400-01-01-.01(4)(a)(1)(i). Never invent a U.S. phone
  number or claim Tennessee citizenship. Use the public viewers first; if a
  formal request is still necessary, ask TDEC for an alternate intake route or
  have the Tennessee requester submit it directly.

## Operational workflow

1. Search the bulk export using the street name, county, owner names, and any
   known well or driller tag.
2. Exclude records with a different county or explicit different street number.
3. If usable coordinates exist, compare them with the official parcel geometry
   while retaining an accuracy warning.
4. If coordinates are redacted, send TDEC only the short candidate list and ask
   for an area-of-interest or underlying-record check.
5. Explain the result to the requester as a researched record brief; do not
   present an unconfirmed candidate as the subject property's well.

Use the current official map and tabular viewers before the bulk export. The
bulk export remains the fallback for repeatable searches, downloads, or when a
viewer is inaccessible.

## Verified example

For parcel `077 00820 000` at 230 Comb Ridge Rd, the bulk export exposed six
street-name candidates. One Grainger County record and the record explicitly
tied to 350 Comb Ridge Rd could be excluded. The remaining four records had no
usable public coordinates, so TDEC was asked to check their underlying reports
for a location sketch, parcel reference, or former address.

For `17615 Highway 194`, Somerville, Fayette County (parcel `065 030.00`), the
TDEC Water Well Program searched both `17615 Highway 194` and `17615 Hwy 194`.
It reported no matching Notice of Intent, completed or abandoned well, driller
report, well tag, or water-well application. This is an address-field no-match,
not proof that no well has ever existed. TDEC could not search by parcel ID and
could not search unknown former addresses or former owners. Public-water
service verification remained a separate utility inquiry.
