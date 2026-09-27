package com.marketing.leadscore.service;

import com.marketing.leadscore.dto.LeadIdentificationRequest;
import com.marketing.leadscore.dto.LeadWebhookRequest;
import com.marketing.leadscore.entity.Customer;
import com.marketing.leadscore.entity.Organization;
import com.marketing.leadscore.entity.WebhookReceipt;
import com.marketing.leadscore.repository.WebhookReceiptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;

@Service
public class LeadWebhookService {

    private static final Set<String> ALLOWED_WEBHOOK_EVENTS = Set.of(
            "FORM_SUBMITTED", "DEMO_REQUESTED", "ACCOUNT_CREATED", "PURCHASE_COMPLETED"
    );

    private final TrackingEventService trackingEventService;
    private final LeadIdentificationService leadIdentificationService;
    private final OrganizationService organizationService;
    private final WebhookReceiptRepository receiptRepository;

    public LeadWebhookService(
            TrackingEventService trackingEventService,
            LeadIdentificationService leadIdentificationService,
            OrganizationService organizationService,
            WebhookReceiptRepository receiptRepository) {
        this.trackingEventService = trackingEventService;
        this.leadIdentificationService = leadIdentificationService;
        this.organizationService = organizationService;
        this.receiptRepository = receiptRepository;
    }

    @Transactional
    public Customer process(LeadWebhookRequest request, String apiKey,
                            String timestamp, String signature, String idempotencyKey) {
        if (!request.consentGiven()) {
            throw new IllegalArgumentException("Webhook consent is required.");
        }

        String eventType = request.eventType().trim().toUpperCase(Locale.ROOT);
        if (!ALLOWED_WEBHOOK_EVENTS.contains(eventType)) {
            throw new IllegalArgumentException("Unsupported webhook event: " + eventType);
        }

        Organization organization = organizationService.authenticate(apiKey);
        validateRequestSecurity(request, apiKey, timestamp, signature, idempotencyKey, organization);
        receiptRepository.save(new WebhookReceipt(
                organization, idempotencyKey.trim(), LocalDateTime.now()));

        trackingEventService.record(
                new com.marketing.leadscore.dto.TrackingEventRequest(
                        eventType,
                        request.visitorId(),
                        null,
                        request.pageUrl(),
                        null,
                        true
                ),
                apiKey
        );

        return leadIdentificationService.identify(
                new LeadIdentificationRequest(
                        request.visitorId(),
                        request.email(),
                        request.name(),
                        request.phone(),
                        request.company(),
                        true
                ),
                apiKey
        );
    }

    private void validateRequestSecurity(
            LeadWebhookRequest request,
            String apiKey,
            String timestamp,
            String signature,
            String idempotencyKey,
            Organization organization) {
        if (timestamp == null || signature == null || idempotencyKey == null
                || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException(
                    "Webhook timestamp, signature, and idempotency key are required.");
        }
        long timestampSeconds;
        try {
            timestampSeconds = Long.parseLong(timestamp);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Webhook timestamp must be Unix seconds.", ex);
        }
        if (Math.abs(Instant.now().getEpochSecond() - timestampSeconds) > 300) {
            throw new IllegalArgumentException("Webhook timestamp is outside the five-minute window.");
        }
        if (idempotencyKey.length() > 100) {
            throw new IllegalArgumentException("Webhook idempotency key is too long.");
        }
        if (receiptRepository.existsByOrganizationIdAndIdempotencyKey(
                organization.getId(), idempotencyKey.trim())) {
            throw new IllegalArgumentException("Webhook idempotency key was already processed.");
        }

        String canonical = String.join("\n",
                timestamp,
                request.eventType().trim().toUpperCase(Locale.ROOT),
                request.visitorId().trim(),
                request.email().trim().toLowerCase(Locale.ROOT),
                request.name().trim(),
                value(request.phone()),
                value(request.company()),
                value(request.pageUrl()));
        String expected = "sha256=" + hmacSha256(apiKey, canonical);
        if (!MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                signature.trim().getBytes(StandardCharsets.UTF_8))) {
            throw new IllegalArgumentException("Invalid webhook signature.");
        }
    }

    private String hmacSha256(String secret, String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            StringBuilder result = new StringBuilder();
            for (byte item : mac.doFinal(value.getBytes(StandardCharsets.UTF_8))) {
                result.append(String.format("%02x", item));
            }
            return result.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to verify webhook signature.", ex);
        }
    }

    private String value(String value) {
        return value == null ? "" : value.trim();
    }
}
