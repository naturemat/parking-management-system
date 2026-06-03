package com.parking.infrastructure.config;

import com.parking.domain.model.Space;
import com.parking.domain.model.SpaceType;
import com.parking.infrastructure.repositories.SpaceRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {
    private final SpaceRepository spaceRepository;

    public DataInitializer(SpaceRepository spaceRepository) {
        this.spaceRepository = spaceRepository;
    }

    @PostConstruct
    public void init() {
        if (spaceRepository.count() == 0) {
            spaceRepository.save(new Space("A1", SpaceType.STANDARD, false, null));
            spaceRepository.save(new Space("A2", SpaceType.STANDARD, false, null));
            spaceRepository.save(new Space("A3", SpaceType.STANDARD, false, null));
            spaceRepository.save(new Space("A4", SpaceType.STANDARD, false, null));
            spaceRepository.save(new Space("A5", SpaceType.STANDARD, false, null));
            spaceRepository.save(new Space("B1", SpaceType.ELECTRIC, false, null));
            spaceRepository.save(new Space("B2", SpaceType.ELECTRIC, false, null));
            spaceRepository.save(new Space("B3", SpaceType.ELECTRIC, false, null));
            spaceRepository.save(new Space("B4", SpaceType.STANDARD, false, null));
            spaceRepository.save(new Space("B5", SpaceType.STANDARD, false, null));
        }
    }
}
