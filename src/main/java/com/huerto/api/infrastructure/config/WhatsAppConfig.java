package com.huerto.api.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableAsync
@EnableConfigurationProperties(WhatsAppProperties.class)
public class WhatsAppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}