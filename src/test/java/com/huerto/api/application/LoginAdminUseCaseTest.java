package com.huerto.api.application;

import com.huerto.api.application.commands.LoginCommand;
import com.huerto.api.application.impl.auth.LoginAdminUseCaseImpl;
import com.huerto.api.domain.exception.InactiveAdminException;
import com.huerto.api.domain.exception.InvalidCredentialsException;
import com.huerto.api.domain.model.Administrator;
import com.huerto.api.domain.enums.AdminPermission;
import com.huerto.api.domain.ports.out.AdministratorRepository;
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
class LoginAdminUseCaseTest {

    @Mock AdministratorRepository administratorRepository;
    @Mock PasswordHasher passwordHasher;
    @Mock TokenProvider tokenProvider;
    @InjectMocks LoginAdminUseCaseImpl loginAdminUseCase;

    private Administrator buildAdmin(UUID id, boolean active) {
        Credentials credentials = new Credentials(
                new Email("admin@huerto.com"), "hashed_password"
        );
        return new Administrator(
                id, "Admin", credentials, AdminPermission.OWNER, active, LocalDateTime.now(), 0
        );
    }

    @Test
    void should_return_token_when_admin_credentials_are_valid() {
        UUID id = UUID.randomUUID();
        Administrator admin = buildAdmin(id, true);
        LoginCommand command = new LoginCommand("admin@huerto.com", "secret1234");

        when(administratorRepository.findByEmail("admin@huerto.com"))
                .thenReturn(Optional.of(admin));
        when(passwordHasher.verify("secret1234", "hashed_password")).thenReturn(true);
        when(tokenProvider.generateToken(id, "admin@huerto.com", "OWNER"))
                .thenReturn("admin.jwt.token");

        String token = loginAdminUseCase.execute(command);

        assertThat(token).isEqualTo("admin.jwt.token");
    }

    @Test
    void should_throw_when_email_not_found() {
        LoginCommand command = new LoginCommand("unknown@huerto.com", "secret1234");

        when(administratorRepository.findByEmail("unknown@huerto.com"))
                .thenReturn(Optional.empty());

        ThrowingCallable execute = () -> loginAdminUseCase.execute(command);

        assertThatThrownBy(execute).isInstanceOf(InvalidCredentialsException.class);
        verify(tokenProvider, never()).generateToken(any(), any(), any());
    }

    @Test
    void should_throw_when_password_is_wrong() {
        UUID id = UUID.randomUUID();
        Administrator admin = buildAdmin(id, true);
        LoginCommand command = new LoginCommand("admin@huerto.com", "wrongpassword");

        when(administratorRepository.findByEmail("admin@huerto.com"))
                .thenReturn(Optional.of(admin));
        when(passwordHasher.verify("wrongpassword", "hashed_password")).thenReturn(false);

        ThrowingCallable execute = () -> loginAdminUseCase.execute(command);

        assertThatThrownBy(execute).isInstanceOf(InvalidCredentialsException.class);
        verify(tokenProvider, never()).generateToken(any(), any(), any());
    }

    @Test
    void should_throw_when_admin_is_inactive() {
        UUID id = UUID.randomUUID();
        Administrator admin = buildAdmin(id, false);
        LoginCommand command = new LoginCommand("admin@huerto.com", "secret1234");

        when(administratorRepository.findByEmail("admin@huerto.com"))
                .thenReturn(Optional.of(admin));
        when(passwordHasher.verify("secret1234", "hashed_password")).thenReturn(true);

        ThrowingCallable execute = () -> loginAdminUseCase.execute(command);

        assertThatThrownBy(execute).isInstanceOf(InactiveAdminException.class);
        verify(tokenProvider, never()).generateToken(any(), any(), any());
    }
}