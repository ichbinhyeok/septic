package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class QaStateLandingAddressBoundaryRegressionTest {

    // Regression: ISSUE-001 state landings presented another state's valid route without explaining
    // that the surrounding state guide no longer applied. Found by /qa on 2026-08-06.
    // Report: .gstack/qa-reports/qa-report-septicpath-com-2026-08-06.md
    @Test
    void crossStateAddressResultKeepsTheMatchedRouteAndMarksTheStateBoundary() throws IOException {
        String script = Files.readString(Path.of("src/main/resources/static/app.js"));

        assertThat(script).contains("resolvedStateCode !== expectedStateCode");
        assertThat(script).contains("Address is in another state");
        assertThat(script).contains("the current state guide does not apply");
        assertThat(script).contains("addressRecordFinderStateMismatch");
        assertThat(script).contains("address_search_state_mismatch");
        assertThat(script).contains("resolved_state_code: resolvedStateCode");
    }
}
