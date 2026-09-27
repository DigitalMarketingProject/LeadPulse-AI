package com.marketing.leadscore.service;

import com.marketing.leadscore.dto.TrackingAnalyticsDTO;
import com.marketing.leadscore.entity.Organization;
import com.marketing.leadscore.entity.TrackingEvent;
import com.marketing.leadscore.repository.TrackingEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrackingAnalyticsServiceTest {

    @Mock
    private OrganizationService organizationService;

    @Mock
    private TrackingEventRepository repository;

    @InjectMocks
    private TrackingAnalyticsService service;

    @Test
    void summarizesVisitorToLeadFunnel() {
        Organization organization = new Organization("Example", "hash", LocalDateTime.now());
        TrackingEvent first = new TrackingEvent(
                organization, "PAGE_VIEW", "visitor-1", "session-1", "/pricing", null, LocalDateTime.now());
        TrackingEvent second = new TrackingEvent(
                organization, "FORM_SUBMITTED", "visitor-1", "session-1", "/pricing", null, LocalDateTime.now());
        second.assignCustomer(new com.marketing.leadscore.entity.Customer());
        TrackingEvent third = new TrackingEvent(
                organization, "PAGE_VIEW", "visitor-2", "session-2", "/home", null, LocalDateTime.now());

        when(organizationService.authenticate("key")).thenReturn(organization);
        when(repository.findByOrganizationId(null)).thenReturn(List.of(first, second, third));

        TrackingAnalyticsDTO result = service.summarize("key");

        assertEquals(3, result.totalEvents());
        assertEquals(2, result.anonymousVisitors());
        assertEquals(1, result.identifiedVisitors());
        assertEquals(50.0, result.visitorToLeadRate());
        assertEquals(2, result.eventsByType().get("PAGE_VIEW"));
        assertEquals("/pricing", result.topPages().get(0).pageUrl());
    }
}
