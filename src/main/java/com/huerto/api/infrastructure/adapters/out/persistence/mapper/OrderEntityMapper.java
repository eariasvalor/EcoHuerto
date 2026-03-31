package com.huerto.api.infrastructure.adapters.out.persistence.mapper;

import com.huerto.api.domain.model.Order;
import com.huerto.api.domain.model.OrderLine;
import com.huerto.api.infrastructure.adapters.out.persistence.entity.OrderEntity;
import com.huerto.api.infrastructure.adapters.out.persistence.entity.OrderLineEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderEntityMapper {

    private final ProductEntityMapper productEntityMapper;

    public OrderEntityMapper(ProductEntityMapper productEntityMapper) {
        this.productEntityMapper = productEntityMapper;
    }

    public OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setId(order.id());
        entity.setVisibleId(order.visibleId());
        entity.setCustomerId(order.customerId());
        entity.setStatus(order.status());
        entity.setCreatedAt(order.createdAt());
        entity.setVersion(order.version());

        List<OrderLineEntity> lines = order.lines().stream()
                .map(line -> toLineEntity(line, entity))
                .toList();
        entity.setLines(lines);

        return entity;
    }

    public Order toDomain(OrderEntity entity) {
        List<OrderLine> lines = entity.getLines().stream()
                .map(this::toLineDomain)
                .toList();

        return new Order(
                entity.getId(),
                entity.getVisibleId(),
                entity.getCustomerId(),
                lines,
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getVersion()
        );
    }

    private OrderLineEntity toLineEntity(OrderLine line, OrderEntity orderEntity) {
        OrderLineEntity entity = new OrderLineEntity();
        entity.setId(line.id());
        entity.setOrder(orderEntity);
        entity.setProduct(productEntityMapper.toEntity(line.product()));
        entity.setQuantity(line.quantity());
        return entity;
    }

    private OrderLine toLineDomain(OrderLineEntity entity) {
        return new OrderLine(
                entity.getId(),
                productEntityMapper.toDomain(entity.getProduct()),
                entity.getQuantity()
        );
    }
}
