package com.marketing.leadscore.service;

import com.marketing.leadscore.exception.ApiRateLimitExceededException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class OrganizationRateLimiterTest {

    @Test
    void enforcesLimitPerOrganization() {
        OrganizationRateLimiter limiter = new OrganizationRateLimiter(1, 60);

        limiter.check(1L);
        limiter.check(2L);

        assertThrows(ApiRateLimitExceededException.class, () -> limiter.check(1L));
    }
}
