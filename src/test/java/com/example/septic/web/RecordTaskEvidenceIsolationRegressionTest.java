package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RecordTaskEvidenceIsolationRegressionTest {

    // Regression: ISSUE-006 — evidence from one property survived when a different property task started.
    // Found by /qa on 2026-08-06.
    // Report: .gstack/qa-reports/records-exhaustive-2026-08-06/qa-report-septicpath-com-2026-08-06.md
    @Test
    void startsAcleanTaskWhenTheWorkflowOrPropertyIdentityChanges() throws IOException {
        String task = Files.readString(Path.of("src/main/resources/static/record-task.js"))
                .replace("\r\n", "\n");
        String app = Files.readString(Path.of("src/main/resources/static/app.js"))
                .replace("\r\n", "\n");

        assertTrue(task.contains("const workflowChanged = Boolean(stored && suppliedWorkflowRunId"));
        assertTrue(task.contains("const propertyChanged = Boolean(stored && resetState && currentPropertyIdentity"));
        assertTrue(task.contains("const startsNewTask = workflowChanged || propertyChanged"));
        assertTrue(task.contains("const current = startsNewTask ? null : stored"));
        assertTrue(task.contains("evidence: current?.evidence || []"));
        assertTrue(task.contains("sessionStorage.removeItem(DOCUMENT_WORKSPACE_KEY)"));
        assertTrue(task.contains("workflowRunId: propertyChanged\n                ? identifier()"));
        assertTrue(app.contains("activeWorkflowRunId = preparedTask.workflowRunId"));
    }
}
