package com.example.septic.service;

import com.example.septic.config.AppStorageProperties;
import com.example.septic.web.ClosingRiskCheckForm;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeadStoragePrivacyScrubTest {

    @TempDir
    Path storageRoot;

    @Test
    void startupScrubsHistoricalPropertyQueriesWithoutLosingWorkflowStages() throws Exception {
        Path eventFile = storageRoot.resolve("events/2026/07/28.ndjson");
        Files.createDirectories(eventFile.getParent());
        Files.writeString(eventFile, """
                {"eventType":"workflow_stage","sourcePage":"/county/?address=123%20Main&workflowRunId=query-secret&src=gsc","targetPath":"/next/?parcel=PIN-44&purpose=buying","workflowRunId":"safe-random-run-id","stage":"official_route_opened","provenance":{"sourcePage":"/county/?address=123%20Main&src=gsc","queryString":"address=123%20Main&src=gsc","referrer":"https://septicpath.com/county/?address=123%20Main&workflowRunId=query-secret&src=gsc"}}
                """, StandardCharsets.UTF_8);

        LeadStorageService service = new LeadStorageService(new AppStorageProperties(storageRoot.toString()));
        service.initializeDirectories();

        String scrubbed = Files.readString(eventFile);
        assertFalse(scrubbed.contains("123%20Main"));
        assertFalse(scrubbed.contains("query-secret"));
        assertFalse(scrubbed.contains("PIN-44"));
        assertTrue(scrubbed.contains("\"workflowRunId\":\"safe-random-run-id\""));
        assertTrue(scrubbed.contains("src=gsc"));
        assertTrue(scrubbed.contains("purpose=buying"));
        assertTrue(scrubbed.contains("\"stage\":\"official_route_opened\""));
    }

    @Test
    void closingRiskRequestKeepsPiiInDedicatedFileAndOutOfAnalyticsEvent() throws Exception {
        LeadStorageService service = new LeadStorageService(new AppStorageProperties(storageRoot.toString()));
        service.initializeDirectories();
        ClosingRiskCheckForm form = new ClosingRiskCheckForm();
        form.setFullName("Taylor Buyer");
        form.setEmail("taylor@example.com");
        form.setTransactionRole("buyer");
        form.setPropertyAddress("123 Private Lane, Knoxville, TN 37920");
        form.setStateCode("TN");
        form.setCountyName("Knox County");
        form.setListingUrl("https://example.com/listing/123");
        form.setListingBedrooms(4);
        form.setPermitBedrooms(3);
        form.setRecordStatus("conflicting");
        form.setDeadline(LocalDate.now().plusDays(6));
        form.setConcern("Need to resolve the bedroom mismatch before inspection ends.");
        form.setSourceContext("tdec_quick_help_record_help");
        form.setConsentAccepted(true);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("203.0.113.9");
        String requestId = service.saveClosingRiskRequest(
                form,
                "/offer-prep-septic-file-check/",
                request
        );

        Path requestFile;
        try (var files = Files.walk(storageRoot.resolve("closing-risk-requests"))) {
            requestFile = files.filter(path -> path.toString().endsWith(".json")).findFirst().orElseThrow();
        }
        String storedRequest = Files.readString(requestFile);
        assertTrue(storedRequest.contains(requestId));
        assertTrue(storedRequest.contains("123 Private Lane"));
        assertTrue(storedRequest.contains("taylor@example.com"));
        assertTrue(storedRequest.contains("2026-09-06-record-help-v2"));
        assertTrue(storedRequest.contains("septic_record_help_beta"));

        Path eventFile;
        try (var files = Files.walk(storageRoot.resolve("events"))) {
            eventFile = files.filter(path -> path.toString().endsWith(".ndjson")).findFirst().orElseThrow();
        }
        String analyticsEvent = Files.readString(eventFile);
        assertTrue(analyticsEvent.contains("record_help_request_submitted"));
        assertTrue(analyticsEvent.contains("tdec_quick_help_record_help"));
        assertTrue(analyticsEvent.contains("within_7_days"));
        assertFalse(analyticsEvent.contains("123 Private Lane"));
        assertFalse(analyticsEvent.contains("taylor@example.com"));
        assertFalse(analyticsEvent.contains("example.com/listing"));
    }
}
