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

class ThirdSearchExposureCountyExpansionRegressionTest {

    @Test
    void remainingDemandBackedCountiesExposeVerifiedExecutionWorkflows() {
        Map<String, String> expectedTiers = Map.of(
                "TN::anderson-county", "search_then_request",
                "NC::randolph-county", "search_then_request",
                "AL::tuscaloosa-county", "office_help",
                "AL::calhoun-county", "office_help",
                "SC::charleston-county", "search_then_request",
                "SC::greenville-county", "search_then_request",
                "SC::anderson-county", "search_then_request",
                "SC::spartanburg-county", "search_then_request"
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
    void countyRoutesPreserveTheirPublishedJurisdictionAndTruthBoundaries() {
        CountyAccessProfileView andersonTn = CountyAccessProfileCatalog.find("TN::anderson-county");
        assertTrue(andersonTn.limitation().contains("865-594-6035"));
        assertTrue(andersonTn.limitation().contains("blank viewer"));
        assertEquals(
                "https://www.tn.gov/environment/contacts/field-offices/knoxville.html",
                andersonTn.secondaryUrl()
        );

        CountyAccessProfileView randolph = CountyAccessProfileCatalog.find("NC::randolph-county");
        assertTrue(randolph.completionLabel().contains("Operation Permit"));
        assertTrue(randolph.limitation().contains("two residential bedrooms"));

        CountyAccessProfileView tuscaloosa = CountyAccessProfileCatalog.find("AL::tuscaloosa-county");
        assertEquals("tel:205-562-6900", tuscaloosa.primaryUrl());
        assertTrue(tuscaloosa.limitation().contains("no public parcel search"));

        CountyAccessProfileView calhoun = CountyAccessProfileCatalog.find("AL::calhoun-county");
        assertEquals("tel:256-237-4324", calhoun.primaryUrl());
        assertTrue(calhoun.summary().contains("small and large flow"));
    }

    @Test
    void southCarolinaCountiesUseDistinctParcelAnchorsAndRegionalRoutes() {
        Map<String, String> parcelUrls = Map.of(
                "SC::charleston-county", "https://prcweb.charlestoncounty.org/",
                "SC::greenville-county", "https://www.greenvillecounty.org/appsas400/RealProperty/",
                "SC::anderson-county", "https://www.andersoncountysc.org/departments-a-z/assessor/",
                "SC::spartanburg-county", "https://www.spartanburgcounty.gov/185/Geographic-Information-Systems"
        );

        parcelUrls.forEach((countyKey, url) -> {
            CountyAccessProfileView profile = CountyAccessProfileCatalog.find(countyKey);
            assertEquals("https://des.sc.gov/sites/des/files/Library/D-2295.pdf", profile.primaryUrl(), countyKey);
            assertEquals(url, profile.secondaryUrl(), countyKey);
            assertTrue(profile.limitation().contains("licensed septic contractor"), countyKey);
        });

        assertTrue(CountyAccessProfileCatalog.find("SC::charleston-county").steps().get(2).contains("Coastal"));
        assertTrue(CountyAccessProfileCatalog.find("SC::greenville-county").steps().get(2).contains("Piedmont II"));
        assertTrue(CountyAccessProfileCatalog.find("SC::anderson-county").steps().get(2).contains("Piedmont II"));
        assertTrue(CountyAccessProfileCatalog.find("SC::spartanburg-county").steps().get(2).contains("Piedmont I"));
    }

    @Test
    void sourceContentUsesCountySpecificSearchJobs() throws IOException {
        String countyPages = Files.readString(Path.of("data/raw/county_records_pages.json"));
        for (String title : new String[]{
                "Anderson County TN Septic Permit Search and TDEC Records",
                "Randolph County NC Septic Permits, Repairs and ePermits",
                "Tuscaloosa County AL Septic Permits and Records Contact",
                "Calhoun County AL Septic Permits and Environmental Office",
                "Charleston County SC Septic Permit and Final Inspection Records",
                "Greenville County South Carolina Septic Records and Permit Lookup",
                "Anderson County SC Septic Permit and Final Inspection Records",
                "Spartanburg County SC Septic Permit and Final Inspection Records"
        }) {
            assertTrue(countyPages.contains("\"title\": \"" + title + "\""), title);
        }
        assertTrue(countyPages.contains("parcel ID or TMS"));
        assertTrue(countyPages.contains("Piedmont II regional division"));
        assertTrue(countyPages.contains("different applications in the county ePermits workflow"));
    }
}
