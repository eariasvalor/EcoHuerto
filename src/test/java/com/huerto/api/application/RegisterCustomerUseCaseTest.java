package com.huerto.api.application;

import com.huerto.api.application.commands.RegisterCustomerCommand;
import com.huerto.api.application.impl.auth.RegisterCustomerUseCaseImpl;
import com.huerto.api.domain.exception.DuplicateEmailException;
import com.huerto.api.domain.model.Customer;
import com.huerto.api.domain.ports.out.CustomerRepository;
import com.huerto.api.domain.ports.out.PasswordHasher;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterCustomerUseCaseTest {

    @Mock CustomerRepository customerRepository;
    @Mock PasswordHasher passwordHasher;
    @InjectMocks RegisterCustomerUseCaseImpl registerCustomerUseCase;

    @Test
    void should_register_customer_when_email_is_new() {
        RegisterCustomerCommand command = new RegisterCustomerCommand(
                "John Doe", "john@huerto.com", "secret1234"
        );

        when(customerRepository.existsByEmail("john@huerto.com")).thenReturn(false);
        when(passwordHasher.hash(any())).thenReturn("hashed_password");
        when(customerRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Customer result = registerCustomerUseCase.execute(command);

        assertThat(result.name()).isEqualTo("John Doe");
        assertThat(result.credentials().email().value()).isEqualTo("john@huerto.com");
        assertThat(result.id()).isNotNull();
        verify(customerRepository).save(any());
    }

    @Test
    void should_throw_when_email_already_exists() {
        RegisterCustomerCommand command = new RegisterCustomerCommand(
                "John Doe", "john@huerto.com", "secret1234"
        );

        when(customerRepository.existsByEmail("john@huerto.com")).thenReturn(true);

        ThrowingCallable execute = () -> registerCustomerUseCase.execute(command);

        assertThatThrownBy(execute).isInstanceOf(DuplicateEmailException.class);
        verify(customerRepository, never()).save(any());
    }
}