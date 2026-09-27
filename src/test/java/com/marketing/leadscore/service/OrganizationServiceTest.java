package com.marketing.leadscore.service;

import com.marketing.leadscore.entity.Organization;
import com.marketing.leadscore.exception.ApiAuthenticationException;
import com.marketing.leadscore.repository.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    @Mock
    private OrganizationRepository repository;

    @InjectMocks
    private OrganizationService service;

    @Test
    void rotatesApiKeyAndInvalidatesTheOldKey() throws Exception {
        String oldKey = "lp_existing";
        String oldHash = hash(oldKey);
        Organization organization = new Organization("Example", oldHash, LocalDateTime.now());
        when(repository.findByApiKeyHash(oldHash)).thenReturn(Optional.of(organization));
        when(repository.save(any(Organization.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrganizationService.CreatedOrganization rotated = service.rotateApiKey(oldKey);

        assertNotEquals(oldKey, rotated.apiKey());
        assertNotEquals(oldHash, organization.getApiKeyHash());
        assertTrue(organization.isApiKeyActive());
        when(repository.findByApiKeyHash(oldHash)).thenReturn(Optional.empty());
        assertThrows(ApiAuthenticationException.class, () -> service.authenticate(oldKey));
    }

    @Test
    void revokesApiKey() throws Exception {
        String key = "lp_revoke";
        Organization organization = new Organization("Example", hash(key), LocalDateTime.now());
        when(repository.findByApiKeyHash(hash(key))).thenReturn(Optional.of(organization));
        when(repository.save(any(Organization.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.revokeApiKey(key);

        assertFalse(organization.isApiKeyActive());
    }

    private String hash(String value) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                .digest(value.getBytes(StandardCharsets.UTF_8)));
    }
}
