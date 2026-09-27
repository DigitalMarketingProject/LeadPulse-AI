package com.marketing.leadscore.service;

import com.marketing.leadscore.dto.LeadWebhookRequest;
import com.marketing.leadscore.entity.Customer;
import com.marketing.leadscore.entity.Organization;
import com.marketing.leadscore.repository.WebhookReceiptRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeadWebhookSecurityTest {

    @Mock
    private TrackingEventService trackingEventService;

    @Mock
    private LeadIdentificationService leadIdentificationService;

    @Mock
    private OrganizationService organizationService;

    @Mock
    private WebhookReceiptRepository receiptRepository;

    @InjectMocks
    private LeadWebhookService service;

    @Test
    void rejectsMissingSecurityHeaders() {
        LeadWebhookRequest request = request();
        assertThrows(IllegalArgumentException.class,
                () -> service.process(request, "key", null, null, null));
        verifyNoInteractions(receiptRepository, trackingEventService, leadIdentificationService);
    }

    @Test
    void rejectsDuplicateIdempotencyKey() {
        Organization organization = new Organization("Example", "hash", LocalDateTime.now());
        when(organizationService.authenticate("key")).thenReturn(organization);
        when(receiptRepository.existsByOrganizationIdAndIdempotencyKey(null, "duplicate"))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> service.process(request(), "key",
                        String.valueOf(Instant.now().getEpochSecond()),
                        "sha256=invalid", "duplicate"));
        verifyNoInteractions(trackingEventService, leadIdentificationService);
    }

    private LeadWebhookRequest request() {
        return new LeadWebhookRequest(
                "FORM_SUBMITTED", "visitor-1", "lead@example.com",
                "Lead", null, "Example", "/demo", true);
    }
}
