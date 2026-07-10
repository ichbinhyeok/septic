package com.example.septic.web;

public record AddressRecordFinderForm(String address) {
    public boolean isUsable() {
        return address != null && address.trim().length() >= 8 && address.trim().length() <= 180;
    }

    public String normalizedAddress() {
        return address == null ? "" : address.trim();
    }
}
