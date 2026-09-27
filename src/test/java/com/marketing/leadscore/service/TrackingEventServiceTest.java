package com.marketing.leadscore.service;

import com.marketing.leadscore.dto.TrackingEventRequest;
import com.marketing.leadscore.entity.TrackingEvent;
import com.marketing.leadscore.entity.Organization;
import com.marketing.leadscore.repository.TrackingEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrackingEventServiceTest {

    @Mock
    private TrackingEventRepository repository;

    @Mock
    private OrganizationService organizationService;

    @InjectMocks
    private TrackingEventService service;

    @Test
    void rejectsEventsWithoutConsent() {
        TrackingEventRequest request = new TrackingEventRequest(
                "PAGE_VIEW", "visitor-1", null, "/", null, false);

        assertThrows(IllegalArgumentException.class, () -> service.record(request, "key"));
    }

    @Test
    void storesAllowedConsentedEvent() {
        TrackingEventRequest request = new TrackingEventRequest(
                "page_view", "visitor-1", "session-1", "/", null, true);

        when(organizationService.authenticate("key"))
                .thenReturn(new Organization("Example", "hash", java.time.LocalDateTime.now()));

        service.record(request, "key");

        verify(repository).save(any(TrackingEvent.class));
    }
}
