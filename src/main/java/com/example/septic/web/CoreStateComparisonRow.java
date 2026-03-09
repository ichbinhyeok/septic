package com.example.septic.web;

public record CoreStateComparisonRow(
        String stateName,
        String stateSlug,
        String firstCall,
        String firstRecord,
        String lowEndTrigger,
        String nextPageTitle,
        String nextPagePath,
        boolean active
) {
}
