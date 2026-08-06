package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecordTaskV2RegressionTest {
    private static final Path TASK = Path.of("src/main/resources/static/record-task.js");

    @Test
    void v2DefinesEveryAcquisitionAndDecisionState() throws IOException {
        String script = Files.readString(TASK);
        for (String state : new String[]{
                "route_ready", "official_opened", "not_found_online", "request_prepared",
                "request_pending", "artifact_acquired", "no_record_response", "wrong_agency",
                "blocked", "document_reviewed", "decision_ready"
        }) {
            assertTrue(script.contains('"' + state + '"'), state);
        }
        assertTrue(script.contains("30 * 24 * 60 * 60 * 1000"));
    }

    @Test
    void submissionRequiresDateAndChannelWhileDraftingDoesNotCountAsSuccess() throws IOException {
        String script = Files.readString(TASK);
        assertTrue(script.contains("/^\\d{4}-\\d{2}-\\d{2}$/"));
        assertTrue(script.contains("!safe(channel, 32)"));
        assertTrue(script.contains("transition(\"request_pending\", \"request_submitted\""));
        assertFalse(script.contains("request_prepared\", \"request_submitted"));
    }

    @Test
    void serverStagePayloadExcludesPropertyValues() throws IOException {
        String script = Files.readString(TASK);
        int start = script.indexOf("const payload = {");
        int end = script.indexOf("};", start);
        String payload = script.substring(start, end);
        assertTrue(payload.contains("workflowRunId"));
        assertTrue(payload.contains("countyKey"));
        assertTrue(payload.contains("stage"));
        assertTrue(payload.contains("outcome"));
        assertFalse(payload.contains("address"));
        assertFalse(payload.contains("identifierValue"));
        assertFalse(payload.contains("reference"));
    }

    @Test
    void migrationIsRouteAndStateScopedAndExplicitClearRemovesBothVersions() throws IOException {
        String script = Files.readString(TASK);
        assertTrue(script.contains("routeMatches"));
        assertTrue(script.contains("stateMatches"));
        assertTrue(script.contains("window.location.pathname === \"/tdec-septic-records/\""));
        assertTrue(script.contains("[STORAGE_KEY, LEGACY_TASK_KEY]"));
        assertTrue(script.contains("[LEGACY_RETURN_KEY, LEGACY_TDEC_KEY]"));
    }

    @Test
    void partialContextUpdatesDoNotEraseRouteOrPropertyFields() throws IOException {
        String script = Files.readString(TASK);
        assertTrue(script.contains("meaningful(suppliedContext)"));
        assertTrue(script.contains("meaningful(suppliedProperty)"));
        assertTrue(script.contains("...(current?.context || {})"));
        assertTrue(script.contains("...(current?.property || {})"));
    }
}
