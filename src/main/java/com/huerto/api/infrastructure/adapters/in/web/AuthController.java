package com.huerto.api.infrastructure.adapters.in.web;

import com.huerto.api.application.commands.LoginCommand;
import com.huerto.api.application.commands.RegisterCustomerCommand;
import com.huerto.api.application.usecase.auth.LoginAdminUseCase;
import com.huerto.api.application.usecase.auth.LoginCustomerUseCase;
import com.huerto.api.application.usecase.auth.RegisterCustomerUseCase;
import com.huerto.api.infrastructure.adapters.in.web.dto.CustomerResponse;
import com.huerto.api.infrastructure.adapters.in.web.dto.LoginRequest;
import com.huerto.api.infrastructure.adapters.in.web.dto.RegisterCustomerRequest;
import com.huerto.api.infrastructure.adapters.in.web.dto.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Authentication and registration")
public class AuthController {

    private final RegisterCustomerUseCase registerCustomerUseCase;
    private final LoginCustomerUseCase loginCustomerUseCase;
    private final LoginAdminUseCase loginAdminUseCase;

    public AuthController(RegisterCustomerUseCase registerCustomerUseCase,
                          LoginCustomerUseCase loginCustomerUseCase,
                          LoginAdminUseCase loginAdminUseCase) {
        this.registerCustomerUseCase = registerCustomerUseCase;
        this.loginCustomerUseCase = loginCustomerUseCase;
        this.loginAdminUseCase = loginAdminUseCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new customer")
            @ApiResponse(responseCode = "201", description = "Customer registered")
            @ApiResponse(responseCode = "400", description = "Invalid request body")
            @ApiResponse(responseCode = "409", description = "Email already registered")
    public CustomerResponse register(@Valid @RequestBody RegisterCustomerRequest request) {
        RegisterCustomerCommand command = new RegisterCustomerCommand(
                request.name(),
                request.email(),
                request.rawPassword()
        );
        return CustomerResponse.from(registerCustomerUseCase.execute(command));
    }

    @PostMapping("/login/customer")
    @Operation(summary = "Login as customer")
            @ApiResponse(responseCode = "200", description = "JWT token returned")
            @ApiResponse(responseCode = "400", description = "Invalid request body")
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public TokenResponse loginCustomer(@Valid @RequestBody LoginRequest request) {
        LoginCommand command = new LoginCommand(request.email(), request.rawPassword());
        return new TokenResponse(loginCustomerUseCase.execute(command));
    }

    @PostMapping("/login/admin")
    @Operation(summary = "Login as administrator")
            @ApiResponse(responseCode = "200", description = "JWT token returned")
            @ApiResponse(responseCode = "400", description = "Invalid request body")
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
            @ApiResponse(responseCode = "403", description = "Administrator account inactive")
    public TokenResponse loginAdmin(@Valid @RequestBody LoginRequest request) {
        LoginCommand command = new LoginCommand(request.email(), request.rawPassword());
        return new TokenResponse(loginAdminUseCase.execute(command));
    }
}