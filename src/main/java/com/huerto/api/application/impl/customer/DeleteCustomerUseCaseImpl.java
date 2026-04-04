package com.huerto.api.application.impl.customer;

import com.huerto.api.application.usecase.customer.DeleteCustomerUseCase;
import com.huerto.api.domain.exception.CustomerNotFoundException;
import com.huerto.api.domain.ports.out.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteCustomerUseCaseImpl implements DeleteCustomerUseCase {

    private final CustomerRepository customerRepository;

    @Override
    public void execute(UUID customerId) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        customerRepository.deleteById(customerId);
    }
}
