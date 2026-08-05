package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TdecRecordsInputTrustRegressionTest {

    private static final Path TDEC_SCRIPT = Path.of("src/main/resources/static/tdec-records.js");

    // Regression: ISSUE-001 — a digits-only fragment such as "123" was accepted as a usable street address.
    // Found by /qa on 2026-08-05.
    // Report: .gstack/qa-reports/qa-report-septicpath-com-2026-08-05.md
    @Test
    void rejectsWeakPropertyCluesAndUsesTheCensusCountyResolver() throws IOException {
        String script = Files.readString(TDEC_SCRIPT);

        assertTrue(script.contains("Include the street, city, and a state abbreviation or ZIP"));
        assertTrue(script.contains("missing_property_clue"));
        assertTrue(script.contains("/api/address-record-finder"));
        assertTrue(script.contains("address_search_completed"));
    }

    @Test
    void usesTheOfficialViewerForRecordsAndSeparateServicesForStatusOrRepair() throws IOException {
        String script = Files.readString(TDEC_SCRIPT);
        assertTrue(script.contains("Open official SSDS Record Search"));
        assertTrue(script.contains("Open TDEC Online Services"));
        assertTrue(script.contains("Open TDEC repair services"));
        assertTrue(script.contains("A 403 is an access failure"));
    }

    @Test
    void keepsOnlyTabScopedHandoffStateAndRestoresTheOutcomePrompt() throws IOException {
        String tdecScript = Files.readString(TDEC_SCRIPT);
        assertTrue(tdecScript.contains("sessionStorage.setItem(SESSION_KEY"));
        assertTrue(tdecScript.contains("window.addEventListener(\"focus\""));
        assertTrue(tdecScript.contains("returnPanel.hidden = false"));
        assertTrue(tdecScript.contains("restoreForm(restoredData)"));
        assertTrue(!tdecScript.contains("localStorage"));
    }
}
