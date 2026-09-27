package com.marketing.leadscore.service;

import com.marketing.leadscore.dto.LeadIdentificationRequest;
import com.marketing.leadscore.entity.Customer;
import com.marketing.leadscore.entity.Organization;
import com.marketing.leadscore.entity.TrackingEvent;
import com.marketing.leadscore.repository.CustomerRepository;
import com.marketing.leadscore.repository.TrackingEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeadIdentificationServiceTest {

    @Mock
    private OrganizationService organizationService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TrackingEventRepository trackingEventRepository;

    @Mock
    private LeadScoringService leadScoringService;

    @InjectMocks
    private LeadIdentificationService service;

    @Test
    void linksVisitorHistoryWhenLeadIdentifies() {
        Organization organization = new Organization("Example", "hash", LocalDateTime.now());
        LeadIdentificationRequest request = new LeadIdentificationRequest(
                "visitor-1", "person@example.com", "Person", null, "Example Ltd", true);
        TrackingEvent event = new TrackingEvent(
                organization, "PAGE_VIEW", "visitor-1", "session-1", "/", null, LocalDateTime.now());

        when(organizationService.authenticate("key")).thenReturn(organization);
        when(customerRepository.findByOrganizationIdAndEmailIgnoreCase(null, "person@example.com"))
                .thenReturn(Optional.empty());
        when(trackingEventRepository
                .findByOrganizationIdAndVisitorIdOrderByOccurredAtDesc(null, "visitor-1"))
                .thenReturn(List.of(event));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            customer.setId(4L);
            return customer;
        });

        Customer customer = service.identify(request, "key");

        assertEquals("person@example.com", customer.getEmail());
        assertEquals(1, customer.getWebsiteVisits());
        assertEquals(customer, event.getCustomer());
        verify(trackingEventRepository).saveAll(List.of(event));
        verify(leadScoringService).applyScoreAndStatus(customer);
    }

    @Test
    void rejectsIdentificationWithoutConsent() {
        LeadIdentificationRequest request = new LeadIdentificationRequest(
                "visitor-1", "person@example.com", "Person", null, null, false);

        assertThrows(IllegalArgumentException.class, () -> service.identify(request, "key"));
        verifyNoInteractions(organizationService, customerRepository, trackingEventRepository);
    }
}
