package com.example.septic.web;

public record SearchIntentOpportunityView(
        String anchorId,
        String eyebrow,
        String heading,
        String summary,
        String actionLabel,
        String actionPath,
        String targetType
) {
}
