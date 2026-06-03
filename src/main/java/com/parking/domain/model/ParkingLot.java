package com.parking.domain.model;

import com.parking.domain.event.DomainEvent;
import com.parking.domain.event.VehicleParkedEvent;
import com.parking.domain.exception.EntityNotFoundException;
import com.parking.domain.exception.IncompatibleVehicleTypeException;
import com.parking.domain.exception.ParkingFullException;
import com.parking.domain.exception.SpaceOccupiedException;
import com.parking.domain.exception.VehicleAlreadyParkedException;
import com.parking.domain.valueObject.Plate;
import com.parking.domain.valueObject.SpaceCode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParkingLot {

    private final List<Space> spaces;
    private final List<DomainEvent> domainEvents;

    public ParkingLot(List<Space> spaces) {
        this.spaces = new ArrayList<>(spaces);
        this.domainEvents = new ArrayList<>();
    }

    public Ticket parkVehicle(Vehicle vehicle, SpaceCode spaceCode) {
        if (isVehicleParked(vehicle.plate())) {
            throw new VehicleAlreadyParkedException(vehicle.plate().value());
        }

        if (isFull()) {
            throw new ParkingFullException();
        }

        Space space = findSpace(spaceCode);

        if (space.isOccupied()) {
            throw new SpaceOccupiedException(spaceCode.value());
        }

        if (!space.isCompatibleWith(vehicle.type())) {
            throw new IncompatibleVehicleTypeException(vehicle.plate().value(), spaceCode.value(), space.type());
        }

        space.assign(vehicle.plate());

        Ticket ticket = new Ticket(vehicle.plate(), spaceCode);

        domainEvents.add(new VehicleParkedEvent(vehicle.plate(), spaceCode, LocalDateTime.now()));

        return ticket;
    }

    public void releaseSpace(SpaceCode spaceCode) {
        Space space = findSpace(spaceCode);
        space.release();
    }

    public boolean isVehicleParked(Plate plate) {
        return spaces.stream()
                .filter(Space::isOccupied)
                .anyMatch(space -> plate.equals(space.assignedVehicle()));
    }

    public boolean isFull() {
        return spaces.stream().allMatch(Space::isOccupied);
    }

    public long countOccupied() {
        return spaces.stream().filter(Space::isOccupied).count();
    }

    public long countAvailable() {
        return spaces.size() - countOccupied();
    }

    public List<Space> spaces() {
        return Collections.unmodifiableList(spaces);
    }

    public List<DomainEvent> domainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    public void clearEvents() {
        domainEvents.clear();
    }

    private Space findSpace(SpaceCode code) {
        return spaces.stream()
                .filter(space -> space.code().equals(code))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Espacio", code.value()));
    }

}
