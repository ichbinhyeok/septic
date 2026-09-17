package com.example.septic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.storage.root=./build/test-storage",
        "app.site.base-url=https://example.test"
})
@AutoConfigureMockMvc
class ResearchPublicationPolicyRegressionTest {
    @Autowired private MockMvc mockMvc;

    @Test
    void formLinksPublicationNoticeWithoutClaimingBlanketPermission() throws Exception {
        mockMvc.perform(get("/offer-prep-septic-file-check/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"/privacy-policy/#research-publication\"")))
                .andExpect(content().string(containsString("Your contact details and private correspondence are not published.")))
                .andExpect(content().string(containsString("name=\"consentAccepted\"")))
                .andExpect(content().string(not(containsString("Your request stays private"))))
                .andExpect(content().string(not(containsString("name=\"publicationConsent\""))));
        mockMvc.perform(get("/privacy-policy/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"research-publication\"")))
                .andExpect(content().string(containsString("Submitting a request does not grant us permission to publish your uploaded documents")))
                .andExpect(content().string(containsString("does not retroactively change earlier consent")))
                .andExpect(content().string(containsString("Redacting personal details does not grant copyright permission")));
    }

    @Test
    void narrativesDoNotRepeatPrivateProjectContextOrFeedback() throws Exception {
        mockMvc.perform(get("/septic-record-brief-example/"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("The listing agent said"))));
        mockMvc.perform(get("/septic-tank-location-records/"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("The customer needed the existing system"))));
        mockMvc.perform(get("/septic-records-checklist/south-carolina/anderson-county/"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("The customer confirmed the delivered explanation was informative"))));
    }
}
