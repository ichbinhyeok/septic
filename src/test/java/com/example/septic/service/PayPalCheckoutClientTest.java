package com.example.septic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.septic.config.PaidUnlockProperties;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PayPalCheckoutClientTest {
    private HttpServer server;
    private String baseUrl;
    private final AtomicInteger tokenRequests = new AtomicInteger();

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/v1/oauth2/token", exchange -> {
            tokenRequests.incrementAndGet();
            respond(exchange, 200, "{\"access_token\":\"sandbox-token\",\"expires_in\":3600}");
        });
        server.createContext("/v2/checkout/orders/ORDER-123", exchange -> respond(exchange, 200, completedOrder("offer-123")));
        server.createContext("/v2/checkout/orders/ORDER-MISMATCH", exchange -> respond(exchange, 200, completedOrder("another-offer")));
        server.createContext("/v1/notifications/verify-webhook-signature", exchange ->
                respond(exchange, 200, "{\"verification_status\":\"SUCCESS\"}"));
        server.start();
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    @Test
    void acceptsOnlyACompletedOrderBoundToTheExpectedOffer() {
        PayPalCheckoutClient client = client();

        PayPalCheckoutClient.CapturedPayment payment = client.getCompletedOrder("ORDER-123", "offer-123");

        assertThat(payment.captureId()).isEqualTo("CAPTURE-123");
        assertThat(payment.amount()).isEqualTo("29.00");
        assertThat(payment.currency()).isEqualTo("USD");
        assertThat(payment.payerEmail()).isEqualTo("buyer@example.com");
        assertThat(tokenRequests).hasValue(1);

        assertThatThrownBy(() -> client.getCompletedOrder("ORDER-MISMATCH", "offer-123"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("does not match");
        assertThat(tokenRequests).hasValue(1);
    }

    @Test
    void captureUsesCanonicalOrderWhenCaptureResponseOmitsIdentityFields() {
        PayPalCheckoutClient client = client();

        PayPalCheckoutClient.CapturedPayment payment = client.captureOrder("ORDER-123", "offer-123");

        assertThat(payment.captureId()).isEqualTo("CAPTURE-123");
        assertThat(payment.offerId()).isEqualTo("offer-123");
        assertThat(payment.amount()).isEqualTo("29.00");
        assertThat(payment.currency()).isEqualTo("USD");
    }

    @Test
    void verifiesWebhookByPayPalPostbackAndRejectsUntrustedCertificateUrls() throws Exception {
        PayPalCheckoutClient client = client();
        var event = JsonMapper.builder().build().readTree("{\"event_type\":\"PAYMENT.CAPTURE.COMPLETED\"}");

        assertThat(client.verifyWebhook(
                "transmission", "2026-09-19T12:00:00Z", "https://api.sandbox.paypal.com/cert.pem",
                "SHA256withRSA", "signature", event
        )).isTrue();
        assertThat(client.verifyWebhook(
                "transmission", "2026-09-19T12:00:00Z", "https://attacker.example/cert.pem",
                "SHA256withRSA", "signature", event
        )).isFalse();
    }

    private PayPalCheckoutClient client() {
        PaidUnlockProperties properties = new PaidUnlockProperties(
                true, "sandbox", "client-id", "client-secret", "webhook-id", 168, 5
        );
        return new PayPalCheckoutClient(properties, HttpClient.newHttpClient(), baseUrl);
    }

    private String completedOrder(String offerId) {
        return """
                {
                  "id":"ORDER-123",
                  "status":"COMPLETED",
                  "payer":{"email_address":"buyer@example.com"},
                  "purchase_units":[{
                    "reference_id":"%s",
                    "custom_id":"%s",
                    "payments":{"captures":[{
                      "id":"CAPTURE-123",
                      "status":"COMPLETED",
                      "amount":{"value":"29.00","currency_code":"USD"}
                    }]}
                  }]
                }
                """.formatted(offerId, offerId);
    }

    private void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}
