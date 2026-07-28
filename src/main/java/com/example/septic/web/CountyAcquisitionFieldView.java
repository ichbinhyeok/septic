package com.example.septic.web;

import java.util.List;

public record CountyAcquisitionFieldView(
        String key,
        String label,
        String placeholder,
        boolean required,
        String autocomplete,
        List<String> options
) {
    public boolean select() {
        return options != null && !options.isEmpty();
    }

    public boolean multiline() {
        return "specificInformation".equals(key)
                || "message".equals(key)
                || "requestDetails".equals(key)
                || "purpose".equals(key);
    }
}
