package com.huerto.api.infrastructure.adapters.out.whatsapp;

import com.huerto.api.domain.enums.DeliveryStatus;
import com.huerto.api.domain.enums.NotificationType;
import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.model.*;
import com.huerto.api.domain.ports.out.NotificationRepository;
import com.huerto.api.domain.valueobject.Description;
import com.huerto.api.domain.valueobject.Price;
import com.huerto.api.infrastructure.config.TwilioProperties;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TwilioWhatsAppAdapterTest {

    @Mock NotificationRepository notificationRepository;
    @Mock MessageCreator messageCreator;

    private TwilioWhatsAppAdapter adapter;

    private static final String PHONE = "+34612345678";
    private static final String ORDER_ID = "HUE-0001";

    @BeforeEach
    void setUp() {
        TwilioProperties props = new TwilioProperties(
                "test-sid", "test-token",
                "whatsapp:+14155238886",
                new TwilioProperties.Admin("+34600000000")
        );
        adapter = new TwilioWhatsAppAdapter(props, notificationRepository);

        when(notificationRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    private Order buildOrder() {
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato", null);
        Product product = new Product(UUID.randomUUID(), "Tomato", new Description("Fresh tomato"), variety,
                Price.of("2.50"), Unit.KG, 2, true, null, 0);
        OrderLine line = new OrderLine(UUID.randomUUID(), product, 2);
        return new Order(UUID.randomUUID(), ORDER_ID, UUID.randomUUID(), "Ana García",
                List.of(line), OrderStatus.CONFIRMED, LocalDateTime.now(), 0);
    }

    @Test
    void should_persist_pending_then_sent_when_status_change_succeeds() {
        try (MockedStatic<Message> messageMock = mockStatic(Message.class)) {
            messageMock.when(() -> Message.creator(
                            any(PhoneNumber.class),
                            any(PhoneNumber.class),
                            any(String.class)))
                    .thenReturn(messageCreator);
            when(messageCreator.create()).thenReturn(mock(Message.class));

            adapter.sendStatusChange(PHONE, ORDER_ID, OrderStatus.CONFIRMED);

            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository, times(2)).save(captor.capture());

            List<Notification> saved = captor.getAllValues();
            assertThat(saved.get(0).deliveryStatus()).isEqualTo(DeliveryStatus.PENDING);
            assertThat(saved.get(1).deliveryStatus()).isEqualTo(DeliveryStatus.SENT);
            assertThat(saved.get(1).sentAt()).isNotNull();
            assertThat(saved.get(0).type()).isEqualTo(NotificationType.STATUS_CHANGE);
        }
    }

    @Test
    void should_persist_failed_and_not_throw_when_twilio_fails() {
        try (MockedStatic<Message> messageMock = mockStatic(Message.class)) {
            messageMock.when(() -> Message.creator(
                            any(PhoneNumber.class),
                            any(PhoneNumber.class),
                            any(String.class)))
                    .thenReturn(messageCreator);
            when(messageCreator.create()).thenThrow(new RuntimeException("Twilio error"));

            assertThatNoException().isThrownBy(() ->
                    adapter.sendStatusChange(PHONE, ORDER_ID, OrderStatus.CONFIRMED));

            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository, times(2)).save(captor.capture());

            List<Notification> saved = captor.getAllValues();
            assertThat(saved.get(0).deliveryStatus()).isEqualTo(DeliveryStatus.PENDING);
            assertThat(saved.get(1).deliveryStatus()).isEqualTo(DeliveryStatus.FAILED);
            assertThat(saved.get(1).attempts()).isEqualTo(1);
        }
    }

    @Test
    void should_send_new_order_to_admin() {
        try (MockedStatic<Message> messageMock = mockStatic(Message.class)) {
            messageMock.when(() -> Message.creator(
                            any(PhoneNumber.class),
                            any(PhoneNumber.class),
                            any(String.class)))
                    .thenReturn(messageCreator);
            when(messageCreator.create()).thenReturn(mock(Message.class));

            adapter.sendNewOrderToAdmin("+34600000000", buildOrder(), "Ana García");

            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository, times(2)).save(captor.capture());

            assertThat(captor.getAllValues().get(0).type())
                    .isEqualTo(NotificationType.NEW_ORDER_ADMIN);
        }
    }

    @Test
    void should_send_manual_notification_without_media() {
        try (MockedStatic<Message> messageMock = mockStatic(Message.class)) {
            messageMock.when(() -> Message.creator(
                            any(PhoneNumber.class),
                            any(PhoneNumber.class),
                            any(String.class)))
                    .thenReturn(messageCreator);
            when(messageCreator.create()).thenReturn(mock(Message.class));

            adapter.sendManualNotification(PHONE, "Tomates disponibles!", null);

            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository, times(2)).save(captor.capture());

            assertThat(captor.getAllValues().get(0).type())
                    .isEqualTo(NotificationType.MANUAL);
        }
    }
}