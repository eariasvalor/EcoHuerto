package com.huerto.api.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huerto.api.application.usecase.order.*;
import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.exception.InsufficientStockException;
import com.huerto.api.domain.exception.InvalidStatusTransitionException;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.*;
import com.huerto.api.domain.valueobject.Description;
import com.huerto.api.domain.valueobject.Price;
import com.huerto.api.infrastructure.adapters.in.web.OrderController;
import com.huerto.api.infrastructure.adapters.in.web.dto.CreateOrderRequest;
import com.huerto.api.infrastructure.config.SecurityConfig;
import com.huerto.api.infrastructure.config.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = OrderController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityConfig.class
        )
)
class OrderControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean CreateOrderUseCase createOrderUseCase;
    @MockBean ListOrdersUseCase listOrdersUseCase;
    @MockBean FindOrderUseCase findOrderUseCase;
    @MockBean ConfirmOrderUseCase confirmOrderUseCase;
    @MockBean MarkReadyUseCase markReadyUseCase;
    @MockBean CancelOrderUseCase cancelOrderUseCase;
    @MockBean SecurityContext securityContext;
    @MockBean ListMyOrdersUseCase listMyOrdersUseCase;
    @MockBean GetOrderStatsUseCase getOrderStatsUseCase;
    @MockBean RevertOrderUseCase revertOrderUseCase;
    @MockBean DeliverOrderUseCase deliverOrderUseCase;

    @BeforeEach
    void setUp() {
        when(securityContext.getCurrentUserId()).thenReturn(UUID.randomUUID());
        when(securityContext.isAdmin()).thenReturn(true);
    }

    private Order buildOrder(UUID orderId, UUID customerId) {
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato", null);
        Product product = new Product(
                UUID.randomUUID(), "Tomato", new Description("Fresh tomato"), variety,
                Price.of("2.50"), Unit.KG, 100, true, null, 0
        );
        OrderLine line = new OrderLine(UUID.randomUUID(), product, 2);
        return new Order(
                orderId, "HUE-0001", customerId, "John Doe",
                List.of(line), OrderStatus.PENDING,
                LocalDateTime.now(), 0
        );
    }

    @Test
    void should_return_201_when_order_is_created() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        CreateOrderRequest request = new CreateOrderRequest(
                customerId,
                List.of(new CreateOrderRequest.OrderLineRequest(productId, 2))
        );

        Order order = buildOrder(orderId, customerId);
        when(createOrderUseCase.execute(any()))
                .thenReturn(new CreateOrderResult(order, false));

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.visibleId").value("HUE-0001"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.lines.length()").value(1))
                .andExpect(jsonPath("$.total").value(5.00))
                .andExpect(jsonPath("$.possibleDuplicate").value(false));
    }

    @Test
    void should_return_404_when_product_not_found() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        CreateOrderRequest request = new CreateOrderRequest(
                customerId,
                List.of(new CreateOrderRequest.OrderLineRequest(productId, 2))
        );

        when(createOrderUseCase.execute(any()))
                .thenThrow(new ResourceNotFoundException("Product", productId));

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_409_when_insufficient_stock() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        CreateOrderRequest request = new CreateOrderRequest(
                customerId,
                List.of(new CreateOrderRequest.OrderLineRequest(productId, 200))
        );

        when(createOrderUseCase.execute(any()))
                .thenThrow(new InsufficientStockException(productId, 10, 200));

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void should_return_400_when_lines_are_empty() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
                UUID.randomUUID(), List.of()
        );

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_200_with_paginated_orders() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Order order = buildOrder(orderId, customerId);
        Page<Order> page = new PageImpl<>(List.of(order), PageRequest.of(0, 10), 1);

        when(listOrdersUseCase.execute(any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/orders")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].visibleId").value("HUE-0001"));
    }

    @Test
    void should_return_200_filtered_by_status() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Order order = buildOrder(orderId, customerId);
        Page<Order> page = new PageImpl<>(List.of(order), PageRequest.of(0, 10), 1);

        when(listOrdersUseCase.execute(
                eq(OrderStatus.PENDING), any(), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/orders")
                        .param("status", "PENDING")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("PENDING"));
    }

    @Test
    void should_return_200_when_order_exists() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Order order = buildOrder(orderId, customerId);

        when(findOrderUseCase.execute(orderId)).thenReturn(order);

        mockMvc.perform(get("/api/v1/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.visibleId").value("HUE-0001"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void should_return_404_when_order_not_found() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(findOrderUseCase.execute(orderId))
                .thenThrow(new ResourceNotFoundException("Order", orderId));

        mockMvc.perform(get("/api/v1/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_200_when_order_is_confirmed() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Order confirmed = new Order(
                orderId, "HUE-0001", customerId, "John Doe",
                buildOrder(orderId, customerId).lines(),
                OrderStatus.CONFIRMED, LocalDateTime.now(), 1
        );

        when(confirmOrderUseCase.execute(orderId)).thenReturn(confirmed);

        mockMvc.perform(patch("/api/v1/orders/{id}/confirm", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void should_return_404_when_order_not_found_on_confirm() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(confirmOrderUseCase.execute(orderId))
                .thenThrow(new ResourceNotFoundException("Order", orderId));

        mockMvc.perform(patch("/api/v1/orders/{id}/confirm", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_422_when_invalid_status_transition_on_confirm() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(confirmOrderUseCase.execute(orderId))
                .thenThrow(new InvalidStatusTransitionException(
                        OrderStatus.CONFIRMED, OrderStatus.CONFIRMED));

        mockMvc.perform(patch("/api/v1/orders/{id}/confirm", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void should_return_200_when_order_is_marked_ready() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Order ready = new Order(
                orderId, "HUE-0001", customerId, "John Doe",
                buildOrder(orderId, customerId).lines(),
                OrderStatus.READY_FOR_PICKUP, LocalDateTime.now(), 1
        );

        when(markReadyUseCase.execute(orderId)).thenReturn(ready);

        mockMvc.perform(patch("/api/v1/orders/{id}/ready", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READY_FOR_PICKUP"));
    }

    @Test
    void should_return_404_when_order_not_found_on_ready() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(markReadyUseCase.execute(orderId))
                .thenThrow(new ResourceNotFoundException("Order", orderId));

        mockMvc.perform(patch("/api/v1/orders/{id}/ready", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_422_when_invalid_transition_on_ready() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(markReadyUseCase.execute(orderId))
                .thenThrow(new InvalidStatusTransitionException(
                        OrderStatus.CONFIRMED, OrderStatus.READY_FOR_PICKUP));

        mockMvc.perform(patch("/api/v1/orders/{id}/ready", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void should_return_200_when_order_is_cancelled() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Order order = buildOrder(orderId, customerId);

        when(findOrderUseCase.execute(orderId)).thenReturn(order);
        when(cancelOrderUseCase.execute(orderId)).thenReturn(
                new Order(orderId, "HUE-0001", customerId, "John Doe",
                        order.lines(), OrderStatus.CANCELLED, LocalDateTime.now(), 1)
        );

        mockMvc.perform(patch("/api/v1/orders/{id}/cancel", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void should_return_404_when_order_not_found_on_cancel() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(findOrderUseCase.execute(orderId))
                .thenThrow(new ResourceNotFoundException("Order", orderId));

        mockMvc.perform(patch("/api/v1/orders/{id}/cancel", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_422_when_order_already_cancelled() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Order order = buildOrder(orderId, customerId);

        when(findOrderUseCase.execute(orderId)).thenReturn(order);
        when(cancelOrderUseCase.execute(orderId))
                .thenThrow(new InvalidStatusTransitionException(
                        OrderStatus.CANCELLED, OrderStatus.CANCELLED));

        mockMvc.perform(patch("/api/v1/orders/{id}/cancel", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void should_return_201_with_possible_duplicate_flag_when_similar_order_exists() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        CreateOrderRequest request = new CreateOrderRequest(
                customerId,
                List.of(new CreateOrderRequest.OrderLineRequest(productId, 2))
        );

        Order order = buildOrder(orderId, customerId);
        when(createOrderUseCase.execute(any()))
                .thenReturn(new CreateOrderResult(order, true));

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.possibleDuplicate").value(true));
    }

    @Test
    void should_return_200_with_my_orders() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Order order = buildOrder(orderId, customerId);
        Page<Order> page = new PageImpl<>(
                List.of(order), PageRequest.of(0, 10), 1
        );

        when(securityContext.getCurrentUserId()).thenReturn(customerId);
        when(listMyOrdersUseCase.execute(eq(customerId), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/orders/my")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].visibleId").value("HUE-0001"));
    }

    @Test
    void should_return_200_with_empty_page_when_no_orders() throws Exception {
        UUID customerId = UUID.randomUUID();

        when(securityContext.getCurrentUserId()).thenReturn(customerId);
        when(listMyOrdersUseCase.execute(eq(customerId), any(Pageable.class)))
                .thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/orders/my")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void should_return_200_when_order_is_reverted() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Order reverted = new Order(
                orderId, "HUE-0001", customerId, "John Doe",
                buildOrder(orderId, customerId).lines(),
                OrderStatus.PENDING, LocalDateTime.now(), 1
        );

        when(revertOrderUseCase.execute(orderId)).thenReturn(reverted);

        mockMvc.perform(patch("/api/v1/orders/{id}/revert", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void should_return_404_when_order_not_found_on_revert() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(revertOrderUseCase.execute(orderId))
                .thenThrow(new ResourceNotFoundException("Order", orderId));

        mockMvc.perform(patch("/api/v1/orders/{id}/revert", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_422_when_order_cannot_be_reverted() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(revertOrderUseCase.execute(orderId))
                .thenThrow(new InvalidStatusTransitionException(
                        OrderStatus.CANCELLED, OrderStatus.PENDING));

        mockMvc.perform(patch("/api/v1/orders/{id}/revert", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void should_return_200_when_order_is_delivered() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Order delivered = new Order(
                orderId, "HUE-0001", customerId, "John Doe",
                buildOrder(orderId, customerId).lines(),
                OrderStatus.DELIVERED, LocalDateTime.now(), 1
        );

        when(deliverOrderUseCase.execute(orderId)).thenReturn(delivered);

        mockMvc.perform(patch("/api/v1/orders/{id}/deliver", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"));
    }

    @Test
    void should_return_422_when_order_cannot_be_delivered() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(deliverOrderUseCase.execute(orderId))
                .thenThrow(new InvalidStatusTransitionException(
                        OrderStatus.PENDING, OrderStatus.DELIVERED));

        mockMvc.perform(patch("/api/v1/orders/{id}/deliver", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void should_return_404_when_order_not_found_on_deliver() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(deliverOrderUseCase.execute(orderId))
                .thenThrow(new ResourceNotFoundException("Order", orderId));

        mockMvc.perform(patch("/api/v1/orders/{id}/deliver", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


}