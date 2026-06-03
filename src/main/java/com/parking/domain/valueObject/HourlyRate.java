package com.parking.domain.valueObject;

public record HourlyRate(double value) {

    public HourlyRate {
        if (value <= 0) {
            throw new IllegalArgumentException("Hourly rate must be positive");
        }
    }

    public Money calculateCost(long hours) {
        return new Money(value * hours);
    }

}
