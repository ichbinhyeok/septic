package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class PremiumDesignRefinementRegressionTest {

    @Test
    void homeNamesSepticRecordsAndKeepsSecondaryDiscoveryCompact() throws IOException {
        String template = Files.readString(Path.of("src/main/jte/pages/home.jte"));

        assertThat(template).contains("searches official septic record systems");
        assertThat(template).doesNotContain("class=\"home-ed-archive\"");
        assertThat(template).doesNotContain("class=\"home-ed-routes\"");
        assertThat(template).contains("See completed investigations");
        assertThat(template).contains("Browse state guides");
    }

    @Test
    void countyIndexRemovesDuplicatePromotionsAndCollapsesSearchPreparation() throws IOException {
        String template = Files.readString(Path.of("src/main/jte/pages/national-records-page.jte"));

        assertThat(template).contains("national-records-page--county-index");
        assertThat(template).contains("class=\"national-records-method__disclosure\"");
        assertThat(template).contains("What information should I gather?");
        assertThat(template).contains("!\"septic-records-by-county\".equals(contentPage.slug())");
    }

    @Test
    void footerLabelsAreNavigationLabelsRatherThanPageHeadings() throws IOException {
        String template = Files.readString(Path.of("src/main/jte/layouts/app.jte"));

        assertThat(template).contains("<p class=\"footer-heading\">Start here</p>");
        assertThat(template).contains("<p class=\"footer-heading\">Explore</p>");
        assertThat(template).contains("<p class=\"footer-heading\">Standards</p>");
        assertThat(template).doesNotContain("<h2 class=\"footer-heading\">");
    }

    @Test
    void recurringControlsMeetTheMinimumTouchTarget() throws IOException {
        String appCss = Files.readString(Path.of("src/main/resources/static/app.css"));
        String workflowsCss = Files.readString(Path.of("src/main/resources/static/workflows.css"));
        String pagesCss = Files.readString(Path.of("src/main/resources/static/pages.css"));

        assertThat(appCss).contains(".button--quiet { min-height: 44px;");
        assertThat(appCss).contains(".footer-list a { display: inline-flex; min-height: 44px;");
        assertThat(workflowsCss).contains(".record-finder__outcomes button { min-height: 44px;");
        assertThat(pagesCss).contains(".tdec-service-qualifier__choices .button { min-height: 44px;");
    }
}
