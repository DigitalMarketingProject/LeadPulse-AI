package com.marketing.leadscore.service;

import com.marketing.leadscore.entity.Activity;
import com.marketing.leadscore.entity.Customer;
import com.marketing.leadscore.repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeadScoringService {

    private final ActivityRepository activityRepository;

    public LeadScoringService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    /**
     * Calculates the unified lead score (0 - 100) combining demographic profile,
     * engagement metrics, and logged marketing activity touchpoints.
     */
    public int calculateScore(Customer customer) {
        if (customer == null) {
            return 0;
        }

        int score = 0;

        // 1. Demographic & Profile Completeness (up to 40 pts)
        if (customer.getName() != null && !customer.getName().isBlank()) {
            score += 5;
        }
        if (customer.getPhone() != null && !customer.getPhone().isBlank()) {
            score += 10;
        }
        if (customer.getCompany() != null && !customer.getCompany().isBlank()) {
            score += 15;
        }
        if (customer.getEmail() != null && !customer.getEmail().isBlank()) {
            String email = customer.getEmail().trim().toLowerCase();
            if (isCorporateEmail(email)) {
                score += 15;
            } else {
                score += 5;
            }
        }

        // 2. Digital Engagement Metrics (up to 35 pts)
        int emailClicks = customer.getEmailClicks() != null ? customer.getEmailClicks() : 0;
        int websiteVisits = customer.getWebsiteVisits() != null ? customer.getWebsiteVisits() : 0;
        int formSubmissions = customer.getFormSubmissions() != null ? customer.getFormSubmissions() : 0;
        int socialClicks = customer.getSocialMediaClicks() != null ? customer.getSocialMediaClicks() : 0;

        score += Math.min(emailClicks * 5, 15);
        score += Math.min(websiteVisits * 3, 12);
        score += Math.min(formSubmissions * 10, 20);
        score += Math.min(socialClicks * 2, 8);

        // 3. Behavioral Touchpoint Activities (up to 40 pts)
        if (customer.getId() != null) {
            List<Activity> activities = activityRepository.findByCustomer(customer);
            if (activities != null) {
                int activityScore = 0;
                for (Activity a : activities) {
                    if (a.getActivityType() == null) continue;
                    String type = a.getActivityType().trim();
                    switch (type) {
                        case "Email Open":
                        case "Email":
                            activityScore += 5;
                            break;
                        case "Link Click":
                            activityScore += 10;
                            break;
                        case "Form Filled":
                        case "Follow Up":
                            activityScore += 15;
                            break;
                        case "Phone Call":
                            activityScore += 20;
                            break;
                        case "Campaign Response":
                            activityScore += 20;
                            break;
                        case "Demo":
                            activityScore += 25;
                            break;
                        case "Meeting":
                            activityScore += 30;
                            break;
                        default:
                            activityScore += 5;
                            break;
                    }
                }
                score += Math.min(activityScore, 40);
            }
        }

        return Math.min(Math.max(score, 0), 100);
    }

    /**
     * Determines lead status category based on numerical score.
     */
    public String getLeadStatus(int score) {
        if (score >= 80) {
            return "Hot";
        } else if (score >= 50) {
            return "Warm";
        } else {
            return "Cold";
        }
    }

    /**
     * Applies score and status directly onto the customer instance.
     */
    public void applyScoreAndStatus(Customer customer) {
        if (customer == null) return;
        int score = calculateScore(customer);
        customer.setLeadScore(score);
        customer.setLeadStatus(getLeadStatus(score));
    }

    private boolean isCorporateEmail(String email) {
        return !email.endsWith("@gmail.com") &&
                !email.endsWith("@yahoo.com") &&
                !email.endsWith("@hotmail.com") &&
                !email.endsWith("@outlook.com") &&
                !email.endsWith("@icloud.com");
    }
}