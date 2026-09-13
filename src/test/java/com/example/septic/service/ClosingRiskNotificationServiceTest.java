package com.example.septic.service;

import com.example.septic.config.ClosingRiskNotificationProperties;
import com.example.septic.web.ClosingRiskCheckForm;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
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
import static org.mockito.Mockito.when;

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
        assertTrue(message.getText().contains("Search entry page: /tdec-septic-records/"));
        assertTrue(message.getText().contains("CTA source page: /septic-records-checklist/tennessee/knox-county/"));
        assertTrue(message.getText().contains("123 Private Lane"));
        assertTrue(message.getText().contains("Record type: septic"));
        assertTrue(message.getText().contains("Research goal: design_capacity"));
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
    void doesNotLabelAnUnspecifiedProcessStageAsATransaction() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        ClosingRiskNotificationService service = new ClosingRiskNotificationService(
                mailSender,
                new ClosingRiskNotificationProperties("shinhyeok22@gmail.com", "shinhyeok22@gmail.com")
        );
        ClosingRiskCheckForm form = completedForm();
        form.setTransactionRole(null);
        form.setDeadline(null);

        assertTrue(service.notifyOperator("request-optional-stage", form));

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        assertEquals("[SepticPath record help] TN / conflicting", captor.getValue().getSubject());
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

    @Test
    void attachesCustomerSourceFilesForHumanReview() throws Exception {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        ClosingRiskNotificationService service = new ClosingRiskNotificationService(
                mailSender,
                new ClosingRiskNotificationProperties("shinhyeok22@gmail.com", "shinhyeok22@gmail.com")
        );
        ClosingRiskCheckForm form = completedForm();
        form.setResearchGoal("understand_file");
        form.setDocuments(List.of(new MockMultipartFile(
                "documents",
                "county-permit.pdf",
                "application/pdf",
                "%PDF-1.7\nrecord".getBytes(StandardCharsets.US_ASCII)
        )));

        assertTrue(service.notifyOperator("request-review", form));

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());
        MimeMessage sent = captor.getValue();
        assertEquals("[SepticPath document review] TN / conflicting — deadline " + form.getDeadline(), sent.getSubject());
        Multipart content = (Multipart) sent.getContent();
        assertEquals(2, content.getCount());
        assertEquals("county-permit.pdf", content.getBodyPart(1).getFileName());
    }

    private ClosingRiskCheckForm completedForm() {
        ClosingRiskCheckForm form = new ClosingRiskCheckForm();
        form.setFullName("Taylor Buyer");
        form.setEmail("taylor@example.com");
        form.setTransactionRole("buyer");
        form.setPropertyAddress("123 Private Lane, Knoxville, TN 37920");
        form.setStateCode("TN");
        form.setRecordType("septic");
        form.setResearchGoal("design_capacity");
        form.setCountyName("Knox County");
        form.setListingUrl("https://example.com/listing/123");
        form.setListingBedrooms(4);
        form.setPermitBedrooms(3);
        form.setRecordStatus("conflicting");
        form.setDeadline(LocalDate.now().plusDays(6));
        form.setConcern("Need to resolve the bedroom mismatch.");
        form.setConsentAccepted(true);
        form.setSourceContext("tdec_quick_help_record_help");
        form.setEntryPageHint("/tdec-septic-records/");
        form.setSourcePageHint("/septic-records-checklist/tennessee/knox-county/");
        return form;
    }
}
