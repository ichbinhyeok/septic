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
        assertTrue(access.countySpecific());
        assertEquals("county_specific", access.profileScope());
        assertEquals("Search first, request if missing", access.capabilityLabel());
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
        assertTrue(sanBernardino.primaryUrl().contains("nextrequest.com/requests/new"));
        CountyAcquisitionProfileView sanBernardinoAcquisition =
                CountyAcquisitionProfileCatalog.find("CA::san-bernardino-county");
        assertEquals("official_portal", sanBernardinoAcquisition.acquisitionMethod());
        assertFalse(sanBernardinoAcquisition.officialFieldPackVerified());
        assertTrue(sanBernardinoAcquisition.archivedOfficialFieldPackVerified());
        assertTrue(sanBernardinoAcquisition.requiredFields().size() >= 10);
        assertFalse(sanBernardinoAcquisition.officialRecipientVerified());
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

    @Test
    void frederickUsesTheVerifiedLiveInformationRequestFields() {
        CountyAcquisitionProfileView profile =
                CountyAcquisitionProfileCatalog.find("MD::frederick-county");

        assertEquals("official_portal", profile.acquisitionMethod());
        assertTrue(profile.officialFieldPackVerified());
        assertTrue(profile.officialRecipientVerified());
        assertTrue(profile.officialUsesPropertyAddress());
        assertTrue(profile.officialUsesParcelIdentifier());
        assertEquals(8, profile.requiredFields().size());
        assertEquals("InformationResearchRequests@Frederickcountymd.gov", profile.recipientEmail());
        assertTrue(profile.fields().stream().anyMatch(field -> "previousOwners".equals(field.key()) && field.required()));
        assertTrue(profile.fields().stream().anyMatch(field -> "wellTag".equals(field.key()) && !field.required()));
    }

    @Test
    void gscFourthWaveUsesVerifiedLiveDoorsAndPreservesBlockedRouteBoundaries() {
        CountyAccessProfileView gloucester = CountyAccessProfileCatalog.find("NJ::gloucester-county");
        CountyAccessProfileView princeGeorges = CountyAccessProfileCatalog.find("MD::prince-georges-county");
        CountyAccessProfileView adams = CountyAccessProfileCatalog.find("CO::adams-county");
        CountyAccessProfileView mahoning = CountyAccessProfileCatalog.find("OH::mahoning-county");
        CountyAccessProfileView wilson = CountyAccessProfileCatalog.find("TN::wilson-county");
        CountyAccessProfileView sevier = CountyAccessProfileCatalog.find("TN::sevier-county");
        CountyAccessProfileView montgomery = CountyAccessProfileCatalog.find("TN::montgomery-county");
        CountyAccessProfileView guilford = CountyAccessProfileCatalog.find("NC::guilford-county");

        assertTrue(gloucester.primaryUrl().contains("Open-Records-Request-Form-OPRA"));
        assertTrue(princeGeorges.primaryUrl().contains("lookseerecords"));
        assertTrue(adams.primaryUrl().contains("experience.arcgis.com"));
        assertEquals("mailto:info@mahoninghealth.org", mahoning.secondaryUrl());
        assertTrue(wilson.primaryUrl().contains("formstack.com/forms/public_records_request"));
        assertEquals("tel:865-429-1766", sevier.primaryUrl());
        assertFalse(sevier.primaryUrl().contains("request_for_information_ssds"));
        assertTrue(montgomery.primaryUrl().contains("formstack.com/forms/public_records_request"));
        assertEquals("tel:336-641-7613", guilford.primaryUrl());
    }

    @Test
    void gscFourthWaveOnlyMarksFieldsVerifiedWhenTheLiveFormWasInspected() {
        CountyAcquisitionProfileView gloucester =
                CountyAcquisitionProfileCatalog.find("NJ::gloucester-county");
        CountyAcquisitionProfileView princeGeorges =
                CountyAcquisitionProfileCatalog.find("MD::prince-georges-county");
        CountyAcquisitionProfileView adams =
                CountyAcquisitionProfileCatalog.find("CO::adams-county");
        CountyAcquisitionProfileView wilson =
                CountyAcquisitionProfileCatalog.find("TN::wilson-county");
        CountyAcquisitionProfileView sevier =
                CountyAcquisitionProfileCatalog.find("TN::sevier-county");
        CountyAcquisitionProfileView montgomery =
                CountyAcquisitionProfileCatalog.find("TN::montgomery-county");
        CountyAcquisitionProfileView guilford =
                CountyAcquisitionProfileCatalog.find("NC::guilford-county");

        assertTrue(gloucester.officialFieldPackVerified());
        assertEquals("official_portal", gloucester.acquisitionMethod());
        assertTrue(princeGeorges.officialFieldPackVerified());
        assertEquals("official_search", princeGeorges.acquisitionMethod());
        assertTrue(adams.officialFieldPackVerified());
        assertEquals("official_search", adams.acquisitionMethod());
        assertTrue(wilson.officialFieldPackVerified());
        assertEquals(9, wilson.requiredFields().size());
        assertTrue(montgomery.officialFieldPackVerified());
        assertEquals(9, montgomery.requiredFields().size());
        assertFalse(sevier.officialFieldPackVerified());
        assertTrue(sevier.archivedOfficialFieldPackVerified());
        assertTrue(sevier.hasPreparedFieldPack());
        assertEquals(2, sevier.requiredFields().size());
        assertEquals("envirhealth@seviercountytn.org", sevier.recipientEmail());
        assertEquals("865-429-1965", sevier.publishedFaxPendingIntakeConfirmation());
        assertTrue(sevier.requestTemplate().contains("Is the previously published Request for Information form still current?"));
        assertFalse(guilford.officialFieldPackVerified());
    }

    @Test
    void northCarolinaExposureRoutesNowCarryTheRightPreparationBoundary() {
        CountyAcquisitionProfileView buncombe = CountyAcquisitionProfileCatalog.find("NC::buncombe-county");
        CountyAcquisitionProfileView wake = CountyAcquisitionProfileCatalog.find("NC::wake-county");
        CountyAcquisitionProfileView union = CountyAcquisitionProfileCatalog.find("NC::union-county");
        CountyAcquisitionProfileView pitt = CountyAcquisitionProfileCatalog.find("NC::pitt-county");
        CountyAcquisitionProfileView pender = CountyAcquisitionProfileCatalog.find("NC::pender-county");

        assertFalse(buncombe.officialFieldPackVerified());
        assertEquals("official_search", buncombe.acquisitionMethod());
        assertTrue(buncombe.fields().stream().anyMatch(field -> "permitCaseNumber".equals(field.key())));

        assertTrue(wake.officialFieldPackVerified());
        assertEquals("official_search", wake.acquisitionMethod());
        assertTrue(wake.requiresAddressOrParcel());
        assertTrue(wake.fields().stream().anyMatch(field -> "realEstateId".equals(field.key())));

        assertFalse(union.officialFieldPackVerified());
        assertEquals("official_portal", union.acquisitionMethod());
        assertTrue(union.fields().stream().anyMatch(field -> "projectType".equals(field.key())));

        assertFalse(pitt.officialFieldPackVerified());
        assertEquals("official_search", pitt.acquisitionMethod());
        assertTrue(pitt.fields().stream().anyMatch(field -> "sitePlanReady".equals(field.key())));

        assertTrue(pender.officialFieldPackVerified());
        assertEquals("official_pdf", pender.acquisitionMethod());
        assertEquals(5, pender.requiredFields().size());
        assertTrue(pender.officialUsesPropertyAddress());
        assertTrue(pender.officialUsesParcelIdentifier());
        assertTrue(pender.requiresAddressOrParcel());
    }

    @Test
    void workflowRegistryRejectsCatalogDriftAndJoinsPublishedProfiles() {
        CountyWorkflowRegistry.validateCatalogs();

        CountyAccessProfileView access = CountyAccessProfileCatalog.find("NC::wake-county");
        CountyAcquisitionProfileView acquisition = CountyAcquisitionProfileCatalog.find("NC::wake-county");
        assertEquals(access.countyKey(), acquisition.countyKey());
    }
}
