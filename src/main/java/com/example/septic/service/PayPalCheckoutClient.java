package com.example.septic.service;

import com.example.septic.config.PaidUnlockProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayPalCheckoutClient {
    private final PaidUnlockProperties properties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiBaseUrl;
    private volatile AccessToken cachedToken;

    @Autowired
    public PayPalCheckoutClient(PaidUnlockProperties properties) {
        this(properties, HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build(), properties.apiBaseUrl());
    }

    PayPalCheckoutClient(PaidUnlockProperties properties, HttpClient httpClient) {
        this(properties, httpClient, properties.apiBaseUrl());
    }

    PayPalCheckoutClient(PaidUnlockProperties properties, HttpClient httpClient, String apiBaseUrl) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.objectMapper = JsonMapper.builder().findAndAddModules().build();
        this.apiBaseUrl = apiBaseUrl;
    }

    public JsonNode createOrder(PaidUnlockStore.Offer offer) {
        requireConfigured();
        Map<String, Object> payload = Map.of(
                "intent", "CAPTURE",
                "purchase_units", new Object[]{Map.of(
                        "reference_id", offer.id(),
                        "custom_id", offer.id(),
                        "description", "SepticPath reviewed property record package",
                        "amount", Map.of(
                                "currency_code", offer.currency(),
                                "value", offer.amount()
                        )
                )}
        );
        return postJson(
                "/v2/checkout/orders",
                payload,
                Map.of("PayPal-Request-Id", "create-" + UUID.randomUUID())
        );
    }

    public CapturedPayment captureOrder(String orderId, String expectedOfferId) {
        requireSafeId(orderId, "order ID");
        try {
            postJson(
                    "/v2/checkout/orders/" + orderId + "/capture",
                    Map.of(),
                    Map.of("PayPal-Request-Id", "capture-" + orderId)
            );
        } catch (PayPalApiException exception) {
            // A browser retry can reach us after PayPal has already captured the order.
            // Only a canonical completed-order lookup may recover that attempt; every
            // offer, capture, amount, and currency check still runs below.
            if (exception.statusCode() != 422) {
                throw exception;
            }
        }
        // PayPal's capture response can omit purchase-unit identity fields even
        // though the canonical order contains them. Validate the canonical order
        // before releasing a package rather than weakening the identity gate.
        return getCompletedOrder(orderId, expectedOfferId);
    }

    public CapturedPayment getCompletedOrder(String orderId, String expectedOfferId) {
        requireSafeId(orderId, "order ID");
        JsonNode response = getJson("/v2/checkout/orders/" + orderId);
        return parseCompletedOrder(response, expectedOfferId);
    }

    public boolean verifyWebhook(
            String transmissionId,
            String transmissionTime,
            String certUrl,
            String authAlgo,
            String transmissionSignature,
            JsonNode event
    ) {
        requireConfigured();
        if (blank(transmissionId) || blank(transmissionTime) || blank(certUrl)
                || blank(authAlgo) || blank(transmissionSignature)) {
            return false;
        }
        if (!certUrl.startsWith("https://api.paypal.com/")
                && !certUrl.startsWith("https://api.sandbox.paypal.com/")) {
            return false;
        }
        JsonNode response = postJson(
                "/v1/notifications/verify-webhook-signature",
                Map.of(
                        "transmission_id", transmissionId,
                        "transmission_time", transmissionTime,
                        "cert_url", certUrl,
                        "auth_algo", authAlgo,
                        "transmission_sig", transmissionSignature,
                        "webhook_id", properties.webhookId(),
                        "webhook_event", event
                ),
                Map.of()
        );
        return "SUCCESS".equals(response.path("verification_status").asText());
    }

    private CapturedPayment parseCompletedOrder(JsonNode order, String expectedOfferId) {
        if (!"COMPLETED".equals(order.path("status").asText())) {
            throw new PaymentNotCompletedException("PayPal order is not completed");
        }
        JsonNode purchaseUnit = first(order.path("purchase_units"));
        if (!expectedOfferId.equals(purchaseUnit.path("custom_id").asText())
                || !expectedOfferId.equals(purchaseUnit.path("reference_id").asText())) {
            throw new IllegalStateException("PayPal order does not match the paid-unlock offer");
        }
        JsonNode capture = first(purchaseUnit.path("payments").path("captures"));
        if (!"COMPLETED".equals(capture.path("status").asText())) {
            throw new PaymentNotCompletedException("PayPal capture is not completed");
        }
        String captureId = capture.path("id").asText();
        String amount = capture.path("amount").path("value").asText();
        String currency = capture.path("amount").path("currency_code").asText();
        String payerEmail = order.path("payer").path("email_address").asText("");
        if (blank(captureId) || blank(amount) || blank(currency)) {
            throw new IllegalStateException("PayPal capture response is incomplete");
        }
        return new CapturedPayment(order.path("id").asText(), captureId, expectedOfferId, payerEmail, amount, currency);
    }

    private JsonNode postJson(String path, Object body, Map<String, String> extraHeaders) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(apiBaseUrl + path))
                    .timeout(Duration.ofSeconds(20))
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessToken())
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)));
            extraHeaders.forEach(builder::header);
            return send(builder.build());
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to serialize PayPal request", exception);
        }
    }

    private JsonNode getJson(String path) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(apiBaseUrl + path))
                .timeout(Duration.ofSeconds(20))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + accessToken())
                .GET()
                .build();
        return send(request);
    }

    private JsonNode send(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new PayPalApiException(response.statusCode(), safeError(response.body()));
            }
            return objectMapper.readTree(response.body());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("PayPal request was interrupted", exception);
        } catch (IOException exception) {
            throw new IllegalStateException("PayPal request failed", exception);
        }
    }

    private synchronized String accessToken() {
        Instant now = Instant.now();
        if (cachedToken != null && cachedToken.expiresAt().isAfter(now.plusSeconds(30))) {
            return cachedToken.value();
        }
        String basic = Base64.getEncoder().encodeToString(
                (properties.clientId() + ":" + properties.clientSecret()).getBytes(StandardCharsets.UTF_8)
        );
        String body = "grant_type=" + URLEncoder.encode("client_credentials", StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder(URI.create(apiBaseUrl + "/v1/oauth2/token"))
                .timeout(Duration.ofSeconds(20))
                .header("Accept", "application/json")
                .header("Accept-Language", "en_US")
                .header("Authorization", "Basic " + basic)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        JsonNode response = send(request);
        String value = response.path("access_token").asText();
        long expiresIn = response.path("expires_in").asLong(300);
        if (blank(value)) {
            throw new IllegalStateException("PayPal did not return an access token");
        }
        cachedToken = new AccessToken(value, now.plusSeconds(Math.max(60, expiresIn)));
        return value;
    }

    private JsonNode first(JsonNode array) {
        if (!array.isArray() || array.isEmpty()) {
            throw new IllegalStateException("PayPal response is missing required payment data");
        }
        return array.get(0);
    }

    private void requireConfigured() {
        if (!properties.isConfigured()) {
            throw new IllegalStateException("PayPal paid unlock is not configured");
        }
    }

    private void requireSafeId(String value, String label) {
        if (value == null || !value.matches("[A-Za-z0-9._-]{3,180}")) {
            throw new IllegalArgumentException("Invalid PayPal " + label);
        }
    }

    private String safeError(String value) {
        if (value == null) {
            return "";
        }
        String cleaned = value.replace('\r', ' ').replace('\n', ' ');
        return cleaned.substring(0, Math.min(cleaned.length(), 800));
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private record AccessToken(String value, Instant expiresAt) {}

    public record CapturedPayment(
            String orderId,
            String captureId,
            String offerId,
            String payerEmail,
            String amount,
            String currency
    ) {}

    public static class PaymentNotCompletedException extends RuntimeException {
        public PaymentNotCompletedException(String message) {
            super(message);
        }
    }

    public static class PayPalApiException extends RuntimeException {
        private final int statusCode;

        public PayPalApiException(int statusCode, String message) {
            super("PayPal API returned " + statusCode + ": " + message);
            this.statusCode = statusCode;
        }

        public int statusCode() {
            return statusCode;
        }
    }
}
