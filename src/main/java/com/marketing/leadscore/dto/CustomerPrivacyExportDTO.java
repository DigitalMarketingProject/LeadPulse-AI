package com.marketing.leadscore.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CustomerPrivacyExportDTO(
        Long customerId,
        String name,
        String email,
        String phone,
        String company,
        int leadScore,
        String leadStatus,
        List<ActivityExport> activities,
        List<TrackingExport> trackingEvents
) {
    public record ActivityExport(String type, String remarks, LocalDate date) {
    }

    public record TrackingExport(String eventType, String pageUrl, String referrer, LocalDateTime occurredAt) {
    }
}
