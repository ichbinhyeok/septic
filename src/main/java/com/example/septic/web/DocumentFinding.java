package com.example.septic.web;

public record DocumentFinding(
        String key,
        String label,
        String value,
        String confidence,
        String evidence
) {
}
