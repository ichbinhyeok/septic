package com.example.septic.service;

import com.example.septic.web.OfficialCountyPdfForm;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class OfficialCountyPdfServiceRegressionTest {

    // Regression: ISSUE-004 — Alamance record choices were not transferred to the original PDF.
    // Found by /qa on 2026-07-28
    // Report: .gstack/qa-reports/qa-report-localhost-8094-2026-07-28.md
    @Test
    void mapsAlamanceDocumentSelectionsToTheCountyPdfFields() {
        OfficialCountyPdfService service = new OfficialCountyPdfService();

        Map<String, String> values = service.countyValues(new OfficialCountyPdfForm(
                "NC::alamance-county",
                "123 Main Street",
                "123456",
                Map.of(
                        "wellPermitRequested", "Do not request",
                        "septicPermitRequested", "Request this record",
                        "waterSampleRequested", "Do not request",
                        "soilEvaluationRequested", "Request this record"
                )
        ));

        assertEquals("X", values.get("Copy of septic permit"));
        assertEquals("X", values.get("Copy of soil evaluation"));
        assertFalse(values.containsKey("Copy of well permit"));
        assertFalse(values.containsKey("Copy of existing water sample results"));
    }

    // Regression: ISSUE-004 — Denton delivery and notification choices stayed blank in the original PDF.
    // Found by /qa on 2026-07-28
    // Report: .gstack/qa-reports/qa-report-localhost-8094-2026-07-28.md
    @Test
    void mapsDentonCopyAndInspectionChoicesToTheCorrectCountyCheckboxes() {
        OfficialCountyPdfService service = new OfficialCountyPdfService();

        Map<String, String> pickup = service.countyValues(new OfficialCountyPdfForm(
                "TX::denton-county",
                "",
                "",
                Map.of(
                        "deliveryChoice", "Receive copies and pick them up",
                        "notificationChoice", "Email"
                )
        ));
        assertEquals("Yes", pickup.get("Check Box1"));
        assertEquals("Yes", pickup.get("Check Box2"));
        assertEquals("Yes", pickup.get("Check Box3"));
        assertFalse(pickup.containsKey("Check Box4"));
        assertFalse(pickup.containsKey("Check Box5"));

        Map<String, String> inspection = service.countyValues(new OfficialCountyPdfForm(
                "TX::denton-county",
                "",
                "",
                Map.of(
                        "deliveryChoice", "Inspect originals",
                        "notificationChoice", "Postal service"
                )
        ));
        assertEquals("Yes", inspection.get("Check Box5"));
        assertEquals("Yes", inspection.get("Check Box7"));
        assertFalse(inspection.containsKey("Check Box6"));
    }
}
