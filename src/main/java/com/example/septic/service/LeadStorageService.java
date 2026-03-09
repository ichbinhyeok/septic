package com.example.septic.service;

import com.example.septic.config.AppStorageProperties;
import com.example.septic.web.EstimateForm;
import com.example.septic.web.QuoteLeadForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.List;
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
            Files.createDirectories(root().resolve("exports").resolve("pending"));
            Files.createDirectories(root().resolve("exports").resolve("daily"));
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to initialize storage directories", exception);
        }
    }

    public String saveQuoteLead(
            QuoteLeadForm quoteLeadForm,
            EstimateForm estimateForm,
            EstimatorResult result,
            String sourcePage,
            HttpServletRequest request
    ) {
        Instant now = Instant.now();
        String leadId = UUID.randomUUID().toString();
        Map<String, Object> provenance = buildProvenance(request, now, sourcePage);
        Map<String, Object> consent = orderedMap(
                "accepted", quoteLeadForm.isConsentAccepted(),
                "acceptedAt", now.toString(),
                "consentText", quoteLeadForm.getConsentTextSnapshot(),
                "consentLanguageVersion", "2026-03-09-v1"
        );

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("leadId", leadId);
        payload.put("submittedAt", now.toString());
        payload.put("sourcePage", sourcePage);
        payload.put("calculatorUsed", "main_cost_estimator");
        payload.put("stateCode", quoteLeadForm.getStateCode());
        payload.put("projectType", quoteLeadForm.getProjectType());
        payload.put("contact", orderedMap(
                "fullName", quoteLeadForm.getFullName(),
                "email", quoteLeadForm.getEmail(),
                "phone", quoteLeadForm.getPhone(),
                "zipCode", quoteLeadForm.getZipCode()
        ));
        payload.put("userInputs", orderedMap(
                "bedrooms", estimateForm.getBedrooms(),
                "occupants", estimateForm.getOccupants(),
                "garbageDisposal", estimateForm.isGarbageDisposal(),
                "additionalKitchen", estimateForm.isAdditionalKitchen(),
                "soilPercStatus", estimateForm.getSoilPercStatus(),
                "highWaterTableOrShallowBedrock", estimateForm.isHighWaterTableOrShallowBedrock(),
                "accessDifficulty", estimateForm.getAccessDifficulty(),
                "timeline", estimateForm.getTimeline()
        ));
        payload.put("resultSummary", orderedMap(
                "likelyMinimumTankGallons", result.likelyMinimumTankGallons(),
                "recommendedTankLowGallons", result.recommendedTankLowGallons(),
                "recommendedTankHighGallons", result.recommendedTankHighGallons(),
                "likelySystemClass", result.likelySystemClass(),
                "totalCostLow", result.totalCostLow(),
                "totalCostMid", result.totalCostMid(),
                "totalCostHigh", result.totalCostHigh(),
                "confidenceLabel", result.confidenceLabel()
        ));
        payload.put("consent", consent);
        payload.put("provenance", provenance);

        Map<String, Object> exportPayload = buildExportPayload(
                leadId,
                now,
                quoteLeadForm,
                estimateForm,
                result,
                consent,
                provenance
        );

        try {
            writeLeadFile(payload, leadId, now);
            writeExportFile(exportPayload, leadId, now);
            appendExportQueue(exportPayload, leadId, quoteLeadForm, estimateForm, result, now);
            appendEvent(orderedMap(
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

    private Map<String, Object> buildExportPayload(
            String leadId,
            Instant now,
            QuoteLeadForm quoteLeadForm,
            EstimateForm estimateForm,
            EstimatorResult result,
            Map<String, Object> consent,
            Map<String, Object> provenance
    ) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("exportVersion", "1.0");
        payload.put("leadId", leadId);
        payload.put("submittedAt", now.toString());
        payload.put("exportStatus", "pending_routing");
        payload.put("vertical", "home_services");
        payload.put("serviceCategory", "septic");
        payload.put("leadType", "quote_request");
        payload.put("consumer", orderedMap(
                "fullName", quoteLeadForm.getFullName(),
                "email", quoteLeadForm.getEmail(),
                "phone", quoteLeadForm.getPhone(),
                "zipCode", quoteLeadForm.getZipCode(),
                "stateCode", quoteLeadForm.getStateCode()
        ));
        payload.put("project", orderedMap(
                "projectType", quoteLeadForm.getProjectType(),
                "bedrooms", estimateForm.getBedrooms(),
                "occupants", estimateForm.getOccupants(),
                "garbageDisposal", estimateForm.isGarbageDisposal(),
                "additionalKitchen", estimateForm.isAdditionalKitchen(),
                "soilPercStatus", estimateForm.getSoilPercStatus(),
                "highWaterTableOrShallowBedrock", estimateForm.isHighWaterTableOrShallowBedrock(),
                "accessDifficulty", estimateForm.getAccessDifficulty(),
                "timeline", estimateForm.getTimeline()
        ));
        payload.put("estimate", orderedMap(
                "likelyMinimumTankGallons", result.likelyMinimumTankGallons(),
                "recommendedTankLowGallons", result.recommendedTankLowGallons(),
                "recommendedTankHighGallons", result.recommendedTankHighGallons(),
                "likelySystemClass", result.likelySystemClass(),
                "totalCostLow", result.totalCostLow(),
                "totalCostMid", result.totalCostMid(),
                "totalCostHigh", result.totalCostHigh(),
                "confidenceLabel", result.confidenceLabel()
        ));
        payload.put("consent", consent);
        payload.put("provenance", provenance);
        payload.put("routingHints", orderedMap(
                "buyerChannels", List.of("batch_json", "batch_csv"),
                "urgencyBucket", estimateForm.getTimeline(),
                "riskBand", result.likelySystemClass(),
                "geoTarget", orderedMap(
                        "stateCode", quoteLeadForm.getStateCode(),
                        "zipCode", quoteLeadForm.getZipCode()
                ),
                "tags", compactList("septic", quoteLeadForm.getStateCode(), quoteLeadForm.getProjectType(), slugify(result.likelySystemClass()))
        ));
        return payload;
    }

    private Map<String, Object> buildProvenance(HttpServletRequest request, Instant now, String sourcePage) {
        String forwardedFor = headerOrBlank(request, "X-Forwarded-For");
        String remoteAddress = forwardedFor.isBlank() ? request.getRemoteAddr() : forwardedFor.split(",")[0].trim();

        Map<String, Object> provenance = new LinkedHashMap<>();
        provenance.put("capturedAt", now.toString());
        provenance.put("sourcePage", sourcePage);
        provenance.put("submittedPath", request.getRequestURI());
        provenance.put("submittedUrl", request.getRequestURL().toString());
        provenance.put("requestMethod", request.getMethod());
        provenance.put("queryString", request.getQueryString());
        provenance.put("referrer", headerOrBlank(request, "Referer"));
        provenance.put("userAgent", headerOrBlank(request, "User-Agent"));
        provenance.put("forwardedFor", forwardedFor);
        provenance.put("remoteAddress", remoteAddress);
        return provenance;
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

    private void writeExportFile(Map<String, Object> payload, String leadId, Instant now) throws IOException {
        Path directory = root()
                .resolve("exports")
                .resolve("pending")
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

    private void appendExportQueue(
            Map<String, Object> exportPayload,
            String leadId,
            QuoteLeadForm quoteLeadForm,
            EstimateForm estimateForm,
            EstimatorResult result,
            Instant now
    ) throws IOException {
        Path directory = root()
                .resolve("exports")
                .resolve("daily")
                .resolve(YEAR.format(now))
                .resolve(MONTH.format(now));
        Files.createDirectories(directory);

        Path csvFile = directory.resolve(DAY.format(now) + ".csv");
        if (Files.notExists(csvFile)) {
            Files.writeString(csvFile, csvHeader(), StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
        }

        String exportPath = "exports/pending/" + YEAR.format(now) + "/" + MONTH.format(now) + "/" + DAY.format(now)
                + "/" + TIMESTAMP.format(now) + "-" + leadId + ".json";
        List<String> columns = new java.util.ArrayList<>();
        columns.add(leadId);
        columns.add(now.toString());
        columns.add(quoteLeadForm.getStateCode());
        columns.add(quoteLeadForm.getZipCode());
        columns.add(quoteLeadForm.getProjectType());
        columns.add(estimateForm.getTimeline());
        columns.add(result.likelySystemClass());
        columns.add(String.valueOf(result.totalCostMid()));
        columns.add(String.valueOf(quoteLeadForm.isConsentAccepted()));
        columns.add(String.valueOf(exportPayload.get("exportStatus")));
        columns.add(exportPath);
        Files.writeString(
                csvFile,
                csvRow(columns),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
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

    private String headerOrBlank(HttpServletRequest request, String headerName) {
        String value = request.getHeader(headerName);
        return value == null ? "" : value;
    }

    private String slugify(String value) {
        return value.toLowerCase()
                .replace(" ", "_")
                .replace("-", "_");
    }

    private String csvHeader() {
        return "lead_id,submitted_at,state_code,zip_code,project_type,timeline,likely_system_class,total_cost_mid,consent_accepted,export_status,export_json_path"
                + System.lineSeparator();
    }

    private String csvRow(List<String> values) {
        return values.stream()
                .map(this::csvEscape)
                .reduce((left, right) -> left + "," + right)
                .orElse("")
                + System.lineSeparator();
    }

    private String csvEscape(String value) {
        String normalized = value == null ? "" : value;
        return "\"" + normalized.replace("\"", "\"\"") + "\"";
    }

    private Map<String, Object> orderedMap(Object... keyValues) {
        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("Key values must be even in length");
        }
        Map<String, Object> map = new LinkedHashMap<>();
        for (int index = 0; index < keyValues.length; index += 2) {
            map.put(String.valueOf(keyValues[index]), keyValues[index + 1]);
        }
        return map;
    }

    private List<String> compactList(String... values) {
        List<String> list = new java.util.ArrayList<>();
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                list.add(value);
            }
        }
        return list;
    }
}
