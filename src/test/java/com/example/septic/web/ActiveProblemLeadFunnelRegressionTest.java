package com.example.septic.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ActiveProblemLeadFunnelRegressionTest {

    // Regression: record-search failure alone is not a service lead. The funnel must ask for a current symptom.
    @Test
    void tdecReturnCheckpointQualifiesCurrentSymptomsBeforeOpeningTheLeadForm() throws IOException {
        String script = Files.readString(
                Path.of("src/main/resources/static/tdec-records.js"),
                StandardCharsets.UTF_8
        );
        String template = Files.readString(
                Path.of("src/main/jte/pages/calculator.jte"),
                StandardCharsets.UTF_8
        );

        assertThat(script).contains("A missing record by itself is not a service lead.");
        assertThat(script).contains("service_lead_intent_selected");
        assertThat(script).contains("backup_slow_drains");
        assertThat(script).contains("surfacing_wastewater");
        assertThat(script).contains("failed_inspection");
        assertThat(script).contains("sourcePageHint: \"/tdec-septic-records/\"");

        assertThat(template).contains("What is happening at the property?");
        assertThat(template).contains("Request service follow-up");
        assertThat(template).contains("Describe the symptom, not your guess at the repair.");
        assertThat(template).contains("This form is not emergency dispatch.");
        assertThat(template).contains("data-ga-param-service-need");
    }
}
