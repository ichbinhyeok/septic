package com.example.septic.web;

public class DrainfieldEstimatorForm {
    private String stateCode = "GA";
    private Integer bedrooms = 3;
    private String soilPercStatus = "unknown";
    private String accessDifficulty = "easy";
    private String timeline = "researching";
    private boolean wetGroundOrSurfacing;
    private boolean noClearReplacementArea;

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

    public String getSoilPercStatus() {
        return soilPercStatus;
    }

    public void setSoilPercStatus(String soilPercStatus) {
        this.soilPercStatus = soilPercStatus;
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

    public boolean isWetGroundOrSurfacing() {
        return wetGroundOrSurfacing;
    }

    public void setWetGroundOrSurfacing(boolean wetGroundOrSurfacing) {
        this.wetGroundOrSurfacing = wetGroundOrSurfacing;
    }

    public boolean isNoClearReplacementArea() {
        return noClearReplacementArea;
    }

    public void setNoClearReplacementArea(boolean noClearReplacementArea) {
        this.noClearReplacementArea = noClearReplacementArea;
    }
}
