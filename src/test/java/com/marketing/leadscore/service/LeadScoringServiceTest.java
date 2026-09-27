package com.marketing.leadscore.service;

import com.marketing.leadscore.entity.Activity;
import com.marketing.leadscore.entity.Customer;
import com.marketing.leadscore.repository.ActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeadScoringServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private LeadScoringService leadScoringService;

    private Customer sampleCustomer;

    @BeforeEach
    void setUp() {
        sampleCustomer = new Customer();
        sampleCustomer.setId(1L);
        sampleCustomer.setName("Alice Smith");
        sampleCustomer.setEmail("alice@acmecorp.com"); // Corporate domain
        sampleCustomer.setPhone("+1234567890");
        sampleCustomer.setCompany("Acme Corp");
    }

    @Test
    void testDemographicScoringOnly() {
        when(activityRepository.findByCustomer(sampleCustomer)).thenReturn(List.of());

        int score = leadScoringService.calculateScore(sampleCustomer);
        // Name(5) + Phone(10) + Company(15) + CorporateEmail(15) = 45 -> Cold (<50)
        assertEquals(45, score);
        assertEquals("Cold", leadScoringService.getLeadStatus(score));
    }

    @Test
    void testScoringWithDigitalEngagement() {
        sampleCustomer.setEmailClicks(2);      // min(10, 15) = 10
        sampleCustomer.setWebsiteVisits(3);    // min(9, 12) = 9
        sampleCustomer.setFormSubmissions(1);  // min(10, 20) = 10
        sampleCustomer.setSocialMediaClicks(2);// min(4, 8) = 4
        // Demographic = 45, Engagement = 10 + 9 + 10 + 4 = 33 -> Total = 78 (Warm)

        when(activityRepository.findByCustomer(sampleCustomer)).thenReturn(List.of());

        int score = leadScoringService.calculateScore(sampleCustomer);
        assertEquals(78, score);
        assertEquals("Warm", leadScoringService.getLeadStatus(score));
    }

    @Test
    void testScoringWithHighImpactActivitiesReachesHot() {
        sampleCustomer.setEmailClicks(2);
        sampleCustomer.setWebsiteVisits(3);

        Activity meeting = new Activity(101L, "Alice Smith", "Meeting", "Discussion", LocalDate.now(), sampleCustomer);
        Activity demo = new Activity(102L, "Alice Smith", "Demo", "Product walkthrough", LocalDate.now(), sampleCustomer);

        when(activityRepository.findByCustomer(sampleCustomer)).thenReturn(List.of(meeting, demo));

        int score = leadScoringService.calculateScore(sampleCustomer);
        assertTrue(score >= 80, "Score should qualify as Hot lead: " + score);
        assertEquals("Hot", leadScoringService.getLeadStatus(score));
    }

    @Test
    void testNullCustomerSafety() {
        assertDoesNotThrow(() -> {
            int score = leadScoringService.calculateScore(null);
            assertEquals(0, score);
            assertEquals("Cold", leadScoringService.getLeadStatus(score));
        });
    }

    @Test
    void testGenericEmailDomainScoring() {
        sampleCustomer.setEmail("alice@gmail.com");
        when(activityRepository.findByCustomer(sampleCustomer)).thenReturn(List.of());

        int score = leadScoringService.calculateScore(sampleCustomer);
        // Name(5) + Phone(10) + Company(15) + GenericEmail(5) = 35
        assertEquals(35, score);
    }
}
