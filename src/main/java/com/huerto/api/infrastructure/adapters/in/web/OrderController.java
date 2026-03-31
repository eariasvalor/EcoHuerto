package com.huerto.api.infrastructure.adapters.in.web;

import com.huerto.api.application.commands.CreateOrderCommand;
import com.huerto.api.application.usecase.order.CreateOrderUseCase;
import com.huerto.api.application.usecase.order.ListOrdersUseCase;
import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.infrastructure.adapters.in.web.dto.CreateOrderRequest;
import com.huerto.api.infrastructure.adapters.in.web.dto.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Order management")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final ListOrdersUseCase listOrdersUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase,
                           ListOrdersUseCase listOrdersUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.listOrdersUseCase = listOrdersUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new order")
            @ApiResponse(responseCode = "201", description = "Order created")
            @ApiResponse(responseCode = "400", description = "Invalid request body")
            @ApiResponse(responseCode = "404", description = "Product or customer not found")
            @ApiResponse(responseCode = "409", description = "Insufficient stock")
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        List<CreateOrderCommand.OrderLineCommand> lines = request.lines().stream()
                .map(l -> new CreateOrderCommand.OrderLineCommand(l.productId(), l.quantity()))
                .toList();
        CreateOrderCommand command = new CreateOrderCommand(request.customerId(), lines);
        return OrderResponse.from(createOrderUseCase.execute(command));
    }

    @GetMapping
    @Operation(summary = "List orders",
            description = "Filterable by status. Sortable by createdAt or status via sort param")
    @ApiResponse(responseCode = "200", description = "Paginated order list")
    public Page<OrderResponse> list(
            @RequestParam(required = false) OrderStatus status,
            Pageable pageable) {
        return listOrdersUseCase.execute(status, pageable).map(OrderResponse::from);
    }
}