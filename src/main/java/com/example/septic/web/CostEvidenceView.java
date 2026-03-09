package com.example.septic.web;

public record CostEvidenceView(
        String title,
        String valueSummary,
        String note,
        String sourceSummary
) {
}
