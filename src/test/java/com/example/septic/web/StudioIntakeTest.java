package com.example.septic.web;

import com.example.septic.service.ClosingRiskNotificationService;
import com.example.septic.service.ClosingRiskRequestLimiter;
import com.example.septic.service.LeadStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.AbstractMockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"app.studio-preview.enabled=true", "app.storage.root=./build/test-storage"})
@AutoConfigureMockMvc
class StudioIntakeTest {
    @Autowired MockMvc mvc;
    @MockitoBean LeadStorageService storage;
    @MockitoBean ClosingRiskNotificationService notifications;
    @MockitoBean ClosingRiskRequestLimiter limiter;
    private static final String PATH = "/design-preview/studio/intake/";

    @BeforeEach void isolateAllExternalEffects() {
        when(limiter.allow(any())).thenReturn(true);
        when(storage.saveClosingRiskRequest(any(), anyString(), any())).thenReturn("test-reference-123");
    }

    private <T extends AbstractMockHttpServletRequestBuilder<T>> T valid(T request) {
        return request.param("email", "qa@example.com").param("propertyAddress", "123 Example Lane, Knoxville TN 37901")
                .param("stateCode", "TN").param("concern", "Please find the approved bedroom count.")
                .param("consentAccepted", "true");
    }

    @Test void independentFormAndBothModesRender() throws Exception {
        mvc.perform(get(PATH)).andExpect(status().isOk())
                .andExpect(content().string(containsString("/studio-intake.css?v=")))
                .andExpect(content().string(containsString("noindex,nofollow")))
                .andExpect(content().string(containsString("multipart/form-data")))
                .andExpect(content().string(not(containsString("/app.css"))));
        mvc.perform(get(PATH).param("mode", "review")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Your source files (required for review)")))
                .andExpect(content().string(containsString("Send my file for review")));
        verifyNoInteractions(storage, notifications);
    }

    @Test void caseHandoffCarriesOnlyKnownEditorialContext() throws Exception {
        for (var study : StudioCaseStudies.ALL) {
            var response = mvc.perform(get(PATH).param("from", "case").param("caseStudy", study.slug()))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Inspired by an investigation"))).andReturn();
            var form = (ClosingRiskCheckForm) response.getModelAndView().getModel().get("closingRiskCheckForm");
            org.assertj.core.api.Assertions.assertThat(form.getSourcePageHintValue()).isEqualTo("/design-preview/studio/work/" + study.slug() + "/");
            org.assertj.core.api.Assertions.assertThat(form.getPropertyAddressValue()).isEmpty();
            org.assertj.core.api.Assertions.assertThat(form.getConcernValue()).isEmpty();
        }
        mvc.perform(get(PATH).param("from", "case").param("caseStudy", "unknown"))
                .andExpect(status().isOk()).andExpect(content().string(not(containsString("Inspired by an investigation"))));
        mvc.perform(post(PATH).param("sourcePageHint", "/design-preview/studio/work/morgan/"))
                .andExpect(content().string(containsString("Inspired by an investigation")));
        verifyNoInteractions(storage, notifications);
    }

    @Test void calculatorHandoffPrefillsEditableInputsWithoutInventingPermitFacts() throws Exception {
        for (var project : com.example.septic.service.ProjectType.values()) {
            var response = mvc.perform(get(PATH).param("from", "calculator").param("state", "TN")
                            .param("project", project.value()).param("bedrooms", "4"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Continuing from your estimate")))
                    .andReturn();
            var form = (ClosingRiskCheckForm) response.getModelAndView().getModel().get("closingRiskCheckForm");
            org.assertj.core.api.Assertions.assertThat(form.getStateCode()).isEqualTo("TN");
            org.assertj.core.api.Assertions.assertThat(form.getConcernValue()).contains(project.label(), "4 bedrooms (not verified against a permit)");
            org.assertj.core.api.Assertions.assertThat(form.getPermitBedroomsValue()).isEmpty();
            org.assertj.core.api.Assertions.assertThat(form.getListingBedroomsValue()).isEmpty();
        }
        verifyNoInteractions(storage, notifications);
    }

    @Test void handoffRejectsUnknownInputsAndRetainsUserEditsOnValidation() throws Exception {
        mvc.perform(get(PATH).param("from", "calculator").param("state", "XX")
                        .param("project", "<script>bad</script>").param("bedrooms", "999"))
                .andExpect(status().isOk()).andExpect(content().string(not(containsString("Continuing from your estimate"))));
        mvc.perform(get(PATH).param("from", "calculator").param("project", "replacement").param("bedrooms", "999"))
                .andExpect(content().string(not(containsString("999 bedrooms"))));
        mvc.perform(post(PATH).param("stateCode", "TN").param("sourceContext", "studio_calculator")
                        .param("concern", "My edited question about replacement records.").param("email", "invalid"))
                .andExpect(content().string(containsString("My edited question about replacement records.")))
                .andExpect(content().string(containsString("Continuing from your estimate")));
        verifyNoInteractions(storage, notifications);
    }

    @Test void validationRetainsValuesAndReportsFieldsWithoutExternalEffects() throws Exception {
        mvc.perform(post(PATH).param("propertyAddress", "123 Example Lane").param("email", "invalid"))
                .andExpect(status().isOk()).andExpect(view().name("studio/intake"))
                .andExpect(content().string(containsString("value=\"123 Example Lane\"")))
                .andExpect(content().string(containsString("Enter a valid email address.")))
                .andExpect(content().string(containsString("Consent is required.")))
                .andExpect(content().string(containsString("data-error-field=\"email\"")));
        verifyNoInteractions(storage, notifications);
    }

    @Test void reviewRequiresAFileEvenWithoutJavascript() throws Exception {
        mvc.perform(valid(post(PATH)).param("intakeMode", "review"))
                .andExpect(content().string(containsString("Add at least one permit")))
                .andExpect(content().string(containsString("data-error-field=\"documents\"")));
        verifyNoInteractions(storage, notifications);
    }

    @Test void rejectsDisguisedFiles() throws Exception {
        var upload = new MockMultipartFile("documents", "record.pdf", "application/pdf", "not a pdf".getBytes());
        mvc.perform(valid(multipart(PATH).file(upload)).param("intakeMode", "review"))
                .andExpect(content().string(containsString("does not match its file type")));
        verifyNoInteractions(storage, notifications);
    }

    @Test void rejectsExcessiveFileCountsAndSizes() throws Exception {
        var small = new MockMultipartFile("documents", "record.txt", "text/plain", "QA only".getBytes());
        mvc.perform(valid(multipart(PATH).file(small).file(small).file(small).file(small)))
                .andExpect(content().string(containsString("Add no more than three files.")));
        var large = new MockMultipartFile("documents", "large.txt", "text/plain", new byte[10 * 1024 * 1024 + 1]);
        mvc.perform(valid(multipart(PATH).file(large)))
                .andExpect(content().string(containsString("Each file must be 10 MB or smaller.")));
        verifyNoInteractions(storage, notifications);
    }

    @Test void reviewUsesExistingStorageAndNotificationsAndShowsTrueReceiptStatus() throws Exception {
        var upload = new MockMultipartFile("documents", "record.txt", "text/plain", "Fictional QA fixture only".getBytes());
        when(notifications.notifyCustomerReceipt(anyString(), any())).thenReturn(true);
        mvc.perform(valid(multipart(PATH).file(upload)).param("intakeMode", "review"))
                .andExpect(status().isOk()).andExpect(content().string(containsString("test-reference-123")))
                .andExpect(content().string(containsString("We also emailed your reference")))
                .andExpect(content().string(not(containsString("<form"))));
        verify(storage).saveClosingRiskRequest(argThat(form -> form.getResearchGoal().equals("understand_file") && form.getDocuments().size() == 1), eq("/offer-prep-septic-file-check/"), any());
        verify(notifications).notifyOperator(eq("test-reference-123"), any());
        verify(storage).recordClosingRiskNotificationOutcome("test-reference-123", false, true);
    }

    @Test void successDoesNotInventAnEmailReceipt() throws Exception {
        mvc.perform(valid(post(PATH))).andExpect(content().string(containsString("An email receipt has not been confirmed.")))
                .andExpect(content().string(containsString("No payment is due now.")));
        verify(storage).recordClosingRiskNotificationOutcome("test-reference-123", false, false);
    }

    @Test void rateLimitIsVisibleAndDoesNotSend() throws Exception {
        when(limiter.allow(any())).thenReturn(false);
        mvc.perform(valid(post(PATH))).andExpect(content().string(containsString("Too many requests")));
        verifyNoInteractions(storage, notifications);
    }

    @Test void honeypotDoesNotSend() throws Exception {
        mvc.perform(valid(post(PATH)).param("website", "spam")).andExpect(status().isOk());
        verifyNoInteractions(storage, notifications);
    }

    @Test void uploadLimitRecoveryRemainsInNewPresentation() throws Exception {
        mvc.perform(get(PATH).param("mode", "review").param("uploadError", "too_large"))
                .andExpect(content().string(containsString("The upload was too large.")))
                .andExpect(content().string(containsString("Your source files (required for review)")));
        var request = new org.springframework.mock.web.MockHttpServletRequest("POST", PATH);
        request.setQueryString("mode=review");
        var handler = new SiteExceptionHandler(null, null, null);
        org.junit.jupiter.api.Assertions.assertEquals("redirect:" + PATH + "?uploadError=too_large&mode=review#intake-form", handler.handleMaxUploadSizeExceeded(request));
    }
}
