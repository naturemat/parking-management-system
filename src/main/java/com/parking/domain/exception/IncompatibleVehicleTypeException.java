package com.parking.domain.exception;

import com.parking.domain.model.SpaceType;
import com.parking.domain.model.VehicleType;

public class IncompatibleVehicleTypeException extends ParkingException {

    public IncompatibleVehicleTypeException(String plate, String spaceCode, SpaceType spaceType) {
        super("El vehiculo " + plate + " no es compatible con el espacio " + spaceCode
                + " (tipo: " + spaceType + ")");
    }

}
