package com.marketing.leadscore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TrackingEventRequest(
        @NotBlank @Size(max = 40) String eventType,
        @NotBlank @Size(max = 100) String visitorId,
        @Size(max = 100) String sessionId,
        @Size(max = 500) String pageUrl,
        @Size(max = 500) String referrer,
        boolean consentGiven
) {
}
