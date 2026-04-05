package com.huerto.api.application;

import com.huerto.api.application.impl.customer.DeleteCustomerUseCaseImpl;
import com.huerto.api.domain.model.Customer;
import com.huerto.api.domain.ports.out.CustomerRepository;
import com.huerto.api.domain.valueobject.Credentials;
import com.huerto.api.domain.valueobject.Email;
import com.huerto.api.util.CustomerTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.huerto.api.domain.exception.CustomerNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DeleteCustomerUseCaseTest {

    @Mock
    CustomerRepository customerRepository;

    @InjectMocks
    DeleteCustomerUseCaseImpl useCase;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void should_delete_customer_successfully() {
        UUID customerId = UUID.randomUUID();
        Customer customer = CustomerTestFactory.buildCustomer(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        doNothing().when(customerRepository).deleteById(customerId);

        useCase.execute(customerId);

        verify(customerRepository).deleteById(customerId);
    }

    @Test
    void should_throw_when_customer_not_found() {
        UUID customerId = UUID.randomUUID();

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(customerId))
                .isInstanceOf(CustomerNotFoundException.class);

        verify(customerRepository, never()).deleteById(any());
    }
}