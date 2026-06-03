package com.parking.domain.exception;

public class NoActiveTicketException extends ParkingException {

    public NoActiveTicketException(String plate) {
        super("No hay ticket activo para el vehiculo: " + plate);
    }

}
