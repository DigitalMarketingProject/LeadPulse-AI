package com.marketing.leadscore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@Profile("production")
public class ProductionSecurityConfig {

    @Bean
    public SecurityFilterChain productionSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        "/api/tracking/**",
                        "/api/integrations/**",
                        "/api/analytics/tracking",
                        "/api/organizations/**",
                        "/api/privacy/**"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        .requestMatchers(
                                "/api/tracking/**",
                                "/api/integrations/**",
                                "/api/analytics/tracking",
                                "/api/organizations/**",
                                "/api/privacy/**").permitAll()
                        .anyRequest().authenticated())
                .cors(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public CorsConfigurationSource productionCorsConfigurationSource(
            @Value("${leadpulse.cors.allowed-origins}") String allowedOrigins) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList());
        configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.List.of(
                "Content-Type", "X-LeadPulse-Api-Key", "X-LeadPulse-Timestamp",
                "X-LeadPulse-Signature", "X-LeadPulse-Idempotency-Key"));
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder productionPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService productionUserDetailsService(
            @Value("${leadpulse.admin.username}") String username,
            @Value("${leadpulse.admin.password-bcrypt}") String passwordHash,
            PasswordEncoder productionPasswordEncoder) {
        if (username.isBlank() || !passwordHash.matches("^\\$2[aby]\\$.{56}$")) {
            throw new IllegalStateException(
                    "Production requires LEADPULSE_ADMIN_USERNAME and a BCrypt LEADPULSE_ADMIN_PASSWORD_BCRYPT.");
        }
        return new InMemoryUserDetailsManager(User.withUsername(username)
                .password(passwordHash)
                .roles("ADMIN")
                .build());
    }
}
