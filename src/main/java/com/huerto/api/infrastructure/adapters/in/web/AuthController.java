package com.huerto.api.infrastructure.adapters.in.web;

import com.huerto.api.application.commands.RegisterCustomerCommand;
import com.huerto.api.application.usecase.customer.RegisterCustomerUseCase;
import com.huerto.api.infrastructure.adapters.in.web.dto.CustomerResponse;
import com.huerto.api.infrastructure.adapters.in.web.dto.RegisterCustomerRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Authentication and registration")
public class AuthController {

    private final RegisterCustomerUseCase registerCustomerUseCase;

    public AuthController(RegisterCustomerUseCase registerCustomerUseCase) {
        this.registerCustomerUseCase = registerCustomerUseCase;
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
}