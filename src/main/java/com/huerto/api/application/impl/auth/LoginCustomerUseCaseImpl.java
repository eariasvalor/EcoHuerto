package com.huerto.api.application.impl.auth;

import com.huerto.api.application.commands.LoginCommand;
import com.huerto.api.application.usecase.auth.LoginCustomerUseCase;
import com.huerto.api.domain.exception.InvalidCredentialsException;
import com.huerto.api.domain.model.Customer;
import com.huerto.api.domain.ports.out.CustomerRepository;
import com.huerto.api.domain.ports.out.PasswordHasher;
import com.huerto.api.domain.ports.out.TokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LoginCustomerUseCaseImpl implements LoginCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final PasswordHasher passwordHasher;
    private final TokenProvider tokenProvider;

    public LoginCustomerUseCaseImpl(CustomerRepository customerRepository,
                                    PasswordHasher passwordHasher,
                                    TokenProvider tokenProvider) {
        this.customerRepository = customerRepository;
        this.passwordHasher = passwordHasher;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public String execute(LoginCommand command) {
        Customer customer = customerRepository.findByEmail(command.email())
                .orElseThrow(InvalidCredentialsException::new);

        boolean valid = customer.credentials()
                .authenticate(command.rawPassword(), passwordHasher);

        if (!valid) throw new InvalidCredentialsException();

        return tokenProvider.generateToken(
                customer.id(),
                customer.credentials().email().value(),
                "CUSTOMER"
        );
    }
}