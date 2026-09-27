package com.marketing.leadscore.service;

import com.marketing.leadscore.entity.Campaign;
import com.marketing.leadscore.repository.CampaignRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @Mock
    private CampaignRepository repository;

    @InjectMocks
    private CampaignService service;

    @Test
    void normalizesNegativePerformanceMetrics() {
        Campaign campaign = new Campaign();
        campaign.setCampaignName("Search");
        campaign.setLeadsGenerated(-5);
        campaign.setConversions(20);
        campaign.setAttributedRevenue(-100.0);
        when(repository.save(campaign)).thenReturn(campaign);

        Campaign saved = service.saveCampaign(campaign);

        assertEquals(0, saved.getLeadsGenerated());
        assertEquals(0, saved.getConversions());
        assertEquals(0.0, saved.getAttributedRevenue());
        assertEquals("Scheduled", saved.getStatus());
    }
}
