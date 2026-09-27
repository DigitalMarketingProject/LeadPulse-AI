package com.marketing.leadscore.service;

import com.marketing.leadscore.entity.Customer;
import com.marketing.leadscore.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerSegmentationService {

    private final CustomerRepository customerRepository;

    public CustomerSegmentationService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public static class SegmentSummary {
        private String segmentName;
        private String description;
        private String recommendedAction;
        private List<Customer> customers;
        private int count;

        public SegmentSummary(String segmentName, String description, String recommendedAction, List<Customer> customers) {
            this.segmentName = segmentName;
            this.description = description;
            this.recommendedAction = recommendedAction;
            this.customers = customers;
            this.count = customers != null ? customers.size() : 0;
        }

        public String getSegmentName() { return segmentName; }
        public String getDescription() { return description; }
        public String getRecommendedAction() { return recommendedAction; }
        public List<Customer> getCustomers() { return customers; }
        public int getCount() { return count; }
    }

    /**
     * Segments all customers based on lead score and engagement level,
     * providing actionable marketing next-steps for each segment.
     */
    public List<SegmentSummary> getLeadSegments() {
        List<Customer> all = customerRepository.findAll();

        List<Customer> enterpriseQualified = new ArrayList<>();
        List<Customer> warmNurturing = new ArrayList<>();
        List<Customer> coldLeads = new ArrayList<>();

        for (Customer c : all) {
            int score = c.getLeadScore() != null ? c.getLeadScore() : 0;
            if (score >= 80) {
                enterpriseQualified.add(c);
            } else if (score >= 50) {
                warmNurturing.add(c);
            } else {
                coldLeads.add(c);
            }
        }

        return List.of(
                new SegmentSummary(
                        "Sales-Ready Hot Leads",
                        "High score prospects with high intent and verified engagement.",
                        "Priority Outreach: Assign to sales rep immediately for personalized product demo.",
                        enterpriseQualified
                ),
                new SegmentSummary(
                        "Warm Nurture Pipeline",
                        "Moderate score leads showing active interest through content and visits.",
                        "Nurturing: Enroll in weekly automated educational newsletter and case study campaigns.",
                        warmNurturing
                ),
                new SegmentSummary(
                        "Cold Awareness Leads",
                        "Early-stage leads with minimal interactions requiring brand awareness.",
                        "Re-engagement: Launch multi-channel retargeting ads and introductory value offer.",
                        coldLeads
                )
        );
    }

    /**
     * Get automated recommendation for an individual customer.
     */
    public String getRecommendationForCustomer(Customer customer) {
        if (customer == null) return "No customer data available.";
        int score = customer.getLeadScore() != null ? customer.getLeadScore() : 0;

        if (score >= 80) {
            return "🔥 Hot Prospect: High conversion probability. Schedule an executive demo call.";
        } else if (score >= 50) {
            return "🟡 Warm Prospect: Send follow-up case study and enroll in automated drip sequence.";
        } else {
            return "🟢 Cold Lead: Retarget with top-of-funnel social media ads and product overview.";
        }
    }
}
