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
