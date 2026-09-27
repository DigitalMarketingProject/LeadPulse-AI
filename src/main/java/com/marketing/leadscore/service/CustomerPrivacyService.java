package com.marketing.leadscore.service;

import com.marketing.leadscore.dto.CustomerPrivacyExportDTO;
import com.marketing.leadscore.entity.Customer;
import com.marketing.leadscore.entity.Organization;
import com.marketing.leadscore.exception.ResourceNotFoundException;
import com.marketing.leadscore.repository.ActivityRepository;
import com.marketing.leadscore.repository.CustomerRepository;
import com.marketing.leadscore.repository.TrackingEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerPrivacyService {

    private final OrganizationService organizationService;
    private final CustomerRepository customerRepository;
    private final ActivityRepository activityRepository;
    private final TrackingEventRepository trackingEventRepository;

    public CustomerPrivacyService(
            OrganizationService organizationService,
            CustomerRepository customerRepository,
            ActivityRepository activityRepository,
            TrackingEventRepository trackingEventRepository) {
        this.organizationService = organizationService;
        this.customerRepository = customerRepository;
        this.activityRepository = activityRepository;
        this.trackingEventRepository = trackingEventRepository;
    }

    @Transactional(readOnly = true)
    public CustomerPrivacyExportDTO exportCustomer(Long customerId, String apiKey) {
        Organization organization = organizationService.authenticate(apiKey);
        Customer customer = findCustomer(organization, customerId);
        var activities = activityRepository.findByCustomerId(customerId).stream()
                .map(activity -> new CustomerPrivacyExportDTO.ActivityExport(
                        activity.getActivityType(), activity.getRemarks(), activity.getActivityDate()))
                .toList();
        var events = trackingEventRepository
                .findByOrganizationIdAndCustomerId(organization.getId(), customerId).stream()
                .map(event -> new CustomerPrivacyExportDTO.TrackingExport(
                        event.getEventType(), event.getPageUrl(), event.getReferrer(), event.getOccurredAt()))
                .toList();
        return new CustomerPrivacyExportDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getCompany(),
                customer.getLeadScore(),
                customer.getLeadStatus(),
                activities,
                events
        );
    }

    @Transactional
    public void eraseCustomer(Long customerId, String apiKey) {
        Organization organization = organizationService.authenticate(apiKey);
        findCustomer(organization, customerId);
        trackingEventRepository.deleteByOrganizationIdAndCustomerId(organization.getId(), customerId);
        activityRepository.deleteByCustomerId(customerId);
        customerRepository.deleteById(customerId);
    }

    private Customer findCustomer(Organization organization, Long customerId) {
        return customerRepository.findByOrganizationIdAndId(organization.getId(), customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer with ID " + customerId + " not found for this organization."));
    }
}
