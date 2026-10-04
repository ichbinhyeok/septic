package com.example.septic.service;

import com.example.septic.config.AppStorageProperties;
import com.example.septic.config.PaidUnlockProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaidUnlockStore {
    public static final String OFFER_VERSION = "record-help-evidence-preview-29-v2";
    public static final String CURRENCY = "USD";
    public static final String AMOUNT = "29.00";
    public static final String FREE_VERSION = "record-help-free-results-v1";
    public static final String OFFICIAL_RECORD_PREVIEW = "official-record";
    public static final String REVIEWED_BRIEF_PREVIEW = "reviewed-brief";
    private static final Set<String> PREVIEW_SLOTS = Set.of(OFFICIAL_RECORD_PREVIEW, REVIEWED_BRIEF_PREVIEW);
    private static final int MAX_PREVIEW_BYTES = 3 * 1024 * 1024;

    private final Path root;
    private final PaidUnlockProperties properties;
    private final ObjectMapper objectMapper;
    private final SecureRandom secureRandom;
    private final Clock clock;

    @Autowired
    public PaidUnlockStore(AppStorageProperties storageProperties, PaidUnlockProperties properties) {
        this(storageProperties, properties, Clock.systemUTC(), new SecureRandom());
    }

    PaidUnlockStore(
            AppStorageProperties storageProperties,
            PaidUnlockProperties properties,
            Clock clock,
            SecureRandom secureRandom
    ) {
        this.root = Path.of(storageProperties.root()).toAbsolutePath().normalize().resolve("paid-unlocks");
        this.properties = properties;
        this.objectMapper = JsonMapper.builder().findAndAddModules().build();
        this.clock = clock;
        this.secureRandom = secureRandom;
    }

    @PostConstruct
    void initialize() {
        try {
            Files.createDirectories(offersDirectory());
            Files.createDirectories(packagesDirectory());
            Files.createDirectories(paymentsDirectory());
            Files.createDirectories(grantsDirectory());
            Files.createDirectories(eventsDirectory());
            Files.createDirectories(previewsDirectory());
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to initialize paid-unlock storage", exception);
        }
    }

    public synchronized PreparedOffer createOffer(
            OfferInput input,
            String packageFileName,
            byte[] packageBytes,
            ReleaseApproval approval
    ) {
        return prepareOffer(input, packageFileName, packageBytes, approval, false);
    }

    /** Creates a new, separately approved free delivery. Never rewrites historical payments. */
    public synchronized Fulfillment createFreeDelivery(
            OfferInput input, String packageFileName, byte[] packageBytes, ReleaseApproval approval
    ) {
        Instant now = Instant.now(clock);
        if (approval == null || approval.approvedAt() == null
                || approval.approvedAt().isAfter(now)
                || !approval.approvedAt().isAfter(now.minus(24, ChronoUnit.HOURS))) {
            throw new IllegalArgumentException("A release approval from the last 24 hours is required");
        }
        safeText(approval.caseId(), 160);
        safeText(approval.subject(), 500);
        safeText(approval.reviewer(), 160);
        PreparedOffer prepared = prepareOffer(input, packageFileName, packageBytes, approval, true);
        Offer offer = prepared.offer();
        if (!packageIsStillApproved(offer)) {
            throw new IllegalStateException("The approved package no longer matches its release manifest");
        }
        String token = randomToken();
        Instant expiresAt = now.plus(properties.downloadTtlHours(), ChronoUnit.HOURS);
        Grant grant = new Grant(sha256(token.getBytes(StandardCharsets.UTF_8)), offer.id(), null,
                expiresAt, 0, properties.maxDownloads());
        try {
            writeJsonAtomic(grantPath(grant.tokenHash()), grant);
            Offer released = offer.withStatus("RELEASED_FREE");
            writeJsonAtomic(offerPath(offer.id()), released);
            appendEvent("free_result_released", offer.id(), null, "RELEASED_FREE");
            return new Fulfillment(released, token, expiresAt, true);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to persist free result delivery", exception);
        }
    }

    private PreparedOffer prepareOffer(
            OfferInput input, String packageFileName, byte[] packageBytes, ReleaseApproval approval, boolean free
    ) {
        validateInput(input, packageFileName, packageBytes, approval);
        String packageSha256 = sha256(packageBytes);
        if (!constantTimeEquals(packageSha256, approval.packageSha256())) {
            throw new IllegalArgumentException("Release approval package hash does not match the uploaded package");
        }

        String offerId = UUID.randomUUID().toString();
        String publicToken = randomToken();
        String storedName = offerId + safeExtension(packageFileName);
        Path packagePath = packagesDirectory().resolve(storedName).normalize();
        requireInside(packagePath, packagesDirectory());
        try {
            Files.write(packagePath, packageBytes, StandardOpenOption.CREATE_NEW);
            Offer offer = new Offer(
                    offerId,
                    free ? FREE_VERSION : OFFER_VERSION,
                    sha256(publicToken.getBytes(StandardCharsets.UTF_8)),
                    normalizeEmail(input.customerEmail()),
                    safeText(input.requestReference(), 160),
                    safeText(input.propertyLabel(), 240),
                    safeText(input.sourceSummary(), 800),
                    safeText(input.documentScope(), 800),
                    safeText(input.answerableQuestion(), 500),
                    safeText(input.limitations(), 800),
                    safeFileName(packageFileName),
                    storedName,
                    packageSha256,
                    free ? "0.00" : AMOUNT,
                    CURRENCY,
                    free ? "FREE_PENDING" : "READY",
                    Instant.now(clock),
                    approval
            );
            writeJsonAtomic(offerPath(offerId), offer);
            appendEvent(free ? "free_result_prepared" : "offer_created", offerId, null, offer.status());
            return new PreparedOffer(offer, publicToken);
        } catch (IOException exception) {
            try {
                Files.deleteIfExists(packagePath);
            } catch (IOException ignored) {
                // Preserve the original failure.
            }
            throw new IllegalStateException("Failed to create the paid-unlock offer", exception);
        }
    }

    public Optional<Offer> findReadyOfferByPublicToken(String publicToken) {
        if (publicToken == null || publicToken.length() < 32 || publicToken.length() > 180) {
            return Optional.empty();
        }
        String tokenHash = sha256(publicToken.getBytes(StandardCharsets.UTF_8));
        return offers().stream().filter(offer -> constantTimeEquals(offer.publicTokenHash(), tokenHash))
                .filter(offer -> "READY".equals(offer.status()) || "PAID".equals(offer.status()))
                .findFirst();
    }

    public Optional<Offer> findOfferById(String offerId) {
        if (!safeId(offerId)) {
            return Optional.empty();
        }
        Path path = offerPath(offerId);
        if (!Files.isRegularFile(path)) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(path.toFile(), Offer.class));
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read paid-unlock offer " + offerId, exception);
        }
    }

    public synchronized PreviewAsset saveRedactedPreview(
            String offerId,
            String slot,
            byte[] bytes,
            boolean redactionAttested
    ) {
        Offer offer = findOfferById(offerId).orElseThrow(() -> new IllegalArgumentException("Unknown offer"));
        String safeSlot = safePreviewSlot(slot);
        if (!redactionAttested) {
            throw new IllegalArgumentException("Preview redaction review must be attested");
        }
        if (!"READY".equals(offer.status())) {
            throw new IllegalArgumentException("Previews can only be added to a ready offer");
        }
        if (bytes == null || bytes.length == 0 || bytes.length > MAX_PREVIEW_BYTES) {
            throw new IllegalArgumentException("Preview image must be a non-empty PNG under 3 MB");
        }
        if (!isPng(bytes)) {
            throw new IllegalArgumentException("Preview image must be a flattened PNG");
        }
        Path directory = previewDirectory(offer.id());
        Path target = directory.resolve(safeSlot + ".png").normalize();
        requireInside(target, directory);
        try {
            Files.createDirectories(directory);
            Path temp = Files.createTempFile(directory, safeSlot, ".tmp");
            Files.write(temp, bytes, StandardOpenOption.TRUNCATE_EXISTING);
            try {
                Files.move(temp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
            }
            appendEvent("redacted_preview_uploaded", offer.id(), null, safeSlot);
            return new PreviewAsset(target, "image/png", sha256(bytes));
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to store redacted preview", exception);
        }
    }

    public List<String> previewSlots(String offerId) {
        if (!safeId(offerId)) {
            return List.of();
        }
        return PREVIEW_SLOTS.stream()
                .filter(slot -> Files.isRegularFile(previewPath(offerId, slot)))
                .sorted()
                .toList();
    }

    public Optional<PreviewAsset> findPreview(String offerId, String slot) {
        if (!safeId(offerId) || !PREVIEW_SLOTS.contains(slot)) {
            return Optional.empty();
        }
        Path path = previewPath(offerId, slot);
        if (!Files.isRegularFile(path)) {
            return Optional.empty();
        }
        try {
            return Optional.of(new PreviewAsset(path, "image/png", sha256(Files.readAllBytes(path))));
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read redacted preview", exception);
        }
    }

    public synchronized Fulfillment fulfill(
            String offerId,
            String captureId,
            String payerEmail,
            String paidAmount,
            String paidCurrency
    ) {
        Offer offer = findOfferById(offerId).orElseThrow(() -> new IllegalArgumentException("Unknown offer"));
        if (FREE_VERSION.equals(offer.offerVersion())) {
            throw new IllegalArgumentException("A free result cannot be marked as paid");
        }
        if (!AMOUNT.equals(paidAmount) || !CURRENCY.equals(paidCurrency)) {
            throw new IllegalArgumentException("Payment amount or currency does not match the offer");
        }
        if (!packageIsStillApproved(offer)) {
            try {
                appendEvent("fulfillment_blocked", offer.id(), captureId, "PACKAGE_GATE_FAILED");
            } catch (IOException exception) {
                throw new IllegalStateException("The package gate failed and its event could not be recorded", exception);
            }
            throw new IllegalStateException("The approved package no longer matches its release manifest");
        }

        Path paymentPath = paymentPath(captureId);
        if (Files.isRegularFile(paymentPath)) {
            try {
                Payment payment = objectMapper.readValue(paymentPath.toFile(), Payment.class);
                if (!payment.offerId().equals(offerId)) {
                    throw new IllegalStateException("Capture ID is already linked to another offer");
                }
                return new Fulfillment(offer, payment.downloadToken(), payment.expiresAt(), false);
            } catch (IOException exception) {
                throw new IllegalStateException("Failed to read existing payment", exception);
            }
        }

        Instant paidAt = Instant.now(clock);
        Instant expiresAt = paidAt.plus(properties.downloadTtlHours(), ChronoUnit.HOURS);
        String downloadToken = randomToken();
        Payment payment = new Payment(
                captureId,
                offerId,
                normalizeEmail(payerEmail),
                paidAmount,
                paidCurrency,
                paidAt,
                downloadToken,
                expiresAt
        );
        Grant grant = new Grant(
                sha256(downloadToken.getBytes(StandardCharsets.UTF_8)),
                offerId,
                captureId,
                expiresAt,
                0,
                properties.maxDownloads()
        );
        try {
            writeJsonAtomic(paymentPath, payment);
            writeJsonAtomic(grantPath(grant.tokenHash()), grant);
            Offer paidOffer = offer.withStatus("PAID");
            writeJsonAtomic(offerPath(offer.id()), paidOffer);
            appendEvent("payment_completed", offer.id(), captureId, "PAID");
            return new Fulfillment(paidOffer, downloadToken, expiresAt, true);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to persist paid-unlock fulfillment", exception);
        }
    }

    public synchronized Optional<DownloadAuthorization> authorizeDownload(String downloadToken) {
        if (downloadToken == null || downloadToken.length() < 32 || downloadToken.length() > 180) {
            return Optional.empty();
        }
        String tokenHash = sha256(downloadToken.getBytes(StandardCharsets.UTF_8));
        Path grantPath = grantPath(tokenHash);
        if (!Files.isRegularFile(grantPath)) {
            return Optional.empty();
        }
        try {
            Grant grant = objectMapper.readValue(grantPath.toFile(), Grant.class);
            if (!constantTimeEquals(grant.tokenHash(), tokenHash)
                    || Instant.now(clock).isAfter(grant.expiresAt())
                    || grant.downloadCount() >= grant.maxDownloads()) {
                return Optional.empty();
            }
            Offer offer = findOfferById(grant.offerId()).orElseThrow();
            if (!deliveryReleased(offer) || !packageIsStillApproved(offer)) {
                return Optional.empty();
            }
            Grant consumed = grant.withDownloadCount(grant.downloadCount() + 1);
            writeJsonAtomic(grantPath, consumed);
            appendEvent("package_downloaded", offer.id(), grant.captureId(), "DELIVERED");
            return Optional.of(new DownloadAuthorization(
                    packagePath(offer),
                    offer.packageFileName(),
                    offer.packageSha256(),
                    consumed.downloadCount(),
                    consumed.maxDownloads()
            ));
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to authorize paid-unlock download", exception);
        }
    }

    public synchronized Optional<DeliveryPreview> inspectDownload(String downloadToken) {
        if (downloadToken == null || downloadToken.length() < 32 || downloadToken.length() > 180) {
            return Optional.empty();
        }
        String tokenHash = sha256(downloadToken.getBytes(StandardCharsets.UTF_8));
        Path grantPath = grantPath(tokenHash);
        if (!Files.isRegularFile(grantPath)) {
            return Optional.empty();
        }
        try {
            Grant grant = objectMapper.readValue(grantPath.toFile(), Grant.class);
            if (!constantTimeEquals(grant.tokenHash(), tokenHash)
                    || Instant.now(clock).isAfter(grant.expiresAt())
                    || grant.downloadCount() >= grant.maxDownloads()) {
                return Optional.empty();
            }
            Offer offer = findOfferById(grant.offerId()).orElseThrow();
            if (!deliveryReleased(offer) || !packageIsStillApproved(offer)) {
                return Optional.empty();
            }
            return Optional.of(new DeliveryPreview(
                    offer,
                    offer.packageFileName(),
                    offer.packageSha256(),
                    grant.expiresAt(),
                    grant.maxDownloads() - grant.downloadCount(),
                    grant.maxDownloads()
            ));
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to inspect paid-unlock delivery", exception);
        }
    }

    private boolean deliveryReleased(Offer offer) {
        return "PAID".equals(offer.status()) || "RELEASED_FREE".equals(offer.status());
    }

    private boolean packageIsStillApproved(Offer offer) {
        Path path = packagePath(offer);
        if (!Files.isRegularFile(path)
                || !offer.releaseApproval().pageReviewComplete()
                || !offer.releaseApproval().identityMatch()
                || !"PASS".equals(offer.releaseApproval().result())
                || !normalizeEmail(offer.customerEmail()).equals(normalizeEmail(offer.releaseApproval().recipient()))) {
            return false;
        }
        try {
            String currentHash = sha256(Files.readAllBytes(path));
            return constantTimeEquals(currentHash, offer.packageSha256())
                    && constantTimeEquals(currentHash, offer.releaseApproval().packageSha256());
        } catch (IOException exception) {
            return false;
        }
    }

    private List<Offer> offers() {
        try (Stream<Path> paths = Files.list(offersDirectory())) {
            return paths
                    .filter(path -> path.getFileName().toString().endsWith(".json"))
                    .sorted(Comparator.comparing(Path::toString))
                    .map(path -> {
                        try {
                            return objectMapper.readValue(path.toFile(), Offer.class);
                        } catch (IOException exception) {
                            throw new IllegalStateException("Failed to read offer " + path, exception);
                        }
                    })
                    .toList();
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to list paid-unlock offers", exception);
        }
    }

    private void validateInput(OfferInput input, String packageFileName, byte[] bytes, ReleaseApproval approval) {
        if (input == null || approval == null || bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("Offer, release approval and package are required");
        }
        if (bytes.length > 15 * 1024 * 1024) {
            throw new IllegalArgumentException("Paid package exceeds the 15 MB limit");
        }
        if (!normalizeEmail(input.customerEmail()).matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("A valid customer email is required");
        }
        if (!"PASS".equals(approval.result()) || !approval.pageReviewComplete() || !approval.identityMatch()) {
            throw new IllegalArgumentException("A passed page and identity review is required");
        }
        if (!normalizeEmail(input.customerEmail()).equals(normalizeEmail(approval.recipient()))) {
            throw new IllegalArgumentException("Release approval recipient does not match the offer customer");
        }
        safeFileName(packageFileName);
        safeText(input.requestReference(), 160);
        safeText(input.propertyLabel(), 240);
    }

    private void appendEvent(String type, String offerId, String captureId, String state) throws IOException {
        String line = objectMapper.writeValueAsString(new UnlockEvent(
                type, Instant.now(clock), offerId, captureId, state
        )) + System.lineSeparator();
        Files.writeString(
                eventsDirectory().resolve("paid-unlocks.jsonl"),
                line,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }

    private void writeJsonAtomic(Path target, Object value) throws IOException {
        Files.createDirectories(target.getParent());
        Path temp = Files.createTempFile(target.getParent(), target.getFileName().toString(), ".tmp");
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(temp.toFile(), value);
        try {
            Files.move(temp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private Path packagePath(Offer offer) {
        Path path = packagesDirectory().resolve(offer.packageStorageName()).normalize();
        requireInside(path, packagesDirectory());
        return path;
    }

    private Path offersDirectory() { return root.resolve("offers"); }
    private Path packagesDirectory() { return root.resolve("packages"); }
    private Path paymentsDirectory() { return root.resolve("payments"); }
    private Path grantsDirectory() { return root.resolve("grants"); }
    private Path eventsDirectory() { return root.resolve("events"); }
    private Path previewsDirectory() { return root.resolve("previews"); }
    private Path previewDirectory(String offerId) {
        Path directory = previewsDirectory().resolve(safeStorageId(offerId)).normalize();
        requireInside(directory, previewsDirectory());
        return directory;
    }
    private Path previewPath(String offerId, String slot) {
        Path directory = previewDirectory(offerId);
        Path path = directory.resolve(safePreviewSlot(slot) + ".png").normalize();
        requireInside(path, directory);
        return path;
    }
    private Path offerPath(String id) { return offersDirectory().resolve(id + ".json"); }
    private Path paymentPath(String id) { return paymentsDirectory().resolve(safeStorageId(id) + ".json"); }
    private Path grantPath(String hash) { return grantsDirectory().resolve(hash + ".json"); }

    private String randomToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String sha256(byte[] bytes) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    private boolean constantTimeEquals(String left, String right) {
        if (left == null || right == null) {
            return false;
        }
        return MessageDigest.isEqual(
                left.getBytes(StandardCharsets.US_ASCII),
                right.getBytes(StandardCharsets.US_ASCII)
        );
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.replace('\r', ' ').replace('\n', ' ').trim().toLowerCase();
    }

    private String safeText(String value, int maxLength) {
        String normalized = value == null ? "" : value.replace('\r', ' ').replace('\n', ' ').trim();
        if (normalized.isBlank() || normalized.length() > maxLength) {
            throw new IllegalArgumentException("Required offer text is missing or too long");
        }
        return normalized;
    }

    private String safeFileName(String name) {
        String normalized = name == null ? "" : name.replace('\\', '/');
        normalized = normalized.substring(normalized.lastIndexOf('/') + 1).trim();
        if (normalized.isBlank() || normalized.length() > 180 || normalized.contains("..")) {
            throw new IllegalArgumentException("Invalid package filename");
        }
        return normalized;
    }

    private String safeExtension(String name) {
        String safeName = safeFileName(name);
        int dot = safeName.lastIndexOf('.');
        if (dot < 0) {
            return ".bin";
        }
        String extension = safeName.substring(dot).toLowerCase();
        return extension.matches("\\.(pdf|zip)") ? extension : ".bin";
    }

    private String safeStorageId(String value) {
        if (!safeId(value)) {
            throw new IllegalArgumentException("Invalid external identifier");
        }
        return value;
    }

    private String safePreviewSlot(String value) {
        if (!PREVIEW_SLOTS.contains(value)) {
            throw new IllegalArgumentException("Unknown preview slot");
        }
        return value;
    }

    private boolean isPng(byte[] bytes) {
        byte[] signature = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        if (bytes.length < signature.length) {
            return false;
        }
        for (int index = 0; index < signature.length; index++) {
            if (bytes[index] != signature[index]) {
                return false;
            }
        }
        return true;
    }

    private boolean safeId(String value) {
        return value != null && value.matches("[A-Za-z0-9._-]{3,180}");
    }

    private void requireInside(Path path, Path directory) {
        if (!path.startsWith(directory.toAbsolutePath().normalize())) {
            throw new IllegalArgumentException("Path escapes paid-unlock storage");
        }
    }

    public record OfferInput(
            String customerEmail,
            String requestReference,
            String propertyLabel,
            String sourceSummary,
            String documentScope,
            String answerableQuestion,
            String limitations
    ) {}

    public record ReleaseApproval(
            String result,
            String caseId,
            String recipient,
            String subject,
            String packageSha256,
            boolean pageReviewComplete,
            boolean identityMatch,
            Instant approvedAt,
            String reviewer
    ) {}

    public record Offer(
            String id,
            String offerVersion,
            String publicTokenHash,
            String customerEmail,
            String requestReference,
            String propertyLabel,
            String sourceSummary,
            String documentScope,
            String answerableQuestion,
            String limitations,
            String packageFileName,
            String packageStorageName,
            String packageSha256,
            String amount,
            String currency,
            String status,
            Instant createdAt,
            ReleaseApproval releaseApproval
    ) {
        public Offer withStatus(String nextStatus) {
            return new Offer(id, offerVersion, publicTokenHash, customerEmail, requestReference, propertyLabel,
                    sourceSummary, documentScope, answerableQuestion, limitations, packageFileName,
                    packageStorageName, packageSha256, amount, currency, nextStatus, createdAt, releaseApproval);
        }
    }

    public record PreparedOffer(Offer offer, String publicToken) {}
    public record PreviewAsset(Path path, String contentType, String sha256) {}
    public record Fulfillment(Offer offer, String downloadToken, Instant expiresAt, boolean newlyCreated) {}
    public record DownloadAuthorization(
            Path path,
            String fileName,
            String sha256,
            int downloadCount,
            int maxDownloads
    ) {}
    public record DeliveryPreview(
            Offer offer,
            String fileName,
            String sha256,
            Instant expiresAt,
            int downloadsRemaining,
            int maxDownloads
    ) {}
    private record Payment(
            String captureId,
            String offerId,
            String payerEmail,
            String amount,
            String currency,
            Instant paidAt,
            String downloadToken,
            Instant expiresAt
    ) {}
    private record Grant(
            String tokenHash,
            String offerId,
            String captureId,
            Instant expiresAt,
            int downloadCount,
            int maxDownloads
    ) {
        Grant withDownloadCount(int next) {
            return new Grant(tokenHash, offerId, captureId, expiresAt, next, maxDownloads);
        }
    }
    private record UnlockEvent(String eventType, Instant occurredAt, String offerId, String captureId, String state) {}
}
