package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class QaMissingOutcomeRegressionTest {

    @Test
    void startingFromAFailedSearchCollapsesTheAlreadyRecordedOutcome() throws IOException {
        // Regression: ISSUE-001 — failed-search mode asked "What happened?" twice.
        // Found by /qa on 2026-08-03.
        // Report: .gstack/qa-reports/qa-report-septicpath-com-2026-08-03.md
        String finder = Files.readString(Path.of("src/main/jte/tags/addressRecordFinder.jte"));
        String script = Files.readString(Path.of("src/main/resources/static/app.js"));
        String styles = Files.readString(Path.of("src/main/resources/static/workflows.css"));

        assertTrue(finder.contains("data-record-change-outcome"));
        assertTrue(script.contains("returnPanel.classList.add(\"record-finder__return--resolved\")"));
        assertTrue(script.contains("returnHeading.textContent = \"Result recorded\""));
        assertTrue(script.contains("returnPanel.classList.remove(\"record-finder__return--resolved\")"));
        assertTrue(styles.contains("button:not([aria-pressed=\"true\"]) { display: none; }"));
    }
}
