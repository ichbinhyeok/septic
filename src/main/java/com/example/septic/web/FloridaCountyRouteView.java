package com.example.septic.web;

import java.util.List;

public record FloridaCountyRouteView(
        String countyName,
        String countyKey,
        boolean depManaged,
        String recordsUrl,
        String recordsLabel,
        String recordsInstructions
) {
    public RecordRouteView toRecordRouteView() {
        boolean programUnverified = "hillsborough".equals(countyKey);
        return new RecordRouteView("FL", "FL::" + countyKey, countyName,
                depManaged ? "Florida DEP and county archive" : "County Environmental Public Health",
                programUnverified ? "county_request" : recordsUrl == null || recordsUrl.isBlank() ? "county_doh" : "verified_county_records",
                programUnverified ? "program_unverified" : "source_reviewed",
                programUnverified ? "" : recordsUrl, "https://www.floridahealth.gov/all-county-locations.html",
                List.of("Property address", "Parcel, owner, or permit number when available"),
                List.of("OSTDS construction permit", "site evaluation", "site plan", "final approval", "repair history"), "");
    }
}
