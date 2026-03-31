package com.huerto.api.application.impl.customer;

import com.huerto.api.application.usecase.customer.ListCustomersUseCase;
import com.huerto.api.domain.model.Customer;
import com.huerto.api.domain.ports.out.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListCustomersUseCaseImpl implements ListCustomersUseCase {

    private final CustomerRepository customerRepository;

    public ListCustomersUseCaseImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Page<Customer> execute(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }
}
