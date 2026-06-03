// ============================================================
// SISTEMA DE GESTION DE ESTACIONAMIENTO (PARKING MANAGEMENT)
// ============================================================
// Single-file spaghetti implementation in Java.
// All domain entities, business rules, and UI in one file.
// Arquitectura de Software - Examen Hemi
// ============================================================

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

// ============================================================
// ENUMERACIONES DEL DOMINIO
// ============================================================

enum VehicleType {
    GASOLINE,
    DIESEL,
    ELECTRIC,
    HYBRID
}

enum SpaceType {
    STANDARD,
    ELECTRIC_CHARGING,
    DISABLED
}

enum SpaceStatus {
    AVAILABLE,
    OCCUPIED
}

// ============================================================
// ENTIDAD: VEHICULO
// ============================================================

class Vehicle {
    String licensePlate;
    VehicleType type;
    String brand;
    String model;
    String ownerName;
    boolean isInsideParking;

    Vehicle(String licensePlate, VehicleType type, String brand, String model, String ownerName) {
        this.licensePlate = licensePlate;
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.ownerName = ownerName;
        this.isInsideParking = false;
    }

    boolean canUseSpace(Space space) {
        // REGLA DE NEGOCIO: Vehiculos electricos solo pueden ocupar espacios compatibles
        if (this.type == VehicleType.ELECTRIC) {
            return space.type == SpaceType.ELECTRIC_CHARGING || space.type == SpaceType.STANDARD;
        }
        // Vehiculos no-electricos no deben ocupar espacios de carga electrica
        return space.type != SpaceType.ELECTRIC_CHARGING;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s %s | %s", licensePlate, type, brand, model, ownerName);
    }
}

// ============================================================
// ENTIDAD: ESPACIO
// ============================================================

class Space {
    String spaceId;
    SpaceType type;
    SpaceStatus status;
    int floorNumber;
    double hourlyRate;

    Space(String spaceId, SpaceType type, int floorNumber, double hourlyRate) {
        this.spaceId = spaceId;
        this.type = type;
        this.floorNumber = floorNumber;
        this.hourlyRate = hourlyRate;
        this.status = SpaceStatus.AVAILABLE;
    }

    boolean isAvailable() {
        return this.status == SpaceStatus.AVAILABLE;
    }

    void occupy() {
        // REGLA: No se puede asignar un espacio ocupado
        if (!isAvailable()) {
            throw new IllegalStateException("ERROR CRITICO: Espacio " + spaceId + " ya esta ocupado.");
        }
        this.status = SpaceStatus.OCCUPIED;
    }

    void vacate() {
        this.status = SpaceStatus.AVAILABLE;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | Piso %d | $%.2f/hora | %s",
            spaceId, type, floorNumber, hourlyRate,
            isAvailable() ? "LIBRE" : "OCUPADO");
    }
}

// ============================================================
// ENTIDAD: TICKET
// ============================================================

class Ticket {
    String ticketId;
    String licensePlate;
    String spaceId;
    LocalDateTime entryDateTime;
    LocalDateTime exitDateTime;
    double totalCost;
    boolean isActive;

    Ticket(String ticketId, String licensePlate, String spaceId) {
        this.ticketId = ticketId;
        this.licensePlate = licensePlate;
        this.spaceId = spaceId;
        this.entryDateTime = LocalDateTime.now();
        this.isActive = true;
        this.totalCost = 0.0;
    }

    // REGLA DE NEGOCIO: El costo depende del tiempo estacionado
    void closeTicket(double hourlyRate) {
        this.exitDateTime = LocalDateTime.now();
        long minutesParked = ChronoUnit.MINUTES.between(entryDateTime, exitDateTime);
        // Se cobra por hora o fraccion (minimo 1 hora)
        double hoursParked = Math.ceil(minutesParked / 60.0);
        if (hoursParked < 1.0) {
            hoursParked = 1.0;
        }
        this.totalCost = hoursParked * hourlyRate;
        this.isActive = false;
    }

    long getParkedMinutes() {
        LocalDateTime end = (exitDateTime != null) ? exitDateTime : LocalDateTime.now();
        return ChronoUnit.MINUTES.between(entryDateTime, end);
    }

