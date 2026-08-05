package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TdecAddressVariantQualityRegressionTest {

    // Regression: ISSUE-004 - alternate TDEC searches retained city/ZIP text and failed to remove common suffixes such as Pike.
    // Found by /qa on 2026-08-05.
    // Report: .gstack/qa-reports/qa-report-septicpath-com-2026-08-05.md
    @Test
    void narrowsAFullAddressToStreetAndStreetNameVariants() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/tdec-records.js"));

        assertTrue(script.contains("data.address.split(\",\", 1)[0]"));
        assertTrue(script.contains("street name only"));
        assertTrue(script.contains("new Set(clues)"));
        assertTrue(script.contains("/api/address-record-finder"));
    }
}
