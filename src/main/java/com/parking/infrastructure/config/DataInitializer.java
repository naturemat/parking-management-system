package com.parking.infrastructure.config;

import com.parking.domain.model.SpaceType;
import com.parking.infrastructure.persistence.jpa.entity.SpaceJpaEntity;
import com.parking.infrastructure.persistence.jpa.repository.SpringDataSpaceJpaRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private final SpringDataSpaceJpaRepository spaceRepository;

    public DataInitializer(SpringDataSpaceJpaRepository spaceRepository) {
        this.spaceRepository = spaceRepository;
    }

    @PostConstruct
    public void init() {
        if (spaceRepository.count() == 0) {
            spaceRepository.save(new SpaceJpaEntity("A1", SpaceType.STANDARD, false, null));
            spaceRepository.save(new SpaceJpaEntity("A2", SpaceType.STANDARD, false, null));
            spaceRepository.save(new SpaceJpaEntity("A3", SpaceType.STANDARD, false, null));
            spaceRepository.save(new SpaceJpaEntity("A4", SpaceType.STANDARD, false, null));
            spaceRepository.save(new SpaceJpaEntity("A5", SpaceType.STANDARD, false, null));
            spaceRepository.save(new SpaceJpaEntity("B1", SpaceType.ELECTRIC, false, null));
            spaceRepository.save(new SpaceJpaEntity("B2", SpaceType.ELECTRIC, false, null));
            spaceRepository.save(new SpaceJpaEntity("B3", SpaceType.ELECTRIC, false, null));
            spaceRepository.save(new SpaceJpaEntity("B4", SpaceType.STANDARD, false, null));
            spaceRepository.save(new SpaceJpaEntity("B5", SpaceType.STANDARD, false, null));
        }
    }

}
