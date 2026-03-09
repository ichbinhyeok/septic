package com.example.septic.web;

public record NavigationClickForm(
        String sourcePage,
        String sourceContext,
        String targetPath,
        String targetType,
        String targetLabel
) {
}
