package com.example.septic.web;

import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"app.studio-preview.enabled=false", "app.storage.root=./build/growth-intent-test-storage"})
@AutoConfigureMockMvc
class GrowthIntentJourneyTest {
    @Autowired MockMvc mvc;

    @Test void bothNcEntryPagesAndTennesseeKeepOneContextualIntake() throws Exception {
        for (String state : List.of("north-carolina", "tennessee")) {
            String path = "/septic-records-checklist/" + state + "/";
            String code = state.equals("north-carolina") ? "NC" : "TN";
            String html = html(path);
            assertThat(html).contains("regional_goal_location", "regional_goal_building", "regional_goal_buying",
                    "/septic-tank-location-records/#location-start", "/septic-tank-location-records/#addition-file-case");
            var link = RecordHelpEntry.of("location").link(path, code, null);
            assertThat(html).contains(link.replace("&", "&amp;"));
            var result = mvc.perform(get(URI.create(link.split("#")[0]))).andExpect(status().isOk()).andReturn();
            var form = (ClosingRiskCheckForm) result.getModelAndView().getModel().get("closingRiskCheckForm");
            assertThat(form.getHelpPurpose()).isEqualTo("location");
            assertThat(form.getStateCode()).isEqualTo(code);
            assertThat(form.getSourcePageHint()).isEqualTo(path);
            assertThat(form.getRecordStatus()).isEqualTo("not_started");
            assertThat(form.getTransactionRole()).isNull();
        }
    }

    @Test void finderServesOwnersWithoutRemovingSelfServeAndFileReview() throws Exception {
        String html = html("/septic-record-finder/");
        assertThat(html).contains("Have an address, but no septic drawing?", "finder_owner_hero",
                "Search the official route myself", "Send my file and questions", "data-address-record-finder-result");
        assertThat(html.indexOf("<option value=\"location\"")).isLessThan(html.indexOf("<option value=\"buying\""));
        assertThat(html).contains(RecordHelpEntry.of("location").link("/septic-record-finder/").replace("&", "&amp;"));
    }

    @Test void locationGuideHasFourUsefulScenariosAndExistingPublicCaseEvidence() throws Exception {
        String html = html("/septic-tank-location-records/");
        assertThat(html).contains("<title>Find Septic Tank Location Records by Address | SepticPath</title>",
                "How to find septic tank location records by address",
                "Address, but no drawing", "Permit found, layout missing", "Nothing usable came back",
                "You have work to plan", "Agency fees need your approval", "id=\"location-pumping\"",
                "id=\"location-projects\"", "id=\"location-reading\"", "record-finding-stcroix");
    }

    @Test void ncPagesSurfaceExistingJohnstonAndHendersonRoutesBeforeOtherCounties() throws Exception {
        String johnston = "/septic-records-checklist/north-carolina/johnston-county/";
        String henderson = "/septic-records-checklist/north-carolina/henderson-county/";
        for (String page : List.of("/septic-records-checklist/north-carolina/",
                "/north-carolina-septic-permit-lookup/")) {
            String html = html(page);
            assertThat(html.indexOf(johnston)).isGreaterThan(0).isLessThan(html.indexOf(henderson));
            assertThat(html.indexOf(henderson)).isLessThan(html.indexOf("/septic-records-checklist/north-carolina/forsyth-county/"));
        }
        assertThat(html("/north-carolina-septic-permit-lookup/"))
                .contains("Johnston County septic permit search", "Street search + permit image",
                        "Henderson County septic permit lookup", "Current portal + older archive");
        html(johnston);
        html(henderson);
    }

    @Test void sitemapDatesReflectChangedPagesWithoutUpdatingUnchangedStates() throws Exception {
        String sitemap = html("/sitemap.xml");
        for (String path : List.of("/septic-tank-location-records/", "/septic-record-finder/",
                "/tdec-septic-records/", "/north-carolina-septic-permit-lookup/",
                "/septic-records-checklist/north-carolina/", "/septic-records-checklist/tennessee/")) {
            assertThat(sitemap).contains("<loc>https://septicpath.com" + path + "</loc><lastmod>2026-10-05</lastmod>");
        }
        String counties = html("/sitemap-county.xml");
        assertThat(counties).contains("<loc>https://septicpath.com/septic-records-checklist/north-carolina/johnston-county/</loc><lastmod>2026-10-05</lastmod>");
    }

    @Test void supportingPublicLinksResolveAndTdecRetainsSearchIntent() throws Exception {
        for (String path : List.of("/septic-tank-location-records/", "/how-to-find-septic-records-online/",
                "/septic-as-built-records/")) html(path);
        assertThat(html("/tdec-septic-records/")).contains("TDEC septic<br><em>permit search.</em>",
                "tdec_search_stalled", "Continue my search for free", "data-tdec-route-form");
    }

    private String html(String path) throws Exception {
        return mvc.perform(get(path)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    }
}
