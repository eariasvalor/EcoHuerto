package com.huerto.api.domain.ports.out;

import com.huerto.api.domain.model.MediaUploadResult;

public interface MediaStoragePort {
    MediaUploadResult upload(byte[] bytes, String mimeType, String filename);
}