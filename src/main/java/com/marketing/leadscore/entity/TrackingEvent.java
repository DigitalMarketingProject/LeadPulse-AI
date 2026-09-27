package com.marketing.leadscore.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tracking_events")
public class TrackingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(nullable = false, length = 40)
    private String eventType;

    @Column(nullable = false, length = 100)
    private String visitorId;

    @Column(length = 100)
    private String sessionId;

    @Column(length = 500)
    private String pageUrl;

    @Column(length = 500)
    private String referrer;

    @Column(nullable = false)
    private LocalDateTime occurredAt;

    protected TrackingEvent() {
    }

    public TrackingEvent(Organization organization, String eventType, String visitorId, String sessionId,
                         String pageUrl, String referrer, LocalDateTime occurredAt) {
        this.organization = organization;
        this.eventType = eventType;
        this.visitorId = visitorId;
        this.sessionId = sessionId;
        this.pageUrl = pageUrl;
        this.referrer = referrer;
        this.occurredAt = occurredAt;
    }

    public Long getId() {
        return id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void assignCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getEventType() {
        return eventType;
    }

    public String getVisitorId() {
        return visitorId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public String getReferrer() {
        return referrer;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
}
