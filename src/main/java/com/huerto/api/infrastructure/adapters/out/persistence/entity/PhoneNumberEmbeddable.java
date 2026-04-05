package com.huerto.api.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter @Setter @NoArgsConstructor
public class PhoneNumberEmbeddable {

    @Column(name = "phone_country_code")
    private String countryCode;

    @Column(name = "phone_number")
    private String number;
}