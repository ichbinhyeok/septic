package com.example.septic.web;

public record FloridaCountyRouteView(
        String countyName,
        String countyKey,
        boolean depManaged,
        String recordsUrl,
        String recordsLabel,
        String recordsInstructions
) {
}
