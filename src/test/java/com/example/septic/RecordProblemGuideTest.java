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
class RecordProblemGuideTest {
    @Autowired MockMvc mvc;
    @Autowired ResearchDataService data;

    @Test
    void newSearchEntrancesArePublicCanonicalAndDiscoverable() throws Exception {
        String sitemap = mvc.perform(get("/sitemap.xml")).andReturn().getResponse().getContentAsString();
        for (String slug : new String[]{"no-septic-records-found", "how-to-read-septic-as-built"}) {
            assertThat(data.findPublicContentPage(slug)).isPresent();
            var response = mvc.perform(get("/" + slug + "/")).andReturn().getResponse();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getContentAsString()).contains(
                    "href=\"https://example.test/" + slug + "/\"",
                    "content=\"index,follow\"", "data-record-help-cta",
                    "/record-problem-guide.css");
            assertThat(sitemap).contains("https://example.test/" + slug + "/");
        }
        String finder = mvc.perform(get("/septic-record-finder/")).andReturn().getResponse().getContentAsString();
        assertThat(finder).contains("/no-septic-records-found/", "/how-to-read-septic-as-built/");
    }

    @Test
    void readingIllustrationLoadsWithoutReplacingAccessibleGuideLinks() throws Exception {
        String reading = mvc.perform(get("/how-to-read-septic-as-built/")).andReturn().getResponse().getContentAsString();
        assertThat(reading).contains("/images/guides/as-built-reading-v1.webp", "width=\"1536\"", "height=\"1024\"",
                "Not an actual property plan", "Not a property plan; not to scale",
                "href=\"#components\"", "href=\"#dimensions\"", "href=\"#field-lines\"", "href=\"#document-stage\"");
        assertThat(reading).doesNotContain("<svg viewBox=\"0 0 580 340\"");
        var image = mvc.perform(get("/images/guides/as-built-reading-v1.webp")).andReturn().getResponse();
        assertThat(image.getStatus()).isEqualTo(200);
        assertThat(image.getContentAsByteArray().length).isGreaterThan(1000);
    }

    @Test
    void eachGuidePreservesTheCorrectIntakeIntentAndSourcePage() throws Exception {
        String missing = mvc.perform(get("/no-septic-records-found/")).andReturn().getResponse().getContentAsString();
        assertThat(missing).contains("intent=missing", "source=%2Fno-septic-records-found%2F",
                "The official viewer will not load", "The office says it has no record");
        String reading = mvc.perform(get("/how-to-read-septic-as-built/")).andReturn().getResponse().getContentAsString();
        assertThat(reading).contains("intent=review", "mode=review", "source=%2Fhow-to-read-septic-as-built%2F",
                "Symbols vary by file", "225 linear feet", "/septic-as-built-records/#drawing-labels");
        var form = mvc.perform(get("/offer-prep-septic-file-check/")
                .param("intent", "review").param("mode", "review")
                .param("source", "/how-to-read-septic-as-built/")).andReturn();
        assertThat(form.getResponse().getStatus()).isEqualTo(200);
        assertThat(form.getResponse().getContentAsString()).contains("multipart/form-data", "intake-form");
    }
}
