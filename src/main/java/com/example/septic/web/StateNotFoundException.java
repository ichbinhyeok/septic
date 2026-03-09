package com.example.septic.web;

public class StateNotFoundException extends RuntimeException {
    public StateNotFoundException(String stateSlug) {
        super("State not found: " + stateSlug);
    }
}
