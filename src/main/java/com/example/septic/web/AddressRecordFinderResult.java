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
        String officeLabel,
        String contactLine,
        String routeReviewedAt,
        List<AddressRecordFinderAction> relayActions,
        List<String> relaySteps,
        String countyKey,
        String routeMode,
        String routeReliability,
        List<String> requiredIdentifiers,
        List<String> requestedDocuments,
        String requestRoute
) {
    public AddressRecordFinderResult(
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
            String officeLabel,
            String contactLine,
            String routeReviewedAt,
            List<AddressRecordFinderAction> relayActions,
            List<String> relaySteps
    ) {
        this(status, heading, message, stateCode, stateName, countyName, matchedAddress,
                routeTitle, routePath, officialRouteUrl, officeLabel, contactLine, routeReviewedAt,
                relayActions, relaySteps, "", "", "", List.of(), List.of(), "");
    }

    public AddressRecordFinderResult(
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
        this(
                status,
                heading,
                message,
                stateCode,
                stateName,
                countyName,
                matchedAddress,
                routeTitle,
                routePath,
                officialRouteUrl,
                "",
                "",
                "",
                relayActions,
                relaySteps
        );
    }
}
