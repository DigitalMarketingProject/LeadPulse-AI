package com.marketing.leadscore.repository;

import com.marketing.leadscore.entity.WebhookReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookReceiptRepository extends JpaRepository<WebhookReceipt, Long> {

    boolean existsByOrganizationIdAndIdempotencyKey(Long organizationId, String idempotencyKey);
}
