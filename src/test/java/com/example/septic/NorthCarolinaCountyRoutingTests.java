package com.example.septic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NorthCarolinaCountyRoutingTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void northCarolinaRecordsHubOffersAnInternalCountyPickerAndDemandOrderedLinks() throws Exception {
        String html = mockMvc.perform(get("/septic-records-checklist/north-carolina/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "<meta name=\"description\" content=\"Enter a North Carolina address or choose the county to reach the environmental health file route for as-builts, final approvals, repairs"
                )))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("data-county-route-picker")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Find your North Carolina county")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/pitt-county/")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/pender-county/")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        int pickerEnd = html.indexOf("</form>", html.indexOf("data-county-route-picker"));
        assertTrue(html.contains("/septic-records-checklist/north-carolina/buncombe-county/"));
        assertTrue(html.contains("/septic-records-checklist/north-carolina/alamance-county/"));
    }

    @Test
    void priorityCountyPagesAnswerExactLookupQueriesAndLinkBackToStateHub() throws Exception {
        assertCountyIntent("buncombe-county", "buncombe county septic permit lookup");
        assertCountyIntent("wake-county", "wake county septic permit lookup");
        assertCountyIntent("pender-county", "pender county septic permit search");
        assertCountyIntent("pitt-county", "pitt county septic permit search");
    }

    private void assertCountyIntent(String countySlug, String query) throws Exception {
        mockMvc.perform(get("/septic-records-checklist/north-carolina/" + countySlug + "/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(query)))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "/septic-records-checklist/north-carolina/"
                )));
    }
}
