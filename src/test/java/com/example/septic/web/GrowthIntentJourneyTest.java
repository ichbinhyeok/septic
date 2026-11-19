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
        for (String intent : List.of("buying", "building", "records")) {
            var link = RecordHelpEntry.of(intent).link("/septic-record-finder/");
            assertThat(html).contains(link.replace("&", "&amp;"));
            var result = mvc.perform(get(URI.create(link.split("#")[0]))).andExpect(status().isOk()).andReturn();
            var form = (ClosingRiskCheckForm) result.getModelAndView().getModel().get("closingRiskCheckForm");
            assertThat(form.getSourcePageHint()).isEqualTo("/septic-record-finder/");
            assertThat(form.getTransactionRole()).isNull();
        }
    }

    @Test void growingStateRoutesKeepContextAndDoNotRequireAnExistingFile() throws Exception {
        for (var state : java.util.Map.of("indiana", "IN", "illinois", "IL", "west-virginia", "WV").entrySet()) {
            String path = "/septic-records-checklist/" + state.getKey() + "/";
            String html = html(path);
            assertThat(html).contains("state-record-search-start", "regional_goal_location", "state_records_free_help");
            String link = RecordHelpEntry.of("location").link(path, state.getValue(), null);
            assertThat(html).contains(link.replace("&", "&amp;"));
            var result = mvc.perform(get(URI.create(link.split("#")[0]))).andExpect(status().isOk()).andReturn();
            var form = (ClosingRiskCheckForm) result.getModelAndView().getModel().get("closingRiskCheckForm");
            assertThat(form.getStateCode()).isEqualTo(state.getValue());
            assertThat(form.getSourcePageHint()).isEqualTo(path);
            assertThat(form.getHelpPurpose()).isEqualTo("location");
            assertThat(form.getRecordStatus()).isEqualTo("not_started");
        }
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
                .contains("Johnston County septic permit search", "Address or permit number + image request",
                        "Henderson County septic permit lookup", "Current portal + older archive");
        html(johnston);
        html(henderson);
    }

    @Test void appHealthCareLinksWataugaDirectlyToOfficialPropertyCriteria() throws Exception {
        String guide = html("/apphealthcare-septic-permit-search/");
        int sectionStart = guide.indexOf("id=\"watauga\"");
        assertThat(sectionStart).isGreaterThan(0);
        assertThat(guide.substring(sectionStart, guide.indexOf("</article>", sectionStart)))
                .contains("https://public.cdpehs.com/NCENVPBLo/OSW_PROPERTY/ShowOSW_PROPERTYTablePage.aspx?ESTTST_CTY=D5",
                        "Open Watauga permit search", "Select the matching property",
                        "0000-00-0000", "View Forms", "tel:+18282644995");
    }

    @Test void sitemapDatesReflectChangedPagesWithoutUpdatingUnchangedStates() throws Exception {
        String sitemap = html("/sitemap.xml");
        for (String path : List.of("/septic-record-finder/", "/onlinerme-septic-records/")) {
            assertThat(sitemap).contains("<loc>https://septicpath.com" + path + "</loc><lastmod>2026-11-18</lastmod>");
        }
        assertThat(sitemap).contains("<loc>https://septicpath.com/septic-records-checklist/tennessee/</loc><lastmod>2026-11-18</lastmod>");
        for (String path : List.of("/how-to-find-septic-records-online/",
                "/sewer-or-septic-by-address/",
                "/texas-ossf-records-search/")) {
            assertThat(sitemap).contains("<loc>https://septicpath.com" + path + "</loc><lastmod>2026-11-05</lastmod>");
        }
        for (String path : List.of("/north-carolina-septic-permit-lookup/", "/septic-records-checklist/north-carolina/",
                "/tdec-septic-records/", "/septic-permit-search-by-address/", "/septic-permit-lookup/",
                "/apphealthcare-septic-permit-search/")) {
            assertThat(sitemap).contains("<loc>https://septicpath.com" + path + "</loc><lastmod>2026-11-19</lastmod>");
        }
        assertThat(sitemap).contains("<loc>https://septicpath.com/septic-tank-location-records/</loc><lastmod>2026-11-18</lastmod>");
        String counties = html("/sitemap-county.xml");
        assertThat(counties).contains("<loc>https://septicpath.com/septic-records-checklist/colorado/el-paso-county/</loc><lastmod>2026-11-19</lastmod>");
        assertThat(counties).contains("<loc>https://septicpath.com/septic-records-checklist/north-carolina/onslow-county/</loc><lastmod>2026-11-19</lastmod>");
        for (String county : List.of("north-carolina/johnston-county", "north-carolina/pender-county",
                "texas/travis-county", "texas/williamson-county")) {
            assertThat(counties).contains("<loc>https://septicpath.com/septic-records-checklist/" + county
                    + "/</loc><lastmod>2026-11-05</lastmod>");
        }
    }

    @Test void supportingPublicLinksResolveAndTdecRetainsSearchIntent() throws Exception {
        for (String path : List.of("/septic-tank-location-records/", "/how-to-find-septic-records-online/",
                "/septic-as-built-records/")) html(path);
        String tdec = html("/tdec-septic-records/");
        assertThat(tdec).contains("TDEC septic <br><em>permit search.</em>",
                "<title>TDEC Septic Permit Search by Address | Tennessee Records</title>",
                "Open official TDEC file search", "Viewer blocked or no match?", "data-tdec-hero-official",
                "tdec_search_stalled", "Continue my search for free", "data-tdec-route-form");
        assertThat(tdec.indexOf("data-tdec-hero-official")).isLessThan(tdec.indexOf("tdec_hero_record_investigation"));
    }

    private String html(String path) throws Exception {
        return mvc.perform(get(path)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    }
}
