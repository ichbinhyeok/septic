package com.example.septic.web;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class PriorityTennesseeHandoffRegressionTest {

    // Regression: ISSUE-COUNTY-001 - Bradley and Cumberland lacked the guided TDEC return workspace.
    // Found by /qa on 2026-08-05.
    // Report: .gstack/qa-reports/qa-report-septicpath-com-priority-counties-2026-08-05.md
    @Test
    void priorityTdecCountiesShareTheSearchAndFieldOfficeHandoff() throws IOException {
        String template = Files.readString(Path.of("src/main/jte/pages/county-records-page.jte"));

        for (String countyKey : new String[]{
                "TN::sumner-county",
                "TN::rutherford-county",
                "TN::bradley-county",
                "TN::cumberland-county"
        }) {
            assertTrue(template.contains("\"" + countyKey + "\".equals(countyAccessProfile.countyKey())"), countyKey);
        }
        assertTrue(template.contains("continue through the verified regional field office"));
        assertTrue(template.contains("permit, layout, closeout, repair file, or written response"));
    }
}
