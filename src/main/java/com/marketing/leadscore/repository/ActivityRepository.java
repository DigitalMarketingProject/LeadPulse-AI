package com.marketing.leadscore.repository;

import com.marketing.leadscore.entity.Activity;
import com.marketing.leadscore.entity.Customer;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository
        extends JpaRepository<Activity,Long> {

    List<Activity> findByCustomer(Customer customer);

    List<Activity> findByCustomerId(Long customerId);

    long deleteByCustomerId(Long customerId);

    long countByActivityType(String activityType);

    List<Activity> findTop10ByOrderByActivityDateDesc();
}