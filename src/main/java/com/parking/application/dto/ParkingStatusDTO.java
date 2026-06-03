package com.parking.application.dto;

import com.parking.domain.model.Space;
import java.util.List;

public class ParkingStatusDTO {

    private final long occupiedSpaces;
    private final long availableSpaces;
    private final List<Space> spaces;

    public ParkingStatusDTO(long occupiedSpaces, long availableSpaces, List<Space> spaces) {
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
