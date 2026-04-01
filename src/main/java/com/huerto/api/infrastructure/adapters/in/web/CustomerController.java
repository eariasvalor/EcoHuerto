package com.huerto.api.infrastructure.adapters.in.web;

import com.huerto.api.application.commands.UpdateCustomerCommand;
import com.huerto.api.application.usecase.customer.FindCustomerUseCase;
import com.huerto.api.application.usecase.customer.ListCustomersUseCase;
import com.huerto.api.application.usecase.customer.UpdateCustomerUseCase;
import com.huerto.api.infrastructure.adapters.in.web.dto.CustomerResponse;
import com.huerto.api.infrastructure.adapters.in.web.dto.UpdateCustomerRequest;
import com.huerto.api.infrastructure.config.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customers", description = "Customer management")
public class CustomerController {

    private final FindCustomerUseCase findCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final ListCustomersUseCase listCustomersUseCase;
    private final SecurityContext securityContext;

    public CustomerController(FindCustomerUseCase findCustomerUseCase,
                              UpdateCustomerUseCase updateCustomerUseCase,
                              ListCustomersUseCase listCustomersUseCase,
                              SecurityContext securityContext) {
        this.findCustomerUseCase = findCustomerUseCase;
        this.updateCustomerUseCase = updateCustomerUseCase;
        this.listCustomersUseCase = listCustomersUseCase;
        this.securityContext = securityContext;
    }

    @GetMapping("/{id}")
    public CustomerResponse findById(@PathVariable UUID id) {
        UUID requesterId = securityContext.getCurrentUserId();
        boolean isAdmin  = securityContext.isAdmin();

        if (!isAdmin && !requesterId.equals(id))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can only access your own profile");

        return CustomerResponse.from(findCustomerUseCase.execute(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer name or password")
            @ApiResponse(responseCode = "200", description = "Customer updated")
            @ApiResponse(responseCode = "403", description = "Cannot update another customer's profile")
            @ApiResponse(responseCode = "400", description = "Invalid request body")
            @ApiResponse(responseCode = "404", description = "Customer not found")
    public CustomerResponse update(
            @Parameter(description = "Customer UUID") @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerRequest request) {

        UUID requesterId = securityContext.getCurrentUserId();
        boolean isAdmin  = securityContext.isAdmin();

        if (!isAdmin && !requesterId.equals(id))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can only update your own profile");

        UpdateCustomerCommand command = new UpdateCustomerCommand(
                id, request.name(), request.rawPassword()
        );
        return CustomerResponse.from(updateCustomerUseCase.execute(command));
    }

    @GetMapping
    @Operation(summary = "List all customers")
    @ApiResponse(responseCode = "200", description = "Paginated customer list")
    public Page<CustomerResponse> list(Pageable pageable) {
        return listCustomersUseCase.execute(pageable).map(CustomerResponse::from);
    }

}