package com.parking.infrastructure.persistence.jpa.entity;

import com.parking.domain.model.SpaceType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "spaces")
public class SpaceJpaEntity {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private SpaceType type;

    private boolean occupied;

    private String assignedVehiclePlate;

    public SpaceJpaEntity() {
    }

    public SpaceJpaEntity(String id, SpaceType type, boolean occupied, String assignedVehiclePlate) {
        this.id = id;
        this.type = type;
        this.occupied = occupied;
        this.assignedVehiclePlate = assignedVehiclePlate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SpaceType getType() {
        return type;
    }

    public void setType(SpaceType type) {
        this.type = type;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public String getAssignedVehiclePlate() {
        return assignedVehiclePlate;
    }

    public void setAssignedVehiclePlate(String assignedVehiclePlate) {
        this.assignedVehiclePlate = assignedVehiclePlate;
    }

}
