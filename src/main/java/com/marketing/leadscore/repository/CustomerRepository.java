package com.marketing.leadscore.repository;

import com.marketing.leadscore.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByNameContainingIgnoreCase(String keyword);

    java.util.Optional<Customer> findByOrganizationIdAndEmailIgnoreCase(Long organizationId, String email);

    java.util.Optional<Customer> findByOrganizationIdAndId(Long organizationId, Long id);

    List<Customer> findTop5ByOrderByLeadScoreDesc();

    long countByLeadScoreGreaterThanEqual(int score);

    long countByLeadScoreBetween(int min, int max);

    long countByLeadScoreLessThan(int score);

    long countByLeadStatus(String leadStatus);

}