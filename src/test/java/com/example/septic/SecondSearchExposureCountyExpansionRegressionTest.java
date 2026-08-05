package com.example.septic;

import com.example.septic.web.CountyAccessProfileCatalog;
import com.example.septic.web.CountyAccessProfileView;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecondSearchExposureCountyExpansionRegressionTest {

    @Test
    void secondDemandBackedBatchExposesDistinctVerifiedWorkflows() {
        Map<String, String> expectedTiers = Map.of(
                "TN::davidson-county", "search_then_request",
                "TN::madison-county", "request_records",
                "TN::shelby-county", "official_start",
                "TN::putnam-county", "search_then_request",
                "IN::monroe-county", "search_then_request"
        );

        expectedTiers.forEach((countyKey, tier) -> {
            CountyAccessProfileView profile = CountyAccessProfileCatalog.find(countyKey);
            assertNotNull(profile, countyKey);
            assertTrue(profile.countySpecific(), countyKey);
            assertEquals(tier, profile.capabilityTier(), countyKey);
            assertTrue(profile.requiredInputs().size() >= 4, countyKey);
            assertTrue(profile.expectedArtifacts().size() >= 3, countyKey);
            assertTrue(profile.steps().size() >= 4, countyKey);
        });
    }

    @Test
    void officialRoutesRetainTheirPublishedInputsAndTruthBoundaries() {
        CountyAccessProfileView davidson = CountyAccessProfileCatalog.find("TN::davidson-county");
        assertEquals("https://documents.nashville.gov/", davidson.primaryUrl());
        assertTrue(davidson.summary().contains("parcel or tax ID"));
        assertTrue(davidson.limitation().contains("empty search"));

        CountyAccessProfileView madison = CountyAccessProfileCatalog.find("TN::madison-county");
        assertTrue(madison.requiredInputs().contains("Applicant name, phone, email, and role"));
        assertTrue(madison.limitation().contains("reCAPTCHA"));

        CountyAccessProfileView shelby = CountyAccessProfileCatalog.find("TN::shelby-county");
        assertTrue(shelby.limitation().contains("Do not pay the new-work application fee"));
        assertFalse(shelby.steps().stream().anyMatch(step -> step.contains("$175")));
        assertTrue(shelby.steps().stream().anyMatch(step -> step.contains("existing septic property file")));

        CountyAccessProfileView monroe = CountyAccessProfileCatalog.find("IN::monroe-county");
        assertTrue(monroe.expectedArtifacts().contains("Soil evaluation and county Minimum Specs document"));
        assertTrue(monroe.completionLabel().contains("OpenGov"));
    }

    @Test
    void sourceContentNoLongerUsesTheGenericCountyTemplate() throws IOException {
        String countyPages = Files.readString(Path.of("data/raw/county_records_pages.json"));
        for (String title : new String[]{
                "Davidson County TN Septic Records and Property File Search",
                "Madison County TN Septic Drawing and Records Request",
                "Shelby County TN Septic Records and Permit File Request",
                "Putnam County TN Septic Permit Search and TDEC Records",
                "Monroe County IN Septic Permit and OpenGov Workflow"
        }) {
            assertTrue(countyPages.contains("\"title\": \"" + title + "\""), title);
        }
        assertTrue(countyPages.contains("scanned Health Environmental Engineering records"));
        assertTrue(countyPages.contains("Use the county's installation or repair permit application only when new work is actually proposed"));
        assertTrue(countyPages.contains("receive the county's Minimum Specs"));
    }
}
