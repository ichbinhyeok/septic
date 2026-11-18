package com.example.septic.service;

import com.example.septic.config.AppStorageProperties;
import com.example.septic.web.ClosingRiskCheckForm;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockHttpServletRequest;
import static org.assertj.core.api.Assertions.assertThat;

class FreeIntakeStorageTest {
    @TempDir Path directory;

    @Test void unpricedServiceInquiryExportsWithoutFabricatedCosts() throws Exception {
        var service = new LeadStorageService(new AppStorageProperties(directory.toString()));
        service.initializeDirectories();
        var form = new com.example.septic.web.QuoteLeadForm();
        form.setStateCode("NC"); form.setProjectType("diagnosis"); form.setServiceNeed("backup_slow_drains");
        form.setEmail("qa@example.com"); form.setPhone("9195550100"); form.setFullName("Fictional QA");
        form.setZipCode("27513"); form.setConsentAccepted(true);
        service.saveQuoteLead(form, form.toEstimateForm(), null, "/septic-backup-slow-drains/", new MockHttpServletRequest());
        try (var files = Files.walk(directory.resolve("exports/pending"))) {
            var file = files.filter(path -> path.toString().endsWith(".json")).findFirst().orElseThrow();
            var saved = JsonMapper.builder().build().readTree(Files.readString(file));
            assertThat(saved.at("/project/projectType").asText()).isEqualTo("diagnosis");
            assertThat(saved.at("/project/bedrooms").isNull()).isTrue();
            assertThat(saved.at("/estimate/status").asText()).isEqualTo("not_estimated");
            assertThat(saved.at("/estimate/totalCostMid").isMissingNode()).isTrue();
            assertThat(saved.at("/routingHints/riskBand").asText()).isEqualTo("not_assessed");
            assertThat(saved.path("exportStatus").asText()).isEqualTo("pending_routing");
            for (String field : java.util.List.of("occupants", "garbageDisposal", "additionalKitchen",
                    "soilPercStatus", "highWaterTableOrShallowBedrock", "accessDifficulty")) {
                assertThat(saved.at("/project/" + field).isNull()).as("Uncollected " + field).isTrue();
            }
        }
        try (var files = Files.walk(directory.resolve("leads"))) {
            var file = files.filter(path -> path.toString().endsWith(".json")).findFirst().orElseThrow();
            var saved = JsonMapper.builder().build().readTree(Files.readString(file));
            assertThat(saved.at("/userInputs/accessDifficulty").isNull()).isTrue();
            assertThat(saved.at("/userInputs/highWaterTableOrShallowBedrock").isNull()).isTrue();
            assertThat(saved.at("/userInputs/timeline").asText()).isEmpty();
        }
    }

    @Test void newCommercialContextIsSavedWithoutInventingAConfirmedLead() throws Exception {
        var service = new LeadStorageService(new AppStorageProperties(directory.toString()));
        service.initializeDirectories();
        var form = new ClosingRiskCheckForm();
        form.setEmail("qa@example.com"); form.setPhone("9195550100"); form.setTransactionRole("owner");
        form.setPropertyAddress("123 Example Lane, Raleigh NC 27601"); form.setStateCode("NC");
        form.setHelpPurpose("location"); form.setTimeline("this_month"); form.setConsentAccepted(true);
        form.setSourcePageHint("/septic-tank-location-records/");
        form.setSourceContext("entry_location");
        form.setEntryPageHint("/no-septic-records-found/");
        form.setEntryChannelHint("bing_search");
        form.setCtaContextHint("tank_location_missing_record");
        String id = service.saveClosingRiskRequest(form, "/offer-prep-septic-file-check/", new MockHttpServletRequest());
        try (var files = Files.walk(directory.resolve("closing-risk-requests"))) {
            Path file = files.filter(path -> path.toString().endsWith(".json") && path.getFileName().toString().contains(id)).findFirst().orElseThrow();
            var saved = JsonMapper.builder().build().readTree(Files.readString(file));
            assertThat(saved.at("/property/helpPurpose").asText()).isEqualTo("location");
            assertThat(saved.at("/property/timeline").asText()).isEqualTo("this_month");
            assertThat(saved.at("/offer/optionalUnlockAmountCents").asInt()).isZero();
            assertThat(saved.at("/offer/resultsIncluded").asBoolean()).isTrue();
            assertThat(saved.at("/consent/languageVersion").asText()).isEqualTo(RecordHelpOffer.VERSION);
            assertThat(saved.at("/consent/consentText").asText()).contains("professionals", "free").doesNotContain("$29");
            assertThat(saved.at("/commercialFollowup/fieldWorkIntent").asText()).isEqualTo("not_confirmed");
            assertThat(saved.at("/commercialFollowup/phoneVerification").asText()).isEqualTo("not_verified");
            assertThat(saved.at("/commercialFollowup/paidAmountCents").asInt()).isZero();
            assertThat(saved.at("/attribution/sourcePage").asText()).isEqualTo("/septic-tank-location-records/");
            assertThat(saved.at("/attribution/sourceContext").asText()).isEqualTo("entry_location");
            assertThat(saved.at("/attribution/entryChannel").asText()).isEqualTo("bing_search");
            assertThat(saved.at("/attribution/entryPage").asText()).isEqualTo("/no-septic-records-found/");
            assertThat(saved.at("/attribution/ctaContext").asText()).isEqualTo("tank_location_missing_record");
        }
    }

    @Test void savedRequestReportCountsUniqueRequestsAndKeepsUnknownHistory() throws Exception {
        Instant now = Instant.now().minusSeconds(5);
        Path day = directory.resolve("closing-risk-requests")
                .resolve(now.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("uuuu/MM/dd")));
        Files.createDirectories(day);
        String observed = """
                {"requestId":"observed", "submittedAt":"%s", "attribution":{
                "entryPage":"/no-septic-records-found/?utm_term=withheld",
                "entryChannel":"google_search", "sourcePage":"/septic-tank-location-records/",
                "ctaContext":"tank_location_missing_record"}, "contact":{"email":"fixture@example.invalid"}}
                """.formatted(now);
        Files.writeString(day.resolve("observed.json"), observed);
        Files.writeString(day.resolve("duplicate.json"), observed);
        Files.writeString(day.resolve("legacy.json"), """
                {"requestId":"legacy", "submittedAt":"%s", "attribution":{"entryChannel":"https://invalid.example","ctaContext":"raw email@example.invalid"}}
                """.formatted(now));
        Files.writeString(day.resolve("other.tmp"), observed);
        Files.writeString(day.resolve("bad.json"), "{bad");

        var report = new EventAnalyticsService(new AppStorageProperties(directory.toString())).storedRecordHelpReport();
        assertThat(report.readable()).isFalse();
        assertThat(report.requests()).isEqualTo(2);
        assertThat(report.channels()).containsEntry("google_search", 1L).containsEntry("unknown", 1L);
        assertThat(report.rows()).anySatisfy(row -> {
            assertThat(row.channel()).isEqualTo("google_search");
            assertThat(row.entryPage()).isEqualTo("/no-septic-records-found/");
            assertThat(row.ctaContext()).isEqualTo("tank_location_missing_record");
        }).anySatisfy(row -> {
            assertThat(row.channel()).isEqualTo("unknown");
            assertThat(row.ctaContext()).isEqualTo("unknown");
        });
        assertThat(report.toString()).doesNotContain("withheld", "fixture@example.invalid", "raw email");
    }
}
