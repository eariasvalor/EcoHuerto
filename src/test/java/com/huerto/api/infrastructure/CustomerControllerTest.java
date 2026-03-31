package com.huerto.api.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huerto.api.application.usecase.customer.FindCustomerUseCase;
import com.huerto.api.application.usecase.customer.UpdateCustomerUseCase;
import com.huerto.api.infrastructure.adapters.in.web.dto.UpdateCustomerRequest;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Customer;
import com.huerto.api.domain.valueobject.Credentials;
import com.huerto.api.domain.valueobject.Email;
import com.huerto.api.infrastructure.adapters.in.web.CustomerController;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@WebMvcTest(
        value = CustomerController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityConfig.class
        )
)
class CustomerControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean FindCustomerUseCase findCustomerUseCase;
    @MockBean UpdateCustomerUseCase updateCustomerUseCase;

    private Customer buildCustomer(UUID id) {
        Credentials credentials = new Credentials(
                new Email("john@huerto.com"), "hashed_password"
        );
        return new Customer(id, "John Doe", credentials, LocalDateTime.now(), 0);
    }

    @Test
    void should_return_200_when_customer_exists() throws Exception {
        UUID id = UUID.randomUUID();
        Customer customer = buildCustomer(id);

        when(findCustomerUseCase.execute(id)).thenReturn(customer);

        mockMvc.perform(get("/api/v1/customers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@huerto.com"));
    }

    @Test
    void should_return_404_when_customer_not_found() throws Exception {
        UUID id = UUID.randomUUID();

        when(findCustomerUseCase.execute(id))
                .thenThrow(new ResourceNotFoundException("Customer", id));

        mockMvc.perform(get("/api/v1/customers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_200_when_customer_is_updated() throws Exception {
        UUID id = UUID.randomUUID();
        Credentials credentials = new Credentials(
                new Email("john@huerto.com"), "hashed_password"
        );
        Customer updated = new Customer(id, "John Updated", credentials, LocalDateTime.now(), 1);

        UpdateCustomerRequest request = new UpdateCustomerRequest("John Updated", null);

        when(updateCustomerUseCase.execute(any())).thenReturn(updated);

        mockMvc.perform(put("/api/v1/customers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("John Updated"));
    }

    @Test
    void should_return_404_when_customer_not_found_on_update() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateCustomerRequest request = new UpdateCustomerRequest("John Updated", null);

        when(updateCustomerUseCase.execute(any()))
                .thenThrow(new ResourceNotFoundException("Customer", id));

        mockMvc.perform(put("/api/v1/customers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_400_when_name_is_blank_on_update() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateCustomerRequest request = new UpdateCustomerRequest("", null);

        mockMvc.perform(put("/api/v1/customers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}