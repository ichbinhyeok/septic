package com.example.septic.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class NoRecordWorkspaceUiRegressionTest {

    // Regression: ISSUE-DOC-002 — the browser workspace treated official negative evidence as an incomplete upload.
    // Found by /qa on 2026-08-05.
    // Report: .gstack/qa-reports/qa-report-septicpath-com-document-workspace-2026-08-05.md
    @Test
    void officialNoRecordOutcomeStopsTheRepeatRequestLoop() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/app.js"));
        int renderStart = script.indexOf("function renderPropertyWorkspace(summary)");
        int renderEnd = script.indexOf("function restoreSessionWorkspace()", renderStart);
        String render = script.substring(renderStart, renderEnd);

        assertTrue(render.contains("Written no-record response saved"));
        assertTrue(render.contains("Plan physical verification or the next property decision"));
        assertTrue(render.contains("official_no_record_response_reviewed"));
        assertTrue(render.contains("if (!specialOutcome)"));
        assertFalse(render.contains("officialNoRecord ? \"Request the missing record\""));
    }
}
