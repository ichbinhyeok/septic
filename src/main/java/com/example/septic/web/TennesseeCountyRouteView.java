package com.example.septic.web;

public record TennesseeCountyRouteView(
        String countyName,
        String countyKey,
        boolean contractCounty,
        String internalPath
) {
    public boolean hasInternalPath() {
        return internalPath != null && !internalPath.isBlank();
    }
}
