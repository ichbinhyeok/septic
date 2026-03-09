package com.example.septic.web;

public class PumpScheduleForm {
    private Integer tankSizeGallons = 1000;
    private Integer occupants = 3;
    private boolean garbageDisposal;
    private String usageProfile = "full_time";

    public String getTankSizeGallonsValue() {
        return tankSizeGallons == null ? "" : tankSizeGallons.toString();
    }

    public String getOccupantsValue() {
        return occupants == null ? "" : occupants.toString();
    }

    public Integer getTankSizeGallons() {
        return tankSizeGallons;
    }

    public void setTankSizeGallons(Integer tankSizeGallons) {
        this.tankSizeGallons = tankSizeGallons;
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

    public String getUsageProfile() {
        return usageProfile;
    }

    public void setUsageProfile(String usageProfile) {
        this.usageProfile = usageProfile;
    }
}
