package com.example.septic.web;

public record CoverageStateRowView(
        String stateName,
        String stateCode,
        String statePath,
        int workflowPageCount,
        int countyPageCount,
        String sourceCountLabel,
        String confidenceLabel,
        String lastVerifiedAt
) {
}
