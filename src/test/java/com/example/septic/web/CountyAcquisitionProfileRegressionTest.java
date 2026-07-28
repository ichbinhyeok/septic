package com.example.septic.web;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CountyAcquisitionProfileRegressionTest {

    // Regression: ISSUE-003 — Prince William showed a ready state without an address or GPIN.
    // Found by /qa on 2026-07-28
    // Report: .gstack/qa-reports/qa-report-localhost-8094-2026-07-28.md
    @Test
    void princeWilliamRequiresEitherAddressOrParcel() {
        CountyAcquisitionProfileView profile =
                CountyAcquisitionProfileCatalog.find("VA::prince-william-county");

        assertTrue(profile.requiresAddressOrParcel());
    }

    // Regression: ISSUE-001 — Knox routed users to a broken PDF instead of the live county form.
    // Found by /qa on 2026-07-28
    // Report: .gstack/qa-reports/qa-report-localhost-8094-2026-07-28.md
    @Test
    void knoxUsesTheVerifiedLivePortalFields() {
        CountyAccessProfileView access = CountyAccessProfileCatalog.find("TN::knox-county");
        CountyAcquisitionProfileView acquisition =
                CountyAcquisitionProfileCatalog.find("TN::knox-county");

        assertEquals("https://knoxcounty.jotform.com/team/eh/ssds-file-search-app", access.primaryUrl());
        assertEquals("official_portal", acquisition.acquisitionMethod());
        assertTrue(acquisition.officialFieldPackVerified());
        assertEquals(10, acquisition.requiredFields().size());
        assertFalse(acquisition.officialRecipientVerified());
    }

    // Regression: ISSUE-002 — Maricopa's paid form blocked the free search with unrelated required fields.
    // Found by /qa on 2026-07-28
    // Report: .gstack/qa-reports/qa-report-localhost-8094-2026-07-28.md
    @Test
    void maricopaKeepsPaidFieldsInAnOptionalFallbackPack() {
        CountyAcquisitionProfileView profile =
                CountyAcquisitionProfileCatalog.find("AZ::maricopa-county");

        assertEquals("official_search", profile.acquisitionMethod());
        assertTrue(profile.fallbackFieldPack());
        assertFalse(profile.officialUsesPropertyAddress());
        assertFalse(profile.requiresAddressOrParcel());
    }

    @Test
    void secondWaveRoutesUseRecordAcquisitionDoorsInsteadOfGenericGuides() {
        CountyAccessProfileView thurston = CountyAccessProfileCatalog.find("WA::thurston-county");
        CountyAccessProfileView harford = CountyAccessProfileCatalog.find("MD::harford-county");
        CountyAccessProfileView sanBernardino = CountyAccessProfileCatalog.find("CA::san-bernardino-county");
        CountyAccessProfileView cumberland = CountyAccessProfileCatalog.find("NC::cumberland-county");

        assertTrue(thurston.primaryUrl().contains("customsearch.aspx"));
        assertTrue(thurston.secondaryUrl().contains("/media/13802"));
        assertTrue(harford.primaryUrl().contains("PIA-Request-Form"));
        assertTrue(harford.limitation().contains("30-day"));
        assertEquals("https://ehs.sbcounty.gov/", sanBernardino.primaryUrl());
        assertTrue(cumberland.summary().contains("septic-layout request"));
    }

    @Test
    void thirdWaveRoutesPreserveEachOfficialSearchAndFallbackContract() {
        CountyAccessProfileView sanDiego = CountyAccessProfileCatalog.find("CA::san-diego-county");
        CountyAccessProfileView washtenaw = CountyAccessProfileCatalog.find("MI::washtenaw-county");
        CountyAccessProfileView gallatin = CountyAccessProfileCatalog.find("MT::gallatin-county");
        CountyAccessProfileView frederick = CountyAccessProfileCatalog.find("MD::frederick-county");
        CountyAccessProfileView stLouis = CountyAccessProfileCatalog.find("MN::st-louis-county");
        CountyAccessProfileView livingston = CountyAccessProfileCatalog.find("MI::livingston-county");
        CountyAccessProfileView hanover = CountyAccessProfileCatalog.find("VA::hanover-county");
        CountyAccessProfileView craven = CountyAccessProfileCatalog.find("NC::craven-county");

        assertTrue(sanDiego.secondaryUrl().contains("pra.sandiegocounty.gov/requests/new"));
        assertTrue(washtenaw.limitation().contains("street number"));
        assertTrue(gallatin.limitation().contains("No Images"));
        assertTrue(frederick.requiredInputs().contains("Current owner and previous owners back to 1950 or the installation year"));
        assertTrue(stLouis.primaryUrl().contains("landexplorer"));
        assertTrue(livingston.secondaryUrl().contains("CustomSearch.aspx"));
        assertTrue(hanover.limitation().contains("3-5 business days"));
        assertTrue(craven.secondaryUrl().contains("Request-For-Document-Septic-Wells"));
    }
}
