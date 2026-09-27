package com.marketing.leadscore.service;

import com.marketing.leadscore.entity.Organization;
import com.marketing.leadscore.exception.ApiAuthenticationException;
import com.marketing.leadscore.repository.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
public class OrganizationService {

    private final OrganizationRepository repository;
    private final SecureRandom secureRandom = new SecureRandom();

    public OrganizationService(OrganizationRepository repository) {
        this.repository = repository;
    }

    public CreatedOrganization create(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Organization name is required.");
        }
        String normalizedName = name.trim();
        if (repository.findAll().stream().anyMatch(existing -> existing.getName().equalsIgnoreCase(normalizedName))) {
            throw new IllegalArgumentException("Organization already exists.");
        }

        String apiKey = generateApiKey();
        repository.save(new Organization(
                normalizedName,
                hash(apiKey),
                LocalDateTime.now()
        ));
        return new CreatedOrganization(normalizedName, apiKey);
    }

    public Organization authenticate(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ApiAuthenticationException("X-LeadPulse-Api-Key header is required.");
        }
        return findActiveByApiKey(apiKey)
                .orElseThrow(() -> new ApiAuthenticationException("Invalid LeadPulse API key."));
    }

    public java.util.Optional<Organization> findActiveByApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            return java.util.Optional.empty();
        }
        return repository.findByApiKeyHash(hash(apiKey.trim()))
                .filter(Organization::isApiKeyActive);
    }

    public CreatedOrganization rotateApiKey(String currentApiKey) {
        Organization organization = authenticate(currentApiKey);
        return rotateApiKey(organization);
    }

    public CreatedOrganization rotateApiKeyForOrganization(Long organizationId) {
        Organization organization = repository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found."));
        return rotateApiKey(organization);
    }

    private CreatedOrganization rotateApiKey(Organization organization) {
        String newApiKey = generateApiKey();
        organization.rotateApiKey(hash(newApiKey));
        repository.save(organization);
        return new CreatedOrganization(organization.getName(), newApiKey);
    }

    public void revokeApiKey(String currentApiKey) {
        Organization organization = authenticate(currentApiKey);
        organization.revokeApiKey();
        repository.save(organization);
    }

    private String generateApiKey() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return "lp_" + HexFormat.of().formatHex(bytes);
    }

    private String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is unavailable.", ex);
        }
    }

    public record CreatedOrganization(String name, String apiKey) {
    }
}
