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
        assertThat(Pattern.compile("data-field-office-url=\"https://www\\.tn\\.gov/environment/contacts/field-offices/")
                .matcher(html).results().count()).isEqualTo(95);
    }

    @Test
    void representativeCountiesMapToAllEightOfficialFieldOffices() throws Exception {
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
        assertCountyOffice(html, "montgomery", "Nashville");
    }

    private void assertCountyOffice(String html, String countyKey, String officeName) {
        Pattern route = Pattern.compile("value=\\\"" + Pattern.quote(countyKey)
                + "\\\"[^>]+data-field-office-name=\\\"" + Pattern.quote(officeName) + "\\\"");
        assertThat(route.matcher(html).find()).as(countyKey + " field office").isTrue();
    }
}
