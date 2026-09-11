package com.example.septic.web;

import com.example.septic.data.model.CountyRecordSearchGuide;
import com.example.septic.data.model.SourceRecord;
import java.util.List;

public record CountySearchGuideView(
        String countyName,
        String stateCode,
        String countyPath,
        CountyRecordSearchGuide guide,
        List<SourceRecord> sources
) {
}
