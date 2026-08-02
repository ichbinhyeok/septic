package com.example.septic;

import com.example.septic.web.CountyAccessProfileCatalog;
import com.example.septic.web.CountyAccessProfileView;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FourthSearchExposureCountyExpansionRegressionTest {

    @Test
    void northCarolinaDemandCountiesExposeCountySpecificExecutionWorkflows() {
        Map<String, String> expectedTiers = Map.of(
                "NC::buncombe-county", "search_then_request",
                "NC::wake-county", "search_then_request",
                "NC::union-county", "request_records",
                "NC::pitt-county", "search_then_request",
                "NC::pender-county", "request_records"
        );

        expectedTiers.forEach((countyKey, tier) -> {
            CountyAccessProfileView profile = CountyAccessProfileCatalog.find(countyKey);
            assertNotNull(profile, countyKey);
            assertTrue(profile.countySpecific(), countyKey);
            assertEquals(tier, profile.capabilityTier(), countyKey);
            assertTrue(profile.requiredInputs().size() >= 5, countyKey);
            assertTrue(profile.expectedArtifacts().size() >= 4, countyKey);
            assertTrue(profile.steps().size() >= 4, countyKey);
        });
    }

    @Test
    void searchCountiesUseTheCurrentOfficialPortalsAndSafeFallbacks() {
        CountyAccessProfileView buncombe = CountyAccessProfileCatalog.find("NC::buncombe-county");
        assertEquals("https://aca-prod.accela.com/buncombeconc/default.aspx", buncombe.primaryUrl());
        assertEquals("https://www.buncombenc.gov/456/Environmental-Health", buncombe.secondaryUrl());
        assertTrue(buncombe.limitation().contains("not proof"));
        assertTrue(buncombe.steps().get(2).contains("existing-system"));

        CountyAccessProfileView wake = CountyAccessProfileCatalog.find("NC::wake-county");
        assertEquals("https://permitsearch.wake.gov/", wake.primaryUrl());
        assertTrue(wake.secondaryUrl().contains("iMAPS%20Permit%20Search.pdf"));
        assertTrue(wake.requiredInputs().contains("Real Estate ID"));
        assertTrue(wake.limitation().contains("not an official no-record result"));

        CountyAccessProfileView pitt = CountyAccessProfileCatalog.find("NC::pitt-county");
        assertTrue(pitt.primaryUrl().contains("energovweb.tylerhost.net"));
        assertTrue(pitt.requiredInputs().contains("Site plan or plat and directions to the property"));
        assertTrue(pitt.expectedArtifacts().stream().anyMatch(value -> value.contains("repair area")));
    }

    @Test
    void requestCountiesExposePublishedFieldsWithoutInventingTurnaroundOrApproval() {
        CountyAccessProfileView union = CountyAccessProfileCatalog.find("NC::union-county");
        assertEquals("https://lfportal.unioncountync.gov/Forms/WellSepticPermitRequest", union.primaryUrl());
        assertTrue(union.summary().contains("pools"));
        assertTrue(union.limitation().contains("does not clear new construction"));

        CountyAccessProfileView pender = CountyAccessProfileCatalog.find("NC::pender-county");
        assertTrue(pender.requiredInputs().contains("Parcel Identification Number"));
        assertTrue(pender.requiredInputs().contains("Year built"));
        assertTrue(pender.limitation().contains("does not promise a fee or turnaround"));
        assertTrue(pender.limitation().contains("do not prove present system condition"));
    }
}
