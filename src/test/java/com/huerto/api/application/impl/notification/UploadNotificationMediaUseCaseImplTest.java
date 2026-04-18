package com.huerto.api.application.impl.notification;

import com.huerto.api.domain.model.MediaUploadResult;
import com.huerto.api.domain.ports.out.MediaStoragePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadNotificationMediaUseCaseImplTest {

    @Mock MediaStoragePort mediaStoragePort;
    @InjectMocks UploadNotificationMediaUseCaseImpl useCase;

    @Test
    void should_delegate_to_media_storage_port() {
        byte[] bytes = "image".getBytes();
        MediaUploadResult expected = MediaUploadResult.fromCloudinary("pub123", "https://url");

        when(mediaStoragePort.upload(bytes, "image/jpeg", "photo.jpg"))
                .thenReturn(expected);

        MediaUploadResult result = useCase.execute(bytes, "image/jpeg", "photo.jpg");

        assertThat(result).isEqualTo(expected);
        verify(mediaStoragePort).upload(bytes, "image/jpeg", "photo.jpg");
    }
}