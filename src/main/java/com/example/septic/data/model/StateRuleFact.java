package com.example.septic.data.model;

public record StateRuleFact(
        String stateCode,
        String factType,
        String label,
        String value,
        String unit,
        String sourceId,
        String sourceSection,
        String effectiveDate,
        String lastVerifiedAt,
        Double confidence,
        String note
) {
    public String renderedValue() {
        if (unit == null || unit.isBlank()) {
            return value;
        }
        return value + " " + unit;
    }
}
