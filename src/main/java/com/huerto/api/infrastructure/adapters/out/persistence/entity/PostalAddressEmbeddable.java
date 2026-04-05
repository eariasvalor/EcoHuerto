package com.huerto.api.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter @Setter @NoArgsConstructor
public class PostalAddressEmbeddable {

    @Column(name = "address_street_type")
    private String streetType;

    @Column(name = "address_street")
    private String street;

    @Column(name = "address_number")
    private String number;

    @Column(name = "address_floor")
    private String floor;

    @Column(name = "address_city")
    private String city;

    @Column(name = "address_postal_code")
    private String postalCode;

    @Column(name = "address_province")
    private String province;
}