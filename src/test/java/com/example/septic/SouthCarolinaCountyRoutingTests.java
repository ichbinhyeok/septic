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
class SouthCarolinaCountyRoutingTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void southCarolinaRecordsHubOffersAnInternalCountyPickerAndDemandOrderedLinks() throws Exception {
        String html = mockMvc.perform(get("/septic-records-checklist/south-carolina/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("data-county-route-picker")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "data-county-route-prefix=\"/septic-records-checklist/south-carolina/\""
                )))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Find your South Carolina county")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        int pickerEnd = html.indexOf("</form>", html.indexOf("data-county-route-picker"));
        String picker = html.substring(html.indexOf("data-county-route-picker"), pickerEnd);
        assertTrue(
                picker.indexOf("/septic-records-checklist/south-carolina/horry-county/")
                        < picker.indexOf("/septic-records-checklist/south-carolina/anderson-county/")
        );

        String countyDirectory = html.substring(html.indexOf("id=\"county-pages\"", pickerEnd));
        assertTrue(
                countyDirectory.indexOf("/septic-records-checklist/south-carolina/horry-county/")
                        < countyDirectory.indexOf("/septic-records-checklist/south-carolina/anderson-county/")
        );
        assertTrue(
                countyDirectory.indexOf("/septic-records-checklist/south-carolina/spartanburg-county/")
                        < countyDirectory.indexOf("/septic-records-checklist/south-carolina/richland-county/")
        );
    }

    @Test
    void prioritySouthCarolinaCountyPagesAnswerExactLookupQueriesAndLinkBackToStateHub() throws Exception {
        assertCountyIntent("horry-county", "horry county septic permit lookup");
        assertCountyIntent("greenville-county", "greenville county scdes septic records");
        assertCountyIntent("spartanburg-county", "spartanburg county septic permit lookup");
        assertCountyIntent("charleston-county", "charleston county septic permit lookup");
        assertCountyIntent("anderson-county", "anderson county south carolina septic records");
    }

    private void assertCountyIntent(String countySlug, String query) throws Exception {
        mockMvc.perform(get("/septic-records-checklist/south-carolina/" + countySlug + "/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(query)))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "/septic-records-checklist/south-carolina/"
                )));
    }
}
