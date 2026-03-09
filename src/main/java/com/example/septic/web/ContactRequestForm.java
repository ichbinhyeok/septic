package com.example.septic.web;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ContactRequestForm {
    @NotBlank(message = "Name is required.")
    @Size(max = 120, message = "Name must be 120 characters or fewer.")
    private String fullName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Enter a valid email.")
    @Size(max = 160, message = "Email must be 160 characters or fewer.")
    private String email;

    @Pattern(
            regexp = "general_question|source_correction|privacy_request|partnership",
            message = "Choose a valid topic."
    )
    private String topic = "general_question";

    @Pattern(regexp = "^$|[A-Z]{2}$", message = "Choose a valid state or leave it blank.")
    private String stateCode = "";

    @NotBlank(message = "Message is required.")
    @Size(max = 2000, message = "Message must be 2,000 characters or fewer.")
    private String message;

    @AssertTrue(message = "Acknowledgement is required.")
    private boolean acknowledgementAccepted;

    public String getAcknowledgementTextSnapshot() {
        return "I understand this form is for general questions, source corrections, privacy requests, and partnership inquiries. Septic pricing requests should use the estimator or quote form.";
    }

    public String getFullNameValue() {
        return fullName == null ? "" : fullName;
    }

    public String getEmailValue() {
        return email == null ? "" : email;
    }

    public String getStateCodeValue() {
        return stateCode == null ? "" : stateCode;
    }

    public String getMessageValue() {
        return message == null ? "" : message;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isAcknowledgementAccepted() {
        return acknowledgementAccepted;
    }

    public void setAcknowledgementAccepted(boolean acknowledgementAccepted) {
        this.acknowledgementAccepted = acknowledgementAccepted;
    }
}
