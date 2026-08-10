package com.example.septic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StJosephCountyRecordCompletionRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void countyPageCarriesTheUserIntoTheOfficialSchematicRequest() throws Exception {
        mockMvc.perform(get("/septic-records-checklist/indiana/st-joseph-county/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-county-acquisition-workspace")))
                .andExpect(content().string(containsString("Open the official Schematic Request PDF")))
                .andExpect(content().string(containsString("Schematic-Request-4-12-2019.pdf")))
                .andExpect(content().string(containsString("envirohd@sjcindiana.com")))
                .andExpect(content().string(containsString("Person requesting schematic(s)")))
                .andExpect(content().string(containsString("Most recent schematic only")))
                .andExpect(content().string(containsString("All available schematics")))
                .andExpect(content().string(containsString("Email PDF")))
                .andExpect(content().string(containsString("Subdivision and lot number")))
                .andExpect(content().string(containsString("not every system is on file")));
    }

    @Test
    void homepageDescribesTheRouteAsACompletedFieldPack() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "Official schematic PDF and verified fields · prepared field pack"
                )));
    }
}
