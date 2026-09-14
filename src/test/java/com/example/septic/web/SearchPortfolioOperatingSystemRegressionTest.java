package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchPortfolioOperatingSystemRegressionTest {

    @Test
    void portfolioAuditUsesDemandProofAndEvidenceQueueInsteadOfPageVolume() throws Exception {
        String tool = Files.readString(Path.of("tools/search-portfolio.mjs"));
        assertTrue(tool.contains("demandTarget"));
        assertTrue(tool.contains("operationalProof"));
        assertTrue(tool.contains("evidence_queue"));
        assertTrue(tool.contains("Do not expand this page yet"));
    }

    @Test
    void operatingGuideRequiresImmutableSignalsAndFullComparisonWindows() throws Exception {
        String guide = Files.readString(Path.of("docs/SEO_OPERATING_SYSTEM.md"));
        assertTrue(guide.contains("immutable operations `growth_signal`"));
        assertTrue(guide.contains("Compare complete 28-day windows"));
        assertTrue(guide.contains("Keep one search promise per page"));
    }
}
