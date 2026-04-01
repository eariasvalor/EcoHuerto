package com.huerto.api.application.impl.auth;

import com.huerto.api.application.commands.LoginCommand;
import com.huerto.api.application.usecase.auth.LoginAdminUseCase;
import com.huerto.api.domain.exception.InactiveAdminException;
import com.huerto.api.domain.exception.InvalidCredentialsException;
import com.huerto.api.domain.model.Administrator;
import com.huerto.api.domain.ports.out.AdministratorRepository;
import com.huerto.api.domain.ports.out.PasswordHasher;
import com.huerto.api.domain.ports.out.TokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LoginAdminUseCaseImpl implements LoginAdminUseCase {

    private final AdministratorRepository administratorRepository;
    private final PasswordHasher passwordHasher;
    private final TokenProvider tokenProvider;

    public LoginAdminUseCaseImpl(AdministratorRepository administratorRepository,
                                 PasswordHasher passwordHasher,
                                 TokenProvider tokenProvider) {
        this.administratorRepository = administratorRepository;
        this.passwordHasher = passwordHasher;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public String execute(LoginCommand command) {
        Administrator admin = administratorRepository.findByEmail(command.email())
                .orElseThrow(InvalidCredentialsException::new);

        boolean valid = admin.credentials()
                .authenticate(command.rawPassword(), passwordHasher);

        if (!valid) throw new InvalidCredentialsException();
        if (!admin.active()) throw new InactiveAdminException();

        return tokenProvider.generateToken(
                admin.id(),
                admin.credentials().email().value(),
                "ADMIN"
        );
    }
}
