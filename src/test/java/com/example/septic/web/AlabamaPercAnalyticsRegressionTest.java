package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AlabamaPercAnalyticsRegressionTest {

    @Test
    void alabamaPercWorkflowEmitsCalculatorEventsAndTracksOfficialHandoffs() throws Exception {
        String appScript = Files.readString(Path.of("src/main/resources/static/app.js"));
        String stateGuide = Files.readString(Path.of("src/main/jte/pages/state-guide.jte"));

        assertTrue(appScript.contains("emitGaEvent(\"calculator_started\", analyticsParams)"));
        assertTrue(appScript.contains("emitGaEvent(\"calculator_completed\""));
        assertTrue(appScript.contains("calculator_type: \"alabama_perc_scope\""));
        assertTrue(appScript.contains("county_selected:"));
        assertTrue(stateGuide.contains("data-track-source-context=\"alabama_perc_fee_manual\" data-track-target-type=\"official_source\""));
        assertTrue(stateGuide.contains("data-track-source-context=\"alabama_perc_selected_county_page\" data-track-target-type=\"official_source\""));
        assertTrue(stateGuide.contains("data-track-source-context=\"alabama_perc_county_form\" data-track-target-type=\"official_source\""));
    }
}
