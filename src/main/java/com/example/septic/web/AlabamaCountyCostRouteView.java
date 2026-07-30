package com.example.septic.web;

public record AlabamaCountyCostRouteView(
        String countyName,
        String demandSignal,
        String routeStatus,
        String officialPageUrl,
        String officialPageLabel,
        String phone,
        String formUrl,
        String formLabel,
        String prepSummary,
        String publicEvaluationStatus,
        String callQuestion,
        String countyRecordsPath
) {
    public boolean hasCountyRecordsPath() {
        return countyRecordsPath != null && !countyRecordsPath.isBlank();
    }
}
