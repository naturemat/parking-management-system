package com.parking.application.service;

import com.parking.application.dto.ParkingStatusDTO;
import com.parking.domain.event.DomainEvent;
import com.parking.domain.event.VehicleExitedEvent;
import com.parking.domain.exception.EntityNotFoundException;
import com.parking.domain.exception.NoActiveTicketException;
import com.parking.domain.model.ParkingLot;
import com.parking.domain.model.Ticket;
import com.parking.domain.model.Vehicle;
import com.parking.domain.model.VehicleType;
import com.parking.domain.repository.ParkingLotRepository;
import com.parking.domain.repository.TicketRepository;
import com.parking.domain.repository.VehicleRepository;
import com.parking.domain.service.ParkingPricingService;
import com.parking.domain.service.RevenueService;
import com.parking.domain.valueObject.Money;
import com.parking.domain.valueObject.Plate;
import com.parking.domain.valueObject.SpaceCode;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ParkingApplicationService {

    private final ParkingLotRepository parkingLotRepository;
    private final TicketRepository ticketRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingPricingService pricingService;
    private final RevenueService revenueService;

    public ParkingApplicationService(ParkingLotRepository parkingLotRepository,
                                     TicketRepository ticketRepository,
                                     VehicleRepository vehicleRepository,
                                     ParkingPricingService pricingService,
                                     RevenueService revenueService) {
        this.parkingLotRepository = parkingLotRepository;
        this.ticketRepository = ticketRepository;
        this.vehicleRepository = vehicleRepository;
        this.pricingService = pricingService;
        this.revenueService = revenueService;
    }

    @Transactional
    public Ticket enterVehicle(String plate, String brand, String model, String typeStr, String spaceId) {
        Plate plateVO = new Plate(plate);
        SpaceCode spaceCodeVO = SpaceCode.fromRaw(spaceId);
        VehicleType vehicleType = VehicleType.valueOf(typeStr.toUpperCase());

        Vehicle vehicle = vehicleRepository.findByPlate(plateVO)
                .orElseGet(() -> vehicleRepository.save(new Vehicle(plateVO, brand, model, vehicleType)));

        if (vehicle.type() != vehicleType) {
            throw new IllegalArgumentException("El vehiculo " + plate + " ya esta registrado como "
                    + vehicle.type() + ", no como " + vehicleType);
        }

        ParkingLot parkingLot = parkingLotRepository.find();
        Ticket ticket = parkingLot.parkVehicle(vehicle, spaceCodeVO);

        parkingLotRepository.save(parkingLot);
        Ticket savedTicket = ticketRepository.save(ticket);

        publishEvents(parkingLot);

        return savedTicket;
    }

    @Transactional
    public Ticket exitVehicle(String plate) {
        Plate plateVO = new Plate(plate);

        Ticket ticket = ticketRepository.findActiveByPlate(plateVO)
                .orElseThrow(() -> new NoActiveTicketException(plate));

        ParkingLot parkingLot = parkingLotRepository.find();
        parkingLot.releaseSpace(ticket.spaceCode());

        LocalDateTime exitTime = LocalDateTime.now();
        Money cost = pricingService.calculateCost(ticket.entryTime(), exitTime);
        ticket.close(cost, exitTime);

        parkingLotRepository.save(parkingLot);
        Ticket savedTicket = ticketRepository.save(ticket);

        parkingLot.registerEvent(new VehicleExitedEvent(
                plateVO, ticket.spaceCode(), ticket.entryTime(), exitTime, cost));

        publishEvents(parkingLot);

        return savedTicket;
    }

    public ParkingStatusDTO getStatus() {
        ParkingLot parkingLot = parkingLotRepository.find();
        return new ParkingStatusDTO(
                parkingLot.countOccupied(),
                parkingLot.countAvailable(),
                parkingLot.spaces());
    }

    public List<Ticket> getActiveTickets() {
        return ticketRepository.findAllActive();
    }

    public Vehicle getVehicleInfo(String plate) {
        return vehicleRepository.findByPlate(new Plate(plate))
                .orElseThrow(() -> new EntityNotFoundException("Vehiculo", plate));
    }

    public List<Ticket> getVehicleHistory(String plate) {
        Plate plateVO = new Plate(plate);
        if (!vehicleRepository.existsByPlate(plateVO)) {
            throw new EntityNotFoundException("Vehiculo", plate);
        }
        return ticketRepository.findByPlate(plateVO);
    }

    public double getTotalRevenue() {
        List<Ticket> allTickets = ticketRepository.findAll();
        return revenueService.calculateTotalRevenue(allTickets).amount();
    }

    public long getTotalRevenueTicketCount() {
        List<Ticket> allTickets = ticketRepository.findAll();
        return revenueService.countClosedTickets(allTickets);
    }

    private void publishEvents(ParkingLot parkingLot) {
        for (DomainEvent event : parkingLot.domainEvents()) {
            System.out.println("  [Evento Publicado] " + event.getClass().getSimpleName()
                    + " | Ocurrio: " + event.occurredAt());
        }
        parkingLot.clearEvents();
    }

}
