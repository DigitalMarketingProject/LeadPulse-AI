package com.marketing.leadscore.entity;

import jakarta.persistence.*;

@Entity
@Table(name="customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    private String name;

    private String email;

    private String phone;

    private String company;

    private Integer leadScore = 0;

    private String leadStatus = "Cold";

    private Integer emailClicks = 0;

    private Integer websiteVisits = 0;

    private Integer formSubmissions = 0;

    private Integer socialMediaClicks = 0;

    public Customer() {
    }

    public Customer(Long id,
                    String name,
                    String email,
                    String phone,
                    String company,
                    Integer leadScore,
                    String leadStatus,
                    Integer emailClicks,
                    Integer websiteVisits,
                    Integer formSubmissions,
                    Integer socialMediaClicks) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.company = company;
        this.leadScore = leadScore != null ? leadScore : 0;
        this.leadStatus = leadStatus != null ? leadStatus : "Cold";
        this.emailClicks = emailClicks != null ? emailClicks : 0;
        this.websiteVisits = websiteVisits != null ? websiteVisits : 0;
        this.formSubmissions = formSubmissions != null ? formSubmissions : 0;
        this.socialMediaClicks = socialMediaClicks != null ? socialMediaClicks : 0;
    }

    public Long getId() {
        return id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company){
        this.company=company;
    }
    public Integer getLeadScore() {
        return leadScore != null ? leadScore : 0;
    }

    public void setLeadScore(Integer leadScore) {
        this.leadScore = leadScore != null ? leadScore : 0;
    }

    public String getLeadStatus() {
        return leadStatus != null ? leadStatus : "Cold";
    }

    public void setLeadStatus(String leadStatus) {
        this.leadStatus = leadStatus;
    }

    public Integer getEmailClicks() {
        return emailClicks != null ? emailClicks : 0;
    }

    public void setEmailClicks(Integer emailClicks) {
        this.emailClicks = emailClicks != null ? emailClicks : 0;
    }

    public Integer getWebsiteVisits() {
        return websiteVisits != null ? websiteVisits : 0;
    }

    public void setWebsiteVisits(Integer websiteVisits) {
        this.websiteVisits = websiteVisits != null ? websiteVisits : 0;
    }

    public Integer getFormSubmissions() {
        return formSubmissions != null ? formSubmissions : 0;
    }

    public void setFormSubmissions(Integer formSubmissions) {
        this.formSubmissions = formSubmissions != null ? formSubmissions : 0;
    }

    public Integer getSocialMediaClicks() {
        return socialMediaClicks != null ? socialMediaClicks : 0;
    }

    public void setSocialMediaClicks(Integer socialMediaClicks) {
        this.socialMediaClicks = socialMediaClicks != null ? socialMediaClicks : 0;
    }
}