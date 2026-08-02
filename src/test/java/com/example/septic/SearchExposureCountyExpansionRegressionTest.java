package com.example.septic;

import com.example.septic.web.CountyAccessProfileCatalog;
import com.example.septic.web.CountyAccessProfileView;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchExposureCountyExpansionRegressionTest {

    // Regression: ISSUE-004 —demand-backed county pages exposed only a generic official starting point.
    // Found by /qa on 2026-08-03
    // Report: .gstack/qa-reports/qa-report-septicpath-com-2026-08-03.md
    @Test
    void nextPriorityCountiesExposeVerifiedActionableWorkflows() {
        Map<String, String> expectedTiers = Map.of(
                "TN::bradley-county", "search_then_request",
                "TN::sullivan-county", "request_records",
                "TN::loudon-county", "search_then_request",
                "TN::maury-county", "search_then_request",
                "TN::jefferson-county", "office_help"
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
    void officialRoutesPreserveTheCountySpecificTruthBoundaries() {
        CountyAccessProfileView sullivan = CountyAccessProfileCatalog.find("TN::sullivan-county");
        assertEquals("mailto:TDEC.Johnsoncity.EFO@tn.gov", sullivan.primaryUrl());
        assertTrue(sullivan.requiredInputs().contains("Original owner name"));
        assertTrue(sullivan.requiredInputs().contains("Previous owner name"));

        CountyAccessProfileView loudon = CountyAccessProfileCatalog.find("TN::loudon-county");
        assertTrue(loudon.summary().contains("septicsystem.files@tn.gov"));
        assertTrue(loudon.limitation().contains("blank online search"));

        CountyAccessProfileView jefferson = CountyAccessProfileCatalog.find("TN::jefferson-county");
        assertTrue(jefferson.limitation().contains("conflicting new-permit prices"));
        assertTrue(jefferson.expectedArtifacts().contains("Septic permit and final approval"));
    }

    @Test
    void searchSnippetsDescribeDifferentCountyJobsInsteadOfRepeatingTheStateTemplate() throws IOException {
        String countyPages = Files.readString(Path.of("data/raw/county_records_pages.json"));

        for (String title : new String[]{
                "Bradley County TN Septic Permit Lookup and TDEC Records",
                "Sullivan County TN Septic Layout and Records Request",
                "Loudon County TN Septic Records Search and Permit Files",
                "Maury County TN Septic Permit Lookup and TDEC Records",
                "Jefferson County TN Septic Permits, Final Approval and Records"
        }) {
            assertTrue(countyPages.contains("\"title\": \"" + title + "\""), title);
        }

        assertTrue(countyPages.contains("property address, subdivision, original owner, and previous owner"));
        assertTrue(countyPages.contains("conflicting fee notice"));
        assertTrue(countyPages.contains("county-published records email and Knoxville field-office fallback"));
    }
}
