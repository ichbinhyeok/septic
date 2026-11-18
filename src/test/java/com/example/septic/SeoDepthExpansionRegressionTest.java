package com.example.septic;

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
class SeoDepthExpansionRegressionTest {
    @Autowired MockMvc mvc;

    @Test void costsAnswerTheFeeQuestionWithoutPresentingAnAllInQuote() throws Exception {
        for (String state : List.of("south-carolina", "oregon")) {
            String path = "/perc-test-cost/" + state + "/";
            String html = page(path);
            assertThat(html).contains("data-verified-agency-fee", "Agency charge only.", "id=\"cost-breakdown\"",
                    "id=\"existing-file\"", "https://septicpath.com" + path, "data-record-help-cta");
            assertThat(html).doesNotContain("perc-intent traffic", "keeps the parcel on a straightforward path", "noindex");
        }
        assertThat(page("/perc-test-cost/south-carolina/")).contains("$150", "backhoe pits", "epermitting.des.sc.gov");
        assertThat(page("/perc-test-cost/oregon/")).contains("$1,121", "July 1, 2026", "County-agent fees may differ", "construction permit are separate");
        assertThat(page("/perc-test-cost/west-virginia/")).doesNotContain("data-verified-agency-fee");
    }

    @Test void percPagesLeadWithTheFeeOrOfficialPricingRoute() throws Exception {
        for (String state : List.of("arkansas", "california", "colorado", "indiana", "maryland", "nevada",
                "new-york", "ohio", "oklahoma", "tennessee", "texas", "utah", "wisconsin", "wyoming")) {
            assertThat(page("/perc-test-cost/" + state + "/"))
                    .containsSubsequence("data-perc-cost-answer", "state_perc_cost_answer", "Permit records and local review")
                    .doesNotContain("quote hold:");
        }
        assertThat(page("/perc-test-cost/maryland/")).contains("$450", "www.aahealth.org/environmental-health/permit-fees");
        assertThat(page("/perc-test-cost/california/")).contains("$792", "lwqd_feeschedule.pdf");
        assertThat(page("/perc-test-cost/tennessee/")).contains("$400", "construction permit fee schedule");
    }

    @Test void countyRecordStartsExposeTheLocalOfficeAndViewerFallback() throws Exception {
        assertThat(page("/septic-records-checklist/south-carolina/aiken-county/"))
                .containsSubsequence("data-county-record-start", "tel:+18557312504", "Call SCDES for a permit copy");
        assertThat(page("/septic-records-checklist/tennessee/cumberland-county/"))
                .containsSubsequence("data-county-record-start", "Open the TDEC septic records viewer",
                        "field-offices/cookeville.html", "county_records_start_alternate", "id=\"county-access-workflow\"");
    }

    @Test void countyInstructionsExposeAttachmentsAndLocalSearchDifferences() throws Exception {
        assertThat(page("/septic-records-checklist/north-carolina/henderson-county/"))
                .contains("id=\"record-search-instructions\"", "Permit Notes", "Last,First", "REID/PIN", "1979–1983");
        assertThat(page("/septic-records-checklist/ohio/lake-county/"))
                .contains("id=\"record-search-instructions\"", "Wood-Crest", "house number", "historical");
        assertThat(page("/north-carolina-septic-permit-lookup/"))
                .contains("/septic-records-checklist/north-carolina/henderson-county/#record-search-instructions");
        assertThat(page("/septic-records-checklist/ohio/"))
                .contains("/septic-records-checklist/ohio/lake-county/#record-search-instructions");
    }

    @Test void conciseCountyPagesPreserveLocalRecordActionsAndOfficialRequestLinks() throws Exception {
        for (List<String> route : List.of(
                List.of("alabama/blount-county", "tel:+12059471076", "Call Blount County Environmental Health"),
                List.of("alabama/cullman-county", "tel:+12567340243", "Call Cullman County Environmental Health"),
                List.of("south-carolina/florence-county", "tel:+18557312504", "Open the Florence County parcel map"),
                List.of("south-carolina/sumter-county", "tel:+18557312504", "Open the Sumter City–County parcel map"),
                List.of("indiana/decatur-county", "extension 7715", "Open Decatur County Environmental Health"),
                List.of("indiana/shelby-county", "records go back to 1955", "Environmental Division")
        )) {
            String path = "/septic-records-checklist/" + route.get(0) + "/";
            assertThat(page(path))
                    .containsSubsequence("data-county-record-start", route.get(1), route.get(2), "Investigate my property")
                    .contains("https://septicpath.com" + path, "Document types and request links", "Open records request wording")
                    .doesNotContain("uses this table as a work surface");
        }
    }

