package com.parking.domain.repository;

import com.parking.domain.model.Ticket;
import com.parking.domain.valueObject.Plate;
import java.util.List;
import java.util.Optional;

public interface TicketRepository {

    Ticket save(Ticket ticket);

    Optional<Ticket> findActiveByPlate(Plate plate);

    List<Ticket> findAllActive();

    List<Ticket> findByPlate(Plate plate);

    List<Ticket> findAll();

}
