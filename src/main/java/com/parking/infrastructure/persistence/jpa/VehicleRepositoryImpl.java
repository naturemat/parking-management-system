package com.parking.infrastructure.persistence.jpa;

import com.parking.domain.model.Vehicle;
import com.parking.domain.repository.VehicleRepository;
import com.parking.domain.valueObject.Plate;
import com.parking.infrastructure.persistence.jpa.entity.VehicleJpaEntity;
import com.parking.infrastructure.persistence.jpa.repository.SpringDataVehicleJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class VehicleRepositoryImpl implements VehicleRepository {

    private final SpringDataVehicleJpaRepository springRepository;

    public VehicleRepositoryImpl(SpringDataVehicleJpaRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        VehicleJpaEntity entity = DomainEntityMapper.toJpa(vehicle);
        VehicleJpaEntity savedEntity = springRepository.save(entity);
        return DomainEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Vehicle> findByPlate(Plate plate) {
        return springRepository.findById(plate.value())
                .map(DomainEntityMapper::toDomain);
    }

    @Override
    public boolean existsByPlate(Plate plate) {
        return springRepository.existsById(plate.value());
    }

}
