package com.parking.domain.valueObject;

public class Money {

    private final double amount;

    public Money(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Money amount cannot be negative");
        }
        this.amount = Math.round(amount * 100.0) / 100.0;
    }

    public double amount() {
        return amount;
    }

    public Money add(Money other) {
        return new Money(this.amount + other.amount);
    }

    public Money multiply(double factor) {
        return new Money(this.amount * factor);
    }

    public String formatted() {
        return String.format("%.2f", amount);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Money money = (Money) other;
        return Double.compare(money.amount, amount) == 0;
    }

    @Override
    public int hashCode() {
        long bits = Double.doubleToLongBits(amount);
        return (int) (bits ^ (bits >>> 32));
    }

}
