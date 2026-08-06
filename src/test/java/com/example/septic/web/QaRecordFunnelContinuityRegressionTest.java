package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class QaRecordFunnelContinuityRegressionTest {

    // Regression: ISSUE-003 — completed address routes remained below the viewport and Texas county handoffs dropped the address.
    // Found by /qa on 2026-08-06.
    // Report: .gstack/qa-reports/qa-report-septicpath-human-intent-2026-08-06.md
    @Test
    void revealsTheCompletedRouteAndCarriesAnAddressIntoTexasCountyInstructions() throws IOException {
        String app = Files.readString(Path.of("src/main/resources/static/app.js"));
        String texas = Files.readString(Path.of("src/main/resources/static/texas-ossf-records.js"));
        int renderStart = app.indexOf("function render(payload)");
        int renderEnd = app.indexOf("function openDirectDocumentWorkspace", renderStart);
        String render = app.substring(renderStart, renderEnd);

        assertTrue(render.contains("result.scrollIntoView({ behavior: \"smooth\", block: \"start\" })"));
        assertTrue(render.contains("result.focus({ preventScroll: true })"));
        assertTrue(texas.contains("function countyInstructionsUrl(path)"));
        assertTrue(texas.contains("url.searchParams.set(\"address\",prepared.clue)"));
        assertTrue(texas.contains("countyInstructionsUrl(c.internalPath)"));
    }
}
