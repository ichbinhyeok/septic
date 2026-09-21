package com.example.septic.web;

public class EstimateForm {
    private String calculatorMode = "cost";
    private boolean calculationSubmitted;
    private String stateCode = "";
    private String projectType = "new_install";
    private String sourcePageHint;
    private Integer bedrooms = 3;
    private Integer occupants;
    private boolean garbageDisposal;
    private boolean additionalKitchen;
    private String soilPercStatus = "unknown";
    private boolean highWaterTableOrShallowBedrock;
    private boolean noClearReplacementArea;
    private String accessDifficulty = "easy";
    private String timeline = "researching";
    private Integer tankSizeGallons = 1000;
    private String occupancyProfile = "balanced";
    private String usageProfile = "full_time";

    public String getCalculatorMode() {
        return calculatorMode;
    }

    public void setCalculatorMode(String calculatorMode) {
        this.calculatorMode = calculatorMode;
    }

    public boolean isCalculationSubmitted() {
        return calculationSubmitted;
    }

    public void setCalculationSubmitted(boolean calculationSubmitted) {
        this.calculationSubmitted = calculationSubmitted;
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

    public String getSourcePageHint() {
        return sourcePageHint;
    }

    public void setSourcePageHint(String sourcePageHint) {
        this.sourcePageHint = sourcePageHint;
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

    public String getOccupantsValue() {
        return occupants == null ? "" : occupants.toString();
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

    public boolean isNoClearReplacementArea() {
        return noClearReplacementArea;
    }

    public void setNoClearReplacementArea(boolean noClearReplacementArea) {
        this.noClearReplacementArea = noClearReplacementArea;
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

    public Integer getTankSizeGallons() {
        return tankSizeGallons;
    }

    public String getTankSizeGallonsValue() {
        return tankSizeGallons == null ? "" : tankSizeGallons.toString();
    }

    public void setTankSizeGallons(Integer tankSizeGallons) {
        this.tankSizeGallons = tankSizeGallons;
    }

    public String getOccupancyProfile() {
        return occupancyProfile;
    }

    public void setOccupancyProfile(String occupancyProfile) {
        this.occupancyProfile = occupancyProfile;
    }

    public String getUsageProfile() {
        return usageProfile;
    }

    public void setUsageProfile(String usageProfile) {
        this.usageProfile = usageProfile;
    }
}
