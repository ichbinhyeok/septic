package com.example.septic.web;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class BlankDocumentWorkspaceRegressionTest {

    // Regression: ISSUE-006 — a blank official form was reported as merely "added" after returning zero facts.
    @Test
    void zeroFactDocumentExplainsThatItDoesNotEstablishAPropertyRecordOutcome() throws Exception {
        String script = Files.readString(Path.of("src/main/resources/static/app.js"));

        assertTrue(script.contains("No property-specific septic facts found"));
        assertTrue(script.contains("This may be a blank form, general guidance, or an unrelated file."));
        assertTrue(script.contains("was read, but no property-specific septic facts were found."));
        assertTrue(script.contains("It does not confirm a permit, approval, layout, inspection, or no-record result."));
    }
}
