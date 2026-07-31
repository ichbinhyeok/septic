package com.example.septic.service;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

/**
 * Queries Thurston County's public Laserfiche archive by the parcel number supplied by the user.
 *
 * <p>The archive is a county document index, not a septic-only database. Search results therefore
 * remain candidates until the user opens the county document and verifies the property and file type.</p>
 */
@Service
public class ThurstonRecordLookupService {
    public static final String SOURCE_URL =
            "https://weblink.co.thurston.wa.us/dspublic/customsearch.aspx?searchname=search";
    private static final String SEARCH_URL = SOURCE_URL + "&dbid=0";
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(8);
    private static final int RESULT_LIMIT = 10;
    private static final Pattern INPUT_TAG = Pattern.compile("(?is)<input\\b[^>]*>");
    private static final Pattern ANCHOR_TAG = Pattern.compile("(?is)<a\\b([^>]*)>(.*?)</a>");
    private static final Pattern ATTRIBUTE = Pattern.compile("(?is)\\b%s\\s*=\\s*([\"'])(.*?)\\1");

    private final HttpClient httpClient;

    public ThurstonRecordLookupService() {
        this(HttpClient.newBuilder()
                .connectTimeout(REQUEST_TIMEOUT)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
                .build());
    }

    ThurstonRecordLookupService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public LookupResult lookup(String parcelId) {
        String safeParcelId = searchable(parcelId);
        if (safeParcelId.length() < 3) {
            return LookupResult.invalid();
        }

        try {
            HttpResponse<String> formResponse = httpClient.send(HttpRequest.newBuilder(URI.create(SOURCE_URL))
                    .timeout(REQUEST_TIMEOUT)
                    .header("Accept", "text/html")
                    .header("User-Agent", "SepticPath Thurston public record lookup")
                    .GET()
                    .build(), HttpResponse.BodyHandlers.ofString());
            if (!successful(formResponse) || !formResponse.body().contains("Search_Input1")) {
                return LookupResult.unavailable();
            }

            Map<String, String> fields = hiddenFields(formResponse.body());
            fields.put("Search_Input0", "");
            fields.put("Search_Input1", safeParcelId);
            fields.put("Search_Input2", "");
            fields.put("Search_Button6", "Submit");
            HttpResponse<String> resultResponse = httpClient.send(HttpRequest.newBuilder(URI.create(SEARCH_URL))
                    .timeout(REQUEST_TIMEOUT)
                    .header("Accept", "text/html")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("User-Agent", "SepticPath Thurston public record lookup")
                    .POST(HttpRequest.BodyPublishers.ofString(formBody(fields)))
                    .build(), HttpResponse.BodyHandlers.ofString());
            if (!successful(resultResponse) || !resultResponse.body().contains("SearchResults")) {
                return LookupResult.unavailable();
            }

            List<RecordCandidate> candidates = documentCandidates(resultResponse.body());
            return candidates.isEmpty() ? LookupResult.notFound() : LookupResult.found(candidates);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return LookupResult.unavailable();
        } catch (Exception ignored) {
            return LookupResult.unavailable();
        }
    }

    private boolean successful(HttpResponse<String> response) {
        return response.statusCode() >= 200 && response.statusCode() < 300;
    }

    private Map<String, String> hiddenFields(String html) {
        Map<String, String> fields = new LinkedHashMap<>();
        Matcher matcher = INPUT_TAG.matcher(html);
        while (matcher.find()) {
            String tag = matcher.group();
            if (!"hidden".equalsIgnoreCase(attribute(tag, "type"))) {
                continue;
            }
            String name = attribute(tag, "name");
            if (name != null && !name.isBlank()) {
                fields.put(name, htmlDecode(attribute(tag, "value")));
            }
        }
        return fields;
    }

    private List<RecordCandidate> documentCandidates(String html) {
        List<RecordCandidate> candidates = new ArrayList<>();
        Set<String> seenUrls = new LinkedHashSet<>();
        Matcher matcher = ANCHOR_TAG.matcher(html);
        while (matcher.find() && candidates.size() < RESULT_LIMIT) {
            String attributes = matcher.group(1);
            if (!String.valueOf(attribute(attributes, "class")).contains("DocumentTitle")) {
                continue;
            }
            String href = htmlDecode(attribute(attributes, "href"));
            String title = stripHtml(matcher.group(2));
            if (href == null || href.isBlank() || title.isBlank()) {
                continue;
            }
            String documentUrl = URI.create(SOURCE_URL).resolve(href).toString();
            if (!seenUrls.add(documentUrl)) {
                continue;
            }
            candidates.add(new RecordCandidate(title, documentUrl, isSepticCandidate(title)));
        }
        return List.copyOf(candidates);
    }

    private String attribute(String tag, String name) {
        Matcher matcher = Pattern.compile(String.format(ATTRIBUTE.pattern(), Pattern.quote(name))).matcher(tag);
        return matcher.find() ? matcher.group(2) : "";
    }

    private String searchable(String value) {
        if (value == null) {
            return "";
        }
        String cleaned = value.strip()
                .replaceAll("[^\\p{L}\\p{N} -]", " ")
                .replaceAll("\\s+", " ");
        return cleaned.substring(0, Math.min(cleaned.length(), 40));
    }

    private String formBody(Map<String, String> fields) {
        return fields.entrySet().stream()
                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                .reduce((left, right) -> left + "&" + right)
                .orElse("");
    }

    private String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private String htmlDecode(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replace("&lt;", "<")
                .replace("&gt;", ">");
    }

    private String stripHtml(String value) {
        return htmlDecode(value.replaceAll("(?is)<[^>]+>", " ")
                .replaceAll("\\s+", " ")
                .strip());
    }

    private boolean isSepticCandidate(String value) {
        String normalized = value.toUpperCase(Locale.US);
        return normalized.contains("SEPTIC")
                || normalized.contains("ONSITE")
                || normalized.contains("ON-SITE")
                || normalized.contains("WASTEWATER")
                || normalized.contains("SEWAGE");
    }

    public record RecordCandidate(String title, String documentUrl, boolean septicCandidate) {}

    public record LookupResult(String status, String heading, String summary, String sourceUrl,
                               List<RecordCandidate> candidates) {
        static LookupResult invalid() {
            return new LookupResult("invalid", "Enter a Thurston County parcel number",
                    "Use at least three characters. The official archive accepts an exact parcel, permit, or project value and does not allow wildcards.",
                    SOURCE_URL, List.of());
        }

        static LookupResult unavailable() {
            return new LookupResult("unavailable", "The Thurston County archive did not respond",
                    "Your parcel number stays on this device. Open the official archive or use the county record-drawing form instead.",
                    SOURCE_URL, List.of());
        }

        static LookupResult notFound() {
            return new LookupResult("not_found", "No county document appeared for that parcel",
                    "This is not an official no-record finding. Check the eleven-digit parcel number, then use the county record-drawing request if the archive is incomplete.",
                    SOURCE_URL, List.of());
        }

        static LookupResult found(List<RecordCandidate> candidates) {
            return new LookupResult("found", candidates.size() + " county document candidate"
                    + (candidates.size() == 1 ? "" : "s") + " found",
                    "These are official archive results. Confirm the parcel and document type before treating a result as a septic record.",
                    SOURCE_URL, candidates);
        }
    }
}
