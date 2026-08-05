package com.example.septic.web;

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
}
