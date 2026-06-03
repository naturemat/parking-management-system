package com.parking.infrastructure.persistence.jpa;

import com.parking.domain.model.Space;
import com.parking.domain.model.Ticket;
import com.parking.domain.model.Vehicle;
import com.parking.domain.valueObject.Money;
import com.parking.domain.valueObject.Plate;
import com.parking.domain.valueObject.SpaceCode;
import com.parking.infrastructure.persistence.jpa.entity.SpaceJpaEntity;
import com.parking.infrastructure.persistence.jpa.entity.TicketJpaEntity;
import com.parking.infrastructure.persistence.jpa.entity.VehicleJpaEntity;

public class DomainEntityMapper {

    public static SpaceJpaEntity toJpa(Space domain) {
        return new SpaceJpaEntity(
                domain.code().value(),
                domain.type(),
                domain.isOccupied(),
                domain.isOccupied() ? domain.assignedVehicle().value() : null);
    }

    public static Space toDomain(SpaceJpaEntity entity) {
        return new Space(
                new SpaceCode(entity.getId()),
                entity.getType(),
                entity.isOccupied(),
                entity.getAssignedVehiclePlate() != null ? new Plate(entity.getAssignedVehiclePlate()) : null);
    }

    public static TicketJpaEntity toJpa(Ticket domain) {
        return new TicketJpaEntity(
                domain.id(),
                domain.vehiclePlate().value(),
                domain.spaceCode().value(),
                domain.entryTime(),
                domain.exitTime(),
                domain.cost().amount(),
                domain.isActive());
    }

    public static Ticket toDomain(TicketJpaEntity entity) {
        return new Ticket(
                entity.getId(),
                new Plate(entity.getVehiclePlate()),
                new SpaceCode(entity.getSpaceId()),
                entity.getEntryTime(),
                entity.getExitTime(),
                new Money(entity.getCost()),
                entity.isActive());
    }

    public static VehicleJpaEntity toJpa(Vehicle domain) {
        return new VehicleJpaEntity(
                domain.plate().value(),
                domain.brand(),
                domain.model(),
                domain.type());
    }

    public static Vehicle toDomain(VehicleJpaEntity entity) {
        return new Vehicle(
                new Plate(entity.getPlate()),
                entity.getBrand(),
                entity.getModel(),
                entity.getType());
    }

}
