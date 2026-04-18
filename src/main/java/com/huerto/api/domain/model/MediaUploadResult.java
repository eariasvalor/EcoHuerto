package com.huerto.api.domain.model;

public record MediaUploadResult(
        String mediaId,
        String mediaUrl
) {
    public static MediaUploadResult fromMeta(String mediaId) {
        return new MediaUploadResult(mediaId, null);
    }

    public static MediaUploadResult fromCloudinary(String publicId, String secureUrl) {
        return new MediaUploadResult(publicId, secureUrl);
    }
}