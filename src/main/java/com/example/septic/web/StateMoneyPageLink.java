package com.example.septic.web;

public record StateMoneyPageLink(
        String title,
        String stateName,
        String stateCode,
        String surfaceTitle,
        String path,
        StateSurfaceSignalView surfaceSignals
) {
    public StateMoneyPageLink(String title, String stateName, String stateCode, String path) {
        this(title, stateName, stateCode, title, path, null);
    }

    public StateMoneyPageLink(String title, String stateName, String stateCode, String path, StateSurfaceSignalView surfaceSignals) {
        this(title, stateName, stateCode, title, path, surfaceSignals);
    }
}
