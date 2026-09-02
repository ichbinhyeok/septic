package com.example.septic.web;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class ClosingRiskCheckForm {
    @NotBlank(message = "Name is required.")
    @Size(max = 120, message = "Name must be 120 characters or fewer.")
    private String fullName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Enter a valid email address.")
    @Size(max = 160, message = "Email must be 160 characters or fewer.")
    private String email;

    @NotBlank(message = "Choose your role in the transaction.")
    @Pattern(regexp = "buyer|seller|agent|other", message = "Choose a valid role.")
    private String transactionRole;

    @NotBlank(message = "Property address is required.")
    @Size(min = 8, max = 220, message = "Enter a complete property address.")
    private String propertyAddress;

    @NotBlank(message = "State is required.")
    @Pattern(regexp = "[A-Z]{2}", message = "Choose a valid state.")
    private String stateCode;

    @Size(max = 120, message = "County must be 120 characters or fewer.")
    private String countyName;

    @Size(max = 500, message = "Listing URL must be 500 characters or fewer.")
    @Pattern(regexp = "^$|https?://[^\\s]+$", message = "Enter a complete http or https listing URL.")
    private String listingUrl;

    @Min(value = 1, message = "Listing bedrooms must be at least 1.")
    @Max(value = 20, message = "Listing bedrooms must be 20 or fewer.")
    private Integer listingBedrooms;

    @Min(value = 1, message = "Permit bedrooms must be at least 1.")
    @Max(value = 20, message = "Permit bedrooms must be 20 or fewer.")
    private Integer permitBedrooms;

    @NotBlank(message = "Choose what is known about the septic file.")
    @Pattern(regexp = "missing|partial|conflicting|unknown", message = "Choose a valid file status.")
    private String recordStatus;

    @NotNull(message = "Due-diligence or closing deadline is required.")
    @FutureOrPresent(message = "Deadline cannot be in the past.")
    private LocalDate deadline;

    @Size(max = 1200, message = "Notes must be 1,200 characters or fewer.")
    private String concern;

    @AssertTrue(message = "Consent is required.")
    private boolean consentAccepted;

    @Size(max = 200)
    private String website;

    public boolean isBotSubmission() {
        return website != null && !website.isBlank();
    }

    public String getConsentTextSnapshot() {
        return "I agree that SepticPath may store these property and contact details and email them to its operator to prepare this free beta file check. I understand this is not an inspection, permit decision, legal opinion, or compliance certification.";
    }

    public String getFullNameValue() { return fullName == null ? "" : fullName; }
    public String getEmailValue() { return email == null ? "" : email; }
    public String getPropertyAddressValue() { return propertyAddress == null ? "" : propertyAddress; }
    public String getCountyNameValue() { return countyName == null ? "" : countyName; }
    public String getListingUrlValue() { return listingUrl == null ? "" : listingUrl; }
    public String getConcernValue() { return concern == null ? "" : concern; }
    public String getDeadlineValue() { return deadline == null ? "" : deadline.toString(); }
    public String getListingBedroomsValue() { return listingBedrooms == null ? "" : listingBedrooms.toString(); }
    public String getPermitBedroomsValue() { return permitBedrooms == null ? "" : permitBedrooms.toString(); }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTransactionRole() { return transactionRole; }
    public void setTransactionRole(String transactionRole) { this.transactionRole = transactionRole; }
    public String getPropertyAddress() { return propertyAddress; }
    public void setPropertyAddress(String propertyAddress) { this.propertyAddress = propertyAddress; }
    public String getStateCode() { return stateCode; }
    public void setStateCode(String stateCode) { this.stateCode = stateCode; }
    public String getCountyName() { return countyName; }
    public void setCountyName(String countyName) { this.countyName = countyName; }
    public String getListingUrl() { return listingUrl; }
    public void setListingUrl(String listingUrl) { this.listingUrl = listingUrl; }
    public Integer getListingBedrooms() { return listingBedrooms; }
    public void setListingBedrooms(Integer listingBedrooms) { this.listingBedrooms = listingBedrooms; }
    public Integer getPermitBedrooms() { return permitBedrooms; }
    public void setPermitBedrooms(Integer permitBedrooms) { this.permitBedrooms = permitBedrooms; }
    public String getRecordStatus() { return recordStatus; }
    public void setRecordStatus(String recordStatus) { this.recordStatus = recordStatus; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public String getConcern() { return concern; }
    public void setConcern(String concern) { this.concern = concern; }
    public boolean isConsentAccepted() { return consentAccepted; }
    public void setConsentAccepted(boolean consentAccepted) { this.consentAccepted = consentAccepted; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
}
