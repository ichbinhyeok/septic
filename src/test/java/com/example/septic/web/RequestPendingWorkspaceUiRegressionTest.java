package com.example.septic.web;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class RequestPendingWorkspaceUiRegressionTest {

    // Regression: ISSUE-DOC-004 — the workspace sent an acknowledged request back to the request builder.
    // Found by /qa on 2026-08-05.
    // Report: .gstack/qa-reports/qa-report-septicpath-com-document-workspace-2026-08-05.md
    @Test
    void pendingRequestUsesFollowUpStateInsteadOfDuplicateRequestAction() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/app.js"));
        int renderStart = script.indexOf("function renderPropertyWorkspace(summary)");
        int renderEnd = script.indexOf("function restoreSessionWorkspace()", renderStart);
        String render = script.substring(renderStart, renderEnd);

        assertTrue(render.contains("Official records request is pending"));
        assertTrue(render.contains("Review the official route and response timing"));
        assertTrue(render.contains("record_request_acknowledgment_reviewed"));
        assertTrue(render.contains("if (!specialOutcome)"));
    }
}
