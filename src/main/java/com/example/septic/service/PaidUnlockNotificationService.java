package com.example.septic.service;

import com.example.septic.config.AppSiteProperties;
import com.example.septic.config.ClosingRiskNotificationProperties;
import com.example.septic.config.PaidUnlockProperties;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class PaidUnlockNotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaidUnlockNotificationService.class);
    private final JavaMailSender mailSender;
    private final ClosingRiskNotificationProperties mailProperties;
    private final AppSiteProperties siteProperties;
    private final PaidUnlockProperties paidUnlockProperties;

    public PaidUnlockNotificationService(
            JavaMailSender mailSender,
            ClosingRiskNotificationProperties mailProperties,
            AppSiteProperties siteProperties,
            PaidUnlockProperties paidUnlockProperties
    ) {
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
        this.siteProperties = siteProperties;
        this.paidUnlockProperties = paidUnlockProperties;
    }

    public boolean sendPackageReady(PaidUnlockStore.Fulfillment fulfillment) {
        if (!mailProperties.isConfigured()) {
            LOGGER.error("Paid package {} could not be emailed because mail is not configured", fulfillment.offer().id());
            return false;
        }
        URI download = siteProperties.baseUri().resolve("/paid-unlock/download/" + fulfillment.downloadToken());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.sender());
        message.setTo(fulfillment.offer().customerEmail());
        message.setReplyTo(mailProperties.recipient());
        message.setSubject("Your SepticPath record package is ready — " + fulfillment.offer().propertyLabel());
        message.setText("""
                Hi,

                Your payment has been confirmed and your reviewed property-record package is ready.

                Download your package:
                %s

                This private link expires at %s and supports up to %d downloads. It contains the source records and the property-specific explanation described in your preview.

                Request reference: %s

                If the link does not work, reply to this email with your request reference. Do not send payment-card details by email.

                Best,
                Shinhyeok
                Founder, SepticPath
                """.formatted(
                        download,
                        fulfillment.expiresAt(),
                        paidUnlockProperties.maxDownloads(),
                        fulfillment.offer().requestReference()
                ));
        try {
            mailSender.send(message);
            return true;
        } catch (MailException exception) {
            LOGGER.error("Failed to email paid package for offer {}", fulfillment.offer().id(), exception);
            return false;
        }
    }

    public void notifyOperatorFailure(String offerId, String detail) {
        if (!mailProperties.isConfigured()) {
            LOGGER.error("Paid-unlock failure for {}: {}", offerId, detail);
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.sender());
        message.setTo(mailProperties.recipient());
        message.setSubject("[SepticPath paid unlock] attention required — " + offerId);
        message.setText("Paid-unlock automation needs attention.\n\nOffer: " + offerId + "\nIssue: " + detail);
        try {
            mailSender.send(message);
        } catch (MailException exception) {
            LOGGER.error("Failed to notify operator about paid-unlock failure for {}", offerId, exception);
        }
    }
}
