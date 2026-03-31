package com.huerto.api.application;

import com.huerto.api.application.commands.LoginCommand;
import com.huerto.api.application.impl.auth.LoginCustomerUseCaseImpl;
import com.huerto.api.domain.exception.InvalidCredentialsException;
import com.huerto.api.domain.model.Customer;
import com.huerto.api.domain.ports.out.CustomerRepository;
import com.huerto.api.domain.ports.out.PasswordHasher;
import com.huerto.api.domain.ports.out.TokenProvider;
import com.huerto.api.domain.valueobject.Credentials;
import com.huerto.api.domain.valueobject.Email;
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
class LoginCustomerUseCaseTest {

    @Mock CustomerRepository customerRepository;
    @Mock PasswordHasher passwordHasher;
    @Mock TokenProvider tokenProvider;
    @InjectMocks LoginCustomerUseCaseImpl loginCustomerUseCase;

    private Customer buildCustomer(UUID id) {
        Credentials credentials = new Credentials(
                new Email("john@huerto.com"), "hashed_password"
        );
        return new Customer(id, "John Doe", credentials, LocalDateTime.now(), 0);
    }

    @Test
    void should_return_token_when_credentials_are_valid() {
        UUID id = UUID.randomUUID();
        Customer customer = buildCustomer(id);
        LoginCommand command = new LoginCommand("john@huerto.com", "secret1234");

        when(customerRepository.findByEmail("john@huerto.com"))
                .thenReturn(Optional.of(customer));
        when(passwordHasher.verify("secret1234", "hashed_password")).thenReturn(true);
        when(tokenProvider.generateToken(id, "john@huerto.com", "CUSTOMER"))
                .thenReturn("jwt.token.here");

        String token = loginCustomerUseCase.execute(command);

        assertThat(token).isEqualTo("jwt.token.here");
    }

    @Test
    void should_throw_when_email_not_found() {
        LoginCommand command = new LoginCommand("unknown@huerto.com", "secret1234");

        when(customerRepository.findByEmail("unknown@huerto.com"))
                .thenReturn(Optional.empty());

        ThrowingCallable execute = () -> loginCustomerUseCase.execute(command);

        assertThatThrownBy(execute).isInstanceOf(InvalidCredentialsException.class);
        verify(tokenProvider, never()).generateToken(any(), any(), any());
    }

    @Test
    void should_throw_when_password_is_wrong() {
        UUID id = UUID.randomUUID();
        Customer customer = buildCustomer(id);
        LoginCommand command = new LoginCommand("john@huerto.com", "wrongpassword");

        when(customerRepository.findByEmail("john@huerto.com"))
                .thenReturn(Optional.of(customer));
        when(passwordHasher.verify("wrongpassword", "hashed_password")).thenReturn(false);

        ThrowingCallable execute = () -> loginCustomerUseCase.execute(command);

        assertThatThrownBy(execute).isInstanceOf(InvalidCredentialsException.class);
        verify(tokenProvider, never()).generateToken(any(), any(), any());
    }
}