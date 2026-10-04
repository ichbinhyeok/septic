package com.example.septic.web;

import com.example.septic.service.ClosingRiskNotificationService;
import com.example.septic.service.ClosingRiskRequestLimiter;
import com.example.septic.service.LeadStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"app.studio-preview.enabled=false", "app.storage.root=./build/free-pivot-test-storage"})
@AutoConfigureMockMvc
class FreeRecordPivotTest {
    @Autowired MockMvc mvc;
    @Autowired com.example.septic.service.PaidUnlockStore results;
    @Autowired com.example.septic.service.OpsReportCredentialsService opsCredentials;
    @MockitoBean LeadStorageService storage;
    @MockitoBean ClosingRiskNotificationService notifications;
    @MockitoBean ClosingRiskRequestLimiter limiter;

    @BeforeEach void isolateEffects() {
        when(limiter.allow(any())).thenReturn(true);
        when(storage.saveClosingRiskRequest(any(), anyString(), any())).thenReturn("fictional-pivot-request");
        when(storage.saveQuoteLead(any(), any(), any(), anyString(), any())).thenReturn("fictional-pivot-lead");
    }

    @Test void publicPagesAgreeOnFreeResultsAndPreserveEvidence() throws Exception {
        for (String path : java.util.List.of("/", "/offer-prep-septic-file-check/", "/septic-record-brief-example/",
                "/tdec-septic-records/", "/dhec-septic-permit-lookup/", "/septic-tank-location-records/",
                "/terms-of-use/", "/privacy-policy/")) {
            mvc.perform(get(path)).andExpect(status().isOk()).andExpect(content().string(not(containsString("$29"))));
        }
        mvc.perform(get("/")).andExpect(content().string(containsString("Get free record help")))
                .andExpect(content().string(containsString("shelby-site-plan-detail-public.png")));
        mvc.perform(get("/septic-records-checklist/north-carolina/wake-county/"))
                .andExpect(status().isOk()).andExpect(content().string(containsString("Search stuck? Get free record help")));
        mvc.perform(get("/terms-of-use/"))
                .andExpect(content().string(containsString("free record research and results")))
                .andExpect(content().string(not(containsString("optional paid result packages"))));
    }

