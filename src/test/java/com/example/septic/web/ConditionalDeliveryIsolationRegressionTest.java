package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConditionalDeliveryIsolationRegressionTest {

    // Regression: ISSUE-001 — St. Joseph conditional labels could override other counties' accessibility state.
    // Found by /qa on 2026-08-11.
    // Report: .gstack/qa-reports/qa-report-septicpath-com-2026-08-11-multi-persona.md
    @Test
    void conditionalDeliveryLabelsStayScopedToStJosephCounty() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/app.js"))
                .replace("\r\n", "\n");

        assertTrue(script.contains("function syncConditionalAcquisitionRequirement() {\n"
                + "                if (countyKey !== \"IN::st-joseph-county\") {\n"
                + "                    return;\n"
                + "                }"));
    }
}
