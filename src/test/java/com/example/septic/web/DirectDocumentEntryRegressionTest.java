package com.example.septic.web;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class DirectDocumentEntryRegressionTest {

    // Regression: ISSUE-DOC-003 — document handoff retained the address-search hero and dropped "County".
    // Found by /qa on 2026-08-05.
    // Report: .gstack/qa-reports/qa-report-septicpath-com-document-workspace-2026-08-05.md
    @Test
    void directDocumentModeStartsInTheDocumentTaskAndPreservesCountyLanguage() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/app.js"));
        int setupStart = script.indexOf("function setupAddressRecordFinders()");
        int setupEnd = script.indexOf("setupAddressRecordFinders();", setupStart);
        String addressFinderSetup = script.substring(setupStart, setupEnd);

        assertTrue(addressFinderSetup.contains("const finderRoot = finder.closest(\".record-finder\")"));
        assertTrue(addressFinderSetup.contains("record-finder--document-mode"));
        assertTrue(addressFinderSetup.contains("Review the septic file you already found."));
        assertTrue(addressFinderSetup.contains("if (startPanel instanceof HTMLElement) startPanel.hidden = true"));
        assertTrue(addressFinderSetup.contains("if (routeSteps instanceof HTMLElement) routeSteps.hidden = true"));
        assertTrue(addressFinderSetup.contains("`${requestedCountyLabel} County`"));
    }
}