    @Test void compactCountyTemplateKeepsDocumentedFallbacksNextToTheLookup() throws Exception {
        for (List<String> route : List.of(
                List.of("north-carolina/wake-county", "Call Wake County records assistance", "Open the official iMAPS search guide"),
                List.of("north-carolina/buncombe-county", "Search Buncombe well and septic records", "Open Buncombe Environmental Health"),
                List.of("texas/tarrant-county", "Open the Tarrant County OSSF office", "Open official Public Information Act instructions"),
                List.of("washington/thurston-county", "https://www.thurstoncountywa.gov/media/13802", "Open the official record drawing request")
        )) {
            String path = "/septic-records-checklist/" + route.get(0) + "/";
            assertThat(page(path))
                    .containsSubsequence("data-county-record-start", route.get(1), route.get(2), "Investigate my property")
                    .contains("https://septicpath.com" + path, "Document types and request links", "Open records request wording")
                    .doesNotContain("uses this table as a work surface");
        }
    }

    @Test void drawingAndPermitRoutesPutOfficialSearchActionsBeforeResearchHelp() throws Exception {
        String thurston = "/septic-records-checklist/washington/thurston-county/";
        assertThat(page(thurston)).containsSubsequence("data-county-record-start", "11-digit tax parcel number",
                "quick-property-search-options", "Search Thurston historic septic permits", "media/13802", "Investigate my property");
        assertThat(page("/septic-as-built-records/")).contains("href=\"" + thurston + "\"", "as_built_thurston_county");
        assertThat(page("/septic-records-checklist/vermont/")).containsSubsequence("institution-hero__actions",
                "https://anrweb.vt.gov/DEC/WWDocs/Default.aspx", "vt_hero_official_permit_search",
                "href=\"#no-permit-match\"", "id=\"no-permit-match\"", "vt_no_result_prepare");
        assertThat(page("/")).containsSubsequence("Self-serve research tools",
                "href=\"/septic-tank-location-records/\"", "home_septic_location_records");
    }

    @Test void platformsDistinguishMissingSearchResultsFromMissingDocuments() throws Exception {
        assertThat(page("/septic-records-checklist/new-hampshire/"))
                .contains("most post-1986", "after 2015", "1967–1986", "Construction Approval", "Operational Approval");
        assertThat(page("/onlinerme-septic-records/"))
                .contains("PSSD", "WW", "specific to Loudoun", "Loudoun Parent Files");
        assertThat(page("/septicsearch-records/"))
                .contains("id=\"septicsearch-no-results\"", "contact-regulators", "Fictional search example");
    }

    @Test void illustrationIsCrawlableAndSitemapAssociatesItOnlyWithItsLandingPages() throws Exception {
        String asset = "/images/guides/septic-as-built-dimensions-explained.webp";
        for (String path : List.of("/septic-as-built-records/", "/how-to-read-septic-as-built/")) {
            assertThat(page(path)).contains("src=\"" + asset + "\"", "width=\"1440\"", "Fictional septic drawing", "drawing_image");
        }
        mvc.perform(get(asset)).andExpect(status().isOk());
        mvc.perform(get(asset.replace(".webp", ".png"))).andExpect(status().isOk());
        String xml = page("/sitemap.xml");
        var factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        var doc = factory.newDocumentBuilder().parse(new java.io.ByteArrayInputStream(xml.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        var images = doc.getElementsByTagNameNS("http://www.google.com/schemas/sitemap-image/1.1", "image");
        assertThat(images.getLength()).isEqualTo(2);
        for (int i = 0; i < images.getLength(); i++) {
            String parent = images.item(i).getParentNode().getTextContent();
            assertThat(parent).containsAnyOf("/septic-as-built-records/", "/how-to-read-septic-as-built/");
            assertThat(parent).contains(parent.contains("/how-to-read-septic-as-built/") ? "2026-11-19" : "2026-11-18", asset);
        }
    }

    private String page(String path) throws Exception {
        return mvc.perform(get(path)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    }
}
