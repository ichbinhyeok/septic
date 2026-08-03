package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class QaRecordRoutePriorityRegressionTest {

    @Test
    void recordResultKeepsOnePrimaryActionAndPreservesFallbacksInDisclosure() throws IOException {
        // Regression: ISSUE-003 — a successful route displayed six parallel next actions.
        // Found by /qa on 2026-08-03.
        // Report: .gstack/qa-reports/qa-report-septicpath-com-2026-08-03.md
        String script = Files.readString(Path.of("src/main/resources/static/app.js"));

        assertTrue(script.contains("const primaryAction = nextActions.find"));
        assertTrue(script.contains("const secondaryActions = nextActions.filter"));
        assertTrue(script.contains("moreActions.className = \"record-finder__more-actions\""));
        assertTrue(script.contains("Other official routes and tools (${secondaryActions.length})"));
        assertTrue(script.contains("moreLinks.append(...secondaryActions)"));
        assertTrue(script.contains("actions.replaceChildren(primaryAction, moreActions)"));
    }
}
