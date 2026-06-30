package com.example.septic.web;

public record CountyAvailabilitySummaryView(
        String confidenceLabel,
        int confidenceScore,
        String confidenceNote,
        String primaryRouteLabel,
        String requestMethodLabel,
        String firstArtifactLabel,
        String sourceDepthLabel,
        String lastVerifiedAt
) {
}
