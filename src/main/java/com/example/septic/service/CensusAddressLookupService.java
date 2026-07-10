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
import org.springframework.stereotype.Service;

/** Resolves an address only long enough to select the relevant local records route. */
@Service
public class CensusAddressLookupService {
    private static final String GEOCODER_URL = "https://geocoding.geo.census.gov/geocoder/geographies/onelineaddress";
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(4);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public CensusAddressLookupService() {
        this(HttpClient.newBuilder().connectTimeout(REQUEST_TIMEOUT).build(), JsonMapper.builder().build());
    }

    CensusAddressLookupService(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public CensusAddressLookupResult lookup(String address) {
        try {
            HttpRequest request = HttpRequest.newBuilder(buildUri(address))
                    .timeout(REQUEST_TIMEOUT)
                    .header("Accept", "application/json")
                    .header("User-Agent", "SepticPath address record finder")
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return CensusAddressLookupResult.unavailable();
            }

            JsonNode firstMatch = objectMapper.readTree(response.body())
                    .path("result")
                    .path("addressMatches")
                    .path(0);
            if (firstMatch.isMissingNode()) {
                return CensusAddressLookupResult.notFound();
            }

            String stateCode = firstMatch.path("geographies").path("States").path(0).path("STUSAB").asText("");
            String countyName = firstMatch.path("geographies").path("Counties").path(0).path("BASENAME").asText("");
            String matchedAddress = firstMatch.path("matchedAddress").asText("");
            if (stateCode.isBlank() || countyName.isBlank()) {
                return CensusAddressLookupResult.notFound();
            }
            return CensusAddressLookupResult.matched(matchedAddress, stateCode, countyName);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return CensusAddressLookupResult.unavailable();
        } catch (Exception ignored) {
            // Address resolution is an enhancement. No address is logged or retained on failure.
            return CensusAddressLookupResult.unavailable();
        }
    }

    private URI buildUri(String address) {
        String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
        return URI.create(GEOCODER_URL
                + "?address=" + encodedAddress
                + "&benchmark=Public_AR_Current"
                + "&vintage=Current_Current"
                + "&format=json");
    }

    public record CensusAddressLookupResult(Status status, String matchedAddress, String stateCode, String countyName) {
        public enum Status {
            MATCHED,
            NOT_FOUND,
            UNAVAILABLE
        }

        static CensusAddressLookupResult matched(String matchedAddress, String stateCode, String countyName) {
            return new CensusAddressLookupResult(Status.MATCHED, matchedAddress, stateCode, countyName);
        }

        static CensusAddressLookupResult notFound() {
            return new CensusAddressLookupResult(Status.NOT_FOUND, "", "", "");
        }

        static CensusAddressLookupResult unavailable() {
            return new CensusAddressLookupResult(Status.UNAVAILABLE, "", "", "");
        }
    }
}
