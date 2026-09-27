package com.marketing.leadscore.controller;

import com.marketing.leadscore.entity.Campaign;
import com.marketing.leadscore.exception.ResourceNotFoundException;
import com.marketing.leadscore.service.CampaignService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignRestController {

    private final CampaignService campaignService;

    public CampaignRestController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @GetMapping
    public List<Campaign> getAllCampaigns(@RequestParam(required = false) String status) {
        if (status != null && !status.isBlank()) {
            return campaignService.getCampaignsByStatus(status);
        }
        return campaignService.getAllCampaigns();
    }

    @GetMapping("/{id}")
    public Campaign getCampaignById(@PathVariable Long id) {
        Campaign campaign = campaignService.getCampaignById(id);
        if (campaign == null) {
            throw new ResourceNotFoundException("Campaign with ID " + id + " not found.");
        }
        return campaign;
    }

    @PostMapping
    public ResponseEntity<Campaign> createCampaign(@RequestBody Campaign campaign) {
        Campaign saved = campaignService.saveCampaign(campaign);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public Campaign updateCampaign(@PathVariable Long id, @RequestBody Campaign campaign) {
        Campaign updated = campaignService.updateCampaign(id, campaign);
        if (updated == null) {
            throw new ResourceNotFoundException("Campaign with ID " + id + " not found.");
        }
        return updated;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long id) {
        Campaign existing = campaignService.getCampaignById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Campaign with ID " + id + " not found.");
        }
        campaignService.deleteCampaign(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/performance")
    public java.util.Map<String, Object> getPerformance(@PathVariable Long id) {
        Campaign campaign = campaignService.getCampaignById(id);
        if (campaign == null) {
            throw new ResourceNotFoundException("Campaign with ID " + id + " not found.");
        }
        double budget = campaign.getBudget() == null ? 0.0 : campaign.getBudget();
        double revenue = campaign.getAttributedRevenue();
        double roi = budget > 0 ? ((revenue - budget) / budget) * 100.0 : 0.0;
        double costPerLead = campaign.getLeadsGenerated() > 0
                ? budget / campaign.getLeadsGenerated() : 0.0;
        double conversionRate = campaign.getLeadsGenerated() > 0
                ? ((double) campaign.getConversions() / campaign.getLeadsGenerated()) * 100.0 : 0.0;
        java.util.Map<String, Object> performance = new java.util.LinkedHashMap<>();
        performance.put("campaignId", id);
        performance.put("campaignName", campaign.getCampaignName() != null ? campaign.getCampaignName() : "");
        performance.put("platform", campaign.getPlatform() != null ? campaign.getPlatform() : "Unassigned");
        performance.put("budget", budget);
        performance.put("attributedRevenue", revenue);
        performance.put("roiPercent", round(roi));
        performance.put("costPerLead", round(costPerLead));
        performance.put("conversionRate", round(conversionRate));
        performance.put("recommendation", recommendation(roi, conversionRate));
        return performance;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private String recommendation(double roi, double conversionRate) {
        if (roi >= 100 && conversionRate >= 10) return "Scale budget: strong return and conversion efficiency.";
        if (roi < 0) return "Review targeting and creative before increasing spend.";
        if (conversionRate < 5) return "Improve landing-page and lead qualification experience.";
        return "Maintain spend and run a controlled creative or audience test.";
    }
}
