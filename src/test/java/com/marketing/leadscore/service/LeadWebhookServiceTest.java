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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import java.time.Instant;
import java.time.LocalDateTime;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@ExtendWith(MockitoExtension.class)
class LeadWebhookServiceTest {

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
    void processesFormSubmissionAndIdentifiesLead() {
        LeadWebhookRequest request = new LeadWebhookRequest(
                "form_submitted", "visitor-1", "lead@example.com",
                "Lead", null, "Example", "/demo", true);
        Customer customer = new Customer();
        customer.setId(12L);
        customer.setLeadScore(65);
        customer.setLeadStatus("Warm");
        Organization organization = new Organization("Example", "hash", LocalDateTime.now());
        when(organizationService.authenticate("key")).thenReturn(organization);
        when(receiptRepository.existsByOrganizationIdAndIdempotencyKey(null, "idempotency-1"))
                .thenReturn(false);
        when(leadIdentificationService.identify(any(), eq("key"))).thenReturn(customer);

        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        Customer result = service.process(
                request, "key", timestamp, sign("key", timestamp), "idempotency-1");

        assertEquals(12L, result.getId());
        verify(trackingEventService).record(any(), eq("key"));
        verify(leadIdentificationService).identify(any(), eq("key"));
    }

    @Test
    void rejectsUnsupportedWebhookEvents() {
        LeadWebhookRequest request = new LeadWebhookRequest(
                "PAGE_VIEW", "visitor-1", "lead@example.com",
                "Lead", null, null, "/", true);

        assertThrows(IllegalArgumentException.class,
                () -> service.process(request, "key", "0", "invalid", "idempotency-1"));
        verifyNoInteractions(trackingEventService, leadIdentificationService);
    }

    private String sign(String key, String timestamp) {
        try {
            String canonical = String.join("\n", timestamp, "FORM_SUBMITTED",
                    "visitor-1", "lead@example.com", "Lead", "", "Example", "/demo");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            StringBuilder result = new StringBuilder("sha256=");
            for (byte item : mac.doFinal(canonical.getBytes(StandardCharsets.UTF_8))) {
                result.append(String.format("%02x", item));
            }
            return result.toString();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
