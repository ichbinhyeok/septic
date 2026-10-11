package com.example.septic;

import com.example.septic.web.CaseResearchGuides;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(properties = {"app.storage.root=./build/test-storage", "app.site.base-url=https://example.test"})
@AutoConfigureMockMvc
class CaseResearchGuidesTest {
    @Autowired MockMvc mvc;
    private String html(String path) throws Exception {
        var response = mvc.perform(get(path)).andReturn().getResponse();
        assertThat(response.getStatus()).as(path).isEqualTo(200);
        return response.getContentAsString();
    }
    @Test void guidesAreIndexedLinkedAndIllustrationsAreExplicit() throws Exception {
        String sitemap = html("/sitemap.xml");
        String overview = html("/septic-record-brief-example/");
        for (var guide : CaseResearchGuides.ALL) {
            String body = html(guide.path());
            assertThat(body).contains("href=\"https://example.test" + guide.path() + "\"", "content=\"index,follow\"",
                "Illustrative research notes, not an official document", "intent=" + guide.intent(), "data-record-help-cta", guide.sourceUrl());
            assertThat(body).doesNotContain("@gmail", "Serenity", "Talmuth", "storage/operations", "Parcel ID", "Gwen");
            assertThat(body).contains(guide.headline().replace("'", "&#39;"), "case_research_hero", "The useful answer", "What you receive", "Read the document details");
            assertThat(sitemap).contains("<loc>https://example.test" + guide.path() + "</loc><lastmod>2026-10-11</lastmod>");
            assertThat(overview).contains(guide.path());
            assertThat(mvc.perform(get("/images/guides/" + guide.image())).andReturn().getResponse().getStatus()).isEqualTo(200);
        }
    }
    @Test void examplesDoNotTurnMissingEvidenceIntoApprovalOrFailure() throws Exception {
        assertThat(html("/septic-repair-records/")).contains("Neither gap proves", "historical repair", "completion evidence");
        assertThat(html("/septic-records-before-pool/")).contains("210 feet", "200 feet", "separate 50-foot", "not a pool siting approval");
        assertThat(html("/septic-final-approval-records/")).contains("historical snapshot", "not the permit", "missing portal field alone does not prove");
        assertThat(html("/how-to-read-septic-as-built/")).contains("neighboring property", "absorption well", "/septic-repair-records/");
        assertThat(html("/no-septic-records-found/")).contains("Hays County", "custodian");
        assertThat(html("/septic-permit-process/")).contains("/septic-final-approval-records/");
        assertThat(html("/septic-tank-location-records/")).contains("/septic-records-before-pool/");
    }
}
