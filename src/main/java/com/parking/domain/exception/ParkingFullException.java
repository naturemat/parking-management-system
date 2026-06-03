package com.parking.domain.exception;

public class ParkingFullException extends ParkingException {

    public ParkingFullException() {
        super("El estacionamiento esta lleno");
    }

}
