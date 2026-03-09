package com.example.septic.web;

public record StateRuleFactView(
        String label,
        String renderedValue,
        String note,
        String sourceAgency,
        String sourceTitle,
        String sourceUrl,
        String sourceSection
) {
}
