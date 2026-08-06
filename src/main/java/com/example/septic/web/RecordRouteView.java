package com.example.septic.web;

import java.util.List;

/** Shared, property-free contract consumed by record workflow UIs. */
public record RecordRouteView(
        String stateCode,
        String countyKey,
        String countyName,
        String officeLabel,
        String routeMode,
        String routeReliability,
        String officialRoute,
        String requestRoute,
        List<String> requiredIdentifiers,
        List<String> requestedDocuments,
        String reviewedAt
) {
}
