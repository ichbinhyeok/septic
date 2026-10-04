package com.example.septic.service;

import com.example.septic.config.ClosingRiskNotificationProperties;
import com.example.septic.web.ClosingRiskCheckForm;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ClosingRiskNotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClosingRiskNotificationService.class);
    private final JavaMailSender mailSender;
    private final ClosingRiskNotificationProperties properties;
    private final RecordHelpDocumentPolicy recordHelpDocumentPolicy;

    public ClosingRiskNotificationService(
            JavaMailSender mailSender,
            ClosingRiskNotificationProperties properties
    ) {
        this.mailSender = mailSender;
        this.properties = properties;
        this.recordHelpDocumentPolicy = new RecordHelpDocumentPolicy();
    }

    public boolean notifyOperator(String requestId, ClosingRiskCheckForm form) {
        if (!properties.isConfigured()) {
            LOGGER.warn("Record help request {} was stored, but Gmail notification is not configured", requestId);
            return false;
        }

        List<MultipartFile> documents = recordHelpDocumentPolicy.present(form.getDocuments());
        if (!documents.isEmpty()) {
            return notifyOperatorWithDocuments(requestId, form, documents);
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(properties.sender());
        message.setTo(properties.recipient());
        message.setReplyTo(safeLine(form.getEmail()));
        message.setSubject(subject(form));
        message.setText(messageText(requestId, form, 0));
        try {
            mailSender.send(message);
            return true;
        } catch (MailException exception) {
            LOGGER.error("Failed to send Gmail notification for record help request {}", requestId, exception);
            return false;
        }
    }

    public boolean notifyCustomerReceipt(String requestId, ClosingRiskCheckForm form) {
        if (!properties.isConfigured()) {
            LOGGER.warn("Customer receipt for record-help request {} was not sent because Gmail is not configured", requestId);
            return false;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(properties.sender());
        message.setTo(safeLine(form.getEmail()));
        message.setReplyTo(properties.recipient());
        message.setSubject("SepticPath received your record request — " + requestId);
        message.setText("""
                We received your SepticPath record-help request.

                Reference: %s
                Property: %s
                Purpose: %s
                Additional details: %s

                We aim to send an initial update within 1–2 business days. Agency response times can take longer.

                Research, agency requests, and the records and explanation we provide are free. No paid unlock or card is required. Any agency fee requires your approval first. Your own uploaded files remain yours.

                If field work may help, we may share your request with relevant local professionals. Their work is priced separately, and availability varies. For an active sewage backup, contact a local septic service directly rather than waiting for records.

                Keep this email and reference number if you need to follow up. Do not email payment-card details or access codes.

                SepticPath is an independent records-research service, not an inspection, permitting, engineering, or legal authority.
                """.formatted(
                requestId,
                safeLine(form.getPropertyAddress()),
                safeLine(form.getHelpPurposeLabel()),
                safeMultiline(form.getConcern())
        ));
        try {
            mailSender.send(message);
            return true;
        } catch (MailException exception) {
            LOGGER.error("Failed to send customer receipt for record-help request {}", requestId, exception);
            return false;
        }
    }

    private boolean notifyOperatorWithDocuments(
            String requestId,
            ClosingRiskCheckForm form,
            List<MultipartFile> documents
    ) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(properties.sender());
            helper.setTo(properties.recipient());
            helper.setReplyTo(safeLine(form.getEmail()));
            helper.setSubject(subject(form));
            helper.setText(messageText(requestId, form, documents.size()), false);
            for (int index = 0; index < documents.size(); index++) {
                MultipartFile document = documents.get(index);
                String displayName = recordHelpDocumentPolicy.displayFileName(document, index + 1);
                ByteArrayResource resource = new ByteArrayResource(document.getBytes()) {
                    @Override
                    public String getFilename() {
                        return displayName;
                    }
                };
                helper.addAttachment(displayName, resource, recordHelpDocumentPolicy.trustedContentType(document));
            }
            mailSender.send(message);
            return true;
        } catch (MessagingException | IOException | MailException exception) {
            LOGGER.error("Failed to send Gmail notification with documents for record help request {}", requestId, exception);
            return false;
        }
    }

    private String subject(ClosingRiskCheckForm form) {
        String service = "understand_file".equals(form.getResearchGoal()) ? "document review" : "record help";
        return "[SepticPath " + service + "] " + safeLine(form.getStateCode()) + " / "
                + safeLine(form.getRecordStatus()) + transactionSuffix(form);
    }

    private String messageText(String requestId, ClosingRiskCheckForm form, int documentCount) {
        return """
                New Septic Record Help request

                Offer: free submission, research, agency requests, source records and explanation. Agency fees at cost require advance approval. No paid unlock. Preserve historical intake and sharing terms for returning customers.

                Request ID: %s
                Source context: %s
                Search entry page: %s
                CTA source page: %s
                Name: %s
                Email: %s
                Phone: %s
                Matched-professional phone sharing authorized: %s
                Requester role: %s
                Stated purpose: %s
                Timeframe: %s
                Property: %s
                State / county: %s / %s
                Record type: %s
                Research goal: %s
                Listing URL: %s
                Listing bedrooms: %s
                Permit bedrooms: %s
                Record status: %s
                Deadline: %s
                Documents attached: %s
                Concern: %s

                Review the stated purpose and region before starting research. If a suitable provider accepts this type of inquiry, service follow-up can proceed alongside research; an agency reply is not a prerequisite. Phone presence is not verified reachability or confirmed field-work intent. Record actual buyer acceptance, contact, payment and processing time separately.

                The requester consented to operator review, email follow-up, and disclosed local-professional matching when field work may help. Matching may share the supplied name, email, phone, property address, role, purpose and timeframe with relevant local professionals. Manual calls and service-specific texts only; no automated marketing or unrelated resale. Some professionals may compensate SepticPath. Do not forward uploaded files or private agency correspondence for matching without the separate permission described in the intake terms.
                """.formatted(
                requestId,
                safeLine(form.getSourceContext()),
                safeLine(form.getEntryPageHint()),
                safeLine(form.getSourcePageHint()),
                safeLine(form.getFullName()),
                safeLine(form.getEmail()),
                safeLine(form.getPhone()),
                form.getPhone() != null && !form.getPhone().isBlank(),
                safeLine(form.getTransactionRole()),
                safeLine(form.getHelpPurposeLabel()),
                safeLine(form.getTimeline()),
                safeLine(form.getPropertyAddress()),
                safeLine(form.getStateCode()),
                safeLine(form.getCountyName()),
                safeLine(form.getRecordType()),
                safeLine(form.getResearchGoal()),
                safeLine(form.getListingUrl()),
                form.getListingBedrooms() == null ? "not supplied" : form.getListingBedrooms(),
                form.getPermitBedrooms() == null ? "not supplied" : form.getPermitBedrooms(),
                safeLine(form.getRecordStatus()),
                form.getDeadline(),
                documentCount,
                safeMultiline(form.getConcern())
        );
    }

    private String safeMultiline(String value) {
        if (value == null) {
            return "";
        }
        return value.replace('\r', ' ').replace('\n', ' ').strip();
    }

    private String transactionSuffix(ClosingRiskCheckForm form) {
        if (form.getTransactionRole() == null || form.getTransactionRole().isBlank()) {
            return "";
        }
        if ("researching".equals(form.getTransactionRole())) {
            return " — research";
        }
        return form.getDeadline() == null ? " — transaction" : " — deadline " + form.getDeadline();
    }

    private String safeLine(String value) {
        return value == null ? "" : value.replace('\r', ' ').replace('\n', ' ').trim();
    }

}
