package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TdecRecordsReturnResetRegressionTest {

    // Regression: ISSUE-002 — a saved official-source state survived while the visitor typed a new or invalid search.
    // Found by /qa on 2026-08-05.
    // Report: .gstack/qa-reports/qa-report-septicpath-com-2026-08-05.md
    @Test
    void changingAnySearchInputClearsThePriorOfficialHandoffState() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/tdec-records.js"));

        assertTrue(script.contains("form?.addEventListener(\"input\", resetReturnState)"));
        assertTrue(script.contains("form?.addEventListener(\"change\", resetReturnState)"));
    }
}
