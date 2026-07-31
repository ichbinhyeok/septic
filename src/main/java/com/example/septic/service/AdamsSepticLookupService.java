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
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class AdamsSepticLookupService {
    public static final String SOURCE_URL = "https://services8.arcgis.com/8G2jD4VY84pgX1Z5/arcgis/rest/services/Septic_Records/FeatureServer/6";
    private static final Duration TIMEOUT = Duration.ofSeconds(6);
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
    private final ObjectMapper objectMapper = JsonMapper.builder().build();

    public LookupResult lookup(String clue) {
        String safe = clean(clue);
        if (safe.length() < 3) return invalid();
        try {
            HttpRequest request = HttpRequest.newBuilder(uri(safe)).timeout(TIMEOUT)
                    .header("Accept", "application/json").header("User-Agent", "SepticPath Adams public septic lookup").GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) return unavailable();
            JsonNode root = objectMapper.readTree(response.body());
            if (root.has("error")) return unavailable();
            List<Candidate> candidates = new ArrayList<>();
            for (JsonNode feature : root.path("features")) {
                JsonNode a = feature.path("attributes");
                candidates.add(new Candidate(text(a,"Address_Full"), text(a,"APN"), text(a,"RECORD_ID"),
                        text(a,"PE_Description"), date(a.path("APPLICATION_DATE").asLong(0))));
            }
            return candidates.isEmpty() ? notFound() : found(candidates);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); return unavailable();
        } catch (Exception e) { return unavailable(); }
    }

    private URI uri(String clue) {
        String value = clue.toUpperCase(Locale.US).replace("'", "''");
        String where = "UPPER(Address_Full) LIKE '%"+value+"%' OR UPPER(APN) LIKE '%"+value+"%' OR UPPER(RECORD_ID) LIKE '%"+value+"%'";
        return URI.create(SOURCE_URL+"/query?f=json&where="+enc(where)+"&outFields="+enc("Address_Full,APN,RECORD_ID,PE_Description,APPLICATION_DATE")+"&returnGeometry=false&orderByFields=APPLICATION_DATE DESC&resultRecordCount=10");
    }
    private String clean(String value) { if (value == null) return ""; String v=value.strip().replaceAll("[^\\p{L}\\p{N} .#'/-]"," ").replaceAll("\\s+"," "); return v.substring(0,Math.min(v.length(),100)); }
    private String enc(String value) { return URLEncoder.encode(value, StandardCharsets.UTF_8); }
    private String text(JsonNode n,String f) { return n.path(f).asText("").strip(); }
    private String date(long millis) { return millis <= 0 ? "" : DateTimeFormatter.ISO_LOCAL_DATE.format(Instant.ofEpochMilli(millis).atZone(ZoneId.of("America/Denver"))); }
    private LookupResult invalid(){return new LookupResult("invalid","Enter an address, APN, or record number","Use at least three characters.",SOURCE_URL,List.of());}
    private LookupResult unavailable(){return new LookupResult("unavailable","The official Adams dataset did not respond","Your search clue stays on this device. Open the official county map and try there.",SOURCE_URL,List.of());}
    private LookupResult notFound(){return new LookupResult("not_found","No matching online candidate appeared","This is not an official no-record finding. Try another clue and check the official county map.",SOURCE_URL,List.of());}
    private LookupResult found(List<Candidate> c){return new LookupResult("found",c.size()+" official candidate"+(c.size()==1?"":"s")+" found","Match the address or APN before relying on a row. These are index candidates, not proof of current system condition.",SOURCE_URL,List.copyOf(c));}
    public record Candidate(String address,String apn,String recordId,String description,String applicationDate){}
    public record LookupResult(String status,String heading,String summary,String sourceUrl,List<Candidate> candidates){}
}
