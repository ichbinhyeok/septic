package com.example.septic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RequestDraftBoundaryRegressionTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void genericBuilderCannotBeMistakenForAnOfficialForm() throws Exception {
        mockMvc.perform(get("/septic-records-request-builder/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Routing draft · not an official form"
                )))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Do not substitute this draft for a county PDF or required portal fields."
                )))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "data-records-request-channel-confirmed"
                )))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "data-records-request-mark-sent disabled"
                )))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("One office-ready request")
                )));
    }
}
