package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductionAnalyticsHostRegressionTest {

    @Test
    void ga4LoadsOnlyOnTheProductionHostname() throws IOException {
        String layout = Files.readString(Path.of("src/main/jte/layouts/app.jte"));

        assertTrue(layout.contains("window.location.hostname === \"septicpath.com\""));
        assertTrue(layout.contains("analyticsScript.src = \"https://www.googletagmanager.com/gtag/js?id=G-S1SY1NS71P\""));
        assertTrue(layout.contains("window.gtag(\"config\", \"G-S1SY1NS71P\")"));
        assertFalse(layout.contains("<script async src=\"https://www.googletagmanager.com/gtag/js?id=G-S1SY1NS71P\""));
    }
}
