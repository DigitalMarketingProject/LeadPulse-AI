package com.marketing.leadscore.service;

import com.marketing.leadscore.entity.Organization;
import com.marketing.leadscore.exception.ResourceNotFoundException;
import com.marketing.leadscore.repository.ActivityRepository;
import com.marketing.leadscore.repository.CustomerRepository;
import com.marketing.leadscore.repository.TrackingEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerPrivacyServiceTest {

    @Mock
    private OrganizationService organizationService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private TrackingEventRepository trackingEventRepository;

    @InjectMocks
    private CustomerPrivacyService service;

    @Test
    void refusesToEraseCustomerOutsideOrganization() {
        Organization organization = new Organization("Example", "hash", LocalDateTime.now());
        when(organizationService.authenticate("key")).thenReturn(organization);
        when(customerRepository.findByOrganizationIdAndId(null, 17L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.eraseCustomer(17L, "key"));

        verifyNoInteractions(activityRepository, trackingEventRepository);
        verify(customerRepository, never()).deleteById(anyLong());
    }
}
