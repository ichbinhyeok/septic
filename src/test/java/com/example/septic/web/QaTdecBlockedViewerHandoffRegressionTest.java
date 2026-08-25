package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class QaTdecBlockedViewerHandoffRegressionTest {

    // Regression: ISSUE-002 — the default Tennessee record action opened a reproducibly blocked viewer.
    // Found by /qa on 2026-08-06.
    // Report: .gstack/qa-reports/qa-report-septicpath-human-intent-2026-08-06.md
    @Test
    void makesTheOfficialViewerPrimaryAndKeepsTheFieldOfficeFallback() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/tdec-records.js"));

        assertTrue(script.contains("title: \"Search the official TDEC SSDS records\""));
        assertTrue(script.contains("url: TDEC_VIEWER"));
        assertTrue(script.contains("context: \"tdec_ssds_record_search\""));
        assertTrue(script.contains("Contact ${data.county.fieldOfficeName} if no file appears"));
        assertTrue(script.contains("\"tdec_field_office_request\", data"));
    }
}
