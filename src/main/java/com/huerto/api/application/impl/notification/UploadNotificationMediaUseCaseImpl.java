package com.huerto.api.application.impl.notification;

import com.huerto.api.application.usecase.notification.UploadNotificationMediaUseCase;
import com.huerto.api.domain.model.MediaUploadResult;
import com.huerto.api.domain.ports.out.MediaStoragePort;
import org.springframework.stereotype.Service;

@Service
public class UploadNotificationMediaUseCaseImpl implements UploadNotificationMediaUseCase {

    private final MediaStoragePort mediaStoragePort;

    public UploadNotificationMediaUseCaseImpl(MediaStoragePort mediaStoragePort) {
        this.mediaStoragePort = mediaStoragePort;
    }

    @Override
    public MediaUploadResult execute(byte[] bytes, String mimeType, String filename) {
        return mediaStoragePort.upload(bytes, mimeType, filename);
    }
}