package com.huerto.api.application;

import com.huerto.api.application.impl.customer.FindCustomerUseCaseImpl;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Customer;
import com.huerto.api.domain.ports.out.CustomerRepository;
import com.huerto.api.domain.valueobject.Credentials;
import com.huerto.api.domain.valueobject.Email;
import com.huerto.api.util.CustomerTestFactory;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindCustomerUseCaseTest {

    @Mock CustomerRepository customerRepository;
    @InjectMocks FindCustomerUseCaseImpl findCustomerUseCase;


    @Test
    void should_return_customer_when_found() {
        UUID id = UUID.randomUUID();
        Customer customer = CustomerTestFactory.buildCustomer(id);

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        Customer result = findCustomerUseCase.execute(id);

        assertThat(result).isEqualTo(customer);
        verify(customerRepository).findById(id);
    }

    @Test
    void should_throw_when_customer_not_found() {
        UUID id = UUID.randomUUID();

        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        ThrowingCallable execute = () -> findCustomerUseCase.execute(id);

        assertThatThrownBy(execute).isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(id.toString());
        verify(customerRepository).findById(id);
    }
}