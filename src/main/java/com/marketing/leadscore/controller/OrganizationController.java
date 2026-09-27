package com.marketing.leadscore.controller;

import com.marketing.leadscore.service.OrganizationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService service;
    private final String bootstrapKey;

    public OrganizationController(
            OrganizationService service,
            @Value("${leadpulse.bootstrap-key}") String bootstrapKey) {
        this.service = service;
        this.bootstrapKey = bootstrapKey;
    }

    @PostMapping
    public ResponseEntity<OrganizationService.CreatedOrganization> create(
            @RequestHeader(name = "X-LeadPulse-Bootstrap-Key", required = false) String suppliedKey,
            @RequestBody Map<String, String> body) {
        if (!bootstrapKey.equals(suppliedKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(body.get("name")));
    }

    @PostMapping("/api-key/rotate")
    public OrganizationService.CreatedOrganization rotateApiKey(
            @RequestHeader(name = "X-LeadPulse-Api-Key", required = false) String apiKey) {
        return service.rotateApiKey(apiKey);
    }

    @DeleteMapping("/api-key")
    public ResponseEntity<Void> revokeApiKey(
            @RequestHeader(name = "X-LeadPulse-Api-Key", required = false) String apiKey) {
        service.revokeApiKey(apiKey);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{organizationId}/api-key/rotate")
    public ResponseEntity<OrganizationService.CreatedOrganization> recoverApiKey(
            @PathVariable Long organizationId,
            @RequestHeader(name = "X-LeadPulse-Bootstrap-Key", required = false) String suppliedKey) {
        if (!bootstrapKey.equals(suppliedKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(service.rotateApiKeyForOrganization(organizationId));
    }
}
