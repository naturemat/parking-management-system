package com.parking.domain.valueObject;

public record SpaceCode(String value) {

    public SpaceCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Space code cannot be empty");
        }
    }

    public static SpaceCode fromRaw(String raw) {
        return new SpaceCode(raw.toUpperCase());
    }

    @Override
    public String toString() {
        return value;
    }

}
