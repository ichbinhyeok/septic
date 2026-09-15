package com.example.septic.web;

import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class SearchOpportunityBridgeRegressionTest {
    private String read(String path) throws Exception {
        return Files.readString(Path.of("src/main/" + path));
    }

    @Test void finderKeepsSearchBeforeProofAndOffersAssistedResult() throws Exception {
        String page = read("jte/pages/record-finder.jte");
        assertTrue(page.indexOf("tags.addressRecordFinder") < page.indexOf("tags.recordEvidence"));
        assertTrue(page.contains("A route is not the record"));
        assertTrue(page.contains("An empty search is not an official no-record finding"));
        assertTrue(page.contains("What the file can answer"));
        String finder = read("jte/tags/addressRecordFinder.jte");
        assertTrue(finder.indexOf("record_finder_start") > finder.indexOf("</form>"));
        assertTrue(finder.contains("data-track-source-context=\"record_finder_result\""));
        assertTrue(finder.contains("data-address-record-finder-result aria-live=\"polite\" hidden"));
    }

    @Test void analyticsDecisionsExcludeUnprocessedGa4Days() throws Exception {
        String operatingSystem = Files.readString(Path.of("docs/SEO_OPERATING_SYSTEM.md"));
        assertTrue(operatingSystem.contains("America/Los_Angeles"));
        assertTrue(operatingSystem.contains("D-3"));
        assertTrue(operatingSystem.contains("blank landing page"));
        assertTrue(operatingSystem.contains("record_help_request_submitted"));
    }

    @Test void evidenceIsRestrictedToDemandBackedRecordsAndPermitIntents() throws Exception {
        String page = read("jte/pages/state-money-page.jte");
        assertTrue(page.contains("@if (\"septic-records-checklist\".equals(stateMoneyPage.contentSlug()))"));
        assertTrue(page.contains("@if (searchIntentHandoff)"));
        assertEquals(2, page.split("@template.tags.recordEvidence", -1).length - 1);
        assertTrue(page.indexOf("state-money-intent-inline") < page.indexOf("<article class=\"hero-sidecard\">"));
        String evidence = read("jte/tags/recordEvidence.jte");
        assertTrue(evidence.contains("data-evidence-progress"));
        assertTrue(evidence.contains("Swipe to compare the original pages"));
        String evidenceScript = read("resources/static/record-evidence.js");
        assertTrue(evidenceScript.contains("Document ${nearestIndex + 1} of ${documents.length}"));
        for (String state : new String[]{"florida-ostds", "texas-ossf"}) {
            String tool = read("jte/pages/" + state + "-records-page.jte");
            assertEquals(1, tool.split("@template.tags.recordEvidence", -1).length - 1);
            assertTrue(tool.contains("This self-serve tool"));
            assertTrue(tool.contains("Tool limits"));
        }
    }

    @Test void outcomeHelpIsContextualAndDoesNotDuplicatePendingRequests() throws Exception {
        String script = read("resources/static/state-records-return.js");
        assertTrue(script.contains("if (outcome !== \"request_submitted\")"));
        assertTrue(script.contains("Ask us to explain the records"));
        assertTrue(script.contains("Ask us to continue the investigation"));
        assertTrue(script.contains("assistance.setAttribute(\"data-record-help-cta\", \"\")"));
        assertTrue(script.contains("`state_return_${stateCode.toLowerCase()}_${outcome}`"));
        assertFalse(script.contains("/offer-prep-septic-file-check/?address="));
        String analytics = read("resources/static/app-core.js");
        assertTrue(analytics.contains("const addedCtas = new MutationObserver"));
        assertTrue(analytics.contains("saveRecordHelpContext();"));
    }
}
