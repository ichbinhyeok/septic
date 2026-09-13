# Online Record Route Discovery Checklist

Last updated: 2026-09-12

## Why this exists

SepticPath once treated an inaccessible TDEC viewer as evidence that no useful
online route existed. Later work found current program-specific viewers,
ArcGIS items, and a downloadable ArcGIS Hub dataset for Tennessee water wells.

This checklist prevents a blocked front end, weak site search, or missing link
from being mistaken for exhaustion of the public data surface.

## Language boundary

Use these statements precisely:

- `viewer_blocked`: a specific front end failed, returned 403, required an
  unsupported login, or otherwise could not be used.
- `no_match_in_source`: a working source returned no match for the identifiers
  searched; state the source scope and search keys.
- `no_working_route_confirmed_yet`: required discovery checks are incomplete or
  only broken candidates have been found.
- `public_path_exhausted`: all required discovery classes below were checked,
  their scope was evaluated, and no property-matched source document or usable
  fallback remained.
- `official_no_match`: the custodian searched the supplied identifiers and
  reported no match. This is still not proof that no system or well ever
  existed.

Never say `there is no online route` merely because one official viewer failed.

## Required discovery classes

Complete every applicable class before marking `public_path_exhausted`.

### 1. Program page and current links

- Open the current state/county program page rather than relying on an old
  indexed viewer URL.
- Follow every relevant `records`, `data`, `map`, `documents`, `permit search`,
  `site explorer`, `reports`, and `online services` link.
- Check page update dates and whether a reorganization notice points to a new
  surface.

### 2. State and county data-viewer directories

- Search the agency-wide data-viewer catalog, open-data catalog, and public
  records directory.
- Search both the environmental program name and the actual record vocabulary:
  `SSDS`, `onsite wastewater`, `septic`, `groundwater protection`, `water well`,
  `driller report`, `permit`, `completion`, and `site plan`.
- Do not assume septic and private-well data use the same application.

### 3. GIS and parcel surfaces

- Check state GIS, county GIS, assessor maps, parcel viewers, and recorded-plat
  layers.
- Look for document attachments, permit popups, layer tables, related records,
  and external links, not just visible map symbols.
- Confirm geographic coverage, date coverage, and whether the result is a parcel
  fact, permit metadata, or the actual source document.

### 4. ArcGIS discovery

- Search ArcGIS Online and ArcGIS Hub using the agency name, program name,
  record terminology, and known agency owner names.
- Inspect public web maps, feature services, map services, item IDs, and Hub
  dataset downloads separately.
- When the application UI fails, test whether the official item metadata,
  FeatureServer/MapServer layer, query endpoint, CSV, GeoJSON, or file download
  remains public.
- Verify that a discovered layer covers the correct program and geography. A
  regional pending-application map is not a statewide historical record source.

### 5. Non-GIS record platforms

Check for agency or county deployments of:

- Laserfiche or FileNet;
- GovQA or NextRequest;
- OpenGov, Accela, EnerGov, Citizen Access, or SmartGov;
- ePermitting, nSITE, or program-specific site explorers;
- downloadable permit reports, monthly data, and public document libraries.

Determine whether each surface provides a search, metadata, a full document, a
formal request intake, or only a new-work application.

### 6. Identifier expansion

Search using:

- exact and normalized street address;
- road-name-only and street suffix variants;
- parcel ID with and without spaces, punctuation, or leading zeroes;
- current and former owner;
- subdivision, lot, legal description, book/page, or permit number;
- legacy structure, mobile-home, trailer, or tax identifiers;
- approximate construction or installation year.

A source is not exhausted if it was tested with only one address string and the
database is known to contain incomplete historical addresses.

### 7. Search-engine and document discovery

- Search the exact address, parcel ID, owner, permit number, and agency record
  vocabulary with official-domain restrictions.
- Search PDF, spreadsheet, GIS-service, and archived agency document results.
- Treat search snippets and third-party results as discovery clues, not source
  truth.

### 8. Browser and network behavior

- Re-test a candidate in a real browser before classifying it as broken.
- Distinguish a login requirement, bot challenge, regional block, retired path,
  missing record, and server error.
- Record the exact URL, date, error, and whether an underlying data endpoint was
  tested.
- Do not repeatedly retry the same 403 after the fallback classes have been
  checked.

### 9. Agency confirmation

When discovery still does not resolve the route, ask the program a narrow
routing question:

> Is there a current public viewer, GIS layer, downloadable dataset, or portal
> for this record type before we submit a manual records request?

An agency reply that identifies a new viewer must update the route registry and
the correction ledger, not remain only in Gmail.

## Exhaustion record

Before using `public_path_exhausted`, retain this anonymized evidence:

```md
- Program page checked:
- Data-viewer/open-data directory checked:
- County/assessor GIS checked:
- ArcGIS Online/Hub search terms and candidates:
- Underlying service/download endpoints tested:
- Non-GIS permit/document platforms checked:
- Identifiers and variants searched:
- Browser/access result:
- Source scope or geographic limitation:
- Agency routing confirmation, if any:
- Final classification:
```

## Corrected example — Tennessee water wells

### Premature conclusion

The older/direct TDEC viewer and service paths returned 403, leading to an
overly broad conclusion that the well data could not be obtained online.

### What later discovery found

- a current TDEC water-well map viewer;
- a current TDEC tabular viewer;
- a public TDEC ArcGIS item;
- an ArcGIS Hub CSV download that remained available when direct TDEC services
  returned 403;
- individual Driller Report links supplied by the Water Well Program.

### Permanent lesson

A failed agency front end does not imply failure of the program-specific data
surface. Search the agency catalog, ArcGIS organization/Hub, underlying service,
and download endpoints before moving the case to manual-only.

This correction applies to Tennessee **water-well** research. It does not prove
that a working equivalent exists for TDEC SSDS/septic records. The septic ArcGIS
candidate found during research was an old/test service and also returned 403,
so Tennessee septic cases still require the records/field-office fallback when
the current public SSDS surfaces are inaccessible.

## Completion gate

An operator may contact an agency before every discovery class is complete when
there is a customer deadline, a mandatory agency-only process, or a verified
records request is clearly required. In that situation, continue online
discovery in parallel and describe the state as `agency request pending`, not
`online research exhausted`.
