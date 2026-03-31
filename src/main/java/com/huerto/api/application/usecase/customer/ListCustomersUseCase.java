package com.huerto.api.application.usecase.customer;

import com.huerto.api.domain.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListCustomersUseCase {
    Page<Customer> execute(Pageable pageable);
}