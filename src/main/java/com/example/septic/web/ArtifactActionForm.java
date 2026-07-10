package com.example.septic.web;

public record ArtifactActionForm(
        String sourcePage,
        String sourceContext,
        String action,
        String artifactType
) {
}
