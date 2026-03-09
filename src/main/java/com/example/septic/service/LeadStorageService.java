package com.example.septic.service;

import com.example.septic.config.AppStorageProperties;
import com.example.septic.web.EstimateForm;
import com.example.septic.web.QuoteLeadForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class LeadStorageService {
    private static final DateTimeFormatter YEAR = DateTimeFormatter.ofPattern("yyyy").withZone(ZoneOffset.UTC);
    private static final DateTimeFormatter MONTH = DateTimeFormatter.ofPattern("MM").withZone(ZoneOffset.UTC);
    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("dd").withZone(ZoneOffset.UTC);
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS").withZone(ZoneOffset.UTC);

    private final AppStorageProperties storageProperties;
    private final ObjectMapper objectMapper;

    public LeadStorageService(AppStorageProperties storageProperties) {
        this.storageProperties = storageProperties;
        this.objectMapper = JsonMapper.builder().findAndAddModules().build();
    }

    @PostConstruct
    void initializeDirectories() {
        try {
            Files.createDirectories(root().resolve("leads"));
            Files.createDirectories(root().resolve("events"));
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to initialize storage directories", exception);
        }
    }

    public String saveQuoteLead(QuoteLeadForm quoteLeadForm, EstimateForm estimateForm, EstimatorResult result, String sourcePage) {
        Instant now = Instant.now();
        String leadId = UUID.randomUUID().toString();

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("leadId", leadId);
        payload.put("submittedAt", now.toString());
        payload.put("sourcePage", sourcePage);
        payload.put("calculatorUsed", "main_cost_estimator");
        payload.put("stateCode", quoteLeadForm.getStateCode());
        payload.put("projectType", quoteLeadForm.getProjectType());
        payload.put("contact", Map.of(
                "fullName", quoteLeadForm.getFullName(),
                "email", quoteLeadForm.getEmail(),
                "phone", quoteLeadForm.getPhone(),
                "zipCode", quoteLeadForm.getZipCode()
        ));
        payload.put("userInputs", Map.of(
                "bedrooms", estimateForm.getBedrooms(),
                "occupants", estimateForm.getOccupants(),
                "garbageDisposal", estimateForm.isGarbageDisposal(),
                "additionalKitchen", estimateForm.isAdditionalKitchen(),
                "soilPercStatus", estimateForm.getSoilPercStatus(),
                "highWaterTableOrShallowBedrock", estimateForm.isHighWaterTableOrShallowBedrock(),
                "accessDifficulty", estimateForm.getAccessDifficulty(),
                "timeline", estimateForm.getTimeline()
        ));
        payload.put("resultSummary", Map.of(
                "likelyMinimumTankGallons", result.likelyMinimumTankGallons(),
                "recommendedTankLowGallons", result.recommendedTankLowGallons(),
                "recommendedTankHighGallons", result.recommendedTankHighGallons(),
                "likelySystemClass", result.likelySystemClass(),
                "totalCostLow", result.totalCostLow(),
                "totalCostMid", result.totalCostMid(),
                "totalCostHigh", result.totalCostHigh(),
                "confidenceLabel", result.confidenceLabel()
        ));
        payload.put("consent", Map.of(
                "accepted", quoteLeadForm.isConsentAccepted(),
                "consentText", quoteLeadForm.getConsentTextSnapshot()
        ));

        try {
            writeLeadFile(payload, leadId, now);
            appendEvent(Map.of(
                    "eventType", "quote_form_submitted",
                    "occurredAt", now.toString(),
                    "leadId", leadId,
                    "sourcePage", sourcePage,
                    "stateCode", quoteLeadForm.getStateCode(),
                    "projectType", quoteLeadForm.getProjectType()
            ), now);
            return leadId;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to persist quote lead", exception);
        }
    }

    private void writeLeadFile(Map<String, Object> payload, String leadId, Instant now) throws IOException {
        Path directory = root()
                .resolve("leads")
                .resolve(YEAR.format(now))
                .resolve(MONTH.format(now))
                .resolve(DAY.format(now));
        Files.createDirectories(directory);

        String baseFileName = TIMESTAMP.format(now) + "-" + leadId;
        Path tempFile = directory.resolve(baseFileName + ".tmp");
        Path finalFile = directory.resolve(baseFileName + ".json");

        objectMapper.writerWithDefaultPrettyPrinter().writeValue(tempFile.toFile(), payload);
        moveAtomically(tempFile, finalFile);
    }

    private void appendEvent(Map<String, Object> event, Instant now) throws IOException {
        Path directory = root()
                .resolve("events")
                .resolve(YEAR.format(now))
                .resolve(MONTH.format(now));
        Files.createDirectories(directory);

        Path eventFile = directory.resolve(DAY.format(now) + ".ndjson");
        String line = objectMapper.writeValueAsString(event) + System.lineSeparator();
        Files.writeString(eventFile, line, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private void moveAtomically(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private Path root() {
        return Path.of(storageProperties.root());
    }
}
