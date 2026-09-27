package com.marketing.leadscore.controller;

import com.marketing.leadscore.entity.Customer;
import com.marketing.leadscore.exception.ResourceNotFoundException;
import com.marketing.leadscore.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/customers", "/customers"})
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    public List<Customer> getCustomers(@RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) {
            return service.searchCustomer(search);
        }
        return service.getAllCustomers();
    }

    @GetMapping("/{id}")
    public Customer getCustomerById(@PathVariable Long id) {
        Customer customer = service.getCustomerById(id);
        if (customer == null) {
            throw new ResourceNotFoundException("Customer with ID " + id + " not found.");
        }
        return customer;
    }

    @PostMapping
    public ResponseEntity<Customer> saveCustomer(@RequestBody Customer customer) {
        Customer saved = service.saveCustomer(customer);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public Customer updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        Customer updated = service.updateCustomer(id, customer);
        if (updated == null) {
            throw new ResourceNotFoundException("Customer with ID " + id + " not found.");
        }
        return updated;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        Customer existing = service.getCustomerById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Customer with ID " + id + " not found.");
        }
        service.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/top")
    public List<Customer> getTopCustomers() {
        return service.getTopCustomers();
    }

    @PostMapping("/{id}/recalculate")
    public Customer recalculateScore(@PathVariable Long id) {
        Customer customer = service.recalculateCustomerScore(id);
        if (customer == null) {
            throw new ResourceNotFoundException("Customer with ID " + id + " not found.");
        }
        return customer;
    }

    @PostMapping("/recalculate-all")
    public ResponseEntity<String> recalculateAllScores() {
        service.recalculateAllCustomerScores();
        return ResponseEntity.ok("All customer lead scores recalculated successfully.");
    }
}