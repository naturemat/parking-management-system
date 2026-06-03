package com.parking.domain.service;

import com.parking.domain.model.Ticket;
import com.parking.domain.valueObject.Money;
import com.parking.domain.valueObject.Plate;
import java.util.List;

public class RevenueService {

    public Money calculateTotalRevenue(List<Ticket> allTickets) {
        return allTickets.stream()
                .filter(ticket -> !ticket.isActive())
                .map(Ticket::cost)
                .reduce(new Money(0), Money::add);
    }

    public long countClosedTickets(List<Ticket> allTickets) {
        return allTickets.stream()
                .filter(ticket -> !ticket.isActive())
                .count();
    }

    public Money calculateRevenueByVehicle(List<Ticket> vehicleTickets) {
        return vehicleTickets.stream()
                .filter(ticket -> !ticket.isActive())
                .map(Ticket::cost)
                .reduce(new Money(0), Money::add);
    }

}
