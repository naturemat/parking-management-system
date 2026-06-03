package com.parking.infrastructure.persistence.jpa;

import com.parking.domain.model.ParkingLot;
import com.parking.domain.model.Space;
import com.parking.domain.repository.ParkingLotRepository;
import com.parking.infrastructure.persistence.jpa.entity.SpaceJpaEntity;
import com.parking.infrastructure.persistence.jpa.repository.SpringDataSpaceJpaRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ParkingLotRepositoryImpl implements ParkingLotRepository {

    private final SpringDataSpaceJpaRepository springRepository;

    public ParkingLotRepositoryImpl(SpringDataSpaceJpaRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public ParkingLot find() {
        List<Space> spaces = springRepository.findAll().stream()
                .map(DomainEntityMapper::toDomain)
                .collect(Collectors.toList());
        return new ParkingLot(spaces);
    }

    @Override
    public void save(ParkingLot parkingLot) {
        List<SpaceJpaEntity> entities = parkingLot.spaces().stream()
                .map(DomainEntityMapper::toJpa)
                .collect(Collectors.toList());
        springRepository.saveAll(entities);
    }

}
