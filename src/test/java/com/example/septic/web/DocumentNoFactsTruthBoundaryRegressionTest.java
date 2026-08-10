package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentNoFactsTruthBoundaryRegressionTest {

    // Regression: ISSUE-008 — a readable but unrelated document was counted as acquired
    // and advanced to decision_ready with generic inspection advice during final browser QA.
    @Test
    void emptyExtractionDoesNotBecomeAcquisitionOrDecisionSuccess() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/app.js"))
                .replace("\r\n", "\n");

        assertTrue(script.contains("if (summary.grouped.size === 0)"));
        assertTrue(script.contains("No usable property facts yet"));
        assertTrue(script.contains("const usableEvidence = Boolean(payload?.recordOutcome)"));
        assertTrue(script.contains("if (usableEvidence) {\n                            window.SepticRecordTask?.addArtifactEvidence"));
        assertTrue(script.contains("if (usableEvidence) {\n                            window.SepticRecordTask?.transition(\"decision_ready\""));
        assertTrue(script.contains("if (usableEvidence) {\n                            sendArtifactAction"));
    }
}
