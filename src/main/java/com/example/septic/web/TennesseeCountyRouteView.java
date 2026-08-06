package com.example.septic.web;

import java.util.List;

public record TennesseeCountyRouteView(
        String countyName,
        String countyKey,
        boolean contractCounty,
        String internalPath,
        String fieldOfficeName,
        String fieldOfficeUrl,
        String recordsUrl,
        String recordsLabel,
        String recordsHint
) {
    public boolean hasInternalPath() {
        return internalPath != null && !internalPath.isBlank();
    }

    public RecordRouteView toRecordRouteView() {
        return new RecordRouteView("TN", "TN::" + countyKey, countyName, contractCounty ? countyName : fieldOfficeName,
                contractCounty ? "contract_county" : "field_office_request",
                contractCounty ? "county_owned" : "viewer_secondary",
                contractCounty ? recordsUrl : fieldOfficeUrl, contractCounty ? internalPath : fieldOfficeUrl,
                List.of("Property address", "Parcel, prior owner, subdivision, or permit number when available"),
                List.of("Construction permit", "soil evaluation", "system layout", "final approval", "repair history"), "");
    }
}
