package com.example.septic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(properties = {"app.storage.root=./build/test-storage", "app.site.base-url=https://example.test"})
@AutoConfigureMockMvc
class ResearchAssetsSearchTest {
    @Autowired MockMvc mvc;

    private String html(String path) throws Exception {
        var response = mvc.perform(get(path)).andReturn().getResponse();
        assertThat(response.getStatus()).as(path).isEqualTo(200);
        return response.getContentAsString();
    }

    @Test void platformGuideIsDiscoverableAndSeparatesSearchFromReview() throws Exception {
        String guide = html("/septicsearch-records/");
        assertThat(guide).contains("href=\"https://example.test/septicsearch-records/\"", "content=\"index,follow\"",
                "https://septicsearch.com/property-records", "https://septicsearch.com/faq",
                "Franklin County", "not a complete national database", "intent=missing", "intent=review",
                "mode=review", "source=%2Fsepticsearch-records%2F", "data-record-help-cta");
        assertThat(html("/sitemap.xml")).contains("<loc>https://example.test/septicsearch-records/</loc><lastmod>2026-10-11</lastmod>");
        for (String path : new String[]{"/no-septic-records-found/", "/florida-ostds-permit-lookup/", "/onlinerme-septic-records/", "/official-septic-lookup-tools/"}) {
            assertThat(html(path)).as(path).contains("/septicsearch-records/");
        }
    }

    @Test void comparisonsPreserveStagesAndDoNotInventCapacity() throws Exception {
        assertThat(html("/how-to-read-septic-as-built/")).contains("id=\"permit-vs-as-built\"", "300 ft", "200 ft",
                "Do not average", "id=\"tank-size-and-bedrooms\"", "not directly stated");
        assertThat(html("/septic-bedroom-permit-checker/")).contains("capacity-source-example", "invented bedroom approval");
        assertThat(html("/septic-as-built-records/")).contains("permit-vs-as-built");
        assertThat(html("/septic-permit-process/")).contains("permit-vs-as-built");
        assertThat(html("/septic-permit-process/")).contains("id=\"design-final-case\"");
        assertThat(html("/septic-tank-location-records/")).contains("id=\"addition-file-case\"");
    }

    @Test void localRoutesAreSpecificAndAccessErrorsAreNotNoRecord() throws Exception {
        String davidson = html("/septic-records-checklist/tennessee/davidson-county/");
        assertThat(davidson).contains("id=\"local-archive-search\"", "HealthCode", "leading and trailing zeros", "How Many Bedrooms?");
        assertThat(davidson).doesNotContain("Start with the TDEC SSDS permits hub", "Open the TDEC SSDS permits hub first");
        String stCroix = html("/septic-records-checklist/wisconsin/st-croix-county/");
        assertThat(stCroix).contains("id=\"local-archive-search\"", "SAN", "Maintenance", "access failure is not a no-record result");
        assertThat(html("/tdec-septic-records/")).contains("/septic-records-checklist/tennessee/davidson-county/#local-archive-search");
        assertThat(html("/septic-records-checklist/tennessee/blount-county/")).doesNotContain("id=\"local-archive-search\"");
    }
}
