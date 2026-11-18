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
        assertTrue(picker.contains("/septic-records-checklist/south-carolina/"));
    }

    @Test
    void prioritySouthCarolinaCountyPagesProvideParcelAndStateRecordsRoutes() throws Exception {
        assertCountyIntent("horry-county", "Horry County", "https://www.horrycountysc.gov/online-services/", "TMS");
        assertCountyIntent("greenville-county", "Greenville County", "https://www.greenvillecounty.org/appsas400/RealProperty/", "Piedmont II");
        assertCountyIntent("spartanburg-county", "Spartanburg County", "https://www.spartanburgcounty.gov/185/Geographic-Information-Systems", "Piedmont I");
        assertCountyIntent("charleston-county", "Charleston County", "https://prcweb.charlestoncounty.org/", "Coastal");
        assertCountyIntent("anderson-county", "Anderson County", "https://www.andersoncountysc.org/departments-a-z/assessor/", "Piedmont II");
    }

    private void assertCountyIntent(String countySlug, String countyName, String parcelUrl, String routeDetail) throws Exception {
        String html = mockMvc.perform(get("/septic-records-checklist/south-carolina/" + countySlug + "/"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(html).contains(countyName, routeDetail,
                "href=\"" + parcelUrl + "\"",
                "href=\"https://des.sc.gov/sites/des/files/Library/D-2295.pdf\"",
                "href=\"/septic-records-checklist/south-carolina/\"");
        assertThat(html).doesNotContain("Search queries this county page is tuned to answer",
                "The page should make", "worth its own page");
    }
}
