package com.huerto.api.domain.valueobject;

public record PostalAddress(
        String streetType,
        String street,
        String number,
        String floor,
        String city,
        String postalCode,
        String province
) {
    public PostalAddress {
        if (streetType == null || streetType.isBlank())
            throw new IllegalArgumentException("Street type must not be blank");
        if (street == null || street.isBlank())
            throw new IllegalArgumentException("Street must not be blank");
        if (number == null || number.isBlank())
            throw new IllegalArgumentException("Number must not be blank");
        if (city == null || city.isBlank())
            throw new IllegalArgumentException("City must not be blank");
        if (postalCode == null || postalCode.isBlank())
            throw new IllegalArgumentException("Postal code must not be blank");
        if (province == null || province.isBlank())
            throw new IllegalArgumentException("Province must not be blank");
        streetType = streetType.trim();
        street = street.trim();
        number = number.trim();
        floor = floor != null ? floor.trim() : null;
        city = city.trim();
        postalCode = postalCode.trim();
        province = province.trim();
    }
}
