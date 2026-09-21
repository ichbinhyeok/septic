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
                Question: %s

                We aim to send an initial update within 1–2 business days. Agency response times can take longer.

                Research and agency requests are free. If we find useful, property-matched evidence, we will email a free preview of the source, document scope, property match, questions it can answer, and material limits. Your actual property-specific answers and located source records are included in the optional US $29 package. Your own uploaded files remain yours. There is no automatic charge. Any agency fee requires your approval first.

                Keep this email and reference number if you need to follow up. Do not email payment-card details or access codes.

                SepticPath is an independent records-research service, not an inspection, permitting, engineering, or legal authority.
                """.formatted(
                requestId,
                safeLine(form.getPropertyAddress()),
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

                Offer: free submission, research and agency requests; evidence preview shows source, scope, property match, answerable questions and limits. Actual answers and located source files unlock for US $29. Agency fees at cost require advance approval. No automatic charge. Preserve the original intake terms for all returning customers, including free-beta service and earlier free-finding promises.

                Request ID: %s
                Source context: %s
                Search entry page: %s
                CTA source page: %s
                Name: %s
                Email: %s
                Phone: %s
                Matched-professional phone sharing authorized: %s
                Process stage: %s
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

                Start by identifying the likely public-record owner and exact file to request. If the process stage is buyer, seller, agent, or other and a deadline or conflict is present, qualify the request for a deeper closing-risk follow-up.

                The requester consented to operator review, email follow-up, and the disclosed local-professional matching terms when field work may help. Matching may share name, email, optional mobile number, property address, and service need with up to three relevant local septic professionals. A supplied mobile number authorizes manual calls or service-specific texts about this request, but not automated or prerecorded marketing, unrelated solicitations, or onward resale. This is record-path help, not an inspection or compliance certification.
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
