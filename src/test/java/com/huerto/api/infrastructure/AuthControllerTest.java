package com.huerto.api.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huerto.api.application.usecase.auth.RegisterCustomerUseCase;
import com.huerto.api.application.usecase.auth.LoginCustomerUseCase;
import com.huerto.api.domain.exception.DuplicateEmailException;
import com.huerto.api.domain.exception.InvalidCredentialsException;
import com.huerto.api.domain.valueobject.Credentials;
import com.huerto.api.domain.valueobject.Email;
import com.huerto.api.domain.model.Customer;
import com.huerto.api.infrastructure.adapters.in.web.AuthController;
import com.huerto.api.infrastructure.adapters.in.web.dto.LoginRequest;
import com.huerto.api.infrastructure.adapters.in.web.dto.RegisterCustomerRequest;
import com.huerto.api.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = AuthController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityConfig.class
        )
)
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean RegisterCustomerUseCase registerCustomerUseCase;
    @MockBean LoginCustomerUseCase loginCustomerUseCase;

    @Test
    void should_return_201_when_customer_is_registered() throws Exception {
        UUID id = UUID.randomUUID();
        RegisterCustomerRequest request = new RegisterCustomerRequest(
                "John Doe", "john@huerto.com", "secret1234"
        );

        Credentials credentials = new Credentials(
                new Email("john@huerto.com"), "hashed_password"
        );
        Customer created = new Customer(
                id, "John Doe", credentials, LocalDateTime.now(), 0
        );

        when(registerCustomerUseCase.execute(any())).thenReturn(created);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@huerto.com"));
    }

    @Test
    void should_return_400_when_name_is_blank() throws Exception {
        RegisterCustomerRequest request = new RegisterCustomerRequest(
                "", "john@huerto.com", "secret1234"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_400_when_email_is_invalid() throws Exception {
        RegisterCustomerRequest request = new RegisterCustomerRequest(
                "John Doe", "not-an-email", "secret1234"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_400_when_password_too_short() throws Exception {
        RegisterCustomerRequest request = new RegisterCustomerRequest(
                "John Doe", "john@huerto.com", "short"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_409_when_email_already_exists() throws Exception {
        RegisterCustomerRequest request = new RegisterCustomerRequest(
                "John Doe", "john@huerto.com", "secret1234"
        );

        when(registerCustomerUseCase.execute(any()))
                .thenThrow(new DuplicateEmailException("john@huerto.com"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void should_return_200_with_token_when_credentials_are_valid() throws Exception {
        LoginRequest request = new LoginRequest("john@huerto.com", "secret1234");

        when(loginCustomerUseCase.execute(any())).thenReturn("jwt.token.here");

        mockMvc.perform(post("/api/v1/auth/login/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token.here"));
    }

    @Test
    void should_return_401_when_credentials_are_invalid() throws Exception {
        LoginRequest request = new LoginRequest("john@huerto.com", "wrongpassword");

        when(loginCustomerUseCase.execute(any()))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/v1/auth/login/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void should_return_400_when_login_request_is_invalid() throws Exception {
        LoginRequest request = new LoginRequest("", "");

        mockMvc.perform(post("/api/v1/auth/login/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}