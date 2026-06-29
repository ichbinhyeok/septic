package com.example.septic.web;

public record CountyIntentRouteView(
        String anchorId,
        String eyebrow,
        String heading,
        String summary,
        String primaryLabel,
        String primaryPath,
        String primaryTargetType,
        String secondaryLabel,
        String secondaryPath,
        String secondaryTargetType
) {
}
