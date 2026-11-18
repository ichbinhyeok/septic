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
                        "<meta name=\"description\" content=\"Find North Carolina septic permits and layouts by county. Search yourself or get free address-based research and results; approved agency fees are extra.\">"
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

    @Test
    void pittCountyShowsTheOfficialSearchAndFallbackBeforeTheWorksheet() throws Exception {
        String html = mockMvc.perform(get("/septic-records-checklist/north-carolina/pitt-county/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "https://www.pittcountync.gov/1280/Permitting-Portal-User-Guide"
                )))
                .andReturn().getResponse().getContentAsString();
        int start = html.indexOf("data-county-record-start");
        int end = html.indexOf("</section>", start);
        assertTrue(start >= 0 && end > start);
        String firstScreen = html.substring(start, end);
        assertTrue(firstScreen.contains("Street address or permit number."));
        assertTrue(firstScreen.contains("Choose Search in the county portal."));
        assertTrue(firstScreen.contains("https://pittcountync-energovweb.tylerhost.net/apps/selfservice#/home"));
        assertTrue(firstScreen.contains("href=\"tel:+12529023200\""));
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
