package com.huerto.api.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "whatsapp")
public record WhatsAppProperties(
        Api api,
        Templates templates,
        Admin admin
) {
    public record Api(
            String token,
            String phoneNumberId,
            String version
    ) {}

    public record Templates(
            String statusChange,
            String newOrderAdmin,
            String manualText,
            String manualImage
    ) {}

    public record Admin(
            String phone
    ) {}
}