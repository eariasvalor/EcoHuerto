package com.huerto.api.infrastructure.adapters.in.web;

import com.huerto.api.application.usecase.order.DeliverOrderUseCase;
import com.huerto.api.application.usecase.order.GetOrderStatsUseCase;
import com.huerto.api.domain.model.Order;
import com.huerto.api.domain.model.OrderStats;
import com.huerto.api.infrastructure.adapters.in.web.dto.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/orders")
@Tag(name = "Admin — Orders", description = "Order statistics for administrators")
public class AdminOrderController {

    private final GetOrderStatsUseCase getOrderStatsUseCase;

    public AdminOrderController(GetOrderStatsUseCase getOrderStatsUseCase) {
        this.getOrderStatsUseCase = getOrderStatsUseCase;
    }

    @GetMapping("/stats")
    @Operation(summary = "Get order statistics grouped by status")
    @ApiResponse(responseCode = "200", description = "Order stats")
    public OrderStats getStats() {
        return getOrderStatsUseCase.execute();
    }

}