package com.example.septic;

import java.io.IOException;

public final class FakeTesseractProcess {
    private FakeTesseractProcess() {
    }

    public static void main(String[] args) throws IOException {
        byte[] signature = System.in.readNBytes(8);
        byte[] pngSignature = new byte[]{(byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a};
        if (!java.util.Arrays.equals(signature, pngSignature)) {
            System.err.println("Expected an in-memory PNG on standard input.");
            System.exit(2);
        }
        System.in.transferTo(java.io.OutputStream.nullOutputStream());
        System.out.println("Permit Number: PIPE-2026-4408");
        System.out.println("Approved for 4 bedrooms.");
        System.out.println("Design flow: 600 GPD.");
    }
}
