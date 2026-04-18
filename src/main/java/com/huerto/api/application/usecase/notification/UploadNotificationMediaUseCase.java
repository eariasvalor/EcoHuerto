package com.huerto.api.application.usecase.notification;

import com.huerto.api.domain.model.MediaUploadResult;

public interface UploadNotificationMediaUseCase {
    MediaUploadResult execute(byte[] bytes, String mimeType, String filename);
}