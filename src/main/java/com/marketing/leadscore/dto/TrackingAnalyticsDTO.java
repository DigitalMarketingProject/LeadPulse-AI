package com.marketing.leadscore.dto;

import java.util.List;
import java.util.Map;

public record TrackingAnalyticsDTO(
        long totalEvents,
        long anonymousVisitors,
        long identifiedVisitors,
        double visitorToLeadRate,
        Map<String, Long> eventsByType,
        List<PageMetric> topPages
) {
    public record PageMetric(String pageUrl, long events) {
    }
}
