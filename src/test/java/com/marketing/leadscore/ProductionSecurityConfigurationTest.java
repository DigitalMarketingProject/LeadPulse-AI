package com.marketing.leadscore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:production_security_test;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "leadpulse.bootstrap-key=test-bootstrap-key",
        "leadpulse.admin.username=admin",
        "leadpulse.admin.password-bcrypt=$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
        "leadpulse.cors.allowed-origins=https://company.example"
})
@AutoConfigureMockMvc
@ActiveProfiles("production")
class ProductionSecurityConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void protectsDashboardFromUnauthenticatedRequests() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void permitsAuthenticatedAdminToUseDashboardApi() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk());
    }

    @Test
    void integrationEndpointUsesApiKeyAuthenticationInsteadOfCsrfToken() throws Exception {
        mockMvc.perform(post("/api/tracking/events")
                        .contentType("application/json")
                        .content("""
                                {"eventType":"PAGE_VIEW","visitorId":"visitor-1","consentGiven":true}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void rendersIntegrationSetupPageForAuthenticatedAdmin() throws Exception {
        mockMvc.perform(get("/integrations"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Keep API keys private.")));
    }
}
