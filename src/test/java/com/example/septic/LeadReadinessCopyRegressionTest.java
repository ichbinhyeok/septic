package com.example.septic;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeadReadinessCopyRegressionTest {

    // Regression: ISSUE-002 — state cost pages promised a local professional match without live provider inventory.
    // Found by /qa on 2026-08-02
    // Report: .gstack/qa-reports/qa-report-septicpath-com-2026-08-02.md
    @Test
    void stateCostPagesPrepareARequestWithoutPromisingAProviderMatch() throws IOException {
        String template = Files.readString(Path.of("src/main/jte/pages/state-money-page.jte"));

        assertFalse(template.contains("Get matched with local septic pros"));
        assertTrue(template.contains("Prepare a scoped quote request"));
        assertTrue(template.contains("data-track-target-type=\"quote_form\""));
    }
}
