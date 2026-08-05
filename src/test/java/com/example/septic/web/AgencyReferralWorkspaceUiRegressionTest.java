package com.example.septic.web;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class AgencyReferralWorkspaceUiRegressionTest {

    // Regression: ISSUE-DOC-006 - a referral did not preserve routing evidence or stop the no-record loop.
    // Found by /qa on 2026-08-05.
    // Report: .gstack/qa-reports/qa-report-septicpath-com-document-workspace-2026-08-05.md
    @Test
    void agencyReferralRoutesTheUserToTheResponsibleFileOwner() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/app.js"));
        int renderStart = script.indexOf("function renderPropertyWorkspace(summary)");
        int renderEnd = script.indexOf("function restoreSessionWorkspace()", renderStart);
        String render = script.substring(renderStart, renderEnd);

        assertTrue(script.contains("\"agency_referral\""));
        assertTrue(render.contains("The request was routed to another office"));
        assertTrue(render.contains("This proves routing only"));
        assertTrue(render.contains("Not searched by this office"));
        assertTrue(render.contains("Resolve the responsible file owner"));
        assertTrue(render.contains("official_record_referral_reviewed"));
    }
}
