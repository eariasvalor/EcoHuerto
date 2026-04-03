package com.huerto.api.application.impl.customer;

import com.huerto.api.application.commands.CreateCustomerCommand;
import com.huerto.api.application.usecase.customer.CreateCustomerUseCase;
import com.huerto.api.domain.exception.DuplicateEmailException;
import com.huerto.api.domain.model.Customer;
import com.huerto.api.domain.ports.out.CustomerRepository;
import com.huerto.api.domain.valueobject.Credentials;
import com.huerto.api.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateCustomerUseCaseImpl implements CreateCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Customer execute(CreateCustomerCommand command) {
        if (customerRepository.existsByEmail(command.email())) {
            throw new DuplicateEmailException(command.email());
        }

        Credentials credentials = new Credentials(
                new Email(command.email()),
                passwordEncoder.encode(command.rawPassword())
        );

        Customer customer = new Customer(
                UUID.randomUUID(),
                command.name(),
                credentials,
                LocalDateTime.now(),
                0
        );

        return customerRepository.save(customer);
    }
}