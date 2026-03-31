package com.huerto.api.application.impl.customer;

import com.fasterxml.uuid.Generators;
import com.huerto.api.application.commands.RegisterCustomerCommand;
import com.huerto.api.application.usecase.customer.RegisterCustomerUseCase;
import com.huerto.api.domain.exception.DuplicateEmailException;
import com.huerto.api.domain.model.Customer;
import com.huerto.api.domain.ports.out.CustomerRepository;
import com.huerto.api.domain.ports.out.PasswordHasher;
import com.huerto.api.domain.valueobject.Credentials;
import com.huerto.api.domain.valueobject.Email;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class RegisterCustomerUseCaseImpl implements RegisterCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final PasswordHasher passwordHasher;

    public RegisterCustomerUseCaseImpl(CustomerRepository customerRepository,
                                       PasswordHasher passwordHasher) {
        this.customerRepository = customerRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public Customer execute(RegisterCustomerCommand command) {
        if (customerRepository.existsByEmail(command.email()))
            throw new DuplicateEmailException(command.email());

        Email email = new Email(command.email());
        Credentials credentials = Credentials.create(email, command.rawPassword(), passwordHasher);

        Customer customer = new Customer(
                Generators.timeBasedEpochGenerator().generate(),
                command.name(),
                credentials,
                LocalDateTime.now(),
                0
        );

        return customerRepository.save(customer);
    }
}
