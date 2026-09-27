package com.marketing.leadscore.controller;

import com.marketing.leadscore.dto.DashboardDTO;
import com.marketing.leadscore.dto.LeadInsightDTO;
import com.marketing.leadscore.dto.TrackingAnalyticsDTO;
import com.marketing.leadscore.entity.Customer;
import com.marketing.leadscore.exception.ResourceNotFoundException;
import com.marketing.leadscore.service.CustomerSegmentationService;
import com.marketing.leadscore.service.CustomerService;
import com.marketing.leadscore.service.DashboardService;
import com.marketing.leadscore.service.LeadInsightService;
import com.marketing.leadscore.service.MarketingAnalyticsService;
import com.marketing.leadscore.service.TrackingAnalyticsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class AnalyticsRestController {

    private final DashboardService dashboardService;
    private final MarketingAnalyticsService analyticsService;
    private final CustomerSegmentationService segmentationService;
    private final CustomerService customerService;
    private final LeadInsightService leadInsightService;
    private final TrackingAnalyticsService trackingAnalyticsService;

    public AnalyticsRestController(
            DashboardService dashboardService,
            MarketingAnalyticsService analyticsService,
            CustomerSegmentationService segmentationService,
            CustomerService customerService,
            LeadInsightService leadInsightService,
            TrackingAnalyticsService trackingAnalyticsService) {
        this.dashboardService = dashboardService;
        this.analyticsService = analyticsService;
        this.segmentationService = segmentationService;
        this.customerService = customerService;
        this.leadInsightService = leadInsightService;
        this.trackingAnalyticsService = trackingAnalyticsService;
    }

    @GetMapping({"/api/dashboard/stats", "/api/dashboard"})
    public DashboardDTO getDashboardStats() {
        return dashboardService.getDashboardStats();
    }

    @GetMapping("/api/analytics/platforms")
    public List<Map<String, Object>> getPlatformPerformance() {
        return analyticsService.getPlatformPerformance();
    }

    @GetMapping("/api/analytics/funnel")
    public Map<String, Object> getConversionFunnel() {
        return analyticsService.getConversionFunnel();
    }

    @GetMapping("/api/analytics/segments")
    public List<CustomerSegmentationService.SegmentSummary> getLeadSegments() {
        return segmentationService.getLeadSegments();
    }

    @GetMapping("/api/analytics/customer/{id}/recommendation")
    public Map<String, String> getCustomerRecommendation(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        if (customer == null) {
            throw new ResourceNotFoundException("Customer with ID " + id + " not found.");
        }
        String recommendation = segmentationService.getRecommendationForCustomer(customer);
        return Map.of(
                "customerId", String.valueOf(id),
                "customerName", customer.getName() != null ? customer.getName() : "",
                "leadScore", String.valueOf(customer.getLeadScore()),
                "leadStatus", customer.getLeadStatus() != null ? customer.getLeadStatus() : "Cold",
                "recommendation", recommendation
        );
    }

    @GetMapping("/api/analytics/customer/{id}/insights")
    public LeadInsightDTO getCustomerInsights(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        if (customer == null) {
            throw new ResourceNotFoundException("Customer with ID " + id + " not found.");
        }
        return leadInsightService.buildInsight(customer);
    }

    @GetMapping("/api/analytics/tracking")
    public TrackingAnalyticsDTO getTrackingAnalytics(
            @RequestHeader(name = "X-LeadPulse-Api-Key", required = false) String apiKey) {
        return trackingAnalyticsService.summarize(apiKey);
    }
}
