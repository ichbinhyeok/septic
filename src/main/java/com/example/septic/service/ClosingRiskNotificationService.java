package com.example.septic.service;

import com.example.septic.config.ClosingRiskNotificationProperties;
import com.example.septic.web.ClosingRiskCheckForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class ClosingRiskNotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClosingRiskNotificationService.class);
    private final JavaMailSender mailSender;
    private final ClosingRiskNotificationProperties properties;

    public ClosingRiskNotificationService(
            JavaMailSender mailSender,
            ClosingRiskNotificationProperties properties
    ) {
        this.mailSender = mailSender;
        this.properties = properties;
    }

    public boolean notifyOperator(String requestId, ClosingRiskCheckForm form) {
        if (!properties.isConfigured()) {
            LOGGER.warn("Closing risk request {} was stored, but Gmail notification is not configured", requestId);
            return false;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(properties.sender());
        message.setTo(properties.recipient());
        message.setReplyTo(safeLine(form.getEmail()));
        message.setSubject("[SepticPath beta] " + safeLine(form.getStateCode()) + " / "
                + safeLine(form.getCountyName()) + " — deadline " + form.getDeadline());
        message.setText("""
                New Septic Closing Risk Check request

                Request ID: %s
                Name: %s
                Email: %s
                Role: %s
                Property: %s
                State / county: %s / %s
                Listing URL: %s
                Listing bedrooms: %s
                Permit bedrooms: %s
                Record status: %s
                Deadline: %s
                Concern: %s

                The requester consented to operator review and email follow-up. This is a file-readiness review, not an inspection or compliance certification.
                """.formatted(
                requestId,
                safeLine(form.getFullName()),
                safeLine(form.getEmail()),
                safeLine(form.getTransactionRole()),
                safeLine(form.getPropertyAddress()),
                safeLine(form.getStateCode()),
                safeLine(form.getCountyName()),
                safeLine(form.getListingUrl()),
                form.getListingBedrooms() == null ? "not supplied" : form.getListingBedrooms(),
                form.getPermitBedrooms() == null ? "not supplied" : form.getPermitBedrooms(),
                safeLine(form.getRecordStatus()),
                form.getDeadline(),
                safeMultiline(form.getConcern())
        ));
        try {
            mailSender.send(message);
            return true;
        } catch (MailException exception) {
            LOGGER.error("Failed to send Gmail notification for closing risk request {}", requestId, exception);
            return false;
        }
    }

    private String safeLine(String value) {
        return value == null ? "" : value.replace('\r', ' ').replace('\n', ' ').trim();
    }

    private String safeMultiline(String value) {
        return value == null ? "" : value.trim();
    }
}
