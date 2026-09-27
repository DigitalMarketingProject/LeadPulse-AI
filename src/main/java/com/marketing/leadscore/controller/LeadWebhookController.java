package com.marketing.leadscore.controller;

import com.marketing.leadscore.dto.LeadWebhookRequest;
import com.marketing.leadscore.entity.Customer;
import com.marketing.leadscore.service.LeadWebhookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/integrations")
public class LeadWebhookController {

    private final LeadWebhookService service;

    public LeadWebhookController(LeadWebhookService service) {
        this.service = service;
    }

    @PostMapping("/lead-events")
    public ResponseEntity<Map<String, Object>> receiveLeadEvent(
            @RequestHeader(name = "X-LeadPulse-Api-Key", required = false) String apiKey,
            @RequestHeader(name = "X-LeadPulse-Timestamp", required = false) String timestamp,
            @RequestHeader(name = "X-LeadPulse-Signature", required = false) String signature,
            @RequestHeader(name = "X-LeadPulse-Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody LeadWebhookRequest request) {
        Customer customer = service.process(request, apiKey, timestamp, signature, idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "accepted", true,
                "customerId", customer.getId(),
                "leadScore", customer.getLeadScore(),
                "leadStatus", customer.getLeadStatus(),
                "message", "Lead event accepted and customer profile updated."
        ));
    }
}
