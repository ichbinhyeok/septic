package com.example.septic.web;

import java.util.List;

public record SouthCarolinaCountyRouteView(
        String countyName,
        String countyKey,
        String internalPath
) {
    public RecordRouteView toRecordRouteView() {
        return new RecordRouteView("SC", "SC::" + countyKey, countyName, "SCDES Onsite Wastewater",
                "identifier_or_request", "identifier_required", internalPath, internalPath,
                List.of("TMS / tax map number or permit number"),
                List.of("Permit to Construct", "soil evaluation", "approved layout", "DES 4432 final inspection", "repair history"), "");
    }
}
