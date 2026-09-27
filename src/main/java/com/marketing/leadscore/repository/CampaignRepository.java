package com.marketing.leadscore.repository;

import com.marketing.leadscore.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository
        extends JpaRepository<Campaign,Long> {

    java.util.List<Campaign> findByStatus(String status);

    long countByStatus(String status);

    java.util.List<Campaign> findByPlatform(String platform);

}