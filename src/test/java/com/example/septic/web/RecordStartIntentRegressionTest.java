package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RecordStartIntentRegressionTest {

    @Test
    void fullRecordFinderExposesThreeDistinctStartingIntentChoices() throws IOException {
        String finder = Files.readString(Path.of("src/main/jte/tags/addressRecordFinder.jte"));

        assertTrue(finder.contains("data-record-start-mode=\"find\""));
        assertTrue(finder.contains("data-record-start-mode=\"missing\""));
        assertTrue(finder.contains("data-record-start-mode=\"review\""));
        assertTrue(finder.contains("Find a record"));
        assertTrue(finder.contains("I could not find it"));
        assertTrue(finder.contains("Review a record I have"));
    }

    @Test
    void missingRecordEntryPreservesRealWorldOutcomeDistinctions() throws IOException {
        String finder = Files.readString(Path.of("src/main/jte/tags/addressRecordFinder.jte"));
        String script = Files.readString(Path.of("src/main/resources/static/app.js"));

        for (String outcome : new String[]{
                "not_found_online", "no_record_response", "request_submitted", "wrong_agency", "blocked"
        }) {
            assertTrue(finder.contains("<option value=\"" + outcome + "\""), outcome);
        }
        assertTrue(script.contains("function applyStartingOutcome(payloadStatus)"));
        assertTrue(script.contains("start_outcome_${outcome}"));
        assertTrue(script.contains("failed_record_search"));
    }

    @Test
    void documentReviewEntryUsesTheExistingPropertyFileWorkspace() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/app.js"));

        assertTrue(script.contains("function openDirectDocumentWorkspace"));
        assertTrue(script.contains("openDirectDocumentWorkspace(\"start_mode_review\")"));
        assertTrue(script.contains("record_start_mode_selected"));
        assertTrue(script.contains("input.required = !reviewing"));
    }

    @Test
    void explicitMissingModeWinsOverAStoredDocumentSession() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/app.js"));

        int restore = script.indexOf("restoreSessionWorkspace();");
        int explicitMode = script.indexOf("if (requestedMissingMode)", restore);
        int documentMode = script.indexOf("if (requestedDocumentMode && documentWorkspace", explicitMode);

        assertTrue(restore >= 0);
        assertTrue(explicitMode > restore);
        assertTrue(documentMode > explicitMode);
        assertTrue(script.substring(explicitMode, documentMode)
                .contains("syncStartMode(\"missing\", { focus: false, openWorkspace: false })"));
        assertTrue(script.substring(explicitMode, documentMode)
                .contains("documentWorkspace.hidden = true"));
    }
}
