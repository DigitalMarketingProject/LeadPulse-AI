package com.marketing.leadscore.service;

import com.marketing.leadscore.dto.LeadInsightDTO;
import com.marketing.leadscore.entity.Activity;
import com.marketing.leadscore.entity.Customer;
import com.marketing.leadscore.repository.ActivityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeadInsightServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private LeadInsightService service;

    @Test
    void identifiesCoolingLeadAndRecommendsReengagement() {
        Customer customer = new Customer();
        customer.setId(7L);
        customer.setName("Acme");
        customer.setLeadScore(72);
        customer.setLeadStatus("Warm");

        Activity oldActivity = new Activity(
                1L, "Acme", "Email Open", "Old interest",
                LocalDate.now().minusDays(12), customer);
        when(activityRepository.findByCustomer(customer)).thenReturn(List.of(oldActivity));

        LeadInsightDTO insight = service.buildInsight(customer);

        assertEquals("Cooling", insight.momentum());
        assertEquals(0, insight.recentActivities());
        assertEquals(1, insight.previousActivities());
        assertEquals("Re-engage within 48 hours with a relevant offer or personal follow-up.",
                insight.nextBestAction());
    }
}
