package com.huerto.api.infrastructure.adapters.out.whatsapp;

import com.huerto.api.domain.enums.DeliveryStatus;
import com.huerto.api.domain.enums.NotificationType;
import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.model.*;
import com.huerto.api.domain.ports.out.NotificationRepository;
import com.huerto.api.domain.valueobject.Price;
import com.huerto.api.infrastructure.config.WhatsAppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@ExtendWith(MockitoExtension.class)
class WhatsAppMetaAdapterTest {

    @Mock NotificationRepository notificationRepository;

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private WhatsAppMetaAdapter adapter;

    private static final String PHONE = "+34612345678";
    private static final String ORDER_ID = "HUE-0001";
    private static final String API_URL =
            "https://graph.facebook.com/v18.0/test-phone-id/messages";

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);

        WhatsAppProperties props = new WhatsAppProperties(
                new WhatsAppProperties.Api("test-token", "test-phone-id", "v18.0"),
                new WhatsAppProperties.Templates(
                        "order_status_change", "new_order_admin",
                        "manual_notification", "manual_notification_image"),
                new WhatsAppProperties.Admin("+34600000000")
        );

        adapter = new WhatsAppMetaAdapter(restTemplate, props, notificationRepository);

        when(notificationRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    private Order buildOrder() {
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato", null);
        Product product = new Product(UUID.randomUUID(), "Tomato", variety,
                Price.of("2.50"), Unit.KG, 2, true, null, 0);
        OrderLine line = new OrderLine(UUID.randomUUID(), product, 2);
        return new Order(UUID.randomUUID(), ORDER_ID, UUID.randomUUID(), "Ana García",
                List.of(line), OrderStatus.CONFIRMED, LocalDateTime.now(), 0);
    }

    @Test
    void should_persist_pending_then_sent_when_status_change_succeeds() {
        mockServer.expect(requestTo(API_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"messages\":[{\"id\":\"wamid.1\"}]}",
                        MediaType.APPLICATION_JSON));

        adapter.sendStatusChange(PHONE, ORDER_ID, OrderStatus.CONFIRMED);

        mockServer.verify();

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(2)).save(captor.capture());

        List<Notification> saved = captor.getAllValues();
        assertThat(saved.get(0).deliveryStatus()).isEqualTo(DeliveryStatus.PENDING);
        assertThat(saved.get(1).deliveryStatus()).isEqualTo(DeliveryStatus.SENT);
        assertThat(saved.get(1).sentAt()).isNotNull();
        assertThat(saved.get(0).type()).isEqualTo(NotificationType.STATUS_CHANGE);
    }

    @Test
    void should_persist_failed_and_not_throw_when_meta_returns_error() {
        mockServer.expect(requestTo(API_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());

        assertThatNoException().isThrownBy(() ->
                adapter.sendStatusChange(PHONE, ORDER_ID, OrderStatus.CONFIRMED));

        mockServer.verify();

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(2)).save(captor.capture());

        List<Notification> saved = captor.getAllValues();
        assertThat(saved.get(0).deliveryStatus()).isEqualTo(DeliveryStatus.PENDING);
        assertThat(saved.get(1).deliveryStatus()).isEqualTo(DeliveryStatus.FAILED);
        assertThat(saved.get(1).attempts()).isEqualTo(1);
    }

    @Test
    void should_send_new_order_to_admin() {
        mockServer.expect(requestTo(API_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"messages\":[{\"id\":\"wamid.2\"}]}",
                        MediaType.APPLICATION_JSON));

        Order order = buildOrder();
        adapter.sendNewOrderToAdmin("+34600000000", order, "Ana García");

        mockServer.verify();

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(2)).save(captor.capture());

        assertThat(captor.getAllValues().get(0).type())
                .isEqualTo(NotificationType.NEW_ORDER_ADMIN);
    }

    @Test
    void should_send_manual_notification_without_media() {
        mockServer.expect(requestTo(API_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"messages\":[{\"id\":\"wamid.3\"}]}",
                        MediaType.APPLICATION_JSON));

        adapter.sendManualNotification(PHONE, "Hola, tomates disponibles!", null);

        mockServer.verify();

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(2)).save(captor.capture());

        assertThat(captor.getAllValues().get(0).type())
                .isEqualTo(NotificationType.MANUAL);
    }
}