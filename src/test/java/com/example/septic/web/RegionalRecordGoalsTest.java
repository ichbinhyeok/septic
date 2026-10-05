package com.example.septic.web;

import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"app.studio-preview.enabled=false", "app.storage.root=./build/regional-goals-test-storage"})
@AutoConfigureMockMvc
class RegionalRecordGoalsTest {
    @Autowired MockMvc mvc;

    @Test void publicRegionalPagesOfferThreeContextualResearchGoals() throws Exception {
        for (String[] region : List.of(
                new String[]{"/north-carolina-septic-permit-lookup/", "NC", ""},
                new String[]{"/tdec-septic-records/", "TN", ""},
                new String[]{"/septic-records-checklist/north-carolina/iredell-county/", "NC", "Iredell County"},
                new String[]{"/septic-records-checklist/tennessee/knox-county/", "TN", "Knox County"})) {
            String html = mvc.perform(get(region[0])).andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            assertThat(html).contains("Where is my septic tank?", "Planning a deck or addition?",
                    "Buying a home with septic?", "Any local field work is separate.", "/regional-record-goals.css");
            for (String intent : List.of("location", "building", "buying")) {
                String link = RecordHelpEntry.of(intent).link(region[0], region[1], region[2]);
                assertThat(html).contains(link.replace("&", "&amp;"), "regional_goal_" + intent);
                var result = mvc.perform(get(URI.create(link.split("#")[0])))
                        .andExpect(status().isOk()).andReturn();
                var form = (ClosingRiskCheckForm) result.getModelAndView().getModel().get("closingRiskCheckForm");
                assertThat(form.getStateCode()).isEqualTo(region[1]);
                if (!region[2].isEmpty()) assertThat(form.getCountyName()).isEqualTo(region[2]);
                assertThat(form.getSourcePageHint()).isEqualTo(region[0]);
                assertThat(form.getSourceContext()).isEqualTo("entry_" + intent);
                assertThat(form.getHelpPurpose()).isEqualTo(RecordHelpEntry.of(intent).helpPurpose());
                assertThat(form.getRecordStatus()).isEqualTo("not_started");
                assertThat(form.getTransactionRole()).isNull();
                assertThat(result.getResponse().getContentAsString()).contains("id=\"intake-form\"",
                        RecordHelpEntry.of(intent).title());
            }
        }
    }

    @Test void projectEntryIsEditableResearchNotConstructionApproval() {
        var entry = RecordHelpEntry.of("building");
        var form = new ClosingRiskCheckForm();
        entry.prefill(form, "/tdec-septic-records/");
        assertThat(form.getResearchGoal()).isEqualTo("system_layout");
        assertThat(form.getHelpPurpose()).isEqualTo("building");
        assertThat(entry.description()).contains("not permission to build or dig");
        assertThat(RecordHelpEntry.fromSource("entry_building")).isEqualTo(entry);
    }
}
