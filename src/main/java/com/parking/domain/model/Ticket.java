package com.parking.domain.model;

import com.parking.domain.valueObject.Money;
import com.parking.domain.valueObject.Plate;
import com.parking.domain.valueObject.SpaceCode;
import java.time.LocalDateTime;

public class Ticket {

    private Long id;
    private final Plate vehiclePlate;
    private final SpaceCode spaceCode;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Money cost;
    private boolean active;

    public Ticket(Plate vehiclePlate, SpaceCode spaceCode) {
        this(null, vehiclePlate, spaceCode, LocalDateTime.now(), null, new Money(0), true);
    }

    public Ticket(Long id, Plate vehiclePlate, SpaceCode spaceCode, LocalDateTime entryTime,
                  LocalDateTime exitTime, Money cost, boolean active) {
        this.id = id;
        this.vehiclePlate = vehiclePlate;
        this.spaceCode = spaceCode;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.cost = cost;
        this.active = active;
    }

    public void close(Money finalCost, LocalDateTime exitTime) {
        this.exitTime = exitTime;
        this.cost = finalCost;
        this.active = false;
    }

    public Long id() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Plate vehiclePlate() {
        return vehiclePlate;
    }

    public SpaceCode spaceCode() {
        return spaceCode;
    }

    public LocalDateTime entryTime() {
        return entryTime;
    }

    public LocalDateTime exitTime() {
        return exitTime;
    }

    public Money cost() {
        return cost;
    }

    public boolean isActive() {
        return active;
    }

}