    @Override
    public String toString() {
        if (isActive) {
            return String.format("TICKET #%s | %s | Espacio: %s | Entrada: %s | Minutos: %d | ESTADO: ACTIVO",
                ticketId, licensePlate, spaceId, entryDateTime, getParkedMinutes());
        } else {
            return String.format("TICKET #%s | %s | Espacio: %s | Entrada: %s | Salida: %s | Total: $%.2f | ESTADO: CERRADO",
                ticketId, licensePlate, spaceId, entryDateTime, exitDateTime, totalCost);
        }
    }
}

// ============================================================
// NUCLEO DEL NEGOCIO: ADMINISTRADOR DEL ESTACIONAMIENTO
// ============================================================

class ParkingManager {
    Map<String, Vehicle> registeredVehicles;
    List<Space> parkingSpaces;
    Map<String, Ticket> activeTickets;
    List<Ticket> closedTickets;
    int nextTicketNumber;
    int totalCapacity;

    ParkingManager(int totalCapacity) {
        this.totalCapacity = totalCapacity;
        this.registeredVehicles = new HashMap<>();
        this.parkingSpaces = new ArrayList<>();
        this.activeTickets = new HashMap<>();
        this.closedTickets = new ArrayList<>();
        this.nextTicketNumber = 1000;
    }

    void registerVehicle(String licensePlate, VehicleType type, String brand, String model, String owner) {
        if (registeredVehicles.containsKey(licensePlate)) {
            System.out.println("[RECHAZADO] El vehiculo con placa " + licensePlate + " ya se encuentra registrado en el sistema.");
            return;
        }
        Vehicle newVehicle = new Vehicle(licensePlate, type, brand, model, owner);
        registeredVehicles.put(licensePlate, newVehicle);
        System.out.println("[ACEPTADO] Vehiculo " + licensePlate + " registrado exitosamente en el sistema.");
    }

    void registerParkingSpace(String spaceId, SpaceType type, int floorNumber, double hourlyRate) {
        parkingSpaces.add(new Space(spaceId, type, floorNumber, hourlyRate));
        System.out.println("[ACEPTADO] Espacio " + spaceId + " creado exitosamente.");
    }

    void attemptVehicleEntry(String licensePlate) {
        // VALIDACION 1: El vehiculo debe estar registrado en el sistema
        if (!registeredVehicles.containsKey(licensePlate)) {
            System.out.println("[RECHAZADO] El vehiculo con placa " + licensePlate + " no esta registrado. Debe registrarse primero.");
            return;
        }

        Vehicle currentVehicle = registeredVehicles.get(licensePlate);

        // VALIDACION 2: REGLA - Un vehiculo no puede ingresar dos veces sin salir
        if (currentVehicle.isInsideParking) {
            System.out.println("[RECHAZADO] El vehiculo " + licensePlate + " ya se encuentra dentro del estacionamiento. Debe salir para volver a ingresar.");
            return;
        }

        // Buscar espacio disponible y compatible
        Space selectedSpace = findAvailableCompatibleSpace(currentVehicle);

        // VALIDACION 3: REGLA - Si el estacionamiento esta lleno se rechaza el ingreso
        if (selectedSpace == null) {
            int totalAvailableSpaces = 0;
            for (Space space : parkingSpaces) {
                if (space.isAvailable()) {
                    totalAvailableSpaces++;
                }
            }

            if (totalAvailableSpaces == 0) {
                System.out.println("[RECHAZADO] El estacionamiento se encuentra completamente lleno. No hay espacios disponibles.");
            } else {
                System.out.println("[RECHAZADO] No se encontraron espacios compatibles para vehiculo tipo " + currentVehicle.type + ".");
                System.out.println("  Espacios libres: " + totalAvailableSpaces + " (ninguno compatible con este tipo de vehiculo).");
            }
            return;
        }

        // REGLA: Asignar espacio y ocuparlo
        selectedSpace.occupy();
        currentVehicle.isInsideParking = true;

        // Generar ticket de ingreso
        String generatedTicketId = "TKT-" + (nextTicketNumber++);
        Ticket newTicket = new Ticket(generatedTicketId, licensePlate, selectedSpace.spaceId);
        activeTickets.put(generatedTicketId, newTicket);

        System.out.println("[INGRESO AUTORIZADO] Vehiculo " + licensePlate + " asignado al espacio " + selectedSpace.spaceId + ".");
        System.out.println("[TICKET GENERADO] " + generatedTicketId + " | Fecha/Hora ingreso: " + newTicket.entryDateTime);
    }

