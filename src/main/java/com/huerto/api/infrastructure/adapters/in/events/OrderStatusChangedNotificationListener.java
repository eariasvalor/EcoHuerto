package com.huerto.api.infrastructure.adapters.in.events;

import com.huerto.api.domain.events.OrderStatusChangedEvent;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Customer;
import com.huerto.api.domain.ports.out.CustomerRepository;
import com.huerto.api.domain.ports.out.WhatsAppPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusChangedNotificationListener {

    private final WhatsAppPort whatsAppPort;
    private final CustomerRepository customerRepository;

    public OrderStatusChangedNotificationListener(WhatsAppPort whatsAppPort,
                                                  CustomerRepository customerRepository) {
        this.whatsAppPort = whatsAppPort;
        this.customerRepository = customerRepository;
    }

    @EventListener
    @Async
    public void onOrderStatusChanged(OrderStatusChangedEvent event) {
        if (!event.newStatus().notifiesCustomer()) return;

        Customer customer = customerRepository.findById(event.order().customerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer", event.order().customerId()));

        whatsAppPort.sendStatusChange(
                customer.phone().fullNumber(),
                event.order().visibleId(),
                event.newStatus()
        );
    }
}