package com.marketing.leadscore.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="campaigns")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String campaignName;

    private String platform;

    private Double budget;

    private LocalDate startDate;

    private LocalDate endDate;

    private String status;

    private Integer leadsGenerated = 0;

    private Integer conversions = 0;

    private Double attributedRevenue = 0.0;

    public Campaign() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String type) {
        this.platform = type;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getLeadsGenerated() {
        return leadsGenerated != null ? leadsGenerated : 0;
    }

    public void setLeadsGenerated(Integer leadsGenerated) {
        this.leadsGenerated = leadsGenerated != null ? leadsGenerated : 0;
    }

    public Integer getConversions() {
        return conversions != null ? conversions : 0;
    }

    public void setConversions(Integer conversions) {
        this.conversions = conversions != null ? conversions : 0;
    }

    public Double getAttributedRevenue() {
        return attributedRevenue != null ? attributedRevenue : 0.0;
    }

    public void setAttributedRevenue(Double attributedRevenue) {
        this.attributedRevenue = attributedRevenue != null ? attributedRevenue : 0.0;
    }

}