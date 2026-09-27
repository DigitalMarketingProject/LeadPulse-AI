package com.marketing.leadscore.service;

import com.marketing.leadscore.entity.Activity;
import com.marketing.leadscore.entity.Customer;
import com.marketing.leadscore.repository.ActivityRepository;
import com.marketing.leadscore.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final CustomerRepository customerRepository;
    private final LeadScoringService leadScoringService;

    public ActivityService(
            ActivityRepository activityRepository,
            CustomerRepository customerRepository,
            LeadScoringService leadScoringService) {

        this.activityRepository = activityRepository;
        this.customerRepository = customerRepository;
        this.leadScoringService = leadScoringService;
    }

    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    public Activity getActivityById(Long id) {
        if (id == null) return null;
        return activityRepository.findById(id).orElse(null);
    }

    public List<Activity> getActivitiesByCustomerId(Long customerId) {
        if (customerId == null) return List.of();
        return activityRepository.findByCustomerId(customerId);
    }

    public List<Activity> getRecentActivities() {
        List<Activity> recent = activityRepository.findTop10ByOrderByActivityDateDesc();
        return recent != null ? recent : List.of();
    }

    public long getActivityCount() {
        return activityRepository.count();
    }

    public Activity saveActivity(Activity activity) {
        if (activity == null) return null;

        // Auto-link customer if only customerName was supplied
        if (activity.getCustomer() == null && activity.getCustomerName() != null && !activity.getCustomerName().isBlank()) {
            List<Customer> matched = customerRepository.findByNameContainingIgnoreCase(activity.getCustomerName().trim());
            if (!matched.isEmpty()) {
                activity.setCustomer(matched.get(0));
            }
        }

        // Sync customerName if customer object is present
        if (activity.getCustomer() != null) {
            Customer cust = activity.getCustomer();
            if (activity.getCustomerName() == null || activity.getCustomerName().isBlank()) {
                activity.setCustomerName(cust.getName());
            }
        }

        Activity saved = activityRepository.save(activity);

        // Recalculate lead score for customer
        if (saved.getCustomer() != null) {
            Customer customer = customerRepository.findById(saved.getCustomer().getId()).orElse(saved.getCustomer());
            leadScoringService.applyScoreAndStatus(customer);
            customerRepository.save(customer);
        }

        return saved;
    }

    public void deleteActivity(Long id) {
        if (id == null) return;
        Activity activity = activityRepository.findById(id).orElse(null);
        if (activity != null) {
            Customer customer = activity.getCustomer();
            // Critical fix: delete from activityRepository, NOT customerRepository!
            activityRepository.deleteById(id);

            // Recompute customer score after activity is removed
            if (customer != null) {
                Customer refreshedCustomer = customerRepository.findById(customer.getId()).orElse(null);
                if (refreshedCustomer != null) {
                    leadScoringService.applyScoreAndStatus(refreshedCustomer);
                    customerRepository.save(refreshedCustomer);
                }
            }
        }
    }
}