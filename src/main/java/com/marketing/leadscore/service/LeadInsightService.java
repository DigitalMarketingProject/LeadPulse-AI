package com.marketing.leadscore.service;

import com.marketing.leadscore.dto.LeadInsightDTO;
import com.marketing.leadscore.entity.Activity;
import com.marketing.leadscore.entity.Customer;
import com.marketing.leadscore.repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class LeadInsightService {

    private final ActivityRepository activityRepository;

    public LeadInsightService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public LeadInsightDTO buildInsight(Customer customer) {
        List<Activity> activities = customer.getId() == null
                ? List.of()
                : activityRepository.findByCustomer(customer);
        LocalDate today = LocalDate.now();
        int recent = countActivitiesSince(activities, today.minusDays(7), today);
        int previous = countActivitiesSince(activities, today.minusDays(30), today.minusDays(8));

        String momentum = determineMomentum(recent, previous);
        List<LeadInsightDTO.ScoreFactor> factors = buildFactors(customer, activities);

        return new LeadInsightDTO(
                customer.getId(),
                customer.getName(),
                customer.getLeadScore(),
                customer.getLeadStatus(),
                momentum,
                recent,
                previous,
                factors,
                nextBestAction(customer, momentum)
        );
    }

    private List<LeadInsightDTO.ScoreFactor> buildFactors(Customer customer, List<Activity> activities) {
        List<LeadInsightDTO.ScoreFactor> factors = new ArrayList<>();
        int profilePoints = 0;
        if (hasText(customer.getName())) profilePoints += 5;
        if (hasText(customer.getPhone())) profilePoints += 10;
        if (hasText(customer.getCompany())) profilePoints += 15;
        if (hasText(customer.getEmail())) profilePoints += isCorporateEmail(customer.getEmail()) ? 15 : 5;
        if (profilePoints > 0) {
            factors.add(new LeadInsightDTO.ScoreFactor(
                    "Profile quality", profilePoints, "Completeness and business email quality"));
        }

        int engagementPoints = Math.min(customer.getEmailClicks() * 5, 15)
                + Math.min(customer.getWebsiteVisits() * 3, 12)
                + Math.min(customer.getFormSubmissions() * 10, 20)
                + Math.min(customer.getSocialMediaClicks() * 2, 8);
        if (engagementPoints > 0) {
            factors.add(new LeadInsightDTO.ScoreFactor(
                    "Digital engagement", engagementPoints, "Email, website, form and social interactions"));
        }

        int activityPoints = activities.stream()
                .mapToInt(activity -> activityPoints(activity.getActivityType()))
                .sum();
        if (activityPoints > 0) {
            factors.add(new LeadInsightDTO.ScoreFactor(
                    "Human touchpoints", Math.min(activityPoints, 40),
                    "Logged calls, demos, meetings and campaign responses"));
        }
        factors.sort(Comparator.comparingInt(LeadInsightDTO.ScoreFactor::points).reversed());
        return factors;
    }

    private String determineMomentum(int recent, int previous) {
        if (recent > previous) return "Rising";
        if (recent == 0 && previous > 0) return "Cooling";
        return "Stable";
    }

    private String nextBestAction(Customer customer, String momentum) {
        if ("Cooling".equals(momentum)) {
            return "Re-engage within 48 hours with a relevant offer or personal follow-up.";
        }
        if (customer.getLeadScore() >= 80) {
            return "Prioritize sales outreach and schedule a personalized demo.";
        }
        if (customer.getLeadScore() >= 50) {
            return "Send a proof point or case study matched to the prospect's interest.";
        }
        return "Use an educational campaign to build awareness before sales outreach.";
    }

    private int countActivitiesSince(List<Activity> activities, LocalDate from, LocalDate to) {
        return (int) activities.stream()
                .filter(activity -> activity.getActivityDate() != null)
                .filter(activity -> !activity.getActivityDate().isBefore(from)
                        && !activity.getActivityDate().isAfter(to))
                .count();
    }

    private int activityPoints(String activityType) {
        if (activityType == null) return 0;
        return switch (activityType.trim()) {
            case "Email Open", "Email" -> 5;
            case "Link Click" -> 10;
            case "Form Filled", "Follow Up" -> 15;
            case "Phone Call" -> 20;
            case "Campaign Response", "Demo" -> 20;
            case "Meeting" -> 30;
            default -> 5;
        };
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private boolean isCorporateEmail(String email) {
        String normalized = email.trim().toLowerCase();
        return !normalized.endsWith("@gmail.com")
                && !normalized.endsWith("@yahoo.com")
                && !normalized.endsWith("@hotmail.com")
                && !normalized.endsWith("@outlook.com")
                && !normalized.endsWith("@icloud.com");
    }
}
