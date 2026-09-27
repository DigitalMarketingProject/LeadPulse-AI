package com.marketing.leadscore.service;

import com.marketing.leadscore.entity.Campaign;
import com.marketing.leadscore.repository.CampaignRepository;
import com.marketing.leadscore.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MarketingAnalyticsService {

    private final CampaignRepository campaignRepository;
    private final CustomerRepository customerRepository;

    public MarketingAnalyticsService(
            CampaignRepository campaignRepository,
            CustomerRepository customerRepository) {
        this.campaignRepository = campaignRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Calculates marketing channel performance across platforms (budget, campaigns, average spend).
     */
    public List<Map<String, Object>> getPlatformPerformance() {
        List<Campaign> campaigns = campaignRepository.findAll();
        Map<String, List<Campaign>> byPlatform = new HashMap<>();

        for (Campaign c : campaigns) {
            String platform = c.getPlatform() != null ? c.getPlatform() : "Unassigned";
            byPlatform.computeIfAbsent(platform, k -> new ArrayList<>()).add(c);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<Campaign>> entry : byPlatform.entrySet()) {
            String platform = entry.getKey();
            List<Campaign> list = entry.getValue();
            double totalBudget = list.stream()
                    .mapToDouble(c -> c.getBudget() != null ? c.getBudget() : 0.0)
                    .sum();
            long runningCount = list.stream()
                    .filter(c -> "Running".equalsIgnoreCase(c.getStatus()))
                    .count();

            Map<String, Object> platformStat = new HashMap<>();
            platformStat.put("platform", platform);
            platformStat.put("campaignCount", list.size());
            platformStat.put("runningCount", runningCount);
            platformStat.put("totalBudget", totalBudget);
            platformStat.put("averageBudget", list.isEmpty() ? 0.0 : totalBudget / list.size());
            result.add(platformStat);
        }

        result.sort((a, b) -> Double.compare(
                (Double) b.get("totalBudget"),
                (Double) a.get("totalBudget")
        ));

        return result;
    }

    /**
     * Computes conversion funnel metrics: Total Leads -> Warm Engaged -> Hot Converted.
     */
    public Map<String, Object> getConversionFunnel() {
        long total = customerRepository.count();
        long hot = customerRepository.countByLeadScoreGreaterThanEqual(80);
        long warm = customerRepository.countByLeadScoreBetween(50, 79);
        long cold = customerRepository.countByLeadScoreLessThan(50);

        Map<String, Object> funnel = new LinkedHashMap<>();
        funnel.put("totalLeads", total);
        funnel.put("coldAwareness", cold);
        funnel.put("warmEngagement", warm);
        funnel.put("hotQualified", hot);
        funnel.put("qualificationRate", total > 0 ? Math.round(((double) hot / total) * 1000.0) / 10.0 : 0.0);

        return funnel;
    }
}