    Space findAvailableCompatibleSpace(Vehicle vehicle) {
        for (Space currentSpace : parkingSpaces) {
            if (currentSpace.isAvailable() && vehicle.canUseSpace(currentSpace)) {
                return currentSpace;
            }
        }
        return null;
    }

    void attemptVehicleExit(String licensePlate) {
        // Buscar ticket activo para este vehiculo
        Ticket currentTicket = null;
        for (Ticket ticket : activeTickets.values()) {
            if (ticket.licensePlate.equals(licensePlate)) {
                currentTicket = ticket;
                break;
            }
        }

        if (currentTicket == null) {
            System.out.println("[RECHAZADO] No se encontro un ticket activo para el vehiculo " + licensePlate + ".");
            return;
        }

        // Localizar y liberar el espacio ocupado
        Space occupiedSpace = null;
        for (Space space : parkingSpaces) {
            if (space.spaceId.equals(currentTicket.spaceId)) {
                occupiedSpace = space;
                break;
            }
        }

        // Obtener tarifa y cerrar ticket (calcular costo)
        double effectiveRate = (occupiedSpace != null) ? occupiedSpace.hourlyRate : 2.50;
        currentTicket.closeTicket(effectiveRate);

        // Liberar recursos del estacionamiento
        if (occupiedSpace != null) {
            occupiedSpace.vacate();
        }
        registeredVehicles.get(licensePlate).isInsideParking = false;

        // Mover ticket de activos a historico
        activeTickets.remove(currentTicket.ticketId);
        closedTickets.add(currentTicket);

        System.out.println("[SALIDA AUTORIZADA] Vehiculo " + licensePlate + " ha salido del estacionamiento.");
        System.out.println("[FACTURA GENERADA] Tiempo estacionado: " + currentTicket.getParkedMinutes() + " minutos.");
        System.out.println("[TOTAL A PAGAR] $" + String.format("%.2f", currentTicket.totalCost));
    }

    void displayFullStatus() {
        int occupiedCount = 0;
        int availableCount = 0;
        int electricSpacesCount = 0;
        int disabledSpacesCount = 0;

        for (Space space : parkingSpaces) {
            if (space.isAvailable()) {
                availableCount++;
            } else {
                occupiedCount++;
            }
            if (space.type == SpaceType.ELECTRIC_CHARGING) electricSpacesCount++;
            if (space.type == SpaceType.DISABLED) disabledSpacesCount++;
        }

        System.out.println("\n======================================================================");
        System.out.println("  REPORTE DE ESTADO DEL ESTACIONAMIENTO");
        System.out.println("======================================================================");
        System.out.println("  Capacidad total del sistema:    " + totalCapacity);
        System.out.println("  Espacios fisicos creados:       " + parkingSpaces.size());
        System.out.println("  Espacios disponibles (libres):  " + availableCount);
        System.out.println("  Espacios ocupados:              " + occupiedCount);
        System.out.println("  Espacios con carga electrica:   " + electricSpacesCount);
        System.out.println("  Espacios para discapacitados:   " + disabledSpacesCount);
        System.out.println("  Tickets activos (vehiculos dentro): " + activeTickets.size());
        System.out.println("  Tickets cerrados (historico):       " + closedTickets.size());
        System.out.println("  Vehiculos registrados en sistema:   " + registeredVehicles.size());

        System.out.println("\n--- DETALLE DE VEHICULOS DENTRO DEL ESTACIONAMIENTO ---");
        boolean anyVehicleInside = false;
        for (Vehicle vehicle : registeredVehicles.values()) {
            if (vehicle.isInsideParking) {
                System.out.println("  [+] " + vehicle.licensePlate + " (" + vehicle.type + ") - " + vehicle.brand + " " + vehicle.model);
                anyVehicleInside = true;
            }
        }
        if (!anyVehicleInside) {
            System.out.println("  (No hay vehiculos estacionados actualmente)");
        }

        System.out.println("\n--- MAPA DE OCUPACION DE ESPACIOS ---");
        for (Space space : parkingSpaces) {
            String occupancyIcon = space.isAvailable() ? "[  LIBRE  ]" : "[ OCUPADO ]";
            String vehicleAssigned = "";
            if (!space.isAvailable()) {
                for (Ticket ticket : activeTickets.values()) {
                    if (ticket.spaceId.equals(space.spaceId)) {
                        vehicleAssigned = " -> Vehiculo: " + ticket.licensePlate;
                        break;
                    }
                }
            }
            System.out.println("  " + occupancyIcon + " " + space.spaceId + " (Piso " + space.floorNumber + ", " + space.type + ", $" + String.format("%.2f", space.hourlyRate) + "/h)" + vehicleAssigned);
        }
        System.out.println("======================================================================\n");
    }

