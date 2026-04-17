package com.huerto.api.infrastructure.adapters.in.events;

import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.events.OrderStatusChangedEvent;
import com.huerto.api.domain.model.*;
import com.huerto.api.domain.ports.out.CustomerRepository;
import com.huerto.api.domain.ports.out.WhatsAppPort;
import com.huerto.api.domain.valueobject.Price;
import com.huerto.api.util.CustomerTestFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderStatusChangedNotificationListenerTest {

    @Mock WhatsAppPort whatsAppPort;
    @Mock CustomerRepository customerRepository;
    @InjectMocks OrderStatusChangedNotificationListener listener;

    private Order buildOrder(UUID customerId, OrderStatus status) {
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato", null);
        Product product = new Product(UUID.randomUUID(), "Tomato", variety,
                Price.of("2.50"), Unit.KG, 100, true, null, 0);
        OrderLine line = new OrderLine(UUID.randomUUID(), product, 2);
        return new Order(UUID.randomUUID(), "HUE-0001", customerId, "Ana García",
                List.of(line), status, LocalDateTime.now(), 0);
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class,
            names = {"PENDING", "CONFIRMED", "READY_FOR_PICKUP", "CANCELLED"})
    void should_send_whatsapp_when_status_notifies_customer(OrderStatus status) {
        UUID customerId = UUID.randomUUID();
        Customer customer = CustomerTestFactory.buildCustomer(customerId);
        Order order = buildOrder(customerId, status);

        OrderStatusChangedEvent event = new OrderStatusChangedEvent(
                order, OrderStatus.PENDING, status, LocalDateTime.now());

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        listener.onOrderStatusChanged(event);

        verify(whatsAppPort).sendStatusChange(
                customer.phone().fullNumber(),
                "HUE-0001",
                status
        );
    }

    @Test
    void should_not_send_whatsapp_when_status_is_delivered() {
        UUID customerId = UUID.randomUUID();
        Order order = buildOrder(customerId, OrderStatus.DELIVERED);

        OrderStatusChangedEvent event = new OrderStatusChangedEvent(
                order, OrderStatus.READY_FOR_PICKUP, OrderStatus.DELIVERED, LocalDateTime.now());

        listener.onOrderStatusChanged(event);

        verify(whatsAppPort, never()).sendStatusChange(any(), any(), any());
        verify(customerRepository, never()).findById(any());
    }
}