package com.example.septic.web;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class QuoteLeadForm {
    private String stateCode = "GA";
    private String projectType = "new_install";
    private Integer bedrooms = 3;
    private Integer occupants;
    private boolean garbageDisposal;
    private boolean additionalKitchen;
    private String soilPercStatus = "unknown";
    private boolean highWaterTableOrShallowBedrock;
    private String accessDifficulty = "easy";
    private String timeline = "researching";

    @NotBlank(message = "Name is required.")
    private String fullName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Enter a valid email.")
    private String email;

    @NotBlank(message = "Phone is required.")
    private String phone;

    @NotBlank(message = "ZIP code is required.")
    @Pattern(regexp = "\\d{5}", message = "Enter a 5-digit ZIP code.")
    private String zipCode;

    @AssertTrue(message = "Consent is required.")
    private boolean consentAccepted;

    public static QuoteLeadForm fromEstimateForm(EstimateForm estimateForm) {
        QuoteLeadForm quoteLeadForm = new QuoteLeadForm();
        quoteLeadForm.stateCode = estimateForm.getStateCode();
        quoteLeadForm.projectType = estimateForm.getProjectType();
        quoteLeadForm.bedrooms = estimateForm.getBedrooms();
        quoteLeadForm.occupants = estimateForm.getOccupants();
        quoteLeadForm.garbageDisposal = estimateForm.isGarbageDisposal();
        quoteLeadForm.additionalKitchen = estimateForm.isAdditionalKitchen();
        quoteLeadForm.soilPercStatus = estimateForm.getSoilPercStatus();
        quoteLeadForm.highWaterTableOrShallowBedrock = estimateForm.isHighWaterTableOrShallowBedrock();
        quoteLeadForm.accessDifficulty = estimateForm.getAccessDifficulty();
        quoteLeadForm.timeline = estimateForm.getTimeline();
        return quoteLeadForm;
    }

    public EstimateForm toEstimateForm() {
        EstimateForm estimateForm = new EstimateForm();
        estimateForm.setStateCode(stateCode);
        estimateForm.setProjectType(projectType);
        estimateForm.setBedrooms(bedrooms);
        estimateForm.setOccupants(occupants);
        estimateForm.setGarbageDisposal(garbageDisposal);
        estimateForm.setAdditionalKitchen(additionalKitchen);
        estimateForm.setSoilPercStatus(soilPercStatus);
        estimateForm.setHighWaterTableOrShallowBedrock(highWaterTableOrShallowBedrock);
        estimateForm.setAccessDifficulty(accessDifficulty);
        estimateForm.setTimeline(timeline);
        return estimateForm;
    }

    public String getOccupantsValue() {
        return occupants == null ? "" : occupants.toString();
    }

    public String getFullNameValue() {
        return fullName == null ? "" : fullName;
    }

    public String getEmailValue() {
        return email == null ? "" : email;
    }

    public String getPhoneValue() {
        return phone == null ? "" : phone;
    }

    public String getZipCodeValue() {
        return zipCode == null ? "" : zipCode;
    }

    public String getConsentTextSnapshot() {
        return "I agree to be contacted about my septic project estimate and matching options.";
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public Integer getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
    }

    public Integer getOccupants() {
        return occupants;
    }

    public void setOccupants(Integer occupants) {
        this.occupants = occupants;
    }

    public boolean isGarbageDisposal() {
        return garbageDisposal;
    }

    public void setGarbageDisposal(boolean garbageDisposal) {
        this.garbageDisposal = garbageDisposal;
    }

    public boolean isAdditionalKitchen() {
        return additionalKitchen;
    }

    public void setAdditionalKitchen(boolean additionalKitchen) {
        this.additionalKitchen = additionalKitchen;
    }

    public String getSoilPercStatus() {
        return soilPercStatus;
    }

    public void setSoilPercStatus(String soilPercStatus) {
        this.soilPercStatus = soilPercStatus;
    }

    public boolean isHighWaterTableOrShallowBedrock() {
        return highWaterTableOrShallowBedrock;
    }

    public void setHighWaterTableOrShallowBedrock(boolean highWaterTableOrShallowBedrock) {
        this.highWaterTableOrShallowBedrock = highWaterTableOrShallowBedrock;
    }

    public String getAccessDifficulty() {
        return accessDifficulty;
    }

    public void setAccessDifficulty(String accessDifficulty) {
        this.accessDifficulty = accessDifficulty;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public boolean isConsentAccepted() {
        return consentAccepted;
    }

    public void setConsentAccepted(boolean consentAccepted) {
        this.consentAccepted = consentAccepted;
    }
}
