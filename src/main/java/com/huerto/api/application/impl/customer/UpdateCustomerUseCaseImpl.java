package com.huerto.api.application.impl.customer;

import com.huerto.api.application.commands.UpdateCustomerCommand;
import com.huerto.api.application.usecase.customer.UpdateCustomerUseCase;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Customer;
import com.huerto.api.domain.ports.out.CustomerRepository;
import com.huerto.api.domain.ports.out.PasswordHasher;
import com.huerto.api.domain.valueobject.Credentials;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateCustomerUseCaseImpl implements UpdateCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final PasswordHasher passwordHasher;

    public UpdateCustomerUseCaseImpl(CustomerRepository customerRepository,
                                     PasswordHasher passwordHasher) {
        this.customerRepository = customerRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public Customer execute(UpdateCustomerCommand command) {
        Customer customer = customerRepository.findById(command.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", command.customerId()));

        Customer updated = customer.updateName(command.name());

        if (command.rawPassword() != null) {
            Credentials newCredentials = Credentials.create(
                    customer.credentials().email(),
                    command.rawPassword(),
                    passwordHasher
            );
            updated = updated.updateCredentials(newCredentials);
        }

        return customerRepository.save(updated);
    }
}
