package com.parking.infrastructure.persistence.jpa.repository;

import com.parking.infrastructure.persistence.jpa.entity.VehicleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataVehicleJpaRepository extends JpaRepository<VehicleJpaEntity, String> {
}
