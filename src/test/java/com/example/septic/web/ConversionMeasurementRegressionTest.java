package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConversionMeasurementRegressionTest {

    @Test
    void coreScriptConnectsContentToolsLeadAndEngagementEvents() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/app-core.js"));

        assertTrue(script.contains("\"conversion_page_viewed\""));
        assertTrue(script.contains("\"case_study_clicked\""));
        assertTrue(script.contains("\"case_study_viewed\""));
        assertTrue(script.contains("\"calculator_cta_clicked\""));
        assertTrue(script.contains("\"meaningful_engagement\""));
        assertTrue(script.contains("\"record_help_form_submit_attempted\""));
        assertTrue(script.contains("\"generate_lead\""));
        assertTrue(script.contains("activeSeconds < 30 || maxScrollDepth < 50"));

        int successLookup = script.indexOf("const success = document.querySelector(\"[data-closing-risk-request-success]\")");
        int formGuard = script.indexOf("if (!(form instanceof HTMLFormElement)) return;", successLookup);
        assertTrue(successLookup >= 0 && formGuard > successLookup,
                "The success-only Studio response has no form, so generate_lead must run before the form guard");

        String studioIntake = Files.readString(Path.of("src/main/jte/studio/intake.jte"));
        assertTrue(studioIntake.contains("data-ga-track-once=\"record-help-request:${closingRiskRequestId}\""));
    }

    @Test
    void privateResultsExcludeCheckoutTrackingAndBearerTokensFromAnalytics() throws IOException {
        String checkout = Files.readString(Path.of("src/main/jte/pages/paid-unlock.jte"));
        String layout = Files.readString(Path.of("src/main/jte/layouts/app.jte"));

        assertTrue(checkout.contains("No paid unlock."));
        assertFalse(checkout.contains("data-ga-event="));
        assertFalse(checkout.contains("data-ga-track-once="));
        assertFalse(checkout.contains("begin_checkout"));
        assertFalse(checkout.contains("paypal.Buttons"));
        assertTrue(layout.contains("!window.location.pathname.startsWith(\"/unlock/\")"));
        assertTrue(layout.contains("!window.location.pathname.startsWith(\"/paid-unlock/\")"));
        for (String file : new String[] {"app-core.js", "app.js"}) {
            String script = Files.readString(Path.of("src/main/resources/static", file));
            assertTrue(script.contains("return \"/private-record-result/\";"));
            int sender = script.indexOf("function sendEvent(endpoint, payload)");
            int privateGuard = script.indexOf("if (/^\\/(unlock|paid-unlock)\\//.test(window.location.pathname)) return;", sender);
            int serialization = script.indexOf("const body = JSON.stringify(payload)", sender);
            assertTrue(sender >= 0 && privateGuard > sender && serialization > privateGuard,
                    "Private result pages must stop analytics before serializing or sending events");
        }
    }

    @Test
    void conversionParametersStayCategoricalAndExcludeCustomerFields() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/app-core.js"));
        int start = script.indexOf("function setupConversionMeasurement");
        int end = script.indexOf("function setupPrimaryFunnelEvents", start);
        String conversionBlock = script.substring(start, end);

        assertTrue(conversionBlock.contains("page_family"));
        assertTrue(conversionBlock.contains("source_context"));
        assertFalse(conversionBlock.contains("propertyAddress"));
        assertFalse(conversionBlock.contains("email"));
        assertFalse(conversionBlock.contains("phone"));
        assertFalse(conversionBlock.contains("parcel"));
    }
}
