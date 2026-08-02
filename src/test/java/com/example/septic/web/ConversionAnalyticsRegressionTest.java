package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConversionAnalyticsRegressionTest {

    @Test
    void calculatorAndFutureLeadActionsUseTheRequiredGa4EventNames() throws IOException {
        String coreScript = Files.readString(Path.of("src/main/resources/static/app-core.js"));
        String calculator = Files.readString(Path.of("src/main/jte/pages/calculator.jte"));
        String result = Files.readString(Path.of("src/main/jte/tags/estimateResult.jte"));

        assertTrue(coreScript.contains("emitGaEvent(\"calculator_started\""));
        assertTrue(coreScript.contains("emitGaEvent(\"calculator_completed\""));
        assertTrue(coreScript.contains("emitGaEvent(\"lead_cta_clicked\""));
        assertTrue(calculator.contains("data-ga-event=\"calculator_submit\""));
        assertTrue(result.contains("data-track-target-type=\"quote_form\""));
    }
}
