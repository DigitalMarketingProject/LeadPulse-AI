package com.marketing.leadscore.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;

    private String activityType;

    private String remarks;

    private LocalDate activityDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public Activity() {
    }

    public Activity(Long id, String customerName, String activityType, String remarks, LocalDate activityDate, Customer customer) {
        this.id = id;
        this.customerName = customerName;
        this.activityType = activityType;
        this.remarks = remarks;
        this.activityDate = activityDate;
        this.customer = customer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id=id;
    }

    public Customer getCustomer() {
    return customer;
}

public void setCustomer(Customer customer) {
    this.customer = customer;
}

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName){
        this.customerName=customerName;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType){
        this.activityType=activityType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks){
        this.remarks=remarks;
    }

    public LocalDate getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(LocalDate activityDate){
        this.activityDate=activityDate;
    }
}