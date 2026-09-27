package com.marketing.leadscore.controller;

import com.marketing.leadscore.dto.CustomerPrivacyExportDTO;
import com.marketing.leadscore.service.CustomerPrivacyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/privacy/customers")
public class CustomerPrivacyController {

    private final CustomerPrivacyService service;

    public CustomerPrivacyController(CustomerPrivacyService service) {
        this.service = service;
    }

    @GetMapping("/{id}/export")
    public CustomerPrivacyExportDTO exportCustomer(
            @PathVariable Long id,
            @RequestHeader(name = "X-LeadPulse-Api-Key", required = false) String apiKey) {
        return service.exportCustomer(id, apiKey);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eraseCustomer(
            @PathVariable Long id,
            @RequestHeader(name = "X-LeadPulse-Api-Key", required = false) String apiKey) {
        service.eraseCustomer(id, apiKey);
        return ResponseEntity.noContent().build();
    }
}
