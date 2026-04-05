package com.huerto.api.application;

import com.huerto.api.application.commands.CreateCustomerCommand;
import com.huerto.api.application.impl.customer.CreateCustomerUseCaseImpl;
import com.huerto.api.domain.exception.DuplicateEmailException;
import com.huerto.api.domain.model.Customer;
import com.huerto.api.domain.ports.out.CustomerRepository;
import com.huerto.api.util.CustomerTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateCustomerUseCaseTest {

    @Mock
    CustomerRepository customerRepository;
    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    CreateCustomerUseCaseImpl useCase;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void should_create_customer_successfully() {
        CreateCustomerCommand command = CustomerTestFactory.buildCreateCommand();

        when(customerRepository.existsByEmail("john@huerto.com")).thenReturn(false);
        when(passwordEncoder.encode("secret1234")).thenReturn("hashed");
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        Customer result = useCase.execute(command);

        assertThat(result.credentials().email().value()).isEqualTo("john@huerto.com");
        assertThat(result.name()).isEqualTo("John Doe");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void should_throw_when_email_already_exists() {
        CreateCustomerCommand command = CustomerTestFactory.buildCreateCommand();

        when(customerRepository.existsByEmail("john@huerto.com")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(DuplicateEmailException.class);

        verify(customerRepository, never()).save(any());
    }
}