package com.example.septic;

import com.example.septic.web.RecordWorkStories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(properties = {"app.storage.root=./build/test-storage", "app.site.base-url=https://example.test"})
@AutoConfigureMockMvc
class RecordWorkStoriesTest {
    @Autowired MockMvc mvc;
    private String html(String path) throws Exception {
        var response = mvc.perform(get(path)).andReturn().getResponse();
        assertThat(response.getStatus()).as(path).isEqualTo(200);
        return response.getContentAsString();
    }
    @Test void reviewedStoriesHaveCanonicalRoutesSitemapAndWorkingImages() throws Exception {
        String sitemap = html("/sitemap.xml");
        for (var story : RecordWorkStories.ALL) {
            String body = html(story.path());
            assertThat(body).contains("href=\"https://example.test" + story.path() + "\"", "content=\"index,follow\"",
                "Reconstructed explanation", "diagrams are conceptual", "intent=review", "mode=review",
                "intent=missing", "data-record-help-cta", "source=%2Fseptic-record-brief-example%2F" + story.slug() + "%2F");
            assertThat(body).doesNotContain("/design-preview/", "Fox Vale", "Cultivator", "Scenic Hills", "@gmail",
                "17700001200", "0693-26-0894", "4545", "13 Fox", "112 Cultivator");
            assertThat(sitemap).contains("<loc>https://example.test" + story.path() + "</loc><lastmod>2026-10-11</lastmod>");
            var asset = mvc.perform(get("/images/work/" + story.image())).andReturn().getResponse();
            assertThat(asset.getStatus()).isEqualTo(200);
            assertThat(asset.getContentAsByteArray().length).isGreaterThan(1000);
        }
    }
    @Test void examplesKeepSourceStagesAndDeliveryBoundaries() throws Exception {
        assertThat(html("/septic-record-brief-example/davidson/")).contains("three added lines", "mixed", "two-page", "current");
        assertThat(html("/septic-record-brief-example/harnett/")).contains("without registration or an agency request", "1,000-gallon", "70 ft", "repair area");
        assertThat(html("/septic-record-brief-example/williamson/")).contains("Certificate of Completion", "370 linear ft", "two-page Record Brief", "Ambiguous");
        assertThat(mvc.perform(get("/septic-record-brief-example/unreviewed/")).andReturn().getResponse().getStatus()).isEqualTo(404);
    }
    @Test void searchEntrancesAndOverviewLinkToRelevantWork() throws Exception {
        assertThat(html("/how-to-read-septic-as-built/")).contains("/septic-record-brief-example/davidson/");
        assertThat(html("/no-septic-records-found/")).contains("/septic-record-brief-example/harnett/");
        assertThat(html("/septic-as-built-records/")).contains("/septic-record-brief-example/davidson/");
        assertThat(html("/how-to-find-septic-records-online/")).contains("/septic-record-brief-example/harnett/");
        String overview = html("/septic-record-brief-example/");
        for (var story : RecordWorkStories.ALL) assertThat(overview).contains(story.path());
    }
}
