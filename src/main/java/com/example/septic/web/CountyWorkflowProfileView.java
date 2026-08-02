package com.example.septic.web;

public record CountyWorkflowProfileView(
        CountyAccessProfileView access,
        CountyAcquisitionProfileView acquisition
) {
}
