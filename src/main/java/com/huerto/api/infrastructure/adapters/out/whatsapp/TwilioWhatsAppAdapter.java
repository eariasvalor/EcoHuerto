package com.huerto.api.infrastructure.adapters.out.whatsapp;

import com.huerto.api.domain.enums.DeliveryStatus;
import com.huerto.api.domain.enums.NotificationType;
import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.model.Notification;
import com.huerto.api.domain.model.Order;
import com.huerto.api.domain.ports.out.NotificationRepository;
import com.huerto.api.domain.ports.out.WhatsAppPort;
import com.huerto.api.infrastructure.config.TwilioProperties;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.twilio.rest.api.v2010.account.MessageCreator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Profile("!meta")
public class TwilioWhatsAppAdapter implements WhatsAppPort {

    private static final Logger log = LoggerFactory.getLogger(TwilioWhatsAppAdapter.class);

    private final TwilioProperties props;
    private final NotificationRepository notificationRepository;

    @Value("${frontend.url}")
    private String frontendUrl;

    public TwilioWhatsAppAdapter(TwilioProperties props,
                                 NotificationRepository notificationRepository) {
        this.props = props;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void sendStatusChange(String phone, String orderId, OrderStatus status) {
        String text = buildStatusChangeText(orderId, status);
        send(phone, text, NotificationType.STATUS_CHANGE, text, null);
    }

    @Override
    public void sendNewOrderToAdmin(String adminPhone, Order order, String customerName) {
        String text = buildNewOrderText(order, customerName);
        send(adminPhone, text, NotificationType.NEW_ORDER_ADMIN, text, null);
    }

    @Override
    public void sendManualNotification(String phone, String text, String mediaUrl) {
        send(phone, text, NotificationType.MANUAL, text, mediaUrl);
    }

    private void send(String phone, String text, NotificationType type,
                      String messageText, String mediaUrl) {
        Notification notification = notificationRepository.save(new Notification(
                UUID.randomUUID(), type,
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                phone, null, messageText, mediaUrl,
                DeliveryStatus.PENDING, 0, LocalDateTime.now(), null
        ));

        try {
            MessageCreator creator = Message.creator(
                    new PhoneNumber("whatsapp:" + phone),
                    new PhoneNumber(props.whatsappFrom()),
                    text
            );

            if (mediaUrl != null) {
                creator.setMediaUrl(java.util.List.of(java.net.URI.create(mediaUrl)));
            }

            creator.create();

            notificationRepository.save(notification
                    .withDeliveryStatus(DeliveryStatus.SENT)
                    .withSentAt(LocalDateTime.now()));

        } catch (Exception e) {
            log.warn("Twilio WhatsApp send failed to {}: {}", phone, e.getMessage());
            notificationRepository.save(notification
                    .withDeliveryStatus(DeliveryStatus.FAILED)
                    .incrementAttempts());
        }
    }

    private String buildStatusChangeText(String orderId, OrderStatus status) {
        return switch (status) {
            case CONFIRMED -> "¡Hola! Tu pedido %s está confirmado. Pronto lo prepararemos."
                    .formatted(orderId);
            case READY_FOR_PICKUP -> "Tu pedido %s está listo. ¡Te lo enviaremos en las próximas horas!"
                    .formatted(orderId);
            case CANCELLED -> "Tu pedido %s se ha cancelado. Contacta con nosotros si tienes dudas."
                    .formatted(orderId);
            case PENDING -> "Hemos recibido tu pedido %s. En breve lo confirmaremos."
                    .formatted(orderId);
            default -> "Tu pedido %s ha cambiado de estado.".formatted(orderId);
        };
    }

    private String buildNewOrderText(Order order, String customerName) {
        return String.join("\n",
                "🛒 Nuevo pedido recibido!",
                "Cliente: " + customerName,
                "Pedido: " + order.visibleId(),
                "Total: " + order.total().amount().toPlainString() + "€",
                "Ver pedidos: " + frontendUrl + "/admin/orders"
        );
    }
}