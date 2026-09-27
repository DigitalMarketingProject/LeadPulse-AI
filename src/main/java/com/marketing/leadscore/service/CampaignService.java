package com.marketing.leadscore.service;

import com.marketing.leadscore.entity.Campaign;
import com.marketing.leadscore.repository.CampaignRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampaignService {

    private final CampaignRepository repository;

    public CampaignService(CampaignRepository repository) {
        this.repository = repository;
    }

    public List<Campaign> getAllCampaigns() {
        return repository.findAll();
    }

    public Campaign getCampaignById(Long id) {
        if (id == null) return null;
        return repository.findById(id).orElse(null);
    }

    public Campaign saveCampaign(Campaign campaign) {
        if (campaign == null) return null;
        normalizePerformanceMetrics(campaign);
        if (campaign.getStatus() == null || campaign.getStatus().isBlank()) {
            campaign.setStatus("Scheduled");
        }
        return repository.save(campaign);
    }

    public Campaign updateCampaign(Long id, Campaign campaignData) {
        Campaign existing = getCampaignById(id);
        if (existing == null) {
            return null;
        }
        if (campaignData.getCampaignName() != null) existing.setCampaignName(campaignData.getCampaignName());
        if (campaignData.getPlatform() != null) existing.setPlatform(campaignData.getPlatform());
        if (campaignData.getBudget() != null) existing.setBudget(campaignData.getBudget());
        if (campaignData.getStartDate() != null) existing.setStartDate(campaignData.getStartDate());
        if (campaignData.getEndDate() != null) existing.setEndDate(campaignData.getEndDate());
        if (campaignData.getStatus() != null) existing.setStatus(campaignData.getStatus());
        if (campaignData.getLeadsGenerated() != null) existing.setLeadsGenerated(campaignData.getLeadsGenerated());
        if (campaignData.getConversions() != null) existing.setConversions(campaignData.getConversions());
        if (campaignData.getAttributedRevenue() != null) existing.setAttributedRevenue(campaignData.getAttributedRevenue());
        normalizePerformanceMetrics(existing);
        return repository.save(existing);
    }

    public void deleteCampaign(Long id) {
        if (id != null && repository.existsById(id)) {
            repository.deleteById(id);
        }
    }

    public long getCampaignCount() {
        return repository.count();
    }

    public long getRunningCampaignCount() {
        return repository.countByStatus("Running");
    }

    public List<Campaign> getCampaignsByStatus(String status) {
        if (status == null || status.isBlank()) {
            return repository.findAll();
        }
        return repository.findByStatus(status);
    }

    public double getTotalBudget() {
        return repository.findAll()
                .stream()
                .mapToDouble(c -> c.getBudget() == null ? 0.0 : c.getBudget())
                .sum();
    }

    private void normalizePerformanceMetrics(Campaign campaign) {
        campaign.setLeadsGenerated(Math.max(0, campaign.getLeadsGenerated()));
        campaign.setConversions(Math.max(0, Math.min(campaign.getConversions(), campaign.getLeadsGenerated())));
        campaign.setAttributedRevenue(Math.max(0.0, campaign.getAttributedRevenue()));
    }
}