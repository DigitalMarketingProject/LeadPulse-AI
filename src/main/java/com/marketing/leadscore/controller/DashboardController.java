package com.marketing.leadscore.controller;

import com.marketing.leadscore.dto.DashboardDTO;
import com.marketing.leadscore.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        DashboardDTO stats = dashboardService.getDashboardStats();

        model.addAttribute("customers", stats.getTotalCustomers());
        model.addAttribute("campaigns", stats.getTotalCampaigns());
        model.addAttribute("activities", stats.getTotalActivities());
        model.addAttribute("budget", stats.getTotalBudget());
        model.addAttribute("averageLead", stats.getAverageLeadScore());
        model.addAttribute("runningCampaigns", stats.getRunningCampaigns());
        model.addAttribute("hot", stats.getHotLeadsCount());
        model.addAttribute("warm", stats.getWarmLeadsCount());
        model.addAttribute("cold", stats.getColdLeadsCount());
        model.addAttribute("conversionRate", stats.getConversionRate());
        model.addAttribute("topCustomers", stats.getTopCustomers());
        model.addAttribute("recentCustomers", stats.getTopCustomers());
        model.addAttribute("activitiesList", stats.getRecentActivities());
        model.addAttribute("stats", stats);

        return "dashboard";
    }

    @GetMapping("/integrations")
    public String integrations() {
        return "integrations";
    }
}