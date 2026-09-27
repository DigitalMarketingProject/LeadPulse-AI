package com.marketing.leadscore.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "organizations")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 64)
    private String apiKeyHash;

    @Column
    private Boolean apiKeyActive = true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Organization() {
    }

    public Organization(String name, String apiKeyHash, LocalDateTime createdAt) {
        this.name = name;
        this.apiKeyHash = apiKeyHash;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getApiKeyHash() {
        return apiKeyHash;
    }

    public boolean isApiKeyActive() {
        return apiKeyActive == null || apiKeyActive;
    }

    public void rotateApiKey(String apiKeyHash) {
        this.apiKeyHash = apiKeyHash;
        this.apiKeyActive = true;
    }

    public void revokeApiKey() {
        this.apiKeyActive = false;
    }
}
