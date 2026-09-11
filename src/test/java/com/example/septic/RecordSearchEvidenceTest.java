package com.example.septic;

import com.example.septic.service.ResearchDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(properties = {"app.storage.root=./build/test-storage", "app.site.base-url=https://example.test"})
@AutoConfigureMockMvc
class RecordSearchEvidenceTest {
    @Autowired MockMvc mvc;
    @Autowired ResearchDataService data;

    @Test
    void curatedSearchInstructionsHaveResolvableSourcesAndPublicRoutes() throws Exception {
        var pages = data.getPublicCountyRecordsPages().stream().filter(p -> p.searchGuide() != null).toList();
        assertThat(pages).hasSize(5);
        for (var page : pages) {
            var guide = page.searchGuide();
            assertThat(guide.steps()).isNotEmpty();
            assertThat(guide.sourceIds()).isNotEmpty();
            for (var id : guide.sourceIds()) assertThat(data.findSource(id)).isPresent();
            var state = data.findStateByCode(page.stateCode()).orElseThrow();
            var response = mvc.perform(get(page.path(state.slug()))).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getContentAsString()).contains("id=\"record-search-instructions\"", "Years covered", guide.reviewedAt());
        }
    }

    @Test
    void indexExportsSourceBackedCoverageWithoutInventingCoverageForOtherCounties() throws Exception {
        var csv = mvc.perform(get("/septic-records-access-index.csv")).andReturn().getResponse();
        assertThat(csv.getStatus()).isEqualTo(200);
        var lines = csv.getContentAsString().lines().toList();
        assertThat(lines).hasSize(326);
        assertThat(lines.getFirst()).contains("search_coverage", "search_instruction_source_urls");
        assertThat(lines.stream().skip(1).filter(line -> line.endsWith(",\"\",\"\",\"\",\"\",\"\",\"\""))).hasSize(320);
        assertThat(csv.getContentAsString()).contains("permit-search-tips", "Faq.aspx?QID=185");
        var html = mvc.perform(get("/septic-records-access-index/")).andReturn().getResponse().getContentAsString();
        assertThat(html).contains("record-search-comparison", "Detailed search instructions are available for these 5 counties");
    }

    @Test
    void drawingDistinguishesHistoricalEvidenceFromCompletedInstallation() throws Exception {
        var html = mvc.perform(get("/septic-as-built-records/")).andReturn().getResponse().getContentAsString();
        assertThat(html).contains("id=\"read-a-septic-drawing\"", "tn-historical-sketch-anonymized.png",
                "225 feet", "not a drain-field area", "not automatically an as-built", "as_built_drawing_help");
        var state = mvc.perform(get("/septic-records-checklist/north-carolina/")).andReturn().getResponse();
        assertThat(state.getStatus()).isEqualTo(200);
        assertThat(state.getContentAsString()).doesNotContain("Search capture", "before the page becomes another broad state overview");
    }
}
