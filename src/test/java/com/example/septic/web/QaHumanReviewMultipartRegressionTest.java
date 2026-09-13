package com.example.septic.web;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "app.storage.root=./build/qa-multipart-test-storage",
                "app.gmail.username=",
                "app.gmail.app-password="
        }
)
class QaHumanReviewMultipartRegressionTest {

    @LocalServerPort
    private int port;

    @Test
    void acceptsAValidHumanReviewFormWithAllMultipartFields() throws Exception {
        // Regression: ISSUE-003 — Tomcat rejected the 20-part review form before reading a 356-byte file.
        // Found by /qa on 2026-09-14
        // Report: .gstack/qa-reports/qa-report-localhost-2026-09-14.md
        String boundary = "SepticPathQaBoundary";
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        field(body, boundary, "website", "");
        field(body, boundary, "sourceContext", "document_review_qa");
        field(body, boundary, "sourcePageHint", "/septic-record-finder/");
        field(body, boundary, "entryPageHint", "/septic-record-finder/?mode=document");
        field(body, boundary, "email", "qa.user@example.com");
        field(body, boundary, "propertyAddress", "100 Oak Lane, Raleigh, NC 27601");
        field(body, boundary, "stateCode", "NC");
        field(body, boundary, "recordType", "septic");
        field(body, boundary, "researchGoal", "understand_file");
        field(body, boundary, "recordStatus", "partial");
        file(body, boundary, "documents", "sample-record.txt", "text/plain",
                "Permit OWTS-2026-0142; final approval; 3 bedrooms.".getBytes(StandardCharsets.UTF_8));
        field(body, boundary, "concern", "Does this confirm three bedrooms, and what is still missing?");
        field(body, boundary, "deadline", "");
        field(body, boundary, "transactionRole", "buyer");
        field(body, boundary, "fullName", "QA Buyer");
        field(body, boundary, "countyName", "Wake County");
        field(body, boundary, "listingUrl", "");
        field(body, boundary, "listingBedrooms", "");
        field(body, boundary, "permitBedrooms", "");
        field(body, boundary, "consentAccepted", "true");
        body.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/offer-prep-septic-file-check/"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("Your document review is in.");
    }

    private static void field(ByteArrayOutputStream body, String boundary, String name, String value) throws Exception {
        body.write(("--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n"
                + value + "\r\n").getBytes(StandardCharsets.UTF_8));
    }

    private static void file(
            ByteArrayOutputStream body,
            String boundary,
            String name,
            String fileName,
            String contentType,
            byte[] content
    ) throws Exception {
        body.write(("--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + fileName + "\"\r\n"
                + "Content-Type: " + contentType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        body.write(content);
        body.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }
}
