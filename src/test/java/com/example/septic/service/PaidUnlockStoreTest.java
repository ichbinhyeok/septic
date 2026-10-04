package com.example.septic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.septic.config.AppStorageProperties;
import com.example.septic.config.PaidUnlockProperties;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HexFormat;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PaidUnlockStoreTest {
    @TempDir
    Path tempDirectory;

    @Test
    void onlyAnApprovedUntamperedPackageCanBeReleased() throws Exception {
        MutableClock clock = new MutableClock(Instant.parse("2026-09-19T12:00:00Z"));
        PaidUnlockStore store = store(clock);
        byte[] packageBytes = "reviewed package".getBytes(StandardCharsets.UTF_8);
        String packageHash = sha256(packageBytes);

        PaidUnlockStore.PreparedOffer prepared = store.createOffer(
                input(),
                "SepticPath_Record_Package.pdf",
                packageBytes,
                approval(packageHash)
        );

        assertThat(store.findReadyOfferByPublicToken(prepared.publicToken())).isPresent();
        String storedOffer = Files.readString(tempDirectory.resolve("paid-unlocks/offers/" + prepared.offer().id() + ".json"));
        assertThat(storedOffer).doesNotContain(prepared.publicToken());

        assertThatThrownBy(() -> store.fulfill(prepared.offer().id(), "CAPTURE-WRONG", "payer@example.com", "28.99", "USD"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("amount or currency");

        PaidUnlockStore.Fulfillment fulfillment = store.fulfill(
                prepared.offer().id(), "CAPTURE-123", "payer@example.com", "29.00", "USD"
        );
        assertThat(fulfillment.newlyCreated()).isTrue();
        assertThat(store.authorizeDownload(fulfillment.downloadToken())).isPresent();

        PaidUnlockStore.Fulfillment duplicate = store.fulfill(
                prepared.offer().id(), "CAPTURE-123", "payer@example.com", "29.00", "USD"
        );
        assertThat(duplicate.newlyCreated()).isFalse();
        assertThat(duplicate.downloadToken()).isEqualTo(fulfillment.downloadToken());

        Path storedPackage = tempDirectory.resolve("paid-unlocks/packages/" + prepared.offer().packageStorageName());
        Files.writeString(storedPackage, "tampered");
        assertThat(store.authorizeDownload(fulfillment.downloadToken())).isEmpty();
    }

    @Test
    void downloadGrantExpiresAndHonorsTheDownloadLimit() {
        MutableClock clock = new MutableClock(Instant.parse("2026-09-19T12:00:00Z"));
        PaidUnlockStore store = store(clock);
        byte[] packageBytes = "reviewed package".getBytes(StandardCharsets.UTF_8);
        PaidUnlockStore.PreparedOffer prepared = store.createOffer(
                input(), "package.zip", packageBytes, approval(sha256(packageBytes))
        );
        PaidUnlockStore.Fulfillment fulfillment = store.fulfill(
                prepared.offer().id(), "CAPTURE-LIMIT", "payer@example.com", "29.00", "USD"
        );

        assertThat(store.inspectDownload(fulfillment.downloadToken())).get()
                .extracting(PaidUnlockStore.DeliveryPreview::downloadsRemaining).isEqualTo(2);
        assertThat(store.authorizeDownload(fulfillment.downloadToken())).isPresent();
        assertThat(store.inspectDownload(fulfillment.downloadToken())).get()
                .extracting(PaidUnlockStore.DeliveryPreview::downloadsRemaining).isEqualTo(1);
        assertThat(store.authorizeDownload(fulfillment.downloadToken())).isPresent();
        assertThat(store.inspectDownload(fulfillment.downloadToken())).isEmpty();
        assertThat(store.authorizeDownload(fulfillment.downloadToken())).isEmpty();

        PaidUnlockStore.PreparedOffer expiring = store.createOffer(
                input(), "another.pdf", packageBytes, approval(sha256(packageBytes))
        );
        PaidUnlockStore.Fulfillment expiringFulfillment = store.fulfill(
                expiring.offer().id(), "CAPTURE-EXPIRY", "payer@example.com", "29.00", "USD"
        );
        clock.set(Instant.parse("2026-09-19T14:00:01Z"));
        assertThat(store.authorizeDownload(expiringFulfillment.downloadToken())).isEmpty();
    }

    @Test
    void redactedPreviewsRequireAttestationAndStaySeparateFromThePaidPackage() throws Exception {
        PaidUnlockStore store = store(Clock.fixed(Instant.parse("2026-09-19T12:00:00Z"), ZoneOffset.UTC));
        byte[] packageBytes = "reviewed package".getBytes(StandardCharsets.UTF_8);
        PaidUnlockStore.PreparedOffer prepared = store.createOffer(
                input(), "package.zip", packageBytes, approval(sha256(packageBytes))
        );
        byte[] png = new byte[] {
                (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x01
        };

        assertThatThrownBy(() -> store.saveRedactedPreview(
                prepared.offer().id(), PaidUnlockStore.OFFICIAL_RECORD_PREVIEW, png, false
        )).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("attested");

        PaidUnlockStore.PreviewAsset preview = store.saveRedactedPreview(
                prepared.offer().id(), PaidUnlockStore.OFFICIAL_RECORD_PREVIEW, png, true
        );
        assertThat(preview.contentType()).isEqualTo("image/png");
        assertThat(store.previewSlots(prepared.offer().id()))
                .containsExactly(PaidUnlockStore.OFFICIAL_RECORD_PREVIEW);
        assertThat(store.findPreview(prepared.offer().id(), PaidUnlockStore.OFFICIAL_RECORD_PREVIEW))
                .get().extracting(PaidUnlockStore.PreviewAsset::sha256).isEqualTo(sha256(png));
        assertThat(Files.readAllBytes(preview.path())).isEqualTo(png);
    }

    private PaidUnlockStore store(Clock clock) {
        PaidUnlockProperties properties = new PaidUnlockProperties(
                true, "sandbox", "client", "secret", "webhook", 2, 2
        );
        PaidUnlockStore store = new PaidUnlockStore(
                new AppStorageProperties(tempDirectory.toString()), properties, clock, new SecureRandom()
        );
        store.initialize();
        return store;
    }

    @Test
    void freeResultsUseLimitedPrivateGrantsWithoutWritingPayments() throws Exception {
        MutableClock clock = new MutableClock(Instant.parse("2026-09-19T12:00:00Z"));
        PaidUnlockStore store = store(clock);
        byte[] bytes = "fictional reviewed PDF fixture".getBytes(StandardCharsets.UTF_8);
        var free = store.createFreeDelivery(input(), "free-result.pdf", bytes, approval(sha256(bytes)));
        assertThat(free.offer().status()).isEqualTo("RELEASED_FREE");
        assertThat(free.offer().amount()).isEqualTo("0.00");
        assertThat(store.inspectDownload(free.downloadToken())).isPresent();
        assertThat(store.authorizeDownload(free.downloadToken())).isPresent();
        assertThat(store.authorizeDownload(free.downloadToken())).isPresent();
        assertThat(store.authorizeDownload(free.downloadToken())).isEmpty();
        assertThat(store.findReadyOfferByPublicToken(free.downloadToken())).isEmpty();
        try (var payments = Files.list(tempDirectory.resolve("paid-unlocks/payments"))) {
            assertThat(payments.count()).isZero();
        }
        assertThat(Files.readString(tempDirectory.resolve("paid-unlocks/events/paid-unlocks.jsonl")))
                .contains("free_result_released").doesNotContain("payment_completed", free.downloadToken());
        assertThatThrownBy(() -> store.fulfill(free.offer().id(), "CAPTURE-FREE", "payer@example.com", "29.00", "USD"))
                .hasMessageContaining("cannot be marked as paid");
    }

    @Test
    void freeResultsRejectStaleFutureMismatchedAndFailedApprovals() {
        var now = Instant.parse("2026-09-19T12:00:00Z");
        PaidUnlockStore store = store(Clock.fixed(now, ZoneOffset.UTC));
        byte[] bytes = "fixture".getBytes(StandardCharsets.UTF_8);
        for (Instant time : List.of(now.minus(24, java.time.temporal.ChronoUnit.HOURS), now.plusSeconds(1))) {
            var gate = new PaidUnlockStore.ReleaseApproval("PASS", "test-case", "customer@example.com", "Free result",
                    sha256(bytes), true, true, time, "reviewer");
            assertThatThrownBy(() -> store.createFreeDelivery(input(), "fixture.pdf", bytes, gate))
                    .hasMessageContaining("last 24 hours");
        }
        var wrongRecipient = new PaidUnlockStore.ReleaseApproval("PASS", "test-case", "other@example.com", "Free result",
                sha256(bytes), true, true, now, "reviewer");
        assertThatThrownBy(() -> store.createFreeDelivery(input(), "fixture.pdf", bytes, wrongRecipient))
                .hasMessageContaining("recipient");
        var failed = new PaidUnlockStore.ReleaseApproval("FAIL", "test-case", "customer@example.com", "Free result",
                sha256(bytes), true, false, now, "reviewer");
        assertThatThrownBy(() -> store.createFreeDelivery(input(), "fixture.pdf", bytes, failed))
                .hasMessageContaining("passed page and identity");
        assertThatThrownBy(() -> store.createFreeDelivery(input(), "fixture.pdf", bytes, approval("bad-hash")))
                .hasMessageContaining("hash");
    }

    @Test
    void freeResultTamperingAndExpirationRemainBlocked() throws Exception {
        MutableClock clock = new MutableClock(Instant.parse("2026-09-19T12:00:00Z"));
        PaidUnlockStore store = store(clock);
        byte[] bytes = "fixture".getBytes(StandardCharsets.UTF_8);
        var free = store.createFreeDelivery(input(), "free.pdf", bytes, approval(sha256(bytes)));
        Files.writeString(tempDirectory.resolve("paid-unlocks/packages/" + free.offer().packageStorageName()), "changed");
        assertThat(store.inspectDownload(free.downloadToken())).isEmpty();
        assertThat(store.authorizeDownload(free.downloadToken())).isEmpty();
        var expiring = store.createFreeDelivery(input(), "expires.pdf", bytes, approval(sha256(bytes)));
        clock.set(Instant.parse("2026-09-19T14:00:01Z"));
        assertThat(store.inspectDownload(expiring.downloadToken())).isEmpty();
        assertThat(store.authorizeDownload(expiring.downloadToken())).isEmpty();
    }

    private PaidUnlockStore.OfferInput input() {
        return new PaidUnlockStore.OfferInput(
                "customer@example.com",
                "REQ-1444-WILLOW",
                "1444 Willow Ridge Rd",
                "Lincoln County official environmental health file",
                "28-page county record package",
                "Where is the recorded septic system located?",
                "The record does not verify current field conditions."
        );
    }

    private PaidUnlockStore.ReleaseApproval approval(String hash) {
        return new PaidUnlockStore.ReleaseApproval(
                "PASS", "case-nc-lincoln-001", "customer@example.com",
                "Your reviewed record package", hash, true, true,
                Instant.parse("2026-09-19T11:55:00Z"), "operator"
        );
    }

    private String sha256(byte[] bytes) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static final class MutableClock extends Clock {
        private Instant instant;

        private MutableClock(Instant instant) {
            this.instant = instant;
        }

        void set(Instant next) {
            this.instant = next;
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}
