package com.marketing.leadscore.service;

import com.marketing.leadscore.entity.Activity;
import com.marketing.leadscore.entity.Customer;
import com.marketing.leadscore.repository.ActivityRepository;
import com.marketing.leadscore.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LeadScoringService leadScoringService;

    @InjectMocks
    private ActivityService activityService;

    @Test
    void testDeleteActivityDeletesActivityAndNotCustomer() {
        Long activityId = 55L;
        Customer customer = new Customer();
        customer.setId(10L);
        customer.setName("Acme Client");

        Activity activity = new Activity(activityId, "Acme Client", "Phone Call", "Notes", LocalDate.now(), customer);

        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));

        activityService.deleteActivity(activityId);

        // Verify that activityRepository.deleteById was invoked with activityId
        verify(activityRepository, times(1)).deleteById(activityId);

        // Crucial verification: customerRepository.deleteById MUST NEVER be called during deleteActivity!
        verify(customerRepository, never()).deleteById(any());

        // Verify that customer's lead score was recalculated
        verify(leadScoringService, times(1)).applyScoreAndStatus(customer);
        verify(customerRepository, times(1)).save(customer);
    }
}
