package com.marketing.leadscore.service;

import com.marketing.leadscore.dto.DashboardDTO;
import com.marketing.leadscore.entity.Campaign;
import com.marketing.leadscore.entity.Customer;
import com.marketing.leadscore.repository.ActivityRepository;
import com.marketing.leadscore.repository.CampaignRepository;
import com.marketing.leadscore.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarketingServicesIntegrationTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void testDashboardStatsAggregation() {
        when(customerRepository.count()).thenReturn(10L);
        when(campaignRepository.count()).thenReturn(3L);
        when(activityRepository.count()).thenReturn(15L);

        when(customerService.getHotLeadCount()).thenReturn(3L);
        when(customerService.getWarmLeadCount()).thenReturn(4L);
        when(customerService.getColdLeadCount()).thenReturn(3L);

        Customer c1 = new Customer();
        c1.setLeadScore(90);
        Customer c2 = new Customer();
        c2.setLeadScore(70);
        when(customerRepository.findAll()).thenReturn(List.of(c1, c2));

        Campaign camp = new Campaign();
        camp.setCampaignName("Google Search Ads");
        camp.setPlatform("Google Ads");
        camp.setBudget(25000.0);
        camp.setStatus("Running");
        when(campaignRepository.findAll()).thenReturn(List.of(camp));

        DashboardDTO stats = dashboardService.getDashboardStats();

        assertEquals(10L, stats.getTotalCustomers());
        assertEquals(3L, stats.getTotalCampaigns());
        assertEquals(1L, stats.getRunningCampaigns());
        assertEquals(15L, stats.getTotalActivities());
        assertEquals(25000.0, stats.getTotalBudget());
        assertEquals(80.0, stats.getAverageLeadScore());
        assertEquals(30.0, stats.getConversionRate()); // 3/10 * 100 = 30.0%
        assertTrue(stats.getPlatformBudgets().containsKey("Google Ads"));
    }

    @Test
    void testCustomerSegmentation() {
        Customer hot = new Customer();
        hot.setName("Hot Lead");
        hot.setLeadScore(85);

        Customer warm = new Customer();
        warm.setName("Warm Lead");
        warm.setLeadScore(65);

        Customer cold = new Customer();
        cold.setName("Cold Lead");
        cold.setLeadScore(25);

        when(customerRepository.findAll()).thenReturn(List.of(hot, warm, cold));

        CustomerSegmentationService segmentationService = new CustomerSegmentationService(customerRepository);
        List<CustomerSegmentationService.SegmentSummary> segments = segmentationService.getLeadSegments();

        assertEquals(3, segments.size());
        assertEquals(1, segments.get(0).getCount()); // Hot
        assertEquals(1, segments.get(1).getCount()); // Warm
        assertEquals(1, segments.get(2).getCount()); // Cold
    }
}
