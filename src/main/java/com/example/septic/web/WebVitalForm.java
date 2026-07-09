package com.example.septic.web;

public record WebVitalForm(
        String metricName,
        Double value,
        String rating,
        String sourcePage,
        String navigationType
) {
}
