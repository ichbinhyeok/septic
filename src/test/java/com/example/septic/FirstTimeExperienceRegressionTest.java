package com.example.septic;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FirstTimeExperienceRegressionTest {

    @Test
    void homepageDisclosesFreeResultsAndAgencyFeeApprovalBesidePrimaryAction() throws IOException {
        String template = Files.readString(Path.of("src/main/jte/pages/home.jte"));

        int primaryAction = template.indexOf("Get free record help");
        int offer = template.indexOf("Free research, records and explanation. Agency fees only with approval.");

        assertTrue(primaryAction >= 0);
        assertTrue(offer > primaryAction);
        assertTrue(offer - primaryAction < 500);
        assertFalse(template.contains("Free beta · No payment details"));
        assertFalse(template.contains("$29"));
        int serviceExplanation = template.indexOf("We search official sources, contact the right office, and explain the records we find—free.");
        assertTrue(serviceExplanation >= 0 && serviceExplanation < primaryAction);
        int selfServe = template.indexOf("class=\"home-service-hero__self-serve\"");
        assertTrue(selfServe > offer);
        assertTrue(template.contains("Source facts preserved"));
        assertTrue(template.contains("Current condition is not confirmed."));
    }

    @Test
    void recordFinderOffersAnAboveFoldAddressActionAndNoEmptyResultHeading() throws IOException {
        String template = Files.readString(Path.of("src/main/jte/tags/addressRecordFinder.jte"));

        assertTrue(template.contains("href=\"#${finderId}-form\""));
        assertTrue(template.contains(">Start with the property address</a>"));
        assertFalse(template.contains("<h3 data-address-record-finder-heading></h3>"));
    }

    @Test
    void calculatorPutsPrimaryInputsBeforeRecordShortcuts() throws IOException {
        String template = Files.readString(Path.of("src/main/jte/pages/calculator.jte"));

        int stateInput = template.indexOf("<span>State</span>");
        int submit = template.indexOf("Show planning estimate");
        int recordShortcuts = template.indexOf("Need records first?");

        assertTrue(stateInput >= 0);
        assertTrue(submit > stateInput);
        assertTrue(recordShortcuts > submit);
    }

    @Test
    void contactPageRoutesPropertyResearchToTheInvestigationForm() throws IOException {
        String template = Files.readString(Path.of("src/main/jte/pages/contact-page.jte"));

        assertTrue(template.contains("Start a property record investigation"));
        assertTrue(template.contains("Reviewed by the SepticPath research desk"));
        assertFalse(template.contains("still estimate-first"));
        assertFalse(template.contains("before a support inbox or business address is published"));
    }
}
