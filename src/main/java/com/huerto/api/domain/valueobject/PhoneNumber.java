package com.huerto.api.domain.valueobject;

public record PhoneNumber(String countryCode, String number) {

    public PhoneNumber {
        if (countryCode == null || countryCode.isBlank())
            throw new IllegalArgumentException("Country code must not be blank");
        if (number == null || number.isBlank())
            throw new IllegalArgumentException("Phone number must not be blank");
        countryCode = countryCode.trim();
        number = number.trim();
    }
}