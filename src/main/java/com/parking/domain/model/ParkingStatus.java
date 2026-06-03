package com.parking.domain.model;

import java.util.List;

public class ParkingStatus {
    private final long occupiedSpaces;
    private final long availableSpaces;
    private final List<Space> spaces;

    public ParkingStatus(long occupiedSpaces, long availableSpaces, List<Space> spaces) {
        this.occupiedSpaces = occupiedSpaces;
        this.availableSpaces = availableSpaces;
        this.spaces = spaces;
    }

    public long getOccupiedSpaces() {
        return occupiedSpaces;
    }

    public long getAvailableSpaces() {
        return availableSpaces;
    }

    public List<Space> getSpaces() {
        return spaces;
    }
}
