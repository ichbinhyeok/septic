package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConversionAnalyticsRegressionTest {

    @Test
    void calculatorAndFutureLeadActionsUseTheRequiredGa4EventNames() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/app.js"));
        String calculator = Files.readString(Path.of("src/main/jte/pages/calculator.jte"));
        String result = Files.readString(Path.of("src/main/jte/tags/estimateResult.jte"));

        assertTrue(script.contains("emitGaEvent(\"calculator_started\""));
        assertTrue(script.contains("emitGaEvent(\"lead_cta_clicked\""));
        assertTrue(calculator.contains("data-ga-event=\"calculator_completed\""));
        assertTrue(calculator.contains("data-ga-event=\"calculator_submit\""));
        assertTrue(result.contains("data-track-target-type=\"quote_form\""));
    }
}
