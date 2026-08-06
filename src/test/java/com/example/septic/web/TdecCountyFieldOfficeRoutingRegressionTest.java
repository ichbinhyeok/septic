package com.example.septic.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(properties = {
        "app.storage.root=./build/test-storage",
        "app.site.base-url=https://example.test"
})
@AutoConfigureMockMvc
class TdecCountyFieldOfficeRoutingRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    // Regression: ISSUE-003 - TDEC-managed counties without a published county page lost their regional office fallback.
    // Found by /qa on 2026-08-05.
    // Report: .gstack/qa-reports/qa-report-septicpath-com-2026-08-05.md
    @Test
    void everyTennesseeCountyCarriesAFieldOfficeRoute() throws Exception {
        String html = mockMvc.perform(get("/tdec-septic-records/"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(Pattern.compile("data-field-office-name=\"").matcher(html).results().count()).isEqualTo(95);
        assertThat(Pattern.compile("data-field-office-url=\"").matcher(html).results().count()).isEqualTo(95);
        assertThat(html).contains("data-records-url=\"https://www.shelbytnhealth.com/182/Septic-Tank-Permitting-Process\"");
    }

    @Test
    // Regression: ISSUE-002 - Coffee, Fayette, Tipton, and Wilson used stale regional-office assignments.
    // Found by /qa on 2026-08-06 against the live TDEC field-office county lists.
    // Report: .gstack/qa-reports/records-entry-2026-08-06/qa-report-septicpath-com-2026-08-06.md
    void representativeCountiesMapToTheEightPublishedSepticAssistanceOffices() throws Exception {
        String html = mockMvc.perform(get("/tdec-septic-records/"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertCountyOffice(html, "bradley", "Chattanooga");
        assertCountyOffice(html, "maury", "Columbia");
        assertCountyOffice(html, "putnam", "Cookeville");
        assertCountyOffice(html, "gibson", "Jackson");
        assertCountyOffice(html, "sullivan", "Johnson City");
        assertCountyOffice(html, "anderson", "Knoxville");
        assertCountyOffice(html, "fayette", "Memphis");
        assertCountyOffice(html, "tipton", "Memphis");
        assertCountyOffice(html, "coffee", "Columbia");
        assertCountyOffice(html, "wilson", "Nashville");
        assertCountyOffice(html, "montgomery", "Nashville");
        assertThat(html).contains("data-field-office-name=\"Memphis\"");
    }

    private void assertCountyOffice(String html, String countyKey, String officeName) {
        Pattern route = Pattern.compile("value=\\\"" + Pattern.quote(countyKey)
                + "\\\"[^>]+data-field-office-name=\\\"" + Pattern.quote(officeName) + "\\\"");
        assertThat(route.matcher(html).find()).as(countyKey + " field office").isTrue();
    }
}
