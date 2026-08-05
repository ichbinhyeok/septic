package com.example.septic.web;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class BlankDocumentUploadStatusRegressionTest {

    // Regression: ISSUE-009 — zero-fact upload rendered correctly, then a block-scoped summary caused a false network error.
    @Test
    void successfulUploadKeepsTheWorkspaceSummaryAvailableForItsStatusMessage() throws Exception {
        String script = Files.readString(Path.of("src/main/resources/static/app.js"));

        assertTrue(script.contains("let summary = null;"));
        assertTrue(script.contains("summary = addDocumentToWorkspace(payload, sourceType);"));
        assertTrue(script.contains("summary?.completeCount === 0"));
    }
}
