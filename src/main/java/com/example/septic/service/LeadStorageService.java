package com.example.septic.service;

import com.example.septic.config.AppStorageProperties;
import com.example.septic.web.EstimateForm;
import com.example.septic.web.ContactRequestForm;
import com.example.septic.web.QuoteLeadForm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
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
            Files.createDirectories(root().resolve("contact-requests"));
            Files.createDirectories(root().resolve("events"));
            Files.createDirectories(root().resolve("exports").resolve("pending"));
            Files.createDirectories(root().resolve("exports").resolve("daily"));
            scrubHistoricalEventQueries();
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
        String sanitizedSourcePageHint = sanitizeSourcePageHint(quoteLeadForm.getSourcePageHint()).orElse("");
        String effectiveSourcePage = sanitizedSourcePageHint.isBlank() ? sourcePage : sanitizedSourcePageHint;
        Map<String, Object> provenance = buildProvenance(request, now, effectiveSourcePage);
        Map<String, Object> consent = orderedMap(
                "accepted", quoteLeadForm.isConsentAccepted(),
                "acceptedAt", now.toString(),
                "consentText", quoteLeadForm.getConsentTextSnapshot(),
                "consentLanguageVersion", "2026-03-09-v1"
        );

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("leadId", leadId);
        payload.put("submittedAt", now.toString());
        payload.put("sourcePage", effectiveSourcePage);
        payload.put("sourcePageHint", sanitizedSourcePageHint);
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
                    "sourcePage", effectiveSourcePage,
                    "sourcePageHint", sanitizedSourcePageHint,
                    "stateCode", quoteLeadForm.getStateCode(),
                    "projectType", quoteLeadForm.getProjectType()
            ), now);
            return leadId;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to persist quote lead", exception);
        }
    }

    public void saveNavigationClick(
            String sourcePage,
            String sourceContext,
            String targetPath,
            String targetType,
            String targetLabel,
            HttpServletRequest request
    ) {
        Instant now = Instant.now();
        String safeSourcePage = safeTrackingSourcePage(sourcePage);
        String safeTargetPath = targetPath != null && targetPath.startsWith("/")
                ? safeTrackingSourcePage(targetPath)
                : safeValue(targetPath, 240);
        try {
            appendEvent(orderedMap(
                    "eventType", targetPath != null && targetPath.startsWith("https://")
                            ? "official_source_click"
                            : "internal_navigation_click",
                    "occurredAt", now.toString(),
                    "sourcePage", safeSourcePage,
                    "sourceContext", safeValue(sourceContext, 120),
                    "targetPath", safeTargetPath,
                    "targetType", safeValue(targetType, 80),
                    "targetLabel", safeValue(targetLabel, 160),
                    "provenance", buildProvenance(request, now, safeSourcePage)
            ), now);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to persist navigation click event", exception);
        }
    }

    public void saveArtifactAction(
            String sourcePage,
            String sourceContext,
            String action,
            String artifactType,
            HttpServletRequest request
    ) {
        Instant now = Instant.now();
        String safeSourcePage = safeTrackingSourcePage(sourcePage);
        try {
            appendEvent(orderedMap(
                    "eventType", "artifact_action",
                    "occurredAt", now.toString(),
                    "sourcePage", safeSourcePage,
                    "sourceContext", safeValue(sourceContext, 120),
                    "action", safeValue(action, 64),
                    "artifactType", safeValue(artifactType, 64),
                    "provenance", buildProvenance(request, now, safeSourcePage)
            ), now);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to persist artifact action event", exception);
        }
    }

    public void saveWorkflowStage(
            String sourcePage,
            String sourceContext,
            String workflowRunId,
            String countyKey,
            String stage,
            String outcome,
            HttpServletRequest request
    ) {
        Instant now = Instant.now();
        String safeSourcePage = safeTrackingSourcePage(sourcePage);
        try {
            appendEvent(orderedMap(
                    "eventType", "workflow_stage",
                    "occurredAt", now.toString(),
                    "sourcePage", safeSourcePage,
                    "sourceContext", safeValue(sourceContext, 120),
                    "workflowRunId", safeValue(workflowRunId, 64),
                    "countyKey", safeValue(countyKey, 80),
                    "stage", safeValue(stage, 64),
                    "outcome", safeValue(outcome, 64),
                    "provenance", buildProvenance(request, now, safeSourcePage)
            ), now);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to persist workflow stage event", exception);
        }
    }

    public void saveWebVital(
            String metricName,
            Double value,
            String rating,
            String sourcePage,
            String navigationType,
            HttpServletRequest request
    ) {
        Instant now = Instant.now();
        String safeSourcePage = safeTrackingSourcePage(sourcePage);
        try {
            appendEvent(orderedMap(
                    "eventType", "web_vital",
                    "occurredAt", now.toString(),
                    "metricName", safeValue(metricName, 32),
                    "value", value,
                    "rating", safeValue(rating, 32),
                    "sourcePage", safeSourcePage,
                    "navigationType", safeValue(navigationType, 60),
                    "provenance", buildProvenance(request, now, safeSourcePage)
            ), now);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to persist web vital event", exception);
        }
    }

    private String safeTrackingSourcePage(String sourcePage) {
        String value = safeValue(sourcePage, 240);
        try {
            URI uri = URI.create(value);
            String path = uri.getRawPath();
            if (path == null || !path.startsWith("/") || path.startsWith("//")) {
                return "/";
            }
            String query = uri.getRawQuery();
            if (query == null || query.isBlank()) {
                return path;
            }
            List<String> safeParameters = java.util.Arrays.stream(query.split("&"))
                    .filter(parameter -> {
                        String[] pair = parameter.split("=", 2);
                        if (pair.length != 2 || !pair[1].matches("[A-Za-z0-9._~-]{1,80}")) {
                            return false;
                        }
                        return switch (pair[0]) {
                            case "src", "utm_source", "utm_medium", "utm_campaign", "utm_content", "utm_term",
                                    "mode", "purpose", "projectType", "recordsMode" -> true;
                            default -> false;
                        };
                    })
                    .toList();
            return safeParameters.isEmpty() ? path : path + "?" + String.join("&", safeParameters);
        } catch (IllegalArgumentException exception) {
            return "/";
        }
    }

    public String saveContactRequest(
            ContactRequestForm contactRequestForm,
            String sourcePage,
            HttpServletRequest request
    ) {
        Instant now = Instant.now();
        String requestId = UUID.randomUUID().toString();
        Map<String, Object> acknowledgement = orderedMap(
                "accepted", contactRequestForm.isAcknowledgementAccepted(),
                "acceptedAt", now.toString(),
                "acknowledgementText", contactRequestForm.getAcknowledgementTextSnapshot(),
                "languageVersion", "2026-03-10-v1"
        );

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("requestId", requestId);
        payload.put("submittedAt", now.toString());
        payload.put("sourcePage", sourcePage);
        payload.put("topic", safeValue(contactRequestForm.getTopic(), 80));
        payload.put("stateCode", safeValue(contactRequestForm.getStateCode(), 2));
        payload.put("contact", orderedMap(
                "fullName", safeValue(contactRequestForm.getFullName(), 120),
                "email", safeValue(contactRequestForm.getEmail(), 160)
        ));
        payload.put("message", safeValue(contactRequestForm.getMessage(), 2000));
        payload.put("acknowledgement", acknowledgement);
        payload.put("provenance", buildProvenance(request, now, sourcePage));

        try {
            writeContactRequestFile(payload, requestId, now);
            appendEvent(orderedMap(
                    "eventType", "contact_request_submitted",
                    "occurredAt", now.toString(),
                    "requestId", requestId,
                    "sourcePage", sourcePage,
                    "topic", safeValue(contactRequestForm.getTopic(), 80),
                    "stateCode", safeValue(contactRequestForm.getStateCode(), 2)
            ), now);
            return requestId;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to persist contact request", exception);
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
        payload.put("source", orderedMap(
                "sourcePage", provenance.get("sourcePage"),
                "sourcePageHint", sanitizeSourcePageHint(quoteLeadForm.getSourcePageHint()).orElse("")
        ));
        payload.put("routingHints", orderedMap(
                "buyerChannels", List.of("batch_json", "batch_csv"),
                "urgencyBucket", estimateForm.getTimeline(),
                "riskBand", result.likelySystemClass(),
                "sourcePage", provenance.get("sourcePage"),
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
        provenance.put("queryString", safeRequestQuery(request));
        provenance.put("referrer", safeReferrer(request));
        provenance.put("userAgent", headerOrBlank(request, "User-Agent"));
        provenance.put("forwardedFor", forwardedFor);
        provenance.put("remoteAddress", remoteAddress);
        return provenance;
    }

    private String safeRequestQuery(HttpServletRequest request) {
        String query = request.getQueryString();
        if (query == null || query.isBlank()) {
            return null;
        }
        String safePath = safeTrackingSourcePage(request.getRequestURI() + "?" + query);
        int queryIndex = safePath.indexOf('?');
        return queryIndex < 0 ? null : safePath.substring(queryIndex + 1);
    }

    private String safeReferrer(HttpServletRequest request) {
        String referrer = headerOrBlank(request, "Referer");
        if (referrer.isBlank()) {
            return "";
        }
        try {
            URI uri = URI.create(referrer);
            if (uri.getScheme() == null || uri.getHost() == null) {
                return "";
            }
            String origin = uri.getScheme() + "://" + uri.getHost()
                    + (uri.getPort() == -1 ? "" : ":" + uri.getPort());
            boolean sameSite = uri.getHost().equalsIgnoreCase(request.getServerName())
                    || uri.getHost().equalsIgnoreCase("septicpath.com")
                    || uri.getHost().endsWith(".septicpath.com");
            if (!sameSite) {
                return origin;
            }
            String relative = (uri.getRawPath() == null ? "/" : uri.getRawPath())
                    + (uri.getRawQuery() == null ? "" : "?" + uri.getRawQuery());
            return origin + safeTrackingSourcePage(relative);
        } catch (IllegalArgumentException exception) {
            return "";
        }
    }

    private void scrubHistoricalEventQueries() throws IOException {
        Path eventsRoot = root().resolve("events");
        try (var paths = Files.walk(eventsRoot)) {
            for (Path eventFile : paths.filter(path -> path.toString().endsWith(".ndjson")).toList()) {
                scrubHistoricalEventFile(eventFile);
            }
        }
    }

    private void scrubHistoricalEventFile(Path eventFile) throws IOException {
        byte[] bytes = Files.readAllBytes(eventFile);
        String content = new String(bytes, storedEventCharset(bytes));
        List<String> originalLines = java.util.Arrays.asList(content.split("\\R", -1));
        List<String> safeLines = new java.util.ArrayList<>(originalLines.size());
        boolean changed = false;
        for (String line : originalLines) {
            String normalizedLine = line.startsWith("\uFEFF") ? line.substring(1) : line;
            if (normalizedLine.isBlank()) {
                safeLines.add(line);
                continue;
            }
            try {
                JsonNode parsed = objectMapper.readTree(normalizedLine);
                if (!(parsed instanceof ObjectNode event)) {
                    safeLines.add(line);
                    continue;
                }
                changed |= scrubEventNode(event);
                safeLines.add(objectMapper.writeValueAsString(event));
            } catch (IOException exception) {
                safeLines.add(line);
            }
        }
        if (!changed) {
            return;
        }
        Path tempFile = eventFile.resolveSibling(eventFile.getFileName() + ".privacy-scrub.tmp");
        Files.write(tempFile, safeLines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        moveAtomically(tempFile, eventFile);
    }

    private Charset storedEventCharset(byte[] bytes) {
        if (bytes.length >= 2 && bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xFE) {
            return StandardCharsets.UTF_16LE;
        }
        if (bytes.length >= 2 && bytes[0] == (byte) 0xFE && bytes[1] == (byte) 0xFF) {
            return StandardCharsets.UTF_16BE;
        }
        return StandardCharsets.UTF_8;
    }

    private boolean scrubEventNode(ObjectNode event) {
        boolean changed = replaceText(event, "sourcePage", safeTrackingSourcePage(event.path("sourcePage").asText("")));
        String targetPath = event.path("targetPath").asText("");
        if (targetPath.startsWith("/")) {
            changed |= replaceText(event, "targetPath", safeTrackingSourcePage(targetPath));
        }
        JsonNode provenanceNode = event.path("provenance");
        if (provenanceNode instanceof ObjectNode provenance) {
            changed |= replaceText(provenance, "sourcePage",
                    safeTrackingSourcePage(provenance.path("sourcePage").asText("")));
            changed |= replaceText(provenance, "referrer",
                    safeStoredReferrer(provenance.path("referrer").asText("")));
            String query = provenance.path("queryString").asText("");
            String safeQueryPath = safeTrackingSourcePage("/?" + query);
            int queryIndex = safeQueryPath.indexOf('?');
            String safeQuery = queryIndex < 0 ? "" : safeQueryPath.substring(queryIndex + 1);
            changed |= replaceText(provenance, "queryString", safeQuery);
        }
        return changed;
    }

    private String safeStoredReferrer(String referrer) {
        if (referrer == null || referrer.isBlank()) {
            return "";
        }
        try {
            URI uri = URI.create(referrer);
            if (uri.getScheme() == null || uri.getHost() == null) {
                return "";
            }
            String origin = uri.getScheme() + "://" + uri.getHost()
                    + (uri.getPort() == -1 ? "" : ":" + uri.getPort());
            boolean septicPathPage = uri.getHost().equalsIgnoreCase("septicpath.com")
                    || uri.getHost().endsWith(".septicpath.com")
                    || uri.getHost().equalsIgnoreCase("127.0.0.1")
                    || uri.getHost().equalsIgnoreCase("localhost");
            if (!septicPathPage) {
                return origin;
            }
            String relative = (uri.getRawPath() == null ? "/" : uri.getRawPath())
                    + (uri.getRawQuery() == null ? "" : "?" + uri.getRawQuery());
            return origin + safeTrackingSourcePage(relative);
        } catch (IllegalArgumentException exception) {
            return "";
        }
    }

    private boolean replaceText(ObjectNode node, String field, String safeValue) {
        if (!node.has(field)) {
            return false;
        }
        String current = node.path(field).asText("");
        if (current.equals(safeValue)) {
            return false;
        }
        node.put(field, safeValue);
        return true;
    }

    private java.util.Optional<String> sanitizeSourcePageHint(String sourcePageHint) {
        if (sourcePageHint == null || sourcePageHint.isBlank()) {
            return java.util.Optional.empty();
        }
        String trimmed = safeValue(sourcePageHint, 240);
        if (!trimmed.startsWith("/") || trimmed.startsWith("//")) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(trimmed);
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

    private void writeContactRequestFile(Map<String, Object> payload, String requestId, Instant now) throws IOException {
        Path directory = root()
                .resolve("contact-requests")
                .resolve(YEAR.format(now))
                .resolve(MONTH.format(now))
                .resolve(DAY.format(now));
        Files.createDirectories(directory);

        String baseFileName = TIMESTAMP.format(now) + "-" + requestId;
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

    private String safeValue(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        if (trimmed.length() <= maxLength) {
            return trimmed;
        }
        return trimmed.substring(0, maxLength);
    }
}
