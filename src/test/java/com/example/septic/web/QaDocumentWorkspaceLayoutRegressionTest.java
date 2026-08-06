package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class QaDocumentWorkspaceLayoutRegressionTest {

    // Regression: ISSUE-004 — opening a document kept the address-search surface around a narrow workspace.
    // Found by /qa on 2026-08-06.
    // Report: .gstack/qa-reports/qa-report-septicpath-human-intent-2026-08-06.md
    @Test
    void switchesDirectDocumentEntryToTheDedicatedWorkspacePresentation() throws IOException {
        String app = Files.readString(Path.of("src/main/resources/static/app.js"));
        String css = Files.readString(Path.of("src/main/resources/static/workflows.css"));

        assertTrue(app.contains("function openDirectDocumentWorkspace"));
        assertTrue(app.contains("finderRoot.classList.add(\"record-finder--document-mode\")"));
        assertTrue(css.contains(".record-finder--document-mode .record-finder__result > :not(.record-document) { display: none; }"));
        assertTrue(css.contains(".record-finder--document-mode .record-finder__start, .record-finder--document-mode .record-finder__form"));
    }
}
