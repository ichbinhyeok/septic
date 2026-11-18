package com.example.septic.web;

import java.util.stream.Collectors;

public record CountyRecordStartView(String officialUrl, String officialLabel, String identifiers, String note) {
    static CountyRecordStartView from(CountyAccessProfileView access, CountySearchGuideView guide) {
        return switch (access.countyKey()) {
            case "OH::hamilton-county" -> new CountyRecordStartView(
                    "https://cagisportal.maps.arcgis.com/apps/webappviewer/index.html?id=7ee4bccae951450fb43a0c8c9f597902",
                    "Open the inspection map",
                    "Search by street address, then select the system marker.",
                    "The map excludes Cincinnati, Norwood and Springdale."
            );
            case "IN::tippecanoe-county" -> new CountyRecordStartView(
                    "https://www.tippecanoe.in.gov/569/Sewage-Disposal",
                    "Contact the Health Department for a diagram",
                    "Site address and any parcel or permit number.",
                    ""
            );
            default -> new CountyRecordStartView(
                    access.primaryUrl(),
                    access.countySpecific() ? access.primaryLabel() : "Open the official records route",
                    identifiers(access, guide),
                    ""
            );
        };
    }

    private static String identifiers(CountyAccessProfileView access, CountySearchGuideView guide) {
        if (guide != null && !guide.guide().identifiers().isBlank()) {
            return guide.guide().identifiers();
        }
        if (!access.countySpecific() || access.requiredInputs().isEmpty()) {
            return "Property address and, if available, the parcel number.";
        }
        return access.requiredInputs().stream().limit(2).collect(Collectors.joining(", "));
    }

    public boolean opensNewWindow() {
        return officialUrl.startsWith("http");
    }
}
