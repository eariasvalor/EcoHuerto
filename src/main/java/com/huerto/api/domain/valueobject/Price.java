package com.huerto.api.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Price(BigDecimal amount, String currency) {
    public static final Price ZERO = new Price(BigDecimal.ZERO, "EUR");
    private static final int SCALE = 2;

    public Price {
        Objects.requireNonNull(amount,   "Amount must not be null");
        Objects.requireNonNull(currency, "Currency must not be null");
        if (currency.isBlank())
            throw new IllegalArgumentException("Currency must not be blank");
        if (amount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Price cannot be negative: " + amount);
        amount = amount.setScale(SCALE, RoundingMode.HALF_UP);
    }


    public static Price of(String amount) {
        return new Price(new BigDecimal(amount), "EUR");
    }

    public static Price of(BigDecimal amount) {
        return new Price(amount, "EUR");
    }

    public static Price of(double amount) {
        return new Price(BigDecimal.valueOf(amount), "EUR");
    }

    public Price multiply(int quantity) {
        if (quantity < 0)
            throw new IllegalArgumentException("Quantity cannot be negative: " + quantity);
        return new Price(amount.multiply(BigDecimal.valueOf(quantity)), currency);
    }

    public Price add(Price other) {
        assertSameCurrency(other);
        return new Price(this.amount.add(other.amount), currency);
    }

    public Price subtract(Price other) {
        assertSameCurrency(other);
        BigDecimal result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Result cannot be negative");
        return new Price(result, currency);
    }


    public boolean isGreaterThan(Price other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isLessThan(Price other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount) < 0;
    }

    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    @Override
    public String toString() {
        return amount.toPlainString() + " " + currency;
    }

    private void assertSameCurrency(Price other) {
        if (!this.currency.equals(other.currency))
            throw new IllegalArgumentException(
                    "Cannot operate on different currencies: "
                            + this.currency + " vs " + other.currency);
    }

}
