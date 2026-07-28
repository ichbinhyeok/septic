package com.example.septic.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

/**
 * Queries Brunswick County's public ArcGIS permit index.
 *
 * <p>The source is a general permit index, not a septic-document repository. Results are therefore
 * returned as candidates and never described as a complete septic file.</p>
 */
@Service
public class BrunswickPermitLookupService {
    public static final String SOURCE_URL =
            "https://services1.arcgis.com/W6gamXPYQeLXrdAd/arcgis/rest/services/Permit_Locations/FeatureServer/0";
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(6);
    private static final int RESULT_LIMIT = 10;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public BrunswickPermitLookupService() {
        this(HttpClient.newBuilder().connectTimeout(REQUEST_TIMEOUT).build(), JsonMapper.builder().build());
    }

    BrunswickPermitLookupService(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public LookupResult lookup(String address, String parcelId) {
        String safeAddress = searchable(address);
        String safeParcelId = searchable(parcelId);
        if (safeAddress.length() < 3 && safeParcelId.length() < 3) {
            return LookupResult.invalid();
        }

        try {
            HttpRequest request = HttpRequest.newBuilder(buildUri(safeAddress, safeParcelId))
                    .timeout(REQUEST_TIMEOUT)
                    .header("Accept", "application/json")
                    .header("User-Agent", "SepticPath Brunswick permit metadata lookup")
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return LookupResult.unavailable();
            }

            JsonNode root = objectMapper.readTree(response.body());
            if (root.has("error")) {
                return LookupResult.unavailable();
            }

            List<PermitCandidate> candidates = new ArrayList<>();
            for (JsonNode feature : root.path("features")) {
                JsonNode attributes = feature.path("attributes");
                String projectType = text(attributes, "ProjectType");
                String category = text(attributes, "ProjectCategory");
                String permitType = text(attributes, "PermitType");
                String description = text(attributes, "Description");
                candidates.add(new PermitCandidate(
                        text(attributes, "PermitNumber"),
                        text(attributes, "ParcelAddress"),
                        text(attributes, "ParcelID"),
                        projectType,
                        category,
                        permitType,
                        text(attributes, "PermitStatus"),
                        description,
                        displayDate(text(attributes, "DateIssued")),
                        isSepticCandidate(projectType, category, permitType, description)
                ));
            }
            return candidates.isEmpty() ? LookupResult.notFound() : LookupResult.found(candidates);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return LookupResult.unavailable();
        } catch (Exception ignored) {
            return LookupResult.unavailable();
        }
    }

    private URI buildUri(String address, String parcelId) {
        List<String> clauses = new ArrayList<>();
        if (!parcelId.isBlank()) {
            clauses.add("UPPER(ParcelID) LIKE '%" + escapeSql(parcelId.toUpperCase(Locale.US)) + "%'");
        }
        if (!address.isBlank()) {
            String addressClue = streetClue(address).toUpperCase(Locale.US);
            clauses.add("UPPER(ParcelAddress) LIKE '%" + escapeSql(addressClue) + "%'");
        }
        String where = String.join(" OR ", clauses);
        String fields = "PermitNumber,ParcelAddress,ParcelID,ProjectType,ProjectCategory,"
                + "PermitType,PermitStatus,Description,DateIssued";
        return URI.create(SOURCE_URL + "/query"
                + "?f=json"
                + "&where=" + encode(where)
                + "&outFields=" + encode(fields)
                + "&returnGeometry=false"
                + "&orderByFields=" + encode("OBJECTID DESC")
                + "&resultRecordCount=" + RESULT_LIMIT);
    }

    private String searchable(String value) {
        if (value == null) {
            return "";
        }
        String cleaned = value.strip()
                .replaceAll("[^\\p{L}\\p{N} .#'/-]", " ")
                .replaceAll("\\s+", " ");
        return cleaned.substring(0, Math.min(cleaned.length(), 100));
    }

    private String streetClue(String address) {
        int comma = address.indexOf(',');
        return comma > 0 ? address.substring(0, comma).strip() : address;
    }

    private String escapeSql(String value) {
        return value.replace("'", "''");
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String text(JsonNode node, String field) {
        return node.path(field).asText("").strip();
    }

    private String displayDate(String value) {
        if (value.length() >= 10 && value.charAt(4) == '-' && value.charAt(7) == '-') {
            return value.substring(0, 10);
        }
        return value;
    }

    private boolean isSepticCandidate(String... values) {
        String combined = String.join(" ", values).toUpperCase(Locale.US);
        return combined.contains("SEPTIC")
                || combined.contains("WASTEWATER")
                || combined.contains("ONSITE")
                || combined.contains("ON-SITE")
                || combined.contains("IMPROVEMENT PERMIT");
    }

    public record PermitCandidate(
            String permitNumber,
            String parcelAddress,
            String parcelId,
            String projectType,
            String projectCategory,
            String permitType,
            String permitStatus,
            String description,
            String dateIssued,
            boolean septicCandidate
    ) {}

    public record LookupResult(
            String status,
            String heading,
            String summary,
            String sourceUrl,
            List<PermitCandidate> candidates
    ) {
        static LookupResult invalid() {
            return new LookupResult(
                    "invalid",
                    "Enter an address or parcel ID",
                    "Use at least three characters so the county index is not queried too broadly.",
                    SOURCE_URL,
                    List.of()
            );
        }

        static LookupResult unavailable() {
            return new LookupResult(
                    "unavailable",
                    "The county permit index did not respond",
                    "Your property clues remain on this device. Open the official permit report or prepare a records request.",
                    SOURCE_URL,
                    List.of()
            );
        }

        static LookupResult notFound() {
            return new LookupResult(
                    "not_found",
                    "No matching permit metadata appeared",
                    "This is not an official no-record finding. Try the parcel ID or ask Environmental Health to search the source file.",
                    SOURCE_URL,
                    List.of()
            );
        }

        static LookupResult found(List<PermitCandidate> candidates) {
            return new LookupResult(
                    "found",
                    candidates.size() + " permit candidate" + (candidates.size() == 1 ? "" : "s") + " found",
                    "These are official county index rows, not the underlying septic permit, layout, IP, CA, or OP.",
                    SOURCE_URL,
                    List.copyOf(candidates)
            );
        }
    }
}
