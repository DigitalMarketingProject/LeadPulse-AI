package com.marketing.leadscore.service;

import com.marketing.leadscore.dto.DashboardDTO;
import com.marketing.leadscore.entity.Campaign;
import com.marketing.leadscore.entity.Customer;
import com.marketing.leadscore.repository.ActivityRepository;
import com.marketing.leadscore.repository.CampaignRepository;
import com.marketing.leadscore.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final CustomerRepository customerRepository;
    private final CampaignRepository campaignRepository;
    private final ActivityRepository activityRepository;
    private final CustomerService customerService;

    public DashboardService(
            CustomerRepository customerRepository,
            CampaignRepository campaignRepository,
            ActivityRepository activityRepository,
            CustomerService customerService) {
        this.customerRepository = customerRepository;
        this.campaignRepository = campaignRepository;
        this.activityRepository = activityRepository;
        this.customerService = customerService;
    }

    public DashboardDTO getDashboardStats() {
        DashboardDTO dto = new DashboardDTO();

        long totalCustomers = customerRepository.count();
        long totalCampaigns = campaignRepository.count();
        long totalActivities = activityRepository.count();

        long hot = customerService.getHotLeadCount();
        long warm = customerService.getWarmLeadCount();
        long cold = customerService.getColdLeadCount();

        List<Customer> allCustomers = customerRepository.findAll();
        double avgScore = allCustomers.stream()
                .mapToInt(c -> c.getLeadScore() != null ? c.getLeadScore() : 0)
                .average()
                .orElse(0.0);

        List<Campaign> allCampaigns = campaignRepository.findAll();
        double totalBudget = allCampaigns.stream()
                .mapToDouble(c -> c.getBudget() != null ? c.getBudget() : 0.0)
                .sum();

        long runningCampaigns = allCampaigns.stream()
                .filter(c -> "Running".equalsIgnoreCase(c.getStatus()))
                .count();

        double conversionRate = totalCustomers > 0 ? ((double) hot / totalCustomers) * 100.0 : 0.0;

        // Platform breakdown
        Map<String, Double> platformBudgets = new HashMap<>();
        Map<String, Long> platformCampaignCounts = new HashMap<>();
        for (Campaign c : allCampaigns) {
            String platform = c.getPlatform() != null ? c.getPlatform() : "Other";
            double budget = c.getBudget() != null ? c.getBudget() : 0.0;
            platformBudgets.put(platform, platformBudgets.getOrDefault(platform, 0.0) + budget);
            platformCampaignCounts.put(platform, platformCampaignCounts.getOrDefault(platform, 0L) + 1L);
        }

        dto.setTotalCustomers(totalCustomers);
        dto.setTotalCampaigns(totalCampaigns);
        dto.setRunningCampaigns(runningCampaigns);
        dto.setTotalActivities(totalActivities);
        dto.setTotalBudget(totalBudget);
        dto.setAverageLeadScore(Math.round(avgScore * 10.0) / 10.0);
        dto.setHotLeadsCount(hot);
        dto.setWarmLeadsCount(warm);
        dto.setColdLeadsCount(cold);
        dto.setConversionRate(Math.round(conversionRate * 10.0) / 10.0);
        dto.setTopCustomers(customerService.getTopCustomers());
        dto.setRecentActivities(activityRepository.findTop10ByOrderByActivityDateDesc());
        dto.setPlatformBudgets(platformBudgets);
        dto.setPlatformCampaignCounts(platformCampaignCounts);

        return dto;
    }
}
