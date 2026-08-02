package com.example.septic;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CountyOutcomeTaxonomyRegressionTest {

    // Regression: ISSUE-001 — blank search, official no-record, wrong agency, and professional help were conflated.
    // Found by /qa on 2026-08-02
    // Report: .gstack/qa-reports/qa-report-septicpath-com-2026-08-02.md
    @Test
    void addressAndCountyFlowsPublishDistinctRealWorldOutcomes() throws IOException {
        String finder = Files.readString(Path.of("src/main/jte/tags/addressRecordFinder.jte"));
        String countyPage = Files.readString(Path.of("src/main/jte/pages/county-records-page.jte"));
        String script = Files.readString(Path.of("src/main/resources/static/app.js"));

        for (String outcome : new String[]{
                "not_found_online", "no_record_response", "wrong_agency", "professional_help"
        }) {
            assertTrue(finder.contains("data-record-outcome=\"" + outcome + "\""), outcome);
            assertTrue(countyPage.contains("data-county-access-outcome=\"" + outcome + "\""), outcome);
            assertTrue(script.contains("outcome === \"" + outcome + "\""), outcome);
        }
    }

    @Test
    void professionalHelpIsPreparedAsAFutureLeadWithoutPromisingAProvider() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/app.js"));

        assertTrue(script.contains("county_outcome_professional_help"));
        assertTrue(script.contains("trackTargetType = \"quote_form\""));
        assertTrue(script.contains("does not guarantee a contractor match"));
        assertTrue(script.contains("does not promise contractor availability"));
    }

    @Test
    void countyOutcomeEventsSeparateNoRecordAndRoutingFailures() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/app.js"));

        assertTrue(script.contains("\"county_no_record_confirmed\""));
        assertTrue(script.contains("\"county_file_owner_mismatch\""));
        assertTrue(script.contains("\"county_professional_help_needed\""));
    }
}
