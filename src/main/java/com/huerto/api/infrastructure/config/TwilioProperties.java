package com.huerto.api.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "twilio")
public record TwilioProperties(
        String accountSid,
        String authToken,
        String whatsappFrom,
        Admin admin
) {
    public record Admin(String phone) {}
}