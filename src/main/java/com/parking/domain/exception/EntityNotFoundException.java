package com.parking.domain.exception;

public class EntityNotFoundException extends ParkingException {

    public EntityNotFoundException(String entityType, String identifier) {
        super(entityType + " no encontrado: " + identifier);
    }

}
