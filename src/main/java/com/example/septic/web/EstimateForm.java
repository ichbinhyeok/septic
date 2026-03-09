package com.example.septic.web;

public class EstimateForm {
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
}
