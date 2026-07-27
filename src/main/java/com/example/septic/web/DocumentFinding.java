package com.example.septic.web;

public record DocumentFinding(
        String key,
        String label,
        String value,
        String confidence,
        String evidence,
        Integer pageNumber
) {
    public DocumentFinding(
            String key,
            String label,
            String value,
            String confidence,
            String evidence
    ) {
        this(key, label, value, confidence, evidence, null);
    }
}
