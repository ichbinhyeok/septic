package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class QaDocumentReviewDensityRegressionTest {

    @Test
    void completedDocumentAnalysisCollapsesSourceInputWithoutRemovingIt() throws IOException {
        // Regression: ISSUE-002 — mobile document results left every source control expanded.
        // Found by /qa on 2026-08-03.
        // Report: .gstack/qa-reports/qa-report-septicpath-com-2026-08-03.md
        String finder = Files.readString(Path.of("src/main/jte/tags/addressRecordFinder.jte"));
        String script = Files.readString(Path.of("src/main/resources/static/app.js"));

        assertTrue(finder.contains("data-record-document-add-source open"));
        assertTrue(finder.contains("data-record-document-add-source-label>Add the first record"));
        assertTrue(script.contains("documentAddSource.open = false"));
        assertTrue(script.contains("documentAddSourceLabel.textContent = \"Add another source\""));
        assertTrue(script.contains("documentAddSource.open = true"));
        assertTrue(script.contains("documentAddSourceLabel.textContent = \"Add the first record\""));
    }
}
