package com.huerto.api.application;

import com.huerto.api.application.commands.UpdateCustomerCommand;
import com.huerto.api.application.impl.customer.UpdateCustomerUseCaseImpl;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Customer;
import com.huerto.api.domain.ports.out.CustomerRepository;
import com.huerto.api.domain.ports.out.PasswordHasher;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateCustomerUseCaseTest {

    @Mock CustomerRepository customerRepository;
    @Mock PasswordHasher passwordHasher;
    @InjectMocks UpdateCustomerUseCaseImpl updateCustomerUseCase;


    @Test
    void should_update_name_only_when_password_is_null() {
        UUID id = UUID.randomUUID();
        Customer customer = CustomerTestFactory.buildCustomer(id);
        UpdateCustomerCommand command = CustomerTestFactory.buildUpdateCommand(id,  null);

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Customer result = updateCustomerUseCase.execute(command);

        assertThat(result.name()).isEqualTo("John Updated");
        assertThat(result.credentials().passwordHash()).isEqualTo("hashed_password");
        verify(passwordHasher, never()).hash(any());
    }

    @Test
    void should_update_name_and_password_when_password_provided() {
        UUID id = UUID.randomUUID();
        Customer customer = CustomerTestFactory.buildCustomer(id);
        UpdateCustomerCommand command = CustomerTestFactory.buildUpdateCommand(id, "newpassword");

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        when(passwordHasher.hash("newpassword")).thenReturn("new_hashed_password");
        when(customerRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Customer result = updateCustomerUseCase.execute(command);

        assertThat(result.name()).isEqualTo("John Updated");
        assertThat(result.credentials().passwordHash()).isEqualTo("new_hashed_password");
        verify(passwordHasher).hash("newpassword");
    }

    @Test
    void should_throw_when_customer_not_found() {
        UUID id = UUID.randomUUID();
        UpdateCustomerCommand command = CustomerTestFactory.buildUpdateCommand(id);

        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        ThrowingCallable execute = () -> updateCustomerUseCase.execute(command);

        assertThatThrownBy(execute).isInstanceOf(ResourceNotFoundException.class);
        verify(customerRepository, never()).save(any());
    }
}