package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PropertyFileFunnelRegressionTest {

    private static final Path APP_JS = Path.of("src/main/resources/static/app.js");

    @Test
    void countyAndOfferPrepRoutesHandDocumentsIntoTheSharedPropertyFileWorkspace() throws IOException {
        String script = Files.readString(APP_JS);
        String offerPrep = Files.readString(Path.of("src/main/jte/pages/offer-prep-septic-file-check.jte"));

        assertTrue(script.contains("finderQuery.get(\"mode\") === \"document\""));
        assertTrue(script.contains("params.set(\"mode\", \"document\")"));
        assertTrue(script.contains("safeValue(address) || requestedPropertyAddress"));
        assertTrue(script.contains("function documentWorkspacePath(route)"));
        assertTrue(script.contains("storedWorkflowRunId !== requestedWorkflowRunId"));
        assertTrue(script.contains("pendingWorkflowRunId !== requestedWorkflowRunId"));
        assertTrue(script.contains("Check the property file you found"));
        assertTrue(offerPrep.contains("data-offer-prep-next"));
        assertTrue(offerPrep.contains("data-offer-prep-next-actions"));
        assertTrue(offerPrep.contains("/septic-transfer-compliance/"));
        assertTrue(offerPrep.contains("Open the transfer workflow"));
        assertTrue(script.contains("Use the transfer workflow"));
    }

    @Test
    void transferWorkflowRemainsDiscoverableOutsideThePrimaryMenu() throws IOException {
        String layout = Files.readString(Path.of("src/main/jte/layouts/app.jte"));

        assertTrue(layout.contains("<li><a href=\"/septic-transfer-compliance/\">Transfer compliance</a></li>"));
    }

    @Test
    void offerPrepAddressRoutingIsNotArtificiallyLimitedToFourStates() throws IOException {
        String script = Files.readString(APP_JS);
        String offerPrep = Files.readString(Path.of("src/main/jte/pages/offer-prep-septic-file-check.jte"));

        assertFalse(script.contains("const supportedStates = new Set([\"TN\", \"IN\", \"NC\", \"SC\"])"));
        assertFalse(script.contains("unsupported_state"));
        assertFalse(offerPrep.contains("<strong>4</strong> transaction states"));
        assertTrue(offerPrep.contains("offerPrepStates.size()"));
    }

    @Test
    void propertyFileFunnelEmitsDecisionQualitySignalsWithoutPropertyValues() throws IOException {
        String script = Files.readString(APP_JS);

        assertTrue(script.contains("\"record_route_completed\""));
        assertTrue(script.contains("\"record_request_prepared\""));
        assertTrue(script.contains("\"file_check_completed\""));
        assertTrue(script.contains("\"missing_record_identified\""));
        assertTrue(script.contains("\"permit_conflict_identified\""));
        assertTrue(script.contains("\"professional_help_recommended\""));

        int start = script.indexOf("emitGaEvent(\"file_check_completed\"");
        int end = script.indexOf("const wrapper", start);
        String propertyFileAnalytics = script.substring(start, end);
        assertFalse(propertyFileAnalytics.contains("matchedAddress"));
        assertFalse(propertyFileAnalytics.contains("fileName"));
        assertFalse(propertyFileAnalytics.contains("evidence"));
    }
}
