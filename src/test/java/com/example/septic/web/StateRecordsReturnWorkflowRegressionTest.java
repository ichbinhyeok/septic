package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StateRecordsReturnWorkflowRegressionTest {

    private static final Path RETURN_TAG = Path.of("src/main/jte/tags/stateRecordsReturn.jte");
    private static final Path RETURN_SCRIPT = Path.of("src/main/resources/static/state-records-return.js");

    // Regression: ISSUE-STATE-RETURN-001 — dedicated state routes lost users after an official-site handoff.
    // Found by /qa on 2026-08-05.
    // Report: .gstack/qa-reports/qa-report-septicpath-com-2026-08-05.md
    @Test
    void everyDedicatedStateRouteIncludesTheSharedReturnWorkflow() throws IOException {
        for (String page : List.of(
                "tdec-records-page.jte",
                "north-carolina-records-page.jte",
                "texas-ossf-records-page.jte",
                "florida-ostds-records-page.jte",
                "south-carolina-records-page.jte"
        )) {
            String template = Files.readString(Path.of("src/main/jte/pages", page));
            assertTrue(template.contains("@template.tags.stateRecordsReturn"), page);
            assertTrue(template.contains("/state-records-return.js"), page);
            assertTrue(template.contains("data-state-fallback-outcome=\"found\""), page);
            assertTrue(template.contains("data-state-fallback-outcome=\"not_found_online\""), page);
            assertTrue(template.contains("no_record_response"), page);
        }
    }

    @Test
    void returnWorkflowKeepsAllRealOfficialOutcomesDistinct() throws IOException {
        String tag = Files.readString(RETURN_TAG);
        String script = Files.readString(RETURN_SCRIPT);

        for (String outcome : List.of(
                "found", "request_submitted", "not_found_online",
                "no_record_response", "wrong_agency", "blocked"
        )) {
            assertTrue(tag.contains("data-state-return-outcome=\"" + outcome + "\""), outcome);
            assertTrue(script.contains(outcome), outcome);
        }
        assertTrue(script.contains("state_record_outcome_recorded"));
        assertTrue(script.contains("state_record_official_returned"));
    }

    @Test
    void savedReturnContextExcludesPropertyIdentifiers() throws IOException {
        String script = Files.readString(RETURN_SCRIPT);
        int start = script.indexOf("function context()");
        int end = script.indexOf("function save(", start);
        String contextBlock = script.substring(start, end);

        assertTrue(contextBlock.contains("matchedAddress: \"\""));
        assertTrue(contextBlock.contains("countyName"));
        assertTrue(contextBlock.contains("existing.countyKey"));
        assertTrue(contextBlock.contains("workflowRunId"));
        assertFalse(contextBlock.contains("parcel"));
        assertFalse(contextBlock.contains("owner"));
        assertFalse(contextBlock.contains("clue"));
    }

    @Test
    void requestPendingStateProvidesPrivateSafeReminderAndReturnLink() throws IOException {
        String tag = Files.readString(RETURN_TAG);
        String script = Files.readString(RETURN_SCRIPT);

        assertTrue(tag.contains("I submitted a request"));
        assertTrue(script.contains("Add a 7-day reminder"));
        assertTrue(script.contains("Copy return link"));
        assertTrue(script.contains("No property identifier is stored in this reminder"));
        assertTrue(script.contains("septicpath-record-task-progress-v1"));
    }
}
