package com.marketing.leadscore.dto;

import java.util.List;

public record LeadInsightDTO(
        Long customerId,
        String customerName,
        int currentScore,
        String leadStatus,
        String momentum,
        int recentActivities,
        int previousActivities,
        List<ScoreFactor> scoreFactors,
        String nextBestAction
) {
    public record ScoreFactor(String factor, int points, String explanation) {
    }
}
