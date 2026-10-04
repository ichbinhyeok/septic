package com.example.septic.web;

import com.example.septic.service.PaidUnlockStore;
import com.example.septic.service.OpsReportCredentialsService;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"app.studio-preview.enabled=false", "app.storage.root=./build/sunday-growth-test-storage"})
@AutoConfigureMockMvc
class SundayGrowthWorkflowTest {
    @Autowired MockMvc mvc;
    @Autowired PaidUnlockStore store;
    @Autowired OpsReportCredentialsService credentials;
    private final com.fasterxml.jackson.databind.ObjectMapper json = JsonMapper.builder().findAndAddModules().build();

    @Test void searchEntryCopyMatchesMeasuredIntentAndKeepsSingleIntake() throws Exception {
        mvc.perform(get("/tdec-septic-records/")).andExpect(status().isOk())
                .andExpect(content().string(containsString("TDEC septic<br><em>permit search.</em>")))
                .andExpect(content().string(containsString("Get free record help")))
                .andExpect(content().string(containsString("&amp;state=TN#intake-form")));
        for (String state : java.util.List.of("north-carolina", "tennessee")) {
            mvc.perform(get("/septic-records-checklist/" + state + "/")).andExpect(status().isOk())
                    .andExpect(content().string(containsString("septic records")))
                    .andExpect(content().string(containsString("Get free record help")))
                    .andExpect(content().string(containsString("intent=records&amp;source=")));
        }
        mvc.perform(get("/septic-system-cost-calculator/alabama/")).andExpect(status().isOk())
                .andExpect(content().string(containsString("How much does a perc test cost in Alabama?")));
    }

    @Test void everyPurposePersistsAndRendersWithoutLeakingPrivateIdentityToTheServiceLink() throws Exception {
        for (String purpose : java.util.List.of("location", "inspection", "pumping", "repair", "building", "records", "unsure")) {
            var released = release(purpose);
            assertThat(store.findOfferById(released.offer().id()).orElseThrow().helpPurpose()).isEqualTo(purpose);
            String html = mvc.perform(get("/paid-unlock/delivery/" + released.downloadToken()))
                    .andExpect(status().isOk()).andExpect(header().string("Cache-Control", containsString("no-store")))
                    .andReturn().getResponse().getContentAsString();
            var step = RecordResultNextStep.forPurpose(purpose);
            assertThat(html).contains(step.heading(), "delivery-checklist");
            if (step.offersService()) assertThat(html).contains(step.servicePath().replace("&", "&amp;"));
            else assertThat(html).doesNotContain("quoteMode=true");
        }
    }

    @Test void historicalJsonWithoutPurposeStillLoadsAndRenders() throws Exception {
        var released = release("pumping");
        Path offerPath = Path.of("build/sunday-growth-test-storage/paid-unlocks/offers", released.offer().id() + ".json");
        var saved = (com.fasterxml.jackson.databind.node.ObjectNode) json.readTree(offerPath.toFile());
        saved.remove("helpPurpose");
        json.writeValue(offerPath.toFile(), saved);
        mvc.perform(get("/paid-unlock/delivery/" + released.downloadToken())).andExpect(status().isOk())
                .andExpect(content().string(containsString("Choose the next step that fits your question.")));
    }

    @Test void operatorJsonEndpointAcceptsPurposeAndRejectsInventedPurpose() throws Exception {
        byte[] bytes = "Fictional QA package only".getBytes(StandardCharsets.UTF_8);
        var input = new LinkedHashMap<String, Object>();
        input.put("customerEmail", "qa@example.com"); input.put("requestReference", "QA-ONLY");
        input.put("propertyLabel", "Fictional QA property"); input.put("sourceSummary", "Synthetic source");
        input.put("documentScope", "Synthetic package"); input.put("answerableQuestion", "Test answer");
        input.put("limitations", "Not a customer file"); input.put("packageFileName", "qa-only.pdf");
        input.put("packageBase64", Base64.getEncoder().encodeToString(bytes)); input.put("releaseApproval", approval(bytes));
        input.put("helpPurpose", "inspection");
        var auth = credentials.credentials();
        String header = "Basic " + Base64.getEncoder().encodeToString((auth.username() + ":" + auth.password()).getBytes(StandardCharsets.UTF_8));
        String response = mvc.perform(post("/ops/record-results/json").header("Authorization", header)
                        .contentType("application/json").content(json.writeValueAsBytes(input)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.emailSent").value(false))
                .andReturn().getResponse().getContentAsString();
        assertThat(store.findOfferById(json.readTree(response).path("offerId").asText()).orElseThrow().helpPurpose()).isEqualTo("inspection");
        input.put("helpPurpose", "guaranteed_repair");
        mvc.perform(post("/ops/record-results/json").header("Authorization", header)
                        .contentType("application/json").content(json.writeValueAsBytes(input)))
                .andExpect(status().isBadRequest());
    }

    private PaidUnlockStore.Fulfillment release(String purpose) throws Exception {
        byte[] bytes = "Fictional QA package only".getBytes(StandardCharsets.UTF_8);
        return store.createFreeDelivery(new PaidUnlockStore.OfferInput("qa@example.com", "QA-ONLY", "Fictional QA property",
                "Synthetic source", "Synthetic package", "Test answer", "Not a customer file", purpose), "qa-only.pdf", bytes, approval(bytes));
    }

    private PaidUnlockStore.ReleaseApproval approval(byte[] bytes) throws Exception {
        return new PaidUnlockStore.ReleaseApproval("PASS", "qa-only", "qa@example.com", "Fictional test",
                HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes)), true, true, Instant.now().minusSeconds(1), "test-fixture");
    }
}
