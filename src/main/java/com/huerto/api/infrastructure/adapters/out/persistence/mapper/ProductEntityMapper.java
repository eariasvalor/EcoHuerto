package com.huerto.api.infrastructure.adapters.out.persistence.mapper;

import com.huerto.api.domain.model.Product;
import com.huerto.api.domain.model.Variety;
import com.huerto.api.domain.valueobject.Price;
import com.huerto.api.infrastructure.adapters.out.persistence.entity.ProductEntity;
import com.huerto.api.infrastructure.adapters.out.persistence.entity.VarietyEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductEntityMapper {

    public ProductEntity toEntity(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setId(product.id());
        entity.setName(product.name());
        entity.setVariety(toVarietyEntity(product.variety()));
        entity.setPrice(product.price().amount());
        entity.setCurrency(product.price().currency());
        entity.setUnit(product.unit());
        entity.setStock(product.stock());
        entity.setAvailable(product.available());
        entity.setImageUrl(product.imageUrl());
        entity.setVersion(product.version());
        return entity;
    }

    public Product toDomain(ProductEntity entity) {
        Variety variety = new Variety(
                entity.getVariety().getId(),
                entity.getVariety().getName(),
                entity.getVariety().getProductCategory(),
                entity.getVariety().getImageUrl()
        );
        return new Product(
                entity.getId(),
                entity.getName(),
                variety,
                new Price(entity.getPrice(), entity.getCurrency()),
                entity.getUnit(),
                entity.getStock(),
                entity.isAvailable(),
                entity.getImageUrl(),
                entity.getVersion()
        );
    }

    private VarietyEntity toVarietyEntity(Variety variety) {
        VarietyEntity entity = new VarietyEntity();
        entity.setId(variety.id());
        entity.setName(variety.name());
        entity.setProductCategory(variety.productCategory());
        entity.setImageUrl(variety.imageUrl());
        return entity;
    }
}