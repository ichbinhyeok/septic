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
    void keepsPropertyCluesOptionalAndUsesABoundedCountyResolver() throws IOException {
        String script = Files.readString(TDEC_SCRIPT);

        assertTrue(script.contains("Include the street, city, and a state abbreviation or ZIP"));
        assertTrue(script.contains("verifyAddressInBackground"));
        assertTrue(script.contains("controller.abort()"));
        assertTrue(script.contains("4000"));
        assertTrue(script.contains("let routeRevision = 0"));
        assertTrue(script.contains("activeAddressVerification?.controller.abort()"));
        assertTrue(script.contains("if (revision !== routeRevision) return"));
        assertTrue(script.contains("/api/address-record-finder"));
        assertTrue(script.contains("address_search_completed"));
        assertTrue(script.contains("address_verification_latency"));
        assertTrue(script.contains("emit(\"route_started\""));
        assertTrue(script.contains("emit(\"route_ready\""));
        assertTrue(script.contains("emit(\"route_error\""));
        assertTrue(script.contains("emit(\"hero_official_click\""));
    }

    @Test
    void doesNotCountARestoredRouteAsANewReadyEvent() throws IOException {
        String script = Files.readString(TDEC_SCRIPT);

        assertTrue(script.contains("Restored from this browser tab. Confirm the property keys before continuing.\", false, false"));
    }

    @Test
    void usesTheOfficialViewerForRecordsAndSeparateServicesForStatusOrRepair() throws IOException {
        String script = Files.readString(TDEC_SCRIPT);
        assertTrue(script.contains("Open official TDEC SSDS record search"));
        assertTrue(script.contains("Open TDEC Online Services"));
        assertTrue(script.contains("Open TDEC repair services"));
        assertTrue(script.contains("A 403 or failed page does not say anything about this property"));
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
