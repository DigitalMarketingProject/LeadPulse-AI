package com.marketing.leadscore.config;

import com.marketing.leadscore.exception.ApiRateLimitExceededException;
import com.marketing.leadscore.service.OrganizationRateLimiter;
import com.marketing.leadscore.service.OrganizationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class OrganizationRateLimitFilter extends OncePerRequestFilter {

    private final OrganizationService organizationService;
    private final OrganizationRateLimiter rateLimiter;

    public OrganizationRateLimitFilter(
            OrganizationService organizationService,
            OrganizationRateLimiter rateLimiter) {
        this.organizationService = organizationService;
        this.rateLimiter = rateLimiter;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !(path.startsWith("/api/tracking/")
                || path.startsWith("/api/integrations/")
                || path.equals("/api/analytics/tracking"));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String apiKey = request.getHeader("X-LeadPulse-Api-Key");
        if (apiKey != null && !apiKey.isBlank()) {
            var organization = organizationService.findActiveByApiKey(apiKey);
            if (organization.isPresent()) {
                try {
                    rateLimiter.check(organization.get().getId());
                } catch (ApiRateLimitExceededException ex) {
                    response.setStatus(429);
                    response.setHeader("Retry-After", String.valueOf(ex.getRetryAfterSeconds()));
                    response.setContentType("application/json");
                    response.getWriter().write("{\"status\":429,\"error\":\"Too Many Requests\"}");
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
