package com.parking.infrastructure.persistence.jpa;

import com.parking.domain.model.Ticket;
import com.parking.domain.repository.TicketRepository;
import com.parking.domain.valueObject.Plate;
import com.parking.infrastructure.persistence.jpa.entity.TicketJpaEntity;
import com.parking.infrastructure.persistence.jpa.repository.SpringDataTicketJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TicketRepositoryImpl implements TicketRepository {

    private final SpringDataTicketJpaRepository springRepository;

    public TicketRepositoryImpl(SpringDataTicketJpaRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public Ticket save(Ticket ticket) {
        TicketJpaEntity entity = DomainEntityMapper.toJpa(ticket);
        TicketJpaEntity savedEntity = springRepository.save(entity);
        Ticket savedTicket = DomainEntityMapper.toDomain(savedEntity);
        savedTicket.setId(savedEntity.getId());
        return savedTicket;
    }

    @Override
    public Optional<Ticket> findActiveByPlate(Plate plate) {
        return springRepository.findByVehiclePlateAndActiveTrue(plate.value())
                .map(DomainEntityMapper::toDomain);
    }

    @Override
    public List<Ticket> findAllActive() {
        return springRepository.findByActiveTrue().stream()
                .map(DomainEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Ticket> findByPlate(Plate plate) {
        return springRepository.findByVehiclePlateOrderByEntryTimeDesc(plate.value()).stream()
                .map(DomainEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Ticket> findAll() {
        return springRepository.findAll().stream()
                .map(DomainEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

}
