package com.huerto.api.infrastructure.adapters.out.whatsapp;

import com.huerto.api.domain.enums.DeliveryStatus;
import com.huerto.api.domain.enums.NotificationType;
import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.model.Notification;
import com.huerto.api.domain.model.Order;
import com.huerto.api.domain.ports.out.NotificationRepository;
import com.huerto.api.domain.ports.out.WhatsAppPort;
import com.huerto.api.infrastructure.config.WhatsAppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class WhatsAppMetaAdapter implements WhatsAppPort {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppMetaAdapter.class);

    private final RestTemplate restTemplate;
    private final WhatsAppProperties props;
    private final NotificationRepository notificationRepository;
    private static final String TEMPLATE_VALUE = "template";

    public WhatsAppMetaAdapter(RestTemplate restTemplate,
                               WhatsAppProperties props,
                               NotificationRepository notificationRepository) {
        this.restTemplate = restTemplate;
        this.props = props;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void sendStatusChange(String phone, String orderId, OrderStatus status) {
        Map<String, Object> body = buildTemplateBody(phone,
                props.templates().statusChange(),
                List.of(orderId, status.name()));

        send(body, NotificationType.STATUS_CHANGE, null, null);
    }

    @Override
    public void sendNewOrderToAdmin(String adminPhone, Order order, String customerName) {
        Map<String, Object> body = buildTemplateBody(adminPhone,
                props.templates().newOrderAdmin(),
                List.of(customerName, order.visibleId(),
                        order.total().amount().toPlainString()));

        send(body, NotificationType.NEW_ORDER_ADMIN, null, null);
    }

    @Override
    public void sendManualNotification(String phone, String text, String mediaId) {
        String templateName = mediaId != null
                ? props.templates().manualImage()
                : props.templates().manualText();

        Map<String, Object> body = buildTemplateBody(phone, templateName, List.of(text));

        send(body, NotificationType.MANUAL, text, mediaId);
    }

    private void send(Map<String, Object> body, NotificationType type,
                      String messageText, String mediaId) {
        Notification notification = notificationRepository.save(new Notification(
                UUID.randomUUID(), type,
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                templateName(body), messageText, mediaId,
                DeliveryStatus.PENDING, 0, LocalDateTime.now(), null
        ));

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(props.api().token());
            headers.setContentType(MediaType.APPLICATION_JSON);

            restTemplate.exchange(
                    apiUrl(), HttpMethod.POST,
                    new HttpEntity<>(body, headers), String.class);

            notificationRepository.save(notification
                    .withDeliveryStatus(DeliveryStatus.SENT)
                    .withSentAt(LocalDateTime.now()));

        } catch (RestClientException e) {
            log.warn("WhatsApp API call failed: {}", e.getMessage());
            notificationRepository.save(notification
                    .withDeliveryStatus(DeliveryStatus.FAILED)
                    .incrementAttempts());
        }
    }

    private Map<String, Object> buildTemplateBody(String phone, String templateName,
                                                  List<String> params) {
        List<Map<String, String>> parameters = params.stream()
                .map(p -> Map.of("type", "text", "text", p))
                .toList();

        return Map.of(
                "messaging_product", "whatsapp",
                "to", phone,
                "type", TEMPLATE_VALUE,
                TEMPLATE_VALUE, Map.of(
                        "name", templateName,
                        "language", Map.of("code", "es"),
                        "components", List.of(Map.of(
                                "type", "body",
                                "parameters", parameters
                        ))
                )
        );
    }

    private String apiUrl() {
        return "https://graph.facebook.com/%s/%s/messages"
                .formatted(props.api().version(), props.api().phoneNumberId());
    }

    @SuppressWarnings("unchecked")
    private String templateName(Map<String, Object> body) {
        Map<String, Object> template = (Map<String, Object>) body.get(TEMPLATE_VALUE);
        return template != null ? (String) template.get("name") : null;
    }
}