package com.example.septic.service;

import com.example.septic.config.AppStorageProperties;
import com.example.septic.web.EventAnalyticsReport;
import com.example.septic.web.EventAnalyticsRow;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

@Service
public class EventAnalyticsService {
    private static final Duration REPORT_WINDOW = Duration.ofDays(28);
    private static final Duration SPRINT_WINDOW = Duration.ofDays(7);
    private static final DateTimeFormatter DISPLAY_TIME = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm 'UTC'")
            .withZone(ZoneOffset.UTC);

    private final AppStorageProperties storageProperties;
    private final JsonMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

    public EventAnalyticsService(AppStorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    public EventAnalyticsReport report() {
        Instant now = Instant.now();
        MutableReport report = new MutableReport(now);
        Path eventRoot = Path.of(storageProperties.root()).resolve("events");
        if (Files.notExists(eventRoot)) {
            return report.toView(true);
        }

        try (Stream<Path> paths = Files.walk(eventRoot)) {
            paths.filter(path -> path.toString().endsWith(".ndjson"))
                    .forEach(path -> readEvents(path, report));
            return report.toView(true);
        } catch (IOException | UncheckedIOException exception) {
            return report.toView(false);
        }
    }

    private void readEvents(Path eventFile, MutableReport report) {
        try {
            byte[] bytes = Files.readAllBytes(eventFile);
            Charset primaryCharset = eventCharset(bytes);
            EventReadResult primaryResult = readEvents(bytes, primaryCharset, report);
            EventReadResult utf8Fallback = primaryCharset.equals(StandardCharsets.UTF_8)
                    ? EventReadResult.empty()
                    : readEvents(bytes, StandardCharsets.UTF_8, report);
            if (primaryResult.hasCandidates() && !primaryResult.hasValidEvents() && !utf8Fallback.hasValidEvents()) {
                report.markUnreadable();
            }
        } catch (IOException exception) {
            report.markUnreadable();
        }
    }

    private EventReadResult readEvents(byte[] bytes, Charset charset, MutableReport report) {
        String content = new String(bytes, charset);
        int candidates = 0;
        int validEvents = 0;
        for (String line : content.split("\\R")) {
            String normalizedLine = withoutByteOrderMark(line).trim();
            if (normalizedLine.isBlank()) {
                continue;
            }
            candidates++;
            try {
                report.accept(objectMapper.readTree(normalizedLine));
                validEvents++;
            } catch (IOException exception) {
                // A malformed historical line must not block the operating report.
            }
        }
        return new EventReadResult(candidates, validEvents);
    }

    private Charset eventCharset(byte[] bytes) {
        if (bytes.length >= 2 && bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xFE) {
            return StandardCharsets.UTF_16LE;
        }
        if (bytes.length >= 2 && bytes[0] == (byte) 0xFE && bytes[1] == (byte) 0xFF) {
            return StandardCharsets.UTF_16BE;
        }
        return StandardCharsets.UTF_8;
    }

    private String withoutByteOrderMark(String line) {
        return line.startsWith("\uFEFF") ? line.substring(1) : line;
    }

    private static final class MutableReport {
        private final Instant now;
        private final Instant sprintStart;
        private final Instant reportStart;
        private boolean readable = true;
        private final Map<RowKey, MutableRow> officialSourceClicks = new HashMap<>();
        private final Map<RowKey, MutableRow> artifactActions = new HashMap<>();
        private final Map<RowKey, MutableRow> internalNavigationClicks = new HashMap<>();

        private MutableReport(Instant now) {
            this.now = now;
            this.sprintStart = now.minus(SPRINT_WINDOW);
            this.reportStart = now.minus(REPORT_WINDOW);
        }

        private void accept(JsonNode event) {
            String eventType = text(event, "eventType");
            Instant occurredAt = parseInstant(text(event, "occurredAt"));
            if (occurredAt == null || occurredAt.isBefore(reportStart) || occurredAt.isAfter(now.plus(Duration.ofMinutes(5)))) {
                return;
            }

            String sourcePage = text(event, "sourcePage");
            String sourceContext = text(event, "sourceContext");
            switch (eventType) {
                case "official_source_click" -> add(
                        officialSourceClicks,
                        new RowKey(sourcePage, sourceContext, firstNonBlank(text(event, "targetLabel"), text(event, "targetPath"))),
                        occurredAt
                );
                case "artifact_action" -> add(
                        artifactActions,
                        new RowKey(sourcePage, sourceContext, artifactDetail(text(event, "action"), text(event, "artifactType"))),
                        occurredAt
                );
                case "internal_navigation_click" -> add(
                        internalNavigationClicks,
                        new RowKey(sourcePage, sourceContext, firstNonBlank(text(event, "targetLabel"), text(event, "targetPath"))),
                        occurredAt
                );
                default -> {
                    // Web vitals and lead events have different monitoring surfaces.
                }
            }
        }

        private void add(Map<RowKey, MutableRow> rows, RowKey key, Instant occurredAt) {
            rows.computeIfAbsent(key, ignored -> new MutableRow(key)).add(occurredAt, sprintStart);
        }

        private EventAnalyticsReport toView(boolean eventRootReadable) {
            long officialSeven = countLastSevenDays(officialSourceClicks);
            long artifactSeven = countLastSevenDays(artifactActions);
            long internalSeven = countLastSevenDays(internalNavigationClicks);
            long officialTwentyEight = countLastTwentyEightDays(officialSourceClicks);
            long artifactTwentyEight = countLastTwentyEightDays(artifactActions);
            long internalTwentyEight = countLastTwentyEightDays(internalNavigationClicks);
            return new EventAnalyticsReport(
                    DISPLAY_TIME.format(now),
                    officialSeven + artifactSeven + internalSeven,
                    officialTwentyEight + artifactTwentyEight + internalTwentyEight,
                    officialSeven,
                    artifactSeven,
                    internalSeven,
                    readable && eventRootReadable,
                    rows(officialSourceClicks),
                    rows(artifactActions),
                    rows(internalNavigationClicks)
            );
        }

        private long countLastSevenDays(Map<RowKey, MutableRow> rows) {
            return rows.values().stream().mapToLong(row -> row.lastSevenDays).sum();
        }

        private long countLastTwentyEightDays(Map<RowKey, MutableRow> rows) {
            return rows.values().stream().mapToLong(row -> row.lastTwentyEightDays).sum();
        }

        private List<EventAnalyticsRow> rows(Map<RowKey, MutableRow> rows) {
            return rows.values().stream()
                    .sorted(Comparator.comparingLong(MutableRow::lastSevenDays).reversed()
                            .thenComparing(Comparator.comparingLong(MutableRow::lastTwentyEightDays).reversed())
                            .thenComparing(row -> row.key.detail))
                    .limit(12)
                    .map(row -> new EventAnalyticsRow(
                            row.key.sourcePage,
                            row.key.sourceContext,
                            row.key.detail,
                            row.lastSevenDays,
                            row.lastTwentyEightDays,
                            DISPLAY_TIME.format(row.lastSeenAt)
                    ))
                    .toList();
        }

        private void markUnreadable() {
            readable = false;
        }
    }

    private static final class MutableRow {
        private final RowKey key;
        private long lastSevenDays;
        private long lastTwentyEightDays;
        private Instant lastSeenAt = Instant.EPOCH;

        private MutableRow(RowKey key) {
            this.key = key;
        }

        private void add(Instant occurredAt, Instant sprintStart) {
            lastTwentyEightDays++;
            if (!occurredAt.isBefore(sprintStart)) {
                lastSevenDays++;
            }
            if (occurredAt.isAfter(lastSeenAt)) {
                lastSeenAt = occurredAt;
            }
        }

        private long lastSevenDays() {
            return lastSevenDays;
        }

        private long lastTwentyEightDays() {
            return lastTwentyEightDays;
        }
    }

    private record RowKey(String sourcePage, String sourceContext, String detail) {
    }

    private record EventReadResult(int candidates, int validEvents) {
        private static EventReadResult empty() {
            return new EventReadResult(0, 0);
        }

        private boolean hasCandidates() {
            return candidates > 0;
        }

        private boolean hasValidEvents() {
            return validEvents > 0;
        }
    }

    private static String text(JsonNode event, String field) {
        String value = event.path(field).asText("").trim();
        return value.length() <= 240 ? value : value.substring(0, 240);
    }

    private static Instant parseInstant(String value) {
        try {
            return Instant.parse(value);
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private static String artifactDetail(String action, String artifactType) {
        String normalizedAction = action.replace('_', ' ').trim();
        String normalizedArtifact = artifactType.replace('_', ' ').trim();
        return firstNonBlank(normalizedAction + " - " + normalizedArtifact, "Artifact action");
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "Unlabeled action";
    }
}
