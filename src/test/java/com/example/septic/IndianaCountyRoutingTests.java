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
        assertTrue(
                picker.indexOf("/septic-records-checklist/indiana/porter-county/")
                        < picker.indexOf("/septic-records-checklist/indiana/st-joseph-county/")
        );
        String countyDirectory = html.substring(html.indexOf("id=\"county-pages\"", pickerEnd));
        assertTrue(
                countyDirectory.indexOf("/septic-records-checklist/indiana/porter-county/")
                        < countyDirectory.indexOf("/septic-records-checklist/indiana/st-joseph-county/")
        );
        assertTrue(
                countyDirectory.indexOf("/septic-records-checklist/indiana/monroe-county/")
                        < countyDirectory.indexOf("/septic-records-checklist/indiana/st-joseph-county/")
        );
    }

    @Test
    void priorityIndianaCountyPagesAnswerExactLookupQueriesAndLinkBackToStateHub() throws Exception {
        assertCountyIntent("porter-county", "porter county septic permit lookup");
        assertCountyIntent("monroe-county", "monroe county indiana septic records");
        assertCountyIntent("brown-county", "brown county indiana septic records");
        assertCountyIntent("grant-county", "grant county indiana septic records");
    }

    private void assertCountyIntent(String countySlug, String query) throws Exception {
        mockMvc.perform(get("/septic-records-checklist/indiana/" + countySlug + "/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(query)))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "/septic-records-checklist/indiana/"
                )));
    }
}
