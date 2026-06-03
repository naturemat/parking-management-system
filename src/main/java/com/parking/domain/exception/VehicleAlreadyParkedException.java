package com.parking.domain.exception;

public class VehicleAlreadyParkedException extends ParkingException {

    public VehicleAlreadyParkedException(String plate) {
        super("El vehiculo con placa " + plate + " ya se encuentra estacionado");
    }

}
