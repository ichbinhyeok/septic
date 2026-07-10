package com.example.septic.web;

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
        String officialRouteUrl
) {
}
