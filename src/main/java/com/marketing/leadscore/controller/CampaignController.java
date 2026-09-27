package com.marketing.leadscore.controller;

import com.marketing.leadscore.entity.Campaign;
import com.marketing.leadscore.service.CampaignService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CampaignController {

    private final CampaignService service;

    public CampaignController(CampaignService service) {
        this.service = service;
    }

    @GetMapping("/campaigns")
    public String campaigns(Model model) {
        model.addAttribute("campaign", new Campaign());
        model.addAttribute("campaigns", service.getAllCampaigns());
        return "campaigns";
    }

    @PostMapping("/saveCampaign")
    public String saveCampaign(Campaign campaign) {
        service.saveCampaign(campaign);
        return "redirect:/campaigns";
    }

    @PostMapping("/deleteCampaign/{id}")
    public String deleteCampaign(@PathVariable Long id) {
        service.deleteCampaign(id);
        return "redirect:/campaigns";
    }
}