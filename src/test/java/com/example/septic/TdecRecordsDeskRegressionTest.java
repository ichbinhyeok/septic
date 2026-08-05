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
    void usesCurrentOfficialPagesAndKeepsViewerFailureAsFallback() throws Exception {
        String html = mockMvc.perform(get("/tdec-septic-records/"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(html)
                .contains("https://www.tn.gov/environment/permits/water/septic-systems-permits.html")
                .contains("https://www.tn.gov/environment/contacts/public-records-request.html")
                .contains("The viewer returned 403")
                .doesNotContain("/environment/permit-permits/water-permits1/");
    }

    @Test
    void doesNotRenderTheGenericAddressFinderOrPromiseRetrieval() throws Exception {
        String html = mockMvc.perform(get("/tdec-septic-records/"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(html)
                .contains("Choose the county first")
                .contains("data-tdec-route-form")
                .contains("data-tdec-request-section")
                .doesNotContain("class=\"record-finder")
                .doesNotContain("Enter the address to find the record owner");
    }
}
