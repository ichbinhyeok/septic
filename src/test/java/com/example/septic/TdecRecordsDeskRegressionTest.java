package com.example.septic;

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
class TdecRecordsDeskRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rendersAllTennesseeCountiesAndExactlyNineLocalPrograms() throws Exception {
        String html = mockMvc.perform(get("/tdec-septic-records/"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(Pattern.compile("data-county-name=\"").matcher(html).results().count()).isEqualTo(95);
        assertThat(Pattern.compile("data-contract-county=\"true\"").matcher(html).results().count()).isEqualTo(9);
        assertThat(html).contains("Davidson County", "Sumner County", "Williamson County");
    }

    @Test
    void usesCurrentOfficialPagesAndExplainsViewerFailureWithoutLeadingWithIt() throws Exception {
        String html = mockMvc.perform(get("/tdec-septic-records/"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(html)
                .contains("https://www.tn.gov/environment/permits/water/septic-systems-permits.html")
                .contains("https://www.tn.gov/environment/contacts/public-records-request.html")
                .contains("A 403 is an access failure")
                .doesNotContain("/environment/permit-permits/water-permits1/");
    }

    @Test
    void doesNotRenderTheGenericAddressFinderOrPromiseRetrieval() throws Exception {
        String html = mockMvc.perform(get("/tdec-septic-records/"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(html)
                .contains("Find the right official record source")
                .contains("Choose the property county. Add the address or other property details if you have them.")
                .contains("data-tdec-route-form")
                .contains("data-tdec-request-section")
                .contains("data-tdec-address")
                .contains("aria-describedby=\"tdec-county-help tdec-form-error\"")
                .contains("id=\"tdec-request-title\" tabindex=\"-1\"")
                .contains("value=\"records\"")
                .contains("value=\"status\"")
                .contains("value=\"missing\"")
                .contains("value=\"repair\"")
                .contains("No address is required")
                .doesNotContain("class=\"record-finder")
                .doesNotContain("Enter the address to find the record owner")
                .doesNotContain("Enter the property county and address");
    }

    @Test
    void sendsAllNineLocallyAdministeredCountiesToTheirOwnOfficialPrograms() throws Exception {
        String html = mockMvc.perform(get("/tdec-septic-records/"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(html)
                .contains("https://www.blounttn.gov/992/Public-Records-Request")
                .contains("https://www.nashville.gov/departments/health/environmental-health/septic-and-sewage-disposal-systems")
                .contains("https://www.hamiltontn.gov/BuildingInspection_Septic.aspx")
                .contains("https://jeffersoncountytn.gov/environmental-health/")
                .contains("https://www.knoxcounty.org/health/groundwater_protection.php")
                .contains("https://madisoncountytn.gov/FormCenter/Health-Department-11/Septic-System-Records-Request-89")
                .contains("https://www.seviercountytn.gov/government/departments/services/environmental_health.php")
                .contains("https://www.shelbytnhealth.com/182/Septic-Tank-Permitting-Process")
                .contains("https://www.williamsoncounty-tn.gov/153/Forms-Hand-outs");
    }
}
