package com.example.septic.web;

public record CountyAvailabilityRowView(
        String artifactLabel,
        String availabilityLabel,
        String methodLabel,
        String evidenceNote,
        String actionLabel,
        String actionUrl,
        String targetType,
        boolean external,
        String statusTone
) {
}
