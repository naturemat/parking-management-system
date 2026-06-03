package com.parking.infrastructure.persistence.jpa.entity;

import com.parking.domain.model.VehicleType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehicles")
public class VehicleJpaEntity {

    @Id
    private String plate;

    private String brand;

    private String model;

    @Enumerated(EnumType.STRING)
    private VehicleType type;

    public VehicleJpaEntity() {
    }

    public VehicleJpaEntity(String plate, String brand, String model, VehicleType type) {
        this.plate = plate;
        this.brand = brand;
        this.model = model;
        this.type = type;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

}
