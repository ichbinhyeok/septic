package com.example.septic.web;

import java.util.List;

public record AddressRecordFinderResult(
        String status,
        String heading,
        String message,
        String stateCode,
        String stateName,
        String countyName,
        String matchedAddress,
        String routeTitle,
        String routePath,
        String officialRouteUrl,
        List<AddressRecordFinderAction> relayActions,
        List<String> relaySteps
) {
}
