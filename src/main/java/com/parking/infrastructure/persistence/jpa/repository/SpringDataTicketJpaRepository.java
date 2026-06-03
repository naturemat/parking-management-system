package com.parking.infrastructure.persistence.jpa.repository;

import com.parking.infrastructure.persistence.jpa.entity.TicketJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataTicketJpaRepository extends JpaRepository<TicketJpaEntity, Long> {

    Optional<TicketJpaEntity> findByVehiclePlateAndActiveTrue(String vehiclePlate);

    List<TicketJpaEntity> findByActiveTrue();

    List<TicketJpaEntity> findByVehiclePlateOrderByEntryTimeDesc(String vehiclePlate);

}
