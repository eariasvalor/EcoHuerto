package com.huerto.api.domain.model;

import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.valueobject.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class ProductStockTest {

    private Variety variety;
    private Product product;

    @BeforeEach
    void setUp() {
        variety = new Variety(UUID.randomUUID(), "Raf", "Tomato");
        product = new Product(
                UUID.randomUUID(), "Tomate Raf", variety,
                Price.of("2.50"), Unit.KG, 10, true, 0
        );
    }

    // --- decreaseStock ---

    @Test
    void should_decrease_stock_correctly() {
        Product result = product.decreaseStock(3);
        assertThat(result.stock()).isEqualTo(7);
    }

    @Test
    void should_set_available_to_false_when_stock_reaches_zero() {
        Product result = product.decreaseStock(10);
        assertThat(result.stock()).isEqualTo(0);
        assertThat(result.available()).isFalse();
    }

    @Test
    void should_keep_available_true_when_stock_is_still_positive() {
        Product result = product.decreaseStock(5);
        assertThat(result.stock()).isEqualTo(5);
        assertThat(result.available()).isTrue();
    }

    @Test
    void should_throw_when_decreasing_more_than_available_stock() {
        assertThatThrownBy(() -> product.decreaseStock(11))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    void should_throw_when_decrease_quantity_is_zero_or_negative() {
        assertThatThrownBy(() -> product.decreaseStock(0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> product.decreaseStock(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // --- increaseStock ---

    @Test
    void should_increase_stock_correctly() {
        Product result = product.increaseStock(5);
        assertThat(result.stock()).isEqualTo(15);
    }

    @Test
    void should_set_available_to_true_when_stock_is_increased() {
        Product unavailable = new Product(
                UUID.randomUUID(), "Tomate Raf", variety,
                Price.of("2.50"), Unit.KG, 0, false, 0
        );
        Product result = unavailable.increaseStock(5);
        assertThat(result.stock()).isEqualTo(5);
        assertThat(result.available()).isTrue();
    }

    @Test
    void should_throw_when_increase_quantity_is_zero_or_negative() {
        assertThatThrownBy(() -> product.increaseStock(0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> product.increaseStock(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}