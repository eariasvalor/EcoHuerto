package com.huerto.api.application;

import com.huerto.api.application.impl.customer.ListCustomersUseCaseImpl;
import com.huerto.api.domain.model.Customer;
import com.huerto.api.domain.ports.out.CustomerRepository;
import com.huerto.api.domain.valueobject.Credentials;
import com.huerto.api.domain.valueobject.Email;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListCustomersUseCaseTest {

    @Mock CustomerRepository customerRepository;
    @InjectMocks ListCustomersUseCaseImpl listCustomersUseCase;

    private Customer buildCustomer() {
        Credentials credentials = new Credentials(
                new Email("john@huerto.com"), "hashed_password"
        );
        return new Customer(UUID.randomUUID(), "John Doe", credentials, LocalDateTime.now(), 0);
    }

    @Test
    void should_return_paginated_customers() {
        Pageable pageable = PageRequest.of(0, 10);
        Customer customer = buildCustomer();
        Page<Customer> page = new PageImpl<>(List.of(customer), pageable, 1);

        when(customerRepository.findAll(pageable)).thenReturn(page);

        Page<Customer> result = listCustomersUseCase.execute(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(customerRepository).findAll(pageable);
    }

    @Test
    void should_return_empty_page_when_no_customers() {
        Pageable pageable = PageRequest.of(0, 10);

        when(customerRepository.findAll(pageable)).thenReturn(Page.empty(pageable));

        Page<Customer> result = listCustomersUseCase.execute(pageable);

        assertThat(result.getContent()).isEmpty();
    }
}
