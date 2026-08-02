package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentWorkspaceConflictRegressionTest {

    private static final Path APP_JS = Path.of("src/main/resources/static/app.js");

    @Test
    void distinctPermitAndRepairRecordNumbersAreKeptWithoutCreatingAConflict() throws IOException {
        String script = Files.readString(APP_JS);
        int conflictKeysStart = script.indexOf("const conflictKeys = new Set([");
        int conflictKeysEnd = script.indexOf("]);", conflictKeysStart);
        String conflictKeys = script.substring(conflictKeysStart, conflictKeysEnd);

        assertFalse(conflictKeys.contains("permit_number"));
        assertTrue(conflictKeys.contains("approved_bedrooms"));
        assertTrue(script.contains("[\"permit_number\", \"Permit or record number\"]"));
    }
}
