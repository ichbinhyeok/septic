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
    void makesTheResponsibleFieldOfficeTheDefaultRecordHandoff() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/tdec-records.js"));

        assertTrue(script.contains("title: `Request the ${countyData.fieldOfficeName} file search`"));
        assertTrue(script.contains("url: countyData.fieldOfficeUrl"));
        assertTrue(script.contains("context: \"tdec_field_office_request\""));
        assertTrue(script.contains("Open official SSDS Record Search (may be blocked)"));
    }
}