    void displayAllTickets() {
        System.out.println("\n--- TICKETS ACTIVOS (VEHICULOS DENTRO) ---");
        if (activeTickets.isEmpty()) {
            System.out.println("  (No hay tickets activos en este momento)");
        } else {
            for (Ticket ticket : activeTickets.values()) {
                System.out.println("  " + ticket.toString());
            }
        }

        System.out.println("\n--- HISTORIAL DE TICKETS CERRADOS ---");
        if (closedTickets.isEmpty()) {
            System.out.println("  (No hay tickets cerrados en el historial)");
        } else {
            for (Ticket ticket : closedTickets) {
                System.out.println("  " + ticket.toString());
            }
        }
    }

    void displayAllVehicles() {
        System.out.println("\n--- LISTADO COMPLETO DE VEHICULOS REGISTRADOS ---");
        if (registeredVehicles.isEmpty()) {
            System.out.println("  (No hay vehiculos registrados)");
            return;
        }
        System.out.println("  PLACA       | TIPO       | VEHICULO             | PROPIETARIO        | DENTRO");
        System.out.println("  ------------+------------+----------------------+--------------------+-------");
        for (Vehicle vehicle : registeredVehicles.values()) {
            String insideIndicator = vehicle.isInsideParking ? "SI" : "NO";
            System.out.printf("  %-11s | %-10s | %-20s | %-18s | %s\n",
                vehicle.licensePlate, vehicle.type, vehicle.brand + " " + vehicle.model,
                vehicle.ownerName, insideIndicator);
        }
    }

    void displayAllSpaces() {
        System.out.println("\n--- LISTADO COMPLETO DE ESPACIOS DEL ESTACIONAMIENTO ---");
        if (parkingSpaces.isEmpty()) {
            System.out.println("  (No hay espacios configurados)");
            return;
        }
        System.out.println("  ID     | TIPO              | PISO  | TARIFA    | ESTADO   | OCUPANTE");
        System.out.println("  -------+-------------------+-------+-----------+----------+----------");
        for (Space space : parkingSpaces) {
            String statusText = space.isAvailable() ? "LIBRE" : "OCUPADO";
            String occupantInfo = "";
            if (!space.isAvailable()) {
                for (Ticket ticket : activeTickets.values()) {
                    if (ticket.spaceId.equals(space.spaceId)) {
                        occupantInfo = ticket.licensePlate;
                        break;
                    }
                }
            }
            System.out.printf("  %-6s | %-17s | Piso %d | $%-6.2f/h | %-8s | %s\n",
                space.spaceId, space.type, space.floorNumber, space.hourlyRate, statusText, occupantInfo);
        }
    }
}

// ============================================================
// INTERFAZ DE USUARIO - MENU CONSOLA
// ============================================================

public class Main {
    static Scanner userInputScanner = new Scanner(System.in);
    static ParkingManager parkingManager;

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("  SISTEMA DE GESTION DE ESTACIONAMIENTO v1.0");
        System.out.println("  Parking Management System");
        System.out.println("  Arquitectura de Software - Examen Hemi");
        System.out.println("======================================================================");

        initializeSystemWithDefaultData();

