package com.parking.domain.repository;

import com.parking.domain.model.Vehicle;
import com.parking.domain.valueObject.Plate;
import java.util.Optional;

public interface VehicleRepository {

    Vehicle save(Vehicle vehicle);

    Optional<Vehicle> findByPlate(Plate plate);

    boolean existsByPlate(Plate plate);

}
