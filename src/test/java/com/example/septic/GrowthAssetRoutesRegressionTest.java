package com.example.septic;

import com.example.septic.web.CountyAccessProfileCatalog;
import com.example.septic.web.CountyAcquisitionProfileCatalog;
import com.example.septic.web.RecordHelpEntry;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GrowthAssetRoutesRegressionTest {
    @Autowired MockMvc mvc;

    @Test void guilfordOffersGisBeforePhoneAndKeepsTheMissingFileFallback() throws Exception {
        String html = page("/septic-records-checklist/north-carolina/guilford-county/");
        assertThat(html).contains("How to search Guilford County septic records online", "REID", "PIN",
                "https://gisdv.guilfordcountync.gov/guilford/", "https://guilfordcountync.nextrequest.com/",
                "a well document alone does not answer a septic question", "tel:336-641-7613",
                "data-county-acquisition-method=\"official_search\"");
        assertThat(html.indexOf("Guilford GIS septic search quick start")).isLessThan(html.indexOf("data-record-evidence "));
        assertThat(CountyAccessProfileCatalog.find("NC::guilford-county").mode()).isEqualTo("portal_with_fallback");
        assertThat(CountyAcquisitionProfileCatalog.find("NC::guilford-county").officialFieldPackVerified()).isFalse();
    }

    @Test void unionSeparatesTheBuildingIndexFromThePaidCountyFileSearch() throws Exception {
        String html = page("/septic-records-checklist/north-carolina/union-county/");
        assertThat(html).contains("https://ucinspect.unioncountync.gov/evolvepublic/",
                "A building permit is not the septic layout", "not an application for a new system",
                "$10 research fee", "September 30, 2026", "payable before the search begins",
                "tel:704-283-3553", "MyHD", "agency charges require your approval");
        assertThat(CountyAcquisitionProfileCatalog.find("NC::union-county").feeLabel()).contains("$10");
        assertThat(html).doesNotContain("strongest county wedges", "No historical-record request fee is published");
    }

    @Test void countyGuideRequestsPreserveCountyAndEntryPageWithoutExtraFields() throws Exception {
        for (String name : List.of("Guilford", "Union")) {
            String county = name + " County";
            String path = "/septic-records-checklist/north-carolina/" + name.toLowerCase(java.util.Locale.ROOT) + "-county/";
            String expected = RecordHelpEntry.of("missing").link(path, "NC", county);
            assertThat(page(path)).contains(expected.replace("&", "&amp;"));
            String form = page(expected.substring(0, expected.indexOf('#')));
            assertThat(form).contains("id=\"intake-form\"", path, county);
        }
    }

    @Test void indianaHasVisibleSearchHelpAndRealCountyRoutes() throws Exception {
        String html = page("/septic-records-checklist/indiana/");
        assertThat(html).contains("Indiana septic records by county and address", "Get free record help",
                "id=\"indiana-record-search-guide\"", "<details open>",
                "/septic-records-checklist/indiana/st-joseph-county/",
                "/septic-records-checklist/indiana/monroe-county/",
                RecordHelpEntry.of("missing").link("/septic-records-checklist/indiana/", "IN", null).replace("&", "&amp;"));
        assertThat(page("/septic-records-checklist/tennessee/")).doesNotContain("id=\"indiana-record-search-guide\"");
        for (String county : List.of("st-joseph-county", "monroe-county")) {
            page("/septic-records-checklist/indiana/" + county + "/");
        }
    }

    @Test void caseLinksResolveToExistingPublicEvidenceNotPreviewPages() throws Exception {
        for (String[] target : List.of(
                new String[]{"/septic-as-built-records/", "layout-case"},
                new String[]{"/septic-record-brief-example/", "sale-case"},
                new String[]{"/septic-permit-search-by-address/", "old-address-case"})) {
            assertThat(page(target[0])).contains("id=\"" + target[1] + "\"");
        }
        for (String path : List.of("/septic-records-checklist/indiana/",
                "/septic-records-checklist/north-carolina/guilford-county/",
                "/septic-records-checklist/north-carolina/union-county/")) {
            assertThat(page(path)).doesNotContain("/design-preview/studio/work/", "WaterQuality_Docs/");
        }
    }

    @Test void materialLastmodOnlyAdvancesTheEditedRoutes() throws Exception {
        String sitemap = page("/sitemap.xml") + page("/sitemap-county.xml");
        for (String path : List.of("/septic-records-checklist/indiana/",
                "/septic-records-checklist/north-carolina/guilford-county/",
                "/septic-records-checklist/north-carolina/union-county/")) {
            assertThat(sitemap).containsPattern(java.util.regex.Pattern.quote(path) + "</loc>\\s*<lastmod>2026-10-05</lastmod>");
        }
    }

    @Test void stateHeroHelpLinksParticipateInTheRecordHelpFunnel() throws Exception {
        for (String state : List.of("north-carolina", "tennessee", "indiana")) {
            String html = page("/septic-records-checklist/" + state + "/");
            var heroLink = java.util.regex.Pattern.compile("<a\\b[^>]*data-track-source-context=\"state_records_free_help\"[^>]*>").matcher(html);
            assertThat(heroLink.find()).as("free-help hero link in %s", state).isTrue();
            assertThat(heroLink.group()).contains("data-record-help-cta", "intent=records", "#intake-form");
        }
    }

    private String page(String path) throws Exception {
        return mvc.perform(get(java.net.URI.create(path))).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    }
}
