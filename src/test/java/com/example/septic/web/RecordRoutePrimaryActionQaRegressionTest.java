package com.example.septic.web;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecordRoutePrimaryActionQaRegressionTest {

    // Regression: ISSUE-003 — St. Mary's presented a parcel GIS as if it produced septic records.
    // Found by /qa on 2026-08-06
    // Report: .gstack/qa-reports/records-entry-2026-08-06/qa-report-septicpath-com-2026-08-06.md
    @Test
    void stMarysUsesTheOfficialPiaRequestBeforeTheParcelGis() {
        CountyAccessProfileView access = CountyAccessProfileCatalog.find("MD::st-marys-county");
        CountyAcquisitionProfileView acquisition =
                CountyAcquisitionProfileCatalog.find("MD::st-marys-county");

        assertEquals("official_request", access.mode());
        assertTrue(access.primaryUrl().contains("Public-Information-Request-Form"));
        assertTrue(access.secondaryUrl().contains("experience.arcgis.com"));
        assertEquals("official_pdf", acquisition.acquisitionMethod());
        assertFalse(acquisition.fallbackFieldPack());
    }

    // Regression: ISSUE-004 — known 403 portals were still the primary CTA.
    // Found by /qa on 2026-08-06
    // Report: .gstack/qa-reports/records-entry-2026-08-06/qa-report-septicpath-com-2026-08-06.md
    @Test
    void knownBlockedPortalsAreNotPrimaryActions() {
        CountyAccessProfileView princeWilliam = CountyAccessProfileCatalog.find("VA::prince-william-county");
        CountyAccessProfileView tarrant = CountyAccessProfileCatalog.find("TX::tarrant-county");
        CountyAccessProfileView sanBernardino = CountyAccessProfileCatalog.find("CA::san-bernardino-county");

        assertTrue(princeWilliam.primaryUrl().contains("onsite-sewage-and-water-services"));
        assertFalse(princeWilliam.primaryUrl().contains("lfportal"));
        assertTrue(tarrant.primaryUrl().endsWith("/ossf.html"));
        assertFalse(tarrant.primaryUrl().contains("justfoia"));
        assertEquals("tel:800-442-2283", sanBernardino.primaryUrl());
        assertFalse(sanBernardino.primaryUrl().contains("nextrequest"));
    }
}
