package com.parking.domain.model;

import com.parking.domain.valueObject.Plate;
import com.parking.domain.valueObject.SpaceCode;

public class Space {

    private final SpaceCode code;
    private final SpaceType type;
    private boolean occupied;
    private Plate assignedVehicle;

    public Space(SpaceCode code, SpaceType type) {
        this.code = code;
        this.type = type;
        this.occupied = false;
        this.assignedVehicle = null;
    }

    public Space(SpaceCode code, SpaceType type, boolean occupied, Plate assignedVehicle) {
        this.code = code;
        this.type = type;
        this.occupied = occupied;
        this.assignedVehicle = assignedVehicle;
    }

    public SpaceCode code() {
        return code;
    }

    public SpaceType type() {
        return type;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public Plate assignedVehicle() {
        return assignedVehicle;
    }

    public void assign(Plate plate) {
        if (occupied) {
            throw new IllegalStateException("Space " + code + " is already occupied");
        }
        this.occupied = true;
        this.assignedVehicle = plate;
    }

    public void release() {
        this.occupied = false;
        this.assignedVehicle = null;
    }

    public boolean isCompatibleWith(VehicleType vehicleType) {
        if (type == SpaceType.ELECTRIC) {
            return vehicleType == VehicleType.ELECTRIC || vehicleType == VehicleType.HYBRID;
        }
        return vehicleType == VehicleType.GASOLINE || vehicleType == VehicleType.DIESEL;
    }

}
