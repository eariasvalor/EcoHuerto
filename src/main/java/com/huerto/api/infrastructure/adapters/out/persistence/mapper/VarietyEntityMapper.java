package com.huerto.api.infrastructure.adapters.out.persistence.mapper;

import com.huerto.api.domain.model.Variety;
import com.huerto.api.infrastructure.adapters.out.persistence.entity.VarietyEntity;
import org.springframework.stereotype.Component;

@Component
public class VarietyEntityMapper {

    public VarietyEntity toEntity(Variety variety) {
        VarietyEntity entity = new VarietyEntity();
        entity.setId(variety.id());
        entity.setName(variety.name());
        entity.setProductCategory(variety.productCategory());
        return entity;
    }

    public Variety toDomain(VarietyEntity entity) {
        return new Variety(
                entity.getId(),
                entity.getName(),
                entity.getProductCategory()
        );
    }
}