    @Test void productionCalculatorHandoffUsesARealPublicRoute() throws Exception {
        String html = mvc.perform(post("/septic-system-cost-calculator/").param("stateCode", "NC")
                        .param("projectType", "pumping").param("bedrooms", "3"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(html).contains("/offer-prep-septic-file-check/?from=calculator", "Get help with this project")
                .doesNotContain("/design-preview/studio/intake/");
        var response = mvc.perform(get("/offer-prep-septic-file-check/").param("from", "calculator")
                        .param("state", "NC").param("project", "pumping").param("bedrooms", "3"))
                .andExpect(status().isOk()).andReturn();
        var form = (ClosingRiskCheckForm) response.getModelAndView().getModel().get("closingRiskCheckForm");
        assertThat(form.getStateCode()).isEqualTo("NC");
        assertThat(form.getConcern()).contains("Pumping", "not verified against a permit");
        assertThat(form.getHelpPurpose()).isNull(); // A page visit is not a confirmed service need.
    }

    @Test void purposeRoleAndPhoneAreRequiredButLongDescriptionIsNot() throws Exception {
        for (String missing : java.util.List.of("helpPurpose", "phone", "transactionRole", "stateCode")) {
            var input = validIntake();
            input.set(missing, "");
            mvc.perform(post("/offer-prep-septic-file-check/").params(input)).andExpect(status().isOk())
                    .andExpect(content().string(containsString("data-error-field=\"" + missing + "\"")));
        }
        verifyNoInteractions(storage, notifications);
        mvc.perform(post("/offer-prep-septic-file-check/").params(validIntake()))
                .andExpect(content().string(containsString("fictional-pivot-request")))
                .andExpect(content().string(containsString("Your records and our explanation are free")));
        verify(storage).saveClosingRiskRequest(argThat(form -> "location".equals(form.getHelpPurpose())
                && "this_month".equals(form.getTimeline()) && form.getConcernValue().isEmpty()
                && "(919) 555-0100".equals(form.getPhone())), anyString(), any());
    }

    @Test void malformedPhoneAndUnknownStateCannotReachStorage() throws Exception {
        for (String phone : java.util.List.of("..........", "123", "call me", "1234567890123456")) {
            var input = validIntake(); input.set("phone", phone);
            mvc.perform(post("/offer-prep-septic-file-check/").params(input))
                    .andExpect(content().string(containsString("data-error-field=\"phone\"")));
        }
        var input = validIntake(); input.set("stateCode", "ZZ");
        mvc.perform(post("/offer-prep-septic-file-check/").params(input))
                .andExpect(content().string(containsString("Choose a valid state.")));
        verifyNoInteractions(storage, notifications);
    }

    @Test void productionFileReviewRequiresAnUploadWithoutJavascript() throws Exception {
        var input = validIntake(); input.set("intakeMode", "review");
        mvc.perform(post("/offer-prep-septic-file-check/").params(input))
                .andExpect(status().isOk()).andExpect(content().string(containsString("Add at least one permit")))
                .andExpect(content().string(containsString("data-error-field=\"documents\"")));
        verifyNoInteractions(storage, notifications);
    }

    @Test void serviceFormDoesNotInventStateSymptomOrTimeline() throws Exception {
        var response = mvc.perform(get("/septic-system-cost-calculator/").param("quoteMode", "true"))
                .andExpect(status().isOk()).andReturn();
        var form = (QuoteLeadForm) response.getModelAndView().getModel().get("quoteLeadForm");
        assertThat(form.getStateCode()).isEmpty();
        assertThat(form.getProjectType()).isEmpty();
        assertThat(form.getServiceNeed()).isEmpty();
        assertThat(form.getTimeline()).isEmpty();
        assertThat(form.getBedrooms()).isNull();
        mvc.perform(post("/quote-request/").param("email", "test@example.com"))
                .andExpect(status().isOk()).andExpect(content().string(containsString("Finish the required fields")));
        verifyNoInteractions(storage);
    }

    @Test void countyLinkCarriesEditableRegionWithoutJavascriptOrFalseNoRecordClaim() throws Exception {
        String source = "/septic-records-checklist/north-carolina/wake-county/";
        String html = mvc.perform(get(source)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String expectedLink = RecordHelpEntry.of("missing").link(source, "NC", "Wake County");
        assertThat(html).contains(expectedLink.replace("&", "&amp;"));
        var response = mvc.perform(get(java.net.URI.create(expectedLink.split("#")[0])))
                .andExpect(status().isOk()).andReturn();
        var form = (ClosingRiskCheckForm) response.getModelAndView().getModel().get("closingRiskCheckForm");
        assertThat(form.getStateCode()).isEqualTo("NC");
        assertThat(form.getCountyName()).isEqualTo("Wake County");
        assertThat(form.getSourcePageHint()).isEqualTo(source);
        assertThat(form.getSourceContext()).isEqualTo("entry_missing");
        assertThat(form.getRecordStatus()).isEqualTo("not_started");
        assertThat(form.getHelpPurpose()).isNull();
        assertThat(response.getResponse().getContentAsString()).contains("Couldn’t find your septic record?", "name=\"stateCode\" required");
    }

    @Test void contextualGuidesUseOneIntakeAndDoNotCreateAnotherChannel() throws Exception {
        for (String slug : java.util.List.of("septic-tank-location-records", "buying-a-house-with-a-septic-system", "septic-permit-search-by-address")) {
            String html = mvc.perform(get("/" + slug + "/")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
            var entry = RecordHelpEntry.forGuide(slug);
            assertThat(html).contains(entry.link("/" + slug + "/").replace("&", "&amp;"), entry.action());
        }
        var response = mvc.perform(get("/offer-prep-septic-file-check/").param("intent", "location")
                        .param("source", "/septic-tank-location-records/"))
                .andExpect(status().isOk()).andReturn();
        var form = (ClosingRiskCheckForm) response.getModelAndView().getModel().get("closingRiskCheckForm");
        assertThat(form.getResearchGoal()).isEqualTo("system_layout");
        assertThat(form.getHelpPurpose()).isEqualTo("location");
        assertThat(form.getTransactionRole()).isNull();
    }

    @Test void entryParametersCannotInjectCopyExternalSourcesOrUnknownRegions() throws Exception {
        var response = mvc.perform(get("/offer-prep-septic-file-check/").param("intent", "<script>bad</script>")
                        .param("source", "https://example.invalid/?email=private").param("state", "ZZ").param("county", "Invented County"))
                .andExpect(status().isOk()).andReturn();
        var form = (ClosingRiskCheckForm) response.getModelAndView().getModel().get("closingRiskCheckForm");
        assertThat(form.getSourceContext()).isEqualTo("entry_records");
        assertThat(form.getSourcePageHintValue()).isEmpty();
        assertThat(form.getStateCode()).isNull();
        assertThat(form.getCountyNameValue()).isEmpty();
        assertThat(response.getResponse().getContentAsString()).doesNotContain("<script>bad</script>", "example.invalid");
    }

    @Test void compactServiceInquiryHasNoBedroomQuestionAndDoesNotMakeAnEstimate() throws Exception {
        String html = mvc.perform(get("/septic-system-cost-calculator/").param("quoteMode", "true"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String form = html.substring(html.indexOf("<form class=\"quote-form\""));
        form = form.substring(0, form.indexOf("</form>"));
        assertThat(form).doesNotContain("name=\"bedrooms\"", "name=\"occupants\"")
                .contains("Timing &amp; contact preferences (optional)");
        assertThat(html).doesNotContain("id=\"property-inputs\" open", "class=\"calc-story");
        mvc.perform(post("/quote-request/").param("stateCode", "NC").param("projectType", "inspection")
                        .param("serviceNeed", "planned_project").param("fullName", "QA Only")
                        .param("email", "qa@example.invalid").param("phone", "9195550100").param("zipCode", "27513")
                        .param("consentAccepted", "true"))
                .andExpect(status().isOk()).andExpect(model().attribute("result", nullValue()))
                .andExpect(content().string(containsString("fictional-pivot-lead")))
                .andExpect(content().string(not(containsString("id=\"quote-request-form\""))));
        verify(storage).saveQuoteLead(argThat(input -> input.getBedrooms() == null && input.getTimeline().isEmpty()), any(), isNull(), anyString(), any());
    }

    @Test void contextualValuesSurviveValidationAndOptionalDetailsCanBeOmitted() throws Exception {
        var input = validIntake();
        input.remove("timeline");
        input.set("sourceContext", "entry_location");
        input.set("sourcePageHint", "/septic-tank-location-records/");
        input.set("countyName", "Wake County");
        input.set("phone", "bad");
        mvc.perform(post("/offer-prep-septic-file-check/").params(input))
                .andExpect(status().isOk()).andExpect(content().string(containsString("Find the records with location clues.")))
                .andExpect(content().string(containsString("value=\"Wake County\"")))
                .andExpect(content().string(containsString("data-error-field=\"phone\"")));
        verifyNoInteractions(storage, notifications);
        input.set("phone", "9195550100");
        mvc.perform(post("/offer-prep-septic-file-check/").params(input))
                .andExpect(status().isOk()).andExpect(content().string(containsString("fictional-pivot-request")));
        verify(storage).saveClosingRiskRequest(argThat(saved -> saved.getTimeline().isEmpty()
                && saved.getConcernValue().isEmpty() && "entry_location".equals(saved.getSourceContext())
                && "/septic-tank-location-records/".equals(saved.getSourcePageHint())), anyString(), any());
    }

    @Test void symptomPagesOfferServiceWithoutAssumingReplacement() throws Exception {
        for (String path : java.util.List.of("/septic-backup-slow-drains/", "/wet-yard-over-septic-drain-field/")) {
            var response = mvc.perform(get(path)).andExpect(status().isOk())
                    .andExpect(content().string(containsString("Request local service help")))
                    .andExpect(content().string(containsString("not emergency dispatch")))
                    .andExpect(content().string(not(containsString("Start with the planning tool now")))).andReturn();
            assertThat((String) response.getModelAndView().getModel().get("contentQuotePath"))
                    .contains("quoteMode=true", "sourcePageHint=").doesNotContain("projectType=replacement");
        }
    }

    @Test void serviceInquiryCanBeSavedWithoutACompletedRecordsRequest() throws Exception {
        String html = mvc.perform(get("/septic-system-cost-calculator/").param("quoteMode", "true"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        var action = java.util.regex.Pattern.compile("id=\"quote-request-form\"[^>]*action=\"([^\"]+)\"").matcher(html);
        assertThat(action.find()).isTrue();
        assertThat(action.group(1)).isEqualTo("/quote-request/#quote-request");
        mvc.perform(post(action.group(1).split("#")[0]).param("stateCode", "NC").param("projectType", "pumping")
                        .param("serviceNeed", "planned_project").param("fullName", "QA Only")
                        .param("email", "qa@example.com").param("phone", "9195550100").param("zipCode", "27513")
                        .param("timeline", "this_month").param("consentAccepted", "true"))
                .andExpect(status().isOk()).andExpect(content().string(containsString("fictional-pivot-lead")));
        verify(storage).saveQuoteLead(argThat(form -> "pumping".equals(form.getProjectType())
                && form.getRecordStatus().isEmpty()), any(), any(), anyString(), any());
        verifyNoInteractions(notifications);
    }

    @Test void privateAnalyticsGuardSurvivesTemplateCompilation() throws Exception {
        String html = mvc.perform(get("/septic-backup-slow-drains/")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(html).contains("!window.location.pathname.startsWith(\"/paid-unlock/\")) {");
    }

    @Test void freeResultPublishingIsProtectedAndNewPaymentsAreRetired() throws Exception {
        mvc.perform(post("/ops/record-results/json").contentType("application/json").content("{}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/ops/paid-unlocks/json").contentType("application/json").content("{}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/paid-unlocks/old-token/paypal/orders"))
                .andExpect(status().isGone()).andExpect(content().string(containsString("now free")));
        mvc.perform(get("/paid-unlock/delivery/invalid-token"))
                .andExpect(status().isNotFound()).andExpect(header().string("Referrer-Policy", "no-referrer"))
                .andExpect(header().string("X-Robots-Tag", "noindex, nofollow, noarchive"));
    }

    @Test void diagnosisAndLocationDoNotInventAReplacementEstimate() throws Exception {
        for (String project : java.util.List.of("diagnosis", "location")) {
            mvc.perform(post("/quote-request/").param("stateCode", "NC").param("projectType", project)
                            .param("serviceNeed", "backup_slow_drains").param("fullName", "QA Only")
                            .param("email", "qa@example.com").param("phone", "9195550100").param("zipCode", "27513")
                            .param("consentAccepted", "true"))
                    .andExpect(status().isOk()).andExpect(model().attribute("result", nullValue()))
                    .andExpect(content().string(containsString("fictional-pivot-lead")));
            verify(storage).saveQuoteLead(argThat(form -> project.equals(form.getProjectType())), any(), isNull(), anyString(), any());
        }
    }

    @Test void approvedFreeResultRendersAndDownloadsWithoutPayment() throws Exception {
        byte[] bytes = "%PDF-1.7\nFictional integration fixture only".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String hash = java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(bytes));
        var result = results.createFreeDelivery(
                new com.example.septic.service.PaidUnlockStore.OfferInput("qa@example.com", "QA-ONLY", "Fictional QA property",
                        "Fictional test source", "Synthetic PDF", "Synthetic test answer", "Not a real customer file"),
                "qa-only.pdf", bytes,
                new com.example.septic.service.PaidUnlockStore.ReleaseApproval("PASS", "qa-only", "qa@example.com",
                        "Fictional test", hash, true, true, java.time.Instant.now().minusSeconds(1), "test-fixture"));
        mvc.perform(get("/paid-unlock/delivery/" + result.downloadToken()))
                .andExpect(status().isOk()).andExpect(content().string(containsString("Free result · Ready to download")))
                .andExpect(content().string(not(containsString("Payment confirmed"))))
                .andExpect(header().string("Cache-Control", containsString("no-store")));
        mvc.perform(get("/paid-unlock/download/" + result.downloadToken()))
                .andExpect(status().isOk()).andExpect(content().bytes(bytes))
                .andExpect(header().string("X-Content-SHA256", hash));
        verifyNoInteractions(notifications);
    }

    @Test void invalidOperatorReleaseFailsWithAnActionableClientError() throws Exception {
        var credentials = opsCredentials.credentials();
        String authentication = "Basic " + java.util.Base64.getEncoder().encodeToString(
                (credentials.username() + ":" + credentials.password()).getBytes(java.nio.charset.StandardCharsets.UTF_8));
        mvc.perform(post("/ops/record-results/json").header("Authorization", authentication)
                        .contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest()).andExpect(content().string(containsString("Nothing was delivered")));
        verifyNoInteractions(notifications);
    }

    private LinkedMultiValueMap<String, String> validIntake() {
        var input = new LinkedMultiValueMap<String, String>();
        input.set("email", "qa@example.com"); input.set("phone", "(919) 555-0100");
        input.set("propertyAddress", "123 Example Lane, Raleigh NC 27601"); input.set("stateCode", "NC");
        input.set("transactionRole", "owner"); input.set("helpPurpose", "location");
        input.set("timeline", "this_month"); input.set("consentAccepted", "true");
        return input;
    }
}
