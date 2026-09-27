package com.marketing.leadscore.service;

import com.marketing.leadscore.dto.LeadIdentificationRequest;
import com.marketing.leadscore.entity.Customer;
import com.marketing.leadscore.entity.Organization;
import com.marketing.leadscore.entity.TrackingEvent;
import com.marketing.leadscore.repository.CustomerRepository;
import com.marketing.leadscore.repository.TrackingEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class LeadIdentificationService {

    private final OrganizationService organizationService;
    private final CustomerRepository customerRepository;
    private final TrackingEventRepository trackingEventRepository;
    private final LeadScoringService leadScoringService;

    public LeadIdentificationService(
            OrganizationService organizationService,
            CustomerRepository customerRepository,
            TrackingEventRepository trackingEventRepository,
            LeadScoringService leadScoringService) {
        this.organizationService = organizationService;
        this.customerRepository = customerRepository;
        this.trackingEventRepository = trackingEventRepository;
        this.leadScoringService = leadScoringService;
    }

    @Transactional
    public Customer identify(LeadIdentificationRequest request, String apiKey) {
        if (!request.consentGiven()) {
            throw new IllegalArgumentException("Identification consent is required.");
        }

        Organization organization = organizationService.authenticate(apiKey);
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        Customer customer = customerRepository
                .findByOrganizationIdAndEmailIgnoreCase(organization.getId(), email)
                .orElseGet(Customer::new);

        customer.setOrganization(organization);
        customer.setName(request.name().trim());
        customer.setEmail(email);
        customer.setPhone(blankToNull(request.phone()));
        customer.setCompany(blankToNull(request.company()));

        List<TrackingEvent> events = trackingEventRepository
                .findByOrganizationIdAndVisitorIdOrderByOccurredAtDesc(
                        organization.getId(), request.visitorId().trim());
        customer.setWebsiteVisits((int) events.stream()
                .filter(event -> "PAGE_VIEW".equals(event.getEventType())
                        || "PRODUCT_VIEW".equals(event.getEventType()))
                .count());
        customer.setFormSubmissions((int) events.stream()
                .filter(event -> "FORM_SUBMITTED".equals(event.getEventType())
                        || "DEMO_REQUESTED".equals(event.getEventType()))
                .count());
        events.forEach(event -> event.assignCustomer(customer));
        trackingEventRepository.saveAll(events);

        leadScoringService.applyScoreAndStatus(customer);
        return customerRepository.save(customer);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
