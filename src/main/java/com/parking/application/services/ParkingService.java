package com.parking.application.services;

import com.parking.domain.model.ParkingStatus;
import com.parking.domain.model.Space;
import com.parking.domain.model.SpaceType;
import com.parking.domain.model.Ticket;
import com.parking.domain.model.Vehicle;
import com.parking.domain.model.VehicleType;
import com.parking.infrastructure.repositories.SpaceRepository;
import com.parking.infrastructure.repositories.TicketRepository;
import com.parking.infrastructure.repositories.VehicleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingService {
    private final VehicleRepository vehicleRepo;
    private final SpaceRepository spaceRepo;
    private final TicketRepository ticketRepo;

    public ParkingService(VehicleRepository vehicleRepo, SpaceRepository spaceRepo, TicketRepository ticketRepo) {
        this.vehicleRepo = vehicleRepo;
        this.spaceRepo = spaceRepo;
        this.ticketRepo = ticketRepo;
    }

    @Transactional
    public Ticket enterVehicle(String plate, String brand, String model, String typeStr, String spaceId) {
        VehicleType vehicleType = VehicleType.valueOf(typeStr.toUpperCase());

        if (!vehicleRepo.existsById(plate)) {
            vehicleRepo.save(new Vehicle(plate, brand, model, vehicleType));
        } else {
            Vehicle existing = vehicleRepo.findById(plate).get();
            if (existing.getType() != vehicleType) {
                throw new RuntimeException("El vehiculo " + plate + " ya esta registrado como "
                        + existing.getType() + ", no como " + vehicleType);
            }
        }

        Optional<Ticket> activeTicket = ticketRepo.findByVehiclePlateAndActiveTrue(plate);
        if (activeTicket.isPresent()) {
            throw new RuntimeException("El vehiculo con placa " + plate + " ya se encuentra estacionado");
        }

        Space space = spaceRepo.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("Espacio no encontrado: " + spaceId));

        if (space.isOccupied()) {
            throw new RuntimeException("El espacio " + spaceId + " ya esta ocupado");
        }

        if (!isCompatible(space.getType(), vehicleType)) {
            throw new RuntimeException("El vehiculo " + plate + " no es compatible con el espacio " + spaceId
                    + " (tipo: " + space.getType() + ")");
        }

        boolean allFull = spaceRepo.findAll().stream().allMatch(Space::isOccupied);
        if (allFull) {
            throw new RuntimeException("El estacionamiento esta lleno");
        }

        space.setOccupied(true);
        space.setAssignedVehiclePlate(plate);
        spaceRepo.save(space);

        Ticket ticket = new Ticket(null, plate, spaceId, LocalDateTime.now(), null, 0.0, true);
        return ticketRepo.save(ticket);
    }

    @Transactional
    public Ticket exitVehicle(String plate) {
        Ticket ticket = ticketRepo.findByVehiclePlateAndActiveTrue(plate)
                .orElseThrow(() -> new RuntimeException("No hay ticket activo para el vehiculo: " + plate));

        spaceRepo.findById(ticket.getSpaceId()).ifPresent(space -> {
            space.setOccupied(false);
            space.setAssignedVehiclePlate(null);
            spaceRepo.save(space);
        });

        ticket.setExitTime(LocalDateTime.now());
        long hours = ChronoUnit.HOURS.between(ticket.getEntryTime(), ticket.getExitTime());
        if (hours < 1) hours = 1;
        ticket.setCost(hours * 2.0);
        ticket.setActive(false);
        return ticketRepo.save(ticket);
    }

    public ParkingStatus getStatus() {
        List<Space> spaces = spaceRepo.findAll();
        long occupied = spaces.stream().filter(Space::isOccupied).count();
        long available = spaces.size() - occupied;
        return new ParkingStatus(occupied, available, spaces);
    }

    public List<Ticket> getActiveTickets() {
        return ticketRepo.findByActiveTrue();
    }

    public Vehicle getVehicleInfo(String plate) {
        return vehicleRepo.findById(plate)
                .orElseThrow(() -> new RuntimeException("Vehiculo no encontrado: " + plate));
    }

    public List<Ticket> getVehicleHistory(String plate) {
        if (!vehicleRepo.existsById(plate)) {
            throw new RuntimeException("Vehiculo no encontrado: " + plate);
        }
        return ticketRepo.findByVehiclePlateOrderByEntryTimeDesc(plate);
    }

    public double getTotalRevenue() {
        return ticketRepo.findAll().stream()
                .filter(t -> !t.isActive())
                .mapToDouble(Ticket::getCost)
                .sum();
    }

    public long getTotalRevenueTicketCount() {
        return ticketRepo.findAll().stream()
                .filter(t -> !t.isActive())
                .count();
    }

    private boolean isCompatible(SpaceType spaceType, VehicleType vehicleType) {
        if (spaceType == SpaceType.ELECTRIC) {
            return vehicleType == VehicleType.ELECTRIC || vehicleType == VehicleType.HYBRID;
        }
        return vehicleType == VehicleType.GASOLINE || vehicleType == VehicleType.DIESEL;
    }
}
