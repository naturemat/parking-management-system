package com.parking.infrastructure.persistence.jpa.repository;

import com.parking.infrastructure.persistence.jpa.entity.SpaceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataSpaceJpaRepository extends JpaRepository<SpaceJpaEntity, String> {
}
