package com.marketing.leadscore.service;

import com.marketing.leadscore.dto.TrackingEventRequest;
import com.marketing.leadscore.entity.TrackingEvent;
import com.marketing.leadscore.entity.Organization;
import com.marketing.leadscore.repository.TrackingEventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;

@Service
public class TrackingEventService {

    private static final Set<String> ALLOWED_EVENTS = Set.of(
            "PAGE_VIEW", "PRODUCT_VIEW", "FORM_STARTED", "FORM_SUBMITTED",
            "DEMO_REQUESTED", "ACCOUNT_CREATED", "CTA_CLICKED", "PURCHASE_COMPLETED"
    );

    private final TrackingEventRepository repository;
    private final OrganizationService organizationService;

    public TrackingEventService(TrackingEventRepository repository, OrganizationService organizationService) {
        this.repository = repository;
        this.organizationService = organizationService;
    }

    public TrackingEvent record(TrackingEventRequest request, String apiKey) {
        if (!request.consentGiven()) {
            throw new IllegalArgumentException("Tracking consent is required.");
        }

        String eventType = request.eventType().trim().toUpperCase(Locale.ROOT);
        if (!ALLOWED_EVENTS.contains(eventType)) {
            throw new IllegalArgumentException("Unsupported tracking event: " + eventType);
        }

        Organization organization = organizationService.authenticate(apiKey);
        return repository.save(new TrackingEvent(
                organization,
                eventType,
                request.visitorId().trim(),
                blankToNull(request.sessionId()),
                blankToNull(request.pageUrl()),
                blankToNull(request.referrer()),
                LocalDateTime.now()
        ));
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
