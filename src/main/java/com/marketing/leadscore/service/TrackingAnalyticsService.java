package com.marketing.leadscore.service;

import com.marketing.leadscore.dto.TrackingAnalyticsDTO;
import com.marketing.leadscore.entity.Organization;
import com.marketing.leadscore.entity.TrackingEvent;
import com.marketing.leadscore.repository.TrackingEventRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TrackingAnalyticsService {

    private final OrganizationService organizationService;
    private final TrackingEventRepository repository;

    public TrackingAnalyticsService(
            OrganizationService organizationService,
            TrackingEventRepository repository) {
        this.organizationService = organizationService;
        this.repository = repository;
    }

    public TrackingAnalyticsDTO summarize(String apiKey) {
        Organization organization = organizationService.authenticate(apiKey);
        List<TrackingEvent> events = repository.findByOrganizationId(organization.getId());

        long visitors = events.stream()
                .map(TrackingEvent::getVisitorId)
                .distinct()
                .count();
        long identifiedVisitors = events.stream()
                .filter(event -> event.getCustomer() != null)
                .map(TrackingEvent::getVisitorId)
                .distinct()
                .count();

        Map<String, Long> eventCounts = events.stream()
                .collect(Collectors.groupingBy(
                        TrackingEvent::getEventType,
                        LinkedHashMap::new,
                        Collectors.counting()));

        List<TrackingAnalyticsDTO.PageMetric> topPages = events.stream()
                .map(TrackingEvent::getPageUrl)
                .filter(page -> page != null && !page.isBlank())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(entry -> new TrackingAnalyticsDTO.PageMetric(entry.getKey(), entry.getValue()))
                .toList();

        return new TrackingAnalyticsDTO(
                events.size(),
                visitors,
                identifiedVisitors,
                visitors == 0 ? 0.0 : round((double) identifiedVisitors / visitors * 100.0),
                eventCounts,
                topPages
        );
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
