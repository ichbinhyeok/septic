package com.example.septic.web;

public record RecordsAccessIndexStateView(
        String stateCode,
        String stateName,
        String recordsPath,
        String packetPath,
        String packetLabel,
        String firstStep,
        int countyRouteCount,
        int directOnlineRouteCount,
        int highConfidenceRouteCount
) {
}
