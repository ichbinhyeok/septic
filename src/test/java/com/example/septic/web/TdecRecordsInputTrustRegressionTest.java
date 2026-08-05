package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TdecRecordsInputTrustRegressionTest {

    private static final Path TDEC_SCRIPT = Path.of("src/main/resources/static/tdec-records.js");
    private static final Path RETURN_SCRIPT = Path.of("src/main/resources/static/state-records-return.js");

    // Regression: ISSUE-001 — a digits-only fragment such as "123" was accepted as a usable street address.
    // Found by /qa on 2026-08-05.
    // Report: .gstack/qa-reports/qa-report-septicpath-com-2026-08-05.md
    @Test
    void rejectsWeakPropertyCluesAndStatesThatCountyMatchingIsNotVerified() throws IOException {
        String script = Files.readString(TDEC_SCRIPT);

        assertTrue(script.contains("A number by itself is not enough"));
        assertTrue(script.contains("invalid_${type}"));
        assertTrue(script.contains("did not verify that this address is inside"));
        assertTrue(script.contains("did not independently match this"));
    }

    @Test
    void leadsWithTheWorkingTdecEntryAndLabelsTheBlockedViewerAsOptional() throws IOException {
        String script = Files.readString(TDEC_SCRIPT);
        int workingPage = script.indexOf("Open the current TDEC SSDS page");
        int directViewer = script.indexOf("Try the direct record viewer (may return 403)");

        assertTrue(workingPage >= 0);
        assertTrue(directViewer > workingPage);
    }

    @Test
    void editingOrPreparingANewSearchClearsThePriorOfficialHandoffState() throws IOException {
        String tdecScript = Files.readString(TDEC_SCRIPT);
        String returnScript = Files.readString(RETURN_SCRIPT);

        assertTrue(tdecScript.contains("state-records-search-reset"));
        assertTrue(returnScript.contains("state-records-search-reset"));
        assertTrue(returnScript.contains("sessionStorage.removeItem(RETURN_KEY)"));
        assertTrue(returnScript.contains("panel.classList.remove(\"is-returning\")"));
    }
}
