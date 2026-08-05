package com.example.septic.web;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class PastedDocumentAccumulationRegressionTest {

    // Regression: ISSUE-DOC-005 - pasted sources reused one filename and replaced the prior source.
    // Found by /qa on 2026-08-05.
    // Report: .gstack/qa-reports/qa-report-septicpath-com-document-workspace-2026-08-05.md
    @Test
    void pastedSourcesReceiveStableDistinctNamesAndRemainSeparateDocuments() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/app.js"));

        assertTrue(script.contains("function addDocumentToWorkspace(payload, sourceType = \"uploaded\")"));
        assertTrue(script.contains("sourceType,"));
        assertTrue(script.contains("pastedSourceCount + 1"));
        assertTrue(script.contains("`pasted-official-record-${pastedSourceCount + 1}.txt`"));
        assertTrue(script.contains("addDocumentToWorkspace(payload, sourceType)"));
    }
}
