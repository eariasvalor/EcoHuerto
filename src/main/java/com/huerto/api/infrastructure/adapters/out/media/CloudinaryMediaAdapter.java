package com.huerto.api.infrastructure.adapters.out.media;

import com.huerto.api.domain.model.MediaUploadResult;
import com.huerto.api.domain.ports.out.ImageStoragePort;
import com.huerto.api.domain.ports.out.MediaStoragePort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!meta")
public class CloudinaryMediaAdapter implements MediaStoragePort {

    private static final String FOLDER = "huerto/notifications/";

    private final ImageStoragePort imageStoragePort;

    public CloudinaryMediaAdapter(ImageStoragePort imageStoragePort) {
        this.imageStoragePort = imageStoragePort;
    }

    @Override
    public MediaUploadResult upload(byte[] bytes, String mimeType, String filename) {
        ImageStoragePort.ImageUploadResult result =
                imageStoragePort.upload(bytes, filename, FOLDER);
        return MediaUploadResult.fromCloudinary(result.publicId(), result.secureUrl());
    }
}