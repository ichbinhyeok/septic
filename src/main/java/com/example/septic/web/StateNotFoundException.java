package com.example.septic.web;

public class StateNotFoundException extends RuntimeException {
    private final String missingPath;

    public StateNotFoundException(String stateSlug) {
        super("State not found: " + stateSlug);
        this.missingPath = stateSlug;
    }

    public String missingPath() {
        return missingPath;
    }
}
