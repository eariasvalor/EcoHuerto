package com.huerto.api.application.impl.notification;

import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.Customer;
import com.huerto.api.domain.ports.out.CustomerRepository;
import com.huerto.api.domain.ports.out.WhatsAppPort;
import com.huerto.api.infrastructure.adapters.in.web.dto.SendManualNotificationResponse;
import com.huerto.api.util.CustomerTestFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendManualNotificationUseCaseImplTest {

    @Mock WhatsAppPort whatsAppPort;
    @Mock CustomerRepository customerRepository;
    @InjectMocks SendManualNotificationUseCaseImpl useCase;

    @Test
    void should_send_to_all_customers_and_return_sent_count() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Customer c1 = CustomerTestFactory.buildCustomer(id1);
        Customer c2 = CustomerTestFactory.buildCustomer(id2);

        when(customerRepository.findById(id1)).thenReturn(Optional.of(c1));
        when(customerRepository.findById(id2)).thenReturn(Optional.of(c2));

        SendManualNotificationResponse result = useCase.execute(
                List.of(id1, id2), "Tomates disponibles!", null);

        assertThat(result.sent()).isEqualTo(2);
        assertThat(result.failed()).isEqualTo(0);
        verify(whatsAppPort, times(2)).sendManualNotification(any(), eq("Tomates disponibles!"), isNull());
    }

    @Test
    void should_count_failed_when_customer_not_found() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Customer c1 = CustomerTestFactory.buildCustomer(id1);

        when(customerRepository.findById(id1)).thenReturn(Optional.of(c1));
        when(customerRepository.findById(id2)).thenReturn(Optional.empty());

        SendManualNotificationResponse result = useCase.execute(
                List.of(id1, id2), "Tomates disponibles!", null);

        assertThat(result.sent()).isEqualTo(1);
        assertThat(result.failed()).isEqualTo(1);
    }

    @Test
    void should_count_failed_when_whatsapp_throws() {
        UUID id1 = UUID.randomUUID();
        Customer c1 = CustomerTestFactory.buildCustomer(id1);

        when(customerRepository.findById(id1)).thenReturn(Optional.of(c1));
        doThrow(new RuntimeException("Twilio error"))
                .when(whatsAppPort).sendManualNotification(any(), any(), any());

        SendManualNotificationResponse result = useCase.execute(
                List.of(id1), "Tomates disponibles!", null);

        assertThat(result.sent()).isEqualTo(0);
        assertThat(result.failed()).isEqualTo(1);
    }

    @Test
    void should_pass_media_url_when_present() {
        UUID id1 = UUID.randomUUID();
        Customer c1 = CustomerTestFactory.buildCustomer(id1);

        when(customerRepository.findById(id1)).thenReturn(Optional.of(c1));

        useCase.execute(List.of(id1), "¡Mira esta foto!", "https://cloudinary.com/img.jpg");

        verify(whatsAppPort).sendManualNotification(
                c1.phone().fullNumber(),
                "Mira esta foto!",
                "https://cloudinary.com/img.jpg"
        );
    }
}