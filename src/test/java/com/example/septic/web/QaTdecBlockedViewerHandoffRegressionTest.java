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
    void makesTheDirectRequestPrimaryAndKeepsTheViewerOptional() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/tdec-records.js"));

        assertTrue(script.contains("title: `Prepare a direct ${countyData.name} records request`"));
        assertTrue(script.contains("url: countyData.fieldOfficeUrl"));
        assertTrue(script.contains("context: \"tdec_request_preparer\""));
        assertTrue(script.contains("action: \"prepare_request\""));
        assertTrue(script.contains("record_request_prepared"));
        assertTrue(script.contains("record_request_channel_selected"));
        assertTrue(script.contains("https://mail.google.com/mail/"));
        assertTrue(script.contains("https://outlook.office.com/mail/deeplink/compose"));
        assertTrue(script.contains("mailto:${recipient}"));
        assertTrue(script.contains("Try the official TDEC online viewer"));
        assertTrue(script.contains("Verify the current viewer on TN.gov"));
        assertTrue(script.contains("https://dataviewers.tdec.tn.gov/dataviewers/f?p=175"));
        assertTrue(script.contains("https://www.tn.gov/environment/about-tdec/tdec-dataviewers.html"));
    }
}
