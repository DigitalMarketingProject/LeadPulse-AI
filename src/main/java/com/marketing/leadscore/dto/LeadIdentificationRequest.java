package com.marketing.leadscore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LeadIdentificationRequest(
        @NotBlank @Size(max = 100) String visitorId,
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(max = 120) String name,
        @Size(max = 40) String phone,
        @Size(max = 160) String company,
        boolean consentGiven
) {
}
