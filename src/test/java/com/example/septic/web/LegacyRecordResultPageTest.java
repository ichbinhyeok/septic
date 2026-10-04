package com.example.septic.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.septic.service.PaidUnlockNotificationService;
import com.example.septic.service.PaidUnlockStore;
import com.example.septic.service.PayPalCheckoutClient;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "app.studio-preview.enabled=false")
@AutoConfigureMockMvc
class LegacyRecordResultPageTest {
    @TempDir
    static Path storageRoot;

    @DynamicPropertySource
    static void isolatedStorage(DynamicPropertyRegistry registry) {
        registry.add("app.storage.root", () -> storageRoot.toString());
    }

    @Autowired MockMvc mvc;
    @Autowired PaidUnlockStore results;
    @MockitoBean PayPalCheckoutClient payments;
    @MockitoBean PaidUnlockNotificationService notifications;

    @ParameterizedTest
    @ValueSource(strings = {"", "/"})
    void readyLegacyResultRendersFreeTransitionWithoutCheckout(String trailingSlash) throws Exception {
        PaidUnlockStore.PreparedOffer prepared = legacyOffer();

        String html = privateResultPage(prepared.publicToken(), trailingSlash);

        assertThat(html).contains(
                "Reply to your SepticPath result email with this reference.",
                "We will check the approved files and recipient before issuing the free private delivery link.")
                .doesNotContain("Your earlier payment is recorded.");
        assertThat(results.findOfferById(prepared.offer().id())).get()
                .extracting(PaidUnlockStore.Offer::status).isEqualTo("READY");
        verifyNoInteractions(payments, notifications);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "/"})
    void paidLegacyResultPreservesPaymentAndPrivateDeliveryWithoutCheckout(String trailingSlash) throws Exception {
        PaidUnlockStore.PreparedOffer prepared = legacyOffer();
        PaidUnlockStore.Fulfillment paid = results.fulfill(
                prepared.offer().id(), "QA-LEGACY-" + prepared.offer().id(),
                "qa-only@example.invalid", "29.00", "USD");

        String html = privateResultPage(prepared.publicToken(), trailingSlash);

        assertThat(html).contains("Your earlier payment is recorded.", "You will not be asked to pay again.")
                .doesNotContain("before issuing the free private delivery link.", paid.downloadToken());
        assertThat(results.findOfferById(prepared.offer().id())).get()
                .extracting(PaidUnlockStore.Offer::status).isEqualTo("PAID");
        assertThat(results.inspectDownload(paid.downloadToken())).isPresent();
        verifyNoInteractions(payments, notifications);
    }

    private String privateResultPage(String publicToken, String trailingSlash) throws Exception {
        String html = mvc.perform(get("/unlock/" + publicToken + trailingSlash))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", containsString("no-store")))
                .andExpect(header().string("X-Robots-Tag", "noindex, nofollow, noarchive"))
                .andExpect(header().string("Referrer-Policy", "no-referrer"))
                .andReturn().getResponse().getContentAsString();
        assertThat(html).contains(
                "Record results are now free.", "No paid unlock.", "QA-LEGACY-ONLY",
                "Fictional QA property", "Your original files are not made public by this change.",
                "<meta name=\"robots\" content=\"noindex,nofollow,noarchive\">")
                .doesNotContain("paypal.com", "paypalobjects.com", "paypal.Buttons", "paypal-buttons",
                        "paypalSdkUrl", "paymentConfigured", "paid-unlock.js", "/paypal/orders", "$29", "29.00",
                        "href=\"/paid-unlock/download/");
        return html;
    }

    private PaidUnlockStore.PreparedOffer legacyOffer() throws Exception {
        byte[] bytes = "%PDF-1.7\nFictional legacy result test fixture only".getBytes(StandardCharsets.UTF_8);
        String hash = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        return results.createOffer(
                new PaidUnlockStore.OfferInput("qa-only@example.invalid", "QA-LEGACY-ONLY", "Fictional QA property",
                        "Synthetic source for regression testing", "Synthetic PDF fixture",
                        "Can an earlier private result link still be opened?", "Not a real customer record"),
                "qa-legacy-only.pdf", bytes,
                new PaidUnlockStore.ReleaseApproval("PASS", "qa-legacy-only", "qa-only@example.invalid",
                        "Fictional regression fixture", hash, true, true, Instant.now().minusSeconds(1), "test-fixture"));
    }
}
