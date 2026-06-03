package com.parking.domain.repository;

import com.parking.domain.model.ParkingLot;

public interface ParkingLotRepository {

    ParkingLot find();

    void save(ParkingLot parkingLot);

}
