package com.huerto.api.infrastructure.adapters.out.persistence.entity;

import com.huerto.api.domain.enums.Unit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter @Setter
public class ProductEntity {

    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "variety_id", nullable = false)
    private VarietyEntity variety;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Unit unit;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private boolean available;

    @Column(name = "image_url")
    private String imageUrl;

    @Version
    private int version;
}