package com.huerto.api.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter @Setter
public class CustomerEntity {

    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Embedded
    private PhoneNumberEmbeddable phone;

    @Embedded
    private PostalAddressEmbeddable address;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Version
    private int version;
}