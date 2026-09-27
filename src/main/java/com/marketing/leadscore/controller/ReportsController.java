package com.marketing.leadscore.controller;

import com.marketing.leadscore.service.ActivityService;
import com.marketing.leadscore.service.CampaignService;
import com.marketing.leadscore.service.CustomerSegmentationService;
import com.marketing.leadscore.service.CustomerService;
import com.marketing.leadscore.service.MarketingAnalyticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportsController {

    private final CustomerService customerService;
    private final CampaignService campaignService;
    private final ActivityService activityService;
    private final MarketingAnalyticsService analyticsService;
    private final CustomerSegmentationService segmentationService;

    public ReportsController(
            CustomerService customerService,
            CampaignService campaignService,
            ActivityService activityService,
            MarketingAnalyticsService analyticsService,
            CustomerSegmentationService segmentationService) {

        this.customerService = customerService;
        this.campaignService = campaignService;
        this.activityService = activityService;
        this.analyticsService = analyticsService;
        this.segmentationService = segmentationService;
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("customers", customerService.getCustomerCount());
        model.addAttribute("hot", customerService.getHotLeadCount());
        model.addAttribute("warm", customerService.getWarmLeadCount());
        model.addAttribute("cold", customerService.getColdLeadCount());
        model.addAttribute("campaigns", campaignService.getCampaignCount());
        model.addAttribute("activities", activityService.getActivityCount());
        model.addAttribute("topCustomers", customerService.getTopCustomers());
        model.addAttribute("platformStats", analyticsService.getPlatformPerformance());
        model.addAttribute("segments", segmentationService.getLeadSegments());

        return "reports";
    }
}