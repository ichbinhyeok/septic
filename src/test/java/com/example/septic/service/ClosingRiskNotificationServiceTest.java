package com.example.septic.service;

import com.example.septic.config.ClosingRiskNotificationProperties;
import com.example.septic.web.ClosingRiskCheckForm;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;

class ClosingRiskNotificationServiceTest {

    @Test
    void sendsActionableRequestToOperatorWithRequesterAsReplyTo() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        ClosingRiskNotificationService service = new ClosingRiskNotificationService(
                mailSender,
                new ClosingRiskNotificationProperties("shinhyeok22@gmail.com", "shinhyeok22@gmail.com")
        );

        assertTrue(service.notifyOperator("request-123", completedForm()));

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage message = captor.getValue();
        assertEquals("shinhyeok22@gmail.com", message.getTo()[0]);
        assertEquals("taylor@example.com", message.getReplyTo());
        assertTrue(message.getSubject().contains("TN / conflicting"));
        assertTrue(message.getText().contains("Request ID: request-123"));
        assertTrue(message.getText().contains("Source context: tdec_quick_help_record_help"));
        assertTrue(message.getText().contains("123 Private Lane"));
        assertTrue(message.getText().contains("Deadline:"));
    }

    @Test
    void storesWithoutAttemptingMailWhenCredentialsAreNotConfigured() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        ClosingRiskNotificationService service = new ClosingRiskNotificationService(
                mailSender,
                new ClosingRiskNotificationProperties("", "shinhyeok22@gmail.com")
        );

        assertFalse(service.notifyOperator("request-456", completedForm()));
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void reportsFailureWithoutLosingTheStoredRequestWhenGmailRejectsTheMessage() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        doThrow(new MailSendException("SMTP unavailable"))
                .when(mailSender).send(any(SimpleMailMessage.class));
        ClosingRiskNotificationService service = new ClosingRiskNotificationService(
                mailSender,
                new ClosingRiskNotificationProperties("shinhyeok22@gmail.com", "shinhyeok22@gmail.com")
        );

        assertFalse(service.notifyOperator("request-789", completedForm()));
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    private ClosingRiskCheckForm completedForm() {
        ClosingRiskCheckForm form = new ClosingRiskCheckForm();
        form.setFullName("Taylor Buyer");
        form.setEmail("taylor@example.com");
        form.setTransactionRole("buyer");
        form.setPropertyAddress("123 Private Lane, Knoxville, TN 37920");
        form.setStateCode("TN");
        form.setCountyName("Knox County");
        form.setListingUrl("https://example.com/listing/123");
        form.setListingBedrooms(4);
        form.setPermitBedrooms(3);
        form.setRecordStatus("conflicting");
        form.setDeadline(LocalDate.now().plusDays(6));
        form.setConcern("Need to resolve the bedroom mismatch.");
        form.setConsentAccepted(true);
        form.setSourceContext("tdec_quick_help_record_help");
        return form;
    }
}
