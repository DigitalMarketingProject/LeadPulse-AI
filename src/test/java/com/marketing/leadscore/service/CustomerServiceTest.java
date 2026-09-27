package com.marketing.leadscore.service;

import com.marketing.leadscore.entity.Customer;
import com.marketing.leadscore.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LeadScoringService leadScoringService;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void testGetTopCustomersNullSafe() {
        Customer c1 = new Customer();
        c1.setName("Lead A");
        c1.setLeadScore(null); // Explicit null score

        Customer c2 = new Customer();
        c2.setName("Lead B");
        c2.setLeadScore(85);

        Customer c3 = new Customer();
        c3.setName("Lead C");
        c3.setLeadScore(40);

        when(customerRepository.findTop5ByOrderByLeadScoreDesc()).thenReturn(List.of());
        when(customerRepository.findAll()).thenReturn(List.of(c1, c2, c3));

        // Must not throw NullPointerException despite c1 having null score
        assertDoesNotThrow(() -> {
            List<Customer> top = customerService.getTopCustomers();
            assertEquals(3, top.size());
            assertEquals("Lead B", top.get(0).getName());
        });
    }

    @Test
    void testSaveCustomerAppliesScoring() {
        Customer c = new Customer();
        c.setName("New Lead");

        when(customerRepository.save(c)).thenReturn(c);

        Customer saved = customerService.saveCustomer(c);

        verify(leadScoringService, times(1)).applyScoreAndStatus(c);
        verify(customerRepository, times(1)).save(c);
        assertNotNull(saved);
    }
}
