package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StJosephConditionalDeliveryRegressionTest {

    // Regression: ISSUE-001 — Email PDF appeared complete without a return email address.
    // Found by /qa on 2026-08-11.
    // Report: .gstack/qa-reports/qa-report-septicpath-com-2026-08-11-multi-persona.md
    @Test
    void selectedDeliveryMethodRequiresItsMatchingReturnDetail() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/app.js"))
                .replace("\r\n", "\n");

        assertTrue(script.contains("countyKey !== \"IN::st-joseph-county\""));
        assertTrue(script.contains("[\"Email PDF\", \"requesterEmail\"]"));
        assertTrue(script.contains("[\"Fax\", \"requesterFax\"]"));
        assertTrue(script.contains("[\"Pick up\", \"pickupDate\"]"));
        assertTrue(script.contains("conditional?.requiredKey === input?.dataset.countyAcquisitionField"));
        assertTrue(script.contains("syncConditionalAcquisitionRequirement();\n                    renderAcquisitionPreview();"));
        assertTrue(script.contains("optionalGroup.open = true"));
    }
}
