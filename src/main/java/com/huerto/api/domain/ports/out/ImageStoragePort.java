package com.huerto.api.domain.ports.out;

public interface ImageStoragePort {

    ImageUploadResult upload(byte[] bytes, String originalFilename, String folder);
    void delete(String publicId);

    record ImageUploadResult(String publicId, String secureUrl) {}
}