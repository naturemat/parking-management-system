package com.parking.domain.exception;

public class SpaceOccupiedException extends ParkingException {

    public SpaceOccupiedException(String spaceCode) {
        super("El espacio " + spaceCode + " ya esta ocupado");
    }

}
