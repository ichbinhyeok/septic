package com.example.septic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class IndianaCountyRoutingTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void indianaRecordsHubOffersAnInternalCountyPickerAndDemandOrderedLinks() throws Exception {
        String html = mockMvc.perform(get("/septic-records-checklist/indiana/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("data-county-route-picker")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "data-county-route-prefix=\"/septic-records-checklist/indiana/\""
                )))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Find your Indiana county")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        int pickerEnd = html.indexOf("</form>", html.indexOf("data-county-route-picker"));
        String picker = html.substring(html.indexOf("data-county-route-picker"), pickerEnd);
        assertTrue(picker.contains("/septic-records-checklist/indiana/"));
    }

    @Test
    void priorityIndianaCountyPagesProvideOfficialRecordRoutesAndLinkBackToStateHub() throws Exception {
        assertCountyIntent("porter-county", "Porter County",
                "https://www.in.gov/localhealth/portercounty/environmental-health/septic-systems/locating-existing-septic-systems/", "1974");
        assertCountyIntent("monroe-county", "Monroe County",
                "https://www.in.gov/counties/monroe/Departments/health-department/wastewater/", "Minimum Specs");
        assertCountyIntent("brown-county", "Brown County",
                "https://www.in.gov/localhealth/browncounty/files/Request-for-Records-Form-.pdf", "system diagram");
        assertCountyIntent("grant-county", "Grant County",
                "https://www.in.gov/localhealth/grantcounty/environmental-division/faqs/", "records search when given an address");
    }

    private void assertCountyIntent(String countySlug, String countyName, String officialUrl, String routeDetail) throws Exception {
        String html = mockMvc.perform(get("/septic-records-checklist/indiana/" + countySlug + "/"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(html).contains(countyName, routeDetail,
                "href=\"" + officialUrl + "\"",
                "href=\"/septic-records-checklist/indiana/\"");
        assertThat(html).doesNotContain("Search queries this county page is tuned to answer",
                "The page should make", "worth its own page");
    }
}
