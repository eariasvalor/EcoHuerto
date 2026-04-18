package com.huerto.api.infrastructure.config;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TwilioProperties.class)
public class TwilioConfig {

    private final TwilioProperties props;

    public TwilioConfig(TwilioProperties props) {
        this.props = props;
    }

    @PostConstruct
    public void init() {
        Twilio.init(props.accountSid(), props.authToken());
    }
}