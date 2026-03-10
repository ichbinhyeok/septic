package com.example.septic.web;

public record StateRuleFactView(
        String label,
        String renderedValue,
        String note,
        String effectiveDate,
        String lastVerifiedAt,
        String confidenceLabel,
        String sourceAgency,
        String sourceTitle,
        String sourceUrl,
        String sourceSection,
        String sourceTrustLevel,
        String sourceStatus
) {
}
