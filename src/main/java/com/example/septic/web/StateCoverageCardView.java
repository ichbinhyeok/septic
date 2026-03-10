package com.example.septic.web;

public record StateCoverageCardView(
        String stateCode,
        String stateName,
        boolean published,
        String statusLabel,
        String statusTone,
        String summary,
        String metaLine,
        String actionPath,
        String actionLabel
) {
}
