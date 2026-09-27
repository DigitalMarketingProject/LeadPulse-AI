package com.marketing.leadscore.dto;

import com.marketing.leadscore.entity.Activity;
import com.marketing.leadscore.entity.Customer;

import java.util.List;
import java.util.Map;

public class DashboardDTO {

    private long totalCustomers;
    private long totalCampaigns;
    private long runningCampaigns;
    private long totalActivities;
    private double totalBudget;
    private double averageLeadScore;
    private long hotLeadsCount;
    private long warmLeadsCount;
    private long coldLeadsCount;
    private double conversionRate;
    private List<Customer> topCustomers;
    private List<Activity> recentActivities;
    private Map<String, Double> platformBudgets;
    private Map<String, Long> platformCampaignCounts;

    public DashboardDTO() {
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public long getTotalCampaigns() {
        return totalCampaigns;
    }

    public void setTotalCampaigns(long totalCampaigns) {
        this.totalCampaigns = totalCampaigns;
    }

    public long getRunningCampaigns() {
        return runningCampaigns;
    }

    public void setRunningCampaigns(long runningCampaigns) {
        this.runningCampaigns = runningCampaigns;
    }

    public long getTotalActivities() {
        return totalActivities;
    }

    public void setTotalActivities(long totalActivities) {
        this.totalActivities = totalActivities;
    }

    public double getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(double totalBudget) {
        this.totalBudget = totalBudget;
    }

    public double getAverageLeadScore() {
        return averageLeadScore;
    }

    public void setAverageLeadScore(double averageLeadScore) {
        this.averageLeadScore = averageLeadScore;
    }

    public long getHotLeadsCount() {
        return hotLeadsCount;
    }

    public void setHotLeadsCount(long hotLeadsCount) {
        this.hotLeadsCount = hotLeadsCount;
    }

    public long getWarmLeadsCount() {
        return warmLeadsCount;
    }

    public void setWarmLeadsCount(long warmLeadsCount) {
        this.warmLeadsCount = warmLeadsCount;
    }

    public long getColdLeadsCount() {
        return coldLeadsCount;
    }

    public void setColdLeadsCount(long coldLeadsCount) {
        this.coldLeadsCount = coldLeadsCount;
    }

    public double getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(double conversionRate) {
        this.conversionRate = conversionRate;
    }

    public List<Customer> getTopCustomers() {
        return topCustomers;
    }

    public void setTopCustomers(List<Customer> topCustomers) {
        this.topCustomers = topCustomers;
    }

    public List<Activity> getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(List<Activity> recentActivities) {
        this.recentActivities = recentActivities;
    }

    public Map<String, Double> getPlatformBudgets() {
        return platformBudgets;
    }

    public void setPlatformBudgets(Map<String, Double> platformBudgets) {
        this.platformBudgets = platformBudgets;
    }

    public Map<String, Long> getPlatformCampaignCounts() {
        return platformCampaignCounts;
    }

    public void setPlatformCampaignCounts(Map<String, Long> platformCampaignCounts) {
        this.platformCampaignCounts = platformCampaignCounts;
    }
}
