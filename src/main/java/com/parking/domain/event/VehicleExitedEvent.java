package com.parking.domain.event;

import com.parking.domain.valueObject.Money;
import com.parking.domain.valueObject.Plate;
import com.parking.domain.valueObject.SpaceCode;
import java.time.LocalDateTime;

public record VehicleExitedEvent(Plate plate, SpaceCode spaceCode, LocalDateTime entryTime,
                                  LocalDateTime exitTime, Money cost) implements DomainEvent {

    @Override
    public LocalDateTime occurredAt() {
        return exitTime;
    }

}
