package com.huerto.api.infrastructure.adapters.in.web;

import com.huerto.api.application.commands.UpdateCustomerCommand;
import com.huerto.api.application.usecase.customer.FindCustomerUseCase;
import com.huerto.api.application.usecase.customer.ListCustomersUseCase;
import com.huerto.api.application.usecase.customer.UpdateCustomerUseCase;
import com.huerto.api.infrastructure.adapters.in.web.dto.CustomerResponse;
import com.huerto.api.infrastructure.adapters.in.web.dto.UpdateCustomerRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customers", description = "Customer management")
public class CustomerController {

    private final FindCustomerUseCase findCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final ListCustomersUseCase listCustomersUseCase;

    public CustomerController(FindCustomerUseCase findCustomerUseCase,
                              UpdateCustomerUseCase updateCustomerUseCase,
                              ListCustomersUseCase listCustomersUseCase) {
        this.findCustomerUseCase = findCustomerUseCase;
        this.updateCustomerUseCase = updateCustomerUseCase;
        this.listCustomersUseCase = listCustomersUseCase;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find a customer by id")
            @ApiResponse(responseCode = "200", description = "Customer found")
            @ApiResponse(responseCode = "404", description = "Customer not found")
    public CustomerResponse findById(
            @Parameter(description = "Customer UUID") @PathVariable UUID id) {
        return CustomerResponse.from(findCustomerUseCase.execute(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer name or password")
            @ApiResponse(responseCode = "200", description = "Customer updated")
            @ApiResponse(responseCode = "400", description = "Invalid request body")
            @ApiResponse(responseCode = "404", description = "Customer not found")
    public CustomerResponse update(
            @Parameter(description = "Customer UUID") @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerRequest request) {
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