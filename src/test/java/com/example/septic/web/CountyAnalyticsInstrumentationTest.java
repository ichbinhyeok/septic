package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CountyAnalyticsInstrumentationTest {

    private static final Path APP_JS = Path.of("src/main/resources/static/app.js");

    @Test
    void countyWorkflowEmitsTheCompleteGa4Funnel() throws IOException {
        String script = Files.readString(APP_JS);

        assertTrue(script.contains("\"county_workflow_viewed\""));
        assertTrue(script.contains("\"county_prepare_started\""));
        assertTrue(script.contains("\"county_prepare_ready\""));
        assertTrue(script.contains("\"county_preparation_downloaded\""));
        assertTrue(script.contains("\"county_official_pdf_prepared\""));
        assertTrue(script.contains("\"county_official_route_opened\""));
        assertTrue(script.contains("\"county_return_outcome\""));
        assertTrue(script.contains("\"county_request_submitted\""));
        assertTrue(script.contains("\"county_record_reported\""));
        assertTrue(script.contains("\"county_record_obtained\""));
        assertTrue(script.contains("\"/events/workflow-stage\""));
        assertTrue(script.contains("recordCountyStage(\"official_route_opened\")"));
        assertTrue(script.contains("recordCountyStage(\"outcome_recorded\", outcome)"));
        assertTrue(script.contains("recordFinderStage(\"document_reviewed\")"));
        assertTrue(script.contains("recordFinderStage(\"property_file_ready\")"));
        assertTrue(script.contains("recordFinderStage(\"task_finished\")"));
        assertFalse(script.contains("\"county_task_completed\""));
    }

    @Test
    void countyGa4ParametersStayCategoricalAndExcludePropertyValues() throws IOException {
        String script = Files.readString(APP_JS);
        int start = script.indexOf("function emitCountyGaEvent");
        int end = script.indexOf("function markGaPreparationStarted", start);
        String parameterBlock = script.substring(start, end);

        assertTrue(parameterBlock.contains("county_key"));
        assertTrue(parameterBlock.contains("state_code"));
        assertTrue(parameterBlock.contains("county_slug"));
        assertTrue(parameterBlock.contains("access_mode"));
        assertTrue(parameterBlock.contains("acquisition_method"));
        assertTrue(parameterBlock.contains("profile_scope"));
        assertTrue(parameterBlock.contains("capability_tier"));
        assertTrue(parameterBlock.contains("workflow_run_id"));
        assertFalse(parameterBlock.contains("address"));
        assertFalse(parameterBlock.contains("parcel"));
        assertFalse(parameterBlock.contains("reference"));
        assertFalse(parameterBlock.contains("recipient"));
    }

    @Test
    void firstPartyEventPathsStripPropertyAndWorkflowQueryValues() throws IOException {
        String script = Files.readString(APP_JS);

        assertTrue(script.contains("function analyticsSafePath"));
        assertTrue(script.contains("analyticsQueryKeys.has(key)"));
        assertTrue(script.contains("sourcePage: analyticsSourcePage()"));
        assertFalse(script.contains("sourcePage: window.location.pathname + window.location.search"));
    }
}
