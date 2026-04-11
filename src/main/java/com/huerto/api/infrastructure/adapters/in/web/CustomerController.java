package com.huerto.api.infrastructure.adapters.in.web;

import com.huerto.api.application.commands.CreateCustomerCommand;
import com.huerto.api.application.commands.UpdateCustomerCommand;
import com.huerto.api.application.usecase.customer.*;
import com.huerto.api.domain.model.Customer;
import com.huerto.api.infrastructure.adapters.in.web.dto.CreateCustomerRequest;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final CreateCustomerUseCase createCustomerUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;

    public CustomerController(FindCustomerUseCase findCustomerUseCase,
                              UpdateCustomerUseCase updateCustomerUseCase,
                              ListCustomersUseCase listCustomersUseCase,
                              SecurityContext securityContext,
                              CreateCustomerUseCase createCustomerUseCase,
                              DeleteCustomerUseCase deleteCustomerUseCase) {
        this.findCustomerUseCase = findCustomerUseCase;
        this.updateCustomerUseCase = updateCustomerUseCase;
        this.listCustomersUseCase = listCustomersUseCase;
        this.securityContext = securityContext;
        this.createCustomerUseCase = createCustomerUseCase;
        this.deleteCustomerUseCase = deleteCustomerUseCase;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find a customer by ID")
    @ApiResponse(responseCode = "200", description = "Customer found")
    @ApiResponse(responseCode = "403", description = "Cannot access another customer's profile")
    @ApiResponse(responseCode = "404", description = "Customer not found")
    public CustomerResponse findById(@Parameter(description = "Customer UUID") @PathVariable UUID id) {
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
                id,
                request.name(),
                request.rawPassword(),
                request.phoneCountryCode(),
                request.phoneNumber(),
                request.addressStreetType(),
                request.addressStreet(),
                request.addressNumber(),
                request.addressFloor(),
                request.addressCity(),
                request.addressPostalCode(),
                request.addressProvince()
        );
        return CustomerResponse.from(updateCustomerUseCase.execute(command));
    }

    @GetMapping
    @Operation(summary = "List all customers")
    @ApiResponse(responseCode = "200", description = "Paginated customer list")
    public Page<CustomerResponse> list(Pageable pageable) {
        return listCustomersUseCase.execute(pageable).map(CustomerResponse::from);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new customer (admin only)")
    @ApiResponse(responseCode = "201", description = "Customer created")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    @ApiResponse(responseCode = "409", description = "Email already in use")
    public ResponseEntity<CustomerResponse> create(@RequestBody @Valid CreateCustomerRequest request) {
        CreateCustomerCommand command = new CreateCustomerCommand(
                request.name(),
                request.email(),
                request.password(),
                request.phoneCountryCode(),
                request.phoneNumber(),
                request.addressStreetType(),
                request.addressStreet(),
                request.addressNumber(),
                request.addressFloor(),
                request.addressCity(),
                request.addressPostalCode(),
                request.addressProvince()
        );
        Customer customer = createCustomerUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerResponse.from(customer));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a customer (admin only)")
    @ApiResponse(responseCode = "204", description = "Customer deleted")
    @ApiResponse(responseCode = "404", description = "Customer not found")
    public ResponseEntity<Void> delete(@Parameter(description = "Customer UUID") @PathVariable UUID id) {
        deleteCustomerUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

}