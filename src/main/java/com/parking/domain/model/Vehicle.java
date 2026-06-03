package com.parking.domain.model;

import com.parking.domain.valueObject.Plate;

public class Vehicle {

    private final Plate plate;
    private final String brand;
    private final String model;
    private final VehicleType type;

    public Vehicle(Plate plate, String brand, String model, VehicleType type) {
        this.plate = plate;
        this.brand = brand;
        this.model = model;
        this.type = type;
    }

    public Plate plate() {
        return plate;
    }

    public String brand() {
        return brand;
    }

    public String model() {
        return model;
    }

    public VehicleType type() {
        return type;
    }

}
