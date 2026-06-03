package com.parking.domain.valueObject;

public record Plate(String value) {

    public Plate {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Plate cannot be empty");
        }
    }

    @Override
    public String toString() {
        return value;
    }

}
