package com.marketing.leadscore.repository;

import com.marketing.leadscore.entity.TrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackingEventRepository extends JpaRepository<TrackingEvent, Long> {

    List<TrackingEvent> findByOrganizationId(Long organizationId);

    List<TrackingEvent> findByOrganizationIdAndCustomerId(Long organizationId, Long customerId);

    long deleteByOrganizationIdAndCustomerId(Long organizationId, Long customerId);

    List<TrackingEvent> findByOrganizationIdAndVisitorIdOrderByOccurredAtDesc(
            Long organizationId, String visitorId);
}
