package com.example.septic.web;

import com.example.septic.config.AppSiteProperties;
import com.example.septic.config.PaidUnlockProperties;
import com.example.septic.service.PaidUnlockNotificationService;
import com.example.septic.service.PaidUnlockStore;
import com.example.septic.service.PayPalCheckoutClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PaidUnlockController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaidUnlockController.class);
    private final PaidUnlockProperties properties;
    private final AppSiteProperties siteProperties;
    private final PaidUnlockStore store;
    private final PayPalCheckoutClient payPalClient;
    private final PaidUnlockNotificationService notificationService;
    private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

    public PaidUnlockController(
            PaidUnlockProperties properties,
            AppSiteProperties siteProperties,
            PaidUnlockStore store,
            PayPalCheckoutClient payPalClient,
            PaidUnlockNotificationService notificationService
    ) {
        this.properties = properties;
        this.siteProperties = siteProperties;
        this.store = store;
        this.payPalClient = payPalClient;
        this.notificationService = notificationService;
    }

    @GetMapping({"/unlock/{publicToken}", "/unlock/{publicToken}/"})
    public String offer(@PathVariable String publicToken, Model model) {
        PaidUnlockStore.Offer offer = store.findReadyOfferByPublicToken(publicToken)
                .orElseThrow(() -> new PaidUnlockNotFoundException("This private offer is unavailable or has expired."));
        model.addAttribute("page", privatePage("Your reviewed property record is ready"));
        model.addAttribute("offer", offer);
        model.addAttribute("publicToken", publicToken);
        model.addAttribute("paymentConfigured", properties.isConfigured());
        model.addAttribute("paypalSdkUrl", properties.isConfigured() ? properties.javascriptSdkUrl() : "");
        return "pages/paid-unlock";
    }

    @PostMapping(value = "/api/paid-unlocks/{publicToken}/paypal/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> createOrder(@PathVariable String publicToken) {
        if (!properties.isConfigured()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Payment is not available yet."));
        }
        PaidUnlockStore.Offer offer = store.findReadyOfferByPublicToken(publicToken)
                .orElseThrow(() -> new PaidUnlockNotFoundException("Unknown offer"));
        if ("PAID".equals(offer.status())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "This package is already paid."));
        }
        JsonNode order = payPalClient.createOrder(offer);
        return ResponseEntity.ok(Map.of("id", order.path("id").asText()));
    }

    @PostMapping(value = "/api/paid-unlocks/{publicToken}/paypal/orders/{orderId}/capture", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> captureOrder(@PathVariable String publicToken, @PathVariable String orderId) {
        PaidUnlockStore.Offer offer = store.findReadyOfferByPublicToken(publicToken)
                .orElseThrow(() -> new PaidUnlockNotFoundException("Unknown offer"));
        PayPalCheckoutClient.CapturedPayment payment = payPalClient.captureOrder(orderId, offer.id());
        PaidUnlockStore.Fulfillment fulfillment = store.fulfill(
                offer.id(),
                payment.captureId(),
                payment.payerEmail(),
                payment.amount(),
                payment.currency()
        );
        boolean emailSent = deliverOrAlert(fulfillment);
        return ResponseEntity.ok(Map.of(
                "status", "COMPLETED",
                "downloadUrl", "/paid-unlock/download/" + fulfillment.downloadToken(),
                "deliveryUrl", "/paid-unlock/delivery/" + fulfillment.downloadToken(),
                "expiresAt", fulfillment.expiresAt().toString(),
                "emailSent", emailSent
        ));
    }

    @PostMapping(value = "/api/paypal/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> webhook(
            @RequestBody String rawBody,
            @RequestHeader(name = "PAYPAL-TRANSMISSION-ID", required = false) String transmissionId,
            @RequestHeader(name = "PAYPAL-TRANSMISSION-TIME", required = false) String transmissionTime,
            @RequestHeader(name = "PAYPAL-CERT-URL", required = false) String certUrl,
            @RequestHeader(name = "PAYPAL-AUTH-ALGO", required = false) String authAlgo,
            @RequestHeader(name = "PAYPAL-TRANSMISSION-SIG", required = false) String transmissionSignature
    ) {
        if (!properties.isConfigured()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        try {
            JsonNode event = objectMapper.readTree(rawBody);
            if (!payPalClient.verifyWebhook(
                    transmissionId, transmissionTime, certUrl, authAlgo, transmissionSignature, event
            )) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            if (!"PAYMENT.CAPTURE.COMPLETED".equals(event.path("event_type").asText())) {
                return ResponseEntity.ok().build();
            }
            JsonNode resource = event.path("resource");
            String offerId = resource.path("custom_id").asText();
            String orderId = resource.path("supplementary_data").path("related_ids").path("order_id").asText();
            PaidUnlockStore.Offer offer = store.findOfferById(offerId)
                    .orElseThrow(() -> new IllegalArgumentException("Webhook references an unknown offer"));
            PayPalCheckoutClient.CapturedPayment payment = payPalClient.getCompletedOrder(orderId, offer.id());
            PaidUnlockStore.Fulfillment fulfillment = store.fulfill(
                    offer.id(), payment.captureId(), payment.payerEmail(), payment.amount(), payment.currency()
            );
            deliverOrAlert(fulfillment);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            LOGGER.error("PayPal webhook processing failed", exception);
            notificationService.notifyOperatorFailure("webhook", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping({"/paid-unlock/download/{downloadToken}", "/paid-unlock/download/{downloadToken}/"})
    public ResponseEntity<?> download(@PathVariable String downloadToken) {
        PaidUnlockStore.DownloadAuthorization authorization = store.authorizeDownload(downloadToken)
                .orElseThrow(() -> new PaidUnlockNotFoundException("This download link is invalid or expired."));
        MediaType mediaType = authorization.fileName().toLowerCase().endsWith(".pdf")
                ? MediaType.APPLICATION_PDF
                : MediaType.parseMediaType("application/zip");
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(authorization.fileName()))
                .header("X-Content-SHA256", authorization.sha256())
                .contentLength(fileSize(authorization.path()))
                .contentType(mediaType)
                .body(new FileSystemResource(authorization.path()));
    }

    @GetMapping({"/paid-unlock/delivery/{downloadToken}", "/paid-unlock/delivery/{downloadToken}/"})
    public String delivery(
            @PathVariable String downloadToken,
            jakarta.servlet.http.HttpServletResponse response,
            Model model
    ) {
        PaidUnlockStore.DeliveryPreview delivery = store.inspectDownload(downloadToken)
                .orElseThrow(() -> new PaidUnlockNotFoundException("This private delivery link is invalid or expired."));
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store, max-age=0");
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");
        model.addAttribute("page", privatePage("Your record package is ready"));
        model.addAttribute("delivery", delivery);
        model.addAttribute("downloadToken", downloadToken);
        model.addAttribute("deliveryExpiresAt", DateTimeFormatter.ofPattern("MMM d, yyyy · h:mm a 'UTC'")
                .withZone(ZoneOffset.UTC).format(delivery.expiresAt()));
        return "pages/paid-delivery";
    }

    @PostMapping(value = "/ops/paid-unlocks", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> createOffer(
            @RequestParam String customerEmail,
            @RequestParam String requestReference,
            @RequestParam String propertyLabel,
            @RequestParam String sourceSummary,
            @RequestParam String documentScope,
            @RequestParam String answerableQuestion,
            @RequestParam String limitations,
            @RequestParam String releaseApprovalJson,
            @RequestParam("package") MultipartFile packageFile
    ) throws IOException {
        PaidUnlockStore.ReleaseApproval approval = objectMapper.readValue(
                releaseApprovalJson,
                PaidUnlockStore.ReleaseApproval.class
        );
        PaidUnlockStore.PreparedOffer prepared = store.createOffer(
                new PaidUnlockStore.OfferInput(
                        customerEmail,
                        requestReference,
                        propertyLabel,
                        sourceSummary,
                        documentScope,
                        answerableQuestion,
                        limitations
                ),
                packageFile.getOriginalFilename(),
                packageFile.getBytes(),
                approval
        );
        String previewUrl = siteProperties.baseUri().resolve("/unlock/" + prepared.publicToken()).toString();
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "offerId", prepared.offer().id(),
                "previewUrl", previewUrl,
                "status", prepared.offer().status()
        ));
    }

    private boolean deliverOrAlert(PaidUnlockStore.Fulfillment fulfillment) {
        if (!fulfillment.newlyCreated()) {
            return false;
        }
        boolean sent = notificationService.sendPackageReady(fulfillment);
        if (!sent) {
            notificationService.notifyOperatorFailure(
                    fulfillment.offer().id(),
                    "Payment was confirmed, but the customer delivery email failed. The private download remains available."
            );
        }
        return sent;
    }

    private PageMeta privatePage(String title) {
        return new PageMeta(
                title + " | SepticPath",
                "Private reviewed property-record package checkout.",
                siteProperties.baseUri().resolve("/unlock/").toString(),
                "noindex,nofollow,noarchive",
                List.of()
        );
    }

    private long fileSize(java.nio.file.Path path) {
        try {
            return Files.size(path);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read paid package size", exception);
        }
    }

    private String contentDisposition(String fileName) {
        String safe = fileName.replace("\"", "").replace("\r", "").replace("\n", "");
        return "attachment; filename=\"" + safe + "\"";
    }

    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NOT_FOUND)
    static class PaidUnlockNotFoundException extends RuntimeException {
        PaidUnlockNotFoundException(String message) {
            super(message);
        }
    }
}
