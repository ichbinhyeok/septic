package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class HomeDesktopFirstViewportQaRegressionTest {

    // Regression: ISSUE-001 - the desktop home viewport showed the promise but hid the address CTA below the fold.
    // Found by /qa on 2026-08-06.
    // Report: .gstack/qa-reports/records-entry-2026-08-06/qa-report-septicpath-com-2026-08-06.md
    @Test
    void homeUsesACompactDesktopWorkspaceBeforeTheMobileBreakpoint() throws IOException {
        String css = Files.readString(Path.of("src/main/resources/static/workflows.css"));

        int compactDesktop = css.indexOf(".home-hero .record-finder__intro { padding-block: 28px 22px; }");
        int responsiveLayer = css.indexOf("@layer responsive");

        assertThat(compactDesktop).isGreaterThan(0);
        assertThat(compactDesktop).isLessThan(responsiveLayer);
        assertThat(css).contains(".home-hero .record-finder__start-options button { min-height: 76px;");
        assertThat(css).contains(".home-hero .record-finder__workspace { padding-block: 18px 24px; }");
    }
}
