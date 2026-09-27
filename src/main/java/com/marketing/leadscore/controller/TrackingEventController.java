package com.marketing.leadscore.controller;

import com.marketing.leadscore.dto.TrackingEventRequest;
import com.marketing.leadscore.dto.LeadIdentificationRequest;
import com.marketing.leadscore.entity.TrackingEvent;
import com.marketing.leadscore.entity.Customer;
import com.marketing.leadscore.service.LeadIdentificationService;
import com.marketing.leadscore.service.TrackingEventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tracking")
public class TrackingEventController {

    private final TrackingEventService service;
    private final LeadIdentificationService identificationService;

    public TrackingEventController(
            TrackingEventService service,
            LeadIdentificationService identificationService) {
        this.service = service;
        this.identificationService = identificationService;
    }

    @PostMapping("/events")
    public ResponseEntity<Map<String, Object>> recordEvent(
            @RequestHeader(name = "X-LeadPulse-Api-Key", required = false) String apiKey,
            @Valid @RequestBody TrackingEventRequest request) {
        TrackingEvent event = service.record(request, apiKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "accepted", true,
                "eventId", event.getId(),
                "message", "Consent-aware anonymous event recorded."
        ));
    }

    @PostMapping("/identify")
    public ResponseEntity<Map<String, Object>> identify(
            @RequestHeader(name = "X-LeadPulse-Api-Key", required = false) String apiKey,
            @Valid @RequestBody LeadIdentificationRequest request) {
        Customer customer = identificationService.identify(request, apiKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "identified", true,
                "customerId", customer.getId(),
                "leadScore", customer.getLeadScore(),
                "leadStatus", customer.getLeadStatus(),
                "message", "Visitor history linked to the lead."
        ));
    }
}