        while (true) {
            displayMainMenu();
            String userOption = userInputScanner.nextLine().trim();

            switch (userOption) {
                case "1": handleRegisterVehicle(); break;
                case "2": handleVehicleEntry(); break;
                case "3": handleVehicleExit(); break;
                case "4": parkingManager.displayFullStatus(); break;
                case "5": parkingManager.displayAllTickets(); break;
                case "6": handleAddParkingSpace(); break;
                case "7": parkingManager.displayAllVehicles(); break;
                case "8": parkingManager.displayAllSpaces(); break;
                case "9": executeDemoScenario(); break;
                case "0":
                    System.out.println("Cerrando el sistema de gestion de estacionamiento...");
                    userInputScanner.close();
                    return;
                default:
                    System.out.println("Opcion invalida. Por favor seleccione una opcion valida del menu.");
            }

            if (!userOption.equals("0")) {
                System.out.println("\nPresione la tecla ENTER para regresar al menu principal...");
                userInputScanner.nextLine();
            }
        }
    }

    static void displayMainMenu() {
        System.out.println("\n======================================================================");
        System.out.println("  MENU PRINCIPAL - SISTEMA DE ESTACIONAMIENTO");
        System.out.println("======================================================================");
        System.out.println("  1. Registrar un nuevo vehiculo en el sistema");
        System.out.println("  2. Registrar ingreso de vehiculo al estacionamiento");
        System.out.println("  3. Registrar salida de vehiculo del estacionamiento");
        System.out.println("  4. Ver estado completo del estacionamiento");
        System.out.println("  5. Consultar tickets (activos e historico)");
        System.out.println("  6. Agregar nuevo espacio de estacionamiento");
        System.out.println("  7. Listar todos los vehiculos registrados");
        System.out.println("  8. Listar todos los espacios del estacionamiento");
        System.out.println("  9. Ejecutar escenario de demostracion automatico");
        System.out.println("  0. Salir del sistema");
        System.out.print("  Ingrese su opcion: ");
    }

    static void initializeSystemWithDefaultData() {
        System.out.print("Ingrese la capacidad maxima del estacionamiento (presione ENTER para usar 20): ");
        String userInput = userInputScanner.nextLine().trim();
        int systemCapacity = 20;
        if (!userInput.isEmpty()) {
            try {
                systemCapacity = Integer.parseInt(userInput);
            } catch (NumberFormatException exception) {
                System.out.println("Valor invalido. Se usara la capacidad por defecto de 20 espacios.");
            }
        }

        parkingManager = new ParkingManager(systemCapacity);
        System.out.println("Sistema inicializado con capacidad maxima de " + systemCapacity + " vehiculos.");

        System.out.println("\nCreando espacios de estacionamiento por defecto...");

        for (int i = 1; i <= 8; i++) {
            parkingManager.registerParkingSpace("A" + i, SpaceType.STANDARD, 1, 2.50);
        }
        for (int i = 1; i <= 6; i++) {
            parkingManager.registerParkingSpace("B" + i, SpaceType.STANDARD, 2, 2.00);
        }
        parkingManager.registerParkingSpace("E1", SpaceType.ELECTRIC_CHARGING, 1, 3.00);
        parkingManager.registerParkingSpace("E2", SpaceType.ELECTRIC_CHARGING, 1, 3.00);
        parkingManager.registerParkingSpace("D1", SpaceType.DISABLED, 1, 1.50);
        parkingManager.registerParkingSpace("D2", SpaceType.DISABLED, 2, 1.50);
        parkingManager.registerParkingSpace("C1", SpaceType.STANDARD, 1, 2.50);
        parkingManager.registerParkingSpace("C2", SpaceType.STANDARD, 1, 2.50);

        System.out.println("\nRegistrando vehiculos de ejemplo en el sistema...");

        parkingManager.registerVehicle("ABC-123", VehicleType.GASOLINE, "Toyota", "Corolla", "Juan Perez");
        parkingManager.registerVehicle("DEF-456", VehicleType.ELECTRIC, "Tesla", "Model 3", "Maria Garcia");
        parkingManager.registerVehicle("GHI-789", VehicleType.HYBRID, "Toyota", "Prius", "Carlos Lopez");
        parkingManager.registerVehicle("JKL-012", VehicleType.ELECTRIC, "Nissan", "Leaf", "Ana Martinez");
        parkingManager.registerVehicle("MNO-345", VehicleType.DIESEL, "Ford", "Ranger", "Pedro Ramirez");
        parkingManager.registerVehicle("PQR-678", VehicleType.GASOLINE, "Honda", "Civic", "Luis Torres");
        parkingManager.registerVehicle("STU-901", VehicleType.ELECTRIC, "Chevrolet", "Bolt", "Sofia Castro");

        System.out.println("\n[INICIALIZACION COMPLETADA]");
        System.out.println("  - Espacios creados: 18 (14 Standard, 2 Carga Electrica, 2 Discapacitados)");
        System.out.println("  - Vehiculos registrados: 7");
        System.out.println("  - Sistema listo para operar.\n");
    }

    static void handleRegisterVehicle() {
        System.out.println("\n--- FORMULARIO DE REGISTRO DE VEHICULO ---");
        System.out.print("Numero de placa: ");
        String inputPlate = userInputScanner.nextLine().trim().toUpperCase();

        System.out.println("Seleccione el tipo de combustible del vehiculo:");
        System.out.println("  1. Gasolina");
        System.out.println("  2. Diesel");
        System.out.println("  3. Electrico");
        System.out.println("  4. Hibrido");
        System.out.print("Opcion: ");
        String typeSelection = userInputScanner.nextLine().trim();

        VehicleType selectedType;
        switch (typeSelection) {
            case "2": selectedType = VehicleType.DIESEL; break;
            case "3": selectedType = VehicleType.ELECTRIC; break;
            case "4": selectedType = VehicleType.HYBRID; break;
            default:  selectedType = VehicleType.GASOLINE; break;
        }

        System.out.print("Marca del vehiculo: ");
        String inputBrand = userInputScanner.nextLine().trim();
        System.out.print("Modelo del vehiculo: ");
        String inputModel = userInputScanner.nextLine().trim();
        System.out.print("Nombre del propietario: ");
        String inputOwner = userInputScanner.nextLine().trim();

        parkingManager.registerVehicle(inputPlate, selectedType, inputBrand, inputModel, inputOwner);
    }

    static void handleVehicleEntry() {
        System.out.println("\n--- INGRESO DE VEHICULO AL ESTACIONAMIENTO ---");
        System.out.print("Ingrese la placa del vehiculo: ");
        String inputPlate = userInputScanner.nextLine().trim().toUpperCase();
        parkingManager.attemptVehicleEntry(inputPlate);
    }

    static void handleVehicleExit() {
        System.out.println("\n--- SALIDA DE VEHICULO DEL ESTACIONAMIENTO ---");
        System.out.print("Ingrese la placa del vehiculo que sale: ");
        String inputPlate = userInputScanner.nextLine().trim().toUpperCase();
        parkingManager.attemptVehicleExit(inputPlate);
    }

    static void handleAddParkingSpace() {
        System.out.println("\n--- FORMULARIO DE CREACION DE NUEVO ESPACIO ---");
        System.out.print("Identificador del espacio (ej: Z1): ");
        String inputSpaceId = userInputScanner.nextLine().trim().toUpperCase();

        System.out.println("Seleccione el tipo de espacio:");
        System.out.println("  1. Estandar");
        System.out.println("  2. Carga electrica");
        System.out.println("  3. Discapacitados");
        System.out.print("Opcion: ");
        String typeSelection = userInputScanner.nextLine().trim();

        SpaceType selectedSpaceType;
        switch (typeSelection) {
            case "2": selectedSpaceType = SpaceType.ELECTRIC_CHARGING; break;
            case "3": selectedSpaceType = SpaceType.DISABLED; break;
            default:  selectedSpaceType = SpaceType.STANDARD; break;
        }

        System.out.print("Numero de piso donde se ubica: ");
        int inputFloor = Integer.parseInt(userInputScanner.nextLine().trim());

        System.out.print("Tarifa por hora (en dolares): ");
        double inputRate = Double.parseDouble(userInputScanner.nextLine().trim());

        parkingManager.registerParkingSpace(inputSpaceId, selectedSpaceType, inputFloor, inputRate);
    }

    // ============================================================
    // ESCENARIO DE DEMOSTRACION AUTOMATICO
    // Prueba exhaustiva de todas las reglas de negocio
    // ============================================================

    static void executeDemoScenario() {
        System.out.println("\n======================================================================");
        System.out.println("  EJECUTANDO ESCENARIO DE DEMOSTRACION AUTOMATICO");
        System.out.println("  Se probaran todas las reglas de negocio del sistema");
        System.out.println("======================================================================");

        System.out.println("\n--- PRUEBA 1: INGRESO DE VEHICULO A GASOLINA ---");
        System.out.println("  Vehiculo: ABC-123 (Toyota Corolla, Gasolina)");
        System.out.println("  Expectativa: Debe ingresar correctamente a un espacio STANDARD");
        parkingManager.attemptVehicleEntry("ABC-123");

        System.out.println("\n--- PRUEBA 2: INGRESO DE VEHICULO ELECTRICO ---");
        System.out.println("  Vehiculo: DEF-456 (Tesla Model 3, Electrico)");
        System.out.println("  Expectativa: Debe ingresar correctamente (puede usar STANDARD o ELECTRIC_CHARGING)");
        parkingManager.attemptVehicleEntry("DEF-456");

        System.out.println("\n--- PRUEBA 3: INTENTO DE REINGRESO DEL MISMO VEHICULO (SIN SALIR) ---");
        System.out.println("  Vehiculo: ABC-123 (Toyota Corolla)");
        System.out.println("  Expectativa: DEBE RECHAZAR - Vehiculo ya esta dentro");
        parkingManager.attemptVehicleEntry("ABC-123");

        System.out.println("\n--- PRUEBA 4: INGRESO DE OTRO VEHICULO ELECTRICO ---");
        System.out.println("  Vehiculo: JKL-012 (Nissan Leaf, Electrico)");
        System.out.println("  Expectativa: Debe ingresar correctamente");
        parkingManager.attemptVehicleEntry("JKL-012");

        System.out.println("\n--- PRUEBA 5: INGRESO DE VEHICULO DIESEL ---");
        System.out.println("  Vehiculo: MNO-345 (Ford Ranger, Diesel)");
        System.out.println("  Expectativa: Debe ingresar correctamente a espacio STANDARD");
        parkingManager.attemptVehicleEntry("MNO-345");

        System.out.println("\n--- PRUEBA 6: INGRESO DE VEHICULO NO REGISTRADO ---");
        System.out.println("  Vehiculo: XXX-999 (No registrado)");
        System.out.println("  Expectativa: DEBE RECHAZAR - Vehiculo no registrado");
        parkingManager.attemptVehicleEntry("XXX-999");

        System.out.println("\n--- PRUEBA 7: INGRESO DE VEHICULO HIBRIDO ---");
        System.out.println("  Vehiculo: GHI-789 (Toyota Prius, Hibrido)");
        System.out.println("  Expectativa: Debe ingresar correctamente");
        parkingManager.attemptVehicleEntry("GHI-789");

        System.out.println("\n--- PRUEBA 8: MOSTRAR ESTADO ACTUAL DEL ESTACIONAMIENTO ---");
        parkingManager.displayFullStatus();

        System.out.println("\n--- PRUEBA 9: SALIDA DE VEHICULO Y CALCULO DE COSTO ---");
        System.out.println("  Vehiculo: ABC-123 (Toyota Corolla)");
        System.out.println("  Expectativa: Debe calcular el costo basado en el tiempo estacionado");
        parkingManager.attemptVehicleExit("ABC-123");

        System.out.println("\n--- PRUEBA 10: REINGRESO DESPUES DE SALIR (DEBE FUNCIONAR) ---");
        System.out.println("  Vehiculo: ABC-123 (Toyota Corolla)");
        System.out.println("  Expectativa: Debe permitir el ingreso porque ya salio");
        parkingManager.attemptVehicleEntry("ABC-123");

        System.out.println("\n--- PRUEBA 11: SALIDA DE VEHICULO SIN TICKET ACTIVO ---");
        System.out.println("  Vehiculo: STU-901 (Chevrolet Bolt, nunca ingreso)");
        System.out.println("  Expectativa: DEBE RECHAZAR - No hay ticket activo");
        parkingManager.attemptVehicleExit("STU-901");

        System.out.println("\n--- PRUEBA 12: LISTADO COMPLETO DE TICKETS ---");
        parkingManager.displayAllTickets();

        System.out.println("\n--- PRUEBA 13: LISTADO DE VEHICULOS REGISTRADOS ---");
        parkingManager.displayAllVehicles();

        System.out.println("\n--- PRUEBA 14: LISTADO DE ESPACIOS ---");
        parkingManager.displayAllSpaces();

        System.out.println("\n======================================================================");
        System.out.println("  ESCENARIO DE DEMOSTRACION COMPLETADO EXITOSAMENTE");
        System.out.println("  Todas las reglas de negocio han sido probadas:");
        System.out.println("  1. Solo vehiculos registrados pueden ingresar");
        System.out.println("  2. Un vehiculo no puede ingresar dos veces sin salir");
        System.out.println("  3. Vehiculos electricos ocupan espacios compatibles");
        System.out.println("  4. Estacionamiento lleno rechaza ingreso");
        System.out.println("  5. Costo depende del tiempo estacionado");
        System.out.println("  6. Reingreso permitido despues de salir");
        System.out.println("======================================================================");
    }
}
