package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TdecAnalyticsAttributionRegressionTest {

    // Regression: ISSUE-001 — TDEC address searches overwrote GA4 traffic-source attribution.
    // Found by /qa on 2026-08-27.
    // Report: .gstack/qa-reports/qa-report-septicpath-com-2026-08-27.md
    @Test
    void keepsTdecEventContextOutOfTheReservedSourceParameter() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/tdec-records.js"));

        assertTrue(script.contains("source_context: \"tdec_records_desk\""));
        assertFalse(script.contains("source: \"tdec_records_desk\""));
    }
}
