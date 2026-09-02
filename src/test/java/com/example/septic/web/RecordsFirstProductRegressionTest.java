package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RecordsFirstProductRegressionTest {

    @Test
    void navigationLeadsWithFindReviewAndDecision() throws IOException {
        String layout = Files.readString(Path.of("src/main/jte/layouts/app.jte"));
        int find = layout.indexOf("Find records</a>");
        int review = layout.indexOf("Review a file</a>");
        int decision = layout.indexOf("Closing Risk Check</a>");
        assertTrue(find > 0 && review > find && decision > review);
        assertTrue(layout.contains("/record-task.js"));
    }

    @Test
    void southCarolinaDoesNotTreatAddressOrMapPinAsARecordResult() throws IOException {
        String page = Files.readString(Path.of("src/main/jte/pages/south-carolina-records-page.jte"));
        String script = Files.readString(Path.of("src/main/resources/static/south-carolina-records.js"));
        assertTrue(page.contains("A broad map pin is not proof"));
        assertTrue(script.contains("directIdentifier"));
        assertTrue(script.contains("Get the ${selected.name} TMS"));
        assertTrue(script.contains("Prepare an SCDES file request"));
    }

    @Test
    void floridaFailsClosedWhenEbridgeProgramFieldsAreUnverified() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/florida-ostds-records.js"));
        assertTrue(script.contains("c.key===\"hillsborough\""));
        assertTrue(script.contains("program_unverified"));
        assertTrue(script.contains("Open county health request route"));
        assertTrue(script.contains("fields unverified"));
    }

    @Test
    void apiAndAdaptersExposeTheCommonRecordRouteContract() throws IOException {
        String result = Files.readString(Path.of("src/main/java/com/example/septic/web/AddressRecordFinderResult.java"));
        String common = Files.readString(Path.of("src/main/java/com/example/septic/web/RecordRouteView.java"));
        for (String field : new String[]{"countyKey", "routeMode", "routeReliability", "requiredIdentifiers", "requestedDocuments", "requestRoute"}) {
            assertTrue(result.contains(field), field);
            assertTrue(common.contains(field) || field.equals("requestRoute"), field);
        }
        assertTrue(Files.readString(Path.of("src/main/java/com/example/septic/web/TennesseeCountyRouteView.java")).contains("toRecordRouteView"));
        assertTrue(Files.readString(Path.of("src/main/java/com/example/septic/web/FloridaCountyRouteView.java")).contains("toRecordRouteView"));
        assertTrue(Files.readString(Path.of("src/main/java/com/example/septic/web/SouthCarolinaCountyRouteView.java")).contains("toRecordRouteView"));
    }

    @Test
    void documentReviewRecordsArtifactReviewAndDecisionStages() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/app.js"));
        assertTrue(script.contains("addArtifactEvidence"));
        assertTrue(script.contains("transition(\"document_reviewed\""));
        assertTrue(script.contains("transition(\"decision_ready\""));
    }

    @Test
    void mobileEntryKeepsTheAddressAndActionInTheFirstScreen() throws IOException {
        String css = Files.readString(Path.of("src/main/resources/static/workflows.css"));
        assertTrue(css.contains(".home-hero .record-finder__start-options { grid-template-columns: repeat(3"));
        assertTrue(css.contains(".home-hero .record-finder__signals { display: none; }"));
        assertTrue(css.contains(".home-hero .record-finder__form-fields { gap: 8px; }"));
    }
}
