package com.huerto.api.application;

import com.huerto.api.application.impl.order.CancelOrderUseCaseImpl;
import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.events.OrderStatusChangedEvent;
import com.huerto.api.domain.exception.InvalidStatusTransitionException;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.*;
import com.huerto.api.domain.ports.out.EventPublisher;
import com.huerto.api.domain.ports.out.OrderRepository;
import com.huerto.api.domain.ports.out.ProductRepository;
import com.huerto.api.domain.valueobject.Description;
import com.huerto.api.domain.valueobject.Price;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelOrderUseCaseTest {

    @Mock OrderRepository orderRepository;
    @Mock ProductRepository productRepository;
    @Mock EventPublisher eventPublisher;
    @InjectMocks CancelOrderUseCaseImpl cancelOrderUseCase;

    private Order buildOrder(UUID id, OrderStatus status) {
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato", null);
        Product product = new Product(
                UUID.randomUUID(), "Tomato", new Description("Fresh tomato"), variety,
                Price.of("2.50"), Unit.KG, 100, true, null,0
        );
        OrderLine line = new OrderLine(UUID.randomUUID(), product, 2);
        return new Order(
                id, "HUE-0001", UUID.randomUUID(), "",
                List.of(line), status, LocalDateTime.now(), 0
        );
    }

    @Test
    void should_cancel_order_when_pending() {
        UUID id = UUID.randomUUID();
        Order order = buildOrder(id, OrderStatus.PENDING);
        Product product = order.lines().get(0).product();

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(productRepository.findById(product.id())).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = cancelOrderUseCase.execute(id);

        assertThat(result.status()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository).save(any());
    }

    @Test
    void should_cancel_order_when_confirmed() {
        UUID id = UUID.randomUUID();
        Order order = buildOrder(id, OrderStatus.CONFIRMED);
        Product product = order.lines().get(0).product();

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(productRepository.findById(product.id())).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = cancelOrderUseCase.execute(id);

        assertThat(result.status()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository).save(any());
    }

    @Test
    void should_throw_when_order_not_found() {
        UUID id = UUID.randomUUID();

        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        ThrowingCallable execute = () -> cancelOrderUseCase.execute(id);

        assertThatThrownBy(execute).isInstanceOf(ResourceNotFoundException.class);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void should_publish_status_changed_event_after_cancelling() {
        UUID id = UUID.randomUUID();
        Order order = buildOrder(id, OrderStatus.PENDING);
        Product product = order.lines().get(0).product();

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(productRepository.findById(product.id())).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        cancelOrderUseCase.execute(id);

        ArgumentCaptor<OrderStatusChangedEvent> captor =
                ArgumentCaptor.forClass(OrderStatusChangedEvent.class);
        verify(eventPublisher).publish(captor.capture());

        assertThat(captor.getValue().previousStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(captor.getValue().newStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void should_throw_when_order_already_cancelled() {
        UUID id = UUID.randomUUID();
        Order order = buildOrder(id, OrderStatus.CANCELLED);
        Product product = order.lines().get(0).product();

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(productRepository.findById(product.id())).thenReturn(Optional.of(product));

        ThrowingCallable execute = () -> cancelOrderUseCase.execute(id);

        assertThatThrownBy(execute).isInstanceOf(InvalidStatusTransitionException.class);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void should_restore_stock_when_cancelling_pending_order() {
        UUID id = UUID.randomUUID();
        Order order = buildOrder(id, OrderStatus.PENDING);
        Product product = order.lines().get(0).product();

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(productRepository.findById(product.id())).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        cancelOrderUseCase.execute(id);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());

        Product savedProduct = productCaptor.getValue();
        assertThat(savedProduct.stock()).isEqualTo(102); // 100 + 2 = 102 (restored)
    }

    @Test
    void should_restore_stock_when_cancelling_confirmed_order() {
        UUID id = UUID.randomUUID();
        Order order = buildOrder(id, OrderStatus.CONFIRMED);
        Product product = order.lines().get(0).product();

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(productRepository.findById(product.id())).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        cancelOrderUseCase.execute(id);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());

        Product savedProduct = productCaptor.getValue();
        assertThat(savedProduct.stock()).isEqualTo(102); // 100 + 2 = 102 (restored)
    }

    @Test
    void should_restore_stock_for_all_products_in_multi_product_order() {
        UUID id = UUID.randomUUID();
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato", null);

        Product product1 = new Product(
                UUID.randomUUID(), "Tomato", new Description("Fresh tomato"), variety,
                Price.of("2.50"), Unit.KG, 10, true, null, 0
        );
        Product product2 = new Product(
                UUID.randomUUID(), "Cucumber", new Description("Fresh cucumber"), variety,
                Price.of("1.50"), Unit.KG, 5, true, null, 0
        );

        OrderLine line1 = new OrderLine(UUID.randomUUID(), product1, 2);
        OrderLine line2 = new OrderLine(UUID.randomUUID(), product2, 3);

        Order order = new Order(
                id, "HUE-0001", UUID.randomUUID(), "",
                List.of(line1, line2), OrderStatus.PENDING, LocalDateTime.now(), 0
        );

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(productRepository.findById(product1.id())).thenReturn(Optional.of(product1));
        when(productRepository.findById(product2.id())).thenReturn(Optional.of(product2));
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        cancelOrderUseCase.execute(id);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(2)).save(productCaptor.capture());

        List<Product> savedProducts = productCaptor.getAllValues();
        assertThat(savedProducts).hasSize(2);
        assertThat(savedProducts.get(0).stock()).isEqualTo(12); // 10 + 2 = 12
        assertThat(savedProducts.get(1).stock()).isEqualTo(8);  // 5 + 3 = 8
    }
}