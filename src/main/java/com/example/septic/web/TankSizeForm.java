package com.example.septic.web;

public class TankSizeForm {
    private String stateCode = "GA";
    private Integer bedrooms = 3;
    private boolean garbageDisposal;
    private boolean additionalKitchen;
    private String occupancyProfile = "balanced";

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public Integer getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
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

    public String getOccupancyProfile() {
        return occupancyProfile;
    }

    public void setOccupancyProfile(String occupancyProfile) {
        this.occupancyProfile = occupancyProfile;
    }
}
