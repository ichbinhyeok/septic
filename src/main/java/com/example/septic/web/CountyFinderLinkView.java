package com.example.septic.web;

public record CountyFinderLinkView(
        String title,
        String path,
        String note,
        String stateCode,
        String stateName,
        String countyName,
        String confidenceLabel,
        int confidenceScore,
        String requestMethodLabel,
        String firstArtifactLabel,
        String sourceDepthLabel,
        boolean parcelAnchorAvailable,
        String searchText,
        String absoluteUrl
) {
}
