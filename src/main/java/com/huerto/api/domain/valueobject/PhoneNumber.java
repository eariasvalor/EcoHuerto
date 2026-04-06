package com.huerto.api.domain.valueobject;

public record PhoneNumber(String countryCode, String number) {

    public PhoneNumber {
        if (countryCode == null || countryCode.isBlank())
            throw new IllegalArgumentException("Country code must not be blank");
        if (number == null || number.isBlank())
            throw new IllegalArgumentException("Phone number must not be blank");

        countryCode = countryCode.trim();
        number = number.trim();

        if (!countryCode.matches("^\\+\\d{1,3}$"))
            throw new IllegalArgumentException("Country code must start with + followed by 1-3 digits (e.g. +34)");
        if (!number.matches("^\\d{6,15}$"))
            throw new IllegalArgumentException("Phone number must contain between 6 and 15 digits");
    }
}