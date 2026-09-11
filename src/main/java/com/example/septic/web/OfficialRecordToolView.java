package com.example.septic.web;

public record OfficialRecordToolView(
        String stateCode,
        String stateName,
        String countyName,
        String countyPath,
        String toolType,
        String toolTypeLabel,
        String officialLabel,
        String officialUrl,
        String lookupClues,
        String firstArtifactLabel,
        boolean parcelClueAvailable,
        boolean documentClueAvailable,
        String searchText
) {
}
