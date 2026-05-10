package com.huerto.api.infrastructure.adapters.in.events;

import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.events.OrderCreatedEvent;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.*;
import com.huerto.api.domain.ports.out.CustomerRepository;
import com.huerto.api.domain.ports.out.WhatsAppPort;
import com.huerto.api.domain.valueobject.Description;
import com.huerto.api.domain.valueobject.Price;
import com.huerto.api.infrastructure.config.WhatsAppProperties;
import com.huerto.api.util.CustomerTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class NewOrderAdminNotificationListenerTest {

    @Mock WhatsAppPort whatsAppPort;
    @Mock CustomerRepository customerRepository;

    private NewOrderAdminNotificationListener listener;

    @BeforeEach
    void setUp() {
        WhatsAppProperties props = new WhatsAppProperties(
                new WhatsAppProperties.Api("token", "phone-id", "v18.0"),
                new WhatsAppProperties.Templates(
                        "order_status_change", "new_order_admin",
                        "manual_notification", "manual_notification_image"),
                new WhatsAppProperties.Admin("+34600000000")
        );
        listener = new NewOrderAdminNotificationListener(
                whatsAppPort, customerRepository, props);
    }

    private Order buildOrder(UUID customerId) {
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato", null);
        Product product = new Product(UUID.randomUUID(), "Tomato", new Description("Fresh tomato"), variety,
                Price.of("2.50"), Unit.KG, 2, true, null, 0);
        OrderLine line = new OrderLine(UUID.randomUUID(), product, 2);
        return new Order(UUID.randomUUID(), "HUE-0001", customerId, "Ana García",
                List.of(line), OrderStatus.PENDING, LocalDateTime.now(), 0);
    }

    @Test
    void should_notify_admin_with_correct_payload_when_order_is_created() {
        UUID customerId = UUID.randomUUID();
        Customer customer = CustomerTestFactory.buildCustomer(customerId);
        Order order = buildOrder(customerId);
        OrderCreatedEvent event = new OrderCreatedEvent(order, LocalDateTime.now());

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        listener.onOrderCreated(event);

        verify(whatsAppPort).sendNewOrderToAdmin(
                "+34600000000",
                order,
                customer.name()
        );
    }

    @Test
    void should_throw_when_customer_not_found() {
        UUID customerId = UUID.randomUUID();
        Order order = buildOrder(customerId);
        OrderCreatedEvent event = new OrderCreatedEvent(order, LocalDateTime.now());

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listener.onOrderCreated(event))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(whatsAppPort, never()).sendNewOrderToAdmin(any(), any(), any());
    }
}