package com.example.septic.service;

import com.example.septic.config.AppStorageProperties;
import com.example.septic.web.ClosingRiskCheckForm;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockHttpServletRequest;
import static org.assertj.core.api.Assertions.assertThat;

class CommercialInquiryBriefTest {
    @TempDir Path root;

    @Test void recordBriefDistinguishesSuppliedDetailsFromVerifiedWork() {
        var brief = CommercialInquiryBrief.fromIntake(Map.of("requestId", "synthetic", "submittedAt", "2026-10-04T00:00:00Z",
                "property", Map.of("stateCode", "NC", "countyName", "Wake", "helpPurpose", "pumping", "recordStatus", "no_record"),
                "contact", Map.of("phone", "+1 919 555 0100", "email", "qa@example.com", "transactionRole", "owner")));
        assertThat(brief).containsEntry("inquiryClass", "record_research_request").containsEntry("purpose", "pumping")
                .containsEntry("phoneSupplied", true).containsEntry("phoneVerification", "not_verified")
                .containsEntry("agencyFindings", "not_reviewed_in_this_intake").containsEntry("bookingStatus", "not_confirmed");
        assertThat(brief.toString()).doesNotContain("qa@example.com", "919 555", "consentText", "documents");
    }

    @Test void missingInputsStayUnknownAndServiceRequestIsNotBooking() {
        var brief = CommercialInquiryBrief.fromIntake(Map.of("leadId", "synthetic-service", "projectType", "location"));
        assertThat(brief).containsEntry("inquiryClass", "service_inquiry").containsEntry("timeframe", "")
                .containsEntry("customerRole", "").containsEntry("phoneSupplied", false)
                .containsEntry("bookingStatus", "not_confirmed").containsEntry("buyerAcceptance", "not_requested");
    }

    @Test void recordStorageActuallyPersistsBriefOnlyInPrivateRequest() throws Exception {
        var service = new LeadStorageService(new AppStorageProperties(root.toString()));
        service.initializeDirectories();
        var form = new ClosingRiskCheckForm();
        form.setEmail("qa@example.com"); form.setPhone("9195550100"); form.setPropertyAddress("Fictional QA property");
        form.setStateCode("NC"); form.setCountyName("Wake"); form.setHelpPurpose("inspection");
        form.setTransactionRole("buyer"); form.setConsentAccepted(true);
        service.saveClosingRiskRequest(form, "/offer-prep-septic-file-check/", new MockHttpServletRequest());
        Path saved;
        try (var files = Files.walk(root.resolve("closing-risk-requests"))) {
            saved = files.filter(f -> f.toString().endsWith(".json")).findFirst().orElseThrow();
        }
        var json = JsonMapper.builder().findAndAddModules().build().readTree(saved.toFile());
        assertThat(json.path("commercialBrief").path("purpose").asText()).isEqualTo("inspection");
        assertThat(json.path("commercialFollowup").path("paidAmountCents").asInt()).isZero();
        try (var files = Files.walk(root.resolve("events"))) {
            for (var f : files.filter(Files::isRegularFile).toList()) assertThat(Files.readString(f)).doesNotContain("commercialBrief", "qa@example.com");
        }
    }
}
