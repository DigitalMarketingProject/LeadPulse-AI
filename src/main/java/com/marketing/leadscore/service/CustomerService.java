package com.marketing.leadscore.service;

import com.marketing.leadscore.entity.Customer;
import com.marketing.leadscore.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository repository;
    private final LeadScoringService leadScoringService;

    public CustomerService(CustomerRepository repository, LeadScoringService leadScoringService) {
        this.repository = repository;
        this.leadScoringService = leadScoringService;
    }

    public Customer getCustomerById(Long id) {
        if (id == null) return null;
        return repository.findById(id).orElse(null);
    }

    public List<Customer> getAllCustomers() {
        return repository.findAll();
    }

    public Customer saveCustomer(Customer customer) {
        if (customer == null) return null;
        leadScoringService.applyScoreAndStatus(customer);
        return repository.save(customer);
    }

    public Customer updateCustomer(Customer customer) {
        if (customer == null) return null;
        leadScoringService.applyScoreAndStatus(customer);
        return repository.save(customer);
    }

    public Customer updateCustomer(Long id, Customer customerData) {
        Customer existing = getCustomerById(id);
        if (existing == null) {
            return null;
        }

        if (customerData.getName() != null) existing.setName(customerData.getName());
        if (customerData.getEmail() != null) existing.setEmail(customerData.getEmail());
        if (customerData.getPhone() != null) existing.setPhone(customerData.getPhone());
        if (customerData.getCompany() != null) existing.setCompany(customerData.getCompany());
        if (customerData.getEmailClicks() != null) existing.setEmailClicks(customerData.getEmailClicks());
        if (customerData.getWebsiteVisits() != null) existing.setWebsiteVisits(customerData.getWebsiteVisits());
        if (customerData.getFormSubmissions() != null) existing.setFormSubmissions(customerData.getFormSubmissions());
        if (customerData.getSocialMediaClicks() != null) existing.setSocialMediaClicks(customerData.getSocialMediaClicks());

        leadScoringService.applyScoreAndStatus(existing);
        return repository.save(existing);
    }

    public void deleteCustomer(Long id) {
        if (id != null && repository.existsById(id)) {
            repository.deleteById(id);
        }
    }

    public List<Customer> searchCustomer(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return repository.findAll();
        }
        return repository.findByNameContainingIgnoreCase(keyword.trim());
    }

    public List<Customer> getTopCustomers() {
        List<Customer> top = repository.findTop5ByOrderByLeadScoreDesc();
        if (top != null && !top.isEmpty()) {
            return top;
        }
        return repository.findAll()
                .stream()
                .sorted((a, b) -> Integer.compare(
                        b.getLeadScore() != null ? b.getLeadScore() : 0,
                        a.getLeadScore() != null ? a.getLeadScore() : 0
                ))
                .limit(5)
                .toList();
    }

    public long getCustomerCount() {
        return repository.count();
    }

    public long getHotLeadCount() {
        return repository.countByLeadScoreGreaterThanEqual(80);
    }

    public long getWarmLeadCount() {
        return repository.countByLeadScoreBetween(50, 79);
    }

    public long getColdLeadCount() {
        return repository.countByLeadScoreLessThan(50);
    }

    public Customer recalculateCustomerScore(Long id) {
        Customer customer = getCustomerById(id);
        if (customer != null) {
            leadScoringService.applyScoreAndStatus(customer);
            return repository.save(customer);
        }
        return null;
    }

    public void recalculateAllCustomerScores() {
        List<Customer> customers = repository.findAll();
        for (Customer c : customers) {
            leadScoringService.applyScoreAndStatus(c);
        }
        repository.saveAll(customers);
    }
}