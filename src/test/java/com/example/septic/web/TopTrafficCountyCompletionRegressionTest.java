package com.example.septic.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TopTrafficCountyCompletionRegressionTest {

    @Test
    void everySearchConsoleCountyHasAResearchedOfficialCompletionLane() throws Exception {
        JsonNode root = new ObjectMapper().readTree(
                Files.readString(Path.of("data", "raw", "search_response_targets.json"))
        );
        List<String> countyKeys = new ArrayList<>();
        root.path("targets").forEach(target -> {
            if ("county_records".equals(target.path("targetType").asText())) {
                countyKeys.add(target.path("key").asText());
            }
        });

        assertEquals(30, countyKeys.size(), "The reviewed GSC county cohort changed; re-audit every added route.");

        for (String countyKey : countyKeys) {
            CountyAccessProfileView access = CountyAccessProfileCatalog.find(countyKey);
            CountyAcquisitionProfileView acquisition = CountyAcquisitionProfileCatalog.find(countyKey);

            assertNotNull(access, countyKey + " must not fall back to generic county guidance");
            assertNotNull(acquisition, countyKey + " must have a document-acquisition handoff");
            assertTrue(access.countySpecific(), countyKey);
            assertFalse(access.primaryUrl().isBlank(), countyKey);
            assertFalse(access.completionLabel().isBlank(), countyKey);
            assertFalse(access.steps().isEmpty(), countyKey);
            assertFalse(acquisition.userOnlyAction().isBlank(), countyKey);
            assertFalse(acquisition.requestedDocuments().isEmpty(), countyKey);
            assertFalse(
                    "official_route".equals(acquisition.acquisitionMethod()),
                    countyKey + " still has only a defensive generic route"
            );
        }
    }

    @Test
    void newCompletionLanesPreserveTheirActualOfficialIntakeType() {
        assertEquals("official_search", acquisition("NC::johnston-county").acquisitionMethod());
        assertEquals("official_search", acquisition("TX::comal-county").acquisitionMethod());
        assertEquals("official_pdf", acquisition("IN::brown-county").acquisitionMethod());
        assertFalse(acquisition("IN::brown-county").officialFieldPackVerified());

        for (String key : List.of("IN::grant-county", "IN::porter-county", "IN::monroe-county")) {
            assertEquals("official_contact", acquisition(key).acquisitionMethod(), key);
            assertTrue(acquisition(key).officialRecipientVerified(), key);
        }

        for (String key : List.of(
                "SC::horry-county", "SC::greenville-county", "SC::spartanburg-county",
                "SC::charleston-county", "SC::anderson-county"
        )) {
            CountyAccessProfileView access = CountyAccessProfileCatalog.find(key);
            CountyAcquisitionProfileView profile = acquisition(key);
            assertTrue(access.primaryUrl().endsWith("/D-2295.pdf"), key);
            assertEquals("official_pdf", profile.acquisitionMethod(), key);
            assertTrue(profile.officialFieldPackVerified(), key);
            assertTrue(profile.officialRecipientVerified(), key);
            assertEquals("foi@des.sc.gov", profile.recipientEmail(), key);
            assertTrue(profile.methodInstruction().contains("blocks the PDF by region"), key);
            assertTrue(profile.fields().stream().anyMatch(field -> "recordDescription".equals(field.key()) && field.required()), key);
        }
    }

    private CountyAcquisitionProfileView acquisition(String key) {
        return CountyAcquisitionProfileCatalog.find(key);
    }
}
