package com.example.septic;

import com.example.septic.web.CountyAccessProfileCatalog;
import com.example.septic.web.CountyAccessProfileView;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PriorityCountyAccessRegressionTest {

    // Regression: ISSUE-003 — high-opportunity counties with specific official workflows rendered as generic starting points.
    // Found by /qa on 2026-08-02
    // Report: .gstack/qa-reports/qa-report-septicpath-com-2026-08-02.md
    @Test
    void priorityCountyRoutesExposeTheirVerifiedCapability() {
        Map<String, String> expectedTiers = Map.of(
                "TN::cumberland-county", "search_then_request",
                "NC::iredell-county", "search_now",
                "IN::porter-county", "office_help",
                "SC::horry-county", "office_help"
        );

        expectedTiers.forEach((countyKey, tier) -> {
            CountyAccessProfileView profile = CountyAccessProfileCatalog.find(countyKey);
            assertNotNull(profile, countyKey);
            assertTrue(profile.countySpecific(), countyKey);
            assertEquals(tier, profile.capabilityTier(), countyKey);
        });
    }

    @Test
    void stateSearchModeIsPresentedAsSearchWithFallback() {
        CountyAccessProfileView profile = CountyAccessProfileCatalog.find("TN::cumberland-county");

        assertEquals("State search with official fallback", profile.officialAccessLabel());
        assertEquals("direct", profile.tone());
    }
}
