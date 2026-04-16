package com.example.septic.web;

public record StateMoneyPrimaryAction(
        String eyebrow,
        String heading,
        String note,
        String buttonLabel,
        String path,
        String sourceContext,
        String targetType,
        boolean calculator
) {
}
