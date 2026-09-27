package com.marketing.leadscore.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "webhook_receipts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"organization_id", "idempotency_key"}))
public class WebhookReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "idempotency_key", nullable = false, length = 100)
    private String idempotencyKey;

    @Column(nullable = false)
    private LocalDateTime receivedAt;

    protected WebhookReceipt() {
    }

    public WebhookReceipt(Organization organization, String idempotencyKey, LocalDateTime receivedAt) {
        this.organization = organization;
        this.idempotencyKey = idempotencyKey;
        this.receivedAt = receivedAt;
    }
}
