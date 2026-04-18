package com.huerto.api.application.impl.notification;

import com.huerto.api.application.usecase.notification.SendManualNotificationUseCase;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Customer;
import com.huerto.api.domain.ports.out.CustomerRepository;
import com.huerto.api.domain.ports.out.WhatsAppPort;
import com.huerto.api.infrastructure.adapters.in.web.dto.SendManualNotificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SendManualNotificationUseCaseImpl implements SendManualNotificationUseCase {

    private static final Logger log = LoggerFactory.getLogger(SendManualNotificationUseCaseImpl.class);

    private final WhatsAppPort whatsAppPort;
    private final CustomerRepository customerRepository;

    public SendManualNotificationUseCaseImpl(WhatsAppPort whatsAppPort,
                                             CustomerRepository customerRepository) {
        this.whatsAppPort = whatsAppPort;
        this.customerRepository = customerRepository;
    }

    @Override
    public SendManualNotificationResponse execute(List<UUID> customerIds,
                                                  String messageText,
                                                  String mediaUrl) {
        int sent = 0;
        int failed = 0;

        for (UUID customerId : customerIds) {
            try {
                Customer customer = customerRepository.findById(customerId)
                        .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));

                whatsAppPort.sendManualNotification(
                        customer.phone().fullNumber(),
                        messageText,
                        mediaUrl
                );
                sent++;
            } catch (Exception e) {
                log.warn("Failed to send manual notification to customer {}: {}",
                        customerId, e.getMessage());
                failed++;
            }
        }

        return new SendManualNotificationResponse(sent, failed);
    }
}