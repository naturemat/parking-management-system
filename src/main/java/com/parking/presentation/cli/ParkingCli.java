package com.parking.presentation.cli;

import com.parking.application.dto.ParkingStatusDTO;
import com.parking.application.service.ParkingApplicationService;
import com.parking.domain.model.Space;
import com.parking.domain.model.Ticket;
import com.parking.domain.model.Vehicle;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;
import org.springframework.stereotype.Component;

@Component
public class ParkingCli {

    private final ParkingApplicationService parkingService;
    private final Scanner scanner = new Scanner(System.in);

    public ParkingCli(ParkingApplicationService parkingService) {
        this.parkingService = parkingService;
    }

    public void start() {
        while (true) {
            System.out.println("\n=== SISTEMA DE GESTION DE ESTACIONAMIENTO ===");
            System.out.println("1. Ingresar vehiculo");
            System.out.println("2. Registrar salida de vehiculo");
            System.out.println("3. Mostrar estado del estacionamiento");
            System.out.println("4. Mostrar tickets activos");
            System.out.println("5. Historial de vehiculo");
            System.out.println("6. Ingresos totales");
            System.out.println("7. Demo automatizada");
            System.out.println("8. Salir");
            System.out.print("Seleccione una opcion: ");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> enterVehicle();
                case "2" -> exitVehicle();
                case "3" -> showStatus();
                case "4" -> showActiveTickets();
                case "5" -> showVehicleHistory();
                case "6" -> showTotalRevenue();
                case "7" -> runAutomatedDemo();
                case "8" -> {
                    System.out.println("Saliendo...");
                    return;
                }
                default -> System.out.println("Opcion invalida");
            }
        }
    }

    private void enterVehicle() {
        try {
            System.out.print("Placa: ");
            String plate = scanner.nextLine().trim();
            System.out.print("Marca: ");
            String brand = scanner.nextLine().trim();
            System.out.print("Modelo: ");
            String model = scanner.nextLine().trim();
            System.out.print("Tipo (ELECTRIC, GASOLINE, DIESEL, HYBRID): ");
            String type = scanner.nextLine().trim().toUpperCase();
            System.out.print("Id del espacio (ej: A1, B1): ");
            String spaceId = scanner.nextLine().trim().toUpperCase();

            Ticket ticket = parkingService.enterVehicle(plate, brand, model, type, spaceId);
            System.out.println("Vehiculo ingresado exitosamente. Ticket #" + ticket.id()
                    + " | Ingreso: " + ticket.entryTime());
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void exitVehicle() {
        try {
            System.out.print("Placa del vehiculo: ");
            String plate = scanner.nextLine().trim();
            Ticket ticket = parkingService.exitVehicle(plate);
            System.out.println("Salida registrada. Ticket #" + ticket.id());
            System.out.println("  Hora entrada: " + ticket.entryTime());
            System.out.println("  Hora salida: " + ticket.exitTime());
            long hours = ChronoUnit.HOURS.between(ticket.entryTime(), ticket.exitTime());
            System.out.println("  Tiempo: " + (hours < 1 ? 1 : hours) + " hora(s)");
            System.out.println("  Costo: $" + ticket.cost().formatted());
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void showStatus() {
        ParkingStatusDTO status = parkingService.getStatus();
        System.out.println("\n=== ESTADO DEL ESTACIONAMIENTO ===");
        System.out.println("Espacios ocupados: " + status.getOccupiedSpaces());
        System.out.println("Espacios disponibles: " + status.getAvailableSpaces());
        System.out.println("\nDetalle de espacios:");
        for (Space space : status.getSpaces()) {
            String estado = space.isOccupied()
                    ? "OCUPADO (" + space.assignedVehicle() + ")"
                    : "LIBRE";
            System.out.println("  " + space.code() + " [" + space.type() + "] - " + estado);
        }
    }

    private void showActiveTickets() {
        List<Ticket> tickets = parkingService.getActiveTickets();
        System.out.println("\n=== TICKETS ACTIVOS ===");
        if (tickets.isEmpty()) {
            System.out.println("No hay tickets activos.");
        } else {
            for (Ticket t : tickets) {
                System.out.println("  #" + t.id()
                        + " | Placa: " + t.vehiclePlate()
                        + " | Espacio: " + t.spaceCode()
                        + " | Ingreso: " + t.entryTime());
            }
        }
    }

    private void showVehicleHistory() {
        try {
            System.out.print("Placa del vehiculo: ");
            String plate = scanner.nextLine().trim();

            Vehicle vehicle = parkingService.getVehicleInfo(plate);
            List<Ticket> tickets = parkingService.getVehicleHistory(plate);

            System.out.println("\n=== HISTORIAL DEL VEHICULO ===");
            System.out.println("  Placa: " + vehicle.plate());
            System.out.println("  Marca: " + vehicle.brand());
            System.out.println("  Modelo: " + vehicle.model());
            System.out.println("  Tipo: " + vehicle.type());

            if (tickets.isEmpty()) {
                System.out.println("\nSin tickets registrados.");
            } else {
                System.out.println("\nTickets (" + tickets.size() + "):");
                for (Ticket t : tickets) {
                    String estado = t.isActive() ? "ACTIVO" : "CERRADO";
                    System.out.println("  #" + t.id()
                            + " | Espacio: " + t.spaceCode()
                            + " | Ingreso: " + t.entryTime()
                            + (t.exitTime() != null ? " | Salida: " + t.exitTime() : "")
                            + (t.cost().amount() > 0 ? " | Costo: $" + t.cost().formatted() : "")
                            + " | " + estado);
                }
            }
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void showTotalRevenue() {
        double revenue = parkingService.getTotalRevenue();
        long count = parkingService.getTotalRevenueTicketCount();
        System.out.println("\n=== INGRESOS TOTALES ===");
        System.out.println("  Tickets cerrados: " + count);
        System.out.println("  Ingreso total: $" + String.format("%.2f", revenue));
    }

    private void runAutomatedDemo() {
        System.out.println("\n==========================================");
        System.out.println("      DEMO AUTOMATIZADA DEL SISTEMA");
        System.out.println("==========================================");

        System.out.println("\n--- Paso 1: Ingreso exitoso ---");
        System.out.println("  Datos: ABC-123 / Toyota / Corolla / GASOLINE / A1");
        try {
            Ticket t1 = parkingService.enterVehicle("ABC-123", "Toyota", "Corolla", "GASOLINE", "A1");
            System.out.println("  RESULTADO: Ingreso exitoso. Ticket #" + t1.id());
        } catch (RuntimeException e) {
            System.out.println("  RESULTADO: RECHAZADO - " + e.getMessage());
        }

        System.out.println("\n--- Paso 2: Ingreso exitoso (electrico) ---");
        System.out.println("  Datos: XYZ-789 / Tesla / Model 3 / ELECTRIC / B1");
        try {
            Ticket t2 = parkingService.enterVehicle("XYZ-789", "Tesla", "Model 3", "ELECTRIC", "B1");
            System.out.println("  RESULTADO: Ingreso exitoso. Ticket #" + t2.id());
        } catch (RuntimeException e) {
            System.out.println("  RESULTADO: RECHAZADO - " + e.getMessage());
        }

        System.out.println("\n--- Paso 3: Rechazo por vehiculo ya estacionado ---");
        System.out.println("  Datos: ABC-123 intenta ingresar otra vez (aun activo)");
        try {
            parkingService.enterVehicle("ABC-123", "Toyota", "Corolla", "GASOLINE", "A2");
            System.out.println("  RESULTADO: Ingreso exitoso (NO DEBERIA)");
        } catch (RuntimeException e) {
            System.out.println("  RESULTADO: RECHAZADO correctamente - " + e.getMessage());
        }

        System.out.println("\n--- Paso 4: Rechazo por espacio ocupado ---");
        System.out.println("  Datos: DEF-456 intenta ocupar A1 (ya ocupado por ABC-123)");
        try {
            parkingService.enterVehicle("DEF-456", "Ford", "Focus", "GASOLINE", "A1");
            System.out.println("  RESULTADO: Ingreso exitoso (NO DEBERIA)");
        } catch (RuntimeException e) {
            System.out.println("  RESULTADO: RECHAZADO correctamente - " + e.getMessage());
        }

        System.out.println("\n--- Paso 5: Rechazo por incompatibilidad ---");
        System.out.println("  Datos: GHI-789 (GASOLINE) intenta ocupar B1 (ELECTRIC)");
        try {
            parkingService.enterVehicle("GHI-789", "Nissan", "Versa", "GASOLINE", "B1");
            System.out.println("  RESULTADO: Ingreso exitoso (NO DEBERIA)");
        } catch (RuntimeException e) {
            System.out.println("  RESULTADO: RECHAZADO correctamente - " + e.getMessage());
        }

        System.out.println("\n--- Paso 6: Ingreso de hibrido en espacio ELECTRIC ---");
        System.out.println("  Datos: HBD-001 / Toyota / Prius / HYBRID / B2");
        try {
            Ticket t3 = parkingService.enterVehicle("HBD-001", "Toyota", "Prius", "HYBRID", "B2");
            System.out.println("  RESULTADO: Ingreso exitoso. Ticket #" + t3.id());
        } catch (RuntimeException e) {
            System.out.println("  RESULTADO: RECHAZADO - " + e.getMessage());
        }

        System.out.println("\n--- Paso 7: Rechazo por incompatibilidad de tipo ---");
        System.out.println("  Datos: ELE-999 (ELECTRIC) intenta ocupar A2 (STANDARD, libre)");
        try {
            parkingService.enterVehicle("ELE-999", "Nissan", "Leaf", "ELECTRIC", "A2");
            System.out.println("  RESULTADO: Ingreso exitoso (NO DEBERIA)");
        } catch (RuntimeException e) {
            System.out.println("  RESULTADO: RECHAZADO correctamente - " + e.getMessage());
        }

        System.out.println("\n--- Paso 8: Estado del estacionamiento ---");
        ParkingStatusDTO status = parkingService.getStatus();
        System.out.println("  Ocupados: " + status.getOccupiedSpaces() + " | Disponibles: " + status.getAvailableSpaces());
        for (Space space : status.getSpaces()) {
            if (space.isOccupied()) {
                System.out.println("    " + space.code() + " [" + space.type() + "] -> " + space.assignedVehicle());
            }
        }

        System.out.println("\n--- Paso 9: Salida de vehiculo con calculo de costo ---");
        System.out.println("  Dato: ABC-123 sale del estacionamiento");
        try {
            Ticket t = parkingService.exitVehicle("ABC-123");
            System.out.println("  RESULTADO: Salida registrada. Ticket #" + t.id());
            long h = ChronoUnit.HOURS.between(t.entryTime(), t.exitTime());
            System.out.println("    Horas: " + (h < 1 ? 1 : h) + " | Costo: $" + t.cost().formatted());
            System.out.println("    (Minimo 1 hora a $2.00)");
        } catch (RuntimeException e) {
            System.out.println("  RESULTADO: ERROR - " + e.getMessage());
        }

        System.out.println("\n--- Paso 10: Historial del vehiculo ABC-123 ---");
        try {
            Vehicle v = parkingService.getVehicleInfo("ABC-123");
            System.out.println("  Vehiculo: " + v.brand() + " " + v.model() + " (" + v.type() + ")");
            List<Ticket> hist = parkingService.getVehicleHistory("ABC-123");
            System.out.println("  Tickets encontrados: " + hist.size());
            for (Ticket t : hist) {
                System.out.println("    #" + t.id() + " | Entrada: " + t.entryTime()
                        + " | Salida: " + (t.exitTime() != null ? t.exitTime() : "---")
                        + " | $" + t.cost().formatted());
            }
        } catch (RuntimeException e) {
            System.out.println("  RESULTADO: ERROR - " + e.getMessage());
        }

        System.out.println("\n--- Paso 11: Ingresos totales ---");
        double revenue = parkingService.getTotalRevenue();
        long count = parkingService.getTotalRevenueTicketCount();
        System.out.println("  Tickets cerrados: " + count);
        System.out.println("  Ingreso total: $" + String.format("%.2f", revenue));

        System.out.println("\n--- Paso 12: Estado final del estacionamiento ---");
        ParkingStatusDTO finalStatus = parkingService.getStatus();
        System.out.println("  Ocupados: " + finalStatus.getOccupiedSpaces() + " | Disponibles: " + finalStatus.getAvailableSpaces());
        for (Space space : finalStatus.getSpaces()) {
            if (space.isOccupied()) {
                System.out.println("    " + space.code() + " [" + space.type() + "] -> " + space.assignedVehicle());
            }
        }

        System.out.println("\n==========================================");
        System.out.println("            DEMO FINALIZADA");
        System.out.println("==========================================");
    }

}
