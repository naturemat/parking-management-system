package com.parking.domain.event;

import com.parking.domain.valueObject.Plate;
import com.parking.domain.valueObject.SpaceCode;
import java.time.LocalDateTime;

public record VehicleParkedEvent(Plate plate, SpaceCode spaceCode, LocalDateTime timestamp) implements DomainEvent {

    @Override
    public LocalDateTime occurredAt() {
        return timestamp;
    }

}
