package com.huerto.api.application.impl.notification;

import com.huerto.api.domain.model.MediaUploadResult;
import com.huerto.api.domain.ports.out.CustomerRepository;
import com.huerto.api.domain.ports.out.MediaStoragePort;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendManualNotificationUseCaseImplTest {

    @Mock WhatsAppPort whatsAppPort;
    @Mock CustomerRepository customerRepository;
    @Mock MediaStoragePort mediaStoragePort;
    @InjectMocks SendManualNotificationUseCaseImpl useCase;

    @Test
    void should_send_to_all_customers_and_return_sent_count() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        when(customerRepository.findById(id1))
                .thenReturn(Optional.of(CustomerTestFactory.buildCustomer(id1)));
        when(customerRepository.findById(id2))
                .thenReturn(Optional.of(CustomerTestFactory.buildCustomer(id2)));

        SendManualNotificationResponse result = useCase.execute(
                List.of(id1, id2), "Tomates disponibles!", null, null, null);

        assertThat(result.sent()).isEqualTo(2);
        assertThat(result.failed()).isEqualTo(0);
        verify(whatsAppPort, times(2)).sendManualNotification(any(), eq("Tomates disponibles!"), isNull());
        verify(mediaStoragePort, never()).upload(any(), any(), any());
    }

    @Test
    void should_count_failed_when_customer_not_found() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        when(customerRepository.findById(id1))
                .thenReturn(Optional.of(CustomerTestFactory.buildCustomer(id1)));
        when(customerRepository.findById(id2)).thenReturn(Optional.empty());

        SendManualNotificationResponse result = useCase.execute(
                List.of(id1, id2), "Tomates disponibles!", null, null, null);

        assertThat(result.sent()).isEqualTo(1);
        assertThat(result.failed()).isEqualTo(1);
    }

    @Test
    void should_count_failed_when_whatsapp_throws() {
        UUID id1 = UUID.randomUUID();

        when(customerRepository.findById(id1))
                .thenReturn(Optional.of(CustomerTestFactory.buildCustomer(id1)));
        doThrow(new RuntimeException("Twilio error"))
                .when(whatsAppPort).sendManualNotification(any(), any(), any());

        SendManualNotificationResponse result = useCase.execute(
                List.of(id1), "Tomates disponibles!", null, null, null);

        assertThat(result.sent()).isEqualTo(0);
        assertThat(result.failed()).isEqualTo(1);
    }

    @Test
    void should_upload_image_and_pass_media_url_when_file_present() {
        UUID id1 = UUID.randomUUID();
        var customer = CustomerTestFactory.buildCustomer(id1);
        byte[] imageBytes = "image".getBytes();

        when(customerRepository.findById(id1)).thenReturn(Optional.of(customer));
        when(mediaStoragePort.upload(imageBytes, "image/jpeg", "photo.jpg"))
                .thenReturn(new MediaUploadResult("pub123", "https://cloudinary.com/photo.jpg"));

        useCase.execute(List.of(id1), "¡Mira esta foto!", imageBytes, "image/jpeg", "photo.jpg");

        verify(mediaStoragePort).upload(imageBytes, "image/jpeg", "photo.jpg");
        verify(whatsAppPort).sendManualNotification(
                customer.phone().fullNumber(),
                "¡Mira esta foto!",
                "https://cloudinary.com/photo.jpg"
        );
    }
}