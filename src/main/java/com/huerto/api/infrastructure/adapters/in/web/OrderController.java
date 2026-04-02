package com.huerto.api.infrastructure.adapters.in.web;

import com.huerto.api.application.commands.CreateOrderCommand;
import com.huerto.api.application.usecase.order.*;
import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.model.Order;
import com.huerto.api.infrastructure.adapters.in.web.dto.CreateOrderRequest;
import com.huerto.api.infrastructure.adapters.in.web.dto.OrderResponse;
import com.huerto.api.infrastructure.config.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Order management")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final ListOrdersUseCase listOrdersUseCase;
    private final FindOrderUseCase findOrderUseCase;
    private final ConfirmOrderUseCase confirmOrderUseCase;
    private final StartPreparationUseCase startPreparationUseCase;
    private final MarkReadyUseCase markReadyUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final SecurityContext securityContext;
    private final ListMyOrdersUseCase listMyOrdersUseCase;
    private final RevertOrderUseCase revertOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase,
                           ListOrdersUseCase listOrdersUseCase,
                           FindOrderUseCase findOrderUseCase,
                           ConfirmOrderUseCase confirmOrderUseCase,
                           StartPreparationUseCase startPreparationUseCase,
                           MarkReadyUseCase markReadyUseCase,
                           CancelOrderUseCase cancelOrderUseCase,
                           SecurityContext securityContext,
                           ListMyOrdersUseCase listMyOrdersUseCase,
                           RevertOrderUseCase revertOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.listOrdersUseCase = listOrdersUseCase;
        this.findOrderUseCase = findOrderUseCase;
        this.confirmOrderUseCase = confirmOrderUseCase;
        this.startPreparationUseCase = startPreparationUseCase;
        this.markReadyUseCase = markReadyUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.securityContext = securityContext;
        this.listMyOrdersUseCase = listMyOrdersUseCase;
        this.revertOrderUseCase = revertOrderUseCase;
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
        CreateOrderResult result = createOrderUseCase.execute(command);
        return OrderResponse.from(result.order(), result.possibleDuplicate());
    }

    @GetMapping
    @Operation(summary = "List all orders with optional filters")
    public Page<OrderResponse> listOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) UUID customerId,
            Pageable pageable) {
        return listOrdersUseCase.execute(status, customerId, pageable)
                .map(OrderResponse::from);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find an order by id")
            @ApiResponse(responseCode = "200", description = "Order found")
            @ApiResponse(responseCode = "403", description = "Cannot access another customer's order")
            @ApiResponse(responseCode = "404", description = "Order not found")
    public OrderResponse findById(
            @Parameter(description = "Order UUID") @PathVariable UUID id) {

        UUID requesterId = securityContext.getCurrentUserId();
        boolean isAdmin  = securityContext.isAdmin();

        Order order = findOrderUseCase.execute(id);

        if (!isAdmin && !order.customerId().equals(requesterId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can only access your own orders");

        return OrderResponse.from(order);
    }

    @PatchMapping("/{id}/confirm")
    @Operation(summary = "Confirm an order")
            @ApiResponse(responseCode = "200", description = "Order confirmed")
            @ApiResponse(responseCode = "404", description = "Order not found")
            @ApiResponse(responseCode = "422", description = "Invalid status transition")
    public OrderResponse confirm(
            @Parameter(description = "Order UUID") @PathVariable UUID id) {
        return OrderResponse.from(confirmOrderUseCase.execute(id));
    }

    @PatchMapping("/{id}/preparation")
    @Operation(summary = "Start order preparation")
            @ApiResponse(responseCode = "200", description = "Order in preparation")
            @ApiResponse(responseCode = "404", description = "Order not found")
            @ApiResponse(responseCode = "422", description = "Invalid status transition")
    public OrderResponse startPreparation(
            @Parameter(description = "Order UUID") @PathVariable UUID id) {
        return OrderResponse.from(startPreparationUseCase.execute(id));
    }

    @PatchMapping("/{id}/ready")
    @Operation(summary = "Mark order as ready for pickup")
            @ApiResponse(responseCode = "200", description = "Order ready for pickup")
            @ApiResponse(responseCode = "404", description = "Order not found")
            @ApiResponse(responseCode = "422", description = "Invalid status transition")
    public OrderResponse markReady(
            @Parameter(description = "Order UUID") @PathVariable UUID id) {
        return OrderResponse.from(markReadyUseCase.execute(id));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel an order")
            @ApiResponse(responseCode = "200", description = "Order cancelled")
            @ApiResponse(responseCode = "403", description = "Cannot cancel another customer's order")
            @ApiResponse(responseCode = "404", description = "Order not found")
            @ApiResponse(responseCode = "422", description = "Order already cancelled")
    public OrderResponse cancel(
            @Parameter(description = "Order UUID") @PathVariable UUID id) {

        UUID requesterId = securityContext.getCurrentUserId();
        boolean isAdmin  = securityContext.isAdmin();

        Order order = findOrderUseCase.execute(id);

        if (!isAdmin && !order.customerId().equals(requesterId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can only cancel your own orders");

        return OrderResponse.from(cancelOrderUseCase.execute(id));
    }

    @GetMapping("/my")
    @Operation(summary = "List my orders")
    @ApiResponse(responseCode = "200", description = "Paginated list of current customer orders")
    public Page<OrderResponse> listMyOrders(Pageable pageable) {
        UUID customerId = securityContext.getCurrentUserId();
        return listMyOrdersUseCase.execute(customerId, pageable).map(OrderResponse::from);
    }

    @PatchMapping("/{id}/revert")
    @Operation(summary = "Revert order to pending confirmation")
            @ApiResponse(responseCode = "200", description = "Order reverted")
            @ApiResponse(responseCode = "404", description = "Order not found")
            @ApiResponse(responseCode = "422", description = "Invalid status transition")
    public OrderResponse revert(@PathVariable UUID id) {
        return OrderResponse.from(revertOrderUseCase.execute(id));
    }
}