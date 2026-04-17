package com.huerto.api.infrastructure.adapters.in.events;

import com.huerto.api.domain.events.OrderCreatedEvent;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Customer;
import com.huerto.api.domain.ports.out.CustomerRepository;
import com.huerto.api.domain.ports.out.WhatsAppPort;
import com.huerto.api.infrastructure.config.WhatsAppProperties;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class NewOrderAdminNotificationListener {

    private final WhatsAppPort whatsAppPort;
    private final CustomerRepository customerRepository;
    private final WhatsAppProperties whatsAppProperties;

    public NewOrderAdminNotificationListener(WhatsAppPort whatsAppPort,
                                             CustomerRepository customerRepository,
                                             WhatsAppProperties whatsAppProperties) {
        this.whatsAppPort = whatsAppPort;
        this.customerRepository = customerRepository;
        this.whatsAppProperties = whatsAppProperties;
    }

    @EventListener
    @Async
    public void onOrderCreated(OrderCreatedEvent event) {
        Customer customer = customerRepository.findById(event.order().customerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer", event.order().customerId()));

        whatsAppPort.sendNewOrderToAdmin(
                whatsAppProperties.admin().phone(),
                event.order(),
                customer.name()
        );
    }
}